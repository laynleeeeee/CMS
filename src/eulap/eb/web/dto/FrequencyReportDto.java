package eulap.eb.web.dto;

import java.util.Date;

/**
 * Data transfer object for Frequency Report.

 *
 */
public class FrequencyReportDto {
	private Integer employeeId;
	private String strEmployeeNumber;
	private String name;
	private String position;
	private Date date;
	private Integer frequency;
	private Double minutes;

	/**
	 * Static type id for frequency report
	 */
	public static final int FREQUENCY_TYPE_LATE = 1;
	public static final int FREQUENCY_TYPE_OVERTIME = 2;
	public static final int FREQUENCY_TYPE_UNDERTIME = 3;
	public static final int FREQUENCY_TYPE_ABSENCE = 4;

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public String getStrEmployeeNumber() {
		return strEmployeeNumber;
	}

	public void setStrEmployeeNumber(String strEmployeeNumber) {
		this.strEmployeeNumber = strEmployeeNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Double getMinutes() {
		return minutes;
	}

	public void setMinutes(Double minutes) {
		this.minutes = minutes;
	}

	@Override
	public String toString() {
		return "FrequencyReportDto [employeeId=" + employeeId + ", strEmployeeNumber=" + strEmployeeNumber + ", name="
				+ name + ", position=" + position + ", date=" + date + ", frequency=" + frequency + ", minutes="
				+ minutes + "]";
	}
}
