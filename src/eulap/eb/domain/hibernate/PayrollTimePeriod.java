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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of PAYROLL_TIME_PERIOD table.

 *
 */

@Entity
@Table(name = "PAYROLL_TIME_PERIOD")
public class PayrollTimePeriod  extends BaseDomain {
	private String name;
	private int month;
	private int year;
	private boolean active;
	private List<PayrollTimePeriodSchedule> payrollTimePeriodSchedules;
	private String payrollTimeScheduleJson;

	public static final int MAX_HEADER_NAME = 50;
	public static final int MAX_LINE_NAME = 25;

	public enum FIELD {
		id, name, month, year, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PAYROLL_TIME_PERIOD_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "NAME", columnDefinition = "varchar(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "MONTH", columnDefinition = "int(2)")
	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}
	
	@Column(name = "YEAR", columnDefinition = "int(4)")
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public String getPayrollTimeScheduleJson() {
		return payrollTimeScheduleJson;
	}

	public void setPayrollTimeScheduleJson(String payrollTimeScheduleJson) {
		this.payrollTimeScheduleJson = payrollTimeScheduleJson;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name = "PAYROLL_TIME_PERIOD_ID", insertable = false, updatable = false)
	public List<PayrollTimePeriodSchedule> getPayrollTimePeriodSchedules() {
		return payrollTimePeriodSchedules;
	}
	
	public void setPayrollTimePeriodSchedules(List<PayrollTimePeriodSchedule> payrollTimePeriodSchedules) {
		this.payrollTimePeriodSchedules = payrollTimePeriodSchedules;
	}

	@Transient
	public void serializePayrollTimeSchedule (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		payrollTimeScheduleJson = gson.toJson(payrollTimePeriodSchedules);
	}

	@Transient
	public void deserializePayrollTimeSchedule () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<PayrollTimePeriodSchedule>>(){}.getType();
		payrollTimePeriodSchedules = gson.fromJson(payrollTimeScheduleJson, type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PayrollTimePeriod [name=").append(name)
				.append(", month=").append(month).append(", year=")
				.append(year).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
