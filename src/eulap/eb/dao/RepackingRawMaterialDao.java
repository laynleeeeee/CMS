package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.RepackingRawMaterial;

/**
 * Data access object interface for {@link RepackingRawMaterial}

 */

public interface RepackingRawMaterialDao extends Dao<RepackingRawMaterial> {

	/**
	 * Get the repacking raw material item object
	 * @param repackingId The repacking item
	 * @param itemId The item id
	 * @return The list of repacking raw material items
	 */
	List<RepackingRawMaterial> getRawMaterialItems(int repackingId, int itemId);

}
