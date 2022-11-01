package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.PCVLiquidationLineDao;
import eulap.eb.dao.PettyCashReplenishmentLineDao;
import eulap.eb.dao.PettyCashVoucherLiquidationDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PettyCashReplenishmentLine;
import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidation;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidationLine;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCustodian;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.report.PCVLiquidationRegisterParam;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.PettyCashVoucherLiquidationRegisterDTO;
import eulap.eb.web.dto.ResultProperty;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Service class for all transactions under {@link PettyCashVoucherLiquidation}.

 *
 */
@Service
public class PettyCashVoucherLiquidationService extends BaseWorkflowService {
	@Autowired
	private PettyCashVoucherLiquidationDao pcvlDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private UserCustodianService userCustodianService;
	@Autowired
	private CustodianAccountService custodianAccountService;
	@Autowired
	private PettyCashVoucherService pcvService;
	@Autowired
	private PCVLiquidationLineDao pcvllDao;
	@Autowired
	private AccountCombinationService accountCombinationService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private PettyCashReplenishmentLineDao pcrlDao;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private AccountService accountService;

	private static final String PCVL_REGISTER_CONF_TYPE = "PettyCashVoucherLiquidation1";

	@Override
	public FormWorkflow getFormWorkflow(int pcvId) {
		return getPettyCashVoucherLiquidation(pcvId).getFormWorkflow();
	}

