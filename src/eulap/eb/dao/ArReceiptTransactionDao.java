package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ArReceiptTransaction;

/**
 * Data access object of {@link ArReceiptTransaction}

 *
 */
public interface ArReceiptTransactionDao extends Dao<ArReceiptTransaction>{

	/**
	 * Get the list of AR receipt transactions by AR receipt.
	 * @param arReceiptId The unique id of AR receipt.
	 * @param arTransactionId The unique id of AR transaction.
	 * @return The list of AR receipt transactions.
	 */
	List<ArReceiptTransaction> getArReceiptTransactions(Integer arReceiptId, Integer arTransactionId);

	/**
	 * Get the transaction total amount of the Ar Transaction.
	 * @param arTransactionId The transaction id.
	 * @return The total transaction amount.
	 */
	Double getTotalTransactionAmount (Integer arTransactionId);

	/**
	 * Get the list of receipt transactions.
	 * @param transactionId The transaction id.
	 * @param asOfDate The maturity date of receipt.
	 * @return The list of receipt transactions.
	 */
	List<ArReceiptTransaction> getReceiptTransactions(Integer transactionId, Date asOfDate);

	/**
	 * Get the transaction amount.
	 * @param arTransactionId The transaction id.
	 * @param trDateFrom The start date of transaction.
	 * @param trDateTo The end date of transaction
	 * @return The transaction amount.
	 */
	Double getReceiptTransactionAmount (Integer arTransactionId, Date trDateFrom, Date trDateTo);

	/**
	 * Get the total amount paid by receipt per customer account.
	 * @param arCustomerAccountId The customer account id.
	 * @param isEdit Check if edit.
	 * @return The total amount paid.
	 */
	double getTotalReceiptPayment (Integer arCustomerAccountId, boolean isEdit);
}
