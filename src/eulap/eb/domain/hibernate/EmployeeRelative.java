package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;

/**
 * Object representation class of EMPLOYEE_RELATIVE Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_RELATIVE")
public class EmployeeRelative extends BaseDomain{
	@Expose
	private Integer employeeId;
	@Expose
	private String name;
	@Expose
	private String position;
	@Expose
	private String relationship;

	public static final int MAX_NAME = 100;
	public static final int MAX_POSITION = 50;
	public static final int MAX_RELATIONSHIP = 50;

	public enum FIELD {
		id, employeeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_RELATIVE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "EMPLOYEE_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name = "NAME", columnDefinition = "varchar(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "POSITION", columnDefinition = "varchar(50)")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Column(name = "RELATIONSHIP", columnDefinition = "varchar(50)")
	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	@Override
	public String toString() {
		return "EmployeeRelative [employeeId=" + employeeId + ", name=" + name + ", position=" + position
				+ ", relationship=" + relationship + "]";
	}
}
