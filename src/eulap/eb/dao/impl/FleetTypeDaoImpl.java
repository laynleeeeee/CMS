package eulap.eb.dao.impl;

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
import eulap.eb.dao.FleetTypeDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FleetType;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * DAO Implementation of {@link FleetTypeDao}

 *
 */
public class FleetTypeDaoImpl extends BaseDao<FleetType> implements FleetTypeDao {

	@Override
	protected Class<FleetType> getDomainClass() {
		return FleetType.class;
	}

	@Override
	public Page<FleetType> searchFleetTypes(String name, SearchStatus status, Integer companyId, Integer fleetCategoryId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(name != null && !name.trim().isEmpty()) {
			dc.add(Restrictions.like(FleetType.FIELD.name.name(), "%"+name.trim()+"%"));
		}
		if(companyId != null && companyId != -1){
			DetachedCriteria comapnyCrit = DetachedCriteria.forClass(Company.class);
			restrictO2OReference(dc, comapnyCrit, companyId);
		}
		if(fleetCategoryId != null && fleetCategoryId.intValue() != -1){
			dc.add(Restrictions.eq(FleetType.FIELD.fleetCategoryId.name(), fleetCategoryId));
		}
		dc = DaoUtil.setSearchStatus(dc,FleetType.FIELD.active.name(), status);
		dc.addOrder(Order.asc(FleetType.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueFleetTypeName(int fleetTypeId, String name, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(FleetType.FIELD.name.name(), name.trim()));
		dc.add(Restrictions.ne(FleetType.FIELD.id.name(), fleetTypeId));
		if(companyId != null && companyId != -1){
			DetachedCriteria comapnyCrit = DetachedCriteria.forClass(Company.class);
			restrictO2OReference(dc, comapnyCrit, companyId);
		}
		return getAll(dc).size() == 0;
	}

	@Override
	public FleetType getByRereferenceObjectId(Integer refObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(FleetType.FIELD.active.name(), true));

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.fromObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.toObjectId.name(), refObjectId));

		dc.add(Subqueries.propertyIn(FleetType.FIELD.ebObjectId.name(), obj2ObjDc));
		return get(dc);
	}
}
