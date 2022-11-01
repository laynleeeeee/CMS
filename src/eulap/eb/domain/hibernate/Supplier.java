package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;
import eulap.common.util.NumberFormatUtil;

/**
 * An class representation of SUPPLIER in CBS Database.

 */
@Entity
@Table(name = "SUPPLIER")
public class Supplier extends BaseDomain {
	public static final int BUS_REG_NON_VAT = 1;
	public static final int BUS_REG_VAT_IN = 2;
	public static final int BUS_REG_VAT_EX = 3;
	private int serviceLeaseKeyId;
	@Expose
	private String name;
	@Expose
	private String address;
	@Expose
	private String contactPerson;
	@Expose
	private String contactNumber;
	@Expose
	private boolean active;
	@Expose
	private String tin;
	@Expose
	private Integer busRegTypeId;
	private BusinessRegistrationType busRegType;
	private Integer bussinessClassificationId;
	private BusinessClassification businessClassification;
	private String firstName;
	private String lastName;
	private String middleName;
	private String streetBrgy;
	private String cityProvince;

	public enum FIELD{
		id, serviceLeaseKeyId, name, address, contactPerson, contactNumber, active, tin, busRegTypeId, bussinessClassificationId,
		firstName, lastName, middleName, streetBrgy, cityProvince
	}

	public static final int NAME_MAX_CHAR = 100;
	public static final int ADDRESS_MAX_CHAR = 150;
	public static final int FN_LN_MN_MAX_CHAR = 50;
	public static final int MAX_TIN = 13;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SUPPLIER_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "EB_SL_KEY_ID", columnDefinition = "INT(10)")
	public int getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(int serviceLeaseKey) {
		this.serviceLeaseKeyId = serviceLeaseKey;
	}

	@Column(name = "NAME", columnDefinition = "VARCHAR(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ADDRESS", columnDefinition = "VARCHAR(150)")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "CONTACT_PERSON", columnDefinition = "VARCHAR(50)")
	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	@Column(name = "CONTACT_NUMBER", columnDefinition = "VARCHAR(20)")
	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the TIN of the Supplier
	 */
	@Column(name="TIN", columnDefinition="varchar(20)")
	public String getTin() {
		return tin;
	}

	/**
	 * Set the TIN of the Supplier
	 */
	public void setTin(String tin) {
		this.tin = tin;
	}

	/**
	 * Get the business registration type id.
	 * @return Business registration type id.
	 */
	@Column(name = "BUS_REG_TYPE_ID", columnDefinition = "INT(10)")
	public Integer getBusRegTypeId() {
		return busRegTypeId;
	}

	/**
	 * Set the business registration type id.
	 * @param busRegTypeId The business registration type id.
	 */
	public void setBusRegTypeId(Integer busRegTypeId) {
		this.busRegTypeId = busRegTypeId;
	}

	/**
	 * Get the associated business registration type.
	 * @return business registration type.
	 */
	@ManyToOne
	@JoinColumn (name = "BUS_REG_TYPE_ID", insertable=false, updatable=false)
	public BusinessRegistrationType getBusRegType() {
		return busRegType;
	}

	/**
	 * Set the associated business registration type.
	 * @param busRegType The business registration type.
	 */
	public void setBusRegType(BusinessRegistrationType busRegType) {
		this.busRegType = busRegType;
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
	 * Get the formatted number and name of the supplier
	 */
	@Transient
	public String getNumberAndName () {
		return NumberFormatUtil.formatTo10Digit(getId()) + " - " + name;
	}

	@Column(name = "BUSINESS_CLASSIFICATION_ID", columnDefinition = "INT(10)")
	public Integer getBussinessClassificationId() {
		return bussinessClassificationId;
	}

	public void setBussinessClassificationId(Integer bussinessClassificationId) {
		this.bussinessClassificationId = bussinessClassificationId;
	}

	@OneToOne
	@JoinColumn(name = "BUSINESS_CLASSIFICATION_ID", updatable = false, insertable = false)
	public BusinessClassification getBusinessClassification() {
		return businessClassification;
	}

	public void setBusinessClassification(BusinessClassification businessClassification) {
		this.businessClassification = businessClassification;
	}

	@Column(name = "FIRST_NAME", columnDefinition = "varchar(50)")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "LAST_NAME", columnDefinition = "varchar(50)")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "MIDDLE_NAME", columnDefinition = "varchar(50)")
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Column(name = "STREET_BRGY", columnDefinition = "varchar(50)")
	public String getStreetBrgy() {
		return streetBrgy;
	}

	public void setStreetBrgy(String streetBrgy) {
		this.streetBrgy = streetBrgy;
	}

	@Column(name = "CITY_PROVINCE", columnDefinition = "varchar(50)")
	public String getCityProvince() {
		return cityProvince;
	}

	public void setCityProvince(String cityProvince) {
		this.cityProvince = cityProvince;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Supplier [serviceLeaseKeyId=").append(serviceLeaseKeyId).append(", name=").append(name)
				.append(", address=").append(address).append(", contactPerson=").append(contactPerson)
				.append(", contactNumber=").append(contactNumber).append(", active=").append(active).append(", tin=")
				.append(tin).append(", busRegTypeId=").append(busRegTypeId).append(", bussinessClassificationId=")
				.append(bussinessClassificationId).append(", firstName=").append(firstName).append(", lastName=")
				.append(lastName).append(", middleName=").append(middleName).append(", streetBrgy=").append(streetBrgy)
				.append(", cityProvince=").append(cityProvince).append("]");
		return builder.toString();
	}
}
