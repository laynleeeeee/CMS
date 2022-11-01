package eulap.eb.web.dto;

import java.util.List;

/**
 * Module configuration
 * 

 * 
 */
public class ModuleConf {
	private final String name;
	private final String title;
	private final String uri;
	private final String workflow;
	private final String viewUri;
	private final ModuleConf edit;
	private final ModuleConf delete;
	private final List<ModuleConf> reports;
	private final int productCode;
	private final int moduleCode;
	private ModuleConf (String name, String title,
			String uri, String workflow, String viewUri, ModuleConf edit, ModuleConf delete,
			List<ModuleConf> reports, int productCode, int moduleCode) {
		this.name = name;
		this.productCode =productCode;
		this.moduleCode = moduleCode;
		this.title = title;
		this.uri = uri;
		this.edit = edit;
		this.delete = delete;
		this.reports = reports;
		this.workflow = workflow;
		this.viewUri = viewUri;
	}
	
	public static ModuleConf getInstance (String name, String title,
			String uri, String workflow, String viewUri, ModuleConf edit, ModuleConf delete, 
			List<ModuleConf> reports, int productCode, int moduleCode) {
		ModuleConf moduleConf = new ModuleConf(name, title, uri, workflow, viewUri, edit, delete, reports, productCode, moduleCode);
		return moduleConf;
	}

	public String getTitle() {
		return title;
	}

	public String getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}
	
	public ModuleConf getEdit() {
		return edit;
	}

	public String getViewUri() {
		return viewUri;
	}

	public ModuleConf getDelete() {
		return delete;
	}

	public List<ModuleConf> getReports() {
		return reports;
	}

	public int getProductCode() {
		return productCode;
	}

	public int getModuleCode() {
		return moduleCode;
	}

	public String getWorkflow() {
		return workflow;
	}

	@Override
	public String toString() {
		return "ModuleConf [name=" + name + ", title=" + title + ", uri=" + uri
				+ ", workflow=" + workflow + ", viewUri=" + viewUri + ", edit="
				+ edit + ", delete=" + delete + ", reports=" + reports
				+ ", productCode=" + productCode + ", moduleCode=" + moduleCode
				+ "]";
	}
}