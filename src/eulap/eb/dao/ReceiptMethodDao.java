package eulap.eb.dao;


import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.domain.hibernate.User;

/**
 * Data access object of {@link ReceiptMethod}}

 */
public interface ReceiptMethodDao extends Dao<ReceiptMethod>{

	/**
	 * Check if receipt method is unique
	 * @param receiptMethod The receipt method that will be evaluated
	 * @return True if unique, otherwise false
	 */
	boolean isUniqueReceiptMethod (ReceiptMethod receiptMethod);

	/**
	 * Get receipt method object
	 * @param receiptId The receipt method id
	 * @return The receipt method object
	 */
	ReceiptMethod getReceiptMethodById (int receiptId);

	/**
	 * Search for receipt methods
	 * @param searchCriteria The search criteria
	 * @param pageSetting The page setting
	 * @return The paged result
	 */
	Page<ReceiptMethod> searchReceiptMethod (String searchCriteria, PageSetting pageSetting);

	/**
	 * Get the list of receipt methods based on the logged user.
	 * @param user The logged user.
	 * @return The list of receipt methods.
	 */
	List<ReceiptMethod> getReceiptMethods (User user);

	/**
	 * Get the list of receipt methods by division.
	 * @param divisionId The division id.
	 * @return The list of receipt methods.
	 */
	List<ReceiptMethod> getReceiptMethodsByDivision (Integer divisionId);

	/**
	 * Search the receipt method.
	 * @param name The receipt method name.
	 * @param companyId The id of the company.
	 * @param bankAccountId The id of bank account.
	 * @param status The status of the receipt method.
	 * @param pageNumber The page number.
	 * @return The page result.
	 */
	Page<ReceiptMethod> searchReceiptMethod(String name, Integer companyId, Integer bankAccountId, SearchStatus status, PageSetting pageSetting);

	/**
	 * Get the receipt methods by company.
	 * @param companyId The company id.
	 * @return The receipt methods.
	 */
	List<ReceiptMethod> getReceiptMethods (Integer companyId);

	/**
	 * Get the list of active receipt methods.
	 * If the provided receipt method id corresponds with an inactive receipt method, it will be included in the list.
	 * @param companyId The company id.
	 * @param receiptMethodId The receuot method id.
	 * @return The list of receipt methods.
	 */
	List<ReceiptMethod> getReceiptMethods (Integer companyId, Integer receiptMethodId);
}
