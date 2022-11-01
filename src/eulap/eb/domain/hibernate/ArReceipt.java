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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.common.util.StringFormatUtil;
import eulap.eb.service.oo.OOChild;

/**
 * A class that represents the AR_RECEIPT table in the CBS database.

 *
 */
@Entity
@Table(name="AR_RECEIPT")
public class ArReceipt extends BaseFormWorkflow{
	private Integer arReceiptTypeId;
	private Integer companyId;
	private String refNumber;
	private Integer serviceLeaseKeyId;
	private Integer sequenceNo;
	private Integer receiptMethodId;
	private Integer arCustomerId;
	private Integer arCustomerAccountId;
	private String receiptNumber;
	private Date receiptDate;
	private Date maturityDate;
	private Double amount;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private List<ArReceiptTransaction> arRTransactions;
	private String arRTransactionMessage;
	private Integer appliedStatusId;
	private ReceiptMethod receiptMethod;
	private ArReceiptType receiptType;
	private Company company;
	private Double totalTAmount;
	private List<AcArLine> acArLines;
	private String acArLinesJson;
	private String acArLineMesage;
	private String arRTransactionsJson;
	private String arTransactionMessage;
	private List<ArReceiptAdvancePayment> arAdvPayments;
	private String arAdvPaymentJson;
	private Double remainingBalance;
	private Integer divisionId;
	private Division division;
	private Integer currencyId;
	private Currency currency;
	private Integer currencyRateId;
	private double currencyRateValue;
	private String referenceDocumentJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocsMessage;
	private List<ArReceiptLine> arReceiptLines;
	private String arReceiptLineJson;
	private Double recoupment;
	private Double retention;

	public static final int OBJECT_TYPE_ID = 119;
	public static final int CHILD_TO_CHILD_OR_TYPE_ID = 24006;

	public enum FIELD {
		id, formWorkflowId, serviceLeaseKeyId, sequenceNo, receiptMethodId, arCustomerId, arCustomerAccountId, receiptNumber,
			receiptDate, maturityDate, description, amount, arReceiptTypeId, companyId, ebObjectId, divisionId, refNumber
	}

	public static final int MAX_RECEIPT_NUMBER = 100;
	public static final int MAX_CHECK_NUMBER = 20;
	public static final int MAX_BANK_NAME = 50;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "AR_RECEIPT_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
		return OBJECT_TYPE_ID;
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
	 * Get the unique id of the company.
	 * @return ar receipt type id.
	 */
	@Column(name = "COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
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
	 * Get the Id of the receipt.
	 * @return The Id of receipt.
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

	@Transient
	public List<ArReceiptTransaction> getArRTransactions() {
		return arRTransactions;
	}

	public void setArRTransactions(List<ArReceiptTransaction> arRTransactions) {
		this.arRTransactions = arRTransactions;
	}

	@Transient
	public String getArRTransactionMessage() {
		return arRTransactionMessage;
	}

	public void setArRTransactionMessage(String arRTransactionMessage) {
		this.arRTransactionMessage = arRTransactionMessage;
	}

	@Transient
	public Integer getAppliedStatusId() {
		return appliedStatusId;
	}

	public void setAppliedStatusId(Integer appliedStatusId) {
		this.appliedStatusId = appliedStatusId;
	}

	@ManyToOne
	@JoinColumn (name = "RECEIPT_METHOD_ID", insertable=false, updatable=false)
	public ReceiptMethod getReceiptMethod() {
		return receiptMethod;
	}

	public void setReceiptMethod(ReceiptMethod receiptMethod) {
		this.receiptMethod = receiptMethod;
	}

	@ManyToOne
	@JoinColumn (name = "AR_RECEIPT_TYPE_ID", insertable=false, updatable=false)
	public ArReceiptType getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(ArReceiptType receiptType) {
		this.receiptType = receiptType;
	}

	@ManyToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	@Transient
	public Double getTotalTAmount() {
		return totalTAmount;
	}

	public void setTotalTAmount(Double totalTAmount) {
		this.totalTAmount = totalTAmount;
	}

	@Transient
	public String getFormattedSeqNo() {
		if(company != null) {
			if(company.getCompanyCode() != null) {
				return StringFormatUtil.getFormattedSeqNo(company.getCompanyCode(), sequenceNo);
			}
		}
		return String.valueOf(sequenceNo);
	}

	@OneToMany
	@JoinColumn (name = "AR_RECEIPT_ID", insertable=false, updatable=false)
	public List<AcArLine> getAcArLines() {
		return acArLines;
	}

	public void setAcArLines(List<AcArLine> acArLines) {
		this.acArLines = acArLines;
	}

	@Transient
	public String getAcArLinesJson() {
		return acArLinesJson;
	}

	public void setAcArLinesJson(String acArLinesJson) {
		this.acArLinesJson = acArLinesJson;
	}

	@Transient
	public String getAcArLineMesage() {
		return acArLineMesage;
	}

	public void setAcArLineMesage(String acArLineMesage) {
		this.acArLineMesage = acArLineMesage;
	}

	@Transient
	public void serializeAcArLines(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		acArLinesJson = gson.toJson(acArLines);
	}

	@Transient
	public void deserializeAcArLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<AcArLine>>(){}.getType();
		acArLines = gson.fromJson(acArLinesJson, type);
	}

