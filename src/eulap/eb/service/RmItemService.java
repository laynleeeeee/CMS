package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ItemAddOnDao;
import eulap.eb.dao.ItemBuyingAddOnDao;
import eulap.eb.dao.ItemBuyingDiscountDao;
import eulap.eb.dao.ItemBuyingPriceDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ItemDiscountDao;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemBuyingAddOn;
import eulap.eb.domain.hibernate.ItemBuyingDiscount;
import eulap.eb.domain.hibernate.ItemBuyingPrice;

/**
 * Business logic for Item with buying details.

 *
 */
@Service
public class RmItemService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemBuyingService buyingService;
	@Autowired
	private ItemBuyingAddOnDao buyingAddOnDao;
	@Autowired
	private ItemBuyingPriceDao buyingPriceDao;
	@Autowired
	private ItemBuyingDiscountDao buyingDiscountDao;
	@Autowired
	private ItemDiscountDao itemDiscountDao;
	@Autowired
	private ItemAddOnDao itemAddOnDao;

	/**
	 * Get all the items bases from the search parameters.
	 * @param divisionId The division id.
	 * @param stockCode The stock code of the item.
	 * @param description The description of the item.
	 * @param uomId The id of the unit measurement.
	 * @param itemCategoryId The id of the category the item is under.
	 * @param status The status of the item {ALL, Active, Inactive}
	 * @param pageNumber The page number.
	 * @return All items in paging format.
	 */
	public Page<Item> getAllItems(String stockCode, String description, Integer uomId,
			Integer itemCategoryId, Integer status, Integer pageNumber) {
		Page<Item> items = itemDao.getRetailItems(stockCode, description, uomId,
				itemCategoryId, status, false, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		for (Item rItem : items.getData()) {
			//Get both the selling and buying details of the retail item.
			rItem.setrItemDetails(itemDao.getRItemDetails(rItem.getId(), true));
		}
		return items;
	}

	/**
	 * Get all the list of retail items with division on item SRP
	 * @param divisionId The division id
	 * @param stockCode The stock code
	 * @param description The item description
	 * @param uomId The unit of measurement id
	 * @param itemCategoryId The item category id
	 * @param status The item status
	 * @param pageNumber The page number
	 * @return The list of retail items with division on item SRP
	 */
	public Page<Item> getAllItemWithDivision(Integer divisionId, String stockCode, String description, Integer uomId,
			Integer itemCategoryId, Integer status, Integer pageNumber) {
		Page<Item> items = itemDao.getRetailItemWithDivision(divisionId, stockCode, description, uomId,
				itemCategoryId, status, false, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		for (Item rItem : items.getData()) {
			//Get both the selling and buying details of the retail item.
			rItem.setrItemDetails(itemDao.getRItemDetails(rItem.getId(), true));
		}
		return items;
	}

	/**
	 * Process the selling and buying details of the retail item to remove empty data.
	 * @param item The retail item.
	 */
	public void processItemDetails(Item item) {
		//Selling
		itemService.processItemDetails(item);
		//Buying
		item.setBuyingPrices(buyingService.filterBuyingPrices(item.getBuyingPrices()));
		item.setBuyingAddOns(buyingService.filterAddOnds(item.getBuyingAddOns()));
		item.setBuyingDiscounts(buyingService.filterDiscounts(item.getBuyingDiscounts()));
	}

	/**
	 * Get the Retail Item with its respective buying details.
	 * @param itemId The unique id of the item.
	 * @return The retail item.
	 */
	public Item getRItemWithBuyingDetails(int itemId) {
		Item rItem = itemService.getItem(itemId);
		itemService.getItemDetails(rItem);
		List<ItemBuyingPrice> buyingPrices = buyingService.getBuyingPrices(itemId, true);
		if(!buyingPrices.isEmpty()) {
			//Set the Buying details
			rItem.setBuyingPrices(buyingPrices);
			rItem.setBuyingAddOns(buyingAddOnDao.getAllByRefId("itemId", itemId));
			rItem.setBuyingDiscounts(buyingDiscountDao.getAllByRefId("itemId", itemId));
		} else {
			//Create an empty list for the buying details
			rItem.setBuyingPrices(new ArrayList<ItemBuyingPrice>());
			rItem.setBuyingAddOns(new ArrayList<ItemBuyingAddOn>());
			rItem.setBuyingDiscounts(new ArrayList<ItemBuyingDiscount>());
		}
		return rItem;
	}

	/**
	 * Save the Retail Item with both selling and buying details.
	 */
	public void saveRmItem(Item rItem, int userId) {
		itemService.saveRItem(rItem, userId);
		saveItemBuyingDetails(rItem);
	}

	/**
	 * Save the buying details of the retail item.
	 */
	private void saveItemBuyingDetails(Item rItem) {
		List<ItemBuyingPrice> buyingPrices = rItem.getBuyingPrices();
		if(buyingPrices != null) {
			for (ItemBuyingPrice ibp : buyingPrices) {
				ibp.setItemId(rItem.getId());
				ibp.setActive(true);
				if(ibp.getId() == 0) {
					buyingPriceDao.save(ibp);
				} else {
					ItemBuyingPrice savedBuyingPrice = buyingPriceDao.get(ibp.getId());
					if(!savedBuyingPrice.getBuyingPrice().equals(ibp.getBuyingPrice())) {
						savedBuyingPrice.setActive(false);
						buyingPriceDao.update(savedBuyingPrice);
						buyingPriceDao.save(ibp);
					}
				}
			}
		}

		//Save buying discounts
		ItemDiscountUtil<ItemBuyingDiscount> discountUtil = new ItemDiscountUtil<ItemBuyingDiscount>();
		discountUtil.saveItemDiscounts(rItem.getBuyingDiscounts(), itemDiscountDao, rItem.getId());

		//Save buying add ons
		ItemAddOnUtil<ItemBuyingAddOn> addOnUtil = new ItemAddOnUtil<ItemBuyingAddOn>();
		addOnUtil.saveAddOns(rItem.getBuyingAddOns(), itemAddOnDao, rItem.getId());
	}
}
