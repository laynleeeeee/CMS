package eulap.eb.dao;

import java.util.Collection;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.DivisionDto;

/**
 * Data Access Object of {@link Division}

 */

public interface DivisionDao extends Dao<Division>{

	/**
	 * Get the division hibernate domain object by id and service lease key id.
	 * @param divisionId The division id.
	 * @param user The logged in user.
	 * @return The division hibernate domain object.
	 */
	Division getDivisionById (int divisionId, User user);

	/**
	 * Get all the divisions.
	 * @param user The logged in user.
	 * @return Paged list of divisions.
	 */
	Page<Division> getAllDivisions(User user);

	/**
	 * Search the Division.
	 * @param number The division number.
	 * @param name The division name.
	 * @param user The logged in user.
	 * @param status The status.
	 * @param pageSetting The page setting.
	 * @return The paged divisions.
	 */
	Page<Division> searchDivisions(String number, String name, User user, SearchStatus status, PageSetting pageSetting);

	/**
	 * Evaluate the division field for duplications.
	 * @param division The division object.
	 * @param divisionField 1=number, 2=name.
	 * @param user The logged in user.
	 * @return True if unique, otherwise there is an already existing data.
	 */
	boolean isUniqueDivisionField(Division division, int divisionField, User user);

	/**
	 * Get all active divisions.
	 * @param user The logged in user.
	 * @return Collection of active divisions.
	 */
	Collection<Division> getActiveDivisions (User user);

	/**
	 * Get the list of divisions that are existing in the account combination table.
	 * @param companyId The company id.
	 * @return The divisions that are configured in the account combination table.
	 */
	Collection<Division> getDivisionByAcctCombination (int companyId);

	/**
	 * Get the list of divisions that are existing in the account combination table.
	 * @param companyId The company id.
	 * @param accountId The accout id.
	 * @param selectedDivisionId The selected division id.
	 * @return The divisions that are configured in the account combination table.
	 */
	Collection<Division> getDivisions(int companyId, int accountId, Integer selectedDivisionId, boolean isReport, User user);

	/**
	 * Get the division object by its number.
	 * @param divisionNumber The division number.
	 * @param serviceLeaseKeyId The service lease Id of the logged user.
	 * @return Division object.
	 */
	Division getDivisionByDivNumber(String divisionNumber, int serviceLeaseKeyId);

	/**
	 * Get the division object by name.
	 * @param divisionName The unique name of the division.
	 * @param isActiveOnly True to filter only active divisions, otherwise false.
	 * @param excludeDivWithAcctCombi True if exclude division that already
	 * been tagged to account combination, otherwise false
	 * @return The division object, otherwise null.
	 */
	Division getDivisionByName(String divisionName, boolean isActiveOnly, boolean excludeDivWithAcctCombi);

	/**
	 * Get the list of division.
	 * @param companyId The company id.
	 * @param divisionNumber The division number.
	 * @param limit The number of items to displayed.
	 * @param isActive True if the company is active, otherwise false.
	 * @return The list of division.
	 */
	List<Division> getDivisionByCompanyIdAndName(Integer companyId, String divisionNumber, boolean isActive, Integer limit);

	/**
	 * Get the list all active divisions.
	 * @return The list of active division.
	 */
	Collection<Division> getActiveDivisions();

	/**
	 * Get the division by employee id.
	 * @param employeeId The employee id.
	 * @return The division name of the employess.
	 */
	Division getDivisionByEmployeeId(Integer employeeId);

	/**
	 * Get the division with the max number.
	 * @return The division.
	 */
	Division getMaxDivisionByNumber();

	/**
	 * Get the list of division/s based on either division number or name.
	 * @param numberOrName Parameter that represents either division number or division name.
	 * @param The id of the division to be excluded.
	 * @param True to filter only main level divisions, otherwise false.
	 * @return The list of divisions.
	 */
	List<Division> getDivisions(String numberOrName, Integer divisionId, boolean isMainLevelOnly);

	/**
	 * Search/filter the divisions by criteria.
	 * @param divNumber The division number.
	 * @param name The division name.
	 * @param user The logged in user.
	 * @param searchStatus The search status.
	 * @param isMainDivisionOnly True if to display only the main division.
	 * @param pageSetting The page setting.
	 * @return Paged collection of filtered divisions by criteria.
	 */
	Page<DivisionDto> searchDivisionWithSubs(String divNumber,
			String name, User user, SearchStatus status, boolean isMainDivisionOnly, PageSetting pageSetting);

	/**
	 * Check if the division is last level.
	 * @param divisionId The division id.
	 * @param searchStatusId -1 if All, 0 if Inactive, 1 if active.
	 * @return True or false.
	 */
	boolean isLastLevel(int divisionId, Integer searchStatusId);

	/**
	 * Get all the children division of the specific division given by its id.
	 * @param divisionId The id of the division to be checked.
	 * @param isActive True if only active children will be retrieved, otherwise false.
	 * @return The list of children.
	 */
	List<DivisionDto> getAllChildren(int divisionId, boolean isActive);

	/**
	 * Get all the children division of the specific division given by its id.
	 * @param divisionId The id of the division to be checked.
	 * @param name The name of the child division.
	 * @param isActive True if only active children will be retrieved, otherwise false.
	 * @return The list of children by parent & name
	 */
	List<DivisionDto> getAllChildren(int divisionId, String name, boolean isActive, boolean isExact);

	/**
	 * Get all last level divisions.
	 * Last level divisions refer to any division that has no child division.
	 * @return List of all last level divisions.
	 */
	List<Division> getLastLevelDivisions(boolean isExcludeNoParent);

	/**
	 * Get the list of divisions by account combination.
	 * @param companyId The company id.
	 * @return List of divisions.
	 */
	List<DivisionDto> getByCombination(Integer companyId);

	/**
	 * Check if division id is used as parent reference division
	 * @param divisionId The division id
	 * @return True if the account is used as parent reference division, otherwise false
	 */
	boolean isUsedAsParentDivision(Integer divisionId);

	/**
	 * Retrieve the list of divisions
	 * @param divisionName The division name
	 * @param divisionId The division id
	 * @param isExact True if get the exact value, otherwise false
	 * @param limit The limit of item to be displayed
	 * @return The list of divisions
	 */
	List<Division> retrieveDivisions(String divisionName, Integer divisionId, boolean isExact, Integer limit);

	/**
	 * Get the list of divisions.
	 * @param name The division name.
	 * @return The list of divisions.
	 */
	List<Division> getProjectDivisionsByName(String name);

	/**
	 * Get the list of division with associated project.
	 * @param divisionName The division name.
	 * @param companyId The company id.
	 * @param isExact True if name to be evaluated is the exact value, otherwise false
	 * @return The list of divisions.
	 */
	List<Division> getProjectDivisions(String divisionName, Integer companyId, boolean isExact);
}