package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EmployeeFamily;

/**
 * Data access object for {@link EmployeeFamily}

 *
 */
public interface EmployeeFamilyDao extends Dao<EmployeeFamily> {

	/**
	 * Get the EmployeeFamily by employee id.
	 * @param employeeId The employee filter.
	 * @return The {@link EmployeeFamily} object.
	 */
	EmployeeFamily getByEmployee(Integer employeeId);

}
