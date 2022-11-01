package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of PAYROLL_TIME_PERIOD_SCHEDULE table.

 *
 */
@Entity
@Table(name = "PAYROLL_TIME_PERIOD_SCHEDULE")
public class PayrollTimePeriodSchedule extends BaseDomain{
	@Expose
	private int payrollTimePeriodId;
	@Expose
	private String name;
	@Expose
	private Date dateFrom;
	@Expose
	private Date dateTo;
	@Expose
	private boolean computeContributions;

	public enum Field {
		id, payrollTimePeriodId, name, dateFrom, dateTo, computeContributions
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PAYROLL_TIME_PERIOD_SCHEDULE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "PAYROLL_TIME_PERIOD_ID", columnDefinition = "int(10)")
	public int getPayrollTimePeriodId() {
		return payrollTimePeriodId;
	}

	public void setPayrollTimePeriodId(int payrollTimePeriodId) {
		this.payrollTimePeriodId = payrollTimePeriodId;
	}

	@Column(name = "NAME", columnDefinition = "varchar(25)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DATE_FROM", columnDefinition = "date")
	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	@Column(name = "DATE_TO", columnDefinition = "date")
	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	@Column(name = "COMPUTE_CONTRIBUTIONS", columnDefinition = "tinyint(1))")
	public boolean isComputeContributions() {
		return computeContributions;
	}

	public void setComputeContributions(boolean contributions) {
		this.computeContributions = contributions;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PayrollTimePeriodSchedule [payrollTimePeriodId=")
				.append(payrollTimePeriodId).append(", name=").append(name)
				.append(", dateFrom=").append(dateFrom).append(", dateTo=")
				.append(dateTo).append(", computeContributions=")
				.append(computeContributions).append("]");
		return builder.toString();
	}

}
