package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ArInvoiceTransaction;

/**
 * Data access object for {@link ArInvoiceTransaction}

 *
 */
public interface ArInvoiceTransactionDao extends Dao<ArInvoiceTransaction> {

	/**
	 * Get the AR invoice transaction
	 * @param arInvoiceId The AR invoice id
	 * @return The AR invoice transaction
	 */
	ArInvoiceTransaction getArInvoiceTransaction(Integer arInvoiceId);
}
