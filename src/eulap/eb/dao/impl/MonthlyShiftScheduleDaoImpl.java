package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.MonthlyShiftScheduleDao;
import eulap.eb.domain.hibernate.MonthlyShiftSchedule;
import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.User;

/**
 * Implenting class of {@link MonthlyShiftSchedule}

 *
 */
public class MonthlyShiftScheduleDaoImpl extends BaseDao<MonthlyShiftSchedule> implements MonthlyShiftScheduleDao{

	@Override
	protected Class<MonthlyShiftSchedule> getDomainClass() {
		return MonthlyShiftSchedule.class;
	}

	@Override
	public Page<MonthlyShiftSchedule> getMonthlyShiftScheduleLines(Integer companyId, Integer month, Integer year, SearchStatus searchStatus,
			User user, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != -1){
			dc.add(Restrictions.eq(MonthlyShiftSchedule.FIELD.companyId.name(), companyId));
		}
		addUserCompany(dc, user);
		DetachedCriteria payrollTpDc = DetachedCriteria.forClass(PayrollTimePeriod.class);
		payrollTpDc.setProjection(Projections.property(PayrollTimePeriod.FIELD.id.name()));
		if(month != -1){
			payrollTpDc.add(Restrictions.eq(PayrollTimePeriod.FIELD.month.name(), month));
		}
		if(year != -1){
			payrollTpDc.add(Restrictions.eq(PayrollTimePeriod.FIELD.year.name(), year));
		}
		dc.add(Subqueries.propertyIn(MonthlyShiftSchedule.FIELD.payrollTimePeriodId.name(), payrollTpDc));
		dc = DaoUtil.setSearchStatus(dc, MonthlyShiftSchedule.FIELD.active.name(), searchStatus);
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueSchedule(MonthlyShiftSchedule shiftSchedule) {
		DetachedCriteria dc = getDetachedCriteria();
		if(shiftSchedule.getCompanyId() != null){
			dc.add(Restrictions.eq(MonthlyShiftSchedule.FIELD.companyId.name(), shiftSchedule.getCompanyId()));
		}
		if(shiftSchedule.getPayrollTimePeriodId() != null){
			dc.add(Restrictions.eq(MonthlyShiftSchedule.FIELD.payrollTimePeriodId.name(), shiftSchedule.getPayrollTimePeriodId()));
		}
		if(shiftSchedule.getPayrollTimePeriodScheduleId() != null){
			dc.add(Restrictions.eq(MonthlyShiftSchedule.FIELD.payrollTimePeriodScheduleId.name(), shiftSchedule.getPayrollTimePeriodScheduleId()));
		}
		dc.add(Restrictions.ne(MonthlyShiftSchedule.FIELD.id.name(), shiftSchedule.getId()));
		return getAll(dc).isEmpty();
	}

}
