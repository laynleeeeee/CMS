package eulap.eb.web.processing.dto;

import eulap.eb.domain.hibernate.Item;

/**
 * Data transfer object for milling recovery 

 *
 */
public class Recovery {
	private Item item;
	private Double quantity;
	private boolean isMainProduct;

	public final static int UOM_OUTPUT_ID = 3;

	/**
	 * Create instance of recovery.
	 * @param item The item object.
	 * @param quantity The converted quantity.
	 * @return The recovery instance.
	 */
	public static Recovery getInstanceOf (Item item, Double quantity, boolean isMainProduct) {
		Recovery recovery = new Recovery();
		recovery.item = item;
		recovery.quantity = quantity;
		recovery.isMainProduct = isMainProduct;
		return recovery;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public boolean isMainProduct() {
		return isMainProduct;
	}

	public void setMainProduct(boolean isMainProduct) {
		this.isMainProduct = isMainProduct;
	}

	@Override
	public String toString() {
		return "Recovery [item=" + item + ", quantity=" + quantity
				+ ", isMainProduct=" + isMainProduct + "]";
	}
}
