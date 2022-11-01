package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.DivisionProject;

/**
 * Data access object interface class for {@line DivisionProject}

 */

public interface DivisionProjectDao extends Dao<DivisionProject> {

	/**
	 * Check for the existing division
	 * @param divisionName The division name
	 * @return True if the division exists, otherwise false
	 */
	boolean hasExistingDivProject(Integer customerId);

	/**
	 * Get the division-project object
	 * @param customerId The customer id
	 * @return The division-project object
	 */
	DivisionProject getDivisionProject(Integer customerId);
}
