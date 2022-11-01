package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.SalesQuotation;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data access object interface for {@link SalesQuotation}

 */

public interface SalesQuotationDao extends Dao<SalesQuotation> {

	/**
	 * Generate form sequence number
	 * @param companyId The company id
	 * @return The generated form sequence number
	 */
	Integer generateSequenceNo(int companyId);

	/**
	 * Get the paged list of {@code SalesQuotation} for approval view
	 * @param searchParam The search parameter object
	 * @param formStatusIds The list of form status ids
	 * @param pageSetting The page setting
	 * @return The paged list of {@code SalesQuotation} for approval view
	 */
	Page<SalesQuotation> getSalesQuotations(ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the latest encoded ship to for the selected customer
	 * @param arCustomerId The customer id
	 * @return The latest encoded ship to field
	 */
	String getCustomerShipTo(Integer arCustomerId);

	/**
	 * Get the paged list for {@code SalesQuotation} for general search
	 * @param searchCriteria The search criteria
	 * @param pageSetting The page setting
	 * @return The paged list for {@code SalesQuotation} for general search
	 */
	Page<SalesQuotation> retrieveSalesQuotations(String searchCriteria,  PageSetting pageSetting);

	/**
	 * Get the paged list of sales quotations for SQ Reference.
	 * @param companyId The company id.
	 * @param arCustomerId The customer id.
	 * @param arCustomerAccountId The customer account id.
	 * @param sequenceNo The sequence number.
	 * @param dateFrom The start of date range.
	 * @param dateTo The end of date range.
	 * @param pageSetting The page setting.
	 * @param user The current user.
	 * @return The paged list of sales quotations.
	 */
	Page<SalesQuotation> getSalesQuotations (Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer sequenceNo, Date dateFrom, Date dateTo, PageSetting pageSetting, User user);

	/**
	 * Check if the sales quotation form is used by sales order form.
	 * @param sqId The sales quotation id.
	 * @return True if the SQ form has been referenced in SO forms.
	 */
	boolean isUsedBySO(Integer sqId);

}
