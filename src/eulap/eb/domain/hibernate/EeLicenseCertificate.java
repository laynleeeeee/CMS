package eulap.eb.domain.hibernate;

import java.util.Date;

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
 * Object representation class of EMPLOYEE_LICENSE_CERTIFICATE table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_LICENSE_CERTIFICATE")
public class EeLicenseCertificate extends BaseDomain implements OOChild {
	@Expose
	private String accreditationType;
	@Expose
	private Date dateIssued;
	@Expose
	private String rating;
	@Expose
	private Integer ebObjectId;
	@Expose
	private Integer referenceObjectId;
	private EBObject ebObject;
	private boolean active;

	/**
	 * Object Type Id for EMPLOYEE_LICENSE_CERTIFICATE
	 */
	public static final int OBJECT_TYPE_ID = 86;
	/**
	 * OR type id for EMPLOYEE_LICENSE_CERTIFICATE
	 */
	public final static int EE_LIC_CERT_OR_TYPE = 29;
	public static final int MAX_CHAR_TYPE = 100;
	public static final int MAX_CHAR_RATING = 5;

	public enum FIELD {
		id, ebObjectId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "EMPLOYEE_LICENSE_CERTIFICATE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="ACCREDITATION_TYPE", columnDefinition="varchar(10)")
	public String getAccreditationType() {
		return accreditationType;
	}

	public void setAccreditationType(String accreditationType) {
		this.accreditationType = accreditationType;
	}

	@Column(name="DATE_ISSUED", columnDefinition="date")
	public Date getDateIssued() {
		return dateIssued;
	}

	public void setDateIssued(Date dateIssued) {
		this.dateIssued = dateIssued;
	}

	@Column(name="RATING", columnDefinition="varchar(10)")
	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
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
		builder.append("EeLicenseCertificate [accreditationType=").append(accreditationType).append(", dateIssued=")
				.append(dateIssued).append(", rating=").append(rating).append(", ebObjectId=").append(ebObjectId)
				.append(", referenceObjectId=").append(referenceObjectId).append(", active=").append(active)
				.append("]");
		return builder.toString();
	}
}
