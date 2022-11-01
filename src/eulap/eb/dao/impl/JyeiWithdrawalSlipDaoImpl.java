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
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.JyeiWithdrawalSlipDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.JyeiWithdrawalSlip;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Implementation class for {@link JyeiWithdrawalSlipDao}

 */

public class JyeiWithdrawalSlipDaoImpl extends BaseDao<JyeiWithdrawalSlip> implements JyeiWithdrawalSlipDao {

	@Override
	protected Class<JyeiWithdrawalSlip> getDomainClass() {
		return JyeiWithdrawalSlip.class;
	}

	@Override
	public Integer generateSequenceNo(Integer companyId, Integer requisitionTypeId) {
		DetachedCriteria dc = DetachedCriteria.forClass(WithdrawalSlip.class);
		dc.setProjection(Projections.max(WithdrawalSlip.FIELD.wsNumber.name()));
		if(companyId != null && companyId != -1){
			DetachedCriteria comapnyCrit = DetachedCriteria.forClass(Company.class);
			restrictO2OReference(dc, comapnyCrit, companyId);
		}
		DetachedCriteria jyeiWSDc = getDetachedCriteria();
		jyeiWSDc.setProjection(Projections.property(JyeiWithdrawalSlip.FIELD.withdrawalSlipId.name()));
		if(requisitionTypeId != null) {
			jyeiWSDc.add(Restrictions.eq(JyeiWithdrawalSlip.FIELD.requisitionTypeId.name(), requisitionTypeId));
		}
		dc.add(Subqueries.propertyIn(WithdrawalSlip.FIELD.id.name(), jyeiWSDc));
		return generateSeqNo(dc);
	}

	@Override
	public Page<JyeiWithdrawalSlip> getWSByRequisitionTypeId(Integer requisitionTypeId, ApprovalSearchParam searchParam,
			List<Integer> formStatusIds, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(JyeiWithdrawalSlip.FIELD.requisitionTypeId.name(), requisitionTypeId));
		DetachedCriteria wsDc = DetachedCriteria.forClass(WithdrawalSlip.class);
		wsDc.setProjection(Projections.property(WithdrawalSlip.FIELD.id.name()));
		wsDc.add(Restrictions.sqlRestriction("WS_NUMBER LIKE ?", "%"+searchParam.getSearchCriteria().trim()+"%", Hibernate.STRING));

		// Workflow status
		addUserCompanyByReferenceObject(wsDc, searchParam.getUser().getCompanyIds(), searchParam);
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		if (formStatusIds.size() > 0)
			addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
		dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		wsDc.add(Subqueries.propertyIn(WithdrawalSlip.FIELD.formWorkflowId.name(), dcWorkflow));
		wsDc.addOrder(Order.desc(WithdrawalSlip.FIELD.createdDate.name()));
		dc.add(Subqueries.propertyIn(JyeiWithdrawalSlip.FIELD.withdrawalSlipId.name(), wsDc));
		dc.createAlias("withdrawalSlip", "ws");
		dc.addOrder(Order.desc("ws.wsNumber"));
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

	@Override
	public JyeiWithdrawalSlip getByWithdrawalSlipId(Integer wsId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(JyeiWithdrawalSlip.FIELD.withdrawalSlipId.name(), wsId));
		return get(dc);
	}

	@Override
	public Page<JyeiWithdrawalSlip> searchJyeiWithdrawalSlips(String criteria, int requisitionTypeId,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(JyeiWithdrawalSlip.FIELD.requisitionTypeId.name(), requisitionTypeId));

		DetachedCriteria wsDc = DetachedCriteria.forClass(WithdrawalSlip.class);
		wsDc.setProjection(Projections.property(WithdrawalSlip.FIELD.id.name()));
		if(StringFormatUtil.isNumeric(criteria)) {
			wsDc.add(Restrictions.sqlRestriction("WS_NUMBER LIKE ?", criteria.trim(), Hibernate.STRING));
		}
		wsDc.addOrder(Order.asc(WithdrawalSlip.FIELD.wsNumber.name()));

		dc.add(Subqueries.propertyIn(JyeiWithdrawalSlip.FIELD.withdrawalSlipId.name(), wsDc));
		return getAll(dc, pageSetting);
	}

	@Override
	public double getSIAvailableStockFromRf(int wsId, Integer itemId, Integer rfObjectId) {
		DetachedCriteria dc = DetachedCriteria.forClass(SerialItem.class);
		dc.add(Restrictions.eq(SerialItem.FIELD.active.name(), true));
		dc.add(Restrictions.eq(SerialItem.FIELD.itemId.name(), itemId));

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));

		DetachedCriteria wsDc = DetachedCriteria.forClass(WithdrawalSlip.class);
		wsDc.setProjection(Projections.property(WithdrawalSlip.FIELD.ebObjectId.name()));
		if(wsId != 0){
			wsDc.add(Restrictions.eq(WithdrawalSlip.FIELD.id.name(), wsId));
		}

		DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
		workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		workflowCriteria.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		DetachedCriteria ooPoDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooPoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));

		DetachedCriteria rfDc = DetachedCriteria.forClass(RequisitionForm.class);
		rfDc.setProjection(Projections.property(RequisitionForm.FIELD.ebObjectId.name()));
		rfDc.add(Restrictions.eq(RequisitionForm.FIELD.ebObjectId.name(), rfObjectId));

		ooPoDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), rfDc));
		wsDc.add(Subqueries.propertyIn(WithdrawalSlip.FIELD.ebObjectId.name(), ooPoDc));
		wsDc.add(Subqueries.propertyIn(WithdrawalSlip.FIELD.formWorkflowId.name(), workflowCriteria));
		ooDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), wsDc));
		dc.add(Subqueries.propertyIn(SerialItem.FIELD.ebObjectId.name(), ooDc));
		dc.setProjection(Projections.sum(SerialItem.FIELD.quantity.name()));
		return getBySumProjection(dc);
	}

	@Override
	public List<WithdrawalSlip> getWsByEbObjectId(Integer ebObjectId, Boolean isComplete) {
		DetachedCriteria wsDc = DetachedCriteria.forClass(WithdrawalSlip.class);
		DetachedCriteria o2oDc = DetachedCriteria.forClass(ObjectToObject.class);
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);

		o2oDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		o2oDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));
		o2oDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(),
				JyeiWithdrawalSlip.WITHDRAWAL_SLIP_REQUISITION_FORM_OR_TYPE));

		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		if(!isComplete) {
			fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		} else {
			fwDc.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		}

		wsDc.add(Subqueries.propertyIn(RPurchaseOrder.FIELD.ebObjectId.name(), o2oDc));
		wsDc.add(Subqueries.propertyIn(RPurchaseOrder.FIELD.formWorkflowId.name(), fwDc));

		return getHibernateTemplate().findByCriteria(wsDc);
	}
}
