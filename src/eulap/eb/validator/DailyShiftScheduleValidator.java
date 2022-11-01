package eulap.eb.validator;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.DateUtil;
import eulap.eb.dao.EmployeeRequestDao;
import eulap.eb.dao.OvertimeDetailDao;
import eulap.eb.dao.PayrollTimePeriodDao;
import eulap.eb.dao.PayrollTimePeriodScheduleDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.DailyShiftSchedule;
import eulap.eb.domain.hibernate.EmployeeRequest;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.OvertimeDetail;
import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DailyShiftScheduleService;
import eulap.eb.service.EmployeeShiftService;
import eulap.eb.web.dto.DSSLineDto;
import eulap.eb.web.dto.DailyShiftScheduleDto;

/**
 * Validator class for {@link DailyShiftSchedule}

 *
 */
@Service
public class DailyShiftScheduleValidator implements Validator {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DailyShiftScheduleService scheduleService;
	@Autowired
	private PayrollTimePeriodScheduleDao payrollTimePeriodScheduleDao;
	@Autowired
	private PayrollTimePeriodDao payrollTimePeriodDao;
	@Autowired
	private EmployeeShiftService employeeShiftService;
	@Autowired
	private EmployeeRequestDao employeeRequestDao;
	@Autowired
	private OvertimeDetailDao overtimeDetailDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return DailyShiftSchedule.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		validate(obj, errors, "");
	}

	public void validate(Object obj, Errors errors, String fieldPrepend) {
		DailyShiftSchedule shiftSchedule = (DailyShiftSchedule) obj;

		//Company
		if (shiftSchedule.getCompanyId() == null) {
			errors.rejectValue(fieldPrepend+"companyId", null, null, ValidatorMessages.getString("DailyShiftScheduleValidator.0"));
		} else {
			Company company = companyService.getCompany(shiftSchedule.getCompanyId());
			if (!company.isActive()) {
				errors.rejectValue(fieldPrepend+"companyId", null, null, ValidatorMessages.getString("DailyShiftScheduleValidator.1"));
			}
		}

		if (shiftSchedule.getMonth() == null || shiftSchedule.getYear() == null) {
			errors.rejectValue(fieldPrepend+"payrollTimePeriodScheduleId", null, null, ValidatorMessages.getString("DailyShiftScheduleValidator.2"));
		}

		if (shiftSchedule.getPayrollTimePeriodScheduleId() == null) {
			errors.rejectValue(fieldPrepend+"payrollTimePeriodScheduleId", null, null, ValidatorMessages.getString("DailyShiftScheduleValidator.3"));
		} else {
			PayrollTimePeriodSchedule periodSchedule = payrollTimePeriodScheduleDao.get(shiftSchedule.getPayrollTimePeriodScheduleId());
			PayrollTimePeriod payrollTimePeriod= payrollTimePeriodDao.get(periodSchedule.getPayrollTimePeriodId());
			if (!payrollTimePeriod.isActive()) {
				errors.rejectValue(fieldPrepend+"payrollTimePeriodScheduleId", null, null, ValidatorMessages.getString("DailyShiftScheduleValidator.7"));
			}
		}

		if (shiftSchedule.getScheduleSheetDtos() == null || shiftSchedule.getScheduleSheetDtos().isEmpty()) {
			errors.rejectValue(fieldPrepend+"scheduleSheetDtos", null, null, ValidatorMessages.getString("DailyShiftScheduleValidator.4"));
		} else {
			List<DailyShiftScheduleDto> dailyShiftScheduleDtos = shiftSchedule.getScheduleSheetDtos();
			List<DSSLineDto> dssLineDtos = null;
			EmployeeShift employeeShift = null;
			for (DailyShiftScheduleDto dailyShiftScheduleDto : dailyShiftScheduleDtos) {
				dssLineDtos = dailyShiftScheduleDto.getDssLineDtos();
				if (dssLineDtos != null && !dssLineDtos.isEmpty()) {
					for (DSSLineDto lineDto : dssLineDtos) {
						Date dsslDate = lineDto.getDate();
						Integer employeeShiftId = lineDto.getEmployeeShiftId();
						if (lineDto.getEmployeeShiftId() != null) {
							employeeShift = employeeShiftService.getEmployeeShift(employeeShiftId);
							if (employeeShift != null) {
								if (!employeeShift.isActive()) {
									String errorMsg = ValidatorMessages.getString("DailyShiftScheduleValidator.8");
									errors.rejectValue(fieldPrepend+"scheduleSheetDtos", null, null,
											String.format(errorMsg, dailyShiftScheduleDto.getEmployeeName()));
								}

								if (!employeeShiftId.equals(lineDto.getOrigEmployeeShiftId())) {
									String hasRequestedOvertime = hasOverlappingDates(dsslDate,
											employeeShift.getFirstHalfShiftStart(), employeeShift.getSecondHalfShiftEnd(),
											null, dailyShiftScheduleDto.getEmployeeId());
									if (!hasRequestedOvertime.isEmpty()) {
										String strDate = DateUtil.formatDate(dsslDate);
										errors.rejectValue(fieldPrepend+"scheduleSheetDtos", null, null,
												"The following request for overtime form created for the date of "
														+ strDate + " - " + hasRequestedOvertime);
									}
								}
							}
						} else {
							String errorMsg = ValidatorMessages.getString("DailyShiftScheduleValidator.5");
							errors.rejectValue(fieldPrepend+"scheduleSheetDtos", null, null,
									String.format(errorMsg, dailyShiftScheduleDto.getEmployeeName()));
							break;
						}
					}
				}
			}
			dssLineDtos = null;
		}

		if (!scheduleService.isUniqueSchedule(shiftSchedule)) {
			errors.rejectValue(fieldPrepend+"payrollTimePeriodScheduleId", null, null, ValidatorMessages.getString("DailyShiftScheduleValidator.6"));
		}
	}

	private String hasOverlappingDates(Date overtimeDate, String startTime, String endTime, Integer employeeRequestId, Integer employeeId) {
		if (overtimeDetailDao.hasRequestedOvertime(employeeRequestId, employeeId, overtimeDate)) {
			List<OvertimeDetail> overtimeDetails = overtimeDetailDao.getAllByEmployeeAndDate(employeeId, overtimeDate, false);
			EmployeeRequest employeeRequest = null;
			int counter = 0;
			String employeeRequestNo = "";
			for (OvertimeDetail od : overtimeDetails) {
				Date savedOTStart = DateUtil.appendTimeToDate(od.getStartTime(), overtimeDate);
				Date savedOTEnd = DateUtil.appendTimeToDate(od.getEndTime(), overtimeDate);
				Date startDate = DateUtil.appendTimeToDate(startTime, overtimeDate);
				Date endDate = DateUtil.appendTimeToDate(endTime, overtimeDate);
				if (checkInBetweenDate(startDate, savedOTStart, savedOTEnd)
						|| checkInBetweenDate(endDate, savedOTStart, savedOTEnd)) {
					employeeRequest = employeeRequestDao.get(od.getEmployeeRequestId());
					if (counter > 0) {
						employeeRequestNo += ", ROT #" + employeeRequest.getSequenceNo();
					} else {
						employeeRequestNo += "ROT #" + employeeRequest.getSequenceNo();
					}
					employeeRequest = null;
				}
				counter++;
			}
			overtimeDetails = null;
			return employeeRequestNo;
		}
		return "";
	}

	private boolean checkInBetweenDate(Date date, Date startDate, Date endDate) {
		return date.after(startDate) && date.before(endDate);
	}
}
