package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.WorkOrderInstructionDao;
import eulap.eb.domain.hibernate.WorkOrderInstruction;

/**
 * DAO implementation class for {@link WorkOrderInstructionDao}

 */

public class WorkOrderInstructionDaoImpl extends BaseDao<WorkOrderInstruction> implements WorkOrderInstructionDao {

	@Override
	protected Class<WorkOrderInstruction> getDomainClass() {
		return WorkOrderInstruction.class;
	}

	@Override
	public List<WorkOrderInstruction> getWoInstructions(Integer woId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(WorkOrderInstruction.FIELD.workOrderId.name(), woId));
		return getAll(dc);
	}

}
