package eulap.eb.web.dto;

import java.util.Date;
import java.util.List;

public class StatementOfAccountMain {
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
	private Double totalCollection;
	private Double totalAmount;
	private Double totalBalance;
	private List<StatementOfAccountDto> statementOfAccountDtos;

	public List<StatementOfAccountDto> getStatementOfAccountDtos() {
		return statementOfAccountDtos;
	}
	public void setStatementOfAccountDtos(
			List<StatementOfAccountDto> statementOfAccountDtos) {
		this.statementOfAccountDtos = statementOfAccountDtos;
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

	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
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
	public void setArLineId(Integer arLineId) {
		this.arLineId = arLineId;
	}

	public Double getTotalCollection() {
		return totalCollection;
	}
	public void setTotalCollection(Double totalCollection) {
		this.totalCollection = totalCollection;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Double getTotalBalance() {
		return totalBalance;
	}
	public void setTotalBalance(Double totalBalance) {
		this.totalBalance = totalBalance;
	}
	@Override
	public String toString() {
		return "StatementOfAccountMain [transactionDate=" + transactionDate
				+ ", dueDate=" + dueDate + ", invoiceNumber=" + invoiceNumber
				+ ", transactionNumber=" + transactionNumber
				+ ", transactionAmount=" + transactionAmount
				+ ", collectionAmount=" + collectionAmount + ", termId="
				+ termId + ", termDays=" + termDays + ", balance=" + balance
				+ ", arDescription=" + arDescription + ", arAmount=" + arAmount
				+ ", arLineId=" + arLineId + ", statementOfAccountDtos="
				+ statementOfAccountDtos + "]";
	}
}
