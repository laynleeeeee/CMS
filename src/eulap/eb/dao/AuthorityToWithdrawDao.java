package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.AuthorityToWithdraw;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data access object interface for {@link AuthorityToWithdraw}

 */

public interface AuthorityToWithdrawDao extends Dao<AuthorityToWithdraw> {

	/**
	 * Generate form sequence number
	 * @param companyId The company id
	 * @return The generated form sequence number
	 */
	Integer generateSequenceNo(int companyId);

	/**
	 * Get the paged list of {@code AuthorityToWithdraw} for approval view
	 * @param searchParam The search parameter object
	 * @param formStatusIds The list of form status ids
	 * @param pageSetting The page setting
	 * @return The paged list of ATW for approval view
	 */
	Page<AuthorityToWithdraw> getAuthorityToWithdraws(ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the paged list for {@code AuthorityToWithdraw} for general search
	 * @param searchCriteria The search criteria
	 * @param pageSetting The page setting
	 * @return The paged list for ATW for general search
	 */
	Page<AuthorityToWithdraw> retrieveAuthorityToWithdraws(String searchCriteria,  PageSetting pageSetting);

	/**
	 * Get the paged list for {@code AuthorityToWithdraw} for DR form referencing
	 * @param companyId The company id
	 * @param arCustomerId The customer id
	 * @param arCustomerAccountId The customer account id
	 * @param atwNumber The ATW number
	 * @param statusId The ATW form status
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param pageSetting The page setting
	 * @param drTypeId The delivery receipt type id
	 * @return The paged list for {@code AuthorityToWithdraw} for DR form referencing
	 */
	Page<AuthorityToWithdraw> getATWReferences(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer atwNumber, Integer statusId, Date dateFrom, Date dateTo, PageSetting pageSetting,
			Integer drTypeId);

	/**
	 * Check if the authority to withdraw form is used by delivery receipt form.
	 * @param atwId The sales order id.
	 * @return True if the ATW form has been referenced in DR forms.
	 */
	boolean isUsedByDR(Integer atwId);

	/**
	 * Get the remaining authority to withdraw item quantity.
	 * @param soId The {@link SalesOrder} id.
	 * @param itemId The item id.
	 * @return The remaining authority to withdraw item quantity.
	 */
	Double getRemainingAtwItemQty(Integer soId, Integer itemId);

	/**
	 * Get the remaining serial item quantity.
	 * @param soId The {@link SalesOrder} id.
	 * @param itemId The item id.
	 * @return The remaining serial item quantity.
	 */
	Double getRemainingSiQty(Integer soId, Integer itemId);
}