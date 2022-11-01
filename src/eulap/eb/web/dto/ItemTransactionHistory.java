package eulap.eb.web.dto;

import java.util.Date;

/**
 * A data transfer object class that show the current transaction of the item.
 * 

 *
 */
public class ItemTransactionHistory {
	private Integer ebObjectId;
	private Integer itemId;
	private Date date;
	private Date createdDate;
	private Integer warehouseId;
	private Double quantity;
	private Double unitCost;
	private Integer parentObjectId;

	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedtDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public Integer getParentObjectId() {
		return parentObjectId;
	}

	public void setParentObjectId(Integer parentObjectId) {
		this.parentObjectId = parentObjectId;
	}

	@Override
	public String toString() {
		return "ItemTransactionHistory [ebObjectId=" + ebObjectId + ", itemId=" + itemId + ", date=" + date
				+ ", currentDate=" + createdDate + ", warehouseId=" + warehouseId + ", quantity=" + quantity
				+ ", unitCost=" + unitCost + ", parentObjectId=" + parentObjectId + "]";
	}
}
