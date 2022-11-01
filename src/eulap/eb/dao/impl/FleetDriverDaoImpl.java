package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FleetDriverDao;
import eulap.eb.domain.hibernate.FleetDriver;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * Implementing class of {@link FleetDriverDao}

 *
 */
public class FleetDriverDaoImpl extends BaseDao<FleetDriver> implements FleetDriverDao{

	@Override
	protected Class<FleetDriver> getDomainClass() {
		return FleetDriver.class;
	}

	@Override
	public List<FleetDriver> getByRefObject(int refObjectId, boolean activeOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		if (activeOnly) {
			dc.add(Restrictions.eq(FleetDriver.FIELD.active.name(), true));
		}

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), ORType.PARENT_OR_TYPE_ID));
		dc.add(Subqueries.propertyIn(FleetDriver.FIELD.ebObjectId.name(), ooDc));
		return getAll(dc);
	}

}
