package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.ItemCategoryAcctSetupDao;
import eulap.eb.dao.ItemCategoryDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.ItemCategoryAccountSetup;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ItemCategoryAccountSetupDto;

/**
 * Class that handles the business logic of Item Categories

 */
@Service
public class ItemCategoryService {
	@Autowired
	private ItemCategoryDao itemCategoryDao;
	@Autowired
	private ItemCategoryAccountSetUpService accountSetUpService;
	@Autowired
	private ItemCategoryAcctSetupDao acctSetupDao;
	@Autowired
	private CompanyService companyService;

	/**
	 * Retrieve item category by id
	 * @param itemCategoryId The item category id
	 * @return The Item Category object
	 */
	public ItemCategory itemCategory (int itemCategoryId){
		return itemCategory(itemCategoryId, null);
	}

	/**
	 * Retrieve item category by id
	 * @param itemCategoryId The item category id
	 * @param user The user current log.
	 * @return The Item Category object
	 */
	public ItemCategory itemCategory (int itemCategoryId, User user){
		ItemCategory category = itemCategoryDao.getItemCategory(itemCategoryId);
		if (category == null) {
			return null;
		} else {
			List<ItemCategoryAccountSetup> accountSetups = new ArrayList<>();
			if(user != null && user.getCompanyIds() != null && !user.getCompanyIds().isEmpty()){
				for (Integer cmpanyId : user.getCompanyIds()) {
					accountSetups.addAll(accountSetUpService.getItemcategoryAcctSetups(itemCategoryId, cmpanyId));
				}
			} else {
				accountSetups = 
						accountSetUpService.getItemcatAcctSetups(itemCategoryId);
			}
			category.setAccountSetups(accountSetups);
		}
		return category;
	}

	/**
	 * Get all item categories
	 * @param pageNumber The page number of the page setting
	 * @return The paged result
	 */
	public Page<ItemCategory> getAllItemCategories(int pageNumber) {
		Page<ItemCategory> result = itemCategoryDao.getAllItemCategories(new PageSetting(pageNumber,PageSetting.MAX_ADMIN_RECORD));
		List<ItemCategoryAccountSetup> accountSetups = null;
		for (ItemCategory itemCategory : result.getData()) {
			accountSetups = accountSetUpService.getItemcatAcctSetups(itemCategory.getId());
			itemCategory.setAccountSetups(accountSetups);
		}
		return result;
	}

	/**
	 * Get all active Item Categories.
	 * @return List of active Item Categories.
	 */
	public List<ItemCategory> getActiveItemCategories() {
		return itemCategoryDao.getActiveItemCategories();
	}

	/**
	 * Evaluate if item category is unique.
	 * @param itemCategory The item category object to be evaluated.
	 * @return True if unique otherwise false.
	 */
	public boolean isUniqueCategory (ItemCategory itemCategory){
		if (itemCategory.getId() == 0)
			return itemCategoryDao.isUniqueItemCategory(itemCategory);
		ItemCategory oldCategory = itemCategoryDao.get(itemCategory.getId());
		// User did not change the name
		if (itemCategory.getName().trim().equalsIgnoreCase(oldCategory.getName().trim()))
			return true;
		return itemCategoryDao.isUniqueItemCategory(itemCategory);
	}

	/**
	 * Save the item category.
	 * @param user The current logged user.
	 * @param itemCategory The item category object to be saved.
	 */
	public void saveItemCategory(User user, ItemCategory itemCategory){
		boolean isNewRecord = itemCategory.getId() == 0 ? true : false;
		AuditUtil.addAudit(itemCategory, new Audit(user.getId(), isNewRecord, new Date()));
		// Remove white space before saving.
		itemCategory.setName(itemCategory.getName().trim());
		itemCategoryDao.saveOrUpdate(itemCategory);

		int itemCategoryId = itemCategory.getId();
		List<ItemCategoryAccountSetup> accountSetups = accountSetUpService.getItemcatAcctSetups(itemCategoryId);
		if(accountSetups != null){
			List<Integer> companyIds = user.getCompanyIds();
			for (ItemCategoryAccountSetup itemCategoryAccountSetup : accountSetups) {
				if(companyIds != null && !companyIds.isEmpty()){
					if(companyIds.contains(itemCategoryAccountSetup.getCompanyId())){
						acctSetupDao.delete(itemCategoryAccountSetup);
					}
				} else {
					acctSetupDao.delete(itemCategoryAccountSetup);
				}
			}
		}

		if(itemCategory.getAccountSetups() != null){
			for (ItemCategoryAccountSetup itemCategoryAccountSetup : itemCategory.getAccountSetups()) {
				itemCategoryAccountSetup.setItemCategoryId(itemCategoryId);
				AuditUtil.addAudit(itemCategoryAccountSetup, new Audit(user.getId(), true, new Date()));
				acctSetupDao.save(itemCategoryAccountSetup);
			}
		}
	}

