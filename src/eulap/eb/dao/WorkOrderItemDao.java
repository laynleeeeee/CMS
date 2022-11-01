package eulap.eb.dao;

import java.util.List;

import org.hibernate.cache.ReadWriteCache.Item;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.domain.hibernate.WorkOrderItem;

/**
 * Data access object interface for {@link WorkOrderItem}

 */

public interface WorkOrderItemDao extends Dao<WorkOrderItem> {

	/**
	 * Get the list of {@link WorkOrderItem} by {@link WorkOrder} id.
	 * @param woId The {@link WorkOrder} id.
	 * @return The list of {@link WorkOrderItem}.
	 */
	List<WorkOrderItem> getWorkOrderItems(Integer woId);

	/**
	 * Get the remaining {@link WorkOrderItem} quantity.
	 * @param woId The {@link WorkOrder} id.
	 * @param itemId The {@link Item} id.
	 * @param rfId The {@link RequisitionForm} id to be excluded. Pass null if no {@link RequisitionForm} to be excluded.
	 * @return The remaining {@link WorkOrderItem} quantity.
	 */
	Double getRemainingQty(Integer woId, Integer itemId, Integer rfId);
}
