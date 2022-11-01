package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;


/**
 * A class that define items for processing.

 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public class ProcessingItem extends BaseItem{
	@Expose
	private Double refQuantity;
	@Expose
	private Integer warehouseId;
	@Expose
	private Warehouse warehouse;
	@Expose
	private Double availableStocks;
	@Expose
	private Integer refId;
	private String source;

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

	@ManyToOne
	@JoinColumn (name = "WAREHOUSE_ID", insertable=false, updatable=false)
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	@Transient
	public Double getAvailableStocks() {
		return availableStocks;
	}

	public void setAvailableStocks(Double availableStocks) {
		this.availableStocks = availableStocks;
	}

	@Transient
	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

	@Transient
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessingItem [refQuantity=").append(refQuantity)
				.append(", warehouseId=").append(warehouseId)
				.append(", warehouse=").append(warehouse).append("]");
		return builder.toString();
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		return false;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		throw new RuntimeException("Assign Object Type Id");
	}
}
