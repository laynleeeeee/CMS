package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EmployeeSalaryDetail;

/**
 * Data Access Object of {@link EmployeeSalaryDetail}

 *
 */
public interface EmployeeSalaryDetailDao extends Dao<EmployeeSalaryDetail>{

	/**
	 * Get the employee salary detail object
	 * by employee id
	 * @param employeeId The employee id
	 * @return The employee salary detail object
	 */
	EmployeeSalaryDetail getSalaryDetailByEmployeeId(Integer employeeId);
}
