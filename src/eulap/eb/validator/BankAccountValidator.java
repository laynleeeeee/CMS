package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.Bank;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.service.AccountService;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;

/**
 * A class that handles data validation for {@link BankAccount}.

 */
@Service
public class BankAccountValidator  implements Validator{
	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;

	@Override
	public boolean supports(Class<?> clazz) {
		return BankAccount.class.equals(clazz);
	}

	/**
	 * Validate bank account
	 * @param target The object target
	 * @param error The validation error
	 * @param bankAccountId The bank account id
	 */
	public void validate(Object target, Errors error, int bankAccountId) {
		BankAccount bankAccount = (BankAccount) target;

		String bankName = bankAccount.getName();
		if (bankName == null || bankName.isEmpty()) {
			error.rejectValue("name", null, null, ValidatorMessages.getString("BankAccountValidator.0"));
		} else if (!bankAccountService.isUnique(bankAccount)) {
			error.rejectValue("name", null, null, ValidatorMessages.getString("BankAccountValidator.1"));
		} else if (bankName.length() > BankAccount.MAX_NAME_CHAR) {
			error.rejectValue("name", null, null, String.format(ValidatorMessages.getString("BankAccountValidator.5"),
					BankAccount.MAX_NAME_CHAR));
		}

		String bankAcctNo = bankAccount.getBankAccountNo();
		if (bankAcctNo == null || bankAcctNo.isEmpty()) {
			error.rejectValue("bankAccountNo", null, null, ValidatorMessages.getString("BankAccountValidator.6"));
		} else if(!bankAccountService.isUniqueField(bankAccount, true)) {
			error.rejectValue("bankAccountNo", null, null, ValidatorMessages.getString("BankAccountValidator.8"));
		} else if(bankAcctNo.length() > BankAccount.MAX_NUMBER_CHAR) {
			error.rejectValue("bankAccountNo", null, null, String.format(ValidatorMessages.getString("BankAccountValidator.7"),
					BankAccount.MAX_NUMBER_CHAR));
		}

		Integer bankId = bankAccount.getBankId();
		if (bankId != null) {
			Bank bank = bankAccountService.getBank(bankId);
			if (bank != null && !bank.isActive()) {
				error.rejectValue("bankId", null, null, ValidatorMessages.getString("BankAccountValidator.9"));
			}
		}

		if (bankAccount.getCompanyId() == 0) {
			error.rejectValue("companyId", null, null, ValidatorMessages.getString("BankAccountValidator.2"));
		} else {
			ValidatorUtil.checkInactiveCompany(companyService, bankAccount.getCompanyId(), "companyId", error, null);
		}

		if (bankAccount.getInBAAccountId() == 0 || bankAccount.getInBADivisionId() == 0) {
			error.rejectValue("cashInBankAcctId", null, null,ValidatorMessages.getString("BankAccountValidator.3"));
		}

		if (bankAccount.getInBADivisionId() != 0) {
			ValidatorUtil.checkInactiveDivision(divisionService, bankAccount.getInBADivisionId(), "inBADivisionId", error, null);
		}

		if (bankAccount.getInBAAccountId() != 0) {
			ValidatorUtil.checkInactiveAccount(accountService, bankAccount.getInBAAccountId(), "cashInBankAcctId", error, null);
		}
	}

	@Override
	public void validate(Object obj, Errors error) {
		throw new RuntimeException(ValidatorMessages.getString("BankAccountValidator.4"));
	}
}