package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ArLineDao;
import eulap.eb.dao.ArMiscellaneousLineDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArMiscellaneousLine;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;

/**
 * Class that handles the business logic of Unit Measurement

 */
@Service
public class UnitMeasurementService {
	@Autowired
	private UnitMeasurementDao unitMeasurementDao;
	@Autowired
	private ArMiscellaneousLineDao miscellaneousLineDao;
	@Autowired
	private ArLineDao arLineDao;

	/**
	 * Get the unit measurement object
	 * @param unitMeasurementId The unit measurement id
	 * @return The unit measurement object
	 */
	public UnitMeasurement getUnitMeasurement (int unitMeasurementId){
		UnitMeasurement unitMeasurement = unitMeasurementDao.getUnitMeasurementById(unitMeasurementId);
		if (unitMeasurement == null)
			return null;
		return unitMeasurement;
	}

	/**
	 * Validate/Check if the unit measurement is unique or not.
	 * @param unitMeasurement The unit measurement object to be checked.
	 * @param unitMeasurementId The unit measurement id of the unit measurement object.
	 * @return True if unit measurement is unique, otherwise false.
	 */
	public boolean isUnique (UnitMeasurement unitMeasurement, int unitMeasurementId){
		if (unitMeasurement.getId() == 0)
			return unitMeasurementDao.isUniqueUnitMeasurement(unitMeasurement, unitMeasurementId);
		UnitMeasurement oldUnitMeasurement = unitMeasurementDao.get(unitMeasurement.getId());
		// If user did not change the name
		if (unitMeasurement.getName().trim().equalsIgnoreCase(oldUnitMeasurement.getName().trim()))
			return true;
		return unitMeasurementDao.isUniqueUnitMeasurement(unitMeasurement, unitMeasurementId);
	}

	/**
	 * Load all unit measurements.
	 * @return The page list of unit measurements.
	 */
	public Page<UnitMeasurement> getAllUnitMeasurements (){
		return unitMeasurementDao.getAllUnitMeasurements();
	}

	/**
	 * Search/filter unit measurement based on the parameters.
	 * @return Paged collection of unit measurement.
	 */
	public Page<UnitMeasurement> searchUnitMeasurements (String name, boolean isActive, boolean isAllSelected, int pageNumber){
		return unitMeasurementDao.searchUnitMeasurements(name.trim(),isActive, isAllSelected, new PageSetting(pageNumber,25));
	}

	/**
	 * Save the unit measurement object.
	 * @param unitMeasurement The unit measurement object to be saved
	 * @param user The logged in user
	 */
	public void saveUnitMeasurement (UnitMeasurement unitMeasurement, User user){
		boolean isNewRecord = unitMeasurement.getId() == 0 ? true : false;
		AuditUtil.addAudit(unitMeasurement, new Audit(user.getId(), isNewRecord, new Date()));
		unitMeasurementDao.saveOrUpdate(unitMeasurement);
	}

	/**
	 * Get the active unit of measurements.
	 * @return The list of active unit of measurements.
	 */
	public List<UnitMeasurement> getActiveUnitMeasurements () {
		return unitMeasurementDao.getActiveUnitMeasurements();
	}

	/**
	 * Get the list of active {@link UnitMeasurement} objects.
	 * Append inactive {@link UnitMeasurement} based on the provided uomId parameter.
	 * @param uomId The {@link UnitMeasurement} id.
	 * @return The list of active {@link UnitMeasurement}.
	 */
	public List<UnitMeasurement> getActiveUnitMeasurements(Integer uomId) {
		List<UnitMeasurement> uoms =  unitMeasurementDao.getAllActive();
		UnitMeasurement uom = uomId != null ? unitMeasurementDao.get(uomId) : null;
		if(uom != null && !uom.isActive()) {
			uoms.add(uom);
		}
		return uoms;
	}

	/**
	 * Get the active unit of measurements.
	 * @param name The name of Unit of Measurement.
	 * @param id The unique id of unit of measurement.
	 * @param noLimit True if display all unit of measurements.
	 * @return The list of active unit of measurements.
	 */
	public List<UnitMeasurement> getActiveUnitMeasurements (String name, Integer id, Boolean noLimit) {
		List<UnitMeasurement> unitMeasurements = unitMeasurementDao.getActiveUnitMeasurements(name, id, noLimit);
		if (unitMeasurements == null || unitMeasurements.isEmpty())
			return Collections.emptyList();
		return unitMeasurements;
	}

	/**
	 * Get all unit of measurement used in AR Line.
	 * Added a default value "None".
	 * @param arLineSetupId The ar line setup id.
	 * @return List of all unit of measurement.
	 */
	public List<UnitMeasurement> getOUMs(int arLineSetupId) {
		List<ArLine> arLines = arLineDao.getArLinesBySetupId(arLineSetupId);
		List<ArMiscellaneousLine> miscLines = miscellaneousLineDao.getArMiscLines(arLineSetupId);
		List<UnitMeasurement> uOMs = new ArrayList<UnitMeasurement>();
		UnitMeasurement uom = new UnitMeasurement();
		for(ArLine arLine : arLines) {
			if(arLine.getUnitOfMeasurementId() != null) {
				addUnitOfMeasurement(uOMs, arLine.getUnitOfMeasurementId());
			}
		}

		for(ArMiscellaneousLine miscLine : miscLines) {
			if(miscLine.getUnitOfMeasurementId() != null) {
				addUnitOfMeasurement(uOMs, miscLine.getUnitOfMeasurementId());
			}
		}
		setDefaultUOM(uom,uOMs);
		return uOMs;
	}

	private List<UnitMeasurement> addUnitOfMeasurement(List<UnitMeasurement> uOMs, int uomId) {
		UnitMeasurement uomObj = unitMeasurementDao.get(uomId);
		//Filter to avoid duplication.
		if(!uOMs.contains(uomObj)) {
			uOMs.add(uomObj);
		}
		return uOMs;
	}

	private List<UnitMeasurement> setDefaultUOM(UnitMeasurement uom, List<UnitMeasurement> uOMs) {
		uom.setId(0);
		uom.setName("None");
		uOMs.add(uom);
		return uOMs;
	}

	/**
	 * Get unit measurement by name.
	 * @param unitMeasurementName The unit of meaurement.
	 * @return The unit measurement name.
	 */
	public UnitMeasurement getUMByName (String unitMeasurementName) {
		return unitMeasurementDao.getUMByName(unitMeasurementName);
	}
}
