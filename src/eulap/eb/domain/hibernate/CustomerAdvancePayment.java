package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.inventory.InventoryTransaction;
import eulap.eb.service.oo.OOChild;


/**
 * Domain class for CUSTOMER_ADVANCE_PAYMENT

 *
 */
@Entity
@Table(name="CUSTOMER_ADVANCE_PAYMENT")
public class CustomerAdvancePayment extends BaseFormWorkflow implements InventoryTransaction{
	private Integer customerAdvancePaymentTypeId;
	private Integer capNumber;
	private Integer companyId;
	private Integer arReceiptTypeId;
	private String refNumber;
	private Integer arCustomerId;
	private Integer arCustomerAccountId;
	private String salesInvoiceNo;
	private Date receiptDate;
	private Date maturityDate;
	private Double cash;
	private Company company;
	private ArReceiptType arReceiptType;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private List<CustomerAdvancePaymentItem> capItems;
	private List<CapArLine> capArLines;
	private Double amount;
	private String errorCAPItems;
	private String capItemsJson;
	private String capArLinesJson;
	private String capArLineMesage;
	private Integer wtAcctSettingId;
	private Double wtAmount;
	private WithholdingTaxAcctSetting wtAcctSetting;
	private Integer receiptMethodId;
	private Integer salesOrderId;
	private String referenceNo;
	private SalesOrder salesOrder;
	private ReceiptMethod receiptMethod;
	private Double soAdvPaymentBalance;
	private Integer divisionId;
	private Division division;
	private Integer currencyId;
	private Currency currency;
	private Integer currencyRateId;
	private double currencyRateValue;
	private String poNumber;
	private String referenceDocumentJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocsMessage;
	private List<CustomerAdvancePaymentLine> capLines;
	private String capLinesJson;
	private Double totalAmount;
	private Integer soNumber;
	private String cancellationRemarks;

	public static final int MAX_REF_NUMBER = 20;
	public static final int MAX_SALE_INVOICE_NO = 100;
	public static final int MAX_RECEIPT_NUMBER = 100;
	public static final int MAX_CHECK_NUMBER = 20;

	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;

	/**
	 * Object type id for {@link CustomerAdvancePayment} Retail = 32
	 */
	public static final int CAP_RETAIL_OBJECT_TYPE_ID = 32;

	/**
	 * Object type id for {@link CustomerAdvancePayment} IS = 146
	 */
	public static final int CAP_IS_OBJECT_TYPE_ID = 146;

	/**
	 * Object type id for {@link CustomerAdvancePayment} WIP - Special Order = 147
	 */
	public static final int CAP_WIP_SO_OBJECT_TYPE_ID = 147;

	public enum FIELD {
		id, formWorkflowId, capNumber,  companyId, arReceiptTypeId, refNumber, arCustomerId, arCustomerAccountId, 
		salesInvoiceNo,  receiptDate, maturityDate, createdDate, updatedDate, customerAdvancePaymentTypeId, ebObjectId,
		cash, salesOrderId, divisionId, poNumber
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "CUSTOMER_ADVANCE_PAYMENT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "CAP_NUMBER")
	public Integer getCapNumber() {
		return capNumber;
	}

	public void setCapNumber(Integer capNumber) {
		this.capNumber = capNumber;
	}

	/**
	 * Get the customer advance payment type id.
	 * @return The customer advance payment type id.
	 */
	@Column(name = "CUSTOMER_ADVANCE_PAYMENT_TYPE_ID", columnDefinition="INT(10")
	public Integer getCustomerAdvancePaymentTypeId() {
		return customerAdvancePaymentTypeId;
	}

	public void setCustomerAdvancePaymentTypeId(Integer customerAdvancePaymentTypeId) {
		this.customerAdvancePaymentTypeId = customerAdvancePaymentTypeId;
	}

	/**
	 * Get the company id.
	 * @return The company id.
	 */
	@Column(name = "COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get the unique id of the ar receipt type.
	 * @return ar receipt type id.
	 */
	@Column(name = "AR_RECEIPT_TYPE_ID")
	public Integer getArReceiptTypeId() {
		return arReceiptTypeId;
	}

	public void setArReceiptTypeId(Integer arReceiptTypeId) {
		this.arReceiptTypeId = arReceiptTypeId;
	}

	/**
	 * Get the reference number.
	 * @return reference number.
	 */
	@Column(name = "REF_NUMBER")
	public String getRefNumber() {
		return refNumber;
	}

	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	/**
	 * Get the Id of the Ar customer id.
	 * @return The Id of Ar customer id.
	 */
	@Column(name = "AR_CUSTOMER_ID")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	/**
	 * Get the Id of the Ar customer account id.
	 * @return The Id of Ar customer account.
	 */
	@Column(name = "AR_CUSTOMER_ACCOUNT_ID")
	public Integer getArCustomerAccountId() {
		return arCustomerAccountId;
	}

	public void setArCustomerAccountId(Integer arCustomerAccountId) {
		this.arCustomerAccountId = arCustomerAccountId;
	}

	/**
	 * Get the sales invoice number.
	 * @return The sales invoice number.
	 */
	@Column(name = "SALE_INVOICE_NO")
	public String getSalesInvoiceNo() {
		return salesInvoiceNo;
	}

	public void setSalesInvoiceNo(String salesInvoiceNo) {
		this.salesInvoiceNo = salesInvoiceNo;
	}

	/**
	 * Get the receipt date.
	 * @return The receipt date.
	 */
	@Column(name = "RECEIPT_DATE")
	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	/**
	 * Get the maturity date.
	 * @return The maturity date.
	 */
	@Column(name = "MATURITY_DATE")
	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	@Column(name = "CASH")
	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = cash;
	}

