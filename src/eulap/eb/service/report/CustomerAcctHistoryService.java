package eulap.eb.service.report;

import java.util.List;
import eulap.eb.web.dto.CustomerAccountHistoryDto;
/**
 * The business logic class of Customer Account History.

 */
public interface CustomerAcctHistoryService {

	/**
	 * Generate Customer Account History report.
	 * Retrieves data from AR Transaction and AR Receipt.
	 * 
	 * @param param The parameter that the user selected.
	 * @return The list of Customer Account History dto.
	 */
	public List<CustomerAccountHistoryDto> generate(CustomerAccountHistoryParam param);
	
	/**
	 * Get the total transaction amount of the customer account.
	 * @param customerAccountId The customer account id.
	 * @return Total transaction amount.
	 */
	public Double getCustomerAcctTotalTransactionAmount(Integer customerAccountId);

	/**
	 * Get the total receipt amount of the customer account.
	 * @param customerAccountId The customer account id.
	 * @return Total receipt amount
	 */
	public Double getCustomerAcctTotalReceiptAmount(Integer customerAccountId);

	/**
	 * Get the total transactions and receipts of the AR Customer Account.
	 * @param customerAcctId The ar customer account id.
	 * @return The total transactions and receipts in {@link CustomerAccountHistoryDto}
	 */
	public CustomerAccountHistoryDto getTotalTransactionsAndReceipts(int customerAcctId);
}