	/**
	 * Get {@link PettyCashVoucherLiquidation}
	 * @param pcvlId The petty cash voucher id.
	 * @return {@link PettyCashVoucherLiquidation}
	 */
	public PettyCashVoucherLiquidation getPettyCashVoucherLiquidation(Integer pcvlId) {
		PettyCashVoucherLiquidation pcvl = pcvlDao.get(pcvlId);
		PettyCashVoucher pcv = pcvService.getPettyCashVoucher(pcvl.getPettyCashVoucherId());
		pcvl.setPcvNumber(pcv.getSequenceNo());
		pcvl.setPcvDate(pcv.getPcvDate());
		pcvl.setPcvDateString(DateUtil.formatDate(pcv.getPcvDate()));
		List<PettyCashVoucherLiquidationLine>  pcvlls = pcvllDao.getAllByRefId(PettyCashVoucherLiquidationLine.FIELD.pcvlId.name(), pcvl.getId());
		for(PettyCashVoucherLiquidationLine pcvll : pcvlls) {
			Supplier s = supplierService.getSupplier(pcvll.getSupplierId());
			AccountCombination ac = accountCombinationService.getAccountCombination(pcvll.getAccountCombinationId());
			pcvll.setPcvDateString(DateUtil.formatDate(pcv.getPcvDate()));
			pcvll.setSupplierName(s.getName());
			pcvll.setSupplierTin(StringFormatUtil.processTin(s.getTin()));
			pcvll.setBrgyStreet(s.getStreetBrgy());
			pcvll.setCity(s.getCityProvince());
			pcvll.setDivisionId(pcvl.getDivisionId());
			pcvll.setDivisionName(divisionService.getDivision(pcvl.getDivisionId()).getName());
			pcvll.setAccountId(ac.getAccount().getId());
			pcvll.setAccountName(ac.getAccount().getAccountName());
		}
		pcvl.setPcvlLines(pcvlls);
		return pcvl;
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		PettyCashVoucherLiquidation pcvl = (PettyCashVoucherLiquidation) form;
		boolean isNew = pcvl.getId() == 0;
		if (isNew) {
			pcvl.setSequenceNo(pcvlDao.generateSequenceNo(pcvl.getCompanyId(), pcvl.getDivisionId()));
		} else {
			PettyCashVoucherLiquidation savedPcv = getPettyCashVoucherLiquidation(pcvl.getId());
			DateUtil.setCreatedDate(pcvl, savedPcv.getCreatedDate());
			List<PettyCashVoucherLiquidationLine>  toBeDeletedLines = pcvllDao.getAllByRefId(
					PettyCashVoucherLiquidationLine.FIELD.pcvlId.name(), pcvl.getId());
			if(toBeDeletedLines != null) {
				for(PettyCashVoucherLiquidationLine pcvll : toBeDeletedLines) {
					pcvlDao.delete(pcvll);
				}
			}
			toBeDeletedLines = null;//Clear memory
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		PettyCashVoucherLiquidation pcvl = (PettyCashVoucherLiquidation) form;
		PettyCashVoucher pcv = pcvService.getPettyCashVoucher(pcvl.getPettyCashVoucherId());
		boolean isNew = pcvl.getId() == 0;
		Integer parentObjectId = pcvl.getEbObjectId();
		AuditUtil.addAudit(pcvl, new Audit(user.getId(), isNew, new Date()));
		pcvlDao.saveOrUpdate(pcvl);
		//Save lines
		List<Domain> toBeSavedPCVLLs = new ArrayList<Domain>();
		List<PettyCashVoucherLiquidationLine> pcvlls = pcvl.getPcvlLines();
		if(pcvlls != null) {
			for(PettyCashVoucherLiquidationLine pcvll : pcvlls) {
				AccountCombination ac = accountCombinationService.getAccountCombination(pcvl.getCompanyId(), pcvl.getDivisionId(), pcvll.getAccountId());
				pcvll.setPcvlId(pcvl.getId());
				pcvll.setPcvDate(pcv.getPcvDate());
				pcvll.setAccountCombinationId(ac.getId());
				toBeSavedPCVLLs.add(pcvll);
			}
			pcvllDao.batchSave(toBeSavedPCVLLs);
		}
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				pcvl.getReferenceDocuments(), true);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return pcvlDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		PettyCashVoucherLiquidation pcv = pcvlDao.getByEbObjectId(ebObjectId);
		Integer pId = pcv.getId();
		FormProperty property = workflowHandler.getProperty(pcv.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit()  + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = pcv.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Petty Cash Voucher Liquidation - " +pcv.getDivision().getName()+" "+ pcv.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + pcv.getCompany().getName())
				.append(" " + pcv.getDivision().getName())
				.append(" " + DateUtil.formatDate(pcv.getPcvlDate()));
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case PettyCashVoucherLiquidation.PCVL_OBJECT_TYPE_ID:
			return pcvlDao.getByEbObjectId(ebObjectId);
		case ReferenceDocument.OBJECT_TYPE_ID:
			return referenceDocumentDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Validates {@link PettyCashVoucherLiquidation}
	 * @param obj {@link PettyCashVoucherLiquidation}
	 * @param errors The errors
	 */
	public void validate(Object obj, Errors errors) {
		PettyCashVoucherLiquidation pcvl = (PettyCashVoucherLiquidation) obj;
		if(pcvl.getPettyCashVoucherId() == null) {
			errors.rejectValue("pettyCashVoucherId", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.9"));
		} else {
				ValidatorUtil.validateCompany(pcvl.getCompanyId(), companyService, errors, "companyId");
				if (!divisionService.getDivision(pcvl.getDivisionId()).isActive()) {
					errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("PettyCashVoucherService.2"));
				}
				if(pcvl.getUserCustodianId() != null) {
					UserCustodian uc = userCustodianService.getUserCustodian(pcvl.getUserCustodianId());
					if (!uc.isActive()) {
						errors.rejectValue("userCustodianId", null, null, ValidatorMessages.getString("PettyCashVoucherService.4"));
					}
					if(!custodianAccountService.getCustodianAccount(uc.getCustodianAccountId()).isActive()) {
						errors.rejectValue("userCustodianId", null, null, ValidatorMessages.getString("PettyCashVoucherService.10"));
					}
				}

				if(pcvl.getPcvlDate() == null) {
					errors.rejectValue("pcvlDate", null, null, ValidatorMessages.getString("PettyCashVoucherService.5"));
				}

				Integer row = 0;
				List<PettyCashVoucherLiquidationLine> pcvlLines = pcvl.getPcvlLines();
				boolean hasLines = pcvlLines != null && !pcvlLines.isEmpty();
				double totalpcvlLinesAmount = 0;
				if(!hasLines) {
					errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.1"));
				} else {
					for(PettyCashVoucherLiquidationLine pcvlLine : pcvlLines) {
						removeWhiteSpacesPCVLL(pcvlLine);
						++row;
						if(pcvlLine.getBmsNumber() != null) {
							if(pcvlLine.getBmsNumber().trim().length() > PettyCashVoucherLiquidationLine.MAX_CHARACTERS) {
								errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.4") + row);
							}
						}
						if(pcvlLine.getOrNumber() == null || pcvlLine.getOrNumber().trim().isEmpty()) {
							errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.2") + row);
						} else if (pcvlLine.getOrNumber().trim().length() > PettyCashVoucherLiquidationLine.MAX_CHARACTERS) {
							errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.3") + row);
						}
						if(pcvlLine.getSupplierId() == null) {
							errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.5") + row);
						} else if (!supplierService.getSupplier(pcvlLine.getSupplierId()).isActive()) {
							errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.12") + row);
						}
						if(pcvlLine.getDescription() == null || pcvlLine.getDescription().isEmpty()) {
							errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.6") + row);
						}
						if(pcvlLine.getAccountId() == null) {
							errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.7") + row);
						} else if(!accountService.getAccount(pcvlLine.getAccountId()).isActive()) {
							errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.13") + row);
						}
						if(!divisionService.getDivision(pcvlLine.getDivisionId()).isActive()) {
							errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.14") + row);
						}
						if(pcvlLine.getUpAmount() == null) {
							errors.rejectValue("pcvlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.8") + row);
						} else {
							totalpcvlLinesAmount += pcvlLine.getUpAmount();
						}
					}
				}
				if (NumberFormatUtil.roundOffNumber(totalpcvlLinesAmount, 6) > NumberFormatUtil.roundOffNumber(pcvl.getAmount(), 6)) {
					errors.rejectValue("grandTotal", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.10"));
				}
				if(pcvl.getCashReturned() == null) {
					errors.rejectValue("cashReturned", null, null, ValidatorMessages.getString("PettyCashVoucherService.8"));
				} else if (!pcvl.getCashReturned().equals(pcvl.getGrandTotal())) {
					errors.rejectValue("cashReturned", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.11"));
				}
			}
		refDocumentService.validateReferences(pcvl.getReferenceDocuments(), errors);
	}

	private void removeWhiteSpacesPCVLL(PettyCashVoucherLiquidationLine pcvll){
		if(pcvll.getBmsNumber() != null) {
			pcvll.setBmsNumber(StringFormatUtil.removeExtraWhiteSpaces(pcvll.getBmsNumber().trim()));
		}
		if(pcvll.getOrNumber() != null) {
			pcvll.setOrNumber(StringFormatUtil.removeExtraWhiteSpaces(pcvll.getOrNumber().trim()));
		}
		if(pcvll.getDescription() != null) {
			pcvll.setDescription(StringFormatUtil.removeExtraWhiteSpaces(pcvll.getDescription().trim()));
		}
	}
	/**
	 * Get the list of {@link PettyCashVoucherLiquidation} by criteria.
	 * @param searchCriteria The searchCriteria.
	 * @param user The current logged user.
	 * @return
	 */
	public List<FormSearchResult> searchPettyCashVoucherLiquidations(int divisionId, String searchCriteria, User user){
		List<PettyCashVoucherLiquidation> pcvls = pcvlDao.searchPettyCashVoucherLiquidations(divisionId, searchCriteria, user);
		List<FormSearchResult> results = new ArrayList<>();
		List<ResultProperty> properties = null;
		for(PettyCashVoucherLiquidation pcvl : pcvls){
			String title = "PCVL No. " + 
					pcvl.getSequenceNo().toString();
			properties = new ArrayList<>();
			properties.add(ResultProperty.getInstance("Company", pcvl.getCompany().getName())); 
			properties.add(ResultProperty.getInstance("Division", pcvl.getDivision().getName())); 
			properties.add(ResultProperty.getInstance("Custodian", pcvl.getUserCustodian().getCustodianAccount().getCustodianAccountName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(pcvl.getPcvlDate())));
			properties.add(ResultProperty.getInstance("Requestor", pcvl.getRequestor()));
			properties.add(ResultProperty.getInstance("Reference", pcvl.getReferenceNo()));
			properties.add(ResultProperty.getInstance("Amount", pcvl.getAmount().toString()));
			String status = pcvl.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status))); 
			results.add(FormSearchResult.getInstanceOf(pcvl.getId(), title, properties));
		}
		return results;
	}

