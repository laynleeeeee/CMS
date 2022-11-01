package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.IsClassSetup;

/**
 * Data access object for {@link IS_CLASS_SETUP}

 *
 */
public interface IsClassSetupDao extends Dao<IsClassSetup>{
	/**
	 * Get all the {@link IsClassSetup} in descending order by sequence order.
	 * @return The list of IsClassSetup.
	 */
	List<IsClassSetup> getIsClassSetups();

}
