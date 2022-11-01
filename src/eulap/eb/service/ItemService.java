package eulap.eb.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ItemAddOnDao;
import eulap.eb.dao.ItemCategoryDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ItemDiscountDao;
import eulap.eb.dao.ItemSrpDao;
import eulap.eb.dao.ItemWeightedAveDao;
import eulap.eb.domain.hibernate.CurrencyRate;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemAddOn;
import eulap.eb.domain.hibernate.ItemAddOnType;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.ItemDiscount;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.ItemWeightedAverage;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.inventory.ReceivedStock;
import eulap.eb.web.dto.DailyItemSale;
import eulap.eb.web.dto.DailyItemSaleDetail;
import eulap.eb.web.dto.GrossProfitAnalysis;
import eulap.eb.web.dto.ItemTransactionHistory;
import eulap.eb.web.dto.StockcardDto;
import eulap.eb.web.processing.dto.AvailableStock;

/**
 * Item service.

 *
 */
@Service
public class ItemService {
	private static double ZERO = 0.0;
	private static Logger LOGGER = Logger.getLogger(ItemService.class);
	@Autowired
	private ItemCategoryDao itemCategoryDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemSrpDao itemSrpDao;
	@Autowired
	private ItemDiscountDao itemDiscountDao;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private ItemAddOnDao itemAddOnDao;
	@Autowired
	private ItemSrpService itemSrpService;
	@Autowired
	private ItemAddOnService itemAddOnService;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ItemWeightedAveDao itemWeightedAveDao;
	@Autowired
	private CurrencyRateService currencyRateService;

	/**
	 * Get the list of items in paging format.
	 * @param isActive Set to true if retrieve only active items, otherwise false.
	 * @param pageSetting The page setting.
	 * @return The list of items.
	 */
	public Page<Item> getAllItems(boolean isActive, PageSetting pageSetting) {
		return itemDao.getAllItems(isActive, pageSetting);
	}

	/**
	 * Get the list of item categories.
	 * @return The list of item categories.
	 */
	public List<ItemCategory> getItemCategories () {
		return itemCategoryDao.getItemCategories();
	}

	/**
	 * Get the retail item details (selling prices, discounts and add ons)
	 * @param item The retail item.
	 */
	public void getItemDetails(Item item) {
		int itemId = item.getId();
		List<ItemSrp> itemSrps = itemSrpService.getItemSrpsByItem(itemId);
		for (ItemSrp is : itemSrps) {
			if (is.getDivisionId() != null) {
				is.setDivisionName(divisionService.getDivision(is.getDivisionId()).getName());
			}
		}
		item.setItemSrps(itemSrps);
		item.setItemDiscounts(itemDiscountService.getItemDiscountsByItem(itemId, false));
		item.setItemAddOns(itemAddOnService.getItemAddOnsByItem(itemId, false));
	}

	/**
	 * Get and process the details of the retail item.
	 * To be processed details are selling prices, discounts and add ons.
	 * @param item The retail item.
	 */
	public void processItemDetails(Item item) {
		item.setItemSrps(itemSrpService.processItemSrps(item.getItemSrps()));
		item.setItemDiscounts(itemDiscountService.processItemDiscounts(item.getItemDiscounts()));
		item.setItemAddOns(itemAddOnService.processItemAddOns(item.getItemAddOns()));
	}

	/**
	 * Get the item by its id.
	 * @param itemId The item id.
	 * @return Item
	 */
	public Item getItem (int itemId) {
		return itemDao.get(itemId);
	}

	/**
	 * Get the Item by using the unique stock code.
	 * @param stockCode The stock code of the item.
	 * @return The {@link Item}
	 */
	public Item getItemByStockCode(String stockCode) {
		return itemDao.getItemByStockCode(stockCode, null);
	}

	/**
	 * Get the Retail - Item object.
	 * @param stockCode The stock code of the item.
	 * @param companyId The company id.
	 * @param warehouseId The warehouse id.
	 * @return The retail item object, otherwise null;
	 */
	public Item getRetailItem(String stockCode, Integer companyId, Integer warehouseId) {
		return getRetailItem(stockCode, companyId, null, warehouseId, null, false);
	}

