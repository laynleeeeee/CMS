package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EmployeeCivilQuery;
import eulap.eb.domain.hibernate.EmployeeFamily;

/**
 * Data access object for {@link EmployeeCivilQuery}

 *
 */
public interface EmployeeCivilQueryDao extends Dao<EmployeeCivilQuery>{

	/**
	 * Get the EmployeeFamily by employee id.
	 * @param employeeId The employee filter.
	 * @return The {@link EmployeeFamily} object.
	 */
	EmployeeCivilQuery getByEmployee(Integer employeeId);

}
