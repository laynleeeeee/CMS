package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of CASH_SALE_TYPE table.

 */
@Entity
@Table(name="CASH_SALE_TYPE")
public class CashSaleType extends BaseDomain {
	private String name;
	private boolean active;

	public static final int RETAIL = 1;
	public static final int INDIV_SELECTION = 3;
	public static final int PROCESSING = 6;

	public enum FIELD { id, name, active }

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CASH_SALE_TYPE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	/**
	 * Get the name of the cash sale type.
	 * @return The name of the cash sale type.
	 */
	@Column(name="NAME", columnDefinition="varchar(20)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the status of the cash sale type.
	 * @return The status of the cash sale type.
	 */
	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CashSaleType [name=").append(name).append(", active=")
				.append(active).append(", getId()=").append(getId())
				.append("]");
		return builder.toString();
	}
}
