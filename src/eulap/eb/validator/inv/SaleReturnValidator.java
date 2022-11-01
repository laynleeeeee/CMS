package eulap.eb.validator.inv;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.service.AccountSaleItemService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArLineService;
import eulap.eb.service.ArTransactionService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemBagQuantityService;
import eulap.eb.service.ItemService;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validator class for sales return

 *
 */
@Service
public class SaleReturnValidator implements Validator {
	@Autowired
	private AccountSaleItemService accountSaleItemService;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArLineService arLineService;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private FormStatusService formStatusService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ArTransaction.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		validateForm(object, errors, "", true);
	}

	public void validateForm(Object object, Errors errors, String fieldPrepend, boolean isItemRequired) {
		ArTransaction arTransaction = (ArTransaction) object;

		// Validate Header
		validateHeader(arTransaction, errors, fieldPrepend);

		// Validate Items and Ar Lines
		validateLines(arTransaction, errors, fieldPrepend, isItemRequired, true);
	}

	/**
	 * Validate Items and Other Charges
	 * @param arTransaction The {@link ArTransaction}
	 * @param errors The {@link Errors}
	 * @param fieldPrepend The field name to be prepended
	 * @param isItemRequired True if Item is required, otherwise false.
	 * @param validateEmptyReturnItems True if should validate empty return items, otherwise false
	 */
	public void validateLines(ArTransaction arTransaction, Errors errors,
			String fieldPrepend, boolean isItemRequired, boolean validateEmptyReturnItems) {
		boolean isValidQty = true;
		List<AccountSaleItem> accountSaleItems = null;
		if(arTransaction.getAccountSaleItems()!=null){
			accountSaleItems = new ArrayList<AccountSaleItem>(arTransaction.getAccountSaleItems());
		}

		String wholeSaleErrorMsg = null;
		if (accountSaleItems != null && !accountSaleItems.isEmpty()) {
			if (accountSaleItemService.hasInvalidItem(accountSaleItems))
				errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("SaleReturnValidator.5"));

			if (accountSaleItemService.hasNoWarehouse(accountSaleItems))
				errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("SaleReturnValidator.6"));

			if(validateEmptyReturnItems) {
				if (accountSaleItemService.hasNoReturnItems(accountSaleItems)) {
					errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("SaleReturnValidator.7"));
				}
			}

			if (accountSaleItemService.hasNoQty(accountSaleItems)) {
				errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("SaleReturnValidator.8"));
				isValidQty = false;
			}

			SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<AccountSaleItem>();
			List<AccountSaleItem> exchangedItems = SaleItemUtil.filterSaleReturnItems(accountSaleItems, false);
			List<AccountSaleItem> returnedItems = SaleItemUtil.filterSaleReturnItems(accountSaleItems, true);
			if(isValidQty) {
				if (saleItemUtil.isExchangingNeg(exchangedItems)) {
					errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("SaleReturnValidator.9"));
				}

				if (accountSaleItemService.hasInvalidReturnItem(returnedItems)) {
					errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("SaleReturnValidator.10"));
				}
			}

			boolean isSplit = saleItemUtil.hasSameItemAndDiscount(accountSaleItemService.getAcctSaleItems(arTransaction.getAccountSaleId()));
			//Validate return items
			for (AccountSaleItem asri : returnedItems) {
				Integer refAcctSaleItemId = asri.getRefAccountSaleItemId();
				Integer refObjectId = arTransaction.getTransactionTypeId() ==
						ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS ? asri.getRefenceObjectId() : null;
				//Compute the remaining quantity of to be returned items
				double remainingQty = isSplit ? accountSaleItemService.getRemainingQty(arTransaction.getAccountSaleId(),
							asri.getItemId(), asri.getWarehouseId()) : accountSaleItemService.getRemainingQty(refAcctSaleItemId);
				String errorMessage = ValidationUtil.validateReturnItems(asri, refAcctSaleItemId,
						refObjectId, remainingQty, asri.getStockCode());
				if(errorMessage != null) {
					errors.rejectValue(fieldPrepend+"errorASItems", null, null, errorMessage);
					break;
				}
				if(asri.getItemBagQuantity() != null) {
					if(asri.getOrigBagQty() == null || asri.getOrigBagQty() == 0.0) {
						errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("SaleReturnValidator.14"));
						break;
					}
					if(asri.getItemBagQuantity() > 0) {
						errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("SaleReturnValidator.12"));
					}

					if(Math.abs(asri.getItemBagQuantity()) > Math.abs(asri.getOrigBagQty())
							|| Math.abs(asri.getItemBagQuantity()) > itemBagQuantityService.getASIRemainingBagQty(arTransaction.getId(), refAcctSaleItemId)) {
						errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("SaleReturnValidator.13"));
					}
				}
			}

			//Validate exchanged items from individual selection type only
			List<AccountSaleItem> saleReturnItems = new ArrayList<>(accountSaleItems);
			if (arTransaction.getTransactionTypeId().equals(ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS)) {
				for (AccountSaleItem asri : saleReturnItems) {
					String errorMessage = null;
					if (asri.getRefAccountSaleItemId() == null) {
						if(errorMessage == null) {
							errorMessage = ValidationUtil.validateAvailableBagsAndStock(itemBagQuantityService, asri.getStockCode(), arTransaction.getCompanyId(), 
									asri.getItemId(), asri.getWarehouseId(), asri.getRefenceObjectId(), asri.getItemBagQuantity(), asri.getQuantity(), asri.getEbObjectId());

						}
						if(errorMessage != null) {
							errors.rejectValue(fieldPrepend+"errorASItems", null, null, errorMessage);
							break;
						}
					}
				}
			} else {
				if(arTransaction.getTransactionDate() != null && wholeSaleErrorMsg == null) {
					if(accountSaleItems != null) {
						//Validate only the exchanged retail items
						int row = returnedItems.size();
						for (AccountSaleItem asi : exchangedItems) {
							row++;
							boolean isWarehouseChanged = !asi.getWarehouseId().equals(asi.getOrigWarehouseId());
							String quantityErrorMsg = ValidationUtil.validateWithdrawnQty(itemService, warehouseService, asi.getItemId(),
									isWarehouseChanged, exchangedItems, arTransaction.getTransactionDate(), asi.getWarehouseId(), row);
							if(quantityErrorMsg != null) {
								errors.rejectValue(fieldPrepend+"errorASItems", null, null, quantityErrorMsg);
								break;
							}
						}
					}
				}

				if(arTransaction.getId() == 0) {
					List<AccountSaleItem> soldItems = accountSaleItemService.getAccountSaleItems(arTransaction.getAccountSaleId(), false);
					List<AccountSaleItem> returnItems =
							saleItemUtil.getSummarisedSaleItems(accountSaleItemService.getASRItemsByReference(arTransaction.getAccountSaleId()));
					SaleItemUtil.updateRefQuantity(saleReturnItems, soldItems, returnItems);
				}
			}
		}

		if(isItemRequired) {
			if(accountSaleItems == null || accountSaleItems.isEmpty()) {
				errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("SaleReturnValidator.11"));
			}

			List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
			List<ArLine> asrArLines = arTransaction.getArLines();
			if(asrArLines != null) {
				otherCharges.addAll(asrArLines);
				if(!otherCharges.isEmpty()) {
					String errorMessage = arLineService.validateArLines(otherCharges, null, arTransaction.getCompanyId());
					if(errorMessage != null) {
						errors.rejectValue(fieldPrepend+"otherChargesMessage", null, null, errorMessage);
					}
				}
			}
		}

		accountSaleItems = null;

		//Validate form status
		FormWorkflow workflow = arTransaction.getId() != 0 ? arTransactionService.getFormWorkflow(arTransaction.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
		}
	}

	/**
	 * Validate the header portion of AR transaction
	 * @param arTransaction The {@link ArTransaction}
	 * @param errors {@link Errors}
	 * @param fieldPrepend The field name to be prepended.
	 */
	public void validateHeader(ArTransaction arTransaction, Errors errors, String fieldPrepend) {
		if (arTransaction.getTransactionTypeId() == ArTransactionType.TYPE_SALE_RETURN) {
			if (arTransaction.getAccountSaleId() == null)
				errors.rejectValue(fieldPrepend+"accountSaleId", null, null, ValidatorMessages.getString("SaleReturnValidator.0"));
		}

		ValidatorUtil.validateCompany(arTransaction.getCompanyId(),
				companyService, errors, fieldPrepend+"companyId");

		if (arTransaction.getCustomerId() == null) {
			errors.rejectValue(fieldPrepend+"customerId", null, null, ValidatorMessages.getString("SaleReturnValidator.1"));
		} else {
			arTransaction.setCreditLimit(
					arCustomerService.getCustomer(arTransaction.getCustomerId()).getMaxAmount());
		}

		if (arTransaction.getCustomerAcctId() == null) {
			errors.rejectValue(fieldPrepend+"customerAcctId", null, null, ValidatorMessages.getString("SaleReturnValidator.2"));
		}

		if (arTransaction.getTransactionDate() == null) {
			errors.rejectValue(fieldPrepend+"transactionDate", null, null, ValidatorMessages.getString("SaleReturnValidator.3"));
		} else if(!timePeriodService.isOpenDate(arTransaction.getTransactionDate())) {
			errors.rejectValue(fieldPrepend+"transactionDate", null, null, ValidatorMessages.getString("SaleReturnValidator.4"));
		}
	}
}
