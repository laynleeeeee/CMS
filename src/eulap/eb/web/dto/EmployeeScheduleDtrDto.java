package eulap.eb.web.dto;

import java.util.List;

/**
 * Employee schedule dtr dto.

 *
 */
public class EmployeeScheduleDtrDto {
	private String scheduleName;
	private List<String> headerNames;
	private List<EmployeeLogDto> employeeLogDtos;
	private int maxRow;

	public EmployeeScheduleDtrDto() {

	}

	public EmployeeScheduleDtrDto(String scheduleName, List<String> headerNames, List<EmployeeLogDto> employeeLogDtos, int maxRow) {
		this.scheduleName = scheduleName;
		this.headerNames = headerNames;
		this.employeeLogDtos = employeeLogDtos;
		this.maxRow = maxRow;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public List<String> getHeaderNames() {
		return headerNames;
	}

	public void setHeaderNames(List<String> headerNames) {
		this.headerNames = headerNames;
	}

	public List<EmployeeLogDto> getEmployeeLogDtos() {
		return employeeLogDtos;
	}

	public void setEmployeeLogDtos(List<EmployeeLogDto> employeeLogDtos) {
		this.employeeLogDtos = employeeLogDtos;
	}

	public int getMaxRow() {
		return maxRow;
	}

	public void setMaxRow(int maxRow) {
		this.maxRow = maxRow;
	}

	@Override
	public String toString() {
		return "EmployeeScheduleDtrDto [scheduleName=" + scheduleName + ", headerNames=" + headerNames
				+ ", employeeLogDtos=" + employeeLogDtos + ", maxRow=" + maxRow + "]";
	}
}
