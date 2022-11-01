package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import eulap.common.domain.BaseDomain;

/**
 * A base class that defines domain with item discount

 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public class BaseItemDiscount extends BaseDomain {
	private Integer itemId;
	private Integer itemDiscountTypeId;
	private Integer companyId;
	private String name;
	private Double value;
	private boolean active;
	private Item item;
	private ItemDiscountType itemDiscountType;
	private Company company;
	private String itemDiscountTypeName;
	private String companyName;

	@Column(name = "ITEM_ID", columnDefinition = "int(10)")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Column(name = "ITEM_DISCOUNT_TYPE_ID", columnDefinition = "int(10)")
	public Integer getItemDiscountTypeId() {
		return itemDiscountTypeId;
	}

	public void setItemDiscountTypeId(Integer itemDiscountTypeId) {
		this.itemDiscountTypeId = itemDiscountTypeId;
	}

	@Column(name = "COMPANY_ID", columnDefinition = "int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "NAME", columnDefinition = "varchar(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "VALUE", columnDefinition = "double")
	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ITEM_ID", insertable = false, updatable = false, nullable = true)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ITEM_DISCOUNT_TYPE_ID", insertable = false, updatable = false, nullable = true)
	public ItemDiscountType getItemDiscountType() {
		return itemDiscountType;
	}

	public void setItemDiscountType(ItemDiscountType itemDiscountType) {
		this.itemDiscountType = itemDiscountType;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false, nullable = true)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Transient
	public String getItemDiscountTypeName() {
		return itemDiscountTypeName;
	}

	public void setItemDiscountTypeName(String itemDiscountTypeName) {
		this.itemDiscountTypeName = itemDiscountTypeName;
	}

	@Transient
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BaseItemDiscount [itemId=").append(itemId)
				.append(", itemDiscountTypeId=").append(itemDiscountTypeId)
				.append(", companyId=").append(companyId).append(", name=")
				.append(name).append(", value=").append(value)
				.append(", active=").append(active).append("]");
		return builder.toString();
	}
}
