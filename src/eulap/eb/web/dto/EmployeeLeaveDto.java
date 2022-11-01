package eulap.eb.web.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data transfer object for Employee Leaves.

 *
 */
public class EmployeeLeaveDto implements Serializable{
	private static final long serialVersionUID = 7116112597864219756L;
	private Integer employeeLeaveId;
	private Integer employeeId;
	private Date dateFrom;
	private Date dateTo;
	private Date updatedDate;
	private Integer workflowStatus;

	public Integer getEmployeeLeaveId() {
		return employeeLeaveId;
	}

	public void setEmployeeLeaveId(Integer employeeLeaveId) {
		this.employeeLeaveId = employeeLeaveId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getWorkflowStatus() {
		return workflowStatus;
	}

	public void setWorkflowStatus(Integer workflowStatus) {
		this.workflowStatus = workflowStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeLeaveDto [employeeLeaveId=").append(employeeLeaveId).append(", employeeId=")
				.append(employeeId).append(", dateFrom=").append(dateFrom).append(", dateTo=").append(dateTo)
				.append(", updatedDate=").append(updatedDate).append(", workflowStatus=").append(workflowStatus)
				.append("]");
		return builder.toString();
	}
}
