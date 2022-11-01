package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;

/**
 * Object representation class of FLEET_INCIDENT Table.

 *
 */
@Entity
@Table(name="FLEET_INCIDENT")
public class FleetIncident extends BaseDomain{

	private Integer refObjectId;
	@Expose
	private Integer ebObjectId;
	private EBObject ebObject;
	@Expose
	private Date date;
	@Expose
	private String employeeName;
	@Expose
	private String location;
	@Expose
	private String cause;
	@Expose
	private String insuranceClaims;
	@Expose
	private String remarks;
	@Expose
	private Boolean active;

	/**
	 * Object Type Id of Fleet Incident = 90.
	 */
	public static final int OBJECT_TYPE_ID = 90;

	/**
	 * Or Type Id of Fleet Incident = 25.
	 */
	public static final int OR_TYPE_REF_DOC = 25;

	/**
	 * Max character for Fleet Incident fields = 100.
	 */
	public static final int MAX_CHAR = 100;

	public enum FIELD {
		id, refObjectId, ebObjectId, date, employeeName, location,
		cause, insuranceClaims, remarks, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "FLEET_INCIDENT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Transient
	public Integer getRefObjectId() {
		return refObjectId;
	}

	public void setRefObjectId(Integer refObjectId) {
		this.refObjectId = refObjectId;
	}

	@Column(name = "EB_OBJECT_ID", columnDefinition = "INT(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", columnDefinition="INT(10)", insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Column(name="DATE", columnDefinition="DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name="EMPLOYEE_NAME", columnDefinition="VARCHAR(100)")
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Column(name="LOCATION", columnDefinition="VARCHAR(100)")
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Column(name="CAUSE", columnDefinition="VARCHAR(100)")
	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	@Column(name="INSURANCE_CLAIMS", columnDefinition="VARCHAR(100)")
	public String getInsuranceClaims() {
		return insuranceClaims;
	}

	public void setInsuranceClaims(String insuranceClaims) {
		this.insuranceClaims = insuranceClaims;
	}

	@Column(name="REMARKS", columnDefinition="TEXT")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name="ACTIVE", columnDefinition="TINYINT(1)")
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition="INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition="TIMESTAMP")
	public Date getCreatedDate(){
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition="INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition="TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetIncident [refObjectId=").append(refObjectId).append(", ebObjectId=").append(ebObjectId)
				.append(", ebObject=").append(ebObject).append(", date=").append(date).append(", employeeName=")
				.append(employeeName).append(", location=").append(location).append(", cause=").append(cause)
				.append(", insuranceClaims=").append(insuranceClaims).append(", remarks=").append(remarks)
				.append(", active=").append(active).append("]");
		return builder.toString();
	}
}
