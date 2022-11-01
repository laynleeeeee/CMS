package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.UserGroupAccessRight;

/**
 * Data access object of {@link UserGroupAccessRight}

 *
 */
public interface UserGroupAccessRightDao extends Dao<UserGroupAccessRight>{
	/**
	 * Get the list of user group access rights based on the user group id.
	 * @param userGroupId The unique id of the user group.
	 * @return The list of user group access rights.
	 */
	List<UserGroupAccessRight> getUserGroupAccessRights (Integer userGroupId);

	/**
	 * Get user group access right by product key.
	 * @param productKey The product key.
	 * @return The user group access right.
	 */
	UserGroupAccessRight getUGARByProductKey (Integer productKey);

	/**
	 * Get user group access right by product key and user group id.
	 * @param userGroupId The id of the user group.
	 * @param productKey The product key.
	 * @return The user group access right.
	 */
	UserGroupAccessRight getUGARByPKAndUG (Integer userGroupId, Integer productKey);

	/**
	 * Check if the user group is an admin. 
	 * @param userGroupId the user group id. 
	 * @return true if the user group is admin, otherwise false. 
	 */
	boolean isAdmin (int userGroupId);
	
	/**
	 * Check if the user group contains one or more admin property from user group access right.
	 * @param userGroupId the user group id. 
	 * @param adminProductKeys The list of product keys from {@link admin.properties}
	 * @return True if the user group has admin property, otherwise false.
	 */
	boolean hasAdminModule (int userGroupId, List<Integer> adminProductKeys);
}
