package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation class for REPACKING_TYPE table

 */

@Entity
@Table(name = "REPACKING_TYPE")
public class RepackingType extends BaseDomain {
	private String name;
	private boolean active;

	public enum FIELD {
		id, name, active
	}

	public static final int TYPE_REPACKING = 1;
	public static final int TYPE_ITEM_CONVERSION = 2;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "REPACKING_TYPE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "NAME", columnDefinition="varchar(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RepackingType [name=").append(name).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
