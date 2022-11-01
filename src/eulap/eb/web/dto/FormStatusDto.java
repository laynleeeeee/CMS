package eulap.eb.web.dto;

import eulap.eb.domain.hibernate.FormStatus;

/**
 * DTO of {@link FormStatus}

 *
 */
public class FormStatusDto {
	private Integer moduleKey;
	private boolean allowAccess;
	private FormStatus formStatus;
	
	public FormStatusDto () {
		
	}
	
	public FormStatusDto (Integer moduleKey, FormStatus formStatus) {
		this.moduleKey = moduleKey;
		this.formStatus = formStatus;
	}
		
	public Integer getModuleKey() {
		return moduleKey;
	}
	
	public void setModuleKey(Integer moduleKey) {
		this.moduleKey = moduleKey;
	}
	
	public boolean isAllowAccess() {
		return allowAccess;
	}
	
	public void setAllowAccess(boolean allowAccess) {
		this.allowAccess = allowAccess;
	}
	
	public FormStatus getFormStatus() {
		return formStatus;
	}
	
	public void setFormStatus(FormStatus formStatus) {
		this.formStatus = formStatus;
	}

	@Override
	public String toString() {
		return "FormStatusDto [moduleKey=" + moduleKey + ", allowAccess="
				+ allowAccess + ", formStatus=" + formStatus + "]";
	}
}
