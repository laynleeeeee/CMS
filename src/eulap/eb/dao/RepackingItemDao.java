package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.RepackingItem;

/**
 * Data Access Object {@link RepackingItem}

 */
public interface RepackingItemDao extends Dao<RepackingItem>{

	/**
	 * Get the list of repacking items.
	 * @param repackingId The unique id of the repacking object.
	 * @param itemId The unique id of the item.
	 * @return The list of repacking items.
	 */
	List<RepackingItem> getRepackingItems(int repackingId, Integer itemId);

	/**
	 * Get all the repacked items.
	 * @param ps The page setting.
	 * @return List of repacked items in paging format.
	 */
	Page<RepackingItem> getAllRepackingItem (PageSetting ps);
}
