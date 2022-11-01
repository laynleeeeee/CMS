package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RriBagDiscountDao;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RriBagDiscount;

/**
 * Implementation class of {@link RriBagDiscountDao}

 *
 */
public class RriBagDiscountDaoImpl extends BaseDao<RriBagDiscount> implements RriBagDiscountDao{

	@Override
	protected Class<RriBagDiscount> getDomainClass() {
		return RriBagDiscount.class;
	}

	@Override
	public List<RriBagDiscount> getRriDiscountsByRefObjectId(Integer refObjectId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));

		dc.add(Subqueries.propertyIn(RriBagDiscount.FIELD.ebObjectId.name(), obj2ObjDc));
		dc.add(Restrictions.eq(RriBagDiscount.FIELD.active.name(), true));
		return getAll(dc);
	}

}
