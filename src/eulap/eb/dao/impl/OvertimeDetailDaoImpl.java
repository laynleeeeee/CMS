package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.OvertimeDetailDao;
import eulap.eb.domain.hibernate.EmployeeRequest;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.OvertimeDetail;

/**
 * Data access object implementation class for {@link OvertimeDetailDao}

 *
 */
public class OvertimeDetailDaoImpl extends BaseDao<OvertimeDetail> implements OvertimeDetailDao {

	@Override
	protected Class<OvertimeDetail> getDomainClass() {
		return OvertimeDetail.class;
	}

	@Override
	public OvertimeDetail getOvertimeDetailByRequestId(Integer employeeRequestId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(employeeRequestId != 0) {
			dc.add(Restrictions.eq(OvertimeDetail.FIELD.employeeRequestId.name(), employeeRequestId));
		}
		return get(dc);
	}

	@Override
	public OvertimeDetail getByEmployeeAndDate(Integer employeeId, Date date, boolean isComplete) {
		return get(getDCByEmployeeAndDate(employeeId, date, isComplete));
	}

	@Override
	public boolean hasRequestedOvertime(Integer pId, Integer employeeId, Date date) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(OvertimeDetail.FIELD.overtimeDate.name(), date));

		DetachedCriteria erDc = DetachedCriteria.forClass(EmployeeRequest.class);
		erDc.setProjection(Projections.property(EmployeeRequest.FIELD.id.name()));
		erDc.add(Restrictions.eq(EmployeeRequest.FIELD.employeeId.name(), employeeId));
		if (pId != null) {
			erDc.add(Restrictions.ne(EmployeeRequest.FIELD.id.name(), pId));
		}

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		erDc.add(Subqueries.propertyIn(EmployeeRequest.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Subqueries.propertyIn(OvertimeDetail.FIELD.employeeRequestId.name(), erDc));

		return getAll(dc).size() > 0;
	}

	@Override
	public List<OvertimeDetail> getAllByEmployeeAndDate(Integer employeeId, Date date, boolean isComplete) {
		return getAll(getDCByEmployeeAndDate(employeeId, date, isComplete));
	}

	private DetachedCriteria getDCByEmployeeAndDate(Integer employeeId, Date date, boolean isComplete) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(OvertimeDetail.FIELD.overtimeDate.name(), date));

		DetachedCriteria erDc = DetachedCriteria.forClass(EmployeeRequest.class);
		erDc.setProjection(Projections.property(EmployeeRequest.FIELD.id.name()));
		erDc.add(Restrictions.eq(EmployeeRequest.FIELD.employeeId.name(), employeeId));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		if (isComplete) {
			fwDc.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		} else {
			fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		}
		erDc.add(Subqueries.propertyIn(EmployeeRequest.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Subqueries.propertyIn(OvertimeDetail.FIELD.employeeRequestId.name(), erDc));
		return dc;
	}
}
