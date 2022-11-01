package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.eb.dao.EmployeeDeductionDao;
import eulap.eb.domain.hibernate.EmployeeDeduction;

/**
 * Implementation class of {@link EmployeeDeductionDao}

 *
 */
public class EmployeeDeductionDaoImpl extends BaseDao<EmployeeDeduction> implements EmployeeDeductionDao{

	@Override
	protected Class<EmployeeDeduction> getDomainClass() {
		return EmployeeDeduction.class;
	}

	@Override
	public List<EmployeeDeduction> initEmployeeDeductions(Integer employeeId, Date dateFrom, Date dateTo) {
		List<EmployeeDeduction> employeeDeductions = new ArrayList<>();
		String sql = "SELECT EMPLOYEE_ID, DEDUCTION_TYPE_ID, NAME, SUM(AMOUNT) AS AMOUNT, SUM(PAID_AMOUNT) AS PAID_AMOUNT, FROM_DEDUCTION_FORM FROM ( "
			+ "SELECT FD.EMPLOYEE_ID, FD.DEDUCTION_TYPE_ID, DT.NAME, FDL.AMOUNT, 0.00 AS PAID_AMOUNT, 1 AS FROM_DEDUCTION_FORM "
			+ "FROM FORM_DEDUCTION_LINE FDL "
			+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = FDL.EB_OBJECT_ID "
			+ "INNER JOIN FORM_DEDUCTION FD ON FD.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
			+ "INNER JOIN DEDUCTION_TYPE DT ON DT.DEDUCTION_TYPE_ID = FD.DEDUCTION_TYPE_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = FD.FORM_WORKFLOW_ID " 
			+ "WHERE FD.EMPLOYEE_ID = ? "
			+ "AND FDL.DATE BETWEEN ? AND ? " 
			+ "AND FW.IS_COMPLETE = 1 "
			+ ") AS TBL GROUP BY DEDUCTION_TYPE_ID";
		Session session = null;
		EmployeeDeduction employeeDeduction = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, employeeId);
			query.setParameter(1, DateUtil.formatToSqlDate(dateFrom));
			query.setParameter(2, DateUtil.formatToSqlDate(dateTo));

			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					employeeDeduction = new EmployeeDeduction();
					employeeDeduction.setEmployeeId((Integer) row[0]);
					employeeDeduction.setDeductionTypeId((Integer) row[1]);
					employeeDeduction.setDeductionTypeName((String) row[2]);
					employeeDeduction.setAmount((Double) row[3]);
					employeeDeduction.setPaidAmount(0.0);
					employeeDeduction.setFromDeductionForm(true);
					employeeDeductions.add(employeeDeduction);
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return employeeDeductions;
	}

	@Override
	public List<EmployeeDeduction> getEmployeeDeductions(Integer payrollId, Integer employeeId) {
		return getAll(getEmplDeductionDc(payrollId, employeeId));
	}

	@Override
	public List<EmployeeDeduction> getEmployeeDeductions(Integer payrollId, Integer employeeId,
			Integer deductionTypeId) {
		DetachedCriteria dc = getEmplDeductionDc(payrollId, employeeId);
		dc.add(Restrictions.eq(EmployeeDeduction.FIELD.deductionTypeId.name(), deductionTypeId));
		return getAll(dc);
	}

	private DetachedCriteria getEmplDeductionDc(Integer payrollId, Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeDeduction.FIELD.employeeId.name(), employeeId));
		dc.add(Restrictions.eq(EmployeeDeduction.FIELD.payrollId.name(), payrollId));
		dc.add(Restrictions.eq(EmployeeDeduction.FIELD.active.name(), true));
		return dc;
	}

	@Override
	public List<EmployeeDeduction> getActiveEmployeeDeductions(Integer payrollId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeDeduction.FIELD.payrollId.name(), payrollId));
		dc.add(Restrictions.eq(EmployeeDeduction.FIELD.active.name(), true));
		return getAll(dc);
	}

}
