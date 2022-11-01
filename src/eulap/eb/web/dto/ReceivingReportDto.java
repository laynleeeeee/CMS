package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.SerialItem;

/**
 * Container class that will hold the values for Receiving report print out.

 * 
 */
public class ReceivingReportDto {
	private List<RReceivingReportItem> receivingReportItems;
	private List<ApInvoiceLine> apInvoiceLines;
	private List<SerialItem> serialItems;

	public List<RReceivingReportItem> getReceivingReportItems() {
		return receivingReportItems;
	}

	public void setReceivingReportItems(
			List<RReceivingReportItem> receivingReportItems) {
		this.receivingReportItems = receivingReportItems;
	}

	public List<ApInvoiceLine> getApInvoiceLines() {
		return apInvoiceLines;
	}

	public void setApInvoiceLines(List<ApInvoiceLine> apInvoiceLines) {
		this.apInvoiceLines = apInvoiceLines;
	}

	public List<SerialItem> getSerialItems() {
		return serialItems;
	}

	public void setSerialItems(List<SerialItem> serialItems) {
		this.serialItems = serialItems;
	}

	@Override
	public String toString() {
		return "ReceivingReportDto [receivingReportItems="
				+ receivingReportItems + ", apInvoiceLines=" + apInvoiceLines
				+ "]";
	}
}
