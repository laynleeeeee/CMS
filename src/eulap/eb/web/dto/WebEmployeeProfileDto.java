package eulap.eb.web.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data transfer object for employee profile.

 *
 */
public class WebEmployeeProfileDto implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer employeeTypeId;
	private Integer employeeId;
	private Integer employeeShiftId;
	private Integer positionId;
	private String employeeNo;
	private String rfid;
	private String firstName;
	private String middleName;
	private String lastName;
	private Integer genderId;
	private Date birthDate;
	private Integer civilStatusId;
	private String contactNo;
	private String address;
	private String emailAddress;
	private Boolean active;
	private Date createdDate;
	private Date updatedDate;
	private Integer createdBy;
	private Integer updatedBy;
	private Integer companyId;
	private String photo;

	public WebEmployeeProfileDto getInstance (Integer employeeTypeId, Integer employeeId, Integer employeeShiftId,
			Integer positionId, String employeeNo, String rfid, String firstName, String middleName,
			String lastName, Integer genderId, Date birthDate, Integer civilStatusId, String contactNo,
			String address, String emailAddress, Boolean active, Date createdDate, Date updatedDate, Integer createdBy,
			Integer updatedBy, String photo, Integer companyId){
		this.employeeTypeId = employeeTypeId;
		this.employeeId = employeeId;
		this.employeeShiftId = employeeShiftId;
		this.positionId = positionId;
		this.employeeNo = employeeNo;
		this.rfid = rfid;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.genderId = genderId;
		this.birthDate = birthDate;
		this.civilStatusId = civilStatusId;
		this.contactNo = contactNo;
		this.address = address;
		this.emailAddress = emailAddress;
		this.active = active;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.employeeTypeId = employeeTypeId;
		this.companyId = companyId;
		this.photo = photo;
		return this;
	}

	public Integer getEmployeeTypeId() {
		return employeeTypeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getEmployeeShiftId() {
		return employeeShiftId;
	}

	public void setEmployeeShiftId(Integer employeeShiftId) {
		this.employeeShiftId = employeeShiftId;
	}

	public Integer getPositionId() {
		return positionId;
	}

	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getGendeId() {
		return genderId;
	}

	public void setGenderId(Integer genderId) {
		this.genderId = genderId;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Integer getCivilStatusId() {
		return civilStatusId;
	}

	public void setCivilStatusId(Integer civilStatusId) {
		this.civilStatusId = civilStatusId;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeTypeId(Integer employeeTypeId) {
		this.employeeTypeId = employeeTypeId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getGenderId() {
		return genderId;
	}

	@Override
	public String toString() {
		return "WebEmployeeProfileDto [employeeTypeId=" + employeeTypeId + ", employeeId=" + employeeId
				+ ", employeeShiftId=" + employeeShiftId + ", positionId=" + positionId + ", employeeNo=" + employeeNo
				+ ", rfid=" + rfid + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName="
				+ lastName + ", genderId=" + genderId + ", birthDate=" + birthDate + ", civilStatusId=" + civilStatusId
				+ ", contactNo=" + contactNo + ", address=" + address + ", emailAddress=" + emailAddress + ", active="
				+ active + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate + ", createdBy=" + createdBy
				+ ", updatedBy=" + updatedBy + "]";
	}
}	