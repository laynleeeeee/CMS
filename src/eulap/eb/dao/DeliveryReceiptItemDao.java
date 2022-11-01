package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.DeliveryReceiptItem;
import eulap.eb.domain.hibernate.SalesOrder;

/**
 * Data access object for {@link DeliveryReceiptItem}

 *
 */
public interface DeliveryReceiptItemDao extends Dao<DeliveryReceiptItem> {

	/**
	 * Get the List of {@link DeliveryReceiptItem} by reference and or type.
	 * @param refObjectId The reference object id.
	 * @param orTypeId The orTypeId.
	 * @return The List of {@link DeliveryReceiptItem}.
	 */
	List<DeliveryReceiptItem> getDRItems(Integer refObjectId, Integer orTypeId);

	/**
	 * Get the remaining qty of non serial item to be extracted from SO
	 * @param refObjectId the reference object id.
	 * @return The remaining qty available for {@link DeliveryReceipt}
	 */
	Double getRemainingQty(List<Integer> refObjectIds, Integer itemId);

	/**
	 * Evaluates whether the serial item is already served.
	 * @param ebObjectId The ebObjectId of the Serial Item from Order Slip.
	 * @return True if served, otherwise false.
	 */
	Boolean isSerialItemServed(Integer ebObjectId);

	/**
	 * Get the List of {@link DeliveryReceiptItem}
	 * @param refObjectId The reference object id.
	 * @param orTypeId The OR type id.
	 * @param itemId The item id.
	 * @return The List of {@link DeliveryReceiptItem}.
	 */
	List<DeliveryReceiptItem> getDRItems(Integer refObjectId, Integer orTypeId, Integer itemId);

	/**
	 * Get the remaining DR non-serialized item quantity.
	 * @param soId The {@link SalesOrder} id.
	 * @param itemId The item id.
	 * @return The remaining DR non-serialized item quantity.
	 */
	Double getSerialItemRemainingQty(Integer soId, Integer itemId);

	/**
	 * Get the remaining DR non-serialized item quantity.
	 * @param soId The {@link SalesOrder} id.
	 * @param itemId The item id.
	 * @return The remaining DR non-serialized item quantity.
	 */
	Double getNonSerialItemRemainingQty(Integer soId, Integer itemId);

	/**
	 * 
	 * @param drId
	 * @param refObjectId
	 * @return
	 */
	Double getRemainingRefItemQty(Integer drId, Integer refObjectId);
}
