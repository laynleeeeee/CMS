package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RriBagQuantityDao;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RriBagQuantity;

/**
 * Implementing class of {@code RriBagQuantityDao}

 *
 */
public class RriBagQuantityDaoImpl extends BaseDao<RriBagQuantity> implements RriBagQuantityDao{

	@Override
	protected Class<RriBagQuantity> getDomainClass() {
		return RriBagQuantity.class;
	}

	@Override
	public List<RriBagQuantity> getRriBagQuantitiesByRefObjectId(Integer refObjectId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));

		dc.add(Subqueries.propertyIn(RriBagQuantity.FIELD.ebObjectId.name(), obj2ObjDc));
		dc.add(Restrictions.eq(RriBagQuantity.FIELD.active.name(), true));
		return getAll(dc);
	}

}
