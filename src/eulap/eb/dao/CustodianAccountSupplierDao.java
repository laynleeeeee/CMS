package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CustodianAccountSupplier;

/**
 * Data access object interface class for {@link CustodianAccountSupplier}

 */

public interface CustodianAccountSupplierDao extends Dao<CustodianAccountSupplier> {

	/**
	 * Check if existing 
	 * @param custodianAccountId The custodian account id
	 * @return True if the division exists, otherwise false
	 */
	boolean hasExistingCustodianAccountSupplier(Integer custodianAccountId);

	/**
	 * Get {@link CustodianAccountSupplier}
	 * @param custodianAccountId The custodian account id
	 * @param supplierId The supplier id
	 * @param supplierAccountId The supplier account id
	 * @return The {@link CustodianAccountSupplier}
	 */
	CustodianAccountSupplier getCustodianAccountSupplier(Integer custodianAccountId, Integer supplierId, Integer supplierAccountId);
}
