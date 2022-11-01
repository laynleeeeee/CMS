package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object class representation for TAX_TYPE table

 */

@Entity
@Table(name="TAX_TYPE")
public class TaxType extends BaseDomain {
	private String name;
	private boolean active;

	public static final int VATABLE = 1;
	public static final int VAT_EXEMPTED = 2;
	public static final int ZERO_RATED = 3;
	public static final int GOODS = 4;
	public static final int SERVICES = 5;
	public static final int CAPITAL = 6;
	public static final int NRA = 7;
	public static final int PRIVATE = 8;
	public static final int GOVERNMENT = 9;
	public static final int IMPORTATION = 10;

	public enum FIELD {
		id, name, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "TAX_TYPE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "NAME", columnDefinition = "VARCHAR(20)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TaxType [name=").append(name).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
