package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ArMiscellaneous;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.QuarterlyAlphaListOfPayeesDATDto;
import eulap.eb.web.dto.QuarterlyAlphaListOfPayeesDto;

/**
 * Data Access Object of {@link ArMiscellaneous}

 *
 */
public interface ArMiscellaneousDao extends Dao<ArMiscellaneous>{
	/**
	 * Search the AR Miscellaneous.
	 * @param searchCriteria The criteria that the use inputed, receipt number, amount.
	 * @param pageSetting The page setting.
	 * @return The list of AR Miscellaneous.
	 */
	Page<ArMiscellaneous> searchArMiscellaneous (String searchCriteria, PageSetting pageSetting);

	/**
	 * Search the AR Miscellaneous.
	 * @param searchCriteria The criteria that the use inputed, receipt number, amount.
	 * @param pageSetting The page setting.
	 * @param divisionId The division id.
	 * @return The list of AR Miscellaneous.
	 */
	Page<ArMiscellaneous> searchArMiscellaneous (String searchCriteria, PageSetting pageSetting, int divisionId);

	/**
	 * Get the paged AR Miscellaneous.
	 * @param searchParam Search AR Miscellaneous by receipt number.
	 * description, company number, date and amount.
	 * @param formStatusIds the statuses of the GL that will be retrieved.
	 * @param pageSetting The page setting.
	 * @return The paged data.
	 */
	Page<ArMiscellaneous> getAllArMiscellaneous (ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the paged AR Miscellaneous.
	 * @param typeId The ar miscellaneous type.
	 * @param searchParam Search AR Miscellaneous by receipt number.
	 * description, company number, date and amount.
	 * @param formStatusIds the statuses of the GL that will be retrieved.
	 * @param pageSetting The page setting.
	 * @return The paged data.
	 */
	Page<ArMiscellaneous> getAllArMiscellaneous (int typeId, ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Checks if the receipt number is unique.
	 * @param arReceipt The AR Miscellaneous object.
	 * @param companyId The unique id of the company associated with the
	 * AR Customer Account.
	 * @return True the receipt number is unique, otherwise false.
	 */
	public boolean isUniqueReceiptNo (ArMiscellaneous arMiscellaneous, Integer companyId);

	/**
	 * Get the AR Miscellaneous.
	 * @param companyId The Id of the selected company.
	 * @param miscellaneousTypeId The id of the miscellaneous type.
	 * @param customerId The id of the customer selected.
	 * @param customerAcctId The id of the customer account selected.
	 * @param receiptDateFrom The start date of the receipt date range.
	 * @param receiptDateTo The end date of the receipt date range.
	 * @param maturityDateFrom The start date of the maturity date range.
	 * @param maturityDatSeTo The end date of the maturity date range.
	 * @param amountFrom The starting amount of the amount range.
	 * @param amountTo The ending amount of the amount range.
	 * @param wfStatusId The form workflow status id.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<ArMiscellaneous> getArMiscellaneous(int companyId, int miscellaneousTypeId, int receiptMethodId, int customerId, int customerAcctId, String receiptNumber,
			Date receiptDateFrom, Date receiptDateTo, Date maturityDateFrom,Date maturityDateTo, Double amountFrom, Double amountTo, int wfStatusId, PageSetting pageSetting);

	/**
	 * Get all the AR Miscellaneous.
	 * @param companyId The id of the selected company.
	 * @param arLineId The id of the ar line.
	 * @param unitOfMeasureId The id of the unit of measurement.
	 * @param receiptDateFrom The start date of the receipt date range.
	 * @param receiptDateTo The end date of the receipt date range.
	 * @param maturityDateFrom The start date of the maturity date range.
	 * @param maturityDatSeTo The end date of the maturity date range.
	 * @param customerId The id of the customer selected.
	 * @param customerAcctId The id of the customer account selected.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<ArMiscellaneous> getArMiscellaneous(Integer companyId, Integer arLineId, Integer unitOfMeasureId, Date receiptDateFrom, 
			Date receiptDateTo, Date maturityDateFrom,Date maturityDateTo, Integer customerId, Integer customerAcctId, PageSetting pageSetting);

	/**
	 * Get the Ar Miscellaneous object by workflow id.
	 * @param formWorkflowId The form workflow id.
	 * @return The Ar Miscellaneous object.
	 */
	ArMiscellaneous getArMiscellaneousByWorkflow (Integer formWorkflowId);

	/**
	 * Generate the sequence number unique per company.
	 * @param companyId the company id.
	 * @param divisionId The division id.
	 * @return the new sequence number.
	 */
	Integer generateSequenceNumber (int companyId, Integer divisionId);

	/**
	 * Get the list of quarterly alphalist of payees DTO
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param month The month
	 * @param year The year
	 * @param processTin True, if processed the TIN, otherwise false
	 * @return List of quarterly alphalist of payees DTO
	 */
	List<QuarterlyAlphaListOfPayeesDto> getQuarterlyAlphalistOfPayees(Integer companyId, Integer divisionId, Integer month,
			Integer year, boolean processTin, String wtTypeId);

	/**
	 * Get the Alphalist of Payees
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param monthFrom The month from
	 * @param monthTo The month to
	 * @param year The year
	 * @param wtTypeId The wt type id list 
	 * @return Alphalist of Payees 
	 */
	List<QuarterlyAlphaListOfPayeesDATDto> getAlphalistOfPayees(Integer companyId, Integer divisionId, Integer monthFrom, Integer monthTo, Integer year, String wtTypeId);
}
