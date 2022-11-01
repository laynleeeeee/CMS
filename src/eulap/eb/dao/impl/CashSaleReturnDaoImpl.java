package eulap.eb.dao.impl;

import java.sql.SQLException;
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
import eulap.eb.dao.CashSaleReturnDao;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Implementing class of {@link CashSaleReturnDao}

 *
 */
public class CashSaleReturnDaoImpl extends BaseDao<CashSaleReturn> implements CashSaleReturnDao {

	@Override
	protected Class<CashSaleReturn> getDomainClass() {
		return CashSaleReturn.class;
	}

	@Override
	public Integer generateCsrNumber(Integer companyId, Integer typeId) {
		DetachedCriteria csrCrit= getDetachedCriteria();
		csrCrit.setProjection(Projections.max(CashSaleReturn.FIELD.csrNumber.name()));
		csrCrit.add((Restrictions.eq(CashSaleReturn.FIELD.cashSaleTypeId.name(), typeId)));
		csrCrit.add((Restrictions.eq(CashSaleReturn.FIELD.companyId.name(), companyId)));
		return generateSeqNo(csrCrit);
	}

	@Override
	public Page<CashSaleReturn> searchCashSaleReturns(String criteria, int typeId, PageSetting pageSetting) {
		DetachedCriteria csrCriteria = getDetachedCriteria();
		csrCriteria.add(Restrictions.eq(CashSaleReturn.FIELD.cashSaleTypeId.name(), typeId));
		if(StringFormatUtil.isNumeric(criteria)) {
			csrCriteria.add(Restrictions.sqlRestriction("CSR_NUMBER LIKE ?", criteria, Hibernate.STRING));
		}
		csrCriteria.addOrder(Order.asc(CashSaleReturn.FIELD.csrNumber.name()));
		return getAll(csrCriteria, pageSetting);
	}

	@Override
	public Page<CashSaleReturn> getCashSaleReturns(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final Integer typeId, final PageSetting pageSetting) {
		HibernateCallback<Page<CashSaleReturn>> hibernateCallback = new HibernateCallback<Page<CashSaleReturn>>() {

			@Override
			public Page<CashSaleReturn> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(CashSaleReturn.class);
			dc.add(Restrictions.eq(CashSaleReturn.FIELD.cashSaleTypeId.name(), typeId));

			SearchCommonUtil.searchCommonParams(dc, null, "companyId",
					CashSaleReturn.FIELD.date.name(), CashSaleReturn.FIELD.date.name(),
					CashSaleReturn.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);

			if(StringFormatUtil.isNumeric(searchParam.getSearchCriteria())) {
				dc.add(Restrictions.sqlRestriction("CSR_NUMBER LIKE ?",
						searchParam.getSearchCriteria().trim(), Hibernate.STRING));
			}
			// Workflow status
			DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
			if (formStatusIds.size() > 0)
				addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
			dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
			dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
			dc.add(Subqueries.propertyIn(CashSaleReturn.FIELD.formWorkflowId.name(), dcWorkflow));
			dc.addOrder(Order.desc(CashSaleReturn.FIELD.date.name()));
			dc.addOrder(Order.desc(CashSaleReturn.FIELD.createdDate.name()));
			return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public boolean isExistingInCashSaleReturn(int cashSaleReturnId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSaleReturn.FIELD.refCashSaleReturnId.name(), cashSaleReturnId));
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(CashSaleReturn.FIELD.formWorkflowId.name(), dcWorkflow));
		return getAll(dc).size() > 0;
	}

	@Override
	public List<CashSaleReturn> getCashReturnSaleUsedInReturns(int cashSaleReturnId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSaleReturn.FIELD.refCashSaleReturnId.name(), cashSaleReturnId));
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(CashSaleReturn.FIELD.formWorkflowId.name(), dcWorkflow));
		return getAll(dc);
	}

	@Override
	public List<CashSaleReturn> getCSRByRefCSId(Integer cashSaleId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSaleReturn.FIELD.cashSaleId.name(), cashSaleId));
		dc.createAlias("formWorkflow", "rfw");
		dc.add(Restrictions.ne("rfw.currentStatusId", FormStatus.CANCELLED_ID));
		return getAll(dc);
	}

	@Override
	public List<CashSaleReturn> getCSRsByCustomer(Integer arCustomerId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSaleReturn.FIELD.arCustomerId.name(), arCustomerId));

		// Cash sale workflow subquery.
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(CashSaleReturn.FIELD.formWorkflowId.name(), dcWorkflow));

		dc.addOrder(Order.desc(CashSaleReturn.FIELD.date.name()));
		return getAll(dc);
	}
}
