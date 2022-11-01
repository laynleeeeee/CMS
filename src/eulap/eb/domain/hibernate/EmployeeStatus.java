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
 * An Object representation of EMPLOYEE_STATUS. 

 */
@Entity
@Table (name="EMPLOYEE_STATUS")
public class EmployeeStatus extends BaseDomain{
	private String name;
	private String description;
	private boolean active;

	public static final String Z = "Z";
	public static final String S_ME = "S/ME";
	public static final String S1_ME1 = "ME1/S1";
	public static final String S2_ME2 = "ME2/S2";
	public static final String S3_ME3 = "ME3/S3";
	public static final String S4_ME4 = "ME4/S4";

	public enum FIELD {id, name, description, active};

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EMPLOYEE_STATUS_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "DESCRIPTION", columnDefinition = "varchar(150)")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeStatus [name=").append(name).append(", description=").append(description)
				.append(", active=").append(active).append("]");
		return builder.toString();
	}
}
