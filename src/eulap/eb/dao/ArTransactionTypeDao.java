package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.User;

/**
 * DAO layer of {@link ArTransactionType}.

 */
public interface ArTransactionTypeDao extends Dao<ArTransactionType>{

	/**
	 * Get all transaction types of the logged user.
	 * @param user The logged user.
	 * @return The transaction types.
	 */
	List<ArTransactionType> getTransactionTypes (User user);

	/**
	 * Get all transaction types.
	 * @param user The logged user.
	 * @return The transaction types.
	 */
	List<ArTransactionType> getAllTransactionTypes(User user);
}
