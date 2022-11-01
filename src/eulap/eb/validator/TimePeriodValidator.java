package eulap.eb.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.domain.hibernate.TimePeriodStatus;
import eulap.eb.service.TimePeriodService;
import eulap.eb.web.dto.FormDetailsDto;

/**
 * Time Period Validator.

 */
@Service
public class TimePeriodValidator implements Validator{
	@Autowired
	private TimePeriodService timePeriodService;

	@Override
	public boolean supports(Class<?> clazz) {
		return TimePeriod.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		TimePeriod timePeriod = (TimePeriod) target;
		if (timePeriod.getName() == null || timePeriod.getName().isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("TimePeriodValidator.0"));
		} else if (timePeriod.getName().length() > 50) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("TimePeriodValidator.1"));
		} else if(StringFormatUtil.containsInvalidChar(timePeriod.getName(),
				StringFormatUtil.PERCENT_CHAR, StringFormatUtil.UNDERSCORE_CHAR)) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("TimePeriodValidator.2"));
		} else if (!timePeriodService.isUniqueName(timePeriod)) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("TimePeriodValidator.3"));
		}

		boolean evaluateDateFromAndTo = true;
		if (timePeriod.getDateFrom() == null) {
			errors.rejectValue("dateFrom", null, null, ValidatorMessages.getString("TimePeriodValidator.4"));
			evaluateDateFromAndTo = false;
		}

		if (timePeriod.getDateTo() == null ){
			errors.rejectValue("dateTo", null, null, ValidatorMessages.getString("TimePeriodValidator.5"));
			evaluateDateFromAndTo = false;
		}

		if (evaluateDateFromAndTo) {
			if (timePeriod.getDateFrom().after(timePeriod.getDateTo()))
				errors.rejectValue("dateFrom", null, null, ValidatorMessages.getString("TimePeriodValidator.6"));
			else if (!timePeriodService.isValidPeriod(timePeriod))
				errors.rejectValue("dateTo", null, null, ValidatorMessages.getString("TimePeriodValidator.7"));
		}

		if(timePeriod.getPeriodStatusId() == TimePeriodStatus.CLOSED) {
			List<FormDetailsDto> getFormsByTimePeriods =
					timePeriodService.getUnpostedForms(timePeriod.getDateFrom(), timePeriod.getDateTo());
			if(getFormsByTimePeriods != null && !getFormsByTimePeriods.isEmpty()) {
				StringBuilder errorMessage = new StringBuilder(ValidatorMessages.getString("TimePeriodValidator.8")); 
				errors.rejectValue(ValidatorMessages.getString("TimePeriodValidator.17"), null, null, errorMessage.toString()); 
				errorMessage = new StringBuilder(""); 
				int count = 0;
				for (FormDetailsDto tpDto : getFormsByTimePeriods) {
					errorMessage.append(tpDto.getSeqNo()); 
					if(!tpDto.getRefNo().isEmpty()) {
						errorMessage.append(", "+tpDto.getRefNo()); 
					}
					count++;
					if(count == 10) {
						errorMessage.append(" ..."); 
					}
					errors.rejectValue("periodStatusId", null, null, errorMessage.toString()); 
					errorMessage = new StringBuilder(""); 
				}
			}
		}
	}
}