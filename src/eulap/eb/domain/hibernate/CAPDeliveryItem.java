package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.eb.service.inventory.MillingItem;

/**
 * Object representation of CAP_DELIVERY_ITEM table.

 *
 */
@Entity
@Table(name="CAP_DELIVERY_ITEM")
public class CAPDeliveryItem extends SaleItem implements MillingItem {
	private Integer capDeliveryId;
	@Expose
	private Double existingStock;
	private Double totalQty;
	@Expose
	private Integer referenceObjectId;
	@Expose
	private Integer referenceId;
	@Expose
	private Double itemBagQuantity;
	@Expose
	private Integer capItemId;

	/**
	 * Objecte type id for {@link CAPDeliveryItem} = 54
	 */
	public static final int OBJECT_TYPE_ID = 54;

	public enum FIELD {
		id, capDeliveryId, warehouseId, itemId, quantity, itemSrpId, srp, 
		itemDiscountId, unitCost, discount, amount, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "CAP_DELIVERY_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="CAP_DELIVERY_ID", columnDefinition="int(10)")
	public Integer getCapDeliveryId() {
		return capDeliveryId;
	}

	public void setCapDeliveryId(Integer capDeliveryId) {
		this.capDeliveryId = capDeliveryId;
	}

	@Transient
	public Double getExistingStock() {
		return existingStock;
	}

	public void setExistingStock(Double existingStock) {
		this.existingStock = existingStock;
	}

	@Transient
	public Double getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(Double totalQty) {
		this.totalQty = totalQty;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}

	@Transient
	public Integer getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Integer referenceObjectId) {
		this.referenceId = referenceObjectId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CAPDeliveryItem [capDeliveryId=").append(capDeliveryId).append(", existingStock=")
				.append(existingStock).append(", totalQty=").append(totalQty).append(", ebObjectId=")
				.append(getEbObjectId()).append(", ebObject=").append(getEbObject()).append(", referenceObjectId=")
				.append(referenceObjectId).append(", getId()=").append(getId()).append(", getCapDeliveryId()=")
				.append(getCapDeliveryId()).append(", getItemDiscountId()=").append(getItemDiscountId())
				.append(", getWarehouseId()=").append(getWarehouseId()).append(", getItemSrpId()=")
				.append(getItemSrpId()).append(", getSrp()=").append(getSrp()).append(", getDiscount()=")
				.append(getDiscount()).append(", getAmount()=").append(getAmount()).append(", getItemAddOnId()=")
				.append(getItemAddOnId()).append(", getItemId()=").append(getItemId()).append(", getQuantity()=")
				.append(getQuantity()).append(", getUnitCost()=").append(getUnitCost()).append("]");
		return builder.toString();
	}

	@Transient
	@Override
	public Double getItemBagQuantity() {
		return itemBagQuantity;
	}

	@Override
	public void setItemBagQuantity(Double itemBagQuantity) {
		this.itemBagQuantity = itemBagQuantity;
	}

	@Transient
	public Integer getCapItemId() {
		return capItemId;
	}

	public void setCapItemId(Integer capItemId) {
		this.capItemId = capItemId;
	}
}
