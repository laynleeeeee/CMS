package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.PettyCashReplenishmentLine;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidationLine;
import eulap.eb.service.report.PCVRRegisterParam;
import eulap.eb.web.dto.PCReplenishmentRegisterDto;

/**
 * Data access object interface for Petty Cash Replenishment

 */

public interface PettyCashReplenishmentLineDao extends Dao<PettyCashReplenishmentLine> {

	/**
	 * Get the list of {@link PettyCashReplenishmentLine}
	 * @param divisionId The division id
	 * @param userCustodianId The user custodian
	 * @param pageSetting The page setting
	 * @return List of {@link PettyCashReplenishmentLine}
	 */
	Page<PettyCashReplenishmentLine> getReplenishments(Integer divisionId, Integer userCustodianId, PageSetting pageSetting);

	/**
	 * Get the list of {@link PettyCashReplenishmentLine} associated at {@link PettyCashReplenishmentLine}
	 * @param objectId The {@link PettyCashVoucherLiquidationLine} id
	 * @return The associated {@link PettyCashReplenishmentLine}
	 */
	List<PettyCashReplenishmentLine> getAssociatedPettyCashReplenishment(Integer objectId);

	/**
	 * Get the list of {@link PCReplenishmentRegisterDto} associated at {@link PettyCashReplenishmentLine}
	 * @param companyId The Company ID
	 * @param division Id The division ID
	 * @param Custodian Id The Custodian ID
	 * @param pcrNo petty cash replenishment number
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @param status The AP Invocice Status Poster or Unposted
	 * @return The associated {@link PCReplenishmentRegisterDto}
	 */
	Page<PCReplenishmentRegisterDto> getReplenishmentsRegister(PCVRRegisterParam param, PageSetting pageSetting);
}
