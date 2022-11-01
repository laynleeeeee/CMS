package eulap.eb.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.lang.reflect.Type;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.BiometricModelDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeDtrDao;
import eulap.eb.dao.EmployeeShiftDao;
import eulap.eb.dao.EmployeeShiftDayOffDao;
import eulap.eb.dao.HolidaySettingDao;
import eulap.eb.dao.LeaveDetailDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.OvertimeDetailDao;
import eulap.eb.dao.PayrollDao;
import eulap.eb.dao.PayrollEmployeeTimeSheetDao;
import eulap.eb.dao.PayrollTimePeriodDao;
import eulap.eb.dao.PayrollTimePeriodScheduleDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.TimeSheetDao;
import eulap.eb.dao.UserCompanyDao;
import eulap.eb.dao.UserDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeDtr;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.EmployeeShiftDayOff;
import eulap.eb.domain.hibernate.EmployeeStatus;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.HolidaySetting;
import eulap.eb.domain.hibernate.HolidayType;
import eulap.eb.domain.hibernate.LeaveDetail;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.OvertimeDetail;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.PayrollEmployeeTimeSheet;
import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.TimeSheet;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;
import eulap.eb.exception.SuppressableStacktraceException;
import eulap.eb.payroll.PayrollDataHandler;
import eulap.eb.payroll.PayrollParser;
import eulap.eb.payroll.PayrollParserFactory;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;
import eulap.eb.web.dto.TimeSheetTemplateDto;
import eulap.eb.web.dto.TimesheetFormDto;
import eulap.eb.web.dto.webservice.TimeSheetDetailsDto;
import eulap.eb.web.dto.webservice.TimeSheetDto;
import eulap.eb.web.dto.webservice.TimeSheetViewDto;
import eulap.eb.webservice.WebServiceCredential;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Service class for {@link TimeSheet}.

 *
 */
@Service
public class TimeSheetService extends BaseWorkflowService{
	@Autowired
	private EmployeeDtrDao employeeDtrDao;
	@Autowired
	private PayrollTimePeriodScheduleDao payrollTimePeriodScheduleDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PayrollEmployeeTimeSheetDao pTimeSheetDao;
	@Autowired
	private UserCompanyDao userCompanyDao;
	@Autowired
	private PayrollTimePeriodDao payrollTimePeriodDao;
	@Autowired
	private PayrollTimePeriodService timePeriodService;
	@Autowired
	private EmployeeShiftDao employeeShiftDao;
	@Autowired
	private LeaveDetailDao leaveDetailDao;
	@Autowired
	private TimeSheetDao timeSheetDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private OvertimeDetailDao overtimeDetailDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private EmployeeProfileService employeeProfileService;
	@Autowired
	private BiometricModelDao biometricModelDao;
	@Autowired
	private EmployeeShiftDayOffDao dayOffDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private PayrollDao payrollDao;
	@Autowired
	private HolidaySettingDao holidaySettingDao;
	@Autowired
	private FormStatusService formStatusService;

	protected static final int MIN_DIFF = 1;
	protected static final int ADDITIONAL_TIME = 3;
	protected static final double STATUS_ABSENT = 1;
	protected static final double STATUS_LEAVE = 2;
	protected static final double STATUS_REG_HOLIDAY = 3;
	protected static final double STATUS_DAY_OFF = 4;

	/**
	 * Get the timesheet object
	 * @param timeSheetId The timesheet id
	 * @return The timesheet object
	 */
	public TimeSheet getTimeSheet(int timeSheetId) {
		TimeSheet timeSheet = timeSheetDao.get(timeSheetId);
		if (timeSheet != null) {
			PayrollTimePeriod ptp = payrollTimePeriodDao.get(timeSheet.getPayrollTimePeriodId());
			timeSheet.setPayrollTimePeriod(ptp);
			timeSheet.setCompany(companyDao.get(timeSheet.getCompanyId()));
			timeSheet.setDivision(divisionDao.get(timeSheet.getDivisionId()));

			PayrollTimePeriodSchedule ptps = payrollTimePeriodScheduleDao.get(timeSheet.getPayrollTimePeriodScheduleId());
			timeSheet.setPayrollTimePeriodSchedule(ptps);
			timeSheet.setDateFrom(ptps.getDateFrom());
			timeSheet.setDateTo(ptps.getDateTo());
			timeSheet.setSelectTPSched(ptps.getId() + "-" + ptp.getId() + (ptps.isComputeContributions() ? "-true" : ""));
		}
		return timeSheet;
	}

	/**
	 * Get the list of employee dtrs by company, division, and payroll time period schedule.
	 * @param companyId The company filter.
	 * @param divisionId The division filter.
	 * @param payrollTimePeriodScheduleId The payroll time period schedule filter.
	 * @param timeSheetId The time sheet id.
	 * @return List of {@link EmployeeDtr}
	 */
	public List<EmployeeDtr> getEmployeeDtrs (Integer companyId, Integer divisionId, Integer payrollTimePeriodScheduleId, Integer timeSheetId) {
		PayrollTimePeriodSchedule schedule = payrollTimePeriodScheduleDao.get(payrollTimePeriodScheduleId);
		if (schedule == null) {
			return null;
		}
		return employeeDtrDao.getEmployeeDtrs(companyId, divisionId, schedule.getDateFrom(), schedule.getDateTo(), timeSheetId);
	}

	private void parseData(Integer companyId, Integer divisionId, Integer payrollTimePeriodScheduleId, 
			Date dateFrom, Date dateTo, PayrollDataHandler dataHandler) throws ParseException {
		PayrollTimePeriodSchedule schedule = payrollTimePeriodScheduleDao.get(payrollTimePeriodScheduleId);
		List<EmployeeDtr> employeeDtrs = employeeDtrDao.getEmployeeDtrs(companyId, divisionId, schedule.getDateFrom(), schedule.getDateTo(), true);
		if (employeeDtrs != null && !employeeDtrs.isEmpty()) {
			for (EmployeeDtr dtr : employeeDtrs) {
				eulap.eb.payroll.EmployeeDtr eDtr =  eulap.eb.payroll.EmployeeDtr.getInstanceOf(dtr.getEmployeeId(), dtr.getLogTime());
				dataHandler.handleParsedData(eDtr);
			}
		}
	}

	/**
	 * Parse and process the daily time recording of the employee.
	 * @param companyName The company name.
	 * @param employeeTypeId The employee type id.
	 * @param biometricModelId The biometric model id.
	 * @param dateFrom The date range start.
	 * @param dateTo The date range end.
	 * @return The list of time sheet dto.
	 * @throws ParseException
	 * @throws SuppressableStacktraceException 
	 */
	public List<TimeSheetDto> parseAndProcessDTRO (int tId, String companyName, Integer divisionId, 
			Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId,
			Date dateFrom, Date dateTo, PayrollDataHandler dataHandler) throws ParseException, SuppressableStacktraceException  {
		Integer companyId =  companyDao.getCompanyIdbyName (companyName);
		parseData(companyId, divisionId, payrollTimePeriodScheduleId, dateFrom, dateTo, dataHandler);

		Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs = dataHandler.getParseData();
		return parseAndProcessDTR(tId, bioId2Dtrs, dateFrom, dateTo, companyName,
				divisionId, payrollTimePeriodId, payrollTimePeriodScheduleId);
	}

	/**
	 * Handle the parsing and processing of employee DTR.
	 * @throws ParseException 
	 * @throws SuppressableStacktraceException 
	 */
	public List<TimeSheetDto> parseAndProcessDTR(int tId, Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs, Date startDate,
			Date endDate, String companyName,  Integer divisionId, 
			Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId) throws ParseException, SuppressableStacktraceException {
		List<TimeSheetDto> timeSheets = parseAndProcessTimeSheet(tId, bioId2Dtrs, startDate, endDate, companyName,
				divisionId, payrollTimePeriodId, payrollTimePeriodScheduleId );
		Collections.sort(timeSheets, new Comparator<TimeSheetDto>() {

			@Override
			public int compare(TimeSheetDto o1, TimeSheetDto o2) {
				return o1.getEmployeeName().compareTo(o2.getEmployeeName());
			}
		});
		return timeSheets;
	}

	private List<TimeSheetDto> parseAndProcessTimeSheet(int tId, Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs, Date dateFrom,
			Date dateTo, String companyName, Integer divisionId, 
			Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId) throws ParseException, SuppressableStacktraceException {
		List<TimeSheetDto> timeSheetDtos = new ArrayList<>();
		List<Date> dates = getPayrollDates(dateFrom, dateTo);
		timeSheetDtos = getEmployeesTimeSheet(tId, bioId2Dtrs, dates, companyName, divisionId, 
				payrollTimePeriodId, payrollTimePeriodScheduleId);
		return timeSheetDtos;
	}

	/**
	 * Get the list of time sheet dto.
	 * @param bioId2Dtrs The EmployeeDtr data from biometric.
	 * @param dates The list of dates.
	 * @param companyName The company name.
	 * @return The list of time sheet dto.
	 * @throws ParseException 
	 * @throws SuppressableStacktraceException 
	 */
	public List<TimeSheetDto> getEmployeesTimeSheet(int tId, Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs, List<Date> dates,
			String companyName,  Integer divisionId, 
			Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId) throws ParseException, SuppressableStacktraceException {
		List<TimeSheetDto> timeSheetDtos = new ArrayList<>();
		Integer companyId = companyService.getCompanyIdByName(companyName);
		List<Integer> employeeIds = new ArrayList<>();
		Employee parsedEmployee = null;
		for (Map.Entry<Integer, List<eulap.eb.payroll.EmployeeDtr>> eEmployee2Dtr : bioId2Dtrs.entrySet()) {
			Integer employeeId = eEmployee2Dtr.getKey();
			 parsedEmployee = employeeDao.get(employeeId);
			if (parsedEmployee == null || !parsedEmployee.isActive()) {
				continue;
			}
			if (parsedEmployee.getDivisionId().equals(divisionId)){
				List<eulap.eb.payroll.EmployeeDtr> dtrs = eEmployee2Dtr.getValue();
				TimeSheetDto tsDtp = processEmployeeTimeSheet(tId, dates, parsedEmployee, dtrs, payrollTimePeriodId, payrollTimePeriodScheduleId);
				timeSheetDtos.add(tsDtp);
				employeeIds.add(parsedEmployee.getId());
			}
		}
		List<Employee> employees = employeeDao.getEmployees(companyId, divisionId, null);
		for (Employee employee : employees) {
			if(!employeeIds.contains(employee.getId())){
				TimeSheetDto tsDtp = appenedEmployeeTimesheet(tId, payrollTimePeriodId, payrollTimePeriodScheduleId,employee, dates);
				timeSheetDtos.add(tsDtp);
			}
		}
		return timeSheetDtos;
	}