	@ManyToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne
	@JoinColumn (name = "AR_RECEIPT_TYPE_ID", insertable=false, updatable=false)
	public ArReceiptType getArReceiptType() {
		return arReceiptType;
	}

	public void setArReceiptType(ArReceiptType arReceiptType) {
		this.arReceiptType = arReceiptType;
	}

	@ManyToOne
	@JoinColumn (name = "AR_CUSTOMER_ID", insertable=false, updatable=false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	@ManyToOne
	@JoinColumn (name = "AR_CUSTOMER_ACCOUNT_ID", insertable=false, updatable=false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@Transient
	public List<CustomerAdvancePaymentItem> getCapItems() {
		return capItems;
	}

	public void setCapItems(List<CustomerAdvancePaymentItem> capItems) {
		this.capItems = capItems;
	}

	@Transient
	public List<CapArLine> getCapArLines() {
		return capArLines;
	}

	public void setCapArLines(List<CapArLine> capArLines) {
		this.capArLines = capArLines;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Transient
	public String getErrorCAPItems() {
		return errorCAPItems;
	}

	public void setErrorCAPItems(String errorCAPItems) {
		this.errorCAPItems = errorCAPItems;
	}

	@Transient
	public String getCapItemsJson() {
		return capItemsJson;
	}

	public void setCapItemsJson(String capItemsJson) {
		this.capItemsJson = capItemsJson;
	}

	@Transient
	public void serializeItems (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		capItemsJson = gson.toJson(capItems);
	}

	@Transient
	public void deserializeItems () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<CustomerAdvancePaymentItem>>(){}.getType();
		capItems = gson.fromJson(capItemsJson, type);
	}

	/**
	 * Formats the CAP  Number to: C 1
	 * @return The formatted RR Number.
	 */
	@Transient
	public String getFormattedCSNumber() {
		if (company != null) {
			if (company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + " " + capNumber;
			}else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + capNumber;
			}
		}
		return null;
	}

	@Transient
	public String getCapArLinesJson() {
		return capArLinesJson;
	}

	public void setCapArLinesJson(String capArLinesJson) {
		this.capArLinesJson = capArLinesJson;
	}

	@Transient
	public String getCapArLineMesage() {
		return capArLineMesage;
	}

	public void setCapArLineMesage(String capArLineMesage) {
		this.capArLineMesage = capArLineMesage;
	}

	@Override
	@Transient
	public List<? extends BaseItem> getInventoryItems() {
		return capItems;
	}

	@Transient
	public void serializeCapArLines(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		capArLinesJson = gson.toJson(capArLines);
	}

	@Transient
	public void deserializeCapArLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<CapArLine>>(){}.getType();
		capArLines = gson.fromJson(capArLinesJson, type);
	}

	@Column(name = "WT_ACCOUNT_SETTING_ID")
	public Integer getWtAcctSettingId() {
		return wtAcctSettingId;
	}

	public void setWtAcctSettingId(Integer wtAcctSettingId) {
		this.wtAcctSettingId = wtAcctSettingId;
	}

	@Column(name = "WT_AMOUNT")
	public Double getWtAmount() {
		return wtAmount;
	}

	public void setWtAmount(Double wtAmount) {
		this.wtAmount = wtAmount;
	}

	@OneToOne
	@JoinColumn(name = "WT_ACCOUNT_SETTING_ID", insertable=false, updatable=false)
	public WithholdingTaxAcctSetting getWtAcctSetting() {
		return wtAcctSetting;
	}

	public void setWtAcctSetting(WithholdingTaxAcctSetting wtAcctSetting) {
		this.wtAcctSetting = wtAcctSetting;
	}

	@Column(name = "RECEIPT_METHOD_ID")
	public Integer getReceiptMethodId() {
		return receiptMethodId;
	}

	public void setReceiptMethodId(Integer receiptMethodId) {
		this.receiptMethodId = receiptMethodId;
	}

