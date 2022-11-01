package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ItemDiscountDao;
import eulap.eb.domain.hibernate.ItemDiscount;

/**
 * Implementing class of {@link ItemDiscountDao}

 *
 */
public class ItemDiscountDaoImpl extends BaseDao<ItemDiscount> implements ItemDiscountDao{

	@Override
	protected Class<ItemDiscount> getDomainClass() {
		return ItemDiscount.class;
	}
	
	@Override
	public List<ItemDiscount> getItemDiscountsByItem(Integer itemId, boolean activeOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemDiscount.FIELD.itemId.name(), itemId));
		if (activeOnly)
			dc.add(Restrictions.eq(ItemDiscount.FIELD.active.name(), true));
		return getAll(dc);
	}

	@Override
	public List<ItemDiscount> getIDsByItemIdAndCompany(int itemId,
			Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemDiscount.FIELD.itemId.name(), itemId));
		dc.add(Restrictions.eq(ItemDiscount.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(ItemDiscount.FIELD.active.name(), true));
		return getAll(dc);
	}

	@Override
	public List<ItemDiscount> getDiscountsWithSlctdDiscId(Integer itemId,
			Integer companyId, Integer itemDiscountId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(companyId != null){
			dc.add(Restrictions.eq(ItemDiscount.FIELD.companyId.name(), companyId));
		}
		if(itemId != null) {
			dc.add(Restrictions.eq(ItemDiscount.FIELD.itemId.name(), itemId));
		}
		Criterion criterion = Restrictions.eq(ItemDiscount.FIELD.active.name(), true);
		if(itemDiscountId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(ItemDiscount.FIELD.id.name(), itemDiscountId),
							Restrictions.eq(ItemDiscount.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		return getAll(dc);
	}

	@Override
	public ItemDiscount getItemDiscountByItemAndName(Integer itemId, String discountName) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemDiscount.FIELD.itemId.name(), itemId));
		dc.add(Restrictions.eq(ItemDiscount.FIELD.active.name(), true));
		if (discountName != null && !discountName.isEmpty()) {
			dc.add(Restrictions.eq(ItemDiscount.FIELD.name.name(), discountName));
		}
		return get(dc);
	}
}
