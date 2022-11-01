package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ArTransactionTypeDao;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.User;

/**
 * Class that handles the business logic for AR Transaction type.

 */
@Service
public class ArTransactionTypeService {
	@Autowired
	private ArTransactionTypeDao arTransactionTypeDao;

	/**
	 * Get all the transaction types.
	 * @param user The current logged in user.
	 * @return The list of transaction types.
	 */
	public List<ArTransactionType> getTransactionTypes (User user){
		return arTransactionTypeDao.getTransactionTypes(user);
	}

	/**
	 * Get all the transaction types.
	 * @param user The current logged in user.
	 * @return The list of all transaction types.
	 */
	public List<ArTransactionType> getAllTransactionTypes (User user){
		return arTransactionTypeDao.getAllTransactionTypes(user);
	}
}
