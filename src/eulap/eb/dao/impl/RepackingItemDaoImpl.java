package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.RepackingItemDao;
import eulap.eb.domain.hibernate.RepackingItem;

/**
 * DAO Implementation of {@link RepackingItemDao}

 */
public class RepackingItemDaoImpl extends BaseDao<RepackingItem> implements RepackingItemDao{

	@Override
	protected Class<RepackingItem> getDomainClass() {
		return RepackingItem.class;
	}

	@Override
	public List<RepackingItem> getRepackingItems(int repackingId, Integer itemId) {
		DetachedCriteria rItemCriteria = getDetachedCriteria();
		rItemCriteria.add(Restrictions.eq(RepackingItem.FIELD.repackingId.name(), repackingId));
		if(itemId != null) {
			rItemCriteria.add(Restrictions.eq(RepackingItem.FIELD.fromItemId.name(), itemId));
		}
		return getAll(rItemCriteria);
	}
	
	@Override
	public Page<RepackingItem> getAllRepackingItem(PageSetting ps) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(Order.asc(RepackingItem.FIELD.id.name()));
		return getAll(dc, ps);
	}
}
