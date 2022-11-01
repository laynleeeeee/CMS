package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ApLineSetup;

/**
 * DAO Layer of {@link ApLineSetup}

 *
 */
public interface ApLineSetupDao extends Dao<ApLineSetup> {

	/**
	 * Validate the name of the AP Line Setup if unique.
	 * @param apLineSetupId The id of the ap line setup.
	 * @param name The name of the ap line setup.
	 * @param companyId The id of the company
	 * @param divisionId The division id
	 * @return True if the name is unique, otherwise false.
	 */
	boolean isUniqueName(Integer apLineSetupId, String name, Integer companyId, Integer divisionId);

	/**
	 * Search AP Line Setups.
	 * @param companyId The id of the company.
	 * @param name The name of the AP Line setup.
	 * @param status The status: Active, Inactive, All.
	 * @param pageSetting The page setting.
	 * @return The list of AP Line Setups in paging format.
	 */
	Page<ApLineSetup> searchApLineSetups(Integer companyId, String name,
			SearchStatus status, PageSetting pageSetting);

	/**
	 * Retrieve the list of AP Line Setups that matches the search parameters.
	 * @param companyId The id of the company.
	 * @param divisionId The division id
	 * @param name The name of the AP Line Setup.
	 * @return The list of AP Line Setups.
	 */
	List<ApLineSetup> getApLineSetups(Integer companyId, Integer divisionId, String name);

	/**
	 * Retrieve the AP Line Setups that matches the search parameters.
	 * @param companyId The id of the company.
	 * @param divisionId The division id
	 * @param name The name of the AP Line Setup.
	 * @return The AP Line Setups.
	 */
	ApLineSetup getApLineSetup(Integer companyId, Integer divisionId, String name);
}
