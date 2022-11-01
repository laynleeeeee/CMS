package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FleetInsurancePermitRenewal;

/**
 * Data Access Object of {@link FleetInsurancePermitRenewal}

 *
 */
public interface FleetInsurancePermitRenewalDao extends Dao<FleetInsurancePermitRenewal>{

	/**
	 * Get active {@link List<FleetInsurancePermitRenewal>} by reference of object id.
	 * @param refObjectId The reference object id.
	 * @return {@link List<FleetInsurancePermitRenewal>}.
	 */
	List<FleetInsurancePermitRenewal> getFleetIPRByRefObjectId(Integer refObjectId);
}
