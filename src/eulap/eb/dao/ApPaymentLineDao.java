package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ApPaymentLine;
import eulap.eb.web.dto.ApPaymentLineDto;

/**
 * Data Access Object for {@link ApPaymentLine}

 *
 */
public interface ApPaymentLineDao extends Dao<ApPaymentLine>{
	/**
	 * Get the list of unpaid payment lines.
	 * @param supplierAcctId The supplier account id.
	 * @param divisionId The division id.
	 * @param invoiceNumber The invoice number.
	 * @param ebObjectIds The eb object ids in string format.
	 * @param pageSetting The {@link PageSetting}
	 * @return The list of unpaid payment lines.
	 */
	Page<ApPaymentLineDto> getUnpaidPaymentLines(Integer supplierAcctId, Integer divisionId,
			String invoiceNumber, String ebObjectIds, Integer currencyId, PageSetting pageSetting);

	/**
	 * Get the list of {@link ApPaymentLine} by parent eb object and ap payment id.
	 * @param parentObjectId The parent eb object id.
	 * @param apPaymentId The ap payment id.
	 * @return
	 */
	List<ApPaymentLine> getApPaymentLines(Integer parentObjectId, Integer apPaymentId);

	/**
	 * Get related Supplier advance payment transaction.
	 * @param invoiceEbObject The invoice eb object id.
	 * @return The list of ApPaymentLineDto.
	 */
	List<ApPaymentLineDto> getSapRefTrans(Integer invoiceEbObject, String ebObjectIds, Integer currencyId);

	/**
	 * Get the list of paid invoices.
	 * @param invoiceId The invoice id.
	 * @param asOfDate The check date of payment.
	 * @return The list of paid invoices.
	 */
	List<ApPaymentLine> getPaidInvoices(Integer invoiceId, Date asOfDate);
}
