package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.TaxUtil;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArMiscellaneousDao;
import eulap.eb.dao.ArMiscellaneousLineDao;
import eulap.eb.domain.hibernate.ArMiscellaneous;
import eulap.eb.domain.hibernate.ArMiscellaneousLine;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.TaxType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 *  Service class that will handle all the business logic of {@link ArMiscellaneous}

 *
 */
@Service
public class ArMiscellaneousService extends BaseWorkflowService{
	@Autowired
	private ArMiscellaneousDao arMiscellaneousDao;
	@Autowired
	private ArMiscellaneousLineDao arMiscellaneousLineDao;
	@Autowired
	private ArCustomerAcctDao arCustomerAcctDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ReferenceDocumentService refDocumentService;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return arMiscellaneousDao.get(id).getFormWorkflow() ;
	}

	/**
	 * Get the Ar Miscellaneous object.
	 * @param arMiscellaneousId The unique id of Ar Miscellaneous.
	 * @return The Ar Miscellaneous object.
	 */
	public ArMiscellaneous getArMiscellaneous (Integer arMiscellaneousId) {
		ArMiscellaneous arMiscellaneous = arMiscellaneousDao.get(arMiscellaneousId);
		double rate = arMiscellaneous.getCurrencyRateValue();
//		double grandTotal = 0.0;
		for(ArMiscellaneousLine aml : arMiscellaneous.getArMiscellaneousLines()) {
			aml.setUpAmount(CurrencyUtil.convertMonetaryValues(aml.getUpAmount(),rate));
			aml.setVatAmount(CurrencyUtil.convertMonetaryValues(aml.getVatAmount(), rate));
			aml.setAmount(CurrencyUtil.convertMonetaryValues(aml.getAmount(), rate));
//			grandTotal += aml.getAmount() + aml.getVatAmount();
		}
		double wtAmount = arMiscellaneous.getWtAmount() != null ? CurrencyUtil.convertMonetaryValues(arMiscellaneous.getWtAmount(), rate) : 0.0;
		double wtVatAmount = arMiscellaneous.getWtVatAmount() != null ? CurrencyUtil.convertMonetaryValues(arMiscellaneous.getWtVatAmount(), rate) : 0.0;
		arMiscellaneous.setWtAmount(wtAmount);
		arMiscellaneous.setWtVatAmount(wtVatAmount);
		arMiscellaneous.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(CurrencyUtil.convertMonetaryValues(arMiscellaneous.getAmount(), rate)));
		return arMiscellaneous;
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		ArMiscellaneous arMiscellaneous = (ArMiscellaneous) form;
		arMiscellaneous.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		boolean isNew = arMiscellaneous.getId() == 0;
		Date currentDate = new Date();
		AuditUtil.addAudit(arMiscellaneous, user, currentDate);
		arMiscellaneous.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		Integer parentObjectId = arMiscellaneous.getEbObjectId();
		if (isNew) {
			arMiscellaneous.setSequenceNo(arMiscellaneousDao.generateSequenceNumber(arMiscellaneous.getCompanyId(), arMiscellaneous.getDivisionId()));
		} else {
			ArMiscellaneous savedArMisc = getArMiscellaneous(arMiscellaneous.getId());
			DateUtil.setCreatedDate(arMiscellaneous, savedArMisc.getCreatedDate());
		}
		boolean isValidRate = arMiscellaneous.getCurrencyRateValue() != null && arMiscellaneous.getCurrencyRateValue() != 0;
		double currencyRate = isValidRate ? arMiscellaneous.getCurrencyRateValue() : 1.0;
		arMiscellaneous.setCurrencyRateValue(currencyRate);
		if (arMiscellaneous.getRefNumber() != null)
			arMiscellaneous.setRefNumber(arMiscellaneous.getRefNumber().trim());
		arMiscellaneous.setReceiptNumber(arMiscellaneous.getReceiptNumber().trim());
		arMiscellaneous.setWtAmount(CurrencyUtil.convertAmountToPhpRate(arMiscellaneous.getWtAmount(), currencyRate, true));
		arMiscellaneousDao.saveOrUpdate(arMiscellaneous);
		if (!isNew) {
			List<Integer> toBeDeleted = new ArrayList<Integer>();
			ArMiscellaneous oldArMiscellaneous = arMiscellaneousDao.get(arMiscellaneous.getId());
			List<ArMiscellaneousLine> oldArMiscellaneousLines = oldArMiscellaneous.getArMiscellaneousLines();
			for (ArMiscellaneousLine arM : oldArMiscellaneousLines) 
				toBeDeleted.add(arM.getId());
			arMiscellaneousLineDao.delete(toBeDeleted);
		}
		double totalAmount = 0;
		double totalWtTaxAmt = 0;
		List<ArMiscellaneousLine> armLines = arMiscellaneous.getArMiscellaneousLines();
		CurrencyUtil.convertServiceLineCostsToPhp(armLines, currencyRate);
		for (ArMiscellaneousLine arml : armLines) {
			arml.setId(0);
			arml.setArMiscellaneousId(arMiscellaneous.getId());
			arMiscellaneousLineDao.saveOrUpdate(arml);
			totalAmount += arml.getAmount();
			totalAmount += arml.getVatAmount() != null ? arml.getVatAmount() : 0;
			Integer armlTaxTypeId = arml.getTaxTypeId();
			if (armlTaxTypeId != null &&  armlTaxTypeId.equals(TaxType.GOVERNMENT)) {
				totalWtTaxAmt += NumberFormatUtil.roundOffTo2DecPlaces(arml.getAmount() * TaxUtil.GOVT_TAX_PERCENTAGE);
			}
		}
		arMiscellaneous.setWtVatAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalWtTaxAmt));
		double amount = totalAmount - arMiscellaneous.getWtAmount() - arMiscellaneous.getWtVatAmount();
		arMiscellaneous.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(amount));
		arMiscellaneousDao.update(arMiscellaneous);

		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				arMiscellaneous.getReferenceDocuments(), true);
	}

	/**
	 * Checks if the receipt number is unique.
	 * @param arReceipt The AR Receipt object.
	 * @return True the receipt number is unique, otherwise false.
	 */
	public boolean isUniqueReceiptNo (ArMiscellaneous arMiscellaneous) {
		if (arMiscellaneous.getArCustomerAccountId() == null)
			return true;
		else {
			Integer companyId = arCustomerAcctDao.get(arMiscellaneous.getArCustomerAccountId()).getCompanyId();
			return arMiscellaneousDao.isUniqueReceiptNo(arMiscellaneous, companyId);
		}
	}
	
	/**
	 * Checks if The AR ArMiscellaneous amount is equal to the total AR ArMiscellaneous Line amount.
	 * @param arReceipt The AR ArMiscellaneous object.
	 * @return True if AR ArMiscellaneous amount is equal to the total AR Receipt Line amount,
	 * otherwise false.
	 */
	public boolean isValidAmount (ArMiscellaneous arMiscellaneous) {
		double totalAmount = arMiscellaneous.getAmount() != null
				? NumberFormatUtil.roundOffTo2DecPlaces(arMiscellaneous.getAmount()) : 0.0;
		List<ArMiscellaneousLine> arMiscLines = arMiscellaneous.getArMiscellaneousLines();
		if (arMiscellaneous.getAmount() != null && arMiscLines != null) {
			double totalArLines = 0;
			for (ArMiscellaneousLine arm : arMiscLines) {
				if (arm.getAmount() != null) {
					totalArLines += arm.getAmount();
				}
				if (arm.getVatAmount() != null) {
					totalArLines += arm.getVatAmount();
				}
			}
			if (arMiscellaneous.getWtAcctSettingId() != null) {
				totalArLines -= arMiscellaneous.getWtAmount();
			}
			return !(totalAmount !=  NumberFormatUtil.roundOffTo2DecPlaces(totalArLines -
					(arMiscellaneous.getWtVatAmount() != null ? arMiscellaneous.getWtVatAmount() : 0.0)));
		}
		return true;
	}
	
	/**
	 * Checks if The AR ArMiscellaneous line amount has zero value.
	 * @param arReceipt The AR ArMiscellaneous object.
	 * @return True if AR ArMiscellaneous line amount has zero value.
	 * otherwise false.
	 */
	public boolean hasZeroAmount (ArMiscellaneous arMiscellaneous) {
		List<ArMiscellaneousLine> arMiscellaneousLines = arMiscellaneous.getArMiscellaneousLines();
		if (arMiscellaneous.getAmount() != null && arMiscellaneousLines != null) {
			for (ArMiscellaneousLine arM : arMiscellaneousLines) {
				if (arM.getAmount() == null || (arM.getAmount() != null && arM.getAmount() == 0.0))
					return true;
			}
		}
		return false;
	}
	
	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		ArMiscellaneous arMiscellaneous = 
				arMiscellaneousDao.getArMiscellaneousByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (arMiscellaneous != null) {
			// Checks if receipt date and maturity date is open time period.
			Date receiptDate = arMiscellaneous.getReceiptDate();
			Date maturityDate = arMiscellaneous.getMaturityDate();
			boolean invalidRDate = !timePeriodService.isOpenDate(receiptDate);
			boolean invalidMDate = !timePeriodService.isOpenDate(maturityDate);
			String message = "";
			if (invalidRDate || invalidMDate) {
				if (invalidRDate)
					message = "Receipt date should be in an open time period.";
				if (invalidMDate)
					message += (invalidRDate ? "<br>" : "") + "Maturity date should be in an open time period.";
				bindingResult.reject("workflowMessage", message);
				currentWorkflowLog.setWorkflowMessage(message);
			}
		}
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return arMiscellaneousDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		ArMiscellaneous arMiscellaneous = arMiscellaneousDao.getByEbObjectId(ebObjectId);
		Integer pId = arMiscellaneous.getId();
		FormProperty property = workflowHandler.getProperty(arMiscellaneous.getWorkflowName(), user);
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		String popupLink = "/"+ property.getEdit() +"?pId=" + pId;

		String latestStatus = arMiscellaneous.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Other Receipt - " + arMiscellaneous.getDivision().getName() +" - "+ arMiscellaneous.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + arMiscellaneous.getArCustomer().getName())
				.append(" " + arMiscellaneous.getArCustomerAccount().getName())
				.append("<b> RECEIPT DATE : </b>" + DateUtil.formatDate(arMiscellaneous.getReceiptDate()))
				.append("<b> MATURITY DATE : </b>" + DateUtil.formatDate(arMiscellaneous.getMaturityDate()))
				.append(" " + arMiscellaneous.getAmount());
		
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case ArMiscellaneous.AR_MISC_OBJECT_TYPE_ID:
			return arMiscellaneousDao.getByEbObjectId(ebObjectId);
		case ArMiscellaneousLine.OBJECT_TYPE_ID:
			return arMiscellaneousLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	public List<FormSearchResult> search(User user, String searchCriteria, int divisionId) {
		// Replace comma in searchCriteria with ""
		String searchCriteriaFinal = searchCriteria.replace(",", "");

		Page<ArMiscellaneous> arMiscellaneouses = arMiscellaneousDao.searchArMiscellaneous(searchCriteriaFinal, new PageSetting(1));

		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (ArMiscellaneous arMiscellaneous : arMiscellaneouses.getData()) {
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String customer = arMiscellaneous.getArCustomer().getName();
			String customerAccount = arMiscellaneous.getArCustomerAccount().getName();
			String title = ("Receipt#" + arMiscellaneous.getReceiptNumber());
			properties.add(ResultProperty.getInstance("Receipt Date", DateUtil.formatDate(arMiscellaneous.getReceiptDate())));
			properties.add(ResultProperty.getInstance("Maturity Date", DateUtil.formatDate(arMiscellaneous.getMaturityDate())));
			properties.add(ResultProperty.getInstance("Customer", customer));
			properties.add(ResultProperty.getInstance("Customer Account", customerAccount));
			properties.add(ResultProperty.getInstance("Amount", NumberFormatUtil.format(arMiscellaneous.getAmount())));
			properties.add(ResultProperty.getInstance("Status",
					arMiscellaneous.getFormWorkflow().getCurrentFormStatus().getDescription()));
			result.add(FormSearchResult.getInstanceOf(arMiscellaneous.getId(), title, properties));
		}
		return result;
	}
}
