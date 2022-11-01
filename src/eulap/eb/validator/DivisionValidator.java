package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.DivisionService;

/**
 * Division Validator.

 */
@Service
public class DivisionValidator implements Validator{
	@Autowired
	private DivisionService divisionService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Division.class.equals(clazz);
	}

	@Override
	public void validate(Object arg0, Errors arg1) {
		// Do nothing.
	}
	
	public void validate(Object target, Errors errors, User user) {
		Division division = (Division) target;
		// Number
		if (validate("number", "Number", division.getNumber(), 5, errors) &&  
				!divisionService.isUnique(division, 1, user))
			errors.rejectValue("number", null, null, ValidatorMessages.getString("DivisionValidator.0"));
		if (division.getNumber() != null && !division.getNumber().trim().isEmpty() && 
				!StringFormatUtil.isNumeric(division.getNumber().trim())) {
			errors.rejectValue("number", null, null, ValidatorMessages.getString("DivisionValidator.1"));
		}
		// Name
		if (validate("name", "Name", division.getName(), 100, errors) &&
				!divisionService.isUnique(division, 2, user))
			errors.rejectValue("name", null, null, ValidatorMessages.getString("DivisionValidator.2"));
		// Description
		validate("description", "Description", division.getDescription(), 200, errors);

		String parentDivisionName = division.getParentDivisionName();
		Integer parentDivisionId = division.getParentDivisionId();
		if (parentDivisionId == null && (parentDivisionName != null && !parentDivisionName.trim().isEmpty())) {
			errors.rejectValue("parentDivisionId", null, null, ValidatorMessages.getString("DivisionValidator.7"));
		}
	}

	private boolean validate (String fieldName, String fieldLabel, String value, int maxLength, Errors errors ) {
		if(value == null || value.trim().isEmpty()){
			errors.rejectValue(fieldName, null, null, fieldLabel + " is a required field");
			return false;
		}else if(StringFormatUtil.containsInvalidChar(value,
			StringFormatUtil.PERCENT_CHAR, StringFormatUtil.UNDERSCORE_CHAR)) {
				errors.rejectValue(fieldName, null, null, fieldLabel+ValidatorMessages.getString("DivisionValidator.3") +
						ValidatorMessages.getString("DivisionValidator.4"));
				return false;
		} else if(value.length() > maxLength) {
				errors.rejectValue(fieldName, null, null, fieldLabel +ValidatorMessages.getString("DivisionValidator.5") + maxLength + ValidatorMessages.getString("DivisionValidator.6"));
				return false;
		}
		return true;
	}
}