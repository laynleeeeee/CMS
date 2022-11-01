package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.ItemAddOnDao;
import eulap.eb.domain.hibernate.ItemAddOn;

/**
 * Class that handles the business logic of {@link ItemAddOn}

 *
 */
@Service
public class ItemAddOnService {
	@Autowired
	private ItemAddOnDao itemAddOnDao;

	/**
	 * Get the item add on object.
	 * @param itemAddOnId The id.
	 */
	public ItemAddOn getItemAddOn (Integer itemAddOnId) {
		return itemAddOnDao.get(itemAddOnId);
	}

	/**
	 * Computes the value of the add on plus the srp.
	 * @param itemAddOnId The unique id of the add on of the item.
	 * @param srp The selling price.
	 * @return The computed add on plus the srp.
	 */
	public ItemAddOn computeAddOnValue(Integer itemAddOnId, double srp, double quantity, Integer taxTypeId) {
		ItemAddOn itemAddOn = getItemAddOn(itemAddOnId);
		//Compute only per unit of measure of the item.
		double computedAddOn = SaleItemUtil.computeAddOn(itemAddOn, quantity < 1 ? 1 : quantity, srp, taxTypeId);
		if(quantity > 1) {
			computedAddOn = NumberFormatUtil.divideWFP(computedAddOn, quantity);
		}
		itemAddOn.setComputedAddOn(computedAddOn);
		return itemAddOn;
	}

	/**
	 * Get the list of item add ons by item.
	 * @param itemId The item id.
	 * @param activeOnly True if to return active add ons only, otherwise false.
	 * @return The list of item add ons.
	 */
	public List<ItemAddOn> getItemAddOnsByItem (Integer itemId, boolean activeOnly) {
		return itemAddOnDao.getItemAddOnsByItem(itemId, activeOnly);
	}

	/**
	 * Get the list of item add ons by item and company.
	 * @param itemId The item id.
	 * @param companyId The id of the company.
	 * @param activeOnly True if to return active add ons only, otherwise false.
	 * @return The list of item add ons.
	 */
	public List<ItemAddOn> getItemAddOnsByItem (Integer itemId, Integer companyId, boolean activeOnly) {
		return itemAddOnDao.getItemAddOnsByItem(itemId, companyId, activeOnly);
	}

	/**
	 * Processes the item add ons that has no company id.
	 * @param itemSrps The item add ons.
	 * @return List of item add ons.
	 */
	public List<ItemAddOn> processItemAddOns (List<ItemAddOn> itemAddOns) {
		ItemAddOnUtil<ItemAddOn> addOnUtil = new ItemAddOnUtil<ItemAddOn>();
		return addOnUtil.filterAddOns(itemAddOns);
	}

	/**
	 * Get the list of item add ons with selected inactive item add on id
	 * @param itemId The item id
	 * @param companyId The company id
	 * @param itemAddOnId The add on id
	 * @return The list of item add ons with selected inactive item add on id
	 */
	public List<ItemAddOn> getAddOnsWSlctdInactiveAddOnId(Integer itemId,
			Integer companyId, Integer itemAddOnId) {
		return itemAddOnDao.getAddOnsWSlctdInactiveAddOnId(itemId, companyId, itemAddOnId);
	}
}
