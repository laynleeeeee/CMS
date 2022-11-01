package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EmployeeEmploymentQuery;
import eulap.eb.domain.hibernate.EmployeeFamily;

/**
 * Data access object for {@link EmployeeEmploymentQuery}

 *
 */
public interface EmployeeEmploymentQueryDao extends Dao<EmployeeEmploymentQuery>{

	/**
	 * Get the EmployeeFamily by employee id.
	 * @param employeeId The employee filter.
	 * @return The {@link EmployeeFamily} object.
	 */
	EmployeeEmploymentQuery getByEmployee(Integer employeeId);

}