	/**
	 * Get the Item object using the stock code.
	 * @param stockCode The stock code.
	 * @param warehouseId The id of the warehouse.
	 * @param icId The id of the category of the item.
	 * @return The Item object, otherwise null.
	 */
	public Item getRetailItem(String stockCode, Integer companyId, Integer divisionId, Integer warehouseId, Integer icId, Boolean isExcludeFG) {
		Item retailItem = itemDao.getRetailItem(stockCode.trim(), warehouseId, icId, companyId, divisionId, isExcludeFG);
		if (retailItem != null) {
			retailItem.setExistingStocks(itemDao.getItemExistingStocks(retailItem.getId(),
					warehouseId == null ? -1 : warehouseId, new Date(), companyId == null ? -1 : companyId));
		}
		return retailItem;
	}

	/**
	 * Get the Item object using the id.
	 * @param itemId The item id.
	 * @param companyId The id of the company.
	 * @return The Item object, otherwise null.
	 */
	public Item getRetailItem(Integer itemId, Integer companyId) {
		return itemDao.getRetailItem(itemId, companyId);
	}

	/**
	 * Get the paged list of retail items.
	 * @param stockCode The stock code.
	 * @param description The description.
	 * @param unitMeasurementId The unit of measure.
	 * @param itemCategoryId The item category.
	 * @param status The status of the item.
	 * @param isOrderByCategory True if item category is the primary ordering, otherwise false.
	 * @param pageNumber The page number.
	 * @return The paged list of retail items.
	 */
	public Page<Item> getRetailItems (String stockCode, String description, Integer unitMeasurementId,
			Integer itemCategoryId, int status, boolean isOrderByCategory, int pageNumber) {
		return getRetailItems(stockCode, description, unitMeasurementId,
				itemCategoryId, status, isOrderByCategory, pageNumber, 25);
	}

	public Page<Item> getRetailItems (String stockCode, String description, Integer unitMeasurementId,
			Integer itemCategoryId, int status, boolean isOrderByCategory, int pageNumber, int itemPerPage) {
		Page<Item> items = itemDao.getRetailItems(stockCode.trim(), description.trim(), unitMeasurementId,
				itemCategoryId,  status, isOrderByCategory, new PageSetting(pageNumber, itemPerPage));
		List<ReceivedStock> unusedStocks = null;
		for (Item item : items.getData()) {
			int itemId = item.getId();
			List<ItemSrp> srps = null;
			srps = itemSrpDao.getItemSrpsByItem(itemId);
			for (ItemSrp itemSrp : srps) {
				if(itemSrp.isActive()) {
					//Set the weighted average.
					unusedStocks = getItemUnusedByCompany(itemSrp.getItemId(),
							itemSrp.getCompanyId(), new Date());
					itemSrp.setItemUnitCost(getWeightedAverage(unusedStocks));
				}
			}
			// Set the srps.
			item.setItemSrps(srps);
			// Set the discounts.
			List<ItemDiscount> discounts = itemDiscountDao.getItemDiscountsByItem(itemId, true);
			item.setItemDiscounts(discounts);
			// Set the add ons.
			List<ItemAddOn> addOns = itemAddOnDao.getItemAddOnsByItem(itemId, true);
			item.setItemAddOns(addOns);
		}
		return items;
	}

	/**
	 * Save the user object in the database.
	 * @param item The item to be saved.
	 * @param user The logged user.
	 */
	public void saveItem (Item item, User user) {
		saveItem(item, user.getId());
	}

	private Item setItemBeforeSaving (Item item, int userId) {
		if(item.getInitialQuantity() == null)
			item.setInitialQuantity(ZERO);
		if(item.getInitialUnitCost() == null)
			item.setInitialUnitCost(ZERO);
		if(item.getReorderingPoint() == null)
			item.setReorderingPoint(0);
		item.setStockCode(item.getStockCode().trim());
		item.setDescription(item.getDescription().trim());
		return item;
	}

	/**
	 * Save the item object in the database.
	 * @param item The item to be saved.
	 * @param userId The id of the logged user.
	 */
	public void saveItem(Item item, int userId) {
		boolean isNewRecord = item.getId() == 0;
		AuditUtil.addAudit(item, new Audit(userId, isNewRecord, new Date ()));
		if(item.getInitialQuantity() == null)
			item.setInitialQuantity(ZERO);
		if(item.getInitialUnitCost() == null)
			item.setInitialUnitCost(ZERO);
		if(item.getReorderingPoint() == null)
			item.setReorderingPoint(0);
		item.setStockCode(item.getStockCode().trim());
		item.setDescription(item.getDescription().trim());
		itemDao.saveOrUpdate(item);
	}

