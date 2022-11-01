package eulap.eb.service;

import java.io.IOException;
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
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.ArReceiptDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.SupplierAdvPaymentDao;
import eulap.eb.dao.SupplierAdvPaymentLineDao;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.SupplierAdvancePayment;
import eulap.eb.domain.hibernate.SupplierAdvancePaymentLine;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.PurchaseOrderDto;
import eulap.eb.web.dto.ResultProperty;

/**
 * Service class that will handle business logic for supplier advance payment form

 */

@Service
public class SupplierAdvPaymentService extends BaseWorkflowService {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private RPurchaseOrderService poService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private SupplierAccountService supplierAccountService;
	@Autowired
	private TermService termService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private SupplierAdvPaymentDao advPaymentDao;
	@Autowired
	private SupplierAdvPaymentLineDao advPaymentLineDao;
	@Autowired
	private ReferenceDocumentDao refDocumentDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ApPaymentDao apPaymentDao;

	/**
	 * Get the list of companies
	 * @param user The current logged user object
	 * @param companyId The company id
	 * @return The list of companies
	 */
	public List<Company> getCompanies(User user, int companyId) {
		return (List<Company>) companyService.getCompaniesWithInactives(user, companyId);
	}

	/**
	 * Get the company object
	 * @param companyId The company id
	 * @return The company object
	 */
	public Company getCompany(int companyId) {
		return companyService.getCompany(companyId);
	}

	/**
	 * Get the list of active currencies. 
	 * Add the inactive currency based on the currencyId parameter.
	 * @param currencyId The currency id.
	 * @return The list of active {@link Currency}.
	 */
	public List<Currency> getActiveCurrencies(Integer currencyId) {
		return currencyService.getActiveCurrencies(currencyId);
	}

	/**
	 * Get the division object
	 * @param divisionId The division id
	 * @return The division object
	 */
	public Division getDivision(int divisionId) {
		return divisionService.getDivision(divisionId);
	}

