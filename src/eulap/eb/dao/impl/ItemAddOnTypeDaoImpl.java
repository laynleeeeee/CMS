package eulap.eb.dao.impl;

import java.util.List;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ItemAddOnTypeDao;
import eulap.eb.domain.hibernate.ItemAddOnType;

public class ItemAddOnTypeDaoImpl extends BaseDao<ItemAddOnType> implements ItemAddOnTypeDao {

	@Override
	protected Class<ItemAddOnType> getDomainClass() {
		return ItemAddOnType.class;
	}

	@Override
	public List<ItemAddOnType> getAllAddOnTypes() {
		return getAll();
	}

}
