package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;
import bp.web.ar.AuditUtil;

/**
 * The company service that handle the business layer of the company object.

 */
@Service
public class CompanyService {
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private EBObjectDao ebObjectDao;

	/**
	 * Get all the companies.
	 * @return The list of companies wrapped in a page.
	 */
	public Page<Company> getALLCompanies () {
		return companyDao.getCompanies();
	}

	/**
	 * Get the list of companies.
	 * @param searchCriteria The search criteria.
	 * @param activeOnly True to search for active companies, otherwise false.
	 * @return The paged date of companies.
	 */
	public Page<Company> getCompanies (String searchCriteria, String searchCategory, boolean activeOnly) {
		PageSetting pageSetting = new PageSetting(1);
		return getCompanies(searchCriteria, searchCategory, activeOnly, pageSetting);
	}
	
	/**
	 * Get the list of companies.
	 * @param searchCriteria The search criteria.
	 * @param searchCategory The search category
	 * @param activeOnly True to search for active companies, otherwise false.
	 * @param pageNumber The page number
	 * @return The paged date of companies.
	 */
	public Page<Company> getCompanies (String searchCriteria, String searchCategory, boolean activeOnly, int pageNumber) {
		PageSetting pageSetting = new PageSetting(pageNumber);
		return getCompanies(searchCriteria, searchCategory, activeOnly, pageSetting);
	}

	private Page<Company> getCompanies (String searchCriteria, String searchCategory, boolean activeOnly, PageSetting pageSetting) {
		// * means search all
		if (searchCriteria.equals("*"))
			searchCriteria = "";
		return companyDao.getCompanies(pageSetting,  searchCriteria, searchCategory,   activeOnly);
	}
	
	/**
	 * Get the list of companies.
	 * @param searchCriteria The search criteria.
	 * @return The paged date of companies.
	 */
	public Page<Company> getCompanies (String searchCriteria, String searchCategory) {
		PageSetting pageSetting = new PageSetting(1);
		return getCompanies(searchCriteria, searchCategory, pageSetting);
	}
	
	/**
	 * Get the list of companies.
	 * @param searchCriteria The search criteria.
	 * @param searchCategory The search category
	 * @param activeOnly True to search for active companies, otherwise false.
	 * @param pageNumber The page number
	 * @return The paged date of companies.
	 */
	
	public Page<Company> getCompanies (String searchCriteria, String searchCategory, int pageNumber) {
		PageSetting pageSetting = new PageSetting(pageNumber);
		return getCompanies(searchCriteria, searchCategory, pageSetting);
	}
	
	private Page<Company> getCompanies (String searchCriteria, String searchCategory, PageSetting pageSetting) {
		// * means search all
		if (searchCriteria.equals("*"))
			searchCriteria = "";
		return companyDao.getCompanies(pageSetting, searchCriteria, searchCategory);
	}
		
	/**
	 * Get the list of all companies.
	 * @return The list of companies.
	 */
	public List<Company> getAll () {
		return new ArrayList<Company>(companyDao.getAll());
	}

	/**
	 * Get the list of all companies exclude the list of user's companies.
	 * @param userCompanies The list of user companies.
	 * @return The list of filtered companies.
	 */
	public Collection<Company> getFilterCompanies (Collection<UserCompany> userCompanies) {
		List<Integer> userCompaniesId = new ArrayList <Integer> ();
		for (UserCompany userCompany : userCompanies)
			userCompaniesId.add(userCompany.getCompanyId());
		return companyDao.getCompanies(userCompaniesId);
	}
	
	/**
	 * Save the company.
	 * @param company The company to be saved.
	 * @param isNewRecord True to set new record, otherwise false.
	 * @param ui user information
	 */
	public void save (Company company, User user, boolean isNewRecord) {
		company.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		AuditUtil.addAudit(company, new Audit (user.getId(), isNewRecord, new Date ()));

		if(isNewRecord) {
			// Assign eb object to company
			EBObject ebObject = new EBObject();
			AuditUtil.addAudit(ebObject, new Audit(user.getId(), true, new Date()));
			ebObject.setObjectTypeId(Company.COMPANY_OBJECT_TYPE_ID);
			ebObjectDao.save(ebObject);
			company.setEbObjectId(ebObject.getId());
		}

		companyDao.saveOrUpdate(company);
	}

	public Company getCompany (int companyId) {
		return companyDao.get(companyId);
	}

	public boolean isUniqueCompanyName (String companyName) {
		return companyDao.isUniqueCompanyName(companyName);
	}
	
	public Page<Company> searchCompanies (String criteria, String field, String status, PageSetting pageSetting){
		LabelName2FieldNameCompany ln2FnC = LabelName2FieldNameCompany.getInstanceOf(field);
		SearchStatus ss = SearchStatus.getInstanceOf(status);
		return companyDao.searchCompanies(ln2FnC, ss, criteria, pageSetting);
	} 

	public int getCompanyIdByName(String name) {
		return companyDao.getCompanyIdbyName(name);
	}

	public String getCompanyName(int companyId) {
		return companyDao.getCompanyName(companyId);
	}

	public Page<Company> getAllCompanies(){
		return companyDao.getCompanies();
	}

