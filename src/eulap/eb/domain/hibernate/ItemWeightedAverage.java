package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object representation class for ITEM_WEIGHTED_AVERAGE table

 */

@Entity
@Table(name="ITEM_WEIGHTED_AVERAGE")
public class ItemWeightedAverage extends BaseDomain {
	private Integer warehouseId;
	private Integer itemId;
	private double quantity;
	private double weightedAve;
	private double totalAmount;

	public enum FIELD {
		id, warehouseId, itemId, weightedAve, updatedBy, updatedDate
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_WEIGHTED_AVERAGE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public void setCreatedDate(Date createdDate) {
		super.setCreatedDate(createdDate);
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
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
	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	@Column(name = "WEIGHTED_AVERAGE")
	public double getWeightedAve() {
		return weightedAve;
	}

	public void setWeightedAve(double weightedAve) {
		this.weightedAve = weightedAve;
	}

	@Transient
	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemWeightedAverage [warehouseId=").append(warehouseId).append(", itemId=").append(itemId)
				.append(", quantity=").append(quantity).append(", weightedAve=").append(weightedAve).append("]");
		return builder.toString();
	}
}
