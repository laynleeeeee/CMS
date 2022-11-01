package eulap.eb.web.dto;

/**
 * Monthly cash flow report detail.

 *
 */
public class MCFlowDetail {
	private Integer accountTypeId;
	private Integer accountId;
	private String accountName;
	private Integer month;
	private String monthName;
	private Double amount;
	private Integer year;

	/**
	 * Create an instance of {@code MCFlowDetail}
	 */
	public static MCFlowDetail getInstanceOf(Integer accountId, String accountName, Integer month,
			Integer year, String monthName, Double amount, Integer accountTypeId) {
		MCFlowDetail mcfd = new MCFlowDetail();
		mcfd.setAccountId(accountId);
		mcfd.setAccountName(accountName);
		mcfd.setMonth(month);
		mcfd.setMonthName(monthName);
		mcfd.setAmount(amount);
		mcfd.setYear(year);
		mcfd.setAccountTypeId(accountTypeId);
		return mcfd;
	}

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

	public Integer getAccountTypeId() {
		return accountTypeId;
	}

	public void setAccountTypeId(Integer accountTypeId) {
		this.accountTypeId = accountTypeId;
	}
}
