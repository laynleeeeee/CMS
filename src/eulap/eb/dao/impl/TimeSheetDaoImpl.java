package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
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
import eulap.eb.dao.TimeSheetDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.TimeSheet;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.TimeSheetTemplateDto;

/**
 * Implementing class of {@link TimeSheetDao}

 *
 */
public class TimeSheetDaoImpl extends BaseDao<TimeSheet> implements TimeSheetDao{

	@Override
	protected Class<TimeSheet> getDomainClass() {
		return TimeSheet.class;
	}

	@Override
	public Page<TimeSheet> getTimeSheets(final ApprovalSearchParam searchParam, final List<Integer> statuses, final PageSetting pageSetting) {
		HibernateCallback<Page<TimeSheet>> hibernateCallback = new HibernateCallback<Page<TimeSheet>>() {

			@Override
			public Page<TimeSheet> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(TimeSheet.class);
				if(!searchParam.getSearchCriteria().trim().isEmpty()){
					dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						TimeSheet.FIELD.date.name(), TimeSheet.FIELD.date.name(),
						TimeSheet.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if(statuses.size() > 0){
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), statuses);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(TimeSheet.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(TimeSheet.FIELD.date.name()));
				dc.addOrder(Order.desc(TimeSheet.FIELD.sequenceNumber.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public List<TimeSheet> searchTimeSheets(final String searchCriteria, final User user) {
		return getHibernateTemplate().execute(new HibernateCallback<List<TimeSheet>>() {

			@Override
			public List<TimeSheet> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(TimeSheet.class);
				if(StringFormatUtil.isNumeric(searchCriteria)){
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
							searchCriteria, Hibernate.STRING));
				}
				if(!user.getCompanyIds().isEmpty()){
					criteria.add(Restrictions.in(TimeSheet.FIELD.companyId.name(), user.getCompanyIds()));
				}
				criteria.addOrder(Order.asc(TimeSheet.FIELD.sequenceNumber.name()));
				return getAllByCriteria(criteria);
			}
		});
	}

	@Override
	public Page<TimeSheet> getTimeSheets(Integer companyId, Integer divisionId, Integer month, Integer year,
			Integer payrollTimePeriodScheduleId, User user, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		if (companyId != null) {
			dc.add(Restrictions.eq(TimeSheet.FIELD.companyId.name(), companyId));
		}
		if (divisionId != null) {
			dc.add(Restrictions.eq(TimeSheet.FIELD.divisionId.name(), divisionId));
		}
		if (payrollTimePeriodScheduleId != null) {
			dc.add(Restrictions.eq(TimeSheet.FIELD.payrollTimePeriodScheduleId.name(), payrollTimePeriodScheduleId));
		}

		// Payroll Time Period
		DetachedCriteria ptpDc = DetachedCriteria.forClass(PayrollTimePeriod.class);
		ptpDc.setProjection(Projections.property(PayrollTimePeriod.FIELD.id.name()));
		if (month != null) {
			ptpDc.add(Restrictions.eq(PayrollTimePeriod.FIELD.month.name(), month));
		}
		if (year != null) {
			ptpDc.add(Restrictions.eq(PayrollTimePeriod.FIELD.year.name(), year));
		}
		ptpDc.add(Restrictions.eq(PayrollTimePeriod.FIELD.active.name(), true));
		dc.add(Subqueries.propertyIn(TimeSheet.FIELD.payrollTimePeriodId.name(), ptpDc));

		// Payroll
		DetachedCriteria payrollDc = DetachedCriteria.forClass(Payroll.class);
		payrollDc.setProjection(Projections.property("timeSheetId"));
		payrollDc.add(Restrictions.isNotNull("timeSheetId"));

		// Payroll Workflow
		DetachedCriteria payrollWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		payrollWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		payrollWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		payrollDc.add(Subqueries.propertyIn(Payroll.FIELD.formWorkflowId.name(), payrollWorkflow));
		dc.add(Subqueries.propertyNotIn(TimeSheet.FIELD.id.name(), payrollDc));

		// Time sheet work flow.
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(TimeSheet.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean hasExistingTimeSheet(int timeSheetId, int timePeriodId, int timePeriodScheduleId, int divisionId, int companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(TimeSheet.FIELD.payrollTimePeriodId.name(), timePeriodId));
		dc.add(Restrictions.eq(TimeSheet.FIELD.payrollTimePeriodScheduleId.name(), timePeriodScheduleId));
		dc.add(Restrictions.eq(TimeSheet.FIELD.divisionId.name(), divisionId));
		dc.add(Restrictions.eq(TimeSheet.FIELD.companyId.name(), companyId));
		if (timeSheetId > 0) {
			dc.add(Restrictions.ne(TimeSheet.FIELD.id.name(), timeSheetId));
		}

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(TimeSheet.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc).size() > 0;
	}

	@Override
	public Integer getTimeSheetIdByDate(Date date, int companyId, int divisionId) {
		String sql = "SELECT TIME_SHEET_ID FROM TIME_SHEET TS "
				+ "INNER JOIN PAYROLL_TIME_PERIOD_SCHEDULE PTPS ON "
				+ "PTPS.PAYROLL_TIME_PERIOD_SCHEDULE_ID = TS.PAYROLL_TIME_PERIOD_SCHEDULE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = TS.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND TS.COMPANY_ID = ? "
				+ "AND TS.DIVISION_ID = ? "
				+ "AND ? BETWEEN PTPS.DATE_FROM AND PTPS.DATE_TO";
		Session session = null;
		Integer timesheetId = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, companyId);
			query.setParameter(1, divisionId);
			query.setParameter(2, date);

			List<Object[]> list = query.list();
			if(list != null && !list.isEmpty()) {
				for(Object obj : list) {
					timesheetId = (Integer) obj;
				}
			}
		} finally {
			if(session != null) {
				session.close();
			}
		}

		return timesheetId;
	}

