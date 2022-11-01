package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

import eulap.common.util.PropertyLoader;
import eulap.eb.web.dto.ModuleConf;

/**
 * Generate the different CMS modules. 
 * this will look up to the following files:
 * <br>
 * 1. form.properties
 * 2. report.properties
 * 3. search.properties
 * 4. edit-form.properties
 * 5. delete-form.properties
 * 6. approval.properties
 * 7. admin.properties

 *
 */
public class EBModuleGenerator {
	public static final int FORMS = 0;
	public static final int REPORTS = 1;
	public static final int FORM_SEARCH = 2;
	public static final int EDIT_FORM = 3;
	public static final int DELETE_FORM = 4;
	public static final int APPROVAL = 5;
	public static final int ADMIN = 6;
	// 111 111 111 111 111 111 111 111 111 111
	// Maximum of 27 different modules
	private static final int MAX_MODULE_FLAG_ON = 268435455; // FFFFFFF
	private static final String MODULES_KEY = "modules";
	private static final String TITLE_KEY = "title";
	private static final String URI_KEY = "uri";
	private static final String PRODUCT_CODE = "product_code";
	private static final String MODULE_CODE = "module_code";
	private static final String EDIT_KEY = "edit";
	private static final String DELETE_KEY = "delete";
	private static final String REPORTS_KEY = "reports";
	private static final String WORKFLOW_KEY = "workflow";

	private static final Logger logger =  Logger.getLogger(EBModuleGenerator.class);

	private static Map<Integer, String> id2Poperties = new HashMap<Integer, String>();
	private static Map<String, Configuration> path2Configuration = new HashMap<>();
	private static Map<Integer, List<ModuleConf>> type2Modules = new HashMap<>();
	//private static List<ModuleConf> moduleConfs = new ArrayList<>();
	
	static {
		id2Poperties.put(FORMS, "form.properties");
		id2Poperties.put(REPORTS, "report.properties");
		id2Poperties.put(FORM_SEARCH, "search.properties");
		id2Poperties.put(EDIT_FORM, "edit-form.properties");
		id2Poperties.put(DELETE_FORM, "delete-form.properties");
		id2Poperties.put(APPROVAL, "approval.properties");
		id2Poperties.put(ADMIN, "admin.properties");
		
		try {
			type2Modules.put(FORMS, getAllModules("/client/conf/dev/", FORMS, -1, MAX_MODULE_FLAG_ON));
			type2Modules.put(REPORTS, getAllModules("/client/conf/dev/", REPORTS, -1, MAX_MODULE_FLAG_ON));
			type2Modules.put(FORM_SEARCH, getAllModules("/client/conf/dev/", FORM_SEARCH, -1, MAX_MODULE_FLAG_ON));
			type2Modules.put(EDIT_FORM, getAllModules("/client/conf/dev/", EDIT_FORM, -1, MAX_MODULE_FLAG_ON));
			type2Modules.put(DELETE_FORM, getAllModules("/client/conf/dev/", DELETE_FORM, -1, MAX_MODULE_FLAG_ON));
			type2Modules.put(APPROVAL, getAllModules("/client/conf/dev/", APPROVAL, -1, MAX_MODULE_FLAG_ON));
			type2Modules.put(ADMIN, getAllModules("/client/conf/dev/", ADMIN, -1, MAX_MODULE_FLAG_ON));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} 
	}

	public static class FormConfComparator implements Comparator<ModuleConf> {
		
		@Override
		public int compare(ModuleConf o1, ModuleConf o2) {
			if (o1.getTitle() != null && o2.getTitle() != null)
				return o1.getTitle().compareTo(o2.getTitle());
			return -1;
		}
	}

	/**
	 * Get all modules that for the product key. 
	 * @param productKey Base on product_key.properties
	 * @return
	 * @throws ConfigurationException 
	 */
	public static List<ModuleConf> getAllModules (String path, int productKey) throws ConfigurationException {
		//Maximum value of module key
		int maxBitCode = MAX_MODULE_FLAG_ON;
		// FORMS
		List<ModuleConf> modules = getAllModules(path, FORMS, productKey, MAX_MODULE_FLAG_ON);
		// REPORTS
		modules.addAll(getAllModules(path, REPORTS, productKey, maxBitCode));
		//FORM_SEARCH;
		modules.addAll(getAllModules(path, FORM_SEARCH, productKey, maxBitCode));
		//EDIT_FORM;
		modules.addAll(getAllModules(path, EDIT_FORM, productKey, maxBitCode));
		//DELETE_FORM;
		modules.addAll(getAllModules(path, DELETE_FORM, productKey, maxBitCode));
		//APPROVAL;
		modules.addAll(getAllModules(path, APPROVAL, productKey, maxBitCode));
		//ADMIN;
		modules.addAll(getAllModules(path, ADMIN, productKey, maxBitCode));

		Collections.sort(modules, new FormConfComparator ());
		return modules;
	}

	/**
	 * Get all modules per type.
	 * @param moduleType The moduel type.
	 * @return The list of all modules
	 * @throws ConfigurationException 
	 */
	public static List<ModuleConf> getALLModules (String formPath, int moduleType) throws ConfigurationException {
		// return getAllModules(formPath, moduleType, -1, MAX_MODULE_FLAG_ON);
		return type2Modules.get(moduleType);
	}

	/**
	 * Get the different modules.
	 * @throws ConfigurationException 
	 */
	public static List<ModuleConf> getAllModules (String path, int moduleType, int productKey, int moduleKey) throws ConfigurationException {
		String filePath = path + id2Poperties.get(moduleType);
		Configuration props = null;
		props = PropertyLoader.getConfiguration(filePath);
		return getModuleConf(path, props, productKey, moduleKey);		
	}

