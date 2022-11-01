package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.MonthlyShiftScheduleLineDao;
import eulap.eb.domain.hibernate.MonthlyShiftSchedule;
import eulap.eb.domain.hibernate.MonthlyShiftScheduleLine;

/**
 * Implenting class of {@link MonthlyShiftScheduleLineDao}

 *
 */
public class MonthlyShiftScheduleLineDaoImpl extends BaseDao<MonthlyShiftScheduleLine> implements MonthlyShiftScheduleLineDao{

	@Override
	protected Class<MonthlyShiftScheduleLine> getDomainClass() {
		return MonthlyShiftScheduleLine.class;
	}

	@Override
	public MonthlyShiftScheduleLine getByPeriodAndSchedule(int payrollTimePeriodId, int payrollTimePeriodScheduleId,
			int employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(MonthlyShiftScheduleLine.FIELD.employeeId.name(), employeeId));

		DetachedCriteria mssDc = DetachedCriteria.forClass(MonthlyShiftSchedule.class);
		mssDc.setProjection(Projections.property(MonthlyShiftSchedule.FIELD.id.name()));
		mssDc.add(Restrictions.eq(MonthlyShiftSchedule.FIELD.payrollTimePeriodId.name(), payrollTimePeriodId));
		mssDc.add(Restrictions.eq(MonthlyShiftSchedule.FIELD.payrollTimePeriodScheduleId.name(), payrollTimePeriodScheduleId));

		dc.add(Subqueries.propertyIn(MonthlyShiftScheduleLine.FIELD.monthlyShiftScheduleId.name(), mssDc));
		return get(dc);
	}

}
