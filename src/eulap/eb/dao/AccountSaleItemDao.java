package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArTransactionType;

/**
 * Data access object of {@link AccountSaleItem}

 *
 */
public interface AccountSaleItemDao extends Dao<AccountSaleItem>{

	/**
	 * Get the list of account sale items by account sale id.
	 * @param arTransactionId The transaction sale id.
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @return The list of account sale items.
	 */
	List<AccountSaleItem> getAccountSaleItems (Integer arTransactionId, Integer itemId, Integer warehouseId);

	/**
	 * Get the list of account sale items by account sale id.
	 * @param arTransactionId The transaction sale id.
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param isASExchangedItem True if {@link ArTransactionType} is Account Sale type,
	 * otherwise get exchanged items from Sale Return type.
	 * @return The list of account sale items.
	 */
	List<AccountSaleItem> getAccountSaleItems (Integer arTransactionId, Integer itemId,
			Integer warehouseId, boolean isASExchangedItem);

	/**
	 * Get the available balance by customer and date.
	 * @param arCustomerId The customer id.
	 * @return The available balance.
	 */
	double getTotalBalance (Integer arCustomerId, int arTransactionId);

	/**
	 * Get the total sales item of the customer.
	 * @param companyId The company id.
	 * @param customerAcctId The customer account id.
	 * @param asOfDate The date of account sales
	 * @return The total sales amount.
	 */
	double getTotalAcctSalesAndReturnItems(Integer companyId, Integer customerAcctId, Date asOfDate);

	/**
	 * Set to true if one of the account sale ids is a reference of Account Sale Return.
	 * @param acctSaleItemIds List of account sale ids.
	 * @return True if one of the ids from the list is used as a reference.
	 */
	boolean isReferenceId(List<Integer> acctSaleItemIds);

	/**
	 * Get the list of account sales return items.
	 * @param referenceId The reference id.
	 * @return The list of account sales return.
	 */
	List<AccountSaleItem> getSalesReturnItem(int referenceId);
	
	/**
	 * Get the list of account sale return items by cash sale reference.
	 * @param refAccountSaleId The reference.
	 * @return The list of account sale return items.
	 */
	List<AccountSaleItem> getASRItemsByReference (int refAccountSaleId);
	
	/**
	 * Get the total account sale items.
	 * @param arTransactionId The transaction id.
	 * @return The total amount of account sale items.
	 */
	double getTotalAcctSaleItems (int arTransactionId);

	/**
	 * Get the remaining quantity of the sale item.
	 * @param arTransactionId The id of the Account Sales.
	 * @return The remaining quantity to be returned.
	 */
	double getRemainingQty(int arTransactionId);

	/**
	 * Get the remaining quantity if the saved sale items
	 * @param saleRefId The sale form reference id
	 * @param itemId The item id
	 * @param warehouseId The warehouse id
	 * @return The remaining quantity
	 */
	double getRemainingQty(Integer saleRefId, Integer itemId, Integer warehouseId);
}
