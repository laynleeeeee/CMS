package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of TIME_PERIOD table

 */
@Entity
@Table(name = "TIME_PERIOD")
public class TimePeriod extends BaseDomain{
	private String name;
	private Date dateFrom;
	private Date dateTo;
	private int periodStatusId;
	private TimePeriodStatus periodStatus;

	public enum Field {id, name, dateFrom, dateTo, periodStatusId}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "TIME_PERIOD_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	/**
	 * Get the name of the time period.
	 * @return The name.
	 */
	@Column(name = "NAME", columnDefinition = "VARCHAR(50)")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the time period.
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the date when the time period starts.
	 * @return The start date.
	 */
	@Column(name = "DATE_FROM", columnDefinition = "DATE")
	public Date getDateFrom() {
		return dateFrom;
	}

	/**
	 * Set the date when the time period starts.
	 * @param dateFrom The start date.
	 */
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	/**
	 * Get the date when the time period ends.
	 * @return The end date.
	 */
	@Column(name = "DATE_TO", columnDefinition = "DATE")
	public Date getDateTo() {
		return dateTo;
	}

	/**
	 * Set the date when the time period ends.
	 * @param dateTo The end date.
	 */
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	/**
	 * Get the Id of the time period status associated with time period.
	 * @return The time period status Id.
	 */
	@Column(name = "TIME_PERIOD_STATUS_ID", columnDefinition = "INT(10)")
	public int getPeriodStatusId() {
		return periodStatusId;
	}

	/**
	 * Set the Id of the time period status associated with time period.
	 * @param periodStatusId The time period status Id.
	 */
	public void setPeriodStatusId(int periodStatusId) {
		this.periodStatusId = periodStatusId;
	}

	/**
	 * Get the time period status object.
	 * @return The time period status object.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TIME_PERIOD_STATUS_ID", insertable = false, updatable = false, nullable = false)
	public TimePeriodStatus getPeriodStatus() {
		return periodStatus;
	}

	/**
	 * Set the time period status object.
	 * @param periodStatus The time period status object.
	 */
	public void setPeriodStatus(TimePeriodStatus periodStatus) {
		this.periodStatus = periodStatus;
	}

	@Override
	public String toString() {
		return "TimePeriod [name=" + name + ", dateFrom=" + dateFrom
				+ ", dateTo=" + dateTo + ", periodStatusId=" + periodStatusId
				+ ", periodStatus=" + periodStatus + ", getId()=" + getId()
				+ ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()="
				+ getCreatedDate() + ", getUpdatedBy()=" + getUpdatedBy()
				+ ", getUpdatedDate()=" + getUpdatedDate() + "]";
	}
}