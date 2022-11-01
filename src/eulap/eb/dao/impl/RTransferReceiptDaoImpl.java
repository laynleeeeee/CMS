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
import eulap.eb.dao.RTransferReceiptDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.domain.hibernate.RTransferReceiptItem;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * DAO Implementation of {@link RTransferReceiptDao}

 */
public class RTransferReceiptDaoImpl extends BaseDao<RTransferReceipt> implements RTransferReceiptDao{

	@Override
	protected Class<RTransferReceipt> getDomainClass() {
		return RTransferReceipt.class;
	}

	@Override
	public Integer generateTRNumber(int companyId) {
		return generateSequenceNumber("trNumber", "companyId", companyId);
	}

	@Override
	public Page<RTransferReceipt> getAllTRsByStatus(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final Integer typeId, final PageSetting pageSetting) {

		HibernateCallback<Page<RTransferReceipt>> hibernateCallback = new HibernateCallback<Page<RTransferReceipt>>() {

			@Override
			public Page<RTransferReceipt> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria trCriteria = session.createCriteria(RTransferReceipt.class);
				trCriteria.add(Restrictions.eq(RTransferReceipt.FIELD.transferReceiptTypeId.name(), typeId));
				SearchCommonUtil.searchCommonParams(trCriteria, null, "companyId",
						RTransferReceipt.FIELD.trDate.name(), RTransferReceipt.FIELD.trDate.name(),
						RTransferReceipt.FIELD.trDate.name(), searchParam.getUser().getCompanyIds(), searchParam);
				if(StringFormatUtil.isNumeric(searchParam.getSearchCriteria().trim())) {
					trCriteria.add(Restrictions.sqlRestriction("TR_NUMBER LIKE ?",
							searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
				workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				if(!formStatusIds.isEmpty())
					addAsOrInCritiria(workflowCriteria, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				trCriteria.add(Subqueries.propertyIn(RTransferReceipt.FIELD.formWorkflowId.name(), workflowCriteria));
				trCriteria.addOrder(Order.desc(RTransferReceipt.FIELD.trNumber.name()));
				trCriteria.addOrder(Order.desc(RTransferReceipt.FIELD.trDate.name()));
				return getAll(trCriteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<RTransferReceipt> searchTRs(String criteria, int typeId, PageSetting pageSetting) {
		DetachedCriteria trCriteria = getDetachedCriteria();
		if(StringFormatUtil.isNumeric(criteria.trim())) {
			trCriteria.add(Restrictions.sqlRestriction("TR_NUMBER LIKE ?",
					criteria.trim(), Hibernate.STRING));
		}
		trCriteria.add(Restrictions.eq(RTransferReceipt.FIELD.transferReceiptTypeId.name(), typeId));
		trCriteria.addOrder(Order.asc(RTransferReceipt.FIELD.trNumber.name()));
		return getAll(trCriteria, pageSetting);
	}

	@Override
	public List<RTransferReceipt> getTrAfterDate(int itemId, int warehouseId, Date date) {
		DetachedCriteria trCriteria = getDetachedCriteria();
		trCriteria.add(Restrictions.gt(RTransferReceipt.FIELD.trDate.name(), date));
		trCriteria.add(Restrictions.eq(RTransferReceipt.FIELD.warehouseFromId.name(), warehouseId));
		DetachedCriteria trItemDc = DetachedCriteria.forClass(RTransferReceiptItem.class);
		trItemDc.setProjection(Projections.property(RTransferReceiptItem.FIELD.rTransferReceiptId.name()));
		trItemDc.add(Restrictions.eq(RTransferReceiptItem.FIELD.itemId.name(), itemId));
		trCriteria.add(Subqueries.propertyIn(RTransferReceipt.FIELD.id.name(), trItemDc));
		trCriteria.createAlias("formWorkflow", "fw");
		trCriteria.add(Restrictions.eq("fw.complete", true));
		return getAll(trCriteria);
	}
}
