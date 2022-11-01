package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.UserGroupAccessRight;

/**
 * DTO for handling UG access rights of reports.

 *
 */
public class UGAccessRightWorkflowDto {
	private String workflowName;
	private Integer moduleCode;
	private List<FormStatusDto> statuses;
	private UserGroupAccessRight userGroupAccessRight;
	
	public UGAccessRightWorkflowDto () {
		
	}
	
	public UGAccessRightWorkflowDto (String workflowName, 
			List<FormStatusDto> statuses, UserGroupAccessRight userGroupAccessRight) {
		this.workflowName = workflowName;
		this.statuses = statuses;
		this.userGroupAccessRight = userGroupAccessRight;
	}
	
	public String getWorkflowName() {
		return workflowName;
	}
	
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	
	public Integer getModuleCode() {
		return moduleCode;
	}
	
	public void setModuleCode(Integer moduleCode) {
		this.moduleCode = moduleCode;
	}
	
	public List<FormStatusDto> getStatuses() {
		return statuses;
	}
	
	public void setStatuses(List<FormStatusDto> statuses) {
		this.statuses = statuses;
	}

	public UserGroupAccessRight getUserGroupAccessRight() {
		return userGroupAccessRight;
	}
	
	public void setUserGroupAccessRight(
			UserGroupAccessRight userGroupAccessRight) {
		this.userGroupAccessRight = userGroupAccessRight;
	}

	@Override
	public String toString() {
		return "UGAccessRightWorkflowDto [workflowName=" + workflowName
				+ ", moduleCode=" + moduleCode + ", statuses=" + statuses
				+ ", userGroupAccessRight=" + userGroupAccessRight + "]";
	}
}
