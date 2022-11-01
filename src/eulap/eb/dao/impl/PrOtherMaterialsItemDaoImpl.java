package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.PrOtherMaterialsItemDao;
import eulap.eb.domain.hibernate.PrOtherMaterialsItem;

/**
 * Implementing class of {@link PrOtherMaterialsItemDao}

 *
 */
public class PrOtherMaterialsItemDaoImpl extends BaseDao<PrOtherMaterialsItem> implements PrOtherMaterialsItemDao{

	@Override
	protected Class<PrOtherMaterialsItem> getDomainClass() {
		return PrOtherMaterialsItem.class;
	}
}
