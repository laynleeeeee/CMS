package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.RriBagQuantity;

/**
 * Data access object of {@code RriBagQuantity}

 *
 */
public interface RriBagQuantityDao extends Dao<RriBagQuantity>{

	/**
	 * Get the list of {@link RriBagQuantity}
	 * @param refObjectId The EB_OBJECT_ID of the parent.
	 * @return The list of {@link RriBagQuantity}.
	 */
	List<RriBagQuantity> getRriBagQuantitiesByRefObjectId(Integer refObjectId);

}
