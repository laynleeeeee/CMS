package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FleetDefaultAccountDao;
import eulap.eb.domain.hibernate.FleetDefaultAccount;

/**
 * Implementing class of {@link FleetDefaultAccountDao}

 *
 */
public class FleetDefaultAccountDaoImpl extends BaseDao<FleetDefaultAccount> implements FleetDefaultAccountDao{

	@Override
	protected Class<FleetDefaultAccount> getDomainClass() {
		return FleetDefaultAccount.class;
	}

}
