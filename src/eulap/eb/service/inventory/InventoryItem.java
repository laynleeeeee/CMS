package eulap.eb.service.inventory;

import eulap.common.domain.Domain;

/**
 * The base class for inventory item.

 */
public interface InventoryItem extends Cloneable,Domain  {

	/**
	 * @return The unique id of the item.
	 */
	Integer getItemId();

	/**
	 * @return The stock code of the item.
	 */
	String getStockCode();

	/**
	 * The quantity of the item.
	 */
	Double getQuantity();

	/**
	 * Set the quantity of the item.
	 */
	void setQuantity(Double quantity);

	/**
	 * Get the inventory item.
	 */
	Double getInventoryCost();

	/**
	 * Set the inventory item.
	 */
	void setInventoryCost(Double inventoryCost);

	/**
	 * @return Get the unit cost.
	 */
	Double getUnitCost();

	/**
	 * Set the unit cost of the item.
	 */
	void setUnitCost(Double unitCost);

	/**
	 * True if this transaction will be ignored for allocation, otherwise false.
	 */
	boolean isIgnore();

	/**
	 * The {@link ItemCostAllocator} will disregard the allocation if the
	 * {@link #isIgnore()} is set to true.
	 * @param isIgnore Set to true to ignore the allocation for this transaction, otherwise false.
	 */
	void setIgnore(boolean isIgnore);

	/**
	 * Get the id of the Received Stock.
	 */
	Integer getReceivedStockId();

	/**
	 * Set the id of the Received Stock.
	 */
	void setReceivedStockId(Integer receivedStockId);

	/**
	 * Returns a copy of this object.
	 */
	Object clone() throws CloneNotSupportedException;
}
