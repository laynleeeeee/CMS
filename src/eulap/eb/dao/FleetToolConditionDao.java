package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FleetToolCondition;

/**
 * Data Access Object of {@link FleetToolCondition}

 *
 */
public interface FleetToolConditionDao extends Dao<FleetToolCondition>{

	/**
	 * Get the fleet tool condition object by the withdrawal slip item eb object id.
	 * @param wsiEbObjectId  The withdrawal slip item eb object id
	 * @return {@link FleetToolCondition}
	 */
	FleetToolCondition getByWSItem(int wsiEbObjectId);

	/**
	 * Get the list of fleet tools by the parent eb object id.
	 * @param refObjectId  The reference object.
	 * @return The list of fleet tools.
	 */
	List<FleetToolCondition> getByRefObject(Integer refObjectId);
}
