package eulap.eb.web.dto;

import java.util.Date;
import java.util.List;

import eulap.eb.domain.hibernate.BiometricModel;
import eulap.eb.domain.hibernate.EmployeeDtr;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.TimeSheet;

/**
 * Data transfer class for Timesheet DTO

 */

public class TimesheetFormDto {
	private Integer timesheetId;
	private String fileData;
	private String file;
	private String fileName;
	private Double fileSize;
	private String description;
	private Date dateFrom;
	private Date dateTo;
	private TimeSheet timeSheet;
	private BiometricModel biometricModel;
	private ReferenceDocument referenceDocument;
	private String employeeDtrsJson;
	private List<EmployeeDtr> employeeDtrs;

	public TimeSheet getTimeSheet() {
		return timeSheet;
	}

	public void setTimeSheet(TimeSheet timeSheet) {
		this.timeSheet = timeSheet;
	}

	public BiometricModel getBiometricModel() {
		return biometricModel;
	}

	public void setBiometricModel(BiometricModel biometricModel) {
		this.biometricModel = biometricModel;
	}

	public ReferenceDocument getReferenceDocument() {
		return referenceDocument;
	}

	public void setReferenceDocument(ReferenceDocument referenceDocument) {
		this.referenceDocument = referenceDocument;
	}

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Double getFileSize() {
		return fileSize;
	}

	public void setFileSize(Double fileSize) {
		this.fileSize = fileSize;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmployeeDtrsJson() {
		return employeeDtrsJson;
	}

	public void setEmployeeDtrsJson(String employeeDtrsJson) {
		this.employeeDtrsJson = employeeDtrsJson;
	}

	public List<EmployeeDtr> getEmployeeDtrs() {
		return employeeDtrs;
	}

	public void setEmployeeDtrs(List<EmployeeDtr> employeeDtrs) {
		this.employeeDtrs = employeeDtrs;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Integer getTimesheetId() {
		return timesheetId;
	}

	public void setTimesheetId(Integer timesheetId) {
		this.timesheetId = timesheetId;
	}
}
