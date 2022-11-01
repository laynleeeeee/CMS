package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.JyeiWithdrawalSlip;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data access object layer for {@link JyeiWithdrawalSlip}

 */
public interface JyeiWithdrawalSlipDao extends Dao<JyeiWithdrawalSlip> {

	/**
	 * Generate sequence number per company and requisition type.
	 * @param companyId The withdrawal slip company id.
	 * @param requisitionTypeId The requisition type id.
	 * @return A unique sequence number by company and requisition type.
	 */
	Integer generateSequenceNo(Integer companyId, Integer requisitionTypeId);

	/**
	 * Get the List of withdrawal slips for approval by requisition type.
	 * @param requisitionTypeId The requisition type.
	 * @param searchParam The search parameters
	 * @param formStatusIds The list of form statuses.
	 * @param pageSetting The page setting.
	 * @return The paged list of withdrawal slip.
	 */
	Page<JyeiWithdrawalSlip> getWSByRequisitionTypeId(Integer requisitionTypeId, ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get GemmaWithdrawalSlip object by withdrawal slip id.
	 * @param wsId The withdrawal slip id.
	 * @return The GemmaWithdrawalSlip object.
	 */
	JyeiWithdrawalSlip getByWithdrawalSlipId(Integer wsId);

	/**
	 * Search for Gemma Withdrawal Slips.
	 * @param criteria The search criteria.
	 * @param requisitionTypeId The requisition type id.
	 * @param pageSetting The page setting.
	 * @return The paged list of Gemma Withdrawal Slips.
	 */
	Page<JyeiWithdrawalSlip> searchJyeiWithdrawalSlips(String criteria, int requisitionTypeId, PageSetting pageSetting);

	/**
	 * Get the available balance of the serial item.
	 * @param wsId The withdrawal slip id.
	 * @param itemId The item id. 
	 * @param rfObjectId The requisition form object id.
	 * @return The total available balance of the item.
	 */
	double getSIAvailableStockFromRf(int wsId, Integer itemId, Integer rfObjectId);

	/**
	 * Get Withdrawal Slip by EB Object id
	 * @param ebObjectId the eb object id
	 * @param isComplete true if complete only, else false
	 * @return the list of withdrawal slip
	 */
	List<WithdrawalSlip> getWsByEbObjectId(Integer ebObjectId, Boolean isComplete);
}
