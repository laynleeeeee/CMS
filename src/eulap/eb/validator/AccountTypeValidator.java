package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountTypeService;

/**
 * Account type validator.

 *
 */
@Service
public class AccountTypeValidator implements Validator {
	@Autowired
	private AccountTypeService accountTypeService;

	@Override
	public boolean supports(Class<?> clazz) {
		return AccountTypeValidator.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		// Do nothing.
	}

	public void validate(Object object, Errors errors, User user) {
		AccountType accountType = (AccountType) object;
		if (accountType.getName() == null || accountType.getName().trim().isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("AccountTypeValidator.0"));
		} else {
			if (!accountTypeService.isUniqueAccountType(accountType, user))
				errors.rejectValue("name", null, null, ValidatorMessages.getString("AccountTypeValidator.1"));
			if (StringFormatUtil.containsInvalidChar(accountType.getName().trim(), StringFormatUtil.PERCENT_CHAR,
					StringFormatUtil.UNDERSCORE_CHAR))
				errors.rejectValue("name", null, null, ValidatorMessages.getString("AccountTypeValidator.2"));
		}
	}
}