	/**
	 * Check the company if unique.
	 * @param company The company object.
	 * @param companyField 1 = companyNumber, 2 = name, 3 = tin
	 * @param user The logged in user.
	 * @return True if unique, otherwise false.
	 */
	public boolean isUnique(Company company, int companyField, User user){
		if (company.getId() == 0)
			return companyDao.isUnique(company, companyField, user);
		Company oldCompany = companyDao.get(company.getId());
		if (companyField == 1){
			if(company.getCompanyNumber().trim().equalsIgnoreCase(oldCompany.getCompanyNumber().trim()))
				return true;
		}else if (companyField == 2) {
			if (company.getName().trim().equalsIgnoreCase(oldCompany.getName().trim()))
				return true;
		}else if (companyField == 3) {
			if (company.getTin() == null || company.getTin().trim().isEmpty()) {
				return true;
			} else if (company.getTin().trim().equals(oldCompany.getTin().trim())){
				if (company.getId() == oldCompany.getId())
					return true;
				return false;
			}
		}else if (companyField == 4) {
			if (oldCompany.getCompanyCode() == null || oldCompany.getCompanyCode().trim().isEmpty()) {
				return true;
			}else if (company.getCompanyCode().trim().equalsIgnoreCase(oldCompany.getCompanyCode().trim())){
				return true;
			}
		}
		return companyDao.isUnique(company, companyField, user);
	}

	/**
	 * Get the list of all companies.
	 * @param companyNumber The assigned number of the company.
	 * @param name The company name.
	 * @param tin the tax identification of the company.
	 * @param code The company code
	 * @param user The logged user.
	 * @param pageSetting The page setting.
	 * @return The list of the companies.
	 */
	private Page<Company> getCompanyList(String companyNumber, String name, String tin, String code, User user, PageSetting pageSetting){
		return companyDao.getCompanyList(companyNumber.trim(), name.trim(), tin.trim(), code.trim(), user, pageSetting);
	}

	public Page<Company> getCompanyList(String companyNumber, String name, String tin, String code, User user, int pageNumber){
		return getCompanyList(companyNumber, name, tin, code, user, new PageSetting(pageNumber));
	}

	/**
	 * Get the list of all companies without paging.
	 */
	public Page<Company> getCompanyList(String companyNumber, String name, String tin, String code, User user){
		return getCompanyList(companyNumber, name, tin, code, user, new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT));
	}

	/**
	 * Get the active companies.
	 * @param user The logged in user.
	 * @param companyName The name of the company as filter criteria.
	 * @param companyNames The names of companies to be excluded.
	 * @param filterCNames The names of companies that must be filtered.
	 * @return Collection of active companies.
	 */
	public Collection<Company> getActiveCompanies(User user, String companyName, String companyNames, String filterCNames){
		List<String> excludeCompany = null;
		List<String> limitToThisCompany = null;
		if (companyNames != null && !companyNames.trim().isEmpty()) {
			excludeCompany = new ArrayList<String>();
			String strCompanyNames[] = companyNames.split(";");
			for (String str : strCompanyNames) 
				if (!str.trim().isEmpty())
					excludeCompany.add(str);
		}
		if (filterCNames != null && !filterCNames.trim().isEmpty()) {
			limitToThisCompany = new ArrayList<String>();
			String strCompanyNames[] = filterCNames.split(";");
			for (String str : strCompanyNames)
				if (!str.trim().isEmpty())
					limitToThisCompany.add(str);
		}
		
		return companyDao.getActiveCompanies(user, companyName, excludeCompany, limitToThisCompany);
	}

	/**
	 * Get the list of companies base on the service lease key of the logged user.
	 * @param user The logged user.
	 * @return The list of companies under the user.
	 */
	public List<Company> getCompanies(User user) {
		return (List<Company>) getActiveCompanies(user, null, null, null);
	}

	/**
	 * Get the collection of companies and the selected company if inactive.
	 * @return The collection of companies filtered by the service lease key of the user.
	 */
	public Collection<Company> getCompaniesWithInactives (User user, Integer companyId) {
		List<Company> companies = getCompanies(user);
		if(companyId != 0) {
			Collection<Integer> companyIds = new ArrayList<Integer>();
			for (Company comp : companies) {
				companyIds.add(comp.getId());
			}
			if(!companyIds.contains(companyId))
				companies.add(companyDao.get(companyId));
		}
		return companies;
	}

	/**
	 * Get the company object using its number.
	 * @param companyNumber The company number.
	 * @param user The logged user.
	 * @return The company object.
	 */
	public Company getCompanyByNumber(String companyNumber, User user) {
		return companyDao.getCompanyByNumber(companyNumber, user.getServiceLeaseKeyId());
	}

	/**
	 * Add TIN word to the tin.
	 * @param tin the tin.
	 * @return The formatted tin.
	 */
	public String getTin(String tin) {
		return "TIN "+tin;
	}

	/**
	 * Get the list of active companies by similar name.
	 * @param companyName The filter
	 * @return The list of companies.
	 */
	public List<Company> getActiveCompaniesByName (String companyName, boolean isActiveOnly, Integer limit) {
		return companyDao.getActiveCompaniesByName(companyName, isActiveOnly, limit);
	}

	/**
	 * Get the company by its name.
	 * @param user The logged user.
	 * @param companyName The name of company.
	 * @return The company object filtered by the company list from {@link UserCompany}
	 */
	public Company getCompanyByName(User user, String companyName) {
		return companyDao.getCompanyByName(user, companyName);
	}
}