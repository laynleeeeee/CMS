package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data access object for {@link CashSaleReturn}

 *
 */
public interface CashSaleReturnDao extends Dao<CashSaleReturn>{

	/**
	 * Generate unique cash sales return number per company. 
	 * @param companyId The company id.
	 * @param typeId The type id.
	 * @return The cash sales return number.
	 */
	Integer generateCsrNumber (Integer companyId, Integer typeId);

	/**
	 * Search for cash sales return.
	 * @param criteria The search criteria.
	 * @param typeId The type id.
	 * @param pageSetting The page setting.
	 * @return Paged search results.
	 */
	Page<CashSaleReturn> searchCashSaleReturns (String criteria, int typeId, PageSetting pageSetting);

	/**
	 * Get the cash sales return.
	 * @param typeId The type id.
	 * @param param The search parameter
	 * @return The paged cash sales return.
	 */
	Page<CashSaleReturn> getCashSaleReturns(ApprovalSearchParam searchParam, List<Integer> formStatusIds, Integer typeId, PageSetting pageSetting);

	/**
	 * Check if the cash sale return id already use in other cash sale return.
	 * @param cashSaleReturnId The cash sale return id.
	 * @return True if the cash sale return id already use in other cash sale return.
	 */
	boolean isExistingInCashSaleReturn(int cashSaleReturnId);

	/**
	 * Get the list of cash sale return uses cashSaleReturnId.
	 * @param cashSaleReturnId The cash sale return id.
	 * @return Get the list of cash sale return uses cashSaleReturnId.
	 */
	List<CashSaleReturn> getCashReturnSaleUsedInReturns(int cashSaleReturnId);

	/**
	 * Get the list of cash sale return by reference cash sale id
	 * @param cashSaleId The cash sale id
	 * @return The list of cash sale returns
	 */
	List<CashSaleReturn> getCSRByRefCSId(Integer cashSaleId);

	/**
	 * Get all cash sale returns by customer.
	 * @param arCustomerId The customer id.
	 */
	List<CashSaleReturn> getCSRsByCustomer(Integer arCustomerId);
}
