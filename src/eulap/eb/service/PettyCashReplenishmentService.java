package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import eulap.eb.dao.APLineDao;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.PCVLiquidationLineDao;
import eulap.eb.dao.PettyCashReplenishmentLineDao;
import eulap.eb.dao.PettyCashVoucherLiquidationDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.TaxTypeDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.APLine;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.CustodianAccount;
import eulap.eb.domain.hibernate.CustodianAccountSupplier;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PettyCashReplenishmentLine;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidation;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidationLine;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.TaxType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCustodian;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.report.PCVRRegisterParam;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.APLinesDto;
import eulap.eb.web.dto.AccountAnalysisReport;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.PCRRegisterMainDto;
import eulap.eb.web.dto.PCReplenishmentRegisterDto;
import eulap.eb.web.dto.ResultProperty;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Service class that will handle business logic for Petty Cash Replenishment

 */

@Service
public class PettyCashReplenishmentService extends BaseWorkflowService {
	@Autowired
	private APInvoiceService apInvoiceService;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private ReferenceDocumentDao referenceDocDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private PettyCashReplenishmentLineDao pcrDao;
	@Autowired
	private ObjectToObjectDao o2oDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PCVLiquidationLineDao pcvllDao;
	@Autowired
	private UserCustodianService userCustodianService;
	@Autowired
	private CustodianAccountService custodianAccountService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountCombinationService accountCombinationService;
	@Autowired
	private PettyCashVoucherLiquidationDao pcvlDao;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private TaxTypeDao ttDao;
	@Autowired
	private APLineDao apLineDao;
	@Autowired
	private CustodianAccountSupplierService casService;
	@Autowired
	private ApPaymentDao apPaymentDao;
	@Autowired
	private AccountDao accountDao;

