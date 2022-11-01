package eulap.eb.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.dto.LoginCredential;

/**
 * Handles the validation of user change password.

 */
@Service
public class ChangePasswordValidator  implements Validator{
	
	@Override
	public boolean supports(Class<?> clazz) {
		return LoginCredential.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		LoginCredential credential = (LoginCredential) obj;

		if (isEmpty(credential.getNewPassword())) {
			errors.rejectValue("message", null, null, ValidatorMessages.getString("ChangePasswordValidator.0"));
		} else {
			if (!credential.getNewPassword().equals(credential.getConfirmPassword())) {
				errors.rejectValue("message", null, null, ValidatorMessages.getString("ChangePasswordValidator.1"));
			}
		}
	}
	
	private boolean isEmpty (String str) {
		return str == null || str.equals("");
	}
}