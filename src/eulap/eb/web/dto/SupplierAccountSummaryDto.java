package eulap.eb.web.dto;

/**
 * Container class for holding the values needed to generate the Supplier
 * Account History Summary Report.

 */

public class SupplierAccountSummaryDto {
	private Double invoiceAmount;
	private Double paymentAmount;
	private Double outstandingBalance;
	private Double gainLoss;
	private Double sapAmount;

	public Double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(Double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Double getOutstandingBalance() {
		return outstandingBalance;
	}

	public void setOutstandingBalance(Double outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}

	public Double getGainLoss() {
		return gainLoss;
	}

	public void setGainLoss(Double gainLoss) {
		this.gainLoss = gainLoss;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SupplierAccountSummaryDto [invoiceAmount=").append(invoiceAmount).append(", paymentAmount=")
				.append(paymentAmount).append(", outstandingBalance=").append(outstandingBalance).append(", gainLoss=")
				.append(gainLoss).append("]");
		return builder.toString();
	}

	public Double getSapAmount() {
		return sapAmount;
	}

	public void setSapAmount(Double sapAmount) {
		this.sapAmount = sapAmount;
	}
}
