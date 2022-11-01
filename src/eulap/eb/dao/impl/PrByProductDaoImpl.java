package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.PrByProductDao;
import eulap.eb.dao.PrRawMaterialsItemDao;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PrByProduct;
import eulap.eb.domain.hibernate.RriBagQuantity;

/**
 * Implementing class of {@link PrRawMaterialsItemDao}

 *
 */
public class PrByProductDaoImpl extends BaseDao<PrByProduct> implements PrByProductDao{

	@Override
	protected Class<PrByProduct> getDomainClass() {
		return PrByProduct.class;
	}

	@Override
	public PrByProduct getByRefObjectId(Integer refObjectId, Integer itemId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));

		dc.add(Subqueries.propertyIn(RriBagQuantity.FIELD.ebObjectId.name(), obj2ObjDc));
		dc.add(Restrictions.eq(PrByProduct.FIELD.itemId.name(), itemId));
		return get(dc);
	}
}
