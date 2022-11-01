package eulap.eb.service.oo;

import java.util.List;

import eulap.common.domain.Domain;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.User;

/**
 * An interface class that handles the processing in getting the information of the eb-object.   

 *
 */
public interface ObjectInfoProcessor {

	/**
	 * Get the object information. 
	 * @param the eb-object id. 
	 * @param user The current logged user.
	 * @return the object information. 
	 */
	ObjectInfo getObjectInfo (int ebObjectId, User user);

	/**
	 * Get the children of this object.
	 * @param objectId the parent object id.
	 */
	List<Domain> getChildren (EBObject parent,  ObjectToObjectDao objectToObjectDao);
	/**
	 * Get the domain object 
	 * @param object The object of the domain
	 * @return return the domain associated to the object id. Null if there is now domain associated with object id. 
	 */
	Domain getDomain (EBObject ebObject);
}
