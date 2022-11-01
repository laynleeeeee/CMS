package eulap.eb.web.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data transfer object for Action notice.

 *
 */
public class ActionNoticeDto implements Serializable{
	private static final long serialVersionUID = -3048979026549104163L;
	private Integer employeeId;
	private Integer actionNoticeTypeId;
	private Date updatedDate;
	private Integer actionNoticeId;
	private Integer statusId;
	private Date actionNoticeDate;
	public static final int SUSPENDED = 1;
	public static final int TERMINATED = 2;
	public static final int CANCELLED_ID = 4;

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getActionNoticeTypeId() {
		return actionNoticeTypeId;
	}

	public void setActionNoticeTypeId(Integer actionNoticeTypeId) {
		this.actionNoticeTypeId = actionNoticeTypeId;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public Date getActionNoticeDate() {
		return actionNoticeDate;
	}

	public void setActionNoticeDate(Date actionNoticeDate) {
		this.actionNoticeDate = actionNoticeDate;
	}

	public Integer getActionNoticeId() {
		return actionNoticeId;
	}

	public void setActionNoticeId(Integer actionNoticeId) {
		this.actionNoticeId = actionNoticeId;
	}

	@Override
	public String toString() {
		return "ActionNoticeDto [employeeId=" + employeeId + ", actionNoticeTypeId=" + actionNoticeTypeId
				+ ", updatedDate=" + updatedDate + ", actionNoticeId=" + actionNoticeId + ", statusId=" + statusId
				+ ", actionNoticeDate=" + actionNoticeDate + "]";
	}
}
