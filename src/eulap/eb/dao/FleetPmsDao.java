package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FleetPms;

/**
 * Data Access Object for {@link FleetPms}

 *
 */
public interface FleetPmsDao extends Dao<FleetPms>{

	/**
	 * Get the List of FleetPms.
	 * @param refObjectId The refObjectId.
	 * @return The Schedule of Preventive Maintenance. 
	 */
	List<FleetPms> getFleetPmsByRefObjectId(Integer refObjectId);

}
