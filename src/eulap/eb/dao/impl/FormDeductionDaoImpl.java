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
import eulap.eb.dao.FormDeductionDao;
import eulap.eb.domain.hibernate.DeductionType;
import eulap.eb.domain.hibernate.FormDeduction;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * implementation class of {@link FormDeductionDao}

 *
 */
public class FormDeductionDaoImpl extends BaseDao<FormDeduction> implements FormDeductionDao{

	@Override
	protected Class<FormDeduction> getDomainClass() {
		return FormDeduction.class;
	}

	@Override
	public Integer generateSequenceNo(Integer typeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(FormDeduction.FIELD.sequenceNumber.name()));
		dc.add(Restrictions.eq(FormDeduction.FIELD.formDeductionTypeId.name(), typeId));
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
	public Page<FormDeduction> getFormDeductions(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final Integer typeId, final PageSetting pageSetting) {

		HibernateCallback<Page<FormDeduction>> hibernateCallback = new HibernateCallback<Page<FormDeduction>>() {

			@Override
			public Page<FormDeduction> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(FormDeduction.class);
				if(!searchParam.getSearchCriteria().trim().isEmpty()){
					String criteria = searchParam.getSearchCriteria();
					if (StringFormatUtil.isNumeric(criteria)) {
						dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", criteria.trim(), Hibernate.STRING));
					} else {
						DetachedCriteria ddTypeDc = DetachedCriteria.forClass(DeductionType.class);
						ddTypeDc.setProjection(Projections.property(DeductionType.FIELD.id.name()));
						ddTypeDc.add(Restrictions.like(DeductionType.FIELD.name.name(), "%" + criteria.trim() + "%"));
						dc.add(Subqueries.propertyIn(FormDeduction.FIELD.deductionTypeId.name(), ddTypeDc));
					}
				}
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						FormDeduction.FIELD.formDate.name(), FormDeduction.FIELD.formDate.name(),
						FormDeduction.FIELD.formDate.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if(formStatusIds.size() > 0){
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(FormDeduction.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.add(Restrictions.eq(FormDeduction.FIELD.formDeductionTypeId.name(), typeId));
				dc.addOrder(Order.desc(FormDeduction.FIELD.formDate.name()));
				dc.addOrder(Order.desc(FormDeduction.FIELD.sequenceNumber.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public List<FormDeduction> searchFormDeductions(final String searchCriteria, final Integer typeId, final User user) {

		return getHibernateTemplate().execute(new HibernateCallback<List<FormDeduction>>() {

			@Override
			public List<FormDeduction> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(FormDeduction.class);
				if(StringFormatUtil.isNumeric(searchCriteria)){
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
							searchCriteria, Hibernate.STRING));
				}
				if(!user.getCompanyIds().isEmpty()){
					/*
					 * For now: This returns null since there is no User Company in the Settings.
					 * Uncomment to search by current logged user company.
					 */
					criteria.add(Restrictions.in(FormDeduction.FIELD.companyId.name(), user.getCompanyIds()));
				}
				criteria.add(Restrictions.eq(FormDeduction.FIELD.formDeductionTypeId.name(), typeId));
				criteria.addOrder(Order.asc(FormDeduction.FIELD.sequenceNumber.name()));
				return getAllByCriteria(criteria);
			}
		});
	}

}
