package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EBObject;

/**
 * Handles the CMS object data access.  

 *
 */
public interface EBObjectDao extends Dao<EBObject> {

	/**
	 * Get the list of eb object by object type id.
	 * @param objectTypId The object type id.S
	 * @return The list of eb object by object type id.
	 */
	List<EBObject> getEbObjectByObjectType(int objectTypId);
}