package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ItemWeightedAveDao;
import eulap.eb.domain.hibernate.ItemWeightedAverage;

/**
 * DAO implementation class for {@code ItemWeightedAveDao}

 */

public class ItemWeightedAveDaoImpl extends BaseDao<ItemWeightedAverage> implements ItemWeightedAveDao {

	@Override
	protected Class<ItemWeightedAverage> getDomainClass() {
		return ItemWeightedAverage.class;
	}

	@Override
	public ItemWeightedAverage getItemWeightedAverage(Integer warehouseId, Integer itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemWeightedAverage.FIELD.warehouseId.name(), warehouseId));
		dc.add(Restrictions.eq(ItemWeightedAverage.FIELD.itemId.name(), itemId));
		return get(dc);
	}
}