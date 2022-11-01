package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.AccountSalesPoItemDao;
import eulap.eb.domain.hibernate.AccountSalesPoItem;

/**
 * Implementation class for {@link AccountSalesPoItemDao}

 *
 */
public class AccountSalesPoItemDaoImpl extends BaseDao<AccountSalesPoItem> implements AccountSalesPoItemDao {

	@Override
	protected Class<AccountSalesPoItem> getDomainClass() {
		return AccountSalesPoItem.class;
	}

}
