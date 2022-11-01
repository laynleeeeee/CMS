package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.web.dto.StockAdjustmentDto;

/**
 * Data Access Object interface of {@link SerialItem}

 */

public interface SerialItemDao extends Dao<SerialItem> {

	/**
	 * Get the list of serialized item by reference objectId.
	 * @param refEbObjectId The reference object id.
	 * @param orTypeId Or type id,
	 * @param isCancelled True if Parent form is Cancelled, otherwise false.
	 * @return The {@link List<SerialItem>}
	 */
	List<SerialItem> getByReferenctObjectId(Integer refEbObjectId, Integer orTypeId, boolean isCancelled);

	/**
	 * Get the list of serialized item serial numbers
	 * @param serialNumber The serial number
	 * @param warehouseId The warehouse id
	 * @param itemId The item id
	 * @param isExact True if the serial number exists, otherwise false
	 * @param referenceObjectId 
	 * @return The list of serialized item serial numbers
	 */
	List<SerialItem> getItemSerialNumbers(String serialNumber, Integer warehouseId,
			Integer itemId, boolean isExact, Integer referenceObjectId);

	/**
	 * Check if has duplicate serial number.
	 * @param serialNumber The serial number.
	 * @param serializedItemId The serialize item id.
	 * @param itemId The item id
	 * @param isExistingOrUsed True when checking if existing, false when checking if used.
	 * @return True, if has duplicate serial number. Otherwise, false.
	 */
	boolean isExistingOrUsedSerialNumber(String serialNumber,
			Integer serializedItemId, Integer itemId, boolean isExistingOrUsed);

	/** 
	 * Get the {@link SerialItem} by Reference Id's.
	 * @param toObjectId The EbObjectId where the Item is associated.
	 * @param orTypeId The OrTypeId.
	 * @return The {@link SerialItem}.
	 */
	SerialItem getSerialItemByReference(Integer toObjectId, Integer orTypeId, boolean isActiveOnly);

	/**
	 * Get the list of cancelled serial items by ref Object.
	 * @param refEbObjectId The reference object id.
	 * @return The list of serial items.
	 */
	Page<SerialItem> getLatestUpdateSerialItemsByRef(Integer refEbObjectId);

	/**
	 * Get the reference form by serial number
	 * @param serialNumber The serial number
	 * @param warehouseId The warehouse id
	 * @param serialItemId The serial item id.
	 * @return The list of reference forms
	 */
	List<SerialItem> getFormByRefSerialNo(String serialNumber, Integer warehouseId, Integer serialItemId);

	/**
	 * Get the Serial Item Object by Object to Object Reference.
	 * @param refObjectId The reference object id.
	 * @param serialNumber The serial number
	 * @return The Serial Item Object.
	 */
	SerialItem getSerialItemByO2ORelationship(Integer refObjectId, String serialNumber);

	/**
	 * Get the Serial Item Object by serial number.
	 * @param serialNumber The serial number.
	 * @param itemId The item id
	 * @return The Serial Item Object.
	 */
	SerialItem getSerialItem(String serialNumber, Integer itemId);

	/**
	 * Get the total available balance per item.
	 * @param invoiceId The invoice id.
	 * @param itemId The item id. 
	 * @param poId The Purchase order id. 
	 * @return The total available balance of the item.
	 */
	double getAvailableStockFromPo(int invoiceId, Integer itemId, Integer poId);

	/**
	 * Get the list of stock adjustment items for both serial and non serial.
	 * @param companyId The id of the Company.
	 * @param warehouseId the id of the warehouse.
	 * @param divisionId The division id.
	 * @param stockAdjustmentTypeId The id of the Stock Adjustment Type.
	 * @param dateFrom The start date.
	 * @param dateTo the end date.
	 * @return The the list of stock adjustment items both serial and non serial.
	 */
	List<StockAdjustmentDto> getStockAdjustmentRegisterData(Integer companyId, Integer warehouseId, Integer divisionId,
			Integer stockAdjustmentTypeId, Date dateFrom, Date dateTo, Integer formStatusId);

	/**
	 * Get the remaining RR serial item for reference
	 * @param referenceObjId The reference object id
	 * @return The remaining RR serial item for reference
	 */
	List<SerialItem> getRrRemainingItems(Integer referenceObjId);

	/**
	 *  Get the remaining invoice goods/services serial item for reference
	 * @param referenceObjId The reference object id
	 * @return The remaining invoice goods/services serial item for reference
	 */
	List<SerialItem> getInvGoodsRemainingItems(Integer referenceObjId);

	/**
	 * Get the remaining PO serial item quantity
	 * @param referenceObjId The reference object id
	 * @param itemId The item id
	 * @return The remaining PO serial item quantity.
	 */
	double getPoRemainingQuantity(Integer referenceObjId, Integer itemId);

	/**
	 * Check if serial number already been used in RTS
	 * @param serialNumber The serial number
	 * @return True if already been used, otherwise false.
	 */
	boolean isAlreadyUsedSerialNumber(String serialNumber);
}
