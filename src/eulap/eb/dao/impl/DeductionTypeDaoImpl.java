package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
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
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.DeductionTypeDao;
import eulap.eb.domain.hibernate.DeductionType;
import eulap.eb.domain.hibernate.EmployeeDeduction;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.TypeOfLeave;
import eulap.eb.domain.hibernate.User;

/**
 * Implementation class for {@Link DeductionTypeDao}

 *
 */
public class DeductionTypeDaoImpl extends BaseDao<DeductionType>implements DeductionTypeDao{

	@Override
	public Page<DeductionType> searchDeductionType(User user, String name, SearchStatus status, PageSetting setting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!name.trim().isEmpty()) {
			dc.add(Restrictions.like(DeductionType.FIELD.name.name(),
					DaoUtil.handleMysqlSpecialCharacter(StringFormatUtil.removeExtraWhiteSpaces(name))+"%"));
		}
		dc = DaoUtil.setSearchStatus(dc, DeductionType.FIELD.active.name(), status);
		dc.addOrder(Order.asc(DeductionType.FIELD.name.name()));
		return getAll(dc, setting);
	}

	@Override
	public boolean isDuplicate(DeductionType deductionType) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(DeductionType.FIELD.name.name(),
				StringFormatUtil.removeExtraWhiteSpaces(deductionType.getName())));
		return getAll(dc).size() >= 1;
	}

	@Override
	protected Class<DeductionType> getDomainClass() {
		return DeductionType.class;
	}

	@Override
	public List<DeductionType> getDeductionTypes(Integer deductionTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(TypeOfLeave.FIELD.active.name(), true);
		if(deductionTypeId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(DeductionType.FIELD.id.name(), deductionTypeId),
							Restrictions.eq(DeductionType.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		dc.addOrder(Order.asc(DeductionType.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public List<DeductionType> getDeductionTypesByPayrollId(Integer payrollId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria payrollDc = DetachedCriteria.forClass(EmployeeDeduction.class);
		payrollDc.setProjection(Projections.property(EmployeeDeduction.FIELD.deductionTypeId.name()));
		payrollDc.add(Restrictions.eq(EmployeeDeduction.FIELD.payrollId.name(), payrollId));
		dc.add(Subqueries.propertyIn(DeductionType.FIELD.id.name(), payrollDc));
		return getAll(dc);
	}

	@Override
	public List<DeductionType> getDeductionTypes(Integer payrollId, Integer employeeId) {
		List<DeductionType> deductionTypes = new ArrayList<>();
		String sql = "SELECT DEDUCTION_TYPE_ID, NAME, ACTIVE FROM ("
			+ "SELECT DD.DEDUCTION_TYPE_ID, DD.NAME, DD.ACTIVE FROM DEDUCTION_TYPE DD "
			+ "INNER JOIN EMPLOYEE_DEDUCTION ED ON ED.DEDUCTION_TYPE_ID = DD.DEDUCTION_TYPE_ID "
			+ "WHERE ED.EMPLOYEE_ID = ? AND ED.PAYROLL_ID = ? AND ED.FROM_DEDUCTION_FORM = 0 "
			+ "AND ED.ACTIVE = 1 "
			+ "UNION ALL "
			+ "SELECT DT.DEDUCTION_TYPE_ID, DT.NAME, DT.ACTIVE FROM DEDUCTION_TYPE DT "
			+ "WHERE DT.DEDUCTION_TYPE_ID NOT IN "
			+ "(SELECT EED.DEDUCTION_TYPE_ID FROM EMPLOYEE_DEDUCTION EED "
			+ "WHERE EED.EMPLOYEE_ID = ? AND EED.PAYROLL_ID = ?) "
			+ "AND DT.ACTIVE = 1) AS DEDUCTION_TYPE_TBL ";
		Session session = null;
		DeductionType deductionType = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, employeeId);
			query.setParameter(1, payrollId);
			query.setParameter(2, employeeId);
			query.setParameter(3, payrollId);

			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					deductionType = new DeductionType();
					deductionType.setId((Integer) row[0]);
					deductionType.setName((String) row[1]);
					deductionType.setActive(((Byte) row[2]).intValue() == 1);
					deductionTypes.add(deductionType);
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return deductionTypes;
	}

	@Override
	public List<DeductionType> getDeductionTypes(Integer employeeId, Integer companyId, Integer divisionId, Integer timePeriodScheduleId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria empDeductDc = DetachedCriteria.forClass(EmployeeDeduction.class);
		empDeductDc.setProjection(Projections.property(EmployeeDeduction.FIELD.deductionTypeId.name()));
		if(employeeId != null && employeeId != -1) {
			empDeductDc.add(Restrictions.eq(EmployeeDeduction.FIELD.employeeId.name(), employeeId));
		}

		DetachedCriteria payrollDc = DetachedCriteria.forClass(Payroll.class);
		payrollDc.setProjection(Projections.property(Payroll.FIELD.id.name()));
		payrollDc.add(Restrictions.eq(Payroll.FIELD.companyId.name(), companyId));
		payrollDc.add(Restrictions.eq(Payroll.FIELD.payrollTimePeriodScheduleId.name(), timePeriodScheduleId));
		if(divisionId != null && divisionId != -1) {
			payrollDc.add(Restrictions.eq(Payroll.FIELD.divisionId.name(), divisionId));
		}
		empDeductDc.add(Subqueries.propertyIn(EmployeeDeduction.FIELD.payrollId.name(), payrollDc));

		dc.add(Subqueries.propertyIn(DeductionType.FIELD.id.name(), empDeductDc));
		return getAll(dc);
	}
}
