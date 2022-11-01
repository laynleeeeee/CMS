package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
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
import eulap.eb.dao.EmployeeShiftDao;
import eulap.eb.domain.hibernate.DailyShiftSchedule;
import eulap.eb.domain.hibernate.DailyShiftScheduleLine;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.User;

/**
 * DAO implementation of EmployeeShiftDao

 */
public class EmployeeShiftDaoImpl extends BaseDao<EmployeeShift> implements EmployeeShiftDao{

	@Override
	protected Class<EmployeeShift> getDomainClass() {
		return EmployeeShift.class;
	}

	@Override
	public Page<EmployeeShift> getEmployeeShifts(String startShift, String endShift, Double dailyWorkingHours,
			SearchStatus status, User user, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		if (startShift != null && !startShift.trim().isEmpty()) {
			dc.add(Restrictions.like(EmployeeShift.FIELD.firstHalfShiftStart.name(), "%" + startShift.trim() + "%"));
		}
		if (endShift != null && !endShift.trim().isEmpty()) {
			dc.add(Restrictions.like(EmployeeShift.FIELD.secondHalfShiftEnd.name(), "%" + endShift.trim() + "%"));
		}
		if (dailyWorkingHours != null) {
			dc.add(Restrictions.sqlRestriction("DAILY_WORKING_HOURS LIKE ? ", "%" + dailyWorkingHours.toString() + "%", Hibernate.STRING));
		}
		dc = DaoUtil.setSearchStatus(dc, EmployeeShift.FIELD.active.name(), status);
		dc.addOrder(Order.asc(EmployeeShift.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<EmployeeShift> getEmployeeShiftByCompanyId(Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeShift.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(EmployeeShift.FIELD.active.name(), true));
		return getAll(dc);
	}

	@Override
	public Page<EmployeeShift> getCscEmployeeShifts(Integer companyId, String name, String firstHalfShiftStart, String firstHalfShiftEnd,
			String secondHalfShiftStart, String secondHalfShiftEnd, Double dailyWorkingHours,
			SearchStatus status, User user,PageSetting pageSetting) {
			DetachedCriteria dc = getDetachedCriteria();
			addUserCompany(dc, user);
			if (companyId != null && companyId.intValue() != -1) {
				dc.add(Restrictions.eq(EmployeeShift.FIELD.companyId.name(), companyId));
			}
			if (name != null && !name.trim().isEmpty()) {
				dc.add(Restrictions.like(EmployeeShift.FIELD.name.name(), "%" + name.trim() + "%"));
			}
			if(firstHalfShiftStart != null && !firstHalfShiftStart.trim().isEmpty()){
				dc.add(Restrictions.like(EmployeeShift.FIELD.firstHalfShiftStart.name(), "%" + firstHalfShiftStart.trim() + "%"));
			}
			if(firstHalfShiftEnd != null && !firstHalfShiftEnd.trim().isEmpty()){
				dc.add(Restrictions.like(EmployeeShift.FIELD.firstHalfShiftEnd.name(), "%" + firstHalfShiftEnd.trim() + "%"));
			}
			if(secondHalfShiftStart != null && !secondHalfShiftStart.trim().isEmpty()){
				dc.add(Restrictions.like(EmployeeShift.FIELD.secondHalfShiftStart.name(), "%" + secondHalfShiftStart.trim() + "%"));
			}
			if(secondHalfShiftEnd != null && !secondHalfShiftEnd.trim().isEmpty()){
				dc.add(Restrictions.like(EmployeeShift.FIELD.secondHalfShiftEnd.name(), "%" + secondHalfShiftEnd.trim() + "%"));
			}
			if (dailyWorkingHours != null) {
				dc.add(Restrictions.sqlRestriction("DAILY_WORKING_HOURS LIKE ? ", "%" + dailyWorkingHours.toString() + "%", Hibernate.STRING));
			}
			dc = DaoUtil.setSearchStatus(dc, EmployeeShift.FIELD.active.name(), status);
			dc.addOrder(Order.asc(EmployeeShift.FIELD.dayOff.name()));
			dc.addOrder(Order.asc(EmployeeShift.FIELD.firstHalfShiftStart.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueName(EmployeeShift employeeShift) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeShift.FIELD.name.name(), employeeShift.getName().trim()));
		if (employeeShift.getCompanyId() != null) {
			dc.add(Restrictions.eq(Employee.FIELD.companyId.name(), employeeShift.getCompanyId()));
		}
		if(employeeShift.getId() != 0){
			dc.add(Restrictions.ne(EmployeeShift.FIELD.id.name(), employeeShift.getId()));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public boolean hasActive(EmployeeShift employeeShift) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.ne(EmployeeShift.FIELD.id.name(), employeeShift.getId()));
		dc.add(Restrictions.eq(EmployeeShift.FIELD.companyId.name(), employeeShift.getCompanyId()));
		dc.add(Restrictions.eq(EmployeeShift.FIELD.active.name(), true));
		return getAll(dc).size() > 0;
	}

	@Override
	public EmployeeShift getBySchedule(Integer companyId, Integer payrollTimePeriodId,
			Integer payrollTimePeriodScheduleId, Integer employeeId, Date date) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria dssDc = DetachedCriteria.forClass(DailyShiftSchedule.class);
		dssDc.setProjection(Projections.property(DailyShiftSchedule.FIELD.id.name()));
		if (companyId != null) {
			dssDc.add(Restrictions.eq(DailyShiftSchedule.FIELD.companyId.name(), companyId));
		}
		if (payrollTimePeriodId != null) {
			dssDc.add(Restrictions.eq(DailyShiftSchedule.FIELD.payrollTimePeriodId.name(),
					payrollTimePeriodId));
		}
		if (payrollTimePeriodScheduleId != null) {
			dssDc.add(Restrictions.eq(DailyShiftSchedule.FIELD.payrollTimePeriodScheduleId.name(),
					payrollTimePeriodScheduleId));
		}
		DetachedCriteria dsslDc = DetachedCriteria.forClass(DailyShiftScheduleLine.class);
		dsslDc.setProjection(Projections.property(DailyShiftScheduleLine.FIELD.employeeShiftId.name()));
		dsslDc.add(Restrictions.eq(DailyShiftScheduleLine.FIELD.employeeId.name(), employeeId));
		dsslDc.add(Restrictions.eq(DailyShiftScheduleLine.FIELD.date.name(), date));
		dsslDc.add(Subqueries.propertyIn(DailyShiftScheduleLine.FIELD.dailyShiftScheduleId.name(), dssDc));
		dc.add(Subqueries.propertyIn(EmployeeShift.FIELD.id.name(), dsslDc));
		return get(dc);
	}

	@Override
	public List<EmployeeShift> getLatestEmployeeShift(Date updatedDate) {
		DetachedCriteria dc = getDetachedCriteria();
		if(updatedDate != null){
			dc.add(Restrictions.gt(EmployeeShift.FIELD.updatedDate.name(), updatedDate));
		}
		return getAll(dc);
	}

	@Override
	public EmployeeShift getByNameAndCompanyId(String shiftName, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EmployeeShift.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(EmployeeShift.FIELD.name.name(), shiftName));
		dc.add(Restrictions.eq(EmployeeShift.FIELD.active.name(), true));
		return get(dc);
	}

}
