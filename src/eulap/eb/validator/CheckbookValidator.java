package eulap.eb.validator;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import eulap.eb.domain.hibernate.Checkbook;
import eulap.eb.service.CheckbookService;
/**
 * A class that handles data validation for {@link Checkbook}.

 */
@Service
public class CheckbookValidator implements Validator{
	@Autowired
	private CheckbookService checkbookService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Checkbook.class.equals(clazz);
	}

	public void validate(Object target, Errors error, int bankAccountId) {
		Checkbook checkbook = (Checkbook) target;
		if(checkbook.getBankAccountId() == 0)
			error.rejectValue("bankAccountId", null, null, ValidatorMessages.getString("CheckbookValidator.0"));
		if(checkbook.getName() == null || checkbook.getName().trim().isEmpty())
			error.rejectValue("name", null, null, ValidatorMessages.getString("CheckbookValidator.1"));
		else if (!checkbookService.isUnique(checkbook, bankAccountId))
			error.rejectValue("name", null, null, ValidatorMessages.getString("CheckbookValidator.2"));
		if(checkbook.getCheckbookNoFrom() == null) {
			error.rejectValue("checkbookNoFrom", null, null, ValidatorMessages.getString("CheckbookValidator.10"));
		} else if (checkbook.getCheckbookNoFrom().compareTo(BigDecimal.ZERO) == 0) {
			error.rejectValue("checkbookNoFrom", null, null, ValidatorMessages.getString("CheckbookValidator.3"));
		}

		if(checkbook.getCheckbookNoTo() == null) {
			error.rejectValue("checkbookNoTo", null, null, ValidatorMessages.getString("CheckbookValidator.11"));
		} else if (checkbook.getCheckbookNoTo().compareTo(BigDecimal.ZERO) == 0) {
			error.rejectValue("checkbookNoTo", null, null, ValidatorMessages.getString("CheckbookValidator.4"));
		} else if(checkbook.getCheckbookNoFrom().compareTo(checkbook.getCheckbookNoTo()) > 0) {
			error.rejectValue("checkbookNoFrom", null, null, ValidatorMessages.getString("CheckbookValidator.5"));
		} else {
			String strCheckNoFrom = checkbook.getCheckbookNoFrom().toPlainString();
			if(strCheckNoFrom.length() > 20) {
				error.rejectValue("checkbookNoFrom", null, null,
						ValidatorMessages.getString("CheckbookValidator.6"));
			} else {
				String strCheckNoTo = checkbook.getCheckbookNoTo().toPlainString();
				if(strCheckNoTo.length() > 20) {
					error.rejectValue("checkbookNoTo", null, null,
							ValidatorMessages.getString("CheckbookValidator.7"));
				}
			}
		}
		if(!checkbookService.isValidSeries(checkbook)) {
			error.rejectValue("checkbookNoTo", null, null, ValidatorMessages.getString("CheckbookValidator.8"));
		}
	}

	@Override
	public void validate(Object obj, Errors errors) {
		throw new RuntimeException(ValidatorMessages.getString("CheckbookValidator.9"));
	}
}