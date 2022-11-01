package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ItemAddOnType;

/**
 * Data access object for {@link ItemAddOnType}

 *
 */
public interface ItemAddOnTypeDao extends Dao<ItemAddOnType> {

	/**
	 * Get the list of item add on types.
	 * @return The list of item add on types.
	 */
	List<ItemAddOnType> getAllAddOnTypes ();
}
