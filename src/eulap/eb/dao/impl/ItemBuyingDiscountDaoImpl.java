package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ItemBuyingDiscountDao;
import eulap.eb.domain.hibernate.ItemBuyingDiscount;

/**
 * DAO Implementation class of {@link ItemBuyingDiscountDao}

 *
 */
public class ItemBuyingDiscountDaoImpl extends BaseDao<ItemBuyingDiscount> implements ItemBuyingDiscountDao {

	@Override
	protected Class<ItemBuyingDiscount> getDomainClass() {
		return ItemBuyingDiscount.class;
	}

	@Override
	public List<ItemBuyingDiscount> getBuyingDiscounts(Integer itemId, Integer companyId) {
		DetachedCriteria bDiscountDc = getDetachedCriteria();
		bDiscountDc.add(Restrictions.eq(ItemBuyingDiscount.FIELD.itemId.name(), itemId));
		bDiscountDc.add(Restrictions.eq(ItemBuyingDiscount.FIELD.companyId.name(), companyId));
		bDiscountDc.add(Restrictions.eq(ItemBuyingDiscount.FIELD.active.name(), true));
		return getAll(bDiscountDc);
	}
}
