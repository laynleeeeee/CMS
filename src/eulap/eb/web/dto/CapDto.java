package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.CapArLine;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentItem;

/**
 * Container class that will hold the values for Customer Advance Payment.

 * 
 */
public class CapDto {
	private List<CustomerAdvancePaymentItem> capItems;
	private List<CapArLine> capLines;

	private CapDto (List<CustomerAdvancePaymentItem> capItems, List<CapArLine> capLines) {
		this.capItems = capItems;
		this.capLines = capLines;
	}

	public static CapDto getInstanceOf(List<CustomerAdvancePaymentItem> capItems, List<CapArLine> capLines) {
		return new CapDto(capItems, capLines);
	}

	public List<CustomerAdvancePaymentItem> getCashSaleItems() {
		return capItems;
	}

	public List<CustomerAdvancePaymentItem> getCapItems() {
		return capItems;
	}

	public void setCapItems(List<CustomerAdvancePaymentItem> capItems) {
		this.capItems = capItems;
	}

	public List<CapArLine> getCapLines() {
		return capLines;
	}

	public void setCapLines(List<CapArLine> capLines) {
		this.capLines = capLines;
	}

	public void setCashSaleItems(List<CustomerAdvancePaymentItem> cashSaleItems) {
		this.capItems = cashSaleItems;
	}

	public List<CapArLine> getCapArLines() {
		return capLines;
	}

	public void setArLines(List<CapArLine> arLines) {
		this.capLines = arLines;
	}

	@Override
	public String toString() {
		return "CapDto [capItems=" + capItems + ", capLines=" + capLines + "]";
	}
}
