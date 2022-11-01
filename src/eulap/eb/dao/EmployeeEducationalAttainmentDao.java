package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EmployeeEducationalAttainment;
import eulap.eb.domain.hibernate.EmployeeFamily;

/**
 * Data access object for {@link EmployeeEducationalAttainment}

 *
 */
public interface EmployeeEducationalAttainmentDao extends Dao<EmployeeEducationalAttainment> {

	/**
	 * Get the EmployeeFamily by employee id.
	 * @param employeeId The employee filter.
	 * @return The {@link EmployeeFamily} object.
	 */
	EmployeeEducationalAttainment getByEmployee(Integer employeeId);
}
