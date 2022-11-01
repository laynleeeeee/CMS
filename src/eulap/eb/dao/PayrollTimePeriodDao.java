package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.PayrollTimePeriod;

/**
 * DAO (Data-Access-Object) of {@link PayrollTimePeriod}

 */
public interface PayrollTimePeriodDao extends Dao<PayrollTimePeriod>{

	/**
	 * Get the list of payroll time period
	 * @param name The name of the payroll time period
	 * @param month The month of the payroll time period
	 * @param year The year of the payroll time period
	 * @param status The status of the payroll time period
	 * @param pageSetting The page setting
	 * @return The list of the payroll time period
	 */
	Page<PayrollTimePeriod> getPayrollTimePeriods(String name, Integer month, Integer year,
			SearchStatus status, PageSetting pageSetting);

	/**
	 * The payroll time period name
	 * @param payrollTimePeriod The payroll time period
	 * @return True if the payroll time period name is unique, otherwise false.
	 */
	boolean isUniqueName(PayrollTimePeriod payrollTimePeriod);

	/**
	 *  Evaluates if the Month and Year already exists.
	 * @param payrollTimePeriod The payroll time period.
	 * @return True if the payroll time period month and year is unique, else false.
	 */
	boolean isUniqueMonthAndYearCombi(PayrollTimePeriod payrollTimePeriod);
}
