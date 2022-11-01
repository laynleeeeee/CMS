package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.service.TermService;
/**
 * A class that handles the validation of term.

 */
@Service
public class TermValidator implements Validator{
	@Autowired
	private TermService termService;

	@Override
	public boolean supports(Class<?> obj) {
		return Term.class.equals(obj);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Term term = (Term) obj;
		if(term.getName() == null || term.getName().trim().isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("TermValidator.0"));
		}else{
			if(!termService.isUniqueTerm(term))
				errors.rejectValue("name", null, null, ValidatorMessages.getString("TermValidator.2"));
			if(term.getName().length() > Term.MAX_TERM_NAME)
				errors.rejectValue("name", null, null, 
						String.format(ValidatorMessages.getString("TermValidator.3"), Term.MAX_TERM_NAME));
		}

		if(term.getDays() == null)
			errors.rejectValue("days", null, null, ValidatorMessages.getString("TermValidator.4"));
	}
}
