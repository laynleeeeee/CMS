package eulap.eb.domain.view.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Object representation of V_AR_LINE_ANALYSIS view.

 * 
 */
@Entity
@Table(name = "V_AR_LINE_ANALYSIS")
public class ARLineAnalysis {
	private String id;
	private String source;
	private Integer sourceId;
	private Integer seqNo;
	private Integer statusId;
	private String status;
	private boolean complete;
	private Integer companyId;
	private Integer arLineSetupId;
	private Integer arCustomerId;
	private Integer arCustomerAcctId;
	private Integer uomId;
	private Date receiptDate;
	private Date maturityDate;
	private Date refDate;
	private String refNumber;
	private String arCustomer;
	private String arCustomerAcct;
	private Double quantity;
	private Double unitPrice;
	private Double amount;
	private Double vatAmount;
	private String service;
	private Integer serviceId;
	private String division;
	private Integer divisionId;

	public static String SOURCE_AR_TRANSACTION = "AR Transaction";
	public static String SOURCE_OTHER_RECEIPT = "Other Receipt";
	public static String SOURCE_ACCOUNT_SALES = "Account Sales";
	public static String SOURCE_CASH_SALES = "Cash Sales";
	public static String SOURCE_CAP_DELIVERY = "Paid in Advance Delivery";
	public static String SOURCE_ACCOUNT_COLLECTION = "Account Collection";
	public static String SOURCE_CASH_SALES_IS = "Cash Sales - IS";
	public static String SOURCE_ACCOUNT_SALES_IS = "Account Sales - IS";
	public static String SOURCE_PROCESSING_REPORT = "Processing Report";
	public static String SOURCE_CAP_DELIVERY_IS = "Paid in Advance Delivery - IS";
	public static String SOURCE_WIP_SPECIAL_ORDER = "WIP - Special Order";
	public static String SOURCE_CAP_DELIVERY_AS = "Paid in Advance Delivery - AS";
	public static String SOURCE_ACCOUNT_SALES_RETURN = "Account Sales Return";
	public static String SOURCE_AR_INVOICE = "AR Invoice";

	public static int ALL_OPTION = -1;
	public static int AR_TRANSACTION = 1;
	public static int OTHER_RECEIPT = 2;
	public static int ACCOUNT_SALES = 3;
	public static int CASH_SALES = 4;
	public static int CAP_DELIVERY = 5;
	public static int ACCOUNT_COLLECTION = 6;
	public static int CASH_SALES_IS = 9;
	public static int ACCOUNT_SALES_IS = 10;
	public static int PROCESSING_REPORT = 11;
	public static int CAP_DELIVERY_IS = 14;
	public static int WIP_SPECIAL_ORDER = 21;
	public static int CAP_DELIVERY_AS = 22;
	public static int ACCOUNT_SALE_RETURN = 13;
	public static int AR_INVOICE = 23;

	public enum FIELD {
		id, sourceId, companyId, statusId, complete, arLineSetupId,
		arCustomerId, arCustomerAcctId, uomId, receiptDate, maturityDate,
		refDate, refNumber, arCustomer, arCustomerAcct, service,serviceId,divisionId,division
	}

	@Column (name="SOURCE", columnDefinition="varchar(16)")
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Column (name="SOURCE_ID", columnDefinition="bigint(11)")
	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	@Id
	@Column (name="ID", columnDefinition="int(11)")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column (name="SEQ_NO", columnDefinition="int(11)")
	public Integer getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	@Column (name="STATUS_ID", columnDefinition="int(11)")
	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	@Column (name="STATUS", columnDefinition="varchar(30)")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column (name="COMPLETE", columnDefinition="tinyint(4)")
	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	@Column (name="COMPANY_ID", columnDefinition="int(11)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column (name="CUSTOMER_ID", columnDefinition="int(11)")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Column (name="CUSTOMER_ACCT_ID", columnDefinition="int(11)")
	public Integer getArCustomerAcctId() {
		return arCustomerAcctId;
	}

	public void setArCustomerAcctId(Integer arCustomerAcctId) {
		this.arCustomerAcctId = arCustomerAcctId;
	}

	@Column (name="UOM_ID", columnDefinition="int(11)")
	public Integer getUomId() {
		return uomId;
	}

	public void setUomId(Integer uomId) {
		this.uomId = uomId;
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

	@Column (name="REF_DATE", columnDefinition="date")
	public Date getRefDate() {
		return refDate;
	}

	public void setRefDate(Date refDate) {
		this.refDate = refDate;
	}

	@Column (name="REF_NUMBER", columnDefinition="varchar(100)")
	public String getRefNumber() {
		return refNumber;
	}

	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	@Column (name="CUSTOMER", columnDefinition="varchar(50)")
	public String getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(String arCustomer) {
		this.arCustomer = arCustomer;
	}

	@Column (name="CUSTOMER_ACCT", columnDefinition="varchar(100)")
	public String getArCustomerAcct() {
		return arCustomerAcct;
	}

	public void setArCustomerAcct(String arCustomerAcct) {
		this.arCustomerAcct = arCustomerAcct;
	}

	@Column (name="QUANTITY", columnDefinition="double")
	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Column (name="UNIT_PRICE", columnDefinition="double")
	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Column (name="AMOUNT", columnDefinition="double")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column (name="VAT_AMOUNT", columnDefinition="double")
	public Double getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(Double vatAmount) {
		this.vatAmount = vatAmount;
	}

	@Column (name="DIVISION")
	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}
	@Column (name="DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column (name="SERVICE")
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
	@Column (name="SERVICE_ID")
	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ARLineAnalysis [source=").append(source)
				.append(", sourceId=").append(sourceId).append(", id=")
				.append(id).append(", seqNo=").append(seqNo)
				.append(", statusId=").append(statusId).append(", status=")
				.append(status).append(", complete=").append(complete)
				.append(", companyId=").append(companyId)
				.append(", arLineSetupId=").append(arLineSetupId)
				.append(", arCustomerId=").append(arCustomerId)
				.append(", arCustomerAcctId=").append(arCustomerAcctId)
				.append(", uomId=").append(uomId).append(", receiptDate=")
				.append(receiptDate).append(", maturityDate=")
				.append(maturityDate).append(", refDate=").append(refDate)
				.append(", refNumber=").append(refNumber)
				.append(", arCustomer=").append(arCustomer)
				.append(", arCustomerAcct=").append(arCustomerAcct)
				.append(", quantity=").append(quantity).append(", unitPrice=")
				.append(unitPrice).append(", amount=").append(amount)
				.append(", vatAmount=").append(vatAmount)
				.append(", service=").append(service)
				.append(", serviceId=").append(serviceId)
				.append(", division=").append(division)
				.append(", divisionId=").append(divisionId)
				.append("]");
		return builder.toString();
	}
}
