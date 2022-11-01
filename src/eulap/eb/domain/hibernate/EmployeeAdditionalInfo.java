package eulap.eb.domain.hibernate;

import eulap.common.domain.BaseDomain;

/**
 * Object representation class of EMPLOYEE_ADDITIONAL_INFO Table.

 *
 */
/*@Entity
@Table(name = "EMPLOYEE_ADDITIONAL_INFO")*/
// TODO: Change the contents to questionaire data.
public class EmployeeAdditionalInfo extends BaseDomain{
	/*private Integer employeeId;
	private String bloodType;
	private String tin;
	private String philhealthNo;
	private String sssNo;
	private String hdmfNo;
	private Date hiredDate;
	private Date employmentPeriodFrom;
	private Date employmentPeriodTo;
	private String permanentAddress;
	private String citizenship;
	private String height;
	private String weight;
	private String eyeColor;
	private String hairColor;
	private String telephoneNumber;
	private String skillTalent;
	private String languageDialect;
	private Integer ebObjectId;

	*//**
	 * Object type id of EMPLOYEE_ADDITIONAL_INFO.
	 *//*
	public static final int OBJECT_TYPE_ID = 67;

	public enum FIELD{
		id, employeeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_ADDITIONAL_INFO_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "EMPLOYMENT_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name = "BLOOD_TYPE", columnDefinition = "varchar(15)")
	public String getBloodType() {
		return bloodType;
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	@Column(name = "TIN", columnDefinition = "varchar(15)")
	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	@Column(name = "PHILHEALTH_NO", columnDefinition = "varchar(15)")
	public String getPhilhealthNo() {
		return philhealthNo;
	}

	public void setPhilhealthNo(String philhealthNo) {
		this.philhealthNo = philhealthNo;
	}

	@Column(name = "SSS_NO", columnDefinition = "varchar(15)")
	public String getSssNo() {
		return sssNo;
	}

	public void setSssNo(String sssNo) {
		this.sssNo = sssNo;
	}

	@Column(name = "HDMF_NO", columnDefinition = "varchar(15)")
	public String getHdmfNo() {
		return hdmfNo;
	}

	public void setHdmfNo(String hdmfNo) {
		this.hdmfNo = hdmfNo;
	}

	@Column(name = "HIRED_DATE", columnDefinition = "date")
	public Date getHiredDate() {
		return hiredDate;
	}

	public void setHiredDate(Date hiredDate) {
		this.hiredDate = hiredDate;
	}

	@Column(name = "EMPLOYMENT_PERIOD_FROM", columnDefinition = "date")
	public Date getEmploymentPeriodFrom() {
		return employmentPeriodFrom;
	}

	public void setEmploymentPeriodFrom(Date employmentPeriodFrom) {
		this.employmentPeriodFrom = employmentPeriodFrom;
	}

	@Column(name = "EMPLOYMENT_PERIOD_TO", columnDefinition = "date")
	public Date getEmploymentPeriodTo() {
		return employmentPeriodTo;
	}

	public void setEmploymentPeriodTo(Date employmentPeriodTo) {
		this.employmentPeriodTo = employmentPeriodTo;
	}

	@Column(name = "PERMANENT_ADDRESS", columnDefinition = "varchar(15)")
	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	@Column(name = "CITIZENSHIP", columnDefinition = "varchar(15)")
	public String getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(String citizenship) {
		this.citizenship = citizenship;
	}

	@Column(name = "HEIGHT", columnDefinition = "varchar(10)")
	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	@Column(name = "WEIGHT", columnDefinition = "varchar(10)")
	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	@Column(name = "EYE_COLOR", columnDefinition = "varchar(15)")
	public String getEyeColor() {
		return eyeColor;
	}

	public void setEyeColor(String eyeColor) {
		this.eyeColor = eyeColor;
	}

	@Column(name = "HAIR_COLOR", columnDefinition = "varchar(15)")
	public String getHairColor() {
		return hairColor;
	}

	public void setHairColor(String hairColor) {
		this.hairColor = hairColor;
	}

	@Column(name = "TELEPHONE_NUMBER", columnDefinition = "varchar(15)")
	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	@Column(name = "SKILL_TALENT", columnDefinition = "varchar(15)")
	public String getSkillTalent() {
		return skillTalent;
	}

	public void setSkillTalent(String skillTalent) {
		this.skillTalent = skillTalent;
	}

	@Column(name = "LANGUAGE_DIALECT", columnDefinition = "varchar(15)")
	public String getLanguageDialect() {
		return languageDialect;
	}

	public void setLanguageDialect(String languageDialect) {
		this.languageDialect = languageDialect;
	}

	@Column(name = "EB_OBJECT_ID", columnDefinition = "INT(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@Override
	public String toString() {
		return "EmployeeAdditionalInfo [employeeId=" + employeeId + ", bloodType=" + bloodType + ", tin=" + tin
				+ ", philhealthNo=" + philhealthNo + ", sssNo=" + sssNo + ", hdmfNo=" + hdmfNo + ", hiredDate="
				+ hiredDate + ", employmentPeriodFrom=" + employmentPeriodFrom + ", employmentPeriodTo="
				+ employmentPeriodTo + ", permanentAddress=" + permanentAddress + ", citizenship=" + citizenship
				+ ", height=" + height + ", weight=" + weight + ", eyeColor=" + eyeColor + ", hairColor=" + hairColor
				+ ", telephoneNumber=" + telephoneNumber + ", skillTalent=" + skillTalent + ", languageDialect="
				+ languageDialect + ", ebObjectId=" + ebObjectId + "]";
	}*/
}
