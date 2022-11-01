package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountService;

/**
 * Account validator.

 *
 */
@Service
public class AccountValidator implements Validator{
	@Autowired
	private AccountService accountService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Account.class.equals(clazz);
	}

	@Override
	public void validate(Object arg0, Errors arg1) {
		// Do nothing.
	}

	public void validate(Object object, Errors errors, User user) {
		Account account = (Account) object;
		if (account.getAccountName() == null || account.getAccountName().trim().isEmpty()) {
			errors.rejectValue("accountName", null, null, ValidatorMessages.getString("AccountValidator.0"));
		} else {
			if (!accountService.isUniqueAccountName(account, user))
				errors.rejectValue("accountName", null, null, ValidatorMessages.getString("AccountValidator.1"));
		}
		if(account.getAccountTypeId() == null){
			errors.rejectValue("accountTypeId", null, null, ValidatorMessages.getString("AccountValidator.3"));
		}
		if (account.getNumber() == null || account.getNumber().trim().isEmpty()) {
			errors.rejectValue("number", null, null, ValidatorMessages.getString("AccountValidator.4"));
		} else {
			if (!StringFormatUtil.isNumeric(account.getNumber().trim()))
				errors.rejectValue("number", null, null, ValidatorMessages.getString("AccountValidator.5"));
			if (!accountService.isUniqueAccountNumber(account))
				errors.rejectValue("number", null, null, ValidatorMessages.getString("AccountValidator.6"));
			if (StringFormatUtil.containsInvalidChar(account.getNumber().trim(), StringFormatUtil.PERCENT_CHAR,
					StringFormatUtil.UNDERSCORE_CHAR))
				errors.rejectValue("number", null, null, ValidatorMessages.getString("AccountValidator.7"));
		}
	}
}
