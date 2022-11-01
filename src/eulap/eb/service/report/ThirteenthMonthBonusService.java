package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.eb.dao.PayrollEmployeeSalaryDao;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.PayrollEmployeeSalary;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EmployeeService;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.service.payroll.PayrollService;
import eulap.eb.web.dto.ThirteenthMonthBonusDto;

/**
 * A class that handles the business for 13th Month Bonus report

 *
 */
@Service
public class ThirteenthMonthBonusService {
	@Autowired
	private PayrollEmployeeSalaryDao payrollEmployeeSalaryDao;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private PayrollService payrollService;
	@Autowired
	private PayrollTimePeriodService timePeriodService;
	@Autowired
	private DivisionService divisionService;

	private static final Integer JANUARY = 1;
	private static final Integer FEBRUARY = 2;
	private static final Integer MARCH = 3;
	private static final Integer APRIL = 4;
	private static final Integer MAY = 5;
	private static final Integer JUNE = 6;
	private static final Integer JULY = 7;
	private static final Integer AUGUST = 8;
	private static final Integer SEPTEMBER = 9;
	private static final Integer OCTOBER = 10;
	private static final Integer NOVEMBER = 11;
	private static final Integer DECEMBER = 12;

	private static final List<Integer> MONTHS = Arrays.asList(JANUARY, FEBRUARY, MARCH,
		APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER);

	private static final int FIRST_PERIOD = 1;
	private static final int SECOND_PERIOD = 2;
	private static final int FIFTEENTH_DAY = 15;
	private static final int THIRTIETH_DAY = 30;

	/**
	 * Get the period, when the date range contains 15th day
	 * First period = 1, Second period = 2
	 * @param startDate The start date
	 * @param endDate The end date
	 * @return The current period for payroll
	 */
	private int getPeriod(Date startDate, Date endDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.set(Calendar.DAY_OF_MONTH, FIFTEENTH_DAY);
		Date fifteenthDay = cal.getTime();
		if (!(fifteenthDay.before(startDate) || fifteenthDay.after(endDate))) {
			 return FIRST_PERIOD;
		 } else {
			 return SECOND_PERIOD;
		 }
	 }

	/**
	 * Get the payroll employee salaries
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @return The list of payroll employee salaries
	 */
	public List<ThirteenthMonthBonusDto> getThirteenthMonthBonusDtos(Integer companyId, Integer divisionId,
			Date dateFrom, Date dateTo) {
		List<ThirteenthMonthBonusDto> thirteenthMonthBonusDtos = new ArrayList<ThirteenthMonthBonusDto>();
		List<PayrollEmployeeSalary> employeeSalaries =
				payrollEmployeeSalaryDao.getEmployeeSalaries(companyId, divisionId, dateFrom, dateTo);
		Calendar cal = Calendar.getInstance();
		for (PayrollEmployeeSalary es : employeeSalaries) {
			int payrollId = es.getPayrollId();
			Employee employee = employeeService.getEmployee(es.getEmployeeId());
			String employeeName = employee.getFirstName() + " ";
			if(employee.getMiddleName() != null && !employee.getMiddleName().isEmpty()) {
				employeeName += employee.getMiddleName().charAt(0) + ". ";
			}
			employeeName += employee.getLastName();
			Payroll payroll = payrollService.getPayroll(payrollId);
			PayrollTimePeriodSchedule timePeriodSchedule =
					timePeriodService.getPayrollTimePeriodSchedule(payroll.getPayrollTimePeriodScheduleId());
			Division division = divisionService.getDivision(employee.getDivisionId());
			for (Integer month : MONTHS) {
				cal = Calendar.getInstance();
				cal.setTime(timePeriodSchedule.getDateTo());
				Integer monthId = cal.get(Calendar.MONTH) + 1;
				if(month.equals(monthId)) {
					cal.set(Calendar.DAY_OF_MONTH, FIFTEENTH_DAY);
					Date firstCutOff = cal.getTime();
					Date secondCutOff = null;
					if(monthId == FEBRUARY) {
						cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
						secondCutOff = cal.getTime();
					} else {
						cal.set(Calendar.DAY_OF_MONTH, THIRTIETH_DAY);
						secondCutOff = cal.getTime();
					}

					String monthName = DateUtil.convertToStringMonth(monthId);
					Integer period = getPeriod(timePeriodSchedule.getDateFrom(), timePeriodSchedule.getDateTo());
					if(period == FIRST_PERIOD) {
						thirteenthMonthBonusDtos.add(new ThirteenthMonthBonusDto(employee.getEmployeeNo(), employeeName,
								employee.getPosition().getName(), monthId, monthName, firstCutOff,
								secondCutOff, es.getBasicPay() != null ? es.getBasicPay() : 0.0, 0.0, division.getName(), employeeName));
					} else {
						thirteenthMonthBonusDtos.add(new ThirteenthMonthBonusDto(employee.getEmployeeNo(), employeeName,
								employee.getPosition().getName(), monthId, monthName, firstCutOff,
								secondCutOff, 0.0, es.getBasicPay() != null ? es.getBasicPay() : 0.0, division.getName(), employeeName));
					}
				} else {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(dateTo);
					cal.set(calendar.get(Calendar.YEAR), month-1, 1);
					cal.set(Calendar.DAY_OF_MONTH, FIFTEENTH_DAY);
					Date firstCutOff = cal.getTime();
					Date secondCutOff = null;
					if(month == FEBRUARY) {
						cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
						secondCutOff = cal.getTime();
					} else {
						cal.set(Calendar.DAY_OF_MONTH, THIRTIETH_DAY);
						secondCutOff = cal.getTime();
					}

					String monthName = DateUtil.convertToStringMonth(month);
					thirteenthMonthBonusDtos.add(new ThirteenthMonthBonusDto(employee.getEmployeeNo(), employeeName,
							employee.getPosition().getName(), month, monthName, firstCutOff, secondCutOff, 
							0.0, 0.0, division.getName(), employeeName));
				}
			}
		}
		return thirteenthMonthBonusDtos;
	}
}
