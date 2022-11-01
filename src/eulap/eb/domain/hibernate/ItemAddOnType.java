package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object of ITEM_ADD_ON_TYPE table in the database.

 *
 */
@Entity
@Table (name="ITEM_ADD_ON_TYPE")
public class ItemAddOnType extends BaseDomain{
	private String name;

	public static final int TYPE_QUANTITY = 3;

	public enum FIELD {
		id, name
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_ADD_ON_TYPE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the name of item discount type.
	 * @return The name.
	 */
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ItemDiscountType [name=" + name + ", getId()=" + getId() + "]";
	}
}
