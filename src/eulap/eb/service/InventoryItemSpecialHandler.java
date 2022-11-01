package eulap.eb.service;

import java.util.List;

import eulap.eb.service.inventory.InventoryItem;

/**
 * An interface for special item handling.


 *
 */
public interface InventoryItemSpecialHandler {

	/**
	 * Do the special handling or update in the item list. 
	 * @param items The item list. 
	 */
	void updateItem (List<? extends InventoryItem> items);
}
