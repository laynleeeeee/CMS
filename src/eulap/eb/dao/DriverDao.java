package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Driver;

/**
 * Data Access Object of {@link Driver}

 *
 */
public interface DriverDao extends Dao<Driver> {

	/**
	 *
	 * Search drivers by the specified criteria.
	 * @param name The driver name.
	 * @param companyId The company id.
	 * @param status The status of the driver.
	 * @param pageSetting The page setting.
	 * @return The paged list of drivers.
	 */
	Page<Driver> searchDrivers(String name, Integer companyId, SearchStatus status, PageSetting pageSetting);

	/**
	 * Check if the driver name is unique.
	 * @param driverId The driver id.
	 * @param companyId The company id.
	 * @param firstName The driver's first name.
	 * @param middleName The driver's middle name.
	 * @param lastName The driver's last name.
	 * @return True if driver name is unique, otherwise, false.
	 */
	boolean isUniqueName(Integer driverId, Integer companyId, String firstName, String middleName, String lastName);

	/**
	 * Get the list of drivers base on company and the driver name.
	 * @param name The driver name.
	 * @param companyId The company id.
	 * @return List of {@link Driver}.
	 */
	List<Driver> getDriversByName(Integer companyId, String name);
}
