package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.LoanProceeds;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.LoanAcctHistoryDto;
import eulap.eb.web.dto.LoanProceedsDto;

/**
 * Data Access Object for {@link LoanProceeds}

 *
 */
public interface LoanProceedsDao extends Dao<LoanProceeds>{

	/**
	 * Get the list of direct loan proceeds data in a paged format
	 * @param searchParam The search parameter criteria
	 * @param formStatusIds The form status id
	 * @param pageSetting The page setting
	 * @param typeId The loan proceeds type id.
	 * @return The list of loan proceeds data in a paged format
	 */
	Page<LoanProceeds> getAllLoanProceeds(ApprovalSearchParam searchParam, List<Integer> statuses,
			PageSetting pageSetting, int typeId);

	/**
	 * Search loan proceeds objects.
	 * @param typeId The loan proceed type id.
	 * @param criteria The loan proceeds sequence number search criteria.
	 * @param pageSetting The page setting object.
	 * @return The list of loan proceeds.
	 */
	Page<LoanProceeds> searchLoanProceeds(Integer typeId, String criteria, PageSetting pageSetting);

	/**
	 * Get the loan account history report data
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param supplierId The supplier id
	 * @param supplierAcctId The supplier account id
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @return The loan account history report data
	 */
	List<LoanAcctHistoryDto> getLoanAcctHistoryData(int companyId, int divisionId, int supplierId,
			int supplierAcctId, Date dateFrom, Date dateTo);

	/**
	 * Get the completed loan proceeds as reference transaction for AP Loan.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param supplierid The supplier id.
	 * @param supplierAcctId The supplier account id.
	 * @param dateFrom The start date filter.
	 * @param dateTo The end date filter.
	 * @param statusId The status id.
	 * @param lpNumber The loan proceeds sequence number.
	 * @param pageSetting The {@link PageSetting}
	 * @return The list of completed {@link LoanProceedsDto} in page format.
	 */
	Page<LoanProceedsDto> getReferenceLoanProceeds(Integer companyId, Integer divisionId, Integer supplierid,
			Integer supplierAcctId, Date dateFrom, Date dateTo, Integer statusId, Integer lpNumber,
			PageSetting pageSetting);

	/**
	 * Get the loan balances summary report data
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param balanceOption The balance option
	 * @param asOfDate The as of date
	 * @return The loan balances summary report data
	 */
	List<LoanAcctHistoryDto> getLoanBalancesSummaryData(int companyId, int divisionId, int balanceOption, Date asOfDate);

	int generateLpSequenceNo(int companyId, int typeId);
}
