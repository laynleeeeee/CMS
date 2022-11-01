package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.User;

/**
 * Item category data access object.


 */
public interface ItemCategoryDao extends Dao<ItemCategory> {
	/**
	 * Get the item categories.
	 * @return The item categories.
	 */
	List<ItemCategory> getItemCategories ();

	/**
	 * Retrieve item category by id
	 * @param itemCategoryId The Id of item category
	 * @return The Item category object
	 */
	ItemCategory getItemCategory (int itemCategoryId);

	/**
	 * Check whether item category is unique
	 * @param itemCategory The Item Category object to be evaluated
	 * @return True if unique otherwise false
	 */
	boolean isUniqueItemCategory (ItemCategory itemCategory);

	/**
	 * Get all Item categories 
	 * @param pageSetting The page setting
	 * @return The paged result
	 */
	Page<ItemCategory> getAllItemCategories(PageSetting pageSetting);

	/**
	 * Search for Item Categories
	 * @param name The name of the category
	 * @param status The status {All, Active and Inactive}
	 * @param pageSetting The page setting
	 * @return The paged result
	 */
	Page<ItemCategory> searchItemCategories(String name, SearchStatus status, PageSetting pageSetting);

	/**
	 * Get the list of active Item Categories.
	 * @return List of all active Item Categories.
	 */
	List<ItemCategory> getActiveItemCategories();
	
	/**
	 * Get the list of item categories by company id.
	 * @param name The name of item category.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return The list of item categories.
	 */
	List<ItemCategory> getItemCategoriesByCompany(String name, int companyId, Integer divisionId);

	/**
	 * Get the list of item categories by user company.
	 * @param name The name of item category.
	 * @param user The user currently logged.
	 * @return The list of item categories.
	 */
	List<ItemCategory> getItemCategoriesByCompany(String name, User user);

	/**
	 * Get the item category by name and company.
	 * @param name The name of item category.
	 * @param companyId The company id.
	 * @return The item category.
	 */
	ItemCategory getItemCategoryByName (String name, Integer companyId);

	/**
	 * Get the item category by name, company and division.
	 * @param name The name of item category.
	 * @param divisionId The division id.
	 * @param companyId The company id.
	 * @return The item category.
	 */
	ItemCategory getItemCategoryByName (String name, Integer companyId, Integer divisionId);
}
