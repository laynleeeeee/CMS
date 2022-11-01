package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ProductLine;
import eulap.eb.service.inventory.ReceivedStock;
import eulap.eb.web.dto.DailyItemSale;
import eulap.eb.web.dto.DailyItemSaleDetail;
import eulap.eb.web.dto.GrossProfitAnalysis;
import eulap.eb.web.dto.ItemSalesCustomer;
import eulap.eb.web.dto.ItemTransaction;
import eulap.eb.web.dto.ItemTransactionHistory;
import eulap.eb.web.dto.PhysicalInventory;
import eulap.eb.web.dto.RItemDetail;
import eulap.eb.web.dto.ReorderPointDto;
import eulap.eb.web.dto.StockcardDto;
import eulap.eb.web.processing.dto.AvailableStock;

/**
 * Item data access object.

 *
 */
public interface ItemDao extends Dao<Item>{

	/**
	 * Get the paged list of items.
	 * @param stockCode The stock code.
	 * @param description The description.
	 * @param unitMeasurementId The unit of measure.
	 * @param itemCategoryId The item category.
	 * @param pageSetting The page setting.
	 * @return The paged list of items.
	 */
	Page<Item> getRetailItems (String stockCode, String description, Integer unitMeasurementId,
			Integer itemCategoryId, int status, boolean isOrderByCategory, PageSetting pageSetting);

	/**
	 * Get the paged list of items.
	 * @param divisionId The division id.
	 * @param stockCode The stock code.
	 * @param description The description.
	 * @param unitMeasurementId The unit of measure.
	 * @param itemCategoryId The item category.
	 * @param pageSetting The page setting.
	 * @return The paged list of items.
	 */
	Page<Item> getRetailItemWithDivision (Integer divisionId, String stockCode, String description, Integer unitMeasurementId,
			Integer itemCategoryId, int status, boolean isOrderByCategory, PageSetting pageSetting);
	/**
	 * Checks if the stock code is unique.
	 * @param stockCode The item stock code.
	 * @param itemId The item id.
	 * @return True if unique, otherwise false.
	 */
	boolean isUniqueStockCode (String stockCode, int itemId);

	/**
	 * Get Item object using its stock code.
	 * @param stockCode The stock code.
	 * @return Item object, otherwise null.
	 */
	Item getItemByStockCode(String stockCode, Integer itemCategoryId);

	/**
	 * Get all active Items.
	 * @param isActive Set to true to get only active items, otherwise false.
	 * @param pageSetting The page setting.
	 * @return Paged list of items.
	 */
	Page<Item> getAllItems(boolean isActive, PageSetting pageSetting);

	/**
	 * Get the item using its stock code.
	 * @param stockCode The unique stock code of the item.
	 * @param itemCategoryId The id of the category of the item.
	 * @return The item object.
	 */
	Item getRetailItem(String stockCode, Integer warehouseId, Integer itemCategoryId, Integer companyId, Integer divisionId, Boolean isExcludeFG);

	/**
	 * get the item by its unique id.
	 * @param itemId The unique item id.
	 * @param companyId The id of the company.
	 * @return The item object.
	 */
	Item getRetailItem(Integer itemId, Integer companyId);

	/**
	 * Get the list of items using its stock code.
	 * @param stockCode The unique stock code of the item.
	 * @return The list of retail items.
	 */
	List<Item> getRetailItems(Integer companyId, Integer divisionId, Integer warehouseId,
			Integer itemCategoryId, String stockCode, List<String> stockCodes, boolean isShowAll,
			Boolean isMixing, Boolean isExcludeFG);

	/**
	 * Get the list of items using its unique stock code and id of the company.
	 * @param stockCode The stock code.
	 * @param companyId The id of the company.
	 * @return The list of items.
	 */
	List<Item> getRetailItems(String stockCode, Integer companyId);

	/**
	 * Checks if item description has duplicate.
	 * @param item The item.
	 * @return True if a duplicate exists, otherwise false.
	 */
	boolean hasDuplicateDescription(Item item);

	/**
	 * Get the received stocks of the retail item.
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param pageSetting The page setting.
	 * @return Get all the received stocks in paged format.
	 */
	Page<ReceivedStock> getItemReceivedStocks(int itemId, int warehouseId, PageSetting pageSetting);

	/**
	 * Get the received stocks of the retail item per warehouse as of date.
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param asOfDate Get the received stocks as of the date.
	 * @param pageSetting The page setting.
	 * @return The received stocks.
	 */
	Page<ReceivedStock> getItemReceivedStocksAsOf(int itemId, int warehouseId, Date asOfDate, PageSetting pageSetting);

