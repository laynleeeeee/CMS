package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.PurchaseOrderLine;

/**
 * Data access object interface for {@link PurchaseOrderLine}

 */

public interface PurchaseOrderLineDao extends Dao<PurchaseOrderLine> {

	/**
	 * Get the {@link PurchaseOrderLine} by the child object id.
	 * @param objectId The child object id.
	 * @return The {@link PurchaseOrderLine}
	 */
	PurchaseOrderLine getByApInvoiceLineObjectId(Integer objectId);

	/**
	 * Get the list of {@link PurchaseOrderLine}
	 * @param poId The purchase order id
	 * @return List of {@link PurchaseOrderLine}
	 */
	List<PurchaseOrderLine> getPOLines(int poId);
}
