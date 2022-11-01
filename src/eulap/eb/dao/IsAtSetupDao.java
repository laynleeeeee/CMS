package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.IsAtSetup;

/**
 * Data access object for {@link IS_AT_SETUP}

 *
 */
public interface IsAtSetupDao extends Dao<IsAtSetup>{
	/**
	 * Get list of {@link IsAtSetup} that have accounts.
	 * @param isClassSetupId The Income statement class setup id.
	 * @param companyId The company id.
	 * @return list of IsAtSetups.
	 */
	List<IsAtSetup> getIsAtSetups (Integer isClassSetupId, Integer companyId);
}