	/**
	 * Save the item object in the database.
	 * @param item The item to be saved.
	 * @param userId The id of the logged user.
	 */
	public void saveRItem(Item item, int userId) {
		LOGGER.info("Saving the Retail Item.");
		boolean isNewRecord = item.getId() == 0;
		AuditUtil.addAudit(item, new Audit (userId, isNewRecord, new Date ()));
		item = setItemBeforeSaving(item, userId);
		itemDao.saveOrUpdate(item);
		int itemId = item.getId();
		List<ItemSrp> itemSrps = item.getItemSrps();
		LOGGER.info("Processing the selling price of the item.");
		if (itemSrps != null) {
			List<ItemSrp> itemSrps2 = itemSrpDao.getItemSrpsByItem(itemId);
			String itemSrpIDsCombi = "";
			Map<String, String> itemSrpHM = new HashMap<String, String>();
			for (ItemSrp itemSrp : itemSrps) {
				itemSrp.setItemId(item.getId());
				itemSrp.setActive(true);
				itemSrpIDsCombi = "c"+itemSrp.getCompanyId()+"i"+itemId+"d"+itemSrp.getDivisionId();
				ItemSrp oldItemSrp = itemSrpDao.getLatestItemSrp(itemSrp.getCompanyId(),
						itemId, itemSrp.getDivisionId());
				itemSrpHM.put(itemSrpIDsCombi, itemSrpIDsCombi);
				if (oldItemSrp == null) {
					itemSrpDao.save(itemSrp);
				} else {
					if (!oldItemSrp.getSrp().equals(itemSrp.getSrp())) {
						double srp = itemSrp.getSrp();
						itemSrp.setSrp(oldItemSrp.getSrp());
						itemSrp.setActive(false);
						LOGGER.info("Deactivating the old Selling price "
								+ "of item: "+item.getStockCodeAndDesc());
						// Create new Item SRP object.
						ItemSrp newItemSrp = ItemSrp.getInstanceOf(itemId,
								itemSrp.getCompanyId(), srp, true, itemSrp.getDivisionId());
						itemSrpDao.save(newItemSrp);
						LOGGER.info("Successfully saved the new Selling price of the item.");
					}
					itemSrpDao.update(itemSrp);
				}
			}
			for (ItemSrp itemSrp : itemSrps2) {
				itemSrpIDsCombi = "c"+itemSrp.getCompanyId()+"i"+itemId+"d"+itemSrp.getDivisionId();
				if(itemSrpHM.get(itemSrpIDsCombi) == null) {
					itemSrp.setActive(false);
					itemSrpDao.update(itemSrp);
				}
			}
		}

		LOGGER.debug("Processing the discounts of the item: "+item.getStockCodeAndDesc());
		ItemDiscountUtil<ItemDiscount> discountUtil = new ItemDiscountUtil<ItemDiscount>();
		discountUtil.saveItemDiscounts(item.getItemDiscounts(), itemDiscountDao, itemId);

		LOGGER.debug("Processing the add ons of the item: "+item.getStockCodeAndDesc());
		List<ItemAddOn> addOns = item.getItemAddOns();
		for (ItemAddOn iao : addOns) {
			// If null, set to the default type, QUANTITY
			if(iao.getItemAddOnTypeId() == null) {
				iao.setItemAddOnTypeId(ItemAddOnType.TYPE_QUANTITY);
			}
		}
		ItemAddOnUtil<ItemAddOn> addOnUtil = new ItemAddOnUtil<ItemAddOn>();
		addOnUtil.saveAddOns(addOns, itemAddOnDao, item.getId());

		LOGGER.info("Saved the Discounts and Add ons of the item.");
	}

	/**
	 * Checks if the stock code is unique.
	 * @param stockCode The item stock code.
	 * @param itemId The item id.
	 * @return True if unique, otherwise false.
	 */
	public boolean isUniqueStockcode (String stockCode, int itemId) {
		return itemDao.isUniqueStockCode(stockCode, itemId);
	}

	/**
	 * Get the list of active item category.
	 * @return The list of item category.
	 */
	public List<ItemCategory> getAllActiveItemCategory(){
		return itemCategoryDao.getActiveItemCategories();
	}

	/**
	 * Get the list of items.
	 * @param stockCode The stock code of the item.
	 * @return The list of items.
	 */
	public List<Item> getItemsByCodeAndDesc(String stockCode) {
		return getRetailItems(null, null, null, null, stockCode, null, false);
	}

