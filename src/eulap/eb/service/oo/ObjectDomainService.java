package eulap.eb.service.oo;

import java.util.ArrayList;
import java.util.List;

import eulap.common.domain.Domain;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.EBObject;

/**
 * An interface for object base domain services.

 *
 */
public abstract class ObjectDomainService implements ObjectInfoProcessor{

	/**
	 * Get the parent of this object id.
	 * @param objectId the child of the parent
	 */
	public Domain getParent (EBObject child, ObjectToObjectDao objectToObjectDao) {
		EBObject parent = objectToObjectDao.getParent(child.getId());
		return getDomain(parent);
	}

	/**
	 * Get the children of this object.
	 * @param objectId the parent object id.
	 */
	public List<Domain> getChildren (EBObject parent,  ObjectToObjectDao objectToObjectDao) {
		List<EBObject> children = objectToObjectDao.getChildren(parent);
		List<Domain> domainChildren= new ArrayList<>();
		for (EBObject child : children) {
			Domain domain = getDomain(child);
			if (domain == null)
				continue;
			domainChildren.add(domain);
		}
		return domainChildren;
	}

	/**
	 * Get the object type of the domain. 
	 * @param ooDomain the object to object type domain. 
	 * @return the object type. 
	 */
	public Integer getObjectTypeId (OODomain ooDomain) {
		//TODO: Remove the OODomain.getObjectTypeId() in all domain level. Let the service (business Logic handle the identification of 
		//object type. 
		return null;
	}

	/**
	 *  Get the children of the domain
	 * @param ooDomain The parent domain object
	 * @return The list of children
	 */
	public List<OOChild> getChildren(OODomain ooDomain) {
		return null;
	}
}
