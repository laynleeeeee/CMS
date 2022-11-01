package eulap.eb.web.dto;

import java.util.Date;
import java.util.List;

/**
 * A class that handles the statement of account report.

 */
public class StatementOfAccountDto {
	private Integer id;
	private Date transactionDate;
	private String dueDate;
	private String invoiceNumber;
	private String transactionNumber;
	private Double transactionAmount;
	private Double collectionAmount;
	private Integer termId;
	private Integer termDays;
	private Double balance;
	private String arDescription;
	private Double arAmount;
	private Integer arLineId;
	private Integer counter;
	private List<StatementAccountDetail> statementAccountDetails;
	private String acSequenceNo;
	private Double collectedAdvanceAmt;
	private String drReferenceIds;
	private String division;
	private String drRefNumber;
	private Integer quantity;
	private String unitMeasurement;
	private double unitPrice;
	private String  poNumber;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public Double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(Double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public Double getCollectionAmount() {
		return collectionAmount;
	}

	public void setCollectionAmount(Double collectionAmount) {
		this.collectionAmount = collectionAmount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	public Integer getTermDays() {
		return termDays;
	}

	public void setTermDays(Integer termDays) {
		this.termDays = termDays;
	}

	public String getArDescription() {
		return arDescription;
	}

	public void setArDescription(String arDescription) {
		this.arDescription = arDescription;
	}

	public Double getArAmount() {
		return arAmount;
	}

	public void setArAmount(Double arAmount) {
		this.arAmount = arAmount;
	}

	public Integer getArLineId() {
		return arLineId;
	}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(Integer counter) {
		this.counter = counter;
	}

	public void setArLineId(Integer arLineId) {
		this.arLineId = arLineId;
	}

	public List<StatementAccountDetail> getStatementAccountDetails() {
		return statementAccountDetails;
	}

	public void setStatementAccountDetails(
			List<StatementAccountDetail> statementAccountDetails) {
		this.statementAccountDetails = statementAccountDetails;
	}

	public String getAcSequenceNo() {
		return acSequenceNo;
	}

	public void setAcSequenceNo(String acSequenceNo) {
		this.acSequenceNo = acSequenceNo;
	}

	public Double getCollectedAdvanceAmt() {
		return collectedAdvanceAmt;
	}

	public void setCollectedAdvanceAmt(Double collectedAdvanceAmt) {
		this.collectedAdvanceAmt = collectedAdvanceAmt;
	}

	public String getDrReferenceIds() {
		return drReferenceIds;
	}

	public void setDrReferenceIds(String drReferenceIds) {
		this.drReferenceIds = drReferenceIds;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getDrRefNumber() {
		return drRefNumber;
	}

	public void setDrRefNumber(String drRefNumber) {
		this.drRefNumber = drRefNumber;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getUnitMeasurement() {
		return unitMeasurement;
	}

	public void setUnitMeasurement(String unitMeasurement) {
		this.unitMeasurement = unitMeasurement;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StatementOfAccountDto [id=").append(id).append(", transactionDate=").append(transactionDate)
				.append(", dueDate=").append(dueDate).append(", invoiceNumber=").append(invoiceNumber)
				.append(", transactionNumber=").append(transactionNumber).append(", transactionAmount=")
				.append(transactionAmount).append(", collectionAmount=").append(collectionAmount).append(", termId=")
				.append(termId).append(", termDays=").append(termDays).append(", balance=").append(balance)
				.append(", arDescription=").append(arDescription).append(", arAmount=").append(arAmount)
				.append(", arLineId=").append(arLineId).append(", counter=").append(counter).append(", acSequenceNo=")
				.append(acSequenceNo).append(", collectedAdvanceAmt=").append(collectedAdvanceAmt)
				.append(", drReferenceIds=").append(drReferenceIds)
				.append(", division=").append(division)
				.append(", drRefNumber=").append(drRefNumber).append(", quantity=").append(quantity)
				.append(", unitMeasurement=").append(unitMeasurement).append(", unitPrice=").append(unitPrice)
				.append(", poNumber=").append(poNumber).append("]");
		return builder.toString();
	}
}
