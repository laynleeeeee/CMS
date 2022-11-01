package eulap.eb.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ItemCategoryDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.domain.hibernate.ItemCategory;

/**
 * Class that will handle business logic for Physical Inventory report generation.

 */
@Service
public class PhysicalInventoryService {
	@Autowired
	private ItemCategoryDao icDao;
	@Autowired
	private ItemDao itemDao;

	/**
	 * Get the list of item categories.
	 * @return The list of categories.
	 */
	public Collection<ItemCategory> getActiveCategories(){
		return icDao.getActiveItemCategories();
	}

	/**
	 * Get the item category entity.
	 * @param itemCategoryId The item category id.
	 * @return The item category entity.
	 */
	public ItemCategory getCategory(int itemCategoryId){
		return icDao.get(itemCategoryId);
	}
}
