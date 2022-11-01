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
import eulap.eb.dao.EmployeeLeaveCreditDao;
import eulap.eb.domain.hibernate.EmployeeLeaveCredit;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Implemenation class of {@link EmployeeLeaveCreditDao}

 *
 */
public class EmployeeLeaveCreditDaoImpl extends BaseDao<EmployeeLeaveCredit> implements EmployeeLeaveCreditDao{

	@Override
	protected Class<EmployeeLeaveCredit> getDomainClass() {
		return EmployeeLeaveCredit.class;
	}

	@Override
	public Integer generateSequenceNo() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(EmployeeLeaveCredit.FIELD.sequenceNumber.name()));
		List<Object> result = getByProjection(dc);
		if (result == null) {
			return 1;
		}
		Object obj = result.iterator().next();
		if (obj == null) {
			return 1;
		}
		return ((Integer) obj) + 1;
	}

	@Override
	public Page<EmployeeLeaveCredit> getEmployeeLeaveCredits(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final PageSetting pageSetting) {

		HibernateCallback<Page<EmployeeLeaveCredit>> hibernateCallback = new HibernateCallback<Page<EmployeeLeaveCredit>>() {

			@Override
			public Page<EmployeeLeaveCredit> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(EmployeeLeaveCredit.class);
				if(!searchParam.getSearchCriteria().trim().isEmpty()){
					dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						EmployeeLeaveCredit.FIELD.date.name(), EmployeeLeaveCredit.FIELD.date.name(),
						EmployeeLeaveCredit.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if(formStatusIds.size() > 0){
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(EmployeeLeaveCredit.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(EmployeeLeaveCredit.FIELD.date.name()));
				dc.addOrder(Order.desc(EmployeeLeaveCredit.FIELD.sequenceNumber.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public List<EmployeeLeaveCredit> searchEmployeeLeaveCredits(final String searchCriteria, final User user) {
		return getHibernateTemplate().execute(new HibernateCallback<List<EmployeeLeaveCredit>>() {

			@Override
			public List<EmployeeLeaveCredit> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(EmployeeLeaveCredit.class);
				if(StringFormatUtil.isNumeric(searchCriteria)){
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
							searchCriteria, Hibernate.STRING));
				}
				if(!user.getCompanyIds().isEmpty()){
					criteria.add(Restrictions.in(EmployeeLeaveCredit.FIELD.companyId.name(), user.getCompanyIds()));
				}
				criteria.addOrder(Order.asc(EmployeeLeaveCredit.FIELD.sequenceNumber.name()));
				return getAllByCriteria(criteria);
			}
		});
	}

}
