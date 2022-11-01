package eulap.eb.service.oo;

import eulap.eb.domain.hibernate.EBObject;

public interface OODomain {
	/**
	 * The object id of this domain.
	 * @return
	 */
	EBObject getEbObject ();
	
	/**
	 * Set the object id.
	 */
	void setEbObjectId (Integer eBObjecctId);

	/**
	 * Get the object type of this domain.
	 */
	Integer getObjectTypeId();
	
	/**
	 * Get the object id of this object.
	 */
	Integer getEbObjectId();
}
