package eulap.eb.web.dto;

import java.util.List;

/**
 * Bank Reconciliation Summary Dto

 */

public class BankReconSummaryDto{
	private Double bankBalance;
	private Double bookBalance;
	private Double adjustedBalance;
	private Double variance;
	private String bankAcctName;
	private String companyName;
	private List<BankRecon> bankRecons;

	public Double getBankBalance() {
		return bankBalance;
	}

	public void setBankBalance(Double bankBalance) {
		this.bankBalance = bankBalance;
	}

	public String getBankAcctName() {
		return bankAcctName;
	}

	public void setBankAcctName(String bankAcctName) {
		this.bankAcctName = bankAcctName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Double getBookBalance() {
		return bookBalance;
	}

	public void setBookBalance(Double bookBalance) {
		this.bookBalance = bookBalance;
	}

	public Double getAdjustedBalance() {
		return adjustedBalance;
	}

	public void setAdjustedBalance(Double adjustedBalance) {
		this.adjustedBalance = adjustedBalance;
	}

	public Double getVariance() {
		return variance;
	}

	public void setVariance(Double variance) {
		this.variance = variance;
	}

	public List<BankRecon> getBankRecons() {
		return bankRecons;
	}

	public void setBankRecons(List<BankRecon> bankRecons) {
		this.bankRecons = bankRecons;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BankReconSummaryDto [bankBalance=").append(bankBalance).append(", bookBalance=")
				.append(bookBalance).append(", adjustedBalance=").append(adjustedBalance).append(", variance=")
				.append(variance).append(", bankAcctName=").append(bankAcctName).append(", companyName=")
				.append(companyName).append(", bankRecons=").append(bankRecons).append("]");
		return builder.toString();
	}
}
