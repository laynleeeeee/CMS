package eulap.eb.web.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data transfer object for Available leaves.

 *
 */
public class AvailableLeavesDto implements Serializable{
	private static final long serialVersionUID = -8471731915146595646L;
	private Integer leaveTypeId;
	private String LeaveType;
	private Integer employeeId;
	private Double availableLeaves;
	private Date erUpdatedDate;
	private Date elsUpdatedDate;

	public String getLeaveType() {
		return LeaveType;
	}

	public void setLeaveType(String leaveType) {
		LeaveType = leaveType;
	}

	public Integer getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Integer leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Double getAvailableLeaves() {
		return availableLeaves;
	}

	public void setAvailableLeaves(Double availableLeaves) {
		this.availableLeaves = availableLeaves;
	}

	public Date getErUpdatedDate() {
		return erUpdatedDate;
	}

	public void setErUpdatedDate(Date erUpdatedDate) {
		this.erUpdatedDate = erUpdatedDate;
	}

	public Date getElsUpdatedDate() {
		return elsUpdatedDate;
	}

	public void setElsUpdatedDate(Date elsUpdatedDate) {
		this.elsUpdatedDate = elsUpdatedDate;
	}

	@Override
	public String toString() {
		return "AvailableLeavesDto [leaveTypeId=" + leaveTypeId + ", LeaveType=" + LeaveType + ", employeeId="
				+ employeeId + ", availableLeaves=" + availableLeaves + ", erUpdatedDate=" + erUpdatedDate
				+ ", elsUpdatedDate=" + elsUpdatedDate + "]";
	}
}
