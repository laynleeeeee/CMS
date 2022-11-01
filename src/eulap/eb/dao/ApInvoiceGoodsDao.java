package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ApInvoiceGoods;

/**
 * Data access object interface for AP invoice goods

 */

public interface ApInvoiceGoodsDao extends Dao<ApInvoiceGoods> {

	/**
	 * Get the remaining quantity based on the reference form
	 * @param invoiceId The invoice id
	 * @param referenceObjId The reference object id
	 * @param itemId The item id
	 * @return The remaining quantity based on the reference form
	 */
	double getRrRemainingQty(Integer invoiceId, Integer referenceObjId, Integer itemId);

	/**
	 * Get the remaining quantity based on the reference form
	 * @param invoiceId The invoice id
	 * @param referenceObjId The reference object id
	 * @param itemId The item id
	 * @return The remaining quantity based on the reference form
	 */
	double getInvGsRemainingQty(Integer invoiceId, Integer referenceObjId, Integer itemId);
}
