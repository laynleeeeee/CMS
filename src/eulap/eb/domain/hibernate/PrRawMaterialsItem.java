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
 * Domain class for PR_RAW_MATERIALS_ITEM

 *
 */
@Entity
@Table(name="PR_RAW_MATERIALS_ITEM")
public class PrRawMaterialsItem extends ProcessingItem implements MillingItem{
	@Expose
	private Integer processingReportId;
	@Expose
	private Integer referenceObjectId;
	@Expose
	private Double itemBagQuantity;

	/**
	 * Object Type for {@link PrRawMaterialsItem} = 5
	 */
	public final static int OBJECT_TYPE_ID = 5;

	public static PrRawMaterialsItem getInstanceOf (ProductLineItem pli, double unitCost) {
		PrRawMaterialsItem prRm = new PrRawMaterialsItem();
		prRm.setItemId(pli.getItemId());
		prRm.setWarehouseId(pli.getWarehouseId());
		prRm.setQuantity(pli.getQuantity());
		prRm.setUnitCost(unitCost);
		return prRm;
	}

	public static PrRawMaterialsItem getInstanceOf (ProductLineItem pli, Item item, Double originalQty) {
		PrRawMaterialsItem rawMaterialItem = new PrRawMaterialsItem();
		rawMaterialItem.setItemId(pli.getItemId());
		rawMaterialItem.setWarehouseId(pli.getWarehouseId());
		rawMaterialItem.setQuantity(pli.getQuantity());
		rawMaterialItem.setItem(item);
		rawMaterialItem.setStockCode(item.getStockCode());
		rawMaterialItem.setOrigQty(originalQty);
		return rawMaterialItem;
	}

	public enum FIELD {
		id,  warehouseId, itemId, quantity, unitCost, processingReportId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PR_RAW_MATERIALS_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
		// Do nothing
		return false;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
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
