package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.PrRawMaterialsItemDao;
import eulap.eb.domain.hibernate.PrRawMaterialsItem;

/**
 * Implementing class of {@link PrRawMaterialsItemDao}

 *
 */
public class PrRawMaterialsItemDaoImpl extends BaseDao<PrRawMaterialsItem> implements PrRawMaterialsItemDao{

	@Override
	protected Class<PrRawMaterialsItem> getDomainClass() {
		return PrRawMaterialsItem.class;
	}

	@Override
	public List<PrRawMaterialsItem> getAllPrRawMaterialItems(Integer processingReportId, Integer itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PrRawMaterialsItem.FIELD.processingReportId.name(), processingReportId));
		if(itemId != null) {
			dc.add(Restrictions.eq(PrRawMaterialsItem.FIELD.itemId.name(), itemId));
		}
		return getAll(dc);
	}
}
