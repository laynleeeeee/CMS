package eulap.eb.web.dto;

/**
 * Daily item sales dto.

 *
 */
public class DailyItemSaleDetail {
	private String sequenceNo;
	private String invoiceNumber;
	private Double quantity;
	private Double amount;
	
	public static DailyItemSaleDetail getInstanceOf (String sequenceNo, String invoiceNumber, 
			Double quantity,  Double amount) {
		DailyItemSaleDetail disd = new DailyItemSaleDetail();
		disd.sequenceNo = sequenceNo;
		disd.invoiceNumber = invoiceNumber;
		disd.quantity = quantity;
		disd.amount = amount;
		return disd;
	}
	
	public String getSequenceNo() {
		return sequenceNo;
	}
	
	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Double getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "DailyItemSaleDetail [sequenceNo=" + sequenceNo
				+ ", invoiceNumber=" + invoiceNumber + ", quantity=" + quantity
				+ ", amount=" + amount + "]";
	}
}
