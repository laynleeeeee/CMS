package eulap.eb.web.dto;

/**
 * Container to hold the payment status.

 *
 */
public class PaymentStatus {
	public static final int FULLY_PAID = 1;
	public static final int PARTIALL_PAID = 2;
	public static final int UNPAID = 3;
	public static final int CANCELLED = 4;
	public static final int ALL = -1;

	private int value;
	private String description;

	public static PaymentStatus getInstanceOf(int value, String description) {
		PaymentStatus status = new PaymentStatus();
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
		return "PaymentStatus [value=" + value + ", description=" + description + "]";
	}
}
