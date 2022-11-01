package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.FormDeduction;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data access object of {@link ProcessingReport}

 *
 */
public interface ProcessingReportDao extends Dao<ProcessingReport> {

	/**
	 * Generate sequence number.
	 * @param companyId The company filter.
	 * @return The generated sequence number.
	 */
	Integer generateSeqNo (int processingTypeId, Integer companyId);

	/**
	 * Search for processing reports.
	 * @param criteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return Paged search results.
	 */	
	Page<ProcessingReport> searchProcessingReports (String criteria, int processingTypeId, PageSetting pageSetting);

	/**
	 * Get the paged FormDeductions
	 * @param searchParam 
	 * @param param The search paramenter
	 * @param typeId The form deduction type id.
	 * @return The paged collection of {@link FormDeduction}
	 */
	Page<ProcessingReport> getProcessingReports(ApprovalSearchParam searchParam, List<Integer> formStatusIds, Integer typeId, PageSetting pageSetting);

	/**
	 * Get the list processing report objects
	 * @param typeId The processing report type id
	 * @param startDate The start date
	 * @param currentDate The as of date
	 * @return The list processing report objects
	 */
	List<ProcessingReport> getProcessingReportByTypeId(Integer typeId, Date startDate, Date currentDate);
}
