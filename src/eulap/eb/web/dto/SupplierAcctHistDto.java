package eulap.eb.web.dto;

import java.util.Date;

/**
 * Container class for holding the values needed to generate the Supplier
 * Account History Report.

 */
public class SupplierAcctHistDto {
	private Integer id;
	private Integer invTypeId;
	private Integer supplierAccountId;
	private Integer divisionId;
	private String division;
	private Date date;
	private String source;
	private String invoiceNo;
	private String poNo;
	private String bmsNo;
	private String referenceNo;
	private String description;
	private String paymentRef;
	private Double invoiceAmount;
	private Double sapAmount;
	private Double paymentAmount;
	private Double balance;
	private Double gainLoss;
	private Integer currencyId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getInvTypeId() {
		return invTypeId;
	}

	public void setInvTypeId(Integer invTypeId) {
		this.invTypeId = invTypeId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(Double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(Integer supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getPoNo() {
		return poNo;
	}

	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}

	public String getBmsNo() {
		return bmsNo;
	}

	public void setBmsNo(String bmsNo) {
		this.bmsNo = bmsNo;
	}

	public Double getGainLoss() {
		return gainLoss;
	}

	public void setGainLoss(Double gainLoss) {
		this.gainLoss = gainLoss;
	}

	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SupplierAcctHistDto [id=").append(id).append(", invTypeId=").append(invTypeId)
				.append(", supplierAccountId=").append(supplierAccountId).append(", divisionId=").append(divisionId)
				.append(", division=").append(division).append(", date=").append(date).append(", source=")
				.append(source).append(", invoiceNo=").append(invoiceNo).append(", poNo=").append(poNo)
				.append(", bmsNo=").append(bmsNo).append(", referenceNo=").append(referenceNo).append(", description=")
				.append(description).append(", paymentRef=").append(paymentRef).append(", invoiceAmount=")
				.append(invoiceAmount).append(", paymentAmount=").append(paymentAmount).append(", balance=")
				.append(balance).append(", gainLoss=").append(gainLoss).append(", currencyId=").append(currencyId)
				.append("]");
		return builder.toString();
	}

	public Double getSapAmount() {
		return sapAmount;
	}

	public void setSapAmount(Double sapAmount) {
		this.sapAmount = sapAmount;
	}
}
