package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.DateUtil;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeShiftDao;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.PayrollEmployeeTimeSheet;
import eulap.eb.web.dto.webservice.TimeSheetDetailsDto;
import eulap.eb.web.dto.webservice.TimeSheetDto;

/**
 * Validator for {@link PayrollEmployeeTimeSheet}

 *
 */
@Service
public class PayrollTimeSheetValidator implements Validator {
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private EmployeeShiftDao employeeShiftDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return Payroll.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		Payroll payroll = (Payroll) object;

		double employeeShiftHrs = 0;
		Employee employee = null;
		EmployeeShift shift = null;
		for (TimeSheetDto tsDto : payroll.getTimeSheetDtos()) {
			employee = employeeDao.getEmployeeByNo(tsDto.getEmployeeNo(), payroll.getCompanyId());
			if (employee != null) {
				for (TimeSheetDetailsDto tsdDto : tsDto.getTimeSheetDetailsDtos()) {
					shift = employeeShiftDao.getBySchedule(payroll.getCompanyId(), 
							payroll.getPayrollTimePeriodId(), payroll.getPayrollTimePeriodScheduleId(), 
							employee.getId(), tsdDto.getDate());
					if (shift != null) {
						employeeShiftHrs = shift.getDailyWorkingHours();
						double totalHrsWorked = tsdDto.getHoursWork() + tsdDto.getAdjustment();
						if(employeeShiftHrs < totalHrsWorked) {
							errors.rejectValue("employeeTimeSheets", null, null, ValidatorMessages.getString("PayrollTimeSheetValidator.0")
									+ tsDto.getEmployeeName()
									+ ValidatorMessages.getString("PayrollTimeSheetValidator.1")+DateUtil.formatDate(tsdDto.getDate())
									+ValidatorMessages.getString("PayrollTimeSheetValidator.2"));
						}
					}
				}
			}
		}
	}
}