	/**
	 * Validate the supplier advance payment form
	 * @param supplierAdvPayment The supplier advance payment
	 * @param errors The validation errors
	 */
	public void validateForm(SupplierAdvancePayment supplierAdvPayment, Errors errors) {
		ValidatorUtil.validateCompany(supplierAdvPayment.getCompanyId(), companyService, errors,
				"companyId");

		Integer divisionId = supplierAdvPayment.getDivisionId();
		if (divisionId == null) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.0"));
		} else if (!divisionService.getDivision(divisionId).isActive()) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.1"));
		}

		if (supplierAdvPayment.getRpurchaseOrderId() == null) {
			errors.rejectValue("rpurchaseOrderId", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.2"));
		}

		Date date = supplierAdvPayment.getDate();
		if (date == null) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.3"));
		} else if (!timePeriodService.isOpenDate(date)) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.4"));
		}

		Integer supplierId = supplierAdvPayment.getSupplierId();
		if (supplierId == null) {
			errors.rejectValue("supplierId", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.5"));
		} else if (!supplierService.getSupplier(supplierId).isActive()) {
			errors.rejectValue("supplierId", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.6"));
		}

		Integer supplierAccountId = supplierAdvPayment.getSupplierAcctId();
		if (supplierAccountId == null) {
			errors.rejectValue("supplierAcctId", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.7"));
		} else if (!supplierAccountService.getSupplierAccount(supplierAccountId).isActive()) {
			errors.rejectValue("supplierAcctId", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.8"));
		}

		Date invoiceDate = supplierAdvPayment.getInvoiceDate();
		if (invoiceDate == null) {
			errors.rejectValue("invoiceDate", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.9"));
		} else if (!timePeriodService.isOpenDate(invoiceDate)) {
			errors.rejectValue("invoiceDate", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.10"));
		}

		Date glDate = supplierAdvPayment.getGlDate();
		if (glDate == null) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.11"));
		} else if (!timePeriodService.isOpenDate(glDate)) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.12"));
		}

		Date dueDate = supplierAdvPayment.getDueDate();
		if (dueDate == null) {
			errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.13"));
		} else if (!timePeriodService.isOpenDate(dueDate)) {
			errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.14"));
		}

		String requestor = supplierAdvPayment.getRequestor();
		if (requestor == null || StringFormatUtil.removeExtraWhiteSpaces(requestor).isEmpty()) {
			errors.rejectValue("requestor", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.15"));
		} else if (StringFormatUtil.removeExtraWhiteSpaces(requestor).length() > SupplierAdvancePayment.REQUESTOR_MAX_CHAR) {
			errors.rejectValue("requestor", null, null, String.format(ValidatorMessages.getString("SupplierAdvPaymentService.16"),
					SupplierAdvancePayment.REQUESTOR_MAX_CHAR));
		}

		if (supplierAdvPayment.getCurrencyId() == null) {
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.17"));
		} else if(!currencyService.getCurency(supplierAdvPayment.getCurrencyId()).isActive()) {
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.22"));
		}

		List<SupplierAdvancePayment> refSaps = advPaymentDao.getSapBySapObjectId(supplierAdvPayment.getEbObjectId());
		if(refSaps != null && !refSaps.isEmpty()) {
			String sapNumbers = "";
			int index = 0;
			for(SupplierAdvancePayment sap : refSaps) {
				sapNumbers += index == 0 ? "SAP-" + sap.getSequenceNumber() : ", SAP-" + sap.getSequenceNumber();
				index++;
			}
			errors.rejectValue("amount", null, null, String.format(ValidatorMessages.getString("SupplierAdvPaymentService.23"), sapNumbers));
		}

		Double amount = supplierAdvPayment.getAmount();
		if (amount == null || amount == 0.0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.18"));
		} else if (amount < 0.0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.19"));
		} else {
			List<SupplierAdvancePaymentLine> sapLines = supplierAdvPayment.getAdvPaymentLines();
			Double totalSapLines = 0.00;
			for(SupplierAdvancePaymentLine sapLine : sapLines) {
				totalSapLines += sapLine.getAmount();
			}
			if(supplierAdvPayment.getAmount() > NumberFormatUtil.roundOffTo2DecPlaces(totalSapLines)) {
				//Advance payment must not exceed the unpaid purchase order amount.
				errors.rejectValue("amount", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.21"));
			}
		}

		if (supplierAdvPayment.getReferenceNo() == null || supplierAdvPayment.getReferenceNo().trim().isEmpty()) {
			errors.rejectValue("referenceNo", null, null, ValidatorMessages.getString("SupplierAdvPaymentService.20"));
		}
		refDocumentService.validateReferences(supplierAdvPayment.getReferenceDocuments(), errors);
	}

	/**
	 * Convert the reference purchase order object to supplier advance payment object
	 * @param advPaymentId The supplier advance payment id
	 * @param refPurchaseOrderId The purchase order id
	 * @return The supplier advance payment object
	 */
	public SupplierAdvancePayment convertPoToAdvPayment(Integer advPaymentId, Integer purchaseOrderRefId) {
		RPurchaseOrder purchaseOrder = getPurchaseOrder(purchaseOrderRefId);
		SupplierAdvancePayment advancePayment = new SupplierAdvancePayment();
		// Set header fields
		advancePayment.setRpurchaseOrderId(purchaseOrder.getId());
		advancePayment.setPoNumber(purchaseOrder.getPoNumber().toString());
		Integer companyId = purchaseOrder.getCompanyId();
		advancePayment.setCompanyId(companyId);
		advancePayment.setCompanyName(companyService.getCompanyName(companyId));
		Integer divisionId = purchaseOrder.getDivisionId();
		advancePayment.setDivisionId(divisionId);
		advancePayment.setDivisionName(getDivision(divisionId).getName());
		advancePayment.setBmsNumber(purchaseOrder.getBmsNumber());
		Integer supplierId = purchaseOrder.getSupplierId();
		advancePayment.setTermDays(termService.getTerm(purchaseOrder.getTermId()).getDays());
		advancePayment.setSupplierId(supplierId);
		advancePayment.setSupplierName(supplierService.getSupplier(supplierId).getName());
		Integer supplierAcctId = purchaseOrder.getSupplierAccountId();
		advancePayment.setSupplierAcctId(supplierAcctId);
		advancePayment.setSupplierAcctName(supplierAccountService.getSupplierAccount(supplierAcctId).getName());
		advancePayment.setCurrency(purchaseOrder.getCurrency());
		advancePayment.setCurrencyId(purchaseOrder.getCurrencyId());
		advancePayment.setCurrencyRateId(purchaseOrder.getCurrencyRateId());
		advancePayment.setCurrencyRateValue(purchaseOrder.getCurrencyRateValue());
		// Add selected PO to lines
		List<SupplierAdvancePaymentLine> advPaymentLines = new ArrayList<SupplierAdvancePaymentLine>();
		SupplierAdvancePaymentLine advPaymentLine = new SupplierAdvancePaymentLine();
		advPaymentLine.setReferenceObjectId(purchaseOrder.getEbObjectId());
		// Set particulars
		String poReferenceNo = "PO-" + purchaseOrder.getPoNumber();
		String remarks = purchaseOrder.getRemarks();
		if (remarks != null && !remarks.isEmpty()) {
			poReferenceNo += "; " + StringFormatUtil.removeExtraWhiteSpaces(remarks);
		}
		poReferenceNo += "; " + DateUtil.formatDate(purchaseOrder.getPoDate());
		advPaymentLine.setReferenceNo(poReferenceNo);
		// Set purchase order amount
		advPaymentLine.setAmount(poService.getPoGrandTotal(purchaseOrder));
		advPaymentLines.add(advPaymentLine);
		// Append advance payment associated to the selected PO
		List<SupplierAdvancePayment> advancePayments = advPaymentDao.getPoAdvPayments(advPaymentId, purchaseOrderRefId);
		for (SupplierAdvancePayment sap : advancePayments) {
			advPaymentLine = new SupplierAdvancePaymentLine();
			advPaymentLine.setReferenceObjectId(sap.getEbObjectId());
			// Format particulars
			String advPaymentRefNo = "SAP-" + sap.getSequenceNumber();
			String refAdvPaymentRmrks = sap.getRemarks();
			if (refAdvPaymentRmrks != null && !refAdvPaymentRmrks.isEmpty()) {
				advPaymentRefNo += "; " + StringFormatUtil.removeExtraWhiteSpaces(refAdvPaymentRmrks);
			}
			advPaymentRefNo += "; " + DateUtil.formatDate(sap.getDate());
			advPaymentLine.setReferenceNo(advPaymentRefNo);
			// Set advance payment amount
			double advPaymentRate = sap.getCurrencyRateValue() > 0.0 ? sap.getCurrencyRateValue() : 1.0;
			double advPaymentAmt = NumberFormatUtil.divideWFP(sap.getAmount(), advPaymentRate);
			advPaymentLine.setAmount(-NumberFormatUtil.roundOffNumber(advPaymentAmt, 6));
			advPaymentLines.add(advPaymentLine);
			// Free up memory
			advPaymentLine = null;
		}
		advancePayment.setAdvPaymentLines(advPaymentLines);
		return advancePayment;
	}

	/**
	 * Get the list of purchase order references
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param supplierId The supplier id
	 * @param supplierAcctId The supplier account id
	 * @param poNumber The purchase order number
	 * @param bmsNumber The BMS number
	 * @param dateFrom The date from filter
	 * @param dateTo The date to filter
	 * @param pageNumber The page number
	 * @return The list of purchase order references
	 */
	public Page<PurchaseOrderDto> getPurchaseOrders(Integer companyId, Integer divisionId, Integer supplierId,
			Integer supplierAcctId, Integer poNumber, String bmsNumber, Date dateFrom, Date dateTo,
			Integer pageNumber) {
		return advPaymentDao.getPurchaseOrders(companyId, divisionId, supplierId, supplierAcctId, poNumber,
				bmsNumber, dateFrom, dateTo, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		SupplierAdvancePayment advancePayment = (SupplierAdvancePayment) form;
		boolean isNew = advancePayment.getId() == 0;
		if (isNew) {
			advancePayment.setSequenceNumber(advPaymentDao.generateSequenceNo(advancePayment.getCompanyId(),
					advancePayment.getDivisionId()));
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		SupplierAdvancePayment advancePayment = (SupplierAdvancePayment) form;
		boolean isNew = advancePayment.getId() == 0;
		double currencyRate = advancePayment.getCurrencyRateValue() > 0 ? advancePayment.getCurrencyRateValue() : 1.0;
		AuditUtil.addAudit(advancePayment, new Audit(user.getId(), isNew, new Date()));
		Integer parentObjectId = advancePayment.getEbObjectId();
		if (!isNew) {
			SupplierAdvancePayment savedAdvPayment = getSupplierAdvPayment(advancePayment.getId());
			DateUtil.setCreatedDate(advancePayment, savedAdvPayment.getCreatedDate());
		}

		String remarks = advancePayment.getRemarks();
		if (remarks != null) {
			advancePayment.setRemarks(StringFormatUtil.removeExtraWhiteSpaces(remarks));
		}
		advancePayment.setAmount(NumberFormatUtil.multiplyWFP(advancePayment.getAmount(), currencyRate));
		advPaymentDao.saveOrUpdate(advancePayment);

		List<SupplierAdvancePaymentLine> advancePaymentLines = advancePayment.getAdvPaymentLines();
		for (SupplierAdvancePaymentLine sapl : advancePaymentLines) {
			sapl.setSupplierAdvPaymentId(advancePayment.getId());
			sapl.setAmount(NumberFormatUtil.multiplyWFP(sapl.getAmount(), currencyRate));
			advPaymentLineDao.saveOrUpdate(sapl);
		}

		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				advancePayment.getReferenceDocuments(), true);
	}

	/**
	 * Get the supplier advance payment object
	 * @param advPaymentId The supplier advance payment id
	 * @return The supplier advance payment object
	 */
	public SupplierAdvancePayment getSupplierAdvPayment(Integer advPaymentId) {
		return advPaymentDao.get(advPaymentId);
	}

	/**
	 * Get the supplier advance payment object for viewing and editing
	 * @param advPaymentId The supplier advance payment id
	 * @return The supplier advance payment object for viewing and editing
	 */
	public SupplierAdvancePayment getSupplrAdvPayment(Integer advPaymentId) throws IOException {
		SupplierAdvancePayment advancePayment = getSupplierAdvPayment(advPaymentId);
		RPurchaseOrder purchaseOrder = getPurchaseOrder(advancePayment.getRpurchaseOrderId());
		advancePayment.setPoNumber(purchaseOrder.getPoNumber().toString());
		advancePayment.setTermDays(termService.getTerm(purchaseOrder.getTermId()).getDays());
		advancePayment.setCompanyName(companyService.getCompanyName(advancePayment.getCompanyId()));
		advancePayment.setDivisionName(getDivision(advancePayment.getDivisionId()).getName());
		advancePayment.setSupplierName(supplierService.getSupplier(advancePayment.getSupplierId()).getName());
		advancePayment.setSupplierAcctName(supplierAccountService.getSupplierAccount(advancePayment.getSupplierAcctId()).getName());
		double advPaymentRate = advancePayment.getCurrencyRateValue() > 0.0 ? advancePayment.getCurrencyRateValue() : 1.0;
		advancePayment.setAmount(NumberFormatUtil.roundOffNumber((NumberFormatUtil.divideWFP(advancePayment.getAmount(), advPaymentRate)), 6));
		List<SupplierAdvancePaymentLine> advancePaymentLines = advPaymentLineDao.getAdvancePaymentLines(advPaymentId);
		double totalLineAmount = 0;
		for (SupplierAdvancePaymentLine sapl : advancePaymentLines) {
			double amount = NumberFormatUtil.divideWFP(sapl.getAmount(), advPaymentRate);
			totalLineAmount += amount;
			sapl.setAmount(NumberFormatUtil.roundOffNumber(amount, 6));
		}
		advancePayment.setTotalLineAmount(totalLineAmount);
		advancePayment.setAdvPaymentLines(advancePaymentLines);
		advancePayment.setReferenceDocuments(refDocumentService.processReferenceDocs(advancePayment.getEbObjectId()));
		return advancePayment;
	}

	private RPurchaseOrder getPurchaseOrder (int purhaseOrderId) {
		return poService.getRPurchaseOrder(purhaseOrderId);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return advPaymentDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return advPaymentDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		SupplierAdvancePayment advPayment = advPaymentDao.getByEbObjectId(ebObjectId);
		Integer pId = advPayment.getId();
		FormProperty property = workflowHandler.getProperty(advPayment.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit()  + "?pId=" + pId;
		String printoutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = advPayment.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Supplier Advance Payment - " + advPayment.getSequenceNumber();
		shortDescription = new StringBuffer(title)
				.append("<b> PO No. : </b> " + getPurchaseOrder(advPayment.getRpurchaseOrderId()).getPoNumber())
				.append(" " + advPayment.getSupplier().getName())
				.append(" " + advPayment.getSupplierAccount().getName())
				.append("<b> INVOICE DATE : </b>" + DateUtil.formatDate(advPayment.getInvoiceDate()))
				.append("<b> GL DATE : </b>" + DateUtil.formatDate(advPayment.getGlDate()))
				.append("<b> DUE DATE : </b>" + DateUtil.formatDate(advPayment.getDueDate()));
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printoutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case SupplierAdvancePayment.OBJECT_TYPE_ID:
			return advPaymentDao.getByEbObjectId(ebObjectId);
		case SupplierAdvancePaymentLine.OBJECT_TYPE_ID:
			return advPaymentLineDao.getByEbObjectId(ebObjectId);
		case ReferenceDocument.OBJECT_TYPE_ID:
			return refDocumentDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the list of supplier advance payments for general search
	 * @param divisionId The division id
	 * @param criteria The search criteria
	 * @return The list of supplier advance payments for general search
	 */
	public List<FormSearchResult> searchAdvancePayments(int divisionId, String searchCriteria) {
		Page<SupplierAdvancePayment> supplierAdvPayments = advPaymentDao.searchSupplierAdvancePayments(divisionId,
				searchCriteria, new PageSetting(PageSetting.START_PAGE));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		for(SupplierAdvancePayment sap : supplierAdvPayments.getData()) {
			title = String.valueOf(sap.getCompany().getCompanyCode() + " " + sap.getSequenceNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("PO No.",
					getPurchaseOrder(sap.getRpurchaseOrderId()).getPoNumber().toString()));
			properties.add(ResultProperty.getInstance("Company", sap.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Division", sap.getDivision().getName()));
			properties.add(ResultProperty.getInstance("Supplier", sap.getSupplier().getName()));
			properties.add(ResultProperty.getInstance("Supplier Account", sap.getSupplierAccount().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(sap.getDate())));
			status = sap.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(sap.getId(), title, properties));
		}
		title = null;
		status = null;
		return result;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID 
				&& currentWorkflowLog.getComment() != null && !currentWorkflowLog.getComment().isEmpty()) {
			SupplierAdvancePayment sap = advPaymentDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
			StringBuffer errorMessage = null;
			if (sap != null) {
				Integer sapId = sap.getId();
				//AP Payment references
				List<ApPayment> apPayments = apPaymentDao.getApPaymentBySap(sapId);
				if (apPayments!= null && !apPayments.isEmpty()) {
					errorMessage = new StringBuffer("Transaction cannot be cancelled because it has associated payment/s: ");
					for (ApPayment apPayment : apPayments) {
						errorMessage.append("<br> APP No. : " + apPayment.getVoucherNumber());
					}
					if (errorMessage != null) {
						bindingResult.reject("workflowMessage", errorMessage.toString());
						currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
					}
				}
				//SAP References
				List<SupplierAdvancePayment> saps = advPaymentDao.getSapBySapObjectId(sap.getEbObjectId());
				if(saps != null && !saps.isEmpty()) {
					errorMessage = new StringBuffer("Transaction cannot be cancelled because it has associated advance payment/s: ");
					for(SupplierAdvancePayment advPayment : saps) {
						errorMessage.append("<br> SAP No. : " + advPayment.getSequenceNumber());
					}
					if (errorMessage != null) {
						bindingResult.reject("workflowMessage", errorMessage.toString());
						currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
					}
				}
			}
		}
	}
}
