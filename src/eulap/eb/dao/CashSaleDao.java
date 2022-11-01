package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.DailyCashCollection;

/**
 * Data access object of {@link CashSale}

 *
 */
public interface CashSaleDao extends Dao<CashSale> {
	
	/**
	 * Generate unique cash sales number per company. 
	 * @param companyId The company id.
	 * @param cashSaleTypeId The type of the Cash Sales {1 = Retail}
	 * @return The cash sales number.
	 */
	Integer generateCsNumber (Integer companyId, Integer cashSaleTypeId);
	
	/**
	 * Search for cash sales.
	 * @param criteria The search criteria.
	 * @param typeId The type id.
	 * @param pageSetting The page setting.
	 * @return Paged search results.
	 */
	Page<CashSale> searchCashSales (String criteria, int typeId, PageSetting pageSetting);
	
	/**
	 * Get the cash sales.
	 * @param typeId The type of the cash sales, {1 = Retail}
	 * @param param The search parameter.
	 * @return The paged cash sales.
	 */
	Page<CashSale> getCashSales(ApprovalSearchParam searchParam, List<Integer> formStatusIds, Integer typeId, PageSetting pageSetting);
	
	/**
	 * Get the cash sales for daily sales report.
	 * @param companyId The company id.
	 * @param salesInvoiceNo The sales invoice number.
	 * @param customerName The name of the customer.
	 * @param date The date.
	 * @return The list of cash sales.
	 */
	List<CashSale> getCashSales (Integer companyId, String salesInvoiceNo, 
			String customerName, Date date);

	/**
	 * Get the list of daily cash collections.
	 * @param companyId The company id.
	 * @param userId The user id.
	 * @param date The date.
	 * @param orderType REFERENCE_NO, INVOICE_NO... Default is REFERENCE_NO. 
	 * @return The list of daily cash collections.
	 */
	List<DailyCashCollection> getDailyCashCollections (Integer companyId, Integer userId, Date date, 
			String orderType, Integer status);

	/**
	 * Get the paged list of cash sales for CS Reference.
	 * @param companyId The company id.
	 * @param arCustomerId The customer id.
	 * @param arCustomerAccountId The customer account id.
	 * @param csNumber The cash sale number.
	 * @param dateFrom The start of date range.
	 * @param dateTo The end of date range.
	 * @param status The status of the cash sale.
	 * @param typeId The type of the cash sales, {1 = Retail}
	 * @param pageSetting The page setting.
	 * @param user The current user logged.
	 * @return The paged list of cash sales.
	 */
	Page<CashSale>  getCashSales (Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer csNumber, Date dateFrom, Date dateTo, Integer status, Integer typeId, PageSetting pageSetting, User user);

	/**
	 * Get the list of cash sales.
	 * @param itemId The item id.
	 * @param warehouseId The warehouse id.
	 * @param date Get the list of cash sales from the date.
	 * @param pageSetting The page setting.
	 * @return The list of cash sales in paged format.
	 */
	Page<CashSale> getCashSalesAfterDate(int itemId, int warehouseId, Date date, PageSetting pageSetting);
	
	/**
	 * Get the oldest transaction date of cash sale.
	 * @param itemId The item id, 
	 * @param warehouseId The warehouse id. 
	 * @return The oldest date.
	 */
	Date getOldestTransactionDate (int itemId, int warehouseId);
	
	/**
	 * Get the cash sale transaction of the specified date. Forms that are not cancelled.
	 */
	Page<CashSale> getCashSalePerDate (int itemId, int warehouseId, Date date, PageSetting pageSetting);
	
	/**
	 * Get the size of cash sales that are not cancelled.
	 * @return The size of cash sales.
	 */
	int getCSSize ();
	
	/**
	 * Get the cash sales.
	 * @param pageSetting The page setting.
	 * @return The paged cash sales.
	 */
	Page<CashSale> getCashSales (PageSetting pageSetting);

	/**
	 * Get the Cash Sales object by workflow id.
	 * @param formWorkflowId The FormWorkflow id.
	 * @return The cash sales object.
	 */
	CashSale getCashSaleByWorkflow(Integer formWorkflowId);

	/**
	 * Checks if the cash Sale is already existing in cash sale return.
	 * @param cashSaleId The cash sale ID.
	 * @return True if existing, otherwise false.
	 */
	Boolean isExistingReturn(Integer cashSaleId);

	/**
	 * Check if the cash sale has duplicate by checking the sales invoice number.
	 * @param salesInvoiceNo The sales invoice number.
	 * @return True if there is existingm otherwise false.
	 */
	boolean hasExistingSI(String salesInvoiceNo);

	/**
	 * Get all cash sales by customer.
	 * @param arCustomerId The customer id.
	 */
	List<CashSale> getCashSalesByCustomer (Integer arCustomerId);

	/**
	 * Get the list of cash sales
	 * @param companyId The company id
	 * @param typeId The cash sales type id
	 * @return The list of cash sales
	 */
	List<CashSale> getCashSales(Integer companyId, Integer typeId);
}
