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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;

/**
 * Object representation for SALES_ORDER table

 *
 */
@Entity
@Table(name="SALES_ORDER")
public class SalesOrder extends BaseFormWorkflow {
	private Integer companyId;
	private Integer divisionId;
	private Integer salesQuotationId;
	private Date date;
	private Date deliveryDate;
	private Integer soTypeId;
	private String poNumber;
	private Integer arCustomerId;
	private Integer arCustomerAcctId;
	private Integer termId;
	private String shipTo;
	private String remarks;
	private Integer customerTypeId;
	private Integer currencyId;
	private Integer currencyRateId;
	private Double currencyRateValue;
	private Double amount;
	private Integer wtAcctSettingId;
	private Double wtAmount;
	private List<SalesOrderItem> soItems;
	private String soItemJson;
	private List<SalesOrderLine> soLines;
	private String soLineJson;
	private List<SalesOrderTruckingLine> sotLines;
	private String sotLineJson;
	private List<SalesOrderEquipmentLine> soeLines;
	private String soeLineJson;
	private Integer sequenceNumber;
	private Company company;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private CustomerType customerType;
	private SalesQuotation salesQuotation;
	private String refSQNumber;
	private WithholdingTaxAcctSetting wtAcctSetting;
	private boolean deposit;
	private Double advancePayment;
	private Double advancePaymentBalance;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;
	private Division division;
	private Term term;
	private SOType soType;
	private Currency currency;
	private CurrencyRate currencyRate;
	private Double wtVatAmount;
	private String customerName;

	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;

	/**
	 * Sales Order Item object type id: 12004
	 */
	public static final int OBJECT_TYPE = 12003;

	public enum FIELD {
		id, companyId, date, arCustomerId, arCustomerAcctId, formWorkflowId, ebObjectId, customerTypeId, sequenceNumber,
		salesQuotationId, divisionId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SALES_ORDER_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column(name = "SALES_QUOTATION_ID")
	public Integer getSalesQuotationId() {
		return salesQuotationId;
	}

	public void setSalesQuotation(SalesQuotation salesQuotation) {
		this.salesQuotation = salesQuotation;
	}

	@Column(name = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "AR_CUSTOMER_ID")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Column(name = "AR_CUSTOMER_ACCOUNT_ID")
	public Integer getArCustomerAcctId() {
		return arCustomerAcctId;
	}

	public void setArCustomerAcctId(Integer arCustomerAcctId) {
		this.arCustomerAcctId = arCustomerAcctId;
	}

	@Column(name = "SHIP_TO")
	public String getShipTo() {
		return shipTo;
	}

	public void setShipTo(String shipTo) {
		this.shipTo = shipTo;
	}

	@Column(name = "REMARKS")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "CURRENCY_ID")
	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	@Column(name = "CURRENCY_RATE_ID")
	public Integer getCurrencyRateId() {
		return currencyRateId;
	}

	public void setCurrencyRateId(Integer currencyRateId) {
		this.currencyRateId = currencyRateId;
	}

	@Column(name = "CURRENCY_RATE_VALUE")
	public Double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(Double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Column(name = "TERM_ID")
	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	@Column(name = "PO_NUMBER")
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@Column(name = "DELIVERY_DATE")
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	@Column(name = "SO_TYPE_ID")
	public Integer getSoTypeId() {
		return soTypeId;
	}

	public void setSoTypeId(Integer soTypeId) {
		this.soTypeId = soTypeId;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (soItems != null) {
			children.addAll(soItems);
		}
		if (soLines != null) {
			children.addAll(soLines);
		}
		if(sotLines != null) {
			children.addAll(sotLines);
		}
		if(soeLines != null) {
			children.addAll(soeLines);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE;
	}

	@Column(name = "CUSTOMER_TYPE_ID")
	public Integer getCustomerTypeId() {
		return customerTypeId;
	}

	public void setCustomerTypeId(Integer customerTypeId) {
		this.customerTypeId = customerTypeId;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
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

	@OneToMany
	@JoinColumn(name = "SALES_ORDER_ID", insertable=false, updatable=false)
	public List<SalesOrderItem> getSoItems() {
		return soItems;
	}

	public void setSoItems(List<SalesOrderItem> soItems) {
		this.soItems = soItems;
	}

	@Transient
	public String getSoItemJson() {
		return soItemJson;
	}

	public void setSoItemJson(String soItemJson) {
		this.soItemJson = soItemJson;
	}

	@Transient
	public void serializeSOItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		soItemJson = gson.toJson(soItems);
	}

	@Transient
	public void deserializeSOItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SalesOrderItem>>(){}.getType();
		soItems = gson.fromJson(soItemJson, type);
	}

	@OneToMany
	@JoinColumn(name = "SALES_ORDER_ID", insertable=false, updatable=false)
	public List<SalesOrderLine> getSoLines() {
		return soLines;
	}

	public void setSoLines(List<SalesOrderLine> soLines) {
		this.soLines = soLines;
	}

	@Transient
	public String getSoLineJson() {
		return soLineJson;
	}

	public void setSoLineJson(String soLineJson) {
		this.soLineJson = soLineJson;
	}

	@Transient
	public void serializeSOLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		soLineJson = gson.toJson(soLines);
	}

