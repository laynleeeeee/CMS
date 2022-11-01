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
 * Object representation for CUSTOMER_TYPE table

 *
 */

@Entity
@Table(name="CUSTOMER_TYPE")
public class CustomerType extends BaseDomain{
	private String name;
	private String description;
	private boolean active;

	public static final int MAX_CHARACTER_NAME = 50;
	public static final int MAX_CHARACTER_DESCRIPTION = 200;

	public enum FIELD {
		id, name, description, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CUSTOMER_TYPE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "NAME", columnDefinition = "varchar(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION", columnDefinition = "varchar(200)")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("CustomerType [").append("name=").append(name)
		.append(", description=").append(description)
		.append(", active=").append(active).append("]");
		return str.toString();
	}
}
