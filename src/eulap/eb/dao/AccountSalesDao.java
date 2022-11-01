package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.AccountSales;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data access object interface for {@link AccountSales}

 *
 */
public interface AccountSalesDao extends Dao<AccountSales> {

	/**
	 * Get the max PO number by company.
	 * @param companyId The company id.
	 * @return The PO Number.
	 */
	int getMaxPONumber(int companyId);

	/**
	 * Get all the Account sales by search parameters.
	 * @param searchParam The search parameter.
	 * @return The paged result.
	 */
	Page<AccountSales> getAllPOsByStatus(ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the list of account sales reference,
	 * @param companyId The company id.
	 * @param arCustomerId The customer id.
	 * @param arCustomerAccountId The customer account id.
	 * @param asNumber The account sales number.
	 * @param dateFrom The date ranged start.
	 * @param dateTo The date ranged end.
	 * @param status The status account sales.
	 * @param user The user current logged.
	 * @return {@link Page<AccountSales>}
	 */
	Page<AccountSales> getAccountSales(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId, Integer asNumber,
			Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting, User user);
}
