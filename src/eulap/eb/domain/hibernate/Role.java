package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import eulap.common.domain.BaseDomain;

/**
 * Object that represents ROLE table in the database.

 */
@Entity
@Table(name = "ROLE")
public class Role extends BaseDomain{

	private String name;
	private boolean active;

	public enum Field {id, name, active}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ROLE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the name of the role.
	 * @return The name.
	 */
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the role.
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Validate if the role is active.
	 * @return True if the role is active, otherwise false.
	 */
	@Column(name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the role status.
	 * @param active Set to true if the role is active, otherwise false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Role [name=" + name + ", active=" + active 
				+ ", getId()=" + getId() +"]";
	}
}
