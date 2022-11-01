package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eulap.eb.dao.ItemAddOnDao;
import eulap.eb.domain.hibernate.BaseItemAddOn;

/**
 * Utility class for Domain objects that extends {@link BaseItemAddOn}

 *
 * @param <T> Domain object that extends {@link BaseItemAddOn}
 */

public class ItemAddOnUtil<T extends BaseItemAddOn> {
	private final Logger logger = Logger.getLogger(ItemAddOnUtil.class);
	public static final int ADD_ON_MAX_NAME = 50;

	/**
	 * Save the domain objects that extends {@link BaseItemAddOn}
	 * @param addOns The list of add ons.
	 * @param itemId The primary key of the item.
	 */
	public void saveAddOns(List<T> addOns, ItemAddOnDao addOnDao, int itemId) {
		if (addOns != null) {
			for (T addOn : addOns) {
				addOn.setItemId(itemId);
				addOn.setName(addOn.getName().trim());
				addOnDao.saveOrUpdate(addOn);
			}
		}
	}

	/**
	 * Removes the empty rows of the base item add on.
	 * @param itemAddOns The list of item add ons.
	 * @return The list of filtered add ons.
	 */
	public List<T> filterAddOns (List<T> itemAddOns) {
		if(itemAddOns == null) {
			return itemAddOns;
		}
		List<T> ret = new ArrayList<T>();
		if (itemAddOns != null) {
			for (T addOn : itemAddOns) {
				String companyName = addOn.getCompanyName();
				if(companyName != null && addOn.getValue() != null) {
					if (!companyName.trim().isEmpty() || addOn.getValue() > 0.0) {
						ret.add(addOn);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Checks if the add on name has exceeded the maximum number of characters.
	 * @param itemAddOns The list of item add ons.
	 * @return True if a add on name has exceeded the maximum number of characters,
	 * otherwise false.
	 */
	public boolean hasNameExceeded (List<T> itemAddOns) {
		if (itemAddOns != null && !itemAddOns.isEmpty()) {
			for (T itemAddOn : itemAddOns) {
				if (itemAddOn.getName() != null) {
					if (itemAddOn.getName().trim().length() > ADD_ON_MAX_NAME) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the value of add on is negative.
	 * @param itemAddOns The list of item add ons.
	 * @return True if a value of an item add on is negative, otherwise false.
	 */
	public boolean hasNegValues (List<T> itemAddOns) {
		if (itemAddOns != null && !itemAddOns.isEmpty()) {
			for (T itemDiscount : itemAddOns) {
				Double value = itemDiscount.getValue();
				if (value != null) {
					if (Double.valueOf(value) < 0)
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the item add on has no name.
	 * @param itemAddOns List of item add ons.
	 * @return True if no name, otherwise false.
	 */
	public boolean hasNoName (List<T> itemAddOns) {
		if (itemAddOns != null) {
			for (T itemAddOn : itemAddOns) {
				if (itemAddOn.getName() == null)
					return true;
				else if (itemAddOn.getName().trim().isEmpty())
					return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the add on name is unique per company.
	 * @param itemAddOns The list of item add ons.
	 * @return True if a add on name is already existing or entered twice or more
	 * with the same company, otherwise false.
	 */
	public boolean hasDuplicateName (List<T> itemAddOns) {
		if (itemAddOns != null && !itemAddOns.isEmpty()) {
			int size = itemAddOns.size();
			for (int i=0; i<size; i++) {
				T id = itemAddOns.get(i);
				logger.debug("id.discount "+id.getName()+" is active: "+id.isActive());
				if(id.isActive()) {
					int cnt = 0;
					for (int j=0; j<size; j++) {
						T id2 = itemAddOns.get(j);
						logger.debug("id2. discount "+id2.getName()+" is active: "+id2.isActive());
						if (id.getCompanyName().trim().equalsIgnoreCase(id2.getCompanyName().trim())
								&& id2.isActive()) {
							logger.debug("Same company name...");
							if (id.getName().trim().equalsIgnoreCase(id2.getName().trim())) {
								logger.debug("Same discount name..." + "\n" + (id.getName() + " === " + id2.getName()));
								cnt++;
							}
						}
						if (cnt > 1) {
							logger.debug("Add on names have duplicates.");
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
