package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.SOType;

/**
 * SO type data access objects.

 *
 */
public interface SOTypeDao extends Dao<SOType>{

	/**
	 * Get all SO types.
	 * @return All SO types
	 */
	List<SOType> getActiveSOTypes(Integer soTypeId);
}
