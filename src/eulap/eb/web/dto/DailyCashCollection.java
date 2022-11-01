package eulap.eb.web.dto;

/**
 * Daily cash collection dto.

 *
 */
public class DailyCashCollection {
	private String time;
	private String refNumber;
	private String invoiceNumber;
	private String customerName;
	private Double amount;

	public static DailyCashCollection getInstanceOf (String time, String refNumber, 
			String invoiceNumber, String customerName, Double amount) {
		DailyCashCollection dcc = new DailyCashCollection();
		dcc.time = time;
		dcc.refNumber = refNumber;
		dcc.invoiceNumber = invoiceNumber;
		dcc.customerName = customerName;
		dcc.amount = amount;
		return dcc;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRefNumber() {
		return refNumber;
	}

	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
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
		return "DailyCashCollection [time=" + time + ", refNumber=" + refNumber
				+ ", customerName=" + customerName + ", amount=" + amount + "]";
	}
}
