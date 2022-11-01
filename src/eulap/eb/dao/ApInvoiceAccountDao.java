package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ApInvoiceAccount;

/**
 * Data access object for {@link ApInvoiceAccount}

 */

public interface ApInvoiceAccountDao extends Dao<ApInvoiceAccount> {

	/**
	 * Get the AP invoice account
	 * @param companyId The company id
	 * @return The AP invoice account
	 */
	ApInvoiceAccount getInvoiceAcctByCompany(Integer companyId);

	/**
	 * Get invoice account by company and division
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @return The invoice account by company and division
	 */
	ApInvoiceAccount getInvoiceAcctByCompanyDiv(Integer companyId, Integer divisionId);
}