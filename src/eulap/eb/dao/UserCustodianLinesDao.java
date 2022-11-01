package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.UserCustodianLines;

/**
 * Data access object for {@link UserCustodianLines}

 *
 */
public interface UserCustodianLinesDao extends Dao<UserCustodianLines>{

	/**
	 * Get list of {@link UserCustodianLines}
	 * @param userCustodianId The user custodian id
	 * @param userId The user id
	 * @return List of {@link UserCustodianLines}
	 */
	public List<UserCustodianLines> getUserCustodianLines(Integer userCustodianId, int userId);
}
