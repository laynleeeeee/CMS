package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArLineSetupService;

/**
 * Validation class for AR Customer Account.

 *
 */
@Service
public class ArCustomerAcctValidator implements Validator{
	@Autowired
	private ArCustomerAcctService accountService;
	@Autowired
	private ArCustomerService customerService;
	@Autowired
	private ArLineSetupService arLineService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ArCustomerAccount.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ArCustomerAccount account = (ArCustomerAccount) obj;

		//Name
		if(account.getName() == null || account.getName().trim().isEmpty())
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.0"));
		else if(account.getName().length() > 100)
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.1"));
		else if(!accountService.isUniqueName(account))
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.2"));

		//Customer
		if(account.getArCustomerId() == 0)
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.3"));
		else if(!customerService.getCustomer(account.getArCustomerId()).isActive())
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.4"));

		//Account Combination
		AccountCombination accountCombi = accountService.getAcctCombi(account.getCompanyId(),
				account.getDebitDivisionId(), account.getDebitAccountId());
		if(accountCombi == null)
			errors.rejectValue("defaultDebitACId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.5"));
		else {
			if(!accountCombi.isActive())
				errors.rejectValue("defaultDebitACId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.6"));

			//Company
			if(!accountService.isActiveAcComponent(account, 1))
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.7"));

			//Division
			if(!accountService.isActiveAcComponent(account, 2))
				errors.rejectValue("debitDivisionId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.8"));

			//Account
			if(!accountService.isActiveAcComponent(account, 3))
				errors.rejectValue("debitAccountId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.9"));
		}

		//Transaction Line
		ArLineSetup arLine = arLineService.getLineSetup(account.getDefaultTransactionLineId());
		if(arLine != null) {
			accountCombi = accountService.accountCombi(arLine.getAccountCombinationId());
			if(!arLine.isActive())
				errors.rejectValue("defaultTransactionLineId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.10"));
			else if(!accountCombi.isActive())
				errors.rejectValue("defaultTransactionLineId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.11"));
		}

		if(account.getDefaultWithdrawalSlipACId() != null) {
			AccountCombination withdrawalSlipAC = accountService.getAcctCombi(account.getCompanyId(),
					account.getWithdrawalSlipDivisionId(), account.getWithdrawalAccountId());
			if(withdrawalSlipAC == null) {
				errors.rejectValue("defaultWithdrawalSlipACId", null, null, "ArCustomerAcctValidator.12");
			} else {
				if(!withdrawalSlipAC.isActive()) {
					errors.rejectValue("defaultWithdrawalSlipACId", null, null, "ArCustomerAcctValidator.13");
				}

				//Division
				if(!accountService.isActiveAcComponent(account, 2)) {
					errors.rejectValue("withdrawalSlipDivisionId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.14"));
				}

				//Account
				if(!accountService.isActiveAcComponent(account, 3)) {
					errors.rejectValue("withdrawalAccountId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.15"));
				}
			}
		}

		if (account.getTermId() == null) {
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("ArCustomerAcctValidator.16"));
		}
	}
}
