package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EmployeeLeaveCreditLineDao;
import eulap.eb.domain.hibernate.EmployeeLeaveCreditLine;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.web.dto.AvailableLeavesDto;

/**
 * Implemenation class of {@link EmployeeLeaveCreditLine}

 *
 */
public class EmployeeLeaveCreditLineDaoImpl extends BaseDao<EmployeeLeaveCreditLine> implements EmployeeLeaveCreditLineDao{

	@Override
	protected Class<EmployeeLeaveCreditLine> getDomainClass() {
		return EmployeeLeaveCreditLine.class;
	}

	@Override
	public List<EmployeeLeaveCreditLine> getEmployeeLeaveCreditLineByEbObject(Integer ebObjectId, Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));

		dc.add(Subqueries.propertyIn(EmployeeLeaveCreditLine.FIELD.ebObjectId.name(), obj2ObjDc));
		if(employeeId != null) {
			dc.add(Restrictions.eq(EmployeeLeaveCreditLine.FIELD.employeeId.name(), employeeId));
		}
		return getAll(dc);
	}

	@Override
	public Double getAvailableLeaves(Integer employeeId, Integer typeOfLeaveId,
			boolean isForPrintOut, boolean isLeaveEarned) {
		String status = !isForPrintOut ? " AND FW.IS_COMPLETE = 1 " : " AND FW.CURRENT_STATUS_ID != 4 ";
		StringBuilder sql = new StringBuilder("SELECT TOTAL_LEAVES, (SUM(TOTAL_LEAVES) - SUM(DAYS_TO_LEAVE)) AS AVAILABLE_LEAVES FROM ( "
				+ "SELECT E.EMPLOYEE_ID AS EMP_ID, 0 AS DAYS_TO_LEAVE, "
				+ "SUM(COALESCE(ELCL.ADD_CREDIT,0)) - SUM(COALESCE(ELCL.DEDUCT_DEBIT,0)) AS TOTAL_LEAVES "
				+ "FROM ELC_LINE ELCL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ELCL.EB_OBJECT_ID "
				+ "INNER JOIN EMPLOYEE_LEAVE_CREDIT ELC ON ELC.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN EMPLOYEE E ON E.EMPLOYEE_ID = ELCL.EMPLOYEE_ID "
				+ "INNER JOIN TYPE_OF_LEAVE TOF ON TOF.TYPE_OF_LEAVE_ID = ELC.TYPE_OF_LEAVE_ID "
				+ "INNER JOIN COMPANY C ON C.COMPANY_ID = ELC.COMPANY_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ELC.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND TOF.TYPE_OF_LEAVE_ID =  " + typeOfLeaveId + " ");
		sql.append(employeeId != null ? "AND E.EMPLOYEE_ID = " + employeeId + " " : "");
			sql.append("UNION ALL "
					+ "SELECT ER.EMPLOYEE_ID AS EMP_ID, SUM(COALESCE(LD.LEAVE_DAYS, 0)) AS DAYS_TO_LEAVE, 0 AS TOTAL_LEAVES FROM EMPLOYEE_REQUEST ER "
					+ "INNER JOIN LEAVE_DETAIL LD ON LD.EMPLOYEE_REQUEST_ID = ER.EMPLOYEE_REQUEST_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ER.FORM_WORKFLOW_ID "
					+ "WHERE LD.TYPE_OF_LEAVE_ID = " + typeOfLeaveId + " ");
			sql.append(employeeId != null ? "AND ER.EMPLOYEE_ID = " + employeeId + " " : "");
			sql.append(status);
		sql.append(") AS TBL");
		if(isLeaveEarned) {
			return employeeId != null ? getTotalAvailableLeaves(sql.toString()).get(0) : 0.0;
		}
		return employeeId != null ? getTotalAvailableLeaves(sql.toString()).get(1) : 0.0;
	}

	private List<Double> getTotalAvailableLeaves(String sql) {
		Collection<Double> availableLeaves = get(sql, new QueryResultHandler<Double>() {

			@Override
			public List<Double> convert(List<Object[]> queryResult) {
				List<Double> ret = new ArrayList<Double>();
				for (Object[] row : queryResult) {
					Double totalLeave = (Double) row[0];
					ret.add(totalLeave);
					Double availableLeave = (Double) row[1];
					ret.add(availableLeave);
					break; // Expecting one row only.
				}
				return ret;
			}

			@Override
			public int setParamater(SQLQuery query) {
				return -1;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("TOTAL_LEAVES", Hibernate.DOUBLE);
				query.addScalar("AVAILABLE_LEAVES", Hibernate.DOUBLE);
			}
		});
		return (List<Double>) availableLeaves;
	}

	@Override
	public List<AvailableLeavesDto> getEmpAvailableLeaves(Date erUpdatedDate, Date elsUpdatedDate) {
		Date date = erUpdatedDate != null && elsUpdatedDate != null
				? erUpdatedDate.before(elsUpdatedDate) ? elsUpdatedDate : erUpdatedDate :
					erUpdatedDate != null ? erUpdatedDate : elsUpdatedDate != null ? elsUpdatedDate : null;
		String sql = "SELECT TYPE_OF_LEAVE_ID, LEAVE_TYPE, EMP_ID, TOTAL_LEAVES, (SUM(TOTAL_LEAVES) - SUM(DAYS_TO_LEAVE)) AS AVAILABLE_LEAVES, "
				+ "MAX(ER_UPDATED_DATE) AS ER_UPDATED_DATE, MAX(ELS_UPDATED_DATE) AS ELS_UPDATED_DATE "
				+ "FROM (SELECT E.EMPLOYEE_ID AS EMP_ID, 0 AS DAYS_TO_LEAVE, "
				+ "SUM(COALESCE(ELCL.ADD_CREDIT,0)) - SUM(COALESCE(ELCL.DEDUCT_DEBIT,0)) AS TOTAL_LEAVES, "
				+ "TOF.TYPE_OF_LEAVE_ID, TOF.NAME AS LEAVE_TYPE, NULL AS ER_UPDATED_DATE, MAX(ELC.UPDATED_DATE) AS ELS_UPDATED_DATE FROM ELC_LINE ELCL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ELCL.EB_OBJECT_ID "
				+ "INNER JOIN EMPLOYEE_LEAVE_CREDIT ELC ON ELC.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN EMPLOYEE E ON E.EMPLOYEE_ID = ELCL.EMPLOYEE_ID "
				+ "INNER JOIN EMPLOYEE_PROFILE EP ON EP.EMPLOYEE_ID = E.EMPLOYEE_ID "
				+ "INNER JOIN TYPE_OF_LEAVE TOF ON TOF.TYPE_OF_LEAVE_ID = ELC.TYPE_OF_LEAVE_ID "
				+ "INNER JOIN COMPANY C ON C.COMPANY_ID = ELC.COMPANY_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ELC.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "GROUP BY E.EMPLOYEE_ID, TOF.TYPE_OF_LEAVE_ID "
				+ "UNION ALL "
				+ "SELECT ER.EMPLOYEE_ID AS EMP_ID, SUM(COALESCE(LD.LEAVE_DAYS, 0)) AS DAYS_TO_LEAVE, NULL AS TOTAL_LEAVES, "
				+ "LD.TYPE_OF_LEAVE_ID, TOF.NAME AS LEAVE_TYPE, MAX(ER.UPDATED_DATE) AS ER_UPDATED_DATE, NULL AS ELS_UPDATED_DATE "
				+ "FROM EMPLOYEE_REQUEST ER INNER JOIN LEAVE_DETAIL LD ON LD.EMPLOYEE_REQUEST_ID = ER.EMPLOYEE_REQUEST_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ER.FORM_WORKFLOW_ID "
				+ "INNER JOIN TYPE_OF_LEAVE TOF ON TOF.TYPE_OF_LEAVE_ID = LD.TYPE_OF_LEAVE_ID "
				+ "INNER JOIN EMPLOYEE E ON E.EMPLOYEE_ID = ER.EMPLOYEE_ID "
				+ "INNER JOIN EMPLOYEE_PROFILE EP ON EP.EMPLOYEE_ID = E.EMPLOYEE_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "GROUP BY ER.EMPLOYEE_ID, LD.TYPE_OF_LEAVE_ID) AS TBL ";
				if(date != null){
					sql += "WHERE EMP_ID IN (SELECT EMP_ID FROM ( "
							+ "SELECT ELCL.EMPLOYEE_ID AS EMP_ID, MAX(FWL.CREATED_DATE) AS UPDATED_DATE "
							+ "FROM ELC_LINE ELCL INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ELCL.EB_OBJECT_ID "
							+ "INNER JOIN EMPLOYEE_LEAVE_CREDIT ELC ON ELC.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
							+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ELC.FORM_WORKFLOW_ID "
							+ "INNER JOIN FORM_WORKFLOW_LOG FWL ON FW.FORM_WORKFLOW_ID = FWL.FORM_WORKFLOW_ID "
							+ "WHERE (FW.IS_COMPLETE = 1 or FW.CURRENT_STATUS_ID = 4) "
							+ "GROUP BY ELCL.EMPLOYEE_ID "
							+ "UNION ALL "
							+ "SELECT ER.EMPLOYEE_ID AS EMP_ID, MAX(FWL.CREATED_DATE) AS UPDATED_DATE FROM EMPLOYEE_REQUEST ER "
							+ "INNER JOIN LEAVE_DETAIL LD ON LD.EMPLOYEE_REQUEST_ID = ER.EMPLOYEE_REQUEST_ID "
							+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ER.FORM_WORKFLOW_ID "
							+ "INNER JOIN FORM_WORKFLOW_LOG FWL ON FW.FORM_WORKFLOW_ID = FWL.FORM_WORKFLOW_ID "
							+ "WHERE (FW.IS_COMPLETE = 1 or FW.CURRENT_STATUS_ID = 4) "
							+ "GROUP BY ER.EMPLOYEE_ID "
							+ ") AS TBL "
							+ "GROUP BY EMP_ID "
							+ "HAVING  MAX(UPDATED_DATE) > ?) ";
				}
				sql += "GROUP BY EMP_ID, TYPE_OF_LEAVE_ID";
		return (List<AvailableLeavesDto>) get(sql, new EmployeeAvialableLeavesHandler(date));
	}

	private static class EmployeeAvialableLeavesHandler implements QueryResultHandler<AvailableLeavesDto> {
		private Date date;

		private EmployeeAvialableLeavesHandler(Date date) {
			this.date = date;
		}

		@Override
		public List<AvailableLeavesDto> convert(List<Object[]> queryResult) {
			List<AvailableLeavesDto> availableLeavesDtos = new ArrayList<>();
			AvailableLeavesDto leavesDto = null;
			for (Object[] rowResult : queryResult) {
				int columnNo = 0;
				leavesDto = new AvailableLeavesDto();
				leavesDto.setLeaveTypeId((Integer) rowResult[columnNo++]);
				leavesDto.setLeaveType((String) rowResult[columnNo++]);
				leavesDto.setEmployeeId((Integer) rowResult[columnNo++]);
				leavesDto.setAvailableLeaves((Double) rowResult[columnNo++]);
				leavesDto.setErUpdatedDate((Date) rowResult[columnNo++]);
				leavesDto.setElsUpdatedDate((Date) rowResult[columnNo++]);
				availableLeavesDtos.add(leavesDto);
			}
			return availableLeavesDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			if(date != null){
				query.setParameter(index++, date);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("TYPE_OF_LEAVE_ID", Hibernate.INTEGER);
			query.addScalar("LEAVE_TYPE", Hibernate.STRING);
			query.addScalar("EMP_ID", Hibernate.INTEGER);
			query.addScalar("AVAILABLE_LEAVES", Hibernate.DOUBLE);
			query.addScalar("ER_UPDATED_DATE", Hibernate.TIMESTAMP);
			query.addScalar("ELS_UPDATED_DATE", Hibernate.TIMESTAMP);
		}
	}
}
