package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object of {@link ITEM_DISCOUNT_TYPE}

 *
 */
@Entity
@Table (name="ITEM_DISCOUNT_TYPE")
public class ItemDiscountType extends BaseDomain{
	private String name;
	
	public enum FIELD {
		id, name
	}
	
	public static final int DISCOUNT_TYPE_PERCENTAGE = 1;
	public static final int DISCOUNT_TYPE_AMOUNT = 2;
	public static final int DISCOUNT_TYPE_QUANITY = 3;
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_DISCOUNT_TYPE_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
