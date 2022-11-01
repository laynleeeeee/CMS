package eulap.eb.dao;

import java.util.Collection;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.LabelName2FieldNameCompany;

/**
 * Handles the company data access.  

 *
 */
public interface CompanyDao extends Dao<Company> {
	/**
	 * Get all companies.
	 * @return the paged companies.
	 */
	Page<Company> getCompanies ();
	/**
	 * Get the list of company.
	 * @param pageSetting the page settings
	 * @param companySearchText The search text entered by user to search for a company
	 * @param searchCategory The search category
	 * @param activeOnly True if search active company only, otherwise false.
	 * @return The Page<Company> that holds the company list.
	 */
	Page<Company> getCompanies (PageSetting pageSetting, String companySearchText, String searchCategory, boolean activeOnly);
	
	boolean isUniqueCompanyName (String companyName);

	/**
	 * Get the list of company.
	 * @param pageSetting the page settings
	 * @param companySearchText The search text entered by user to search for a company
	 * @param searchCategory The search category
	 * @return The Page<Company> that holds the company list.
	 */
	Page<Company> getCompanies (PageSetting pageSetting, String companySearchText, String searchCategory);
	
	public Collection<Company> getCompanies (Collection<Integer> toBeExcluded);
	
	/**
	 * Search for companies. 
	 * @param ln2Fn label name
	 * @param ss the search status
	 * @param searchCriteria search criteria
	 * @param pageSetting the page setting
	 * @return the list of companies.
	 */
	Page<Company> searchCompanies (LabelName2FieldNameCompany ln2Fn, SearchStatus ss, String searchCriteria, PageSetting pageSetting);

	/**
	 * Get the company id of a company by its name
	 * @param name
	 * @return company id
	 */
	int getCompanyIdbyName (String name);
	
	/**
	 * Get the company name of a company by its id
	 * @param companyId
	 * @return name
	 */
	String getCompanyName (int companyId); 

	/**
	 * Check the company if unique.
	 * @param company The company object.
	 * @param companyField 1 = companyNumber, 2 = name, 3 = tin
	 * @param user The logged user.
	 * @return True if unique, otherwise false.
	 */
	boolean isUnique(Company company, int companyField, User user);

	/**
	 * Get the list of companies.
	 * @param companyNumber The company number.
	 * @param name The name of the company.
	 * @param tin The Tax Identification of the company.
	 * @param user The logged user.
	 * @param pageSetting The page setting.
	 * @return The list of companies.
	 */
	Page<Company> getCompanyList(String companyNumber, String name, String tin, User user, PageSetting pageSetting);

	/**
	 * Get all the active companies.
	 * @param user The logged in user.
	 * @param companyName The name of the company as filter criteria.
	 * @param companyNames The names of companies to be excluded.
	 * @param filterCNames The names of companies that must be filtered.
	 * @return Collection of active companies.
	 */
	Collection<Company> getActiveCompanies(User user, String companyName, 
			List<String> companyNames, List<String> filterCNames);
	/**
	 * Get the list of companies
	 * @param serviceLeaseKey The service lease key. 
	 * @return The list of companies.
	 */
	List<Company> getCompanies (int serviceLeaseKey);

	/**
	 * Get the company object using its number.
	 * @param companyNumber The company number.
	 * @param serviceLeaseKeyId The service lease Id of the logged user.
	 * @return The company object.
	 */
	Company getCompanyByNumber (String companyNumber, int serviceLeaseKeyId);

	/**
	 * Get the company by its name.
	 * @param companyName The name of company.
	 * @return The company object.
	 */
	Company getCompanyByName (String companyName);

	/**
	 * Get the company by its name.
	 * @param user The logged user.
	 * @param companyName The name of company.
	 * @return The company object filtered by the company list from {@link UserCompany}
	 */
	Company getCompanyByName (User user, String companyName);

	/**
	 * Get the list of companies.
	 * @param companyNumber The company number.
	 * @param name The name of the company.
	 * @param tin The Tax Identification of the company.
	 * @param code The company code.
	 * @param user The logged user.
	 * @param pageSetting The page setting.
	 * @return The list of companies.
	 */
	Page<Company> getCompanyList(String companyNumber, String name, String tin, String code, User user, PageSetting pageSetting);

	List<Company> getActiveCompaniesByName (String companyName, boolean isActiveOnly, Integer limit);

	/**
	 * Generate the number of the company.
	 * @return The next available number for the company.
	 */
	int generateCompanyNumber();

	/**
	 * Get the Company Object by EbObjectID.
	 * @param ebObjectId The fromObjectId
	 * @return The Company.
	 */
	Company getCompanyByEbObjectId(Integer ebObjectId);
}