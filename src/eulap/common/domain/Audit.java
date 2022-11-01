package eulap.common.domain;

import java.util.Date;

/**
 * Holder of audit information of the domain object

 *
 */
public class Audit {
	private final int userId;
	private final boolean isNew;
	private final Date date;

	public Audit (int userId, boolean isNew, Date date) {
		this.userId = userId;
		this.isNew = isNew;
		this.date = date;
		
	}

	/**
	 * Get the instance of this audit.
	 * @param userId The user id.
	 * @param isNew true for new record, otherwise false.
	 * @param date The current date.
	 * @return The audit object.
	 */
	public static Audit getInstace (int userId, boolean isNew, Date date){ 
		return new Audit(userId, isNew, date);
	}
	
	/**
	 * Get the user id.
	 * @return The user id.
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Check if this is a new record
	 * @return true if new record, otherwise false.
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * Get the date create/updated.
	 * @return The date created/updated
	 */
	public Date getDate() {
		return date;
	}
}
