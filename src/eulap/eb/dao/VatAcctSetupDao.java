package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.VatAcctSetup;

/**
 * Data access object interface for {@link VatAcctSetup}

 */

public interface VatAcctSetupDao extends Dao<VatAcctSetup> {

	/**
	 * Get the VAT account setup by company and division
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @return The VAT account setup
	 */
	VatAcctSetup getVatAccountSetup(Integer companyId, Integer divisionId);

	/**
	 * Get the VAT account setup by company and division.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param taxTypeId The tax type id.
	 * @return The VAT account setup.
	 */
	VatAcctSetup getVatAccountSetup(Integer companyId, Integer divisionId, Integer taxTypeId);
}
