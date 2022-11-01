package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.RPurchaseOrderItem;

/**
 * Data Access Object {@link RPurchaseOrderItem}

 *
 */
public interface RPurchaseOrderItemDao extends Dao<RPurchaseOrderItem>{

	/**
	 * Get the list of Retail - PO Items.
	 * @param rPurchaseOrderId The unique PO id.
	 * @return The list of Retail - PO Items.
	 */
	List<RPurchaseOrderItem> getPOItems(int rPurchaseOrderId);

	/**
	 * Get the PO Remaining QTY by reference object id.
	 * @param refenceObjectId The reference object id.
	 * @param itemId The item id
	 * @return The remaining quantity.
	 */
	double getRemainingQty(Integer refenceObjectId, Integer itemId);

	/**
	 * Get the total quantity of {@link RPurchaseOrderItem} that are not yet received.
	 * @param itemId The item id.
	 * @return The quantity of not received {@link RPurchaseOrderItem}.
	 */
	double getNotReceivedPoiQty(Integer itemId, Integer poiId);

	/**
	 * Get {@link RPurchaseOrderItem} by child object id.
	 * @param objectId The child object id.
	 * @return The {@link RPurchaseOrderItem}.
	 */
	RPurchaseOrderItem getByChildObjectId(Integer objectId);
}
