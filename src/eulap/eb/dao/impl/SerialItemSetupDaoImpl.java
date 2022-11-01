package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SerialItemSetupDao;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.SerialItemSetup;

/**
 * DAO Implementation of {@link SerialItemSetup}

 *
 */
public class SerialItemSetupDaoImpl extends BaseDao<SerialItemSetup> implements SerialItemSetupDao{

	@Override
	protected Class<SerialItemSetup> getDomainClass() {
		return SerialItemSetup.class;
	}

	@Override
	public SerialItemSetup getSerialItemSetupByItemId(Integer itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SerialItemSetup.FIELD.itemId.name(), itemId));
		return get(dc);
	}

	@Override
	public List<SerialItemSetup> getRetailItems(Integer companyId, Integer itemCategoryId, String stockCode,
			boolean isSerialized, boolean isActive, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SerialItemSetup.FIELD.serializedItem.name(), isSerialized));
		DetachedCriteria itemDc = DetachedCriteria.forClass(Item.class);
		itemDc.setProjection(Projections.property(Item.FIELD.id.name()));
		if (stockCode != null && !stockCode.trim().isEmpty()) {
			itemDc.add(Restrictions.or(Restrictions.like(Item.FIELD.description.name(), "%" + stockCode.trim() + "%"),
					Restrictions.like(Item.FIELD.stockCode.name(), "%" + stockCode.trim() + "%")));
		}
		if (isActive) {
			itemDc.add(Restrictions.eq(Item.FIELD.active.name(), true));
		}
		if (itemCategoryId != null) {
			itemDc.add(Restrictions.eq(Item.FIELD.itemCategoryId.name(), itemCategoryId));
		}
		dc.add(Subqueries.propertyIn(SerialItemSetup.FIELD.itemId.name(), itemDc));
		if (companyId != null) {
			DetachedCriteria srpDc = DetachedCriteria.forClass(ItemSrp.class);
			srpDc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
			srpDc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
			srpDc.add(Restrictions.eq(ItemSrp.FIELD.companyId.name(), companyId));
			if (divisionId != null) {
				srpDc.add(Restrictions.eq(ItemSrp.FIELD.divisionId.name(), divisionId));
			}
			dc.add(Subqueries.propertyIn(SerialItemSetup.FIELD.itemId.name(), srpDc));
		}
		return getAll(dc);
	}

	@Override
	public SerialItemSetup getRetailItem(String stockCode, Integer companyId, Integer warehouseId,
			boolean isSerialized, boolean isActiveOnly, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria itemDc = DetachedCriteria.forClass(Item.class);
		itemDc.setProjection(Projections.property(Item.FIELD.id.name()));
		if (stockCode != null && !stockCode.trim().isEmpty()) {
			itemDc.add(Restrictions.eq(Item.FIELD.stockCode.name(), stockCode.trim()));
		}
		if (isActiveOnly) {
			itemDc.add(Restrictions.eq(Item.FIELD.active.name(), true));
		}
		dc.add(Subqueries.propertyIn(SerialItemSetup.FIELD.itemId.name(), itemDc));
		if (companyId != null) {
			DetachedCriteria srpDc = DetachedCriteria.forClass(ItemSrp.class);
			srpDc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
			srpDc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
			srpDc.add(Restrictions.eq(ItemSrp.FIELD.companyId.name(), companyId));
			if (divisionId != null) {
				srpDc.add(Restrictions.eq(ItemSrp.FIELD.divisionId.name(), divisionId));
			}
			dc.add(Subqueries.propertyIn(SerialItemSetup.FIELD.itemId.name(), srpDc));
		}

		dc.add(Restrictions.eq(SerialItemSetup.FIELD.serializedItem.name(), isSerialized));
		return get(dc);
	}
}
