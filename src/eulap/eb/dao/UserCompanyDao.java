package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.UserCompany;

/**
 * Handles the user company data access.  

 */
public interface UserCompanyDao extends Dao<UserCompany> {

	/**
	 * Get the User Company by id.
	 * @param userId The user id.
	 * @return The list of user company
	 */
	List<UserCompany> getUserCompanies (int userId);

	/**
	 * Get all the user companies.
	 * @param userName The user name.
	 * @param companyName The company name
	 * @param status True if active, otherwise false.
	 * @param pageNumber The page number.
	 * @return All the user companies by paged format.
	 */
	Page<UserCompany> getAllUserCompanies(String userName,String companyName, SearchStatus searchStatus, PageSetting pageNumber);

	/**
	 * Get the list of user companies by user and company.
	 * @param userId The user filter.
	 * @param companyName The company filter.
	 * @return The list of user companies.
	 */
	List<UserCompany> getUCByUserAndCompany (int userId, String companyName);
}