	@Transient
	public void deserializeSOLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SalesOrderLine>>(){}.getType();
		soLines = gson.fromJson(soLineJson, type);
	}

	@Column(name = "SEQUENCE_NO")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn(name = "DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@OneToOne
	@JoinColumn(name = "TERM_ID", insertable=false, updatable=false)
	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	@OneToOne
	@JoinColumn(name = "SO_TYPE_ID", insertable=false, updatable=false)
	public SOType getSoType() {
		return soType;
	}

	public void setSoType(SOType soType) {
		this.soType = soType;
	}

	@OneToOne
	@JoinColumn(name = "CURRENCY_ID", insertable=false, updatable=false)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@OneToOne
	@JoinColumn(name = "CURRENCY_RATE_ID", insertable=false, updatable=false)
	public CurrencyRate getCurrencyRate() {
		return currencyRate;
	}

	public void setCurrencyRate(CurrencyRate currencyRate) {
		this.currencyRate = currencyRate;
	}

	@OneToOne
	@JoinColumn(name = "AR_CUSTOMER_ID", insertable=false, updatable=false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	@Transient
	public SalesQuotation getSalesQuotation() {
		return salesQuotation;
	}

	public void setSalesQuotationId(Integer salesQuotationId) {
		this.salesQuotationId = salesQuotationId;
	}

	@OneToOne
	@JoinColumn(name = "AR_CUSTOMER_ACCOUNT_ID", insertable=false, updatable=false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@OneToOne
	@JoinColumn(name = "CUSTOMER_TYPE_ID", insertable=false, updatable=false)
	public CustomerType getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

	@OneToOne
	@JoinColumn(name = "WT_ACCOUNT_SETTING_ID", insertable=false, updatable=false)
	public WithholdingTaxAcctSetting getWtAcctSetting() {
		return wtAcctSetting;
	}

	public void setWtAcctSetting(WithholdingTaxAcctSetting wtAcctSetting) {
		this.wtAcctSetting = wtAcctSetting;
	}

	@Column(name = "DEPOSIT")
	public boolean isDeposit() {
		return deposit;
	}

	public void setDeposit(boolean deposit) {
		this.deposit = deposit;
	}

	@Column(name = "ADVANCE_PAYMENT")
	public Double getAdvancePayment() {
		return advancePayment;
	}

	public void setAdvancePayment(Double advancePayment) {
		this.advancePayment = advancePayment;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return date;
	}

	@Transient
	public String getRefSQNumber() {
		return refSQNumber;
	}

	public void setRefSQNumber(String refSQNumber) {
		this.refSQNumber = refSQNumber;
	}

	@Transient
	public Double getAdvancePaymentBalance() {
		return advancePaymentBalance;
	}

	public void setAdvancePaymentBalance(Double advancePaymentBalance) {
		this.advancePaymentBalance = advancePaymentBalance;
	}

	@Transient
	public List<SalesOrderTruckingLine> getSotLines() {
		return sotLines;
	}

	public void setSotLines(List<SalesOrderTruckingLine> sotLines) {
		this.sotLines = sotLines;
	}

	@Transient
	public String getSotLineJson() {
		return sotLineJson;
	}

	public void setSotLineJson(String sotLineJson) {
		this.sotLineJson = sotLineJson;
	}

	@Transient
	public void serializeSOTLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		sotLineJson = gson.toJson(sotLines);
	}

	@Transient
	public void deserializeSOTLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SalesOrderTruckingLine>>(){}.getType();
		sotLines = gson.fromJson(sotLineJson, type);
	}

	@Transient
	public List<SalesOrderEquipmentLine> getSoeLines() {
		return soeLines;
	}

	public void setSoeLines(List<SalesOrderEquipmentLine> soeLines) {
		this.soeLines = soeLines;
	}

	@Transient
	public String getSoeLineJson() {
		return soeLineJson;
	}

	public void setSoeLineJson(String soeLineJson) {
		this.soeLineJson = soeLineJson;
	}

	@Transient
	public void serializeSOELines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		soeLineJson = gson.toJson(soeLines);
	}

	@Transient
	public void deserializeSOELines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SalesOrderEquipmentLine>>(){}.getType();
		soeLines = gson.fromJson(soeLineJson, type);
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@Transient
	public String getReferenceDocumentsJson() {
		return referenceDocumentsJson;
	}

	public void setReferenceDocumentsJson(String referenceDocumentsJson) {
		this.referenceDocumentsJson = referenceDocumentsJson;
	}

	@Transient
	public void serializeReferenceDocuments (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentsJson, type);
	}

	@Transient
	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		String workflowName = super.getWorkflowName();
		if (divisionId != null) {
			return workflowName + divisionId;
		}
		return workflowName;
	}

	@Column(name = "WT_VAT_AMOUNT")
	public Double getWtVatAmount() {
		return wtVatAmount;
	}

	public void setWtVatAmount(Double wtVatAmount) {
		this.wtVatAmount = wtVatAmount;
	}

	@Transient
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SalesOrder [companyId=").append(companyId).append(", divisionId=").append(divisionId)
				.append(", salesQuotationId=").append(salesQuotationId).append(", date=").append(date)
				.append(", deliveryDate=").append(deliveryDate).append(", soTypeId=").append(soTypeId)
				.append(", poNumber=").append(poNumber).append(", arCustomerId=").append(arCustomerId)
				.append(", arCustomerAcctId=").append(arCustomerAcctId).append(", termId=").append(termId)
				.append(", shipTo=").append(shipTo).append(", remarks=").append(remarks).append(", customerTypeId=")
				.append(customerTypeId).append(", currencyId=").append(currencyId).append(", currencyRateId=")
				.append(currencyRateId).append(", currencyRateValue=").append(currencyRateValue).append(", amount=")
				.append(amount).append(", wtAcctSettingId=").append(wtAcctSettingId).append(", wtAmount=")
				.append(wtAmount).append(", sequenceNumber=").append(sequenceNumber).append(", refSQNumber=")
				.append(refSQNumber).append(", deposit=").append(deposit).append(", advancePayment=")
				.append(advancePayment).append(", advancePaymentBalance=").append(advancePaymentBalance)
				.append(", wtVatAmount=").append(wtVatAmount).append("]");
		return builder.toString();
	}
}
