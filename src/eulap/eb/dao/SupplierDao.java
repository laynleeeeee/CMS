package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.BusinessClassification;
import eulap.eb.domain.hibernate.BusinessRegistrationType;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.User;

/**
 * A base class that defines the database transactions of Supplier

 *
 */
public interface SupplierDao extends Dao<Supplier>{
	/**
	 * Check if the supplier is unique.
	 * @param supplier The supplier that will be evaluated.
	 * @return True if unique otherwise, false.
	 */
	boolean isUniqueSupplier (Supplier supplier);

	/**
	 * Get active suppliers under a service lease key.
	 * @param serviceLeaseKeyId The service lease key.
	 * @return The list of suppliers
	 */
	List<Supplier> getSuppliers (int serviceLeaseKeyId);

	/**
	 * Search for suppliers.
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return The page result.
	 */
	Page<Supplier> searchSupplier(String searchCriteria, PageSetting pageSetting);

	/**
	 * Get all suppliers under the service lease key.
	 * @param serviceLeaseKeyId The service lease key.
	 * @return List of all suppliers (both active and inactive).
	 */
	List<Supplier> getAllSuppliers (int serviceLeaseKeyId);

	/**
	 * Retrieve suppliers filtered by company and service lease key.
	 * @param companyId The Id of the company.
	 * @param serviceLeaseKeyId The service lease key of the logged user.
	 * @return List of suppliers (both active and inactive).
	 */
	List<Supplier> getSuppliersByCompany(int companyId, int serviceLeaseKeyId);

	/**
	 * Loads all the suppliers based on the service lease.
	 * @param serviceLeaseKeyId The service lease of the logged user.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<Supplier> loadSuppliers(int serviceLeaseKeyId, PageSetting pageSetting);

	/**
	 * Get the list of {@link Supplier} based on the parameter.
	 * @param bussinessClassificationId The {@link BusinessClassification} id.
	 * @param name The supplier name.
	 * @param streetBrgy The supplier street brangay.
	 * @param cityProvince The supploer city province.
	 * @param status The supplier status.
	 * @param busRegTypeId The {@link BusinessRegistrationType} id.
	 * @param serviceLeaseKeyId The service lease key id.
	 * @param pageSetting The {@link PageSetting}.
	 * @return The list of {@link Supplier} in page format.
	 */
	Page<Supplier> searchSuppliers(Integer bussinessClassificationId, String name, String streetBrgy, String cityProvince, SearchStatus status,
			int busRegTypeId, int serviceLeaseKeyId, PageSetting pageSetting);

	/**
	 * Validate if TIN is already existing.
	 * @param tin The Tin to be validated.
	 * @return True of unique, otherwise false.
	 */
	boolean isUniqueTin(String tin, int serviceLeaseKeyId);

	/**
	 * Get the list of suppliers by name.
	 * @param name The name of supplier.
	 * @return The list of suppliers.
	 */
	List<Supplier> getSuppliers(User user, int serviceLeaseKeyId, String name);

	/**
	 * Get the supplier by name.
	 * @param name The name of supplier.
	 * @return The supplier
	 */
	Supplier getSupplier(String name);

	/**
	 * Get list of suppliers by business reg type id.
	 * @param busRegTypeId The business registration type id.
	 * @return The list of suppliers.
	 */
	List<Supplier> getSuppliersByBusReg (User user, String name, Integer busRegTypeId);

	/**
	 * Get list of suppliers by business reg type id.
	 * @param busRegTypeId The business registration type id.
	 * @return The list of suppliers.
	 */
	Supplier getSupplierByBusRegType (String name, Integer busRegTypeId);

	/**
	 * Retrieve the list of Suppliers based on the company id and name
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param name The name of the supplier
	 * @param isExact True if it must be exact, otherwise false.
	 * @return The list of Suppliers.
	 */
	List<Supplier> getSuppliers(Integer companyId, Integer divisionId, String name, Boolean isExact, User user, Integer limit, Integer accountId);

	/**
	 * Get the list of company by supplier
	 * @param supplierId the supplier id
	 * @param companyName the name of the company
	 * @param user The current user logged.
	 * @return the list of companies
	 */
	List<Company> getCompaniesBySupplier(Integer supplierId, String companyName, User user);

	/**
	 * Validate if TIN is already existing.
	 * @param supplier supplier tin to be validated.
	 * @return True of unique, otherwise false.
	 */
	boolean isUniqueTin(Supplier supplier);
}
