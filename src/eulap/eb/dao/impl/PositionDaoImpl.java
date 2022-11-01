package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.PositionDao;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.Position;

/**
 * Implementing class of {@link PositionDao}

 *
 */
public class PositionDaoImpl extends BaseDao<Position> implements PositionDao{

	@Override
	protected Class<Position> getDomainClass() {
		return Position.class;
	}
	
	@Override
	public List<Position> getPositions(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(Position.FIELD.name.name(), "%" +name+ "%"));
		dc.add(Restrictions.eq(Position.FIELD.active.name(), true));
		dc.addOrder(Order.asc(Position.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public Position getPositionByName(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Position.FIELD.name.name(), name));
		dc.add(Restrictions.eq(Position.FIELD.active.name(), true));
		return get(dc);
	}

	@Override
	public List<Position> getAllPositions() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Position.FIELD.active.name(), true));
		dc.addOrder(Order.asc(Position.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public Page<Position> searchPosition(String name, Integer status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		boolean result = status == 1;
		if (name != null)
			dc.add(Restrictions.like(Position.FIELD.name.name(), "%" +name+ "%"));
		if (status != -1)
			dc.add(Restrictions.eq(Position.FIELD.active.name(), result));
		dc.addOrder(Order.asc(Position.FIELD.name.name()));
		return getAll(dc,pageSetting);
	}

	@Override
	public boolean isUniquePosition(Position position) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Position.FIELD.name.name(), position.getName()));
		if (position.getId() != 0)
			dc.add(Restrictions.not(Restrictions.eq(Position.FIELD.id.name(), position.getId())));
		return getAll(dc).size() < 1;
	}

	@Override
	public Position getPositionByEmployeeId(Integer employeeId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria employeeDc = DetachedCriteria.forClass(Employee.class);
		employeeDc.setProjection(Projections.property(Employee.FIELD.positionId.name()));
		if(employeeId != null) {
			employeeDc.add(Restrictions.eq(Employee.FIELD.id.name(), employeeId));
		}
		dc.add(Subqueries.propertyIn(Position.FIELD.id.name(), employeeDc));
		return get(dc);
	}
}
