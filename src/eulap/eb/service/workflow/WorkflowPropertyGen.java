package eulap.eb.service.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import eulap.common.util.PropertyLoader;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.EBModuleGenerator;
import eulap.eb.service.oo.OOProp;
import eulap.eb.web.dto.ModuleConf;

/**
 * A class that reads the workflow related property file and converts it to objects. 

 *
 */
public class WorkflowPropertyGen {
	private static final Logger logger = Logger.getLogger(WorkflowPropertyGen.class);
	private static final String TYPE_ID = "typeId";
	private static final String FORM = "form";
	private static final String EDIT = "edit";
	private static final String SERVICE = "service";
	private static final String FLOW = "flow";
	private static final String TITLE = "title";
	private static final String PRINT = "print";
	private static final String FLOW_NEXT = "next";
	private static final String FLOW_REQUIRED = "required";
	private static final String FLOW_STATUS_ID = "statusId";
	private static final String FLOW_PRODUCT_CODE = "product_code";
	private static final String FLOW_MODULE_CODE = "module_code";
	private static final String FLOW_IS_EDITABLE = "is_editable";
	private static final String ACCT_TRANSACTION_TYPES = "acct_transaction_types";
	private static final String OO_PARENT_TO_CHILD = "oo.parent_to_child";
	private static final String OO_CHILD_TO_CHILD = "oo.child_to_child";
	private static Map<String, Configuration> path2Configuration = new HashMap<>();

	public static List<FormProperty> getAllFormProperties (String path, User user, FormPropertyHandler extraHandler) throws ConfigurationException {
		List<FormProperty> formProperties = new ArrayList<FormProperty>();
		Configuration config = null;
		try {
			config = PropertyLoader.getConfiguration(path + "form-workflow.properties");
			Iterator<String> keys = config.getKeys();
			List<String> processedForms = new ArrayList<String>();
			while(keys.hasNext()) {
				String rawFormName = keys.next();
				logger.debug("Key " + rawFormName);

				int endOfFormName = rawFormName.indexOf(".");
				logger.debug("form name" + endOfFormName);
				String formName = rawFormName.substring(0, endOfFormName);
				if (!processedForms.contains(formName)) {
					Log.debug("Getting the property of " + formName);
					FormProperty fromProperty = getFormProperty(path, formName, user, extraHandler);
					processedForms.add(formName);
					formProperties.add(fromProperty);
				}
			}
		} finally {
			if (config != null) {
				config.clear();
			}
		}
		return formProperties;
	}
	/**
	 * Get all form properties from form-workflow.properties
	 * @param path The path of the property file. 
	 * @param user The current login user. 
	 * @return All of the form properties that is configured in form-worklow.properties/
	 * @throws ConfigurationException 
	 */
	public static List<FormProperty> getAllFormProperties (String path, User user) throws ConfigurationException {
		return getAllFormProperties(path, user, null);
	}

	/**
	 * Get the form property given the name.
	 * @param name The name of the form in the property file. 
	 * @param user The current login user.
	 */
	public static FormProperty getFormProperty (String path, String name, User user, FormPropertyHandler extraHandler) {
		FormProperty formProperty = null;
		Configuration config = null;
		try {
			String filePath = path+"form-workflow.properties";
			config = path2Configuration.get(filePath);
			if (config == null) {
				config = PropertyLoader.getConfiguration(filePath);
				path2Configuration.put(filePath, config);
			}

			List<Integer> transactionTypes = new ArrayList<Integer>();
			int productKey = 0;
			int typeId = config.getInt(name + "." + TYPE_ID, 0);
			String form = config.getString(name + "." + FORM, "");
			String print = config.getString(name + "." + PRINT, "");
			String service = config.getString(name + "." + SERVICE, "");
			String flow = config.getString(name + "." + FLOW, "");
			String formTitle = config.getString(name + "." + TITLE, "");
			// Object to object property
			int parentToChild = config.getInt(name + "." + OO_PARENT_TO_CHILD, 0);
			int childToChild = config.getInt(name + "." + OO_CHILD_TO_CHILD, 0);
			OOProp ooProp = OOProp.getInstanceOf(parentToChild, childToChild);
			String strAcctTransTypes =  config.getString(name + "." + ACCT_TRANSACTION_TYPES, "");
			if (!strAcctTransTypes.isEmpty()) {
				for (String strTransType : strAcctTransTypes.split(";")) {
					int transType = Integer.valueOf(strTransType);
					switch (transType) {
					case FormProperty.ACCT_RECEIPT: // fall through
					case FormProperty.ACCT_INVOICE: // fall through
					case FormProperty.ACCT_AR_TRANSACTION: // fall through
					case FormProperty.ACCT_PIAD: // fall through
						break;
					default:
						throw new RuntimeException("Unknown accounting transaction type.");
					}
					transactionTypes.add(transType);
				}
			}

			List<FormStatusProp> formStatusProps = new ArrayList<FormStatusProp>();
			if (flow != null && !flow.trim().isEmpty()) {
				List<Integer> nextIds = null;
				for (String strFlowId : flow.split(";")) {
					nextIds = new ArrayList<Integer>();
					String next = config.getString(name + "." +strFlowId + "." + FLOW_NEXT);
					for (String strNextId : next.split(";")) {
						nextIds.add(Integer.valueOf(strNextId));
					}
					int id = Integer.valueOf(strFlowId);
					int statusId = config.getInt(name + "." + strFlowId + "." + FLOW_STATUS_ID);
					String title = config.getString(name + "." + strFlowId + "." + TITLE);
					String required = config.getString(name + "." + strFlowId + "." + FLOW_REQUIRED);
					List<Integer> requiredStatus = new ArrayList<Integer>();
					if (required != null && !required.isEmpty()) {
						for (String strRequired : required.split(";")) {
							requiredStatus.add(Integer.valueOf(strRequired));
						}
					}
					String productCode = config.getString(name + "." + strFlowId + "." + FLOW_PRODUCT_CODE, "0");
					int productCodeFlag = Integer.valueOf(productCode);
					if (productKey == 0) {
						productKey = productCodeFlag;
					}
					String moduleCode = config.getString(name + "." + strFlowId + "." + FLOW_MODULE_CODE, "0");
					int moduleCodeFlag = Integer.valueOf(moduleCode);
					String strIsEditable = config.getString(name + "."+ strFlowId + "." + FLOW_IS_EDITABLE, "false");
					boolean isEditable = Boolean.valueOf(strIsEditable);
					formStatusProps.add(FormStatusProp.getInstanceOf(id, statusId, title, nextIds.get(0),
							productCodeFlag, moduleCodeFlag, requiredStatus, isEditable, nextIds));
				}
			}
			String editConf = config.getString(name + "." + EDIT);
			String editUri = getEditUri(path, config.getString(name + "." + EDIT), productKey);

			formProperty =  FormProperty.getInstanceOf(name, formTitle, typeId, form, editConf, editUri,
					print, service, ooProp, formStatusProps, transactionTypes);
			if (extraHandler != null) {
				extraHandler.handleProperties(formProperty, name, config);
			}
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
		return formProperty;
	}

	/**
	 * Get the URI for the editing of form.
	 * @throws ConfigurationException 
	 */
	private static String getEditUri(String path, String name, int productKey) throws ConfigurationException {
		ModuleConf editConfig = EBModuleGenerator.getEditModuleConfig(path, name, productKey);
		// If the editConfig is null it must return an empty string.
		// This is a special case handling for forms (YBL) that don't
		// have "edit" property in the form-workflow.properties.
		return editConfig != null ? editConfig.getUri() : "";
	}
}
