package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FleetItemConsumedDto;
import eulap.eb.web.dto.FleetJobOrderDto;

/**
 * Data Access of {@link FleetProfile}

 *
 */
public interface FleetProfileDao extends Dao<FleetProfile> {

	/**
	 * Get the list of fleet profiles.
	 * @param codeVesselName The code or vessel name filter.
	 * @param user The logged user.
	 */
	List<FleetProfile> getFleetProfiles(String codeVesselName, User user);

	/**
	 * Check if the fleet profile code / vessel name is unique.
	 * @param fleetProfile The fleet profile object to be checked.
	 * @return True if unique, otherwise false.
	 */
	boolean isUniqueCode (FleetProfile fleetProfile);

	/**
	 * Get the fleet job orders.
	 * @param dateFrom The date start.
	 * @param dateTo The date end.
	 * @param pageSetting The page setting.
	 * @return The list of job orders from fleet.
	 */
	Page<FleetJobOrderDto> getFleetJobOrders(Integer divisionId, Date dateFrom, Date dateTo, PageSetting pageSetting);

	/**
	 * Get the list of Fleet profiles by company id.
	 * @param companyId The company id.
	 * @param codeVesselName The vessel name.
	 * @param isExact If exact code vessel name.
	 * @return {@link List<FleetProfile>}
	 */
	List<FleetProfile> getFleetProfilesByCompanyId(String codeVesselName, Integer companyId, Boolean isExact); 

	/**
	 * Get the fleet items consumed.
	 * @param dateFrom The date start.
	 * @param dateTo The date end.
	 * @param stockCode The item stock code.
	 * @param description The item description.
	 * @param pageSetting The page setting.
	 * @return The list of items consumed from fleet.
	 */
	Page<FleetItemConsumedDto> getFleetItemsConsumed(Integer divisionId, Date dateFrom, Date dateTo, 
			String stockCode, String description, boolean isDescending, Integer itemCategoryId, PageSetting pageSetting);

	/**
	 * Check if plate number is unique.
	 * @param plateNo The plate number.
	 * @param fleetProfileId The fleet profile id.
	 * @return True if plate number is unique, otherwise, false.
	 */
	boolean isUniquePlateNo(String plateNo, Integer fleetProfileId);

	/**
	 * Get the list of {@link FleetProfile} by plate number.
	 * @param companyId The company id.
	 * @param plateNo The plate number.
	 * @param isExact True if exact plate number value, otherwise false.
	 * @return The list of {@link FleetProfile}.
	 */
	List<FleetProfile> getFleetsByPlateNo(Integer companyId, String plateNo, Boolean isExact);
}
