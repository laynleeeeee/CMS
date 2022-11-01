package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.PettyCashVoucherDao;
import eulap.eb.dao.PettyCashVoucherLiquidationDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidation;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCustodian;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Service class for all transactions under {@link PettyCashVoucher}.

 *
 */
@Service
public class PettyCashVoucherService extends BaseWorkflowService {
	@Autowired
	private PettyCashVoucherDao pcvDao;
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
	private PettyCashVoucherLiquidationDao pcvlDao;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;

	@Override
	public FormWorkflow getFormWorkflow(int pcvId) {
		return getPettyCashVoucher(pcvId).getFormWorkflow();
	}

	/**
	 * Get {@link PettyCashVoucher}
	 * @param pcvId The petty cash voucher id.
	 * @return {@link PettyCashVoucher}
	 */
	public PettyCashVoucher getPettyCashVoucher(Integer pcvId) {
		return pcvDao.get(pcvId);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		PettyCashVoucher pcv = (PettyCashVoucher) form;
		boolean isNew = pcv.getId() == 0;
		pcv.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		Integer parentObjectId = pcv.getEbObjectId();
		AuditUtil.addAudit(pcv, new Audit(user.getId(), isNew, new Date()));
		if (isNew) {
			pcv.setSequenceNo(pcvDao.generateSequenceNo(pcv.getCompanyId(), pcv.getDivisionId()));
		} else {
			PettyCashVoucher savedPcv = getPettyCashVoucher(pcv.getId());
			DateUtil.setCreatedDate(pcv, savedPcv.getCreatedDate());
		}
		pcvDao.saveOrUpdate(pcv);
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				pcv.getReferenceDocuments(), true);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return pcvDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		PettyCashVoucher pcv = pcvDao.getByEbObjectId(ebObjectId);
		Integer pId = pcv.getId();
		FormProperty property = workflowHandler.getProperty(pcv.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit()  + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = pcv.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Petty Cash Voucher - " +pcv.getDivision().getName()+" "+ pcv.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + pcv.getCompany().getName())
				.append(" " + pcv.getDivision().getName())
				.append(" " + DateUtil.formatDate(pcv.getPcvDate()));
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case PettyCashVoucher.PCV_OBJECT_TYPE_ID:
			return pcvDao.getByEbObjectId(ebObjectId);
		case ReferenceDocument.OBJECT_TYPE_ID:
			return referenceDocumentDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Validates {@link PettyCashVoucher}
	 * @param obj {@link PettyCashVoucher}
	 * @param errors The errors
	 */
	public void validate(Object obj, Errors errors) {
		PettyCashVoucher pcv = (PettyCashVoucher) obj;

		ValidatorUtil.validateCompany(pcv.getCompanyId(), companyService, errors, "companyId");
		if(pcv.getDivisionId() == null) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("PettyCashVoucherService.1"));
		} else if (!divisionService.getDivision(pcv.getDivisionId()).isActive()) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("PettyCashVoucherService.2"));
		}
		if(pcv.getUserCustodianId() == null) {
			errors.rejectValue("userCustodianId", null, null, ValidatorMessages.getString("PettyCashVoucherService.3"));
		} else {
			UserCustodian uc = userCustodianService.getUserCustodian(pcv.getUserCustodianId());
			if (!uc.isActive()) {
				errors.rejectValue("userCustodianId", null, null, ValidatorMessages.getString("PettyCashVoucherService.4"));
			}
			if(!custodianAccountService.getCustodianAccount(uc.getCustodianAccountId()).isActive()) {
				errors.rejectValue("userCustodianId", null, null, ValidatorMessages.getString("PettyCashVoucherService.10"));
			}
		}

		if(pcv.getPcvDate() == null) {
			errors.rejectValue("pcvDate", null, null, ValidatorMessages.getString("PettyCashVoucherService.5"));
		}
		pcv.setRequestor(removeExtraSpaces(pcv.getRequestor()));
		if(pcv.getRequestor().isEmpty() || pcv.getRequestor() == null) {
			errors.rejectValue("requestor", null, null, ValidatorMessages.getString("PettyCashVoucherService.6"));
		} else if(pcv.getRequestor().length() > 50) {
			errors.rejectValue("requestor", null, null, ValidatorMessages.getString("PettyCashVoucherService.9"));
		}
		pcv.setReferenceNo(removeExtraSpaces(pcv.getReferenceNo()));
		if(pcv.getReferenceNo().isEmpty() || pcv.getReferenceNo() == null) {
			errors.rejectValue("referenceNo", null, null, ValidatorMessages.getString("PettyCashVoucherService.12"));
		} else if(pcv.getReferenceNo().length() > 50) {
			errors.rejectValue("referenceNo", null, null, ValidatorMessages.getString("PettyCashVoucherService.13"));
		}
		pcv.setDescription(removeExtraSpaces(pcv.getDescription()));
		if(pcv.getDescription().isEmpty() || pcv.getDescription() == null) {
			errors.rejectValue("description", null, null, ValidatorMessages.getString("PettyCashVoucherService.7"));
		}

		if(pcv.getAmount() == null) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("PettyCashVoucherService.8"));
		} else if(pcv.getAmount() <= 0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("PettyCashVoucherService.11"));
		}

		refDocumentService.validateReferences(pcv.getReferenceDocuments(), errors);
	}

	@SuppressWarnings("null")
	private String removeExtraSpaces(String string) {
		if(string != null || !string.trim().isEmpty()) {
			string = StringFormatUtil.removeExtraWhiteSpaces(string).trim();
		}
		return string;
	}

	/**
	 * Get the list of EmployeeDocument by criteria.
	 * @param searchCriteria The searchCriteria.
	 * @param user The current logged user.
	 * @return
	 */
	public List<FormSearchResult> searchPettyCashVouchers(int divisionId, String searchCriteria, User user){
		List<PettyCashVoucher> pcvs = pcvDao.searchPettyCashVouchers(divisionId, searchCriteria, user);
		List<FormSearchResult> results = new ArrayList<>();
		List<ResultProperty> properties = null;
		for(PettyCashVoucher pcv : pcvs){
			String title = "PCV No. " + 
					pcv.getSequenceNo().toString();
			properties = new ArrayList<>();
			properties.add(ResultProperty.getInstance("Company", pcv.getCompany().getName())); 
			properties.add(ResultProperty.getInstance("Division", pcv.getDivision().getName())); 
			properties.add(ResultProperty.getInstance("Custodian", pcv.getUserCustodian().getCustodianAccount().getCustodianAccountName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(pcv.getPcvDate())));
			properties.add(ResultProperty.getInstance("Requestor", pcv.getRequestor()));
			properties.add(ResultProperty.getInstance("Reference", pcv.getReferenceNo()));
			properties.add(ResultProperty.getInstance("Amount", pcv.getAmount().toString()));
			String status = pcv.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status))); 
			results.add(FormSearchResult.getInstanceOf(pcv.getId(), title, properties));
		}
		return results;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		PettyCashVoucher pcv = pcvDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		if (pcv != null) {
			int pcvId = pcv.getId();
			String message = "Petty Cash Voucher with sequence number " + pcv.getSequenceNo() + " cannot " +
					"be cancelled because it has associated Liquidation: ";
			List<PettyCashVoucherLiquidation> pcvls = pcvlDao.getAssociatedLiquidations(pcvId);
			String strPCVLS = "";
			if (pcvls != null) {
				for (PettyCashVoucherLiquidation pcvl : pcvls) {
					strPCVLS += "<br> Sequence No: " + pcvl.getSequenceNo();
				}
			}
			if (!strPCVLS.isEmpty()) {
				message += strPCVLS;
				bindingResult.reject("workflowMessage", message);
				currentWorkflowLog.setWorkflowMessage(message);
			}
		}
	}

	/**
	 * Get the petty cash voucher statuses.
	 * @param user The user logged.
	 * @return The petty cash voucher statuses.
	 */
	public List<FormStatus> getTransactionStatuses(User user) {
		List<FormStatus> statuses = new ArrayList<FormStatus>();
		FormStatus cancelledStatus = formStatusDao.get(FormStatus.CANCELLED_ID);
		statuses = workflowServiceHandler.getAllStatuses("PettyCashVoucher1", user, false);
		statuses.add(cancelledStatus);
		return statuses;
	}
}
