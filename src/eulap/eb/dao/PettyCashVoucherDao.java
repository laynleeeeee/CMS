package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.report.DailyPettyCashFundReportParam;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.PettyCashVoucherDto;
import eulap.eb.web.dto.UnliquidatedPCVAgingDto;
import eulap.eb.web.dto.DailyPettyCashFundReportDto;

/**
 * DAO layer of {@link PettyCashVoucher}

 */

public interface PettyCashVoucherDao extends Dao<PettyCashVoucher>{
	/**
	 * Generates Sequence Number for {@link PettyCashVoucher}.
	 * @return
	 */
	Integer generateSequenceNo(Integer companyId, Integer divisionId);

	/**
	 * Get the list of {@link PettyCashVoucher}
	 * @param searchCriteria The search Criteria
	 * @param user The current logged user.
	 * @return The List of {@link PettyCashVoucher}
	 */
	List<PettyCashVoucher> searchPettyCashVouchers(int divisionId, String searchCriteria, User user);

	/** Get {@link PettyCashVoucher}.
	 * @param searchParam The search parameter.
	 * @param statuses The form status ids.
	 * @param pageSetting The page settings.
	 * @return The paged collection of {@link PettyCashVoucher}
	 */
	Page<PettyCashVoucher> getPettyCashVouchers(int typeId, ApprovalSearchParam searchParam, List<Integer> statuses,
			PageSetting pageSetting);

	/** Get the list of petty cash voucher register.
		 * @param companyId The company ID
		 * @param divisionId The Division ID
		 * @param custodianId The Custodian ID
		 * @param requestor The requestor ID
		 * @param dateFrom pcv start date.
		 * @param dateTo pcv end date.
		 * @param statusId The invoice status ID.
		 * @return The petty cash voucher register report.
	 */
	Page<PettyCashVoucherDto> getPettyCashVoucherRegisterData(Integer companyId, Integer divisionId, Integer custodianId,
			String requestor, Date dateFrom, Date dateTo, int statusId, PageSetting pageSetting);

	/** Get the page of petty cash voucher register.
	 * @param companyId The company ID
	 * @param divisionId The Division ID
	 * @param custodianId The Custodian ID
	 * @param requestor The requestor ID
	 * @param asOfDate pcv as of date.
	 * @param pageSetting The pageSetting.
	 * @return The unliquidated petty cash voucher aging report.
	 */
	Page<UnliquidatedPCVAgingDto> getUnliquidatedPCVAgingData(Integer companyId, Integer divisionId, Integer custodianId,
			String requestor, Date asOfDate, PageSetting pageSetting);

	/**
	 * Search the Daily Petty Cash Fund Report.
	 * @param param The object that holds the search parameters.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<DailyPettyCashFundReportDto> searchDailyPettyCashFundReport(DailyPettyCashFundReportParam param, PageSetting pageSetting);
}
