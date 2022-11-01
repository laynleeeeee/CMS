package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptLine;
import eulap.eb.domain.hibernate.ArReceiptLineType;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;

/**
 * Data access object of {@link ArReceiptLine}

 *
 */
public interface ArReceiptLineDao extends Dao<ArReceiptLine>{
	/**
	 * Get the list of {@link ArReceiptLine} based on the paramaters.
	 * @param companyId The company id. 
	 * @param divisionId The division id.
	 * @param currencyId The currency id.
	 * @param arCustomerAcctId The ar customer account id.
	 * @param transNumber The transaction number.
	 * @param refObjIds The reference object ids in string format.
	 * @param pageSetting The page setting.
	 * @return list of {@link ArReceiptLine}.
	 */
	Page<ArReceiptLine> getArReceiptLines(Integer companyId, Integer divisionId, Integer currencyId, 
			Integer arCustomerAcctId, String transNumber, String refObjIds, PageSetting pageSetting);

	/**
	 * Get the list of {@link ArReceiptLine} by {@link ArReceipt} id.
	 * @param arReceiptId The {@link ArReceipt} id.
	 * @return List of  {@link ArReceiptLine}.
	 */
	List<ArReceiptLine> getArReceiptLines(Integer arReceiptId);

	/**
	 * Get the list of {@link CustomerAdvancePayment} related transactions.
	 * @param arInvoiceObjectId The {@link ArInvoice} object id.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param currencyId The currency id.
	 * @param refObjIds The to be excluded reference object ids.
	 * @return The list of {@link CustomerAdvancePayment} related transactions.
	 */
	List<ArReceiptLine> getCapReference(Integer arInvoiceObjectId, Integer companyId, Integer divisionId,
			Integer currencyId, String refObjIds);

	/**
	 * Get the remaining balance of the reference transaction.
	 * @param refObjecId The reference transaction object id.
	 * @param arReceiptLineTypeId The {@link ArReceiptLineType} id.
	 * @return The remaining balance.
	 */
	double getRemainingBalance(Integer refObjecId, int arReceiptLineTypeId, Integer arReceiptId);
}
