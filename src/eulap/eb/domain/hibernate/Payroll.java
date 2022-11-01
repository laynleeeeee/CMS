package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;
import eulap.eb.web.dto.webservice.EmployeeSalaryDTO;
import eulap.eb.web.dto.webservice.TimeSheetDto;
import eulap.eb.web.dto.webservice.TimeSheetViewDto;
/**
 * An Object representation of Payroll. 

 */
@Entity
@Table (name="PAYROLL")
public class Payroll extends BaseFormWorkflow {
	private Integer companyId;
	private Integer divisionId;
	private Integer payrollTimePeriodId;
	private Integer payrollTimePeriodScheduleId;
	private Integer employeeTypeId;
	private Integer sequenceNumber;
	private Integer timeSheetId;
	private Integer biometricModelId;
	private Date date;
	private ReferenceDocument referenceDocument;
	private String fileData;
	private List<PayrollEmployeeSalary> payrollEmployeeSalaries;
	private String payrollEmployeeSalaryJson;
	private List<PayrollEmployeeTimeSheet> employeeTimeSheets;
	private String timeSheetDtoJson;
	private Company company;
	private Division division;
	private EmployeeType employeeType;
	private BiometricModel biometricModel;
	private List<Date> tSDateTitles;
	private List<TimeSheetViewDto> timeSheetViewDtos;
	private List<TimeSheetDto> timeSheetDtos;
	private String employeeSalaryDTOJson;
	private List<EmployeeSalaryDTO> employeeSalaryDTOs;
	private String file;
	private String fileName;
	private Double fileSize;
	private String description;
	private PayrollTimePeriod payrollTimePeriod;
	private PayrollTimePeriodSchedule payrollTimePeriodSchedule;
	private Date dateFrom;
	private Date dateTo;
	private String selectTPSched;
	private List<EmployeeDeduction> employeeDeductions;

	public enum FIELD {id, companyId, employeeTypeId, sequenceNumber, date,
		formWorkflowId, payrollTimePeriodId, payrollTimePeriodScheduleId, divisionId, timeSheetId}

	/**
	 * Payroll object type = 31.
	 */
	public static final int OBJECT_TYPE = 31;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PAYROLL_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_ID", columnDefinition = "INT(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "DIVISION_ID", columnDefinition = "INT(10)")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column(name = "PAYROLL_TIME_PERIOD_ID", columnDefinition = "INT(10)")
	public Integer getPayrollTimePeriodId() {
		return payrollTimePeriodId;
	}

	public void setPayrollTimePeriodId(Integer payrollTimePeriodId) {
		this.payrollTimePeriodId = payrollTimePeriodId;
	}

	@Column(name = "PAYROLL_TIME_PERIOD_SCHEDULE_ID", columnDefinition = "INT(10)")
	public Integer getPayrollTimePeriodScheduleId() {
		return payrollTimePeriodScheduleId;
	}

	public void setPayrollTimePeriodScheduleId(
			Integer payrollTimePeriodScheduleId) {
		this.payrollTimePeriodScheduleId = payrollTimePeriodScheduleId;
	}

	@Column(name = "EMPLOYEE_TYPE_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeTypeId() {
		return employeeTypeId;
	}

	public void setEmployeeTypeId(Integer employeeTypeId) {
		this.employeeTypeId = employeeTypeId;
	}

	@Column(name = "SEQUENCE_NO", columnDefinition = "INT(10)")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Column(name = "TIME_SHEET_ID", columnDefinition = "INT(10)")
	public Integer getTimeSheetId() {
		return timeSheetId;
	}

	public void setTimeSheetId(Integer timeSheetId) {
		this.timeSheetId = timeSheetId;
	}

	@Column(name = "BIOMETRIC_MODEL_ID", columnDefinition = "INT(10)")
	public Integer getBiometricModelId() {
		return biometricModelId;
	}

	public void setBiometricModelId(Integer biometricId) {
		this.biometricModelId = biometricId;
	}

