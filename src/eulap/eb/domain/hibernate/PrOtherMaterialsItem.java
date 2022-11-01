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
 * Domain class for PR_OTHER_MATERIALS_ITEM

 *
 */
@Entity
@Table(name="PR_OTHER_MATERIALS_ITEM")
public class PrOtherMaterialsItem extends ProcessingItem implements MillingItem{
	@Expose
	private Integer processingReportId;
	@Expose
	private Integer referenceObjectId;
	@Expose
	private Double itemBagQuantity;

	/**
	 * Object Type for {@link PrOtherMaterialsItem} = 6
	 */
	public static final int OBJECT_TYPE_ID = 6;

	public enum FIELD {
		id, cashSaleId, warehouseId, itemId, quantity, itemSrpId, srp, 
		itemDiscountId, unitCost, discount, amount, processingReportId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PR_OTHER_MATERIALS_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "PROCESSING_REPORT_ID")
	public Integer getProcessingReportId() {
		return processingReportId;
	}

	public void setProcessingReportId(Integer processingReportId) {
		this.processingReportId = processingReportId;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		// TODO Do nothing
		return false;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		//PR_OTHER_MATERIALS_ITEM type in OBJECT_TYPE table
		return 6;
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
	@Override
	public Double getItemBagQuantity() {
		return itemBagQuantity;
	}

	@Override
	public void setItemBagQuantity(Double itemBagQuantity) {
		this.itemBagQuantity = itemBagQuantity;
	}
}