	@Override
	public Page<TimeSheetTemplateDto> getTimesheetTemplateDetails(Integer payrollTimePeriodId,
			Integer payrollTimePeriodScheduleId, Integer divisionId, Integer companyId, PageSetting pageSetting) {
		String sql = "SELECT E.EMPLOYEE_NO, E.BIOMETRIC_ID, CONCAT(E.FIRST_NAME, ' ', LPAD(E.MIDDLE_NAME, 1, 1), '. ', E.LAST_NAME) AS EMPLOYEE_NAME, "
				+ "DSSL.DATE FROM DAILY_SHIFT_SCHEDULE DSS "
				+ "INNER JOIN DAILY_SHIFT_SCHEDULE_LINE DSSL ON DSS.DAILY_SHIFT_SCHEDULE_ID = DSSL.DAILY_SHIFT_SCHEDULE_ID "
				+ "INNER JOIN EMPLOYEE E ON E.EMPLOYEE_ID = DSSL.EMPLOYEE_ID "
				+ "WHERE DSS.COMPANY_ID = ? "
				+ (payrollTimePeriodId != null ? "AND DSS.PAYROLL_TIME_PERIOD_ID = ? " : "")
				+ (payrollTimePeriodScheduleId != null ? "AND DSS.PAYROLL_TIME_PERIOD_SCHEDULE_ID = ? " : "")
				+ (divisionId != null ? "AND E.DIVISION_ID = ? " : "")
				+ "ORDER BY E.EMPLOYEE_NO, DSSL.DATE";
		TimeSheetTemplateHandler handler = new TimeSheetTemplateHandler(payrollTimePeriodId, payrollTimePeriodScheduleId,
				divisionId, companyId);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class TimeSheetTemplateHandler implements QueryResultHandler<TimeSheetTemplateDto> {
		private Integer payrollTimePeriodId;
		private Integer payrollTimePeriodScheduleId;
		private Integer divisionId;
		private Integer companyId;

		private TimeSheetTemplateHandler(Integer payrollTimePeriodId, Integer payrollTimePeriodScheduleId,
				Integer divisionId, Integer companyId) {
			this.payrollTimePeriodId = payrollTimePeriodId;
			this.payrollTimePeriodScheduleId = payrollTimePeriodScheduleId;
			this.divisionId = divisionId;
			this.companyId = companyId;
		}

		@Override
		public List<TimeSheetTemplateDto> convert(List<Object[]> queryResult) {
			List<TimeSheetTemplateDto> dtos = new
					ArrayList<TimeSheetTemplateDto>();
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				String employeeNo = (String) rowResult[colNum++];
				Integer biometricId = (Integer) rowResult[colNum++];
				String employeeName = (String) rowResult[colNum++];
				Date date = (Date) rowResult[colNum++];
				dtos.add(TimeSheetTemplateDto.getInstanceOf(employeeNo, biometricId, employeeName, date));
			}
			return dtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			if(payrollTimePeriodId != null){
				query.setParameter(++index, payrollTimePeriodId);
			}
			if(payrollTimePeriodScheduleId != null){
				query.setParameter(++index, payrollTimePeriodScheduleId);
			}
			if(divisionId != null){
				query.setParameter(++index, divisionId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("EMPLOYEE_NO", Hibernate.STRING);
			query.addScalar("BIOMETRIC_ID", Hibernate.INTEGER);
			query.addScalar("EMPLOYEE_NAME", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
		}
	}
}
