package eulap.eb.service.report;

import java.util.Date;

/**
 * A class that handles different parameters in filtering the AR transaction
 * register report.
 *

 */
public class ArTransactionRegisterParam {
	private int companyId;
	private int transactionClassificationId;
	private int customerId;
	private int customerAcctId;
	private String transactionNumber;
	private Date transactionDateFrom;
	private Date transactionDateTo;
	private Date glDateFrom;
	private Date glDateTo;
	private Date dueDateFrom;
	private Date dueDateTo;
	private Double amountFrom;
	private Double amountTo;
	private Integer sequenceNoFrom;
	private Integer sequenceNoTo;
	private int transactionStatusId;
	private int paymentStatusId;
	private Date asOfDate;
	private int divisionId;

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int  getTransactionClassificationId() {
		return transactionClassificationId;
	}

	public void setTransactionClassificationId(int transactionClassificationId) {
		this.transactionClassificationId = transactionClassificationId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getCustomerAcctId() {
		return customerAcctId;
	}

	public void setCustomerAcctId(int customerAcctId) {
		this.customerAcctId = customerAcctId;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public Date getTransactionDateFrom() {
		return transactionDateFrom;
	}

	public void setTransactionDateFrom(Date transactionDateFrom) {
		this.transactionDateFrom = transactionDateFrom;
	}

	public Date getTransactionDateTo() {
		return transactionDateTo;
	}

	public void setTransactionDateTo(Date transactionDateTo) {
		this.transactionDateTo = transactionDateTo;
	}

	public Date getGlDateFrom() {
		return glDateFrom;
	}

	public void setGlDateFrom(Date glDateFrom) {
		this.glDateFrom = glDateFrom;
	}

	public Date getGlDateTo() {
		return glDateTo;
	}

	public void setGlDateTo(Date glDateTo) {
		this.glDateTo = glDateTo;
	}

	public Date getDueDateFrom() {
		return dueDateFrom;
	}

	public void setDueDateFrom(Date dueDateFrom) {
		this.dueDateFrom = dueDateFrom;
	}

	public Date getDueDateTo() {
		return dueDateTo;
	}

	public void setDueDateTo(Date dueDateTo) {
		this.dueDateTo = dueDateTo;
	}

	public Double getAmountFrom() {
		return amountFrom;
	}

	public void setAmountFrom(Double amountFrom) {
		this.amountFrom = amountFrom;
	}

	public Double getAmountTo() {
		return amountTo;
	}

	public void setAmountTo(Double amountTo) {
		this.amountTo = amountTo;
	}

	public Integer getSequenceNoFrom() {
		return sequenceNoFrom;
	}

	public void setSequenceNoFrom(Integer sequenceNoFrom) {
		this.sequenceNoFrom = sequenceNoFrom;
	}

	public Integer getSequenceNoTo() {
		return sequenceNoTo;
	}

	public void setSequenceNoTo(Integer sequenceNoTo) {
		this.sequenceNoTo = sequenceNoTo;
	}

	public int getTransactionStatusId() {
		return transactionStatusId;
	}

	public void setTransactionStatusId(int transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	public int getPaymentStatusId() {
		return paymentStatusId;
	}

	public void setPaymentStatusId(int paymentStatusId) {
		this.paymentStatusId = paymentStatusId;
	}

	public Date getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(Date asOfDate) {
		this.asOfDate = asOfDate;
	}

	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArTransactionRegisterParam [companyId=").append(companyId)
				.append(", transactionClassificationId=").append(transactionClassificationId).append(", customerId=")
				.append(customerId).append(", customerAcctId=").append(customerAcctId).append(", transactionNumber=")
				.append(transactionNumber).append(", transactionDateFrom=").append(transactionDateFrom)
				.append(", transactionDateTo=").append(transactionDateTo).append(", glDateFrom=").append(glDateFrom)
				.append(", glDateTo=").append(glDateTo).append(", dueDateFrom=").append(dueDateFrom)
				.append(", dueDateTo=").append(dueDateTo).append(", amountFrom=").append(amountFrom)
				.append(", amountTo=").append(amountTo).append(", sequenceNoFrom=").append(sequenceNoFrom)
				.append(", sequenceNoTo=").append(sequenceNoTo).append(", transactionStatusId=")
				.append(transactionStatusId).append(", paymentStatusId=").append(paymentStatusId).append(", asOfDate=")
				.append(asOfDate).append(", divisionId=").append(divisionId).append("]");
		return builder.toString();
	}
}
