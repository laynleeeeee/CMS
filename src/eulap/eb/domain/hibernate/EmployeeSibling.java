package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;
import eulap.eb.domain.hibernate.CivilStatus;

/**
 * Object representation class of EMPLOYEE_SIBLING Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_SIBLING")
public class EmployeeSibling extends BaseDomain{
	@Expose
	private Integer employeeId;
	@Expose
	private String name;
	@Expose
	private Integer age;
	@Expose
	private Integer civilStatusId;
	@Expose
	private String occupation;
	private CivilStatus civilStatus;

	public static final int MAX_NAME = 100;
	public static final int MAX_OCCUPATION = 100;

	public enum FIELD {
		id, employeeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_SIBLING_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "AGE", columnDefinition = "INT(2)")
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Column(name = "CIVIL_STATUS_ID", columnDefinition = "INT(10)")
	public Integer getCivilStatusId() {
		return civilStatusId;
	}
	public void setCivilStatusId(Integer civilStatusId) {
		this.civilStatusId = civilStatusId;
	}

	@Column(name = "OCCUPATION", columnDefinition = "varchar(100)")
	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	@OneToOne
	@JoinColumn (name="CIVIL_STATUS_ID", insertable=false, updatable=false)
	public CivilStatus getCivilStatus() {
		return civilStatus;
	}
	
	public void setCivilStatus(CivilStatus civilStatus) {
		this.civilStatus = civilStatus;
	}

	@Override
	public String toString() {
		return "EmployeeSibling [employeeId=" + employeeId + ", name=" + name + ", age=" + age + ", civilStatusId="
				+ civilStatusId + ", occupation=" + occupation + "]";
	}
}
