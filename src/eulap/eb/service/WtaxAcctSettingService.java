package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.WithholdingTaxAcctSettingDao;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;

/**
 * Service class that will handle business logic for {@link WithholdingTaxAcctSetting}

 */

@Service
public class WtaxAcctSettingService {
	@Autowired
	private WithholdingTaxAcctSettingDao wtAcctSettingDao;

	/**
	 * Get the withholding tax account setting
	 * @param wtAccountSettingId The withholding tax account setting id
	 * @return The withholding tax account setting
	 */
	public WithholdingTaxAcctSetting getWtAccountSetting(Integer wtAccountSettingId) {
		return wtAcctSettingDao.get(wtAccountSettingId);
	}

	/**
	 * Get all the list of withholding tax account setting
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param isCreditable True if get the creditable withholding taxes, otherwise false
	 * @return The list of withholding tax account setting
	 */
	public List<WithholdingTaxAcctSetting> getWtAccountSettings(Integer companyId, Integer divisionId, boolean isCreditable) {
		return wtAcctSettingDao.getWithholdingTaxAcctSettings(companyId, divisionId, isCreditable);
	}
}