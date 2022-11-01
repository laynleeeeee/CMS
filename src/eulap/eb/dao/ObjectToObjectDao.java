package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * Handles the Object to Object data access.

 *
 */
public interface ObjectToObjectDao extends Dao<ObjectToObject> {

	/**
	 * Get the parent object
	 * @param ebObjectId the child object
	 */
	EBObject getParent(Integer ebObjectId);

	/**
	 * Get the reference of the object.
	 * @param ebObjectId the object id.
	 * @param orTypeId the object relationship type.
	 */
	EBObject getOtherReference (Integer ebObjectId, int orTypeId);
	
	/**
	 * Delete the object to object references. 
	 */
	void deleteReference (Integer ebObjectId);

	/**
	 * Get the reference objects
	 * @param fromObjectId From object id.
	 * @param toObjectId To object id
	 * @param orTypeId object relationship type.
	 * @return the reference objects
	 */
	List<ObjectToObject> getReferenceObjects(Integer fromObjectId, Integer toObjectId, Integer orTypeId);

	/**
	 * Get the reference objects
	 * @param fromObjectId From object id.
	 * @param toObjectId To object id
	 * @param orTypeId object relationship type.
	 * @param excludeorTypeId exclude these or types
	 * @return the reference objects
	 */
	List<ObjectToObject> getReferenceObjects(Integer fromObjectId, Integer toObjectId, Integer orTypeId, Integer ...excludeOrTypeId);

	/**
	 * Get all parents
	 */
	List<EBObject> getParents ();

	/**
	 * Get the reference objects
	 * @param toObjectId To object id
	 * @param orTypeId object relationship type.
	 * @return the reference objects
	 */
	List<ObjectToObject> getReferenceObjects(Integer toObjectId, int orTypeId);

	/**
	 * Get the children of the parent.
	 * @param parent parent object
	 */
	List<EBObject> getChildren (EBObject parent);
}