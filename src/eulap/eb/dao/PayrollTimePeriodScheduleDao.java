package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;

/**
 * DAO (Data-Access-Object) of {@link PayrollTimePeriodSchedule}

 */
public interface PayrollTimePeriodScheduleDao extends Dao<PayrollTimePeriodSchedule> {

	/**
	 * Get the list of payroll time period schedule
	 * by payroll time period id
	 * @param payrollTimePeriodId The payroll time period id
	 * @return The list of payroll time period schedule
	 */
	List<PayrollTimePeriodSchedule> getTimePeriodSchedules(Integer payrollTimePeriodId);

	/**
	 * Evaluate if the payroll time period is valid.
	 * <br>Payroll time period should not be in-range or equal to an existing time period.
	 * @param payrollTimePeriod The time period to be evaluated.
	 * @return True if time period is valid, otherwise false.
	 */
	boolean isValidPayrollTimePeriod(PayrollTimePeriodSchedule payrollTimePeriodSched);

	/**
	 * Get the list of payroll time period schedules by month and year.
	 * @param month The month filter.
	 * @param year The year filter.
	 * @param isActiveOnly true if active only, else false
	 * @return The list of payroll time period schedule
	 */
	List<PayrollTimePeriodSchedule> getTimePeriodSchedules(Integer month, Integer year, boolean isActiveOnly);

	/**
	 * Get the list of payroll time period schedules by month and year.
	 * @param month The month filter.
	 * @param year The year filter.
	 * @param isActiveOnly true if active only, else false
	 * @param timePeriodId the time period ID
	 * @return The list of payroll time period schedule
	 */
	List<PayrollTimePeriodSchedule> getTimePeriodSchedules(Integer month, Integer year, boolean isActiveOnly, Integer timePeriodId);

	/**
	 * Check if the time period schedule is already is used in the payroll form.
	 * @param schedule The schedule object to be checked.
	 * @return True if used, otherwise false.
	 */
	boolean isUsed (PayrollTimePeriodSchedule schedule);
}
