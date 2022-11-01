package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Term;

/**
 * A base class that defines the access and operation in the TERM table.

 *
 */
public interface TermDao extends Dao<Term>{

	/**
	 * Get all terms based on the service lease key id.
	 * @param serviceLeaseKeyId The current lease key id.
	 * @return The list of terms
	 */
	List<Term> getAllTerms (int serviceLeaseKeyId);

	/**
	 * Get term by supplier account.
	 * @param supplierAccountId The supplier account id.
	 * @param serviceLeaseKeyId The current lease key id.
	 * @return The term.
	 */
	Term getTermBySupplierAccount (int supplierAccountId, int serviceLeaseKeyId);

	/**
	 * Get term by customer account.
	 * @param customerAccountId The customer account id.
	 * @param serviceLeaseKeyId The current lease key id.
	 * @return The term.
	 */
	Term getTermByCustomerAccount (int customerAccountId, int serviceLeaseKeyId);

	/**
	 * Check if the term is unique.
	 * @param term The term object.
	 * @return True if unique, otherwise false.
	 */
	boolean isUnique(Term term);

	/**
	 * Search/filter term.
	 * @param name The term name.
	 * @param days The day of the term.
	 * @param status 1=active and 0=inactive.
	 * @param pageSetting The page setting.
	 * @return The page result of filtered term.
	 */
	Page<Term> searchTerm(String name, Integer days, int status, PageSetting pageSetting);

	/**
	 * Get the term using its name.
	 * @param name The name of the term.
	 * @return The term.
	 */
	Term getTermByName(String name);

	/**
	 * Get the list of all Terms.
	 * @return The list of {@link Term}
	 */
	List<Term> getAllTerms();

	/**
	 *
	 * Get the list of terms for forms
	 * @param termId the term id
	 * @return List of terms
	 */
	List<Term> getTerms(Integer termId);
}