	/**
	 * Get the received stocks of the retail item per warehouse as of date.
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param startDate Get the received stocks from this date.
	 * @param pageSetting The page setting.
	 * @return The received stocks.
	 */
	Page<ReceivedStock> getItemReceivedStocksAfterDate(int itemId, int warehouseId, Date startDate, PageSetting pageSetting);

	Page<ReceivedStock> getItemReceivedStocks(int itemId, int warehouseId,
			Date startDate, Date endDate, PageSetting pageSetting);

	/**
	 * Compute the existing stocks of the item per warehouse.
	 * @param itemId The id of the Item.
	 * @param warehouseId The id of the warehouse.
	 * @param asOfDate The end date of the date range.
	 * @return The existing stocks of the item.
	 */
	double getItemExistingStocks(int itemId, int warehouseId, Date asOfDate);

	/**
	 * Get the data for the inventory listing report using the Unused Stocks of the item.
	 * @param itemCategoryId The id of the item category, -1 = ALL.
	 * @param companyId The id of the company.
	 * @param warehouseId The id of the warehouse.
	 * @param stockOptionId The stock option, -1 = ALL, 0 = No zero.
	 * @param asOfDate The as of date.
	 * @param statusId The id of the status, {-1 = ALL, 1 = ACTIVE, 0 = INACTIVE}
	 * @param pageSetting The page setting.
	 * @return The data to be used to generate the inventory listing report.
	 */
	Page<PhysicalInventory> generateInvListingFromUnusedStocks(Integer itemCategoryId, int companyId,
			int warehouseId, int stockOptionId, Date asOfDate, int statusId, PageSetting pageSetting);

	/**
	 * Get the data for the inventory listing report using the V_ITEM_HISTORY view.
	 * @param itemCategoryId The id of the item category, -1 = ALL.
	 * @param companyId The id of the company.
	 * @param warehouseId The id of the warehouse.
	 * @param stockOptionId The stock option, -1 = ALL, 0 = No zero.
	 * @param asOfDate The as of date.
	 * @param statusId The id of the status, {-1 = ALL, 1 = ACTIVE, 0 = INACTIVE}
	 * @param orderBy Sets the order of the report. {1 stock code, 2 description}
	 * @param workflowStatusId The status of the workflow {-1 = ALL, 1 = COMPLETE}
	 * @param pageSetting The page setting.
	 * @return The data to be used to generate the inventory listing report.
	 */
	Page<PhysicalInventory> generateInventoryListingData(Integer divisionId, Integer itemCategoryId, int companyId,
			int warehouseId, int stockOptionId, Date asOfDate, int statusId, int workflowStatusId, int orderBy, PageSetting pageSetting);

	/**
	 * Get the item's transactions for the stockcard per item report.
	 * @param itemId The id of the item.
	 * @param companyId The id of the company.
	 * @param divisionId The id of the division.
	 * @param warehouseId The id of the warehouse, 0 = ALL.
	 * @param dateFrom The start date of the date range.
	 * @param dateTo The end date of the date range.
	 * @param pageSetting The page setting.
	 * @return The item's transactions.
	 */
	Page<StockcardDto> getStockcardPerItem(int itemId, int companyId, Integer divisionId, int warehouseId,
			Date dateFrom, Date dateTo, PageSetting pageSetting);

	/**
	 * Get the list of unused stocks for the Beginning Balance of Stockcard/Bincard report.
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param typeId The type of the report. {1=Stockcard, 2=Bincard}
	 * @param asOfDate As of date.
	 * @return The paged result.
	 */
	List<StockcardDto> getStockcardBeginningBal(int companyId, int itemId, int warehouseId, int typeId, Date asOfDate);

	/**
	 * Get the IN/OUT transactions of the item.
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param date The form date.
	 * @param pageSetting The page setting.
	 * @return The paged result of the item's transactions.
	 */
	Page<ItemTransaction> getItemTransaction(int itemId, int warehouseId, Date date, PageSetting pageSetting);

	/**
	 * Get all of the re-packed items.
	 * @param pageSetting the page settings.
	 */
	Page<Item> getRepackedItems (PageSetting pageSetting);

	/**
	 * Get the list of gross profit analysis report.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param itemCategoryId The item category id.
	 * @param dateFrom The start date.
	 * @param dateTo The end date.
	 * @return Gross profit analysis.
	 */
	List<GrossProfitAnalysis> getGrossProfitAnalysis (Integer companyId,
			Integer divisionId, Integer itemCategoryId, Date dateFrom, Date dateTo);