	@Column(name = "SALES_ORDER_ID")
	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	@Column(name = "REFERENCE_NO")
	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	@OneToOne
	@JoinColumn(name = "RECEIPT_METHOD_ID", insertable=false, updatable=false)
	public ReceiptMethod getReceiptMethod() {
		return receiptMethod;
	}

	public void setReceiptMethod(ReceiptMethod receiptMethod) {
		this.receiptMethod = receiptMethod;
	}

	@OneToOne
	@JoinColumn(name = "SALES_ORDER_ID", insertable=false, updatable=false)
	public SalesOrder getSalesOrder() {
		return salesOrder;
	}

	public void setSalesOrder(SalesOrder salesOrder) {
		this.salesOrder = salesOrder;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		switch (customerAdvancePaymentTypeId) {
		case CustomerAdvancePaymentType.RETAIL:
			return CAP_RETAIL_OBJECT_TYPE_ID;
		case CustomerAdvancePaymentType.INDIV_SELECTION:
			return CAP_IS_OBJECT_TYPE_ID;
		case CustomerAdvancePaymentType.WIP_SPECIAL_ORDER:
			return CAP_WIP_SO_OBJECT_TYPE_ID;
		default:
			return CAP_RETAIL_OBJECT_TYPE_ID;
		}
	}

	@Override
	@Transient
	public String getShortDescription() {
		return "CAP " + capNumber;
	}

	@Override 
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + customerAdvancePaymentTypeId;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if(capItems != null) {
			children.addAll(capItems);
		}
		if(capArLines != null) {
			children.addAll(capArLines);
		}
		if(capLines != null) {
			children.addAll(capLines);
		}
		if(referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return receiptDate;
	}

	@Transient
	public Double getSoAdvPaymentBalance() {
		return soAdvPaymentBalance;
	}

	@Column(name="DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@OneToOne
	@JoinColumn(name="DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@Column (name = "CURRENCY_ID")
	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	@Column (name = "CURRENCY_RATE_ID")
	public Integer getCurrencyRateId() {
		return currencyRateId;
	}

	public void setCurrencyRateId(Integer currencyRateId) {
		this.currencyRateId = currencyRateId;
	}

	@Column (name = "CURRENCY_RATE_VALUE")
	public double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Column(name = "PO_NUMBER")
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@OneToOne
	@JoinColumn(name = "CURRENCY_ID", insertable=false, updatable=false)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Transient
	public String getReferenceDocumentJson() {
		return referenceDocumentJson;
	}

	public void setReferenceDocumentJson(String referenceDocumentJson) {
		this.referenceDocumentJson = referenceDocumentJson;
	}

	@Transient
	public void serializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentJson, type);
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@Transient
	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@Transient
	public List<CustomerAdvancePaymentLine> getCapLines() {
		return capLines;
	}

	public void setCapLines(List<CustomerAdvancePaymentLine> capLines) {
		this.capLines = capLines;
	}

	@Transient
	public String getCapLinesJson() {
		return capLinesJson;
	}

	public void setCapLinesJson(String capLinesJson) {
		this.capLinesJson = capLinesJson;
	}

	@Transient
	public void serializeCapLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		capLinesJson = gson.toJson(capLines);
	}

	public void deserializeCapLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<CustomerAdvancePaymentLine>>(){}.getType();
		capLines = gson.fromJson(capLinesJson, type);
	}

	@Transient
	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Transient
	public Integer getSoNumber() {
		return soNumber;
	}

	public void setSoNumber(Integer soNumber) {
		this.soNumber = soNumber;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CustomerAdvancePayment [customerAdvancePaymentTypeId=").append(customerAdvancePaymentTypeId)
				.append(", capNumber=").append(capNumber).append(", companyId=").append(companyId)
				.append(", arReceiptTypeId=").append(arReceiptTypeId).append(", refNumber=").append(refNumber)
				.append(", arCustomerId=").append(arCustomerId).append(", arCustomerAccountId=")
				.append(arCustomerAccountId).append(", salesInvoiceNo=").append(salesInvoiceNo).append(", receiptDate=")
				.append(receiptDate).append(", maturityDate=").append(maturityDate).append(", cash=").append(cash)
				.append(", arReceiptType=").append(arReceiptType).append(", amount=").append(amount)
				.append(", errorCAPItems=").append(errorCAPItems).append(", capItemsJson=").append(capItemsJson)
				.append(", capArLinesJson=").append(capArLinesJson).append(", capArLineMesage=").append(capArLineMesage)
				.append(", wtAcctSettingId=").append(wtAcctSettingId).append(", wtAmount=").append(wtAmount)
				.append(", wtAcctSetting=").append(wtAcctSetting).append(", receiptMethodId=").append(receiptMethodId)
				.append(", salesOrderId=").append(salesOrderId).append(", referenceNo=").append(referenceNo)
				.append(", divisionId=").append(divisionId).append("]").append(", cancellationRemarks=").append(cancellationRemarks).append("]"); //k
		return builder.toString();
	}
}