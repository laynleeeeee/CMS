package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.eb.dao.LeaveDetailDao;
import eulap.eb.domain.hibernate.EmployeeRequest;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.LeaveDetail;
import eulap.eb.domain.hibernate.OvertimeDetail;

/**
 * Data access object implementation class for {@link LeaveDetailDao}

 *
 */

public class LeaveDetailDaoImpl extends BaseDao<LeaveDetail> implements LeaveDetailDao {

	@Override
	protected Class<LeaveDetail> getDomainClass() {
		return LeaveDetail.class;
	}

	@Override
	public LeaveDetail getLeaveDetailByRequestId(Integer employeeRequestId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(employeeRequestId != 0) {
			dc.add(Restrictions.eq(LeaveDetail.FIELD.employeeRequestId.name(), employeeRequestId));
		}
		return get(dc);
	}

	@Override
	public boolean hasLeave(Integer employeeId, Date date) {
		String sql = "SELECT DATE_FROM, DATE_TO FROM LEAVE_DETAIL LV "
				+ "INNER JOIN EMPLOYEE_REQUEST ER ON ER.EMPLOYEE_REQUEST_ID = LV.EMPLOYEE_REQUEST_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ER.FORM_WORKFLOW_ID "
				+ "WHERE ER.EMPLOYEE_ID = ? "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND (? BETWEEN LV.DATE_FROM AND LV.DATE_TO)";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, employeeId);
			query.setParameter(1, DateUtil.formatToSqlDate(date));
			List<?> list = query.list();
			return !list.isEmpty();
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public LeaveDetail getByEmployeeAndDate(Integer employeeId, Date date) {
		String sql = "SELECT LV.LEAVE_DETAIL_ID, LV.EMPLOYEE_REQUEST_ID, LV.TYPE_OF_LEAVE_ID, "
				+ "LV.DATE_FROM, LV.DATE_TO, LV.LEAVE_DAYS, TOV.PAID_LEAVE, LV.HALF_DAY, LV.PERIOD "
				+ "FROM LEAVE_DETAIL LV "
				+ "INNER JOIN EMPLOYEE_REQUEST ER ON ER.EMPLOYEE_REQUEST_ID = LV.EMPLOYEE_REQUEST_ID "
				+ "INNER JOIN TYPE_OF_LEAVE TOV ON TOV.TYPE_OF_LEAVE_ID = LV.TYPE_OF_LEAVE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ER.FORM_WORKFLOW_ID "
				+ "WHERE ER.EMPLOYEE_ID = ? "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND (? BETWEEN LV.DATE_FROM AND LV.DATE_TO)";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, employeeId);
			query.setParameter(1, DateUtil.formatToSqlDate(date));
			if (query.list().isEmpty()) {
				return null;
			}
			LeaveDetail leaveDetail = new LeaveDetail();
			List<Object[]> list = query.list();
			for (Object[] obj : list) {
				leaveDetail.setId((Integer) obj[0]);
				leaveDetail.setEmployeeRequestId((Integer) obj[1]);
				leaveDetail.setTypeOfLeaveId((Integer) obj[2]);
				leaveDetail.setDateFrom((Date) obj[3]);
				leaveDetail.setDateTo((Date) obj[4]);
				leaveDetail.setLeaveDays((Double) obj[5]);
				leaveDetail.setPaid((Boolean) obj[6]);
				leaveDetail.setHalfDay((Boolean) obj[7]);
				leaveDetail.setPeriod((Integer) obj[8]);
				break;
			}
			return leaveDetail;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public List<LeaveDetail> getEmployeeLeavesByFormDate(Date updatedDate) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria erDc = DetachedCriteria.forClass(EmployeeRequest.class);
		erDc.setProjection(Projections.property(EmployeeRequest.FIELD.id.name()));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.or(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true),
				Restrictions.eq(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID)));

		DetachedCriteria fwlDc = DetachedCriteria.forClass(FormWorkflowLog.class);
		fwlDc.setProjection(Projections.property(FormWorkflowLog.FIELD.formWorkflowId.name()));
		if(updatedDate != null){
			fwlDc.add(Restrictions.ge(FormWorkflowLog.FIELD.createdDate.name(), updatedDate));
		}
		fwDc.add(Subqueries.propertyIn(FormWorkflow.FIELD.id.name(), fwlDc));

		erDc.add(Subqueries.propertyIn(EmployeeRequest.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Subqueries.propertyIn(OvertimeDetail.FIELD.employeeRequestId.name(), erDc));
		return getAll(dc);
	}
}
