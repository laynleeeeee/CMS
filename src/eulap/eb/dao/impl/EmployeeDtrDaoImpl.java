package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.EmployeeDtrDao;
import eulap.eb.domain.hibernate.EmployeeDtr;
import eulap.eb.domain.hibernate.PayrollEmployeeTimeSheet;
import eulap.eb.web.dto.DTRDailyShiftScheduleDto;
import eulap.eb.web.dto.DTRMonthlyScheduleDto;
import eulap.eb.web.dto.EmployeeAttendanceReportDto;

/**
 * Implementing class of {@link EmployeeDtrDao}

 *
 */
public class EmployeeDtrDaoImpl extends BaseDao<EmployeeDtr> implements EmployeeDtrDao {

	@Override
	protected Class<EmployeeDtr> getDomainClass() {
		return EmployeeDtr.class;
	}

	@Override
	public List<String> getLogTimes(int employeeId, Date date) {
		List<String> logTimes = new ArrayList<>();
		String sql = "SELECT TIME_FORMAT(LOG_TIME, '%H:%i') "
				+ "FROM EMPLOYEE_DTR DTR "
				+ "INNER JOIN PAYROLL_EMPLOYEE_TIMESHEET PET ON PET.PAYROLL_EMPLOYEE_TIMESHEET_ID = DTR.PAYROLL_EMPLOYEE_TIMESHEET_ID "
				+ "INNER JOIN TIME_SHEET TS ON TS.TIME_SHEET_ID = PET.TIME_SHEET_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON TS.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
				+ "WHERE DTR.EMPLOYEE_ID = ? "
				+ "AND DATE(DTR.LOG_TIME) = ? "
				+ "AND DTR.ACTIVE = 1 "
				+ "AND FW.CURRENT_STATUS_ID != 4";

		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, employeeId);
			query.setParameter(1, DateUtil.formatToSqlDate(date));
			List<?> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object obj : list) {
					if (obj instanceof String) {
						logTimes.add((String) obj);
					}
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return logTimes;
	}

	@Override
	public Page<EmployeeAttendanceReportDto> getEmployeeAttendanceReport(Integer companyId, Integer divisionId,
			Date dateFrom, Date dateTo, Boolean isOrderByLastName, PageSetting pageSetting) {
		String sql = "SELECT E.EMPLOYEE_ID, "
				+ "CONCAT(E.LAST_NAME, ', ', E.FIRST_NAME, ' ', E.MIDDLE_NAME) AS EMPLOYEE_NAME, "
				+ "PES.DATE AS TIMESTAMP, E.EMPLOYEE_NO, PES.HOURS_WORKED, PES.ADJUSTMENT "
				+ "FROM EMPLOYEE E "
				+ "INNER JOIN PAYROLL_EMPLOYEE_TIMESHEET PES ON PES.EMPLOYEE_ID = E.EMPLOYEE_ID "
				+ "INNER JOIN TIME_SHEET TS ON TS.TIME_SHEET_ID = PES.TIME_SHEET_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = TS.FORM_WORKFLOW_ID "
				+ "WHERE E.COMPANY_ID = ? "
				+ "AND E.DIVISION_ID = ? "
				+ "AND DATE(PES.DATE) BETWEEN ? AND ? "
				+ "AND FW.IS_COMPLETE = 1 ";
		if(isOrderByLastName) {
				sql += "ORDER BY E.LAST_NAME, E.FIRST_NAME, E.MIDDLE_NAME, PES.DATE";
		} else {
				sql += "ORDER BY EMPLOYEE_NAME, PES.DATE";
		}
		EmployeeAttendanceReportHandler handler = new EmployeeAttendanceReportHandler(
				companyId,  divisionId, dateFrom, dateTo);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class EmployeeAttendanceReportHandler implements QueryResultHandler<EmployeeAttendanceReportDto> {
		private Integer companyId;
		private Integer divisionId;
		private Date dateFrom;
		private Date dateTo;

		private EmployeeAttendanceReportHandler (Integer companyId, Integer divisionId,
				Date dateFrom, Date dateTo) {
				this.companyId = companyId;
				this.divisionId = divisionId;
				this.dateFrom = dateFrom;
				this.dateTo = dateTo;
		}

		@Override
		public List<EmployeeAttendanceReportDto> convert(List<Object[]> queryResult) {
			List<EmployeeAttendanceReportDto> empAttendances = new ArrayList<>();
			for (Object obj : queryResult) {
				Object[] rowResult = (Object[]) obj;
				int colNum = 0;
				empAttendances.add(EmployeeAttendanceReportDto.getInstanceOf((Integer) rowResult[colNum++], 
						(String) rowResult[colNum++], (Date) rowResult[colNum++], (String) rowResult[colNum++],
						(Double) rowResult[colNum++], (Double) rowResult[colNum++]));
			}
			return empAttendances;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			query.setParameter(++index, divisionId);
			query.setParameter(++index, dateFrom);
			query.setParameter(++index, dateTo);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("EMPLOYEE_ID", Hibernate.INTEGER);
			query.addScalar("EMPLOYEE_NAME", Hibernate.STRING);
			query.addScalar("TIMESTAMP", Hibernate.TIMESTAMP);
			query.addScalar("EMPLOYEE_NO", Hibernate.STRING);
			query.addScalar("HOURS_WORKED", Hibernate.DOUBLE);
			query.addScalar("ADJUSTMENT", Hibernate.DOUBLE);
		}
	}

	private List<EmployeeDtr> getEmployeeDtrs(Integer companyId, Integer divisionId, Date dateFrom, 
			Date dateTo, Boolean isActive, Integer timeSheetId, Integer employeeId) {
		List<EmployeeDtr> employeeDtrs = new ArrayList<>();
		String sql = "SELECT EDTR.EMPLOYEE_DTR_ID, EDTR.EMPLOYEE_ID, EDTR.LOG_TIME, EDTR.IS_SYNCHRONIZE, EDTR.ACTIVE FROM EMPLOYEE_DTR EDTR "
			+ "INNER JOIN EMPLOYEE E ON E.EMPLOYEE_ID = EDTR.EMPLOYEE_ID "
			+ "INNER JOIN PAYROLL_EMPLOYEE_TIMESHEET PET ON PET.PAYROLL_EMPLOYEE_TIMESHEET_ID = EDTR.PAYROLL_EMPLOYEE_TIMESHEET_ID "
			+ "INNER JOIN TIME_SHEET TS ON TS.TIME_SHEET_ID = PET.TIME_SHEET_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON TS.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
			+ "WHERE E.COMPANY_ID = ? "
			+ "AND EDTR.ACTIVE = 1 "
			+ "AND (DATE(EDTR.LOG_TIME) BETWEEN ? AND ?) "
			+ "AND FW.CURRENT_STATUS_ID != 4 ";
			if(isActive != null){
				sql += "AND E.ACTIVE = ? ";
			}
			if(timeSheetId != null && timeSheetId != -1) {
				sql += "AND TS.TIME_SHEET_ID = ? ";
			}
			if(employeeId != null && employeeId != -1) {
				sql += "AND EDTR.EMPLOYEE_ID = ? ";
			}
			if(divisionId != null && divisionId != -1){
				sql += "AND E.DIVISION_ID = ? ";
			}
			sql += "ORDER BY EDTR.LOG_TIME";
		Session session = null;
		EmployeeDtr employeeDtr = null;
		try {
			session = getSession();
			int index = 0;
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(index, companyId);
			query.setParameter(++index, DateUtil.formatToSqlDate(dateFrom));
			query.setParameter(++index, DateUtil.formatToSqlDate(dateTo));
			if(isActive != null){
				query.setParameter(++index, isActive);
			}
			if(timeSheetId != null && timeSheetId != -1){
				query.setParameter(++index, timeSheetId);
			}
			if(employeeId != null && employeeId != -1){
				query.setParameter(++index, employeeId);
			}
			if(divisionId != null && divisionId != -1){
				query.setParameter(++index, divisionId);
			}
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					employeeDtr = new EmployeeDtr();
					employeeDtr.setId((Integer) row[0]);
					employeeDtr.setEmployeeId((Integer) row[1]);
					employeeDtr.setLogTime((Date) row[2]);
					employeeDtr.setIsSynchronize((Boolean) row[3]);
					employeeDtr.setActive((Boolean) row[4]);
					employeeDtrs.add(employeeDtr);
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return employeeDtrs;
	}

	@Override
	public List<DTRDailyShiftScheduleDto> getLatestDailyShifts(Date updatedDate) {
		String sql = "SELECT DSSL.EMPLOYEE_ID, ES.EMPLOYEE_SHIFT_ID, DATE, STR_TO_DATE(CONCAT(DATE, ' ', "
				+ "ES.FIRST_HALF_SHIFT_START),'%Y-%m-%d %H:%i') AS START_SHIFT, "
				+ "STR_TO_DATE(CONCAT(DATE, ' ', ES.SECOND_HALF_SHIFT_END),'%Y-%m-%d %H:%i') AS END_SHIFT, "
				+ "DSS.UPDATED_DATE FROM DAILY_SHIFT_SCHEDULE_LINE DSSL "
				+ "INNER JOIN EMPLOYEE_SHIFT ES ON DSSL.EMPLOYEE_SHIFT_ID = ES.EMPLOYEE_SHIFT_ID "
				+ "INNER JOIN DAILY_SHIFT_SCHEDULE DSS ON DSS.DAILY_SHIFT_SCHEDULE_ID = DSSL.DAILY_SHIFT_SCHEDULE_ID "
				+ "INNER JOIN EMPLOYEE E ON E.EMPLOYEE_ID = DSSL.EMPLOYEE_ID "
				+ "INNER JOIN EMPLOYEE_PROFILE EP ON EP.EMPLOYEE_ID = E.EMPLOYEE_ID  "
				+ (updatedDate != null ? "WHERE AND DSS.UPDATED_DATE > ?" : "");
		return (List<DTRDailyShiftScheduleDto>) get(sql, new DTRDailyShiftScheduleHandler(updatedDate));
	}

	private static class DTRDailyShiftScheduleHandler implements QueryResultHandler<DTRDailyShiftScheduleDto> {
		private Date updatedDate;

		private DTRDailyShiftScheduleHandler(Date updatedDate) {
			this.updatedDate = updatedDate;
		}

		@Override
		public List<DTRDailyShiftScheduleDto> convert(List<Object[]> queryResult) {
			List<DTRDailyShiftScheduleDto> dailyShiftScheduleDtos = new ArrayList<>();
			DTRDailyShiftScheduleDto scheduleDto = null;
			for (Object[] rowResult : queryResult) {
				int columnNo = 0;
				scheduleDto = new DTRDailyShiftScheduleDto();
				scheduleDto.setEmployeeId((Integer) rowResult[columnNo++]);
				scheduleDto.setEmployeeShiftId((Integer) rowResult[columnNo++]);
				scheduleDto.setStartShift((Date) rowResult[columnNo++]);
				scheduleDto.setEndShift((Date) rowResult[columnNo++]);
				scheduleDto.setUpdatedDate((Date) rowResult[columnNo++]);
				dailyShiftScheduleDtos.add(scheduleDto);
			}
			return dailyShiftScheduleDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			if(updatedDate != null){
				query.setParameter(index++, updatedDate);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("EMPLOYEE_ID", Hibernate.INTEGER);
			query.addScalar("EMPLOYEE_SHIFT_ID", Hibernate.INTEGER);
			query.addScalar("START_SHIFT", Hibernate.TIMESTAMP);
			query.addScalar("END_SHIFT", Hibernate.TIMESTAMP);
			query.addScalar("UPDATED_DATE", Hibernate.TIMESTAMP);
		}
	}

	@Override
	public List<EmployeeDtr> getEmployeeDtrs(Integer companyId, Integer divisionId, Date dateFrom, Date dateTo,
			Integer timeSheetId) {
		return getEmployeeDtrs(companyId, divisionId, dateFrom, dateTo, null, timeSheetId, -1);
	}

	@Override
	public List<EmployeeDtr> getEmployeeDtrs(Integer companyId, Integer divisionId, Date dateFrom, Date dateTo, Boolean isActive) {
		return getEmployeeDtrs(companyId, divisionId, dateFrom, dateTo, null, -1, -1);
	}

	@Override
	public List<DTRMonthlyScheduleDto> getLatestMonthlySheds(Date updatedDate, Date updatedTimePeriodDate) {
		String sql = "SELECT MSSL.EMPLOYEE_ID, MSSL.EMPLOYEE_SHIFT_ID, PTPS.PAYROLL_TIME_PERIOD_SCHEDULE_ID, "
				+ "PTPS.DATE_FROM, PTPS.DATE_TO, MSS.UPDATED_DATE AS MS_UPDATED_DATE, PTP.UPDATED_DATE AS PT_UPDATED_DATE "
				+ "FROM MONTHLY_SHIFT_SCHEDULE MSS "
				+ "INNER JOIN PAYROLL_TIME_PERIOD_SCHEDULE PTPS ON PTPS.PAYROLL_TIME_PERIOD_SCHEDULE_ID = MSS.PAYROLL_TIME_PERIOD_SCHEDULE_ID "
				+ "INNER JOIN MONTHLY_SHIFT_SCHEDULE_LINE MSSL ON MSSL.MONTHLY_SHIFT_SCHEDULE_ID = MSS.MONTHLY_SHIFT_SCHEDULE_ID "
				+ "INNER JOIN PAYROLL_TIME_PERIOD PTP ON PTP.PAYROLL_TIME_PERIOD_ID = MSS.PAYROLL_TIME_PERIOD_ID "
				+ (updatedDate != null ? "WHERE MSS.UPDATED_DATE > ? " : "")
				+ (updatedTimePeriodDate != null ? "|| PTP.UPDATED_DATE > ?" : "");
		return (List<DTRMonthlyScheduleDto>) get(sql, new DTRMonthlyScheduleHandler(updatedDate, updatedTimePeriodDate));
	}

	private static class DTRMonthlyScheduleHandler implements QueryResultHandler<DTRMonthlyScheduleDto> {
		private Date updatedDate;
		private Date updatedTimePeriodDate;

		private DTRMonthlyScheduleHandler(Date updatedDate, Date updatedTimePeriodDate) {
			this.updatedDate = updatedDate;
			this.updatedTimePeriodDate = updatedTimePeriodDate;
		}

		@Override
		public List<DTRMonthlyScheduleDto> convert(List<Object[]> queryResult) {
			List<DTRMonthlyScheduleDto> dtrMonthlyScheduleDtos = new ArrayList<>();
			DTRMonthlyScheduleDto scheduleDto = null;
			for (Object[] rowResult : queryResult) {
				int columnNo = 0;
				scheduleDto = new DTRMonthlyScheduleDto();
				scheduleDto.setEmployeeId((Integer) rowResult[columnNo++]);
				scheduleDto.setEmployeeShiftId((Integer) rowResult[columnNo++]);
				scheduleDto.setTimePeriodSchedId((Integer) rowResult[columnNo++]);
				scheduleDto.setDateFrom((Date) rowResult[columnNo++]);
				scheduleDto.setDateTo((Date) rowResult[columnNo++]);
				scheduleDto.setUpdatedDate((Date) rowResult[columnNo++]);
				scheduleDto.setUpdatedTimePeriodDate((Date) rowResult[columnNo++]);
				dtrMonthlyScheduleDtos.add(scheduleDto);
			}
			return dtrMonthlyScheduleDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			if(updatedDate != null){
				query.setParameter(index++, updatedDate);
			}
			if(updatedTimePeriodDate != null){
				query.setParameter(index++, updatedTimePeriodDate);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("EMPLOYEE_ID", Hibernate.INTEGER);
			query.addScalar("EMPLOYEE_SHIFT_ID", Hibernate.INTEGER);
			query.addScalar("PAYROLL_TIME_PERIOD_SCHEDULE_ID", Hibernate.INTEGER);
			query.addScalar("DATE_FROM", Hibernate.DATE);
			query.addScalar("DATE_TO", Hibernate.DATE);
			query.addScalar("MS_UPDATED_DATE", Hibernate.TIMESTAMP);
			query.addScalar("PT_UPDATED_DATE", Hibernate.TIMESTAMP);
		}
	}

	@Override
	public boolean isExistingLog(Integer employeeId, Date date) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeDtr.FIELD.employeeId.name(), employeeId));
		dc.add(Restrictions.eq(EmployeeDtr.FIELD.logTime.name(), date));
		return getAll(dc).size() > 0;
	}

	@Override
	public List<EmployeeDtr> geEmployeeDtrsByTimeSheet(int timeSheetId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria empDc = DetachedCriteria.forClass(PayrollEmployeeTimeSheet.class);
		empDc.setProjection(Projections.property(PayrollEmployeeTimeSheet.FIELD.id.name()));
		empDc.add(Restrictions.eq(PayrollEmployeeTimeSheet.FIELD.timeSheetId.name(), timeSheetId));

		dc.add(Subqueries.propertyIn(EmployeeDtr.FIELD.payrollEmployeeTimesheetId.name(), empDc));
		return getAll(dc);
	}

	@Override
	public List<EmployeeDtr> getEmployeeDtrsByEmployee(Integer companyId, Integer divisionId, Date dateFrom,
			Date dateTo, Integer employeeId) {
		return getEmployeeDtrs(companyId, divisionId, dateFrom, dateTo, true, -1, employeeId);
	}
}
