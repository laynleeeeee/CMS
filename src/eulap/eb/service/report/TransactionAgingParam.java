package eulap.eb.service.report;

import java.util.Date;


/**
 * A class that handles parameters in filtering Ar Transaction aging report.

 */
public class TransactionAgingParam {

	public static final int DUE_DATE_AGE_BASIS = 1;
	public static final int TRANSACTION_DATE_AGE_BASIS = 2;
	public static final int GL_DATE_AGE_BASIS = 3;

	private Integer companyId;
	private Integer transTypeID;
	private Integer customerId;
	private Integer customerAcctId;
	private Integer accountId;
	private String termId;
	private int ageBasis;
	private boolean showTrans;
	private int typeId;
	private Date asOfDate;
	private int divisionId;
	private int transactionClassificationId;
	private String classification;

	public Integer getTransTypeID() {
		return transTypeID;
	}

	public void setTransTypeID(Integer transTypeID) {
		this.transTypeID = transTypeID;
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

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public int getAgeBasis() {
		return ageBasis;
	}

	public void setAgeBasis(int ageBasis) {
		this.ageBasis = ageBasis;
	}

	public boolean isShowTrans() {
		return showTrans;
	}

	public void setShowTrans(boolean showTrans) {
		this.showTrans = showTrans;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
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

	public int  getTransactionClassificationId() {
		return transactionClassificationId;
	}

	public void setTransactionClassificationId(int transactionClassificationId) {
		this.transactionClassificationId = transactionClassificationId;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionAgingParam [companyId=").append(companyId).append(", transTypeID=")
				.append(transTypeID).append(", customerId=").append(customerId).append(", customerAcctId=")
				.append(customerAcctId).append(", termId=").append(termId).append(", ageBasis=").append(ageBasis)
				.append(", showTrans=").append(showTrans).append(", typeId=").append(typeId).append(", asOfDate=")
				.append(asOfDate).append(", divisionId=").append(divisionId)
				.append(", transactionClassificationId=").append(transactionClassificationId)
				.append(", classification=").append(classification).append("]");
		return builder.toString();
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
}