	/**
	 * Get the list of items.
	 * @param companyId The company id.
	 * @param stockCode The stock code of the item.
	 * @param stockCodes The stock codes to be excluded.
	 * @param isMixing Set to true if the item is the finished product from Product Line setup.
	 * @return The list of items.
	 */
	public List<Item> getItemsByCodeAndDesc(Integer companyId, Integer divisionId, Integer warehouseId,
			Integer itemCategoryId, String stockCode, String stockCodes, Boolean isMixing, Boolean isExcludeFG) {
		List<String> lsStockCodes = null;
		if (stockCodes != null && !stockCodes.trim().isEmpty()) {
			lsStockCodes = new ArrayList<String>();
			String strStockCodes[] = stockCodes.split(";");
			for (String str : strStockCodes) {
				if (!str.trim().isEmpty()) {
					lsStockCodes.add(str);
				}
			}
		}
		return getRetailItems(companyId, divisionId, warehouseId, itemCategoryId,
				stockCode, lsStockCodes, false, isMixing, isExcludeFG);
	}

	/**
	 * Get the list of items.
	 * @param companyId The company id.
	 * @param stockCode The stock code of the item.
	 * @param stockCodes The stock codes to be excluded.
	 * @return The list of items.
	 */
	public List<Item> getItemsByCodeAndDesc(Integer companyId, Integer divisionId, Integer warehouseId,
			Integer itemCategoryId, String stockCode, String stockCodes, Boolean isExcludeFG) {
		return getItemsByCodeAndDesc(companyId, divisionId, warehouseId, itemCategoryId, stockCode, stockCodes, null, isExcludeFG);
	}

	/**
	 * Get the list of retail items.
	 * @param isShowAll Set to true to show all items, otherwise displays at most 10 items.
	 * @param isMixing Set to true if the item is the finished product from Product Line setup.
	 * @return The list of retail items.
	 */
	public List<Item> getRetailItems(Integer companyId, Integer divisionId, Integer warehouseId,
			Integer itemCategoryId, String stockCode, List<String> lsStockCodes, boolean isShowAll, Boolean isMixing, Boolean isExcludeFG) {
		return itemDao.getRetailItems(companyId, divisionId, warehouseId, itemCategoryId, stockCode, lsStockCodes, isShowAll, isMixing, isExcludeFG);
	}

	/**
	 * Get the list of retail items.
	 * @param isShowAll Set to true to show all items, otherwise displays at most 10 items.
	 * @return The list of retail items.
	 */
	public List<Item> getRetailItems(Integer companyId, Integer divisionId, Integer warehouseId,
			Integer itemCategoryId, String stockCode, List<String> lsStockCodes, boolean isShowAll) {
		return getRetailItems(companyId, divisionId, warehouseId, itemCategoryId, stockCode, lsStockCodes, isShowAll, null, null);
	}

	/**
	 * Get the list of retail items.
	 * @param stockCode The stock code.
	 * @param companyId The id of the company.
	 * @return The list of retail items.
	 */
	public List<Item> getRetailItems(String stockCode, Integer companyId) {
		return itemDao.getRetailItems(stockCode, companyId);
	}

	private void collectUnusedStocks (int itemId, double existingStocks, List<ReceivedStock> unUsedStocks,
			Page<ReceivedStock> receivedStocks, Integer warehouseId) {
		for (ReceivedStock rs : receivedStocks.getData()) {
			double quantity = rs.getQuantity();
			if (quantity >= existingStocks) {
				unUsedStocks.add(new ReceivedStock(rs.getDate(), rs.getItemId(), existingStocks,
						rs.getUnitCost(), rs.getInventoryCost(), rs.getForm(), rs.getFormId()));
				existingStocks = existingStocks - quantity;
				break;
			}
			existingStocks = existingStocks - quantity;
			unUsedStocks.add(new ReceivedStock(rs.getDate(), rs.getItemId(), quantity,
					rs.getUnitCost(), rs.getInventoryCost(), rs.getForm(), rs.getFormId()));
		}
		if (existingStocks > 0) {
			PageSetting ps = new PageSetting(receivedStocks.getNextPage());
			Page<ReceivedStock> newPageReceivedStocks = getItemReceivedStocks(itemId, warehouseId, ps);
			if (newPageReceivedStocks.getData().isEmpty())
				return;
			collectUnusedStocks(itemId, existingStocks, unUsedStocks, newPageReceivedStocks, warehouseId);
		}
	}

