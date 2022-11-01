package eulap.eb.web.dto;

/**
 * The total invoice per marketing representative report DTO.

 */
public class InvoicePerRepresentativeDto {
	private String customer;
	private Double invoiceAmt;
	private String representative;

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public Double getInvoiceAmt() {
		return invoiceAmt;
	}

	public void setInvoiceAmt(Double invoiceAmt) {
		this.invoiceAmt = invoiceAmt;
	}

	public String getRepresentative() {
		return representative;
	}

	public void setRepresentative(String representative) {
		this.representative = representative;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoicePerRepresentativeDto [customer=").append(customer).append(", invoiceAmt=")
				.append(invoiceAmt).append(", representative=").append(representative).append("]");
		return builder.toString();
	}
}
