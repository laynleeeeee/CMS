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
import eulap.eb.dao.PersonnelActionNoticeDao;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PersonnelActionNotice;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Implementation class for {@link PersonnelActionNoticeDao}

 *
 */
public class PersonnelActionNoticeDaoImpl extends BaseDao<PersonnelActionNotice> implements PersonnelActionNoticeDao {

	@Override
	protected Class<PersonnelActionNotice> getDomainClass() {
		return PersonnelActionNotice.class;
	}

	@Override
	public Integer generateSequenceNo() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(PersonnelActionNotice.FIELD.sequenceNo.name()));
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
	public Page<PersonnelActionNotice> getActionNotices(final ApprovalSearchParam searchParam, final List<Integer> statuses,
			final PageSetting pageSetting) {
		HibernateCallback<Page<PersonnelActionNotice>> hibernateCallback = new HibernateCallback<Page<PersonnelActionNotice>>() {

			@Override
			public Page<PersonnelActionNotice> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(PersonnelActionNotice.class);
				if(!searchParam.getSearchCriteria().trim().isEmpty()){
					dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						PersonnelActionNotice.FIELD.date.name(), PersonnelActionNotice.FIELD.date.name(),
						PersonnelActionNotice.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if(statuses.size() > 0){
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), statuses);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(PersonnelActionNotice.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(PersonnelActionNotice.FIELD.date.name()));
				dc.addOrder(Order.desc(PersonnelActionNotice.FIELD.sequenceNo.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public List<PersonnelActionNotice> searchActionNotices(final String searchCriteria, final User user) {
		return getHibernateTemplate().execute(new HibernateCallback<List<PersonnelActionNotice>>() {

			@Override
			public List<PersonnelActionNotice> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(PersonnelActionNotice.class);
				if(StringFormatUtil.isNumeric(searchCriteria)){
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
							searchCriteria, Hibernate.STRING));
				}
				if(!user.getCompanyIds().isEmpty()){
					criteria.add(Restrictions.in(PersonnelActionNotice.FIELD.companyId.name(), user.getCompanyIds()));
				}
				criteria.addOrder(Order.asc(PersonnelActionNotice.FIELD.sequenceNo.name()));
				return getAllByCriteria(criteria);
			}
		});
	}

	@Override
	public List<PersonnelActionNotice> getLatestActionNotices(Date updatedDate) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.or(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true),
				Restrictions.eq(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID)));
		if(updatedDate != null){
			DetachedCriteria dcWorkflowlOG = DetachedCriteria.forClass(FormWorkflowLog.class);
			dcWorkflowlOG.setProjection(Projections.property(FormWorkflowLog.FIELD.formWorkflowId.name()));
			dcWorkflowlOG.add(Restrictions.gt(FormWorkflowLog.FIELD.createdDate.name(), updatedDate));
			dcWorkflow.add(Subqueries.propertyIn(FormWorkflow.FIELD.id.name(), dcWorkflowlOG));
		}

		DetachedCriteria eDc = DetachedCriteria.forClass(Employee.class);
		eDc.setProjection(Projections.property(Employee.FIELD.id.name()));
		DetachedCriteria epDc = DetachedCriteria.forClass(EmployeeProfile.class);
		epDc.setProjection(Projections.property(EmployeeProfile.FIELD.employeeId.name()));
		eDc.add(Subqueries.propertyIn(Employee.FIELD.id.name(), epDc));

		dc.add(Subqueries.propertyIn(PersonnelActionNotice.FIELD.employeeId.name(), eDc));
		dc.add(Subqueries.propertyIn(PersonnelActionNotice.FIELD.formWorkflowId.name(), dcWorkflow));
		return getAll(dc);
	}

}
