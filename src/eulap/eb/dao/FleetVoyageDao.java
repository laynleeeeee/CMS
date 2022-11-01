package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FleetVoyage;

/**
 * Data Access Object for {@link FleetVoyage}

 *
 */
public interface FleetVoyageDao extends Dao<FleetVoyage>{

	/**
	 * Get the list of {@link FleetVoyage}
	 * @param refObjectId The refObjectId.
	 * @return The list of {@link FleetVoyage}
	 */
	List<FleetVoyage> getFleetVoyagesByRefObjectId(Integer refObjectId);

}
