package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FleetInsurancePermitRenewalDao;
import eulap.eb.domain.hibernate.FleetInsurancePermitRenewal;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * DAO Implementation of {@link FleetInsurancePermitRenewalDao}

 *
 */
public class FleetInsurancePermitRenewalDaoImpl extends BaseDao<FleetInsurancePermitRenewal> implements FleetInsurancePermitRenewalDao{

	@Override
	protected Class<FleetInsurancePermitRenewal> getDomainClass() {
		return FleetInsurancePermitRenewal.class;
	}

	@Override
	public List<FleetInsurancePermitRenewal> getFleetIPRByRefObjectId(Integer refObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(FleetInsurancePermitRenewal.FIELD.active.name(), true));

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));

		dc.add(Subqueries.propertyIn(FleetInsurancePermitRenewal.FIELD.ebObjectId.name(), obj2ObjDc));
		return getAll(dc);
	}
}
