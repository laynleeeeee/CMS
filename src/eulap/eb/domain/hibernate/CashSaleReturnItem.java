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
 * Domain class for CASH_SALE_RETURN_ITEM

 *
 */
@Entity
@Table(name="CASH_SALE_RETURN_ITEM")
public class CashSaleReturnItem extends SaleItem implements SalesReturnItem, MillingItem {
	@Expose
	private Integer cashSaleReturnId;
	@Expose
	private Integer cashSaleItemId;
	@Expose
	private Integer refCashSaleReturnItemId;
	@Expose
	private Integer referenceObjectId;
	@Expose
	private Integer salesRefId;
	@Expose
	private Double itemBagQuantity;
	@Expose
	private Double origBagQty;

	public static final int RETURN_OBJECT_TYPE_ID = 20;
	public static final int EXCHANGE_OBJECT_TYPE_ID = 21;

	public enum FIELD {
		id, cashSaleReturnId, cashSaleItemId, warehouseId, itemId, quantity, itemSrpId, srp, 
		itemDiscountId, unitCost, discount, amount
	}
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "CASH_SALE_RETURN_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the cash sales id.
	 * @return The cash sales id.
	 */
	@Column(name = "CASH_SALE_RETURN_ID")
	public Integer getCashSaleReturnId() {
		return cashSaleReturnId;
	}
	
	public void setCashSaleReturnId(Integer cashSaleReturnId) {
		this.cashSaleReturnId = cashSaleReturnId;
	}
	
	/**
	 * Get the cash sales item id.
	 * @return The cash sales item id.
	 */
	@Column(name = "CASH_SALE_ITEM_ID")
	public Integer getCashSaleItemId() {
		return cashSaleItemId;
	}
	
	public void setCashSaleItemId(Integer cashSaleItemId) {
		this.cashSaleItemId = cashSaleItemId;
	}

	@Column(name = "REF_CASH_SALE_RETURN_ITEM_ID")
	public Integer getRefCashSaleReturnItemId() {
		return refCashSaleReturnItemId;
	}

	public void setRefCashSaleReturnItemId(Integer refCashSaleReturnItemId) {
		this.refCashSaleReturnItemId = refCashSaleReturnItemId;
	}

	@Override
	@Transient
	public Integer getSalesReferenceId() {
		return cashSaleItemId != null && cashSaleItemId != 0 ? cashSaleItemId : refCashSaleReturnItemId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		if(super.getQuantity() < 0) {
			return RETURN_OBJECT_TYPE_ID;
		}
		return EXCHANGE_OBJECT_TYPE_ID;
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
	public Integer getSalesRefId() {
		return salesRefId;
	}

	public void setSalesRefId(Integer salesRefId) {
		this.salesRefId = salesRefId;
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
	public Double getOrigBagQty() {
		return origBagQty;
	}

	public void setOrigBagQty(Double origBagQty) {
		this.origBagQty = origBagQty;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CashSaleReturnItem [cashSaleReturnId=").append(cashSaleReturnId).append(", cashSaleItemId=")
				.append(cashSaleItemId).append(", refCashSaleReturnItemId=").append(refCashSaleReturnItemId)
				.append(", ebObjectId=").append(getEbObjectId()).append(", ebObject=").append(getEbObject())
				.append(", referenceObjectId=").append(referenceObjectId).append(", salesRefId=").append(salesRefId)
				.append(", itemBagQuantity=").append(itemBagQuantity).append(", origBagQty=").append(origBagQty)
				.append("]");
		return builder.toString();
	}
}
