package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.FleetProfileDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.web.dto.FleetAttribCostJrDto;
import eulap.eb.web.dto.FleetItemConsumedDto;
import eulap.eb.web.dto.FleetJobOrderDto;

/**
 * Service class for Fleet attributable cost.

 *
 */
@Service
public class FleetAttribCostService {
	@Autowired
	private FleetProfileDao fleetProfileDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;

	/**
	 * Get the fleet job orders.
	 * @param dateFrom The date start.
	 * @param dateTo The date end.
	 * @param pageNumber The page number.
	 * @return The list of job orders from fleet.
	 */
	public Page<FleetJobOrderDto> getFleetJobOrders(Integer divisionId, Date dateFrom, Date dateTo, PageSetting pageSetting) {
		return fleetProfileDao.getFleetJobOrders(divisionId, dateFrom, dateTo, pageSetting);
	}

	public FleetAttribCostJrDto getFleetAttribCostJrDto(FleetProfile fleetProfile,
			Page<FleetJobOrderDto> jobOrderDtos, Page<FleetItemConsumedDto> itemConsumedDtos) {
		FleetAttribCostJrDto fleetAttribCostJrDto = new FleetAttribCostJrDto();
		fleetAttribCostJrDto.setCodeVessel(fleetProfile.getCodeVesselName());
		fleetAttribCostJrDto.setMake(fleetProfile.getMake());
		fleetAttribCostJrDto.setOfficialNo(fleetProfile.getOfficialNo());
		fleetAttribCostJrDto.setChassisNo(fleetProfile.getChassisNo());
		fleetAttribCostJrDto.setCallSign(fleetProfile.getCallSign());
		fleetAttribCostJrDto.setEngineNo(fleetProfile.getEngineNo());
		fleetAttribCostJrDto.setJobOrderDtos((List<FleetJobOrderDto>) jobOrderDtos.getData());
		fleetAttribCostJrDto.setItemConsumedDtos((List<FleetItemConsumedDto>) itemConsumedDtos.getData());
		return fleetAttribCostJrDto;
	}

	public Company getCompanyByRefEbObjId(Integer refEbObjId) {
		EBObject compEbObject = objectToObjectDao.getOtherReference(refEbObjId, FleetProfile.FP_COMPANY_OR_TYPE_ID);
		return companyDao.getByEbObjectId(compEbObject.getId());
	}

	/**
	 * Get the fleet items consumed.
	 * @param dateFrom The date start.
	 * @param dateTo The date end.
	 * @param stockCode The item stock code.
	 * @param description The item description.
	 * @param pageNumber The page number.
	 * @return The list of items consumed from fleet.
	 */
	public Page<FleetItemConsumedDto> getFleetItemsConsumed(Integer divisionId, Date dateFrom, Date dateTo, 
			String stockCode, String description, boolean isDescending, Integer itemCategoryId, PageSetting pageSetting) {
		return fleetProfileDao.getFleetItemsConsumed(divisionId, dateFrom, dateTo, stockCode, description, isDescending, itemCategoryId,
				pageSetting);
	}
}
