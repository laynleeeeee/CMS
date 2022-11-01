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
 * Object representation class for PURCHASE_REQUISITION_ITEM table.

 */

@Entity
@Table(name="PURCHASE_REQUISITION_ITEM")
public class PurchaseRequisitionItem extends BaseItem {
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Integer purchaseRequisitionId;

	public static final int PRI_OBJECT_TYPE_ID = 3036;
	public static final int RFI_PRI_OR_TYPE_ID = 3012;

	public enum FIELD {
		id, purchaseRequisitionId, itemId, ebObjectId, quantity
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PURCHASE_REQUISITION_ITEM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="PURCHASE_REQUISITION_ID", columnDefinition="int(10)")
	public Integer getPurchaseRequisitionId() {
		return purchaseRequisitionId;
	}

	public void setPurchaseRequisitionId(Integer purchaseRequisitionId) {
		this.purchaseRequisitionId = purchaseRequisitionId;
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
		return PRI_OBJECT_TYPE_ID;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		return false;
	}
}
