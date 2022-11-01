package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;


/**
 * A class that define items for sales.

 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SaleItem extends BaseItem {
	@Expose
	private Integer itemDiscountId;
	@Expose
	private Double refQuantity;
	@Expose
	private Integer warehouseId;
	@Expose
	private Integer itemSrpId;
	@Expose
	private Double srp;
	@Expose
	private Double discount;
	@Expose
	private Double amount;
	@Expose
	private Integer itemAddOnId;
	@Expose 
	private Double addOn;
	@Expose
	private Double origSrp;
	private ItemDiscount itemDiscount;
	private ItemAddOn itemAddOn;
	private ItemSrp itemSrp;
	private Warehouse warehouse;
	@Expose
	private Double grossAmount;
	@Expose
	private Double netAmount;
	@Expose
	private Integer origWarehouseId;

	/**
	 * Get the item discount id.
	 * @return The item discount id.
	 */
	@Column(name = "ITEM_DISCOUNT_ID")
	public Integer getItemDiscountId() {
		return itemDiscountId;
	}

	public void setItemDiscountId(Integer itemDiscountId) {
		this.itemDiscountId = itemDiscountId;
	}

	@Transient
	public Double getRefQuantity() {
		return refQuantity;
	}

	public void setRefQuantity(Double refQuantity) {
		this.refQuantity = refQuantity;
	}
	
	/**
	 * Get the ware house id.
	 * @return The ware house id.
	 */
	@Column(name = "WAREHOUSE_ID")
	public Integer getWarehouseId() {
		return warehouseId;
	}
	
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	/**
	 * Get the item srp id.
	 * @return The item srp id.
	 */
	@Column(name = "ITEM_SRP_ID")
	public Integer getItemSrpId() {
		return itemSrpId;
	}
	
	public void setItemSrpId(Integer itemSrpId) {
		this.itemSrpId = itemSrpId;
	}
	
	/**
	 * Get the srp.
	 * @return The srp.
	 */
	@Column(name = "SRP")
	public Double getSrp() {
		return srp;
	}
	
	public void setSrp(Double srp) {
		this.srp = srp;
	}

	/**
	 * Get the discount.
	 * @return The discount.
	 */
	@Column(name = "DISCOUNT")
	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	
	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	@Column(name = "ITEM_ADD_ON_ID")
	public Integer getItemAddOnId() {
		return itemAddOnId;
	}
	
	public void setItemAddOnId(Integer itemAddOnId) {
		this.itemAddOnId = itemAddOnId;
	}
	
	@Transient
	public Double getAddOn() {
		return addOn;
	}
	
	public void setAddOn(Double addOn) {
		this.addOn = addOn;
	}
	
	@Transient
	public Double getOrigSrp() {
		return origSrp;
	}
	
	public void setOrigSrp(Double origSrp) {
		this.origSrp = origSrp;
	}
	
	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name = "ITEM_DISCOUNT_ID", insertable=false, updatable=false, nullable=true)
	public ItemDiscount getItemDiscount() {
		return itemDiscount;
	}
	
	public void setItemDiscount(ItemDiscount itemDiscount) {
		this.itemDiscount = itemDiscount;
	}
	
	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name = "ITEM_ADD_ON_ID", insertable=false, updatable=false, nullable=true)
	public ItemAddOn getItemAddOn() {
		return itemAddOn;
	}
	
	public void setItemAddOn(ItemAddOn itemAddOn) {
		this.itemAddOn = itemAddOn;
	}

	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name = "ITEM_SRP_ID", insertable=false, updatable=false, nullable=true)
	public ItemSrp getItemSrp() {
		return itemSrp;
	}

	public void setItemSrp(ItemSrp itemSrp) {
		this.itemSrp = itemSrp;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		if (!(itemLine instanceof SaleItem))
			return false;
		SaleItem si = (SaleItem) itemLine;
		return getItemId() == itemLine.getItemId() &&
				itemDiscountId == si.itemDiscountId &&
				itemSrpId == si.itemSrpId;
	}

	@ManyToOne
	@JoinColumn (name = "WAREHOUSE_ID", insertable=false, updatable=false)
	public Warehouse getWarehouse() {
		return warehouse;
	}
	
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	@Transient
	public Double getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(Double grossAmount) {
		this.grossAmount = grossAmount;
	}

	@Transient
	public Double getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(Double netAmount) {
		this.netAmount = netAmount;
	}

	@Transient
	public Integer getOrigWarehouseId() {
		return origWarehouseId;
	}

	public void setOrigWarehouseId(Integer origWarehouseId) {
		this.origWarehouseId = origWarehouseId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SaleItem [itemDiscountId=").append(itemDiscountId).append(", refQuantity=").append(refQuantity)
				.append(", warehouseId=").append(warehouseId).append(", itemSrpId=").append(itemSrpId).append(", srp=")
				.append(srp).append(", discount=").append(discount).append(", amount=").append(amount)
				.append(", itemAddOnId=").append(itemAddOnId).append(", addOn=").append(addOn).append(", origSrp=")
				.append(origSrp).append(", grossAmount=").append(grossAmount).append(", netAmount=").append(netAmount)
				.append(", origWarehouseId=").append(origWarehouseId).append("]");
		return builder.toString();
	}
}
