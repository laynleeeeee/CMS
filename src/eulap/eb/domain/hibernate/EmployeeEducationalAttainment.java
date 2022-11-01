package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation class of EMPLOYEE_EDUCATIONAL_ATTAINMENT Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_EDUCATIONAL_ATTAINMENT")
public class EmployeeEducationalAttainment extends BaseDomain{
	private Integer employeeEducationalAttainmentTypeId;
	private Integer employeeId;
	private String elementarySchool;
	private Integer elementaryYear;
	private String elementaryCourse;
	private String hsSchool;
	private Integer hsYear;
	private String hsCourse;
	private String collegeSchool;
	private Integer collegeYear;
	private String collegeCourse;
	private String postGradSchool;
	private Integer postGradYear;
	private String postGradCourse;
	private String vocationalSchool;
	private Integer vocationalYear;
	private String vocationalCourse;
	private String vocationalAddress;
	private String elementaryAddress;
	private String hsAddress;
	private String collegeAddress;
	private String postGradAddress;
	private String employeeSkills;

	public static final int MAX_ELEMENTARY_SCHOOL = 100;
	public static final int MAX_ELEMENTARY_COURSE = 100;
	public static final int MAX_HS_SCHOOL = 100;
	public static final int MAX_HS_COURSE = 100;
	public static final int MAX_COLLEGE_SCHOOL = 100;
	public static final int MAX_COLLEGE_COURSE = 100;
	public static final int MAX_POST_GRAD_SCHOOL = 100;
	public static final int MAX_POST_GRAD_COURSE = 100;

	public enum FIELD {
		id, employeeId, elementarySchool, elementaryYear, elementaryCourse, hsSchool, hsYear, hsCourse,
		collegeSchool, collegeYear, collegeCourse, postGradSchool, postGradYear, postGradCourse
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_EDUCATIONAL_ATTAINMENT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="EDUCATIONAL_ATTAINMENT_TYPE_ID", columnDefinition="int(10)")
	public Integer getEmployeeEducationalAttainmentTypeId() {
		return employeeEducationalAttainmentTypeId;
	}

	public void setEmployeeEducationalAttainmentTypeId(Integer employeeEducationalAttainmentTypeId) {
		this.employeeEducationalAttainmentTypeId = employeeEducationalAttainmentTypeId;
	}