	@Column(name = "DATE", columnDefinition = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Transient
	public ReferenceDocument getReferenceDocument() {
		return referenceDocument;
	}

	public void setReferenceDocument(ReferenceDocument referenceDocument) {
		this.referenceDocument = referenceDocument;
	}

	@OneToMany
	@JoinColumn(name = "PAYROLL_ID", insertable=false, updatable=false)
	public List<PayrollEmployeeSalary> getPayrollEmployeeSalaries() {
		return payrollEmployeeSalaries;
	}

	public void setPayrollEmployeeSalaries(List<PayrollEmployeeSalary> payrollEmployeeSalaries) {
		this.payrollEmployeeSalaries = payrollEmployeeSalaries;
	}

	@Transient
	public String getTimeSheetDtoJson() {
		return timeSheetDtoJson;
	}
	
	public void setTimeSheetDtoJson(String timeSheetDtoJson) {
		this.timeSheetDtoJson = timeSheetDtoJson;
	}

	@Transient
	public String getPayrollEmployeeSalaryJson() {
		return payrollEmployeeSalaryJson;
	}

	public void setPayrollEmployeeSalaryJson(String payrollEmployeeSalaryJson) {
		this.payrollEmployeeSalaryJson = payrollEmployeeSalaryJson;
	}

	@Transient
	public void serializePayrollEmployeeSalary() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		payrollEmployeeSalaryJson = gson.toJson(payrollEmployeeSalaries);
	}

