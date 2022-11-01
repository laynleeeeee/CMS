package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object representation for {@link LeaveDetail}

 *
 */

@Entity
@Table(name = "LEAVE_DETAIL")
public class LeaveDetail extends BaseDomain {
	private Integer employeeRequestId;
	private Integer typeOfLeaveId;
	private Date dateFrom;
	private Date dateTo;
	private double leaveDays;
	private Double leaveBalance;
	private String remarks;
	private TypeOfLeave typeOfLeave;
	private boolean paid;
	private boolean halfDay;
	private int period;

	public static final int LEAVE_PERIOD_AM = 1;
	public static final int LEAVE_PERIOD_PM = 2;

	public enum FIELD {
		id, employeeRequestId, typeOfLeaveId, dateFrom, dateTo, halfDay, period
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "LEAVE_DETAIL_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "EMPLOYEE_REQUEST_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeRequestId() {
		return employeeRequestId;
	}

	public void setEmployeeRequestId(Integer employeeRequestId) {
		this.employeeRequestId = employeeRequestId;
	}

	@Column(name = "TYPE_OF_LEAVE_ID", columnDefinition = "INT(10)")
	public Integer getTypeOfLeaveId() {
		return typeOfLeaveId;
	}

	public void setTypeOfLeaveId(Integer typeOfLeaveId) {
		this.typeOfLeaveId = typeOfLeaveId;
	}

	@Column(name = "DATE_FROM", columnDefinition = "DATE")
	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	@Column(name = "DATE_TO", columnDefinition = "DATE")
	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	@Column(name = "LEAVE_DAYS", columnDefinition = "DOUBLE(4,2)")
	public double getLeaveDays() {
		return leaveDays;
	}

	public void setLeaveDays(double leaveDays) {
		this.leaveDays = leaveDays;
	}

	@Column(name = "REMARKS", columnDefinition = "TEXT")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@OneToOne
	@JoinColumn(name = "TYPE_OF_LEAVE_ID", columnDefinition = "int(10)", insertable = false, updatable = false)
	public TypeOfLeave getTypeOfLeave() {
		return typeOfLeave;
	}

	public void setTypeOfLeave(TypeOfLeave typeOfLeave) {
		this.typeOfLeave = typeOfLeave;
	}

	@Transient
	public Double getLeaveBalance() {
		return leaveBalance;
	}

	public void setLeaveBalance(Double leaveBalance) {
		this.leaveBalance = leaveBalance;
	}

	@Transient
	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	@Column(name = "HALF_DAY", columnDefinition = "TINYINT(1)")
	public boolean isHalfDay() {
		return halfDay;
	}

	public void setHalfDay(boolean halfDay) {
		this.halfDay = halfDay;
	}

	@Column(name = "PERIOD", columnDefinition = "INT(11)")
	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LeaveDetail [employeeRequestId=").append(employeeRequestId).append(", typeOfLeaveId=")
				.append(typeOfLeaveId).append(", dateFrom=").append(dateFrom).append(", dateTo=").append(dateTo)
				.append(", leaveDays=").append(leaveDays).append(", leaveBalance=").append(leaveBalance)
				.append(", remarks=").append(remarks).append(", paid=").append(paid).append("]");
		return builder.toString();
	}
}
