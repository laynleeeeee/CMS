package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object for type of leave table

 *
 */
@Entity
@Table(name="TYPE_OF_LEAVE")
public class TypeOfLeave extends BaseDomain{
	private String name;
	private boolean paidLeave;
	private boolean active;
	private String description;

	/**
	 * Maximum character for name field with a value of 25.
	 */
	public static final int MAX_CHAR_NAME=25;

	public enum FIELD {
		id, name, paidLeave, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "TYPE_OF_LEAVE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "int(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "timestamp")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "int(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "timestamp")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name="NAME", columnDefinition="varchar(25)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="PAID_LEAVE", columnDefinition="tinyint(1)")
	public boolean isPaidLeave() {
		return paidLeave;
	}

	public void setPaidLeave(boolean paidLeave) {
		this.paidLeave = paidLeave;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name="DESCRIPTION", columnDefinition="TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "TypeOfLeave [name=" + name + ", paidLeave=" + paidLeave + ", active=" + active + ", description="
				+ description + "]";
	}

}
