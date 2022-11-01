package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CsiRawMaterial;

/**
 * DAO Layer of {@link CsiRawMaterial}

 *
 */
public interface CsiRawMaterialDao extends Dao<CsiRawMaterial>{

	/**
	 * Get the list of Raw Materials of the finished product.
	 * @param cashSaleId The id of the cash sales.
	 * @param finishedProductId The id of the finished product.
	 * @return The list of Raw Materials of the finished product.
	 */
	List<CsiRawMaterial> getRawMaterials(int cashSaleId, int finishedProductId);

	/**
	 * Retrieve list of raw material items
	 * @param cashSaleId The cash sale object id
	 * @param itemId The item id
	 * @return The list of raw material items
	 */
	List<CsiRawMaterial> getCsRawMaterialItems(int cashSaleId, int itemId);
}
