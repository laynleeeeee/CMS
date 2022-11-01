package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.BusinessRegistrationType;

public interface BusRegTypeDao extends Dao<BusinessRegistrationType>{

	/**
	 * Get the list of Business Registration Type.
	 * @return The list of Business Registration Type.
	 */
	List<BusinessRegistrationType> getBusRegType();

	/**
	 * Get busRegType object by id.
	 * @param busRegTypeId The busRegType id.
	 * @return The busRegType object.
	 */
	BusinessRegistrationType getBusRegType(Integer busRegTypeId);
}
