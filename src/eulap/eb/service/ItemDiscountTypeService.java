package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ItemDiscountTypeDao;
import eulap.eb.domain.hibernate.ItemDiscountType;

/**
 * Class that handles the business logic of {@link ItemDiscountType}

 *
 */
@Service
public class ItemDiscountTypeService {
	@Autowired
	private ItemDiscountTypeDao itemDiscountTypeDao;

	/**
	 * Get all item discount types.
	 * @return The list of item discount types.
	 */
	public List<ItemDiscountType> getItemDiscountTypes() {
		return (List<ItemDiscountType>) itemDiscountTypeDao.getAll();
	}

	/**
	 * Get the list of all item discount types.
	 * @param name The name of the item discount type.
	 * @return The list of item discount types.
	 */
	public List<ItemDiscountType> getItemDiscountTypes(String name) {
		return itemDiscountTypeDao.getItemDiscountTypes(name);
	}

	/**
	 * Get the item discount type by name.
	 * @param name The name of item discount type.
	 * @return The item discount type.
	 */
	public ItemDiscountType getItemDiscountType(String name) {
		if (name == null)
			return null;
		return itemDiscountTypeDao.getItemDiscountType(name);
	}
}
