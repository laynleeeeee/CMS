package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.validation.BindingResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.common.domain.BaseDomain;
import eulap.common.util.DateUtil;
import eulap.eb.web.dto.EmployeeScheduleDtrDto;

/**
 * Object representation class of EMPLOYEE_PROFILE Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_PROFILE")
public class EmployeeProfile extends BaseDomain{
	private Integer employeeId;
	private Integer employeeNumber;
	private String rfid;
	private String bloodType;
	private String tin;
	private String philhealthNo;
	private String sssNo;
	private String hdmfNo;
	private Date hiredDate;
	private Date employmentPeriodFrom;
	private Date employmentPeriodTo;
	private String permanentAddress;
	private String birthPlace;
	private String citizenship;
	private String height;
	private String weight;
	private String eyeColor;
	private String hairColor;
	private String telephoneNumber;
	private Integer genderId;
	private Integer civilStatusId;
	private String religion;
	private String languageDialect;
	private Integer ebObjectId;
	private Employee employee;
	private EmployeeFamily employeeFamily;
	private EmployeeEducationalAttainment employeeEducationalAttainment;
	private EmployeeCivilQuery employeeCivilQuery;
	private EmployeeEmploymentQuery employeeEmploymentQuery;
	private Gender gender;
	private CivilStatus civilStatus;
	private List<EmployeeSibling> employeeSiblings;
	private List<EmployeeChildren> employeeChildren;
	private List<EmployeeEmployment> employeeEmployments;
	private List<EmployeeRelative> employeeRelatives;
	private List<EeEmergencyContact> emergencyContacts;
	private List<EeLicenseCertificate> licenseCertificates;
	private List<EeSeminarAttended> seminarAttendeds;
	private List<EeNationalCompetency> nationalCompetencies;
	private String employeeChildrenJson;
	private String eeEmergencyContactJson;
	private String eeLicenseCertificateJson;
	private String eeSeminarAttendedJson;
	private String eeNationalCompetencyJson;
	private String employeeSiblingsJson;
	private String employeeEmploymentsJson;
	private String employeeRelativesJson;
	private String employeeSiblingsMsg;
	private String employeeChildrenMsg;
	private String employeeEmploymentsMsg;
	private String employeeRelativesMsg;
	private ReferenceDocument referenceDocument;
	private BindingResult result;
	private List<EmployeeScheduleDtrDto> employeeScheduleDtrDtos;
	private Integer formPage;
	private EmployeeShift employeeShift;
	private Integer employeeShiftId;
	private String strEmployeeNumber;

	/**
	 * Object type id of EMPLOYEE_PROFILE.
	 */
	public static final int OBJECT_TYPE_ID = 139;

	/**
	 * Product key for EMPLOYEE_PROFILE.
	 */
	public static final int PRODUCT_KEY = 20024;

	public static final int MAX_CHAR_EMPLOYEE_NUMBER = 8;
	public static final int MAX_CHAR_BLOOD_TYPE = 10;
	public static final int MAX_CHAR_TIN = 15;
	public static final int MAX_CHAR_PHILHEATH_NO = 15;
	public static final int MAX_CHAR_SSS_NO = 15;
	public static final int MAX_CHAR_HDMF_NO = 15;
	public static final int MAX_CHAR_BIRTH_PLACE = 150;
	public static final int MAX_CHAR_CITIZENSHIP = 25;
	public static final int MAX_CHAR_HEIGHT = 10;
	public static final int MAX_CHAR_WEIGHT = 10;
	public static final int MAX_CHAR_EYE_COLOR = 15;
	public static final int MAX_CHAR_HAIR_COLOR = 15;
	public static final int MAX_CHAR_TELEPHONE_NUMBER = 15;
	public static final int MAX_CHAR_RELIGION = 25;
	public static final int MAX_CHAR_SKILL_TALENT = 250;
	public static final int MAX_CHAR_LANGUAGE_DIALECT = 250;
	public static final int MAX_RFID = 10;
	public static final int MAX_PERMANENT_ADDRESS = 150;

	public static final int FORM_PAGE_BASIC_INFO = 1;
	public static final int FORM_PAGE_FAMILY = 2;
	public static final int FORM_PAGE_EDUC_ATTAINMENT = 3;
	public static final int FORM_PAGE_FINAL = 4;

	public enum FIELD{
		id, employeeId, employeeNumber, bloodType, tin, philhealthNo, sssNo, hdmfNo, hiredDate, employmentPeriodFrom, employmentPeriodTo, 
		permanentAddress, citizenship, height, weight, eyeColor, hairColor, telephoneNumber, languageDialect, ebObjectId, rfid, employeeShiftId, genderId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_PROFILE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "EMPLOYEE_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name = "EMPLOYEE_NUMBER", columnDefinition = "INT(10)")
	public Integer getEmployeeNumber() {
		return employeeNumber;
	}

	@Column(name = "RFID", columnDefinition = "varchar(10)")
	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public void setEmployeeNumber(Integer employeeNumber) {
		this.employeeNumber = employeeNumber;
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

	@Column(name = "PERMANENT_ADDRESS", columnDefinition = "varchar(150)")
	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	@Column(name = "BIRTH_PLACE", columnDefinition = "varchar(150)")
	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
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

	@Column(name = "GENDER_ID", columnDefinition = "INT(10)")
	public Integer getGenderId() {
		return genderId;
	}

	public void setGenderId(Integer genderId) {
		this.genderId = genderId;
	}

	@Column(name = "CIVIL_STATUS_ID", columnDefinition = "INT(10)")
	public Integer getCivilStatusId() {
		return civilStatusId;
	}

	public void setCivilStatusId(Integer civilStatusId) {
		this.civilStatusId = civilStatusId;
	}

	@Column(name = "RELIGION", columnDefinition = "varchar(25)")
	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	@Column(name = "LANGUAGE_DIALECT", columnDefinition = "varchar(250)")
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

	@OneToOne
	@JoinColumn (name="EMPLOYEE_ID", insertable=false, updatable=false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Transient
	public EmployeeFamily getEmployeeFamily() {
		return employeeFamily;
	}

	public void setEmployeeFamily(EmployeeFamily employeeFamily) {
		this.employeeFamily = employeeFamily;
	}

	@Transient
	public EmployeeEducationalAttainment getEmployeeEducationalAttainment() {
		return employeeEducationalAttainment;
	}

	public void setEmployeeEducationalAttainment(EmployeeEducationalAttainment employeeEducationalAttainment) {
		this.employeeEducationalAttainment = employeeEducationalAttainment;
	}

	@Transient
	public EmployeeCivilQuery getEmployeeCivilQuery() {
		return employeeCivilQuery;
	}

	public void setEmployeeCivilQuery(EmployeeCivilQuery employeeCivilQuery) {
		this.employeeCivilQuery = employeeCivilQuery;
	}

	@Transient
	public EmployeeEmploymentQuery getEmployeeEmploymentQuery() {
		return employeeEmploymentQuery;
	}

	public void setEmployeeEmploymentQuery(EmployeeEmploymentQuery employeeEmploymentQuery) {
		this.employeeEmploymentQuery = employeeEmploymentQuery;
	}

	@OneToOne
	@JoinColumn (name="GENDER_ID", insertable=false, updatable=false)
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@OneToOne
	@JoinColumn (name="CIVIL_STATUS_ID", insertable=false, updatable=false)
	public CivilStatus getCivilStatus() {
		return civilStatus;
	}

	public void setCivilStatus(CivilStatus civilStatus) {
		this.civilStatus = civilStatus;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="EMPLOYEE_ID", insertable=false, updatable=false)
	public List<EmployeeChildren> getEmployeeChildren() {
		return employeeChildren;
	}

	public void setEmployeeChildren(List<EmployeeChildren> employeeChildren) {
		this.employeeChildren = employeeChildren;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="EMPLOYEE_ID", insertable=false, updatable=false)
	public List<EmployeeSibling> getEmployeeSiblings() {
		return employeeSiblings;
	}

	public void setEmployeeSiblings(List<EmployeeSibling> employeeSiblings) {
		this.employeeSiblings = employeeSiblings;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="EMPLOYEE_ID", insertable=false, updatable=false)
	public List<EmployeeEmployment> getEmployeeEmployments() {
		return employeeEmployments;
	}

	public void setEmployeeEmployments(List<EmployeeEmployment> employeeEmployments) {
		this.employeeEmployments = employeeEmployments;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="EMPLOYEE_ID", insertable=false, updatable=false)
	public List<EmployeeRelative> getEmployeeRelatives() {
		return employeeRelatives;
	}

	public void setEmployeeRelatives(List<EmployeeRelative> employeeRelatives) {
		this.employeeRelatives = employeeRelatives;
	}

	@Transient
	public String getEmployeeSiblingsJson() {
		return employeeSiblingsJson;
	}

	public void setEmployeeSiblingsJson(String employeeSiblingsJson) {
		this.employeeSiblingsJson = employeeSiblingsJson;
	}

	@Transient
	public String getEmployeeChildrenJson() {
		return employeeChildrenJson;
	}

	public void setEmployeeChildrenJson(String employeeChildrenJson) {
		this.employeeChildrenJson = employeeChildrenJson;
	}

	@Transient
	public String getEmployeeEmploymentsJson() {
		return employeeEmploymentsJson;
	}

	public void setEmployeeEmploymentsJson(String employeeEmploymentsJson) {
		this.employeeEmploymentsJson = employeeEmploymentsJson;
	}

	@Transient
	public String getEmployeeRelativesJson() {
		return employeeRelativesJson;
	}

	public void setEmployeeRelativesJson(String employeeRelativesJson) {
		this.employeeRelativesJson = employeeRelativesJson;
	}

	@Transient
	public String getEmployeeSiblingsMsg() {
		return employeeSiblingsMsg;
	}

	public void setEmployeeSiblingsMsg(String employeeSiblingsMsg) {
		this.employeeSiblingsMsg = employeeSiblingsMsg;
	}

	@Transient
	public String getEmployeeChildrenMsg() {
		return employeeChildrenMsg;
	}

	public void setEmployeeChildrenMsg(String employeeChildrenMsg) {
		this.employeeChildrenMsg = employeeChildrenMsg;
	}

	@Transient
	public String getEmployeeEmploymentsMsg() {
		return employeeEmploymentsMsg;
	}

	public void setEmployeeEmploymentsMsg(String employeeEmploymentsMsg) {
		this.employeeEmploymentsMsg = employeeEmploymentsMsg;
	}

	@Transient
	public String getEmployeeRelativesMsg() {
		return employeeRelativesMsg;
	}

	public void setEmployeeRelativesMsg(String employeeRelativesMsg) {
		this.employeeRelativesMsg = employeeRelativesMsg;
	}

	@Transient
	public void serializeEmployeeSibling (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		employeeSiblingsJson = gson.toJson(employeeSiblings);
	}

	@Transient
	public void deserializeEmployeeSibling () {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EmployeeSibling>>(){}.getType();
		employeeSiblings = gson.fromJson(employeeSiblingsJson, type);
	}

	@Transient
	public void serializeEmployeeChildren (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		employeeChildrenJson = gson.toJson(employeeChildren);
	}

	@Transient
	public void deserializeEmployeeChildren () {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EmployeeChildren>>(){}.getType();
		employeeChildren = gson.fromJson(employeeChildrenJson, type);
	}

	@Transient
	public void serializeEmployeeEmployement (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		employeeEmploymentsJson = gson.toJson(employeeEmployments);
	}

	@Transient
	public void deserializeEmployeeEmployement () {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EmployeeEmployment>>(){}.getType();
		employeeEmployments = gson.fromJson(employeeEmploymentsJson, type);
	}

	@Transient
	public void serializeEmployeeRelative (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		employeeRelativesJson = gson.toJson(employeeRelatives);
	}

	@Transient
	public void deserializeEmployeeRelative () {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EmployeeRelative>>(){}.getType();
		employeeRelatives = gson.fromJson(employeeRelativesJson, type);
	}

	@Transient
	public ReferenceDocument getReferenceDocument() {
		return referenceDocument;
	}

	public void setReferenceDocument(ReferenceDocument referenceDocument) {
		this.referenceDocument = referenceDocument;
	}

	@Transient
	public BindingResult getResult() {
		return result;
	}

	public void setResult(BindingResult result) {
		this.result = result;
	}

	@Transient
	public Date getRenewalDate () {
		return DateUtil.addMonthsToDate(hiredDate, 6);
	}

	@Transient
	public String getStrEmployeeNumber() {
		return strEmployeeNumber;
	}

	public void setStrEmployeeNumber(String strEmployeeNumber) {
		this.strEmployeeNumber = strEmployeeNumber;
	}

	@OneToOne
	@JoinColumn(name="EMPLOYEE_SHIFT_ID", insertable=false, updatable=false)
	public EmployeeShift getEmployeeShift() {
		return employeeShift;
	}
	
	public void setEmployeeShift(EmployeeShift employeeShift) {
		this.employeeShift = employeeShift;
	}

	@Column(name="EMPLOYEE_SHIFT_ID", columnDefinition="INT(10)")
	public Integer getEmployeeShiftId() {
		return employeeShiftId;
	}
	public void setEmployeeShiftId(Integer employeeShiftId) {
		this.employeeShiftId = employeeShiftId;
	}
	@Transient
	public Integer getAge() {
		if (employee != null && employee.getBirthDate() != null) {
			return DateUtil.computeAge(employee.getBirthDate());
		}
		return 0;
	}

	@Transient
	public List<EmployeeScheduleDtrDto> getEmployeeScheduleDtrDtos() {
		return employeeScheduleDtrDtos;
	}

	public void setEmployeeScheduleDtrDtos(List<EmployeeScheduleDtrDto> employeeScheduleDtrDtos) {
		this.employeeScheduleDtrDtos = employeeScheduleDtrDtos;
	}

	@Transient
	public Integer getFormPage() {
		return formPage;
	}

	public void setFormPage(Integer formPage) {
		this.formPage = formPage;
	}

	@Transient
	public List<EeEmergencyContact> getEmergencyContacts() {
		return emergencyContacts;
	}

	public void setEmergencyContacts(List<EeEmergencyContact> emergencyContacts) {
		this.emergencyContacts = emergencyContacts;
	}

	@Transient
	public List<EeLicenseCertificate> getLicenseCertificates() {
		return licenseCertificates;
	}

	public void setLicenseCertificates(List<EeLicenseCertificate> licenseCertificates) {
		this.licenseCertificates = licenseCertificates;
	}

	@Transient
	public List<EeSeminarAttended> getSeminarAttendeds() {
		return seminarAttendeds;
	}

	public void setSeminarAttendeds(List<EeSeminarAttended> seminarAttendeds) {
		this.seminarAttendeds = seminarAttendeds;
	}

	@Transient
	public List<EeNationalCompetency> getNationalCompetencies() {
		return nationalCompetencies;
	}

	public void setNationalCompetencies(List<EeNationalCompetency> nationalCompetencies) {
		this.nationalCompetencies = nationalCompetencies;
	}

	@Transient
	public String getEeEmergencyContactJson() {
		return eeEmergencyContactJson;
	}

	public void setEeEmergencyContactJson(String eeEmergencyContactJson) {
		this.eeEmergencyContactJson = eeEmergencyContactJson;
	}

	@Transient
	public void serializeEmergencyContacts(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		eeEmergencyContactJson = gson.toJson(emergencyContacts);
	}

	@Transient
	public void deserializeEmergencyContacts() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EeEmergencyContact>>(){}.getType();
		emergencyContacts = gson.fromJson(eeEmergencyContactJson, type);
	}

	@Transient
	public String getEeLicenseCertificateJson() {
		return eeLicenseCertificateJson;
	}

	public void setEeLicenseCertificateJson(String eeLicenseCertificateJson) {
		this.eeLicenseCertificateJson = eeLicenseCertificateJson;
	}

	@Transient
	public void serializeLicensesCertificates(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		eeLicenseCertificateJson = gson.toJson(licenseCertificates);
	}

	@Transient
	public void deserializeLicensesCertificates() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EeLicenseCertificate>>(){}.getType();
		licenseCertificates = gson.fromJson(eeLicenseCertificateJson, type);
	}

	@Transient
	public String getEeSeminarAttendedJson() {
		return eeSeminarAttendedJson;
	}

	public void setEeSeminarAttendedJson(String eeSeminarAttendedJson) {
		this.eeSeminarAttendedJson = eeSeminarAttendedJson;
	}

	@Transient
	public void serializeSeminarsAttended(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		eeSeminarAttendedJson = gson.toJson(seminarAttendeds);
	}

	@Transient
	public void deserializeSeminarsAttended() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EeSeminarAttended>>(){}.getType();
		seminarAttendeds = gson.fromJson(eeSeminarAttendedJson, type);
	}

	@Transient
	public String getEeNationalCompetencyJson() {
		return eeNationalCompetencyJson;
	}

	public void setEeNationalCompetencyJson(String eeNationalCompetencyJson) {
		this.eeNationalCompetencyJson = eeNationalCompetencyJson;
	}

	@Transient
	public void serializeNationalCompetencies(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		eeNationalCompetencyJson = gson.toJson(nationalCompetencies);
	}

	@Transient
	public void deserializeNationalCompetencies() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<EeNationalCompetency>>(){}.getType();
		nationalCompetencies = gson.fromJson(eeNationalCompetencyJson, type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeProfile [employeeId=").append(employeeId).append(", employeeNumber=")
				.append(employeeNumber).append(", rfid=").append(rfid).append(", bloodType=").append(bloodType)
				.append(", tin=").append(tin).append(", philhealthNo=").append(philhealthNo).append(", sssNo=")
				.append(sssNo).append(", hdmfNo=").append(hdmfNo).append(", hiredDate=").append(hiredDate)
				.append(", employmentPeriodFrom=").append(employmentPeriodFrom).append(", employmentPeriodTo=")
				.append(employmentPeriodTo).append(", permanentAddress=").append(permanentAddress)
				.append(", birthPlace=").append(birthPlace).append(", citizenship=").append(citizenship)
				.append(", height=").append(height).append(", weight=").append(weight).append(", eyeColor=")
				.append(eyeColor).append(", hairColor=").append(hairColor).append(", telephoneNumber=")
				.append(telephoneNumber).append(", genderId=").append(genderId).append(", civilStatusId=")
				.append(civilStatusId).append(", religion=").append(religion).append(", languageDialect=")
				.append(languageDialect).append(", ebObjectId=").append(ebObjectId).append(", gender=")
				.append(gender).append(", civilStatus=").append(civilStatus).append(", formPage=")
				.append(formPage).append(", strEmployeeNumber=").append(strEmployeeNumber)
				.append("]");
		return builder.toString();
	}
}