	/**
	 * Checks if item description has duplicate.
	 * @param item The item object.
	 * @return True if a duplicate exists, otherwise false.
	 */
	public boolean hasDuplicateDescription (Item item) {
		return itemDao.hasDuplicateDescription(item);
	}

	/**
	 * Get the received stocks by warehouse as of today.
	 */
	public Page<ReceivedStock> getItemReceivedStocks(int itemId, int warehouseId, PageSetting pageSetting) {
		return itemDao.getItemReceivedStocks(itemId, warehouseId, pageSetting);
	}

	/**
	 * Get the received stocks by warehouse as of date.
	 */
	public Page<ReceivedStock> getItemReceivedStocksAsOf(int itemId, int warehouseId, Date asOfDate, PageSetting pageSetting) {
		return itemDao.getItemReceivedStocksAsOf(itemId, warehouseId, asOfDate, pageSetting);
	}

	/**
	 * Compute the existing stocks of the item as of date.
	 */
	public double getItemExistingStocks(int itemId, int warehouseId, Date asOfDate) {
		return itemDao.getItemExistingStocks(itemId, warehouseId, asOfDate);
	}

	/**
	 * Compute the existing stocks of the item as of today.
	 */
	public double getItemExistingStocks(int itemId, int warehouseId) {
		return itemDao.getItemExistingStocks(itemId, warehouseId, new Date());
	}

	private List<ReceivedStock> getItemUnusedStocks (int itemId, Integer warehouseId,
			Integer companyId, double existingStocks, Date asOfDate) {
		List<ReceivedStock> unUsedStocks = new ArrayList<ReceivedStock>();
		if (existingStocks < 0) {
			//Returns an empty Received Stock if the existing stocks is negative
			ReceivedStock rs = new ReceivedStock(asOfDate, itemId, existingStocks, ZERO, ZERO, null, 0);
			unUsedStocks.add(rs);
			return unUsedStocks;
		}

		PageSetting pageSetting = new PageSetting(1);
		Page<ReceivedStock> receivedStocks = null;
		if(warehouseId == null) {
			List<Warehouse> warehouses = warehouseService.getWarehouseList(companyId);
			for (Warehouse wh : warehouses) {
				receivedStocks = getItemReceivedStocksAsOf(itemId, wh.getId(), asOfDate, pageSetting);
				collectUnusedStocks(itemId, existingStocks, unUsedStocks, receivedStocks, wh.getId());
			}
		} else {
			receivedStocks = getItemReceivedStocksAsOf(itemId, warehouseId, asOfDate, pageSetting);
			collectUnusedStocks(itemId, existingStocks, unUsedStocks, receivedStocks, warehouseId);
		}
		Collections.reverse(unUsedStocks);
		return unUsedStocks;
	}

	/**
	 * Get the unused stocks per company's warehouse.
	 */
	public List<ReceivedStock> getItemUnusedStocks(int itemId, int warehouseId, Date asOfDate) {
		return getItemUnusedStocks(itemId, warehouseId, null, getItemExistingStocks(itemId, warehouseId, asOfDate), asOfDate);
	}

	/**
	 * Get the unused stocks of an item by company.
	 * @param itemId The id of the item.
	 * @param companyId The id of the company.
	 * @param asOfDate Get the unused stocks until this date.
	 * @return The list of unused stocks.
	 */
	public List<ReceivedStock> getItemUnusedByCompany(int itemId, int companyId, Date asOfDate) {
		List<ReceivedStock> unusedStocks = new ArrayList<ReceivedStock>();
		Double existingStocks = 0.0;
		List<Warehouse> warehouses = warehouseService.getWarehouseList(companyId);
		for (Warehouse wh : warehouses) {
			existingStocks = getItemExistingStocks(itemId, wh.getId(), asOfDate);
			unusedStocks.addAll(getItemUnusedStocks(itemId, wh.getId(), null, existingStocks, asOfDate));
		}
		return unusedStocks;
	}

	/**
	 * Compute the weighted average of unused stocks of the item.
	 * @param unusedStocks The list of unused stocks of the item.
	 * @return The weighted average.
	 */
	public double getWeightedAverage(List<ReceivedStock> unusedStocks) {
		double totalAmt = 0;
		double totalQty = 0;
		for (ReceivedStock receivedStock : unusedStocks) {
			totalQty += receivedStock.getQuantity();
			totalAmt += (NumberFormatUtil.multiplyWFP(receivedStock.getUnitCost(), receivedStock.getQuantity()));
		}

		if(totalAmt == 0 && totalQty == 0)
			return 0;
		double weightedAve = NumberFormatUtil.divideWFP(totalAmt, totalQty);
		return NumberFormatUtil.roundOffTo2DecPlaces(weightedAve);
	}

