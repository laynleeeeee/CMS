package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.dto.LoginCredential;
import eulap.eb.service.UserService;

/**
 * Handles the validation of user.

 */
@Service
public class LoginValidator implements Validator{
	@Autowired
	private UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return LoginCredential.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		LoginCredential credential = (LoginCredential) obj;		
		if (!userService.isUniqueUserName(credential.getUserName())) {
			if (isEmpty(credential.getPassword()) || !userService.validateUser(credential)) 
				errors.rejectValue("message", null, null, ValidatorMessages.getString("LoginValidator.0"));
		} else {
			errors.rejectValue("message", null, null, ValidatorMessages.getString("LoginValidator.1"));
		}
	}
	
	private boolean isEmpty (String str) {
		return str == null || str.equals("");
	}
}
