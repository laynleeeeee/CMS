package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.UserCompanyHead;

/**
 * Handles the user company data access.

 */
public interface UserCompanyHeadDao extends Dao<UserCompanyHead> {

	/**
	 * Get the user company head per user.
	 * @param userId The user id.
	 * @return The user company head object.
	 */
	UserCompanyHead getUserCompanyHeadPerUser(Integer userId);
}
