package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.ReclassRegisterDto;

/**
 * Data Access Object {@link Repacking}

 */

public interface RepackingDao extends Dao<Repacking> {

	/**
	 * Generate the Repacking number by company.
	 * @param companyId The unique id of the company.
	 * @return The generated Repacking number.
	 */
	int generateRNumber(int companyId);

	/**
	 *  Generate the repacking number by company and type id
	 * @param companyId The company id
	 * @param repackingTypeId The repacking type id
	 * @return The generated repacking number by company and type id
	 */
	int generateRNumberByTypeId(int companyId, int repackingTypeId);

	/**
	 *  Generate the repacking number by company and type id
	 * @param companyId The company id
	 * @param repackingTypeId The repacking type id
	 * @param divisionId The division id
	 * @return The generated repacking number by company and type id
	 */
	int generateRNumberByTypeId(int companyId, int repackingTypeId, Integer divisionId);

	/**
	 * Get all repacking forms by status.
	 * @param param The search parameter.
	 * @return The list of repacking forms by status.
	 */
	Page<Repacking> getAllRepackingByStatus(ApprovalSearchParam searchParam,
			List<Integer> formStatusIds, PageSetting pageSetting, int typeId);

	/**
	 * Get all repacking forms by status.
	 * @param param The search parameter.
	 * @param typeId The type id.
	 * @param divisionId The division id.
	 * @return The list of repacking forms by status.
	 */
	Page<Repacking> getAllRepackingByStatus(ApprovalSearchParam searchParam,
			List<Integer> formStatusIds, PageSetting pageSetting, int typeId, int divisionId);

	/**
	 * Search repacking forms.
	 * @param typeId The repacking type id
	 * @param criteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return The list of repacking forms.
	 */
	Page<Repacking> searchRepackingForms(int typeId, Integer divisionId, String criteria, PageSetting pageSetting);
	
	/**
	 * Get the re-packing transactions
	 * @param itemId The item id.
	 * @param warehouseId the warehouse id
	 * @param date the transaction date.
	 */
	Page<Repacking> getRepackingTransactions (int itemId, int warehouseId, Date date, PageSetting pageSetting);


	/**
	 * Get the reclass register data
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param warehouseId The warehouse id
	 * @param dateFrom The date from.
	 * @param dateTo The date to.
	 */
	Page<ReclassRegisterDto> getReclassRegisterData (Integer companyId, Integer divisionId, Integer warehouseId,
			Date dateFrom, Date dateTo, Integer statusId, PageSetting pageSetting);
}
