package eulap.eb.web.dto;

/**
 * Monthly cash flow total data transfer object.

 *
 */
public class MCFlowTotalDto {
	private Integer accountId;
	private String accountName;
	private Integer month;
	private String monthName;
	private Double amount;
	private Integer year;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "MCFlowDetail [accountId=" + accountId + ", accountName=" + accountName + ", month=" + month
				+ ", monthName=" + monthName + ", amount=" + amount + "]";
	}
}