	/**
	 * Get the all of the re-packed items.
	 */
	public Page<Item> getRepackedItems () {
		PageSetting ps = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		return itemDao.getRepackedItems(ps);
	}

	/**
	 * Get the list of gross profit analysis report.
	 * @param companyId The company id.
	 * @param itemCategoryId The item category id.
	 * @param dateFrom The start date.
	 * @param dateTo The end date.
	 * @return Gross profit analysis.
	 */
	public List<GrossProfitAnalysis> getGrossProfitAnalysis(Integer companyId, Integer divisionId,
	Integer itemCategoryId, Date dateFrom, Date dateTo) {
		return itemDao.getGrossProfitAnalysis(companyId, divisionId, itemCategoryId, dateFrom, dateTo);
	}

	/**
	 * Get the list of daily item sales.
	 * @param companyId The company id.
	 * @param stockCode The stock code of the item.
	 * @param invoiceNo The invoice number of the sale.
	 * @param date The date.
	 * @param itemCategoryId the item category id.
	 * @return The list of daily item sales.
	 */
	public List<DailyItemSale> getDailyItemSales (Integer companyId, String stockCode, String invoiceNo, Date dateFrom,
			Date dateTo, Integer itemCategoryId, Integer warehouseId) {
		List<DailyItemSale> dailyItemSales = itemDao.getDailyItemSales(companyId, stockCode, dateFrom, dateTo, itemCategoryId, warehouseId);
		List<DailyItemSale> ret = new ArrayList<DailyItemSale>();
		if (dailyItemSales != null && !dailyItemSales.isEmpty()) {
			for (DailyItemSale dis : dailyItemSales) {
				List<DailyItemSaleDetail> details = itemDao.getDailyItemSaleDetails(
						companyId, dis.getStockCode(), invoiceNo, dateFrom, dateTo, itemCategoryId, warehouseId);
				if (details != null && !details.isEmpty()) {
					dis.setDailyItemSaleDetails(details);
					ret.add(dis);
					details = null;
				}
			}
		}
		return ret;
	}

	/**
	 * Checks if the item belongs to the item category.
	 * @param itemId The item id.
	 * @param itemCategoryId The item category id.
	 * @return True if the item category id parameter is equal to the actual item category id
	 * of the item, otherwise false.
	 */
	public boolean belongsToCategory (int itemId, int itemCategoryId) {
		Item item = itemDao.get(itemId);
		if (item.getItemCategoryId().intValue() == itemCategoryId)
			return true;
		return false;
	}

	/**
	 * Get retail item by stock code whether active or not.
	 * @param stockCode The stock code of the item.
	 * @param companyId The company id
	 * @param warehouseId The warehouse id
	 * @param isActiveOnly True to get only active item, otherwise false.
	 * @return The retail item.
	 */
	public Item getRetailItem (String stockCode, Integer companyId, Integer warehouseId, boolean isActiveOnly, Integer divisionId, Boolean isExcludeFG) {
		return itemDao.getRetailItem(stockCode, companyId, warehouseId, isActiveOnly, divisionId, false);
	}

	/**
	 * Get the availables stocks.
	 * @param stockCode The item stock code.
	 * @param warehouseId The warehouse filter.
	 * @param companyId The id of the company where the item is configured.
	 * @return The list of available stocks.
	 */
	public List<AvailableStock> getAvailableStocks (String stockCode, Integer warehouseId, Integer companyId) {
		return itemDao.getAvailableStocks(stockCode, warehouseId, companyId);
	}

	/**
	 * Get the available stocks of the item using the reference object id.
	 * @param stockCode The stock code of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param refObjectId The id of the reference object.
	 * @return Null if available stocks are already exhausted for this batch, otherwise the {@link AvailableStock}
	 */
	public AvailableStock getAvailableStockByReference(String stockCode, Integer warehouseId, Integer refObjectId) {
		AvailableStock availableStock = itemDao.getAvailStocksByReference(stockCode, warehouseId, refObjectId);
		return availableStock;
	}

	/**
	 * Compute the total available stocks.
	 * @param stockCode The stock code.
	 * @param warehouseId The id of the warehouse.
	 * @return The total available stocks.
	 */
	public double getTotalAvailStocks(String stockCode, Integer warehouseId) {
		return itemDao.getTotalAvailStocks(stockCode, warehouseId);
	}

