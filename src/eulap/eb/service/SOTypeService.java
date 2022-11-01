package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.SOTypeDao;
import eulap.eb.domain.hibernate.SOType;

/**
 * Service class that will handle business logic for {@link SOType}

 */

@Service
public class SOTypeService {
	@Autowired
	private SOTypeDao soTypeDao;

	/**
	 * Get the {@link SOType} object by id.
	 * @param soTypeId The currency id.
	 * @return The {@link SOType} object.
	 */
	public SOType getSOType(Integer soTypeId) {
		return soTypeDao.get(soTypeId);
	}

	/**
	 * Get the list of active so types. 
	 * Add the inactive so type based on the soTypeId parameter.
	 * @param soTypeId The so type id.
	 * @return The list of active {@link SOType}.
	 */
	public List<SOType> getActiveSOTypes(Integer soTypeId) {
		return soTypeDao.getActiveSOTypes(soTypeId);
	}
}
