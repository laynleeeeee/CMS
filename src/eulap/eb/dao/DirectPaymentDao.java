package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.DirectPayment;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data Access Object for {@link DirectPayment}

 *
 */
public interface DirectPaymentDao extends Dao<DirectPayment>{

	/**
	 * Get the list of direct payment data in a paged format
	 * @param searchParam The search parameter criteria
	 * @param formStatusIds The form status id
	 * @param pageSetting The page setting
	 * @return The list of direct payment data in a paged format
	 */
	Page<DirectPayment> getAllDirectPayments(ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the direct payment by AP payment id
	 * @param apPaymentId The AP payment id
	 * @return The direct payment object
	 */
	DirectPayment getDirectPaymentByPaymentId(Integer apPaymentId);

	/**
	 * Check if the invoice number is unique per form
	 * @param invoiceNo The invoice number
	 * @param directPaymentId The direct payment id
	 * @return True if the invoice number is existing, otherwise false
	 */
	boolean isExistingInvoiceNo(String invoiceNo, Integer directPaymentId);
}