	@Transient
	public String getArRTransactionsJson() {
		return arRTransactionsJson;
	}

	public void setArRTransactionsJson(String arRTransactionsJson) {
		this.arRTransactionsJson = arRTransactionsJson;
	}

	@Transient
	public String getArTransactionMessage() {
		return arTransactionMessage;
	}

	public void setArTransactionMessage(String arTransactionMessage) {
		this.arTransactionMessage = arTransactionMessage;
	}

	@Transient
	public void serializeTransactionLines(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		arRTransactionsJson = gson.toJson(arRTransactions);
	}

	@Transient
	public void deserializeTransactionLines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ArReceiptTransaction>>(){}.getType();
		arRTransactions = gson.fromJson(arRTransactionsJson, type);
	}

	@Transient
	public List<ArReceiptAdvancePayment> getArAdvPayments() {
		return arAdvPayments;
	}

	public void setArAdvPayments(List<ArReceiptAdvancePayment> arAdvPayments) {
		this.arAdvPayments = arAdvPayments;
	}

	@Transient
	public void serializeAdvPayments(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		arAdvPaymentJson = gson.toJson(arAdvPayments);
	}

	@Transient
	public void deserializeAdvPayments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ArReceiptAdvancePayment>>(){}.getType();
		arAdvPayments = gson.fromJson(arAdvPaymentJson, type);
	}

	@Transient
	public String getArAdvPaymentJson() {
		return arAdvPaymentJson;
	}

	public void setArAdvPaymentJson(String arAdvPaymentJson) {
		this.arAdvPaymentJson = arAdvPaymentJson;
	}

	@Transient
	public Double getRemainingBalance() {
		return remainingBalance;
	}

	public void setRemainingBalance(Double remainingBalance) {
		this.remainingBalance = remainingBalance;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<>();
		if (acArLines != null && !acArLines.isEmpty()) {
			children.addAll(acArLines);
		}
		if (arAdvPayments != null && !arAdvPayments.isEmpty()) {
			children.addAll(arAdvPayments);
		}
		if(referenceDocuments != null && !referenceDocuments.isEmpty()) {
			children.addAll(referenceDocuments);
		}
		if(arReceiptLines != null && !arReceiptLines.isEmpty()) {
			children.addAll(arReceiptLines);
		}
		return children;
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
	public List<ArReceiptLine> getArReceiptLines() {
		return arReceiptLines;
	}

	public void setArReceiptLines(List<ArReceiptLine> arReceiptLines) {
		this.arReceiptLines = arReceiptLines;
	}

	@Transient
	public String getArReceiptLineJson() {
		return arReceiptLineJson;
	}

	public void setArReceiptLineJson(String arReceiptLineJson) {
		this.arReceiptLineJson = arReceiptLineJson;
	}

	@Transient
	public void serializeArReceiptLines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		arReceiptLineJson = gson.toJson(arReceiptLines);
	}

	@Transient
	public void deserializeArReceiptLines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ArReceiptLine>>(){}.getType();
		arReceiptLines = gson.fromJson(arReceiptLineJson, type);
	}

	@Column (name = "RECOUPMENT")
	public Double getRecoupment() {
		return recoupment;
	}

	public void setRecoupment(Double recoupment) {
		this.recoupment = recoupment;
	}

	@Column (name = "RETENTION")
	public Double getRetention() {
		return retention;
	}

	public void setRetention(Double retention) {
		this.retention = retention;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArReceipt [arReceiptTypeId=").append(arReceiptTypeId).append(", companyId=").append(companyId)
				.append(", refNumber=").append(refNumber).append(", serviceLeaseKeyId=").append(serviceLeaseKeyId)
				.append(", sequenceNo=").append(sequenceNo).append(", receiptMethodId=").append(receiptMethodId)
				.append(", arCustomerId=").append(arCustomerId).append(", arCustomerAccountId=")
				.append(arCustomerAccountId).append(", receiptNumber=").append(receiptNumber).append(", receiptDate=")
				.append(receiptDate).append(", maturityDate=").append(maturityDate).append(", amount=").append(amount)
				.append(", arRTransactionMessage=").append(arRTransactionMessage).append(", appliedStatusId=")
				.append(appliedStatusId).append(", totalTAmount=").append(totalTAmount).append(", remainingBalance=")
				.append(remainingBalance).append(", divisionId=").append(divisionId).append(", currencyId=")
				.append(currencyId).append(", currencyRateId=").append(currencyRateId).append(", currencyRateValue=")
				.append(currencyRateValue).append("]");
		return builder.toString();
	}
}
