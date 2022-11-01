package eulap.eb.web.dto;


import eulap.common.util.NumberFormatUtil;


/**
 * A class that handles the Uploaded Student Grade.

 */
public class UploadedStudentGradeDto {
	private String year;
	private Integer number;
	private String firstName;
	private String lastName;
	private String subject;
	private Double grade;
	private String middleName;
	
	public static UploadedStudentGradeDto getInstanceOf(String year, Integer number, String firstName,
			String lastName,String middleName, String subject, Double grade) {
		UploadedStudentGradeDto dto =  new UploadedStudentGradeDto();
		dto.year = year;
		dto.number = number;
		dto.firstName = firstName;
		dto.lastName = lastName;
		dto.subject = subject;
		dto.grade = grade;
		dto.middleName = middleName;
		return dto;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getNumber() {
		return NumberFormatUtil.formatNumber(number, 6);
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Override
	public String toString() {
		return "UploadedStudentGradeDto [year=" + year + ", number=" + number
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", subject=" + subject + ", grade=" + grade + ", middleName="
				+ middleName + "]";
	}
}
