package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.web.dto.CSItemDto;

/**
 * Data access object for {@link CashSaleItem}

 *
 */
public interface CashSaleItemDao extends Dao<CashSaleItem> {
	
	/**
	 * Get the list of cash sales items by cash sales id.
	 * @param cashSaleId The cash sales id.
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @return The list of cash sales items.
	 */
	public List<CashSaleItem> getCashSaleItems(Integer cashSaleId, Integer itemId, Integer warehouseId);
	
	/**
	 * Get the list of cash sales items by cash sales id and max result.
	 * @param cashSaleId The cash sales id.
	 * @return The list of cash sales items.
	 */
	public List<CashSaleItem> getCSItemsWithLimit (Integer cashSaleId, int maxResult);
	
	
	/**
	 * Get the total cash sale amount of all cash sale items per 
	 * cash sale.
	 * @param cashSaleId The cash sale id.
	 * @return The cash sale amount.
	 */
	public double getTotalCSAmount (Integer cashSaleId);
	
	/**
	 * Get the size of cash sale items per cash sale.
	 * @param cashSaleId The cash sale id.
	 * @return The size of cash sale.
	 */
	public int getCSItemSize (Integer cashSaleId);
	
	/**
	 * 
	 * @param cashSaleId The cash sale id.
	 * @return True if there is duplicate, otherwise false.
	 */
	boolean hasDuplicate (int cashSaleId);
	
	/**
	 * 
	 * @param cashSaleId The cash sale id.
	 * @return Get the list of cash sale item dtos.
	 */
	List<CSItemDto> getCsItemDtos (int cashSaleId);

	/**
	 * Set to true if one of the cash sale item ids is a reference of Cash Sale Return.
	 * @param cashSalesItemIds List of cash sale ids.
	 * @return True if one of the ids from the list is used as a reference.
	 */
	boolean isReferenceId(List<Integer> cashSalesItemIds);

	/**
	 * Get the total cash sale amount of all cash sale items per 
	 * cash sale.
	 * @param date The cash sale receipt date.
	 * @return The cash sale amount.
	 */
	double getTotalAmountCSIByDate(Date date);

	/**
	 * Get the cash sale item total amount w/ tax
	 * @param cashSaleId The cash sale id
	 * @return The cash sale item total amount
	 */
	double getTotalCSIAmtWithTax(int cashSaleId);
}
