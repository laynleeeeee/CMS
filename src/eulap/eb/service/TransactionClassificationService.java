package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.TransactionClassificationDao;
import eulap.eb.domain.hibernate.TransactionClassification;
import eulap.eb.domain.hibernate.User;

/**
 * Handles the business logic in retrieving transaction classification. 

 *
 */
@Service
public class TransactionClassificationService {
	@Autowired
	private TransactionClassificationDao transactionClassificationDao;

	/**
	 * Get all the invoice classifications.
	 * @param user The logged user.
	 * @return The list of all invoices classifications;
	 */
	public List<TransactionClassification> getAllTransactionClassifications(User user) {		
		return transactionClassificationDao.getAllTransactionClassifications(user);
	}
}
