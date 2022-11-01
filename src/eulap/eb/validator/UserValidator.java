package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.UserService;

/**
 * Class that handles the validation of the user when saving.

 *
 */
@Service
public class UserValidator implements Validator{
	@Autowired
	private UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		User user = (User) obj;		

		if (user.getUsername() == null || user.getUsername().trim().isEmpty()){
			errors.rejectValue("username", null, null, ValidatorMessages.getString("UserValidator.0"));
		} else if (user.getUsername().trim().length() > 20) {
			errors.rejectValue(ValidatorMessages.getString("UserValidator.1"), null, null, ValidatorMessages.getString("UserValidator.2"));
		} else {
			if (!userService.isUniqueUserName(user.getUsername())) {
				if (userService.getUserIdByUsername(user.getUsername()) != user.getId()) {
					errors.rejectValue("username", null, null, ValidatorMessages.getString("UserValidator.3"));
				}
			}
		}

		if(user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			errors.rejectValue("password", null, null, ValidatorMessages.getString("UserValidator.4"));
		}

		boolean isValidFirstName = true;
		boolean isValidLastName = true;
		String firstName = user.getFirstName();
		if (firstName == null || firstName.trim().isEmpty()){
			errors.rejectValue("firstName", null, null, ValidatorMessages.getString("UserValidator.5"));
			isValidFirstName = false;
		} else if(firstName.trim().length() > 40) {
			errors.rejectValue("firstName", null, null, ValidatorMessages.getString("UserValidator.6"));
			isValidFirstName = false;
		}

		String lastName = user.getLastName();
		if (lastName == null || lastName.trim().isEmpty()){
			errors.rejectValue("lastName", null, null, ValidatorMessages.getString("UserValidator.7"));
			isValidLastName = false;
		} else if(lastName.trim().length() > 40) {
			errors.rejectValue("lastName", null, null, ValidatorMessages.getString("UserValidator.8"));
			isValidLastName = false;
		}

		if (isValidFirstName && isValidLastName) {
			if (!userService.isUniqueName(firstName, lastName, user.getId())) {
				errors.rejectValue("lastName", null, null,
						String.format(ValidatorMessages.getString("UserValidator.21"), firstName, lastName));
			}
		}

		if (user.getMiddleName() != null && !user.getMiddleName().trim().isEmpty() 
				&& user.getMiddleName().trim().length() > 40) {
			errors.rejectValue("middleName", null, null, ValidatorMessages.getString("UserValidator.10"));
		}

		if (user.getContactNumber() == null || user.getContactNumber().trim().isEmpty()){
			errors.rejectValue("contactNumber", null, null, ValidatorMessages.getString("UserValidator.11"));
		} else if(user.getContactNumber().trim().length() > 20) {
			errors.rejectValue("contactNumber", null, null, ValidatorMessages.getString("UserValidator.12"));
		} else if(user.getContactNumber() != null && !user.getContactNumber().trim().isEmpty()){
			if (!StringFormatUtil.isNumeric(user.getContactNumber()))
				errors.rejectValue("contactNumber", null, null, ValidatorMessages.getString("UserValidator.13"));
		}

		if (user.getAddress() == null || user.getAddress().trim().isEmpty()){
			errors.rejectValue("address", null, null, ValidatorMessages.getString("UserValidator.14"));
		} else if(user.getAddress().trim().length() > 150) {
			errors.rejectValue("address", null, null, ValidatorMessages.getString("UserValidator.15"));
		}

		if (user.getEmailAddress() == null || user.getEmailAddress().trim().isEmpty()){
			errors.rejectValue("emailAddress", null, null, ValidatorMessages.getString("UserValidator.16"));
		} else if(user.getEmailAddress().trim().length() > 50) {
			errors.rejectValue("emailAddress", null, null, ValidatorMessages.getString("UserValidator.17"));
		}

		if (user.getBirthDate() == null) {
			errors.rejectValue("birthDate", null, null, ValidatorMessages.getString("UserValidator.18"));
		}

		if (user.getPositionId() == null){
			errors.rejectValue("positionId", null, null, ValidatorMessages.getString("UserValidator.19"));
		}

		if (user.getUserGroupId() == null){
			errors.rejectValue("userGroupId", null, null, ValidatorMessages.getString("UserValidator.20"));
		}
	}
}
