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
import eulap.eb.dao.AccountSalesDao;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.AccountSales;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Implementation class for {@link AccountSaleDao}

 *
 */
public class AccountSalesDaoImpl extends BaseDao<AccountSales> implements AccountSalesDao {

	@Override
	protected Class<AccountSales> getDomainClass() {
		return AccountSales.class;
	}

	@Override
	public int getMaxPONumber(int companyId) {
		return generateSequenceNumber(AccountSales.FIELD.poNumber.name(), AccountSales.FIELD.companyId.name(), companyId);
	}

	@Override
	public Page<AccountSales> getAllPOsByStatus(ApprovalSearchParam searchParam, List<Integer> formStatusIds,
			PageSetting pageSetting) {

		HibernateCallback<Page<AccountSales>> hibernateCallback = new HibernateCallback<Page<AccountSales>>() {

			@Override
			public Page<AccountSales> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria poCriteria = session.createCriteria(AccountSales.class);
				if(StringFormatUtil.isNumeric(searchParam.getSearchCriteria())) {
					poCriteria.add(Restrictions.sqlRestriction("PO_NUMBER LIKE ?",
							searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(poCriteria, null, "companyId",
						AccountSales.FIELD.poDate.name(), AccountSales.FIELD.poDate.name(),
						AccountSales.FIELD.poDate.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
				if(!formStatusIds.isEmpty())
					addAsOrInCritiria(workflowCriteria, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				workflowCriteria.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				poCriteria.add(Subqueries.propertyIn(AccountSales.FIELD.formWorkflowId.name(), workflowCriteria));
				poCriteria.addOrder(Order.desc(AccountSales.FIELD.poDate.name()));
				poCriteria.addOrder(Order.desc(AccountSales.FIELD.poNumber.name()));
				return getAll(poCriteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<AccountSales> getAccountSales(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer asNumber, Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);

		if (companyId != null) {
			dc.add(Restrictions.eq(AccountSales.FIELD.companyId.name(), companyId));
		}
		if (arCustomerId != null) {
			dc.add(Restrictions.eq(AccountSales.FIELD.arCustomerId.name(), arCustomerId));
		}
		if (arCustomerAccountId != null) {
			dc.add(Restrictions.eq(AccountSales.FIELD.arCustomerAccountId.name(), arCustomerAccountId));
		}
		if (asNumber != null) {
			dc.add(Restrictions.eq(AccountSales.FIELD.poNumber.name(), asNumber));
		}
		if (dateFrom != null && dateTo != null) {
			dc.add(Restrictions.between(AccountSales.FIELD.poDate.name(), dateFrom, dateTo));
		} else if (dateFrom != null) {
			dc.add(Restrictions.ge(AccountSales.FIELD.poDate.name(), dateFrom));
		} else if (dateTo != null) {
			dc.add(Restrictions.le(AccountSales.FIELD.poDate.name(), dateTo));
		}


		// Account sale Workflow
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		if (status != AccountSales.STATUS_ALL) {
			DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
			otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.fromObjectId.name()));
			DetachedCriteria atDc = DetachedCriteria.forClass(ArTransaction.class);
			atDc.setProjection(Projections.property(ArTransaction.FIELD.ebObjectId.name()));

			//Account sale return Workflow - Add account sale that are used by cancelled account sale return.
			DetachedCriteria asrWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
			asrWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
			asrWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), 
					FormStatus.CANCELLED_ID));
			atDc.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), asrWorkflow));

			otoDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.toObjectId.name(), atDc));
			if (status == AccountSales.STATUS_USED) {
				dc.add(Subqueries.propertyIn(AccountSales.FIELD.ebObjectId.name(), otoDc));
			} else if (status == AccountSales.STATUS_UNUSED) {
				dc.add(Subqueries.propertyNotIn(AccountSales.FIELD.ebObjectId.name(), otoDc));
			}
		}
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(AccountSales.FIELD.formWorkflowId.name(), dcWorkflow));

		dc.addOrder(Order.desc(AccountSales.FIELD.poDate.name()));
		dc.addOrder(Order.desc(AccountSales.FIELD.poNumber.name()));
		return getAll(dc, pageSetting);
	}
}
