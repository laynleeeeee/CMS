package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;
import eulap.common.util.DateUtil;

/**
 * Object representation class of EMPLOYEE_CHILDREN Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_CHILDREN")
public class EmployeeChildren extends BaseDomain{
	@Expose
	private Integer employeeId;
	@Expose
	private String name;
	@Expose
	private Date birthDate;

	public static final int MAX_NAME = 100;

	public enum FIELD {
		id, employeeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_CHILDREN_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "BIRTH_DATE", columnDefinition = "date")
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Transient
	public int getAge() {
		if (birthDate != null) {
			return DateUtil.computeAge(birthDate);
		}
		return 0;
	}

	@Override
	public String toString() {
		return "EmployeeChildren [employeeId=" + employeeId + ", name=" + name + ", birthDate=" + birthDate + "]";
	}
}
