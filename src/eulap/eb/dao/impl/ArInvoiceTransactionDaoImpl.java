package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArInvoiceTransactionDao;
import eulap.eb.domain.hibernate.ArInvoiceTransaction;

/**
 * Implementing class of {@link ArInvoiceTransactionDao}

 */

public class ArInvoiceTransactionDaoImpl extends BaseDao<ArInvoiceTransaction> implements ArInvoiceTransactionDao {

	@Override
	protected Class<ArInvoiceTransaction> getDomainClass() {
		return ArInvoiceTransaction.class;
	}

	@Override
	public ArInvoiceTransaction getArInvoiceTransaction(Integer arInvoiceId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArInvoiceTransaction.FIELD.arInvoiceId.name(), arInvoiceId));
		return get(dc);
	}
}
