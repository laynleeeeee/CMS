package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.EmployeeProfileDao;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.domain.hibernate.EmployeeType;
import eulap.eb.web.dto.EmployeeFileDocumentDto;

/**
 * Implementing class of {@link EmployeeProfile}

 *
 */
public class EmployeeProfileDaoImpl extends BaseDao<EmployeeProfile> implements EmployeeProfileDao {

	@Override
	protected Class<EmployeeProfile> getDomainClass() {
		return EmployeeProfile.class;
	}

	@Override
	public Page<EmployeeProfile> getEmployeePerBranch(Integer companyId, Integer divisionId, SearchStatus status,
			Date asOfDate, Boolean isOrderByLastName, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.le(EmployeeProfile.FIELD.hiredDate.name(), asOfDate));
		DetachedCriteria empDc = DetachedCriteria.forClass(Employee.class);
		empDc.setProjection(Projections.property(Employee.FIELD.id.name()));
		if(companyId != null && companyId != 0){
			empDc.add(Restrictions.eq(Employee.FIELD.companyId.name(), companyId));
		}
		if(divisionId != null && divisionId != -1){
			empDc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), divisionId));
		}
		empDc = DaoUtil.setSearchStatus(empDc, Employee.FIELD.active.name(), status);
		dc.add(Subqueries.propertyIn(EmployeeProfile.FIELD.employeeId.name(), empDc));
		if(isOrderByLastName) {
			dc.createAlias("employee", "employee").addOrder(Order.asc("employee.lastName"));
			dc.addOrder(Order.asc("employee.firstName"));
			dc.addOrder(Order.asc("employee.middleName"));
		}
		return getAll(dc, pageSetting);
	}

	@Override
	public EmployeeProfile getByEmployee(Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeProfile.FIELD.employeeId.name(), employeeId));
		return get(dc);
	}

	public Page<EmployeeProfile> getEmployeeForRegularization(Integer companyId, Integer divisionId,
			Date dateFrom, Date dateTo, Boolean isOrderByLastName, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.between(EmployeeProfile.FIELD.hiredDate.name(), dateFrom, dateTo));
		DetachedCriteria empDc = DetachedCriteria.forClass(Employee.class);
		empDc.setProjection(Projections.property(Employee.FIELD.id.name()));
		if(companyId != null && companyId != 0){
			empDc.add(Restrictions.eq(Employee.FIELD.companyId.name(), companyId));
		}
		if(divisionId != null && divisionId != -1){
			empDc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), divisionId));
		}
		dc.add(Subqueries.propertyIn(EmployeeProfile.FIELD.employeeId.name(), empDc));
		if(isOrderByLastName) {
			dc.createAlias("employee", "employee").addOrder(Order.asc("employee.lastName"));
			dc.addOrder(Order.asc("employee.firstName"));
			dc.addOrder(Order.asc("employee.middleName"));
		}
		return getAll(dc, pageSetting);
	}

	@Override
	public Integer generateEmpNumber(Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(EmployeeProfile.FIELD.employeeNumber.name()));
		if (companyId != null) {
			dc.createAlias("employee", "e").add(Restrictions.eq("e.companyId", companyId));
		}
		List<Object> result = getByProjection(dc);
		if (result == null) {
			return 1;
		}
		Object obj = result.iterator().next();
		if (obj == null) {
			return 1;
		}
		return ((Integer) obj) + 1;
	}

	@Override
	public List<EmployeeProfile> getNewEmployee(Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.gt(EmployeeProfile.FIELD.employeeId.name(), employeeId));
		return getAll(dc);
	}

	@Override
	public List<EmployeeProfile> getNewlyUpdatedEmployee(Integer employeeId, Date updatedDate) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.le(EmployeeProfile.FIELD.employeeId.name(), employeeId));
		DetachedCriteria empDc = DetachedCriteria.forClass(Employee.class);
		empDc.setProjection(Projections.property(Employee.FIELD.id.name()));
		empDc.add(Restrictions.gt(Employee.FIELD.updatedDate.name(), updatedDate));
		dc.add(Subqueries.propertyIn(EmployeeProfile.FIELD.employeeId.name(), empDc));
		return getAll(dc);
	}

	public Page<EmployeeFileDocumentDto> getEmployeeFilesAndDocs(Integer employeeId, Integer formTypeId,
			PageSetting pageSetting) {
		String sql = "SELECT FORM_TYPE_ID, FORM_ID, DATE, DESCRIPTION, CREATED_DATE, SEQUENCE_NO FROM ( ";

		String edSql = "SELECT 1 AS FORM_TYPE_ID, ED.EMPLOYEE_DOCUMENT_ID AS FORM_ID, ED.DATE AS DATE, "
			+ "ED.REMARKS AS DESCRIPTION, ED.CREATED_DATE, ED.SEQUENCE_NO FROM "
			+ "EMPLOYEE_DOCUMENT ED "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ED.FORM_WORKFLOW_ID "
			+ "WHERE ED.EMPLOYEE_ID = ? "
			+ "AND FW.IS_COMPLETE = 1";

		String elcSql = "SELECT 2 AS FORM_TYPE_ID, ELC.EMPLOYEE_LEAVE_CREDIT_ID AS FORM_ID, ELC.DATE AS DATE, "
			+ "ELC.REMARKS AS DESCRIPTION, ELC.CREATED_DATE, ELC.SEQUENCE_NO FROM "
			+ "EMPLOYEE_LEAVE_CREDIT ELC "
			+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = ELC.EB_OBJECT_ID "
			+ "INNER JOIN ELC_LINE ELCL ON ELCL.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ELC.FORM_WORKFLOW_ID "
			+ "WHERE ELCL.EMPLOYEE_ID = ? "
			+ "AND FW.IS_COMPLETE = 1";

		String cbcSql = "SELECT 3 AS FORM_TYPE_ID, CBC.FORM_DEDUCTION_ID AS FORM_ID, "
			+ "CBC.FORM_DATE AS DATE, CBC.REMARKS AS DESCRIPTION, CBC.CREATED_DATE, CBC.SEQUENCE_NO FROM "
			+ "FORM_DEDUCTION CBC "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CBC.FORM_WORKFLOW_ID "
			+ "WHERE CBC.EMPLOYEE_ID = ? "
			+ "AND CBC.FORM_DEDUCTION_TYPE_ID = 2 "
			+ "AND FW.IS_COMPLETE = 1";

		String atdSql = "SELECT 4 AS FORM_TYPE_ID, ATD.FORM_DEDUCTION_ID AS FORM_ID, "
			+ "ATD.FORM_DATE AS DATE, ATD.REMARKS AS DESCRIPTION, ATD.CREATED_DATE, ATD.SEQUENCE_NO FROM "
			+ "FORM_DEDUCTION ATD "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ATD.FORM_WORKFLOW_ID "
			+ "WHERE ATD.EMPLOYEE_ID = ? " 
			+ " AND ATD.FORM_DEDUCTION_TYPE_ID = 1 "
			+ "AND FW.IS_COMPLETE = 1";

		String erlSql = "SELECT 5 AS FORM_TYPE_ID, ERL.EMPLOYEE_REQUEST_ID AS FORM_ID, ERL.DATE AS DATE, "
			+ "LD.REMARKS AS DESCRIPTION, ERL.CREATED_DATE, ERL.SEQUENCE_NO FROM "
			+ "EMPLOYEE_REQUEST ERL "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ERL.FORM_WORKFLOW_ID "
			+ "INNER JOIN LEAVE_DETAIL LD ON LD.EMPLOYEE_REQUEST_ID = ERL.EMPLOYEE_REQUEST_ID "
			+ "WHERE ERL.EMPLOYEE_ID = ? "
			+ "AND ERL.REQUEST_TYPE_ID = 1 "
			+ "AND FW.IS_COMPLETE = 1";

		String eroSql = "SELECT 6 AS FORM_TYPE_ID, ERO.EMPLOYEE_REQUEST_ID AS FORM_ID, ERO.DATE  AS DATE, "
			+ "OD.PURPOSE AS DESCRIPTION, ERO.CREATED_DATE, ERO.SEQUENCE_NO FROM "
			+ "EMPLOYEE_REQUEST ERO "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ERO.FORM_WORKFLOW_ID "
			+ "INNER JOIN OVERTIME_DETAIL OD ON OD.EMPLOYEE_REQUEST_ID = ERO.EMPLOYEE_REQUEST_ID "
			+ "WHERE ERO.EMPLOYEE_ID = ? "
			+ "AND ERO.REQUEST_TYPE_ID = 2 "
			+ "AND FW.IS_COMPLETE = 1";

		String panSql = "SELECT 7 AS FORM_TYPE_ID, PAN.PERSONNEL_ACTION_NOTICE_ID AS FORM_ID, "
			+ "PAN.DATE AS DATE, PAN.JUSTIFICATION AS DESCRIPTION, PAN.CREATED_DATE, PAN.SEQUENCE_NO FROM "
			+ "PERSONNEL_ACTION_NOTICE PAN "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PAN.FORM_WORKFLOW_ID "
			+ "WHERE PAN.EMPLOYEE_ID = ? "
			+ "AND FW.IS_COMPLETE = 1";

		if (formTypeId == EmployeeFileDocumentDto.FT_ALL) {
			String unionAll = " UNION ALL ";
			sql += edSql + unionAll + elcSql + unionAll + cbcSql + unionAll + atdSql
				+ unionAll + erlSql + unionAll + eroSql + unionAll + panSql;
		} else if (formTypeId == EmployeeFileDocumentDto.FT_EMPLOYEE_DOCUMENT){
			sql += edSql;
		} else if (formTypeId == EmployeeFileDocumentDto.FT_EMPLOYEE_LEAVE_CREDIT){
			sql += elcSql;
		} else if (formTypeId == EmployeeFileDocumentDto.FT_CASH_BOND){
			sql += cbcSql;
		} else if (formTypeId == EmployeeFileDocumentDto.FT_AUTH_TO_DEDUCT){
			sql += atdSql;
		} else if (formTypeId == EmployeeFileDocumentDto.FT_REQ_FOR_LEAVE){
			sql += erlSql;
		} else if (formTypeId == EmployeeFileDocumentDto.FT_REQ_FOR_OVERTIME){
			sql += eroSql;
		} else if (formTypeId == EmployeeFileDocumentDto.FT_PAN){
			sql += panSql;
		}
		sql += " ) AS EMPLOYEE_DOCUMENT_FILE ORDER BY DATE DESC, DESCRIPTION ASC, CREATED_DATE DESC";

		EmployeeFileDocHandler handler = new EmployeeFileDocHandler(employeeId,  formTypeId);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class EmployeeFileDocHandler implements QueryResultHandler<EmployeeFileDocumentDto> {

		private Integer employeeId;
		private Integer formTypeId;

		private EmployeeFileDocHandler (Integer employeeId, Integer formTypeId) {
			this.employeeId = employeeId;
			this.formTypeId = formTypeId;
		}

		@Override
		public List<EmployeeFileDocumentDto> convert(List<Object[]> queryResult) {
			List<EmployeeFileDocumentDto> empFileDocuments = new ArrayList<>();
			for (Object obj : queryResult) {
				Object[] rowResult = (Object[]) obj;
				int colNum = 0;
				Integer formTypeId = (Integer) rowResult[colNum++];
				Integer formId = (Integer) rowResult[colNum++];
				Date date = (Date) rowResult[colNum++];
				String description = (String) rowResult[colNum++];
				Integer sequenceNo = (Integer) rowResult[colNum++];
				empFileDocuments.add(EmployeeFileDocumentDto.getInstanceOf(formTypeId, formId, date, description, sequenceNo));
			}
			return empFileDocuments;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, employeeId);
			if (formTypeId == EmployeeFileDocumentDto.FT_ALL) {
				query.setParameter(++index, employeeId);
				query.setParameter(++index, employeeId);
				query.setParameter(++index, employeeId);
				query.setParameter(++index, employeeId);
				query.setParameter(++index, employeeId);
				query.setParameter(++index, employeeId);
			}

			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("FORM_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("FORM_ID", Hibernate.INTEGER);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
		}
	}

	@Override
	public boolean isUniqueRfid(EmployeeProfile employeeProfile) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeProfile.FIELD.rfid.name(), employeeProfile.getRfid()));
		if(employeeProfile.getId() != 0) {
			dc.add(Restrictions.ne(EmployeeProfile.FIELD.id.name(), employeeProfile.getId()));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public boolean isUniqueEmployeeNo(EmployeeProfile employeeProfile) {
		DetachedCriteria dc = getDetachedCriteria();

		Employee employee = employeeProfile.getEmployee();
		DetachedCriteria eeDc = DetachedCriteria.forClass(Employee.class);
		eeDc.add(Restrictions.eq(Employee.FIELD.employeeNo.name(), employeeProfile.getStrEmployeeNumber().trim()));
		eeDc.add(Restrictions.eq(Employee.FIELD.companyId.name(), employee.getCompanyId()));
		eeDc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), employee.getDivisionId()));
		if(employee.getId() != 0){
			eeDc.add(Restrictions.ne(Employee.FIELD.id.name(), employee.getId()));
		}
		eeDc.setProjection(Projections.property(Employee.FIELD.id.name()));

		dc.add(Subqueries.propertyIn(EmployeeProfile.FIELD.employeeId.name(), eeDc));
		return getAll(dc).isEmpty();
	}

	@Override
	public Page<EmployeeProfile> getEmployeeForRegularizationAsOfDate(Integer companyId, Integer divisionId,
			Date asOfDate, Boolean isOrderByLastName, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.le(EmployeeProfile.FIELD.hiredDate.name(), asOfDate));
		DetachedCriteria empDc = DetachedCriteria.forClass(Employee.class);
		empDc.setProjection(Projections.property(Employee.FIELD.id.name()));
		if(companyId != null && companyId != 0){
			empDc.add(Restrictions.eq(Employee.FIELD.companyId.name(), companyId));
		}
		if(divisionId != null && divisionId != -1){
			empDc.add(Restrictions.eq(Employee.FIELD.divisionId.name(), divisionId));
		}
		empDc.add(Restrictions.ne(Employee.FIELD.employeeTypeId.name(), EmployeeType.TYPE_REGULAR));
		dc.add(Subqueries.propertyIn(EmployeeProfile.FIELD.employeeId.name(), empDc));
		if(isOrderByLastName) {
			dc.createAlias("employee", "employee").addOrder(Order.asc("employee.lastName"));
			dc.addOrder(Order.asc("employee.firstName"));
			dc.addOrder(Order.asc("employee.middleName"));
		}
		return getAll(dc, pageSetting);
	}
}
