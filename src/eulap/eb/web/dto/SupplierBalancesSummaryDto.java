package eulap.eb.web.dto;

import eulap.eb.domain.hibernate.Supplier;

/**
 * A container class that handles the supplier balances summary report.

 *
 */
public class SupplierBalancesSummaryDto {
	private String supplierName;
	private Double totalInvoiceAmount;
	private Double totalPaidAmount;
	private Double totalGainLossAmount;
	private Double balance;

	public static SupplierBalancesSummaryDto getInstanceOf(Supplier supplier, Double totalInvoiceAmount,
			Double totalPaidAmount, Double totalGainLossAmount, Double balance) {
		SupplierBalancesSummaryDto dto = new SupplierBalancesSummaryDto();
		dto.supplierName = supplier.getName();
		dto.totalInvoiceAmount = totalInvoiceAmount;
		dto.totalPaidAmount = totalPaidAmount;
		dto.totalGainLossAmount = totalGainLossAmount;
		dto.balance = balance;
		return dto;
	}

	public static SupplierBalancesSummaryDto getInstanceOf(String supplierName, Double totalInvoiceAmount,
			Double totalPaidAmount, Double totalGainLossAmount, Double balance) {
		SupplierBalancesSummaryDto dto = new SupplierBalancesSummaryDto();
		dto.supplierName = supplierName;
		dto.totalInvoiceAmount = totalInvoiceAmount;
		dto.totalPaidAmount = totalPaidAmount;
		dto.totalGainLossAmount = totalGainLossAmount;
		dto.balance = balance;
		return dto;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Double getTotalInvoiceAmount() {
		return totalInvoiceAmount;
	}

	public void setTotalInvoiceAmount(Double totalInvoiceAmount) {
		this.totalInvoiceAmount = totalInvoiceAmount;
	}

	public Double getTotalPaidAmount() {
		return totalPaidAmount;
	}

	public void setTotalPaidAmount(Double totalPaidAmount) {
		this.totalPaidAmount = totalPaidAmount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getTotalGainLossAmount() {
		return totalGainLossAmount;
	}

	public void setTotalGainLossAmount(Double totalGainLossAmount) {
		this.totalGainLossAmount = totalGainLossAmount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SupplierBalancesSummaryDto [supplierName=").append(supplierName).append(", totalInvoiceAmount=")
				.append(totalInvoiceAmount).append(", totalPaidAmount=").append(totalPaidAmount)
				.append(", totalGainLossAmount=").append(totalGainLossAmount).append(", balance=").append(balance)
				.append("]");
		return builder.toString();
	}
}
