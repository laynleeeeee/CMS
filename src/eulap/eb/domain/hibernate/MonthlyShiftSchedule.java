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
import eulap.eb.web.dto.MonthlyShiftScheduleDto;

@Entity
@Table(name = "MONTHLY_SHIFT_SCHEDULE")
public class MonthlyShiftSchedule extends BaseDomain {
	private Integer companyId;
	private Integer payrollTimePeriodId;
	private Integer payrollTimePeriodScheduleId;
	private Company company;
	private Integer month;
	private Integer year;
	private String employeeShiftScheduleJson;
	private PayrollTimePeriod payrollTimePeriod;
	private PayrollTimePeriodSchedule payrollTimePeriodSchedule;
	private List<MonthlyShiftScheduleDto> monthlyShiftScheduleDtos;
	private List<MonthlyShiftScheduleLine> monthlyShiftScheduleLines;
	private Boolean active;

	public enum FIELD {
		id, companyId, payrollTimePeriodId, payrollTimePeriodScheduleId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "MONTHLY_SHIFT_SCHEDULE_ID", columnDefinition="int(10", unique = true, nullable = false, insertable = false, updatable = false)
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
	public String getEmployeeTimeScheduleJson() {
		return employeeShiftScheduleJson;
	}

	public void setEmployeeTimeScheduleJson(String employeeShiftScheduleJson) {
		this.employeeShiftScheduleJson = employeeShiftScheduleJson;
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
	public String getEmployeeShiftScheduleJson() {
		return employeeShiftScheduleJson;
	}

	public void setEmployeeShiftScheduleJson(String employeeShiftScheduleJson) {
		this.employeeShiftScheduleJson = employeeShiftScheduleJson;
	}

	@Transient
	public List<MonthlyShiftScheduleDto> getMonthlyShiftScheduleDtos() {
		return monthlyShiftScheduleDtos;
	}

	public void setMonthlyShiftScheduleDtos(List<MonthlyShiftScheduleDto> monthlyShiftScheduleDtos) {
		this.monthlyShiftScheduleDtos = monthlyShiftScheduleDtos;
	}
	
	@Transient
	public void serializeMonthlySchedDTO() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		employeeShiftScheduleJson = gson.toJson(monthlyShiftScheduleDtos);
	}

	public void deSerializeMonthlySchedDTO() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<MonthlyShiftScheduleDto>>(){}.getType();
		monthlyShiftScheduleDtos = gson.fromJson(employeeShiftScheduleJson, type);
	}

	@Transient
	public List<MonthlyShiftScheduleLine> getMonthlyShiftScheduleLines() {
		return monthlyShiftScheduleLines;
	}

	public void setMonthlyShiftScheduleLines(List<MonthlyShiftScheduleLine> monthlyShiftScheduleLines) {
		this.monthlyShiftScheduleLines = monthlyShiftScheduleLines;
	}

	@Column(name = "ACTIVE", columnDefinition="TINYINT(1)")
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "MonthlyShiftSchedule [companyId=" + companyId + ", payrollTimePeriodId=" + payrollTimePeriodId
				+ ", payrollTimePeriodScheduleId=" + payrollTimePeriodScheduleId + ", company=" + company + ", month="
				+ month + ", year=" + year + ", employeeShiftScheduleJson=" + employeeShiftScheduleJson
				+ ", payrollTimePeriod=" + payrollTimePeriod + ", payrollTimePeriodSchedule="
				+ payrollTimePeriodSchedule + ", monthlyShiftScheduleDtos=" + monthlyShiftScheduleDtos
				+ ", monthlyShiftScheduleLines=" + monthlyShiftScheduleLines + ", active=" + active + "]";
	}

}
