package eulap.eb.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ItemCategoryAcctSetupDao;
import eulap.eb.domain.hibernate.ItemCategoryAccountSetup;

/**
 * Item Category Account SetUp Service

 *
 */
@Service
public class ItemCategoryAccountSetUpService {
	@Autowired
	private ItemCategoryAcctSetupDao acctCatSetupDao;
	@Autowired
	private CompanyService companyService;

	/**
	 * Check if the Item Category Account Setup is unique.
	 * @param accountSetup The {@link ItemCategoryAccountSetup}
	 * @return Return if item account setup is unique combination.
	 */
	public boolean isUniqueItemCatAcctSetup(
			ItemCategoryAccountSetup accountSetup) {
		return acctCatSetupDao.isUniqueItemCatAcctSetup(accountSetup);
	}

	/**
	 * Get the Item Account Setup.
	 * @param pId The Item Account Setup id.
	 * @return Return Item Account Setup.
	 */
	public ItemCategoryAccountSetup getItemcatAcctSetup(Integer pId) {
		return acctCatSetupDao.get(pId);
	}

	/**
	 * Get the list of item category account setup per item category.
	 * @param itemCategoryId The item category id.
	 * @return The list of item category account setup per item category.
	 */
	public List<ItemCategoryAccountSetup> getItemcatAcctSetups(Integer itemCategoryId) {
		return getItemcategoryAcctSetups(itemCategoryId, null);
	}

	/**
	 * Check if the company is unique.
	 * @param accountSetups
	 * @return True, if the company is unique per item category, otherwise false.
	 */
	public boolean isUniqueCompanyPerItemCategory(List<ItemCategoryAccountSetup> accountSetups) {
		Map<String, ItemCategoryAccountSetup> hmItemSetup =
				new HashMap<>();
		String key = null;
		for (ItemCategoryAccountSetup itemAccountSetup : accountSetups) {
			key = "c"+itemAccountSetup.getCompanyId()+"d"+itemAccountSetup.getDivisionId();
			if (hmItemSetup.containsKey(key) && itemAccountSetup.isActive()) {
				return false;
			} else {
				if(itemAccountSetup.isActive()) {
					hmItemSetup.put(key, itemAccountSetup);
				}
			}
		}
		return true;
	}

	/**
	 * Get the list of item category account setup per item category.
	 * @param itemCategoryId The item category id.
	 * @return The list of item category account setup per item category.
	 */
	public List<ItemCategoryAccountSetup> getItemcategoryAcctSetups(Integer itemCategoryId, Integer companyId) {
		List<ItemCategoryAccountSetup> accountSetups = acctCatSetupDao.getItemcatAcctSetups(itemCategoryId, companyId);
		for (ItemCategoryAccountSetup accountSetup : accountSetups) {
			accountSetup.setCompanyName(companyService.getCompanyName(accountSetup.getCompanyId()));
			accountSetup.setCostAccountName(accountSetup.getCostAccountCombi().getAccount().getAccountName());
			accountSetup.setInventoryAccountName(accountSetup.getInventoryAccountCombi().getAccount().getAccountName());
			accountSetup.setSalesAccountName(accountSetup.getSalesAccountCombi().getAccount().getAccountName());
			accountSetup.setSalesDiscountAccountName(accountSetup.getSalesDiscountAccountCombi().getAccount().getAccountName());
			accountSetup.setSalesReturnAccountName(accountSetup.getSalesReturnAccountCombi().getAccount().getAccountName());
			accountSetup.setDivisionId(accountSetup.getSalesAccountCombi().getDivisionId());
			accountSetup.setDivisionName(accountSetup.getSalesAccountCombi().getDivision().getName());
		}
		return accountSetups;
	}
}
