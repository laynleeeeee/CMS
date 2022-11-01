package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.common.domain.BaseDomain;
import eulap.eb.web.dto.DailyShiftScheduleDto;

@Entity
@Table(name = "DAILY_SHIFT_SCHEDULE")
public class DailyShiftSchedule extends BaseDomain {
	private Integer companyId;
	private Integer payrollTimePeriodId;
	private Integer payrollTimePeriodScheduleId;
	private Company company;
	private Integer month;
	private Integer year;
	private List<DailyShiftScheduleDto> scheduleSheetDtos;
	private String employeeTimeScheduleJson;
	private PayrollTimePeriod payrollTimePeriod;
	private PayrollTimePeriodSchedule payrollTimePeriodSchedule;
	private List<DailyShiftScheduleLine> shiftScheduleLines;
	private Integer divisionId;
	private Division division;
	private Integer ebObjectId;
	private EBObject ebObject;
	private ReferenceDocument referenceDocument;

	public enum FIELD {
		id, companyId, payrollTimePeriodId, payrollTimePeriodScheduleId, divisionId
	}

	public static final int OBJECT_TYPE_ID = 7000;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "DAILY_SHIFT_SCHEDULE_ID", columnDefinition="int(10", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_ID", columnDefinition = "int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "PAYROLL_TIME_PERIOD_ID", columnDefinition = "int(10)")
	public Integer getPayrollTimePeriodId() {
		return payrollTimePeriodId;
	}

	public void setPayrollTimePeriodId(Integer payrollTimePeriodId) {
		this.payrollTimePeriodId = payrollTimePeriodId;
	}

	@Column(name = "PAYROLL_TIME_PERIOD_SCHEDULE_ID", columnDefinition = "int(10)")
	public Integer getPayrollTimePeriodScheduleId() {
		return payrollTimePeriodScheduleId;
	}

	public void setPayrollTimePeriodScheduleId(Integer payrollTimePeriodScheduleId) {
		this.payrollTimePeriodScheduleId = payrollTimePeriodScheduleId;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Transient
	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	@Transient
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Transient
	public List<DailyShiftScheduleDto> getScheduleSheetDtos() {
		return scheduleSheetDtos;
	}

	public void setScheduleSheetDtos(List<DailyShiftScheduleDto> scheduleSheetDtos) {
		this.scheduleSheetDtos = scheduleSheetDtos;
	}

	@Transient
	public String getEmployeeTimeScheduleJson() {
		return employeeTimeScheduleJson;
	}

	public void setEmployeeTimeScheduleJson(String employeeTimeScheduleJson) {
		this.employeeTimeScheduleJson = employeeTimeScheduleJson;
	}

	@Transient
	public void serializeEmployeeTimeSchedDTO() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		employeeTimeScheduleJson = gson.toJson(scheduleSheetDtos);
	}

	public void deSerializeEmployeeTimeSchedDTO() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<DailyShiftScheduleDto>>(){}.getType();
		scheduleSheetDtos = gson.fromJson(employeeTimeScheduleJson, type);
	}

	@OneToOne
	@JoinColumn(name = "PAYROLL_TIME_PERIOD_ID", insertable = false, updatable = false)
	public PayrollTimePeriod getPayrollTimePeriod() {
		return payrollTimePeriod;
	}

	public void setPayrollTimePeriod(PayrollTimePeriod payrollTimePeriod) {
		this.payrollTimePeriod = payrollTimePeriod;
	}

	@OneToOne
	@JoinColumn(name = "PAYROLL_TIME_PERIOD_SCHEDULE_ID", insertable = false, updatable = false)
	public PayrollTimePeriodSchedule getPayrollTimePeriodSchedule() {
		return payrollTimePeriodSchedule;
	}

	public void setPayrollTimePeriodSchedule(
			PayrollTimePeriodSchedule payrollTimePeriodSchedule) {
		this.payrollTimePeriodSchedule = payrollTimePeriodSchedule;
	}

	@Transient
	public List<DailyShiftScheduleLine> getShiftScheduleLines() {
		return shiftScheduleLines;
	}

	public void setShiftScheduleLines(List<DailyShiftScheduleLine> shiftScheduleLines) {
		this.shiftScheduleLines = shiftScheduleLines;
	}

	@Column(name = "DIVISION_ID", columnDefinition = "int(10)")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@OneToOne
	@JoinColumn(name = "DIVISION_ID", insertable = false, updatable = false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@Column(name = "EB_OBJECT_ID", columnDefinition = "int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", nullable=true, insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Transient
	public ReferenceDocument getReferenceDocument() {
		return referenceDocument;
	}

	public void setReferenceDocument(ReferenceDocument referenceDocument) {
		this.referenceDocument = referenceDocument;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DailyShiftSchedule [companyId=").append(companyId).append(", payrollTimePeriodId=")
				.append(payrollTimePeriodId).append(", payrollTimePeriodScheduleId=")
				.append(payrollTimePeriodScheduleId).append(", company=").append(company).append(", month=")
				.append(month).append(", year=").append(year).append("]");
		return builder.toString();
	}
}
