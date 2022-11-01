package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.service.UserGroupService;

/**
 * Class that handles the validation of the user group when saving.

 *
 */
@Service
public class UserGroupValidator implements Validator {
	@Autowired
	private UserGroupService userGroupService;

	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return UserGroup.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		UserGroup userGroup = (UserGroup) obj;

		String name = userGroup.getName();
		if (name == null || name.trim().isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("UserGroupValidator.0"));
		} else if (name.trim().length() > 50) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("UserGroupValidator.1"));
		} else {
			if (StringFormatUtil.containsInvalidChar(userGroup.getName(), StringFormatUtil.PERCENT_CHAR,
					StringFormatUtil.UNDERSCORE_CHAR)) {
				errors.rejectValue("name", null, null, ValidatorMessages.getString("UserGroupValidator.2"));
			} else {
				if (!userGroupService.isUniqueUserGroupName(userGroup.getName())) {
					if (userGroupService.getUserGroupIdByName(userGroup.getName()) != userGroup.getId()) {
						errors.rejectValue("name", null, null, ValidatorMessages.getString("UserGroupValidator.3"));
					}
				}
			}
		}

		String description = userGroup.getDescription();
		if (description == null || description.trim().isEmpty()) {
			errors.rejectValue("description", null, null, ValidatorMessages.getString("UserGroupValidator.4"));
		} else if (description.trim().length() > 150) {
			errors.rejectValue("description", null, null, ValidatorMessages.getString("UserGroupValidator.6"));
		} else {
			if (StringFormatUtil.containsInvalidChar(userGroup.getDescription(), StringFormatUtil.PERCENT_CHAR,
					StringFormatUtil.UNDERSCORE_CHAR)) {
				errors.rejectValue("description", null, null, ValidatorMessages.getString("UserGroupValidator.5"));
			}
		}
	}
}
