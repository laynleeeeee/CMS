package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ItemDiscountDao;
import eulap.eb.domain.hibernate.ItemDiscount;

/**
 * Class that impelements the business logic of {@link ItemDiscount}

 *
 */
@Service
public class ItemDiscountService {
	@Autowired
	private ItemDiscountDao itemDiscountDao;

	/**
	 * Get the list of item discounts by item.
	 * @param itemId The item id.
	 * @param activeOnly True if to return active discounts only, otherwise false.
	 * @return The list of item discounts.
	 */
	public List<ItemDiscount> getItemDiscountsByItem (Integer itemId, boolean activeOnly) {
		return itemDiscountDao.getItemDiscountsByItem(itemId, activeOnly);
	}

	/**
	 * Processes the item discounts that has no company id and discount type id.
	 * @param itemSrps The item discounts.
	 * @return List of item discounts.
	 */
	public List<ItemDiscount> processItemDiscounts (List<ItemDiscount> itemDiscounts) {
		if(itemDiscounts.isEmpty()) {
			return itemDiscounts;
		}
		ItemDiscountUtil<ItemDiscount> discountUtil = new ItemDiscountUtil<ItemDiscount>();
		return discountUtil.filterItemDiscounts(itemDiscounts);
	}

	/**
	 * Get the list of item discounts by stock code and company.
	 * @param itemId The item Id.
	 * @param companyId The company id.
	 * @return The list of item discounts.
	 */
	public List<ItemDiscount> getIDsByItemIdCodeAndCompany (Integer itemId, Integer companyId) {
		return itemDiscountDao.getIDsByItemIdAndCompany(itemId, companyId);
	}

	/**
	 * Get the item discount object
	 */
	public ItemDiscount getItemDiscount (int itemDiscountId) {
		return itemDiscountDao.get(itemDiscountId);
	}

	/**
	 * Get the list of item discount id with selected inactive discount id
	 * @param itemId The item id
	 * @param companyId The company id
	 * @param itemDiscountId The item discount id
	 * @return The list of item discount id with selected inactive discount id
	 */
	public List<ItemDiscount> getDiscountsWithSlctdDiscId(Integer itemId, Integer companyId,
			Integer itemDiscountId) {
		return itemDiscountDao.getDiscountsWithSlctdDiscId(itemId, companyId, itemDiscountId);
	}
}
