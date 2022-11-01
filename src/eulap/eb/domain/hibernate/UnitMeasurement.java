package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the unit measurement table

 */
@Entity
@Table (name = "UNIT_MEASUREMENT")
public class UnitMeasurement extends BaseDomain{
	private String name;
	boolean active;

	public enum FIELD {id, name, active};

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column (name = "UNITOFMEASUREMENT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId(){
	    return super.getId();
	}

	/**
	 * Get the name of the measurement
	 * @return The name of the measurement
	 */
	@Column (name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the measurement
	 * @return The name of the measurement
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Verify the status of unit measurement if active
	 * @return True if active, otherwise false
	 */
	@Column (name = "ACTIVE" , columnDefinition = "TINYINT(1)")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the status of unit measurement
	 * @param active Set to true if active, otherwise false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "UnitMeasurement [name=" + name
				+ ", active=" + active + "]";
	}
}
