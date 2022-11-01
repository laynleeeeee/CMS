package eulap.eb.web.dto;

import java.util.Date;

/**
 * Data transfer object class for setiting max day schedule line details

 */

public class MaxDayPerScheduleLine {
	private Integer dayDiff;
	private Date maxDate;

	public MaxDayPerScheduleLine() {}

	public MaxDayPerScheduleLine(Integer dayDiff, Date maxDate) {
		this.dayDiff = dayDiff;
		this.maxDate = maxDate;
	}

	public Integer getDayDiff() {
		return dayDiff;
	}

	public void setDayDiff(Integer dayDiff) {
		this.dayDiff = dayDiff;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MaxDayPerScheduleLine [dayDiff=").append(dayDiff).append(", maxDate=").append(maxDate)
				.append("]");
		return builder.toString();
	}
}
