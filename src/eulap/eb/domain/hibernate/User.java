package eulap.eb.domain.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.AccessRight;
import eulap.common.domain.BaseDomain;
import eulap.eb.service.oo.OOChild;

/**
 * Object representation of USER table in the database.


 */
@Entity
@Table(name = "USER")
public class User extends BaseDomain implements OOChild{
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String middleName;
	private Date birthDate;
	private String contactNumber;
	private String emailAddress;
	private String address;
	private Integer userGroupId;
	private int companyId;
	private int accessCode;
	private boolean active;
	private int serviceLeaseKeyId;
	private Integer positionId;
	private List<UserCompany> userCompanies;
	private UserGroup userGroup;
	private Company company;
	private AccessRight accessRight;
	private ServiceLeaseKey serviceLeaseKey;
	private List<UserAccessRight> userAccessRights;
	private Position position;
	private UserLoginStatus userLoginStatus;
	private Integer ebObjectId;
	private EBObject ebObject;
	public static final int USER_OBJECT_TYPE_ID = 80;

	public enum FIELD {
		id, username, firstName, middleName, lastName, userGroupId, positionId, active, companyId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "USER_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * Get the username of user.
	 * @return The username.
	 */
	@Column(name = "USER_NAME")
	public String getUsername() {
		return username;
	}

	/**
	 * Set the username of user.
	 * @param username The username of the user.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Get the password of user.
	 * @return The password.
	 */
	@Column(name = "PASSWORD")
	public String getPassword() {
		return password;
	}

