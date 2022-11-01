package eulap.eb.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.ItemDiscountDao;
import eulap.eb.dao.ItemDiscountTypeDao;
import eulap.eb.domain.hibernate.BaseItemDiscount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.ItemDiscountType;

/**
 * Utility class for Domain objects that extends {@link BaseItemDiscount}

 *
 * @param <T> Domain object that extends {@link BaseItemDiscount}
 */
public class ItemDiscountUtil<T extends BaseItemDiscount> {
	private static Logger logger = Logger.getLogger(ItemDiscountUtil.class);
	public static final int DISCOUNT_MAX_NAME = 50;

	/**
	 * Save the Domain objects that extends {@link BaseItemDiscount}
	 * @param itemDiscounts The list of item discounts to be saved.
	 * @param discountDao The Dao.
	 * @param itemId The primary key of the item.
	 */
	public void saveItemDiscounts(List<T> itemDiscounts, ItemDiscountDao discountDao, int itemId) {
		if (itemDiscounts != null) {
			for (T discount : itemDiscounts) {
				discount.setItemId(itemId);
				discount.setName(discount.getName().trim());
				discountDao.saveOrUpdate(discount);
			}
		}
		logger.debug("Successfully saved "+itemDiscounts.size()+" discounts for item id: "+itemId);
	}

	/**
	 * Filters the list of {@link BaseItemDiscount} to only get the discounts with data.
	 * @param itemDiscounts The list of item discounts to be processed.
	 * @return The list of item discounts.
	 */
	public List<T> filterItemDiscounts(List<T> itemDiscounts) {
		logger.debug("Filtering the list of item discounts to remove empty discount.");
		if(itemDiscounts == null) {
			return itemDiscounts;
		}
		List<T> ret = new ArrayList<T>();
		for (T itemDiscount : itemDiscounts) {
			String companyName = itemDiscount.getCompanyName();
			Double value = itemDiscount.getValue();
			if (companyName != null && !companyName.trim().isEmpty()){
				ret.add(itemDiscount);
			} else if (value != null && !value.equals(0.0)) {
				ret.add(itemDiscount);
			}
		}
		return ret;
	}

	/**
	 * Checks if the item discount has no name.
	 * @param itemDiscounts List of item discounts.
	 * @return True if no name, otherwise false.
	 */
	public boolean hasNoName (List<T> itemDiscounts) {
		if (itemDiscounts != null) {
			for (T itemDiscount : itemDiscounts) {
				if (itemDiscount.getName() == null)
					return true;
				else if (itemDiscount.getName().trim().isEmpty())
					return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the item contains invalid company/ies.
	 * @param itemDiscounts List of item discounts.
	 * @return True if has invalid company/ies, otherwise false.
	 */
	public boolean hasInvalidCompany (List<T> itemDiscounts, CompanyDao companyDao) {
		if (itemDiscounts != null) {
			Company company = null;
			for (T itemDiscount : itemDiscounts) {
				String companyName = itemDiscount.getCompanyName();
				if (companyName == null)
					return true;
				else if (companyName.trim().isEmpty())
					return true;
				else {
					company = companyDao.getCompanyByName(companyName.trim());
					if (company == null)
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the item has invalid item discount types.
	 * @param itemDiscounts The list of item discounts.
	 * @return True if item has invalid item discount type, otherwise false.
	 */
	public boolean hasInvalidItemDiscountType (List<T> itemDiscounts, ItemDiscountTypeDao discTypeDao) {
		if (itemDiscounts != null) {
			ItemDiscountType itemDiscountType = null;
			for (T itemDiscount : itemDiscounts) {
				String discTypeName = itemDiscount.getItemDiscountTypeName();
				if (discTypeName == null)
					return true;
				else if (discTypeName.trim().isEmpty())
					return true;
				else {
					itemDiscountType = discTypeDao.getItemDiscountType(discTypeName);
					if (itemDiscountType == null)
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the companies in item discounts are existing in the list of item srps.
	 * @param companyIds The list of company ids from item srps.
	 * @param itemDiscounts The list of item discounts.
	 * @return True if companies  in item discounts are existing in the list of item srps,
	 * otherwise, false.
	 */
	public boolean isCompanyNotInSrps (List<Integer> companyIds, List<T> itemDiscounts) {
		Set<Integer> discountCompanyIds = new HashSet<Integer>();
		for (T itemDiscount : itemDiscounts) {
			if (itemDiscount.getCompanyId() != null)
				discountCompanyIds.add(itemDiscount.getCompanyId());
		}

		for (Integer srpC : discountCompanyIds) {
			if (!companyIds.contains(srpC))
				return true;
		}

		return false;
	}

	/**
	 * Checks if the value of discount is negative.
	 * @param itemDiscounts The list of item discounts.
	 * @return True if a value of an item discount is negative, otherwise false.
	 */
	public boolean hasZeroNegValues (List<T> itemDiscounts) {
		if (itemDiscounts != null && !itemDiscounts.isEmpty()) {
			for (T itemDiscount : itemDiscounts) {
				Double value = itemDiscount.getValue();
				if (value != null) {
					if (Double.valueOf(value) <= 0)
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the discount name is unique per company.
	 * @param itemDiscounts The list of item discounts.
	 * @return True if a discount name is already existing or entered twice or more
	 * with the same company, otherwise false.
	 */
	public boolean hasDuplicateName (List<T> itemDiscounts) {
		if (itemDiscounts != null && !itemDiscounts.isEmpty()) {
			int size = itemDiscounts.size();
			for (int i=0; i<size; i++) {
				T id = itemDiscounts.get(i);
				logger.debug("id.discount "+id.getName()+" is active: "+id.isActive());
				if(id.isActive()) {
					int cnt = 0;
					for (int j=0; j<size; j++) {
						T id2 = itemDiscounts.get(j);
						logger.debug("id2. discount "+id2.getName()+" is active: "+id2.isActive());
						if (id.getCompanyName().trim().equalsIgnoreCase(id2.getCompanyName().trim())
								&& id2.isActive()) {
							logger.debug("Same company name...");
							if (id.getName().trim().equalsIgnoreCase(id2.getName().trim())) {
								logger.debug("Same discount name..." + "\n" + (id.getName() + " === " + id2.getName()));
								cnt++;
							}
						}
						if (cnt > 1)
							return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the discount name has exceeded the maximum number of characters.
	 * @param itemDiscounts The list of item discounts.
	 * @return True if a discount name has exceeded the maximum number of characters,
	 * otherwise false.
	 */
	public boolean hasNameExceeded (List<T> itemDiscounts) {
		if (itemDiscounts != null && !itemDiscounts.isEmpty()) {
			for (T itemDiscount : itemDiscounts) {
				if (itemDiscount.getName() != null) {
					if (itemDiscount.getName().trim().length() > DISCOUNT_MAX_NAME)
						return true;
				}
			}
		}
		return false;
	}
}
