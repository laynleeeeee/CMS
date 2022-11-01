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
 * Object representation class of EMPLOYEE_NATIONAL_COMPETENCIES table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_NATIONAL_COMPETENCY")
public class EeNationalCompetency extends BaseDomain implements OOChild {
	@Expose
	private String description;
	@Expose
	private Integer ebObjectId;
	@Expose
	private Integer referenceObjectId;
	private EBObject ebObject;
	private boolean active;

	/**
	 * Object Type Id for EMPLOYEE_NATIONAL_COMPETENCY
	 */
	public static final int OBJECT_TYPE_ID = 113;
	/**
	 * OR type id for EMPLOYEE_NATIONAL_COMPETENCY
	 */
	public final static int EE_NAT_COMP_OR_TYPE = 65;
	public static final int MAX_CHAR_TYPE = 100;

	public enum FIELD {
		id, ebObjectId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "EMPLOYEE_NATIONAL_COMPETENCY_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="DESCRIPTION", columnDefinition="varchar(100)")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		builder.append("EeNationalCompetencies [description=").append(description).append(", ebObjectId=")
				.append(ebObjectId).append(", referenceObjectId=").append(referenceObjectId).append(", active=")
				.append(active).append("]");
		return builder.toString();
	}
}
