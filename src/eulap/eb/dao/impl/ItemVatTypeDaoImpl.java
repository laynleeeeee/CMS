package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ItemVatTypeDao;
import eulap.eb.domain.hibernate.ItemVatType;

/**
 * Implementation class of {@link ItemVatTypeDao}

 *
 */
public class ItemVatTypeDaoImpl extends BaseDao<ItemVatType> implements ItemVatTypeDao {

	@Override
	protected Class<ItemVatType> getDomainClass() {
		return ItemVatType.class;
	}

	@Override
	public List<ItemVatType> getItemVatTypes(Integer itemVatTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(ItemVatType.FIELD.active.name(), true);
		if(itemVatTypeId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(ItemVatType.FIELD.id.name(), itemVatTypeId),
							Restrictions.eq(ItemVatType.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		return getAll(dc);
	}

	@Override
	public ItemVatType getItemVatTypeByName(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemVatType.FIELD.name.name(), name));
		return get(dc);
	}
}
