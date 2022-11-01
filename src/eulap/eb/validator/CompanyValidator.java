package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.EmailFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;

/**
 * Class that handles the validation of the company when saving.


 */
@Service
public class CompanyValidator implements Validator {
	@Autowired
	private CompanyService companyService;
	
	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Company.class.equals(clazz);
	}
	
	@Override
	public void validate(Object arg0, Errors arg1) {
		// Do nothing
	}
	
	public void validate(Object obj, User user, Errors errors) {
		Company company = (Company) obj;

		if (company.getCompanyNumber() == null || company.getCompanyNumber().trim().isEmpty()){
			errors.rejectValue("companyNumber", null, null, ValidatorMessages.getString("CompanyValidator.0"));
		} else {
			if (!StringFormatUtil.isNumeric(company.getCompanyNumber())){
				errors.rejectValue("companyNumber", null, null, ValidatorMessages.getString("CompanyValidator.1"));
			}else{
				if (!companyService.isUnique(company, 1, user))
					errors.rejectValue("companyNumber", null, null, ValidatorMessages.getString("CompanyValidator.2"));				
			}
		}

		if (company.getName() == null || company.getName().trim().isEmpty()){
			errors.rejectValue("name", null, null, ValidatorMessages.getString("CompanyValidator.3"));
		} else {
			if (StringFormatUtil.containsInvalidChar(company.getName(), StringFormatUtil.PERCENT_CHAR,
					StringFormatUtil.UNDERSCORE_CHAR)){
				errors.rejectValue("name", null, null, ValidatorMessages.getString("CompanyValidator.4"));	
			}else{
				if (!companyService.isUnique(company, 2, user)){	
					errors.rejectValue("name", null, null, ValidatorMessages.getString("CompanyValidator.5"));
				}
			}
		}

		if (company.getCompanyCode() == null || company.getCompanyCode().trim().isEmpty()){
			errors.rejectValue("companyCode", null, null, ValidatorMessages.getString("CompanyValidator.6"));
		} else {
			if (StringFormatUtil.containsInvalidChar(company.getCompanyCode(), StringFormatUtil.PERCENT_CHAR,
					StringFormatUtil.UNDERSCORE_CHAR)){
				errors.rejectValue("companyCode", null, null, ValidatorMessages.getString("CompanyValidator.7"));	
			}else{
				if (!companyService.isUnique(company, 4, user)){	
					errors.rejectValue("companyCode", null, null, ValidatorMessages.getString("CompanyValidator.8"));
				}
			}
		}

		if (company.getAddress() == null || company.getAddress().trim().isEmpty()) {
			errors.rejectValue("address", null, null, ValidatorMessages.getString("CompanyValidator.9"));
		}else{
			if (StringFormatUtil.containsInvalidChar(company.getAddress(), StringFormatUtil.PERCENT_CHAR,
					StringFormatUtil.UNDERSCORE_CHAR))
					errors.rejectValue("address", null, null, ValidatorMessages.getString("CompanyValidator.10"));
		}

		if (!company.getTin().trim().isEmpty() && company.getTin() != null) {
			if (!StringFormatUtil.isValidTIN(company.getTin())){
				errors.rejectValue("tin", null, null, ValidatorMessages.getString("CompanyValidator.11"));
			}else {
				if (!companyService.isUnique(company, 3, user))
					errors.rejectValue("tin", null, null, ValidatorMessages.getString("CompanyValidator.12"));
			}
		}

		if (company.getContactNumber() != null && !company.getContactNumber().trim().isEmpty()){
			if (!StringFormatUtil.isNumeric(company.getContactNumber()))
				errors.rejectValue("contactNumber", null, null, ValidatorMessages.getString("CompanyValidator.13"));
		}

		if (company.getEmailAddress() != null || !company.getAddress().trim().isEmpty()){
			if (!EmailFormatUtil.validate(company.getEmailAddress()))
				errors.rejectValue("emailAddress", null, null, ValidatorMessages.getString("CompanyValidator.14"));
		}
	}
}