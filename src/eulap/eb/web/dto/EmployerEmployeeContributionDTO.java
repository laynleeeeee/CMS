package eulap.eb.web.dto;

/**
 * Data Access Object for Employer-Employee Contribution Report

 *
 */
public class EmployerEmployeeContributionDTO {
	private int employeeId;
	private String employeeName;
	private double sssEe;
	private double sssEr;
	private double sssEc;
	private double philHealthEe;
	private double philHealthEr;
	private double pagibigEe;
	private double pagibigEr;

	public static EmployerEmployeeContributionDTO getInstanceOf(int employeeId, String employeeName, double sssEe,
		double sssEr, double sssEc, double philHealthEe, double philHealthEr, double pagibigEe,
		double pagibigEr) {
		EmployerEmployeeContributionDTO dto = new EmployerEmployeeContributionDTO();
		dto.employeeId = employeeId;
		dto.employeeName = employeeName;
		dto.sssEe = sssEe;
		dto.sssEr = sssEr;
		dto.sssEc = sssEc;
		dto.philHealthEe = philHealthEe;
		dto.philHealthEr = philHealthEr;
		dto.pagibigEe = pagibigEe;
		dto.pagibigEr = pagibigEr;
		return dto;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public double getSssEe() {
		return sssEe;
	}

	public void setSssEe(double sssEe) {
		this.sssEe = sssEe;
	}

	public double getSssEr() {
		return sssEr;
	}

	public void setSssEr(double sssEr) {
		this.sssEr = sssEr;
	}

	public double getSssEc() {
		return sssEc;
	}

	public void setSssEc(double sssEc) {
		this.sssEc = sssEc;
	}

	public double getPhilHealthEe() {
		return philHealthEe;
	}

	public void setPhilHealthEe(double philHealthEe) {
		this.philHealthEe = philHealthEe;
	}

	public double getPhilHealthEr() {
		return philHealthEr;
	}

	public void setPhilHealthEr(double philHealthEr) {
		this.philHealthEr = philHealthEr;
	}

	public double getPagibigEe() {
		return pagibigEe;
	}

	public void setPagibigEe(double pagibigEe) {
		this.pagibigEe = pagibigEe;
	}

	public double getPagibigEr() {
		return pagibigEr;
	}

	public void setPagibigEr(double pagibigEr) {
		this.pagibigEr = pagibigEr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeEmployerContributionDTO [employeeName=")
				.append(employeeName).append(", sssEe=").append(sssEe)
				.append(", sssEr=").append(sssEr).append(", sssEc=")
				.append(sssEc).append(", philHealthEe=").append(philHealthEe)
				.append(", philHealthEr=").append(philHealthEr)
				.append(", pagibigEe=").append(pagibigEe)
				.append(", pagibigEr=").append(pagibigEr).append("]");
		return builder.toString();
	}
}
