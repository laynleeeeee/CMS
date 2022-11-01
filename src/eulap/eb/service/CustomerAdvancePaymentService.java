package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.ArReceiptDao;
import eulap.eb.dao.CAPDeliveryDao;
import eulap.eb.dao.CapArLineDao;
import eulap.eb.dao.CapLineDao;
import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.dao.CustomerAdvancePaymentItemDao;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptAdvancePayment;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.CAPDelivery;
import eulap.eb.domain.hibernate.CapArLine;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentItem;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentLine;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.CapDto;

/**
 * Class that handles all the business logic of {@link CustomerAdvancePayment}

 *
 */
@Service
public class CustomerAdvancePaymentService extends BaseWorkflowService{
	private final Logger logger = Logger.getLogger(CustomerAdvancePaymentService.class);
	@Autowired
	private CustomerAdvancePaymentDao capDao;
	@Autowired
	private CustomerAdvancePaymentItemDao capItemDao;
	@Autowired
	private CapArLineDao capArLineDao;
	@Autowired
	private CustomerAdvancePaymentItemService capItemService;
	@Autowired
	private CAPDeliveryDao capDeliveryDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private SalesOrderDao salesOrderDao;
	@Autowired
	private SalesOrderService salesOrderService;
	@Autowired
	private ArCustomerDao arCustomerDao;
	@Autowired
	private CapLineDao capLineDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private ArReceiptDao arReceiptDao;

	/**
	 * Get the customer advance payment object by id.
	 * @param customerAdvancePaymentId The customer advance payment id.
	 * @return The customer advance payment object.
	 */
	public CustomerAdvancePayment getCustomerAdvancePayment(Integer customerAdvancePaymentId) {
		return capDao.get(customerAdvancePaymentId);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return capDao.get(id).getFormWorkflow();
	}

	/**
	 * Get the list of Customer Advance Payment AR Lines.
	 * @param capId The id of the customer advance payment.
	 * @return List of Customer Advance Payment AR Lines.
	 */
	public List<CapArLine> getCapArLines(Integer capId) {
		return capArLineDao.getCapArLines(capId);
	}

	/**
	 * Checks if the cash is less than the total amount.
	 * @param totalAmount The total amount of the customer advance payment items.
	 * @param cash The cash entered by the user.
	 * @return True if cash is less than amount, otherwise false.
	 */
	public boolean isCashLessTAmount (double totalAmount, double cash) {
		return NumberFormatUtil.roundOffTo2DecPlaces(cash) < 
				NumberFormatUtil.roundOffTo2DecPlaces(totalAmount);
	}

	/**
	 * Get the customer advance payment object by id.
	 * @param capId The customer advance payment id.
	 * @return The customer advance payment object.
	 */
	public CustomerAdvancePayment getCapPrint(Integer capId) {
		CustomerAdvancePayment cap = capDao.get(capId);
		if (cap != null) {
			double currencyRate = cap.getCurrencyRateValue() > 0 ? cap.getCurrencyRateValue() : 1.0;
			cap.setAmount(CurrencyUtil.convertMonetaryValues(cap.getAmount(), currencyRate, true));
			List<CustomerAdvancePaymentLine> capLines = capLineDao.getCapLines(capId);
			convertCapLinesAmtByCurrency(capLines, currencyRate);
			cap.setCapLines(capLines);
		}
		return cap;
	}

	/**
	 * Convert {@link CustomerAdvancePaymentLine} amount from php rate to the saved rate. 
	 * @param capLines The {@link CustomerAdvancePaymentLine}.
	 * @param currencyRate The saved currency rate.
	 */
	private void convertCapLinesAmtByCurrency(List<CustomerAdvancePaymentLine> capLines, double currencyRate) {
		for(CustomerAdvancePaymentLine capLine : capLines) {
			capLine.setAmount(capLine.getAmount() / currencyRate);
		}
	}

