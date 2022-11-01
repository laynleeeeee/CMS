package eulap.eb.web.dto;

import java.util.Date;

/**
 * Account analysis report dto.

 */
public class AccountAnalysisReport {
	private String source;
	private String divisionNumber;
	private String divisionName;
	private Date glDate;
	private String referenceNo;
	private String description;
	private int normalBalanceId;
	private double debitAmount;
	private double creditAmount;
	private double balance;
	private int divisionId;

	public static AccountAnalysisReport getInstanceOf(String source,
			String divisionName, Date glDate, String referenceNo,
			String description, double debitAmount, double creditAmount,
			double balance) {
		AccountAnalysisReport aar = new AccountAnalysisReport();
		aar.source = source;
		aar.divisionName = divisionName;
		aar.glDate = glDate;
		aar.referenceNo = referenceNo;
		aar.description = description;
		aar.debitAmount = debitAmount;
		aar.creditAmount = creditAmount;
		aar.balance = balance;
		return aar;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDivisionNumber() {
		return divisionNumber;
	}

	public void setDivisionNumber(String divisionNumber) {
		this.divisionNumber = divisionNumber;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
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

	public int getNormalBalanceId() {
		return normalBalanceId;
	}

	public void setNormalBalanceId(int normalBalanceId) {
		this.normalBalanceId = normalBalanceId;
	}

	public double getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(double debitAmount) {
		this.debitAmount = debitAmount;
	}

	public double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	@Override
	public String toString() {
		return "AccountAnalysisReport [source=" + source + ", divisionNumber="
				+ divisionNumber + ", divisionName=" + divisionName
				+ ", glDate=" + glDate + ", referenceNo=" + referenceNo
				+ ", description=" + description + ", normalBalanceId="
				+ normalBalanceId + ", debitAmount=" + debitAmount
				+ ", creditAmount=" + creditAmount + ", balance=" + balance
				+ "]";
	}
}
