package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.eb.dao.PayrollTimePeriodScheduleDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.domain.hibernate.TimeSheet;

/**
 * Implementation class of {@link PayrollTimePeriodScheduleDao}

 *
 */
public class PayrollTimePeriodScheduleDaoImpl extends BaseDao<PayrollTimePeriodSchedule> implements PayrollTimePeriodScheduleDao{

	@Override
	protected Class<PayrollTimePeriodSchedule> getDomainClass() {
		return PayrollTimePeriodSchedule.class;
	}

	@Override
	public List<PayrollTimePeriodSchedule> getTimePeriodSchedules(
			Integer payrollTimePeriodId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollTimePeriodSchedule.Field.payrollTimePeriodId.name(), payrollTimePeriodId));
		return getAll(dc);
	}

	@Override
	public boolean isValidPayrollTimePeriod(PayrollTimePeriodSchedule payrollTimePeriodSched) {
		boolean isEdit = payrollTimePeriodSched.getId() != 0;
		String sql = "SELECT * FROM PAYROLL_TIME_PERIOD_SCHEDULE WHERE (? BETWEEN DATE_FROM AND DATE_TO" +
				" OR ? BETWEEN  DATE_FROM AND DATE_TO OR DATE_FROM >= ? and DATE_TO <= ?) ";
		if (isEdit) {
			sql += "and PAYROLL_TIME_PERIOD_ID != ?";
		}
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, DateUtil.formatToSqlDate(payrollTimePeriodSched.getDateFrom()));
			query.setParameter(1, DateUtil.formatToSqlDate(payrollTimePeriodSched.getDateTo()));
			query.setParameter(2, DateUtil.formatToSqlDate(payrollTimePeriodSched.getDateFrom()));
			query.setParameter(3, DateUtil.formatToSqlDate(payrollTimePeriodSched.getDateTo()));
			if (isEdit)
				query.setParameter(4, payrollTimePeriodSched.getPayrollTimePeriodId());
			List<?> list = query.list();
			return list.size() < 1;
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public List<PayrollTimePeriodSchedule> getTimePeriodSchedules(
			Integer month, Integer year, boolean isActiveOnly) {
		return getAll(getTimePeriodSched(month, year, isActiveOnly, null));
	}

	private DetachedCriteria getTimePeriodSched(Integer month, Integer year, boolean isActiveOnly, Integer timePeriodId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria ptpDc = DetachedCriteria.forClass(PayrollTimePeriod.class);
		ptpDc.setProjection(Projections.property(PayrollTimePeriod.FIELD.id.name()));
		if(!month.equals(-1)) {
			ptpDc.add(Restrictions.eq(PayrollTimePeriod.FIELD.month.name(), month));
		}
		ptpDc.add(Restrictions.eq(PayrollTimePeriod.FIELD.year.name(), year));
		if (isActiveOnly) {
			Criterion criterion = Restrictions.eq(PayrollTimePeriod.FIELD.active.name(), true);
			// Append saved inactive payroll time period
			if (timePeriodId != null) {
				criterion = Restrictions.or(criterion, Restrictions.and(
					Restrictions.eq(PayrollTimePeriod.FIELD.id.name(), timePeriodId),
					Restrictions.eq(PayrollTimePeriod.FIELD.active.name(), false)));
			}
			ptpDc.add(criterion);
		}
		dc.add(Subqueries.propertyIn(PayrollTimePeriodSchedule.Field.payrollTimePeriodId.name(), ptpDc));
		dc.addOrder(Order.asc(PayrollTimePeriodSchedule.Field.dateFrom.name()));
		return dc;
	}

	@Override
	public List<PayrollTimePeriodSchedule> getTimePeriodSchedules(
			Integer month, Integer year, boolean isActiveOnly, Integer timePeriodId) {
		return getAll(getTimePeriodSched(month, year, isActiveOnly, timePeriodId));
	}

	@Override
	public boolean isUsed(PayrollTimePeriodSchedule schedule) {
		DetachedCriteria dc = getDetachedCriteria();
		// Check time period schedule if used in time sheet form
		DetachedCriteria tsDc = DetachedCriteria.forClass(TimeSheet.class);
		tsDc.setProjection(Projections.property(TimeSheet.FIELD.payrollTimePeriodScheduleId.name()));
		tsDc.add(Restrictions.eq(TimeSheet.FIELD.payrollTimePeriodScheduleId.name(), schedule.getId()));
		// Check time sheet current status if not canceled in status
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		tsDc.add(Subqueries.propertyIn(TimeSheet.FIELD.formWorkflowId.name(), tsDc));
		dc.add(Subqueries.propertyIn(PayrollTimePeriodSchedule.Field.id.name(), tsDc));
		return getAll(dc).size() > 0;
	}

}
