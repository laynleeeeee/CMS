package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FleetIncidentDao;
import eulap.eb.domain.hibernate.FleetIncident;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * Implementation class of {@link FleetIncidentDao}

 *
 */
public class FleetIncidentDaoImpl extends BaseDao<FleetIncident> implements FleetIncidentDao{

	@Override
	protected Class<FleetIncident> getDomainClass() {
		return FleetIncident.class;
	}

	@Override
	public List<FleetIncident> getFleetIncidentsByRefObject(Integer refObjectId, Boolean isActiveOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		if (isActiveOnly) {
			dc.add(Restrictions.eq(FleetIncident.FIELD.active.name(), true));
		}

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), ORType.PARENT_OR_TYPE_ID));
		dc.add(Subqueries.propertyIn(FleetIncident.FIELD.ebObjectId.name(), ooDc));
		return getAll(dc);
	}

}
