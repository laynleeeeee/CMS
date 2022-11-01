package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data Access Object {@link RTransferReceipt}

 */
public interface RTransferReceiptDao extends Dao<RTransferReceipt>{

	/**
	 * Generate the TR Number.
	 * @param companyId The companyId.
	 * @return The generated TR Number.
	 */
	Integer generateTRNumber(int companyId);

	/**
	 * Get all the Retail - Transfer Receipts by status.
	 * @param typeId transfer reciept type Id.
	 * @param searchParam The search parameter.
	 * @return The paged result.
	 */
	Page<RTransferReceipt> getAllTRsByStatus(ApprovalSearchParam searchParam, List<Integer> formStatusIds, Integer typeId, PageSetting pageSetting);

	/**
	 * Search for TR Forms.
	 * @param criteria The search keyword.
	 * @param typeId The type of TR {1=Retail, 2=Individual Selection}
	 * @param pageSetting The page setting.
	 * @return The list of TR Forms.
	 */
	Page<RTransferReceipt> searchTRs(String criteria, int typeId, PageSetting pageSetting);

	/**
	 * Get the list of Retail - TRs.
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param date Get the TRs after this date.
	 * @return The list of Retail - TRs.
	 */
	List<RTransferReceipt> getTrAfterDate(int itemId, int warehouseId, Date date);
}
