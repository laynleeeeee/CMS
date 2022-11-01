package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation class of EMPLOYEE_EMPLOYEMENT_QUERY Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_EMPLOYEMENT_QUERY")
public class EmployeeEmploymentQuery extends BaseDomain{
	private Integer employeeId;
	private boolean prevEmployed;
	private String separationReason;
	private boolean diagnosedWithDisease;
	private String diseaseDetail;
	private String identifyingMark;
	private String emergencyContact;

	public static final int MAX_SEPARATION_REASON = 250;
	public static final int MAX_DISEASE_DETAIL = 250;
	public static final int MAX_IDENTIFYING_MARK = 250;
	public static final int MAX_EMERGENCY_CONTACT = 50;

	public enum FIELD {
		id, employeeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_EMPLOYEMENT_QUERY_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "PREV_EMPLOYED", columnDefinition = "TINYINT(1)")
	public boolean isPrevEmployed() {
		return prevEmployed;
	}

	public void setPrevEmployed(boolean prevEmployed) {
		this.prevEmployed = prevEmployed;
	}

	@Column(name = "SEPARATION_REASON", columnDefinition = "VARCHAR(250)")
	public String getSeparationReason() {
		return separationReason;
	}

	public void setSeparationReason(String separationReason) {
		this.separationReason = separationReason;
	}

	@Column(name = "DIAGNOSED_WITH_DISEASE", columnDefinition = "TINYINT(1)")
	public boolean isDiagnosedWithDisease() {
		return diagnosedWithDisease;
	}

	public void setDiagnosedWithDisease(boolean diagnosedWithDisease) {
		this.diagnosedWithDisease = diagnosedWithDisease;
	}

	@Column(name = "DISEASE_DETAIL", columnDefinition = "VARCHAR(250)")
	public String getDiseaseDetail() {
		return diseaseDetail;
	}

	public void setDiseaseDetail(String diseaseDetail) {
		this.diseaseDetail = diseaseDetail;
	}

	@Column(name = "IDENTIFYING_MARK", columnDefinition = "VARCHAR(250)")
	public String getIdentifyingMark() {
		return identifyingMark;
	}

	public void setIdentifyingMark(String identifyingMark) {
		this.identifyingMark = identifyingMark;
	}

	@Column(name = "EMERGENCY_CONTACT", columnDefinition = "VARCHAR(25)")
	public String getEmergencyContact() {
		return emergencyContact;
	}

	public void setEmergencyContact(String emergencyContact) {
		this.emergencyContact = emergencyContact;
	}

	@Override
	public String toString() {
		return "EmployeeEmploymentQuery [employeeId=" + employeeId + ", prevEmployed=" + prevEmployed
				+ ", separationReason=" + separationReason + ", diagnosedWithDisease=" + diagnosedWithDisease
				+ ", diseaseDetail=" + diseaseDetail + ", identifyingMark=" + identifyingMark + ", emergencyContact="
				+ emergencyContact + "]";
	}
}
	
