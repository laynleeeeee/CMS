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
import eulap.eb.dao.SalesQuotationDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.SalesQuotation;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * DAO implementation class for {@link SalesQuotationDao}

 */

public class SalesQuotationDaoImpl extends BaseDao<SalesQuotation> implements SalesQuotationDao {

	@Override
	protected Class<SalesQuotation> getDomainClass() {
		return SalesQuotation.class;
	}

	@Override
	public Integer generateSequenceNo(int companyId) {
		return generateSequenceNumber(SalesQuotation.FIELD.sequenceNumber.name(),
				SalesQuotation.FIELD.companyId.name(), companyId);
	}

	@Override
	public Page<SalesQuotation> getSalesQuotations(ApprovalSearchParam searchParam, List<Integer> formStatusIds,
			PageSetting pageSetting) {
		HibernateCallback<Page<SalesQuotation>> hibernateCallback = new HibernateCallback<Page<SalesQuotation>>() {
			@Override
			public Page<SalesQuotation> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(SalesQuotation.class);
				SearchCommonUtil.searchCommonParams(dc, null, SalesQuotation.FIELD.companyId.name(),
						SalesQuotation.FIELD.date.name(), SalesQuotation.FIELD.date.name(),
						SalesQuotation.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				String strCriteria = searchParam.getSearchCriteria();
				if (strCriteria != null && !strCriteria.trim().isEmpty()) {
					dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ? ", strCriteria.trim(), Hibernate.STRING));
				}
				// Workflow status
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0) {
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(),formStatusIds);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(SalesQuotation.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(SalesQuotation.FIELD.date.name()));
				dc.addOrder(Order.desc(SalesQuotation.FIELD.id.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public String getCustomerShipTo(Integer arCustomerId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria wdc = DetachedCriteria.forClass(FormWorkflow.class);
		wdc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		wdc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(SalesQuotation.FIELD.formWorkflowId.name(), wdc));
		dc.add(Restrictions.eq(SalesQuotation.FIELD.arCustomerId.name(), arCustomerId));
		dc.addOrder(Order.desc(SalesQuotation.FIELD.createdDate.name()));
		dc.getExecutableCriteria(getSession()).setMaxResults(1);
		return get(dc) != null ? get(dc).getShipTo() : null;
	}

	@Override
	public Page<SalesQuotation> retrieveSalesQuotations(String searchCriteria, PageSetting pageSetting) {
		HibernateCallback<Page<SalesQuotation>> sqhCallBack = new HibernateCallback<Page<SalesQuotation>>() {
			@Override
			public Page<SalesQuotation> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(SalesQuotation.class);
				if (!searchCriteria.isEmpty()) {
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchCriteria.trim(), Hibernate.STRING));
				} 
				return getAll(criteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(sqhCallBack);
	}

	@Override
	public Page<SalesQuotation> getSalesQuotations(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer sequenceNo, Date dateFrom, Date dateTo, PageSetting pageSetting, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);

		if (companyId != null) {
			dc.add(Restrictions.eq(SalesQuotation.FIELD.companyId.name(), companyId));
		}
		if (arCustomerId != null) {
			dc.add(Restrictions.eq(SalesQuotation.FIELD.arCustomerId.name(), arCustomerId));
		}
		if (arCustomerAccountId != null) {
			dc.add(Restrictions.eq(SalesQuotation.FIELD.arCustomerAcctId.name(), arCustomerAccountId));
		}
		if (sequenceNo != null) {
			dc.add(Restrictions.eq(SalesQuotation.FIELD.sequenceNumber.name(), sequenceNo));
		}
		if (dateFrom != null && dateTo != null) {
			dc.add(Restrictions.between(SalesQuotation.FIELD.date.name(), dateFrom, dateTo));
		} else if (dateFrom != null) {
			dc.add(Restrictions.ge(SalesQuotation.FIELD.date.name(), dateFrom));
		} else if (dateTo != null) {
			dc.add(Restrictions.le(SalesQuotation.FIELD.date.name(), dateTo));
		}

		// Sales Quotation Workflow
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(SalesQuotation.FIELD.formWorkflowId.name(), dcWorkflow));

		dc.addOrder(Order.desc(SalesQuotation.FIELD.date.name()));
		dc.addOrder(Order.desc(SalesQuotation.FIELD.sequenceNumber.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUsedBySO(Integer sqId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		DetachedCriteria soDc = DetachedCriteria.forClass(SalesOrder.class);
		soDc.setProjection(Projections.property(SalesOrder.FIELD.salesQuotationId.name()));
		soDc.add(Restrictions.eq(SalesOrder.FIELD.salesQuotationId.name(), sqId));
		soDc.add(Subqueries.propertyIn(SalesOrder.FIELD.formWorkflowId.name(), fwDc));

		dc.add(Subqueries.propertyIn(SalesOrder.FIELD.id.name(), soDc));
		return getAll(dc).size() > 0;
	}

}
