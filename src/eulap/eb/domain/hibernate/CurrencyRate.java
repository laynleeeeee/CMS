package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation for CURRENCY_RATE table

 *
 */

@Entity
@Table(name="CURRENCY_RATE")
public class CurrencyRate extends BaseDomain{
	private Date date;
	private Double rate;
	private Integer currencyId;
	private boolean active;
	private String time;
	private Currency currency;

	public enum FIELD {
		id, date, rate, currencyId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CURRENCY_RATE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "CURRENCY_ID", columnDefinition = "int(10)")
	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	@Column(name = "DATE", columnDefinition = "timestamp")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "RATE", columnDefinition = "double")
	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@OneToOne
	@JoinColumn(name = "CURRENCY_ID", updatable = false, insertable = false)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CurrencyRate [date=").append(date).append(", rate=").append(rate).append(", currencyId=")
				.append(currencyId).append(", active=").append(active).append(", time=").append(time).append("]");
		return builder.toString();
	}
}
