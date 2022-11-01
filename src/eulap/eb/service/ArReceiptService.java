package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AcArLineDao;
import eulap.eb.dao.ArAdvancePaymentDao;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArInvoiceDao;
import eulap.eb.dao.ArReceiptDao;
import eulap.eb.dao.ArReceiptLineDao;
import eulap.eb.dao.ArReceiptTransactionDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.dao.DeliveryReceiptDao;
import eulap.eb.domain.hibernate.AcArLine;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptAdvancePayment;
import eulap.eb.domain.hibernate.ArReceiptLine;
import eulap.eb.domain.hibernate.ArReceiptLineType;
import eulap.eb.domain.hibernate.ArReceiptTransaction;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 *  Service class that will handle all the business logic of {@link ArReceipt}

 *
 */
@Service
public class ArReceiptService extends BaseWorkflowService{
	private static Logger logger = Logger.getLogger(ArReceiptService.class);
	@Autowired
	private ArReceiptDao arReceiptDao;
	@Autowired
	private ArReceiptTransactionDao arReceiptTransactionDao;
	@Autowired
	private ArCustomerAcctDao arCustomerAcctDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private AcArLineDao acArLineDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ArAdvancePaymentDao arAdvancePaymentDao;
	@Autowired
	private CustomerAdvancePaymentDao capDao;
	@Autowired
	private ArReceiptLineDao arReceiptLineDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private ArInvoiceDao arInvoiceDao;
	@Autowired
	private DeliveryReceiptDao drDao;

	/**
	 * Get the Ar Receipt object.
	 * @param arReceiptId The unique id of Ar Receipt.
	 * @return The Ar Receipt object.
	 */
	public ArReceipt getArReceipt (Integer arReceiptId) {
		return arReceiptDao.get(arReceiptId);
	}

	public ArReceipt getArReceiptWithArReceiptLines(Integer arReceiptId) {
		ArReceipt arReceipt = getArReceipt(arReceiptId);
		if(arReceipt != null) {
			double currencyRate = arReceipt.getCurrencyRateValue() > 0 ? arReceipt.getCurrencyRateValue() : 1.0;
			List<ArReceiptLine> arReceiptLines = arReceiptLineDao.getArReceiptLines(arReceiptId);
			processArLinesData(arReceiptLines);
			arReceipt.setArReceiptLines(arReceiptLines);
			arReceipt.setAmount(CurrencyUtil.convertMonetaryValues(arReceipt.getAmount(), currencyRate));
			arReceipt.setRetention(CurrencyUtil.convertMonetaryValues(arReceipt.getRetention(), currencyRate));
			arReceipt.setRecoupment(CurrencyUtil.convertMonetaryValues(arReceipt.getRecoupment(), currencyRate));
		}
		return arReceipt;
	}

