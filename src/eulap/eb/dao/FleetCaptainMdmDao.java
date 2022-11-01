package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FleetCaptainMdm;

/**
 * Data Access Object for {@link FleetCaptainMdm}

 *
 */
public interface FleetCaptainMdmDao extends Dao<FleetCaptainMdm>{

	/**
	 * Get the list of {@link FleetCaptainMdm} by refObjectId
	 * @param refObjectId The refObjectId.
	 * @param isActiveOnly True if active, otherwise false.
	 * @return The list of {@link FleetCaptainMdm}
	 */
	List<FleetCaptainMdm> getFleetCaptainMdms(Integer refObjectId, Boolean isActiveOnly);

}
