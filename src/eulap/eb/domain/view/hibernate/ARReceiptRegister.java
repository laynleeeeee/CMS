package eulap.eb.domain.view.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Object representation of V_AR_RECEIPT_REGISTER view.

 * 
 */
@Entity
@Table(name = "V_AR_RECEIPT_REGISTER")
public class ARReceiptRegister {
	private String source;
	private Integer sourceId;
	private String id;
	private String seqNo;
	private Integer companyId;
	private Integer divisionId;
	private Integer receiptTypeId;
	private Integer receiptMethodId;
	private Integer customerId;
	private Integer customerAcctId;
	private Integer statusId;
	private String receiptType;
	private Date receiptDate;
	private Date maturityDate;
	private String receiptNumber;
	private String checkNumber;
	private String customer;
	private String customerAcct;
	private String receiptMethod;
	private Double amount;
	private Double paidAmount;
	private Double balance;
	private String status;
	private String division;
	private String cancellationRemarks;


	public static int ALL_OPTION = -1;
	public static int ACCT_COLLECTION = 1;
	public static int AR_MISC = 2;
	public static int CASH_SALE = 3;
	public static int CUSTOMER_ADV_PAYMENT = 4;
	public static int CASH_SALE_IS = 6;
	public static int CUSTOMER_ADVANCE_PAYMENT_IS = 7;
	public static int CASH_SALE_PROCESSING = 9;

	public static String SOURCE_ACCT_COLLECTION = "Account Collection";
	public static String SOURCE_AR_MISC = "Other Receipt";
	public static String SOURCE_CASH_SALES = "Cash Sales";
	public static String SOURCE_CUSTOMER_ADVANCE_PAYMENT = "Customer Advance Payment";
	public static String SOURCE_CASH_SALES_IS = "Cash Sales - IS";
	public static String SOURCE_CUSTOMER_ADVANCE_PAYMENT_IS = "Customer Advance Payment - IS";
	public static String SOURCE_CASH_SALES_PR = "Cash Sales - Processing";

	public enum FIELD {
		source, sourceId, seqNo, companyId, receiptTypeId, receiptMethodId, customerId,
		customerAcctId,divisionId, statusId, amount, receiptDate, maturityDate, receiptNumber,
		customer, customerAcct, receiptMethod
	}

	@Column(name = "DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column(name = "DIVISION")
	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	@Column(name = "CANCELLATION_REMARKS")
	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	@Column(name = "SOURCE", columnDefinition = "varchar(18)")
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Column (name="SOURCE_ID", columnDefinition="bigint(20)")
	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	@Id
	@Column (name="ID", columnDefinition="varchar(14)", unique=false)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column (name="SEQ_NO", columnDefinition="varchar(20)")
	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	@Column (name="COMPANY_ID", columnDefinition="int(11)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column (name="RECEIPT_TYPE_ID", columnDefinition="int(11)")
	public Integer getReceiptTypeId() {
		return receiptTypeId;
	}

	public void setReceiptTypeId(Integer receiptTypeId) {
		this.receiptTypeId = receiptTypeId;
	}

	@Column (name="RECEIPT_METHOD_ID", columnDefinition="int(11)")
	public Integer getReceiptMethodId() {
		return receiptMethodId;
	}

	public void setReceiptMethodId(Integer receiptMethodId) {
		this.receiptMethodId = receiptMethodId;
	}

	@Column (name="CUSTOMER_ID", columnDefinition="int(11)")
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	@Column (name="CUSTOMER_ACCT_ID", columnDefinition="int(11)")
	public Integer getCustomerAcctId() {
		return customerAcctId;
	}

	public void setCustomerAcctId(Integer customerAcctId) {
		this.customerAcctId = customerAcctId;
	}

	@Column (name="STATUS_ID", columnDefinition="int(11)")
	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	@Column (name="RECEIPT_TYPE", columnDefinition="varchar(20)")
	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	@Column (name="RECEIPT_DATE", columnDefinition="date")
	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	@Column (name="MATURITY_DATE", columnDefinition="date")
	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	@Column (name="RECEIPT_NO", columnDefinition="varchar(125)")
	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	@Column (name="CHECK_NO", columnDefinition="varchar(20)")
	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	@Column (name="CUSTOMER", columnDefinition="varchar(50)")
	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	@Column (name="CUSTOMER_ACCT", columnDefinition="varchar(100)")
	public String getCustomerAcct() {
		return customerAcct;
	}

	public void setCustomerAcct(String customerAcct) {
		this.customerAcct = customerAcct;
	}

	@Column (name="RECEIPT_METHOD", columnDefinition="varchar(100)")
	public String getReceiptMethod() {
		return receiptMethod;
	}

	public void setReceiptMethod(String receiptMethod) {
		this.receiptMethod = receiptMethod;
	}

	@Column (name="AMOUNT", columnDefinition="double")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column (name="PAID_AMOUNT", columnDefinition="double")
	public Double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

	@Column (name="BALANCE", columnDefinition="double")
	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@Column (name="STATUS", columnDefinition="varchar(30)")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ARReceiptRegister [source=").append(source).append(", sourceId=").append(sourceId)
				.append(", id=").append(id).append(", seqNo=").append(seqNo).append(", companyId=").append(companyId)
				.append(", divisionId=").append(divisionId).append(", division=").append(division)
				.append(", receiptTypeId=").append(receiptTypeId).append(", receiptMethodId=").append(receiptMethodId)
				.append(", customerId=").append(customerId).append(", customerAcctId=").append(customerAcctId)
				.append(", statusId=").append(statusId).append(", receiptType=").append(receiptType)
				.append(", receiptDate=").append(receiptDate).append(", maturityDate=").append(maturityDate)
				.append(", receiptNumber=").append(receiptNumber).append(", checkNumber=").append(checkNumber)
				.append(", customer=").append(customer).append(", customerAcct=").append(customerAcct)
				.append(", receiptMethod=").append(receiptMethod).append(", amount=").append(amount)
				.append(", paidAmount=").append(paidAmount).append(", balance=").append(balance)
				.append(", cancellationRemarks=").append(cancellationRemarks)
				.append(", status=")
				.append(status).append("]");
		return builder.toString();
	}
}
