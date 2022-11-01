package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ItemCategoryAccountSetup;

/**
 * Data Access Object {@link ItemCategoryAccountSetup}

 */
public interface ItemCategoryAcctSetupDao extends Dao<ItemCategoryAccountSetup>{


	/**
	 * Get the item category account set up.
	 * @param companyId
	 * @param itemCatId
	 * @param status
	 * @param pageNumber
	 * @return Return the item category account set up.
	 */
	Page<ItemCategoryAccountSetup> getItemCategory(int companyId,
			int itemCatId, SearchStatus searchStatus, PageSetting pageSetting);

	/**
	 * Check if the Item Category Account Setup is unique.
	 * @param accountSetup The {@link ItemCategoryAccountSetup}
	 * @return Return if item account setup is unique combination.
	 */
	boolean isUniqueItemCatAcctSetup(ItemCategoryAccountSetup accountSetup);

	/**
	 * Get the list of item category account setup per item category.
	 * @param itemCategoryId The item category id.
	 * @param companyId The company id.
	 * @return The list of item category account setup per item category.
	 */
	List<ItemCategoryAccountSetup> getItemcatAcctSetups(Integer itemCategoryId, Integer companyId);

	/**
	 * Get the default account combinations based on the provided company and division id.
	 * Default Accounts:
	 * 	Cost Account = Cost of Sales
	 * 	Inventory Account = Merchandise Inventory
	 * 	Sales Account = Sales
	 * 	Sales Discount Account = Sales Discount
	 * 	Sales Return Account = Sales Return
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return The {@link ItemCategoryAccountSetup} object.
	 */
	ItemCategoryAccountSetup getAcctCombinations(Integer companyId, Integer divisionId);

	/**
	 * Get the item category account setup by company and division
	 * @param itemCategoryId The item category id
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @return The item category account setup by company and division
	 */
	ItemCategoryAccountSetup getItemCategoryAcctByCompanyDiv(Integer itemCategoryId, Integer companyId, Integer divisionId);
}
