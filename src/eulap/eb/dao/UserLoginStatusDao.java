package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.UserLoginStatus;

public interface UserLoginStatusDao extends Dao<UserLoginStatus>{

	/**
	 * Check if user is already existing
	 * @param userId
	 * @return return True if user is existing, otherwise False.
	 */
	boolean isExisting (int userId);
	
	int getFailedLoginAttempts(int userId);
	
	UserLoginStatus getUserLoginStatus(int userId);
}
