package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.User;
/**
 * Data Access Object of {@link EmployeeShift}

 */
public interface EmployeeShiftDao extends Dao<EmployeeShift>{

	/**
	 * Get the paged list of employee shift objects based on the specified filter parameters.
	 * @param startShift The start of the shift.
	 * @param endShift The end of the shift.
	 * @param dailyWorkingHours The number of work hours per day.
	 * @param status ALL, ACTIVE, or INACTIVE.
	 * @param user The user currently log.
	 * @param pageSetting The page setting.
	 * @return The list of employee shifts.
	 */
	Page<EmployeeShift> getEmployeeShifts (String startShift, String endShift, Double dailyWorkingHours, SearchStatus status, User user, PageSetting pageSetting);

	/**
	 * Get the paged list of CSC's employee shift objects.
	 * @param companyId The company filter.
	 * @param name The name of the shift.
	 * @param firstHalfShiftStart The first half start of the shift.
	 * @param firstHalfShiftEnd The first half end of the shift.
	 * @param secondHalfShiftStart The second half start of the shift.
	 * @param secondHalfShiftEnd The second half end of the shift.
	 * @param dailyWorkingHours The daily working hours.
	 * @param status ALL, ACTIVE, or INACTIVE.
	 * @param user The user currently log.
	 * @param pageSetting he page setting.
	 * @return The list of employee shifts of CSC.
	 */
	Page<EmployeeShift> getCscEmployeeShifts(Integer companyId, String name, String firstHalfShiftStart, String firstHalfShiftEnd, String secondHalfShiftStart,
			String secondHalfShiftEnd, Double dailyWorkingHours, SearchStatus status, User user, PageSetting pageSetting);

	/**
	 * Get the list of employee shift.
	 * @param companyId The company id.
	 * @return The list of employee shift.
	 */
	List<EmployeeShift> getEmployeeShiftByCompanyId(Integer companyId);

	/**
	 * Checks if the name is unique.
	 * @return True if name is unique, otherwise false.
	 */
	boolean isUniqueName(EmployeeShift employeeShift);

	/**
	 * Checks if there is already an active setting for a company.
	 * @param setting The EmpployeeShift object.
	 * @return True if there is already existing, otherwise false.
	 */
	boolean hasActive(EmployeeShift employeeShift);

	/**
	 * Get employee shift by company, payroll time period, and schedule.
	 * @param companyId The company id, 
	 * @param payollTimePeriodId The payroll time period id.
	 * @param payrollTimePeriodScheduleId The payroll time period schedule id.
	 * @param employeeId The employee id.
	 * @param date The date.
	 * @return The employee shift.
	 */
	EmployeeShift getBySchedule(Integer companyId, Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId, 
			Integer employeeId, Date date);

	/**
	 * Get the list of the latest employee shift.
	 * @param updatedDate The updated date.
	 * @return The list of employee shift.
	 */
	List<EmployeeShift> getLatestEmployeeShift(Date updatedDate);

	/**
	 * Get employee shift by name and company. 
	 * @param shiftName The shift name.
	 * @param companyId The company id.
	 * @return {@link EmployeeShift}
	 */
	EmployeeShift getByNameAndCompanyId(String shiftName, Integer companyId);
}
