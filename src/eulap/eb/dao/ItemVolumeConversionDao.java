package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ItemVolumeConversion;

/**
 * Interface that defines the item volume conversion data access object

 *
 */
public interface ItemVolumeConversionDao extends Dao<ItemVolumeConversion>{

	/**
	 * Get all Item Volume Conversion By search criteria.
	 * @param itemId The item Id.
	 * @param companyId THe company id.
	 * @param status The status
	 * @param pageNumber The page number.
	 * @return All filtered item Volume Conversion.
	 */
	Page<ItemVolumeConversion> getAllItemVolumeConversion(Integer itemId,
			Integer companyId, SearchStatus searchStatus,
			PageSetting pageSetting);

	/**
	 * Check if has duplicate item per company;
	 * @param volumeConversion The Item Volume Conversion.
	 * @return True if has Duplicate Item otherwise false.
	 */
	boolean hasDuplicateItem(ItemVolumeConversion volumeConversion);

	/**
	 * Get the item volume conversion per item id. 
	 * @param itemId The item id.
	 * @return The item volume conversion object.
	 */
	ItemVolumeConversion getVolumeConversionPerItem(Integer itemId);
}
