package eulap.eb.validator.inv;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.InventoryAccount;
import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.InventoryAccountService;
import eulap.eb.service.ReceiptMethodService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validator class for Inventory Account.

 *
 */
@Service
public class InventoryAccountValidator implements Validator{
	private static Logger logger = Logger.getLogger(InventoryAccountValidator.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private InventoryAccountService inventoryAccountService;
	@Autowired
	private ReceiptMethodService receiptMethodService;
	@Autowired
	private AccountCombinationService accountCombinationService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;

	@Override
	public boolean supports(Class<?> clazz) {
		return InventoryAccount.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		logger.info("Validating the Inventory Forms Account Setup.");
		InventoryAccount ia = (InventoryAccount) obj;

		Integer companyId = ia.getCompanyId();
		logger.debug("Validating company id: "+companyId);
		if(companyId == null) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("InventoryAccountValidator.0"));
		} else if(!companyService.getCompany(companyId).isActive()) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("InventoryAccountValidator.1"));
		} else {
			logger.debug("Validating the uniqueness of the Inventory Forms Account Setup.");
			InventoryAccount savedIA = inventoryAccountService.getInvAcctByCompanyId(companyId, ia.getId());
			if(savedIA != null) {
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("InventoryAccountValidator.2"));
			}
		}

		Integer cashSalesRmId = ia.getCashSalesRmId();
		logger.debug("Validating cash sales receipt method id: "+cashSalesRmId);
		if(cashSalesRmId == null) {
			errors.rejectValue("cashSalesRmId", null, null, ValidatorMessages.getString("InventoryAccountValidator.3"));
		} else {
			checkRMStatus(cashSalesRmId, "cashSalesRmId", errors);
		}

		Integer customerAdvPaymentRmId = ia.getCustomerAdvPaymentRmId();
		logger.debug("Validating customer advance payment receipt method id: "+customerAdvPaymentRmId);
		if(customerAdvPaymentRmId == null) {
			errors.rejectValue("customerAdvPaymentRmId", null, null, ValidatorMessages.getString("InventoryAccountValidator.4"));
		} else {
			checkRMStatus(customerAdvPaymentRmId, "customerAdvPaymentRmId", errors);
		}
	}

	private void checkRMStatus (int receiptMethodId, String attrName, Errors error) {
		ReceiptMethod receiptMethod = receiptMethodService.getReceiptMethod(receiptMethodId);
		if (receiptMethod != null) {
			if (receiptMethod.getDebitAcctCombinationId() != null) {
				checkACStatus(receiptMethod.getDebitAcctCombinationId(), attrName, error,
						ValidatorMessages.getString("InventoryAccountValidator.5"));
			}
			checkACStatus(receiptMethod.getCreditAcctCombinationId(), attrName, error,
					ValidatorMessages.getString("InventoryAccountValidator.6"));
		}
	}

	private void checkACStatus (int acctCombiId, String attrName, Errors error, String customMessage) {
		AccountCombination accountCombination = accountCombinationService.getAccountCombination(acctCombiId);
		if (accountCombination != null) {
			ValidatorUtil.checkInactiveDivision(divisionService, accountCombination.getDivisionId(), attrName, error, "Division" +  customMessage);
			ValidatorUtil.checkInactiveAccount(accountService, accountCombination.getAccountId(), attrName, error, "Account" + customMessage);
		}
	}
}