	public List<AvailableStock> getItemsWithAvailStocks(String stockCode, Integer warehouseId) {
		return itemDao.getItemsWithAvailStocks(stockCode, warehouseId);
	}

	/**
	 * From the list of available stocks, check if there is available stocks with the refObjectId parameter.
	 * <b>If reference is not in the list, add the Received stocks with zero quantity in the list.
	 */
	public List<AvailableStock> addRefObjectToAvailStocks(List<AvailableStock> availableStocks,
			Integer refObjectId, String stockCode, Integer warehouseId) {

		//If list is empty, add the reference stocks to the list.
		if(availableStocks.isEmpty()) {
			return getAndAddSelectedStock(availableStocks, refObjectId, stockCode, warehouseId);
		}

		List<Integer> objectIds = new ArrayList<>();
		for (AvailableStock as : availableStocks) {
			objectIds.add(as.getEbObjectId());
		}

		//Check the frequency of the reference object id in the list.
		int frequency = Collections.frequency(objectIds, refObjectId);
		if(frequency == 0) {
			//IF the reference object id is not in the list, add the reference in the available stocks.
			availableStocks = getAndAddSelectedStock(availableStocks, refObjectId, stockCode, warehouseId);
		}
		return availableStocks;
	}

	/**
	 * Retrieve the reference stocks and add it to the list.
	 */
	private List<AvailableStock> getAndAddSelectedStock(List<AvailableStock> availableStocks,
			Integer refObjectId, String stockCode, Integer warehouseId) {
		if(refObjectId > 0) {
			AvailableStock selectedStocks = getAvailableStockByReference(stockCode, warehouseId, refObjectId);
			if(selectedStocks != null) {
				selectedStocks.setQuantity(0.0);
				availableStocks.add(selectedStocks);
			}
			return availableStocks;
		}
		return Collections.emptyList();
	}

	/**
	 * Get the list of items for Menu (Items that are flagged as Menu in ProductLine).
	 * @param stockCode The stock code of the item.
	 * @param companyId The id of the company.
	 * @param isExact True if stock code is exact for search criteria, otherwise false
	 * @param isActiveOnly True if retrieve active items only, otherwise false
	 * @return The list of items.
	 */
	public List<Item> getMainProducts(String stockCode, Integer companyId, Boolean isExact, Boolean isActiveOnly) {
		return itemDao.getMainProducts(stockCode, (companyId != null ? companyId : 0), isExact, isActiveOnly);
	}

	/**
	 * Get the item by description.
	 * @param description The description filter.
	 * @return The item.
	 */
	public Item getByDescription(String description) {
		return itemDao.getByDescription(description);
	}

	/**
	 * Get the list of forms by the reference object id
	 * @param refObjectId The reference eb object id
	 * @return The list of forms by the reference object id
	 */
	public List<AvailableStock> getFormsByRefObjectId(Integer refObjectId) {
		return itemDao.getFormsByRefObjectId(refObjectId);
	}

	/**
	 * Get the weighted average of the item that are available for this date.
	 * @param itemId The item id.
	 * @param companyId the company id.
	 * @param warehouseId the warehouse id.
	 * @param date the current date.
	 */
	public ReceivedStock getItemAvailableStocksAsOfDate (int itemId, int companyId, int warehouseId, Date date) {
		ReceivedStock rsAsOfDate =  itemDao.getAvailableStocks(itemId, companyId, warehouseId, date);
		ReceivedStock rsFuture = itemDao.getReceivedStocksFuture(itemId, companyId, warehouseId, date);
		// Compute the weighted average.
		Double quantityAsofDate = rsAsOfDate.getQuantity();
		Double unitCostAsofDate = rsAsOfDate.getUnitCost();
		Double totalAsofDate = NumberFormatUtil.multiplyWFP(quantityAsofDate, unitCostAsofDate);

		Double quantityfuture = rsFuture.getQuantity();
		Double unitCostFuture = rsFuture.getUnitCost();
		Double totalFuture = NumberFormatUtil.multiplyWFP(quantityfuture, unitCostFuture);

		Double totalQauntity = quantityAsofDate + quantityfuture;
		Double totalCost = totalAsofDate + totalFuture;
		Double avarageCost = NumberFormatUtil.divideWFP(totalCost, totalQauntity);

		return new ReceivedStock (null, itemId, totalQauntity, avarageCost, avarageCost, null, -1);
	}