	@Column(name = "EMPLOYEE_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name = "ELEMENTARY_SCHOOL", columnDefinition = "varchar(100)")
	public String getElementarySchool() {
		return elementarySchool;
	}

	public void setElementarySchool(String elementarySchool) {
		this.elementarySchool = elementarySchool;
	}

	@Column(name = "ELEMENTARY_YEAR", columnDefinition = "INT(10)")
	public Integer getElementaryYear() {
		return elementaryYear;
	}

	public void setElementaryYear(Integer elementaryYear) {
		this.elementaryYear = elementaryYear;
	}

	@Column(name = "ELEMENTARY_COURSE", columnDefinition = "varchar(100)")
	public String getElementaryCourse() {
		return elementaryCourse;
	}
	
	public void setElementaryCourse(String elementaryCourse) {
		this.elementaryCourse = elementaryCourse;
	}

	@Column(name = "HS_SCHOOL", columnDefinition = "varchar(100)")
	public String getHsSchool() {
		return hsSchool;
	}

	public void setHsSchool(String hsSchool) {
		this.hsSchool = hsSchool;
	}

	@Column(name = "HS_COURSE", columnDefinition = "varchar(100)")
	public String getHsCourse() {
		return hsCourse;
	}

	public void setHsCourse(String hsCourse) {
		this.hsCourse = hsCourse;
	}

	@Column(name = "HS_YEAR", columnDefinition = "INT(10)")
	public Integer getHsYear() {
		return hsYear;
	}

	public void setHsYear(Integer hsYear) {
		this.hsYear = hsYear;
	}

	@Column(name = "COLLEGE_SCHOOL", columnDefinition = "varchar(100)")
	public String getCollegeSchool() {
		return collegeSchool;
	}

	public void setCollegeSchool(String collegeSchool) {
		this.collegeSchool = collegeSchool;
	}

	@Column(name = "COLLEGE_YEAR", columnDefinition = "INT(10)")
	public Integer getCollegeYear() {
		return collegeYear;
	}

	@Column(name = "COLLEGE_COURSE", columnDefinition = "varchar(100)")
	public String getCollegeCourse() {
		return collegeCourse;
	}

	public void setCollegeCourse(String collegeCourse) {
		this.collegeCourse = collegeCourse;
	}

	public void setCollegeYear(Integer collegeYear) {
		this.collegeYear = collegeYear;
	}

	@Column(name = "POST_GRAD_SCHOOL", columnDefinition = "varchar(100)")
	public String getPostGradSchool() {
		return postGradSchool;
	}

	public void setPostGradSchool(String postGradSchool) {
		this.postGradSchool = postGradSchool;
	}

	@Column(name = "POST_GRAD_YEAR", columnDefinition = "INT(10)")
	public Integer getPostGradYear() {
		return postGradYear;
	}

	public void setPostGradYear(Integer postGradYear) {
		this.postGradYear = postGradYear;
	}

	@Column(name = "POST_GRAD_COURSE", columnDefinition = "varchar(100)")
	public String getPostGradCourse() {
		return postGradCourse;
	}

	public void setPostGradCourse(String postGradCourse) {
		this.postGradCourse = postGradCourse;
	}

	@Column(name="VOCATIONAL_SCHOOL", columnDefinition="varchar(100)")
	public String getVocationalSchool() {
		return vocationalSchool;
	}

	public void setVocationalSchool(String vocationalSchool) {
		this.vocationalSchool = vocationalSchool;
	}

	@Column(name="VOCATIONAL_YEAR", columnDefinition="int(4)")
	public Integer getVocationalYear() {
		return vocationalYear;
	}

	public void setVocationalYear(Integer vocationalYear) {
		this.vocationalYear = vocationalYear;
	}

	@Column(name="VOCATIONAL_COURSE", columnDefinition="varchar(100)")
	public String getVocationalCourse() {
		return vocationalCourse;
	}

	public void setVocationalCourse(String vocationalCourse) {
		this.vocationalCourse = vocationalCourse;
	}

	@Column(name="VOCATIONAL_ADDRESS", columnDefinition="varchar(100)")
	public String getVocationalAddress() {
		return vocationalAddress;
	}

	public void setVocationalAddress(String vocationalAddress) {
		this.vocationalAddress = vocationalAddress;
	}

	@Column(name="ELEMENTARY_ADDRESS", columnDefinition="varchar(100)")
	public String getElementaryAddress() {
		return elementaryAddress;
	}

	public void setElementaryAddress(String elementaryAddress) {
		this.elementaryAddress = elementaryAddress;
	}

	@Column(name="HS_ADDRESS", columnDefinition="varchar(100)")
	public String getHsAddress() {
		return hsAddress;
	}

	public void setHsAddress(String hsAddress) {
		this.hsAddress = hsAddress;
	}

	@Column(name="COLLEGE_ADDRESS", columnDefinition="varchar(100)")
	public String getCollegeAddress() {
		return collegeAddress;
	}

	public void setCollegeAddress(String collegeAddress) {
		this.collegeAddress = collegeAddress;
	}

	@Column(name="POST_GRAD_ADDRESS", columnDefinition="varchar(100)")
	public String getPostGradAddress() {
		return postGradAddress;
	}

	public void setPostGradAddress(String postGradAddress) {
		this.postGradAddress = postGradAddress;
	}

	@Column(name="EMPLOYEE_SKILLS", columnDefinition="varchar(250)")
	public String getEmployeeSkills() {
		return employeeSkills;
	}

	public void setEmployeeSkills(String employeeSkills) {
		this.employeeSkills = employeeSkills;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeEducationalAttainment [employeeEducationalAttainmentTypeId=")
				.append(employeeEducationalAttainmentTypeId).append(", employeeId=").append(employeeId)
				.append(", elementarySchool=").append(elementarySchool).append(", elementaryYear=")
				.append(elementaryYear).append(", elementaryCourse=").append(elementaryCourse).append(", hsSchool=")
				.append(hsSchool).append(", hsYear=").append(hsYear).append(", hsCourse=").append(hsCourse)
				.append(", collegeSchool=").append(collegeSchool).append(", collegeYear=").append(collegeYear)
				.append(", collegeCourse=").append(collegeCourse).append(", postGradSchool=").append(postGradSchool)
				.append(", postGradYear=").append(postGradYear).append(", postGradCourse=").append(postGradCourse)
				.append(", vocationalSchool=").append(vocationalSchool).append(", vocationalYear=")
				.append(vocationalYear).append(", vocationalCourse=").append(vocationalCourse)
				.append(", vocationalAddress=").append(vocationalAddress).append(", elementaryAddress=")
				.append(elementaryAddress).append(", hsAddress=").append(hsAddress).append(", collegeAddress=")
				.append(collegeAddress).append(", postGradAddress=").append(postGradAddress).append(", employeeSkills=")
				.append(employeeSkills).append("]");
		return builder.toString();
	}
}
