package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.PayrollEmployeeTimeSheetDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.PayrollEmployeeTimeSheet;

/**
 * Implementation class of {@link PayrollEmployeeTimeSheetDao}

 */
public class PayrollEmployeeTimeSheetDaoImpl extends BaseDao<PayrollEmployeeTimeSheet> implements PayrollEmployeeTimeSheetDao{

	@Override
	protected Class<PayrollEmployeeTimeSheet> getDomainClass() {
		return PayrollEmployeeTimeSheet.class;
	}

	@Override
	public List<PayrollEmployeeTimeSheet> getByPayroll(int payrollId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.payrollId.name(), payrollId));

		dc.createAlias("employee", "e");
		dc.addOrder(Order.asc("e.lastName"));
		dc.addOrder(Order.asc("e.firstName"));
		dc.addOrder(Order.asc("e.middleName"));
		return getAll(dc);
	}

	@Override
	public List<PayrollEmployeeTimeSheet> getByTimeSheet(int timeSheetId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.timeSheetId.name(), timeSheetId));

		dc.createAlias("employee", "e");
		dc.addOrder(Order.asc("e.lastName"));
		dc.addOrder(Order.asc("e.firstName"));
		dc.addOrder(Order.asc("e.middleName"));
		dc.addOrder(Order.asc(PayrollEmployeeTimeSheet.FIELD.date.name()));
		return getAll(dc);
	}

	@Override
	public List<PayrollEmployeeTimeSheet> getByEmployeeIdAndPayrollId(int employeeId, int payrollId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.employeeId.name(), employeeId));
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.payrollId.name(), payrollId));
		dc.addOrder(Order.asc(PayrollEmployeeTimeSheet.FIELD.date.name()));
		return getAll(dc);
	}

	@Override
	public List<PayrollEmployeeTimeSheet> getByEmployeeIdAndTimeSheetId(int employeeId, int timeSheetId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.employeeId.name(), employeeId));
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.timeSheetId.name(), timeSheetId));
		dc.addOrder(Order.asc(PayrollEmployeeTimeSheet.FIELD.date.name()));
		return getAll(dc);
	}

	@Override
	public List<PayrollEmployeeTimeSheet> getByEmployeeAndSchedule(
			int employeeId, int scheduleId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.employeeId.name(), employeeId));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		DetachedCriteria payrollDc = DetachedCriteria.forClass(Payroll.class);
		payrollDc.setProjection(Projections.property(Payroll.FIELD.id.name()));
		payrollDc.add(Restrictions.eq(Payroll.FIELD.payrollTimePeriodScheduleId.name(), scheduleId));
		payrollDc.add(Subqueries.propertyIn(Payroll.FIELD.formWorkflowId.name(), fwDc));

		dc.add(Subqueries.propertyIn(PayrollEmployeeTimeSheet.FIELD.payrollId.name(), payrollDc));
		return getAll(dc);
	}

	@Override
	public PayrollEmployeeTimeSheet getByEmpTSAndDate(int employeeId, int timeSheetId, Date date) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.employeeId.name(), employeeId));
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.timeSheetId.name(), timeSheetId));
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.date.name(), date));
		dc.addOrder(Order.asc(PayrollEmployeeTimeSheet.FIELD.date.name()));
		return get(dc);
	}

	@Override
	public PayrollEmployeeTimeSheet getByEmployeeId(Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.employeeId.name(), employeeId));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		DetachedCriteria payrollDc = DetachedCriteria.forClass(Payroll.class);
		payrollDc.setProjection(Projections.property(Payroll.FIELD.id.name()));
		payrollDc.add(Subqueries.propertyIn(Payroll.FIELD.formWorkflowId.name(), fwDc));

		dc.add(Subqueries.propertyIn(PayrollEmployeeTimeSheet.FIELD.payrollId.name(), payrollDc));
		dc.addOrder(Order.desc(PayrollEmployeeTimeSheet.FIELD.date.name()));
		return get(dc);
	}
}
