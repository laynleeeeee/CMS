package eulap.eb.validator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.UserCompany;
import eulap.eb.domain.hibernate.UserCompanyHead;
import eulap.eb.service.CompanyService;
import eulap.eb.service.UserCompanyService;

/**
 * Validation class for User Company.

 */
@Service
public class UserCompanyValidator implements Validator {
	private static Logger logger = Logger.getLogger(UserCompanyValidator.class);
	@Autowired
	private UserCompanyService userCompanyService;
	@Autowired
	private CompanyService companyService;

	@Override
	public boolean supports(Class<?> clazz) {
		return UserCompanyHead.class.equals(clazz);
	}


	@Override
	public void validate(Object object, Errors errors) {
		UserCompanyHead userCompanyHead = (UserCompanyHead) object;

		logger.debug("Validating the name: " + userCompanyHead.getUserName());

		if(userCompanyHead.getUserName() == null) {
			errors.rejectValue("userName", null, null, ValidatorMessages.getString("UserCompanyValidator.0"));
		} else if (userCompanyHead.getUserName().toString().trim().isEmpty()) {
			errors.rejectValue("userName", null, null, ValidatorMessages.getString("UserCompanyValidator.1"));
		}

		if(userCompanyHead.getUserCompanies().isEmpty()){
			errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("UserCompanyValidator.2"));
		} else if (userCompanyService.hasDuplicateCompany(userCompanyHead.getUserCompanies())) {
			errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("UserCompanyValidator.3"));
		}

		for (UserCompany uc : userCompanyHead.getUserCompanies()) {
			Company company = companyService.getCompany(uc.getCompanyId());
			if (company != null && !company.isActive()) {
				errors.rejectValue("errorMessage", null, null, company.getName()+ValidatorMessages.getString("UserCompanyValidator.4"));
				break;
			}
		}
	}
}
