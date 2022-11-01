package eulap.eb.service.oo;

import java.io.InvalidClassException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.ObjectTypeDao;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ObjectType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.common.EBApplicationContextUtil;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;

/**
 * Business logic of object to object implementation. 

 *
 */
@Service
public class OOServiceHandler {
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private ObjectTypeDao objectTypeDao;
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * Create a new object for the parent and children. This will save automatically in the server.
	 * @param o2o object to object domain.
	 */
	public void createAndLinkObject (OODomain o2oFormDomain, String workflowName, User user, WorkflowServiceHandler wSHandler,
			ObjectDomainService objectDomainService, boolean isDeleteReference) {
		if (o2oFormDomain instanceof OOParent) {
			//saving parent to parent 
			OOParent ooParentForm = (OOParent) o2oFormDomain;
			boolean isNew = ooParentForm.getEbObjectId() == null;
			
			FormProperty formProp = wSHandler.getProperty(workflowName, user);
			Date date = new Date();
			OOProp ooProp = formProp.getOoProp();
			Integer parentObjectId = ooParentForm.getEbObjectId();
			if (isNew) {
				Integer parentTypeId = objectDomainService.getObjectTypeId(ooParentForm);
				if (parentTypeId == null) {
					parentTypeId = o2oFormDomain.getObjectTypeId();
				}
				EBObject parentObject = EBObject.getInstanceOf(user, date, parentTypeId);
				ebObjectDao.save(parentObject);
				parentObjectId = parentObject.getId();
				ooParentForm.setEbObjectId(parentObject.getId());
			} else {
				if (isDeleteReference) {
					// delete the reference and recreate the relationship.
					objectToObjectDao.deleteReference(parentObjectId);
				}
			}

			int parentToChildOoR = ooProp.getParentToChild();
			int childToOtherObject = ooProp.getChildToChild();
			List<OOChild> children = objectDomainService.getChildren(ooParentForm);
			if (children == null || children.isEmpty()) {
				children = ooParentForm.getChildren();
			}
			for (OOChild child : children) {
				int childTypeId = child.getObjectTypeId();
				EBObject childObject = EBObject.getInstanceOf(user, date, childTypeId);
				ebObjectDao.save(childObject);
				child.setEbObjectId(childObject.getId());
				// Create link parent and child relationship. 
				saveObjectToObjectRelationship(parentObjectId, childObject.getId(), parentToChildOoR, user);

				// Create link child to other object.
				Integer otherObjectId = child.getRefenceObjectId();
				if (otherObjectId != null && otherObjectId != 0) {
					saveObjectToObjectRelationship(otherObjectId, childObject.getId(), childToOtherObject, user);
				}
			}
		}
	}

	/**
	 * Get the object information processor.
	 * @param objectId the object id.
	 */
	public ObjectInfoProcessor getObjectInfoProcessor (int objectId, User user) throws ClassNotFoundException, InvalidClassException {
		EBObject ebObject = ebObjectDao.get(objectId);
		int objectTypeId = ebObject.getObjectTypeId();
		ObjectType oType = objectTypeDao.get(objectTypeId);
		String className = oType.getServiceClass();
		Object object = EBApplicationContextUtil.getInstanceof(applicationContext, className);
		return (ObjectInfoProcessor) object;
	}

	/**
	 * Get the object information.
	 * @param ebObjectId the eb-object id.
	 */
	public ObjectInfo getObjectInfo (int ebObjectId, User user) throws InvalidClassException, ClassNotFoundException {
		ObjectInfoProcessor infoProcessor = getObjectInfoProcessor(ebObjectId, user);
		return infoProcessor.getObjectInfo(ebObjectId, user);
	}

	/**
	 * Save object to object relationship
	 * @param parentObjectId The parent object id
	 * @param childObjectId The child object id
	 * @param ortypeId The OR type id
	 * @param user The current user logged user object
	 */
	public void saveObjectToObjectRelationship(Integer parentObjectId, Integer childObjectId, Integer ortypeId, User user) {
		objectToObjectDao.save(ObjectToObject.getInstanceOf(parentObjectId, childObjectId, ortypeId, user, new Date()));
	}
}
