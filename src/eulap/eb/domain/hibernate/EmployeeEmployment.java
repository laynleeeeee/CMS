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
 * Object representation class of EMPLOYEE_EMPLOYMENT Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_EMPLOYMENT")
public class EmployeeEmployment extends BaseDomain{
	@Expose
	private Integer employeeId;
	@Expose
	private String companyName;
	@Expose
	private Integer year;
	@Expose
	private String position;
	@Expose
	private String separationReason;

	public static final int MAX_COMPANY_NAME = 100;
	public static final int MAX_POSITION = 50;
	public static final int MAX_SEPARATION_REASON = 150;
	public static final int MAX_YEAR = 4;
	public enum FIELD {
		id, employeeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_EMPLOYMENT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_NAME", columnDefinition = "varchar(100)")
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Column(name = "YEAR", columnDefinition = "INT(4)")
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Column(name = "POSITION", columnDefinition = "varchar(50)")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Column(name = "SEPARATION_REASON", columnDefinition = "varchar(150)")
	public String getSeparationReason() {
		return separationReason;
	}

	public void setSeparationReason(String separationReason) {
		this.separationReason = separationReason;
	}

	@Override
	public String toString() {
		return "EmployeeEmployment [employeeId=" + employeeId + ", companyName=" + companyName + ", year=" + year
				+ ", position=" + position + ", separationReason=" + separationReason + "]";
	}
}
