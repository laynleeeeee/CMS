package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.TaxType;

/**
 * Data access object interface for {@link TaxType}

 */

public interface TaxTypeDao extends Dao<TaxType> {

	/**
	 * Get the list of tax type objects
	 * @param taxTypeId The tax type id
	 * @return The list of tax type objects
	 */
	List<TaxType> geTaxTypes(Integer taxTypeId);

	/**
	 * Get the list of accounts receivable tax types
	 * @param taxTypeId The tax type id
	 * @return The list of accounts receivable tax types
	 */
	List<TaxType> getAcctReceivableTaxTypes(Integer taxTypeId);

	/**
	 * Get the list of accounts payable tax types
	 * @param taxTypeId The tax type id
	 * @param isImportation If the accounts payable tax type is for importation
	 * @return The list of accounts payable tax types
	 */
	List<TaxType> getAcctPayableTaxTypes(Integer taxTypeId, boolean isImportation);

}
