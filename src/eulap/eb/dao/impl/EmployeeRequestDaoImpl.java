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
import eulap.eb.dao.EmployeeRequestDao;
import eulap.eb.domain.hibernate.EmployeeRequest;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Implementation class for {@link EmployeeRequestDao}

 *
 */
public class EmployeeRequestDaoImpl extends BaseDao<EmployeeRequest> implements EmployeeRequestDao {

	@Override
	protected Class<EmployeeRequest> getDomainClass() {
		return EmployeeRequest.class;
	}

	@Override
	public Integer generateSequenceNo(Integer typeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeRequest.FIELD.requestTypeId.name(), typeId));
		dc.setProjection(Projections.max(EmployeeRequest.FIELD.sequenceNo.name()));
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
	public Page<EmployeeRequest> getRequests(final int typeId, final ApprovalSearchParam searchParam, final List<Integer> statuses,
			final PageSetting pageSetting) {
		HibernateCallback<Page<EmployeeRequest>> hibernateCallback = new HibernateCallback<Page<EmployeeRequest>>() {

			@Override
			public Page<EmployeeRequest> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(EmployeeRequest.class);
				dc.add(Restrictions.eq(EmployeeRequest.FIELD.requestTypeId.name(), typeId));
				if(!searchParam.getSearchCriteria().trim().isEmpty()){
					dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						EmployeeRequest.FIELD.date.name(), EmployeeRequest.FIELD.date.name(),
						EmployeeRequest.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if(statuses.size() > 0){
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), statuses);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(EmployeeRequest.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(EmployeeRequest.FIELD.date.name()));
				dc.addOrder(Order.desc(EmployeeRequest.FIELD.sequenceNo.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public List<EmployeeRequest> searchEmployeeRequest(final Integer typeId, final String searchCriteria, final User user) {
		return getHibernateTemplate().execute(new HibernateCallback<List<EmployeeRequest>>() {

			@Override
			public List<EmployeeRequest> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(EmployeeRequest.class);
				if(StringFormatUtil.isNumeric(searchCriteria)){
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
							searchCriteria, Hibernate.STRING));
				}
				if(!user.getCompanyIds().isEmpty()){
					criteria.add(Restrictions.in(EmployeeRequest.FIELD.companyId.name(), user.getCompanyIds()));
				}
				criteria.add(Restrictions.eq(EmployeeRequest.FIELD.requestTypeId.name(), typeId));
				criteria.addOrder(Order.asc(EmployeeRequest.FIELD.sequenceNo.name()));
				return getAllByCriteria(criteria);
			}
		});
	}
}
