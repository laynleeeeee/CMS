package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object class that represents EMPLOYEE table from the database

 *
 */

@Entity
@Table(name="EMPLOYEE")
public class Employee extends BaseDomain{
	private Integer companyId;
	private Integer divisionId;
	private Integer biometricId;
	private String employeeNo;
	private String firstName;
	private String middleName;
	private String lastName;
	private Integer positionId;
	private Integer gender;
	private Date birthDate;
	private Integer civilStatus;
	private String contactNo;
	private String address;
	private String emailAddress;
	private boolean active;
	private Integer employeeShiftId;
	private Integer employeeTypeId;
	private Integer employeeStatusId;
	private EmployeeSalaryDetail salaryDetail;
	private Company company;
	private Position position;
	private EmployeeShift employeeShift;
	private EmployeeStatus employeeStatus;
	private Division division;
	private String completeName;

	public static final int NAME_MAX_CHAR = 40;
	public static final int EMPLOYEE_NO_MAX_CHAR = 10;
	public static final int ADDRESS_MAX_CHAR = 150;
	public static final int CONTACT_NO_MAX_CHAR = 20;

	public enum FIELD {
		companyId, divisionId, biometricId, employeeNo, firstName, middleName, lastName, positionId,
		gender, birthDate, civilStatus, contactNo, address, emailAddress, active, employeeShiftId,
		employeeTypeId, id, updatedDate, employeeStatusId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EMPLOYEE_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "DIVISION_ID", columnDefinition="int(10)")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column(name = "BIOMETRIC_ID", columnDefinition="int(10)")
	public Integer getBiometricId() {
		return biometricId;
	}

	public void setBiometricId(Integer biometricId) {
		this.biometricId = biometricId;
	}

	@Column(name = "EMPLOYEE_NO", columnDefinition="varchar(10)")
	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	@Column(name = "FIRST_NAME", columnDefinition="varchar(40)")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "MIDDLE_NAME", columnDefinition="varchar(40)")
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Column(name = "LAST_NAME", columnDefinition="varchar(40)")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "POSITION_ID", columnDefinition="int(10)")
	public Integer getPositionId() {
		return positionId;
	}

	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}

	@Column(name = "GENDER", columnDefinition="int(2)")
	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	@Column(name = "BIRTH_DATE", columnDefinition="date")
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Column(name = "CIVIL_STATUS", columnDefinition="int(2)")
	public Integer getCivilStatus() {
		return civilStatus;
	}

	public void setCivilStatus(Integer civilStatus) {
		this.civilStatus = civilStatus;
	}

	@Column(name = "CONTACT_NO", columnDefinition="varchar(20)")
	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	@Column(name = "ADDRESS", columnDefinition="varchar(150)")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "EMAIL_ADDRESS", columnDefinition="varchar(40)")
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "EMPLOYEE_SHIFT_ID", columnDefinition="int(10)")
	public Integer getEmployeeShiftId() {
		return employeeShiftId;
	}

	public void setEmployeeShiftId(Integer employeeShiftId) {
		this.employeeShiftId = employeeShiftId;
	}

	@Column(name = "EMPLOYEE_TYPE_ID", columnDefinition="int(10)")
	public Integer getEmployeeTypeId() {
		return employeeTypeId;
	}

	public void setEmployeeTypeId(Integer employeeTypeId) {
		this.employeeTypeId = employeeTypeId;
	}

	@Fetch(FetchMode.SELECT)
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "EMPLOYEE_ID", insertable = false, updatable = false)
	public EmployeeSalaryDetail getSalaryDetail() {
		return salaryDetail;
	}

	public void setSalaryDetail(EmployeeSalaryDetail salaryDetail) {
		this.salaryDetail = salaryDetail;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn(name = "POSITION_ID", insertable = false, updatable = false)
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	@Column(name = "EMPLOYEE_STATUS_ID", columnDefinition="int(10)")
	public Integer getEmployeeStatusId() {
		return employeeStatusId;
	}

	public void setEmployeeStatusId(Integer employeeStatusId) {
		this.employeeStatusId = employeeStatusId;
	}

	@OneToOne
	@JoinColumn(name = "EMPLOYEE_SHIFT_ID", insertable = false, updatable = false)
	public EmployeeShift getEmployeeShift() {
		return employeeShift;
	}

	public void setEmployeeShift(EmployeeShift employeeShift) {
		this.employeeShift = employeeShift;
	}

	@OneToOne
	@JoinColumn(name = "EMPLOYEE_STATUS_ID", insertable = false, updatable = false)
	public EmployeeStatus getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(EmployeeStatus employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	@Transient
	public String getFullName () {
		return lastName + ", " +firstName+ " " + (!middleName.isEmpty() ? middleName.charAt(0) : "");
	}

	@OneToOne
	@JoinColumn(name = "DIVISION_ID", insertable = false, updatable = false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@Transient
	public String getCompleteName() {
		return completeName;
	}

	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Employee [companyId=").append(companyId)
				.append(", divisionId=").append(divisionId)
				.append(", biometricId=").append(biometricId)
				.append(", employeeNo=").append(employeeNo)
				.append(", firstName=").append(firstName)
				.append(", middleName=").append(middleName)
				.append(", lastName=").append(lastName).append(", positionId=")
				.append(positionId).append(", gender=").append(gender)
				.append(", birthDate=").append(birthDate)
				.append(", civilStatus=").append(civilStatus)
				.append(", contactNo=").append(contactNo).append(", address=")
				.append(address).append(", emailAddress=").append(emailAddress)
				.append(", active=").append(active)
				.append(", employeeShiftId=").append(employeeShiftId)
				.append(", employeeTypeId=").append(employeeTypeId).append("]");
		return builder.toString();
	}

	/**
	 * Get the full name of the employee. Format: firstName middle initial. lastName
	 * @return The full name of the user.
	 */
	@Transient
	public String getEmployeeFullName() {
		String employeeFullName = firstName + " ";
		if(middleName != null && !middleName.isEmpty()) {
			employeeFullName += middleName.charAt(0) + ". ";
		}
		employeeFullName += lastName;
		return employeeFullName;
	}
}
