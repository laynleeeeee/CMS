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
 * Object representation of ACCOUNT_SALES_PO_ITEM table.

 *
 */
@Entity
@Table(name = "ACCOUNT_SALES_PO_ITEM")
public class AccountSalesPoItem extends BaseFormLine implements InventoryItem {
	@Expose
	private Integer accoutnSaleId;
	@Expose
	private Integer itemId;
	@Expose
	private Double quantity;
	@Expose
	private Double unitCost;
	@Expose
	private String stockCode;
	@Expose
	private Integer warehouseId;
	@Expose
	private Double origQty;
	private Item item;
	private Warehouse warehouse;
	private Double existingStocks;

	public static final int OBJECT_TYPE_ID = 167;

	public enum FIELD {
		id, accoutnSaleId, itemId, quantity, unitCost
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ACCOUNT_SALES_PO_ITEM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="ACCOUNT_SALE_ID", columnDefinition="int(10)")
	public Integer getAccoutnSaleId() {
		return accoutnSaleId;
	}

	public void setAccoutnSaleId(Integer accoutnSaleId) {
		this.accoutnSaleId = accoutnSaleId;
	}

	/**
	 * Get the item id.
	 * @return The item id.
	 */
	@Column(name = "ITEM_ID", columnDefinition="int(10)")
	public Integer getItemId() {
		return itemId;
	}

	/**
	 * Set the item id.
	 * @param itemId The item id.
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@ManyToOne
	@JoinColumn (name = "ITEM_ID", insertable=false, updatable=false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * Get the quantity.
	 * @return The quantity.
	 */
	@Column(name = "QUANTITY", columnDefinition="double")
	public Double getQuantity() {
		return quantity;
	}

	/**
	 * Set the quantity.
	 * @param quantity The quantity.
	 */
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	/**
	 * Get the warehouse id.
	 * @return The quantity.
	 */
	@Column(name = "WAREHOUSE_ID", columnDefinition="int(10)")
	public Integer getWarehouseId() {
		return warehouseId;
	}

	/**
	 * Set the quantity.
	 * @param quantity The quantity.
	 */
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

	/**
	 * Get the unit cot
	 * @return the unit cost
	 */
	@Transient
	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	@Transient
	public Double getExistingStocks() {
		return existingStocks;
	}

	public void setExistingStocks(Double existingStocks) {
		this.existingStocks = existingStocks;
	}

	@Override
	public String toString() {
		return "AccountSalesPoItem [accoutnSaleId=" + accoutnSaleId + ", itemId=" + itemId + ", quantity=" + quantity
				+ ", unitCost=" + unitCost + ", stockCode=" + stockCode + ", warehouseId=" + warehouseId + ", item="
				+ item + "]";
	}

	@Transient
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}


	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		// ACCOUNT_SALES_PO_ITEM type in OBJECT_TYPE table.
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Double getInventoryCost() {
		// Not supported.
		throw new RuntimeException("not supported method");
	}

	@Override
	public void setInventoryCost(Double inventoryCost) {
		// Not supported.
		throw new RuntimeException("not supported method");
	}

	@Override
	@Transient
	public boolean isIgnore() {
		// Not supported.
		throw new RuntimeException("not supported method");
	}

	@Override
	public void setIgnore(boolean isIgnore) {
		// Not supported.
		throw new RuntimeException("not supported method");
	}

	@Override
	@Transient
	public Integer getReceivedStockId() {
		// Not supported.
		throw new RuntimeException("not supported method");
	}

	@Override
	public void setReceivedStockId(Integer receivedStockId) {
		// Not supported.
		throw new RuntimeException("not supported method");
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Transient
	public Double getOrigQty() {
		return origQty;
	}

	public void setOrigQty(Double origQty) {
		this.origQty = origQty;
	}
}
