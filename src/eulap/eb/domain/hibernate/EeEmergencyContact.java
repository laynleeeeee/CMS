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
 * Object representation class of EMPLOYEE_EMERGENCY_CONTACT table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_EMERGENCY_CONTACT")
public class EeEmergencyContact extends BaseDomain implements OOChild {
	@Expose
	private String name;
	@Expose
	private String address;
	@Expose
	private String contactNo;
	@Expose
	private Integer ebObjectId;
	@Expose
	private Integer referenceObjectId;
	private EBObject ebObject;
	private boolean active;

	/**
	 * Object Type Id for EMPLOYEE_EMERGENCY_CONTACT
	 */
	public static final int OBJECT_TYPE_ID = 85;
	/**
	 * OR type id for EMPLOYEE_EMERGENCY_CONTACT
	 */
	public final static int EE_EMER_CONTACT_OR_TYPE = 31;
	/**
	 * Maximum characters allowed for name field.
	 */
	public static final int MAX_CHAR_NAME = 100;
	/**
	 * Maximum characters allowed for address field.
	 */
	public static final int MAX_CHAR_ADDRESS = 150;
	/**
	 * Maximum characters allowed for contact number field.
	 */
	public static final int MAX_CHAR_CONTACT_NO = 20;

	public enum FIELD {
		id, ebObjectId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "EMPLOYEE_EMERGENCY_CONTACT_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="NAME", columnDefinition="varchar(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="ADDRESS", columnDefinition="varchar(150)")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name="CONTACT_NO", columnDefinition="varchar(20)")
	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
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
		builder.append("EeEmergencyContact [name=").append(name).append(", address=").append(address)
				.append(", contactNo=").append(contactNo).append(", ebObjectId=").append(ebObjectId)
				.append(", referenceObjectId=").append(referenceObjectId).append(", active=").append(active)
				.append("]");
		return builder.toString();
	}
}
