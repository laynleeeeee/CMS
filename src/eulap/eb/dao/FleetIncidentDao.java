package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FleetIncident;

/**
 * Data Access Object for {@link FleetIncident}

 *
 */
public interface FleetIncidentDao extends Dao<FleetIncident>{

	/**
	 * Get the list of {@link FleetIncident}
	 * @param refObjectId The Reference Object Id.
	 * @param isActiveOnly True if active only, otherwise false.
	 * @return The list of {@link FleetIncident}
	 */
	List<FleetIncident> getFleetIncidentsByRefObject(Integer refObjectId, Boolean isActiveOnly);

}
