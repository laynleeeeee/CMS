package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ItemBuyingAddOnDao;
import eulap.eb.domain.hibernate.ItemBuyingAddOn;

/**
 * DAO Implementation class of {@link ItemBuyingAddOnDao}

 *
 */
public class ItemBuyingAddOnDaoImpl extends BaseDao<ItemBuyingAddOn> implements ItemBuyingAddOnDao {

	@Override
	protected Class<ItemBuyingAddOn> getDomainClass() {
		return ItemBuyingAddOn.class;
	}

	@Override
	public List<ItemBuyingAddOn> getBuyingAddOns(Integer itemId, Integer companyId) {
		DetachedCriteria addOnDc = getDetachedCriteria();
		addOnDc.add(Restrictions.eq(ItemBuyingAddOn.FIELD.itemId.name(), itemId));
		addOnDc.add(Restrictions.eq(ItemBuyingAddOn.FIELD.companyId.name(), companyId));
		addOnDc.add(Restrictions.eq(ItemBuyingAddOn.FIELD.active.name(), true));
		return getAll(addOnDc);
	}
}
