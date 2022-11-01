package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation class for BIR_ATC

 */

@Entity
@Table (name="BIR_ATC")
public class BirAtc extends BaseDomain {
	private String name;
	private String description;
	private boolean active;

	public enum FIELD {
		id, name, description, active
	}
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "BIR_ATC_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}
	
	@Column(name = "NAME", columnDefinition="VARCHAR(20)")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION", columnDefinition="text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		builder.append("BirAtc [name=").append(name).append(", description=").append(description)
				.append(", active=").append(active).append("]");
		return builder.toString();
	}
}
