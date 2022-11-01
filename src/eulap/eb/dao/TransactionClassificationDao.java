package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.TransactionClassification;
import eulap.eb.domain.hibernate.User;

/**
 * Invoice type data access objects.

 *
 */
public interface TransactionClassificationDao extends Dao<TransactionClassification>{

	/**
	 * Get all the invoice classifications.
	 * @param user The logged user.
	 * @return The invoice classifications.
	 */
	List<TransactionClassification> getAllTransactionClassifications(User user);
}
