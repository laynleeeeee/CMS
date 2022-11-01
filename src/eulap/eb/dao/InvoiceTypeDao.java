package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.User;

/**
 * Invoice type data access objects.

 *
 */
public interface InvoiceTypeDao extends Dao<InvoiceType>{

	/**
	 * Get all the invoices based on the logged user.
	 * @param user The current logged user. 
	 * @return The invoice types.
	 */
	List<InvoiceType> getInvoiceTypes (User user);

	/**
	 * Get all the invoice types.
	 * @param user The logged user.
	 * @return The invoice types.
	 */
	List<InvoiceType> getAllInvoiceTypes(User user);
}
