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
 * A class that represents object relationship type.

 *
 */
@Entity
@Table(name = "OR_TYPE")
public class ORType extends BaseDomain{

	public static final int PARENT_OR_TYPE_ID = 1;
	public static final int RAW_TO_PROCESSED_OR_TYPE_ID = 2;
	public static final int STOCK_ADJUSTMENT_OR_TYPE_ID = 3;

	private String name;
	private boolean active;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "OR_TYPE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column (name = "NAME", columnDefinition="VARCHAR(50)")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Verify the status of the division if active.
	 * @return True if active, otherwise false.
	 */
	@Column(name = "ACTIVE", columnDefinition = "TINYINT(1)")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the status of the division.
	 * @param active Set to true if active, otherwise false.
	 */
	public void setActive(boolean active) {
		this.active = active;
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
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Override
	public String toString() {
		return "ORType [getId()=" + getId() + ", name=" + name + ", active="
				+ active + "]";
	}
}
