package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ItemDiscountTypeDao;
import eulap.eb.domain.hibernate.ItemDiscountType;

public class ItemDiscountTypeDaoImpl extends BaseDao<ItemDiscountType> implements ItemDiscountTypeDao {

	@Override
	protected Class<ItemDiscountType> getDomainClass() {
		return ItemDiscountType.class;
	}

	@Override
	public ItemDiscountType getItemDiscountType(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemDiscountType.FIELD.name.name(), name.trim()));
		return get(dc);
	}

	@Override
	public List<ItemDiscountType> getItemDiscountTypes(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		if (name != null) {
			dc.add(Restrictions.like(ItemDiscountType.FIELD.name.name(), StringFormatUtil.appendWildCard(name.trim())));
		}
		return getAll(dc);
	}
}
