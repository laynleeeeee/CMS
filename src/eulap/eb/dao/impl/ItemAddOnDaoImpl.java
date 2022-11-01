package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ItemAddOnDao;
import eulap.eb.domain.hibernate.ItemAddOn;

/**
 * Implementing class of {@link ItemAddOnDao}

 *
 */
public class ItemAddOnDaoImpl extends BaseDao<ItemAddOn> implements ItemAddOnDao {

	@Override
	protected Class<ItemAddOn> getDomainClass() {
		return ItemAddOn.class;
	}

	@Override
	public List<ItemAddOn> getItemAddOnsByItem(Integer itemId, boolean activeOnly) {
		return getAll(getAddOnCrit(itemId, null, activeOnly));
	}

	private DetachedCriteria getAddOnCrit(Integer itemId, Integer companyId, boolean activeOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemAddOn.FIELD.itemId.name(), itemId));
		if(companyId != null) {
			dc.add(Restrictions.eq(ItemAddOn.FIELD.companyId.name(), companyId));
		}
		if (activeOnly)
			dc.add(Restrictions.eq(ItemAddOn.FIELD.active.name(), true));
		return dc;
	}

	@Override
	public List<ItemAddOn> getItemAddOnsByItem(Integer itemId, Integer companyId, boolean activeOnly) {
		return getAll(getAddOnCrit(itemId, companyId, activeOnly));
	}

	@Override
	public List<ItemAddOn> getAddOnsWSlctdInactiveAddOnId(Integer itemId,
			Integer companyId, Integer itemAddOnId) {
		DetachedCriteria dc = getAddOnCrit(itemId, companyId, false);
		Criterion criterion = Restrictions.eq(ItemAddOn.FIELD.active.name(), true);
		if(itemAddOnId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(ItemAddOn.FIELD.id.name(), itemAddOnId),
							Restrictions.eq(ItemAddOn.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		return getAll(dc);
	}

}
