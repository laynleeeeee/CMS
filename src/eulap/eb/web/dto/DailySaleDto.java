package eulap.eb.web.dto;

/**
 * Daily sale dto.

 *
 */
public class DailySaleDto {
	private String sequenceNo;
	private String salesInvoiceNo;
	private String customerName;
	private Double amount;
	
	public static DailySaleDto getInstanceOf (String sequenceNo, String salesInvoiceNo, 
			String customerName, Double amount) {
		DailySaleDto dto = new DailySaleDto();
		dto.sequenceNo = sequenceNo;
		dto.salesInvoiceNo = salesInvoiceNo;
		dto.customerName = customerName;
		dto.amount = amount;
		return dto;
	}
	
	public String getSequenceNo() {
		return sequenceNo;
	}
	
	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	
	public String getSalesInvoiceNo() {
		return salesInvoiceNo;
	}
	
	public void setSalesInvoiceNo(String salesInvoiceNo) {
		this.salesInvoiceNo = salesInvoiceNo;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "DailySaleDto [sequenceNo=" + sequenceNo + ", salesInvoiceNo="
				+ salesInvoiceNo + ", customerName=" + customerName
				+ ", amount=" + amount + "]";
	}
}
