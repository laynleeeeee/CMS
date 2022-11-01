package eulap.eb.service;

import java.util.List;

import eulap.eb.service.inventory.InventoryItem;

/**
 * Base class for inventory Item.
 * 
 * TODO:
 * For now, this class will do nothing. We can discuss in the future for the 
 * proper implementation of this class.  


 *
 */
public class BaseInventoryItemSpecialHandler implements InventoryItemSpecialHandler {

	@Override
	public void updateItem(List<? extends InventoryItem> items) {
		// do nothing
	}

}