	/**
	 * Get the different modules that are allowed in a certain access code.
	 * @param moduleType the module type that will be retrieved. 
	 * @param accessCode The access code of the form. 
	 * @return The forms of that is enable to the access code.
	 * @throws ConfigurationException 
	 */
	public static List<ModuleConf> getModules (String formPath, int moduleType,  int productKey, int moduleKey) throws ConfigurationException {
		// return getAllModules(formPath, moduleType, productKey, moduleKey);
		return filterModules(moduleType, productKey, moduleKey);
	}

	private static List<ModuleConf> filterModules (int moduleType,  int productKey, int moduleKey) {
		List<ModuleConf> filtered = new ArrayList<>();
		List<ModuleConf> mcs = type2Modules.get(moduleType);
		for (ModuleConf mc : mcs) {
			if (mc.getProductCode() == productKey && 
					(mc.getModuleCode() & moduleKey) == mc.getModuleCode()) {
				filtered.add(mc);
			}
		}
		return filtered;
	}
	
	private static List<ModuleConf> getModuleConf (String formPath, Configuration props,  int productKey, int moduleKey) throws ConfigurationException {
		List<ModuleConf> ret = new ArrayList<ModuleConf>();
		String form = props.getString(MODULES_KEY);
		for (String formName : form.split(";")){
			ModuleConf conf = getModuleConf(formPath, props, formName, productKey, moduleKey);
			if (conf != null)
				ret.add(conf);
		}
		Collections.sort(ret, new FormConfComparator());
		return ret;
	}

	/**
	 * Get the specific edit configuration in the property file
	 * @param name the name that is mapped in the edit.properties
	 * @param productKey The product key of the configuration.
	 * @return The module configuration.
	 * @throws ConfigurationException 
	 */
	public static ModuleConf getEditModuleConfig (String formPath, String name, int productKey) throws ConfigurationException {
		String filePath = formPath + id2Poperties.get(EDIT_FORM);
		Configuration config = null;
		config = path2Configuration.get(filePath);
		if (config == null) {
			config = PropertyLoader.getConfiguration(filePath);
			path2Configuration.put(filePath, config);
		}
		return getModuleConf(formPath, config, name, productKey, MAX_MODULE_FLAG_ON);
	}

	private static ModuleConf getModuleConf (String formPath, Configuration config, String name,  int productKey, int moduleKey) throws ConfigurationException {
		//product code
		//String strProductCode =  props.getProperty(name+"."+PRODUCT_CODE, "0");
		//Integer.valueOf(strProductCode);
		int productCode = config.getInt(name+"."+PRODUCT_CODE, 0); 	
		// If product key is equal to negative one (-1)
		logger.debug("product key : " + productKey);
		logger.debug("product code : " + productCode);
		if (productKey != -1 && productKey != productCode)
			return null;

		//Module code
		//String strModuleCode =  props.getProperty(name+"."+MODULE_CODE, "0");
		int moduleCode = config.getInt(name+"."+MODULE_CODE, 0); //Integer.valueOf(strModuleCode);
		if ((moduleCode & moduleKey) != moduleCode)
			return null;
		
		String title = config.getString(name+"."+TITLE_KEY);
		String uri = config.getString(name+"."+URI_KEY);

		String workflow = config.getString(name+"."+WORKFLOW_KEY);
		//Edit
		String edit = config.getString(name+"."+EDIT_KEY);
		ModuleConf editConf = getModuleConf(formPath, EDIT_FORM, edit, productKey, moduleKey);
		String view = "";
		if (edit == null || editConf == null) {
			view = getViewUri(formPath, workflow);
		}
		String delete = config.getString(name+"."+DELETE_KEY);
		ModuleConf deleteConf = getModuleConf(formPath, DELETE_FORM, delete, productKey, moduleKey);
		//Delete

		String reportValue = config.getString(name+"."+REPORTS_KEY);
		List<ModuleConf> configuredReports = new ArrayList<ModuleConf>();
		if (reportValue != null && !reportValue.isEmpty()){
			String[] splittedReport = reportValue.split(";");
			String filePath = formPath + id2Poperties.get(REPORTS);
			Configuration reportProps = null;
			reportProps = path2Configuration.get(filePath);
			if (reportProps == null) {
				reportProps = PropertyLoader.getConfiguration(filePath);
				path2Configuration.put(filePath, config);
			}
			for (String reportName : splittedReport) {
				ModuleConf conf = getModuleConf(formPath, reportProps, reportName, productKey, moduleKey);	
				configuredReports.add(conf);
			}
		}
		
		return ModuleConf.getInstance(name, title, uri,  workflow, view, editConf,
				deleteConf, configuredReports, productCode, moduleCode);
	}

	private static ModuleConf getModuleConf (String formPath, int propType, String name,  int productKey, int moduleKey) throws ConfigurationException{
		ModuleConf conf = null;
		if (name != null) {
			String filePath = formPath + id2Poperties.get(propType);
			Configuration config = null;
			config = path2Configuration.get(filePath);
			if (config == null) {
				config = PropertyLoader.getConfiguration(filePath);
				path2Configuration.put(filePath, config);
			}
			conf = getModuleConf(formPath, config, name, productKey, moduleKey);
		}
		return conf;
	}

	private static String getViewUri (String formPath, String name) throws ConfigurationException {
		Configuration workflowProps = null;
		String filePath = formPath+"form-workflow.properties";
		workflowProps = path2Configuration.get(filePath);
		if (workflowProps == null) {
			workflowProps = PropertyLoader.getConfiguration(filePath);
			path2Configuration.put(filePath, workflowProps);
		}
		String form = workflowProps.getString(name+"."+EDIT_KEY);
		return form;
	}
}
