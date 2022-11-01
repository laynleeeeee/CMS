package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.service.ArLineSetupService;

/**
 * Validation class for AR Line Setup.

 *
 */
@Service
public class ArLineSetupValidator implements Validator{
	@Autowired
	private ArLineSetupService lineSetupService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ArLineSetup.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ArLineSetup arLine = (ArLineSetup) obj;

		//Name
		if(arLine.getName() == null || arLine.getName().trim().isEmpty())
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ArLineSetupValidator.0"));
		else if(arLine.getName().trim().length() > 100)
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ArLineSetupValidator.1"));
		else if(lineSetupService.hasDuplicate((arLine)))
			errors.rejectValue("name", null, null, ValidatorMessages.getString("ArLineSetupValidator.2"));

		//Account Combination
		if(lineSetupService.getAcctCombi(arLine) == null) {
			errors.rejectValue("arLineMessage", null, null, ValidatorMessages.getString("ArLineSetupValidator.3"));
		} else {
			if(!lineSetupService.isActiveAC(arLine)) {
				errors.rejectValue("arLineMessage", null, null, ValidatorMessages.getString("ArLineSetupValidator.4"));
			}

			//Company
			 if(!lineSetupService.isActiveAcComponent(arLine, 1)) {
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("ArLineSetupValidator.5"));
			 }

			//Division
			if(!lineSetupService.isActiveAcComponent(arLine, 2)) {
				errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ArLineSetupValidator.6"));
			}

			//Account
			if(!lineSetupService.isActiveAcComponent(arLine, 3)) {
				errors.rejectValue("accountId", null, null, ValidatorMessages.getString("ArLineSetupValidator.7"));
			}
		}
		//Discount account combination
		AccountCombination ac = lineSetupService.getAcctCombi(arLine);
		if(ac == null) {
			errors.rejectValue("arLineMessage", null, null, ValidatorMessages.getString("ArLineSetupValidator.3"));
		} else {
			if(!ac.isActive()) {
				errors.rejectValue("arLineMessage", null, null, ValidatorMessages.getString("ArLineSetupValidator.8"));
			}

			//Account
			if(!lineSetupService.isActiveAcComponent(arLine, 3)) {
				errors.rejectValue("discAccId", null, null, ValidatorMessages.getString("ArLineSetupValidator.7"));
			}
		}

		if(arLine.getAmount() == null) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArLineSetupValidator.9"));
		} else if(arLine.getAmount() <= 0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArLineSetupValidator.10"));
		}
	}
}