	/**
	 * Get the daily item sales report.
	 * @param companyId The company id.
	 * @param stockCode The stock code or description of the item.
	 * @param dateFrom The date from.
	 * @param dateTo The date to.
	 * @param itemCategoryId The item category id.
	 * @return The list of daily item sales.
	 */
	List<DailyItemSale> getDailyItemSales (Integer companyId, String stockCode, Date dateFrom,
			Date dateTo, Integer itemCategoryId, Integer warehouseId);
	
	/**
	 * Get the daily item sale details.
	 * @param companyId The company id.
	 * @param stockCode The stock code or description of the item.
	 * @param invoiceNo The invoice number.
	 * @param dateFrom The date from.
	 * @param dateTo The date to.
	 * @param itemCategoryId The item category id.
	 * @return The list of daily item sale details.
	 */
	List<DailyItemSaleDetail> getDailyItemSaleDetails (Integer companyId, String stockCode,
			String invoiceNo, Date dateFrom, Date dateTo, Integer itemCategoryId, Integer warehouseId);
	
	/**
	 * Get the item sales report.
	 * @param companyId The company id.
	 * @param customerId The customer id.
	 * @param customerAccountId The customer account id.
	 * @param itemCategoryId The item category id.
	 * @param itemId The item id.
	 * @param dateFrom The begginning date.
	 * @param dateTo The end date.
	 * @param pageSetting The page setting.
	 * @return The paged list of item sales by customer report.
	 */
	Page<ItemSalesCustomer> getItemSalesCustomer (int companyId, int divisionId,  int customerId, int customerAccountId, int itemCategoryId,
			int itemId, Date dateFrom, Date dateTo, PageSetting pageSetting);
	
	/**
	 * Get retail item by stock code whether active or not.
	 * @param stockCode The stock code of the item.
	 * @param companyId The company id
	 * @param warehouseId The warehouse id
	 * @param isActiveOnly True to get only active item, otherwise false.
	 * @return The retail item.
	 */
	Item getRetailItem (String stockCode, Integer companyId, Integer warehouseId, boolean isActiveOnly, Integer divisionId, Boolean isExcludeFG);

	/**
	 * Get the details of the retail item (Selling and buying prices, discounts, add ons).
	 * @param itemId The unique id of the item.
	 * @param isActiveOnly Set to true to retrieve active details only.
	 * @return The details of the retail item.
	 */
	List<RItemDetail> getRItemDetails(int itemId, boolean isActiveOnly);

	/**
	 * Get the data to be used in Reordering Point Report.
	 * @param companyId The id of the company.
	 * @param divisionId The id of the division.
	 * @param warehouses The list of warehouses under the company.
	 * @param categoryId The id of the category.
	 * @param statusId The status of the item. {All, Active, Inactive}
	 * @param asOfDate As of date.
	 * @param description The item description. 
	 * @param pageSetting The page setting.
	 * @return The list of data in paging format.
	 */
	Page<ReorderPointDto> getReorderingPointData(Integer companyId, Integer divisionId,Integer  warehouseId,
			Integer statusId, Integer categoryId, Date asOfDate, String orderBy, Integer stockOptionId, boolean isExcludePO,
			String description, PageSetting pageSetting);

	/**
	 * Get the list of buying items.
	 * @param companyId The id of the company.
	 * @param stockCode The stock code of the item.
	 * @return The list of buying items.
	 */
	List<Item> getBuyingItems(Integer companyId, String stockCode);

	/**
	 * Get the retail item configured for buying.
	 * @param companyId The id of the company.
	 * @param warehouseId The id of the wareouse.
	 * @param stockCode The stock code or description of the item.
	 * @return The retail item for buying.
	 */
	Item getBuyingItem(Integer companyId, Integer warehouseId, String stockCode);

	/**
	 * Get the available stocks.
	 * @param stockCode The item stock code.
	 * @param warehouseId The warehouse filter.
	 * @param companyId The if of the company the item was configured.
	 * @return The list of available stocks.
	 */
	List<AvailableStock> getAvailableStocks (String stockCode, Integer warehouseId, Integer companyId);

	/**
	 * Get the available stocks per batch.
	 * @param stockCode The stock code of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param refObjectId The id of the reference object id.
	 * @return The available stocks of the reference object id.
	 */
	AvailableStock getAvailStocksByReference(String stockCode, Integer warehouseId, Integer refObjectId);