	public CustomerAdvancePayment getCapWithItems (Integer capId) throws IOException {
		CustomerAdvancePayment cap = getCustomerAdvancePayment(capId);
		cap.setSoNumber(cap.getSalesOrder().getSequenceNumber());
		//Process amount  by currency rate
		double currencyRate = cap.getCurrencyRateValue() > 0 ? cap.getCurrencyRateValue() : 1.0;
		cap.setAmount(cap.getAmount() / currencyRate);
		List<CustomerAdvancePaymentLine> capLines = capLineDao.getCapLines(capId);
		convertCapLinesAmtByCurrency(capLines, currencyRate);
		cap.setCapLines(capLines);
		cap.setReferenceDocuments(refDocumentService.processReferenceDocs(cap.getEbObjectId()));
		return cap;
	}

	/**
	 * Get the detailed AR Lines of Customer Advance Payment.
	 * @param capId The id of the customer advance payment.
	 * @return The list of AR Lines
	 */
	public List<CapArLine> getDetailedArLines(Integer capId) {
		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		otherCharges.addAll(getCapArLines(capId));
		List<AROtherCharge> processedList = getDetailOtherCharges(otherCharges);
		List<CapArLine> ret = new ArrayList<CapArLine>();
		for (AROtherCharge oc : processedList) {
			ret.add((CapArLine) oc);
		}
		return ret;
	}

	/**
	 * Get the detailed Other Charges and set the AR Line Setup name and UOM name.
	 * @return The list of Other Charges.
	 */
	public List<AROtherCharge> getDetailOtherCharges(List<AROtherCharge> otherCharges) {
		if(otherCharges.isEmpty()) {
			logger.debug("No AR Lines/Other Charges to be processed.");
			return otherCharges;
		}
		List<AROtherCharge> ret = new ArrayList<AROtherCharge>();
		for (AROtherCharge oc : otherCharges) {
			logger.debug("Setting the AR Line setup name and UOM name to AR Line: "+oc.getId());
			oc.setArLineSetupName(oc.getArLineSetup().getName());
			if(oc.getUnitOfMeasurementId() != null) {
				oc.setUnitMeasurementName(oc.getUnitMeasurement().getName());
			}
			ret.add(oc);
		}
		logger.debug("Successfully retrieved "+ret.size()+" AR Lines.");
		return ret;
	}

