package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.domain.hibernate.WorkOrderInstruction;

/**
 * Data access object interface for {@link WorkOrderInstruction}

 */

public interface WorkOrderInstructionDao extends Dao<WorkOrderInstruction> {

	/**
	 * Get the list of {@link WorkOrderInstruction} by {@link WorkOrder} id.
	 * @param woId The {@link WorkOrder} id.
	 * @return The List of {@link WorkOrderInstruction}.
	 */
	List<WorkOrderInstruction> getWoInstructions(Integer woId);
}
