package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.PayrollEmployeeTimeSheet;

/**
 * DAO (Data-Access-Object) of {@link PayrollEmployeeTimeSheet}

 */
public interface PayrollEmployeeTimeSheetDao extends Dao<PayrollEmployeeTimeSheet>{

	/**
	 * Get list of timesheets associated by the payroll.
	 * @param payrollId The payroll filter parameter.
	 * @return List of time sheets.
	 */
	List<PayrollEmployeeTimeSheet> getByPayroll(int payrollId);

	/**
	 * Get list of timesheets associated by the timesheet.
	 * @param timeSheetId The payroll filter parameter.
	 * @return List of time sheets.
	 */
	List<PayrollEmployeeTimeSheet> getByTimeSheet(int timeSheetId);

	/**
	 * Get Employee Time Sheet by Employee Id and Payroll Id
	 * @param employeeId
	 * @param payrollId
	 * @return
	 */
	List<PayrollEmployeeTimeSheet> getByEmployeeIdAndPayrollId(int employeeId, int payrollId);

	/**
	 * Get Employee Time Sheet by Employee Id and Payroll Id
	 * @param employeeId
	 * @param timeSheetId
	 * @return
	 */
	List<PayrollEmployeeTimeSheet> getByEmployeeIdAndTimeSheetId(int employeeId, int timeSheetId);

	/**
	 * Get Employee Time Sheet by Employee Id and Payroll Id
	 * @param employeeId The employee filter.
	 * @param payrollId The payroll filter.
	 * @return get list of time sheets.
	 */
	List<PayrollEmployeeTimeSheet> getByEmployeeAndSchedule(int employeeId, int scheduleId);

	/**
	 * Get the time sheet record by employee, timesheet, and date.
	 * @param employeeId The employee filter.
	 * @param timeSheetId The time sheet filter.
	 * @param date The date.
	 * @return
	 */
	PayrollEmployeeTimeSheet getByEmpTSAndDate (int employeeId, int timeSheetId, Date date);

	/**
	 * Get the latest time sheet record by employee.
	 * @param employeeId The employee filter.
	 * @return {@link PayrollEmployeeTimeSheet}
	 */
	PayrollEmployeeTimeSheet getByEmployeeId(Integer employeeId);
}