	/**
	 * Set the password of user.
	 * @param password The password of the user.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get the first name of user.
	 * @return The first name.
	 */
	@Column(name = "FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Set the first name of user.
	 * @param firstName The first name of the user.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Get the last name of user.
	 * @return The last name.
	 */
	@Column(name = "LAST_NAME")
	public String getLastName() {
		return lastName;
	}

	/**
	 * Set the last name of user.
	 * @param lastName The last name of the user.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Get the middle name of user.
	 * @return The middle name.
	 */
	@Column(name = "MIDDLE_NAME")
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * Set the middle name of user.
	 * @param middleName The middle name of the user.
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * Get the birth date of user.
	 * @return The birth date.
	 */
	@Column(name = "BIRTH_DATE")
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * Set the birth date of user.
	 * @param birthDate The birth date of the user.
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * Get the contact number of user.
	 * @return The contact number.
	 */
	@Column(name = "CONTACT_NUMBER")
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * Set the contact number of user.
	 * @param contactNumber The contact number of the user.
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/**
	 * Get the email address of user.
	 * @return The email address.
	 */
	@Column(name = "EMAIL_ADDRESS")
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Set the email address of user.
	 * @param emailAddress The email address of the user.
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Get the address of user.
	 * @return The address.
	 */
	@Column(name = "ADDRESS")
	public String getAddress() {
		return address;
	}

	/**
	 * Set the address of user.
	 * @param address The address of the user.
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Get the user group id of the user group associated with the user.
	 * @return The user group id.
	 */
	@Column(name = "USER_GROUP_ID")
	public Integer getUserGroupId() {
		return userGroupId;
	}

	/**
	 * Set the user group id of the user group associated with the user.
	 * @param user group id The user group id of the user group associated with the user.
	 */
	public void setUserGroupId(Integer userGroupId) {
		this.userGroupId = userGroupId;
	}

	/**
	 * Get the Id of the company associated with the user.
	 * @return The company Id.
	 */
	@Column(name = "COMPANY_ID")
	public int getCompanyId() {
		return companyId;
	}

	/**
	 * Set the Id of the company associated with the user.
	 * @param companyId The company Id.
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	/**
	 * Validate if the user is active.
	 * @return True if the user is active, otherwise false.
	 */
	@Column(name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the user status.
	 * @param active Set to true if the user is active, otherwise false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the list of companies associated with the user.
	 * @return The list of companies.
	 */
	@OneToMany (fetch=FetchType.LAZY)
	@JoinColumn(name = "USER_ID", updatable = false, insertable = false)
	public List<UserCompany> getUserCompanies() {
		return userCompanies;
	}

	/**
	 * Set the list of companies associated with the user.
	 * @param companies The list of selected companies associated with the user.
	 */
	public void setUserCompanies(List<UserCompany> userCompanies) {
		this.userCompanies = userCompanies;
	}

	/**
	 * Get the company associated with the user.
	 * @return The company.
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	@JoinColumn(name = "COMPANY_ID", updatable = false, insertable = false)
	public Company getCompany() {
		return company;
	}

	/**
	 * Set the company associated with the user.
	 * @param company The company.
	 */
	public void setCompany(Company company) {
		this.company = company;
	}

	/**
	 * Get the user group associated with the user.
	 * @return The user group.
	 */
	@ManyToOne
	@JoinColumn (name = "USER_GROUP_ID", insertable=false,updatable=false)
	public UserGroup getUserGroup() {
		return userGroup;
	}

	/**
	 * Set the user group associated with the user.
	 * @param user group  The user group associated with the user.
	 */
	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	/**
	 * Get the access of of the user.
	 * @return The access code.
	 */
	@Transient
	public int getAccessCode() {
		return accessCode;
	}

	/**
	 * Set the access code of the user.
	 * @param accessCode The access code.
	 */
	public void setAccessCode(int accessCode) {
		this.accessCode = accessCode;
	}

	/**
	 * Get the access right of the user.
	 * @return The access right.
	 */
	@Transient
	public AccessRight getAccessRight() {
		return accessRight;
	}

	/**
	 * Get the access right of the user.
	 * @param accessRight The access right.
	 */
	public void setAccessRight(AccessRight accessRight) {
		this.accessRight = accessRight;
	}

	/**
	 * Get the company from the list of user's company.
	 * @param companyId The company id
	 * @return The company that is under the logged user.
	 */
	@Transient
	public Company getUserCompany (int companyId) {
		for (UserCompany uc : userCompanies) {
			if (uc.getCompanyId() == companyId)
				return uc.getCompany();
		}
		throw new IllegalArgumentException("unable to find companyid");
	}

	/**
	 * Get the user companies.
	 */
	@Transient
	public List<Company> getCompanies () {
		return getCompanies(false);
	}

	private List<Company> getCompanies (boolean activeOnly) {
		List<Company> ret = new ArrayList<Company>();
		for(UserCompany uc : userCompanies){
			if (activeOnly && uc.isActive()) {
				ret.add(uc.getCompany());
			} else {
				ret.add(uc.getCompany());
			}
		}
		return ret;
	}
	
	/**
	 * Get the assigned companies for this user.
	 */
	@Transient
	public List<Company> getActiveCompanies () {
		return getCompanies(true);
	}
	
	@Column(name = "EB_SL_KEY_ID", columnDefinition="INT(10)")
	public int getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(int serviceLeaseKeyId) {
		this.serviceLeaseKeyId = serviceLeaseKeyId;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn (name = "EB_SL_KEY_ID", insertable=false, updatable=false, nullable=true)
	public ServiceLeaseKey getServiceLeaseKey() {
		return serviceLeaseKey;
	}

	public void setServiceLeaseKey(ServiceLeaseKey serviceLeaseKey) {
		this.serviceLeaseKey = serviceLeaseKey;
	}

	@Transient
	public Collection<UserAccessRight> getUserAccessRights() {
		return userAccessRights;
	}
	
	public void setUserAccessRights(List<UserAccessRight> userAccessRights) {
		this.userAccessRights = userAccessRights;
	}
	
	/**
	 * Evaluate if the user has an access to certain module and product. 
	 * @param productKey The product key.
	 * @param moduleCode Access code. 
	 * @return True if the user has access, otherwise false. 
	 */
	@Transient
	public boolean hasAccess (int productKey, int moduleCode) {
	// TODO : Changed the user to user group.
//		for (UserAccessRight ar : userAccessRights) {
//			if (ar.getProductKey() == productKey) {
//				if ((ar.getModuleKey() & moduleCode) == moduleCode)
//					return true;
//			}
//		}
		for (UserGroupAccessRight ugar : userGroup.getUserGroupAccessRights()) {
			if (ugar.getProductKey() == productKey) {
				if ((ugar.getModuleKey() & moduleCode) == moduleCode)
					return true;
			}
		}
		return false;
	}

	/**
	 * Get the position id of the user.
	 * @return The position id.
	 */
	@Column(name = "POSITION_ID")
	public Integer getPositionId() {
		return positionId;
	}

	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}

	/**
	 * Get the Position assigned to the user.
	 */
	@OneToOne
	@JoinColumn(name = "POSITION_ID", insertable = false, updatable = false)
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * Check user if has access rights to Admin Settings.
	 * @return True if has access, otherwise false.
	 */
	@Transient
	public boolean isAdmin(){
		// 0 = Admin - Product Key.
		// 1 = Admin - Settings Module key.
		return hasAccess(0, 1);
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", middleName=" + middleName + ", birthDate=" + birthDate
				+ ", contactNumber=" + contactNumber + ", emailAddress="
				+ emailAddress + ", address=" + address + ", userGroupId="
				+ userGroupId + ", companyId=" + companyId + ", accessCode="
				+ accessCode + ", active=" + active + ", serviceLeaseKeyId="
				+ serviceLeaseKeyId + ", positionId=" + positionId
				+ ", userCompanies=" + userCompanies + ", userGroup="
				+ userGroup + ", company=" + company + ", accessRight="
				+ accessRight + ", serviceLeaseKey=" + serviceLeaseKey
				+ ", userAccessRights=" + userAccessRights + ", position="
				+ position + "]";
	}
	
	@Transient
	public UserLoginStatus getUserLoginStatus() {
		return userLoginStatus;
	}
	
	public void setUserLoginStatus(UserLoginStatus userLoginStatus) {
		this.userLoginStatus = userLoginStatus;
	}

	/**
	 * Get the full name of the user. Format: LastName, FirstName MiddleName
	 * @return The full name of the user.
	 */
	@Transient
	public String getFullName() {
		return (lastName != null && !lastName.trim().isEmpty() ? lastName + ", " : "") +
				(firstName != null && !firstName.trim().isEmpty() ? firstName + " " : "") +
				(middleName != null && !middleName.trim().isEmpty() ? middleName : "");
	}

	/**
	 * Get the user companies id.
	 */
	@Transient
	public List<Integer> getCompanyIds () {
		return getCompanyIds(false);
	}

	private List<Integer> getCompanyIds (boolean activeOnly) {
		List<Integer> ret = new ArrayList<Integer>();
		for(UserCompany uc : userCompanies){
			if (activeOnly && uc.isActive()) {
				ret.add(uc.getCompany().getId());
			} else {
				ret.add(uc.getCompany().getId());
			}
		}
		return ret;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
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
	public Integer getObjectTypeId() {
		// USER type in OBJECT_TYPE table.
		return USER_OBJECT_TYPE_ID;
	}

	/**
	 * Get the full name of the user. Format: firstName middle initial. lastName
	 * @return The full name of the user.
	 */
	@Transient
	public String getUserFullName() {
		String userFullName = firstName + " ";
		if(middleName != null && !middleName.isEmpty()) {
			userFullName += middleName.charAt(0) + ". ";
		}
		userFullName += lastName;
		return userFullName;
	}
}