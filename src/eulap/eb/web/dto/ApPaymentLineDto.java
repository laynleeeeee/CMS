package eulap.eb.web.dto;

import com.google.gson.annotations.Expose;

/**
 * The AP Payment line DTO.

 */

public class ApPaymentLineDto{
	@Expose
	private Integer ebObjectId;
	@Expose
	private Integer sequenceNumber;
	@Expose
	private Double amount;
	@Expose
	private Double paidAmount;
	@Expose
	private Double balance;
	@Expose
	private String referenceNumber;
	@Expose
	private Integer apPaymentLineTypeId;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Integer apPaymentId;
	@Expose
	private Double currencyRateValue;
	private String accountName;
	private String accountNumber;
	private Double credit;
	private Double debit;
	private String particular;

	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public Integer getApPaymentLineTypeId() {
		return apPaymentLineTypeId;
	}

	public void setApPaymentLineTypeId(Integer apPaymentLineTypeId) {
		this.apPaymentLineTypeId = apPaymentLineTypeId;
	}

	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setRefenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	public Integer getApPaymentId() {
		return apPaymentId;
	}

	public void setApPaymentId(Integer apPaymentId) {
		this.apPaymentId = apPaymentId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public Double getDebit() {
		return debit;
	}

	public void setDebit(Double debit) {
		this.debit = debit;
	}

	public String getParticular() {
		return particular;
	}

	public void setParticular(String particular) {
		this.particular = particular;
	}

	public Double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(Double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApPaymentLineDto [ebObjectId=").append(ebObjectId).append(", sequenceNumber=")
				.append(sequenceNumber).append(", amount=").append(amount).append(", paidAmount=").append(paidAmount)
				.append(", balance=").append(balance).append(", referenceNumber=").append(referenceNumber)
				.append(", apPaymentLineTypeId=").append(apPaymentLineTypeId).append(", refenceObjectId=")
				.append(refenceObjectId).append(", apPaymentId=").append(apPaymentId).append(", accountName=")
				.append(accountName).append(", accountNumber=").append(accountNumber).append(", credit=").append(credit)
				.append(", debit=").append(debit).append(", particular=").append(particular).append("]");
		return builder.toString();
	}
}
