package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;

/**
 * A class that represents the AR_MISCELLANEOUS table in the CBS database.

 *
 */
@Entity
@Table(name="AR_MISCELLANEOUS")
public class ArMiscellaneous extends BaseFormWorkflow{
	private Integer arMiscellaneousTypeId;
	private Integer companyId;
	private Integer divisionId;
	private String refNumber;
	private Integer serviceLeaseKeyId;
	private Integer sequenceNo;
	private Integer receiptMethodId;
	private Integer arCustomerId;
	private Integer arCustomerAccountId;
	private String receiptNumber;
	private Date receiptDate;
	private Date maturityDate;
	private String description;
	private Double amount;
	private Integer currencyId;
	private Integer currencyRateId;
	private Double currencyRateValue;
	private ArMiscellaneousType arMiscellaneousType;
	private ReceiptMethod receiptMethod;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private List<ArMiscellaneousLine> arMiscellaneousLines;
	private String arMiscellaneousLinesJson;
	private String arMiscLineMessage;
	private Integer wtAcctSettingId;
	private Double wtAmount;
	private WithholdingTaxAcctSetting wtAcctSetting;
	private Company company;
	private Division division;
	private Currency currency;
	private CurrencyRate currencyRate;
	private Double wtVatAmount;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;
	private Double totalAmount;
	private String customerName;

	public enum FIELD {
		id, formWorkflowId, serviceLeaseKeyId, sequenceNo, receiptMethodId, arCustomerId, arCustomerAccountId, receiptNumber, 
			receiptDate, maturityDate, description, amount, arMiscellaneousTypeId, ebObjectId, companyId, divisionId, currencyId, currencyRateId, currencyRateValue
	}

	public static final int MAX_RECEIPT_NUMBER = 100;
	public static final int MAX_CHECK_NUMBER = 20;