	/**
	 * Compute the total of the available stocks.
	 * @param stockCode The stock code of the item.
	 * @param warehouseId The id of the warehouse.
	 * @return The total available stocks of the item.
	 */
	double getTotalAvailStocks(String stockCode, Integer warehouseId);

	/**
	 * Get the list of items with available stocks.
	 * @param stockCode The stock code of the item.
	 * @param warehouseId The id of the warehouse.
	 * @return The list of {@link AvailableStock}
	 */
	List<AvailableStock> getItemsWithAvailStocks(String stockCode, Integer warehouseId);

	/**
	 * Get the data to be used in Available Stocks Report.
	 * @param warehouseId The id of the warehouse.
	 * @param itemCategoryId The id of the category the item is under.
	 * @param stockCode The stock code of the item.
	 * @param orderBy The ordering of report, can be ordered by Stock Code or Description of the item.
	 * @param asOfDate The as of date.
	 * @return The paged data for Available Stocks Report.
	 */
	Page<AvailableStock> getAvailableStocksRptData(Integer warehouseId, Integer itemCategoryId,
			String stockCode, String orderBy, Date asOfDate, PageSetting pageSetting);

	/**
	 * Get Item sold by customer
	 * @param companyId The company id. 
	 * @param customerId The customer id
	 * @param customerAccountId the customer account id
	 * @param itemCategoryId item category id
	 * @param itemId item category id.
	 * @param dateFrom start date
	 * @param dateTo end date
	 * @param pageSetting the page setting. 
	 */
	Page<ItemSalesCustomer> getItemSoldByCustomer (int companyId, int divisionId, int customerId, int customerAccountId,
			int itemCategoryId, int itemId, Date dateFrom, Date dateTo, boolean isExludeReturns,
			Integer warehouseId, PageSetting pageSetting);

	/**
	 * Get the list of items for Menu (Items that are flagged as Menu in {@link ProductLine}).
	 * @param stockCode The stock code of the item.
	 * @param companyId The id of the company.
	 * @param isExact True if stock code is exact for search criteria, otherwise false
	 * @param isActiveOnly True if retrieve active items only, otherwise false
	 * @return The list of items for Menu.
	 */
	List<Item> getMainProducts(String stockCode, int companyId, Boolean isExact, Boolean isActiveOnly);

	/**
	 * Get the item by description.
	 * @param description The description filter.
	 * @return The item.
	 */
	Item getByDescription(String description);

	/**
	 * Get the list of forms by the reference object id
	 * @param refObjectId The reference eb object id
	 * @return The list of forms by the reference object id
	 */
	List<AvailableStock> getFormsByRefObjectId(Integer refObjectId);
	
	/**
	 * Get the available stocks as of date. 
	 * @param itemId the item id.
	 * @param companyId the company id.
	 * @param warehouseId the warehouse id.
	 * @param date as of date.
	 */
	ReceivedStock getAvailableStocks (int itemId, int companyId, int warehouseId, Date date);

	/**
	 * Get received stocks that are dated in the future.
	 * Weighted average
	 * @param itemId the item id
	 * @param companyId the company id
	 * @param warehouseId the warehouse id
	 * @param date the current date.
	 * @return the received stocks that are dated in the future.
	 */
	ReceivedStock getReceivedStocksFuture (int itemId, int companyId, int warehouseId, Date date);
	
	/**
	 * Get the future withdrawal transaction of the item. 
	 * @param companyId the company id.
	 * @param warehouseId the warehouse id.
	 * @param itemId the item id. 
	 * @param date the current date. 
	 * @param pageSetting the page setting
	 * @return the item transaction history. 
	 */
	Page<ItemTransactionHistory> getFutureWithdrawalTransactions (int companyId, int warehouseId, 
			int itemId, Date date, PageSetting pageSetting);

	/**
	 * Compute the existing stocks of the item per company and warehouse.
	 * @param itemId The id of the Item.
	 * @param companyId The company id.
	 * @param warehouseId The id of the warehouse.
	 * @param asOfDate The end date of the date range.
	 * @return The existing stocks of the item.
	 */
	double getItemExistingStocks(int itemId, int warehouseId, Date asOfDate, int companyId);

	/**
	 * Get the item latest unit cost
	 * @param companyId The company id
	 * @param warehouseId The warehouse id
	 * @param itemId The item id
	 * @return The item latest unit cost
	 */
	double getLatestItemUnitCost(int companyId, int warehouseId, int itemId);

	/**
	 * Get {@code Item} by its barcode.
	 * @param barcode The item barcode.
	 * @return  The Item object.
	 */
	Item getByBarcode(String barcode);
}