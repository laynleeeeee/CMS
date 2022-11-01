package eulap.eb.web.dto;

import java.util.Date;

/**
 * A class that handles the Ar Transaction aging report data.

 */
public class ArTransactionAgingDto {

	private String companyName;
	private String type;
	private String customer;
	private String customerAccount;
	private String term;
	private String transNumber;
	private Date transDate;
	private Date glDate;
	private Date dueDate;
	private double amount;
	private double range1To30;
	private double range0;
	private double range31To60;
	private double range61To90;
	private double range91To120;
	private double range121To150;
	private double range151ToUp;
	private int ageDays;
	private Integer termId;
	private Integer transactionId;
	private Integer customerId;
	private Integer customerAcctId;
	private Integer transactionTypeId;
	private double totalPayment;
	private double balance;
	private boolean reached61Days;
	private String division;
	private String transactionClassification;

	public ArTransactionAgingDto() {
	}

	public ArTransactionAgingDto (String companyName, String type, String customer, String customerAccount, String term,
			String transNumber, double amount, Date dueDate, Date transDate, Date glDate, Integer termId, Integer transactionId,
			Integer customerId, Integer customerAcctId, Integer transactionTypeId, double totalPayment, String division,
			String transactionClassification) {
		this.companyName = companyName;
		this.type = type;
		this.customer = customer;
		this.amount = amount;
		this.customerAccount = customerAccount;
		this.term = term;
		this.transNumber = transNumber;
		this.transDate = transDate;
		this.dueDate = dueDate;
		this.glDate = glDate;
		this.transactionId = transactionId;
		this.customerId = customerId;
		this.customerAcctId = customerAcctId;
		this.transactionTypeId = transactionTypeId;
		this.totalPayment = totalPayment;
		this.division= division;
		this.transactionClassification=transactionClassification;
	}

	public static ArTransactionAgingDto getInstanceOf(String companyName, String type, String customer, String customerAccount,
			String term, String transNumber, double amount, Date dueDate, Date transDate, Date glDate, Integer termId,
			Integer transactionId, Integer customerId, Integer customerAcctId, Integer transactionTypeId, double totalPayment,
			String division, String transactionClassification) {
		return new ArTransactionAgingDto(companyName, type, customer, customerAccount, term, transNumber, amount, dueDate,
				transDate, glDate, termId, transactionId, customerId, customerAcctId, transactionTypeId, totalPayment, division,
				transactionClassification);
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(String customerAccount) {
		this.customerAccount = customerAccount;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getTransNumber() {
		return transNumber;
	}

	public void setTransNumber(String transNumber) {
		this.transNumber = transNumber;
	}

	public Date getTransDate() {
		return transDate;
	}

	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getRange1To30() {
		return range1To30;
	}

	public void setRange1To30(double range1To30) {
		this.range1To30 = range1To30;
	}

	public double getRange31To60() {
		return range31To60;
	}

	public void setRange31To60(double range31To60) {
		this.range31To60 = range31To60;
	}

	public double getRange61To90() {
		return range61To90;
	}

	public void setRange61To90(double range61To90) {
		this.range61To90 = range61To90;
	}

	public double getRange91To120() {
		return range91To120;
	}

	public void setRange91To120(double range91To120) {
		this.range91To120 = range91To120;
	}

	public double getRange121To150() {
		return range121To150;
	}

	public void setRange121To150(double range121To150) {
		this.range121To150 = range121To150;
	}

	public double getRange151ToUp() {
		return range151ToUp;
	}

	public void setRange151ToUp(double range151ToUp) {
		this.range151ToUp = range151ToUp;
	}

	public int getAgeDays() {
		return ageDays;
	}

	public void setAgeDays(int ageDays) {
		this.ageDays = ageDays;
	}
	
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getCustomerAcctId() {
		return customerAcctId;
	}

	public void setCustomerAcctId(Integer customerAcctId) {
		this.customerAcctId = customerAcctId;
	}

	public Integer getTransactionTypeId() {
		return transactionTypeId;
	}

	public void setTransactionTypeId(Integer transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}

	public double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(double totalPayment) {
		this.totalPayment = totalPayment;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public boolean isReached61Days() {
		return reached61Days;
	}

	public void setReached61Days(boolean reached61Days) {
		this.reached61Days = reached61Days;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getTransactionClassifications() {
		return transactionClassification;
	}

	public void setTransactionClassification(String transactionClassification) {
		this.transactionClassification = transactionClassification;
	}

	public double getRange0() {
		return range0;
	}

	public void setRange0(double range0) {
		this.range0 = range0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArTransactionAgingDto [companyName=").append(companyName).append(", type=").append(type)
				.append(", customer=").append(customer).append(", customerAccount=").append(customerAccount)
				.append(", term=").append(term).append(", transNumber=").append(transNumber).append(", transDate=")
				.append(transDate).append(", glDate=").append(glDate).append(", dueDate=").append(dueDate)
				.append(", amount=").append(amount).append(", range1To30=").append(range1To30).append(", range31To60=")
				.append(range31To60).append(", range61To90=").append(range61To90).append(", range91To120=")
				.append(range91To120).append(", range121To150=").append(range121To150).append(", range151ToUp=")
				.append(range151ToUp).append(", ageDays=").append(ageDays).append(", termId=").append(termId)
				.append(", transactionId=").append(transactionId).append(", customerId=").append(customerId)
				.append(", customerAcctId=").append(customerAcctId).append(", transactionTypeId=")
				.append(transactionTypeId).append(", totalPayment=").append(totalPayment).append(", balance=")
				.append(balance).append(", reached61Days=").append(reached61Days).append(", division=").append(division)
				.append(", range0=").append(range0).append(", transactionClassification=").append(transactionClassification).append("]");
		return builder.toString();
	}
}
