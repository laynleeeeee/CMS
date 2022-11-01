package eulap.eb.service.payroll;

/**
 * handle the enum for employee status id.


 *
 */
public enum WTEmployeeStatus {
	STATUS_Z(1, "Z"), STATUS_S_ME(2, "S/ME"), STATUS_S_ME_1(3, "ME1/S1"), STATUS_S_ME_2(4, "ME2/S2"), STATUS_S_ME_3(5,
			"ME3/S3"), STATUS_S_ME_4(6, "ME4/S4");
	private final int employeStatusId;
	private final String name;

	WTEmployeeStatus(int employeStatusId, String name) {
		this.employeStatusId = employeStatusId;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getEmployeStatusId() {
		return employeStatusId;
	}

	/**
	 * Get the employee status by employee status id.
	 * @param employeStatusId the empployee status id.
	 * @return The withholding tax employee status.
	 */
	public static WTEmployeeStatus getStatus(int employeStatusId) {
		for (WTEmployeeStatus status : values()) {
			if (status.employeStatusId == employeStatusId)
				return status;
		}
		throw new RuntimeException("Unkown employee status id : " + employeStatusId);
	}
}