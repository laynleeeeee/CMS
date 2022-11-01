package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.FleetType;

/**
 * Data Access Object of {@link FleetType}

 *
 */
public interface FleetTypeDao extends Dao<FleetType>{

	/**
	 * Searching for fleet type base on the filter.
	 * @param name The fleet type name.
	 * @param companyId companyId The company id.
	 * @param status The status of fleets.
	 * @param pageNumber The page number.
	 * @return The list of {@link FleetType}
	 */
	Page<FleetType> searchFleetTypes(String name, SearchStatus searchStatus, Integer companyId, Integer fleetCategoryId, PageSetting pageSetting);

	/**
	 * Check if fleet type name is unique.
	 * @param fleetTypeId The fleet type ID.
	 * @param name The fleet type name.
	 * @param companyId The company id.
	 * @return True if the fleet type is unique, otherwise false.
	 */
	boolean isUniqueFleetTypeName(int fleetTypeId, String name, Integer companyId);

	/**
	 * Get the fleet type by reference object id.
	 * @param refObjectId The reference object id.
	 * @return {@link FleetType}
	 */
	FleetType getByRereferenceObjectId(Integer refObjectId);
}
