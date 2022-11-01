package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.UnitMeasurement;

/**
 * Data access object of {@link UnitMeasurement}

 */
public interface UnitMeasurementDao extends Dao<UnitMeasurement>{

	/**
	 * Get unit of measurement object
	 * @param unitMeasurementId The unit of measurement id
	 * @return The unit of measurement object
	 */
	UnitMeasurement getUnitMeasurementById (int unitMeasurementId);

	/**
	 * Check if unit measurement is unique
	 * @param unitMeasurement The unit of measurement object
	 * @param unitMeasurementId The unit of measurement id
	 * @return True if unique, otherwise false
	 */
	boolean isUniqueUnitMeasurement (UnitMeasurement unitMeasurement, int unitMeasurementId);

	/**
	 * Get all user measurements.
	 * @return The page collection of unit measurements.
	 */
	Page<UnitMeasurement> getAllUnitMeasurements();

	/**
	 * Search/filter unit measurement based on the parameters.
	 * @param name The unit measurement name.
	 * @param isActive True if Active, otherwise false.
	 * @param isAllSelected True if selected, otherwise false.
	 * @param pageSetting The page setting.
	 * @return The paged collection of unit measurement.
	 */
	Page<UnitMeasurement> searchUnitMeasurements (String name, boolean isActive, boolean isAllSelected, PageSetting pageSetting);

	/**
	 * Get the active unit of measurements.
	 * @return The list of active unit of measurements.
	 */
	List<UnitMeasurement> getActiveUnitMeasurements ();

	/**
	 * Get the active unit of measurements.
	 * @param name The name of Unit of Measurement.
	 * @param id The unique id of unit of measurement.
	 * @return The list of active unit of measurements.
	 */
	List<UnitMeasurement> getActiveUnitMeasurements (String name, Integer id, Boolean noLimit);

	/**
	 * Get unit measurement by name.
	 * @param unitMeasurementName The unit of measurement.
	 * @return The unit measurement name.
	 */
	UnitMeasurement getUMByName (String unitMeasurementName);

	/**
	 * Get the kilogram unit of measurement.
	 * @return The kilo unit of measurement.
	 */
	UnitMeasurement getKilo ();
}
