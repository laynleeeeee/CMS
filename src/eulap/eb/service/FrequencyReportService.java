package eulap.eb.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeDtrDao;
import eulap.eb.dao.LeaveDetailDao;
import eulap.eb.dao.OvertimeDetailDao;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeDtr;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.LeaveDetail;
import eulap.eb.domain.hibernate.OvertimeDetail;
import eulap.eb.exception.SuppressableStacktraceException;
import eulap.eb.payroll.PayrollDataHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FrequencyReportDto;

/**
 * Service class for Frequency Report.

 *
 */
@Service
public class FrequencyReportService {
	@Autowired
	private EmployeeDtrDao employeeDtrDao;
	@Autowired
	private TimeSheetService timeSheetService;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private OvertimeDetailDao overtimeDetailDao;
	@Autowired
	private EmployeeProfileService profileService;
	@Autowired
	private LeaveDetailDao leaveDetailDao;

	/**
	 * Get the list of frequency report dtos with employee name format.
	 * @param divisionId The division id.
	 * @param companyId The company id.
	 * @param startDate The date start.
	 * @param endDate The date end.
	 * @param status The status of the employee
	 * @param typeId The type id.
	 * @param isFirstNameFirst True if the employee full name format is first name first, otherwise False.
	 * @return The list of frequency report dtos.
	 * @throws Exception 
	 */
	public List<FrequencyReportDto> getFrequencyReport(Integer companyId, Integer divisionId, Date startDate,
			Date endDate, Integer status, Integer typeId, boolean isFirstNameFirst) throws Exception {
		List<FrequencyReportDto> frequencyReportDtos = new ArrayList<>();
		Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs =  parseData(companyId, divisionId, startDate, endDate, status);
		if(bioId2Dtrs != null){
			Employee employee = null;
			EmployeeShift employeeShift = null;
			List<eulap.eb.service.TimeSheetService.DTRInAndOut> inAndOuts = null;
			List<eulap.eb.payroll.EmployeeDtr> dtrs = null;
			boolean isAddFrequency = false;
			FrequencyReportDto frequencyReportDto = null;
			List<Date> dates = DateUtil.getDatesFromRange(startDate, endDate);
			int cnt =1;
			LeaveDetail lv = null;
			List<String> strEmployees = new ArrayList<>();
			Map<Integer, String> hmEmployees = new HashMap<>();
			for (Map.Entry<Integer, List<eulap.eb.payroll.EmployeeDtr>> eEmployee2Dtr : bioId2Dtrs.entrySet()) {
				dtrs = eEmployee2Dtr.getValue();
				for (Date date : dates) {
					isAddFrequency = false;
					Integer employeeId = eEmployee2Dtr.getKey();
					employee = employeeDao.get(employeeId);
					if (employee == null || (status == 0 && employee.isActive()) || (status == 1 && !employee.isActive())) {
						continue;
					}
					employeeShift = profileService.getEeCurrentShift(employeeId, date);
					frequencyReportDto = new FrequencyReportDto();
					if (employeeShift != null) {
						lv = leaveDetailDao.getByEmployeeAndDate(employeeId, date);
						boolean isHalfday = lv != null ? lv.isHalfDay() : false;
						boolean hasMorningLeave = lv != null && lv.isHalfDay() ? lv.getPeriod() == LeaveDetail.LEAVE_PERIOD_AM : false;
						if (typeId.equals(FrequencyReportDto.FREQUENCY_TYPE_UNDERTIME)) {
							inAndOuts = timeSheetService.mapInAndOuts(date, dtrs, employeeShift, employeeShift.isNightShift());
							double undertime = inAndOuts.isEmpty() ? 0 : timeSheetService.computeUndertime(date, 
									inAndOuts.get(inAndOuts.size()-1).getOut(), employee, employeeShift, isHalfday, hasMorningLeave);
							if (undertime > 0) {
								frequencyReportDto.setMinutes(undertime * 60);
								isAddFrequency = true;
							}
						} else if (typeId.equals(FrequencyReportDto.FREQUENCY_TYPE_LATE)) {
							List<eulap.eb.payroll.EmployeeDtr> currentLogs = timeSheetService.getDTRByDate(date, dtrs);
							double late = 0;
							if (currentLogs != null && !currentLogs.isEmpty()) {
								if (isHalfday && lv.getPeriod() == LeaveDetail.LEAVE_PERIOD_AM) {
									late = 0;
								} else {
									late = timeSheetService.getTimeDifference(currentLogs.get(0).getDate(),
											timeSheetService.getDateShift(employeeShift.getFirstHalfShiftStart(), date));
								}
								if (late > 0) {
									frequencyReportDto.setMinutes(late * 60);
									isAddFrequency = true;
								}
							}
						} else if(typeId.equals(FrequencyReportDto.FREQUENCY_TYPE_OVERTIME)) {
							if (employeeShift.isNightShift()) {
								continue; // skip if night shift
							}
							List<eulap.eb.payroll.EmployeeDtr> dtrsByDate = timeSheetService.getDTRByDate(date, dtrs);
							if (dtrsByDate != null && !dtrsByDate.isEmpty()) {
								Date startShift = timeSheetService.getDateShift(employeeShift.getFirstHalfShiftStart(), date);
								Date endShift = timeSheetService.getDateShift(employeeShift.getSecondHalfShiftEnd(), date);
								Date dtrLastLog = dtrsByDate.get(dtrsByDate.size()-1).getDate();
								Date lastLog = null;
								Date overtimeEndtime = null;
								List<OvertimeDetail> overtimeDetails = overtimeDetailDao.getAllByEmployeeAndDate(employeeId, date, true);
								if (overtimeDetails != null && !overtimeDetails.isEmpty()) {
									for (OvertimeDetail ot : overtimeDetails) {
										Date overtimeStartTime = timeSheetService.getDateShift(ot.getStartTime(), date);
										if (overtimeStartTime.before(startShift)) {
											continue;
										}
										overtimeEndtime = timeSheetService.getDateShift(ot.getEndTime(), date);
									}
									overtimeDetails = null;
								}
								overtimeEndtime = overtimeEndtime != null ? overtimeEndtime : endShift;
								lastLog = dtrLastLog.after(overtimeEndtime) ? overtimeEndtime : dtrLastLog;
								double overStay = timeSheetService.getTimeDifference(dtrLastLog, lastLog);
								if (overStay > 0) {
									frequencyReportDto.setMinutes(overStay * 60);
									isAddFrequency = true;
								}
							}
						}
					} else {
						if (!hmEmployees.containsKey(employeeId)) {
							hmEmployees.put(employeeId, cnt++ + ".)" +  employee.getFullName());
						}
						continue;
					}

					if(isAddFrequency){
						String employeeName = "";
						if(isFirstNameFirst) {
							employeeName = employee.getFirstName() + " ";
							if(employee.getMiddleName() != null && !employee.getMiddleName().isEmpty()) {
								employeeName += employee.getMiddleName().charAt(0) + ". ";
							}
							employeeName += employee.getLastName();
						} else {
							employeeName = employee.getFullName();
						}
						frequencyReportDto.setDate(date);
						frequencyReportDto.setStrEmployeeNumber(employee.getEmployeeNo());
						frequencyReportDto.setFrequency(1);
						frequencyReportDto.setPosition(employee.getPosition().getName());
						frequencyReportDto.setName(employeeName);
						frequencyReportDtos.add(frequencyReportDto);
					}
				}
			}
			if (!hmEmployees.isEmpty()) {
				String message = ValidatorMessages.getString("FrequencyReportService.0");
				strEmployees = new ArrayList<String>( hmEmployees.values());
				hmEmployees = null;
				for (String s : strEmployees) {
					message += s + "\n";
				}
				strEmployees = null;
				throw new SuppressableStacktraceException(message, true);
			}
		}
		return frequencyReportDtos;
	}

