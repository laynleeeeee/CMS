package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.HibernateCallback;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.StockAdjustmentDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.StockAdjustmentItem;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * DAO Implementation of {@link StockAdjustmentDao}

 */
public class StockAdjustmentDaoImpl extends BaseDao<StockAdjustment> implements StockAdjustmentDao{

	@Override
	protected Class<StockAdjustment> getDomainClass() {
		return StockAdjustment.class;
	}

	@Override
	public Page<StockAdjustment> searchStockAdjustments(String criteria,
			int typeId, PageSetting pageSetting) {
		DetachedCriteria saCriteria = getDetachedCriteria();
		searchSANumber(saCriteria, criteria);
		saCriteria.add(Restrictions.eq(StockAdjustment.FIELD.stockAdjustmentClassificationId.name(), typeId));
		saCriteria.addOrder(Order.asc(StockAdjustment.FIELD.saNumber.name()));
		return getAll(saCriteria, pageSetting);
	}

	private DetachedCriteria filterStockAdjustments(int typeId) {
		DetachedCriteria saItemDc = DetachedCriteria.forClass(StockAdjustmentItem.class);
		saItemDc.setProjection(Projections.property(StockAdjustmentItem.FIELD.stockAdjustmentId.name()));
		if(typeId == StockAdjustment.STOCK_ADJUSTMENT_IN
				|| typeId == StockAdjustment.STOCK_ADJUSTMENT_IS_IN) {
			//Stock IN
			saItemDc.add(Restrictions.gt(StockAdjustmentItem.FIELD.quantity.name(), 0.0));
		} else if(typeId == StockAdjustment.STOCK_ADJUSTMENT_OUT
				|| typeId == StockAdjustment.STOCK_ADJUSTMENT_IS_OUT) {
			//Stock OUT
			saItemDc.add(Restrictions.lt(StockAdjustmentItem.FIELD.quantity.name(), 0.0));
		}

		// TODO: implement filtering by for form type id
		if(typeId == StockAdjustment.STOCK_ADJUSTMENT_IS_IN
				|| typeId == StockAdjustment.STOCK_ADJUSTMENT_IS_OUT) {
			saItemDc.add(Restrictions.isNotNull(StockAdjustmentItem.FIELD.ebObjectId.name()));
		} else {
			saItemDc.add(Restrictions.isNull(StockAdjustmentItem.FIELD.ebObjectId.name()));
		}

		return saItemDc;
	}

	@Override
	public Page<StockAdjustment> getAllSAsByStatus(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final Integer typeId, final PageSetting pageSetting) {

		HibernateCallback<Page<StockAdjustment>> hibernateCallback = new HibernateCallback<Page<StockAdjustment>>() {

			@Override
			public Page<StockAdjustment> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria saCriteria = session.createCriteria(StockAdjustment.class);
				if(StringFormatUtil.isNumeric(searchParam.getSearchCriteria())) {
					saCriteria.add(Restrictions.sqlRestriction("SA_NUMBER LIKE ?",
							searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				saCriteria.add(Restrictions.eq(StockAdjustment.FIELD.stockAdjustmentClassificationId.name(), typeId));
				SearchCommonUtil.searchCommonParams(saCriteria, null, "companyId",
						StockAdjustment.FIELD.saDate.name(), StockAdjustment.FIELD.saDate.name(),
						StockAdjustment.FIELD.saDate.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
				workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				if(!formStatusIds.isEmpty())
					addAsOrInCritiria(workflowCriteria, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				saCriteria.add(Subqueries.propertyIn(StockAdjustment.FIELD.formWorkflowId.name(), workflowCriteria));
				saCriteria.addOrder(Order.desc(StockAdjustment.FIELD.saNumber.name()));
				saCriteria.addOrder(Order.desc(StockAdjustment.FIELD.saDate.name()));
				return getAll(saCriteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	private void searchSANumber(DetachedCriteria saCriteria, String criteria) {
		if(StringFormatUtil.isNumeric(criteria)) {
			saCriteria.add(Restrictions.sqlRestriction("SA_NUMBER LIKE ?",
					criteria.trim(), Hibernate.STRING));
		}
	}

	@Override
	public Page<StockAdjustment> getWithdrawnStockAdjustments(int itemId,
			int warehouseId, Date date, PageSetting pageSetting) {
		DetachedCriteria saDc = getDetachedCriteria();
		saDc.createAlias("formWorkflow", "fw");
		saDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		saDc.add(Restrictions.eq(StockAdjustment.FIELD.saDate.name(), date));
		saDc.add(Restrictions.eq(StockAdjustment.FIELD.warehouseId.name(), warehouseId));

		saDc.add(Restrictions.eq(StockAdjustment.FIELD.stockAdjustmentClassificationId.name(), StockAdjustment.STOCK_ADJUSTMENT_OUT));
		return getAll(saDc, pageSetting);
	}


	@Override
	public int generateSANoByCompAndTypeId(int companyId, int typeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(StockAdjustment.FIELD.saNumber.name()));
		dc.add(Restrictions.eq(StockAdjustment.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(StockAdjustment.FIELD.stockAdjustmentClassificationId.name(), typeId));
		return generateSeqNo(dc);
	}
}
