package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.PrMainProductDao;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PrMainProduct;
import eulap.eb.domain.hibernate.RriBagQuantity;

/**
 * Implementing class of {@link PrMainProductDao}

 *
 */
public class PrMainProductDaoImpl extends BaseDao<PrMainProduct> implements PrMainProductDao{

	@Override
	protected Class<PrMainProduct> getDomainClass() {
		return PrMainProduct.class;
	}

	@Override
	public PrMainProduct getRefObjectId(Integer refObjectId, Integer itemId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));

		dc.add(Subqueries.propertyIn(RriBagQuantity.FIELD.ebObjectId.name(), obj2ObjDc));
		dc.add(Restrictions.eq(PrMainProduct.FIELD.itemId.name(), itemId));
		return get(dc);
	}
}
