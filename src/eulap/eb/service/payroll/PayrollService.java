package eulap.eb.service.payroll;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

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
import eulap.eb.dao.DeductionTypeDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeDeductionDao;
import eulap.eb.dao.EmployeeSalaryDetailDao;
import eulap.eb.dao.EmployeeShiftDao;
import eulap.eb.dao.EmployeeTypeDao;
import eulap.eb.dao.HolidaySettingDao;
import eulap.eb.dao.LeaveDetailDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.PayrollDao;
import eulap.eb.dao.PayrollEmployeeSalaryDao;
import eulap.eb.dao.PayrollEmployeeTimeSheetDao;
import eulap.eb.dao.PayrollTimePeriodDao;
import eulap.eb.dao.PayrollTimePeriodScheduleDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.TimeSheetDao;
import eulap.eb.dao.UserCompanyDao;
import eulap.eb.dao.UserDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.BiometricModel;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.DeductionType;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeDeduction;
import eulap.eb.domain.hibernate.EmployeeSalaryDetail;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.EmployeeType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.HolidaySetting;
import eulap.eb.domain.hibernate.HolidayType;
import eulap.eb.domain.hibernate.LeaveDetail;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.PayrollEmployeeSalary;
import eulap.eb.domain.hibernate.PayrollEmployeeTimeSheet;
import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.TimeSheet;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;
import eulap.eb.service.CompanyService;
import eulap.eb.service.PagibigComputer;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.service.PhilHealthComputer;
import eulap.eb.service.SssComputer;
import eulap.eb.service.TimeSheetService;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.EmployeeDeductionDto;
import eulap.eb.web.dto.EmployerEmployeeContributionDTO;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.PayrollDto;
import eulap.eb.web.dto.PayrollEmplSalaryRecordDto;
import eulap.eb.web.dto.PayrollRecordDetailsDto;
import eulap.eb.web.dto.ResultProperty;
import eulap.eb.web.dto.webservice.EmployeeSalaryDTO;
import eulap.eb.web.dto.webservice.TimeSheetDetailsDto;
import eulap.eb.web.dto.webservice.TimeSheetDto;
import eulap.eb.webservice.WebServiceCredential;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Class that handles business logic of {@link Payroll}


 *
 */
