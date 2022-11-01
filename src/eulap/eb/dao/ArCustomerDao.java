package eulap.eb.dao;

import java.util.Collection;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ArCustomer;

/**
 * Data Access Object of {@link ArCustomer}

 *
 */
public interface ArCustomerDao extends Dao<ArCustomer>{

	/**
	 * Evaluate the AR Customer name if unique.
	 * @param name The name to be evaluated.
	 * @return True if unique, false if duplicate.
	 */
	public boolean isUniqueName(String name);

	/**
	 * Search AR Customers.
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return Paged result.
	 */
	Page<ArCustomer> searchArCustomer(String searchCriteria, PageSetting pageSetting);

	/**
	 * Get the AR Customers under the same service lease key Id.
	 * @return Collection of AR Customers.
	 */
	Collection<ArCustomer> getArCustomers(int serviceLeaseKeyId);

	/**
	 * Get the list of AR Customers based on the name and service lease key.
	 * @param name The name of the ar customer.
	 * @param isExact True if it must be exact, otherwise false.
	 * @param user The logged user.
	 * @return The list of AR Customers.
	 */
	List<ArCustomer> getArCustomers(String name, Boolean isExact, int serviceLeaseKeyId);

	/**
	 * Search Ar Customer.
	 * @param name The name of the customer.
	 * @param address The address of the customer.
	 * @param status The customer status.
	 * @param pageSetting The page setting.
	 * @return The page result.
	 */
	Page<ArCustomer> searchArCustomer(Integer bussClassId, String name, String streetBrgy, String cityProvince, SearchStatus status,
			PageSetting pageSetting);

	/**
	 * Get the list of AR customers.
	 * @param companyId The company id of customer account.
	 * @return The list of customers.
	 */
	List<ArCustomer> getArCustomerList(Integer companyId);

	/**
	 * Get the list of AR customers based on the company id and name.
	 * @param companyId The selected company id.
	 * @param name The name of the customer.
	 * @param isExact True if it must be exact, otherwise false.
	 * @param divisionId The divsision id
	 * @return The list of AR customers.
	 */
	List<ArCustomer> getArCustomers(Integer companyId, String name, Boolean isExact, Integer divisionId);

	/**
	 * Get the list of AR Customers based on the name.
	 * @param name The name of the ar customer.
	 * @param activeOnly True to filter active only, False to get all.
	 * @param limit The maximum number of result per query.
	 * @return The list of AR Customers.
	 */
	List<ArCustomer> getArCustomers(String name, boolean activeOnly, Integer limit);

	/**
	 * Get the ar customer object by name.
	 * @param customerName The name filter.
	 * @return The customer object.
	 */
	ArCustomer getByName (String customerName);

	/**
	 * Check if the TIN is unique/
	 * @return True if tin is unique per customer.
	 */
	boolean isUniqueTin(ArCustomer arCustomer);

	/**
	 * Determine if customer is unique.
	 * @param arCustomer The {@link ArCustomer}.
	 * @return True if customer is unique, otherwise false
	 */
	public boolean isUniqueCustomer(ArCustomer arCustomer);
}
