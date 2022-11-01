package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.HibernateCallback;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ProcessingReportDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Implementing class of {@link ProcessingReportDao}

 *
 */
public class ProcessingReportDaoImpl extends BaseDao<ProcessingReport> implements ProcessingReportDao{

	@Override
	protected Class<ProcessingReport> getDomainClass() {
		return ProcessingReport.class;
	}

	@Override
	public Integer generateSeqNo(int processingTypeId, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(ProcessingReport.FIELD.sequenceNo.name()));
		dc.add(Restrictions.eq(ProcessingReport.FIELD.processingReportTypeId.name(), processingTypeId));
		if (companyId != null) {
			dc.add(Restrictions.eq(ProcessingReport.FIELD.companyId.name(), companyId));
		}
		return generateSeqNo(dc);
	}

	@Override
	public Page<ProcessingReport> searchProcessingReports(String criteria, int processingTypeId,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ProcessingReport.FIELD.processingReportTypeId.name(), processingTypeId));
		if(StringFormatUtil.isNumeric(criteria)) {
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
					criteria.trim(), Hibernate.STRING));
		}
		dc.addOrder(Order.asc(ProcessingReport.FIELD.sequenceNo.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<ProcessingReport> getProcessingReports(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final Integer typeId, final PageSetting pageSetting) {

		HibernateCallback<Page<ProcessingReport>> hibernateCallback = new HibernateCallback<Page<ProcessingReport>>() {

			@Override
			public Page<ProcessingReport> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(ProcessingReport.class);
				dc.add(Restrictions.eq(ProcessingReport.FIELD.processingReportTypeId.name(), typeId));
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						ProcessingReport.FIELD.date.name(), ProcessingReport.FIELD.date.name(),
						ProcessingReport.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				Criterion mCrit = Restrictions.like(ProcessingReport.FIELD.refNumber.name(),
						"%" + searchParam.getSearchCriteria().trim() + "%");
				if (StringFormatUtil.isNumeric(searchParam.getSearchCriteria())) {
					dc.add(Restrictions.or(mCrit,
							Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
									searchParam.getSearchCriteria().trim(), Hibernate.STRING)));
				} else {
					dc.add(mCrit);
				}
				// Workflow status
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0)
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(ProcessingReport.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(ProcessingReport.FIELD.sequenceNo.name()));
				dc.addOrder(Order.desc(ProcessingReport.FIELD.date.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public List<ProcessingReport> getProcessingReportByTypeId(Integer typeId, Date startDate, Date currentDate) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ProcessingReport.FIELD.processingReportTypeId.name(), typeId));
		dc.add(Restrictions.between(ProcessingReport.FIELD.createdDate.name(), startDate, currentDate));
		return getAll(dc);
	}
}
