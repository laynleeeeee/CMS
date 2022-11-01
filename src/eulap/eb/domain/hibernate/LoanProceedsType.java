package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation for loan proceeds type table.

 */

@Entity
@Table(name = "LOAN_PROCEEDS_TYPE")
public class LoanProceedsType extends BaseDomain {
	private String name;
	private boolean active;

	public enum FIELD {
		id, name, active
	}

	public static final int LPT_CENTRAL = 1;
	public static final int LPT_NSB3 = 2;
	public static final int LPT_NSB4 = 3;
	public static final int LPT_NSB5 = 4;
	public static final int LPT_NSB8 = 5;
	public static final int LPT_NSB8A = 6;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "LOAN_PROCEEDS_TYPE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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
		builder.append("LoanProceedsType [name=").append(name).append(", active=").append(active).append("]");
		return builder.toString();
	}

}
