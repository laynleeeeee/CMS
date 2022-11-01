package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.SerialItemSetup;

/**
 * Data Access Object of {@link SerialItemSetup}

 *
 */
public interface SerialItemSetupDao extends Dao<SerialItemSetup>{

	/**
	 * Get the serial item setup
	 * @param itemId The retail item id
	 * @return The serial item setup object
	 */
	SerialItemSetup getSerialItemSetupByItemId(Integer itemId);

	/**
	 * Get the serialized and non serialized items
	 * @param companyId The company id
	 * @param stockCode The stock code
	 * @param isSerialized True if the serialized, otherwise false
	 * @param isActive True if item is active, otherwise false
	 * @param divisionId The division id
	 * @return The list of items
	 */
	List<SerialItemSetup> getRetailItems(Integer companyId, Integer itemCategoryId, String stockCode,
			boolean isSerialized, boolean isActive, Integer divisionId);

	/**
	 * Get the serialized or non serialized item.
	 * @param stockCode The stock code.
	 * @param companyId The company id.
	 * @param warehouseId The warehouse id.
	 * @param isSerialized True if the serialized, otherwise false
	 * @param isActiveOnly True if item is active only, otherwise false
	 * @param divisionId The division id
	 * @return The serialized or non serialized item.
	 */
	SerialItemSetup getRetailItem(String stockCode, Integer companyId, Integer warehouseId,
			boolean isSerialized, boolean isActiveOnly, Integer divisionId);
}
