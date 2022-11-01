package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.InvoiceTypeDao;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.User;

/**
 * Handles the business logic in retrieving invoice type. 

 *
 */
@Service
public class InvoiceTypeService {
	@Autowired
	private InvoiceTypeDao typeDao;
	
	/**
	 * Get all the invoices associated to the logged in user. 
	 * @param user The current logged in user. 
	 * @return The list of invoices type.
	 */
	public List<InvoiceType> getInvoiceTypes (User user) {
		return typeDao.getInvoiceTypes(user);
	}

	/**
	 * Get all the invoice types.
	 * @param user The logged user.
	 * @return The list of all invoices types;
	 */
	public List<InvoiceType> getAllInvoiceTypes(User user) {
		return typeDao.getAllInvoiceTypes(user);
	}
}
