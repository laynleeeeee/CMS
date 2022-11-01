package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.UserCustodian;

/**
 * Data access object for {@link UserCustodian}

 *
 */
public interface UserCustodianDao extends Dao<UserCustodian>{

	/**
	 * Gets the list of user custodians.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param userCustodianName The user custodian name.
	 * @param status The status.
	 * @param pageSetting The page setting.
	 * @return List of user custodians.
	 */
	Page<UserCustodian> getUserCustodians(Integer companyId, Integer divisionId, String userCustodianName, SearchStatus status, PageSetting pageSetting);

	/**
	 * Check if user custodian is duplicated.
	 * @param userCustodian The user custodian object.
	 * @return True or false.
	 */
	boolean isDuplicateUserCustodian(UserCustodian userCustodian);

	/**
	 * Gets the list of {@link UserCustodian}
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param userCustodianId The user custodian id
	 * @return List of {@link UserCustodian}
	 */
	List<UserCustodian> getUserCustodians(Integer companyId, Integer divisionId, Integer userCustodianId);

	/**
	 * Get the list of user custodian
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param name The custodian name
	 * @param isExact True if exact value, otherwise false
	 * @return The list of user custodian
	 */
	List<UserCustodian> getUserCustodians(Integer companyId, Integer divisionId, Integer userCustodianId, String name, boolean isExact);
}
