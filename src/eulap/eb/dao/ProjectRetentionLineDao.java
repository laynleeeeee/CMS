package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ProjectRetentionLine;
import eulap.eb.domain.hibernate.SalesOrder;

/**
 * Data access object for {@link ProjectRetentionLine}

 */

public interface ProjectRetentionLineDao extends Dao<ProjectRetentionLine> {
	/**
	 * Get the list of transaction with retention based on the sales order id.
	 * @param salesOrderId The {@link SalesOrder} id.
	 * @return The list of {@link ProjectRetentionLine}.
	 */
	List<ProjectRetentionLine> getTransactionsBySoId(Integer salesOrderId);

	/**
	 * 
	 * @param objectId
	 * @return
	 */
	double getRemainingRetBal(Integer objectId, Integer prlId, boolean isInvoice);
}
