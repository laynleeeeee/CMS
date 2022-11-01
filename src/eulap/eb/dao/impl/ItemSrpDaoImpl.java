package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ItemSrpDao;
import eulap.eb.domain.hibernate.ItemSrp;

/**
 * Implementing class of {@link ItemSrpDao}

 *
 */
public class ItemSrpDaoImpl extends BaseDao<ItemSrp> implements ItemSrpDao{

	@Override
	protected Class<ItemSrp> getDomainClass() {
		return ItemSrp.class;
	}

	@Override
	public List<ItemSrp> getItemSrpsByItem(Integer itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemSrp.FIELD.itemId.name(), itemId));
		dc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
		return getAll(dc);
	}

	@Override
	public ItemSrp getLatestItemSrp(Integer companyId, Integer itemId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (companyId != null) {
			dc.add(Restrictions.eq(ItemSrp.FIELD.companyId.name(), companyId));
		}
		if (divisionId != null) {
			dc.add(Restrictions.eq(ItemSrp.FIELD.divisionId.name(), divisionId));
		}
		if (itemId != null) {
			dc.add(Restrictions.eq(ItemSrp.FIELD.itemId.name(), itemId));
		}
		dc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
		return get(dc);
	}

	@Override
	public boolean hasItemSrp(int itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemSrp.FIELD.itemId.name(), itemId));
		dc.add(Restrictions.eq(ItemSrp.FIELD.active.name(), true));
		return getAll(dc).size() > 0;
	}
}
