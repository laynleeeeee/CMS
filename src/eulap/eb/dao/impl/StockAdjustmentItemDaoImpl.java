package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.StockAdjustmentItemDao;
import eulap.eb.domain.hibernate.StockAdjustmentItem;

/**
 * DAO Implementation of {@link StockAdjustmentItemDao}

 */
public class StockAdjustmentItemDaoImpl extends BaseDao<StockAdjustmentItem> implements StockAdjustmentItemDao{

	@Override
	protected Class<StockAdjustmentItem> getDomainClass() {
		return StockAdjustmentItem.class;
	}

	@Override
	public List<StockAdjustmentItem> getSAItems(int stockAdjustmentId, Integer itemId) {
		DetachedCriteria saItemsCriteria = getDetachedCriteria();
		saItemsCriteria.add(Restrictions.eq(StockAdjustmentItem.FIELD.stockAdjustmentId.name(), stockAdjustmentId));
		if(itemId != null) {
			saItemsCriteria.add(Restrictions.eq(StockAdjustmentItem.FIELD.itemId.name(), itemId));
		}
		return getAll(saItemsCriteria);
	}
	
	@Override
	public List<StockAdjustmentItem> getSAItems(int stockAdjustmentId) {
		return getSAItems(stockAdjustmentId, null);
	}

	@Override
	public Page<StockAdjustmentItem> getStockAdjustmentRegisterData(Integer companyId, Integer warehouseId,
			Integer adjustmentTypeId, Date dateFrom, Date dateTo, PageSetting pageSetting) {
		DetachedCriteria saItemCriteria = getDetachedCriteria();
		saItemCriteria.createAlias("stockAdjustment", "sa");
		if(companyId != null) {
			saItemCriteria.add(Restrictions.eq("sa.companyId", companyId));
		}
		if(warehouseId != null) {
			if(warehouseId != -1)
				saItemCriteria.add(Restrictions.eq("sa.warehouseId", warehouseId));
		}
		if(adjustmentTypeId != null) {
			if(adjustmentTypeId != -1) {
				saItemCriteria.add(Restrictions.eq("sa.stockAdjustmentTypeId", adjustmentTypeId));
			}
		}
		if(dateFrom != null && dateTo != null) {
			saItemCriteria.add(Restrictions.between("sa.saDate", dateFrom, dateTo));
		}

		//Exclude inactive adjustment types.
		saItemCriteria.createAlias("sa.adjustmentType", "at");
		saItemCriteria.add(Restrictions.eq("at.active", true));

		//Exclude inactive warehouses.
		saItemCriteria.createAlias("sa.warehouse", "w");
		saItemCriteria.add(Restrictions.eq("w.active", true));

		saItemCriteria.createAlias("sa.formWorkflow", "fw");
		saItemCriteria.add(Restrictions.eq("fw.complete", true));
		saItemCriteria.addOrder(Order.asc("sa.saDate"));
		saItemCriteria.addOrder(Order.asc("sa.saNumber"));
		saItemCriteria.createAlias("item", "i");
		saItemCriteria.addOrder(Order.asc("i.stockCode"));
		saItemCriteria.addOrder(Order.asc("id"));
		return getAll(saItemCriteria, pageSetting);
	}

	@Override
	public double getItemAverageCost(int stockAdjustmentId, int itemId) {
		Double averageCost = 0.0;
		String sql = "SELECT STOCK_ADJUSTMENT_ID, ABS(SUM(COALESCE(UNIT_COST, 0) * COALESCE(QUANTITY, 0)) / SUM(COALESCE(QUANTITY, 0))) AS AVERAGE_COST "
				+ "FROM STOCK_ADJUSTMENT_ITEM WHERE STOCK_ADJUSTMENT_ID = ? AND ITEM_ID = ? ";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, stockAdjustmentId);
			query.setParameter(1, itemId);

			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					averageCost = (Double) row[1];
					break; // expecting one (1) row only
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return averageCost != null ? NumberFormatUtil.roundOffNumber(averageCost, 6) : 0;
	}
}
