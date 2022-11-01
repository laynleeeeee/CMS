package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.CAPDeliveryArLine;
import eulap.eb.domain.hibernate.CAPDeliveryItem;

/**
 * Container class that will hold the values for CAP Delivery Item.

 * 
 */
public class CapDeliveryDto {
	private List<CAPDeliveryItem> deliveryItems;
	private List<CAPDeliveryArLine> deliveryArLines;

	private CapDeliveryDto (List<CAPDeliveryItem> deliveryItems, List<CAPDeliveryArLine> deliveryArLines) {
		this.deliveryItems = deliveryItems;
		this.deliveryArLines = deliveryArLines;
	}

	public static CapDeliveryDto getInstanceOf(List<CAPDeliveryItem> deliveryItems, List<CAPDeliveryArLine> deliveryArLines) {
		return new CapDeliveryDto(deliveryItems, deliveryArLines);
	}

	public List<CAPDeliveryItem> getCashSaleItems() {
		return deliveryItems;
	}

	public List<CAPDeliveryItem> getdeliveryItems() {
		return deliveryItems;
	}

	public void setdeliveryItems(List<CAPDeliveryItem> deliveryItems) {
		this.deliveryItems = deliveryItems;
	}

	public List<CAPDeliveryArLine> getdeliveryArLines() {
		return deliveryArLines;
	}

	public void setdeliveryArLines(List<CAPDeliveryArLine> deliveryArLines) {
		this.deliveryArLines = deliveryArLines;
	}

	public void setCashSaleItems(List<CAPDeliveryItem> cashSaleItems) {
		this.deliveryItems = cashSaleItems;
	}

	public List<CAPDeliveryArLine> getCAPDeliveryArLines() {
		return deliveryArLines;
	}

	public void setArLines(List<CAPDeliveryArLine> arLines) {
		this.deliveryArLines = arLines;
	}

	@Override
	public String toString() {
		return "CapDeliveryDto [deliveryItems=" + deliveryItems + ", deliveryArLines=" + deliveryArLines + "]";
	}
}
