package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FleetDryDock;

/**
 * Data access object of {@link FleetDryDock}

 *
 */
public interface FleetDryDockDao extends Dao<FleetDryDock>{

	/**
	 * Get the list of Dry Dockings
	 * @param refObjectId The reference object id
	 * @return The list of Dry Dockings
	 */
	List<FleetDryDock> getFleetDryDockings(Integer refObjectId);

}
