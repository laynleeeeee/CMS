package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ItemAddOnTypeDao;
import eulap.eb.domain.hibernate.ItemAddOnType;

/**
 * Business logic for Item add on type requests

 *
 */
@Service
public class ItemAddOnTypeService {
	@Autowired
	private ItemAddOnTypeDao addOnTypeDao;

	/**
	 * Retrieves all add on types.
	 * @return The list of {@link ItemAddOnType}
	 */
	public List<ItemAddOnType> getAllTypes() {
		return addOnTypeDao.getAllAddOnTypes();
	}
}
