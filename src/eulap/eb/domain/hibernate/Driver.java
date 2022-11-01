package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of DRIVER table.

 *
 */
@Entity
@Table(name="DRIVER")
public class Driver extends BaseDomain {
	private Integer genderId;
	private Integer civilStatusId;
	private Integer companyId;
	private String firstName;
	private String middleName;
	private String lastName;
	private Date birthDate;
	private String contactNo;
	private String address;
	private boolean active;
	private Company company;

	public static final int MAX_FIRST_NAME = 40;
	public static final int MAX_LAST_NAME = 40;
	public static final int MAX_MIDDLE_NAME = 40;
	public static final int MAX_CONTACT_NO = 20;
	public static final int MAX_ADDRESS = 150;

	public enum FIELD {
		id, genderId, civilStatusId, firstName, middleName, lastName, birthDate, contactNo,
		address, active, companyId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "DRIVER_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name="GENDER_ID", columnDefinition="int(10)")
	public Integer getGenderId() {
		return genderId;
	}

	public void setGenderId(Integer genderId) {
		this.genderId = genderId;
	}

	@Column(name="CIVIL_STATUS_ID", columnDefinition="int(10)")
	public Integer getCivilStatusId() {
		return civilStatusId;
	}

	public void setCivilStatusId(Integer civilStatusId) {
		this.civilStatusId = civilStatusId;
	}

	@Column(name="FIRST_NAME", columnDefinition="varchar(40)")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name="MIDDLE_NAME", columnDefinition="varchar(40)")
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Column(name="LAST_NAME", columnDefinition="varchar(40)")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name="BIRTH_DATE", columnDefinition="date")
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Column(name="CONTACT_NO", columnDefinition="varchar(20)")
	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	@Column(name="ADDRESS", columnDefinition="varchar(150)")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Driver [genderId=").append(genderId).append(", civilStatusId=").append(civilStatusId)
				.append(", companyId=").append(companyId).append(", firstName=").append(firstName)
				.append(", middleName=").append(middleName).append(", lastName=").append(lastName)
				.append(", birthDate=").append(birthDate).append(", contactNo=").append(contactNo).append(", address=")
				.append(address).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
