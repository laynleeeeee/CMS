package eulap.eb.web.dto;

import eulap.eb.domain.hibernate.UserGroupAccessRight;

/**
 * DTO for handling UG access rights of reports / admins.

 *
 */
public class UGAccessRightRAdminDto {
	private String reportName;
	private Integer moduleCode;
	private boolean allowAccess;
	private UserGroupAccessRight userGroupAccessRight;
	
	public UGAccessRightRAdminDto () {
		
	}
	
	public UGAccessRightRAdminDto (String reportName, Integer moduleCode,
			boolean allowAccess, UserGroupAccessRight userGroupAccessRight) {
		this.reportName = reportName;
		this.moduleCode = moduleCode;
		this.allowAccess = allowAccess;
		this.userGroupAccessRight = userGroupAccessRight;
	}
	
	public String getReportName() {
		return reportName;
	}
	
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public Integer getModuleCode() {
		return moduleCode;
	}
	
	public void setModuleCode(Integer moduleCode) {
		this.moduleCode = moduleCode;
	}
	
	public boolean isAllowAccess() {
		return allowAccess;
	}
	
	public void setAllowAccess(boolean allowAccess) {
		this.allowAccess = allowAccess;
	}
	
	public UserGroupAccessRight getUserGroupAccessRight() {
		return userGroupAccessRight;
	}
	
	public void setUserGroupAccessRight(UserGroupAccessRight userGroupAccessRight) {
		this.userGroupAccessRight = userGroupAccessRight;
	}

	@Override
	public String toString() {
		return "UGAccessRightReportDto [reportName=" + reportName
				+ ", moduleCode=" + moduleCode + ", allowAccess=" + allowAccess
				+ ", userGroupAccessRight=" + userGroupAccessRight + "]";
	}
}
