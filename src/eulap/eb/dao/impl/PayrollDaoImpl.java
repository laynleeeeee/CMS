package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
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
import eulap.eb.dao.PayrollDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.EmployerEmployeeContributionDTO;

/**
 * Implementation class of {@link PayrollDao}

 */
public class PayrollDaoImpl extends BaseDao<Payroll> implements PayrollDao{

	@Override
	protected Class<Payroll> getDomainClass() {
		return Payroll.class;
	}

	@Override
	public Page<Payroll> getPayrolls(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds, final PageSetting pageSetting) {
		HibernateCallback<Page<Payroll>> hibernateCallback = new HibernateCallback<Page<Payroll>>() {
			@Override
			public Page<Payroll> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(Payroll.class);
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						Payroll.FIELD.date.name(), Payroll.FIELD.date.name(),
						Payroll.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				String strCriteria = searchParam.getSearchCriteria();
				if (strCriteria != null && !strCriteria.trim().isEmpty()) {
					dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ? ", strCriteria.trim(), Hibernate.STRING));
				}
				// Workflow status
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0)
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(),formStatusIds);
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(Payroll.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.addOrder(Order.desc(Payroll.FIELD.date.name()));
				dc.addOrder(Order.desc(Payroll.FIELD.id.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<Payroll> searchPayrolls(String criteria, User user, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(StringFormatUtil.isNumeric(criteria)){
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ? ", criteria.trim(), Hibernate.STRING));
		}
		addUserCompany(dc, user);
		return getAll(dc, pageSetting);
	}

	/**
	 * Get the EmployerEmployeeContributionDTO.
	 * @param companyId The company id.
	 * @param month The time period month
	 * @param year The time period year
	 * @param timePeriodScheduleId The time period schedule id.
	 * @param divisionId The division id.
	 * @param pageSetting The page setting.
	 * @param isFirstNameFirst True if the employee name format is first name first, otherwise false.
	 * @return The employer employee contribution DTO.
	 */
	public Page<EmployerEmployeeContributionDTO> getEEContibutions(Integer companyId, Integer month, Integer year,
			Integer timePeriodScheduleId,  Integer divisionId, PageSetting pageSetting, boolean isFirstNameFirst) {
		String sql = "";
		if(!isFirstNameFirst) {
			sql += "SELECT E.EMPLOYEE_ID, CONCAT(E.LAST_NAME, ', ', E.FIRST_NAME, ' ', E.MIDDLE_NAME) AS EMPLOYEE_NAME, ";
		} else {
			sql += "SELECT E.EMPLOYEE_ID, CONCAT(E.FIRST_NAME,REPLACE(CONCAT(' ',LEFT(E.MIDDLE_NAME,1),'. '),' .',''),E.LAST_NAME) AS EMPLOYEE_NAME, ";
		}
		sql += "SUM(SSS) AS SSS, SUM(SSS_ER) AS SSS_ER, SUM(SSS_EC) AS SSS_EC, SUM(PHILHEALTH) AS PHILHEALTH, "
				+ "SUM(PHILHEALTH_ER) AS PHILHEALTH_ER, SUM(PAGIBIG) AS PAGIBIG, SUM(PAGIBIG_ER) AS PAGIBIG_ER "
				+ "FROM PAYROLL_EMPLOYEE_SALARY PES "
				+ "INNER JOIN PAYROLL P ON P.PAYROLL_ID = PES.PAYROLL_ID "
				+ "INNER JOIN PAYROLL_TIME_PERIOD PTP ON PTP.PAYROLL_TIME_PERIOD_ID = P.PAYROLL_TIME_PERIOD_ID "
				+ "INNER JOIN EMPLOYEE E ON E.EMPLOYEE_ID = PES.EMPLOYEE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = P.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND P.COMPANY_ID = ? "
				+ "AND PTP.YEAR = ? ";
				if(!month.equals(-1)) {
					sql += "AND PTP.MONTH = ? ";
				}
				if(!timePeriodScheduleId.equals(-1)) {
					sql += "AND P.PAYROLL_TIME_PERIOD_SCHEDULE_ID = ? ";
				}
				if(!divisionId.equals(-1)) {
					sql += "AND P.DIVISION_ID = ? ";
				}
			sql +="GROUP BY EMPLOYEE_ID ORDER BY EMPLOYEE_NAME";
		EEContributionHandler handler = new EEContributionHandler(companyId, month, year, timePeriodScheduleId, divisionId);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class EEContributionHandler implements QueryResultHandler<EmployerEmployeeContributionDTO> {
		private Integer companyId;
		private Integer month;
		private Integer year;
		private Integer timePeriodScheduleId;
		private Integer divisionId;

		private EEContributionHandler(Integer companyId, Integer month, Integer year,
				Integer timePeriodScheduleId, Integer divisionId) {
			this.companyId = companyId;
			this.month = month;
			this.year = year;
			this.timePeriodScheduleId = timePeriodScheduleId;
			this.divisionId = divisionId;
		}

		@Override
		public List<EmployerEmployeeContributionDTO> convert(List<Object[]> queryResult) {
			List<EmployerEmployeeContributionDTO> dtos = new
					ArrayList<EmployerEmployeeContributionDTO>();
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				Integer employeeId = (Integer) rowResult[colNum++];
				String employeeName = (String) rowResult[colNum++];
				Double sssEe = (Double) rowResult[colNum++];
				Double sssEr = (Double) rowResult[colNum++];
				Double sssEc = (Double) rowResult[colNum++];
				Double philHealthEe = (Double) rowResult[colNum++];
				Double philHealthEr = (Double) rowResult[colNum++];
				Double pagibigEe = (Double) rowResult[colNum++];
				Double pagibigEr = (Double) rowResult[colNum++];
				dtos.add(EmployerEmployeeContributionDTO.getInstanceOf(employeeId, employeeName, sssEe,
						sssEr, sssEc, philHealthEe, philHealthEr, pagibigEe, pagibigEr));
			}
			return dtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			query.setParameter(++index, year);
			if(month != -1) {
				query.setParameter(++index, month);
			}
			if(timePeriodScheduleId != -1) {
				query.setParameter(++index, timePeriodScheduleId);
			}
			if(divisionId != -1) {
				query.setParameter(++index, divisionId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("EMPLOYEE_ID", Hibernate.INTEGER);
			query.addScalar("EMPLOYEE_NAME", Hibernate.STRING);
			query.addScalar("SSS", Hibernate.DOUBLE);
			query.addScalar("SSS_ER", Hibernate.DOUBLE);
			query.addScalar("SSS_EC", Hibernate.DOUBLE);
			query.addScalar("PHILHEALTH", Hibernate.DOUBLE);
			query.addScalar("PHILHEALTH_ER", Hibernate.DOUBLE);
			query.addScalar("PAGIBIG", Hibernate.DOUBLE);
			query.addScalar("PAGIBIG_ER", Hibernate.DOUBLE);
		}
	}

	@Override
	public boolean hasExistingPayroll(int payrollId, int timePeriodId, int timePeriodScheduleId, int divisionId, int companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.ne(Payroll.FIELD.id.name(), payrollId));
		dc.add(Restrictions.eq(Payroll.FIELD.payrollTimePeriodId.name(), timePeriodId));
		dc.add(Restrictions.eq(Payroll.FIELD.payrollTimePeriodScheduleId.name(), timePeriodScheduleId));
		dc.add(Restrictions.eq(Payroll.FIELD.divisionId.name(), divisionId));
		dc.add(Restrictions.eq(Payroll.FIELD.companyId.name(), companyId));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(Payroll.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc).size() > 0;
	}

	@Override
	public Payroll getByTimeSheet(int timeSheetId, boolean isExcludeCancelled) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Payroll.FIELD.timeSheetId.name(), timeSheetId));
		if (isExcludeCancelled) {
			DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
			fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
			fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
			dc.add(Subqueries.propertyIn(Payroll.FIELD.formWorkflowId.name(), fwDc));
		}
		return get(dc);
	}

	@Override
	public Payroll getPayroll(Integer ptpId, Integer ptpSchedId, Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Payroll.FIELD.payrollTimePeriodId.name(), ptpId));
		dc.add(Restrictions.eq(Payroll.FIELD.payrollTimePeriodScheduleId.name(), ptpSchedId));
		dc.add(Restrictions.eq(Payroll.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(Payroll.FIELD.divisionId.name(), divisionId));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dc.add(Subqueries.propertyIn(Payroll.FIELD.formWorkflowId.name(), fwDc));
		return get(dc);
	}
}
