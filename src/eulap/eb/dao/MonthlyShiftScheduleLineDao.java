package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.MonthlyShiftScheduleLine;

/**
 * DAO for {@link MonthlyShiftScheduleLine}

 *
 */

public interface MonthlyShiftScheduleLineDao extends Dao<MonthlyShiftScheduleLine>{

	/**
	 * Get the monthly shift schedule line.
	 * @param payrollTimePeriodId The payroll time period id.
	 * @param payrollTimePeriodScheduleId The payroll time period schedule id.
	 * @param employeeId The employee id.
	 * @return
	 */
	MonthlyShiftScheduleLine getByPeriodAndSchedule(int payrollTimePeriodId, int payrollTimePeriodScheduleId, int employeeId);

}
