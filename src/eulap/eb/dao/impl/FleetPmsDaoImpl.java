package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FleetPmsDao;
import eulap.eb.domain.hibernate.FleetPms;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * DAO Implementation of {@link FleetPmsDao}

 *
 */
public class FleetPmsDaoImpl extends BaseDao<FleetPms> implements FleetPmsDao {

	@Override
	protected Class<FleetPms> getDomainClass() {
		return FleetPms.class;
	}

	@Override
	public List<FleetPms> getFleetPmsByRefObjectId(Integer refObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(FleetPms.FIELD.active.name(), true));

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), ORType.PARENT_OR_TYPE_ID));

		dc.add(Subqueries.propertyIn(FleetPms.FIELD.ebObjectId.name(), ooDc));
		dc.addOrder(Order.desc(FleetPms.FIELD.id.name()));

		return getAll(dc);
	}
}
