package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Object representation of R_PURCHASE_ORDER_ITEM table.

 *
 */
@Entity
@Table(name = "R_PURCHASE_ORDER_ITEM")
public class RPurchaseOrderItem extends BaseItem {
	@Expose
	private Integer rPurchaseOrderId;
	@Expose
	private Integer refenceObjectId;
	private Double amount;

	public static final int PO_ITEM_OBJECT_TYPE_ID = 39;

	/**
	 * OR Type ID for Purchase Request Item-Purchase Order Item relationship: 3010
	 */
	public static final int PRI_POI_OR_TYPE_ID = 3010;

	public enum FIELD {
		id, rPurchaseOrderId, itemId, quantity, unitCost, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "R_PURCHASE_ORDER_ITEM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="R_PURCHASE_ORDER_ID", columnDefinition="int(10)")
	public Integer getrPurchaseOrderId() {
		return rPurchaseOrderId;
	}

	public void setrPurchaseOrderId(Integer rPurchaseOrderId) {
		this.rPurchaseOrderId = rPurchaseOrderId;
	}

	@Transient
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		return false;
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
	@Transient
	public Integer getObjectTypeId() {
		return PO_ITEM_OBJECT_TYPE_ID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RPurchaseOrderItem [rPurchaseOrderId=").append(rPurchaseOrderId)
				.append(", refenceObjectId=").append("]");
		return builder.toString();
	}
}