	/**
	 * Get the list of absent employees.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param dateFrom The date start.
	 * @param dateTo The date end.
	 * @param status The status of the employee
	 * @param isFirstNameFirst True if the employee name format is first name first, otherwise false.
	 * @param isOrderByLastName true if order by last name, else false
	 * @return The list of late employees.
	 */
	public List<FrequencyReportDto> getAbsentEmployees(Integer companyId, Integer divisionId, Date dateFrom,
			Date dateTo, Integer status, boolean isFirstNameFirst, boolean isOrderByLastName) {
		List<FrequencyReportDto> frequencyReportDtos = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		List<Date> dates = DateUtil.getDatesFromRange(dateFrom, dateTo);
		for (Date date : dates) {
			cal.setTime(date);
			frequencyReportDtos.addAll(employeeDao.getAbsentEmployees(companyId, divisionId, date, status,
					cal.get(Calendar.DAY_OF_WEEK),isFirstNameFirst, isOrderByLastName));
		}
		return frequencyReportDtos;
	}

	private Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> parseData(Integer companyId, Integer divisionId, Date dateFrom, Date dateTo, Integer status) throws ParseException {
		Boolean isActive = null;
		if(!status.equals(-1)){
			isActive = status.equals(1);
		}
		List<EmployeeDtr> employeeDtrs = employeeDtrDao.getEmployeeDtrs(companyId, divisionId, dateFrom, dateTo, isActive);
		Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs =  null;
		if (employeeDtrs != null && !employeeDtrs.isEmpty()) {
			PayrollDataHandler dataHandler = new PayrollDataHandler();
			for (EmployeeDtr dtr : employeeDtrs) {
				eulap.eb.payroll.EmployeeDtr eDtr =  eulap.eb.payroll.EmployeeDtr.getInstanceOf(dtr.getEmployeeId(), dtr.getLogTime());
				dataHandler.handleParsedData(eDtr);
			}
			bioId2Dtrs = dataHandler.getParseData();
		}
		return bioId2Dtrs;
	}
}
