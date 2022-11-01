package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import eulap.common.domain.BaseDomain;
/**
 * A class that represent BUS_REG_TYPE table.

 */
@Entity
@Table (name="BUS_REG_TYPE")
public class BusinessRegistrationType extends BaseDomain{
	private String name;

	public enum FIELD {
		id, name
	}

	public final static int BUS_REG_TYPE_NON_VAT = 1;
	public final static int BUS_REG_TYPE_VAT_IN = 2;
	public final static int BUS_REG_TYPE_VAT_EX = 3;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "BUS_REG_TYPE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the name of Business Registration Type.
	 * @return The name.
	 */
	@Column (name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of Business Registration Type.
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "BusinessRegistrationType [name=" + name + ", getId()="
				+ getId() + "]";
	}
}