	/**
	 * Get the timesheet for view form
	 * @param timeSheetId The timesheet id
	 * @return The timesheet for view form
	 */
	public TimesheetFormDto getTimeSheetView(int timeSheetId) {
		TimesheetFormDto timesheetFormDto = new TimesheetFormDto();
		TimeSheet timeSheet = timeSheetDao.get(timeSheetId);
		if (timesheetFormDto != null && timeSheet != null) {
			timeSheet.setCompany(companyDao.get(timeSheet.getCompanyId()));
			timeSheet.setDivision(divisionDao.get(timeSheet.getDivisionId()));
			if (timeSheet.getBiometricModelId() != null) {
				timesheetFormDto.setBiometricModel(biometricModelDao.get(timeSheet.getBiometricModelId()));
			}
			timesheetFormDto.setReferenceDocument(referenceDocumentDao.getRDByEbObject(
					timeSheet.getEbObjectId(), ReferenceDocument.OR_TYPE_ID));
			setPeriods(timeSheet);
			List<PayrollEmployeeTimeSheet> pETimesSheets = pTimeSheetDao.getByTimeSheet(timeSheetId);
			timeSheet.settSDateTitles(initTSDateHeader(pETimesSheets));
			timeSheet.setTimeSheetViewDtos(initTSViewData(pETimesSheets));
		}
		timesheetFormDto.setTimeSheet(timeSheet);
		return timesheetFormDto;
	}

