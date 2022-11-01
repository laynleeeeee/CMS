package eulap.eb.service.inventory;

import java.util.Date;
import java.util.Queue;

import eulap.eb.web.dto.ItemTransaction;


/**
 * An interface for handling special process in updating of unit cost of the item
 * every time there is an update of status, cancellation and saving of previous dates.


 *
 */
public interface FormUnitCosthandler {

	/**
	 * Update the unit cost of items, this include all of the previous transaction of the item.
	 */
	void updateUnitCost (RItemCostUpdateService costUpdateService, WeightedAveItemAllocator<ItemTransaction> fifoAllocator, ItemTransaction it,
			int itemId, int warehouseId, Date formDate, boolean isAllocateRpTo);

	/**
	 * Process the allocated item unit cost
	 * @param itemId The item id
	 * @param warehouseId The warehoused id
	 * @param allocatedItems The list of allocated items
	 * @param currentAllocItem The current allocated items
	 * @throws CloneNotSupportedException
	 */
	void processAllocatedItem (int itemId, int warehouseId, Queue<ItemTransaction> allocatedItems,
			ItemTransaction currentAllocItem) throws CloneNotSupportedException;

	/**
	 * Set string form label
	 * @return The form label
	 */
	String getFormLabel ();
}