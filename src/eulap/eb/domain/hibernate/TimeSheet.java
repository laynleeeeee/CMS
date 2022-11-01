package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.validation.BindingResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;
import eulap.eb.web.dto.webservice.TimeSheetDto;
import eulap.eb.web.dto.webservice.TimeSheetViewDto;

/**
 * Object representation for TIME_SHEET table.

 *
 */
@Entity
@Table(name="TIME_SHEET")
public class TimeSheet extends BaseFormWorkflow {
	private Integer companyId;
	private Integer divisionId;
	private Integer payrollTimePeriodId;
	private Integer payrollTimePeriodScheduleId;
	private Integer biometricModelId;
	private Integer sequenceNumber;
	private Date date;
	private String timeSheetDtoJson;
	private List<PayrollEmployeeTimeSheet> employeeTimeSheets;
	private List<TimeSheetViewDto> timeSheetViewDtos;
	private List<TimeSheetDto> timeSheetDtos;
	private Date dateFrom;
	private Date dateTo;
	private String selectTPSched;
	private BindingResult result;
	private Company company;
	private Division division;
	private PayrollTimePeriod payrollTimePeriod;
	private PayrollTimePeriodSchedule payrollTimePeriodSchedule;
	private List<Date> tSDateTitles;

	/**
	 * Object Type for {@link TimeSheet}
	 */
	public static final int OBJECT_TYPE_ID = 73;

	public enum FIELD {id, companyId, divisionId, employeeTypeId, sequenceNumber, date,
		formWorkflowId, payrollTimePeriodId, payrollTimePeriodScheduleId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "TIME_SHEET_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "int(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "timestamp")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "int(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "timestamp")
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

	@Column(name = "BIOMETRIC_MODEL_ID", columnDefinition = "INT(10)")
	public Integer getBiometricModelId() {
		return biometricModelId;
	}

	public void setBiometricModelId(Integer biometricModelId) {
		this.biometricModelId = biometricModelId;
	}

	@Column(name = "SEQUENCE_NO", columnDefinition = "INT(10)")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Column(name = "DATE", columnDefinition = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Transient
	public String getTimeSheetDtoJson() {
		return timeSheetDtoJson;
	}

	public void setTimeSheetDtoJson(String timeSheetDtoJson) {
		this.timeSheetDtoJson = timeSheetDtoJson;
	}

	@Transient
	public List<PayrollEmployeeTimeSheet> getEmployeeTimeSheets() {
		return employeeTimeSheets;
	}

	public void setEmployeeTimeSheets(List<PayrollEmployeeTimeSheet> employeeTimeSheets) {
		this.employeeTimeSheets = employeeTimeSheets;
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

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (employeeTimeSheets != null) {
			children.addAll(employeeTimeSheets);
		}
		return children;
	}

	@Transient
	public BindingResult getResult() {
		return result;
	}

	public void setResult(BindingResult result) {
		this.result = result;
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

	public void setPayrollTimePeriodSchedule(PayrollTimePeriodSchedule payrollTimePeriodSchedule) {
		this.payrollTimePeriodSchedule = payrollTimePeriodSchedule;
	}

	@Transient
	public List<Date> gettSDateTitles() {
		return tSDateTitles;
	}

	public void settSDateTitles(List<Date> tSDateTitles) {
		this.tSDateTitles = tSDateTitles;
	}

	@Override
	public String toString() {
		return "TimeSheet [companyId=" + companyId + ", divisionId=" + divisionId + ", payrollTimePeriodId="
				+ payrollTimePeriodId + ", payrollTimePeriodScheduleId=" + payrollTimePeriodScheduleId
				+ ", sequenceNumber=" + sequenceNumber + ", ebObjectId=" + getEbObjectId() + ", date=" + date + ", ebObject="
				+ getEbObject() + ", timeSheetDtoJson=" + timeSheetDtoJson + ", employeeTimeSheets=" + employeeTimeSheets
				+ ", timeSheetViewDtos=" + timeSheetViewDtos + ", timeSheetDtos=" + timeSheetDtos + ", dateFrom="
				+ dateFrom + ", dateTo=" + dateTo + ", selectTPSched=" + selectTPSched + "]";
	}
}
