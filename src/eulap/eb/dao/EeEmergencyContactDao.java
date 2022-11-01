package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EeEmergencyContact;

/**
 * Data Access Object interface of {@link EeEmergencyContact}

 */

public interface EeEmergencyContactDao extends Dao<EeEmergencyContact> {

	/**
	 * Get the list of employee emergency contacts
	 * @param ebObjectId The parent EB object id
	 * @return The list of employee emergency contacts
	 */
	List<EeEmergencyContact> getEmerContactsByEbObjId(Integer ebObjectId);
}