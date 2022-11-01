package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceItem;

/**
 * Data transfer object for {@link ApInvoiceItem}

 */

public class ApInvoiceItemDto {
	private APInvoice apInvoice;
	private double netAmount;
	private List<ApInvoiceDto> apInvoiceDtos;

	public double getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(double netAmount) {
		this.netAmount = netAmount;
	}

	public APInvoice getApInvoice() {
		return apInvoice;
	}

	public void setApInvoice(APInvoice apInvoice) {
		this.apInvoice = apInvoice;
	}

	public List<ApInvoiceDto> getApInvoiceDtos() {
		return apInvoiceDtos;
	}

	public void setApInvoiceDtos(List<ApInvoiceDto> apInvoiceDtos) {
		this.apInvoiceDtos = apInvoiceDtos;
	}
}