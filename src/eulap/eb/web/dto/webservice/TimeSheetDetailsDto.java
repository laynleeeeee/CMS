package eulap.eb.web.dto.webservice;


import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;
/**
 * Time sheet details dto.

 */

public class TimeSheetDetailsDto implements Serializable {
	private static final long serialVersionUID = 5763178161683019754L;
	@Expose
	private Date date;
	@Expose
	private double hoursWork;
	@Expose
	private double adjustment;
	@Expose
	private double late;
	@Expose
	private double undertime;
	@Expose
	private double overtime;
	@Expose
	private double timeReduce;
	@Expose
	private double status;
	@Expose
	private String statusLabel;
	@Expose
	private double daysWorked;
	@Expose
	private double dailyWorkingHours;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getHoursWork() {
		return hoursWork;
	}

	public void setHoursWork(double hoursWork) {
		this.hoursWork = hoursWork;
	}

	public double getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(double adjustment) {
		this.adjustment = adjustment;
	}

	public double getLate() {
		return late;
	}

	public void setLate(double late) {
		this.late = late;
	}

	public double getUndertime() {
		return undertime;
	}

	public void setUndertime(double undertime) {
		this.undertime = undertime;
	}

	public double getOvertime() {
		return overtime;
	}

	public void setOvertime(double overtime) {
		this.overtime = overtime;
	}

	public double getStatus() {
		return status;
	}

	public void setStatus(double status) {
		this.status = status;
	}

	public String getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}

	public double getDaysWorked() {
		return daysWorked;
	}

	public void setDaysWorked(double daysWorked) {
		this.daysWorked = daysWorked;
	}

	public double getDailyWorkingHours() {
		return dailyWorkingHours;
	}

	public void setDailyWorkingHours(double dailyWorkingHours) {
		this.dailyWorkingHours = dailyWorkingHours;
	}

	public double getTimeReduce() {
		return timeReduce;
	}

	public void setTimeReduce(double timeReduce) {
		this.timeReduce = timeReduce;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeSheetDetailsDto [date=").append(date).append(", hoursWork=").append(hoursWork)
				.append(", adjustment=").append(adjustment).append(", late=").append(late).append(", undertime=")
				.append(undertime).append(", overtime=").append(overtime).append(", status=").append(status)
				.append(", statusLabel=").append(statusLabel).append(", daysWorked=").append(daysWorked)
				.append(", dailyWorkingHours=").append(dailyWorkingHours).append(", timeReduce=").append(timeReduce).append("]");
		return builder.toString();
	}
}
