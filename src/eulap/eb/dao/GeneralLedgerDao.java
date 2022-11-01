package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data access object of {@link GeneralLedger}


 */
public interface GeneralLedgerDao extends Dao<GeneralLedger>{
	/**
	 * Generate sequence number.
	 * @return Generated sequence number.
	 */
	int generateSequenceNo (int divisionId);

	/**
	 * Get the General Ledger.
	 * @param id The unique id of the GL
	 * @return The general ledger.
	 */
	GeneralLedger getGL (int id);

	/**
	 * Get the all of the general ledger data.
	 * @param searchParam Searches the GL for company number, amount, date, sequence number and description.
	 * @param formStatusIds the statuses of the GL that will be retrieved.
	 * @param pageSetting The page setting.
	 * @return The paged data.
	 */
	Page<GeneralLedger> getAllGeneralLedger (ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Search for general ledger entries.
	 * @param sequenceNo The sequence number of the general ledger.
	 * @param description The description of the general ledger.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<GeneralLedger> searchGeneralLedger (String searchCriteria, PageSetting pageSetting);

	/**
	 * Search for general ledger entries.
	 * @param divisionId division Id
	 * @param sequenceNo The sequence number of the general ledger.
	 * @param description The description of the general ledger.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<GeneralLedger> searchGeneralLedgers (Integer divisionId, String searchCriteria, PageSetting pageSetting);
	
	/**
	 * Get the list of General Ledger that has null Form Workflow Id.
	 * @return The list of General Ledger that has null Form Workflow Id.
	 */
	List<GeneralLedger> getGLWithNullWF ();
	
	/**
	 * Checks if the GENERAL_LEDGER table contains at lease one null Form Workflow Id.
	 * @return True if the GENERAL_LEDGER table contains at lease one null Form Workflow Id,
	 * otherwise false.
	 */
	boolean hasNullFW();
	
	/**
	 * Get the paged results of General Ledger by criteria for Journal Voucher Register report.
	 * @param companyId  The company id.
	 * @param divisionId The divisionId.
	 * @param fromGLDate The gl date start range.
	 * @param toGLDate The gl date end range.
	 * @param status The gl status.
	 * @param createdBy The user id for created by.
	 * @param updatedBy The user id for updated by.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<GeneralLedger> searchJournalVoucherRegister (Integer companyId, Integer divisionId, Date fromGLDate, 
			Date toGLDate, Integer status, Integer createdBy, Integer updatedBy, PageSetting pageSetting);
	
	/**
	 * Get the general ledger object by workflow.
	 * @param formWorkflowId The form workflow id.
	 * @return The general ledger.
	 */
	GeneralLedger getGLByWorkflow (Integer formWorkflowId);

	/**
	 * Get the list of all general ledgers by division for approval
	 * @param typeId The division type id
	 * @param searchParam The search parameter object
	 * @param formStatusIds The list of form status ids
	 * @param pageSetting The page settings
	 * @return The list of all general journals by division for approval
	 */
	Page<GeneralLedger> getAllGLDivsByStatus(int typeId, ApprovalSearchParam searchParam,
			List<Integer> formStatusIds, PageSetting pageSetting);
}
