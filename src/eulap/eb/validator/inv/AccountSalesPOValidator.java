package eulap.eb.validator.inv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AccountSales;
import eulap.eb.domain.hibernate.AccountSalesPoItem;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemService;
import eulap.eb.service.WarehouseService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validation class of Account Sales

 *
 */
@Service
public class AccountSalesPOValidator implements Validator{
	private static Logger logger = Logger.getLogger(AccountSalesPOValidator.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private WarehouseService warehouseService;

	@Override
	public boolean supports(Class<?> clazz) {
		return AccountSales.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		validate(object, errors, false);
	}

	public void validate(Object object, Errors errors, boolean isRequestByUserInput) {
		AccountSales as = (AccountSales) object;

		logger.debug("Validating company id: "+as.getCompanyId());
		ValidatorUtil.validateCompany(as.getCompanyId(), companyService, errors, "companyId");

		logger.debug("Validating customer id: "+as.getArCustomerId());
		if(as.getArCustomerId() == null)
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("AccountSalesPOValidator.0"));

		logger.debug("Validating customer account id: "+as.getArCustomerAccountId());
		if(as.getArCustomerAccountId() == null) {
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("AccountSalesPOValidator.1"));
		}

		logger.debug("Validating date: "+as.getPoDate());
		if(as.getPoDate() == null) {
			errors.rejectValue("poDate", null, null, ValidatorMessages.getString("AccountSalesPOValidator.2"));
		}

		logger.debug("Validating remarks: "+as.getRemarks());
		if(as.getRemarks() != null) {
			if(as.getRemarks().trim().length() > AccountSales.MAX_REMARKS_CHAR) {
				errors.rejectValue("remarks", null, null, ValidatorMessages.getString("AccountSalesPOValidator.4"));
			}
		}

		Integer row = 0;
		List<AccountSalesPoItem> poItems = as.getAsPoItems();
		List<String> poItemIds = extractItemIds(poItems);
		Integer frequencyOfItemId = null;
		Integer itemId = null;
		Warehouse warehouse = null;
		for (AccountSalesPoItem poItem : poItems) {
			if(poItem.getItemId() != null) {
				row++;

				//Validate stock code
				itemId = poItem.getItemId();
				logger.debug("Validating item id: "+itemId);
				if(itemId == -1) {
					errors.rejectValue("poMessage", null, null, ValidatorMessages.getString("AccountSalesPOValidator.5")+row);
					break;
				}

				//Duplicate stock codes
				logger.debug("Checking for duplicates");
				frequencyOfItemId = Collections.frequency(poItemIds, poItem.getItemId()+"w"+poItem.getWarehouseId());
				logger.debug("Frequency for current item is: "+frequencyOfItemId);
				if(frequencyOfItemId > 1) {
					errors.rejectValue("poMessage", null, null, ValidatorMessages.getString("AccountSalesPOValidator.6"));
					break;
				}

				//Quantity
				logger.debug("Validating quantity: "+poItem.getQuantity());
				if(poItem.getQuantity() == null) {
					errors.rejectValue("poMessage", null, null, ValidatorMessages.getString("AccountSalesPOValidator.7")+row+ValidatorMessages.getString("AccountSalesPOValidator.8"));
					break;
				} else if(poItem.getQuantity() <= 0) {
					errors.rejectValue("poMessage", null, null, ValidatorMessages.getString("AccountSalesPOValidator.9")+row+ValidatorMessages.getString("AccountSalesPOValidator.10"));
					break;
				}

				double existingStocks = itemService.getItemExistingStocks(poItem.getItemId(), poItem.getWarehouseId());
				double existing = ((poItem.getOrigQty() == null ? 0 : poItem.getOrigQty()) + existingStocks);
				if(poItem.getWarehouseId() == null) {
					errors.rejectValue("poMessage", null, null, ValidatorMessages.getString("AccountSalesPOValidator.16"));
				}
				if(poItem.getQuantity() > existing) {
					warehouse = warehouseService.getWarehouse(poItem.getWarehouseId());
					errors.rejectValue("poMessage", null, null, String.format(ValidatorMessages.getString("AccountSalesPOValidator.17"), warehouse.getName(),
							String.valueOf(existingStocks), row));
				}
			} else if(poItem.getQuantity() != null || poItem.getStockCode() != null) {
				errors.rejectValue("poMessage", null, null, ValidatorMessages.getString("AccountSalesPOValidator.13"));
				break;
			}
		}

		if(row == 0) {
			logger.debug("User did not select stock code.");
			errors.rejectValue("poMessage", null, null, ValidatorMessages.getString("AccountSalesPOValidator.14"));
		}
	}

	/**
	 * Extract the item ids from the list of PO Items
	 * @return List of item ids.
	 */
	public List<String> extractItemIds(List<AccountSalesPoItem> poItems) {
		logger.debug("Extracting item ids from list.");
		List<String> itemIds = new ArrayList<String>();
		if (poItems != null) {
			for (AccountSalesPoItem item : poItems) {
				if(item.getItemId() != null) {
					if(item.getItemId() != -1) {
						itemIds.add(item.getItemId()+"w"+item.getWarehouseId());
						logger.trace("Added id: "+item.getItemId());
					}
				}
			}
		}

		if(itemIds.isEmpty()) {
			logger.debug("No item selected for PO.");
		} else {
			logger.debug("Successfully extracted "+itemIds.size()+" item ids.");
		}
		return itemIds;
	}
}
