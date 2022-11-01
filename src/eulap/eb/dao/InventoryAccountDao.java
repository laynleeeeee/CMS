package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.InventoryAccount;

/**
 * Data Access Object {@link InventoryAccount}

 */
public interface InventoryAccountDao extends Dao<InventoryAccount>{

	/**
	 * Search inventory accounts.
	 * @param companyId The company id.
	 * @param statusId The status of the inventory account.
	 * @param pageSetting The page setting.
	 * @return The list of inventory accounts in paged format.
	 */
	Page<InventoryAccount> searchInventoryAccts(int companyId, int statusId, PageSetting pageSetting);

	/**
	 * Get the inventory account object using the company id.
	 * @param companyId The id of the company
	 * @return The Inventory Account, otherwise null.
	 */
	InventoryAccount getInvAcctByCompanyId(int companyId, int inventoryAcctId);
}
