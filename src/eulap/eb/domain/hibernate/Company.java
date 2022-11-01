package eulap.eb.domain.hibernate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import eulap.eb.service.oo.OOChild;

/**
 * Object representation of COMPANY table in the database.



 */
@Entity
@Table(name = "COMPANY")
public class Company extends BaseDomain implements OOChild {
	private String name;
	private String address;
	private String contactNumber;
	private String emailAddress;
	private boolean active;
	private String companyNumber;
	private String tin;
	private int serviceLeaseKeyId;
	private String logo;
	private String companyCode;
	private Integer ebObjectId;
	private EBObject ebObject;

	public static final int COMPANY_OBJECT_TYPE_ID = 81;

	public enum Field {id, name, address, tin, companyNumber, contactNumber, companyCode, emailAddress, active, serviceLeaseKeyId, ebObjectId}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "COMPANY_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	/**
	 * Get the company name.
	 * @return The company name.
	 */
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the company.
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the address of the company.
	 * @return The company's address.
	 */
	@Column(name = "ADDRESS")
	public String getAddress() {
		return address;
	}

	/**
	 * Set the address of the company.
	 * @param address The address.
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Get the contact number.
	 * @return The contact number.
	 */
	@Column(name = "CONTACT_NUMBER")
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * Set the contact number.
	 * @param contactNumber The contact number.
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/**
	 * Get the email address of this company.
	 * @return The email address.
	 */
	@Column(name = "EMAIL_ADDRESS")
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Set the email address of this company.
	 * @param emailAddress The email address.
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Validate if the company is active.
	 * @return True if the company is active, otherwise false.
	 */
	@Column(name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the company status.
	 * @param active Set to true if the company is active, otherwise false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the company number.
	 * @return The company number.
	 */
	@Column(name="NUMBER")
	public String getCompanyNumber() {
		return companyNumber;
	}

	/**
	 * Set the company number.
	 * @param companyNumber The company number.
	 */
	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	/**
	 * Get the tax identification number of the company
	 * @return The TIN.
	 */
	@Column(name="TIN")
	public String getTin() {
		return tin;
	}

	/**
	 * Set the tax identification number of the company.
	 * @param tin The tax identification number.
	 */
	public void setTin(String tin) {
		this.tin = tin;
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

	/**
	 * Get file name of the logo for the company.
	 */
	@Column(name = "LOGO", columnDefinition="varchar(50)")
	public String getLogo() {
		return logo;
	}

	/**
	 * Set file name of the logo for the company.
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Transient
	public String getNumberAndName () {
		if (companyNumber.isEmpty()) {
			return name;
		}
		Integer number = Integer.valueOf(companyNumber);
		String formattedNumber = companyNumber;
		if (number != null){
			NumberFormat formatter = new DecimalFormat("####.##");
			formatter.setMinimumIntegerDigits(5);
			formattedNumber = formatter.format(number);
		}
		return formattedNumber + " - "+name;
	}

	@Override
	public String toString() {
		return "Company [name=" + name + ", address=" + address
				+ ", contactNumber=" + contactNumber + ", emailAddress="
				+ emailAddress + ", active=" + active + ", companyNumber="
				+ companyNumber + ", tin=" + tin + ", serviceLeaseKeyId="
				+ serviceLeaseKeyId + ", getId()=" + getId()
				+ ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()="
				+ getCreatedDate() + ", getUpdatedBy()=" + getUpdatedBy()
				+ ", getUpdatedDate()=" + getUpdatedDate() + "]";
	}

	@Column(name = "COMPANY_CODE", columnDefinition="varchar(10)")
	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	@Override
	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", nullable=true, insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		// The object type id of Company object.
		return COMPANY_OBJECT_TYPE_ID;
	}

	@Column(name="EB_OBJECT_ID", columnDefinition="int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	@Override
	public void setEbObjectId(Integer eBObjecctId) {
		this.ebObjectId = eBObjecctId;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

}