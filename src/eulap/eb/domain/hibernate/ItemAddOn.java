package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * Domain object of {@link ITEM_ADD_ON}

 *
 */
@Entity
@Table (name="ITEM_ADD_ON")
public class ItemAddOn extends BaseItemAddOn {
	private Integer itemAddOnTypeId;
	private ItemAddOnType itemAddOnType;
	private Double computedAddOn;

	public enum FIELD {
		id, itemId, companyId, name, value, active, item,
		itemDiscountTypeName, companyName, itemAddOnTypeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_ADD_ON_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "ITEM_ADD_ON_TYPE_ID", columnDefinition = "int(10)")
	public Integer getItemAddOnTypeId() {
		return itemAddOnTypeId;
	}

	public void setItemAddOnTypeId(Integer itemAddOnTypeId) {
		this.itemAddOnTypeId = itemAddOnTypeId;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ITEM_ADD_ON_TYPE_ID", insertable = false, updatable = false, nullable = true)
	public ItemAddOnType getItemAddOnType() {
		return itemAddOnType;
	}

	public void setItemAddOnType(ItemAddOnType itemAddOnType) {
		this.itemAddOnType = itemAddOnType;
	}

	@Transient
	public Double getComputedAddOn() {
		return computedAddOn;
	}

	public void setComputedAddOn(Double computedAddOn) {
		this.computedAddOn = computedAddOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemAddOn [getId()=").append(getId())
				.append(", getItemId()=").append(getItemId())
				.append(", getCompanyId()=").append(getCompanyId())
				.append(", getName()=").append(getName())
				.append(", getValue()=").append(getValue())
				.append(", isActive()=").append(isActive()).append("]");
		return builder.toString();
	}
}
