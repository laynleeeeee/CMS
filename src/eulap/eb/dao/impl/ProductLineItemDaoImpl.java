package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ProductLineItemDao;
import eulap.eb.domain.hibernate.ProductLine;
import eulap.eb.domain.hibernate.ProductLineItem;

/**
 * Implementing class of {@link ProductLineItemDao}

 *
 */
public class ProductLineItemDaoImpl extends BaseDao<ProductLineItem> implements ProductLineItemDao{

	@Override
	protected Class<ProductLineItem> getDomainClass() {
		return ProductLineItem.class;
	}

	@Override
	public List<ProductLineItem> getRawMaterials(int mainItemId) {
		DetachedCriteria pliDc = getDetachedCriteria();
		DetachedCriteria prodLineDc = DetachedCriteria.forClass(ProductLine.class);
		prodLineDc.setProjection(Projections.property(ProductLine.FIELD.id.name()));
		prodLineDc.add(Restrictions.eq(ProductLine.FIELD.mainItemId.name(), mainItemId));
		pliDc.add(Subqueries.propertyIn(ProductLineItem.FIELD.productLineId.name(), prodLineDc));
		return getAll(pliDc);
	}
}
