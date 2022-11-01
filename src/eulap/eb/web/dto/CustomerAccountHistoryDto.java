package eulap.eb.web.dto;

import java.util.Date;

/**
 * A class that handles the customer account history report.

 */

public class CustomerAccountHistoryDto {
	private Date date;
	private String referenceNumber;
	private String invoiceNumber;
	private String description;
	private double transactionAmount;
	private double receiptAmount;
	private double balance;
	private double advances;
	private String division;
	private Integer currencyId;
	private String arOrNo;
	private String poNo;
	private double gainLoss;

	public static CustomerAccountHistoryDto getInstanceOf(Date date, String referenceNumber, String invoiceNumber,
			String description, String arOrNo, String poNo, double transactionAmount, double receiptAmount,
			double gainLoss, double balance, String division, Integer currencyId) {
		CustomerAccountHistoryDto dto =  new CustomerAccountHistoryDto();
		dto.date = date;
		dto.referenceNumber = referenceNumber;
		dto.invoiceNumber = invoiceNumber;
		dto.description = description;
		dto.arOrNo = arOrNo;
		dto.poNo = poNo;
		dto.transactionAmount = transactionAmount;
		dto.receiptAmount = receiptAmount;
		dto.gainLoss = gainLoss;
		dto.balance = balance;
		dto.division = division;
		dto.currencyId = currencyId;
		return dto;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public double getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(double receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getAdvances() {
		return advances;
	}

	public void setAdvances(double advances) {
		this.advances = advances;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	public String getArOrNo() {
		return arOrNo;
	}

	public void setArOrNo(String arOrNo) {
		this.arOrNo = arOrNo;
	}

	public String getPoNo() {
		return poNo;
	}

	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}

	public double getGainLoss() {
		return gainLoss;
	}

	public void setGainLoss(double gainLoss) {
		this.gainLoss = gainLoss;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CustomerAccountHistoryDto [date=").append(date).append(", referenceNumber=")
				.append(referenceNumber).append(", invoiceNumber=").append(invoiceNumber).append(", description=")
				.append(description).append(", transactionAmount=").append(transactionAmount).append(", receiptAmount=")
				.append(receiptAmount).append(", balance=").append(balance).append(", advances=").append(advances)
				.append(", division=").append(division).append(", currencyId=").append(currencyId).append(", arOrNo=")
				.append(arOrNo).append(", poNo=").append(poNo).append(", gainLoss=").append(gainLoss).append("]");
		return builder.toString();
	}
}
