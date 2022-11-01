package eulap.eb.service.inventory;

import java.util.Date;
import java.util.List;

import eulap.eb.domain.hibernate.BaseItem;

/**
 * An interface that define the inventory transaction. 

 *
 */
public interface InventoryTransaction {
	
	/**
	 * Get the created date of this transaction
	 */
	Date getCreatedDate();
	
	/**
	 * Get the updated data of this transactions
	 */
	Date getUpdatedDate();
	
	/**
	 * Get the inventory items of this transaction.
	 */
	List<? extends BaseItem> getInventoryItems();
}
