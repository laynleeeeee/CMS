package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.CashSaleReturnDto;

/**
 * Data access object for {@link CashSaleReturnItem}

 *
 */
public interface CashSaleReturnItemDao extends Dao<CashSaleReturnItem> {
	/**
	 * Get the list of cash sales items by cash sales id.
	 * @param cashSaleReturnId The cash sales id.
	 * @param itemId The item id.
	 * @param warehouseId The warehouse id.
	 * @return The list of cash sales items.
	 */
	public List<CashSaleReturnItem> getCashSaleReturnItems(Integer cashSaleReturnId,
			Integer itemId, Integer warehouseId, boolean isExchangedItems);

	/**
	 * Get the total cash sale amount of all cash sale items per 
	 * cash sale return.
	 * @param cashSaleReturnId The cash sale id.
	 * @return The cash sale amount.
	 */
	public double getTotalCSRAmount (Integer cashSaleReturnId);

	/**
	 * Get the list of cash sale return items by cash sale item reference.
	 * @param referenceId The cash sale item id.
	 * @return The list of returns of the reference.
	 */
	List<CashSaleReturnItem> getCSaleReturnItems(int referenceId);
	
	/**
	 * Get the list of cash sale return items by cash sale reference.
	 * @param cashSaleId The reference.
	 * @return The list of cash sale return items.
	 */
	List<CashSaleReturnItem> getCSRItemsByReference (int cashSaleId);

	/**
	 * Compute the remaining quantity of the sale item.
	 * @param cashSaleItemId The unique cash sale item id.
	 * @param isCSAsReference true if reference is Cash Sale, otherwise false.
	 * @return The remaining stocks.
	 */
	double getRemainingQty(int cashSaleItemId, boolean isCSAsReference);

	/**
	 * Get the list cash sale return ref.
	 * @param companyId The company id.
	 * @param arCustomerId The customer id.
	 * @param arCustomerAccountId The ar customer account id.
	 * @param csNumber The cash sale number.
	 * @param dateFrom The date from.
	 * @param dateTo The date to.
	 * @param status The status.
	 * @param typeId The type id.
	 * @param pageNumber The page number.
	 * @param user The user logged.
	 * @return The list cash sale return ref.
	 */
	public Page<CashSaleReturnDto> getCashSaleReturnRef(Integer referenceId, String refType, Integer companyId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer refNumber, Date dateFrom, Date dateTo, Integer status, Integer typeId,
			User user, PageSetting pageSetting);

	/**
	 * Get the list of cash sale return items by cash sale return reference.
	 * @param cashSaleReturnId The cash sale return id.
	 * @param referenceId The reference cash sale return id.
	 * @return The list of returns of the reference csr.
	 */
	public List<CashSaleReturnItem> getCSRItemsByReferenceCSR(int referenceId, int cashSaleReturnId);

	/**
	 * Compute the remaining quantity of the sale item
	 * @param referenceId The reference cash sale/cash sale return id.
	 * @param itemId The item id
	 * @param warehouseId The warehouse id
	 * @param isCSAsReference True if the reference form used is cash sales, otherwise cash sale return
	 * @return The remaining stocks.
	 */
	double getRemainingQty(int referenceId, int itemId, int warehouseId, boolean isCSAsReference);
}