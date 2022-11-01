package eulap.eb.domain.hibernate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of DIVISION table

 */
@Entity
@Table(name = "DIVISION")
public class Division extends BaseDomain{
	private Integer parentDivisionId;
	private String number;
	private String name;
	private String description;
	private boolean active;
	private Division parentDivision;
	private int serviceLeaseKeyId;
	private Integer ebObjectId;
	private EBObject ebObject;
	private String parentDivisionName;

	/**
	 * Create an instance of division.
	 */
	public static Division getInstanceOf(Integer id, Integer parentDivisionId, Integer ebObjectId, String number, String name, 
			String description, boolean active, int createdBy, Date createdDate, int updatedBy, Date updatedDate, int serviceLeaseKeyId) {
		Division division = new Division();
		division.setId(id);
		division.setParentDivisionId(parentDivisionId);
		division.setEbObjectId(ebObjectId);
		division.setNumber(number);
		division.setName(name);
		division.setDescription(description);
		division.setActive(active);
		division.setCreatedBy(createdBy);
		division.setCreatedDate(createdDate);
		division.setUpdatedBy(updatedBy);
		division.setUpdatedDate(updatedDate);
		division.setServiceLeaseKeyId(serviceLeaseKeyId);
		return division;
	}

	public static final int MAX_NUMBER = 5;
	public static final int OBJECT_TYPE_ID = 101;
	public static final int OR_TYPE_ID = 51;
	public static final int MAX_LEVEL = 2;

	public enum Field {id, parentDivisionId, number, name, description, active, serviceLeaseKeyId, ebObjectId}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "DIVISION_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	/**
	 * Get the parent division id.
	 * @return parentDivisionId The parent division id.
	 */
	@Column(name = "PARENT_DIVISION_ID", columnDefinition = "INT(10)")
	public Integer getParentDivisionId() {
		return parentDivisionId;
	}

	/**
	 * Set the parent division id.
	 * @param parentDivisionId The parent division id.
	 */
	public void setParentDivisionId(Integer parentDivisionId) {
		this.parentDivisionId = parentDivisionId;
	}

	/**
	 * Get the number of the division.
	 * @return The number.
	 */
	@Column(name = "NUMBER", columnDefinition = "VARCHAR(5)")
	public String getNumber() {
		return number;
	}

	/**
	 * Set the number of the division.
	 * @param number The division number.
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Get the name of the division.
	 * @return The division name.
	 */
	@Column(name = "NAME", columnDefinition = "VARCHAR(100)")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the division.
	 * @param name The division name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the description of the division.
	 * @return The description.
	 */
	@Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(200)")
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the division.
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the parent division object.
	 * @return The parent division object.
	 */
	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name = "PARENT_DIVISION_ID", insertable=false, updatable=false, nullable=true)
	public Division getParentDivision() {
		return parentDivision;
	}

	/**
	 * Set the parent division object.
	 * @param parentDivision The parent division object.
	 */
	public void setParentDivision(Division parentDivision) {
		this.parentDivision = parentDivision;
	}

	/**
	 * Verify the status of the division if active.
	 * @return True if active, otherwise false.
	 */
	@Column(name = "ACTIVE", columnDefinition = "TINYINT(1)")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the status of the division.
	 * @param active Set to true if active, otherwise false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the service lease key id.
	 * @return The service lease key id.
	 */
	@Column(name = "EB_SL_KEY_ID", columnDefinition="INT(10)")
	public int getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	/**
	 * Set the service lease key id.
	 * @param serviceLeaseKeyId The service lease key id.
	 */
	public void setServiceLeaseKeyId(int serviceLeaseKeyId) {
		this.serviceLeaseKeyId = serviceLeaseKeyId;
	}

	@Transient
	public String getNumberAndName () {
		Integer number = Integer.valueOf(this.number);
		String formattedNumber = this.number;
		if (number != null){
			NumberFormat formatter = new DecimalFormat("####.##");
			formatter.setMinimumIntegerDigits(5);
			formattedNumber = formatter.format(number);
		}
		return formattedNumber + " - "+name;
	}

	@Column(name = "EB_OBJECT_ID", columnDefinition="INT(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", nullable=true, insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Transient
	public static int getOrTypeId() {
		return OR_TYPE_ID;
	}

	@Transient
	public String getParentDivisionName() {
		return parentDivisionName;
	}

	public void setParentDivisionName(String parentDivisionName) {
		this.parentDivisionName = parentDivisionName;
	}

	@Override
	public String toString() {
		return "Division [parentDivisionId=" + parentDivisionId + ", number=" + number + ", name=" + name
				+ ", description=" + description + ", active=" + active + ", serviceLeaseKeyId=" + serviceLeaseKeyId
				+ ", ebObjectId=" + ebObjectId + ", ebObject=" + ebObject + "]";
	}
}