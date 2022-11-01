package eulap.eb.web.dto;

import java.util.List;

/**
 * Employee log dto.

 *
 */
public class EmployeeLogDto {
	private List<String> logTimes;

	public EmployeeLogDto() {

	}

	public EmployeeLogDto(List<String> logTimes) {
		this.logTimes = logTimes;
	}

	public List<String> getLogTimes() {
		return logTimes;
	}

	public void setLogTimes(List<String> logTimes) {
		this.logTimes = logTimes;
	}

	@Override
	public String toString() {
		return "EmployeeLogDto [logTimes=" + logTimes + "]";
	}
}
