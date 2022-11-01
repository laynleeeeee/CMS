package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.ItemVolumeConversionDao;
import eulap.eb.domain.hibernate.ItemVolumeConversion;

/**
 * Hibernate implementation object of {@link ItemVolumeConversionDao}.

 *
 */
public class ItemVolumeConversionDaoImpl extends BaseDao<ItemVolumeConversion> implements ItemVolumeConversionDao{

	@Override
	protected Class<ItemVolumeConversion> getDomainClass() {
		return ItemVolumeConversion.class;
	}

	@Override
	public Page<ItemVolumeConversion> getAllItemVolumeConversion(
			Integer itemId, Integer companyId, SearchStatus status,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(itemId != -1){
			dc.add(Restrictions.eq(ItemVolumeConversion.FIELD.itemId.name(), itemId));
		}
		if(companyId != -1){
			dc.add(Restrictions.eq(ItemVolumeConversion.FIELD.companyId.name(), companyId));
		}
		dc = DaoUtil.setSearchStatus(dc, ItemVolumeConversion.FIELD.active.name(), status);
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean hasDuplicateItem(ItemVolumeConversion volumeConversion) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemVolumeConversion.FIELD.itemId.name(), volumeConversion.getItemId()));
		dc.add(Restrictions.eq(ItemVolumeConversion.FIELD.companyId.name(), volumeConversion.getCompanyId()));
		if (volumeConversion.getId() != 0) {
			dc.add(Restrictions.ne(ItemVolumeConversion.FIELD.id.name(), volumeConversion.getId()));
		}
		return getAll(dc).size() > 0;
	}

	@Override
	public ItemVolumeConversion getVolumeConversionPerItem(Integer itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemVolumeConversion.FIELD.itemId.name(), itemId));
		return get(dc);
	}
}
