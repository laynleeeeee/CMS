package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.InvoiceClassification;
import eulap.eb.domain.hibernate.User;

/**
 * Invoice type data access objects.

 *
 */
public interface InvoiceClassificationDao extends Dao<InvoiceClassification>{

	/**
	 * Get all the invoice classifications.
	 * @param user The logged user.
	 * @return The invoice classifications.
	 */
	List<InvoiceClassification> getAllInvoiceClassifications(User user);
}
