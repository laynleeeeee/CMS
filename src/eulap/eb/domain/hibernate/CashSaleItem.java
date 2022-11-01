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
 * Domain class for CASH_SALE_ITEM

 *
 */
@Entity
@Table(name="CASH_SALE_ITEM")
public class CashSaleItem extends SaleItem implements MillingItem {
	@Expose
	private Integer cashSaleId;
	private Double totalQty;
	@Expose
	private Integer referenceObjectId;
	private Integer productLineId;
	@Expose
	private Double itemBagQuantity;

	/**
	 * Object Type for Cash Sale Item = 12.
	 */
	public static final int OBJECT_TYPE_ID = 12;

	/**
	 * Cash-Sales-Relationship
	 */
	public static final int CSI_OR_TYPE_ID = 4;

	public enum FIELD {
		id, cashSaleId, warehouseId, itemId, quantity, itemSrpId, srp, 
		itemDiscountId, unitCost, discount, amount, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "CASH_SALE_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the cash sales id.
	 * @return The cash sales id.
	 */
	@Column(name = "CASH_SALE_ID")
	public Integer getCashSaleId() {
		return cashSaleId;
	}

	public void setCashSaleId(Integer cashSaleId) {
		this.cashSaleId = cashSaleId;
	}

	@Override
	public String toString() {
		return "CashSaleItem [cashSaleId=" + cashSaleId + ", itemId=" + getItemId() + ", warehouseId="
				+ getWarehouseId() + ", itemSrpId=" + getItemSrpId() + ", srp=" + getSrp()
				+ ", itemDiscountId=" + getItemDiscountId() + ", discount=" + getDiscount() 
				+ ", amount=" + getAmount() + "]";
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
	public Integer getProductLineId() {
		return productLineId;
	}

	public void setProductLineId(Integer productLineId) {
		this.productLineId = productLineId;
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
}
