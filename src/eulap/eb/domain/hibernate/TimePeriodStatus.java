package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of TIME_PERIOD_STATUS table

 */
@Entity
@Table(name = "TIME_PERIOD_STATUS")
public class TimePeriodStatus extends BaseDomain{
	public static final int NEVER_OPENED = 1;
	public static final int OPEN = 2;
	public static final int CLOSED = 3;
	private String name;

	public enum Field {id, name}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "TIME_PERIOD_STATUS_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the name of the status {Open, Closed, Never Opened}.
	 * @return The name.
	 */
	@Column(name = "NAME", columnDefinition = "VARCHAR(15)")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the status {Open, Closed, Never Opened}.
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}
}