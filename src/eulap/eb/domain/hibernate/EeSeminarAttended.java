package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;
import eulap.eb.service.oo.OOChild;

/**
 * Object representation class of EMPLOYEE_SEMINAR_ATTENDED table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_SEMINAR_ATTENDED")
public class EeSeminarAttended extends BaseDomain implements OOChild {
	@Expose
	private String courseTitle;
	@Expose
	private String companyAgency;
	@Expose
	private String seminarDate;
	@Expose
	private Integer ebObjectId;
	@Expose
	private Integer referenceObjectId;
	private EBObject ebObject;
	private boolean active;

	/**
	 * Object Type Id for EMPLOYEE_SEMINAR_ATTENDED
	 */
	public static final int OBJECT_TYPE_ID = 87;
	/**
	 * OR type id for EMPLOYEE_SEMINAR_ATTENDED
	 */
	public final static int EE_SEMINAR_OR_TYPE = 30;
	public static final int MAX_CHAR_TILE = 150;
	public static final int MAX_CHAR_COMPANY = 150;
	public static final int MAX_CHAR_DATE = 50;

	public enum FIELD {
		id, ebObjectId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "EMPLOYEE_SEMINAR_ATTENDED_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Transient
	@JoinColumn (name="EB_OBJECT_ID", columnDefinition="int(10)", insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Column(name="EB_OBJECT_ID", columnDefinition="int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	@Override
	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}

	@Column(name="COURSE_TITLE", columnDefinition="varchar(150)")
	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	@Column(name="COMPANY_AGENCY", columnDefinition="varchar(150)")
	public String getCompanyAgency() {
		return companyAgency;
	}

	public void setCompanyAgency(String companyAgency) {
		this.companyAgency = companyAgency;
	}

	@Column(name="INCLUSIVE_DATE", columnDefinition="varchar(50)")
	public String getSeminarDate() {
		return seminarDate;
	}

	public void setSeminarDate(String seminarDate) {
		this.seminarDate = seminarDate;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EeSeminarAttended [courseTitle=").append(courseTitle).append(", companyAgency=")
				.append(companyAgency).append(", seminarDate=").append(seminarDate).append(", ebObjectId=")
				.append(ebObjectId).append(", referenceObjectId=").append(referenceObjectId).append(", active=")
				.append(active).append("]");
		return builder.toString();
	}
}
