package eulap.eb.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.ItemCategoryAccountSetup;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ItemCategoryAccountSetUpService;
import eulap.eb.service.ItemCategoryService;
/**
 * Class that handles validation of Item Category 

 */

@Service
public class ItemCategoryValidator implements Validator{
	@Autowired
	private ItemCategoryService itemCategoryService;
	@Autowired
	private ItemCategoryAccountSetUpService accountSetUpService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountCombinationService accountCombinationService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ItemCategory.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ItemCategory itemCategory = (ItemCategory) target;
		// Name
		if (itemCategory.getName() == null || itemCategory.getName().trim().isEmpty()){
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ItemCategoryValidator.0"));
		}
		else if (!itemCategoryService.isUniqueCategory(itemCategory)){
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ItemCategoryValidator.1"));
		} else if (itemCategory.getName().trim().length() > ItemCategory.MAX_NAME){
			errors.rejectValue("name", null, null, String.format(ValidatorMessages.getString("ItemCategoryValidator.2"), ItemCategory.MAX_NAME));
		}

		List<ItemCategoryAccountSetup> accountSetups = itemCategory.getAccountSetups();
		if(accountSetups != null && !accountSetups.isEmpty()){
			boolean invalidItemSetup = false;
			for (ItemCategoryAccountSetup accountSetup : accountSetups) {
				if(accountSetup.getCompanyId() == null){
					if(accountSetup.getCompanyName() != null && !accountSetup.getCompanyName().trim().isEmpty()){
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.3"));
					} else {
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.4"));
					}
					invalidItemSetup = true;
					break;
				} else if(accountSetup.getDivisionId() == null){
					if(accountSetup.getDivisionName() != null && !accountSetup.getDivisionName().trim().isEmpty()){
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.5"));
					} else {
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.6"));
					}
					invalidItemSetup = true;
					break;
				} else if(accountSetup.getCostAccount() == null){
					if(accountSetup.getCostAccountName() != null && !accountSetup.getCostAccountName().trim().isEmpty()){
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.7"));
					} else {
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.8"));
					}
					invalidItemSetup = true;
					break;
				} else if(accountSetup.getInventoryAccount() == null){
					if(accountSetup.getInventoryAccountName() != null && !accountSetup.getInventoryAccountName().trim().isEmpty()){
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.9"));
					} else {
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.10"));
					}
					invalidItemSetup = true;
					break;
				} else if(accountSetup.getSalesAccount() == null){
					if(accountSetup.getSalesAccountName() != null && !accountSetup.getSalesAccountName().trim().isEmpty()){
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.11"));
					} else {
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.12"));
					}
					invalidItemSetup = true;
					break;
				} else if(accountSetup.getSalesDiscountAccount() == null){
					if(accountSetup.getSalesDiscountAccountName() != null && !accountSetup.getSalesDiscountAccountName().trim().isEmpty()){
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.13"));
					} else {
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.14"));
					}
					invalidItemSetup = true;
					break;
				} else if(accountSetup.getSalesReturnAccount() == null){
					if(accountSetup.getSalesReturnAccountName() != null && !accountSetup.getSalesReturnAccountName().trim().isEmpty()){
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.15"));
					} else {
						errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.16"));
					}
					invalidItemSetup = true;
					break;
				}
			}

			if(!invalidItemSetup) {
				if(!accountSetUpService.isUniqueCompanyPerItemCategory(accountSetups)) {
					errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ItemCategoryValidator.17"));
				} else {
					int index = 1;
					for (ItemCategoryAccountSetup accountSetup : accountSetups) {
						ValidatorUtil.checkInactiveCompany(companyService, accountSetup.getCompanyId(), "errorMessage", errors, ValidatorMessages.getString("ItemCategoryValidator.18")
								+ ValidatorMessages.getString("ItemCategoryValidator.19") + index + ValidatorMessages.getString("ItemCategoryValidator.20"));
						ValidatorUtil.checkInactiveDivision(divisionService, accountSetup.getDivisionId(), "errorMessage", errors, ValidatorMessages.getString("ItemCategoryValidator.21")
								+ ValidatorMessages.getString("ItemCategoryValidator.22") + index + ValidatorMessages.getString("ItemCategoryValidator.23"));
						if (accountSetup.getCostAccount() != null) {
							checkAccount(accountSetup.getCostAccount(), "errorMessage", errors, ValidatorMessages.getString("ItemCategoryValidator.24"), index);
						}

						if (accountSetup.getInventoryAccount() != null) {
							checkAccount(accountSetup.getInventoryAccount(), "errorMessage", errors, ValidatorMessages.getString("ItemCategoryValidator.25"), index);
						}

						if (accountSetup.getSalesAccount() != null) {
							checkAccount(accountSetup.getSalesAccount(), "errorMessage", errors, ValidatorMessages.getString("ItemCategoryValidator.26"),  index);
						}

						if (accountSetup.getSalesDiscountAccount() != null) {
							checkAccount(accountSetup.getSalesDiscountAccount(), "errorMessage", errors, ValidatorMessages.getString("ItemCategoryValidator.27"),  index);
						}

						if (accountSetup.getSalesReturnAccount() != null) {
							checkAccount(accountSetup.getSalesReturnAccount(), "errorMessage", errors, ValidatorMessages.getString("ItemCategoryValidator.28"),  index);
						}
						index++;
					}
				}
			}
		}

	}

	private void checkAccount (int acctCombiId, String attrName, Errors error, String acctName, int index) {
		AccountCombination ac = accountCombinationService.getAccountCombination(acctCombiId);
		if (ac != null) {
			ValidatorUtil.checkInactiveAccount(accountService, ac.getAccountId(), attrName, error, ValidatorMessages.getString("ItemCategoryValidator.29") + acctName + ValidatorMessages.getString("ItemCategoryValidator.30")
					+ ValidatorMessages.getString("ItemCategoryValidator.31") + index + ValidatorMessages.getString("ItemCategoryValidator.32"));
		}
	}
}
