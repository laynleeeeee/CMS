package eulap.eb.web.dto;

/**
 * Container to hold the delivery status.

 *
 */
public class DeliveryStatus {
	public static final int PARTIALLY_SERVED = 1;
	public static final int FULLY_SERVED = 2;
	public static final int UNSERVED = 3;
	public static final int CANCELLED = 4;
	public static final int ALL = -1;

	private int value;
	private String description;

	public static DeliveryStatus getInstanceOf(int value, String description) {
		DeliveryStatus status = new DeliveryStatus();
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
		return "DeliveryStatus [value=" + value + ", description=" + description + "]";
	}
}
