package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ItemDiscountType;

/**
 * Data access object for {@link ItemDiscountType}

 *
 */
public interface ItemDiscountTypeDao extends Dao<ItemDiscountType> {
	
	/**
	 * Get the item discount type by name.
	 * @param name The name of item discount type.
	 * @return The item discount type.
	 */
	ItemDiscountType getItemDiscountType (String name);
	
	/**
	 * Get the list of item discount types by name.
	 * @param name The name of item discount type.
	 * @return The list of item discount types.
	 */
	List<ItemDiscountType> getItemDiscountTypes (String name);
	
}
