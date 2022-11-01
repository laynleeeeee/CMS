package eulap.eb.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.service.PayrollTimePeriodService;

/**
 * Validator class for {@link PayrollTimePeriod}

 *
 */
@Service
public class PayrollTimePeriodValidator implements Validator {
	@Autowired
	private PayrollTimePeriodService payrollTimePeriodService;

	@Override
	public boolean supports(Class<?> clazz) {
		return PayrollTimePeriod.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		PayrollTimePeriod payrollTimePeriod = (PayrollTimePeriod) obj;

		if(payrollTimePeriod.getName() == null || payrollTimePeriod.getName().trim().isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("PayrollTimePeriodValidator.0"));
		} else if(payrollTimePeriod.getName().trim().length() > PayrollTimePeriod.MAX_HEADER_NAME) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("PayrollTimePeriodValidator.1") 
					+ PayrollTimePeriod.MAX_HEADER_NAME + ValidatorMessages.getString("PayrollTimePeriodValidator.2"));
		} else if(!payrollTimePeriodService.isUniqueName(payrollTimePeriod)) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("PayrollTimePeriodValidator.3"));
		}

		if(!payrollTimePeriodService.isUniqueMonthAndYearCombi(payrollTimePeriod)){
			errors.rejectValue("month", null, null, ValidatorMessages.getString("PayrollTimePeriodValidator.4"));
		}

		List<PayrollTimePeriodSchedule> payrollTimePeriodSchedules =
				payrollTimePeriod.getPayrollTimePeriodSchedules();
		if(payrollTimePeriodSchedules != null && !payrollTimePeriodSchedules.isEmpty()) {
			for (PayrollTimePeriodSchedule ptps : payrollTimePeriodSchedules) {
				if(ptps.getName() == null || ptps.getName().trim().isEmpty()) {
					errors.rejectValue("payrollTimePeriodSchedules", null, null, ValidatorMessages.getString("PayrollTimePeriodValidator.5"));
					break;
				} else if(ptps.getName().trim().length() > PayrollTimePeriod.MAX_LINE_NAME) {
					errors.rejectValue("payrollTimePeriodSchedules", null, null, ValidatorMessages.getString("PayrollTimePeriodValidator.6")
							+ PayrollTimePeriod.MAX_LINE_NAME + ValidatorMessages.getString("PayrollTimePeriodValidator.7"));
					break;
				}

				boolean evaluateDateFromAndTo = true;
				if (ptps.getDateFrom() == null) {
					errors.rejectValue("payrollTimePeriodSchedules", null, null,
							ValidatorMessages.getString("PayrollTimePeriodValidator.8"));
					evaluateDateFromAndTo = false;
					break;
				}

				if (ptps.getDateTo() == null ){
					errors.rejectValue("payrollTimePeriodSchedules", null, null,
							ValidatorMessages.getString("PayrollTimePeriodValidator.9"));
					evaluateDateFromAndTo = false;
					break;
				}
				if (evaluateDateFromAndTo) {
					if (ptps.getDateFrom().after(ptps.getDateTo())) {
						errors.rejectValue("payrollTimePeriodSchedules", null, null,
								ValidatorMessages.getString("PayrollTimePeriodValidator.10"));
						break;
					}
				}
			}
			checkUsedSchedule(payrollTimePeriod.getId(), payrollTimePeriodSchedules, errors);
			if(payrollTimePeriodService.hasDuplicatePayrollTimePeriodShedName(payrollTimePeriodSchedules)){
				errors.rejectValue("payrollTimePeriodSchedules", null, null,
						ValidatorMessages.getString("PayrollTimePeriodValidator.11"));
			}
			if (payrollTimePeriodService.isInvalidTimePeriod(payrollTimePeriodSchedules)) {
				errors.rejectValue("payrollTimePeriodSchedules", null, null,
						ValidatorMessages.getString("PayrollTimePeriodValidator.12"));
			}
			if (payrollTimePeriodService.hasNoContribuition(payrollTimePeriodSchedules)) {
				errors.rejectValue("payrollTimePeriodSchedules", null, null, ValidatorMessages.getString("PayrollTimePeriodValidator.13"));
			}
		} else {
			errors.rejectValue("payrollTimePeriodSchedules", null,
					null, ValidatorMessages.getString("PayrollTimePeriodValidator.14"));
		}
	}

	private void checkUsedSchedule (int pId, List<PayrollTimePeriodSchedule>  schedules,
			Errors errors) {
		if (pId == 0) {
			return;
		}
		List<PayrollTimePeriodSchedule> toBeDeleted = 
				new ArrayList<>(payrollTimePeriodService.getPayrollTimePeriodSchedules(pId));
		toBeDeleted.removeAll(schedules);
		if (!toBeDeleted.isEmpty()) {
			for (PayrollTimePeriodSchedule tbd : toBeDeleted) {
				if (payrollTimePeriodService.isUsed(tbd)) {
					errors.rejectValue("payrollTimePeriodSchedules", null, null,
							ValidatorMessages.getString("PayrollTimePeriodValidator.15") + tbd.getName() + ValidatorMessages.getString("PayrollTimePeriodValidator.16"));
				}
			}
			toBeDeleted = null;
		}
	}

}
