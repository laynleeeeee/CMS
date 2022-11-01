package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.domain.Audit;
import eulap.common.domain.UserInfo;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.UserGroupDao;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserGroup;
import bp.web.ar.AuditUtil;

/**
 * The user group service that handle the business layer of the user group object.

 *
 */
@Service
public class UserGroupService {
	@Autowired
	private UserGroupDao userGroupDao;
	
	public void setUserGroupDao(UserGroupDao userGroupDao) {
		this.userGroupDao = userGroupDao;
	}

	/**
	 * Get all the user groups.
	 * @return The list of user groups wrapped in a page.
	 */
	public Page<UserGroup> getALLUserGroups () {
		return userGroupDao.getUserGroups();
	}
	
	/**
	 * Get the list of user groups.
	 * @param searchCriteria The search criteria.
	 * @param activeOnly True to search for active user groups, otherwise false.
	 * @return The paged date of user groups.
	 */
	public Page<UserGroup> getUserGroups (String searchCriteria, String searchCategory, boolean activeOnly) {
		PageSetting pageSetting = new PageSetting(0);
		return getUserGroups(searchCriteria, searchCategory, activeOnly, pageSetting);
	}
	
	/**
	 * Get the list of user groups.
	 * @param searchCriteria The search criteria.
	 * @param searchCategory The search category
	 * @param activeOnly True to search for active user groups, otherwise false.
	 * @param pageNumber The page number
	 * @return The paged date of user groups.
	 */
	public Page<UserGroup> getUserGroups (String searchCriteria, String searchCategory, boolean activeOnly, int pageNumber) {
		PageSetting pageSetting = new PageSetting(pageNumber);
		return getUserGroups(searchCriteria, searchCategory, activeOnly, pageSetting);
	}
	
	private Page<UserGroup> getUserGroups (String searchCriteria, String searchCategory, boolean activeOnly, PageSetting pageSetting) {
		// * means search all
		if (searchCriteria.equals("*"))
			searchCriteria = "";
		return userGroupDao.getUserGroups(pageSetting,  searchCriteria, searchCategory,   activeOnly);
	}	
	
	/**
	 * Get the list of user groups.
	 * @param searchCriteria The search criteria.
	 * @return The paged date of user groups.
	 */	
	public Page<UserGroup> getUserGroups (String searchCriteria, String searchCategory) {
		PageSetting pageSetting = new PageSetting(0);
		return getUserGroups(searchCriteria, searchCategory, pageSetting);
	}
	
	/**
	 * Get the list of user groups.
	 * @param searchCriteria The search criteria.
	 * @param searchCategory The search category
	 * @param activeOnly True to search for active user groups, otherwise false.
	 * @param pageNumber The page number
	 * @return The paged date of user groups.
	 */	
	public Page<UserGroup> getUserGroups (String searchCriteria, String searchCategory, int pageNumber) {
		PageSetting pageSetting = new PageSetting(pageNumber);
		return getUserGroups(searchCriteria, searchCategory, pageSetting);
	}
	
	private Page<UserGroup> getUserGroups (String searchCriteria, String searchCategory, PageSetting pageSetting) {
		// * means search all
		if (searchCriteria.equals("*"))
			searchCriteria = "";
		return userGroupDao.getUserGroups(pageSetting, searchCriteria, searchCategory);
	}
	
	/**
	 * Get the list of all user groups.
	 * @return The list of user groups.
	 */
	public List<UserGroup> getAll () {
		return new ArrayList<UserGroup>(userGroupDao.getAll());
	}

	public void save(UserGroup userGroup, boolean isNewRecord, UserInfo ui) {
		AuditUtil.addAudit(userGroup, new Audit (ui.getUserId(), isNewRecord, new Date ()));
		userGroupDao.saveOrUpdate(userGroup);
	}

	public UserGroup getUserGroup (int userGroupId) {
		return userGroupDao.get(userGroupId);
	}
	
	public boolean isUniqueUserGroupName (String userGroupName) {
		return userGroupDao.isUniqueUserGroupName(userGroupName);
	}
	
	public Page<UserGroup> searchUserGroups (String criteria, String field, String status, PageSetting pageSetting){
		LabelName2FieldNameUG ln2FnC = LabelName2FieldNameUG.getInstanceOf(field);
		SearchStatus ss = SearchStatus.getInstanceOf(status);
		return userGroupDao.searchUserGroups(ln2FnC, ss, criteria, pageSetting);
	} 
	
	public int getUserGroupIdByName(String name) {
		return userGroupDao.getUserGroupIdbyName(name);
	}

	/**
	 * Get user group by name.
	 * @param name The name of user group.
	 * @return The user group object.
	 */
	public UserGroup getUserGroupByName (String name){
		return userGroupDao.getUserGroupByName(name);
	}

	/**
	 * Get the list of user group.
	 * @return The list of user group.
	 */
	public List<UserGroup> getUserGroups (String name){
		return userGroupDao.getUserGroup(name);
	}
	
	/**
	 * Get all active user groups.
	 * @return The list of active user groups.
	 */
	public List<UserGroup> getActiveUserGroups () {
		return userGroupDao.getActiveUserGroups();
	}

	/**
	 * Get the paged list of user groups.
	 * @param groupName The user group name.
	 * @param description The group description.
	 * @param status 1=active, 2=inactive.
	 * @param pageNumber The page number.
	 * @return The paged result.
	 */
	public Page<UserGroup> getUserGroups(String groupName, String description, int status, int pageNumber) {
		return userGroupDao.getUserGroups(groupName.trim(), description.trim(), status, new PageSetting(pageNumber, 25));
	}

	/**
	 * Save the {@link UserGroup}.
	 */
	public void save(UserGroup userGroup, User user) {
		boolean isNewRecord = userGroup.getId() == 0 ? true : false;
		AuditUtil.addAudit(userGroup, new Audit (user.getId(), isNewRecord, new Date ()));
		userGroup.setName(userGroup.getName().trim());
		userGroup.setDescription(userGroup.getDescription().trim());
		userGroupDao.saveOrUpdate(userGroup);
	}
}