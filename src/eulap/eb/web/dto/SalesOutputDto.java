package eulap.eb.web.dto;

/**
 * The sales output report DTO.

 */
public class SalesOutputDto {
	private Integer customerId;
	private String customer;
	private Double amount;
	private String month;
	private Integer monthId;
	private Integer year;
	private Integer sort;
	private Double totalInvoice;
	private Integer rank;
	private Double average;

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Integer getMonthId() {
		return monthId;
	}

	public void setMonthId(Integer monthId) {
		this.monthId = monthId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Double getTotalInvoice() {
		return totalInvoice;
	}

	public void setTotalInvoice(Double totalInvoice) {
		this.totalInvoice = totalInvoice;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SalesOutput [customerId=").append(customerId).append(", customer=").append(customer)
				.append(", amount=").append(amount).append(", month=").append(month).append(", monthId=")
				.append(monthId).append(", year=").append(year).append("]");
		return builder.toString();
	}
}
