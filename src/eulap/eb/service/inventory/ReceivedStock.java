package eulap.eb.service.inventory;

import java.util.Date;

/**
 * A class that holds the information of received stocks
 * 

 * 
 */
public class ReceivedStock {
	private final Date date;
	private final int itemId;
	private double quantity;
	private final double unitCost;
	private final double inventoryCost;
	private final String form;
	private final int formId;

	public static String RSS_FORM = "RSS";

	public ReceivedStock (Date date, int itemId, double quantity,
			double unitCost, double inventoryCost, String form, int formId) {
		this.date = date;
		this.itemId = itemId;
		this.quantity = quantity;
		this.unitCost = unitCost;
		this.inventoryCost = inventoryCost;
		this.form = form;
		this.formId = formId;
	}

	public static ReceivedStock getInstanceOf(Date date, int itemId, double quantity, double unitCost) {
		return new ReceivedStock(date, itemId, quantity, unitCost, 0, null, 0);
	}

	public Date getDate() {
		return date;
	}

	public int getItemId() {
		return itemId;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	public double getInventoryCost() {
		return inventoryCost;
	}

	public double getUnitCost() {
		return unitCost;
	}
	
	public String getForm() {
		return form;
	}

	public int getFormId() {
		return formId;
	}

	@Override
	public String toString() {
		return "ReceivedStocks [date=" + date + ", itemId=" + itemId
				+ ", quantity=" + quantity + ", unitCost=" + unitCost
				+ ", inventoryCost=" + inventoryCost + ", form=" + form + "]";
	}
}
