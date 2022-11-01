package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.service.UnitMeasurementService;

/**
 * Class that handles validation of Unit Measurement

 */
@Service
public class UnitMeasurementValidator implements Validator{
	@Autowired
	private UnitMeasurementService unitMeasurementService;

	@Override
	public boolean supports(Class<?> clazz) {
		return UnitMeasurement.class.equals(clazz);
	}

	@Override
	public void validate(Object arg0, Errors arg1) {
		throw new RuntimeException(ValidatorMessages.getString("UnitMeasurementValidator.0"));
	}

	public void validate(Object target, Errors error, int unitMeasurementId) {
		UnitMeasurement unitMeasurement = (UnitMeasurement) target;
		// Name
		if (unitMeasurement.getName() == null || unitMeasurement.getName().trim().isEmpty()){
			error.rejectValue("name", null, null, ValidatorMessages.getString("UnitMeasurementValidator.1"));
		}
		else if (!unitMeasurementService.isUnique(unitMeasurement, unitMeasurementId)){
			error.rejectValue("name", null, null, ValidatorMessages.getString("UnitMeasurementValidator.2"));
		}
		else if (unitMeasurement.getName().trim().length() > 20 ){
			error.rejectValue("name", null, null, ValidatorMessages.getString("UnitMeasurementValidator.3"));
		}
	}
}