	/**
	 * Get the future withdrawal transactions
	 * @param companyId the company id
	 * @param warehouseId the warehouse id.
	 * @param itemId the item id.
	 * @param date the current date.
	 * @param pageSetting the page settings.
	 * @return the future withdrawal transactions.
	 */
	public Page<ItemTransactionHistory> getFutureWTHistory (int companyId, int warehouseId,
			int itemId, Date date, PageSetting pageSetting) {
		return itemDao.getFutureWithdrawalTransactions(companyId, warehouseId, itemId, date, pageSetting);
	}

	/**
	 * Get item beginning balance based on the current date
	 * @param companyId The company The company id
	 * @param warehouseId The warehouse id
	 * @param itemId The item id
	 * @return The item beginning balance based on the current date
	 */
	public List<StockcardDto> getStockcardBeginningBal(Integer companyId, Integer warehouseId, Integer itemId) {
		Date currentDate = DateUtil.removeTimeFromDate(new Date());
		return itemDao.getStockcardBeginningBal(companyId, itemId, warehouseId, 0, currentDate);
	}

	/**
	 * Check item if category is finished goods
	 * @param item The item
	 * @return Return if category is finished goods
	 */
	public boolean ifFinishedGoods(Item item) {
		boolean finished = false;
		boolean goods = false;
		if(item != null) {
			Integer icId = item.getItemCategoryId();
			if(icId != null) {
				ItemCategory ic = itemCategoryDao.get(icId);
				String name = ic.getName().toLowerCase();
				//finish;finished to anticipate grammar
				if(name.contains("finish")) {
					finished = true;
				}
				//good;goods to anticipate grammar
				if(name.contains("good")) {
					goods = true;
				}
			}
		}
		if(finished && goods) {
			return true;
		}
		return false;
	}

	/**
	 * Get the item weighted average
	 * @param warehouseId The warehouse id
	 * @param itemId The item id
	 * @return The item weighted average
	 */
	public ItemWeightedAverage getItemWeightedAverage(int warehouseId, int itemId) {
		return itemWeightedAveDao.getItemWeightedAverage(warehouseId, itemId);
	}

	/**
	 * Get converted weighted average
	 * @param companyId The company id
	 * @param warehouseId The warehouse id
	 * @param itemId The item id
	 * @param currencyId The currency id
	 * @return The converted weighted average
	 */
	public Double getWeightedAverageWithRate(Integer companyId, Integer warehouseId, Integer itemId, Integer currencyId) {
		ItemWeightedAverage itemWeightedAverage = getItemWeightedAverage(warehouseId, itemId);
		double rateValue = 1;
		if (currencyId != null) {
			CurrencyRate currencyRate = currencyRateService.getLatestRate(currencyId);
			if (currencyRate != null && currencyRate.isActive()) {
				rateValue = currencyRate.getRate();
				currencyRate = null;
			}
		}
		double weightedAverage = 0;
		if (itemWeightedAverage != null) {
			weightedAverage = itemWeightedAverage.getWeightedAve();
		}
		return weightedAverage / rateValue;
	}

	/**
	 * Get item weighted average from inventory listing and compute based on latest currency rate selected
	 * @param companyId The company id
	 * @param warehouseId The warehouse id
	 * @param itemId The item id
	 * @param currencyId The currency id
	 * @return The converted weighted average
	 */
	public Double getItemWeightedAverage(Integer companyId, Integer warehouseId, Integer itemId, Integer currencyId) {
		List<StockcardDto> stockcardDtos = getStockcardBeginningBal(companyId, warehouseId, itemId);
		double rateValue = 1.0;
		if (currencyId != null) {
			CurrencyRate currencyRate = currencyRateService.getLatestRate(currencyId);
			if (currencyRate != null && currencyRate.isActive()) {
				rateValue = currencyRate.getRate();
				currencyRate = null;
			}
		}
		double weightedAverage = 0;
		if (stockcardDtos != null && !stockcardDtos.isEmpty()) {
			Double weightedAveCost = stockcardDtos.iterator().next().getUnitCost();
			if (weightedAveCost != null) {
				weightedAverage = weightedAveCost;
			}
		}
		return NumberFormatUtil.divideWFP(weightedAverage, rateValue);
	}

	/**
	 * Get the warehouse object
	 * @param warehouseId The warehouse id
	 * @return The warehouse object
	 */
	public Warehouse getWarehouse(int warehouseId) {
		return warehouseService.getWarehouse(warehouseId);
	}
}
