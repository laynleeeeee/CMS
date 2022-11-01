package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
/**
 * Account combination validator

 */
@Service
public class AccountCombinationValidator implements Validator {
	@Autowired
	private AccountCombinationService accountCombinationService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;

	@Override
	public boolean supports(Class<?> clazz) {
		return AccountCombinationValidator.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		AccountCombination accountCombination = (AccountCombination) object;
		if (!accountCombinationService.isValidCombination(accountCombination.getCompanyId(),
				accountCombination.getDivisionId(), accountCombination.getAccountId()))
			errors.rejectValue("combination", null, null, ValidatorMessages.getString("AccountCombinationValidator.0"));
		if (!accountCombinationService.isUniqueAccountCombination(accountCombination))
			errors.rejectValue("combination", null, null, ValidatorMessages.getString("AccountCombinationValidator.1"));

		ValidatorUtil.checkInactiveCompany(companyService, accountCombination.getCompanyId(), "companyId", errors, null);
		ValidatorUtil.checkInactiveDivision(divisionService, accountCombination.getDivisionId(), "divisionId", errors, null);
		ValidatorUtil.checkInactiveAccount(accountService, accountCombination.getAccountId(), "accountId", errors, null);
	}

}
