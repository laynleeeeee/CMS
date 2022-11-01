package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Class representation of ITEM_BUYING_DISCOUNT table in the database.

 * 
 */
@Entity
@Table(name = "ITEM_BUYING_DISCOUNT")
public class ItemBuyingDiscount extends BaseItemDiscount {

	public enum FIELD {
		id, itemId, companyId, discountTypeId, name, value, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_BUYING_DISCOUNT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemBuyingDiscount [getId()=").append(getId())
				.append(", getItemId()=").append(getItemId())
				.append(", getItemDiscountTypeId()=")
				.append(getItemDiscountTypeId()).append(", getCompanyId()=")
				.append(getCompanyId()).append(", getValue()=")
				.append(getValue()).append(", isActive()=").append(isActive())
				.append("]");
		return builder.toString();
	}
}
