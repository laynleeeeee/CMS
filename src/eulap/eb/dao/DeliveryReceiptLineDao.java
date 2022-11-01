package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.DeliveryReceiptLine;

/**
 * Data access object interface for {@link DeliveryReceiptLine}

 */

public interface DeliveryReceiptLineDao extends Dao<DeliveryReceiptLine> {
	/**
	 * Get the remaining quantity of {@link DeliveryReceiptLine}.
	 * @param referenceObjectId The reference object id.
	 * @return The remaining quantity.
	 */
	Double getRemainingQty(Integer drId, Integer referenceObjectId);
}