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
 * Object representation class for WORK_ORDER_ITEM table

 */

@Entity
@Table(name="WORK_ORDER_ITEM")
public class WorkOrderItem extends BaseFormLine {
	@Expose
	private Integer workOrderId;
	@Expose
	private Integer itemId;
	@Expose
	private String stockCode;
	@Expose
	private Double quantity;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Double existingStocks;
	private Item item;

	public enum FIELD {
		id, workOrderId, ebObjectId, itemId, quantity
	}

	public static final int OBJECT_TYPE_ID = 12015;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "WORK_ORDER_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "WORK_ORDER_ID")
	public Integer getWorkOrderId() {
		return workOrderId;
	}

	public void setWorkOrderId(Integer workOrderId) {
		this.workOrderId = workOrderId;
	}

	@Column(name = "ITEM_ID")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Transient
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	@Column(name = "QUANTITY")
	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@OneToOne
	@JoinColumn(name = "ITEM_ID", insertable=false, updatable=false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
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

	@Transient
	public Double getExistingStocks() {
		return existingStocks;
	}

	public void setExistingStocks(Double existingStocks) {
		this.existingStocks = existingStocks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkOrderItem [workOrderId=").append(workOrderId).append(", itemId=").append(itemId)
				.append(", stockCode=").append(stockCode).append(", quantity=").append(quantity)
				.append(", refenceObjectId=").append(refenceObjectId).append(", existingStocks=").append(existingStocks)
				.append(", item=").append(item).append("]");
		return builder.toString();
	}
}
