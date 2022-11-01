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
 * Object representation class of ACTION_NOTICE Table.

 *
 */
@Entity
@Table(name = "ACTION_NOTICE")
public class ActionNotice extends BaseDomain{

	private String name;
	private boolean active;

	/**
	 *  Maximum character for Action Notice name field with a value of 25.
	 */
	public static final int MAX_ACTION_NOTICE_NAME = 25;

	public enum FIELD{
		id, name, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="ACTION_NOTICE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "NAME", columnDefinition = "VARCHAR(25)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ActionNotice [name=" + name + ", active=" + active + "]";
	}

	/**
	 * Validate if the action notice is active.
	 * @return True if the action notice is active, otherwise false.
	 */
	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
