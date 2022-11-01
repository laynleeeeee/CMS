package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.CustomerType;

/**
 * Data Access Object of {@link CustomerType}

 *
 */

public interface CustomerTypeDao extends Dao<CustomerType>{

	/**
	 * Search customer type.
	 * @param name The name of the customer type
	 * @param description The description of the customer type
	 * @param status The status of the customer type
	 * @param pageSetting The page setting
	 * @return The page result.
	 */
	Page<CustomerType> searchCustomerTypes(String name, String description,
			SearchStatus status, PageSetting pageSetting);

	/**
	 * Check if the customer type has duplicate entry
	 * @param customerType The customer type
	 * @return True if the customer type has duplicated entry, otherwise false
	 */
	boolean isUniqueCustomerType(CustomerType customerType);

	/**
	 * Get the list of customer types
	 * @param customerTypeId The customer type id
	 * @return The list of customer types
	 */
	List<CustomerType> getCustomerTypesWithInactive(Integer customerTypeId, Integer customerId);
}