	public void deSerializePayrollEmployeeSalary() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<PayrollEmployeeSalary>>(){}.getType();
		payrollEmployeeSalaries = gson.fromJson(payrollEmployeeSalaryJson, type);
	}

	@Transient
	public List<PayrollEmployeeTimeSheet> getEmployeeTimeSheets() {
		return employeeTimeSheets;
	}

	public void setEmployeeTimeSheets(List<PayrollEmployeeTimeSheet> employeeTimeSheets) {
		this.employeeTimeSheets = employeeTimeSheets;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (referenceDocument != null) {
			children.add(referenceDocument);
		}
		if (payrollEmployeeSalaries != null) {
			children.addAll(payrollEmployeeSalaries);
		}
		if (employeeDeductions != null) {
			children.addAll(employeeDeductions);
		}
		return children;
	}

	@Transient
	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	@Transient
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Transient
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@Transient
	public EmployeeType getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(EmployeeType employeeType) {
		this.employeeType = employeeType;
	}

	@Transient
	public BiometricModel getBiometricModel() {
		return biometricModel;
	}

	public void setBiometricModel(BiometricModel biometricModel) {
		this.biometricModel = biometricModel;
	}

	@Transient
	public List<Date> gettSDateTitles() {
		return tSDateTitles;
	}

	public void settSDateTitles(List<Date> tSDateTitles) {
		this.tSDateTitles = tSDateTitles;
	}

	@Transient
	public List<TimeSheetViewDto> getTimeSheetViewDtos() {
		return timeSheetViewDtos;
	}

	public void setTimeSheetViewDtos(List<TimeSheetViewDto> timeSheetViewDtos) {
		this.timeSheetViewDtos = timeSheetViewDtos;
	}

	@Transient
	public List<TimeSheetDto> getTimeSheetDtos() {
		return timeSheetDtos;
	}

	public void setTimeSheetDtos(List<TimeSheetDto> timeSheetDtos) {
		this.timeSheetDtos = timeSheetDtos;
	}

	@Transient
	public void serializeTimeSheetDto() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		timeSheetDtoJson = gson.toJson(timeSheetDtos);
	}

	public void deSerializeTimeSheetDto() {
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<TimeSheetDto>>(){}.getType();
		timeSheetDtos = gson.fromJson(timeSheetDtoJson, type);
	}

	@Transient
	public String getEmployeeSalaryDTOJson() {
		return employeeSalaryDTOJson;
	}

	public void setEmployeeSalaryDTOJson(String employeeSalaryDTOJson) {
		this.employeeSalaryDTOJson = employeeSalaryDTOJson;
	}

	@Transient
	public List<EmployeeSalaryDTO> getEmployeeSalaryDTOs() {
		return employeeSalaryDTOs;
	}

	public void setEmployeeSalaryDTOs(List<EmployeeSalaryDTO> employeeSalaryDTOs) {
		this.employeeSalaryDTOs = employeeSalaryDTOs;
	}

	@Transient
	public void serializeEmployeeSalaryDTO() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		employeeSalaryDTOJson = gson.toJson(employeeSalaryDTOs);
	}

	public void deSerializeEmployeeSalaryDTO() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<EmployeeSalaryDTO>>(){}.getType();
		employeeSalaryDTOs = gson.fromJson(employeeSalaryDTOJson, type);
	}

	@Transient
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Transient
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Transient
	public Double getFileSize() {
		return fileSize;
	}

	public void setFileSize(Double fileSize) {
		this.fileSize = fileSize;
	}

	@Transient
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public String getFormattedSequenceNumber() {
		if (company != null) {
			if (company.getCompanyCode() != null){
				String companyCode = company.getCompanyCode();
				return companyCode + " " + sequenceNumber;
			} else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + sequenceNumber;
			}
		}
		return null;
	}

	@Transient
	public PayrollTimePeriod getPayrollTimePeriod() {
		return payrollTimePeriod;
	}

	public void setPayrollTimePeriod(PayrollTimePeriod payrollTimePeriod) {
		this.payrollTimePeriod = payrollTimePeriod;
	}

	@Transient
	public PayrollTimePeriodSchedule getPayrollTimePeriodSchedule() {
		return payrollTimePeriodSchedule;
	}

	public void setPayrollTimePeriodSchedule(
			PayrollTimePeriodSchedule payrollTimePeriodSchedule) {
		this.payrollTimePeriodSchedule = payrollTimePeriodSchedule;
	}

	@Transient
	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	@Transient
	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	@Transient
	public String getSelectTPSched() {
		return selectTPSched;
	}

	public void setSelectTPSched(String selectTPSched) {
		this.selectTPSched = selectTPSched;
	}

	@Transient
	@Override
	public Date getGLDate() {
		return date;
	}

	@OneToMany
	@JoinColumn(name = "PAYROLL_ID", insertable=false, updatable=false)
	public List<EmployeeDeduction> getEmployeeDeductions() {
		return employeeDeductions;
	}

	public void setEmployeeDeductions(List<EmployeeDeduction> employeeDeductions) {
		this.employeeDeductions = employeeDeductions;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Payroll [companyId=").append(companyId)
				.append(", divisionId=").append(divisionId)
				.append(", payrollTimePeriodId=").append(payrollTimePeriodId)
				.append(", payrollTimePeriodScheduleId=")
				.append(payrollTimePeriodScheduleId)
				.append(", employeeTypeId=").append(employeeTypeId)
				.append(", sequenceNumber=").append(sequenceNumber)
				.append(", ebObjectId=").append(getEbObjectId())
				.append(", biometricModelId=").append(biometricModelId)
				.append(", date=").append(date).append(", fileData=")
				.append(fileData)
				.append(", payrollEmployeeSalaryJson=")
				.append(payrollEmployeeSalaryJson)
				.append(", employeeTimeSheets=").append(employeeTimeSheets)
				.append(", timeSheetDtoJson=").append(timeSheetDtoJson)
				.append(", biometricModel=").append(biometricModel)
				.append(", tSDateTitles=").append(tSDateTitles)
				.append(", timeSheetViewDtos=").append(timeSheetViewDtos)
				.append(", timeSheetDtos=").append(timeSheetDtos)
				.append(", employeeSalaryDTOJson=")
				.append(employeeSalaryDTOJson).append(", employeeSalaryDTOs=")
				.append(employeeSalaryDTOs).append(", file=").append(file)
				.append(", fileName=").append(fileName).append(", fileSize=")
				.append(fileSize).append(", description=").append(description)
				.append("]");
		return builder.toString();
	}
}
