package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ArReceiptTypeDao;
import eulap.eb.domain.hibernate.ArReceiptType;

/**
 * Class that handles all the business logic of {@link ArReceiptType}

 *
 */
@Service
public class ArReceiptTypeService {
	@Autowired
	private ArReceiptTypeDao arReceiptTypeDao;
	
	/**
	 * Get all the ar receipt types.
	 * @return List of ar receipt type.
	 */
	public List<ArReceiptType> getArReceiptTypes () {
		return (List<ArReceiptType>) arReceiptTypeDao.getAll();
	}
}
