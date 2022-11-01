package eulap.eb.validator.inv;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.service.AccountSaleItemService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArLineService;
import eulap.eb.service.ArTransactionService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemBagQuantityService;
import eulap.eb.service.ItemService;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.TermService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validator class for account sales.

 *
 */
@Service
public class AccountSaleValidator implements Validator {
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private AccountSaleItemService accountSaleItemService;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ArLineService arLineService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TermService termService;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private FormStatusService formStatusService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ArTransaction.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		validate(object, errors, "", true);
	}

	/**
	 * Validate account sale form
	 * @param object The form object
	 * @param errors The validation errors
	 * @param fieldPrepend The form validation path prefix
	 * @param isValidateItems True if to validate empty item table, otherwise, false.
	 */
	public void validate(Object object, Errors errors, String fieldPrepend, boolean isValidateItems) {
		ArTransaction arTransaction = (ArTransaction) object;

		ValidatorUtil.validateCompany(arTransaction.getCompanyId(),
				companyService, errors, fieldPrepend+"companyId");

		if (arTransaction.getCustomerId() == null || arTransaction.getCustomerId() == -1)
			errors.rejectValue(fieldPrepend+"customerId", null, null, ValidatorMessages.getString("AccountSaleValidator.0"));
		else 
			arTransaction.setCreditLimit(
					arCustomerService.getCustomer(arTransaction.getCustomerId()).getMaxAmount());

		if (arTransaction.getTermId() == null){
			errors.rejectValue(fieldPrepend+"termId", null, null, ValidatorMessages.getString("AccountSaleValidator.1"));
		} else {
			Term term = termService.getTerm(arTransaction.getTermId());
			if (term.isActive() == false){
				errors.rejectValue(fieldPrepend+"termId", null, null, ValidatorMessages.getString("AccountSaleValidator.2"));
			}
		}

		if (arTransaction.getCustomerAcctId() == null)
			errors.rejectValue(fieldPrepend+"customerAcctId", null, null, ValidatorMessages.getString("AccountSaleValidator.3"));

		if (arTransaction.getTransactionDate() == null)
			errors.rejectValue(fieldPrepend+"transactionDate", null, null, ValidatorMessages.getString("AccountSaleValidator.4"));
		else if(!timePeriodService.isOpenDate(arTransaction.getTransactionDate()))
			errors.rejectValue(fieldPrepend+"transactionDate", null, null, ValidatorMessages.getString("AccountSaleValidator.5"));

		if (arTransaction.getDueDate() == null)
			errors.rejectValue(fieldPrepend+"dueDate", null, null, ValidatorMessages.getString("AccountSaleValidator.6"));
		else if(!timePeriodService.isOpenDate(arTransaction.getDueDate()))
			errors.rejectValue(fieldPrepend+"dueDate", null, null, ValidatorMessages.getString("AccountSaleValidator.7"));

		SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<AccountSaleItem>();
		List<AccountSaleItem> accountSaleItems = new ArrayList<AccountSaleItem>(arTransaction.getAccountSaleItems());
		boolean isValidItemIdAndQty = true;
		if (accountSaleItems != null && !accountSaleItems.isEmpty()) {
			if(isValidateItems) {
				if (arTransaction.getAmount().doubleValue() != SaleItemUtil.computeHeaderAmt(accountSaleItems)) {
					errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("AccountSaleValidator.8"));
				}
			}
			if (accountSaleItemService.hasInvalidItem(accountSaleItems)) {
				isValidItemIdAndQty = false;
				errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("AccountSaleValidator.9"));
			}
			if (accountSaleItemService.hasNoWarehouse(accountSaleItems)) {
				isValidItemIdAndQty = false;
				errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("AccountSaleValidator.10"));
			}
			if (saleItemUtil.hasNoQty(accountSaleItems)) {
				isValidItemIdAndQty = false;
				errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("AccountSaleValidator.11"));
			} else if (accountSaleItemService.hasNegQty(accountSaleItems)){
				isValidItemIdAndQty = false;
				errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("AccountSaleValidator.12"));
			}
			if (arTransaction.getCreditLimit() != null && arTransaction.getCreditLimit() != 0.0) {
				if (arTransaction.getAvailableBalance() != null) {
					double availableBalance = arTransactionService.computeAvailableBalance(arTransaction.getCustomerId(), arTransaction.getId());
					if (arTransactionService.isOverAvailableBalance(arTransaction.getTotalAmount(), availableBalance))
						errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("AccountSaleValidator.13"));
				}
			}
			if(!arTransaction.getTransactionTypeId().equals(ArTransactionType.TYPE_ACCOUNT_SALES_IS)) {
				String duplicateDiscErrorMsg = saleItemUtil.validateDuplicateItemAndDisc(accountSaleItems);
				if(duplicateDiscErrorMsg != null) {
					errors.rejectValue(fieldPrepend+"errorASItems", null, null, duplicateDiscErrorMsg);
				}
			}

		} else if(isValidateItems) {
			errors.rejectValue(fieldPrepend+"errorASItems", null, null, ValidatorMessages.getString("AccountSaleValidator.14"));
		}

		if(isValidItemIdAndQty) {
			if(arTransaction.getTransactionTypeId().equals(ArTransactionType.TYPE_ACCOUNT_SALES_IS)) {
				//Validate the quantity from individual selection
				String errorMsg = null;
				for (AccountSaleItem asi : accountSaleItems) {
					errorMsg = ValidationUtil.validateRefId(asi.getStockCode(), asi.getRefenceObjectId());
					if(errorMsg == null) {
						errorMsg = ValidationUtil.validateAvailableBagsAndStock(itemBagQuantityService, asi.getStockCode(), 
								arTransaction.getCompanyId(), asi.getItemId(), asi.getWarehouseId(), asi.getRefenceObjectId(), asi.getItemBagQuantity(), asi.getQuantity(), asi.getEbObjectId());
					}
					if(errorMsg != null) {
						errors.rejectValue(fieldPrepend+"errorASItems", null, null, errorMsg);
						break;
					}
				}

			} else if(arTransaction.getTransactionDate() != null) {
				//Validate the existing stocks as of the form date.
				int row = 0;
				if(arTransaction.getReferenceNo() == null || arTransaction.getReferenceNo().trim().isEmpty()) {
					for (AccountSaleItem asi : accountSaleItems) {
						row++;
						if(asi.getWarehouseId() != null){
							boolean isWarehouseChanged = !asi.getWarehouseId().equals(asi.getOrigWarehouseId());
							String quantityErrorMsg = ValidationUtil.validateSaleItems(itemService, warehouseService, asi.getItemId(),
									isWarehouseChanged, accountSaleItems, arTransaction.getTransactionDate(), asi.getWarehouseId(), row);
							if(quantityErrorMsg != null) {
								errors.rejectValue(fieldPrepend+"errorASItems", null, null, quantityErrorMsg);
								break;
							}
						}
					}
				}
			}
		}

		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		otherCharges.addAll(arTransaction.getArLines());
		if(!otherCharges.isEmpty()) {
			String errorMessage = arLineService.validateArLines(otherCharges, null, arTransaction.getCompanyId());
			if(errorMessage != null) {
				errors.rejectValue(fieldPrepend+"otherChargesMessage", null, null, errorMessage);
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
}
