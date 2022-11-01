package eulap.eb.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.DailyShiftScheduleLineDao;
import eulap.eb.domain.hibernate.DailyShiftScheduleLine;
import eulap.eb.web.dto.MaxDayPerScheduleLine;

/**
 * DAO implementation of {@link DailyShiftScheduleLineDao}

 *
 */
public class DailyShiftScheduleLineDaoImpl extends BaseDao<DailyShiftScheduleLine> implements DailyShiftScheduleLineDao{

	@Override
	protected Class<DailyShiftScheduleLine> getDomainClass() {
		return DailyShiftScheduleLine.class;
	}

	@Override
	public DailyShiftScheduleLine getDailyShiftSchedLine(Integer employeeId, Date date) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(DailyShiftScheduleLine.FIELD.employeeId.name(), employeeId));
		dc.add(Restrictions.eq(DailyShiftScheduleLine.FIELD.date.name(), date));
		return get(dc);
	}

	@Override
	public List<DailyShiftScheduleLine> getAllDailyShiftShedule(int dailyShiftScheduleId,
			boolean isFirstNameFirst) {
		List<DailyShiftScheduleLine> dailyShiftScheduleLines = new ArrayList<>();
		String sql = "SELECT CONCAT(E.FIRST_NAME, IF(E.MIDDLE_NAME != '' AND E.MIDDLE_NAME IS NOT NULL, "
				+ "CONCAT(' ', LPAD(E.MIDDLE_NAME, 1, ''), '. '), ' '), E.LAST_NAME) AS EMPLOYEE_NAME, "
				+ "DATE, ES.NAME, DAILY_SHIFT_SCHEDULE_ID "
				+ "FROM DAILY_SHIFT_SCHEDULE_LINE DSSL "
				+ "INNER JOIN EMPLOYEE E ON E.EMPLOYEE_ID = DSSL.EMPLOYEE_ID "
				+ "INNER JOIN EMPLOYEE_SHIFT ES ON ES.EMPLOYEE_SHIFT_ID = DSSL.EMPLOYEE_SHIFT_ID "
				+ "WHERE DAILY_SHIFT_SCHEDULE_ID = ? ";
		if(isFirstNameFirst) {
			sql += "ORDER BY LOWER(FIRST_NAME), LOWER(LAST_NAME), DATE";
		} else {
			sql += "ORDER BY LOWER(LAST_NAME), LOWER(FIRST_NAME), DATE";
		}
		Session session = null;
		DailyShiftScheduleLine dailyShiftScheduleLine = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, dailyShiftScheduleId);

			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				String employeeName = "";
				List<String> employeeNames = new ArrayList<>();
				for (Object[] row : list) {
					dailyShiftScheduleLine = new DailyShiftScheduleLine();
					employeeName = (String) row[0];
					if(!employeeNames.contains(employeeName)){
						dailyShiftScheduleLine.setEmployeeName(employeeName);
						employeeNames.add(employeeName);
					}
					dailyShiftScheduleLine.setDate((Date) row[1]);
					dailyShiftScheduleLine.setShiftName((String) row[2]);
					dailyShiftScheduleLine.setDailyShiftScheduleId((Integer) row[3]);
					dailyShiftScheduleLines.add(dailyShiftScheduleLine);
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return dailyShiftScheduleLines;
	}

	@Override
	public MaxDayPerScheduleLine getMaxDayPerLine(int payrollTimePeriodScheduleLineId) {
		String sql = "SELECT DATEDIFF(DATE_TO, MAX(DATE)), MAX(DATE) AS MAX_DATE "
				+ "FROM DAILY_SHIFT_SCHEDULE_LINE DSSL "
				+ "INNER JOIN DAILY_SHIFT_SCHEDULE DSS ON DSS.DAILY_SHIFT_SCHEDULE_ID = DSSL.DAILY_SHIFT_SCHEDULE_ID "
				+ "INNER JOIN PAYROLL_TIME_PERIOD_SCHEDULE PPS ON PPS.PAYROLL_TIME_PERIOD_SCHEDULE_ID = DSS.PAYROLL_TIME_PERIOD_SCHEDULE_ID "
				+ "WHERE DSS.PAYROLL_TIME_PERIOD_SCHEDULE_ID = ? ";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, payrollTimePeriodScheduleLineId);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					BigInteger bi = (BigInteger) row[0];
					Integer dayDiff = bi.intValue();
					Date maxDate = (Date) row[1];
					return new MaxDayPerScheduleLine(dayDiff, maxDate);
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return null;
	}
}
