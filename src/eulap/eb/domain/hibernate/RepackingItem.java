package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.eb.service.inventory.InventoryItem;

/**
 * Object representation of REPACKING_ITEM table.

 *
 */
@Entity
@Table(name = "REPACKING_ITEM")
public class RepackingItem extends BaseFormLine implements InventoryItem {
	@Expose
	private Integer repackingId;
	@Expose
	private Integer fromItemId;
	@Expose
	private Integer toItemId;
	@Expose
	private Double quantity;
	@Expose
	private Double repackedQuantity;
	@Expose
	private String fromStockCode;
	@Expose
	private String toStockCode;
	@Expose
	private Double unitCost;
	@Expose
	private Double repackedUnitCost;
	private Item fromItem;
	private Item toItem;
	@Expose
	private Double fromExistingStock;
	@Expose
	private Double toExistingStock;
	private Double totalQty;
	@Expose
	private Double origQty;
	@Expose
	private Integer referenceObjectId;
	@Expose
	private String toDescription;
	@Expose
	private String fromDescription;
	@Expose
	private Integer origItemId;
	@Expose
	private Double origUnitCost;
	private Double fromTotal;
	private Double toTotal;
	@Expose
	private Double toOrigQty;

	public enum FIELD {
		id, repackingId, fromItemId, toItemId, quantity,
		repackedQuantity, unitCost
	}

	public static final int REPACKING_ITEM_OBJ_TYPE_ID = 60;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "REPACKING_ITEM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "REPACKING_ID", columnDefinition="int(10)")
	public Integer getRepackingId() {
		return repackingId;
	}

	public void setRepackingId(Integer repackingId) {
		this.repackingId = repackingId;
	}

	@Column(name = "FROM_ITEM_ID", columnDefinition="int(10)")
	public Integer getFromItemId() {
		return fromItemId;
	}

	public void setFromItemId(Integer fromItemId) {
		this.fromItemId = fromItemId;
	}

	@Column(name = "TO_ITEM_ID", columnDefinition="int(10)")
	public Integer getToItemId() {
		return toItemId;
	}

	public void setToItemId(Integer toItemId) {
		this.toItemId = toItemId;
	}

	@Column(name = "QUANTITY", columnDefinition="double")
	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Column(name = "REPACKED_QUANTITY", columnDefinition="double")
	public Double getRepackedQuantity() {
		return repackedQuantity;
	}

	public void setRepackedQuantity(Double repackedQuantity) {
		this.repackedQuantity = repackedQuantity;
	}

	@Column(name = "UNIT_COST", columnDefinition="double")
	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	@Column(name = "REPACKED_UNIT_COST", columnDefinition="double")
	public Double getRepackedUnitCost() {
		return repackedUnitCost;
	}

	public void setRepackedUnitCost(Double repackedUnitCost) {
		this.repackedUnitCost = repackedUnitCost;
	}

	@ManyToOne
	@JoinColumn(name = "FROM_ITEM_ID", insertable=false, updatable=false)
	public Item getFromItem() {
		return fromItem;
	}

	public void setFromItem(Item fromItem) {
		this.fromItem = fromItem;
	}

	@ManyToOne
	@JoinColumn(name = "TO_ITEM_ID", insertable=false, updatable=false)
	public Item getToItem() {
		return toItem;
	}

	public void setToItem(Item toItem) {
		this.toItem = toItem;
	}

	@Transient
	public String getFromStockCode() {
		return fromStockCode;
	}

	public void setFromStockCode(String fromStockCode) {
		this.fromStockCode = fromStockCode;
	}

	@Transient
	public String getToStockCode() {
		return toStockCode;
	}

	public void setToStockCode(String toStockCode) {
		this.toStockCode = toStockCode;
	}
	
	@Transient
	public Double getFromExistingStock() {
		return fromExistingStock;
	}
	
