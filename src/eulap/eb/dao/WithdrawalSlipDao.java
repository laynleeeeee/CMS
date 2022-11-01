package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data Access Object of {@link WithdrawalSlip}

 *
 */
public interface WithdrawalSlipDao extends Dao<WithdrawalSlip>{

	/**
	 * Generate Withdrawal slip number.
	 * @param companyId Th company id.
	 * @return @return The generated withdrawal slip number.
	 */
	Integer generateWSNumber(Integer companyId);

	/**
	 * Get the withdrawal slip.
	 * @param param The search parameter.
	 * @return The paged withdrawal slip.
	 */
	Page<WithdrawalSlip> getWithdrawalSlip(ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);
}