	/**
	 * Get the list of payroll dates range.
	 * @param startdate The start date.
	 * @param enddate The end date.
	 * @return The list of payroll dates.
	 */
	public List<Date> getPayrollDates(Date startdate, Date enddate) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startdate);
		while (calendar.getTime().before(enddate)) {
			Date result = calendar.getTime();
			dates.add(result);
			calendar.add(Calendar.DATE, 1);
		}
		dates.add(calendar.getTime());
		return dates;
	}

	/**
	 * Get the list of time sheet dto.
	 * @param bioId2Dtrs The EmployeeDtr data from biometric.
	 * @param dates The list of dates.
	 * @param companyName The company name.
	 * @return The list of time sheet dto.
	 * @throws ParseException 
	 * @throws SuppressableStacktraceException 
	 */
	public List<TimeSheetDto> getEmployeesTimeSheet(int tId, Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs, List<Date> dates,
			String companyName, Integer employeeTypeId, Integer divisionId, 
			Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId) throws ParseException, SuppressableStacktraceException {
		List<TimeSheetDto> timeSheetDtos = new ArrayList<>();
		Integer companyId = companyService.getCompanyIdByName(companyName);
		List<Integer> employeeIds = new ArrayList<>();
		Employee parsedEmployee = null;
		for (Map.Entry<Integer, List<eulap.eb.payroll.EmployeeDtr>> eEmployee2Dtr : bioId2Dtrs.entrySet()) {
			Integer employeeId = eEmployee2Dtr.getKey();
			 parsedEmployee = employeeDao.get(employeeId);
			if (parsedEmployee == null) {
				continue;
			}
			if (parsedEmployee.getDivisionId().equals(divisionId)){
				List<eulap.eb.payroll.EmployeeDtr> dtrs = eEmployee2Dtr.getValue();
				TimeSheetDto tsDtp = processEmployeeTimeSheet(tId, dates, parsedEmployee, dtrs, payrollTimePeriodId, payrollTimePeriodScheduleId);
				timeSheetDtos.add(tsDtp);
				employeeIds.add(parsedEmployee.getId());
			}
		}
		List<Employee> employees = employeeDao.getEmployees(companyId, divisionId, employeeTypeId);
		for (Employee employee : employees) {
			if(!employeeIds.contains(employee.getId())){
				TimeSheetDto tsDtp = appenedEmployeeTimesheet(tId, payrollTimePeriodId, payrollTimePeriodScheduleId, employee, dates);
				timeSheetDtos.add(tsDtp);
			}
		}
		return timeSheetDtos;
	}

	/**
	 * Append employee that have no log in DTR file to timesheet
	 * @param tId The timesheet id
	 * @param payrollTimePeriodId The payroll time period
	 * @param payrollTimePeriodScheduleId The payroll time period schedule
	 * @param employee The employee object
	 * @param dates List of dates
	 * @return The employee timesheet
	 */
	public TimeSheetDto appenedEmployeeTimesheet(int tId, int payrollTimePeriodId, int payrollTimePeriodScheduleId, 
			Employee employee, List<Date> dates){
		List<TimeSheetDetailsDto> tsDetails = new ArrayList<TimeSheetDetailsDto>();
		PayrollEmployeeTimeSheet pet = null;
		EmployeeShift shift = null;
		Integer employeeId = employee.getId();
		for (Date d : dates) {
			shift = employeeShiftDao.getBySchedule(employee.getCompanyId(), 
					payrollTimePeriodId, payrollTimePeriodScheduleId, employeeId, d);
			if (shift == null) {
				throw new RuntimeException("No shift is assigned to " + employee.getFullName() + " for the date " 
						+ DateUtil.formatDate(d));
			}
			pet = pTimeSheetDao.getByEmpTSAndDate(employeeId, tId, d);
			TimeSheetDetailsDto dtrDetail = new TimeSheetDetailsDto();
			dtrDetail.setDate(d);
			dtrDetail.setHoursWork(0);
			dtrDetail.setLate(0);
			if (pet != null) {
				dtrDetail.setAdjustment(pet.getAdjustment());
			}
			boolean isDayOff = isDayOff(shift.getId(), d);
			boolean hasLeave = leaveDetailDao.hasLeave(employeeId, d);
			HolidaySetting holiday = holidaySettingDao.getByDate(d);
			if(holiday != null && holiday.getHolidayTypeId() == HolidayType.TYPE_REGULAR) {
				dtrDetail.setStatus(STATUS_REG_HOLIDAY);
			} else {
				if (!isDayOff && !hasLeave) {
					dtrDetail.setStatus(STATUS_ABSENT);
				} else if (hasLeave) {
					dtrDetail.setStatus(STATUS_LEAVE);
				} else if (isDayOff) {
					dtrDetail.setStatus(STATUS_DAY_OFF);
				}
			}
			tsDetails.add(dtrDetail);
		}
		return new TimeSheetDto(employeeId, employee.getEmployeeNo(), employee.getFullName(), tsDetails, employee.getEmployeeStatus().getName());
	}

	/**
	 * Process and consolidate employee timesheet-DTR details
	 * @param tId The timesheet id
	 * @param payrollDates The list of payroll dates
	 * @param employee The employee object
	 * @param dtrs The list of employee DTRs
	 * @param payrollTimePeriodId The payroll time period id
	 * @param payrollTimePeriodScheduleId The payroll time period id
	 * @return The processed employee timesheet
	 * @throws ParseException
	 * @throws SuppressableStacktraceException
	 */
	public TimeSheetDto processEmployeeTimeSheet(int tId, List<Date> payrollDates, Employee employee, List<eulap.eb.payroll.EmployeeDtr> dtrs, 
			Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId) throws ParseException, SuppressableStacktraceException {
		return processEmployeeTimeSheet(tId, payrollDates, employee, dtrs, payrollTimePeriodId, payrollTimePeriodScheduleId, false);
	}

	/**
	 * Process and consolidate employee timesheet-DTR details
	 * @param tId The timesheet id
	 * @param payrollDates The list of payroll dates
	 * @param employee The employee object
	 * @param dtrs The list of employee DTRs
	 * @param payrollTimePeriodId The payroll time period id
	 * @param payrollTimePeriodScheduleId The payroll time period id
	 * @param isAutoReflectOT True if auto reflect OT, otherwise false.
	 * @return The processed employee timesheet
	 * @throws ParseException
	 * @throws SuppressableStacktraceException
	 */
	public TimeSheetDto processEmployeeTimeSheet(int tId, List<Date> payrollDates, Employee employee, List<eulap.eb.payroll.EmployeeDtr> dtrs, 
			Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId, boolean isAutoReflectOT) throws ParseException, SuppressableStacktraceException {
		List<TimeSheetDetailsDto> tsDetails = new ArrayList<TimeSheetDetailsDto>();
		// Sort the DTR by date.
		Collections.sort(dtrs, new Comparator<eulap.eb.payroll.EmployeeDtr>() {
			@Override
			public int compare(eulap.eb.payroll.EmployeeDtr e1, eulap.eb.payroll.EmployeeDtr e2) {
				if (e1.getDate().after(e2.getDate())) {
					return 1;
				} else if (e1.getDate().equals(e2.getDate())) {
					return 0;
				}
				return -1;
			}
		});

		PayrollEmployeeTimeSheet pet = null;
		EmployeeShift employeeShift = null;
		List<DTRInAndOut> inAndOuts = null;
		Map<Date, List<eulap.eb.payroll.EmployeeDtr>> hmDtrs = new HashMap<Date, List<eulap.eb.payroll.EmployeeDtr>>();
		boolean isHalfday = false;
		boolean hasMorningLeave = false;
		double late = 0;
		double undertime = 0;
		double hoursWorked = 0;
		LeaveDetail ld = null;
		List<EmployeeDtr> employeeDtrs = new ArrayList<>();
		List<String> strEmployees = new ArrayList<>();
		Map<Integer, String> hmEmployees = new HashMap<>();
		int count = 1;
		int employeeId = employee.getId();
		mapEeDtrs(hmDtrs, dtrs, employeeId);
		for (Date currentDate : payrollDates) {
			employeeShift = employeeProfileService.getEeCurrentShift(employeeId, currentDate);
			if (employeeShift == null) {
				if (!hmEmployees.containsKey(employeeId)) {
					hmEmployees.put(employeeId, count++ + ".) " +  employee.getFullName());
				}
				continue;
			}
			double dailyWorkingHours = employeeShift.getDailyWorkingHours();
			pet = pTimeSheetDao.getByEmpTSAndDate(employeeId, tId, currentDate);
			TimeSheetDetailsDto dtrDetail = new TimeSheetDetailsDto();
			dtrDetail.setDate(currentDate);
			List<eulap.eb.payroll.EmployeeDtr> dtrsByDate = (hmDtrs.get(currentDate) == null) ? new ArrayList<>() : hmDtrs.get(currentDate);
			double overtime = 0.0;
			if (!dtrsByDate.isEmpty()) {
				overtime = computeOvertime(employee.getId(), dtrsByDate.get(0), dtrsByDate.get(dtrsByDate.size() - 1));
			} else if (isAutoReflectOT) { // SSC-2151 : Approved overtime with no logs will be included
				OvertimeDetail overtimeDetail = overtimeDetailDao.getByEmployeeAndDate(employee.getId(), currentDate, true);
				if (overtimeDetail != null) {
					overtime = overtimeDetail.getOvertimeHours();
				}
			}
			dtrDetail.setOvertime(overtime);
			inAndOuts = new ArrayList<>();
			if (!dtrsByDate.isEmpty()) {
				DTRInAndOut inAndOut = new DTRInAndOut(null, null);
				Date prevDtrDate = null;
				for(eulap.eb.payroll.EmployeeDtr dtr : dtrsByDate) {
					Date dtrDate = dtr.getDate();
					if(inAndOut.getIn() == null && dtrs.iterator().hasNext()) {
						inAndOut.setIn(dtrDate);
					} else {
						inAndOut.setOut(dtrDate);
						if(inAndOut.getIn() == null) {
							inAndOut.setIn(prevDtrDate);
						}
					}
					if((inAndOut.getIn() != null && inAndOut.getOut() != null) || !dtrs.iterator().hasNext()) {
						inAndOuts.add(inAndOut);
						inAndOut = new DTRInAndOut(null, null);
					}
					prevDtrDate = dtrDate;
				}
				inAndOut = null;
			}

			ld = leaveDetailDao.getByEmployeeAndDate(employeeId, currentDate);
			if (!inAndOuts.isEmpty() && ld != null) {
				isHalfday = ld.isHalfDay();
				hasMorningLeave = ld.getPeriod() == LeaveDetail.LEAVE_PERIOD_AM;
			}

			late = 0;
			undertime = 0;
			if (!inAndOuts.isEmpty() && (inAndOuts.get(0).getIn() != null && inAndOuts.get(0).getOut() != null)) {
				undertime = !inAndOuts.isEmpty() ? computeUndertime(currentDate, inAndOuts.get(
						inAndOuts.size()-1).getOut(), employee, employeeShift, isHalfday, hasMorningLeave) : 0;
				late = !inAndOuts.isEmpty() ? computeLate(currentDate, inAndOuts, employee, employeeShift, isHalfday, hasMorningLeave) : 0;
			}

			hoursWorked = 0;
			if (!inAndOuts.isEmpty()) {
				double halfDayWorkHours = NumberFormatUtil.divideWFP(dailyWorkingHours, 2);
				for (DTRInAndOut io : inAndOuts) {
					if (io.getOut() == null || io.getIn() == null) {
						hoursWorked = 0;
					} else {
						hoursWorked += getTimeDifference(io.getOut(), io.getIn());
					}
				}

				double origHoursWorked = hoursWorked + late + undertime; // re-adding values
				if (origHoursWorked >= dailyWorkingHours) {
					if (late > halfDayWorkHours) {
						late = late - employeeShift.getAllowableBreak();
					}
					if (undertime > halfDayWorkHours) {
						undertime = undertime - employeeShift.getAllowableBreak();
					}
					hoursWorked = computeHoursWorked(dailyWorkingHours, late, undertime, 0.0);
				} else if (isHalfday) {
					hoursWorked = computeHoursWorked(halfDayWorkHours, late, undertime, 0.0);
				}
			}
			setStatus(employeeShift, dtrDetail, employee.getId(), currentDate, inAndOuts.isEmpty());
			dtrDetail.setHoursWork((hoursWorked < 0) ? 0 : hoursWorked);
			dtrDetail.setLate(late);
			dtrDetail.setUndertime(undertime);
			if (pet != null) {
				dtrDetail.setAdjustment(pet.getAdjustment());
			}
			dtrDetail.setDaysWorked(computeDays(employeeShift, dtrDetail.getHoursWork()));
			dtrDetail.setDailyWorkingHours(employeeShift.getDailyWorkingHours());

			tsDetails.add(dtrDetail);
			employeeDtrs.addAll(getEmployeeDtrs(dtrsByDate, employeeId));

			hasMorningLeave = false;
			isHalfday = false;
			inAndOuts = null;
			dtrsByDate = null;
		}

		if (!hmEmployees.isEmpty()) {
			String message = "There are employees that do not have complete daily shift schedule configuration: \n\n";
			strEmployees = new ArrayList<String>( hmEmployees.values());
			hmEmployees = null;
			for (String s : strEmployees) {
				message += s + "\n";
			}
			strEmployees = null;
			throw new SuppressableStacktraceException(message, true);
		}
		String employeeName = employee.getFullName();
		EmployeeStatus status = employee.getEmployeeStatus();
		TimeSheetDto tsDto = new TimeSheetDto(employeeId, employee.getEmployeeNo(), employeeName, tsDetails, status.getName());
		tsDto.setEmployeeDtrs(employeeDtrs);
		return tsDto;
	}

	protected List<EmployeeDtr> getEmployeeDtrs(List<eulap.eb.payroll.EmployeeDtr> dtrsByDate, int employeeId) {
		List<EmployeeDtr> employeeDtrs = new ArrayList<>();
		EmployeeDtr eDtr = null;
		if (!dtrsByDate.isEmpty()) {
			for (eulap.eb.payroll.EmployeeDtr dtr : dtrsByDate) {
				eDtr = new EmployeeDtr();
				eDtr.setEmployeeId(employeeId);
				eDtr.setLogTime(dtr.getDate());
				eDtr.setIsSynchronize(true);
				eDtr.setActive(true);
				employeeDtrs.add(eDtr);
			}
		}
		return employeeDtrs;
	}

	protected void setStatus(EmployeeShift shift, TimeSheetDetailsDto dtrDetail, int employeeId, Date date, boolean hasNoLog) {
		if (hasNoLog) {
			boolean hasLeave = leaveDetailDao.hasLeave(employeeId, date);
			HolidaySetting holiday = holidaySettingDao.getByDate(date);
			if(holiday != null && holiday.getHolidayTypeId() == HolidayType.TYPE_REGULAR) {
				dtrDetail.setStatus(STATUS_REG_HOLIDAY);
			} else {
				if (hasLeave) {
					dtrDetail.setStatus(STATUS_LEAVE); // Leave
				} else if (isDayOff(shift.getId(), date)) {  // Day Off
					dtrDetail.setStatus(STATUS_DAY_OFF);
				} else {
					dtrDetail.setStatus(STATUS_ABSENT); // Not Day Off
				}
			}
		}
	}

	/**
	 * Check if the date is the employee day off
	 * @param shiftId The employee shift id
	 * @param date The date to evaluated
	 * @return True if the date is the employees dayoff, otherwise false
	 */
	public boolean isDayOff (int shiftId, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		List<EmployeeShiftDayOff> dayOffs = dayOffDao.getAllByRefId(EmployeeShiftDayOff.FIELD.employeeShiftId.name(), shiftId);
		if (!dayOffs.isEmpty()) {
			for (EmployeeShiftDayOff d : dayOffs) {
				if (d.getDayOfTheWeek().intValue() == cal.get(Calendar.DAY_OF_WEEK)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Compute the total overtime.
	 * @param employeeId The employee id.
	 * @param dtrOut The employee dtr.
	 * @return The total overtime.
	 */
	public double computeOvertime(Integer employeeId, eulap.eb.payroll.EmployeeDtr dtrIn, eulap.eb.payroll.EmployeeDtr dtrOut){
		Date firstIn = dtrIn.getDate();
		Date lastOut = dtrOut.getDate();
		Date date = DateUtil.removeTimeFromDate(lastOut);
		double overtime = 0;
		double diffOt = 0;
		List<OvertimeDetail> overtimeDetails = overtimeDetailDao.getAllByEmployeeAndDate(employeeId, date, true);
		for (OvertimeDetail ot : overtimeDetails) {
			Date otStart = getDateShift(ot.getStartTime(), date);
			Date otEnd = getDateShift(ot.getEndTime(), date);
			lastOut = lastOut.after(otEnd) ? otEnd : lastOut; // reset out if the last log exceed the requested OT time

			double diffOtStartFirstin = getTimeDifference(firstIn, otStart);
			double diffOtEndFirstIn = getTimeDifference(otEnd, firstIn);
			double diffOtEndLastOut = getTimeDifference(otEnd, lastOut);
			if(diffOtEndFirstIn > diffOtEndLastOut) {
				diffOt = diffOtEndLastOut;
			} else {
				diffOt = diffOtStartFirstin;
			}
			overtime += ot.getOvertimeHours() - (diffOt > 0 ? diffOt : 0);
		}
		return overtime > 0 ? overtime : 0;
	}

	/**
	 * Compute the total under time of the employee.
	 * @param currentDate The current date.
	 * @param lastOut The last data  of the DTR for the day.
	 * @param employee The employee.
	 * @param shift The employee shift,
	 * @return The total under time of the employee.
	 */
	public double computeUndertime(Date currentDate, Date lastOut, Employee employee,
			EmployeeShift shift, boolean isHalfDay, boolean isMorningLeave) {
		Date nextDate = DateUtil.addDaysToDate(currentDate, 1);
		Date endShift = getDateShift(shift.getSecondHalfShiftEnd(), shift.isNightShift() ? nextDate : currentDate);
		if(isHalfDay && !isMorningLeave) {
			endShift =  DateUtil.addHourToDate(getDateShift(shift.getFirstHalfShiftStart(), currentDate), 4);
		}
		double undertime = getTimeDifference(endShift, lastOut);
		return undertime > 0 ? undertime : 0;

	}

	/**
	 * Compute late.
	 * @param currentDate The current date.
	 * @param inAndOuts The in and outs of the employee.
	 * @param employee The employee.
	 * @param shift The shift of the employee.
	 * @return the total late of the employee for the day.
	 */
	public double computeLate(Date currentDate, List<DTRInAndOut> inAndOuts,
			Employee employee, EmployeeShift shift, boolean isHalfDay, boolean isMorningLeave) {
		double late = 0;
		if (inAndOuts != null && !inAndOuts.isEmpty()) {
			Date startShift = DateUtil.appendTimeToDate(shift.getFirstHalfShiftStart(), currentDate);
			Date firstIn = inAndOuts.get(0).in;
			if(isHalfDay && isMorningLeave) {
				startShift =  DateUtil.addHourToDate(getDateShift(shift.getSecondHalfShiftEnd(), currentDate), -4);
			}
			late = getTimeDifference(firstIn, startShift);
		}
		return late > 0 ? late : 0;
	}

	/**
	 * Compute the employee over break.
	 * @param currentDate The current date.
	 * @param inAndOuts The in and out of the emplyee.
	 * @param employee The employee.
	 * @param shift The shift of the employee.
	 * @return The total over break for the day.
	 */
	public double computeOverbreak(Date currentDate, List<DTRInAndOut> inAndOuts, Employee employee, EmployeeShift shift) {
		double overbreak = 0;
		if (inAndOuts != null && !inAndOuts.isEmpty()) {
			double hoursBreaked = 0;
			double maxAllowableBreak = shift.getAllowableBreak();
			DTRInAndOut io = null;
			for (int i=0; i<inAndOuts.size(); i++) {
				io = inAndOuts.get(i);
				if (i > 0) {
					hoursBreaked += getTimeDifference(io.in, inAndOuts.get(i-1).out);
				}
			}
			overbreak =  hoursBreaked > maxAllowableBreak ? hoursBreaked - maxAllowableBreak : 0;
		}
		return overbreak;
	}

	/**
	 * Compute the total hours work.
	 * @param dailyWorkingHours The expected working hours.
	 * @param late The late of employee.
	 * @param undertime The undertime of employee.
	 * @param overbreak The overbreak of employee.
	 * @return The total working hours.
	 */
	public double computeHoursWorked (double dailyWorkingHours, double late, double undertime, double overbreak) {
		double hoursWorked = dailyWorkingHours - overbreak - late - undertime;
		if (hoursWorked > dailyWorkingHours) {
			hoursWorked = dailyWorkingHours;
		}
		return hoursWorked;
	}

	/**
	 * The date DTR date.
	 * @param date The date.
	 * @param dtrs The list of employee dtr.
	 * @return The list of employee DTR.
	 */
	public List<eulap.eb.payroll.EmployeeDtr> getDTRByDate(Date date, List<eulap.eb.payroll.EmployeeDtr> dtrs) {
		List<eulap.eb.payroll.EmployeeDtr> ret = new ArrayList<>();
		for (eulap.eb.payroll.EmployeeDtr dtr : dtrs) {
			Date dtrWithoutTime = DateUtil.removeTimeFromDate(dtr.getDate());
			int dayDiff = DateUtil.getDayDifference(date, dtrWithoutTime);
			if (dayDiff == 0) {
				ret.add(dtr);
			}
		}
		return ret;
	}

	/**
	 * Class that holds the in and out of the shift and in the employee dtr.
	 */
	public static class DTRInAndOut {
		private Date in;
		private Date out;

		protected DTRInAndOut(Date in, Date out) {
			this.in = in;
			this.out = out;
		}

		public Date getIn() {
			return in;
		}

		public void setIn(Date in) {
			this.in = in;
		}

		public Date getOut() {
			return out;
		}

		public void setOut(Date out) {
			this.out = out;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DTRInAndOut [in=");
			builder.append(in);
			builder.append(", out=");
			builder.append(out);
			builder.append("]");
			return builder.toString();
		}
	}

	/**
	 * Convert string time of shift to date.
	 * @param shift The employee shift.
	 * @param date The date.
	 * @return Employee shift in date format.
	 */
	public Date getDateShift(String shift, Date date) {
		int startHr = Integer.valueOf(shift.split(":")[0]);
		int startMin = Integer.valueOf(shift.split(":")[1]);
		Calendar cShift = Calendar.getInstance();
		cShift.setTime(DateUtil.removeTimeFromDate(date));
		cShift.set(Calendar.HOUR, startHr);
		cShift.set(Calendar.MINUTE, startMin);
		return cShift.getTime();
	}

	/**
	 * Get the time difference of two dates.
	 */
	public double getTimeDifference (Date date1, Date date2) {
		return (Double.parseDouble((date1.getTime() - date2.getTime() + ""))/1000) / 60 / 60;
	}

	public List<DTRInAndOut> mapInAndOuts (Date currentDate,  List<eulap.eb.payroll.EmployeeDtr> dtrs, 
			EmployeeShift shift, boolean isPrevNightShift) {
		List<DTRInAndOut> inAndOuts = new ArrayList<>();
		if (dtrs != null && !dtrs.isEmpty()) {
			List<eulap.eb.payroll.EmployeeDtr> currentLogs = getDTRByDate(currentDate, dtrs);
			if (currentLogs != null && !currentLogs.isEmpty() && shift != null) {
				boolean isNightShift = shift.isNightShift();
				Date nextDate = DateUtil.addDaysToDate(currentDate, 1);
				Date startShift = getDateShift(shift.getFirstHalfShiftStart(), currentDate);
				Date endShift = getDateShift(shift.getSecondHalfShiftEnd(), isNightShift ? nextDate : currentDate);
				if (isPrevNightShift || currentLogs.get(0).getDate().before(DateUtil.addHourToDate(startShift, -ADDITIONAL_TIME))) {
					currentLogs.remove(0);
				}
				boolean isOdd = currentLogs.size() % 2 != 0;
				if (isOdd) {
					List<eulap.eb.payroll.EmployeeDtr> nextLogs = getDTRByDate(nextDate, dtrs);
					if (nextLogs != null && !nextLogs.isEmpty()) {
						Date extendedEndShift = DateUtil.addHourToDate(endShift, ADDITIONAL_TIME);
						Date nextDayOut = nextLogs.iterator().next().getDate();
						if (nextDayOut.before(extendedEndShift) || nextDayOut.equals(extendedEndShift)) {
							currentLogs.add(nextLogs.iterator().next());
						}
					}
				}
				currentLogs = filterDtrs(currentLogs, startShift, endShift);
				Date in = null;
				Date out = null;
				for (eulap.eb.payroll.EmployeeDtr dtr : currentLogs) {
					if (in == null) {
						in = dtr.getDate();
					} else if (out == null) {
						out = dtr.getDate();
					}
					if (in != null && out != null) {
						inAndOuts.add(new DTRInAndOut(in, out));
						in = null;
						out = null;
					}
				}
			}
		}
		return inAndOuts;
	}

	protected List<eulap.eb.payroll.EmployeeDtr> filterDtrs (List<eulap.eb.payroll.EmployeeDtr> dtrs, Date startShift, Date endShift) {
		List<eulap.eb.payroll.EmployeeDtr> filteredDtrs = new ArrayList<>();
		int length = dtrs.size();
		eulap.eb.payroll.EmployeeDtr currentDtr = null;
		for (int i=0; i<length; i++) {
			currentDtr = dtrs.get(i);
			if (i >= 1 && !isMoreThanOneMinDiff(dtrs.get(i-1).getDate(), currentDtr.getDate())) {
				continue;
			}
			if (currentDtr.getDate().before(startShift)) {
				currentDtr.setDate(startShift);
			} else if (currentDtr.getDate().after(endShift)) {
				currentDtr.setDate(endShift);
			}
			filteredDtrs.add(currentDtr);
		}
		return filteredDtrs;
	}


	/**
	 * Get the list of company.
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public List<String> getCompanies(WebServiceCredential credential) throws IOException,
			ClassNotFoundException {
			// Check credential
			User user = userDao.getUser(credential.getUserName(), credential.getPassword());
			List<UserCompany> userCompanies = userCompanyDao.getUserCompanies(user.getId());
			user.setUserCompanies(userCompanies);
			List<Company> companies = companyService.getCompanies(user);
			List<String> companyNames = new ArrayList<String>();
			for (Company company : companies) {
				companyNames.add(company.getName());
			}
			return companyNames;
	}

	/**
	 * Process time sheet.
	 * @param timeSheetDtos The time sheet DTO.
	 * @param companyId The company id. 
	 * @return The processed time sheet.
	 */
	public List<PayrollEmployeeTimeSheet> processEmployeeTimeSheet(int payrollTimePeriodId, int payrollTimePeriodScheduleId,
			List<TimeSheetDto> timeSheetDtos, int companyId) {
		List<PayrollEmployeeTimeSheet> employeeTimeSheets = new ArrayList<>();
		PayrollEmployeeTimeSheet employeeTimeSheet = null;
		Employee employee = null;
		EmployeeShift shift = null;
		double dailyWorkingHours = 0;
		Integer employeeId = null;
		Integer tsEmployeeId = null;
		List<TimeSheetDetailsDto> timeSheetDetailsDtos = null;
		for (TimeSheetDto timeSheetDto : timeSheetDtos) {
			tsEmployeeId = timeSheetDto.getEmployeeId();
			if (tsEmployeeId != null) {
				employee = employeeDao.get(tsEmployeeId);
			} else {
				employee = employeeDao.getEmployeeByNo(timeSheetDto.getEmployeeNo(), companyId);
			}

			if (employee != null) {
				employeeId = employee.getId();
				timeSheetDto.setEmployeeName(employee.getFullName());
				employee = null;
			}

			timeSheetDetailsDtos = timeSheetDto.getTimeSheetDetailsDtos();
			if (timeSheetDetailsDtos != null && !timeSheetDetailsDtos.isEmpty()) {
				for (TimeSheetDetailsDto sheetDetailsDto : timeSheetDetailsDtos) {
					shift = employeeShiftDao.getBySchedule(companyId, payrollTimePeriodId,
							payrollTimePeriodScheduleId, employeeId, sheetDetailsDto.getDate());
					dailyWorkingHours = shift.getDailyWorkingHours();
					employeeTimeSheet = new PayrollEmployeeTimeSheet();
					employeeTimeSheet.setEmployeeId(employeeId);
					employeeTimeSheet.setDate(sheetDetailsDto.getDate());
					employeeTimeSheet.setHoursWorked(sheetDetailsDto.getHoursWork());
					employeeTimeSheet.setAdjustment(sheetDetailsDto.getAdjustment());
					double totalTime = employeeTimeSheet.getAdjustment() + employeeTimeSheet.getHoursWorked();
					if (totalTime >= dailyWorkingHours) {
						employeeTimeSheet.setLate(0);
					} else {
						double remainingTime = dailyWorkingHours - totalTime;
						if (sheetDetailsDto.getLate() > remainingTime) {
							employeeTimeSheet.setLate(remainingTime);
						} else {
							employeeTimeSheet.setLate(sheetDetailsDto.getLate());
						}
					}
					employeeTimeSheet.setUndertime(sheetDetailsDto.getUndertime());
					employeeTimeSheet.setOvertime(sheetDetailsDto.getOvertime());
					employeeTimeSheet.setDaysWorked(computeDays(shift, sheetDetailsDto.getHoursWork()));
					employeeTimeSheet.setStatus(setEmployeeDtrStatus(shift, totalTime,
							employeeTimeSheet.getDate(), employeeId));
					employeeTimeSheets.add(employeeTimeSheet);

					employeeTimeSheet = null;
					shift = null;
				}
			}
		}
		return employeeTimeSheets;
	}

	protected double computeDays(EmployeeShift shift, double totalHoursWorked) {
		return NumberFormatUtil.divideWFP(totalHoursWorked, shift.getDailyWorkingHours());
	}

	/**
	 * Set the to be saved status
	 * @param sheetDetailsDto The time sheet DTO
	 * @param shift The employee shift
	 * @param hoursWorked The total hours worked
	 * @param date The current date
	 * @param employeeId The employee id
	 */
	protected double setEmployeeDtrStatus(EmployeeShift shift, double hoursWorked,
			Date date, Integer employeeId) {
		double status = 0.0;
		boolean hasLeave = leaveDetailDao.hasLeave(employeeId, date);
		HolidaySetting holiday = holidaySettingDao.getByDate(date);
		if(holiday != null && holiday.getHolidayTypeId() == HolidayType.TYPE_REGULAR) {
			status = STATUS_REG_HOLIDAY;
		} else {
			if (hasLeave) {
				status = STATUS_LEAVE;
			} else if (isDayOff(shift.getId(), date)) {
				status = STATUS_DAY_OFF;
			} else if(hoursWorked == 0.0) {
				status = STATUS_ABSENT;
			}
		}
		return status;
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		TimeSheet timeSheet = (TimeSheet) form;
		boolean isNew = timeSheet.getId() == 0;
		AuditUtil.addAudit(timeSheet, new Audit(user.getId(), isNew, new Date()));
		if (isNew) {
			timeSheet.setSequenceNumber(timeSheetDao.generateSN(timeSheet.getCompanyId()));
		}
		timeSheetDao.saveOrUpdate(timeSheet);
	}

	/**
	 * Save the time sheet form
	 * @param timesheetFormDto The time sheet DTO
	 * @param user The current user logged
	 */
	public void saveTimesheetForm(TimesheetFormDto timesheetFormDto, User user) throws InvalidClassException, ClassNotFoundException {
		TimeSheet timeSheet = timesheetFormDto.getTimeSheet();
		timeSheet.setEmployeeTimeSheets(processEmployeeTimeSheet(
				timeSheet.getPayrollTimePeriodId(),
				timeSheet.getPayrollTimePeriodScheduleId(),
				timeSheet.getTimeSheetDtos(), timeSheet.getCompanyId()));
		ebFormServiceHandler.saveForm(timeSheet, user);
		saveEmployeeTimeSheets(timeSheet, timesheetFormDto);
		saveRefDoc(timesheetFormDto, user);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		TimeSheet timeSheet = timeSheetDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		if (timeSheet != null) {
			if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
				Payroll payroll = payrollDao.getByTimeSheet(timeSheet.getId(), true);
				if (payroll != null) {
					String errorMessage = "Unable to cancel timesheet for corresponding form was created: Payroll - " + payroll.getSequenceNumber();
					bindingResult.reject("workflowMessage", errorMessage);
					currentWorkflowLog.setWorkflowMessage(errorMessage);
					payroll = null;
				}
			}
			timeSheet = null;
		}
	}

	protected void saveEmployeeDtr(TimesheetFormDto timesheetFormDto, Integer userId) {
		List<Domain> toBeSaved = new ArrayList<>();
		List<EmployeeDtr> employeeDtrs = timesheetFormDto.getEmployeeDtrs();
		if(employeeDtrs != null) {
			for (EmployeeDtr dtr : employeeDtrs) {
				toBeSaved.add(dtr);
			}
			if (!toBeSaved.isEmpty()) {
				employeeDtrDao.batchSave(toBeSaved);
			}
		}
	}

	private void saveRefDoc(TimesheetFormDto timesheetFormDto, User user) {
		ReferenceDocument document = new ReferenceDocument();
		document.setFile(timesheetFormDto.getFile());
		document.setFileName(timesheetFormDto.getFileName());
		document.setFileSize(timesheetFormDto.getFileSize());
		document.setDescription(timesheetFormDto.getFileName());
		int ebObjectId = saveAndGetEbObject(user, ReferenceDocument.OBJECT_TYPE_ID, new Date());
		document.setEbObjectId(ebObjectId);
		List<Domain> o2os = new ArrayList<>();
		o2os.add(ObjectToObject.getInstanceOf(timesheetFormDto.getTimeSheet().getEbObjectId(), ebObjectId,
				ReferenceDocument.OR_TYPE_ID, user, new Date()));
		objectToObjectDao.batchSave(o2os);
		referenceDocumentDao.save(document);
	}

	/**
	 * Save and get the eb object id.
	 * @param user The logged user.
	 * @param objectTypeId The object type id.
	 * @param currentDate The current date.
	 * @return The eb object id.
	 */
	public Integer saveAndGetEbObject(User user, Integer objectTypeId, Date currentDate) {
		EBObject ebObject = new EBObject();
		ebObject.setObjectTypeId(objectTypeId);
		AuditUtil.addAudit(ebObject, new Audit(user.getId(), true, currentDate));
		ebObjectDao.save(ebObject);
		return ebObject.getId();
	}

	/**
	 * Saving method for PayrollEmployeeTimeSheet.
	 * @param timeSheet The time sheet object.
	 * @param timesheetFormDto The time sheet form dto.
	 */
	protected void saveEmployeeTimeSheets(TimeSheet timeSheet, TimesheetFormDto timesheetFormDto) {
		int timeSheetId = timeSheet.getId();
		List<PayrollEmployeeTimeSheet> timeSheets = pTimeSheetDao.getAllByRefId("timeSheetId", timeSheetId);
		List<PayrollEmployeeTimeSheet> employeeTimeSheets = timeSheet.getEmployeeTimeSheets();
		List<EmployeeDtr> employeeDtrs = employeeDtrDao.geEmployeeDtrsByTimeSheet(timeSheetId);

		//delete employeeDTR first before deleting timesheet
		Collection<Integer> empDtrToBeDeleted = new ArrayList<>();
		for (EmployeeDtr employeeDtrDel : employeeDtrs) {
			empDtrToBeDeleted.add(employeeDtrDel.getId());
		}
		if (!empDtrToBeDeleted.isEmpty()) {
			employeeDtrDao.delete(empDtrToBeDeleted);
		}
		//delete employee timesheet
		Collection<Integer> petToBeDeleted = new ArrayList<>();
		for (PayrollEmployeeTimeSheet payrollEmployeeTimeSheet : timeSheets) {
			petToBeDeleted.add(payrollEmployeeTimeSheet.getId());
		}
		if (!petToBeDeleted.isEmpty()) {
			pTimeSheetDao.delete(petToBeDeleted);
		}

		List<Domain> toBesavedEDTR = new ArrayList<>();
		List<EmployeeDtr> empDtrs = timesheetFormDto.getEmployeeDtrs();
		for (PayrollEmployeeTimeSheet payrollEmployeeTimeSheet : employeeTimeSheets) {
			payrollEmployeeTimeSheet.setTimeSheetId(timeSheetId);
			pTimeSheetDao.save(payrollEmployeeTimeSheet);
			saveEmployeeDtrs(empDtrs, payrollEmployeeTimeSheet, toBesavedEDTR);
		}

		if(!toBesavedEDTR.isEmpty()) {
			employeeDtrDao.batchSave(toBesavedEDTR);
		}
	}

	protected void saveEmployeeDtrs(List<EmployeeDtr> employeeDtrs,
			PayrollEmployeeTimeSheet payrollEmployeeTimeSheet, List<Domain> toBesavedEDTR) {
		for (EmployeeDtr employeeDtr : employeeDtrs) {
			if(payrollEmployeeTimeSheet.getEmployeeId().equals(employeeDtr.getEmployeeId()) && 
					DateUtil.removeTimeFromDate(payrollEmployeeTimeSheet.getDate())
					.equals(DateUtil.removeTimeFromDate(employeeDtr.getLogTime()))) {
				employeeDtr.setPayrollEmployeeTimesheetId(payrollEmployeeTimeSheet.getId());
				toBesavedEDTR.add(employeeDtr);
			}
		}
	}

	protected void setPeriods(TimeSheet timeSheet) {
		PayrollTimePeriod ptp = 
				payrollTimePeriodDao.get(timeSheet.getPayrollTimePeriodId());
		PayrollTimePeriodSchedule ptps = 
				payrollTimePeriodScheduleDao.get(timeSheet.getPayrollTimePeriodScheduleId());
		timeSheet.setPayrollTimePeriod(ptp);
		timeSheet.setPayrollTimePeriodSchedule(ptps);
		timeSheet.setDateFrom(ptps.getDateFrom());
		timeSheet.setDateTo(ptps.getDateTo());
	}

	protected List<Date> initTSDateHeader (List<PayrollEmployeeTimeSheet> pETimesSheets) {
		List<Date> dateHeaders = new ArrayList<>();
		if (pETimesSheets != null && !pETimesSheets.isEmpty()) {
			Map<Date, Date> hmTS = new HashMap<Date, Date>();
			for (PayrollEmployeeTimeSheet pet : pETimesSheets) {
				if (!hmTS.containsKey(pet.getDate())) {
					hmTS.put(pet.getDate(), pet.getDate());
				}
			}
			dateHeaders = new ArrayList<Date>(hmTS.values());
			hmTS = null;
			Collections.sort(dateHeaders, new Comparator<Date>() {
				@Override
				public int compare(Date d1, Date d2) {
					return d1.compareTo(d2);
				}
			});
		}
		return dateHeaders;
	}

	protected List<TimeSheetViewDto> initTSViewData (List<PayrollEmployeeTimeSheet> pETimesSheets) {
		List<TimeSheetViewDto> views = new ArrayList<>();
		if (pETimesSheets != null && !pETimesSheets.isEmpty()) {
			Map<Integer, TimeSheetViewDto> hmTS = new HashMap<Integer, TimeSheetViewDto>();
			List<Double> hoursWorked = null;
			for (PayrollEmployeeTimeSheet pet : pETimesSheets) {
				TimeSheetViewDto ts = null;
				if (!hmTS.containsKey(pet.getEmployeeId())) {
					ts = new TimeSheetViewDto();
					int employeeId = pet.getEmployeeId();
					ts.setEmployeeId(employeeId);

					Employee employee = employeeDao.get(employeeId);
					ts.setEmployeeNo(employee.getEmployeeNo());
					ts.setEmployeeName(employee.getFullName());
					employee = null;

					hoursWorked = new ArrayList<>();
					hoursWorked.add(pet.getHoursWorked());
					ts.setHoursWorked(hoursWorked);

					hoursWorked.add(pet.getOvertime());
					ts.setOvertimes(hoursWorked);

					hoursWorked.add(pet.getAdjustment());
					ts.setAdjustments(hoursWorked);

					hoursWorked.add(pet.getStatus());
					ts.setEmpStats(hoursWorked);
					hmTS.put(employeeId, ts);
				} else {
					ts = hmTS.get(pet.getEmployeeId());
					ts.getHoursWorked().add(pet.getHoursWorked());
					ts.getHoursWorked().add(pet.getOvertime());
					ts.getHoursWorked().add(pet.getAdjustment());
					ts.getHoursWorked().add(pet.getStatus());
					hmTS.put(pet.getEmployeeId(), ts);
				}
			}
			if (!hmTS.isEmpty()) {
				views = new ArrayList<>(hmTS.values());
			}
		}
		Collections.sort(views, new Comparator<TimeSheetViewDto>(){

			@Override
			public int compare(TimeSheetViewDto o1, TimeSheetViewDto o2) {
				return o1.getEmployeeName().compareTo(o2.getEmployeeName());
			}
		});
		return views;
	}

	/**
	 * Get the payroll with details;
	 * @param payrollId The payroll id.
	 * @return The payroll object.
	 */
	public TimeSheet getTimeSheetWithDetails (Integer timeSheetId) {
		TimeSheet timeSheet = timeSheetDao.get(timeSheetId);
		timeSheet.setEmployeeTimeSheets(pTimeSheetDao.getByTimeSheet(timeSheetId));
		setPeriods(timeSheet);
		return timeSheet;
	}

	/**
	 * Convert the json from client to list TimeSheetDto object.
	 * @param jsonString The json to be converted.
	 * @return The list of TimeSheetDto.
	 */
	public List<TimeSheetDto> convJson2TimeSheet(String jsonString) {
		List<TimeSheetDto> timeSheetDtos = new ArrayList<>();
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<TimeSheetDto>>(){}.getType();
		timeSheetDtos = gson.fromJson(jsonString, type);
		return timeSheetDtos;
	}

	/**
	 * Convert set the time sheet details
	 * @param timeSheetId The time sheet id
	 * @return The time sheet DTO
	 */
	public List<TimeSheetDto> convertTimesheetToDto(Integer timeSheetId){
		List<PayrollEmployeeTimeSheet> timeSheets = null;
		List<TimeSheetDto> timeSheetDtos = new ArrayList<>();
		TimeSheetDto tSheetDto = new TimeSheetDto();
		List<TimeSheetDetailsDto> timeSheetDetailsDtos = null;
		TimeSheetDetailsDto detailsDto = null;
		List<Employee> employees = employeeDao.getEmployeesByTimeSheetId(timeSheetId);
		for (Employee employee : employees) {
			timeSheets = getTimesheetByEmployeeAndTimesheetId(employee.getId(), timeSheetId);
			tSheetDto = new TimeSheetDto();
			timeSheetDetailsDtos = new ArrayList<>();
			tSheetDto.setEmployeeId(employee.getId());
			tSheetDto.setEmployeeNo(employee.getEmployeeNo());
			tSheetDto.setEmployeeName(employee.getFullName());
			tSheetDto.setEmployeeStatus(employee.getEmployeeStatus().getName());
			for (PayrollEmployeeTimeSheet payrollEmployeeTimeSheet : timeSheets) {
				detailsDto = new TimeSheetDetailsDto();
				detailsDto.setDate(payrollEmployeeTimeSheet.getDate());
				detailsDto.setLate(payrollEmployeeTimeSheet.getLate());
				detailsDto.setHoursWork(payrollEmployeeTimeSheet.getHoursWorked());
				detailsDto.setAdjustment(payrollEmployeeTimeSheet.getAdjustment());
				detailsDto.setOvertime(payrollEmployeeTimeSheet.getOvertime());
				detailsDto.setStatus(payrollEmployeeTimeSheet.getStatus());
				detailsDto.setUndertime(payrollEmployeeTimeSheet.getUndertime());
				timeSheetDetailsDtos.add(detailsDto);
			}
			tSheetDto.setTimeSheetDetailsDtos(timeSheetDetailsDtos);
			timeSheetDtos.add(tSheetDto);
		}
		return timeSheetDtos;
	}

	/**
	 * Get the list of payroll employee time sheets
	 * @param employeeId The employee id
	 * @param timeSheetId The time sheet id
	 * @return The list of payroll employee time sheets
	 */
	public List<PayrollEmployeeTimeSheet> getTimesheetByEmployeeAndTimesheetId(Integer employeeId,
			Integer timeSheetId) {
		return pTimeSheetDao.getByEmployeeIdAndTimeSheetId(employeeId, timeSheetId);
	}

	/**
	 * Checks if the timesheet has duplicate time period and time period schedule.
	 * @param timePeriodId The time period id.
	 * @param timePeriodScheduleId The time period schedule id.
	 * @return True if there is already existing, otherwise false.
	 */
	public boolean hasExistingTimeSheet (int timeSheetId, int timePeriodId, int timePeriodScheduleId, int divisionId, int companyId) {
		return timeSheetDao.hasExistingTimeSheet(timeSheetId, timePeriodId, timePeriodScheduleId, divisionId, companyId);
	}

	/**
	 * Get the date from payroll time period schedule
	 * @param timePeriodScheduleId The time period id
	 * @return The day from payroll time period schedule
	 */
	public void getDateFromTimePeriodSchedule(Model model, Integer timePeriodScheduleId,
			Integer month, Integer year) {
		Date startDate = null;
		Date endDate = null;
		if(!timePeriodScheduleId.equals(-1) && !month.equals(-1)) {
			//Set the start date and the end date of the time period schedule
			PayrollTimePeriodSchedule ptps =
					timePeriodService.getPayrollTimePeriodSchedule(timePeriodScheduleId);
			startDate = ptps.getDateFrom();
			endDate = ptps.getDateTo();
		} else if(timePeriodScheduleId.equals(-1) && !month.equals(-1)) {
			//Set the date from the first start date and the last end date of the
			//payroll time period schedule
			List<PayrollTimePeriodSchedule> timePeriods =
					timePeriodService.getTimePeriodSchedules(month, year);
			if(timePeriods != null && !timePeriods.isEmpty()) {
				startDate = timePeriods.get(0).getDateFrom();
				endDate = timePeriods.get(timePeriods.size() - 1).getDateTo();
			} else {
				//Set day from the 1st day to the last day of the month
				Calendar cal = Calendar.getInstance();
				cal.set(year, month -1, 1);
				//Set the start date of the month
				cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
				startDate = cal.getTime();
				//Set the end date of the month
				cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
				endDate = cal.getTime();
			}
		}
		model.addAttribute("dateFrom", startDate);
		model.addAttribute("dateTo", endDate);
	}

	private boolean isMoreThanOneMinDiff (Date firstDate, Date secondDate) {
		long diff = secondDate.getTime() - firstDate.getTime();
		if (NumberFormatUtil.divideWFP(diff, NumberFormatUtil.multiplyWFP(60, 1000)) > MIN_DIFF) {
			return true;
		}
		return false;
	}

	/**
	 * Get the list of time sheets.
	 * @param searchCriteria The Search criteria.
	 * @param user The logged user/
	 * @return The list of time sheets.
	 */
	public List<FormSearchResult> searchTimeSheets(String searchCriteria, User user) {
		List<TimeSheet> timeSheets = timeSheetDao.searchTimeSheets(searchCriteria, user);
		List<FormSearchResult> results = new ArrayList<>();
		List<ResultProperty> properties = null;
		Company company = null;
		Division division = null;
		PayrollTimePeriod timePeriod = null;
		PayrollTimePeriodSchedule schedule = null;
		String status = null;
		String title = null;
		for (TimeSheet ts : timeSheets) {
			properties = new ArrayList<>();
			title = "Time sheet No. " + ts.getSequenceNumber();
			company = companyDao.get(ts.getCompanyId());
			properties.add(ResultProperty.getInstance("Company", company.getNumberAndName()));
			division = divisionDao.get(ts.getDivisionId());
			properties.add(ResultProperty.getInstance("Division", division.getNumberAndName()));
			timePeriod = payrollTimePeriodDao.get(ts.getPayrollTimePeriodId());
			properties.add(ResultProperty.getInstance("Month/Year", getMonthName(timePeriod.getMonth()) + " " + timePeriod.getYear()));
			schedule = payrollTimePeriodScheduleDao.get(ts.getPayrollTimePeriodScheduleId());
			properties.add(ResultProperty.getInstance("Time Period", DateUtil.formatDate(schedule.getDateFrom()) + " - " + 
					DateUtil.formatDate(schedule.getDateTo())));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(ts.getDate())));
			status = ts.getFormWorkflow().getCurrentStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			results.add(FormSearchResult.getInstanceOf(ts.getId(), title, properties));
			company = null;
			division = null;
			timePeriod = null;
			schedule = null;
		}
		return results;
	}

	private String getMonthName(int monthNumber) {
		String[] months = new DateFormatSymbols().getMonths();
		return months[monthNumber-1].toUpperCase();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return null;
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return timeSheetDao.getByEbObjectId(ebObjectId);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return timeSheetDao.get(id).getFormWorkflow();
	}

	/**
	 * Get the paged list of time sheets as reference for payroll.
	 * @param companyId The company filter.
	 * @param divisionId The division filter.
	 * @param month The month filter.
	 * @param year The year filter.
	 * @param payrollTimePeriodScheduleId The time period schedule filter.
	 * @param pageSetting The page setting.
	 * @return The paged list of time sheets.
	 */
	public Page<TimeSheet> getTimeSheets(Integer companyId, Integer divisionId, Integer month, 
			Integer year, Integer payrollTimePeriodScheduleId, User user, int pageNumber) {
		Page<TimeSheet> timeSheet = timeSheetDao.getTimeSheets(companyId, divisionId, month, year, payrollTimePeriodScheduleId, 
				user, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		if (timeSheet != null && timeSheet.getDataSize() > 0) {
			for (TimeSheet ts : timeSheet.getData()) {
				Company company = companyDao.get(ts.getCompanyId());
				Division division = divisionDao.get(ts.getDivisionId());
				PayrollTimePeriod payrollTimePeriod = payrollTimePeriodDao.get(ts.getPayrollTimePeriodId());
				PayrollTimePeriodSchedule payrollTimePeriodSchedule = payrollTimePeriodScheduleDao.get(ts.getPayrollTimePeriodScheduleId());
				ts.setCompany(company);
				ts.setDivision(division);
				ts.setPayrollTimePeriod(payrollTimePeriod);
				ts.setPayrollTimePeriodSchedule(payrollTimePeriodSchedule);
			}
		}
		return timeSheet;
	}

	/**
	 * Validate the time sheet form
	 * @param ts The time sheet object
	 * @param errors The validation error message
	 */
	public void validateTimeSheet(TimeSheet ts, Errors errors) {
		validateTimeSheet(ts, false, errors);
	}

	/**
	 * Validate the time sheet form
	 * @param ts The time sheet object
	 * @param isTimeLogUserInput True if the time logs is user input, otherwise false
	 * @param errors The validation error message
	 */
	public void validateTimeSheet(TimeSheet ts, boolean isTimeLogUserInput, Errors errors) {
		double employeeShiftHrs = 0;
		Employee employee = null;
		EmployeeShift shift = null;

		Integer companyId = ts.getCompanyId();
		if(companyId == null) {
			errors.rejectValue("timeSheet.companyId", null, null, "Company is required.");
		} else if(!companyService.getCompany(companyId).isActive()) {
			errors.rejectValue("timeSheet.companyId", null, null, "Company is inactive.");
		}

		if (ts.getDate() == null) {
			errors.rejectValue("timeSheet.date", null, null, "Date is required field.");
		}

		Integer ptpId = ts.getPayrollTimePeriodId();
		Integer ptpsId = ts.getPayrollTimePeriodScheduleId();
		if (ptpId == null && ptpsId == null) {
			errors.rejectValue("timeSheet.payrollTimePeriodScheduleId", null, null, "There is no Time Period "
					+ "configured for the selected month and year.");
		}

		boolean hasExistingTimeSheet = hasExistingTimeSheet(ts.getId(), ptpId,
				ptpsId, ts.getDivisionId(), companyId);
		if (ptpId != null && ptpsId != null && hasExistingTimeSheet) {
			errors.rejectValue("timeSheet.payrollTimePeriodScheduleId", null, null, 
					"There is already existing payroll with same Company, Division, Month/Year, and Time Period.");
		}

		Map<Integer, String> hmEmployees = new HashMap<>();
		if (!isTimeLogUserInput) {
			if(ts.getTimeSheetDtos() != null && !ts.getTimeSheetDtos().isEmpty()) {
				for (TimeSheetDto tsDto : ts.getTimeSheetDtos()) {
					employee = employeeDao.getEmployeeByNo(tsDto.getEmployeeNo(), ts.getCompanyId());
					Integer employeeId = employee.getId();
					if (employee != null) {
						for (TimeSheetDetailsDto tsdDto : tsDto.getTimeSheetDetailsDtos()) {
							shift = employeeShiftDao.getBySchedule(companyId, ptpId, ptpsId, employeeId,
									tsdDto.getDate());
							if (shift != null) {
								employeeShiftHrs = shift.getDailyWorkingHours();
								double totalHrsWorked = tsdDto.getHoursWork() + tsdDto.getAdjustment();
								if (employeeShiftHrs < totalHrsWorked) {
									errors.rejectValue("timeSheet.employeeTimeSheets", null, null, "Total hours worked of "
											+ tsDto.getEmployeeName() + " for the date of " + DateUtil.formatDate(tsdDto.getDate())
											+ " should not exceed total working hours of employee shift.");
								}
							} else if (!hmEmployees.containsKey(employeeId)) {
								hmEmployees.put(employeeId, employee.getFullName());
							}
						}
					}
				}
			} else {
				errors.rejectValue("timeSheet.employeeTimeSheets", null, null, "At least one time sheet is required.");
			}
		}

		List<String> strEmployees = new ArrayList<String>();
		if (!hmEmployees.isEmpty()) {
			String message = "Incomplete daily shift schedule configuration for the following employee/s:";
			strEmployees = new ArrayList<String>(hmEmployees.values());
			int count = 0;
			for (String str : strEmployees) {
				if (count == 0) {
					message += str;
				} else {
					message += ", " + str;
				}
			}
			errors.rejectValue("timeSheet.employeeTimeSheets", null, null, message);

			// Clear values
			hmEmployees = null;
			strEmployees = null;
		}

		//Validate form status
		FormWorkflow workflow = ts.getId() != 0 ? getFormWorkflow(ts.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("timeSheet.formWorkflowId", null, null, workflowError);
		}
	}

	/**
	 * Get and process the timesheet details for editing
	 * @param timeSheetId The timesheet id
	 * @return The timesheet form DTO
	 */
	public TimesheetFormDto geTimeSheetDetails(Integer timeSheetId) {
		TimesheetFormDto timesheetFormDto = new TimesheetFormDto();
		TimeSheet timeSheet = getTimeSheet(timeSheetId);
		timeSheet.setTimeSheetDtos(convertTimesheetToDto(timeSheet.getId()));
		timesheetFormDto.setReferenceDocument(
				referenceDocumentDao.getRDByEbObject(timeSheet.getEbObjectId()));
		timesheetFormDto.setEmployeeDtrs(getEmployeeDtrs(timeSheet.getCompanyId(),
				timeSheet.getDivisionId(), timeSheet.getPayrollTimePeriodScheduleId(),
				timeSheet.getId()));
		timesheetFormDto.setTimeSheet(timeSheet);
		return timesheetFormDto;
	}

	/**
	 * Convert the list employee DTRs to JSON string from time sheet DTO
	 * @param timeSheetDtos The list of objects than contains the list of employee DTRs.
	 * @return The converted list of employee DTRs to JSON string
	 */
	public String getSerializedDtrs(List<TimeSheetDto> timeSheetDtos) {
		if (timeSheetDtos != null && !timeSheetDtos.isEmpty()) {
			List<EmployeeDtr> employeeDtrs = new ArrayList<>();
			for (TimeSheetDto tsd : timeSheetDtos) {
				if (tsd.getEmployeeDtrs() != null) {
					employeeDtrs.addAll(tsd.getEmployeeDtrs());
				}
			}
			Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy HH:mm:ss").create();
			return gson.toJson(employeeDtrs);
		}
		return "";
	}

	/**
	 * Convert the employee DTRs to JSON string
	 * @param employeeDtrs The list of employee DTRs
	 * @return The converted employee DTRs
	 */
	public String getSerializedEmployeeDtrs(List<EmployeeDtr> employeeDtrs) {
		String strEmployeeDtr = "";
		if (employeeDtrs != null && !employeeDtrs.isEmpty()) {
			Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy HH:mm:ss").create();
			strEmployeeDtr = gson.toJson(employeeDtrs);
		}
		return strEmployeeDtr;
	}

	/**
	 * Convert the JSON string to employee DTRs
	 * @param employeeDtrsJson The employee DTR JSON string
	 * @return The converted employee DTRs
	 */
	public List<EmployeeDtr> getDeserializedDtrs(String employeeDtrsJson) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy HH:mm:ss").create();
		Type type = new TypeToken <List<EmployeeDtr>>(){}.getType();
		return gson.fromJson(employeeDtrsJson, type);
	}

	/**
	 * Convert the multipart file to InputStream.
	 * @param file The multipart file.
	 * @return The converted multipart file.
	 * @throws IOException
	 */
	public InputStream convertMpf2InputStream (MultipartFile file) throws IOException {
		byte [] byteArr = file.getBytes();
		return new ByteArrayInputStream(byteArr);
	}

	/**
	 * Encode the multipart file into base 64 string.
	 * @param file The multipart file.
	 * @return The converted multipart file.
	 * @throws IOException
	 */
	public String encodeFileToBase64Binary(MultipartFile file)
			throws IOException {
		byte[] bytes =  file.getBytes();
		byte[] encoded = Base64.encodeBase64(bytes);
		return new String(encoded);
	}

	/**
	 * Parse and process the daily time recording of the employee.
	 * @param companyId The company id
	 * @param employeeTypeId The employee type id.
	 * @param biometricModelId The biometric model id.
	 * @param dateFrom The date range start.
	 * @param dateTo The date range end.
	 * @return The list of time sheet dto.
	 * @throws ParseException
	 * @throws IOException 
	 * @throws SuppressableStacktraceException 
	 */
	public List<TimeSheetDto> parseAndProcessDTRO (int tId, Integer companyId, Integer divisionId,
			Integer employeeTypeId, int biometricModelId, Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId, 
			Date dateFrom, Date dateTo, MultipartFile mpf) throws ParseException, IOException, SuppressableStacktraceException  {
		return parseAndProcessDTRO(tId, companyId, divisionId, employeeTypeId, 
				biometricModelId, payrollTimePeriodId, payrollTimePeriodScheduleId, dateFrom, dateTo, mpf, false);
	}

	/**
	 * Parse and process the daily time recording of the employee.
	 * @param companyId The company id
	 * @param employeeTypeId The employee type id.
	 * @param biometricModelId The biometric model id.
	 * @param dateFrom The date range start.
	 * @param dateTo The date range end.
	 * @return The list of time sheet dto.
	 * @throws ParseException
	 * @throws IOException 
	 * @throws SuppressableStacktraceException 
	 */
	public List<TimeSheetDto> parseAndProcessDTRO (int tId, Integer companyId, Integer divisionId,
			Integer employeeTypeId, int biometricModelId, Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId, 
			Date dateFrom, Date dateTo, MultipartFile mpf, boolean isAutoReflectOT) throws ParseException, IOException, SuppressableStacktraceException  {
		PayrollDataHandler dataHandler = new PayrollDataHandler();

		PayrollParser parser = PayrollParserFactory.getParser(biometricModelId);
		// Get day before start until of payroll period
		Date dayBeforeStart = DateUtil.deductDaysToDate(dateFrom, 1);
		parser.parseData(dayBeforeStart, dateTo, convertMpf2InputStream(mpf), dataHandler);

		Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs = dataHandler.getParseData();
		return parseAndProcessDTR(tId, bioId2Dtrs, dateFrom, dateTo, companyId, divisionId, payrollTimePeriodId,
				payrollTimePeriodScheduleId, isAutoReflectOT, employeeTypeId);
	}

	/**
	 * Handle the parsing and processing of employee DTR.
	 * @throws ParseException 
	 * @throws SuppressableStacktraceException 
	 */
	public List<TimeSheetDto> parseAndProcessDTR(int tId, Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs, Date startDate,
			Date endDate, Integer companyId, Integer divisionId, Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId,
			boolean isAutoReflectOT, Integer employeeTypeId) throws ParseException, SuppressableStacktraceException {
		List<TimeSheetDto> timeSheets = parseAndProcessTimeSheet(tId, bioId2Dtrs, startDate, endDate, companyId,
				divisionId, payrollTimePeriodId, payrollTimePeriodScheduleId, isAutoReflectOT, employeeTypeId);
		Collections.sort(timeSheets, new Comparator<TimeSheetDto>() {
			@Override
			public int compare(TimeSheetDto o1, TimeSheetDto o2) {
				return o1.getEmployeeName().toLowerCase().compareTo(o2.getEmployeeName().toLowerCase());
			}
		});
		return timeSheets;
	}

	private List<TimeSheetDto> parseAndProcessTimeSheet(int tId, Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs, Date dateFrom,
			Date dateTo, Integer companyId, Integer divisionId, Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId,
			boolean isAutoReflectOT, Integer employeeTypeId) throws ParseException, SuppressableStacktraceException {
		List<TimeSheetDto> timeSheetDtos = new ArrayList<>();
		List<Date> dates = getPayrollDates(dateFrom, dateTo);
		timeSheetDtos = getEmployeesTimeSheet(tId, bioId2Dtrs, dates, companyId, divisionId,
				payrollTimePeriodId, payrollTimePeriodScheduleId, isAutoReflectOT, employeeTypeId);
		return timeSheetDtos;
	}

	/**
	 * Get the list of time sheet dto.
	 * @param bioId2Dtrs The EmployeeDtr data from biometric.
	 * @param dates The list of dates.
	 * @param companyId The company id
	 * @return The list of time sheet dto.
	 * @throws ParseException 
	 * @throws SuppressableStacktraceException 
	 */
	public List<TimeSheetDto> getEmployeesTimeSheet(int tId, Map<Integer, List<eulap.eb.payroll.EmployeeDtr>> bioId2Dtrs, List<Date> dates,
			Integer companyId, Integer divisionId, Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId, boolean isAutoReflectOT,
			Integer employeeTypeId) throws ParseException, SuppressableStacktraceException {
		List<TimeSheetDto> timeSheetDtos = new ArrayList<>();
		List<Integer> employeeIds = new ArrayList<>();
		Employee parsedEmployee = null;
		for (Map.Entry<Integer, List<eulap.eb.payroll.EmployeeDtr>> eEmployee2Dtr : bioId2Dtrs.entrySet()) {
			Integer biometricId = eEmployee2Dtr.getKey();
			parsedEmployee = employeeDao.getEmployeeByBiometricIdCompAndEmpType(biometricId, companyId, employeeTypeId, divisionId);
			if (parsedEmployee == null || !parsedEmployee.isActive()) {
				continue;
			}
			if (parsedEmployee.getDivisionId().equals(divisionId)){
				List<eulap.eb.payroll.EmployeeDtr> dtrs = eEmployee2Dtr.getValue();
				TimeSheetDto tsDtp = processEmployeeTimeSheet(tId, dates, parsedEmployee, dtrs, payrollTimePeriodId, payrollTimePeriodScheduleId, isAutoReflectOT);
				timeSheetDtos.add(tsDtp);
				employeeIds.add(parsedEmployee.getId());
			}
		}
		List<Employee> employees = employeeDao.getEmployees(companyId, divisionId, employeeTypeId);
		for (Employee employee : employees) {
			if(!employeeIds.contains(employee.getId())){
				TimeSheetDto tsDtp = appenedEmployeeTimesheet(tId, payrollTimePeriodId, payrollTimePeriodScheduleId,employee, dates);
				timeSheetDtos.add(tsDtp);
			}
		}
		return timeSheetDtos;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		TimeSheet timeSheet = getTimeSheet(timeSheetDao.getByEbObjectId(ebObjectId).getId());
		Integer pId = timeSheet.getId();
		FormProperty property = workflowHandler.getProperty(timeSheet.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = timeSheet.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Timesheet - " + timeSheet.getSequenceNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + timeSheet.getCompany().getName())
				.append(" " + timeSheet.getPayrollTimePeriod().getName())
				.append(" " + timeSheet.getPayrollTimePeriodSchedule().getName());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case TimeSheet.OBJECT_TYPE_ID:
			return timeSheetDao.getByEbObjectId(ebObjectId);
		case PayrollEmployeeTimeSheet.PET_OBJECT_TYPE:
			return pTimeSheetDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Generate data source for time sheet template.
	 * @param payrollTimePeriodId The time period id.
	 * @param payrollTimePeriodScheduleId The time period schedule id.
	 * @param divisionId The division id.
	 * @param companyId The company id.
	 * @return The generated data source for the template.
	 */
	public JRDataSource downTimesheetTemplate(Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId,
			Integer divisionId, Integer companyId) {
		EBJRServiceHandler<TimeSheetTemplateDto>  handler = new JRTimeTemplateHandler(payrollTimePeriodId,
				payrollTimePeriodScheduleId, divisionId, companyId, this);
		return new EBDataSource<TimeSheetTemplateDto>(handler);
	}

	private static class JRTimeTemplateHandler implements EBJRServiceHandler<TimeSheetTemplateDto> {
		private Integer payrollTimePeriodId;
		private Integer payrollTimePeriodScheduleId;
		private Integer divisionId;
		private Integer companyId;
		private TimeSheetService timeSheetService;

		private JRTimeTemplateHandler (Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId,
				Integer divisionId, Integer companyId, TimeSheetService timeSheetService){
			this.payrollTimePeriodId = payrollTimePeriodId;
			this.companyId = companyId;
			this.payrollTimePeriodScheduleId = payrollTimePeriodScheduleId;
			this.divisionId = divisionId;
			this.timeSheetService = timeSheetService;
		}

		@Override
		public void close() throws IOException {
			timeSheetService = null;
		}

		@Override
		public Page<TimeSheetTemplateDto> nextPage(PageSetting pageSetting) {
			return timeSheetService.timeSheetDao.getTimesheetTemplateDetails(payrollTimePeriodId,
					payrollTimePeriodScheduleId, divisionId, companyId, pageSetting);
		}
	}

	public Map<Date, List<eulap.eb.payroll.EmployeeDtr>> mapEeDtrs(Map<Date, List<eulap.eb.payroll.EmployeeDtr>> hmDtrs,
			List<eulap.eb.payroll.EmployeeDtr> copyDtrs, int employeeId) {
		List<eulap.eb.payroll.EmployeeDtr> eeDtrs = null;
		Date logInDate = null;
		try {
			Date prevLogInDate = null;
			for(eulap.eb.payroll.EmployeeDtr copyDtr : copyDtrs) {
				logInDate = updateLogInDate(employeeId, DateUtil.removeTimeFromDate(copyDtr.getDate()), copyDtr);
				if(logInDate != null) {
					if(!hmDtrs.containsKey(logInDate)) {
						if(!hmDtrs.isEmpty()) {
							hmDtrs.put(prevLogInDate, removeRedundantLogs(hmDtrs.get(prevLogInDate)));
						}
						eeDtrs = new ArrayList<>();
						eeDtrs.add((eulap.eb.payroll.EmployeeDtr) copyDtr.clone());
						hmDtrs.put(logInDate, eeDtrs);
						prevLogInDate = logInDate;
					} else {
						hmDtrs.get(logInDate).add((eulap.eb.payroll.EmployeeDtr) copyDtr.clone());
						eeDtrs = new ArrayList<>();
					}
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		eeDtrs = null;
		logInDate = null;
		return hmDtrs;
	}

	// Check and update login date for no in dtr logs of night shift employee shift.
	private Date updateLogInDate(int employeeId, Date logInDate, eulap.eb.payroll.EmployeeDtr eeDtr) {
		if (eeDtr != null) {
			Date prevDay = DateUtil.deductDaysToDate(logInDate, 1);
			Date nextDay = DateUtil.addDaysToDate(logInDate, 1);
			EmployeeShift currDayEeShift = employeeProfileService.getEeCurrentShift(employeeId, logInDate);
			EmployeeShift prevDayEeShift = employeeProfileService.getEeCurrentShift(employeeId, prevDay);
			EmployeeShift nextDayEeShift = employeeProfileService.getEeCurrentShift(employeeId, nextDay);
			Date actualLogDate = eeDtr.getDate();
			if ((prevDayEeShift != null && prevDayEeShift.isNightShift() && currDayEeShift != null)) {
				Date currDayShiftStart = DateUtil.appendTimeToDate(currDayEeShift.getFirstHalfShiftStart(), logInDate);
				Date prevDayShiftEnd = DateUtil.appendTimeToDate(prevDayEeShift.getSecondHalfShiftEnd(),
						prevDayEeShift.isNightShift() ? logInDate : prevDay);
				double diffStart = Math.abs(getTimeDifference(currDayShiftStart, actualLogDate));
				double diffEnd = Math.abs(getTimeDifference(prevDayShiftEnd, actualLogDate));

				if ((diffStart > diffEnd)) {
					logInDate = prevDay;
				}
			} else if(prevDayEeShift != null && prevDayEeShift.isNightShift() && currDayEeShift == null) {
				logInDate = prevDay;
			} else if(nextDayEeShift != null && currDayEeShift == null && prevDayEeShift == null) {
				logInDate = nextDay;
			} else if(nextDayEeShift != null && currDayEeShift != null) { //For logs that are before 12mn but the shift starts during or after midnight or reverse.
				Date prevDayLogOut = null;
				Date nextDayLogIn = null;
				Date currDayLogIn = DateUtil.appendTimeToDate(currDayEeShift.getFirstHalfShiftStart(), logInDate);
				Date currDayLogOut = !currDayEeShift.isNightShift() ?  DateUtil.appendTimeToDate(currDayEeShift.getSecondHalfShiftEnd(), logInDate)
						: DateUtil.appendTimeToDate(currDayEeShift.getSecondHalfShiftEnd(), nextDay);
				if(prevDayEeShift == null) {
					Date nightShiftCompareDate = DateUtil.addDaysToDate(actualLogDate, 1);
					double eeDtrToLoginDiff = Math.abs(getTimeDifference(currDayLogIn, actualLogDate));
					double eeDtrToLogOUtDiff = Math.abs(getTimeDifference(currDayLogOut, currDayEeShift.isNightShift() ? nightShiftCompareDate : actualLogDate));
					if(eeDtrToLoginDiff > eeDtrToLogOUtDiff) {
						return logInDate;
					}
				}
				if(prevDayEeShift != null) {
					prevDayLogOut = !prevDayEeShift.isNightShift() ?  DateUtil.appendTimeToDate(prevDayEeShift.getSecondHalfShiftEnd(), prevDay)
							: DateUtil.appendTimeToDate(prevDayEeShift.getSecondHalfShiftEnd(), logInDate);
				} else {
					nextDayLogIn = DateUtil.appendTimeToDate(nextDayEeShift.getFirstHalfShiftStart(), nextDay);
				}
				double diffPrevDayLogOut = prevDayLogOut != null ? Math.abs(getTimeDifference(prevDayLogOut, actualLogDate)) : 0;
				double diffCurrDayLogIn = Math.abs(getTimeDifference(currDayLogIn, actualLogDate));
				double diffCurrDayLogOut = Math.abs(getTimeDifference(currDayLogOut, actualLogDate));
				double diffNextDayLogIn = nextDayLogIn != null ? Math.abs(getTimeDifference(nextDayLogIn, actualLogDate)) : 0;
				if(nextDayLogIn != null && (diffCurrDayLogOut > diffNextDayLogIn)) {
					logInDate = nextDay;
				} else if(prevDayLogOut != null && (diffCurrDayLogIn > diffPrevDayLogOut)) {
					logInDate = prevDay;
				}
			}
		}
		return logInDate;
	}

	private List<eulap.eb.payroll.EmployeeDtr> removeRedundantLogs(List<eulap.eb.payroll.EmployeeDtr> dtrs) {
		sortDates(dtrs);
		// remove the duplicate logs
		List<eulap.eb.payroll.EmployeeDtr> ret = new ArrayList<>();
		eulap.eb.payroll.EmployeeDtr currentDtr = null;
		Date date = null;
		int length = dtrs.size();
		for (int i=0; i<length; i++) {
			currentDtr = dtrs.get(i);
			date = currentDtr.getDate();
			if (i >= 1 && !isMoreThanFiveMinDiff(dtrs.get(i-1).getDate(), date)) {
				continue;
			}
			ret.add(currentDtr);
		}
		return ret;
	}

	private boolean isMoreThanFiveMinDiff (Date firstDate, Date secondDate) {
		Long diff = secondDate.getTime() - firstDate.getTime();
		if (NumberFormatUtil.divideWFP(diff.doubleValue(), NumberFormatUtil.multiplyWFP(60 * 1000, 1000)) > 5)  {
			return true;
		}
		return false;
	}

	private void sortDates(List<eulap.eb.payroll.EmployeeDtr> dtrs) {
		// Sort EDTR logs set to ascending order
		Collections.sort(dtrs, new Comparator<eulap.eb.payroll.EmployeeDtr>() {
			@Override
			public int compare(eulap.eb.payroll.EmployeeDtr e1, eulap.eb.payroll.EmployeeDtr e2) {
				if (e1.getDate().after(e2.getDate())) {
					return 1;
				} else if (e1.getDate().equals(e2.getDate())) {
					return 0;
				} else {
					return -1;
				}
			}
		});
	}
}
