package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ProjectRetention;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.RetentionCostDto;

/**
 * Data access object for {@link ProjectRetention}

 */

public interface ProjectRetentionDao extends Dao<ProjectRetention> {

	/**
	 * Generate sequence number.
	 * @param companyId The company id.
	 * @param typeId The project retention type id.
	 * @return The sequence number.
	 */
	int generateSeqNo(int companyId, Integer typeId);

	/**
	 * Get all {@link ProjectRetention} by status id.
	 * @param searchParam The {@link ApprovalSearchParam}.
	 * @param formStatusIds The list of status ids.
	 * @param pageSetting The {@link PageSetting}.
	 * @param typeId The project retention type id.
	 * @return
	 */
	Page<ProjectRetention> getAllPrByStatus (ApprovalSearchParam searchParam, List<Integer> formStatusIds, 
			PageSetting pageSetting, int typeId);

	/**
	 * Get the list of project retentions.
	 * @param typeId The project retention type id.
	 * @param criteria The search criteria.
	 * @param pageSetting The page setting object.
	 * @return The list of project retentions in page format.
	 */
	Page<ProjectRetention> searchProjectRetentions(Integer typeId, String criteria, PageSetting pageSetting);

	/**
	 * Get the retention costs report data.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param customerId The customer id.
	 * @param customerAcctId The customer account id.
	 * @param dateFrom The date from delivery date filter.
	 * @param dateTo The date to delivery date filter.
	 * @param asOfDate The as of date filter for all not fully paid retentions.
	 * @param pageSetting The pagesetting object.
	 * @return The retention costs report data.
	 */
	Page<RetentionCostDto> generateRetentionCostRprt(Integer companyId, Integer divisionId, Integer customerId,
			Integer customerAcctId, Date dateFrom, Date dateTo, Date asOfDate, PageSetting pageSetting);
}
