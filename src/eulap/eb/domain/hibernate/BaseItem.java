package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

import eulap.eb.service.inventory.InventoryItem;

/**
 * A base class that define domain with item that has unit cost.

 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BaseItem extends BaseFormLine implements InventoryItem{
	private Item item;
	@Expose
	private Integer itemId;
	@Expose
	private Double quantity;
	@Expose
	private Double unitCost;
	@Expose
	private String stockCode;
	private Double processedQty;
	@Expose
	private Double origQty;
	@Expose
	private Double existingStocks;
	@Expose
	private Integer origRefObjectId;
	private Integer receivedStockId;
	@Expose
	private Double vatAmount;
	@Expose
	private Integer taxTypeId;
	private TaxType taxType;

	@ManyToOne
	@JoinColumn(name = "ITEM_ID", insertable=false, updatable=false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Column(name = "ITEM_ID", columnDefinition="int(10)")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Column(name = "QUANTITY", columnDefinition="double")
	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Column(name = "UNIT_COST", columnDefinition="double")
	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	@Transient
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	@Override
	@Transient
	public Double getInventoryCost() {
		// Not supported.
		return null;
	}

	@Override
	public void setInventoryCost(Double inventoryCost) {
		// Not supported
	}

	@Override
	@Transient
	public boolean isIgnore() {
		// not supported.
		return false;
	}

	@Override
	public void setIgnore(boolean isIgnore) {
		// not supported.
	}

	@Override
	@Transient
	public Integer getReceivedStockId() {
		return receivedStockId;
	}

	@Override
	public void setReceivedStockId(Integer receivedStockId) {
		this.receivedStockId = receivedStockId;
	}

	@Transient
	public Double getProcessedQty() {
		return processedQty;
	}

	public void setProcessedQty(Double processedQty) {
		this.processedQty = processedQty;
	}
	
	@Transient
	public Double getOrigQty() {
		return origQty;
	}
	
	public void setOrigQty(Double origQty) {
		this.origQty = origQty;
	}

	@Transient
	public Double getExistingStocks() {
		return existingStocks;
	}

	public void setExistingStocks(Double existingStocks) {
		this.existingStocks = existingStocks;
	}

	public abstract boolean isSplitWithUnitCost (BaseItem itemLine);

	@Transient
	public Integer getOrigRefObjectId() {
		return origRefObjectId;
	}

	public void setOrigRefObjectId(Integer origRefObjectId) {
		this.origRefObjectId = origRefObjectId;
	}

	@Column(name = "TAX_TYPE_ID")
	public Integer getTaxTypeId() {
		return taxTypeId;
	}

	public void setTaxTypeId(Integer taxTypeId) {
		this.taxTypeId = taxTypeId;
	}

	@Column(name = "VAT_AMOUNT")
	public Double getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(Double vatAmount) {
		this.vatAmount = vatAmount;
	}

	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name = "TAX_TYPE_ID", insertable=false, updatable=false, nullable=true)
	public TaxType getTaxType() {
		return taxType;
	}

	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	@Override
	public String toString() {
		return "BaseItem [item=" + item + ", itemId=" + itemId + ", quantity="
				+ quantity + ", unitCost=" + unitCost + "]";
	}
}
