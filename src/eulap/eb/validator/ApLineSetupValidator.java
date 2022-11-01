package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApLineSetup;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.ApLineSetupService;

/**
 * Validation class for AP Line Setup.

 *
 */
@Service
public class ApLineSetupValidator implements Validator {
	@Autowired
	private ApLineSetupService apLineSetupService;
	@Autowired
	private AccountCombinationService acctCombiService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ApLineSetup.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ApLineSetup apLineSetup = (ApLineSetup) obj;

		//Name
		if (apLineSetup.getName() == null || apLineSetup.getName().trim().isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ApLineSetupValidator.0"));
		} else if (apLineSetup.getName().trim().length() > 100) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ApLineSetupValidator.1"));
		} else if (!apLineSetupService.isUniqueName(apLineSetup.getId(), apLineSetup.getName(),
				apLineSetup.getCompanyId(), apLineSetup.getDivisionId())) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ApLineSetupValidator.2"));
		}

		if (apLineSetup.getCompanyId() == null) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("ApLineSetupValidator.3"));
		}

		if (apLineSetup.getDivisionId() == null) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ApLineSetupValidator.4"));
		}

		if (apLineSetup.getAccountId() == null) {
			errors.rejectValue("accountId", null, null, ValidatorMessages.getString("ApLineSetupValidator.5"));
		}

		//Account Combination
		if (apLineSetup.getCompanyId() != null && apLineSetup.getDivisionId() != null && apLineSetup.getAccountId() != null) {
			AccountCombination acctCombi = acctCombiService.getAccountCombination(apLineSetup.getCompanyId(),
					apLineSetup.getDivisionId(), apLineSetup.getAccountId());
			if (acctCombi == null) {
				errors.rejectValue("accountCombinationId", null, null, ValidatorMessages.getString("ApLineSetupValidator.6"));
			} else if (!acctCombi.isActive()) {
				errors.rejectValue("accountCombinationId", null, null, ValidatorMessages.getString("ApLineSetupValidator.7"));
			} else {
				if (!acctCombi.getCompany().isActive()) {
					errors.rejectValue("companyId", null, null, ValidatorMessages.getString("ApLineSetupValidator.8"));
				}

				if (!acctCombi.getDivision().isActive()) {
					errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ApLineSetupValidator.9"));
				}

				if (!acctCombi.getAccount().isActive()) {
					errors.rejectValue("accountId", null, null, ValidatorMessages.getString("ApLineSetupValidator.10"));
				}
			}
		}
	}
}
