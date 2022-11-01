package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object representation of {@link OvertimeDetail}

 *
 */
@Entity
@Table(name = "OVERTIME_DETAIL")
public class OvertimeDetail extends BaseDomain{
	private Integer employeeRequestId;
	private Date overtimeDate;
	private String startTime;
	private String endTime;
	private Double overtimeHours;
	private String purpose;
	private Double allowableBreak;

	/**
	 * Static value for maximum characters in start time and end time = 5
	 */
	public static final int TIME_MAX_CHAR = 5;
	/**
	 * Static value for maximum amount in overtime hours.
	 */
	public static final double MAX_OT_HOURS = 24;

	public enum FIELD {
		id, employeeRequestId, overtimeDate
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "OVERTIME_DETAIL_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "EMPLOYEE_REQUEST_ID", columnDefinition = "int(10)")
	public Integer getEmployeeRequestId() {
		return employeeRequestId;
	}

	public void setEmployeeRequestId(Integer employeeRequestId) {
		this.employeeRequestId = employeeRequestId;
	}

	@Column(name = "OVERTIME_DATE", columnDefinition = "DATE")
	public Date getOvertimeDate() {
		return overtimeDate;
	}

	public void setOvertimeDate(Date overtimeDate) {
		this.overtimeDate = overtimeDate;
	}

	@Column(name = "START_TIME", columnDefinition = "varchar(5)")
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Column(name = "END_TIME", columnDefinition = "varchar(5)")
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Column(name = "OVERTIME_HOURS", columnDefinition = "double(4,2)")
	public Double getOvertimeHours() {
		return overtimeHours;
	}

	public void setOvertimeHours(Double overtimeHours) {
		this.overtimeHours = overtimeHours;
	}

	@Column(name = "PURPOSE", columnDefinition = "TEXT")
	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	@Override
	public String toString() {
		return "OvertimeDetail [employeeRequestId=" + employeeRequestId + ", overtimeDate=" + overtimeDate
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", overtimeHours=" + overtimeHours
				+ ", purpose=" + purpose + "]";
	}

	@Transient
	public Double getAllowableBreak() {
		return allowableBreak;
	}

	public void setAllowableBreak(Double allowableBreak) {
		this.allowableBreak = allowableBreak;
	}
}
