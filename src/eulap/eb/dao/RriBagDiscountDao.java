package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.RriBagDiscount;

/**
 * Data Access Object of {@link RriBagDiscount}.

 *
 */
public interface RriBagDiscountDao extends Dao<RriBagDiscount>{

	/**
	 * Get the list of {@link RriBagDiscount}
	 * @param refObjectId The EbObjectId of the parent.
	 * @return The list of {@link RriBagDiscount}.
	 */
	List<RriBagDiscount> getRriDiscountsByRefObjectId(Integer refObjectId);
}