	/**
	 * Get {@link PettyCashVoucherLiquidation}
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param userCustodianId The user custodian id
	 * @param requestor The requestor
	 * @param pcvNo The petty cash voucher sequence number
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @param pageSetting The page settings
	 * @return Page of {@link PettyCashVoucherLiquidation}
	 */
	public Page<PettyCashVoucher> getPCVLiquidations(Integer companyId, Integer divisionId, Integer userCustodianId,
			String requestor, Integer pcvNo, Date dateFrom, Date dateTo, Integer pageNumber) {
		Page<PettyCashVoucher> pcvs = pcvlDao.getPCVReferences(companyId, divisionId, userCustodianId, requestor, pcvNo, dateFrom, dateTo, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		for(PettyCashVoucher pcv : pcvs.getData()) {
			pcv.setUserCustodian(userCustodianService.getUserCustodian(pcv.getUserCustodianId()));
		}
		return pcvs;
	}

	/**
	 * Convert {@link PettyCashVoucher} to {@link PettyCashVoucherLiquidation}
	 * @param pcvId The {@link PettyCashVoucher} id 
	 * @return The converted {@link PettyCashVoucherLiquidation}
	 */
	public PettyCashVoucherLiquidation convPCVtoPCVL(int pcvId) {
		PettyCashVoucher pcv = pcvService.getPettyCashVoucher(pcvId);
		PettyCashVoucherLiquidation pcvl = new PettyCashVoucherLiquidation();
		if(pcv != null) {
			pcvl.setCompanyId(pcv.getCompanyId());
			pcvl.setDivisionId(pcv.getDivisionId());
			pcvl.setPettyCashVoucherId(pcv.getId());
			pcvl.setUserCustodianId(pcv.getUserCustodianId());
			pcvl.setRequestor(pcv.getRequestor());
			pcvl.setReferenceNo(pcv.getReferenceNo());
			pcvl.setDescription(pcv.getDescription());
			pcvl.setAmount(pcv.getAmount());
			pcvl.setPcvNumber(pcv.getSequenceNo());
			pcvl.setPcvDate(pcv.getPcvDate());
			pcvl.setCompany(pcv.getCompany());
			pcvl.setDivision(pcv.getDivision());
			pcvl.setUserCustodian(pcv.getUserCustodian());
			pcvl.setPettyCashVoucer(pcv);
			pcvl.setUserCustodianName(pcv.getUserCustodian().getCustodianAccount().getCustodianName());
			pcvl.setPcvDateString(DateUtil.formatDate(pcv.getPcvDate()));
		}
		return pcvl;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID 
				&& currentWorkflowLog.getComment() != null && !currentWorkflowLog.getComment().isEmpty()) {
			PettyCashVoucherLiquidation pcvl = pcvlDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
			if(pcvl != null) {
				StringBuffer errorMessage = null;
				List<PettyCashVoucherLiquidationLine>  pcvlls = pcvllDao.getAllByRefId(PettyCashVoucherLiquidationLine.FIELD.pcvlId.name(), pcvl.getId());
				if(pcvlls != null && !pcvlls.isEmpty()) {
					for(PettyCashVoucherLiquidationLine pcvll : pcvlls) {
						List<PettyCashReplenishmentLine> pcrls = pcrlDao.getAssociatedPettyCashReplenishment(pcvll.getEbObjectId());
						if(pcrls.size() != 0) {
							errorMessage = new StringBuffer("Transaction cannot be cancelled because it has associated transaction/s: ");
							for(PettyCashReplenishmentLine pcrl : pcrls) {
								APInvoice apInvoice = apInvoiceDao.get(pcrl.getApInvoiceId());
								errorMessage.append("<br> PCR No. : " + apInvoice.getSequenceNumber());
							}
						}
					}
					if(errorMessage != null) {
						bindingResult.reject("workflowMessage", errorMessage.toString());
						currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
					}
				}
			}
		}
	}


