package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation class of EMPLOYEE_FAMILY Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_FAMILY")
public class EmployeeFamily extends BaseDomain{
	private Integer employeeId;
	private String fatherName;
	private String motherName;
	private String spouseName;
	private String fatherOccupation;
	private String motherOccupation;
	private String spouseOccupation;

	public static final int MAX_FATHER_NAME = 100;
	public static final int MAX_MOTHER_NAME = 100;
	public static final int MAX_SPOUSE_NAME = 100;
	public static final int MAX_FATHER_OCCUPATION = 100;
	public static final int MAX_MOTHER_OCCUPATION = 100;
	public static final int MAX_SPOUSE_OCCUPATION = 100;

	public enum FIELD {
		id, employeeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_FAMILY_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "FATHER_NAME", columnDefinition = "varchar(100)")
	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	@Column(name = "MOTHER_NAME", columnDefinition = "varchar(100)")
	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	@Column(name = "SPOUSE_NAME", columnDefinition = "varchar(100)")
	public String getSpouseName() {
		return spouseName;
	}

	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}

	@Column(name = "FATHER_OCCUPATION", columnDefinition = "varchar(100)")
	public String getFatherOccupation() {
		return fatherOccupation;
	}

	public void setFatherOccupation(String fatherOccupation) {
		this.fatherOccupation = fatherOccupation;
	}

	@Column(name = "MOTHER_OCCUPATION", columnDefinition = "varchar(100)")
	public String getMotherOccupation() {
		return motherOccupation;
	}

	public void setMotherOccupation(String motherOccupation) {
		this.motherOccupation = motherOccupation;
	}

	@Column(name = "SPOUSE_OCCUPATION", columnDefinition = "varchar(100)")
	public String getSpouseOccupation() {
		return spouseOccupation;
	}

	public void setSpouseOccupation(String spouseOccupation) {
		this.spouseOccupation = spouseOccupation;
	}

	@Override
	public String toString() {
		return "EmployeeFamily [employeeId=" + employeeId + ", fatherName=" + fatherName + ", motherName=" + motherName
				+ ", spouseName=" + spouseName + ", fatherOccupation=" + fatherOccupation + ", motherOccupation="
				+ motherOccupation + ", spouseOccupation=" + spouseOccupation + "]";
	}
}
