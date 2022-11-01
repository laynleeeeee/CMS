package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.service.LabelName2FieldNameUG;

/**
 * Handles the user group data access.  

 *
 */
public interface UserGroupDao extends Dao<UserGroup> {
	/**
	 * Get all user groups.
	 * @return the paged user groups.
	 */
	Page<UserGroup> getUserGroups ();
	
	/**
	 * Get the list of user group.
	 * @param pageSetting the page settings
	 * @param userGroupSearchText The user group name
	 * @param searchCategory The search category
	 * @param activeOnly True if search active user group only, otherwise false.
	 * @return The Page<UserGroup> that holds the user group list.
	 */
	Page<UserGroup> getUserGroups(PageSetting pageSetting, String userGroupSearchText, String searchCategory, boolean activeOnly);
	
	boolean isUniqueUserGroupName (String userGroupSearchText);
	
	/**
	 * Get the list of user groups.
	 * @param pageSetting the page settings
	 * @param userGroupSearchText The user group name
	 * @return The Page<UserGroup> that holds the  user group list.
	 */
	Page<UserGroup> getUserGroups(PageSetting pageSetting, String userGroupSearchText, String searchCategory);
	
	/**
	 * Search for userGroups. 
	 * @param ln2Fn label name
	 * @param ss the search status
	 * @param searchCriteria search criteria
	 * @param pageSetting the page setting
	 * @return the list of userGroups.
	 */
	Page<UserGroup> searchUserGroups(LabelName2FieldNameUG ln2Fn, SearchStatus ss, String searchCriteria, PageSetting pageSetting);
	
	/**
	 * Get the user group id of a user group by its user group name
	 * @param name
	 * @return user group id
	 */
	int getUserGroupIdbyName (String name);

	/**
	 * Get the user group by name.
	 * @param name The name of the user group.
	 * @return The user group object.
	 */
	UserGroup getUserGroupByName (String name);

	/**
	 * Get the list of user group by name.
	 * @param name The name of user group.
	 * @return The list of user group.
	 */
	List<UserGroup> getUserGroup (String name);
	
	/**
	 * Get all active user groups.
	 * @return The list of active user groups.
	 */
	List<UserGroup> getActiveUserGroups ();

	/**
	 * Get the paged list of user groups.
	 * @param groupName The user group name.
	 * @param description The group description.
	 * @param status 1= active, 2 inactive.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<UserGroup> getUserGroups(String groupName, String description, int status, PageSetting pageSetting);
}