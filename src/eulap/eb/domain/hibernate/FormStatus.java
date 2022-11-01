package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of FORM_STATUS
 * 

 *
 */
@Entity
@Table (name="FORM_STATUS")
public class FormStatus extends BaseDomain {
	public static final int CREATED_ID = 1;
	public static final int NOTED_ID = 2;
	public static final int APPROVED_ID = 3;
	public static final int CANCELLED_ID = 4;
	public static final int REQUESTED_ID = 5;
	public static final int POSTED_ID =6;
	public static final int RECEIVED_ID = 7;
	public static final int CHECKED_ID = 8;
	public static final int PREPARED_ID = 9;
	public static final int ISSUED_ID = 10;
	public static final int RELEASED_ID = 11;
	public static final int RETURNED_ID = 12;
	public static final int UNDELIVERED_ID = 13;
	public static final int NEW_ID = 14;
	public static final int NEGOTIABLE_ID = 15;
	public static final int CLEARED_ID = 16;
	public static final int VALIDATED_ID = 17;
	public static final int REMITTED_ID = 18;
	public static final int PAID_ID = 19;
	public static final int PARTIALLY_SERVED = 20;
	public static final int FULLY_SERVED = 21;
	public static final int ENCODED_ID = 22;
	public static final int ASSIGNEDID = 23;
	public static final int FIXED_ID = 25;
	public static final int VERIFIED_ID = 24;
	public static final int CLOSED_ID = 26;
	public static final int REOPEN_ID = 27;
	public static final int ACCEPTED_ID = 28;
	public static final int COMPLETED_ID = 29;
	public static final int REVIEWED_ID = 30;
	public static final int DRAFTED_ID = 31;
	public static final int STALED_ID = 32;

	public static final String CANCELLED_LABEL = "CANCELLED";
	public static final String STALED_LABEL = "STALED";

	private String description;
	private boolean selected;
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "FORM_STATUS_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}
	
	@Column (name="DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FormStatus)) {
			return false;
		}
		FormStatus compareTo = (FormStatus) obj;
		return this.getId() == compareTo.getId() &&
				this.description.equals(compareTo.description);
	}

	@Override
	public int hashCode() {
		return description.hashCode() + Integer.valueOf(getId()).hashCode();
	}

	@Override
	public String toString() {
		return "FormStatus [ID=" + getId() + ", description="
				+ description + "]";
	}
}
