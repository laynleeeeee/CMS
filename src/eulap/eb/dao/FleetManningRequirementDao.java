package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.FleetManningRequirement;

/**
 * Data Access Object for {@link FleetManningRequirement}

 *
 */
public interface FleetManningRequirementDao extends Dao<FleetManningRequirement>{

	/**
	 * Get {@link List<FleetManningRequirement>} by reference of object id.
	 * @param refObjectId The reference object id.
	 * @param orTypeId The or type id.
	 * @return {@link List<FleetManningRequirement>}.
	 */
	List<FleetManningRequirement> getAllFleetMRByRefObjectId(Integer refObjectId, Integer orTypeId);

	/**
	 * Get the page of Fleet Manning requirements base on the filters.
	 * @param position
	 * @param license
	 * @param number
	 * @param remarks
	 * @param department
	 * @param status
	 * @param pageNumber
	 * @param refObjectId
	 * @return {@link Page<FleetManningRequirement>}
	 */
	Page<FleetManningRequirement> searchManningRequirements(String position, String license, String number,
			String remarks, String department, Integer refObjectId, SearchStatus searchStatus, PageSetting pageSetting);

	/**
	 * Check if manning is unique per License, Number, Position and Department.
	 * @param manningRequirement The manning requirements main object.
	 * @param refObjectId The reference object.
	 * @return True if manning requirement is unique, Otherwise false.
	 */
	boolean isUniqueManningRequirements(FleetManningRequirement manningRequirement, Integer refObjectId);

}
