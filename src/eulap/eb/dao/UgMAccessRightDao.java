package eulap.eb.dao;

import java.util.Collection;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.UgMAccessRight;
import eulap.eb.domain.hibernate.User;

/**
 * User group access right data access object.

 *
 */
public interface UgMAccessRightDao extends Dao<UgMAccessRight> {
	
	/**
	 * Get the user group access right given the logged user.
	 * @param user The user object.
	 * @return The user group access rights.
	 */
	Collection<UgMAccessRight>  getUgMAccessRight(User user);
}
