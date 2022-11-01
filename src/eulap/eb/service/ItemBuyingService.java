package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ItemBuyingAddOnDao;
import eulap.eb.dao.ItemBuyingDiscountDao;
import eulap.eb.dao.ItemBuyingPriceDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemBuyingAddOn;
import eulap.eb.domain.hibernate.ItemBuyingDiscount;
import eulap.eb.domain.hibernate.ItemBuyingPrice;

/**
 * Class that handles the business logic of Items' buying details.

 */
@Service
public class ItemBuyingService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemBuyingPriceDao buyingPriceDao;
	@Autowired
	private ItemBuyingAddOnDao buyingAddOnDao;
	@Autowired
	private ItemBuyingDiscountDao buyingDiscountDao;

	/**
	 * Process the buying add ons to remove empty data.
	 * @param buyingAddOns The list of {@link ItemBuyingAddOn}.
	 * @return The list of add ons that have data.
	 */
	public List<ItemBuyingAddOn> filterAddOnds(List<ItemBuyingAddOn> buyingAddOns) {
		ItemAddOnUtil<ItemBuyingAddOn> addOnUtil = new ItemAddOnUtil<ItemBuyingAddOn>();
		return addOnUtil.filterAddOns(buyingAddOns);
	}

	/**
	 * Process the buying discounts to remove empty data.
	 * @param buyingAddOns The list of {@link ItemBuyingDiscount}.
	 * @return The list of discounts that have data.
	 */
	public List<ItemBuyingDiscount> filterDiscounts(List<ItemBuyingDiscount> discounts) {
		if(discounts.isEmpty()) {
			return discounts;
		}
		ItemDiscountUtil<ItemBuyingDiscount> discountUtil = new ItemDiscountUtil<ItemBuyingDiscount>();
		return discountUtil.filterItemDiscounts(discounts);
	}

	/**
	 * Filters the list of {@link ItemBuyingPrice} to retain only with data.
	 * @param buyingPrices The list of item buying prices.
	 * @return The filtered list.
	 */
	public List<ItemBuyingPrice> filterBuyingPrices(List<ItemBuyingPrice> buyingPrices) {
		if(buyingPrices == null) {
			return buyingPrices;
		}
		List<ItemBuyingPrice> processedPrices = new ArrayList<ItemBuyingPrice>();
		for (ItemBuyingPrice ibp : buyingPrices) {
			if(!ibp.getCompanyName().trim().isEmpty() || ibp.getBuyingPrice() != 0.0) {
				processedPrices.add(ibp);
			}
		}
		return processedPrices;
	}

	/**
	 * Extract the list of company ids from the list of buying prices of the item.
	 * @param buyingPrices the list of buying prices.
	 * @return The list of distinct company ids.
	 */
	public List<Integer> getCompanyIds(List<ItemBuyingPrice> buyingPrices) {
		List<Integer> companyIds  = new ArrayList<Integer>();
		if(!buyingPrices.isEmpty()) {
			for (ItemBuyingPrice bp : buyingPrices) {
				if(Collections.frequency(companyIds, bp.getCompanyId()) == 0) {
					companyIds.add(bp.getCompanyId());
				}
			}
		}
		return companyIds;
	}

	/**
	 * Checks if the list of buying prices have invalid details.
	 * @param buyingPrices The list of buying prices.
	 * @return True if there is an invalid company, otherwise false.
	 */
	public String validateBuyingPrices(List<ItemBuyingPrice> buyingPrices) {
		if(!buyingPrices.isEmpty()) {
			for (ItemBuyingPrice ibp : buyingPrices) {
				if(ibp.getCompanyId() == null && ibp.getBuyingPrice() != null) {
					return "Invalid company for buying price.";
				}

				if(ibp.getCompanyId() != null && ibp.getBuyingPrice().compareTo(0.0) <= 0) {
					return "Price should be greater than zero.";
				}
			}
		}
		return null;
	}

	/**
	 * Get the retail item configured for buying.
	 * @param companyId The id of the company.
	 * @param warehouseId The id of the warehouse.
	 * @param stockCode The stock code of the item.
	 * @return The {@link Item} object.
	 */
	public Item getBuyingItem(Integer companyId, Integer warehouseId, String stockCode) {
		return itemDao.getBuyingItem(companyId, warehouseId, stockCode);
	}

	/**
	 * Get the list of items that have buying details.
	 * @param companyId The company id.
	 * @param stockCode The stock code of the item.
	 * @return The list of items with buying discounts.
	 */
	public List<Item> getBuyingItems(Integer companyId, String stockCode) {
		return itemDao.getBuyingItems(companyId, stockCode.trim());
	}

	/**
	 * Get the latest buying price of the item.
	 * @param itemId The unique id of the item.
	 * @param companyId The unique id of the company.
	 * @return The {@link ItemBuyingPrice} of the latest buying price.
	 */
	public ItemBuyingPrice getLatestBPrice(Integer itemId, Integer companyId) {
		return buyingPriceDao.getLatestBuyingPrice(itemId, companyId);
	}

	/**
	 * Get the list of Buying Discounts of the item.
	 * @param itemId The unique id of the item.
	 * @param companyId The id of the company.
	 * @return The list of {@link ItemBuyingDiscount}
	 */
	public List<ItemBuyingDiscount> getBuyingDiscounts(Integer itemId, Integer companyId) {
		return buyingDiscountDao.getBuyingDiscounts(itemId, companyId);
	}

	/**
	 * Get the Buying discount of an item using its unique id.
	 * @param id The id of the buying discount.
	 * @return The {@link ItemBuyingDiscount} object.
	 */
	public ItemBuyingDiscount getBDiscount(int id) {
		return buyingDiscountDao.get(id);
	}

	/**
	 * Get the buying discount then compute the discount.
	 * @param discountId the unique id of the discount.
	 * @param quantity The quantity of the item.
	 * @param unitCost The unit cost of the item.
	 * @return The computed discount.
	 */
	public double computeDiscount(Integer discountId, Double quantity, Double unitCost) {
		ItemBuyingDiscount buyingDiscount = buyingDiscountDao.get(discountId);
		if (buyingDiscount == null) {
			return 0;
		}
		return SaleItemUtil.computeDiscount(buyingDiscount, buyingDiscount.getItemDiscountTypeId(),
				quantity, unitCost, null);
	}

	/**
	 * Get the list of buying add ons of the item.
	 * @param itemId The id of the item.
	 * @param companyId The id of the company.
	 * @return The list of {@link ItemBuyingAddOn}.
	 */
	public List<ItemBuyingAddOn> getBuyingAddOns(Integer itemId, Integer companyId) {
		return buyingAddOnDao.getBuyingAddOns(itemId, companyId);
	}

	/**
	 * Get the buying add on using its unique id.
	 * @param id The id of the buying add on.
	 * @return The {@link ItemBuyingAddOn} object.
	 */
	public ItemBuyingAddOn getBAddOn(int id) {
		return buyingAddOnDao.get(id);
	}

	/**
	 * Get the list of Buying prices of the item.
	 * @param itemId The id of the item.
	 * @param isActiveOnly Set to true if retrieve only active prices, otherwise false.
	 * @return The list of {@link ItemBuyingPrice}.
	 */
	public List<ItemBuyingPrice> getBuyingPrices(int itemId, boolean isActiveOnly) {
		return buyingPriceDao.getBuyingPrices(itemId, isActiveOnly);
	}
}
