package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.domain.hibernate.DailyShiftSchedule;
import eulap.eb.domain.hibernate.DailyShiftScheduleLine;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.domain.hibernate.MonthlyShiftSchedule;
import eulap.eb.domain.hibernate.MonthlyShiftScheduleLine;
import eulap.eb.domain.hibernate.PayrollEmployeeTimeSheet;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FrequencyReportDto;

/**
 * DAO implementation of {@link EmployeeDao}

 *
 */
public class EmployeeDaoImpl extends BaseDao<Employee> implements EmployeeDao{

	@Override
	protected Class<Employee> getDomainClass() {
		return Employee.class;
	}

	@Override
	public Page<Employee> searchEmployees(String name, String position, int divisionId, User user,
			SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = null;
		addUserCompany(dc, user);
		if(name != null && !name.trim().isEmpty()) {
			criterion = Restrictions.or(Restrictions.like(Employee.FIELD.firstName.name(), "%"+name+"%"),
					Restrictions.like(Employee.FIELD.middleName.name(), "%"+name+"%"));
			dc.add(Restrictions.or(criterion, Restrictions.like(Employee.FIELD.lastName.name(), "%"+name+"%")));
		}
		if(position != null && !position.trim().isEmpty()) {
			DetachedCriteria positionDc = DetachedCriteria.forClass(Position.class);
			positionDc.setProjection(Projections.property(Position.FIELD.id.name()));
			positionDc.add(Restrictions.like(Position.FIELD.name.name(),"%"+ position.trim()+"%"));
			dc.add(Subqueries.propertyIn(Employee.FIELD.positionId.name(), positionDc));
		}
		if (divisionId != -1) {
			dc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), divisionId));
		}
		if(status != null) {
			dc = DaoUtil.setSearchStatus(dc, Employee.FIELD.active.name(), status);
		}
		dc.addOrder(Order.asc(Employee.FIELD.lastName.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueBiometricId(Employee employee) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Employee.FIELD.biometricId.name(), employee.getBiometricId()));
		if (employee.getCompanyId() != null) {
			dc.add(Restrictions.eq(Employee.FIELD.companyId.name(), employee.getCompanyId()));
		}
		if (employee.getDivisionId() != null) {
			dc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), employee.getDivisionId()));
		}
		if (employee.getId() != 0) {
			dc.add(Restrictions.ne(Employee.FIELD.id.name(), employee.getId()));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public Employee getEmployeeByBiometricIdCompAndEmpType(Integer biometricId, Integer companyId,
			Integer employeeTypeId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (biometricId != null) {
			dc.add(Restrictions.eq(Employee.FIELD.biometricId.name(), biometricId));
		}
		if(companyId != null && companyId != 0){
			dc.add(Restrictions.eq(Employee.FIELD.companyId.name(), companyId));
		}
		if (divisionId != null) {
			dc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), divisionId));
		} 
		if(employeeTypeId != null && employeeTypeId != 0){
			dc.add(Restrictions.eq(Employee.FIELD.employeeTypeId.name(), employeeTypeId));
		}
		dc.add(Restrictions.eq(Employee.FIELD.active.name(), true));
		return get(dc);
	}

	@Override
	public Employee getEmployeeByNo(String employeeNo, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Employee.FIELD.employeeNo.name(), employeeNo));
		dc.add(Restrictions.eq(Employee.FIELD.companyId.name(), companyId));
		return get(dc);
	}

	@Override
	public boolean isUniqueEmployeeNo(Employee employee) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Employee.FIELD.employeeNo.name(), employee.getEmployeeNo().trim()));
		if (employee.getCompanyId() != null) {
			dc.add(Restrictions.eq(Employee.FIELD.companyId.name(), employee.getCompanyId()));
		}
		if (employee.getDivisionId() != null) {
			dc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), employee.getDivisionId()));
		}
		if(employee.getId() != 0){
			dc.add(Restrictions.ne(Employee.FIELD.id.name(), employee.getId()));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public List<Employee> getEmployees(Integer companyId, Integer divisionId, Integer employeeTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null && companyId != 0){
			dc.add(Restrictions.eq(Employee.FIELD.companyId.name(), companyId));
		}
		if(divisionId != null && divisionId != 0){
			dc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), divisionId));
		}
		if(employeeTypeId != null && employeeTypeId != 0){
			dc.add(Restrictions.eq(Employee.FIELD.employeeTypeId.name(), employeeTypeId));
		}
		dc.add(Restrictions.eq(Employee.FIELD.active.name(), true));

		dc.addOrder(Order.asc(Employee.FIELD.lastName.name()));
		dc.addOrder(Order.asc(Employee.FIELD.firstName.name()));
		dc.addOrder(Order.asc(Employee.FIELD.middleName.name()));
		return getAll(dc);
	}

	public List<Employee> getEmployeesByPayrollId(int payrollId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria payrollETSDc = DetachedCriteria.forClass(PayrollEmployeeTimeSheet.class);
		payrollETSDc.setProjection(Projections.property(PayrollEmployeeTimeSheet.FIELD.employeeId.name()));
		payrollETSDc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.payrollId.name(), payrollId));
		dc.add(Subqueries.propertyIn(Employee.FIELD.id.name(), payrollETSDc));
		dc.addOrder(Order.asc(Employee.FIELD.lastName.name()));
		dc.addOrder(Order.asc(Employee.FIELD.firstName.name()));
		dc.addOrder(Order.asc(Employee.FIELD.middleName.name()));

		return getAll(dc);
	}

	@Override
	public List<Employee> getEmployeesByName(Integer companyId, String name) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null){
			dc.add(Restrictions.eq(Employee.FIELD.companyId.name(), companyId));
		}
		if(name != null){
			LogicalExpression orName = Restrictions.or(Restrictions.like(Employee.FIELD.lastName.name(), name + "%"), 
					Restrictions.like(Employee.FIELD.middleName.name(), name + "%"));

			dc.add(Restrictions.or(Restrictions.like(Employee.FIELD.firstName.name(), name + "%"), orName));
		}
		dc.add(Restrictions.eq(Employee.FIELD.active.name(), true));
		dc.addOrder(Order.asc(Employee.FIELD.lastName.name()));
		dc.addOrder(Order.asc(Employee.FIELD.middleName.name()));
		dc.addOrder(Order.asc(Employee.FIELD.firstName.name()));
		return getAll(dc);
	}

	@Override
	public List<Employee> getEmployeeByCompanyAndName(Integer companyId, String name, boolean isFirstNameFirst) {
		String sql = "SELECT EMPLOYEE_ID, LAST_NAME, FIRST_NAME, MIDDLE_NAME FROM EMPLOYEE WHERE EMPLOYEE_ID = ( "
						+ "SELECT TBL.EMPLOYEE_ID FROM ( ";
		if(!isFirstNameFirst) {
			sql += "SELECT EMPLOYEE_ID, CONCAT(LAST_NAME, ', ', FIRST_NAME, ' ', MIDDLE_NAME) AS NAME ";
		} else {
			sql += "SELECT EMPLOYEE_ID, CONCAT(FIRST_NAME,REPLACE(CONCAT(' ',LEFT(MIDDLE_NAME,1),'. '),' .',''),LAST_NAME) AS NAME ";
		}
		sql += "FROM EMPLOYEE) TBL "
				+ "INNER JOIN EMPLOYEE E ON E.EMPLOYEE_ID = TBL.EMPLOYEE_ID "
				+ "WHERE E.COMPANY_ID = ? "
				+ "AND E.ACTIVE = 1 "
				+ "AND TBL.NAME = ?) ";
		return (List<Employee>) get(sql, new EmployeeHandler(companyId, name));
	}

	private static class EmployeeHandler implements QueryResultHandler<Employee> {
		private Integer companyId;
		private String name;

		private EmployeeHandler(Integer companyId, String name) {
			this.companyId = companyId;
			this.name = name;
		}

		@Override
		public List<Employee> convert(List<Object[]> queryResult) {
			List<Employee> employees = new ArrayList<>();
			Employee employee = null;
			for (Object[] rowResult : queryResult) {
				int columnNo = 0;
				employee = new Employee();
				employee.setId((Integer) rowResult[columnNo++]);
				employee.setLastName((String) rowResult[columnNo++]);
				employee.setFirstName((String) rowResult[columnNo++]);
				employee.setMiddleName((String) rowResult[columnNo++]);
				employees.add(employee);
			}
			return employees;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			query.setParameter(++index, name);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("EMPLOYEE_ID", Hibernate.INTEGER);
			query.addScalar("LAST_NAME", Hibernate.STRING);
			query.addScalar("FIRST_NAME", Hibernate.STRING);
			query.addScalar("MIDDLE_NAME", Hibernate.STRING);
		}
	}

	@Override
	public List<Employee> getNoMonthlySchedule(Integer companyId, Integer scheduleId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Employee.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(Employee.FIELD.active.name(), true));

		DetachedCriteria mssDc = DetachedCriteria.forClass(MonthlyShiftSchedule.class);
		mssDc.setProjection(Projections.property(MonthlyShiftSchedule.FIELD.id.name()));
		mssDc.add(Restrictions.eq(MonthlyShiftSchedule.FIELD.payrollTimePeriodScheduleId.name(), scheduleId));

		DetachedCriteria msslDc = DetachedCriteria.forClass(MonthlyShiftScheduleLine.class);
		msslDc.setProjection(Projections.property(MonthlyShiftScheduleLine.FIELD.employeeId.name()));
		msslDc.add(Subqueries.propertyIn(MonthlyShiftScheduleLine.FIELD.monthlyShiftScheduleId.name(), mssDc));
		dc.add(Subqueries.propertyNotIn(Employee.FIELD.id.name(), msslDc));
		return getAll(dc);
	}

	@Override
	public List<Employee> getEmployeesByName(Integer companyId, Integer divisionId, User user, String name) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		if(companyId != null){
			dc.add(Restrictions.eq(Employee.FIELD.companyId.name(), companyId));
		}
		if(divisionId != null){
			dc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), divisionId));
		}
		if(name != null){
			LogicalExpression orName = Restrictions.or(Restrictions.like(Employee.FIELD.lastName.name(), name + "%"),
					Restrictions.like(Employee.FIELD.middleName.name(), name + "%"));
			dc.add(Restrictions.or(Restrictions.like(Employee.FIELD.firstName.name(), name + "%"), orName));
		}

		DetachedCriteria epDc = DetachedCriteria.forClass(EmployeeProfile.class);
		epDc.setProjection(Projections.property(EmployeeProfile.FIELD.employeeId.name()));
		dc.add(Subqueries.propertyIn(Employee.FIELD.id.name(), epDc));
		dc.add(Restrictions.eq(Employee.FIELD.active.name(), true));
		dc.addOrder(Order.asc(Employee.FIELD.lastName.name()));
		dc.addOrder(Order.asc(Employee.FIELD.middleName.name()));
		dc.addOrder(Order.asc(Employee.FIELD.firstName.name()));
		return getAll(dc);
	}

	@Override
	public List<FrequencyReportDto> getAbsentEmployees(Integer companyId, Integer divisionId,
			Date date, Integer status, int dayOfWeek, boolean isFirstNameFirst, boolean isOrderByLastName) {
		String sql = "SELECT E.EMPLOYEE_NO, CONCAT(E.LAST_NAME, ', ', E.FIRST_NAME, ' ', E.MIDDLE_NAME) AS EMPLOYEE_NAME, ";
		if (isFirstNameFirst) {
			sql = "SELECT EMPLOYEE_NO, CONCAT(E.FIRST_NAME, REPLACE(CONCAT(' ', LEFT(E.MIDDLE_NAME, 1), '. '), ' .', ''), E.LAST_NAME) AS EMPLOYEE_NAME, ";
		}
		sql += "P.NAME AS POSITION_NAME, PET.DATE "
			+ "FROM PAYROLL_EMPLOYEE_TIMESHEET PET "
			+ "INNER JOIN EMPLOYEE E ON E.EMPLOYEE_ID = PET.EMPLOYEE_ID "
			+ "INNER JOIN POSITION P ON P.POSITION_ID = E.POSITION_ID "
			+ "INNER JOIN TIME_SHEET TS ON TS.TIME_SHEET_ID = PET.TIME_SHEET_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = TS.FORM_WORKFLOW_ID "
			+ "INNER JOIN DAILY_SHIFT_SCHEDULE_LINE DSSL ON DSSL.EMPLOYEE_ID = E.EMPLOYEE_ID "
			+ "WHERE FW.IS_COMPLETE = 1 "
			+ "AND PET.DATE = DSSL.DATE "
			+ "AND PET.DATE = ? "
			+ "AND PET.HOURS_WORKED = 0 "
			+ "AND PET.EMPLOYEE_ID NOT IN (SELECT EMPLOYEE_ID FROM (SELECT EMPLOYEE_ID FROM EMPLOYEE_REQUEST ER "
			+ "INNER JOIN LEAVE_DETAIL LD ON LD.EMPLOYEE_REQUEST_ID = ER.EMPLOYEE_REQUEST_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ER.FORM_WORKFLOW_ID "
			+ "WHERE FW.IS_COMPLETE = 1 AND ? BETWEEN LD.DATE_FROM AND LD.DATE_TO) AS TBL GROUP BY EMPLOYEE_ID) "
			+ "AND WEEKDAY(PET.DATE) NOT IN (SELECT IF((ESDO.DAY_OF_THE_WEEK - 2) < 0, 6, (ESDO.DAY_OF_THE_WEEK - 2)) AS WEEK_DAY "
			+ "FROM EMPLOYEE_SHIFT_DAY_OFF ESDO WHERE ESDO.EMPLOYEE_SHIFT_ID = DSSL.EMPLOYEE_SHIFT_ID) "
			+ "AND E.COMPANY_ID = ? "
			+ "AND E.DIVISION_ID = ? "
			+ (status >= 0 ? " AND E.ACTIVE = ? " : "");
		if (isOrderByLastName) {
			sql += "ORDER BY DATE, NAME";
		} else {
			sql += "ORDER BY DATE, EMPLOYEE_NO";
		}
		return (List<FrequencyReportDto>) get(sql, new AbsentEmployeesHandler(companyId, divisionId, date, status, dayOfWeek));
	}

	private static class AbsentEmployeesHandler implements QueryResultHandler<FrequencyReportDto> {
		private Date date;
		private Integer status;
		private Integer companyId;
		private Integer divisionId;

		private AbsentEmployeesHandler(Integer companyId, Integer divisionId, Date date, Integer status, int dayOfWeek) {
			this.date = date;
			this.status = status;
			this.companyId = companyId;
			this.divisionId = divisionId;
		}

		@Override
		public List<FrequencyReportDto> convert(List<Object[]> queryResult) {
			List<FrequencyReportDto> frequencyReportDtos = new ArrayList<>();
			FrequencyReportDto frequencyReportDto = null;
			for (Object[] rowResult : queryResult) {
				int columnNo = 0;
				frequencyReportDto = new FrequencyReportDto();
				frequencyReportDto.setStrEmployeeNumber((String) rowResult[columnNo++]);
				frequencyReportDto.setName((String) rowResult[columnNo++]);
				frequencyReportDto.setPosition((String) rowResult[columnNo++]);
				frequencyReportDto.setDate((Date) rowResult[columnNo++]);
				frequencyReportDtos.add(frequencyReportDto);
			}
			return frequencyReportDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, date);
			query.setParameter(++index, date);
			query.setParameter(++index, companyId);
			query.setParameter(++index, divisionId);
			if (status >= 0) {
				query.setParameter(++index, status);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("EMPLOYEE_NO", Hibernate.STRING);
			query.addScalar("EMPLOYEE_NAME", Hibernate.STRING);
			query.addScalar("POSITION_NAME", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
		}
	}

	@Override
	public List<Employee> getEmployeesByTimeSheetId(int timeSheetId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria payrollETSDc = DetachedCriteria.forClass(PayrollEmployeeTimeSheet.class);
		payrollETSDc.setProjection(Projections.property(PayrollEmployeeTimeSheet.FIELD.employeeId.name()));
		payrollETSDc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.timeSheetId.name(), timeSheetId));
		dc.add(Subqueries.propertyIn(Employee.FIELD.id.name(), payrollETSDc));
		dc.addOrder(Order.asc(Employee.FIELD.lastName.name()));
		dc.addOrder(Order.asc(Employee.FIELD.firstName.name()));
		dc.addOrder(Order.asc(Employee.FIELD.middleName.name()));
		dc.add(Restrictions.eq(Employee.FIELD.active.name(), true));
		return getAll(dc);
	}

	@Override
	public Boolean isUniqueEmployeeName(Integer companyId, String completeName) {
		String sql = "SELECT EMPLOYEE_ID, COMPLETE_NAME FROM("
				+ "SELECT E.EMPLOYEE_ID, REPLACE(CONCAT(E.FIRST_NAME, ' ' ,E.MIDDLE_NAME, ' ', E.LAST_NAME), '  ', ' ') AS COMPLETE_NAME FROM EMPLOYEE E "
				+ "WHERE E.COMPANY_ID = ?) AS TBL "
				+ "WHERE COMPLETE_NAME = ?";
		List<Employee> employees = (List<Employee>) get(sql, new UniqueEmployeeNameHandler(companyId, completeName));
		return employees.isEmpty();
	}

	private static class UniqueEmployeeNameHandler implements QueryResultHandler<Employee>{

		private Integer companyId;
		private String employeeCompleteName;
		private UniqueEmployeeNameHandler(Integer companyId, String employeeCompleteName) {
			this.companyId = companyId;
			this.employeeCompleteName = employeeCompleteName;
		}

		@Override
		public List<Employee> convert(List<Object[]> queryResult) {
			List<Employee> employees = new ArrayList<>();
			Employee employee = null;
			for(Object[] obj : queryResult) {
				int index = 0;
				employee = new Employee();
				employee.setId((int) obj[index++]);
				employee.setCompleteName((String) obj[index]);
				employees.add(employee);
			}
			return employees;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index++, companyId);
			query.setParameter(index, employeeCompleteName);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("EMPLOYEE_ID", Hibernate.INTEGER);
			query.addScalar("COMPLETE_NAME", Hibernate.STRING);
		}
	}

	@Override
	public List<Employee> getEmployeeBySchedule(Integer companyId, Integer divisionId,
			Integer scheduleId, boolean hasSchedule) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Employee.FIELD.companyId.name(), companyId));
		if (divisionId != null) {
			dc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), divisionId));
		}
		dc.add(Restrictions.eq(Employee.FIELD.active.name(), true));

		DetachedCriteria dssDc = DetachedCriteria.forClass(DailyShiftSchedule.class);
		dssDc.setProjection(Projections.property(DailyShiftSchedule.FIELD.id.name()));
		dssDc.add(Restrictions.eq(DailyShiftSchedule.FIELD.payrollTimePeriodScheduleId.name(), scheduleId));

		DetachedCriteria dsslDc = DetachedCriteria.forClass(DailyShiftScheduleLine.class);
		dsslDc.setProjection(Projections.property(DailyShiftScheduleLine.FIELD.employeeId.name()));
		dsslDc.add(Subqueries.propertyIn(DailyShiftScheduleLine.FIELD.dailyShiftScheduleId.name(), dssDc));
		if (hasSchedule) {
			dc.add(Subqueries.propertyIn(Employee.FIELD.id.name(), dsslDc));
		} else {
			dc.add(Subqueries.propertyNotIn(Employee.FIELD.id.name(), dsslDc));
		}
		return getAll(dc);
	}

}
