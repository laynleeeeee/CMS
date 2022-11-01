package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ApInvoiceLine;

/**
 * DAO Layer of {@link ApInvoiceLine}

 *
 */
public interface ApInvoiceLineDao extends Dao<ApInvoiceLine> {

	/**
	 * Get the list of apInvoice line per invoice.
	 * @param invoiceId The ap invoice Id.
	 * @return The list of ap invoice line.
	 */
	List<ApInvoiceLine> getApInvoiceLineByInvoiceId(int invoiceId);

	/**
	 * Get the remaining quantity of AP Invoice line, from the reference purchase order transaction.
	 * @param referenceObjectId The reference object id.
	 * @param apLineSetupId The AP Line Setup id.
	 * @return The remaining quantity.
	 */
	Double getRemainingQty(Integer referenceObjectId, Integer apLineSetupId, Integer invoiceId);

	/**
	 * Get the remaining quantity based on the reference object form
	 * @param invoiceId The AP invoice id
	 * @param referenceObjId The reference object id
	 * @param apLineSetupId The AP line setup id
	 * @return The remaining quantity based on the reference object form
	 */
	double getRemainingRrLineQty(Integer invoiceId, Integer referenceObjId, Integer apLineSetupId);

	/**
	 * 
	 * @param invoiceId
	 * @param referenceObjId
	 * @param apLineSetupId
	 * @return
	 */
	double getRemainingInvGsLineQty(Integer invoiceId, Integer referenceObjId, Integer apLineSetupId);
}
