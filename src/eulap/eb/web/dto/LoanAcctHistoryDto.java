package eulap.eb.web.dto;

import java.util.Date;

/**
 * Data transfer object class for loan history/loan balances report

 */

public class LoanAcctHistoryDto {
	private String supplier;
	private String division;
	private Date date;
	private String refNo;
	private String checkNo;
	private String description;
	private double loanAmount;
	private double principal;
	private double interest;
	private double totalPayment;
	private double balance;

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getCheckNo() {
		return checkNo;
	}

	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(double loanAmount) {
		this.loanAmount = loanAmount;
	}

	public double getPrincipal() {
		return principal;
	}

	public void setPrincipal(double principal) {
		this.principal = principal;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoanAcctHistoryDto [division=").append(division).append(", date=").append(date)
				.append(", refNo=").append(refNo).append(", checkNo=").append(checkNo).append(", description=")
				.append(description).append(", loanAmount=").append(loanAmount).append(", principal=").append(principal)
				.append(", interest=").append(interest).append(", totalPayment=").append(totalPayment)
				.append(", balance=").append(balance).append(", supplier=").append(supplier).append("]");
		return builder.toString();
	}
}