	public static final int MAX_DECIMAL_PLACES = 6;

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice apInvoice = (APInvoice) form;
		if (apInvoice.getId() == 0) {
			apInvoice.setSequenceNumber(apInvoiceDao.generateReplenishmentSequenceNo(apInvoice.getInvoiceTypeId(),
					apInvoice.getCompanyId(), apInvoice.getDivisionId()));
		} else {
			List<PettyCashReplenishmentLine> savedPCRL = pcrDao.getAllByRefId(PettyCashReplenishmentLine.FIELD.apInvoiceId.name(), apInvoice.getId());
			for(PettyCashReplenishmentLine pcrl : savedPCRL) {
				pcrDao.delete(pcrl);
			}
			savedPCRL = null;
			List<APLine> savedApLines = apLineDao.getAllByRefId(APLine.FIELD.aPInvoiceId.name(), apInvoice.getId());
			if (savedApLines != null && !savedApLines.isEmpty()) {
				for (APLine apLine : savedApLines) {
					apLineDao.delete(apLine);
				}
			}
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice apInvoice = (APInvoice) form;
		boolean isNew = apInvoice.getId() == 0;
		Integer parentObjectId = apInvoice.getEbObjectId();
		AuditUtil.addAudit(apInvoice, new Audit(user.getId(), isNew, new Date()));
		UserCustodian userCustodian = userCustodianService.getUserCustodian(apInvoice.getUserCustodianId());
		CustodianAccountSupplier cas = casService.getCAS(userCustodian.getCustodianAccountId(), null, null);
		if (!isNew) {
			APInvoice savedApInvoice = apInvoiceDao.get(apInvoice.getId());
			DateUtil.setCreatedDate(savedApInvoice, savedApInvoice.getCreatedDate());
			savedApInvoice = null;
		}
		apInvoice.setSupplierId(cas.getSupplierId());
		apInvoice.setSupplierAccountId(cas.getSupplierAccountId());
		apInvoice.setTermId(userCustodian.getCustodianAccount().getTermId());
		apInvoice.setDueDate(apInvoice.getGlDate());
		apInvoice.setInvoiceDate(apInvoice.getGlDate());
		apInvoice.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		apInvoice.setCurrencyId(Currency.DEFUALT_PHP_ID);
		apInvoice.setCurrencyRateValue(Currency.DEFUALT_PHP_VALUE);
		apInvoiceDao.saveOrUpdate(apInvoice);
		savePettyCashReplenishments(apInvoice);
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				apInvoice.getReferenceDocuments(), true);
	}

	private void savePettyCashReplenishments(APInvoice apInvoice) {
		List<PettyCashReplenishmentLine> pcrls = apInvoice.getPcrls();
		List<Domain> toBeSavedPCRLs = new ArrayList<Domain>();
		for (PettyCashReplenishmentLine pcrl : pcrls) {
			AccountCombination ac = accountCombinationService.getAccountCombination(apInvoice.getCompanyId(), apInvoice.getDivisionId(), pcrl.getAccountId());
			pcrl.setApInvoiceId(apInvoice.getId());
			pcrl.setAccountCombinationId(ac.getId());
			toBeSavedPCRLs.add(pcrl);
			//Overwrite PettyCashVoucherLiquidationLine
			EBObject refObjectId = o2oDao.getOtherReference(pcrl.getEbObjectId(), PettyCashReplenishmentLine.PCRL_PCVLL_RELATIONSHIP);
			if(refObjectId != null) {
				PettyCashVoucherLiquidationLine pcvll = pcvllDao.getByEbObjectId(refObjectId.getId());
				pcvll.setAccountCombinationId(ac.getId());
				pcvllDao.update(pcvll);
			}
		}
		pcrDao.batchSave(toBeSavedPCRLs);

		List<APLine> apLines = apInvoice.getaPlines();
		List<Domain> toBeSavedLines = new ArrayList<Domain>();
		if(apLines != null && !apLines.isEmpty()) {
			for (APLine apLine : apLines) {
				AccountCombination acc = accountCombinationService.getAccountCombination(apInvoice.getCompanyId(), apInvoice.getDivisionId(), apLine.getAccountId());
				apLine.setaPInvoiceId(apInvoice.getId());
				apLine.setAccountCombinationId(acc.getId());
				toBeSavedLines.add(apLine);
			}
			apLineDao.batchSave(toBeSavedLines);
		}
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return apInvoiceService.getFormWorkflow(id);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return apInvoiceService.getFormByWorkflow(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		APInvoice apInvoice = apInvoiceDao.getByEbObjectId(ebObjectId);
		Integer pId = apInvoice.getId();
		FormProperty property = workflowHandler.getProperty(apInvoice.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit()  + "?pId=" + pId;
		String printoutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = apInvoice.getFormWorkflow().getCurrentFormStatus().getDescription();
		Division div = divisionService.getDivision(apInvoice.getDivisionId());
		String title = "Petty Cash Replenishment - " + div.getName() + " " + apInvoice.getSequenceNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + apInvoice.getUserCustodian().getCustodianAccount().getCustodianName())
				.append(" " + apInvoice.getUserCustodian().getCustodianAccount().getCustodianAccountName())
				.append("<b> Date : </b>" + DateUtil.formatDate(apInvoice.getGlDate()))
				.append(NumberFormatUtil.format(apInvoice.getAmount()));
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), "", popupLink, printoutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case APInvoice.PCR_OBJECT_TYPE_ID:
				return apInvoiceDao.getByEbObjectId(ebObjectId);
			case PettyCashReplenishmentLine.PCRL_OBJECT_TYPE_ID:
				return pcrDao.getByEbObjectId(ebObjectId);
			case APLine.AP_LINE_OBJECT_TYPE:
				return apLineDao.getByEbObjectId(ebObjectId);
			case ReferenceDocument.OBJECT_TYPE_ID:
				return referenceDocDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get list of {@link PettyCashReplenishmentLine} based on division and user custodian
	 * @param divisionId The division id
	 * @param userCustodianId The user custodian id
	 * @param pageNumber The page number
	 * @return List of {@link PettyCashReplenishmentLine}
	 * @throws IOException 
	 */
	public List<PettyCashReplenishmentLine> getReplenishments(Integer divisionId, Integer userCustodianId, Integer apInvoiceId) throws IOException{
		List<PettyCashReplenishmentLine> pcrls = new ArrayList<PettyCashReplenishmentLine>();
		APInvoice apInvoice = null;
		if (apInvoiceId != null && apInvoiceId != 0) {
			apInvoice = getApInvoicePcr(apInvoiceId); 
			if (userCustodianId.equals(apInvoice.getUserCustodianId())) {
				pcrls = apInvoice.getPcrls();
			}
		} else {
			Page<PettyCashReplenishmentLine> ppcrls = pcrDao.getReplenishments(divisionId, userCustodianId,
					new PageSetting(PageSetting.START_PAGE, PageSetting.NO_PAGE_CONSTRAINT));
			for (PettyCashReplenishmentLine pcrl : ppcrls.getData()) {
				pcrls.add(pcrl);
			}
		}
		return pcrls;
	}

	/**
	 * Get both normal and summarized {@link APLine}
	 * @param accounts The account string
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @return The {@link APLinesDto}
	 */
	public APLinesDto getApLinesAndSummarizedLines(String accounts, Integer companyId, Integer divisionId) {
		APLinesDto apLinesDto = new APLinesDto();
		List<APLine> apLines = new ArrayList<APLine>();
		List<APLine> summarizedapLines = new ArrayList<APLine>();
		if(accounts != "") {
			apLines = getSummaryOfAccounts(accounts, companyId, divisionId, false);
			summarizedapLines = getSummaryOfAccounts(accounts, companyId, divisionId, true);
		}
		apLinesDto.setApLines(apLines);
		apLinesDto.setSummarizedApLines(summarizedapLines);
		return apLinesDto;
	}

	/**
	 * Get the list of {@link APLine} based on accounts
	 * @param accounts The string of accounts
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param isSummarized If is summarized
	 * @return List of {@link APLine}
	 */
	public List<APLine> getSummaryOfAccounts (String accounts, Integer companyId, Integer divisionId, boolean isSummarized){
		List<APLine> apLines = new ArrayList<APLine>();
		if(accounts != "") {
			String[] sequenceNoAndAccounts = accounts.trim().split("-|;");
			int length = sequenceNoAndAccounts.length;
			int[] pcvllIdAndAccountId = new int[length];

			for(int i = 0; i < sequenceNoAndAccounts.length ; i++){
				if(sequenceNoAndAccounts[i] != "") {
					pcvllIdAndAccountId[i] = Integer.parseInt(sequenceNoAndAccounts[i]);
				}
			}
			APLine apLine = null;
			if(pcvllIdAndAccountId.length % 2 == 0) {
				for(int i = 0; i < pcvllIdAndAccountId.length ; i+=2) {
					if(pcvllIdAndAccountId[i] != 0 && pcvllIdAndAccountId[i+1] != 0) {
						apLine = new APLine();
						PettyCashVoucherLiquidationLine pcvll = pcvllDao.get(pcvllIdAndAccountId[i]);
						apLine.setAccountId(pcvllIdAndAccountId[i+1]);
						Account account = accountService.getAccount(apLine.getAccountId());
						apLine.setAccountName(account.getAccountName());
						apLine.setAccountNumber(account.getNumber());
						AccountCombination ac = accountCombinationService.getAccountCombination(companyId, divisionId, apLine.getAccountId());
						apLine.setAccountCombinationId(ac.getId());
						apLine.setGrossAmount(pcvll.getUpAmount());
						apLine.setVatAmount(pcvll.getVatAmount() != null ? pcvll.getVatAmount() : 0);
						if(pcvll.getTaxTypeId() != null) {
							apLine.setTaxTypeId(pcvll.getTaxTypeId());
						}
						apLine.setAmount(pcvll.getAmount());
						apLines.add(apLine);
					}
				}
			}
		}
		return summarizeAPLines(apLines, isSummarized);
	}

	/**
	 * Combine and sum the amounts of {@link APLine} with the same account.
	 * @param aPLine The {@link APLine}.
	 * @param isSummarized If is summarized
	 * @return The processed list of {@link APLine}.
	 */
	private List<APLine> summarizeAPLines(List<APLine> apLines, boolean isSummarized) {
		Map<Integer, APLine> discHM = new HashMap<Integer, APLine>();
		APLine apLine = null;
		Integer key = null;
		if(isSummarized) {
			for(APLine apL : apLines) {
					key = apL.getAccountId();
					if(discHM.containsKey(key)) {
						apLine = discHM.get(key);
						if(apLine.getGrossAmount() != null) {
							apLine.setGrossAmount(apLine.getGrossAmount() + apL.getGrossAmount());
						}
						if(apLine.getTaxTypeId() != null) {
							apLine.setTaxTypeId(apLine.getTaxTypeId());
						}
						if(apLine.getVatAmount() != null) {
							apLine.setVatAmount(apLine.getVatAmount() + (apL.getVatAmount() != null ? apL.getVatAmount() : 0));
						}
						if(apLine.getAmount() != 0) {
							apLine.setAmount(apLine.getAmount() + apL.getAmount());
						}
						discHM.put(key, apLine);
					} else {
						discHM.put(key, apL);
					}
				}
			return new ArrayList<APLine>(discHM.values());
		}
		return apLines;
	}

	/**
	 * Get {@link APInvoice} with {@link PettyCashReplenishmentLine}
	 * @param apInvoiceId The ap invoice id
	 * @return {@link APInvoice}
	 * @throws IOException
	 */
	public APInvoice getApInvoicePcr(Integer apInvoiceId) throws IOException {
		APInvoice apInvoice = getPettyCashReplenishment(apInvoiceId);
		apInvoice.setReferenceDocuments(refDocumentService.processReferenceDocs(apInvoice.getEbObjectId()));
		return apInvoice;
	}

	private APInvoice getPettyCashReplenishment(Integer apInvoiceId) {
		APInvoice apInvoice = apInvoiceDao.get(apInvoiceId);
		List<PettyCashReplenishmentLine> pcrls = pcrDao.getAllByRefId(PettyCashReplenishmentLine.FIELD.apInvoiceId.name(), apInvoiceId);
		List<APLine> apLines = new ArrayList<APLine>();
		APLine apLine = null;
		for (PettyCashReplenishmentLine pcrl : pcrls) {
			EBObject refObjectId = o2oDao.getOtherReference(pcrl.getEbObjectId(), PettyCashReplenishmentLine.PCRL_PCVLL_RELATIONSHIP);
			if(refObjectId != null) {
				PettyCashVoucherLiquidationLine pcvll = pcvllDao.getByEbObjectId(refObjectId.getId());
				PettyCashVoucherLiquidation pcvl = pcvlDao.get(pcvll.getPcvlId());
				Supplier supplier = supplierService.getSupplier(pcvll.getSupplierId());
				AccountCombination ac = accountCombinationService.getAccountCombination(pcrl.getAccountCombinationId());
				TaxType tt = null;
				if(pcvll.getTaxTypeId() != null) {
					tt = ttDao.get(pcvll.getTaxTypeId());
					pcrl.setTaxName(tt.getName());
				}
				pcrl.setRefenceObjectId(pcvll.getEbObjectId());
				//pcrls
				pcrl.setPcvllId(pcvll.getId());
				pcrl.setPcvlDateString(DateUtil.formatDate(pcvl.getPcvlDate()));
				pcrl.setSequenceNo(pcvl.getSequenceNo());
				pcrl.setBmsNumber(pcvll.getBmsNumber() != null ? pcvll.getBmsNumber() : "");
				pcrl.setOrNumber(pcvll.getOrNumber());
				pcrl.setSupplierName(supplier.getName());
				pcrl.setSupplierTin(StringFormatUtil.processTin(supplier.getTin()));
				pcrl.setBrgyStreet(supplier.getStreetBrgy());
				pcrl.setCity(supplier.getCityProvince());
				pcrl.setDescription(pcvll.getDescription() != null ? pcvll.getDescription() : "");
				pcrl.setDivisionName(ac.getDivision().getName());
				pcrl.setDivisionId(ac.getDivisionId());
				pcrl.setAccountName(ac.getAccount().getAccountName());
				pcrl.setAccountId(ac.getAccountId());
				pcrl.setGrossAmount(pcvll.getUpAmount());
				pcrl.setVatAmount(pcvll.getVatAmount() != null ? pcvll.getVatAmount() : 0);
				pcrl.setAmount(pcvll.getAmount());
				//apLines
				apLine = new APLine();
				apLine.setDivisionId(pcrl.getDivisionId());
				apLine.setDivisionName(pcrl.getDivisionName());
				apLine.setAccountId(pcrl.getAccountId());
				Account account = accountService.getAccount(pcrl.getAccountId());
				apLine.setAccountName(account.getAccountName());
				apLine.setAccountNumber(account.getNumber());
				apLine.setAccountCombinationId(pcrl.getAccountCombinationId());
				apLine.setGrossAmount(pcvll.getUpAmount());
				apLine.setTaxTypeId(pcvll.getTaxTypeId());
				apLine.setVatAmount(pcvll.getVatAmount());
				apLine.setAmount(pcvll.getAmount());
			}
			apLines.add(apLine);
		}
		apInvoice.setaPlines(summarizeAPLines(apLines, false));
		apInvoice.setSummarizedApLines(summarizeAPLines(apInvoice.getaPlines(), true));
		apInvoice.setPcrls(pcrls);
		return apInvoice;
	}

	/**
	 * Validate the AP invoice goods/services form
	 * @param apInvoice The AP invoice object
	 * @param errors The validation errors
	 */
	public void validateForm(APInvoice apInvoice, Errors errors) {
		ValidatorUtil.validateCompany(apInvoice.getCompanyId(), companyService, errors, "companyId");

		Integer divisionId = apInvoice.getDivisionId();
		if (divisionId == null) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ApInvoiceGsService.1"));
		} else if (!divisionService.getDivision(divisionId).isActive()) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ApInvoiceGsService.2"));
		}

		if(apInvoice.getUserCustodianId() == null) {
			errors.rejectValue("userCustodianId", null, null, ValidatorMessages.getString("PettyCashVoucherService.3"));
		} else {
			UserCustodian uc = userCustodianService.getUserCustodian(apInvoice.getUserCustodianId());
			if (!uc.isActive()) {
				errors.rejectValue("userCustodianId", null, null, ValidatorMessages.getString("PettyCashVoucherService.4"));
			}
			if(!custodianAccountService.getCustodianAccount(uc.getCustodianAccountId()).isActive()) {
				errors.rejectValue("userCustodianId", null, null, ValidatorMessages.getString("PettyCashVoucherService.10"));
			}
		}

		if (apInvoice.getGlDate() == null) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("APInvoiceValidator.13"));
		} else if (!timePeriodService.isOpenDate(apInvoice.getGlDate())) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("APInvoiceValidator.14"));
		}
		boolean hasPcrl = false;
		Integer row = 0;
		for(PettyCashReplenishmentLine pcrl : apInvoice.getPcrls()) {
			if(pcrl.getPcvllId() != null) {
				++row;
				hasPcrl = true;
				if(pcrl.getAccountId() == null) {
					errors.rejectValue("pcrlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.7") + row);
				} else if(!accountService.getAccount(pcrl.getAccountId()).isActive()) {
					errors.rejectValue("pcrlErrorMessage", null, null, ValidatorMessages.getString("PettyCashVoucherLiquidationService.13") + row);
				}
			}
		}
		if(!hasPcrl) {
			errors.rejectValue("pcrlErrorMessage", null, null, ValidatorMessages.getString("PettyCashReplenishmentService.1"));
		}

		refDocumentService.validateReferences(apInvoice.getReferenceDocuments(), errors);
	}

	/**
	 * Get the list of AP invoices for general search
	 * @param typeId The invoice type id
	 * @param divisionId The division id
	 * @param searchCriteria The search criteria
	 * @return The list of AP invoices for general search
	 */
	public List<FormSearchResult> searchInvoices(int typeId, int divisionId, String searchCriteria) {
		Page<APInvoice> apInvoices = apInvoiceDao.searchInvoiceGoodsAndServices(typeId, divisionId,
				searchCriteria, new PageSetting(PageSetting.START_PAGE));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		for(APInvoice apInvoice : apInvoices.getData()) {
			title = String.valueOf(apInvoice.getCompany().getCompanyCode() + " " + apInvoice.getSequenceNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Sequence No.", apInvoice.getSequenceNumber().toString()));
			properties.add(ResultProperty.getInstance("Company", apInvoice.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Division", apInvoice.getDivision().getName()));
			properties.add(ResultProperty.getInstance("Custodian", apInvoice.getUserCustodian().getCustodianAccount().getCustodianName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(apInvoice.getGlDate())));
			status = apInvoice.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(apInvoice.getId(), title, properties));
		}
		title = null;
		status = null;
		return result;
	}

	/**
	 * Get PCF Amount
	 * @param companyId The company id
	 * @param userCustodianId The user custodian id
	 * @param asOfDate The as of date
	 * @return The PCF Amount
	 */
	public Double getPCFAmount(Integer companyId, Integer userCustodianId, Date asOfDate) {
		if (companyId != null && userCustodianId != null && asOfDate != null) {
			UserCustodian uc = userCustodianService.getUserCustodianObj(userCustodianId);
			CustodianAccount ca = uc.getCustodianAccount();
			AccountAnalysisReport beginningBal = accountDao.getAccountBalance(companyId,
					ca.getFdAccountCombination().getAccount(), ca.getFdAccountCombination().getDivision().getNumber(),
					ca.getFdAccountCombination().getDivision().getNumber(), asOfDate, null);
			return beginningBal.getBalance();
		}
		return 0.0;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		APInvoice apInvoice = apInvoiceDao.getAPInvoiceByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (apInvoice != null) {
			StringBuffer errorMessage = null;
			Integer objectId = apInvoice.getEbObjectId();
			//Check if used in AP Payment forms.
			List<ApPayment> apPayments = apPaymentDao.getApPaymentsByObjectId(objectId);
			if(apPayments != null && !apPayments.isEmpty()) {
				errorMessage = new StringBuffer("Petty Cash Replenishment cannot be cancelled because it has associated transaction/s: ");
				//AP Payment
				for (ApPayment payment : apPayments) {
					errorMessage.append("<br> APP No. : " + payment.getVoucherNumber());
				}
				if(errorMessage != null) {
					bindingResult.reject("workflowMessage", errorMessage.toString());
					currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
				}
			}
		}
	}

	/**
	 * Get the petty cash replenishment register report data
	 * @param param The report parameter object
	 * @return The petty cash replenishment register report data
	 */
	public JRDataSource generatePCReplenishmentRegister(PCVRRegisterParam param) {
		EBJRServiceHandler<PCRRegisterMainDto> handler = new PCReplenishmentHandler(param, this);
		return new EBDataSource<PCRRegisterMainDto>(handler);
	}

	private static class PCReplenishmentHandler implements EBJRServiceHandler<PCRRegisterMainDto> {
		private PCVRRegisterParam param;
		private PettyCashReplenishmentService pcrService;

		private PCReplenishmentHandler (PCVRRegisterParam param, PettyCashReplenishmentService pcrService) {
			this.param = param;
			this.pcrService = pcrService;
		}

		@Override
		public void close() throws IOException {
			pcrService = null;
		}

		@Override
		public Page<PCRRegisterMainDto> nextPage(PageSetting pageSetting) {
			Page<PCReplenishmentRegisterDto> pcrRegisterDtos = pcrService.pcrDao.getReplenishmentsRegister(param, pageSetting);
			Map<Integer, PCRRegisterMainDto> pcrRegHm = new HashMap<Integer, PCRRegisterMainDto>();
			PCRRegisterMainDto mainDto = null;
			PCRRegisterMainDto editedDto = null;
			List<PCReplenishmentRegisterDto> pcrrDtos = null;
			List<PCReplenishmentRegisterDto> editedPcrrDtos = null;
			for (PCReplenishmentRegisterDto dto : pcrRegisterDtos.getData()) {
				int pcvrId = dto.getPvcrId();
				if (pcrRegHm.containsKey(pcvrId)) {
					editedDto = pcrRegHm.get(pcvrId);
					editedPcrrDtos = editedDto.getPrcRegisterDtos();
					editedPcrrDtos.add(dto);
					editedDto.setPrcRegisterDtos(editedPcrrDtos);
					pcrRegHm.put(pcvrId, editedDto);
					editedDto = null;
				} else {
					mainDto = new PCRRegisterMainDto();
					pcrrDtos = new ArrayList<PCReplenishmentRegisterDto>();
					pcrrDtos.add(dto);
					mainDto.setPcvrId(pcvrId);
					mainDto.setPrcRegisterDtos(pcrrDtos);
					pcrRegHm.put(pcvrId, mainDto);
				}
			}
			List<PCRRegisterMainDto> mainDtos = new ArrayList<PCRRegisterMainDto>(pcrRegHm.values());
			return new Page<PCRRegisterMainDto>(pageSetting, mainDtos, pcrRegHm.values().size());
		}
	}
}
