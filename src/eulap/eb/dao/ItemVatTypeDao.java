package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ItemVatType;

/**
 * Data access object of {@link ItemVatType}

 *
 */
public interface ItemVatTypeDao extends Dao<ItemVatType> {

	/**
	 * Get the list of item vat types.
	 * @param The item vat types id.
	 * @return The list of item vat types.
	 */
	List<ItemVatType> getItemVatTypes(Integer itemVatTypeId);

	/**
	 * Get the item VAT type object by name
	 * @param name The item VAT type name
	 * @return The item VAT type object
	 */
	ItemVatType getItemVatTypeByName(String name);
}
