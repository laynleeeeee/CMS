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

import eulap.common.domain.BaseDomain;
import eulap.common.util.NumberFormatUtil;

/**
 * Object representation of AR_CUSTOMER table.

 *
 */
@Entity
@Table(name="AR_CUSTOMER")
public class ArCustomer extends BaseDomain{
	private String name;
	private String firstName;
	private String lastName;
	private String middleName;
	private String streetBrgy;
	private String cityProvince;
	private String contactNumber;
	private String emailAddress;
	private Double maxAmount;
	private String tin;
	private boolean active;
	private Integer serviceLeaseKeyId;
	private Integer ebObjectId;
	private EBObject ebObject;
	private Integer customerTypeId;
	private boolean project;
	private String address;
	private Integer bussinessClassificationId;
	private BusinessClassification businessClassification;
	private String contactPerson;

	public static final int MAX_ADDRESS = 150;
	public static final int MAX_CONTACT_NUMBER = 20;
	public static final int MAX_EMAIL_ADDRESS = 50;
	public static final int OBJECT_TYPE_ID = 99;
	public static final int MAX_NAME = 200;
	public static final int MAX_FIRST_NAME = 50;
	public static final int MAX_MIDDLE_NAME = 50;
	public static final int MAX_LAST_NAME = 50;
	public static final int MAX_CONTACT_PERSON = 50;
	public static final int MAX_TIN = 13;

	public enum FIELD {
		id, name, streetBrgy, cityProvince, contactNumber, emailAddress, maxAmount, active, serviceLeaseKeyId,
		tin, customerTypeId, project, firstName, lastName, middleName, bussinessClassificationId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_CUSTOMER_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "int(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "timestamp")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "int(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "timestamp")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	/**
	 * Get the name of the AR Customer.
	 * @return The name of the AR Customer.
	 */
	@Column(name="NAME", columnDefinition="varchar(200)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the contact number of the AR Customer.
	 * @return The contact number of the AR Customer.
	 */
	@Column(name="CONTACT_NUMBER", columnDefinition="varchar(20)")
	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/**
	 * Get the email address of the AR Customer.
	 * @return The email address of the AR Customer.
	 */
	@Column(name="EMAIL_ADDRESS", columnDefinition="varchar(50)")
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Get the maximum allowable amount of the AR Customer.
	 * @return The maximum allowable amount of the AR Customer.
	 */
	@Column(name="MAX_AMOUNT", columnDefinition="decimal(12,2)")
	public Double getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(Double maxAmount) {
		this.maxAmount = maxAmount;
	}

	/**
	 * Get the TIN of the Customer
	 */
	@Column(name="TIN", columnDefinition="varchar(20)")
	public String getTin() {
		return tin;
	}

	/**
	 * Set the TIN of the Customer
	 */
	public void setTin(String tin) {
		this.tin = tin;
	}

	/**
	 * Verify if AR Customer is active.
	 * @return True if active, otherwise false.
	 */
	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the service lease key Id of the AR Customer.
	 * @return The service lease key Id of the AR Customer.
	 */
	@Column(name = "EB_SL_KEY_ID", columnDefinition = "int(10)")
	public Integer getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(Integer serviceLeaseKeyId) {
		this.serviceLeaseKeyId = serviceLeaseKeyId;
	}

	/**
	 * Get the formatted number and name of the AR Customer.
	 * @return The formatted 10 digit number and name.
	 */
	@Transient
	public String getNumberAndName() {
		return NumberFormatUtil.formatTo10Digit(getId())+ " - "+name;
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

	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Column(name="CUSTOMER_TYPE_ID", columnDefinition="int(10)")
	public Integer getCustomerTypeId() {
		return customerTypeId;
	}

	public void setCustomerTypeId(Integer customerTypeId) {
		this.customerTypeId = customerTypeId;
	}

	@Column(name="PROJECT", columnDefinition="tinyint(1)")
	public boolean isProject() {
		return project;
	}

	public void setProject(boolean project) {
		this.project = project;
	}

	@Column(name="ADDRESS", columnDefinition="varchar(150)")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name="FIRST_NAME", columnDefinition="varchar(50)")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name="LAST_NAME", columnDefinition="varchar(50)")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name="MIDDLE_NAME", columnDefinition="varchar(50)")
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Column(name="STREET_BRGY", columnDefinition="varchar(150)")
	public String getStreetBrgy() {
		return streetBrgy;
	}

	public void setStreetBrgy(String streetBrgy) {
		this.streetBrgy = streetBrgy;
	}

	@Column(name="CITY_PROVINCE", columnDefinition="varchar(150)")
	public String getCityProvince() {
		return cityProvince;
	}

	public void setCityProvince(String cityProvince) {
		this.cityProvince = cityProvince;
	}

	@Column(name="BUSINESS_CLASSIFICATION_ID", columnDefinition="int(10)")
	public Integer getBussinessClassificationId() {
		return bussinessClassificationId;
	}

	public void setBussinessClassificationId(Integer bussinessClassificationId) {
		this.bussinessClassificationId = bussinessClassificationId;
	}

	@Column(name="CONTACT_PERSON", columnDefinition="varchar(50)")
	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	@OneToOne
	@JoinColumn(name = "BUSINESS_CLASSIFICATION_ID", updatable = false, insertable = false)
	public BusinessClassification getBusinessClassification() {
		return businessClassification;
	}

	public void setBusinessClassification(BusinessClassification businessClassification) {
		this.businessClassification = businessClassification;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArCustomer [name=").append(name).append(", firstName=").append(firstName).append(", lastName=")
				.append(lastName).append(", middleName=").append(middleName).append(", streetBrgy=").append(streetBrgy)
				.append(", cityProvince=").append(cityProvince).append(", contactNumber=").append(contactNumber)
				.append(", emailAddress=").append(emailAddress).append(", maxAmount=").append(maxAmount)
				.append(", tin=").append(tin).append(", active=").append(active).append(", serviceLeaseKeyId=")
				.append(serviceLeaseKeyId).append(", ebObjectId=").append(ebObjectId).append(", customerTypeId=")
				.append(customerTypeId).append(", project=").append(project).append(", bussinessClassificationId=")
				.append(bussinessClassificationId).append("]");
		return builder.toString();
	}
}
