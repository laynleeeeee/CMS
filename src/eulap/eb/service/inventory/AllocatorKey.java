package eulap.eb.service.inventory;

/**
 * Keys used in the Hash Map for FIFO Allocator.

 *
 */
public class AllocatorKey {
	private Integer itemId;
	private Integer warehouseId;

	private AllocatorKey (Integer itemId, Integer warehouseId) {
		this.itemId = itemId;
		this.warehouseId = warehouseId;
	}

	/**
	 * Get the instance of the {@link AllocatorKey} object.
	 */
	public static AllocatorKey getInstanceOf(Integer itemId, Integer warehouseId) {
		return new AllocatorKey(itemId, warehouseId);
	}

	@Override
	public int hashCode() {
		if(itemId != null && warehouseId != null) {
			return itemId.hashCode() * warehouseId.hashCode();
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AllocatorKey))
			return false;
		AllocatorKey key = (AllocatorKey) obj;
		return itemId.equals(key.itemId) && warehouseId.equals(key.warehouseId);
	}
}
