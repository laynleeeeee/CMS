package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.WorkOrderItemDao;
import eulap.eb.domain.hibernate.WorkOrderItem;

/**
 * DAO implementation class for {@link WorkOrderItemDao}

 */

public class WorkOrderItemDaoImpl extends BaseDao<WorkOrderItem> implements WorkOrderItemDao {

	@Override
	protected Class<WorkOrderItem> getDomainClass() {
		return WorkOrderItem.class;
	}

	@Override
	public List<WorkOrderItem> getWorkOrderItems(Integer woId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(WorkOrderItem.FIELD.workOrderId.name(), woId));
		return getAll(dc);
	}

	@Override
	public Double getRemainingQty(Integer woId, Integer itemId, Integer rfId) {
		StringBuilder sql = new StringBuilder("SELECT WO.WORK_ORDER_ID, WOI.QUANTITY - "
			+ "(SELECT COALESCE(SUM(RFI.QUANTITY),0) "
			+ "FROM REQUISITION_FORM RF "
			+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = RF.EB_OBJECT_ID "
			+ "INNER JOIN REQUISITION_FORM_ITEM RFI ON RFI.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = RF.FORM_WORKFLOW_ID "
			+ "WHERE FW.CURRENT_STATUS_ID != 4 "
			+ "AND RF.REQUISITION_TYPE_ID = 6 "
			+ "AND RF.WORK_ORDER_ID = ?  "
			+ "AND RFI.ITEM_ID = ? ");
		if (rfId != null) {
			sql.append("AND RF.REQUISITION_FORM_ID != ? ");
		}
		sql.append(" ) AS QTY FROM WORK_ORDER WO "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WO.FORM_WORKFLOW_ID "
			+ "INNER JOIN WORK_ORDER_ITEM WOI ON WOI.WORK_ORDER_ID = WO.WORK_ORDER_ID "
			+ "WHERE FW.CURRENT_STATUS_ID != 4 "
			+ "AND FW.CURRENT_STATUS_ID != 1 "
			+ "AND WO.WORK_ORDER_ID = ? "
			+ "AND WOI.ITEM_ID = ? ");
		Session session = null;
		Double quantity = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql.toString());
			int index = 0;
			query.setParameter(index, woId);
			query.setParameter(++index, itemId);
			if(rfId != null) {
				query.setParameter(++index, rfId);
			}
			query.setParameter(++index, woId);
			query.setParameter(++index, itemId);
			List<Object[]> list = query.list();
			if(list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					quantity = (Double) row[1];
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return quantity;
	}
}
