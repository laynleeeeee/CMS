package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptAdvancePayment;

/**
 * Data access object interface for {@link ArReceiptAdvancePayment}

 */

public interface ArAdvancePaymentDao extends Dao<ArReceiptAdvancePayment> {

	/**
	 * Get the list of {@link ArReceiptAdvancePayment} by {@link ArReceipt} id.
	 * @param arReceiptId The {@link ArReceipt} id.
	 * @return The list of {@link ArReceiptAdvancePayment}.
	 */
	List<ArReceiptAdvancePayment> getArRecAdvPayments(Integer arReceiptId);
}
