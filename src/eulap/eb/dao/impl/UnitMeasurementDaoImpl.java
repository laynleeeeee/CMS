package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.UnitMeasurement;

/**
 * The implementation class of {@link UnitMeasurementDao}

 */
public class UnitMeasurementDaoImpl extends BaseDao<UnitMeasurement> implements UnitMeasurementDao {

	@Override
	protected Class<UnitMeasurement> getDomainClass() {
		return UnitMeasurement.class;
	}

	@Override
	public UnitMeasurement getUnitMeasurementById(int unitMeasurementId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UnitMeasurement.FIELD.id.name(), unitMeasurementId));
		return get(dc);
	}

	@Override
	public boolean isUniqueUnitMeasurement(UnitMeasurement unitMeasurement,
			int unitMeasurementId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UnitMeasurement.FIELD.name.name(), unitMeasurement.getName().trim()));
		if (unitMeasurement.getId() != 0)
			dc.add(Restrictions.not(Restrictions.eq(UnitMeasurement.FIELD.id.name(), unitMeasurementId)));
		return getAll(dc).size() < 1;
	}

	@Override
	public Page<UnitMeasurement> searchUnitMeasurements(String name,
			boolean isActive, boolean isAllSelected, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if( name != null && !name.trim().isEmpty())
			dc.add(Restrictions.like(UnitMeasurement.FIELD.name.name(), "%" + name.trim() + "%"));
			if(!isAllSelected)
				dc.add(Restrictions.eq(UnitMeasurement.FIELD.active.name(), isActive));
		dc.addOrder(Order.asc(UnitMeasurement.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<UnitMeasurement> getAllUnitMeasurements() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(Order.asc(UnitMeasurement.FIELD.name.name()));
		return getAll(dc, new PageSetting(1, 25));
	}

	@Override
	public List<UnitMeasurement> getActiveUnitMeasurements() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UnitMeasurement.FIELD.active.name(), true));
		dc.addOrder(Order.asc(UnitMeasurement.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public List<UnitMeasurement> getActiveUnitMeasurements(String name, Integer id, Boolean noLimit) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UnitMeasurement.FIELD.active.name(), true));

		if (name != null && !name.trim().isEmpty() && id == null) {
			dc.add(Restrictions.like(UnitMeasurement.FIELD.name.name(), "%" + name.trim() + "%"));
		} else if (id != null) {
			dc.add(Restrictions.eq(UnitMeasurement.FIELD.id.name(), id));
		}

		dc.addOrder(Order.asc(UnitMeasurement.FIELD.name.name()));
		if (noLimit == null)
			dc.getExecutableCriteria(getSession()).setMaxResults(10);
		return getAll(dc);
	}

	@Override
	public UnitMeasurement getUMByName(String unitMeasurementName) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UnitMeasurement.FIELD.name.name(), unitMeasurementName.trim()));
		return get(dc);
	}

	@Override
	public UnitMeasurement getKilo() {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion critKilo = Restrictions.eq(UnitMeasurement.FIELD.name.name(), "KILO");
		Criterion critKilogram = Restrictions.or( Restrictions.eq(UnitMeasurement.FIELD.name.name(), "KILOGRAM"),
				 Restrictions.eq(UnitMeasurement.FIELD.name.name(), "KG"));
		dc.add(Restrictions.or(critKilo, critKilogram));
		return get(dc);
	}
}
