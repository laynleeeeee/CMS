package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ServiceSetting;

/**
 * Data-Access-Object of {@link ServiceSetting}

 */
public interface ServiceSettingDao extends Dao<ServiceSetting>{
	/**
	 * Check if service setting name is unique.
	 * @param service The {@link ServiceSetting} object.
	 * @return True if unique, otherwise false.
	 */
	boolean isUniqueService(ServiceSetting service);

	/**
	 * Get the list of {@link ServiceSetting} in page format.
	 * @param name The service setting name.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param status The status.
	 * @param pageSetting The {@link PageSetting} object.
	 * @return The list of {@link ServiceSetting} in page format.
	 */
	public Page<ServiceSetting> searchServiceSettings(String name, Integer companyId, Integer divisionId,
			SearchStatus status, PageSetting pageSetting);

	/**
	 * Get the list of service setups based on its name.
	 * @param name The name of the service setup.
	 * @param arCustAcctId The unique id of Ar Customer Account.
	 * @param id The unique id of service Setup.
	 * @param noLimit True if display all unit of measurements.
	 * @param isExact True if the name must be exact.
	 * @param divisionId The division id
	 * @return The list of service setups.
	 */
	public List<ServiceSetting> getServiceSettings (String name, Integer arCustAcctId, Integer id, Boolean noLimit, 
			Boolean isExact, Integer divisionId);

	/**
	 * Get service setting by name.
	 * @param serviceSettingName The service setting name.
	 * @return The {@link ServiceSetting}
	 */
	public ServiceSetting getServiceSettingByName(String serviceSettingName);

	/**
	 * Get service setting by division.
	 * @param name The name of the service settings
	 * @param noLimit True to display all service settings, otherwise show 10 result.
	 * @param isExact True to search for exact name (Disregard wild card search) in the service setting table.
	 * @param divisionId The division id.
	 * @param companyId the company id.
	 * @return The {@link ServiceSetting}
	 */
	public List<ServiceSetting> getServiceSettingByDivision(String name, Boolean noLimit, Boolean isExact, Integer divisionId, Integer companyId);
}
