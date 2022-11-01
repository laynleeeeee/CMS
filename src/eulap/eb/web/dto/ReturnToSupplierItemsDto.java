package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;
import eulap.eb.domain.hibernate.SerialItem;

/**
 * Return to supplier item details

 *
 */
public class ReturnToSupplierItemsDto {
	private List<RReturnToSupplierItem> rtsItems;
	private List<SerialItem> serialItems;
	private List<ApInvoiceLine> apInvoiceLines;

	public List<RReturnToSupplierItem> getRtsItems() {
		return rtsItems;
	}

	public void setRtsItems(List<RReturnToSupplierItem> rtsItems) {
		this.rtsItems = rtsItems;
	}

	public List<SerialItem> getSerialItems() {
		return serialItems;
	}

	public void setSerialItems(List<SerialItem> serialItems) {
		this.serialItems = serialItems;
	}

	public List<ApInvoiceLine> getApInvoiceLines() {
		return apInvoiceLines;
	}

	public void setApInvoiceLines(List<ApInvoiceLine> apInvoiceLines) {
		this.apInvoiceLines = apInvoiceLines;
	}

	@Override
	public String toString() {
		return "ReturnToSupplierItemsDto [rtsItems=" + rtsItems + ", serialItems=" + serialItems + ", apInvoiceLines="
				+ apInvoiceLines + "]";
	}
}
