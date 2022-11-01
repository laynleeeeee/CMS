package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.DailyShiftScheduleDao;
import eulap.eb.domain.hibernate.DailyShiftSchedule;
import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.User;

public class DailyShiftScheduleDaoImpl extends BaseDao<DailyShiftSchedule> implements DailyShiftScheduleDao{

	@Override
	protected Class<DailyShiftSchedule> getDomainClass() {
		return DailyShiftSchedule.class;
	}

	@Override
	public Page<DailyShiftSchedule> getDailyShiftScheduleLines(Integer companyId,
			Integer month, Integer year, User user,
			PageSetting pageSetting, boolean isAddOrder) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		if(companyId != -1){
			dc.add(Restrictions.eq(DailyShiftSchedule.FIELD.companyId.name(), companyId));
		}
		DetachedCriteria payrollTpDc = DetachedCriteria.forClass(PayrollTimePeriod.class);
		payrollTpDc.setProjection(Projections.property(PayrollTimePeriod.FIELD.id.name()));
		if(month != -1){
			payrollTpDc.add(Restrictions.eq(PayrollTimePeriod.FIELD.month.name(), month));
		}
		if(year != -1){
			payrollTpDc.add(Restrictions.eq(PayrollTimePeriod.FIELD.year.name(), year));
		}

		dc.add(Subqueries.propertyIn(DailyShiftSchedule.FIELD.payrollTimePeriodId.name(), payrollTpDc));

		if(isAddOrder) {
			dc.createAlias("payrollTimePeriod", "ptp");
			dc.addOrder(Order.desc("ptp.year"));
			dc.addOrder(Order.desc("ptp.month"));
		}

		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueSchedule(DailyShiftSchedule shiftSchedule) {
		DetachedCriteria dc = getDetachedCriteria();
		if(shiftSchedule.getCompanyId() != null){
			dc.add(Restrictions.eq(DailyShiftSchedule.FIELD.companyId.name(), shiftSchedule.getCompanyId()));
		}
		if(shiftSchedule.getDivisionId() != null){
			dc.add(Restrictions.eq(DailyShiftSchedule.FIELD.divisionId.name(), shiftSchedule.getDivisionId()));
		}
		if(shiftSchedule.getPayrollTimePeriodId() != null){
			dc.add(Restrictions.eq(DailyShiftSchedule.FIELD.payrollTimePeriodId.name(), shiftSchedule.getPayrollTimePeriodId()));
		}
		if(shiftSchedule.getPayrollTimePeriodScheduleId() != null){
			dc.add(Restrictions.eq(DailyShiftSchedule.FIELD.payrollTimePeriodScheduleId.name(), shiftSchedule.getPayrollTimePeriodScheduleId()));
		}
		dc.add(Restrictions.ne(DailyShiftSchedule.FIELD.id.name(), shiftSchedule.getId()));
		return getAll(dc).isEmpty();
	}
}
