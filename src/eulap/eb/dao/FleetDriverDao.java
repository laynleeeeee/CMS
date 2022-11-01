package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FleetDriver;

/**
 * Data access object for {@link FleetDriver}

 *
 */
public interface FleetDriverDao extends Dao<FleetDriver> {

	/**
	 * Get the list of fleet drivers by the parent eb object id.
	 * @param refObjectId  The reference object.
	 * @return The list of fleet drivers.
	 */
	List<FleetDriver> getByRefObject(int refObjectId, boolean activeOnly);
}
