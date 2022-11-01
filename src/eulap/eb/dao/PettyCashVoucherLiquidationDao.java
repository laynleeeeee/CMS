package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidation;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.report.PCVLiquidationRegisterParam;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.PettyCashVoucherLiquidationRegisterDTO;

/**
 * DAO layer of {@link PettyCashVoucherLiquidation}

 */

public interface PettyCashVoucherLiquidationDao extends Dao<PettyCashVoucherLiquidation>{
	/**
	 * Generates Sequence Number for {@link PettyCashVoucherLiquidation}.
	 * @return
	 */
	Integer generateSequenceNo(Integer companyId, Integer divisionId);

	/**
	 * Get the list of {@link PettyCashVoucherLiquidation}
	 * @param searchCriteria The search Criteria
	 * @param user The current logged user.
	 * @return The List of {@link PettyCashVoucherLiquidation}
	 */
	List<PettyCashVoucherLiquidation> searchPettyCashVoucherLiquidations(int divisionId, String searchCriteria, User user);

	/** Get {@link PettyCashVoucherLiquidation}.
	 * @param searchParam The search parameter.
	 * @param statuses The form status ids.
	 * @param pageSetting The page settings.
	 * @return The paged collection of {@link PettyCashVoucherLiquidation}
	 */
	Page<PettyCashVoucherLiquidation> getPettyCashVoucherLiquidations(int typeId, ApprovalSearchParam searchParam,
			List<Integer> statuses, PageSetting pageSetting);

	/**
	 * Get {@link PettyCashVoucher}
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param userCustodianId The user custodian id
	 * @param requestor The requestor Name
	 * @param pcvNo The petty cash voucher sequence number
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @param pageSetting The page settings
	 * @return Page of {@link PettyCashVoucher}
	 */
	Page<PettyCashVoucher> getPCVReferences(Integer companyId, Integer divisionId, Integer userCustodianId,
			String requestor, Integer pcvNo, Date dateFrom, Date dateTo, PageSetting pageSetting);

	/**
	 * Get the list of {@link PettyCashVoucherLiquidation}
	 * @param pcvId The {@link PettyCashVoucher} id
	 * @return The list of {@link PettyCashVoucherLiquidation}
	 */
	List<PettyCashVoucherLiquidation> getAssociatedLiquidations(Integer pcvId);
	/**
	 * Get {@link PettyCashVoucher}
	 * Get {@link PettyCashVoucherLiquidationRegister}
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param userCustodianId The user custodian id
	 * @param requestorName The requestor Name
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @param pageSetting The page settings
	 * @param transactionStatusId The status of transaction
	 * @return Page of {@link PettyCashVoucherLiquidationRegister}
	 */
	Page<PettyCashVoucherLiquidationRegisterDTO> getPettyCashVoucherLiquidationRegister(
			PCVLiquidationRegisterParam param, PageSetting pageSetting);

	/**
	 * Get the total liquidated petty cash amount per day.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param userCustodianAcctId The user custodian account id.
	 * @param currentStatusId The current status id.
	 * @param date The date.
	 * @return The total liquidated petty cash amount per day.
	 */
	double getTotalPettyCashLiquidationPerDay(Integer companyId, Integer divisionId, Integer userCustodianAcctId, 
			Date date);

}
