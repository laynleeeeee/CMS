package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.FleetCategoryDao;
import eulap.eb.dao.FleetTypeDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FleetCategory;
import eulap.eb.domain.hibernate.FleetType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorUtil;

/**
 * A class that handles the business logic of Fleet. 

 *
 */
@Service
public class FleetTypeService {
	private static Logger logger = Logger.getLogger(FleetTypeService.class);
	@Autowired
	private FleetTypeDao fleetTypeDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private FleetCategoryDao fleetCategoryDao;

	/**
	 * Save fleet type.
	 * @param fleetType The fleet type.
	 * @param user The user current log.
	 */
	public void saveFleetType(FleetType fleetType, User user) {
		logger.info("Saving Fleet Type.");
		Date currentDate = new Date();
		boolean isNewRecord = fleetType.getId() == 0;
		if(isNewRecord){
			EBObject eb = new EBObject();
			AuditUtil.addAudit(eb, new Audit(user.getId(), true, currentDate));
			eb.setObjectTypeId(FleetType.FLEET_TYPE_OBJECT_TYPE_ID);
			ebObjectDao.save(eb);
			fleetType.setEbObjectId(eb.getId());
			Company company = companyDao.get(fleetType.getCompanyId());
			objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(company.getEbObjectId(), fleetType.getEbObjectId(), FleetType.FLEET_TYPE_COMPANY_OR_TYPE, user, currentDate));
		} else {
			FleetType ft = fleetTypeDao.get(fleetType.getId());
			if(ft != null){
				EBObject ebObject = objectToObjectDao.getOtherReference(ft.getEbObjectId(), FleetType.FLEET_TYPE_COMPANY_OR_TYPE);
				List<ObjectToObject> objectToObjects = objectToObjectDao.getReferenceObjects(ebObject.getId(), fleetType.getEbObjectId(), FleetType.FLEET_TYPE_COMPANY_OR_TYPE);
				if(objectToObjects != null && !objectToObjects.isEmpty()){
					ObjectToObject toObject = objectToObjects.iterator().next();
					Company company = companyDao.get(fleetType.getCompanyId());
					toObject.setFromObjectId(company.getEbObjectId());
					objectToObjectDao.update(toObject);
				}
			}
		}
		AuditUtil.addAudit(fleetType, new Audit(user.getId(), isNewRecord, currentDate));
		fleetType.setName(fleetType.getName().trim());
		fleetTypeDao.saveOrUpdate(fleetType);
	}

	/**
	 * Searching for fleet type base on the filter.
	 * @param name The fleet type name.
	 * @param status The status of fleets.
	 * @param pageNumber The page number.
	 * @param companyId The company id.
	 * @return The list of {@link FleetType}
	 */
	public Page<FleetType> searchFleetTypes(String name, String status, Integer pageNumber, Integer companyId, Integer fleetCategoryId) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<FleetType> result = fleetTypeDao.searchFleetTypes(name, searchStatus, companyId, fleetCategoryId, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		for (FleetType fleetType : result.getData()) {
			fleetType.setCompany(companyDao.get(getCompanyByRefObjectId(fleetType.getEbObjectId())));
		}
		return result;
	}

	/**
	 * Get the fleet type by Id.
	 * @param fleetTypeId The fleet type id.
	 * @return The fleet type.
	 */
	public FleetType getFleetTypeById(Integer fleetTypeId) {
		return fleetTypeDao.get(fleetTypeId);
	}

	public void validateFleetTypes(FleetType fleetType, Errors errors) {
		if(fleetType.getName() == null || fleetType.getName().trim().isEmpty()) {
			errors.rejectValue("name", null, null, "Fleet Type is a required field.");
		} else if(!fleetTypeDao.isUniqueFleetTypeName(fleetType.getId(), fleetType.getName(), fleetType.getCompanyId())) {
			errors.rejectValue("name", null, null, "Fleet Type is already existing.");
		} else if(fleetType.getName().trim().length() > FleetType.MAX_FLEET_TYPE_NAME) {
			errors.rejectValue("name", null, null, "Fleet Type must not exceed " + FleetType.MAX_FLEET_TYPE_NAME + " characters");
		} else if (StringFormatUtil.containsInvalidChar(fleetType.getName(), StringFormatUtil.PERCENT_CHAR,
				StringFormatUtil.UNDERSCORE_CHAR)) {
				errors.rejectValue("name", null, null, "Invalid characters \'%\' and \'_\'");
		}
		if(fleetType.getCompanyId() == null){
			errors.rejectValue("companyId", null, null, "Company is required");
		} else {
			ValidatorUtil.checkInactiveCompany(companyService, fleetType.getCompanyId(), "companyId", errors, null);
		}
		if(fleetType.getFleetCategoryId() == null || fleetType.getFleetCategoryId().intValue() == -1){
			errors.rejectValue("fleetCategoryId", null, null, "Category is required");
		}
	}

	/**
	 * Get the list of all active fleet types.
	 * @return The list of all active fleet types.
	 */
	public List<FleetType> getAllActiveFTypes () {
		return fleetTypeDao.getAllActive();
	}

	/**
	 * Get the fleet type by reference object id.
	 * @param refObjectId The reference object id.
	 * @return {@link FleetType}
	 */
	public FleetType getFleetTypeByReferenceObjectId(Integer refObjectId){
		return fleetTypeDao.getByRereferenceObjectId(refObjectId);
	}

	/**
	 * Get the company id by ebObject.
	 * @param ebObjectId The eb object id.
	 * @return The company id.
	 */
	public Integer getCompanyByRefObjectId(Integer ebObjectId) {
		EBObject ebObject = objectToObjectDao.getOtherReference(ebObjectId, FleetType.FLEET_TYPE_COMPANY_OR_TYPE);
		Integer companyId = 0;
		if(ebObject != null){
			companyId = companyDao.getAllByRefId(Company.Field.ebObjectId.name(), ebObject.getId()).iterator().next().getId();
		}
		return companyId;
	}

	/**
	 * Get all active fleet categories.
	 */
	public List<FleetCategory> getAllFleetCategories() {
		return fleetCategoryDao.getAllActive();
	}
}