	/**
	 * Object type Id for Ar Miscellaneous = 131.
	 */
	public static final int AR_MISC_OBJECT_TYPE_ID = 131;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "AR_MISCELLANEOUS_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return AR_MISC_OBJECT_TYPE_ID;
	}

	/**
	 * Get the unique id of the ar miscellaneous type.
	 * @return ar miscellaneous type id.
	 */
	@Column(name = "AR_MISCELLANEOUS_TYPE_ID")
	public Integer getArMiscellaneousTypeId() {
		return arMiscellaneousTypeId;
	}

	public void setArMiscellaneousTypeId(Integer arMiscellaneousTypeId) {
		this.arMiscellaneousTypeId = arMiscellaneousTypeId;
	}

	@Column(name = "COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@OneToOne
	@JoinColumn(name="COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "DIVISION_ID")
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
	 * Get the Id of the service lease key of the AR receipt.
	 * @return The Id of the service lease key.
	 */
	@Column(name = "EB_SL_KEY_ID")
	public Integer getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(Integer serviceLeaseKeyId) {
		this.serviceLeaseKeyId = serviceLeaseKeyId;
	}

	/**
	 * Get the sequence number of Ar receipt.
	 * @return The sequence number.
	 */
	@Column(name = "SEQUENCE_NO")
	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	/**
	 * Get the Id of the receipt method.
	 * @return The Id of receipt method.
	 */
	@Column(name = "RECEIPT_METHOD_ID")
	public Integer getReceiptMethodId() {
		return receiptMethodId;
	}

	public void setReceiptMethodId(Integer receiptMethodId) {
		this.receiptMethodId = receiptMethodId;
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
	 * Get the receipt number.
	 * @return The receipt number.
	 */
	@Column(name = "RECEIPT_NUMBER")
	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
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

	/**
	 * Get the description.
	 * @return The description.
	 */
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the amount.
	 * @return The amount.
	 */
	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "CURRENCY_ID")
	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	@OneToOne
	@JoinColumn(name="CURRENCY_ID", insertable=false, updatable=false)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Column(name = "CURRENCY_RATE_ID")
	public Integer getCurrencyRateId() {
		return currencyRateId;
	}

	public void setCurrencyRateId(Integer currencyRateId) {
		this.currencyRateId = currencyRateId;
	}

	@OneToOne
	@JoinColumn(name="CURRENCY_RATE_ID", insertable=false, updatable=false)
	public CurrencyRate getCurrencyRate() {
		return currencyRate;
	}

	public void setCurrencyRate(CurrencyRate currencyRate) {
		this.currencyRate = currencyRate;
	}

	@Column(name = "CURRENCY_RATE_VALUE")
	public Double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(Double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Column(name = "WT_VAT_AMOUNT")
	public Double getWtVatAmount() {
		return wtVatAmount;
	}

	public void setWtVatAmount(Double wtVatAmount) {
		this.wtVatAmount = wtVatAmount;
	}

	/**
	 * Get the associated miscellaneous type.
	 * @return The associated miscellaneous type.
	 */
	@ManyToOne
	@JoinColumn (name = "AR_MISCELLANEOUS_TYPE_ID", insertable=false, updatable=false)
	public ArMiscellaneousType getArMiscellaneousType() {
		return arMiscellaneousType;
	}

	public void setArMiscellaneousType(ArMiscellaneousType arMiscellaneousType) {
		this.arMiscellaneousType = arMiscellaneousType;
	}

	/**
	 * Get the associated receipt method.
	 * @return The associated receipt method.
	 */
	@ManyToOne
	@JoinColumn (name = "RECEIPT_METHOD_ID", insertable=false, updatable=false)
	public ReceiptMethod getReceiptMethod() {
		return receiptMethod;
	}

	public void setReceiptMethod(ReceiptMethod receiptMethod) {
		this.receiptMethod = receiptMethod;
	}

	/**
	 * Get the associated AR customer.
	 * @return The associated AR customer.
	 */
	@ManyToOne
	@JoinColumn (name = "AR_CUSTOMER_ID", insertable=false, updatable=false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	/**
	 * Get the associated AR customer account.
	 * @return The associated AR customer account.
	 */
	@ManyToOne
	@JoinColumn (name = "AR_CUSTOMER_ACCOUNT_ID", insertable=false, updatable=false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn (name = "AR_MISCELLANEOUS_ID", insertable=false, updatable=false)
	public List<ArMiscellaneousLine> getArMiscellaneousLines() {
		return arMiscellaneousLines;
	}

	public void setArMiscellaneousLines(
			List<ArMiscellaneousLine> arMiscellaneousLines) {
		this.arMiscellaneousLines = arMiscellaneousLines;
	}

	@Transient
	public String getArMiscLineMessage() {
		return arMiscLineMessage;
	}

	public void setArMiscLineMessage(String arMiscLineMessage) {
		this.arMiscLineMessage = arMiscLineMessage;
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

	@Transient
	public String getArMiscellaneousLinesJson() {
		return arMiscellaneousLinesJson;
	}

	public void setArMiscellaneousLinesJson(String arMiscellaneousLinesJson) {
		this.arMiscellaneousLinesJson = arMiscellaneousLinesJson;
	}

	@Transient
	public void serializeArMiscellaneousLines(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		arMiscellaneousLinesJson = gson.toJson(arMiscellaneousLines);
	}

	@Transient
	public void deserializeArMiscellaneousLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ArMiscellaneousLine>>(){}.getType();
		arMiscellaneousLines = gson.fromJson(arMiscellaneousLinesJson, type);
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
		return "ArMiscellaneous [arMiscellaneousTypeId="
				+ arMiscellaneousTypeId + ", refNumber=" + refNumber
				+ ", serviceLeaseKeyId=" + serviceLeaseKeyId + ", sequenceNo="
				+ sequenceNo + ", receiptId=" + receiptMethodId + ", arCustomerId="
				+ arCustomerId + ", arCustomerAccountId=" + arCustomerAccountId
				+ ", receiptNumber=" + receiptNumber + ", receiptDate="
				+ receiptDate + ", maturityDate=" + maturityDate
				+ ", description=" + description + ", amount=" + amount
				+ ", companyId=" + companyId
				+ ", divisionId=" + divisionId
				+ ", currencyId=" + currencyId
				+ ", currencyRateId=" + currencyRateId
				+ ", currencyRateValue=" + currencyRateValue
				+ ", arMiscLineMessage="
				+ arMiscLineMessage + ", getId()=" + getId()
				+ ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()="
				+ getCreatedDate() + ", getUpdatedBy()=" + getUpdatedBy()
				+ ", getUpdatedDate()=" + getUpdatedDate() + "]";
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if(arMiscellaneousLines != null && !arMiscellaneousLines.isEmpty()) {
			children.addAll(arMiscellaneousLines);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
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

	@Transient
	public Double getTotalAmount() {
		return totalAmount;
	}
	
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
}
