package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.PrOtherChargeDao;
import eulap.eb.domain.hibernate.PrOtherCharge;

/**
 * Implementing class of {@link PrOtherChargeDao}

 *
 */
public class PrOtherChargeDaoImpl extends BaseDao<PrOtherCharge> implements PrOtherChargeDao{

	@Override
	protected Class<PrOtherCharge> getDomainClass() {
		return PrOtherCharge.class;
	}
}
