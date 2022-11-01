package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation class of EMPLOYEE_CIVIL_QUERY Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_CIVIL_QUERY")
public class EmployeeCivilQuery extends BaseDomain{
	private Integer employeeId;
	private boolean administrativeInvestigation;
	private String adminInvesDetail;
	private boolean crimeConvicted;
	private String crimeConvictedDetail;
	private boolean usedProhibitedDrug;

	public static final int MAX_AID = 250;
	public static final int MAX_CCD = 250;

	public enum FIELD {
		id, employeeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_CIVIL_QUERY_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "ADMINISTRATIVE_INVESTIGATION", columnDefinition = "TINYINT(1)")
	public boolean isAdministrativeInvestigation() {
		return administrativeInvestigation;
	}

	public void setAdministrativeInvestigation(boolean administrativeInvestigation) {
		this.administrativeInvestigation = administrativeInvestigation;
	}

	@Column(name = "ADMIN_INVES_DETAIL", columnDefinition = "VARCHAR(250)")
	public String getAdminInvesDetail() {
		return adminInvesDetail;
	}

	public void setAdminInvesDetail(String adminInvesDetail) {
		this.adminInvesDetail = adminInvesDetail;
	}

	@Column(name = "CRIME_CONVICTED", columnDefinition = "TINYINT(1)")
	public boolean isCrimeConvicted() {
		return crimeConvicted;
	}

	public void setCrimeConvicted(boolean crimeConvicted) {
		this.crimeConvicted = crimeConvicted;
	}

	@Column(name = "CRIME_CONVICTED_DETAIL", columnDefinition = "VARCHAR(250)")
	public String getCrimeConvictedDetail() {
		return crimeConvictedDetail;
	}

	public void setCrimeConvictedDetail(String crimeConvictedDetail) {
		this.crimeConvictedDetail = crimeConvictedDetail;
	}

	@Column(name = "USED_PROHIBITED_DRUG", columnDefinition = "TINYINT(1)")
	public boolean isUsedProhibitedDrug() {
		return usedProhibitedDrug;
	}

	public void setUsedProhibitedDrug(boolean usedProhibitedDrug) {
		this.usedProhibitedDrug = usedProhibitedDrug;
	}

	@Override
	public String toString() {
		return "EmployeeCivilQuery [employeeId=" + employeeId + ", administrativeInvestigation="
				+ administrativeInvestigation + ", adminInvesDetail=" + adminInvesDetail + ", crimeConvicted="
				+ crimeConvicted + ", crimeConvictedDetail=" + crimeConvictedDetail + ", usedProhibitedDrug="
				+ usedProhibitedDrug + "]";
	}
}
