package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.web.dto.AvblStocksAndBagsDto;

/**
 * Data acces object for {@code ItemBagQuantity}

 *
 */
public interface ItemBagQuantityDao extends Dao<ItemBagQuantity>{

	/**
	 * Get the reference document object by the eb object id.
	 * @param ebObjectId The eb object id.
	 * @param orTypeId The or type id.
	 */
	ItemBagQuantity getByRefId (int refObjectId, int orTypeId);

	/**
	 * Get the Remaining Bag QTY of Account Sale Item
	 * to be used for returns.
	 * @param arTransactionId The arTransactionId.
	 * @param refAcctSaleItemId The reference accountSaleItemId.
	 * @return The remaining Bag QTY.
	 */
	Double getASIRemainingBagQty(Integer arTransactionId, Integer refAcctSaleItemId);

	/**
	 * Get the remaining Bag Qty of CAP-IS.
	 * @param capId The Customer Advance Payment Id.
	 * @param itemId The item id.
	 * @param capdId The Cap Delivery Id.
	 * @return The remaining qty available for PIAD.
	 */
	Double getCapIsRemainingBagQty(Integer capId, Integer itemId, Integer capdId);

	/**
	 * Get the available item bag quantity
	 * @param refObjectId The reference object id
	 * @return The list of available item bag quantities
	 */
	List<ItemBagQuantity> getAvailableItemBagQty(Integer refObjectId);

	/**
	 * Get the list of the available item bag quantity.
	 * @param companyId The company id.
	 * @param itemId The item id.
	 * @param warehoouseId The id of the warehouse.
	 * @param refBagQtyObjId The refBagQtyObjId.
	 * @param itemObjectId The ebObjectId of the Item.
	 * @return The list of available bag quantities.
	 */
	Page<AvblStocksAndBagsDto> getAvailableBags(Integer companyId, Integer itemId, Integer warehouseId, Integer refBagQtyObjId, Integer itemObjectId);

	/**
	 * Get available stocks and bags data.
	 * @param companyId The company id.
	 * @param itemId The item id.
	 * @param warehouseId The warehouse id.
	 * @param refBagQtyObjId The refBagQtyObjId.
	 * @param itemObjectId The ebObjectId of the Item.
	 * @param itemCategoryId The item category id.
	 * @param stockCode The stock code.
	 * @param asOfDate The as of date.
	 * @param orderBy Order by.
	 * @param pageSetting The page setting.
	 * @return The paged collection of available stocks and bags data.
	 */
	Page<AvblStocksAndBagsDto> getAvblBagsAndStocksRpt(Integer companyId, Integer itemId, Integer warehouseId, Integer refBagQtyObjId,
			Integer itemObjectId,Integer itemCategoryId, String stockCode, Date asOfDate, String orderBy, PageSetting pageSetting);

	/**
	 * Get the reference Available Bags.
	 * @param companyId The id of the company.
	 * @param sourceObjectId The ebObjectId of the source form.
	 * @param itemId The id of the Items.
	 * @param warehouseId The if of the warehouse
	 * @return The reference details.
	 */
	AvblStocksAndBagsDto getRefAvailableBags(Integer companyId, Integer sourceObjectId, Integer itemId, Integer warehouseId);
}
