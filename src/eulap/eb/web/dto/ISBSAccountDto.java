package eulap.eb.web.dto;

/**
 * Income Statement and Balance Sheet account Dto.


 * 
 */
public class ISBSAccountDto implements Cloneable {
	private Double amount;
	private String divisionName;
	private Integer divisionId;
	private String accountName;
	private Integer accountId;
	private Integer level;
	private String parentAccountId;
	private Double budgetAmount;
	private Integer accountTypeId;
	private Integer pAcctId;
	private Integer companyId;
	private Integer month;
	private String monthName;
	private String acctNo;
	private Boolean parent;

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getParentAccountId() {
		return parentAccountId;
	}

	public void setParentAccountId(String parentAccountId) {
		this.parentAccountId = parentAccountId;
	}

	public Double getBudgetAmount() {
		return budgetAmount;
	}

	public void setBudgetAmount(Double budgetAmount) {
		this.budgetAmount = budgetAmount;
	}

	public Integer getAccountTypeId() {
		return accountTypeId;
	}

	public void setAccountTypeId(Integer accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	public Object clone() throws CloneNotSupportedException{  
		return super.clone();
	}

	public Integer getpAcctId() {
		return pAcctId;
	}

	public void setpAcctId(Integer pAcctId) {
		this.pAcctId = pAcctId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ISBSAccountDto [amount=").append(amount).append(", divisionName=").append(divisionName)
				.append(", divisionId=").append(divisionId).append(", accountName=").append(accountName)
				.append(", accountId=").append(accountId).append(", level=").append(level).append(", parentAccountId=")
				.append(parentAccountId).append(", budgetAmount=").append(budgetAmount).append(", accountTypeId=")
				.append(accountTypeId).append(", pAcctId=").append(pAcctId).append(", companyId=").append(companyId)
				.append(", acctNo=").append(acctNo).append("]");
		return builder.toString();
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

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public Boolean getParent() {
		return parent;
	}

	public void setParent(Boolean parent) {
		this.parent = parent;
	}
}
