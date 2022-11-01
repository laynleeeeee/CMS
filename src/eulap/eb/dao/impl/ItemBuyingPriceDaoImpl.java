package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ItemBuyingPriceDao;
import eulap.eb.domain.hibernate.ItemBuyingPrice;

/**
 * Implementation class of {@link ItemBuyingPriceDao}

 *
 */
public class ItemBuyingPriceDaoImpl extends BaseDao<ItemBuyingPrice> implements ItemBuyingPriceDao {

	@Override
	protected Class<ItemBuyingPrice> getDomainClass() {
		return ItemBuyingPrice.class;
	}

	@Override
	public ItemBuyingPrice getLatestBuyingPrice(int itemId, int companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		getCommonParams(dc, itemId, companyId, true);
		return get(dc);
	}

	@Override
	public List<ItemBuyingPrice> getBuyingPrices(int itemId, boolean isActiveOnly) {
		DetachedCriteria ibpDc = getDetachedCriteria();
		getCommonParams(ibpDc, itemId, null, isActiveOnly);
		return getAll(ibpDc);
	}

	private void getCommonParams(DetachedCriteria ibpDc, int itemId, Integer companyId, boolean isActiveOnly) {
		ibpDc.add(Restrictions.eq(ItemBuyingPrice.FIELD.itemId.name(), itemId));
		ibpDc.add(Restrictions.eq(ItemBuyingPrice.FIELD.active.name(), isActiveOnly));
		if(companyId != null) {
			ibpDc.add(Restrictions.eq(ItemBuyingPrice.FIELD.companyId.name(), companyId));
		}
	}
}
