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
import eulap.eb.dao.EmployeeDocumentDao;
import eulap.eb.domain.hibernate.EmployeeDocument;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Implementing class of {@link EmployeeDocumentDao}

 *
 */
public class EmployeeDocumentDaoImpl extends BaseDao<EmployeeDocument> implements EmployeeDocumentDao {

	@Override
	protected Class<EmployeeDocument> getDomainClass() {
		return EmployeeDocument.class;
	}

	@Override
	public Integer generateSequenceNo() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(EmployeeDocument.FIELD.sequenceNo.name()));
		List<Object> result = getByProjection(dc);
		if (result == null)
			return 1;
		Object obj = result.iterator().next();
		if (obj == null)
			return 1;
		return ((Integer) obj) + 1;
	}

	@Override
	public Page<EmployeeDocument> getEmployeeDocuments(final ApprovalSearchParam searchParam, final List<Integer> statuses,
			final PageSetting pageSetting) {
		HibernateCallback<Page<EmployeeDocument>> hibernateCallback = new HibernateCallback<Page<EmployeeDocument>>() {

			@Override
			public Page<EmployeeDocument> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(EmployeeDocument.class);
				if(!searchParam.getSearchCriteria().trim().isEmpty()){
					dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						EmployeeDocument.FIELD.date.name(), EmployeeDocument.FIELD.date.name(),
						EmployeeDocument.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if(statuses.size() > 0){
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), statuses);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(EmployeeDocument.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(EmployeeDocument.FIELD.date.name()));
				dc.addOrder(Order.desc(EmployeeDocument.FIELD.sequenceNo.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public List<EmployeeDocument> searchEmployeeDocuments(final String searchCriteria, final User user) {
		return getHibernateTemplate().execute(new HibernateCallback<List<EmployeeDocument>>() {

			@Override
			public List<EmployeeDocument> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(EmployeeDocument.class);
				if(StringFormatUtil.isNumeric(searchCriteria)){
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchCriteria, Hibernate.STRING));
				}
				if(!user.getCompanyIds().isEmpty()){
					criteria.add(Restrictions.in(EmployeeDocument.FIELD.companyId.name(), user.getCompanyIds()));
				}
				criteria.addOrder(Order.asc(EmployeeDocument.FIELD.sequenceNo.name()));
				return getAllByCriteria(criteria);
			}
		});
	}

}
