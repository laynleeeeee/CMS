package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Domain object of {@link ITEM_DISCOUNT}

 *
 */
@Entity
@Table (name="ITEM_DISCOUNT")
public class ItemDiscount extends BaseItemDiscount {

	public enum FIELD {
		id, itemId, itemDiscountTypeId, companyId, name, value, active, item,
		itemDiscountType, itemDiscountTypeName, companyName
	}
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_DISCOUNT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemDiscount [getItemId()=").append(getItemId())
				.append(", getItemDiscountTypeId()=")
				.append(getItemDiscountTypeId()).append(", getCompanyId()=")
				.append(getCompanyId()).append(", getName()=")
				.append(getName()).append(", getValue()=").append(getValue())
				.append(", isActive()=").append(isActive())
				.append(", getItemDiscountType()=")
				.append(getItemDiscountType()).append("]");
		return builder.toString();
	}
}
