package eulap.eb.domain.hibernate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the FLEET_MANNING_REQUIREMENT table.

 *
 */
@Entity
@Table(name = "FLEET_MANNING_REQUIREMENT")
public class FleetManningRequirement extends BaseDomain {
	private String position;
	private String license;
	private String number;
	private String remarks;
	private boolean active;
	private Integer ebObjectId;
	private EBObject ebObject;
	private String department;

	public static final String DEPARTMENT_DECK = "Deck";
	public static final String DEPARTMENT_ENGINE = "Engine";
	public static final  List<String> DEPARTMENTS = Arrays.asList(DEPARTMENT_DECK, DEPARTMENT_ENGINE);
	public static final int OBJECT_TYPE_ID = 94;
	public static final int REFERENCE_DOCUMENT_OR_TYPE_ID = 34;
	public static final int MANNING_REQUIREMENT_DECK_OR_TYPE_ID = 35;
	public static final int MANNING_REQUIREMENT_ENGINE_OR_TYPE_ID = 36;
	public static final int MAX_POSITION = 50;
	public static final int MAX_LICENSE = 50;
	public static final int MAX_NUMBER = 20;

	public enum FIELD {
		id, position, license, number, remarks, ebObjectId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "FLEET_MANNING_REQUIREMENT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "POSITION", columnDefinition = "varchar(50)")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Column(name = "LICENSE", columnDefinition = "varchar(50)")
	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	@Column(name = "NUMBER", columnDefinition = "varchar(20)")
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Column(name = "REMARKS", columnDefinition = "text")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", nullable=true, insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Column(name="EB_OBJECT_ID", columnDefinition="int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer eBObjecctId) {
		this.ebObjectId = eBObjecctId;
	}

	@Override
	public String toString() {
		return "FleetManningRequirement [position=" + position + ", license=" + license + ", number=" + number
				+ ", remarks=" + remarks + ", active=" + active + ", getId()=" + getId() + "]";
	}

	@Transient
	public Integer getOrTypeId() {
		return department.equals(DEPARTMENT_DECK) ? MANNING_REQUIREMENT_DECK_OR_TYPE_ID
				: MANNING_REQUIREMENT_ENGINE_OR_TYPE_ID;
	}

	@Transient
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
}
