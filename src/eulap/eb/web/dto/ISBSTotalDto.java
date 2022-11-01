package eulap.eb.web.dto;

/**
 * Income Statement and Balance Sheet Total sDto.


 * 
 */
public class ISBSTotalDto {
	private Double amount;
	private Double budgetAmount;
	private Integer divisionId;
	private Integer month;
	private Integer companyId;

	/**
	 * Create instance of {@code ISBSTotalDto}
	 * @param acctTypeBudgetAmount 
	 */
	public static ISBSTotalDto getInstanceOf(Double amount, Double budgetAmount, Integer divisionId, Integer month) {
		ISBSTotalDto dto = new ISBSTotalDto();
		dto.setAmount(amount);
		dto.setBudgetAmount(budgetAmount);
		dto.setDivisionId(divisionId);
		dto.setMonth(month);
		return dto;
	}

	public static ISBSTotalDto getInstanceOf(Double amount, Integer companyId) {
		ISBSTotalDto dto = new ISBSTotalDto();
		dto.setAmount(amount);
		dto.setCompanyId(companyId);
		return dto;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getBudgetAmount() {
		return budgetAmount;
	}

	public void setBudgetAmount(Double budgetAmount) {
		this.budgetAmount = budgetAmount;
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
		builder.append("IncomeStatementTotal [amount=").append(amount).append(", divisionId=").append(divisionId)
			.append(", companyId=").append(companyId).append("]");
		return builder.toString();
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}
}