@Service
public class PayrollService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(PayrollService.class);
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private PayrollDao payrollDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private EmployeeSalaryDetailDao salaryDetailDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PayrollEmployeeSalaryDao payrollEmployeeSalaryDao;
	@Autowired
	private PayrollEmployeeTimeSheetDao timeSheetDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private EmployeeTypeDao employeeTypeDao;
	@Autowired
	private UserCompanyDao userCompanyDao;
	@Autowired
	private PayrollTimePeriodDao timePeriodDao;
	@Autowired
	private PayrollTimePeriodScheduleDao timePeriodScheduleDao;
	@Autowired
	private HolidaySettingDao holidaySettingDao;
	@Autowired
	private PayrollTimePeriodService timePeriodService;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private TimeSheetDao tsDao;
	@Autowired
	private EmployeeDeductionDao employeeDeductionDao;
	@Autowired
	private DeductionTypeDao deductionTypeDao;
	@Autowired
	private LeaveDetailDao leaveDetailDao;
	@Autowired
	private EmployeeShiftDao employeeShiftDao;
	@Autowired
	private BiometricModelDao modelDao;
	@Autowired
	private TimeSheetService timeSheetService;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private PayrollTimePeriodDao payrollTimePeriodDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;

	private static final double HOLIDAY_PERCENTAGE = 0.30;
	private static final double SPECIAL_HOLIDAY_PERCENTAGE = 0.50;
	private static final int CONTRACTUAL_COUNT_DAYS = 26;

	private static final double REGULAR_OT_MULTIPLIER = 1.25;
	private static final double SPECIAL_DAY_OT_MULTIPLIER = 1.30;

	/**
	 * Get the payroll object
	 * @param id The payroll id
	 * @return The payroll object
	 */
	public Payroll getPayroll(int id) {
		return payrollDao.get(id);
	}

	/**
	 * Get the payroll object from database and other associated records when isEdit is true.
	 * @param payrollId The payroll filter.
	 * @param isEdit True to get other related records.
	 * @return The payroll object.
	 */
	public Payroll getPayroll(Integer payrollId, boolean isEdit) {
		if (isEdit) {
			Payroll payroll = payrollDao.get(payrollId);
			payroll.setCompany(companyDao.get(payroll.getCompanyId()));
			payroll.setDivision(divisionDao.get(payroll.getDivisionId()));
			payroll.setPayrollTimePeriod(timePeriodDao.get(payroll.getPayrollTimePeriodId()));
			payroll.setPayrollTimePeriodSchedule(timePeriodScheduleDao.get(payroll.getPayrollTimePeriodScheduleId()));
			payroll.setPayrollEmployeeSalaries(getPayrollEmployeeSalaryDetails(payroll.getId()));
			return payroll;
		}
		return payrollDao.get(payrollId);
	}

	/**
	 * Checks if the payroll has duplicate time period and time period schedule.
	 * @param timePeriodId The time period id.
	 * @param timePeriodScheduleId The time period schedule id.
	 * @param divisionId The division id.
	 * @param companyId The company id
	 * @return True if there is already existing, otherwise false.
	 */
	public boolean hasExistingPayroll (int payrollId, int timePeriodId, int timePeriodScheduleId, int divisionId, int companyId) {
		return payrollDao.hasExistingPayroll(payrollId, timePeriodId, timePeriodScheduleId, divisionId, companyId);
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return payrollDao.getByEbObjectId(ebObjectId);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return payrollDao.get(id).getFormWorkflow();
	}

	/**
	 * Get the list of active biometric models.
	 * @return The list of active biometric models.
	 */
	public List<BiometricModel> getBiometricModels() {
		return modelDao.getAllActive();
	}

	/**
	 * Compute the employees salary
	 * @param companyId The company id.
	 */
	public List<EmployeeSalaryDTO> computeEmployeesSalary(int payrollId, Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId, 
		List<TimeSheetDto> timeSheets, int companyId, String totalDeductions, boolean isOverrideContribComputation) {
		List<EmployeeSalaryDTO> employeeSalaries = new ArrayList<EmployeeSalaryDTO>();
		List<EmpDeduction> deductions = getDeductions(totalDeductions);
		double totalEeDeductions = 0;
		Employee employee = null;
		for (TimeSheetDto timeSheet : timeSheets) {
			totalEeDeductions = 0;
			for (EmpDeduction ed : deductions) {
				if (ed.getEmployeeId() == timeSheet.getEmployeeId()) {
					totalEeDeductions = ed.getDeduction();
					break;
				}
			}
			employee = employeeDao.get(timeSheet.getEmployeeId());
			EmployeeSalaryDTO employeeSalary = computeSalary(payrollId, payrollTimePeriodId,
					payrollTimePeriodScheduleId, timeSheet, employee, totalEeDeductions, isOverrideContribComputation);
			employeeSalaries.add(employeeSalary);
		}
		return employeeSalaries;
	}

	protected static class EmpDeduction {
		private int employeeId;
		private double deduction;

		private EmpDeduction (int employeeId, double deduction) {
			this.employeeId = employeeId;
			this.deduction = deduction;
		}

		public int getEmployeeId() {
			return employeeId;
		}

		public double getDeduction() {
			return deduction;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("EmpDeduction [employeeId=").append(employeeId).append(", deduction=").append(deduction)
					.append("]");
			return builder.toString();
		}
	}


	/**
	 * Process the list of deductions
	 * @param totalDeductions The list of total deductions
	 * @return The total deductions
	 */
	protected List<EmpDeduction> getDeductions(String totalDeductions) {
		List<EmpDeduction> deductions = new ArrayList<>();
		if (totalDeductions != null && !totalDeductions.trim().isEmpty()) {
			String arr[] = totalDeductions.split("-");
			for (String a : arr) {
				String tmp[] = a.split(";");
				int employeeId = Integer.parseInt(tmp[0]);
				double deduction = Double.parseDouble(tmp[1]);
				deductions.add(new EmpDeduction(employeeId, deduction));
			}
		}
		return deductions;
	}

	/**
	 * Process and compute employee salary
	 * @param payrollId The payroll id
	 * @param payrollTimePeriodId The payroll time period id
	 * @param payrollTimePeriodScheduleId The payroll time period schedule id
	 * @param timeSheet The timesheet object
	 * @param employee The employee object
	 * @param totalDeduction The total employee deduction
	 * @return The processed employee salary
	 */
	protected EmployeeSalaryDTO computeSalary(int payrollId, Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId,
			TimeSheetDto timeSheet, Employee employee, double totalDeduction, boolean isOverrideContribComputation) {
		double totalHolidayPay = 0.0;
		double totalLate = 0.0;
		double totalOvertime = 0.0;
		double totalCola = 0.0;
		double totalHoursWorked = 0.0;
		double totalDailyPay = 0.0;
		double totalPaidLeaves = 0.0;
		LeaveDetail ld = null;
		EmployeeShift shift = null;
		Integer employeeId = employee.getId();
		Integer companyId = employee.getCompanyId();
		Date previousDate = null;
		double previousDayHoursWorked = 0;
		int cnt = 0;
		EmployeeSalaryDetail salaryDetail = salaryDetailDao.getSalaryDetailByEmployeeId(employeeId);
		PayrollEmployeeTimeSheet prevTimeSheet = timeSheetDao.getByEmployeeId(employeeId);
		for (TimeSheetDetailsDto tsDetail : timeSheet.getTimeSheetDetailsDtos()) {
			shift = employeeShiftDao.getBySchedule(companyId, payrollTimePeriodId, payrollTimePeriodScheduleId,
					employeeId, tsDetail.getDate());
			if (cnt == 0) {
				if(prevTimeSheet != null && DateUtil.removeTimeFromDate(prevTimeSheet.getDate())
						.equals(DateUtil.removeTimeFromDate(
								DateUtil.deductDaysToDate(tsDetail.getDate(), 1)))) {
					previousDayHoursWorked = prevTimeSheet.getDaysWorked();
				}
				cnt++;
			}
			if (shift != null) {
				if (salaryDetail == null) {
					throw new RuntimeException("No salary detail for " + employee.getFullName());
				}
				int shiftId = shift.getId();
				double dailyWorkingHours = shift.getDailyWorkingHours();
				double dailyRate = salaryDetail.getDailySalary();
				double hoursWorked = tsDetail.getHoursWork() + tsDetail.getAdjustment();
				double cola = salaryDetail.getEcola();
				double late = tsDetail.getLate();
				Date date = tsDetail.getDate();
				if (hoursWorked != 0) {
					totalHoursWorked = (hoursWorked + late);
					totalDailyPay += totalHoursWorked * (dailyRate / dailyWorkingHours);

					// Compute the total COLA
					totalCola += hoursWorked * (cola / dailyWorkingHours);
				}

				boolean isHolidayOrDayOff = isHoliday(date, companyId) || timeSheetService.isDayOff(shiftId, date);
				totalOvertime += computeOvertime(tsDetail.getOvertime(), dailyRate, dailyWorkingHours, isHolidayOrDayOff);

				if (late > 0) {
					totalLate += computeLate(late, dailyRate, dailyWorkingHours);
				}

				ld = leaveDetailDao.getByEmployeeAndDate(employeeId, date);
				if (ld != null && ld.isPaid()) {
					if (ld.isHalfDay()) {
						totalPaidLeaves += dailyRate / 2;
					} else {
						totalPaidLeaves += dailyRate;
					}
				}

				if (employee.getEmployeeTypeId().equals(EmployeeType.TYPE_REGULAR)) {
					previousDate = DateUtil.deductDaysToDate(date, 1);
					EmployeeShift prevShift = employeeShiftDao.getBySchedule(companyId, payrollTimePeriodId,
							payrollTimePeriodScheduleId, employeeId, previousDate);
					Integer prevShiftId = prevShift != null ? prevShift.getId() : shiftId;
					if ((previousDayHoursWorked > 0 || timeSheetService.isDayOff(prevShiftId, previousDate))
							|| hasLeave(employeeId, previousDate) || isHoliday(previousDate, companyId)) {
						totalHolidayPay += computeHolidayPayAddCola(date, dailyRate, companyId, hoursWorked, cola, shiftId);
					}
					prevShift = null;
					prevShiftId = null;
				} else {
					totalHolidayPay += computeHolidayPayAddCola(date, dailyRate, companyId, hoursWorked, cola, shiftId);
				}
				previousDayHoursWorked = hoursWorked;
			}
		}
		EmployeeSalaryDTO employeeSalary = new EmployeeSalaryDTO();
		employeeSalary.setEmployeeNo(employee.getEmployeeNo());
		employeeSalary.setEmployeeName(timeSheet.getEmployeeName());
		employeeSalary.setEmployeeStatus(timeSheet.getEmployeeStatus());
		employeeSalary.setBasicPay(totalDailyPay);
		employeeSalary.setCola(totalCola);
		employeeSalary.setOvertime(totalOvertime);
		employeeSalary.setSundayHolidayPay(totalHolidayPay);
		employeeSalary.setDeduction(totalDeduction);
		employeeSalary.setLateAbsent(totalLate);
		employeeSalary.setPaidLeave(totalPaidLeaves);

		double taxableIncome = 0;
		double compensation = 0;
		double deduction = 0;

		PayrollTimePeriodSchedule ptps = timePeriodScheduleDao.get(payrollTimePeriodScheduleId);
		if (ptps.isComputeContributions() && !isOverrideContribComputation) {
			// Compute contributions
			processContributions(employee, employeeSalary, salaryDetail);
		}

		//	Compensation
		compensation += employeeSalary.getBasicPay();
		compensation += employeeSalary.getCola();
		compensation += employeeSalary.getBonus();
		compensation += employeeSalary.getOvertime();
		compensation += employeeSalary.getSundayHolidayPay();
		compensation += employeeSalary.getPaidLeave();
		employeeSalary.setGrossPay(compensation);

		//	Deduction
		deduction += employeeSalary.getSss();
		deduction += employeeSalary.getPagibig();
		deduction += employeeSalary.getPhilHealth();
		deduction += employeeSalary.getLateAbsent();
		deduction += employeeSalary.getDeduction();

		//Will temporarily disable withholding tax computation.
		// Compute employee withholding tax based on the gross pay of the employee
//		WTEmployeeStatus status = WTEmployeeStatus.getStatus(employee.getEmployeeStatusId());
		// Set default period to semi-monthly
		// TODO : I will check if this will be one per payroll
//		double withholdingTax = WithholdingTaxComputer.computeWithholdingTax(WithholdingTaxComputer.SEMI_MONTHLY_PERIOD, status, compensation);
//		deduction += withholdingTax;
//		employeeSalary.setWithholdingTax(withholdingTax);

		taxableIncome = NumberFormatUtil.roundOffTo2DecPlaces(compensation - deduction);
		double adjustment = employeeSalary.getAdjustment() != null ? employeeSalary.getAdjustment() : 0;
		employeeSalary.setNetPay(taxableIncome + adjustment);
		return employeeSalary;
	}

	/**
	 * Check if the provided date is a holiday.
	 * @param date The provided date.
	 * @param companyId The company id.
	 * @return True if the provided date is a holiday, otherwise false.
	 */
	protected boolean isHoliday(Date date, Integer companyId) {
		return holidaySettingDao.isHoliday(date, companyId);
	}

	/**
	 * Updated holiday pay computation based DOLE holiday pay rules out as of 16 December 2019
	 * @param date The holiday date
	 * @param dailyRate The employee daily rate
	 * @param companyId The company id
	 * @param hoursWorked The total hours worked
	 * @param cola The employee COLA
	 * @param shiftId The employee shift object id
	 * @return The computed holiday pay
	 */
	protected double computeHolidayPayAddCola(Date date, double dailyRate, Integer companyId,
			double hoursWorked, double cola, int shiftId) {
		boolean isDayOff = timeSheetService.isDayOff(shiftId, date);
		if (isHoliday(date, companyId)) {
			HolidaySetting holidaySetting = holidaySettingDao.getByDate(date);
			Integer holidayTypeId = holidaySetting.getHolidayTypeId();
			double holidayPay = dailyRate + cola;
			if (holidayTypeId.equals(HolidayType.TYPE_REGULAR)) {
				if (isDayOff && hoursWorked > 0) {
					return holidayPay + ((dailyRate * HOLIDAY_PERCENTAGE) * 2);
				}
				return holidayPay;
			} else if (holidayTypeId.equals(HolidayType.TYPE_SPECIAL_NON_WORKING)) {
				if (hoursWorked > 0) {
					if (isDayOff) {
						return (dailyRate * SPECIAL_HOLIDAY_PERCENTAGE);
					}
					return (dailyRate * HOLIDAY_PERCENTAGE);
				}
			}
		}
		return 0;
	}

	protected double computeHolidayPay(Date date, double dailyRate, Integer companyId, double hoursWorked) {
		if (isHoliday(date, companyId)) {
			HolidaySetting holidaySetting = holidaySettingDao.getByDate(date);
			if (holidaySetting.getHolidayTypeId().equals(HolidayType.TYPE_REGULAR)) {
				return dailyRate;
			} else if(holidaySetting.getHolidayTypeId().equals(HolidayType.TYPE_SPECIAL_NON_WORKING)) {
				if(hoursWorked > 0) {
					return dailyRate * HOLIDAY_PERCENTAGE;
				}
			}
		}
		return 0;
	}

	protected double computeLate(double late, double dailyRate, double dailyWorkingHours) {
		return ((dailyRate / dailyWorkingHours) * late);
	}

	protected boolean hasLeave(Integer employeeId, Date currentDate) {
		return leaveDetailDao.hasLeave(employeeId, currentDate);
	}

	/**
	 * Compute the total overtime pay
	 * @param overtime The overtime hours
	 * @param dailyRate The daily rate
	 * @param dailyWorkingHours The daily working hours
	 * @param isHolidayOrDayOff True if holiday or day off
	 * @return The computed overtime pay
	 */
	protected double computeOvertime(double overtime, double dailyRate, double dailyWorkingHours, boolean isHolidayOrDayOff) {
		double hourlyRate = dailyRate / dailyWorkingHours;
		double overtimeRate = hourlyRate * (isHolidayOrDayOff ? SPECIAL_DAY_OT_MULTIPLIER : REGULAR_OT_MULTIPLIER);
		return overtime * overtimeRate;
	}

	/**
	 * Process/set the values for contributions.
	 * It includes both for employee & employer.
	 * @param employeeSalary The employee salary DTO.
	 * @param salaryDetail The {@link EmployeeSalaryDetail} object which 
	 * contains data needed for processing.
	 */
	protected void processContributions (Employee employee, EmployeeSalaryDTO employeeSalary, EmployeeSalaryDetail salaryDetail) {
		double monthlySalary = salaryDetail.getDailySalary() * CONTRACTUAL_COUNT_DAYS;
		// SSS
		if (!salaryDetail.isExcludeSss()) {
			employeeSalary.setSss(SssComputer.getEmployeeContribution(monthlySalary));
			employeeSalary.setSssEr(SssComputer.getEmployerContribution(monthlySalary));
			employeeSalary.setSssEc(SssComputer.getEc(monthlySalary));
		}

		// Philhealth
		if (!salaryDetail.isExcludePhic()) {
			employeeSalary.setPhilHealth(PhilHealthComputer.getEmployeeContrib(monthlySalary));
			employeeSalary.setPhilHealthEr(PhilHealthComputer.getEmployerContrib(monthlySalary));
		}

		// Pag-Ibig
		if (!salaryDetail.isExcludeHdmf()) {
			employeeSalary.setPagibig(PagibigComputer.getEmployeeContribution(monthlySalary));
			employeeSalary.setPagibigEr(PagibigComputer.getEmployerContribution(monthlySalary));
		}
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
	 * @param timeSheetDtos The time sheet dto.
	 * @param companyId The company id. 
	 * @return The processed time sheet.
	 */
	public List<PayrollEmployeeTimeSheet> processEmployeeTimeSheet(List<TimeSheetDto> timeSheetDtos, int companyId) {
		List<PayrollEmployeeTimeSheet> employeeTimeSheets = new ArrayList<>();
		PayrollEmployeeTimeSheet employeeTimeSheet = null;
		Employee e = null;
		for (TimeSheetDto timeSheetDto : timeSheetDtos) {
			e = employeeDao.getEmployeeByNo(timeSheetDto.getEmployeeNo(), companyId);
			for (TimeSheetDetailsDto sheetDetailsDto : timeSheetDto.getTimeSheetDetailsDtos()) {
				employeeTimeSheet = new PayrollEmployeeTimeSheet();
				employeeTimeSheet.setEmployeeId(e.getId());
				employeeTimeSheet.setDate(sheetDetailsDto.getDate());
				employeeTimeSheet.setHoursWorked(sheetDetailsDto.getHoursWork());
				employeeTimeSheet.setAdjustment(sheetDetailsDto.getAdjustment());
				employeeTimeSheets.add(employeeTimeSheet);
			}
		}
		return employeeTimeSheets;
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		Payroll payroll = (Payroll) form;
		logger.info("Saving the payroll.");
		boolean isNew = payroll.getId() == 0;
		AuditUtil.addAudit(payroll, new Audit(user.getId(), isNew, new Date()));
		if (isNew) {
			payroll.setSequenceNumber(payrollDao.generateSN(payroll.getCompanyId()));
		}
		payrollDao.saveOrUpdate(payroll);

		List<PayrollEmployeeSalary> pEmployeeSalaries = payroll.getPayrollEmployeeSalaries();
		List<EmployeeDeduction> employeeDeductions = payroll.getEmployeeDeductions();
		int payrollId = payroll.getId();
		// Save the payroll employee salaries.
		if(!pEmployeeSalaries.isEmpty() && employeeDeductions != null) {
			for(PayrollEmployeeSalary pes : pEmployeeSalaries) {
				logger.info("Saving payroll employee salaries.");
				pes.setPayrollId(payrollId);
				payrollEmployeeSalaryDao.save(pes);
			}
		}
		List<PayrollEmployeeTimeSheet> pets = timeSheetDao.getByTimeSheet(payroll.getTimeSheetId());
		for(PayrollEmployeeTimeSheet pet : pets) {
			pet.setPayrollId(payrollId);
			timeSheetDao.saveOrUpdate(pet);
		}
		// Save the employee deductions.
		if(!employeeDeductions.isEmpty() && employeeDeductions != null) {
			logger.info("Saving employee deductions.");
			saveEmployeeDeductions(payroll, user);
		}
		logger.info("Successfully saved payroll.");
	}

	/**
	 * Get and set the payroll detail for view form
	 * @param payrollId The payroll id
	 * @return The payroll object
	 */
	public Payroll getPayrollView(int payrollId) {
		Payroll payroll = payrollDao.get(payrollId);
		if (payroll != null) {
			Company company = companyDao.get(payroll.getCompanyId());
			payroll.setCompany(company);

			Division division = divisionDao.get(payroll.getDivisionId());
			payroll.setDivision(division);

			EmployeeType employeeType = employeeTypeDao.get(payroll.getEmployeeTypeId());
			payroll.setEmployeeType(employeeType);

			ReferenceDocument refDocument = referenceDocumentDao.getRDByEbObject(payroll.getEbObjectId());
			payroll.setReferenceDocument(refDocument);

			PayrollTimePeriod ptp = 
					timePeriodDao.get(payroll.getPayrollTimePeriodId());
			PayrollTimePeriodSchedule ptps = 
					timePeriodScheduleDao.get(payroll.getPayrollTimePeriodScheduleId());
			payroll.setPayrollTimePeriod(ptp);
			payroll.setPayrollTimePeriodSchedule(ptps);
			payroll.setDateFrom(ptps.getDateFrom());
			payroll.setDateTo(ptps.getDateTo());

			setPeriods(payroll);

			List<PayrollEmployeeSalary> pESalaries = getAndProcessEmployeeSalary(payrollId);
			payroll.setPayrollEmployeeSalaries(pESalaries);
		}
		return payroll;
	}

	protected void setPeriods (Payroll payroll) {
		PayrollTimePeriod ptp = 
				timePeriodDao.get(payroll.getPayrollTimePeriodId());
		PayrollTimePeriodSchedule ptps = 
				timePeriodScheduleDao.get(payroll.getPayrollTimePeriodScheduleId());
		payroll.setPayrollTimePeriod(ptp);
		payroll.setPayrollTimePeriodSchedule(ptps);
		payroll.setDateFrom(ptps.getDateFrom());
		payroll.setDateTo(ptps.getDateTo());
	}

	protected List<PayrollEmployeeSalary> getAndProcessEmployeeSalary (int payrollId) {
		List<PayrollEmployeeSalary> payrollEmployeeSalaries = 
				payrollEmployeeSalaryDao.getAllByRefId("payrollId", payrollId);
		if (payrollEmployeeSalaries != null && !payrollEmployeeSalaries.isEmpty()) {
			for (PayrollEmployeeSalary pes : payrollEmployeeSalaries) {
				setGrossAndNetPay(pes);
				Employee employee = employeeDao.get(pes.getEmployeeId());
				if (employee != null) {
					String employeeName = employee.getFullName();
					pes.setEmployeeName(employeeName);
				}
				employee = null;
			}
		}
		Collections.sort(payrollEmployeeSalaries, new Comparator<PayrollEmployeeSalary>() {
			@Override
			public int compare(PayrollEmployeeSalary p1, PayrollEmployeeSalary p2) {
				return p1.getEmployeeName().compareTo(p2.getEmployeeName());
			}
		});
		return payrollEmployeeSalaries;
	}

	/**
	 * Set and compute the total gross and net pay
	 * @param employeeSalary The payroll employee salary object
	 */
	public void setGrossAndNetPay (PayrollEmployeeSalary employeeSalary)  {
		double grossPay = 0;
		double deduction = 0;

		grossPay += employeeSalary.getBasicPay();
		grossPay += employeeSalary.getPaidLeave();
		grossPay += employeeSalary.getOvertime();
		grossPay += employeeSalary.getCola();
		grossPay += employeeSalary.getBonus();
		grossPay += employeeSalary.getSundayHolidayPay() != null ? employeeSalary.getSundayHolidayPay() : 0;
		grossPay += employeeSalary.getNightDifferential() != null ? employeeSalary.getNightDifferential() : 0;
		grossPay += employeeSalary.getRegHolidayPay() != null ? employeeSalary.getRegHolidayPay() : 0;
		employeeSalary.setGrossPay(grossPay);

		deduction += employeeSalary.getLateAbsent();
		deduction += employeeSalary.getDeduction();
		deduction += employeeSalary.getSss();
		deduction += employeeSalary.getPhilHealth();
		deduction += employeeSalary.getPagibig();
		deduction += employeeSalary.getWithholdingTax();
		deduction += employeeSalary.getCashAdvance() != null ? employeeSalary.getCashAdvance() : 0;
		deduction += employeeSalary.getOtherDeductions() != null ? employeeSalary.getOtherDeductions() : 0.0;

		double adjustment = employeeSalary.getAdjustment() != null ? employeeSalary.getAdjustment() : 0;
		double netPay = grossPay - deduction;
		employeeSalary.setNetPay(netPay + adjustment);
	}

	/**
	 * Get the list of payroll employee salary by payroll id.
	 * @param payrollId The payroll id.
	 * @return The list of payroll employee salary.
	 */
	public List<PayrollEmployeeSalary> getPayrollEmployeeSalaryDetails(Integer payrollId){
		List<PayrollEmployeeSalary> salaries = payrollEmployeeSalaryDao.getAllByRefId("payrollId", payrollId);
		if (salaries != null && !salaries.isEmpty()) {
			Payroll payroll = payrollDao.get(payrollId);
			List<PayrollEmployeeTimeSheet> tSPerEmployee = null;
			for (PayrollEmployeeSalary pes : salaries) {
				tSPerEmployee = timeSheetDao.getByEmployeeIdAndTimeSheetId(
					pes.getEmployeeId(), payroll.getTimeSheetId());
				pes.setTotalHoursWorked(computeTotalHoursWorked(tSPerEmployee));
			}
		}
		return salaries;
	}

	/**
	 * Get the payroll with details;
	 * @param payrollId The payroll id.
	 * @return The payroll object.
	 */
	public Payroll getPayrollWithDetails (Integer payrollId) {
		Payroll payroll = payrollDao.get(payrollId);
		payroll.setEmployeeTimeSheets(timeSheetDao.getAllByRefId(PayrollEmployeeTimeSheet.FIELD.timeSheetId.name(), payroll.getTimeSheetId()));
		Integer employeeTypeId = payroll.getEmployeeTypeId();
		if (employeeTypeId != null) {
			payroll.setEmployeeType(employeeTypeDao.get(employeeTypeId));
		}
		setPeriods(payroll);
		return payroll;
	}

	/**
	 * Compute the salary.
	 * @param timesheetJson Time sheet json.
	 * @param strDate Date started.
	 * @param employeeTypeId The employee type id.
	 * @param companyName The company name.
	 * @return The list of computed salary dto.
	 */
	public List<EmployeeSalaryDTO> computeEmployeesSalary(int payrollId, Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId,
			Integer timeSheetId, Integer companyId, String totalDeductions, boolean isOverrideContribComputation) {
		List<TimeSheetDto> timeSheets = timeSheetService.convertTimesheetToDto(timeSheetId);
		return computeEmployeesSalary(payrollId, payrollTimePeriodId, payrollTimePeriodScheduleId, timeSheets, 
				companyId, totalDeductions, isOverrideContribComputation);
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		Payroll payroll = (Payroll) form;
		payroll.setDescription("Payroll for " + DateUtil.formatDate(payroll.getDate()));
		payroll.setPayrollEmployeeSalaries(setPayrollEmployeeSalaries(payroll));
	}

	/**
	 * Set the payroll employee salaries to the payroll object.
	 * @param payroll The payroll object.
	 */
	private List<PayrollEmployeeSalary> setPayrollEmployeeSalaries(Payroll payroll) {
		int payrollId = payroll.getId();
		boolean isNew = payrollId == 0;
		if(!isNew) {
			List<Integer> toBeDeletedPes = new ArrayList<>();
			List<PayrollEmployeeSalary> employeeSalaries = payrollEmployeeSalaryDao.getAllByRefId("payrollId", payrollId);
			for (PayrollEmployeeSalary savePes : employeeSalaries) {
				toBeDeletedPes.add(savePes.getId());
			}
			payrollEmployeeSalaryDao.delete(toBeDeletedPes);
		}

		List<PayrollEmployeeSalary> pEmployeeSalaries = new ArrayList<>();
		PayrollEmployeeSalary employeeSalary = null;
		for (EmployeeSalaryDTO dto : payroll.getEmployeeSalaryDTOs()) {
			employeeSalary = new PayrollEmployeeSalary();
			Employee e = employeeDao.getEmployeeByNo(dto.getEmployeeNo(), payroll.getCompanyId());
			employeeSalary.setEmployeeId(e.getId());
			employeeSalary.setBasicPay(dto.getBasicPay());
			employeeSalary.setPaidLeave(dto.getPaidLeave());
			employeeSalary.setCola(dto.getCola());
			employeeSalary.setOvertime(dto.getOvertime());
			employeeSalary.setSundayHolidayPay(dto.getSundayHolidayPay());
			employeeSalary.setRegHolidayPay(dto.getRegHolidayPay());
			employeeSalary.setNonWorkHolidayPay(dto.getNonWorkHolidayPay());
			employeeSalary.setBonus(dto.getBonus());
			employeeSalary.setGrossPay(dto.getGrossPay());
			employeeSalary.setDeduction(dto.getDeduction());
			employeeSalary.setLateAbsent(dto.getLateAbsent());
			employeeSalary.setSss(dto.getSss());
			employeeSalary.setSssEr(dto.getSssEr());
			employeeSalary.setSssEc(dto.getSssEc());
			employeeSalary.setPagibig(dto.getPagibig());
			employeeSalary.setPagibigEr(dto.getPagibigEr());
			employeeSalary.setPhilHealth(dto.getPhilHealth());
			employeeSalary.setPhilHealthEr(dto.getPhilHealthEr());
			employeeSalary.setWithholdingTax(dto.getWithholdingTax());
			employeeSalary.setNightDifferential(dto.getNightDiff());
			employeeSalary.setAdjustment(dto.getAdjustment() != null ? dto.getAdjustment() : 0.0);
			employeeSalary.setDailyRate(dto.getDailyRate());
			employeeSalary.setWorkingDays(dto.getWorkingDays());
			employeeSalary.setCashAdvance(dto.getCashAdvance());
			double otherDeductions = dto.getOtherDeductions() != null ? dto.getOtherDeductions() : 0.0;
			employeeSalary.setOtherDeductions(otherDeductions);
			double otherBalances = dto.getOtherBalances() != null ? dto.getOtherBalances() : 0.0;
			employeeSalary.setOtherBalances(otherBalances);
			double caBalances = dto.getCashAdvBalances() != null ? dto.getCashAdvBalances() : 0.0;
			employeeSalary.setCashAdvBalances(caBalances);
			double totalDeduction = dto.getDeduction() + dto.getSss() + dto.getPhilHealth()
				+ dto.getPagibig() + dto.getWithholdingTax() + dto.getCashAdvance() + otherDeductions;
			double netPay = dto.getGrossPay() - totalDeduction;
			employeeSalary.setNetPay(netPay + employeeSalary.getAdjustment());
			pEmployeeSalaries.add(employeeSalary);
		}
		return pEmployeeSalaries;
	}

	/**
	 * Save the employee deductions.
	 * @param payroll The payroll object.
	 * @param user The current user logged
	 */
	protected void saveEmployeeDeductions(Payroll payroll, User user) {
		List<EmployeeDeduction> saved = employeeDeductionDao.getActiveEmployeeDeductions(payroll.getId());
		if (saved != null && !saved.isEmpty()) {
			List<Domain> toBeInactive = new ArrayList<>();
			for (EmployeeDeduction ed : saved) {
				ed.setActive(false);
				toBeInactive.add(ed);
			}
			employeeDeductionDao.batchSaveOrUpdate(toBeInactive);
			toBeInactive = null;
			saved = null;
		}

		// Save with EB OBJECT.
		List<EmployeeDeduction> employeeDeductions = payroll.getEmployeeDeductions();
		if (employeeDeductions != null) {
			Date currentDate = new Date();
			List<Domain> toBeSaved = new ArrayList<>();
			List<Domain> o2os = new ArrayList<>();
			for (EmployeeDeduction ed : employeeDeductions) {
				if (ed.getDeductionTypeId() != null && ed.getAmount() != 0) {
					ed.setActive(true);
					int fdlObjectId = ed.getFdlEbObjectId() != null ? ed.getFdlEbObjectId() : 0;
					if(fdlObjectId == 0) {
						EBObject ebObject = new EBObject();
						ebObject.setObjectTypeId(EmployeeDeduction.OBJECT_TYPE_ID);
						AuditUtil.addAudit(ebObject, new Audit(user.getId(), true, new Date()));
						ebObjectDao.save(ebObject);
						ed.setFdlEbObjectId(ebObject.getId());
					}
					// Saving the Object To Object.
					o2os.add(ObjectToObject.getInstanceOf(ed.getFdlEbObjectId(), ed.getEbObjectId(),
							EmployeeDeduction.OR_TYPE_ID, user, currentDate));
					ed.setPayrollId(payroll.getId());
					ed.setPaidAmount(ed.getAmount());
					toBeSaved.add(ed);
				}
			}
			objectToObjectDao.batchSave(o2os);
			employeeDeductionDao.batchSave(toBeSaved);
		}
	}

	/**
	 * Search payroll form in general search
	 * @param searchCriteria The search criteria
	 * @param user The current user logged
	 * @return The payroll form result
	 */
	public List<FormSearchResult> searchPayrolls(String searchCriteria, User user){
		Page<Payroll> payroll = payrollDao.searchPayrolls(searchCriteria, user,new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		List<ResultProperty> properties = null;
		String title = null;
		String status = null;
		Company company = null;
		Division division = null;
		for (Payroll pr : payroll.getData()){
			properties = new ArrayList<ResultProperty>();
			title = "Payroll No. " + pr.getSequenceNumber();
			company = companyDao.get(pr.getCompanyId());
			properties.add(ResultProperty.getInstance("Company", company.getNumberAndName()));
			division = divisionDao.get(pr.getDivisionId());
			properties.add(ResultProperty.getInstance("Division", division.getNumberAndName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(pr.getDate())));
			status = pr.getFormWorkflow().getCurrentStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			if (pr.getEmployeeTypeId() != null) {
				properties.add(ResultProperty.getInstance("Employee Type", employeeTypeDao.get(pr.getEmployeeTypeId()).getName()));
			}
			result.add(FormSearchResult.getInstanceOf(pr.getId(), title, properties));
			company = null;
			division = null;
		}
		return result;
	}

	/**
	 * Convert the payroll employee salary to DTO
	 * @param payrollId The payroll id
	 * @return The list of employee DTO
	 */
	public List<EmployeeSalaryDTO> convertEmployeeSalaryToDto(Integer payrollId){
		List<PayrollEmployeeSalary> employeeSalaries = null;
		List<EmployeeSalaryDTO> employeeSalaryDtos = new ArrayList<>();
		EmployeeSalaryDTO employeeSalaryDto = new EmployeeSalaryDTO();
		employeeSalaries = payrollEmployeeSalaryDao.getPayrollESDOrderByLastName(payrollId);
		for (PayrollEmployeeSalary pes : employeeSalaries) {
			Employee employee = pes.getEmployee();
			employeeSalaryDto = new EmployeeSalaryDTO();
			employeeSalaryDto.setEmployeeNo(employee.getEmployeeNo());
			employeeSalaryDto.setEmployeeName(employee.getFullName());
			employeeSalaryDto.setEmployeeId(pes.getEmployeeId());
			employeeSalaryDto.setEmployeeStatus(employee.getEmployeeStatus().getName());
			employeeSalaryDto.setSalaryRate(employee.getSalaryDetail() != null
					? employee.getSalaryDetail().getDailySalary() : 0.0);
			employeeSalaryDto.setBasicPay(pes.getBasicPay());
			employeeSalaryDto.setOvertime(pes.getOvertime());
			employeeSalaryDto.setSundayHolidayPay(pes.getSundayHolidayPay());
			employeeSalaryDto.setRegHolidayPay(pes.getRegHolidayPay());
			employeeSalaryDto.setNonWorkHolidayPay(pes.getNonWorkHolidayPay());
			employeeSalaryDto.setPaidLeave(pes.getPaidLeave());
			employeeSalaryDto.setDeMinimis(pes.getDeMinimis() != null ? pes.getDeMinimis() : 0.0);
			employeeSalaryDto.setCola(pes.getCola());
			employeeSalaryDto.setBonus(pes.getBonus());
			employeeSalaryDto.setDeduction(pes.getDeduction());
			employeeSalaryDto.setSss(pes.getSss());
			employeeSalaryDto.setPhilHealth(pes.getPhilHealth());
			employeeSalaryDto.setPagibig(pes.getPagibig());
			employeeSalaryDto.setLateAbsent(pes.getLateAbsent());
			employeeSalaryDto.setWithholdingTax(pes.getWithholdingTax());
			employeeSalaryDto.setEmployeeName(employee.getFullName());
			employeeSalaryDto.setNightDiff(pes.getNightDifferential());
			employeeSalaryDto.setAdjustment(pes.getAdjustment() != null ? pes.getAdjustment() : 0.0);
			employeeSalaryDto.setDailyRate(pes.getDailyRate() != null ? pes.getDailyRate() : 0.0);
			employeeSalaryDto.setWorkingDays(pes.getWorkingDays() != null ? pes.getWorkingDays() : 0.0);
			employeeSalaryDto.setCashAdvance(pes.getCashAdvance() != null ? pes.getCashAdvance() : 0.0);
			setGrossAndNetPay(pes);
			employeeSalaryDto.setGrossPay(pes.getGrossPay());
			employeeSalaryDto.setNetPay(pes.getNetPay());
			employeeSalaryDto.setOtherDeductions(pes.getOtherDeductions() != null ? pes.getOtherDeductions() : 0.0);
			employeeSalaryDto.setCashAdvBalances(pes.getCashAdvBalances() != null ? pes.getCashAdvBalances() : 0.0);
			employeeSalaryDto.setOtherBalances(pes.getOtherBalances() != null ? pes.getOtherBalances() : 0.0);
			employeeSalaryDtos.add(employeeSalaryDto);
		}
		return employeeSalaryDtos;
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

	/**
	 * Compute the total hours worked
	 * @param employeeTimeSheets The list of employee time sheet
	 * @return The computed total hours worked
	 */
	public double computeTotalHoursWorked (List<PayrollEmployeeTimeSheet> employeeTimeSheets) {
		if (employeeTimeSheets != null && !employeeTimeSheets.isEmpty()) {
			double totalHoursWorked = 0;
			for (PayrollEmployeeTimeSheet pet : employeeTimeSheets) {
				double hoursWorked =  pet.getHoursWorked();
				double adjustment = pet.getAdjustment() != null ? pet.getAdjustment() : 0;
				totalHoursWorked += hoursWorked + adjustment;
			}
			return totalHoursWorked;
		}
		return 0;
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return null;
	}

	/**
	 * Add cash sale return items in cash sale return.
	 * @param cashSaleReturn The cash sale return.
	 * @return The cash sale return.
	 */
	public Payroll convertTSDtoPayroll(TimeSheet timeSheet) {
		if (timeSheet != null) {
			Payroll payroll = new Payroll();
			payroll.setTimeSheetId(timeSheet.getId());
			payroll.setCompanyId(timeSheet.getCompanyId());
			payroll.setDivisionId(timeSheet.getDivisionId());
			payroll.setPayrollTimePeriodId(timeSheet.getPayrollTimePeriodId());
			payroll.setPayrollTimePeriodScheduleId(timeSheet.getPayrollTimePeriodScheduleId());

			Company company = companyDao.get(timeSheet.getCompanyId());
			Division division = divisionDao.get(timeSheet.getDivisionId());
			PayrollTimePeriod payrollTimePeriod = timePeriodDao.get(timeSheet.getPayrollTimePeriodId());
			PayrollTimePeriodSchedule payrollTimePeriodSchedule = timePeriodScheduleDao.get(timeSheet.getPayrollTimePeriodScheduleId());

			payroll.setCompany(company);
			payroll.setDivision(division);
			payroll.setPayrollTimePeriod(payrollTimePeriod);
			payroll.setPayrollTimePeriodSchedule(payrollTimePeriodSchedule);
			return payroll;
		}
		return null;
	}

	/**
	 * Initialize the employee deduction.
	 * @param timeSheetId The time sheet filter.
	 * @return
	 */
	public List<EmployeeDeductionDto> initEmployeeDeductions(Integer timeSheetId) {
		List<EmployeeDeductionDto> employeeDeductionDtos = new ArrayList<>();
		TimeSheet timeSheet = tsDao.get(timeSheetId);
		int payrollTimePeriodScheduleId = timeSheet.getPayrollTimePeriodScheduleId();
		PayrollTimePeriodSchedule schedule = timePeriodScheduleDao.get(payrollTimePeriodScheduleId);
		Date dateFrom = schedule.getDateFrom();
		Date dateTo = schedule.getDateTo();
		timeSheet = null;
		schedule = null;
		List<Employee> employees = employeeDao.getEmployeesByTimeSheetId(timeSheetId);
		EmployeeDeductionDto eDto = null;
		if (employees != null && !employees.isEmpty()) {
			for (Employee e : employees) {
				eDto = new EmployeeDeductionDto();
				eDto.setEmployeeId(e.getId());
				eDto.setEmployeeNo(e.getEmployeeNo());
				eDto.setEmployeeName(e.getFullName());
				eDto.setEmployeeDeductions(employeeDeductionDao.initEmployeeDeductions(e.getId(), dateFrom, dateTo));
				employeeDeductionDtos.add(eDto);
			}
		}
		return employeeDeductionDtos;
	}

	/**
	 * Get all the active deduction types
	 * @return The list of deduction types
	 */
	public List<DeductionType> getAllActive() {
		return deductionTypeDao.getAllActive();
	}

	/**
	 * Get the list of employee deductions.
	 * @param payrollId The payroll id.
	 * @return The list of employee deductions.
	 */
	public List<EmployeeDeductionDto> getEmployeeDeductions(Integer payrollId) {
		Payroll payroll = payrollDao.get(payrollId);
		List<EmployeeDeductionDto> employeeDeductionDtos = new ArrayList<>();
		List<Employee> employees = employeeDao.getEmployeesByTimeSheetId(payroll.getTimeSheetId());
		EmployeeDeductionDto eDto = null;
		List<EmployeeDeduction> deductions = new ArrayList<>();
		if (employees != null && !employees.isEmpty()) {
			for (Employee e : employees) {
				eDto = new EmployeeDeductionDto();
				eDto.setEmployeeId(e.getId());
				eDto.setEmployeeNo(e.getEmployeeNo());
				eDto.setEmployeeName(e.getFullName());
				deductions = employeeDeductionDao.getEmployeeDeductions(payrollId, e.getId());
				for (EmployeeDeduction employeeDeduction : deductions) {
					employeeDeduction.setDeductionTypeName(deductionTypeDao.get(employeeDeduction.getDeductionTypeId()).getName());
				}
				eDto.setDeductionTypes(deductionTypeDao.getDeductionTypes(payrollId, e.getId()));
				eDto.setEmployeeDeductions(deductions);
				employeeDeductionDtos.add(eDto);
			}
		}
		return employeeDeductionDtos;
	}

	/**
	 * Get the payroll employee salary details for payslip printout
	 * @param payrollId The payroll id
	 * @return The payroll employee salary details
	 */
	public List<PayrollEmplSalaryRecordDto> getPayrollEmplSalaryRecordDto(Integer payrollId) {
		Payroll payroll = getPayroll(payrollId, false);
		EmployeeDeduction deduction = null;
		PayrollEmplSalaryRecordDto emplSalaryRecordDto = null;
		List<PayrollEmployeeSalary> employeeSalaries = payrollEmployeeSalaryDao.getPayrollESDOrderByLastName(payrollId);
		List<PayrollEmplSalaryRecordDto> emplSalaryRecordDtos = new ArrayList<>();
		List<PayrollEmployeeTimeSheet> payrolltimeSheet = new ArrayList<>();
		List<DeductionType> deductionTypes = deductionTypeDao.getDeductionTypesByPayrollId(payrollId);
		for (PayrollEmployeeSalary pes : employeeSalaries) {
			emplSalaryRecordDto = new PayrollEmplSalaryRecordDto();
			emplSalaryRecordDto.setBasicPay(pes.getBasicPay());
			emplSalaryRecordDto.setBonus(pes.getBonus());
			emplSalaryRecordDto.setCola(pes.getCola());
			emplSalaryRecordDto.setDeduction(pes.getDeduction());
			emplSalaryRecordDto.setEmployeeName(pes.getEmployee().getFullName());
			emplSalaryRecordDto.setGrossPay(pes.getGrossPay());
			emplSalaryRecordDto.setLateAbsent(pes.getLateAbsent());
			emplSalaryRecordDto.setNetPay(pes.getNetPay());
			emplSalaryRecordDto.setOvertime(pes.getOvertime());
			emplSalaryRecordDto.setSundayHolidayPay(pes.getSundayHolidayPay());
			emplSalaryRecordDto.setRegHolidayPay(pes.getRegHolidayPay());
			emplSalaryRecordDto.setNonWorkHolidayPay(pes.getNonWorkHolidayPay());
			emplSalaryRecordDto.setSss(pes.getSss());
			emplSalaryRecordDto.setPaidLeave(pes.getPaidLeave() != null ? pes.getPaidLeave() : 0.00);
			emplSalaryRecordDto.setPagibig(pes.getPagibig());
			emplSalaryRecordDto.setPhilHealth(pes.getPhilHealth());
			emplSalaryRecordDto.setWithholdingTax(pes.getWithholdingTax());
			emplSalaryRecordDto.setNightDiff(pes.getNightDifferential());
			emplSalaryRecordDto.setAdjustment(pes.getAdjustment() != null ? pes.getAdjustment() : 0.0);
			emplSalaryRecordDto.setDailyRate(pes.getDailyRate() != null ? pes.getDailyRate() : 0.0);
			emplSalaryRecordDto.setWorkingDays(pes.getWorkingDays() != null ? pes.getWorkingDays() : 0.0);
			emplSalaryRecordDto.setCashAdvance(pes.getCashAdvance() != null ? pes.getCashAdvance() : 0.0);
			payrolltimeSheet = timeSheetService.getTimesheetByEmployeeAndTimesheetId(pes.getEmployeeId(), payroll.getTimeSheetId());
			emplSalaryRecordDto.setTotalHoursWorked(computeTotalHoursWorked(payrolltimeSheet));
			emplSalaryRecordDto.setTotalLateHours(computeTotalLateHours(payrolltimeSheet));
			emplSalaryRecordDto.setOtherDeductions(pes.getOtherDeductions() != null ? pes.getOtherDeductions() : 0.0);
			emplSalaryRecordDto.setOtherBalances(pes.getOtherBalances() != null ? pes.getOtherBalances() : 0.0);
			emplSalaryRecordDto.setCashAdvBalances(pes.getCashAdvBalances() != null ? pes.getCashAdvBalances() : 0.0);
			List<EmployeeDeduction> emplDeductions = new ArrayList<>();
			List<EmployeeDeduction> deductions = new ArrayList<>();
			double totalDeductions = 0;
			double deductionAmt = 0;
			for (DeductionType deductionType : deductionTypes) {
				deduction = new EmployeeDeduction();
				emplDeductions = employeeDeductionDao.getEmployeeDeductions(payrollId, pes.getEmployeeId(), deductionType.getId());
				deduction.setDeductionTypeName(deductionType.getName());
				deductionAmt = emplDeductions != null && !emplDeductions.isEmpty() ? emplDeductions.iterator().next().getAmount() : 0.0;
				totalDeductions += deductionAmt;
				deduction.setAmount(deductionAmt);
				deductions.add(deduction);
			}
			emplSalaryRecordDto.setTotalDeductions(totalDeductions);
			emplSalaryRecordDto.setEmployeeDeductions(deductions);
			emplSalaryRecordDtos.add(emplSalaryRecordDto);
		}
		return emplSalaryRecordDtos;
	}

	/**
	 * Compute the total late hours
	 * @param employeeTimeSheets The list payroll employee time sheet
	 * @return The total late hours
	 */
	public double computeTotalLateHours(List<PayrollEmployeeTimeSheet> employeeTimeSheets) {
		if (employeeTimeSheets != null && !employeeTimeSheets.isEmpty()) {
			double totalLateHours = 0;
			for (PayrollEmployeeTimeSheet pet : employeeTimeSheets) {
				totalLateHours += pet.getLate();
			}
			return totalLateHours;
		}
		return 0;
	}

	/**
	 * Get the payroll employee salary record for main payroll printout
	 * @param payrollId The payroll id
	 * @return The  payroll employee salary records
	 */
	public List<PayrollRecordDetailsDto> getPayrollEmplRecords(Integer payrollId, boolean isExcludeFields) {
		List<PayrollEmployeeSalary> employeeSalaries = payrollEmployeeSalaryDao.getPayrollESDOrderByLastName(payrollId);
		List<DeductionType> deductionTypes = deductionTypeDao.getDeductionTypesByPayrollId(payrollId);
		List<PayrollRecordDetailsDto> payrollRecordDetails = new ArrayList<>();
		List<EmployeeDeduction> emplDeductions = new ArrayList<>();
		int orderCount;
		for (PayrollEmployeeSalary pes : employeeSalaries) {
			orderCount = 0;
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "Basic Pay", pes.getBasicPay(), orderCount++, true));
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "Paid Leave", pes.getPaidLeave(), orderCount++, true));
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "Overtime", pes.getOvertime(), orderCount++, true));
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "Holiday Pay", pes.getSundayHolidayPay(), orderCount++, true));
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "COLA", pes.getCola(), orderCount++, true));
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "Bonus", pes.getBonus(), orderCount++, true));
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "Gross Pay", computeGrossPay(pes), orderCount++, true));
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "Late", pes.getLateAbsent(), orderCount++, true));
			for (DeductionType deductionType : deductionTypes) {
				emplDeductions = employeeDeductionDao.getEmployeeDeductions(payrollId, pes.getEmployeeId(), deductionType.getId());
				payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), deductionType.getName(),
						emplDeductions != null && !emplDeductions.isEmpty() ? emplDeductions.iterator().next().getAmount() : 0.0, orderCount++, true));
			}
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "SSS", pes.getSss(), orderCount++, true));
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "PHIC", pes.getPhilHealth(), orderCount++, true));
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "HDMF", pes.getPagibig(), orderCount++, true));
			double adjustment = pes.getAdjustment() != null ? pes.getAdjustment() : 0.0;
			if (isExcludeFields) {
				payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "Adjusment", adjustment, orderCount++, true));
			} else {
				payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "Withholding Tax", pes.getWithholdingTax(), orderCount++, true));
			}
			payrollRecordDetails.add(new PayrollRecordDetailsDto(pes.getEmployee().getFullName(), "Net Pay", computeNetPay(pes) + adjustment, orderCount++, true));
		}
		return payrollRecordDetails;
	}

	private double computeGrossPay(PayrollEmployeeSalary pes) {
		double grossPay = 0;
		grossPay += pes.getBasicPay();
		grossPay += pes.getOvertime();
		grossPay += pes.getPaidLeave();
		grossPay += pes.getCola();
		grossPay += pes.getBonus();
		grossPay += pes.getSundayHolidayPay() != null ? pes.getSundayHolidayPay() : 0;
		grossPay += pes.getNightDifferential() != null ? pes.getNightDifferential() : 0;
		grossPay += pes.getRegHolidayPay() != null ? pes.getRegHolidayPay() : 0;
		return grossPay;
	}

	private double computeNetPay(PayrollEmployeeSalary pes) {
		double grossPay = computeGrossPay(pes);
		double deduction = 0;
		deduction += pes.getLateAbsent();
		deduction += pes.getDeduction();
		deduction += pes.getSss();
		deduction += pes.getPhilHealth();
		deduction += pes.getPagibig();
		deduction += pes.getPrevBalance() != null ? pes.getPrevBalance() : 0;
		deduction += pes.getWithholdingTax() != null ? pes.getWithholdingTax() : 0;
		deduction += pes.getCashAdvance() != null ? pes.getCashAdvance() : 0;
		deduction += pes.getOtherDeductions() != null ? pes.getOtherDeductions() : 0;
		return grossPay - deduction;
	}

	/**
	 * Generate the data source for Employer-Employee Contribution Report
	 * @param companyId The company id
	 * @param month The month
	 * @param year The year
	 * @param timePeriodScheduleId The time period schedule id
	 */
	public JRDataSource generateContributionReport(Integer companyId, Integer month, Integer year,
			Integer timePeriodScheduleId, Integer divisionId, boolean isFirstNameFirst) {
		EBJRServiceHandler<EmployerEmployeeContributionDTO> handler =
				new JREEContributionHandler(this, companyId, month, year,
						timePeriodScheduleId, divisionId, isFirstNameFirst);
		return new EBDataSource<EmployerEmployeeContributionDTO>(handler);
	}

	private static class JREEContributionHandler implements EBJRServiceHandler<EmployerEmployeeContributionDTO> {
		private PayrollService payrollService;
		private Integer companyId;
		private Integer month;
		private Integer year;
		private Integer timePeriodScheduleId;
		private Integer divisionId;
		private boolean isFirstNameFirst;

		private JREEContributionHandler(PayrollService payrollService, Integer companyId, Integer month,
				Integer year, Integer timePeriodScheduleId, Integer divisionId, boolean isFirstNameFirst) {
			this.payrollService = payrollService;
			this.companyId = companyId;
			this.month = month;
			this.year = year;
			this.timePeriodScheduleId = timePeriodScheduleId;
			this.divisionId = divisionId;
			this.isFirstNameFirst = isFirstNameFirst;
		}

		@Override
		public void close() throws IOException {
			payrollService = null;
		}

		@Override
		public Page<EmployerEmployeeContributionDTO> nextPage(PageSetting pageSetting) {
			Page<EmployerEmployeeContributionDTO> eeContributionDtos =
					payrollService.payrollDao.getEEContibutions(companyId,
							month, year, timePeriodScheduleId, divisionId, pageSetting, isFirstNameFirst);
			return eeContributionDtos;
		}
	}

	/**
	 * Get and process the payroll for view form
	 * @param payrollId The payroll id
	 * @return The payroll object
	 */
	public PayrollDto getPayrollForView(Integer payrollId) {
		Payroll payroll = payrollDao.get(payrollId);
		PayrollDto payrollDto = new PayrollDto();
		List<EmployeeSalaryDTO> employeeSalaryDtos = new ArrayList<>();
		if (payroll != null) {
			Company company = companyService.getCompany(payroll.getCompanyId());
			payroll.setCompany(company);

			Division division = divisionDao.get(payroll.getDivisionId());
			payroll.setDivision(division);

			ReferenceDocument refDocument = referenceDocumentDao.getRDByEbObject(payroll.getEbObjectId());
			payroll.setReferenceDocument(refDocument);

			PayrollTimePeriod ptp = timePeriodDao.get(payroll.getPayrollTimePeriodId());
			payroll.setPayrollTimePeriod(ptp);

			PayrollTimePeriodSchedule ptps = timePeriodScheduleDao.get(payroll.getPayrollTimePeriodScheduleId());
			payroll.setPayrollTimePeriodSchedule(ptps);

			List<PayrollEmployeeSalary> payrollEmployeeSalaries = getAndProcessEmployeeSalary(payrollId);
			EmployeeSalaryDTO dto = null;
			for (PayrollEmployeeSalary pes : payrollEmployeeSalaries) {
				dto = new EmployeeSalaryDTO();
				Employee employee = pes.getEmployee();
				dto.setEmployeeId(pes.getEmployeeId());
				dto.setEmployeeNo(employee.getEmployeeNo());
				dto.setEmployeeName(employee.getFullName());
				dto.setEmployeeStatus(employee.getEmployeeStatus().getName());
				// Compensation
				dto.setBasicPay(pes.getBasicPay());
				dto.setPaidLeave(pes.getPaidLeave());
				dto.setCola(pes.getCola());
				dto.setBonus(pes.getBonus());
				dto.setSundayHolidayPay(pes.getSundayHolidayPay());
				dto.setOvertime(pes.getOvertime());
				dto.setNightDiff(pes.getNightDifferential());
				dto.setRegHolidayPay(pes.getRegHolidayPay());
				dto.setGrossPay(computeGrossPay(pes));
				// Deduction
				dto.setDeduction(pes.getDeduction());
				dto.setLateAbsent(pes.getLateAbsent());
				dto.setAdjustment(pes.getAdjustment() != null ? pes.getAdjustment() : 0.0);
				dto.setDailyRate(pes.getDailyRate() != null ? pes.getDailyRate() : 0.0);
				dto.setWorkingDays(pes.getWorkingDays() != null ? pes.getWorkingDays() : 0.0);
				dto.setWithholdingTax(pes.getWithholdingTax());
				dto.setSss(pes.getSss());
				dto.setPhilHealth(pes.getPhilHealth());
				dto.setPagibig(pes.getPagibig());
				dto.setCashAdvance(pes.getCashAdvance() != null ? pes.getCashAdvance() : 0.0);
				dto.setNetPay(computeNetPay(pes) + dto.getAdjustment());
				dto.setOtherDeductions(pes.getOtherDeductions() != null ? pes.getOtherDeductions() : 0.0);
				dto.setCashAdvBalances(pes.getCashAdvBalances() != null ? pes.getCashAdvBalances() : 0.0);
				dto.setOtherBalances(pes.getOtherBalances() != null ? pes.getOtherBalances() : 0.0);
				employeeSalaryDtos.add(dto);
			}
		}
		payrollDto.setPayroll(payroll);
		payrollDto.setEmployeeSalaryDtos(employeeSalaryDtos);
		return payrollDto;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		Payroll payroll = payrollDao.getByEbObjectId(ebObjectId);
		Integer pId = payroll.getId();
		FormProperty property = workflowHandler.getProperty(payroll.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = payroll.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Payroll - " + payroll.getSequenceNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + companyDao.getCompanyName(payroll.getCompanyId()))
				.append(" " + divisionDao.get(payroll.getDivisionId()))
				.append(" MONTH " + payrollTimePeriodDao.get(payroll.getPayrollTimePeriodId()).getMonth())
				.append(" YEAR " + payrollTimePeriodDao.get(payroll.getPayrollTimePeriodId()).getYear())
				.append("<b> DATE : </b>" + DateUtil.formatDate(payroll.getDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case Payroll.OBJECT_TYPE:
			return payrollDao.getByEbObjectId(ebObjectId);
		case EmployeeDeduction.OBJECT_TYPE_ID:
			return employeeDeductionDao.getByEbObjectId(ebObjectId);
		case PayrollEmployeeSalary.PES_OBJECT_TYPE:
			return payrollEmployeeSalaryDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Format the payroll time period schedule
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @return The payroll time period schedule
	 */
	public String formatTimePeriodSched(Date dateFrom, Date dateTo) {
		Calendar cal = Calendar.getInstance();
		// Set start date
		cal.setTime(dateFrom);
		DateFormatSymbols dfs = new DateFormatSymbols();
		String startMonth = dfs.getMonths()[cal.get(Calendar.MONTH)];
		int startDay = cal.get(Calendar.DAY_OF_MONTH);
		int startYear = cal.get(Calendar.YEAR);

		// Set end date
		cal.setTime(dateTo);
		String endMonth = dfs.getMonths()[cal.get(Calendar.MONTH)];
		int endDay = cal.get(Calendar.DAY_OF_MONTH);
		int endYear = cal.get(Calendar.YEAR);

		// Set time period schedule
		String startDate = startMonth + " " + startDay;
		if (startYear != endYear) {
			startDate += ", " + startYear;
		}
		String endDate = "";
		if (!startMonth.equals(endMonth)) {
			endDate += endMonth + " ";
		}
		endDate += endDay + ", " + endYear;
		return startDate + " - " + endDate;
	}
}