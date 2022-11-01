package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FleetDryDockDao;
import eulap.eb.domain.hibernate.FleetDryDock;
import eulap.eb.domain.hibernate.FleetPms;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * Implementation class for {@link FleetDryDockDao}

 *
 */
public class FleetDryDockDaoImpl extends BaseDao<FleetDryDock> implements FleetDryDockDao{

	@Override
	protected Class<FleetDryDock> getDomainClass() {
		return FleetDryDock.class;
	}

	@Override
	public List<FleetDryDock> getFleetDryDockings(Integer refObjectId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), ORType.PARENT_OR_TYPE_ID));

		dc.add(Subqueries.propertyIn(FleetDryDock.FIELD.ebObjectId.name(), ooDc));
		dc.addOrder(Order.desc(FleetDryDock.FIELD.id.name()));

		return getAll(dc);
	}

}
