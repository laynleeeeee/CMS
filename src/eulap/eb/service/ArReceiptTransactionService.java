package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.ArReceiptTransactionDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptTransaction;
import eulap.eb.domain.hibernate.ArTransaction;

/**
 * A class that handles all the business logic of {@link ArReceiptTransaction}

 *
 */
@Service
public class ArReceiptTransactionService {
	private static Logger logger = Logger.getLogger(ArReceiptTransactionService.class);
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private ArReceiptTransactionDao arReceiptTransactionDao;
	@Autowired
	private ArTransactionService arTransactionService;

	/**
	 * Processes the AR Receipt transactions that has no transaction id.
	 * @param arReceipt The AR Receipt object.
	 * @return List of AR Receipt transactions.
	 */
	public List<ArReceiptTransaction> processArRTransactions (ArReceipt arReceipt) {
		Integer arCustomerAcctId = arReceipt.getArCustomerAccountId();
		List<ArReceiptTransaction> arRTransactions = arReceipt.getArRTransactions();
		List<ArReceiptTransaction> ret = new ArrayList<ArReceiptTransaction>();
		if (arRTransactions != null) {
			for (ArReceiptTransaction arRT : arRTransactions) {
				String transactionNumber = arRT.getTransactionNumber();
				if (transactionNumber != null && !transactionNumber.trim().isEmpty()  
						|| (arRT.getAmount() != null && arRT.getAmount() != 0.00)) {
					ArTransaction arTransaction = null;
					if (transactionNumber != null && !transactionNumber.trim().isEmpty()) {
						arTransaction = arTransactionDao.getArTByCustAcctAndTNum(arCustomerAcctId, transactionNumber);
					}
					if (arTransaction != null) {
						arRT.setArTransactionId(arTransaction.getId());
					}
					arRT.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(arRT.getAmount()));
					ret.add(arRT);
				}
			}
		}
		return ret;
	}

	/**
	 * Reset the AR transaction objects
	 * @param arReceipt The AR receipt object
	 */
	public void resetTransactions(ArReceipt arReceipt) {
		Integer customerAcctId = arReceipt.getArCustomerAccountId();
		List<ArReceiptTransaction> arRTransactions = arReceipt.getArRTransactions();
		for (ArReceiptTransaction arT : arRTransactions) {
			String transactionNumber = arT.getTransactionNumber();
			if (transactionNumber != null) {
				arT.setArTransaction(arTransactionService.getArTransactionByNumber(transactionNumber, customerAcctId));
			}
		}
	}

	/**
	 * 
	 * @param arCustomerAcctId The unique id of ar customer account.
	 * @param arReceiptTransactions The list of Ar Receipt Transactions.
	 * @return True if same customer account, otherwise false.
	 */
	public boolean isSameCustAcct (Integer arCustomerAcctId , List<ArReceiptTransaction> arReceiptTransactions) {
		for (ArReceiptTransaction art : arReceiptTransactions) {
			if (art.getArTransactionId() != null && art.getArTransactionId().intValue() != 0) {
				int tArCustAcctId = arTransactionDao.get(art.getArTransactionId()).getCustomerAcctId();
				if (arCustomerAcctId != null && arCustomerAcctId != tArCustAcctId)
					return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the receipt contains invalid Ar Transaction.
	 * @param arReceiptTransactions List of Ar Receipt Transactions.
	 * @return True if has invalid transaction/s, otherwise false. 
	 */
	public boolean hasInvalidTransaction (List<ArReceiptTransaction> arReceiptTransactions) {
		if (arReceiptTransactions != null) {
			for (ArReceiptTransaction art : arReceiptTransactions) {
				if (art.getArTransactionId() == null)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if ar transaction is validated.
	 * @param arReceiptTransactions The list transactions.
	 * @return True if validated, otherwise false.
	 */
	public boolean isTransactionValidated (List<ArReceiptTransaction> arReceiptTransactions) {
		if (arReceiptTransactions != null && !arReceiptTransactions.isEmpty()) {
			for (ArReceiptTransaction art : arReceiptTransactions) {
				Integer tId = art.getArTransactionId();
				if (tId != null && tId != 0) {
					if (arTransactionDao.get(tId) == null)
						return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks if the transaction has exceeded its balance.
	 * @param arReceiptTransactions List of Ar Receipt Transactions.
	 * @return Returns the message if transaction has exceeded balance, 
	 * otherwise return null.
	 */
	public String exceededTBalance(List<ArReceiptTransaction> arReceiptTransactions) {
		logger.info("Validating the AR Receipt Transactions for exceeding balance.");
		String message = "";
		int ctr = 1;
		if (arReceiptTransactions != null && !arReceiptTransactions.isEmpty()) {
			int transactionId = arReceiptTransactions.iterator().next().getArTransactionId();
			Double transactionAmt = arTransactionDao.get(transactionId).getAmount();
			Double tTransactionAmt = getTotalTransactionAmount(transactionId);
			Double transactionBalance = transactionAmt - tTransactionAmt;
			for (ArReceiptTransaction art : arReceiptTransactions) {
				logger.debug("Validating the transaction number "+art.getTransactionNumber()+" with amount "+art.getAmount());

				logger.trace("Balance for transaction number: "+art.getTransactionNumber()+" is "+transactionBalance);
				
				if (art.getId() != 0) {
					//Absolute value for the amount.
					double amount = Math.abs(Double.valueOf(art.getAmount()));
					ArReceiptTransaction oldART = arReceiptTransactionDao.get(art.getId());
					amount = amount > Math.abs(oldART.getAmount()) ? amount -= oldART.getAmount() : 0.0;
					
					if (amount > Math.abs(Double.valueOf(transactionBalance))) {
						message += "The transaction at row " + ctr + " has " + 
								"exceeded the total transaction balance of " + NumberFormatUtil.format(transactionBalance) + ".\r\n";
					}
					ctr++;
				}
			}
		}

		if (!message.trim().isEmpty()) {
			logger.info("No errors for AR Transactions with exceeding balance.");
			return message;
		}
		return null;
	}

	/**
	 * Get all the list of ar receipt transaction.
	 * @param arReceiptId The ar receipt id.
	 * @return The list of ar receipt transaction.
	 */
	public List<ArReceiptTransaction> getArReceiptTransaction(int arReceiptId){
		return arReceiptTransactionDao.getAllByRefId("arReceiptId", arReceiptId);
	}

	/**
	 * Get the transaction total amount of the Ar Transaction.
	 * @param arTransactionId The transaction id.
	 * @return The total transaction amount.
	 */
	public double getTotalTransactionAmount(Integer arTransactionId) {
		return arReceiptTransactionDao.getTotalTransactionAmount(arTransactionId);
	}

	/**
	 * Get the AR receipt transaction object
	 * @param receiptTransactionId The receipt transaction id
	 * @return The AR receipt transaction object
	 */
	public ArReceiptTransaction getReceiptTransaction(Integer receiptTransactionId) {
		return arReceiptTransactionDao.get(receiptTransactionId);
	}
}
