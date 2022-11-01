package eulap.eb.web.dto;

/**
 * Container that will hold the applied status.

 */
public class AppliedStatus {
	public static final int UNAPPLIED = 1;
	public static final int PARTIALLY_APPLIED = 2;
	public static final int FULLY_APPLIED = 3;

	private int value;
	private String description;

	public static AppliedStatus getInstaceOf(int value, String description) {
		AppliedStatus status = new AppliedStatus();
		status.value = value;
		status.description = description;
		return status;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "AppliedStatus [value=" + value + ", description=" + description
				+ "]";
	}
}
