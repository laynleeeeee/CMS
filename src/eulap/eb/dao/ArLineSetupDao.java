package eulap.eb.dao;

import java.util.Collection;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ArLineSetup;

/**
 * Data Access Object of {@link ArLineSetup}

 *
 */
public interface ArLineSetupDao extends Dao<ArLineSetup>{

	/**
	 * Evaluate the AR Line Setup name if unique.
	 * @param name The name to be evaluated.
	 * @return True if unique, false if duplicate.
	 */
	public boolean hasDuplicate(ArLineSetup arLineSetup);

	/**
	 * Search AR Line Setup.
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return Paged result.
	 */
	Page<ArLineSetup> searchArLines(String searchCriteria, PageSetting pageSetting);

	/**
	 * Get the AR Lines based on the Id service lease key.
	 * @param serviceLeaseKeyId The service lease key of the logged user.
	 * @return Collection of AR Line Setup.
	 */
	Collection<ArLineSetup> getArLines(int companyId, int serviceLeaseKeyId);

	/**
	 * Get all active AR Line Setup accounts.
	 * @return The list of active AR Line Setup accounts.
	 */
	List<ArLineSetup> getAllActiveARLineAccts();

	/**
	 * Get the AR Line Setup object using its number.
	 * @param arLineNumber The Ar Line setup number.
	 * @param serviceLeaseKeyId The service lease Id of the logged user.
	 * @return The Ar line setup object.
	 */
	ArLineSetup getArLineSetupByNumber (String arLineNumber, int serviceLeaseKeyId);

	/**
	 * Get the list of AR Line setups based on its name.
	 * @param name The name of the ar line setup.
	 * @param arCustAcctId The unique id of Ar Customer Account.
	 * @param id The unique id of Ar Line Setup.
	 * @param noLimit True if display all unit of measurements.
	 * @param isExact True if the name must be exact.
	 * @param serviceLeaseKeyId The service lease key of the logged user.
	 * @param divisionId The division id
	 * @return The list of AR Line setups.
	 */
	public List<ArLineSetup> getArLineSetUps (String name, Integer arCustAcctId, Integer id, Boolean noLimit, 
			Boolean isExact, int serviceLeaseKeyId, Integer divisionId);

	/**
	 * Get the list of AR Line setups based on its name.
	 * @param companyId The selected company id.
	 * @param name The name of the ar line setup.
	 * @param isExact True if the name must be exact.
	 * @param serviceLeaseKeyId The service lease key of the logged user.
	 * @return The list of AR Line set ups.
	 */
	public List<ArLineSetup> getArLineSetUps (Integer companyId, String name, Boolean isExact, int serviceLeaseKeyId);

	/**
	 * Get the Ar Line setup object by name.
	 * @param arLineSetupName The ar line setup name.
	 * @param companyId The id of the company.
	 * @param divisionId The division id
	 * @return AR Line Setup object.
	 */
	public ArLineSetup getALSetupByNameAndCompany (String arLineSetupName, Integer companyId, Integer divisionId);

	/**
	 * Search the AR Line Setup.
	 * @param name The name of the AR Line setup.
	 * @param compnayId The company id.
	 * @param status The status of the AR Line Setup.
	 * @param pageSetting The page setting.
	 * @return The page result.
	 */
	public Page<ArLineSetup> searchArLineSetup(String name, Integer companyId, SearchStatus status, PageSetting pageSetting);
}