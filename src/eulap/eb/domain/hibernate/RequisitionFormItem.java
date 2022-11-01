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
 * Object representation for REQUISITION_FORM_ITEM table.

 */
@Entity
@Table(name="REQUISITION_FORM_ITEM")
public class RequisitionFormItem extends BaseItem {
	@Expose
	private Integer warehouseId;
	private Warehouse warehouse;
	@Expose
	private Integer refenceObjectId;

	public static final int OBJECT_TYPE_ID = 3003;

	public enum FIELD {
		id, itemId, warehouseId, ebObjectId, quantity
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "REQUISITION_FORM_ITEM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="WAREHOUSE_ID", columnDefinition="int(10)")
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@OneToOne
	@JoinColumn(name = "WAREHOUSE_ID", insertable=false, updatable=false)
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
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

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequisitionFormItem [warehouseId=").append(warehouseId)
		.append(", itemId=").append(getItemId())
		.append(", ebObjectId=").append(getEbObjectId())
		.append(", quantity=").append(getQuantity()).append("]");
		return builder.toString();
	}
}
