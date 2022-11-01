package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.SupplierAdvPaymentAccount;

/**
 * Data access object for {@link SupplierAdvPaymentAccount}

 */

public interface SupplierAdvPaymentAccountDao extends Dao<SupplierAdvPaymentAccount> {

	/**
	 * Get the supplier advance payment account
	 * @param companyId The company id
	 * @return The AP invoice account
	 */
	SupplierAdvPaymentAccount getSapAcctByCompany(Integer companyId);

	/**
	 * Get supplier advance payment account by company and division
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @return The invoice account by company and division
	 */
	SupplierAdvPaymentAccount getSapAcctByCompanyDiv(Integer companyId, Integer divisionId);
}