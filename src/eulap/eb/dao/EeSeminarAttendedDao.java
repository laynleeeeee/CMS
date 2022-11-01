package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EeSeminarAttended;

/**
 * Data Access Object interface of {@link EeSeminarAttended}

 */

public interface EeSeminarAttendedDao extends Dao<EeSeminarAttended> {

	/**
	 * Get the list of employee emergency contacts
	 * @param ebObjectId The parent EB object id
	 * @return The list of employee emergency contacts
	 */
	List<EeSeminarAttended> getSeminarAttendedByEbObjectId(Integer ebObjectId);

}