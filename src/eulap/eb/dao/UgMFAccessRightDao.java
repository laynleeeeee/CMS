package eulap.eb.dao;

import java.util.Collection;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.UgMFAccessRight;
import eulap.eb.domain.hibernate.User;

/**
 * Module function access right data access object.

 *
 */
public interface UgMFAccessRightDao extends Dao<UgMFAccessRight>{
	
	/**
	 * Get the module function access right given the logged user.
	 * @param user
	 * @return The module function access rights.
	 */	
	Collection<UgMFAccessRight> getUGMFAccessRight (User user);
}