	private void processArLinesData(List<ArReceiptLine> arReceiptLines) {
		for(ArReceiptLine arReceiptLine : arReceiptLines) {
			double amount = CurrencyUtil.convertMonetaryValues(arReceiptLine.getAmount(), arReceiptLine.getCurrencyRateValue());
			String refNumber = arReceiptLine.getReferenceNo() + " " + NumberFormatUtil.format(amount);
			arReceiptLine.setAmount(amount);
			arReceiptLine.setReferenceNo(refNumber);
		}
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		ArReceipt arReceipt = (ArReceipt) form;
		boolean isNew = arReceipt.getId() == 0;
		if (isNew) {
			arReceipt.setSequenceNo(arReceiptDao.generateSequenceNo(arReceipt.getCompanyId(), arReceipt.getDivisionId()));
		}
		//Convert to PHP.
		double rate = arReceipt.getCurrencyRateValue() != 0 ? arReceipt.getCurrencyRateValue() : 1;
		arReceipt.setCurrencyRateValue(rate);
		arReceipt.setAmount(CurrencyUtil.convertAmountToPhpRate(arReceipt.getAmount(), rate, true));
		arReceipt.setRecoupment(CurrencyUtil.convertAmountToPhpRate(arReceipt.getRecoupment(), rate, true));
		arReceipt.setRetention(CurrencyUtil.convertAmountToPhpRate(arReceipt.getRetention(), rate, true));
		for(ArReceiptLine arl : arReceipt.getArReceiptLines()) {
			arl.setAmount(CurrencyUtil.convertAmountToPhpRate(arl.getAmount(), arl.getCurrencyRateValue(), true));
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		ArReceipt arReceipt = (ArReceipt) form;
		arReceipt.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		boolean isNew = arReceipt.getId() == 0;
		AuditUtil.addAudit(arReceipt, user, new Date());
		arReceipt.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		if (!isNew) {
			ArReceipt savedArReceipt = getArReceipt(arReceipt.getId());
			DateUtil.setCreatedDate(arReceipt, savedArReceipt.getCreatedDate());
		}
		String refNumber = arReceipt.getRefNumber();
		if (refNumber != null) {
			arReceipt.setRefNumber(StringFormatUtil.removeExtraWhiteSpaces(refNumber));
		}
		String receiptNumber = arReceipt.getReceiptNumber();
		if (receiptNumber != null) {
			arReceipt.setReceiptNumber(StringFormatUtil.removeExtraWhiteSpaces(receiptNumber));
		}
		Integer parentObjectId = arReceipt.getEbObjectId();
		arReceiptDao.saveOrUpdate(arReceipt);

		List<ArReceiptLine> arReceiptLines = arReceipt.getArReceiptLines();
		for(ArReceiptLine arReceiptLine : arReceiptLines) {
			arReceiptLine.setArReceiptId(arReceipt.getId());
			arReceiptLineDao.saveOrUpdate(arReceiptLine);
		}
		//Save reference documents
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				arReceipt.getReferenceDocuments(), true);
	}

	/**
	 * Get the list of AR receipt transactions by AR receipt.
	 * @param arReceiptId The unique id of AR receipt.
	 * @return The list of AR receipt transactions.
	 */
	public List<ArReceiptTransaction> getArReceiptTransactions(Integer arReceiptId) {
		return arReceiptTransactionDao.getArReceiptTransactions(arReceiptId, null);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return arReceiptDao.get(id).getFormWorkflow();
	}

	/**
	 * Checks if the receipt number is unique.
	 * @param arReceipt The AR Receipt object.
	 * @return True the receipt number is unique, otherwise false.
	 */
	public boolean isUniqueReceiptNo (ArReceipt arReceipt) {
		if (arReceipt.getArCustomerAccountId() == null)
			return true;
		else {
			Integer companyId = arCustomerAcctDao.get(arReceipt.getArCustomerAccountId()).getCompanyId();
			return arReceiptDao.isUniqueReceiptNo(arReceipt, companyId);
		}
	}

