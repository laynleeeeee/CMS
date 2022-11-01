package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.InvoiceImportationDetails;

/**
 * A base class for accessing data in Invoice importation details. 

 *
 */
public interface InvoiceImportationDetailsDao extends Dao<InvoiceImportationDetails>{

	/**
	 * Get {@link InvoiceImportationDetails}
	 * @param apInvoiceId The AP Invoice id
	 * @return The {@link InvoiceImportationDetails}
	 */
	InvoiceImportationDetails getIIDByInvoiceId(Integer apInvoiceId);
}
