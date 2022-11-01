package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Object representation for WORK_ORDER_PURCHASED_ITEM table.

 *
 */
@Entity
@Table(name="WORK_ORDER_PURCHASED_ITEM")
public class WorkOrderPurchasedItem extends BaseFormLine {
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Integer workOrderId;
	@Expose
	private Integer warehouseId;
	@Expose
	private Integer itemId;
	private Warehouse warehouse;
	private Item item;
	@Expose
	private Integer origWarehouseId;
	@Expose
	private Double existingStocks;
	@Expose
	private String stockCode;
	@Expose
	private Double quantity;

	public static final int OBJECT_TYPE_ID = 12028;

	public enum FIELD {
		id, workOrderId, warehouseId, itemId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "WORK_ORDER_PURCHASED_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setRefenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Column(name = "WORK_ORDER_ID")
	public Integer getWorkOrderId() {
		return workOrderId;
	}

	public void setWorkOrderId(Integer workOrderId) {
		this.workOrderId = workOrderId;
	}

	@Column(name = "WAREHOUSE_ID")
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Column(name = "ITEM_ID")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Column(name = "QUANTITY")
	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@OneToOne
	@JoinColumn(name = "WAREHOUSE_ID", insertable=false, updatable=false)
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	@OneToOne
	@JoinColumn(name = "ITEM_ID", insertable=false, updatable=false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Transient
	public Integer getOrigWarehouseId() {
		return origWarehouseId;
	}

	public void setOrigWarehouseId(Integer origWarehouseId) {
		this.origWarehouseId = origWarehouseId;
	}

	@Transient
	public Double getExistingStocks() {
		return existingStocks;
	}

	public void setExistingStocks(Double existingStocks) {
		this.existingStocks = existingStocks;
	}

	@Transient
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkOrderPurchasedItem [refenceObjectId=").append(refenceObjectId).append(", workOrderId=")
				.append(workOrderId).append(", warehouseId=").append(warehouseId).append(", itemId=").append(itemId)
				.append(", warehouse=").append(warehouse).append(", item=").append(item).append(", origWarehouseId=")
				.append(origWarehouseId).append(", existingStocks=").append(existingStocks).append(", stockCode=")
				.append(stockCode).append(", quantity=").append(quantity).append("]");
		return builder.toString();
	}
}
