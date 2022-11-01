package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FleetVoyageDao;
import eulap.eb.domain.hibernate.FleetVoyage;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * Data access object for {@link FleetVoyageDao}

 *
 */
public class FleetVoyageDaoImpl extends BaseDao<FleetVoyage> implements FleetVoyageDao{

	@Override
	protected Class<FleetVoyage> getDomainClass() {
		return FleetVoyage.class;
	}

	@Override
	public List<FleetVoyage> getFleetVoyagesByRefObjectId(Integer refObjectId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), ORType.PARENT_OR_TYPE_ID));

		dc.add(Subqueries.propertyIn(FleetVoyage.FIELD.ebObjectId.name(), ooDc));
		dc.addOrder(Order.desc(FleetVoyage.FIELD.id.name()));

		return getAll(dc);
	}

}
