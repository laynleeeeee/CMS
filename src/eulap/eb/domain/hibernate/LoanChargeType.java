package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object representation class for LOAN_CHARGE_TYPE table

 */


@Entity
@Table(name = "LOAN_CHARGE_TYPE")
public class LoanChargeType extends BaseDomain {
	private String name;
	private boolean active;

	public enum FIELD {
		id, name, active
	}

	public static final int INTEREST = 1;
	public static final int OTHER_CHARGES = 2;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "LOAN_CHARGE_TYPE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoanChargeType [name=").append(name).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
