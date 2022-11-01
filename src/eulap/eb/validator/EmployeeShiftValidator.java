package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.TimeFormatUtil;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EmployeeShiftService;

/**
 * Validator class for {@link EmployeeShift}

 *
 */

@Service
public class EmployeeShiftValidator implements Validator{
	@Autowired
	private EmployeeShiftService employeeShiftService;
	@Autowired
	private CompanyService companyService;

	@Override
	public boolean supports(Class<?> clazz) {
		return EmployeeShift.class.equals(clazz);
	}
	@Override
	public void validate(Object obj, Errors errors) {
		validate(obj,errors,false);
	}

	public void validate(Object obj, Errors errors, boolean isAllowZeroBreakTime) {
		EmployeeShift employeeShift = (EmployeeShift) obj;

		// Company
		if(employeeShift.getCompanyId() == null) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("EmployeeShiftValidator.0"));
		} else if(!companyService.getCompany(employeeShift.getCompanyId()).isActive()) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("EmployeeShiftValidator.1"));
		}

		// Name
		if(employeeShift.getName() == null || employeeShift.getName().trim().isEmpty()){
			errors.rejectValue("name", null, null,
					ValidatorMessages.getString("EmployeeShiftValidator.2"));
		} else if (employeeShift.getName().trim().length() > EmployeeShift.MAX_NAME) {
			errors.rejectValue("name", null, null,
					ValidatorMessages.getString("EmployeeShiftValidator.3") + EmployeeShift.MAX_NAME + ValidatorMessages.getString("EmployeeShiftValidator.4"));
		} else if (!employeeShiftService.isUniqueName(employeeShift)) {
			errors.rejectValue("name", null, null,
					ValidatorMessages.getString("EmployeeShiftValidator.5"));
		}

		// First Half Start Shift
		if(employeeShift.getFirstHalfShiftStart() == null || employeeShift.getFirstHalfShiftStart().trim().isEmpty()){
			errors.rejectValue("firstHalfShiftStart", null, null,
					ValidatorMessages.getString("EmployeeShiftValidator.6"));
		}else if (!TimeFormatUtil.isMilitaryTime(employeeShift.getFirstHalfShiftStart())) {
			errors.rejectValue("firstHalfShiftStart", null, null,
					ValidatorMessages.getString("EmployeeShiftValidator.7"));
		}

		// Second Half end Shift
		if(employeeShift.getSecondHalfShiftEnd() == null || employeeShift.getSecondHalfShiftEnd().trim().isEmpty()){
			errors.rejectValue("secondHalfShiftEnd", null, null,
					ValidatorMessages.getString("EmployeeShiftValidator.8"));
		}else if (!TimeFormatUtil.isMilitaryTime(employeeShift.getSecondHalfShiftEnd())) {
			errors.rejectValue("secondHalfShiftEnd", null, null,
					ValidatorMessages.getString("EmployeeShiftValidator.9"));
		}

		// Daily Working Hours.
		if(employeeShift.getDailyWorkingHours() == null || employeeShift.getDailyWorkingHours() == 0) {
			errors.rejectValue("dailyWorkingHours", null, null, ValidatorMessages.getString("EmployeeShiftValidator.10"));
		} else if(employeeShift.getDailyWorkingHours() > EmployeeShift.MAX_HOUR){
			errors.rejectValue("dailyWorkingHours", null, null, ValidatorMessages.getString("EmployeeShiftValidator.11") +
					EmployeeShift.MAX_HOUR + ValidatorMessages.getString("EmployeeShiftValidator.12"));
		}
		// Allowable Break Time
		if(!isAllowZeroBreakTime) {
			if(employeeShift.getAllowableBreak() < 0) {
				errors.rejectValue("allowableBreak", null, null, ValidatorMessages.getString("EmployeeShiftValidator.16"));
			}
		}
		else {
			if (employeeShift.getAllowableBreak() <= 0) {
				errors.rejectValue("allowableBreak", null, null, ValidatorMessages.getString("EmployeeShiftValidator.13"));
			}
		}
		if (employeeShift.getAllowableBreak() != null) {
				if (employeeShift.getAllowableBreak() > EmployeeShift.MAX_HOUR) {
			errors.rejectValue("allowableBreak", null, null, ValidatorMessages.getString("EmployeeShiftValidator.14") +
					EmployeeShift.MAX_HOUR + ValidatorMessages.getString("EmployeeShiftValidator.15"));
		}
	}
}
	}
