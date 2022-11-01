package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CapDeliveryTransactionDao;
import eulap.eb.domain.hibernate.CapDeliveryTransaction;

/**
 * Implementation class of {@link CapDeliveryTransactionDao}

 *
 */
public class CapDeliveryTransactionDaoImpl extends BaseDao<CapDeliveryTransaction> implements CapDeliveryTransactionDao{


	@Override
	protected Class<CapDeliveryTransaction> getDomainClass() {
		return CapDeliveryTransaction.class;
	}

	@Override
	public CapDeliveryTransaction getCapdTransaction(Integer capDeliveryId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CapDeliveryTransaction.FIELD.capDeliveryId.name(), capDeliveryId));
		return get(dc);
	}
}
