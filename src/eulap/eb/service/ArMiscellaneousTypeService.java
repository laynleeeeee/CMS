package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ArMiscellaneousTypeDao;
import eulap.eb.domain.hibernate.ArMiscellaneousType;

/**
 * Class that handles all the business logic of {@link ArMiscellaneousType}

 *
 */
@Service
public class ArMiscellaneousTypeService {
	@Autowired
	private ArMiscellaneousTypeDao arMiscellaneousTypeDao;
	
	/**
	 * Get all the ar miscellaneous types.
	 * @return List of ar miscellaneous type.
	 */
	public List<ArMiscellaneousType> getArMiscellaneousTypes () {
		return (List<ArMiscellaneousType>) arMiscellaneousTypeDao.getAll();
	}

}