	/**
	 * Compute the total sales and total other charges of the CAP.
	 * @param capId The id of the customer advance payment.
	 * @return The total sales + other charges.
	 */
	public double computeTotalCapSales(int capId) {
		Double totalCAPSales = SaleItemUtil.computeTotalAmt(capItemDao.getAllByRefId("customerAdvancePaymentId", capId));
		List<CapArLine> arLines = getCapArLines(capId);
		if(!arLines.isEmpty()) {
			List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>(arLines);
			totalCAPSales += SaleItemUtil.computeTotalOtherCharges(otherCharges);
		}
		return totalCAPSales;
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		CustomerAdvancePayment customerAdvPayment = (CustomerAdvancePayment) form;
		logger.info("Saving customer advance payment.");
		boolean isNew = customerAdvPayment.getId() == 0;
		Date currentDate = new Date();
		AuditUtil.addAudit(customerAdvPayment, new Audit(user.getId(), isNew, currentDate));
		double currencyRate = customerAdvPayment.getCurrencyRateValue() > 0 ? customerAdvPayment.getCurrencyRateValue() : 1.0;
		if (isNew) {
			int capNumber = capDao.generateCapNumber(customerAdvPayment.getCompanyId(),
					customerAdvPayment.getCustomerAdvancePaymentTypeId());
			customerAdvPayment.setCapNumber(capNumber);
		} else {
			CustomerAdvancePayment savedCap = getCustomerAdvancePayment(customerAdvPayment.getId());
			DateUtil.setCreatedDate(customerAdvPayment, savedCap.getCreatedDate());
		}
		Integer parentObjectId = customerAdvPayment.getEbObjectId();
		customerAdvPayment.setAmount(customerAdvPayment.getAmount() * currencyRate);
		capDao.saveOrUpdate(customerAdvPayment);

		List<CustomerAdvancePaymentLine> capLines = customerAdvPayment.getCapLines();
		if (capLines != null) {
			double totalAmount = 0;
			for (CustomerAdvancePaymentLine capl : capLines) {
				capl.setCustomerAdvPaymentId(customerAdvPayment.getId());
				double amount = NumberFormatUtil.roundOffTo2DecPlaces(capl.getAmount());
				amount = CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true);
				totalAmount += amount;
				capl.setAmount(amount);
				capLineDao.saveOrUpdate(capl);
			}
			customerAdvPayment.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalAmount));
			capDao.update(customerAdvPayment);
		}

		//Save reference documents
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				customerAdvPayment.getReferenceDocuments(), true);
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return capDao.getAllByRefId("ebObjectId", ebObjectId).get(0);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		CustomerAdvancePayment advancePayment = capDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		// Check only if form will be cancelled
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			List<CAPDelivery> deliveries = capDeliveryDao.getDeliveryByCapId(advancePayment.getId());
			StringBuffer errorMessage = null;
			if(!deliveries.isEmpty()) {
				errorMessage = new StringBuffer("Unable to cancel form, corresponding document was created. "
						+ "Reference form/s:");
				for (CAPDelivery capd : deliveries) {
					errorMessage.append("<br>" + capd.getShortDescription());
				}

				if(errorMessage != null) {
					bindingResult.reject("workflowMessage", errorMessage.toString());
					currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
				}
			}
			List<CustomerAdvancePayment> caps = capDao.getCapsByCapId(advancePayment.getId());
			if(caps != null && !caps.isEmpty()) {
				errorMessage = new StringBuffer("Transaction cannot be cancelled because it has associated advance payment/s: ");
				for (CustomerAdvancePayment cap : caps) {
					errorMessage.append("<br> CAP No. : " + cap.getCapNumber());
				}
				if(errorMessage != null) {
					bindingResult.reject("workflowMessage", errorMessage.toString());
					currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
				}
			}
			List<ArReceipt> arReceipts = arReceiptDao.getArReceiptsByCapId(advancePayment.getId());
			if(arReceipts != null && !arReceipts.isEmpty()) {
				errorMessage =  new StringBuffer("Transaction cannot be cancelled because it has associated Account Collection/s: ");
				for(ArReceipt arReceipt : arReceipts) {
					errorMessage.append("<br> AC No. : " + arReceipt.getSequenceNo());
				}
				if(errorMessage != null) {
					bindingResult.reject("workflowMessage", errorMessage.toString());
					currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
				}
			}
		}
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return capDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		CustomerAdvancePayment cap = capDao.getByEbObjectId(ebObjectId);
		Integer pId = cap.getId();
		Integer typeId = cap.getCustomerAdvancePaymentTypeId();
		FormProperty property = workflowHandler.getProperty(cap.getWorkflowName(), user);
		String popupLink = "/customerAdvancePayment/"+typeId+"/form?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		String formName = "";
		if(typeId != null) {
			switch (typeId) {
			case CustomerAdvancePaymentType.RETAIL:
				formName = "Customer Advance Payment";
				break;
			case CustomerAdvancePaymentType.INDIV_SELECTION:
				formName = "Customer Advance Payment - IS";
				break;
			case CustomerAdvancePaymentType.WIP_SPECIAL_ORDER:
				formName = "Customer Advance Payment - IS";
				break;
			case CustomerAdvancePaymentType.CAP_CENTRAL:
				formName = "Customer Advance Payment - Central";
				break;
			case CustomerAdvancePaymentType.CAP_NSB3:
				formName = "Customer Advance Payment - NSB 3";
				break;
			case CustomerAdvancePaymentType.CAP_NSB4:
				formName = "Customer Advance Payment - NSB 4";
				break;
			case CustomerAdvancePaymentType.CAP_NSB5:
				formName = "Customer Advance Payment - NSB 5";
				break;
			case CustomerAdvancePaymentType.CAP_NSB8:
				formName = "Customer Advance Payment - NSB 8";
				break;
			case CustomerAdvancePaymentType.CAP_NSB8A:
				formName = "Customer Advance Payment - NSB 8A";
				break;
			default:
				formName = "Customer Advance Payment";
				break;
			}
		}
		String latestStatus = cap.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName + " - " + cap.getCapNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + cap.getArCustomer().getName())
				.append(" " + cap.getArCustomerAccount().getName())
				.append("<b> RECEIPT DATE : </b>" + DateUtil.formatDate(cap.getReceiptDate()))
				.append("<b> MATURITY DATE : </b>" + DateUtil.formatDate(cap.getMaturityDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case CustomerAdvancePayment.CAP_RETAIL_OBJECT_TYPE_ID:
		case CustomerAdvancePayment.CAP_IS_OBJECT_TYPE_ID:
		case CustomerAdvancePayment.CAP_WIP_SO_OBJECT_TYPE_ID:
			return capDao.getByEbObjectId(ebObjectId);
		case CustomerAdvancePaymentItem.OBJECT_TYPE_ID:
			return capItemDao.getByEbObjectId(ebObjectId);
		case CapArLine.OBJECT_TYPE_ID:
			return capArLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the customer advance payment data source
	 * @param pId The customer advance payment id
	 * @return The customer advance payment data source
	 */
	public List<CapDto> getDataSource(int pId) {
		List<CapDto> datasource = new ArrayList<CapDto>();
		List<CustomerAdvancePaymentItem> capItems = capItemService.getCAPItems(pId, null, null, false);
		List<CapArLine> capArLines = getCapArLines(pId);
		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		otherCharges.addAll(capArLines);
		List<AROtherCharge> processedList = getDetailOtherCharges(otherCharges);
		List<CapArLine> processedArLines = new ArrayList<CapArLine>();
		for (AROtherCharge oc : processedList) {
			processedArLines.add((CapArLine) oc);
		}
		datasource.add(CapDto.getInstanceOf(capItems, processedArLines));
		return datasource;
	}

	/**
	 * The total amount
	 * @param pId The form id
	 * @param isVAT True if get the total VAT, otherwise false
	 * @return The total amount
	 */
	public double getTotalAmount(int pId, boolean isVAT) {
		double totalVAT = 0;
		double totalAmount = 0;
		List<CustomerAdvancePaymentItem> capItems = capItemService.getCAPItemsPrintOut(pId);
		for (CustomerAdvancePaymentItem capi : capItems) {
			totalAmount += capi.getAmount() != null ? capi.getAmount() : 0.0;
			totalVAT += capi.getVatAmount() != null ? capi.getVatAmount() : 0.0;
		}
		capItems = null;

		List<CapArLine> capArLines = getCapArLines(pId);
		for (CapArLine capal : capArLines) {
			totalAmount += capal.getAmount() != null ? capal.getAmount() : 0.0;
			totalVAT += capal.getVatAmount() != null ? capal.getVatAmount() : 0.0;
		}
		capArLines = null;

		if (isVAT) {
			return totalVAT;
		}
		return totalAmount;
	}

	/**
	 * Get the sales order object by sequence number and company id and converts it to Customer Advance Payment.
	 * @param seqNo The sequence number.
	 * @param companyId The company id. 
	 * @return The sales order object.
	 */
	public CustomerAdvancePayment convSOToCAP(int salesOrderId, int typeId, Integer capId) {
		SalesOrder salesOrder = salesOrderDao.get(salesOrderId);
		salesOrder.setSoItems(salesOrderService.getSOItems(salesOrderId, false));
		salesOrder.setSoLines(salesOrderService.getSOLines(salesOrderId));

		CustomerAdvancePayment cap = new CustomerAdvancePayment();
		List<CustomerAdvancePaymentLine> capLines = new ArrayList<>();
		CustomerAdvancePaymentLine capLine = null;
		if (salesOrder != null) {
			cap.setCompanyId(salesOrder.getCompanyId());
			cap.setCompany(salesOrder.getCompany());
			cap.setSalesOrderId(salesOrder.getId());
			cap.setReferenceNo(salesOrder.getSequenceNumber().toString());
			cap.setCustomerAdvancePaymentTypeId(typeId);
			cap.setArCustomerId(salesOrder.getArCustomerId());
			cap.setArCustomer(salesOrder.getArCustomer());
			cap.setArCustomerAccountId(salesOrder.getArCustomerAcctId());
			cap.setArCustomerAccount(salesOrder.getArCustomerAccount());
			cap.setDivisionId(salesOrder.getDivisionId());
			cap.setDivision(salesOrder.getDivision());
			cap.setPoNumber(salesOrder.getPoNumber());
			cap.setCurrencyId(salesOrder.getCurrencyId());
			cap.setCurrency(salesOrder.getCurrency());
			cap.setCurrencyRateId(salesOrder.getCurrencyRateId());
			cap.setCurrencyRateValue(salesOrder.getCurrencyRateValue());
			capLine = new CustomerAdvancePaymentLine();
			capLine.setReferenceObjectId(salesOrder.getEbObjectId());
			//Sales Order
			String seqNo = "SO-"+salesOrder.getSequenceNumber();
			String remarks = salesOrder.getRemarks();
			String particular = "";
			if(remarks != null) {
				particular = seqNo + " " + StringFormatUtil.removeExtraWhiteSpaces(remarks);
			}
			particular += " " + DateUtil.formatDate(salesOrder.getDate());
			capLine.setReferenceNo(particular);
			double soCurrencyRate = salesOrder.getCurrencyRateValue() > 0.0 ? salesOrder.getCurrencyRateValue() : 1.0;
			double soAdvPayment = salesOrder.getAdvancePayment() / soCurrencyRate;
			capLine.setAmount(NumberFormatUtil.roundOffNumber(soAdvPayment, 6));
			capLines.add(capLine);
			//CAP
			List<CustomerAdvancePayment> caps = capDao.getSoAdvPayments(capId, salesOrderId);
			for(CustomerAdvancePayment rCap : caps) {
				capLine = new CustomerAdvancePaymentLine();
				capLine.setReferenceObjectId(rCap.getEbObjectId());
				particular = "CAP-" + rCap.getCapNumber() + " " + DateUtil.formatDate(rCap.getReceiptDate());
				capLine.setReferenceNo(particular);
				double advPaymentRate = rCap.getCurrencyRateValue() > 0.0 ? rCap.getCurrencyRateValue() : 1.0;
				double advPaymentAmt = rCap.getAmount() / advPaymentRate;
				capLine.setAmount(-NumberFormatUtil.roundOffNumber(advPaymentAmt, 6));
				capLines.add(capLine);
			}
		}
		cap.setCapLines(capLines);
		return cap;
	}

	/**
	 * Get the total advance payments for the specified sales order.
	 * @param soId The sales order id.
	 * @param The customer advance payment id.
	 * @return The total advance payments.
	 */
	public Double getTotalAdvPyments(Integer soId, Integer capId) {
		return capDao.getTotalAdvPaymentsBySO(soId, capId);
	}

	/**
	 * Get the list of customer advance payments by selected customer transaction
	 * @param arTransactionIds The AR transaction ids
	 * @return The list of customer advance payments
	 */
	public List<ArReceiptAdvancePayment> getAdvancePayments(String arTransactionIds) {
		List<ArReceiptAdvancePayment> arAdvancePayments = new ArrayList<ArReceiptAdvancePayment>();
		List<CustomerAdvancePayment> customerAdvPayments = capDao.getCustomerAdvancePayments(arTransactionIds);
		ArReceiptAdvancePayment arrap = null;
		for (CustomerAdvancePayment cap : customerAdvPayments) {
			arrap = new ArReceiptAdvancePayment();
			arrap.setCustomerAdvancePaymentId(cap.getId());
			arrap.setAmount(cap.getCash());
			arrap.setCapNumber(cap.getCapNumber().toString());
			arAdvancePayments.add(arrap);
			arrap = null;
		}
		return arAdvancePayments;
	}

	/**
	 * Get the list of customer advance payments
	 * @param companyId The company id
	 * @param customerId The customer id 
	 * @param customerAcctId The customer account id
	 * @param capNumber The CAP number
	 * @param isExact True if exact, otherwise false
	 * @return The list of customer advance payments
	 */
	public List<CustomerAdvancePayment> getCustomerAdvancePayments(Integer companyId, Integer customerId,
			Integer customerAcctId, Integer capNumber, boolean isExact, String toBeExcludedCapIds, Integer arReceiptId) {
		return capDao.getCustomerAdvancePayments(companyId, customerId, customerAcctId, capNumber, isExact,
				toBeExcludedCapIds, arReceiptId);
	}

	/**
	 * Get the list of {@link SalesOrder} reference.
	 * @param companyId The company id.
	 * @param arCustomerId The ar customer id.
	 * @param arCustomerAcctId The ar customer account id.
	 * @param soNumber The sales order sequence number.
	 * @param statusId The status id.
	 * @param dateFrom The start date filter.
	 * @param dateTo The end date filter.
	 * @param poNumber The po/pcr number.
	 * @param divisionId The division id.
	 * @param pageNumber The pagenumber.
	 * @return List of {@link SalesOrder} reference.
	 */
	public Page<SalesOrder> getCapSalesOrders(Integer companyId, Integer arCustomerId, Integer arCustomerAcctId,
			Integer soNumber, Integer statusId, Date dateFrom, Date dateTo, String poNumber, Integer divisionId, 
			Integer pageNumber) {
		Page<SalesOrder> salesOrders = salesOrderDao.getCapSalesOrders(companyId, arCustomerId, arCustomerAcctId, soNumber, statusId, dateFrom, dateTo, 
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD), poNumber, divisionId);
		for(SalesOrder so : salesOrders.getData()) {
			if(so.getArCustomerId() != null) {
				so.setArCustomer(arCustomerDao.get(so.getArCustomerId()));
			}
		}
		return salesOrders;
	}

	/**
	 * Get the CAP division id by CAP type id.
	 * @param typeId The DR type id.
	 * @return The division id.
	 */
	public int getDivisionByCapTypeId(int typeId) {
		int divisionId = 0;
		switch (typeId) {
		case CustomerAdvancePaymentType.CAP_CENTRAL:
			divisionId = 1;
			break;
		case CustomerAdvancePaymentType.CAP_NSB3:
			divisionId = 2;
			break;
		case CustomerAdvancePaymentType.CAP_NSB4:
			divisionId = 3;
			break;
		case CustomerAdvancePaymentType.CAP_NSB5:
			divisionId = 4;
			break;
		case CustomerAdvancePaymentType.CAP_NSB8:
			divisionId = 5;
			break;
		case CustomerAdvancePaymentType.CAP_NSB8A:
			divisionId = 6;
			break;
		}
		return divisionId;
	}

	/**
	 * Convert number to words.
	 * If currency id is not PHP, remove pesos.
	 * @param number The number.
	 * @param currencyId The currency id.
	 * @return The string format of the number.
	 */
	public String convertNumberToWords(Double number, Integer currencyId) {
		String amountInWords = NumberFormatUtil.amountToWordsWithDecimals(NumberFormatUtil.roundOffTo2DecPlaces(number));
		if(currencyId != Currency.DEFUALT_PHP_ID) {
			amountInWords = amountInWords.replace("Pesos", "");
		}
		return amountInWords;
	}

	/**
	 * Get the {@link SalesOrder} reference remaining advance payment balance.
	 * @param capLines The {@link CustomerAdvancePaymentLine}.
	 * @param capAmount The {@link CustomerAdvancePayment} amount.
	 * @return The computed remaining balance.
	 */
	public double getSoRemainingBalance(List<CustomerAdvancePaymentLine> capLines, double capAmount) {
		double totalCapLinesAmt = 0;
		if(capLines != null) {
			for(CustomerAdvancePaymentLine capLine : capLines) {
				totalCapLinesAmt += capLine.getAmount();
			}
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalCapLinesAmt) - capAmount;
	}
}