	/**
	 * Get the petty cash voucher liquidation report data
	 * @param param The petty cash voucher liquidation parameter object
	 * @return The petty cash voucher liquidation report data
	 */

	public JRDataSource generatePCVLiquidationRegister(PCVLiquidationRegisterParam param) {
		EBJRServiceHandler<PettyCashVoucherLiquidationRegisterDTO> handler = new PCVLiquidationRegisterHandler(param, this);
		return new  EBDataSource<PettyCashVoucherLiquidationRegisterDTO>(handler);
	}

	private static class PCVLiquidationRegisterHandler implements EBJRServiceHandler<PettyCashVoucherLiquidationRegisterDTO> {
		private PCVLiquidationRegisterParam param;
		private PettyCashVoucherLiquidationService pettyCashVoucherLiquidationService;
		private PCVLiquidationRegisterHandler (PCVLiquidationRegisterParam param,
				PettyCashVoucherLiquidationService pettyCashVoucherLiquidationService){
			this.param = param;
			this.pettyCashVoucherLiquidationService = pettyCashVoucherLiquidationService;
		}
		@Override
		public void close() throws IOException {
			pettyCashVoucherLiquidationService = null;
		}
		@Override
		public Page<PettyCashVoucherLiquidationRegisterDTO> nextPage(PageSetting pageSetting) {
			return pettyCashVoucherLiquidationService.pcvlDao.getPettyCashVoucherLiquidationRegister(param, pageSetting);
		}
	}

	/**
	 * Get the statuses of the forms.
	 * @param user The logged in user.
	 * @return The list of {@link Petty Cash Voucher Liquidation FormStatus}.
	 */
	public List<FormStatus> getFormStatuses(User user) {
		List<FormStatus> formStatuses = workflowHandler.getAllStatuses(PCVL_REGISTER_CONF_TYPE, user, false);
		// Set is a unique collection.
		Set<FormStatus> statuses = new HashSet<FormStatus>();
		statuses.addAll(formStatuses);

		FormStatus cancelled = new FormStatus();
		cancelled.setDescription(FormStatus.CANCELLED_LABEL);
		cancelled.setId(FormStatus.CANCELLED_ID);
		cancelled.setSelected(true);
		statuses.add(cancelled);
		return new ArrayList<FormStatus>(statuses);
	}
}
