package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the NORMAL_BALANCE table.

 *
 */
@Entity
@Table(name = "NORMAL_BALANCE")
public class NormalBalance extends BaseDomain {
	private String name;
	public static int DEBIT = 1;
	public static int CREDIT = 2;
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "NORMAL_BALANCE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the normal balance name.
	 * @return name The normal balance name.
	 */
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the normal balance name.
	 * @param name The normal balance name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "NormalBalance [name=" + name + ", getId()=" + getId() + "]";
	}
}
