package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.ProductLineDao;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ProductLine;
import eulap.eb.domain.hibernate.ProductLineItem;

/**
 * Implementing class of {@link ProductLineDao}

 *
 */
public class ProductLineDaoImpl extends BaseDao<ProductLine> implements ProductLineDao{

	@Override
	protected Class<ProductLine> getDomainClass() {
		return ProductLine.class;
	}

	@Override
	public boolean isDuplicateProductline(ProductLine productLine) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ProductLine.FIELD.mainItemId.name(), productLine.getMainItemId()));
		if(productLine.getId() != 0){
			dc.add(Restrictions.ne(ProductLine.FIELD.id.name(), productLine.getId()));
		}
		return !getAll(dc).isEmpty();
	}

	@Override
	public Page<ProductLine> getProductList(String productLine, String rawMaterial, SearchStatus searchStatus,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(productLine != null && !productLine.isEmpty()){
			DetachedCriteria itemLineDc = DetachedCriteria.forClass(Item.class);
			itemLineDc.setProjection(Projections.property(Item.FIELD.id.name()));
			itemLineDc.add(Restrictions.or(Restrictions.like(Item.FIELD.stockCode.name(), "%"+productLine+"%"),
					Restrictions.like(Item.FIELD.description.name(), "%"+productLine+"%")));
			dc.add(Subqueries.propertyIn(ProductLine.FIELD.mainItemId.name(), itemLineDc));
		}
		if(rawMaterial != null && !rawMaterial.isEmpty()){
			DetachedCriteria productLineItemDc = DetachedCriteria.forClass(ProductLineItem.class);
			productLineItemDc.setProjection(Projections.property(ProductLineItem.FIELD.productLineId.name()));
			DetachedCriteria itemRawDc = DetachedCriteria.forClass(Item.class);
			itemRawDc.setProjection(Projections.property(Item.FIELD.id.name()));
			itemRawDc.add(Restrictions.or(Restrictions.like(Item.FIELD.stockCode.name(), "%"+rawMaterial+"%"),
					Restrictions.like(Item.FIELD.description.name(), "%"+rawMaterial+"%")));
			productLineItemDc.add(Subqueries.propertyIn(ProductLineItem.FIELD.itemId.name(), itemRawDc));
			dc.add(Subqueries.propertyIn(ProductLine.FIELD.id.name(), productLineItemDc));
		}
		dc = DaoUtil.setSearchStatus(dc, ProductLine.FIELD.active.name(), searchStatus);
		dc.addOrder(Order.asc(ProductLine.FIELD.id.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public ProductLine getByItem(int itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ProductLine.FIELD.mainItemId.name(), itemId));
		return get(dc) ;
	}

	public boolean hasConfig(int itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ProductLine.FIELD.mainItemId.name(), itemId));
		dc.add(Restrictions.eq(ProductLine.FIELD.menu.name(), true));
		return getAll(dc).size() > 0;
	}
}
