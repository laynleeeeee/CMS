package eulap.eb.service.inventory;

import java.util.List;

/**
 * An interface for item allocator. 

 *
 */
public interface ItemAllocator <T extends InventoryItem> {
	
	/**
	 * Allocate the unit cost of the time. 
	 * @param item The item to be allocated with unit cost. 
	 */
	List<T> allocateCost (T item) throws CloneNotSupportedException;
}
