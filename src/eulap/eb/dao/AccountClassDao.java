package eulap.eb.dao;

import java.util.Collection;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.AccountClass;
import eulap.eb.domain.hibernate.User;

/**
 * Account class data access object.

 *
 */
public interface AccountClassDao extends Dao<AccountClass>{
	/**
	 * Get the collection of account classes.
	 * @param user The logged user.
	 * @return The collection of account classes.
	 */
	public Collection<AccountClass> getAccountClasses (User user);

}
