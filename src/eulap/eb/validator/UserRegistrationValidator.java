package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.KeyCode;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.KeyCodeService;
import eulap.eb.service.UserService;
import eulap.eb.web.dto.UserRegistration;

/**
 * Validator class for User Registration module.

 *
 */
@Service
public class UserRegistrationValidator implements Validator {
	@Autowired
	private KeyCodeService keyCodeService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private UserService userService;

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public void validate(Object obj, Errors errors) {
		UserRegistration userRegistration = (UserRegistration) obj;

		//Key Code
		String code = userRegistration.getKeyCode().trim();
		if(code.isEmpty()) {
			errors.rejectValue("keyCode", null, null, ValidatorMessages.getString("UserRegistrationValidator.0"));
		} else {
			KeyCode keyCode = keyCodeService.getKeyCode(code);
			if(keyCode == null) {
				errors.rejectValue("keyCode", null, null, ValidatorMessages.getString("UserRegistrationValidator.1"));
			} else if(keyCode.isUsed()) {
				errors.rejectValue("keyCode", null, null, ValidatorMessages.getString("UserRegistrationValidator.2"));
			} else if(!keyCode.isActive()) {
				errors.rejectValue("keyCode", null, null, ValidatorMessages.getString("UserRegistrationValidator.3"));
			}
		}

		//Validate Company
		Company company = userRegistration.getCompany();
		if(company.getName().trim().isEmpty()) {
			errors.rejectValue("company.name", null, null, ValidatorMessages.getString("UserRegistrationValidator.4"));
		} else if (!companyService.isUnique(company, 2, null)){
			errors.rejectValue("company.name", null, null, ValidatorMessages.getString("UserRegistrationValidator.5"));
		} else if(company.getName().trim().length() > 100) {
			errors.rejectValue("company.name", null, null, ValidatorMessages.getString("UserRegistrationValidator.6"));
		}

		if (company.getAddress().trim().isEmpty()) {
			errors.rejectValue("company.address", null, null, ValidatorMessages.getString("UserRegistrationValidator.7"));
		} else if(company.getAddress().trim().length() > 200) {
			errors.rejectValue("company.address", null, null, ValidatorMessages.getString("UserRegistrationValidator.8"));
		}

		//Validate User
		User user = userRegistration.getUser();

		//Username
		if (user.getUsername() == null || user.getUsername().trim().isEmpty()){
			errors.rejectValue("user.username", null, null, ValidatorMessages.getString("UserRegistrationValidator.9"));
		} else if (user.getUsername().trim().length() > 20) {
			errors.rejectValue("user.username", null, null, ValidatorMessages.getString("UserRegistrationValidator.10"));
		} else {
			if (!userService.isUniqueUserName(user.getUsername())) {
				if (userService.getUserIdByUsername(user.getUsername()) != user.getId()) {
					errors.rejectValue("user.username", null, null, ValidatorMessages.getString("UserRegistrationValidator.11"));
				}
			}
		}

		// Password
		if(user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			errors.rejectValue("user.password", null, null, ValidatorMessages.getString("UserRegistrationValidator.12"));
		} else {
			String password2 = userRegistration.getReEnteredPassword();
			if(password2.isEmpty()) {
				errors.rejectValue(ValidatorMessages.getString("UserRegistrationValidator.26"), null, null, ValidatorMessages.getString("UserRegistrationValidator.13"));
			} else if(!password2.equals(user.getPassword())){
				errors.rejectValue("reEnteredPassword", null, null, ValidatorMessages.getString("UserRegistrationValidator.14"));
			}
		}

		// First name
		if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()){
			errors.rejectValue("user.firstName", null, null, ValidatorMessages.getString("UserRegistrationValidator.15"));
		} else if(user.getFirstName().trim().length() > 40) {
			errors.rejectValue("user.firstName", null, null, ValidatorMessages.getString("UserRegistrationValidator.16"));
		}

		// Last name
		if (user.getLastName() == null || user.getLastName().trim().isEmpty()){
			errors.rejectValue("user.lastName", null, null, ValidatorMessages.getString("UserRegistrationValidator.17"));
		} else if(user.getLastName().trim().length() > 40) {
			errors.rejectValue("user.lastName", null, null, ValidatorMessages.getString("UserRegistrationValidator.18"));
		}

		// Middle name
		if (user.getMiddleName() == null || user.getMiddleName().trim().isEmpty()){
			errors.rejectValue("user.middleName", null, null, ValidatorMessages.getString("UserRegistrationValidator.19"));
		} else if(user.getMiddleName().trim().length() > 40) {
			errors.rejectValue("user.middleName", null, null, ValidatorMessages.getString("UserRegistrationValidator.20"));
		}

		// Address
		if (user.getAddress() == null || user.getAddress().trim().isEmpty()){
			errors.rejectValue("user.address", null, null, ValidatorMessages.getString("UserRegistrationValidator.21"));
		} else if(user.getAddress().trim().length() > 150) {
			errors.rejectValue("user.address", null, null, ValidatorMessages.getString("UserRegistrationValidator.22"));
		}

		// Contact Number
		if (user.getContactNumber() == null || user.getContactNumber().trim().isEmpty()){
			errors.rejectValue("user.contactNumber", null, null, ValidatorMessages.getString("UserRegistrationValidator.23"));
		} else if(user.getContactNumber().trim().length() > 20) {
			errors.rejectValue("user.contactNumber", null, null, ValidatorMessages.getString("UserRegistrationValidator.24"));
		} else if(user.getContactNumber() != null && !user.getContactNumber().trim().isEmpty()){
			if (!StringFormatUtil.isNumeric(user.getContactNumber()))
				errors.rejectValue("user.contactNumber", null, null, ValidatorMessages.getString("UserRegistrationValidator.25"));
		}

		// Birthdate
		if (user.getBirthDate() == null) {
			errors.rejectValue("user.birthDate", null, null, ValidatorMessages.getString("UserRegistrationValidator.27"));
		}
	}

}
