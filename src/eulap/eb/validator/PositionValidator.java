package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.Position;
import eulap.eb.service.PositionService;

/**
 * Validator for {@link Position}

 */
@Service
public class PositionValidator implements Validator{
	@Autowired
	private PositionService posService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Position.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		Position position = (Position) object;
		
		if (position.getName() == null || position.getName().trim().isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("PositionValidator.0"));
		} else if(position.getName().trim().length() > 40) {
			errors.rejectValue("name", null, null,
					ValidatorMessages.getString("PositionValidator.1")+ Position.MAX_CHAR_NAME +ValidatorMessages.getString("PositionValidator.2"));
		} else if (!posService.isUnique(position))
			errors.rejectValue("name", null, null, ValidatorMessages.getString("PositionValidator.3"));
	}
}
