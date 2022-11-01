package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.WithdrawalSlipDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * DAO Implementation of {@link WithdrawalSlipDao}

 *
 */
public class WithdrawalSlipDaoImpl extends BaseDao<WithdrawalSlip> implements WithdrawalSlipDao {

	@Override
	protected Class<WithdrawalSlip> getDomainClass() {
		return WithdrawalSlip.class;
	}

	@Override
	public Integer generateWSNumber(Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(WithdrawalSlip.FIELD.wsNumber.name()));
		if(companyId != null && companyId != -1){
			DetachedCriteria comapnyCrit = DetachedCriteria.forClass(Company.class);
			restrictO2OReference(dc, comapnyCrit, companyId);
		}
		return generateSeqNo(dc);
	}

	@Override
	public Page<WithdrawalSlip> getWithdrawalSlip(ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.sqlRestriction("WS_NUMBER LIKE ?", "%"+searchParam.getSearchCriteria().trim()+"%", Hibernate.STRING));
		// Workflow status
		addUserCompanyByReferenceObject(dc, searchParam.getUser().getCompanyIds(), searchParam);
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		if (formStatusIds.size() > 0)
			addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
		dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(WithdrawalSlip.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.addOrder(Order.desc(WithdrawalSlip.FIELD.createdDate.name()));
		return getAll(dc, pageSetting);
	}

	private void addUserCompanyByReferenceObject(DetachedCriteria dc, List<Integer> companyIds, ApprovalSearchParam searchParam) {
		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		DetachedCriteria comapnyCrit = DetachedCriteria.forClass(Company.class);

		comapnyCrit.setProjection(Projections.property(WithdrawalSlip.FIELD.ebObjectId.name()));
		if (!companyIds.isEmpty()) {
			comapnyCrit.add(Restrictions.in(Company.Field.id.name(), companyIds));
		}
		if(searchParam.getCompanyNo() != null) {
			comapnyCrit.add(Restrictions.like(Company.Field.companyNumber.name(), "%"+searchParam.getCompanyNo().trim()+"%"));
		}
		obj2ObjDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), comapnyCrit));

		dc.add(Subqueries.propertyIn(WithdrawalSlip.FIELD.ebObjectId.name(), obj2ObjDc));
		//Search for date/date range
		Date dateFrom = searchParam.getDateFrom();
		Date dateTo = searchParam.getDateTo();
		if(dateFrom != null) {
			if(dateTo != null) {
				//Criterion for date range
				Criterion firstDateRangeCriterion  = Restrictions.between(WithdrawalSlip.FIELD.date.name(), dateFrom, dateTo);
				if(WithdrawalSlip.FIELD.date.name() != null) {
					Criterion secondDateRangeCriterion = Restrictions.between(WithdrawalSlip.FIELD.date.name(), dateFrom, dateTo);
					LogicalExpression dateRangeExp = Restrictions.or(firstDateRangeCriterion, secondDateRangeCriterion);

					//Add date range criteria for dateProperty3
					if(WithdrawalSlip.FIELD.date.name() != null) {
						dateRangeExp = Restrictions.or(dateRangeExp,
								Restrictions.between(WithdrawalSlip.FIELD.date.name(), dateFrom, dateTo));
					}
					dc.add(dateRangeExp);
				} else {
					dc.add(firstDateRangeCriterion);
				}
			} else {
				//Criterion for date
				Criterion firstDateCriterion = Restrictions.eq(WithdrawalSlip.FIELD.date.name(), dateFrom);
				if(WithdrawalSlip.FIELD.date.name() != null) {
					Criterion secondDateCriterion = Restrictions.eq(WithdrawalSlip.FIELD.date.name(), dateFrom);
					LogicalExpression dateExpression = Restrictions.or(firstDateCriterion, secondDateCriterion);

					//Add criteria for dateProperty3
					if(WithdrawalSlip.FIELD.date.name() != null) {
						dateExpression = Restrictions.or(dateExpression,
								Restrictions.eq(WithdrawalSlip.FIELD.date.name(), dateFrom));
					}
					dc.add(dateExpression);
				} else {
					dc.add(firstDateCriterion);
				}
			}
		}
	}
}