	/**
	 * Search for item categories
	 * @param name The name of the item category
	 * @param status The status {All, Active, Inactive}
	 * @param pageNumber The page number of the pageSetting
	 * @param user The user current log.
	 * @return The paged result
	 */
	public Page<ItemCategory> searchCategories(String name, String status, int pageNumber, User user){
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<ItemCategory> result = itemCategoryDao.searchItemCategories(name.trim(),
				searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		List<ItemCategoryAccountSetup> accountSetups = null;
		List<Company> companies = companyService.getCompanies(user);
		List<ItemCategoryAccountSetupDto> accountSetupDtos = null;
		ItemCategoryAccountSetupDto accountSetupDto = null;
		for (ItemCategory itemCategory : result.getData()) {
			accountSetupDtos = new ArrayList<ItemCategoryAccountSetupDto>();
			int itemCategoryId = itemCategory.getId();
			for (Company company : companies) {
				accountSetups = accountSetUpService.getItemcategoryAcctSetups(itemCategoryId, company.getId());
				accountSetupDto = new ItemCategoryAccountSetupDto();
				if(!accountSetups.isEmpty()){
					accountSetupDto.setHdnId("ic"+itemCategoryId+"c"+company.getId());
					accountSetupDto.setAccountSetups(accountSetups);
					accountSetupDto.setCompanyName(company.getName());
					accountSetupDtos.add(accountSetupDto);
				}
			}
			itemCategory.setAccountSetupDtos(accountSetupDtos);
		}
		return result;
	}

	/**
	 * Get the list of item categories by company id.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return The list of item categories.
	 */
	public List<ItemCategory> getItemCategoriesByCompany(String name, int companyId, Integer divisionId) {
		return itemCategoryDao.getItemCategoriesByCompany(name, companyId, divisionId);
	}

	/**
	 * Get the item category by name and company.
	 * @param name The name of item category.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return The item category.
	 */
	public ItemCategory getItemCategoryByName (String name, int companyId, Integer divisionId) {
		return itemCategoryDao.getItemCategoryByName(name, companyId, divisionId);
	}

	/**
	 * Get Item Category.
	 * @param itemCategoryId the item category id.
	 * @return The Item Category.
	 */
	public ItemCategory getItemCategory(Integer itemCategoryId) {
		return itemCategoryDao.get(itemCategoryId);
	}

	/**
	 * Process Item Category Account Setup that has no details.
	 * @param itemCategory The item category.
	 */
	public List<ItemCategoryAccountSetup> processItemCategoryAcctSetup(ItemCategory itemCategory) {
		List<ItemCategoryAccountSetup> ret = new ArrayList<ItemCategoryAccountSetup>();
		for (ItemCategoryAccountSetup accountSetup : itemCategory.getAccountSetups()) {
			if(accountSetup.getCompanyName() != null && !accountSetup.getCompanyName().trim().isEmpty()){
				ret.add(accountSetup);
			} else if(accountSetup.getDivisionName() != null && !accountSetup.getDivisionName().trim().isEmpty()){
				ret.add(accountSetup);
			} else if(accountSetup.getCostAccountName() != null && !accountSetup.getCostAccountName().trim().isEmpty()){
				ret.add(accountSetup);
			} else if(accountSetup.getInventoryAccountName() != null && !accountSetup.getInventoryAccountName().trim().isEmpty()){
				ret.add(accountSetup);
			} else if(accountSetup.getSalesAccountName() != null && !accountSetup.getSalesAccountName().trim().isEmpty()){
				ret.add(accountSetup);
			} else if(accountSetup.getSalesDiscountAccountName() != null && !accountSetup.getSalesDiscountAccountName().trim().isEmpty()){
				ret.add(accountSetup);
			} else if(accountSetup.getSalesReturnAccountName() != null && !accountSetup.getSalesReturnAccountName().trim().isEmpty()){
				ret.add(accountSetup);
			}
		}
		return ret;
	}

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
	public ItemCategoryAccountSetup processAcctCombinations(Integer companyId, Integer divisionId) {
		return acctSetupDao.getAcctCombinations(companyId, divisionId);
	}

	/**
	 * Get the list of item categories by name.
	 * @param categoryName The item category name.
	 * @return The list of item categories.
	 */
	public List<ItemCategory> getByName(String categoryName, User user) {
		return itemCategoryDao.getItemCategoriesByCompany(categoryName, user);
	}

	/**
	 * Get the item category by name.
	 * @param name The name of item category.
	 * @return The item category.
	 */
	public ItemCategory getItemCategoryByExactName (String name) {
		return itemCategoryDao.getItemCategoryByName(name, null);
	}
}
