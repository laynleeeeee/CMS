package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.eb.service.inventory.MillingItem;
import eulap.eb.service.oo.OOChild;

/**
 * Object representation of STOCK_ADJUSTMENT_ITEM table.

 *
 */
@Entity
@Table(name = "STOCK_ADJUSTMENT_ITEM")
public class StockAdjustmentItem extends BaseItem implements OOChild, MillingItem {
	private static Logger logger = Logger.getLogger(StockAdjustmentItem.class);
	@Expose
	private Integer stockAdjustmentId;
	private StockAdjustment stockAdjustment;
	@Expose
	private Integer referenceObjectId;
	@Expose
	private Integer typeId;
	@Expose
	private String stockCodeIs;
	@Expose
	private Double itemBagQuantity;
	@Expose
	private String poNumber;
	@Expose
	private Integer origItemId;
	@Expose
	private Double origUnitCost;
	private Double amount;

	public static final int OBJECT_TYPE_ID = 134;

	public enum FIELD {
		id, stockAdjustmentId, itemId, quantity, unitCost, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "STOCK_ADJUSTMENT_ITEM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="STOCK_ADJUSTMENT_ID", columnDefinition="int(10)")
	public Integer getStockAdjustmentId() {
		return stockAdjustmentId;
	}

	public void setStockAdjustmentId(Integer stockAdjustmentId) {
		this.stockAdjustmentId = stockAdjustmentId;
	}

	@ManyToOne
	@JoinColumn (name = "STOCK_ADJUSTMENT_ID", insertable=false, updatable=false)
	public StockAdjustment getStockAdjustment() {
		return stockAdjustment;
	}

	public void setStockAdjustment(StockAdjustment stockAdjustment) {
		this.stockAdjustment = stockAdjustment;
	}

	@Column(name="PO_NUMBER")
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@Transient
	public String getStockCodeIs() {
		return stockCodeIs;
	}

	public void setStockCodeIs(String stockCodeIs) {
		this.stockCodeIs = stockCodeIs;
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
		// Set the type of STOCK_ADJUSTMENT_ITEM in OBJECT_TYPE table
		// Check if EB Object id has a value
		if(getEbObjectId() != null) {
			//Only applicable when stock adjustment item is already saved.
			if(super.getQuantity() < 0) {
				return 11; //Stock Adjustment OUT
			} else {
				return 16; //Stock Adjustment IN
			}
		} else if(typeId != null) {
			if(typeId.equals(StockAdjustment.STOCK_ADJUSTMENT_IS_IN)) {
				return 16;
			} else if(typeId.equals(StockAdjustment.STOCK_ADJUSTMENT_IS_OUT)) {
				return 11;
			}
		} else {
			double qty = getQuantity();
			if (qty > 0) {
				return 16;
			} else {
				return 11;
			}
		}
		logger.warn("Type id and EB Object Id is null. Returning the Object Type id to null also.");
		return null;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		// Do nothing
		return false;
	}

	@Transient
	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
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

	@Column(name="AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StockAdjustmentItem [stockAdjustmentId=").append(stockAdjustmentId)
				.append(", referenceObjectId=").append(referenceObjectId).append(", typeId=").append(typeId)
				.append(", poNumber=").append(poNumber).append(", itemId=").append(getItemId())
				.append(", quantity=").append(getQuantity()).append(", stockCode=").append(getStockCode())
				.append(", unitCost=").append(getUnitCost()).append(", amount=").append(amount).append("]");
		return builder.toString();
	}
}