package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.dto.LoginCredential;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.LabelName2FieldName;
import eulap.eb.webservice.WebServiceCredential;

/**
 * Handles the user data access.  

 *
 */
public interface UserDao extends Dao<User>{
	/**
	 * Get all users.
	 * @return the paged users.
	 */
	Page<User> getUsers ();

	/**
	 * Search for users. 
	 * @param ln2Fn label name
	 * @param ss the search status
	 * @param searchCriteria search criteria
	 * @param pageSetting the page setting
	 * @return the list of users.
	 */
	Page<User> searchUsers (LabelName2FieldName ln2Fn, SearchStatus ss, String searchCriteria, PageSetting pageSetting);
	
	boolean isUniqueUserName (String userName);

	/**
	 * Get the list of users.
	 * @param pageSetting the page settings
	 * @param userName The user name
	 * @return The Page<User> that holds the  user list.
	 */
	Page<User> getUsers (PageSetting pageSetting, String userGroupSearchText, String searchCategory);

	/**
	 * Validate the user.
	 * @param loginCredetial The login credential.
	 * @return true for valid user, otherwise false.
	 */
	boolean validateUser (LoginCredential loginCredetial);

	/**
	 * Is valid user. The password should be encrypted based on the CMS framework
	 */
	boolean isValidUserPEncrypted(WebServiceCredential credential);

	/**
	 * Retrieved the logged in information of the user. 
	 * @param userName the user name.
	 * @param password the user's password, this is encryped with SHA-1 Algorith.
	 * @return The user object.
	 */
	User getUser (String userName, String pasword);

	/**
	 * Get the user id of a user by its user name
	 * @param userName
	 * @return user id
	 */
	int getUserIdbyUserName (String userName);

	/**
	 * Search / filter Users.
	 * @param userName The username.
	 * @param firstName The first name of the user.
	 * @param lastName The last name of the user.
	 * @param userGroupId The user group id of the user.
	 * @param positionId The position id of the user.
	 * @param status The status of user account.
	 * @return The page result of user.
	 */
	Page<User> searchUser(String userName, String firstName,String lastName, Integer userGroupId,
			Integer positionId, Integer loginStatus, Integer status, PageSetting pageSetting);

	/**
	 * Get the list of users.
	 * @return The list of users.
	 */
	List<User> getAllUsers();

	/**
	 * Get the list of users by position.
	 * @param positionId The position id.
	 * @param companyId The id of the company.
	 * @return The list of users.
	 */
	List<User> getUsersByPosition (int positionId, Integer companyId);

	/**
	 * Get the list of users by name.
	 * @param name The name of user.
	 * @return The list of users.
	 */
	List<User> getUsersByName (String name);

	/**
	 * Get the paged list of users that are existing in the user companies.
	 * @param userName
	 * @param companyName
	 * @param searchStatus
	 * @param pageSetting
	 * @return The list of users with companies.
	 */
	Page<User> getUsersWithCompanies (String userName, String companyName, SearchStatus searchStatus, PageSetting pageSetting);

	/**
	 * Get the users by user name.
	 * @param userName The name of a user.
	 * @param isActiveOnly Check if the user is active.
	 * @param limit Number of user should display
	 * @param userId The user id.
	 * @return The list of users
	 */
	List<User> getUsersByUsername (String userName, boolean isActiveOnly, Integer limit, Integer userId);

	/**
	 * Get the user By userCompany Id.
	 * @param userCompanyId The user Company id.
	 * @return The user.
	 */
	User getUserByUserCompany(int userCompanyId);

	/**
	 * Get the User By name.
	 * @param name The name of the user.
	 * @return The user.
	 */
	User getUserByName(String name);

	/**
	 * Get the list of users by Eb Object Id.
	 * @param ebObjectId The Eb Object Id.
	 * @return The list of users.
	 */
	List<User> getUsersByEbObjectId(Integer ebObjectId);

	/**
	 * Get the users by user name.
	 * @param userName The name of a user.
	 * @param isActiveOnly Check if the user is active.
	 * @param limit Number of user should display
	 * @param userId The user id.
	 * @return The list of users
	 */
	List<User> getUsersByNameAndId(String userName, boolean isActiveOnly, Integer limit, Integer userId);

	/**
	 * Check if the user has a unique first and last name
	 * @param firstName The user's first name
	 * @param lastName The user's last name
	 * @param userId The user id
	 * @return True if the name is unique, otherwise false
	 */
	boolean isUniqueName(String firstName, String lastName, Integer userId);
}
