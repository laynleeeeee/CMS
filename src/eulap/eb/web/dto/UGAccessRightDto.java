package eulap.eb.web.dto;

import java.util.List;

/**
 * User Group Access DTO.

 *
 */
public class UGAccessRightDto {
	private Integer userGroupId;
	
	private List<UGAccessRightRAdminDto> uGarAdminDtos;
	private List<UGAccessRightFormDto> uGARFormDtos;
	private List<UGAccessRightRAdminDto> uGARReportDtos;	
	private List<UGAccessRightWorkflowDto> uGARWorkflowDtos;
	
	public UGAccessRightDto () {
		
	}
	
	public UGAccessRightDto (Integer userGroupId, 
			List<UGAccessRightRAdminDto> uGarAdminDtos, 
			List<UGAccessRightFormDto> uGARFormDtos, 
			List<UGAccessRightRAdminDto> uGARReportDtos, 
			List<UGAccessRightWorkflowDto> uGARWorkflowDtos ) {
		this.userGroupId = userGroupId;
		this.uGarAdminDtos = uGarAdminDtos;
		this.uGARFormDtos = uGARFormDtos;
		this.uGARReportDtos = uGARReportDtos;
		this.uGARWorkflowDtos = uGARWorkflowDtos;
	}
	
	public Integer getUserGroupId() {
		return userGroupId;
	}
	
	public void setUserGroupId(Integer userGroupId) {
		this.userGroupId = userGroupId;
	}
	
	public List<UGAccessRightRAdminDto> getuGarAdminDtos() {
		return uGarAdminDtos;
	}
	
	public void setuGarAdminDtos(List<UGAccessRightRAdminDto> uGarAdminDtos) {
		this.uGarAdminDtos = uGarAdminDtos;
	}
	
	public List<UGAccessRightFormDto> getuGARFormDtos() {
		return uGARFormDtos;
	}
	
	public void setuGARFormDtos(List<UGAccessRightFormDto> uGARFormDtos) {
		this.uGARFormDtos = uGARFormDtos;
	}
	
	public List<UGAccessRightRAdminDto> getuGARReportDtos() {
		return uGARReportDtos;
	}
	
	public void setuGARReportDtos(List<UGAccessRightRAdminDto> uGARReportDtos) {
		this.uGARReportDtos = uGARReportDtos;
	}
	
	public List<UGAccessRightWorkflowDto> getuGARWorkflowDtos() {
		return uGARWorkflowDtos;
	}
	
	public void setuGARWorkflowDtos(
			List<UGAccessRightWorkflowDto> uGARWorkflowDtos) {
		this.uGARWorkflowDtos = uGARWorkflowDtos;
	}
}