	public void setFromExistingStock(Double fromExistingStock) {
		this.fromExistingStock = fromExistingStock;
	}
	
	@Transient
	public Double getToExistingStock() {
		return toExistingStock;
	}
	
	public void setToExistingStock(Double toExistingStock) {
		this.toExistingStock = toExistingStock;
	}

	@Transient
	public Double getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(Double totalQty) {
		this.totalQty = totalQty;
	}
	
	@Transient
	public Double getOrigQty() {
		return origQty;
	}
	
	public void setOrigQty(Double origQty) {
		this.origQty = origQty;
	}

	@Transient
	@Override
	public Integer getItemId() {
		return fromItemId;
	}

	@Transient
	@Override
	public String getStockCode() {
		return fromStockCode;
	}

	@Transient
	@Override
	public Double getInventoryCost() {
		return unitCost;
	}

	@Override
	public void setInventoryCost(Double inventoryCost) {
		// do nothing.
	}

	@Transient
	@Override
	public boolean isIgnore() {
		// do nothing.
		return false;
	}

	@Override
	public void setIgnore(boolean isIgnore) {
		// do nothing for now. 
	}
	
	@Transient
	@Override
	public Integer getReceivedStockId() {
		// do nothing for now. 
		return null;
	}

	@Override
	public void setReceivedStockId(Integer receivedStockId) {
		// do nothing for now. 
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		//OBJECT_TYPE_ID for REPACKING_ITEM
		return REPACKING_ITEM_OBJ_TYPE_ID;
	}

	@Transient
	public String getFromDescription() {
		return fromDescription;
	}

	public void setFromDescription(String fromDescription) {
		this.fromDescription = fromDescription;
	}

	@Transient
	public String getToDescription() {
		return toDescription;
	}

	public void setToDescription(String toDescription) {
		this.toDescription = toDescription;
	}

	@Transient
	public Integer getOrigItemId() {
		return origItemId;
	}

	public void setOrigItemId(Integer origItemId) {
		this.origItemId = origItemId;
	}

	@Transient
	public Double getOrigUnitCost() {
		return origUnitCost;
	}

	public void setOrigUnitCost(Double origUnitCost) {
		this.origUnitCost = origUnitCost;
	}

	@Column(name = "FROM_AMOUNT", columnDefinition="double")
	public Double getFromTotal() {
		return fromTotal;
	}
	public void setFromTotal(Double fromTotal) {
		this.fromTotal = fromTotal;
	}

	@Column(name = "TO_AMOUNT", columnDefinition="double")
	public Double getToTotal() {
		return toTotal;
	}
	public void setToTotal(Double toTotal) {
		this.toTotal = toTotal;
	}

	@Transient
	public Double getToOrigQty() {
		return toOrigQty;
	}

	public void setToOrigQty(Double toOrigQty) {
		this.toOrigQty = toOrigQty;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RepackingItem [repackingId=").append(repackingId).append(", fromItemId=").append(fromItemId)
				.append(", toItemId=").append(toItemId).append(", quantity=").append(quantity)
				.append(", repackedQuantity=").append(repackedQuantity).append(", fromStockCode=").append(fromStockCode)
				.append(", toStockCode=").append(toStockCode).append(", unitCost=").append(unitCost)
				.append(", repackedUnitCost=").append(repackedUnitCost).append(", fromExistingStock=")
				.append(fromExistingStock).append(", toExistingStock=").append(toExistingStock).append(", totalQty=")
				.append(totalQty).append(", origQty=").append(origQty).append(", referenceObjectId=")
				.append(referenceObjectId).append(", toDescription=").append(toDescription).append(", fromDescription=")
				.append(fromDescription).append(", origItemId=").append(origItemId).append(", origUnitCost=")
				.append(origUnitCost).append(", fromTotal=").append(fromTotal).append(", toTotal=").append(toTotal)
				.append(", toOrigQty=").append(toOrigQty).append("]");
		return builder.toString();
	}
}
