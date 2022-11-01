package eulap.eb.web.dto;

import java.util.Date;

import javax.persistence.Transient;

import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.Term;

/**
 * A class that handles the transaction report data.

 */
public class ArTransactionRegisterDto {
	private Integer id;
	private ArTransactionType arTransactionType;
	private Date transactionDate;
	private String transactionNumber;
	private ArCustomer arCustomer;
	private ArCustomerAccount customerAccount;
	private Term term;
	private Date glDate;
	private Date dueDate;
	private double amount;
	private double balance;
	private String status;
	private Integer sequenceNumber;
	private String refNo;
	private String customerName;
	private String customerAcctName;
	private String transactioType;
	private String termName;
	private String formattedSequenceNo;
	private Integer arTransactionId;
	private Integer customerId;
	private double totalPayment;
	private double depositAmt;
	private String division;
	private String poNumber;
	private String cancellationRemarks;
	private String transactionClassification;
	private String collectionStatus;
	public static Integer ALL = -1;

	public static ArTransactionRegisterDto getInstanceOf(ArTransaction arTransaction, double balance, String status , 
			String cancellationRemarks,String division , String divisionName,String transactionClassification) {
		ArTransactionRegisterDto dto = new ArTransactionRegisterDto();
		dto.arTransactionType = arTransaction.getArTransactionType();
		dto.transactionDate =  arTransaction.getTransactionDate();
		dto.transactionNumber = arTransaction.getTransactionNumber();
		dto.arCustomer = arTransaction.getArCustomer();
		dto.customerAccount = arTransaction.getArCustomerAccount();
		dto.term = arTransaction.getTerm();
		if(arTransaction.getGlDate() == null) {
			//Set the transaction date if the GL date is null.
			dto.setGlDate(arTransaction.getTransactionDate());
		} else {
			dto.glDate = arTransaction.getGlDate();
		}
		dto.dueDate =  arTransaction.getDueDate();
		dto.amount = arTransaction.getAmount();
		dto.balance = balance;
		dto.status = status;
		dto.cancellationRemarks = cancellationRemarks;
		dto.division= division;
		dto.transactionClassification=transactionClassification;
		dto.sequenceNumber = arTransaction.getSequenceNumber();
		return dto;
	}


	public static ArTransactionRegisterDto getInstanceOf(Integer id, Integer sequenceNumber, 
			String transactionNumber, double amount, double depositAmt) {
		ArTransactionRegisterDto dto = new ArTransactionRegisterDto();
		dto.id = id;
		dto.sequenceNumber = sequenceNumber;
		dto.transactionNumber = transactionNumber;
		dto.amount = amount;
		dto.depositAmt = depositAmt;
		return dto;
	}

	@Transient
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ArTransactionType getArTransactionType() {
		return arTransactionType;
	}

	public void setArTransactionType(ArTransactionType arTransactionType) {
		this.arTransactionType = arTransactionType;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	public ArCustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(ArCustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAcctName() {
		return customerAcctName;
	}

	public void setCustomerAcctName(String customerAcctName) {
		this.customerAcctName = customerAcctName;
	}

	public String getTransactioType() {
		return transactioType;
	}

	public void setTransactioType(String transactioType) {
		this.transactioType = transactioType;
	}
	public String getTransactionClassifications() {
		return transactionClassification;
	}

	public void setTransactionClassification(String transactioClassification) {
		this.transactionClassification = transactioClassification;
	}
	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getFormattedSequenceNo() {
		return formattedSequenceNo;
	}

	public void setFormattedSequenceNo(String formattedSequenceNo) {
		this.formattedSequenceNo = formattedSequenceNo;
	}

	public Integer getArTransactionId() {
		return arTransactionId;
	}

	public void setArTransactionId(Integer arTransactionId) {
		this.arTransactionId = arTransactionId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(double totalPayment) {
		this.totalPayment = totalPayment;
	}

	public double getDepositAmt() {
		return depositAmt;
	}

	public void setDepositAmt(double depositAmt) {
		this.depositAmt = depositAmt;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	public String getCollectionStatus() {
		return collectionStatus;
	}

	public void setCollectionStatus(String collectionStatus) {
		this.collectionStatus = collectionStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArTransactionRegisterDto [id=").append(id).append(", arTransactionType=")
				.append(arTransactionType).append(", transactionDate=").append(transactionDate)
				.append(", transactionNumber=").append(transactionNumber).append(", term=").append(term)
				.append(", glDate=").append(glDate).append(", dueDate=").append(dueDate).append(", amount=")
				.append(amount).append(", balance=").append(balance).append(", status=").append(status)
				.append(", sequenceNumber=").append(sequenceNumber).append(", refNo=").append(refNo)
				.append(", customerName=").append(customerName).append(", customerAcctName=").append(customerAcctName)
				.append(", transactioType=").append(transactioType).append(", termName=").append(termName)
				.append(", formattedSequenceNo=").append(formattedSequenceNo).append(", arTransactionId=")
				.append(arTransactionId).append(", customerId=").append(customerId).append(", totalPayment=")
				.append(totalPayment).append(", depositAmt=").append(depositAmt).append(", division=").append(division)
				.append(", poNumber=").append(poNumber).append(", cancellationRemarks=").append(cancellationRemarks)
				.append(", transactionClassification=").append(transactionClassification).append("]");
		return builder.toString();
	}
}