	/**
	 * Checks if The AR Receipt amount is equal to the total AR Receipt Transaction amount.
	 * @param arReceipt The AR Receipt object.
	 * @return True if AR Receipt amount is equal to the total AR Receipt Transaction amount,
	 * otherwise false.
	 */
	public boolean isValidAmount (ArReceipt arReceipt) {
		List<ArReceiptLine> arReceiptLines = arReceipt.getArReceiptLines();
		Double headerAmount = arReceipt.getAmount();
		double recoupment = arReceipt.getRecoupment() != null ? arReceipt.getRecoupment() : 0;
		double retention = arReceipt.getRetention() != null ? arReceipt.getRetention() : 0;
		if (headerAmount != null && arReceiptLines != null) {
			double totalArReceiptAmt = 0;
			for(ArReceiptLine arReceiptLine : arReceiptLines) {
				if(arReceiptLine.getAmount() != null) {
					totalArReceiptAmt += arReceiptLine.getAmount();
				}
			}
			totalArReceiptAmt = totalArReceiptAmt - recoupment - retention;
			if (NumberFormatUtil.roundOffTo2DecPlaces(headerAmount) == NumberFormatUtil.roundOffTo2DecPlaces(totalArReceiptAmt)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		ArReceipt arReceipt = arReceiptDao.getArReceiptByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (arReceipt != null) {
			// Checks if receipt date and maturity date is open time period.
			Date receiptDate = arReceipt.getReceiptDate();
			Date maturityDate = arReceipt.getMaturityDate();
			boolean invalidRDate = !timePeriodService.isOpenDate(receiptDate);
			boolean invalidMDate = !timePeriodService.isOpenDate(maturityDate);
			if (invalidRDate || invalidMDate) {
				String message = "";
				if (invalidRDate)
					message = "Receipt date should be in an open time period.";
				if (invalidMDate)
					message += (invalidRDate ? "<br>" : "") + "Maturity date should be in an open time period.";
				bindingResult.reject("workflowMessage", message);
				currentWorkflowLog.setWorkflowMessage(message);
			} else {
				if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
					List<ArReceiptTransaction> arRTransactions = arReceiptTransactionDao.getAllByRefId("arReceiptId", arReceipt.getId());
					for (ArReceiptTransaction arRT : arRTransactions) {
						arRT.setAmount(0.0);
						arReceiptTransactionDao.saveOrUpdate(arRT);
					}
				}
			}
		}
	}

	/**
	 * Get the total receipt amount of the customer.
	 * @param arCustomerId The ar customer id.
	 * @return The total receipt amount.
	 */
	public double getCustomerTotalReceipt(Integer arCustomerId) {
		return NumberFormatUtil.roundOffTo2DecPlaces(arReceiptDao.getCustomerTotalReceipt(arCustomerId));
	}

	/**
	 * Get the remaining balance per customer account.
	 * @param isEdit Check if edit.
	 * @param arCustomerAccountId The customer account id.
	 * @return The remaining balance.
	 */
	private double getCustAcctRemBalance (boolean isEdit, Integer arCustomerAccountId) {
		double totalTransactionAmt = arTransactionDao.getTotalTransactionAmount(arCustomerAccountId);
		double totalReceiptPayment = arReceiptTransactionDao.getTotalReceiptPayment(arCustomerAccountId, isEdit);
		return  NumberFormatUtil.roundOffTo2DecPlaces(totalTransactionAmt - totalReceiptPayment);
	}

	public String validateRemainingBal (boolean isEdit, Integer arCustomerAccountId, Double totalTAmount) {
		if (arCustomerAccountId != null && totalTAmount != null) {
			double remainingBal = NumberFormatUtil.roundOffTo2DecPlaces(getCustAcctRemBalance(isEdit, arCustomerAccountId));
			if ((double)totalTAmount > remainingBal) {
				return "Remaining balance for selected account is "+NumberFormatUtil.format(remainingBal);
			}
		}
		return null;
	}

	public void processOtherCharges(List<AcArLine> acArLines, int arReceiptId, boolean isNew) {
		if(!isNew) {
			logger.debug("Deleting saved Other Charges.");
			List<Integer> toBeDeletedIds = new ArrayList<Integer>();
			List<AcArLine> toBeDeleted = getAcArLines(arReceiptId);
			for (AcArLine acArline : toBeDeleted) {
				toBeDeletedIds.add(acArline.getId());
			}
			acArLineDao.delete(toBeDeletedIds);
		}

		for (AcArLine arReeipt : acArLines) {
			arReeipt.setArReceiptId(arReceiptId);
			acArLineDao.save(arReeipt);
		}
		logger.info("Successfully saved Other Charges of AR Receipt "+arReceiptId);
	}

	/**
	 * Get the AR line of AR receipt by arReceiptId
	 * @param arReceiptId The AR receipt id.
	 * @return The AR line of AR receipt
	 */
	public List<AcArLine> getAcArLines(int arReceiptId) {
		List<AcArLine> acArLines =  acArLineDao.getArReceipt(arReceiptId);
		for (AcArLine acArLine : acArLines) {
			acArLine.setArLineSetupName(acArLine.getArLineSetup().getName());
			acArLine.setUnitMeasurementName(acArLine.getUnitMeasurement() != null ?
					acArLine.getUnitMeasurement().getName() : "");
		}
		return acArLines;
	}

	/**
	 * Get the list of ArRereceipt
	 * @param pId The ArReceipt Id.
	 * @return The list of ArRereceipt
	 */
	public List<ArReceipt> getArReceipts(Integer pId) {
		List<ArReceipt> receipts = arReceiptDao.getArReceipts(pId);
		List<ArReceiptTransaction> arRTransactions = arReceiptTransactionDao.getAllByRefId("arReceiptId", pId);
		List<AcArLine> acArLines = getAcArLines(pId);
		for (ArReceipt arReceipt : receipts) {
			arReceipt.setArRTransactions(arRTransactions);
			arReceipt.setAcArLines(acArLines);
			arReceipt.setArAdvPayments(getArReceAdvPayments(arReceipt.getId()));
		}
		return receipts;
	}

	/**
	 * Get the grand total of the AR receipt.
	 * @param arReceiptId The AR receipt id.
	 * @return The grand total of the AR receipt.
	 */
	public Double getGrandTotal(Integer arReceiptId) {
		Double grandTotal = 0.00;
		Double arLineTotal = 0.00;
		List<AcArLine> acArLines =  acArLineDao.getArReceipt(arReceiptId);
		for (AcArLine acArLine : acArLines) {
			arLineTotal += acArLine.getAmount();
		}
		Double transactionTotal = 0.00;
		List<ArReceiptTransaction> transactions = arReceiptTransactionDao.getArReceiptTransactions(arReceiptId, null);
		for (ArReceiptTransaction arReceiptTransaction : transactions) {
			transactionTotal += arReceiptTransaction.getAmount();
		}
		grandTotal = arLineTotal + transactionTotal;
		return NumberFormatUtil.roundOffTo2DecPlaces(grandTotal);
	}

	/**
	 * Get the list of Account Collections of the Transaction.
	 * @param arTransactionId The unique id of the AR Transaction.
	 * @return The list of Account Collections.
	 */
	public List<ArReceipt> getPayments(int arTransactionId) {
		return arReceiptDao.getTransactionPayments(arTransactionId);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return arReceiptDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		ArReceipt arReceipt = arReceiptDao.getByEbObjectId(ebObjectId);
		Integer pId = arReceipt.getId();
		FormProperty property = workflowHandler.getProperty(arReceipt.getWorkflowName(), user);
		String popupLink = "/arReceipt/"+arReceipt.getDivisionId()+"/form/new?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = arReceipt.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Account Collection - " + arReceipt.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + arReceipt.getArCustomer().getName())
				.append(" " + arReceipt.getArCustomerAccount().getName())
				.append("<b> RECEIPT DATE : </b>" + DateUtil.formatDate(arReceipt.getReceiptDate()))
				.append("<b> MATURITY DATE : </b>" + DateUtil.formatDate(arReceipt.getMaturityDate()))
				.append(" " + arReceipt.getAmount());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case ArReceipt.OBJECT_TYPE_ID:
			return arReceiptDao.getByEbObjectId(ebObjectId);
		case AcArLine.OBJECT_TYPE_ID:
			return acArLineDao.getByEbObjectId(ebObjectId);
		case ArReceiptAdvancePayment.OBJECT_TYPE_ID:
			return arAdvancePaymentDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the list of {@link ArReceiptAdvancePayment} by {@link ArReceipt} id.
	 * @param arReceiptId The {@link ArReceipt} id.
	 * @return The list of {@link ArReceiptAdvancePayment}.
	 */
	public List<ArReceiptAdvancePayment> getArReceAdvPayments(Integer arReceiptId) {
		List<ArReceiptAdvancePayment> advPayments = new ArrayList<ArReceiptAdvancePayment>();
		if(arReceiptId != null) {
			advPayments = arAdvancePaymentDao.getArRecAdvPayments(arReceiptId);
			if(advPayments != null) {
				for(ArReceiptAdvancePayment advPayment : advPayments) {
					CustomerAdvancePayment cap = capDao.get(advPayment.getCustomerAdvancePaymentId());
					if(cap != null) {
						advPayment.setCapNumber(cap.getCapNumber().toString());
						advPayment.setAmount(cap.getCash());
					}
				}
			}
		}
		return advPayments;
	}

	/**
	 * Compute the subtotal.
	 * subtotal = total ar lines amount + total ar transactions amount.
	 * @param arReceipt The {@link ArReceipt}.
	 * @return The subtotal.
	 */
	public Double computeSubTotal(ArReceipt arReceipt) {
		List<ArReceiptTransaction> arRTransactions = arReceipt.getArRTransactions();
		List<AcArLine> acArLines = arReceipt.getAcArLines();
		Double total = 0.0;
		if(arRTransactions != null) {
			for(ArReceiptTransaction arTransaction : arRTransactions) {
				if(arTransaction.getAmount() != null) {
					total += arTransaction.getAmount();
				}
			}
		}
		if(acArLines != null) {
			for(AcArLine acArLine : acArLines) {
				if(acArLine.getAmount() != null) {
					total += acArLine.getAmount();
				}
			}
		}
		return total;
	}

	/**
	 * Compute total advance payments.
	 * @param arReceipt The {@link ArReceipt}.
	 * @return The total advance payments.
	 */
	public Double computeAdvPayments(ArReceipt arReceipt) {
		List<ArReceiptAdvancePayment> arAdvPayments = arReceipt.getArAdvPayments();
		Double total = 0.0;
		if(arAdvPayments != null) {
			for(ArReceiptAdvancePayment advPayment : arAdvPayments) {
				if(advPayment.getAmount() != null) {
					total += advPayment.getAmount();
				}
			}
		}
		return -(total);
	}

	/**
	 * Get the list of {@link ArReceiptLine} based on the paramaters.
	 * @param companyId The company id. 
	 * @param divisionId The division id.
	 * @param currencyId The currency id.
	 * @param arCustomerAcctId The ar customer account id.
	 * @param transNumber The transaction number.
	 * @return list of {@link ArReceiptLine}.
	 */
	public Collection<ArReceiptLine> getArReceiptLines(Integer companyId, Integer divisionId, Integer currencyId, 
			Integer arCustomerAcctId, String transNumber, String refObjIds) {
		Page<ArReceiptLine> arReceiptLines = arReceiptLineDao.getArReceiptLines(companyId, divisionId, currencyId, arCustomerAcctId, transNumber, refObjIds, new PageSetting(1, 10));
		for(ArReceiptLine arReceiptLine : arReceiptLines.getData()) {
			double rate = arReceiptLine.getCurrencyRateValue();
			double amount = CurrencyUtil.convertMonetaryValues(arReceiptLine.getAmount(), rate);
			arReceiptLine.setAmount(amount);
			arReceiptLine.setReferenceNo(arReceiptLine.getReferenceNo() + " " + NumberFormatUtil.format(amount));
		}
		return arReceiptLines.getData();
	}

	/**
	 * Get the list of {@link CustomerAdvancePayment} related transactions.
	 * @param arInvoiceObjectId The {@link ArInvoice} object id.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param currencyId The currency id.
	 * @param refObjIds The to be excluded reference object ids.
	 * @return The list of {@link CustomerAdvancePayment} related transactions.
	 */
	public List<ArReceiptLine> getCapReferences(Integer arInvoiceObjectId, Integer companyId, Integer divisionId,
			Integer currencyId, String refObjIds) {
		return arReceiptLineDao.getCapReference(arInvoiceObjectId, companyId, divisionId, currencyId, refObjIds);
	}

	/**
	 * Compute the total advance payment.
	 * @param arReceiptLines The list of {@link ArReceiptLine}.
	 * @return The total advance payment.
	 */
	public double getTotalAdvPayment(List<ArReceiptLine> arReceiptLines) {
		double totalAdvPayment = 0;
		if(arReceiptLines != null) {
			for(ArReceiptLine arReceiptLine : arReceiptLines) {
				totalAdvPayment += arReceiptLine.getArReceiptLineTypeId() == ArReceiptLineType.CUSTOMER_ADVANCE_PAYMENT ? arReceiptLine.getAmount() : 0.00;
			}
		}
		return totalAdvPayment;
	}

	/**
	 * Compute the total amount excluding the advance payments.
	 * @param arReceiptLines The list of {@link ArReceiptLine}.
	 * @return The total amount.
	 */
	public double getSubTotal(List<ArReceiptLine> arReceiptLines) {
		double subTotal = 0;
		if(arReceiptLines != null) {
			for(ArReceiptLine arReceiptLine : arReceiptLines) {
				subTotal += arReceiptLine.getArReceiptLineTypeId() != ArReceiptLineType.CUSTOMER_ADVANCE_PAYMENT ? arReceiptLine.getAmount() : 0.00;
			}
		}
		return subTotal;
	}

	/**
	 * Search the AR Receipts.
	 * @param searchCriteria The criteria that the use inputed, receipt number, amount.
	 * @param divisionId The division id.
	 * @param pageSetting The page setting.
	 * @return The list of AR Receipts.
	 */
	public List<FormSearchResult> searchArReceipt(User user, Integer divisionId, String searchCriteria) {
		// Replace comma in searchCriteria with ""
		String searchCriteriaFinal = searchCriteria.replace(",", "");
		Page<ArReceipt> arReceipts = arReceiptDao.searchArReceipts(searchCriteriaFinal, divisionId, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (ArReceipt arReceipt : arReceipts.getData()) {
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String customer = arReceipt.getArCustomer().getName();
			String customerAccount = arReceipt.getArCustomerAccount().getName();
			String title = ("Receipt#" + arReceipt.getReceiptNumber());
			properties.add(ResultProperty.getInstance("Receipt Date", DateUtil.formatDate(arReceipt.getReceiptDate())));
			properties.add(ResultProperty.getInstance("Maturity Date", DateUtil.formatDate(arReceipt.getMaturityDate())));
			properties.add(ResultProperty.getInstance("Customer", customer));
			properties.add(ResultProperty.getInstance("Customer Account", customerAccount));
			properties.add(ResultProperty.getInstance("Amount", NumberFormatUtil.format(arReceipt.getAmount())));
			properties.add(ResultProperty.getInstance("Status", 
					arReceipt.getFormWorkflow().getCurrentFormStatus().getDescription()));
			result.add(FormSearchResult.getInstanceOf(arReceipt.getId(), title, properties));
		}
		return result;
	}

	public Double negateAmount(Double amount) {
		if(amount != null) {
			return NumberFormatUtil.negateAmount(amount);
		}
		return amount;
	}

	/**
	 * Get the remaining cap balance of the reference ar invoice transaction.
	 * @param ariRefObjectId The ar transaction reference object id.
	 * @param arReceiptId The ar receipt id.
	 * @return The remaining cap balance.
	 */
	public double getCapRemainingBalance(Integer ariRefObjectId, Integer arReceiptId) {
		ArInvoice ari = arInvoiceDao.getByEbObjectId(ariRefObjectId);
		double balance = 0;
		if(ari != null) {
			DeliveryReceipt dr = drDao.get(ari.getDeliveryRecieptId());
			balance = capDao.getRemainingCapBalance(dr.getSalesOrderId(), null, arReceiptId);
		}
		return balance;
	}

	/**
	 * Get the list of {@link ArReceipt} by AR transaction id
	 * @param arTransactionId The AR transaction id
	 * @return The list of {@link ArReceipt} objects.
	 */
	public List<ArReceipt> getArReceiptsByTrId(Integer arTransactionId) {
		return arReceiptDao.getArReceiptsByTrId(arTransactionId);
	}
}
