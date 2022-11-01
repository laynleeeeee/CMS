package eulap.eb.web.dto;

/**
 * A class that handles the customer balances summary report.

 */
public class CustomerBalancesSummaryDto {
	private String customerName;
	private Double totalTransaction;
	private Double totalReceipt;
	private Double balance;
	private Double totalAdvances;
	private String drReferenceIds;
	private String division;
	private Double gainLoss;
	private String currency;

	public static CustomerBalancesSummaryDto getInstanceOf(String customerName, Double totalTransaction,
			Double totalReceipt, Double balance, Double totalAdvances, String drReferenceIds) {
		CustomerBalancesSummaryDto dto =  new CustomerBalancesSummaryDto();
		dto.customerName = customerName;
		dto.totalTransaction = totalTransaction;
		dto.totalReceipt = totalReceipt;
		dto.balance = balance;
		dto.totalAdvances = totalAdvances;
		dto.drReferenceIds = drReferenceIds;
		return dto;
	}

	public static CustomerBalancesSummaryDto getInstanceOf(String customerName, Double totalTransaction,
			Double totalReceipt, Double balance, Double totalAdvances) {
		CustomerBalancesSummaryDto dto =  new CustomerBalancesSummaryDto();
		dto.customerName = customerName;
		dto.totalTransaction = totalTransaction;
		dto.totalReceipt = totalReceipt;
		dto.balance = balance;
		dto.totalAdvances = totalAdvances;
		return dto;
	}

	public static CustomerBalancesSummaryDto getInstancesOf(String customerName, Double totalTransaction,
			Double totalReceipt, Double gainLoss, Double balance) {
		CustomerBalancesSummaryDto dto =  new CustomerBalancesSummaryDto();
		dto.customerName = customerName;
		dto.totalTransaction = totalTransaction;
		dto.totalReceipt = totalReceipt;
		dto.balance = balance;
		dto.gainLoss = gainLoss;
		return dto;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Double getTotalTransaction() {
		return totalTransaction;
	}

	public void setTotalTransaction(Double totalTransaction) {
		this.totalTransaction = totalTransaction;
	}

	public Double getTotalReceipt() {
		return totalReceipt;
	}

	public void setTotalReceipt(Double totalReceipt) {
		this.totalReceipt = totalReceipt;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getTotalAdvances() {
		return totalAdvances;
	}

	public void setTotalAdvances(Double totalAdvances) {
		this.totalAdvances = totalAdvances;
	}

	public String getDrReferenceIds() {
		return drReferenceIds;
	}

	public void setDrReferenceIds(String drReferenceIds) {
		this.drReferenceIds = drReferenceIds;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Double getGainLoss() {
		return gainLoss;
	}

	public void setGainLoss(Double gainLoss) {
		this.gainLoss = gainLoss;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CustomerBalancesSummaryDto [customerName=").append(customerName).append(", totalTransaction=")
				.append(totalTransaction).append(", totalReceipt=").append(totalReceipt).append(", balance=")
				.append(balance).append(", totalAdvances=").append(totalAdvances).append(", drReferenceIds=")
				.append(drReferenceIds).append(", division=").append(division).append(", gainLoss=").append(gainLoss)
				.append(", currency=").append(currency).append("]");
		return builder.toString();
	}
}
