package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.PayrollEmployeeSalaryDao;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.PayrollEmployeeSalary;
import eulap.eb.web.dto.webservice.EmployeeSalaryDTO;

/**
 * Implementation class of {@link PayrollEmployeeSalary}

 */
public class PayrollEmployeeSalaryDaoImpl extends BaseDao<PayrollEmployeeSalary> implements PayrollEmployeeSalaryDao{

	@Override
	protected Class<PayrollEmployeeSalary> getDomainClass() {
		return PayrollEmployeeSalary.class;
	}

	@Override
	public List<PayrollEmployeeSalary> getPayrollESDOrderByLastName(int payrollId) {
		return getPayrollESD(payrollId, false);
	}

	@Override
	public List<PayrollEmployeeSalary> getPayrollESD(int payrollId, boolean isFirstNameFirst) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollEmployeeSalary.FIELD.payrollId.name(), payrollId));

		dc.createAlias("employee", "e");
		if(isFirstNameFirst) {
			dc.addOrder(Order.asc("e.firstName"));
			dc.addOrder(Order.asc("e.middleName"));
			dc.addOrder(Order.asc("e.lastName"));
		} else {
			dc.addOrder(Order.asc("e.lastName"));
			dc.addOrder(Order.asc("e.firstName"));
			dc.addOrder(Order.asc("e.middleName"));
		}
		return getAll(dc);
	}

	@Override
	public EmployeeSalaryDTO getContributionsSummary(int employeeId,
			int payrollTimePeriodId) {
		String sql = "SELECT SUM(SSS), SUM(SSS_ER), SUM(SSS_EC), SUM(PHILHEALTH), SUM(PHILHEALTH_ER), "
				+ "SUM(PAGIBIG), SUM(PAGIBIG_ER) FROM PAYROLL_EMPLOYEE_SALARY PES "
				+ "INNER JOIN PAYROLL P ON P.PAYROLL_ID = PES.PAYROLL_ID " 
				+ "INNER JOIN PAYROLL_TIME_PERIOD PTP ON PTP.PAYROLL_TIME_PERIOD_ID = P.PAYROLL_TIME_PERIOD_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = P.FORM_WORKFLOW_ID " 
				+ "WHERE EMPLOYEE_ID = ? AND P.PAYROLL_TIME_PERIOD_ID = ? AND  FW.CURRENT_STATUS_ID != 4 "
				+ "GROUP BY EMPLOYEE_ID ";
		Session session = null;
		EmployeeSalaryDTO salaryDto = new EmployeeSalaryDTO();
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, employeeId);
			query.setParameter(1, payrollTimePeriodId);
			List<Object[]> queryResult = query.list();
			for (Object[] row : queryResult) {
				salaryDto.setSss((Double) row[0]);
				salaryDto.setSssEr((Double) row[1]);
				salaryDto.setSssEc((Double) row[2]);
				salaryDto.setPhilHealth((Double) row[3]);
				salaryDto.setPhilHealthEr((Double) row[4]);
				salaryDto.setPagibig((Double) row[5]);
				salaryDto.setPagibigEr((Double) row[6]);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return salaryDto;
	}

	@Override
	public Page<PayrollEmployeeSalary> getTotalSalaryPerBranch(Integer companyId, Integer divisionId, Integer employeeStatusId,
			Date dateFrom, Date dateTo, Boolean isOrderByLastName, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria payrollDc = DetachedCriteria.forClass(Payroll.class);
		payrollDc.setProjection(Projections.property(Payroll.FIELD.id.name()));
		payrollDc.add(Restrictions.between(Payroll.FIELD.date.name(), dateFrom, dateTo));
		if (divisionId > 0) {
			payrollDc.add(Restrictions.eq(Payroll.FIELD.divisionId.name(), divisionId));
		}
		payrollDc.add(Restrictions.eq(Payroll.FIELD.companyId.name(), companyId));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));

		payrollDc.add(Subqueries.propertyIn(Payroll.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Subqueries.propertyIn(PayrollEmployeeSalary.FIELD.payrollId.name(), payrollDc));

		if (employeeStatusId != null) {
			DetachedCriteria employeeDc = DetachedCriteria.forClass(Employee.class);
			employeeDc.setProjection(Projections.property(Employee.FIELD.id.name()));
			employeeDc.add(Restrictions.eq(Employee.FIELD.employeeStatusId.name(), employeeStatusId));
			dc.add(Subqueries.propertyIn(PayrollEmployeeSalary.FIELD.employeeId.name(), employeeDc));
		}

		if (isOrderByLastName) {
			dc.createAlias("employee", "employee").addOrder(Order.asc("employee.lastName"));
			dc.addOrder(Order.asc("employee.firstName"));
			dc.addOrder(Order.asc("employee.middleName"));
		}
		return getAll(dc, pageSetting);
	}

	@Override
	public double getPrevBalance(int employeeId, int payrollId) {
		String sql = "SELECT COALESCE(SUM(PES.NET_PAY), 0) AS PREV_BALANCE FROM PAYROLL_EMPLOYEE_SALARY PES "
			+ "INNER JOIN PAYROLL P ON P.PAYROLL_ID = PES.PAYROLL_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = P.FORM_WORKFLOW_ID "
			+ "WHERE PES.EMPLOYEE_ID = ? "
			+ (payrollId != 0 ? "AND P.PAYROLL_ID < ? " : "")
			+ "AND FW.IS_COMPLETE = 1 ";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, employeeId);
			if (payrollId != 0) {
				query.setParameter(1, payrollId);
			}
			return (Double) query.list().iterator().next();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public List<PayrollEmployeeSalary> getEmployeeSalaries(Integer companyId, Integer divisionId, Date dateFrom,
			Date dateTo) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria payrollDc = DetachedCriteria.forClass(Payroll.class);
		payrollDc.setProjection(Projections.property(Payroll.FIELD.id.name()));
		payrollDc.add(Restrictions.between(Payroll.FIELD.date.name(), dateFrom, dateTo));
		if(!divisionId.equals(-1)) {
			payrollDc.add(Restrictions.eq(Payroll.FIELD.divisionId.name(), divisionId));
		}
		payrollDc.add(Restrictions.eq(Payroll.FIELD.companyId.name(), companyId));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));

		payrollDc.add(Subqueries.propertyIn(Payroll.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Subqueries.propertyIn(PayrollEmployeeSalary.FIELD.payrollId.name(), payrollDc));

		DetachedCriteria employeeDc = DetachedCriteria.forClass(Employee.class);
		employeeDc.setProjection(Projections.property(Employee.FIELD.id.name()));
		employeeDc.addOrder(Order.asc(Employee.FIELD.lastName.name()));
		employeeDc.addOrder(Order.asc(Employee.FIELD.firstName.name()));
		employeeDc.addOrder(Order.asc(Employee.FIELD.middleName.name()));
		dc.add(Subqueries.propertyIn(PayrollEmployeeSalary.FIELD.employeeId.name(), employeeDc));
		return getAll(dc);
	}

	@Override
	public PayrollEmployeeSalary getByEmplAndPayrollId(Integer employeeId, Integer payrollId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PayrollEmployeeSalary.FIELD.employeeId.name(), employeeId));
		dc.add(Restrictions.eq(PayrollEmployeeSalary.FIELD.payrollId.name(), payrollId));
		return get(dc);
	}
}
