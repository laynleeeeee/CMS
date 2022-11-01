package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;

/**
 * Data access object of {@link WithholdingTaxAcctSetting}

 *
 */

public interface WithholdingTaxAcctSettingDao extends Dao<WithholdingTaxAcctSetting> {

	/**
	 * Get all the list of withholding tax account setting
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param isCreditable True if get the creditable withholding taxes, otherwise false
	 * @return The list of withholding tax account setting
	 */
	List<WithholdingTaxAcctSetting> getWithholdingTaxAcctSettings(Integer companyId, Integer divisionId, boolean isCreditable);
}