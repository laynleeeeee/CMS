package eulap.eb.validator;

import org.springframework.validation.Errors;

import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;

/**
 * 

 *
 */
public class ValidatorUtil {

	/**
	 * Validate the company selected in the forms.
	 */
	public static void validateCompany(Integer companyId, CompanyService companyService,
			Errors errors, String attribName) {
		if (companyId == null) {
			errors.rejectValue(attribName, null, null, ValidatorMessages.getString("CommonValidtor.0"));
		} else {
			checkInactiveCompany(companyService, companyId, attribName, errors, ValidatorMessages.getString("CommonValidtor.1"));
		}
	}

	public static void checkInactiveCompany (CompanyService companyService, int companyId, String attrName, Errors error, 
			String customMessage) {
		Company company = companyService.getCompany(companyId);
		if (company != null && !company.isActive()) {
			error.rejectValue(attrName, null, null, customMessage == null ? company.getName()+" "+ValidatorMessages.getString("CommonValidtor.2") : customMessage);
		}
	}

	public static void checkInactiveDivision (DivisionService divisionService,  int divisionId, String attrName, Errors error, 
			String customMessage) {
		Division division = divisionService.getDivision(divisionId);
		if (division != null && !division.isActive()) {
			error.rejectValue(attrName, null, null, customMessage == null ? division.getName()+" "+ValidatorMessages.getString("CommonValidtor.2") : customMessage);
		}
	}

	public static void checkInactiveAccount (AccountService accountService, int accountId, String attrName, Errors error, 
			String customMessage) {
		Account account = accountService.getAccount(accountId);
		if (account != null && !account.isActive()) {
			error.rejectValue(attrName, null, null, customMessage == null ? account.getAccountName()+" "+ValidatorMessages.getString("CommonValidtor.2") : customMessage);
		}
	}
}
