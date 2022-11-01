package eulap.eb.service.oo;

import java.io.InvalidClassException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import eulap.common.util.PropertyLoader;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.ObjectTypeDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.ObjectType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.workflow.InventoryWorkflowService;
import eulap.eb.service.workflow.WorkflowService;
import eulap.eb.service.workflow.WorkflowServiceHandler;

/**
 * A helper class that link the objects. 

 *
 */
@Service
public class OOLinkHelper {
	@Autowired 
	private EBObjectDao ebObjectDao;
	@Autowired
	private ObjectTypeDao objectTypeDao;
	@Autowired
	private ObjectToObjectDao ooDao;
	@Autowired
	private String CMSFormPath;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private ApplicationContext applicationContext;
	// We will keep the reference instance since the service layer
	private Map<Integer, InventoryWorkflowService> objectType2Service = new HashMap<>();
	private static final String WORKFLOW_NAME = "workflow.name";

	/**
	 * Get the parent of the object.
	 * @param childObjectId child to object id. 
	 * @param user The current loged user. 
	 */
	public BaseFormWorkflow getParent (EBObject ebObject, User user) throws InvalidClassException, ClassNotFoundException {
		EBObject parent = getParent(ebObject.getId());
		String formPath = CMSFormPath;
		Properties ooProp = PropertyLoader.getProperties(formPath+"oo.properties");
		String propertyName = ooProp.getProperty(parent.getObjectTypeId()+"."+WORKFLOW_NAME);
		WorkflowService workflowService =
				workflowServiceHandler.getFormServiceHandler(propertyName, user);
		return workflowService.getForm(parent.getId());
	}

	/**
	 * Get the reference short description of this object.
	 * @param ebObjectId the object id.
	 * @param orTypeId object relationship type.
	 * @param user the current log in user.
	 */
	public String getRefShortName (Integer ebObjectId, Integer orTypeId, User user) throws InvalidClassException, ClassNotFoundException{
		EBObject otherObjectId = getReferenceObject(ebObjectId, orTypeId);
		if(otherObjectId == null) {
			return null;
		}

		BaseFormWorkflow form = getParent(otherObjectId, user);
		return form.getShortDescription();
	}

	/**
	 * Get the reference object.
	 */
	public EBObject getReferenceObject (Integer ebObjectId, Integer orTypeId) {
		return ooDao.getOtherReference(ebObjectId, orTypeId);
	}

	/**
	 * Get the parent of this object.
	 */
	public EBObject getParent(Integer childObjectId) {
		return ooDao.getParent(childObjectId);
	}

	/**
	 * Get the workflow-service associated to this EB object id.  
	 * @param objectId
	 * @return
	 * @throws ClassNotFoundException
	 */
	public InventoryWorkflowService getWorkflowService (int objectId) throws ClassNotFoundException {
		EBObject ebObj = ebObjectDao.get(objectId);
		Integer objectTypeId = ebObj.getObjectTypeId();
		InventoryWorkflowService service = objectType2Service.get(objectTypeId);
		if (service == null) {
			ObjectType ot = objectTypeDao.get(objectTypeId);
			service = getInstance(ot.getServiceClass());
			objectType2Service.put(ot.getId(), service);
		}
		return service;
	}

	private <T> T getInstance (String clazzName) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(clazzName);
		Object object = applicationContext.getBean(clazz);
		return (T) object;
	}
}
