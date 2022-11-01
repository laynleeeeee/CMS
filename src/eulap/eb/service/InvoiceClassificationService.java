package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.InvoiceClassificationDao;
import eulap.eb.domain.hibernate.InvoiceClassification;
import eulap.eb.domain.hibernate.User;

/**
 * Handles the business logic in retrieving invoice classification. 

 *
 */
@Service
public class InvoiceClassificationService {
	@Autowired
	private InvoiceClassificationDao invoiceClassificationDao;
	

	/**
	 * Get all the invoice classifications.
	 * @param user The logged user.
	 * @return The list of all invoices classifications;
	 */
	public List<InvoiceClassification> getAllInvoiceClassifications(User user) {		
		return invoiceClassificationDao.getAllInvoiceClassifications(user);
	}
}
