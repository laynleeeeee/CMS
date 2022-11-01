package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.NormalBalanceDao;
import eulap.eb.domain.hibernate.NormalBalance;

/**
 * Implementation class of {@link NormalBalanceDao}

 *
 */
public class NormalBalanceDaoImpl extends BaseDao<NormalBalance> implements NormalBalanceDao{

	@Override
	protected Class<NormalBalance> getDomainClass() {
		return NormalBalance.class;
	}
}
