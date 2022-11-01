package eulap.eb.domain.hibernate;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;
import eulap.eb.web.dto.RItemDetail;

/**
 * A class that represents ITEM in the CBS database.

 *
 */
@Entity
@Table (name="ITEM")
public class Item extends BaseDomain {
	private Integer itemCategoryId;
	private Integer unitMeasurementId;
	@Expose
	private String stockCode; //SKU
	@Expose
	private String description;
	private Double initialUnitCost;
	private Double initialQuantity;
	private boolean active;
	private ItemCategory itemCategory;
	private UnitMeasurement unitMeasurement;
	private Double existingStocks;
	private Integer reorderingPoint;
	private Double inventoryCost;
	private List<ItemSrp> itemSrps;
	private List<ItemDiscount> itemDiscounts;
	private String errorItemDiscounts;
	private String errorItemSrps;
	private Double itemSrp;
	private Double unitCost;
	private List<ItemAddOn> itemAddOns;
	private String errorItemAddOns;
	private List<ItemBuyingPrice> buyingPrices;
	private List<ItemBuyingAddOn> buyingAddOns;
	private List<ItemBuyingDiscount> buyingDiscounts;
	private List<RItemDetail> rItemDetails;
	private String manufacturerPartNo;
	private String purchaseDesc;
	private String salesDesc;
	private Integer maxOrderingPoint;
	private String barcode;
	private Integer itemVatTypeId;
	private ItemVatType itemVatType;

	public enum FIELD {id, itemCategoryId, unitMeasurementId, stockCode, description, initialUnitCost,
		initialQuantity, active, itemCategory, unitMeasurement, purchaseRequestItems, reorderingPoint,
		manufacturerPartNo, purchaseDesc, salesDesc, maxOrderingPoint, barcode
	}

	public static final int MAX_STOCK_CODE = 50;
	public static final int MAX_DESCRIPTION = 200;
	public static final int MAX_MANU_PART_NO = 50;
	public static final int MAX_PURCHASE_DESCRIPTION = 100;
	public static final int MAX_SALE_DESCRIPTION = 100;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	/**
	 * Get the item category id of the item.
	 * @return The item category id.
	 */
	@Column(name = "ITEM_CATEGORY_ID")
	public Integer getItemCategoryId() {
		return itemCategoryId;
	}

	/**
	 * Set the item category id of the item.
	 * @param itemCategoryId The item category id.
	 */
	public void setItemCategoryId(Integer itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	/**
	 * Get the unit measurement id of the item.
	 * @return The unit measurement id.
	 */
	@Column(name = "UNIT_MEASUREMENT_ID")
	public Integer getUnitMeasurementId() {
		return unitMeasurementId;
	}

	/**
	 * Set the unit measurement id of the item.
	 * @param unitMeasurementId The unit measurement id.
	 */
	public void setUnitMeasurementId(Integer unitMeasurementId) {
		this.unitMeasurementId = unitMeasurementId;
	}

	/**
	 * Get the stock code of the item.
	 * @return The stock code.
	 */
	@Column(name = "STOCK_CODE")
	public String getStockCode() {
		return stockCode;
	}

	/**
	 * Set the stock code of the item.
	 * @param stockCode The stock code.
	 */
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	
	/**
	 * Get the description of item.
	 * @return The description.
	 */
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the initial unit cost of the item.
	 * @return The initial unit cost.
	 */
	@Column(name = "INITIAL_UNIT_COST")
	public Double getInitialUnitCost() {
		return initialUnitCost;
	}

	/**
	 * Set the initial unit cost of the item.
	 * @param initialUnitCost The initial unit cost.
	 */
	public void setInitialUnitCost(Double initialUnitCost) {
		this.initialUnitCost = initialUnitCost;
	}

	/**
	 * Get the initial quantity of the item.
	 * @return The initial quantity.
	 */
	@Column(name = "INITIAL_QUANTITY")
	public Double getInitialQuantity() {
		return initialQuantity;
	}

	/**
	 * Set the initial quantity of the item.
	 * @param initialQuantity The initial quantity.
	 */
	public void setInitialQuantity(Double initialQuantity) {
		this.initialQuantity = initialQuantity;
	}

	/**
	 * Check if the item is active.
	 * @return True if active, otherwise false.
	 */
	@Column(name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the item to either true or false.
	 * @param active True or false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * Get the associated item category.
	 * @return Item category.
	 */
	@ManyToOne
	@JoinColumn (name = "ITEM_CATEGORY_ID", insertable=false, updatable=false)
	public ItemCategory getItemCategory() {
		return itemCategory;
	}

	/**
	 * Set the associated unit measurement.
	 * @param itemCategory
	 */
	public void setItemCategory(ItemCategory itemCategory) {
		this.itemCategory = itemCategory;
	}
	
	/**
	 * Get the associated unit measurement.
	 * @return Unit measurement.
	 */
	@ManyToOne
	@JoinColumn (name = "UNIT_MEASUREMENT_ID", insertable=false, updatable=false)
	public UnitMeasurement getUnitMeasurement() {
		return unitMeasurement;
	}

	/**
	 * Set the associated unit measurement.
	 * @param unitMeasurement
	 */
	public void setUnitMeasurement(UnitMeasurement unitMeasurement) {
		this.unitMeasurement = unitMeasurement;
	}

	/**
	 * Get the reordering point of an item
	 * @return The reordering point.
	 */
	@Column(name="REORDERING_POINT")
	public Integer getReorderingPoint() {
		return reorderingPoint;
	}

	/**
	 * Set the reordering point of an item.
	 * @param reorderingPoint The reordering point.
	 */
	public void setReorderingPoint(Integer reorderingPoint) {
		this.reorderingPoint = reorderingPoint;
	}

	/**
	 * Get the Stock code and Description of the Item.
	 */
	@Transient
	public String getStockCodeAndDesc() {
		return stockCode+" - "+description;
	}

	@Transient
	public Double getExistingStocks() {
		return existingStocks;
	}
	
	public void setExistingStocks(Double existingStocks) {
		this.existingStocks = existingStocks;
	}

	@Transient
	public Double getInventoryCost() {
		return inventoryCost;
	}

	public void setInventoryCost(Double inventoryCost) {
		this.inventoryCost = inventoryCost;
	}
	
	/**
	 * Get the item location.
	 * @return The item location
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="ITEM_ID", insertable=false, updatable=false)
	public List<ItemSrp> getItemSrps() {
		return itemSrps;
	}
	
	public void setItemSrps(List<ItemSrp> itemSrps) {
		this.itemSrps = itemSrps;
	}
	
	/**
	 * Get the item discounts.
	 * @return The item discounts.
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="ITEM_ID", insertable=false, updatable=false)
	public List<ItemDiscount> getItemDiscounts() {
		return itemDiscounts;
	}
	
	public void setItemDiscounts(List<ItemDiscount> itemDiscounts) {
		this.itemDiscounts = itemDiscounts;
	}

	@Transient
	public Double getItemSrp() {
		return itemSrp;
	}

	public void setItemSrp(Double itemSrp) {
		this.itemSrp = itemSrp;
	}

	@Transient
	public String getErrorItemDiscounts() {
		return errorItemDiscounts;
	}
	
	public void setErrorItemDiscounts(String errorItemDiscounts) {
		this.errorItemDiscounts = errorItemDiscounts;
	}
	
	@Transient
	public String getErrorItemSrps() {
		return errorItemSrps;
	}
	
	public void setErrorItemSrps(String errorItemSrps) {
		this.errorItemSrps = errorItemSrps;
	}


	@Transient
	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}
	
	/**
	 * Get the item discounts.
	 * @return The item discounts.
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="ITEM_ID", insertable=false, updatable=false)
	public List<ItemAddOn> getItemAddOns() {
		return itemAddOns;
	}
	
	public void setItemAddOns(List<ItemAddOn> itemAddOns) {
		this.itemAddOns = itemAddOns;
	}
	
	@Transient
	public String getErrorItemAddOns() {
		return errorItemAddOns;
	}

	public void setErrorItemAddOns(String errorItemAddOns) {
		this.errorItemAddOns = errorItemAddOns;
	}

	/**
	 * Get the item buying prices.
	 * @return The item buying prices.
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="ITEM_ID", insertable=false, updatable=false)
	public List<ItemBuyingPrice> getBuyingPrices() {
		return buyingPrices;
	}

	public void setBuyingPrices(List<ItemBuyingPrice> buyingPrices) {
		this.buyingPrices = buyingPrices;
	}

	/**
	 * Get the item buying add ons.
	 * @return The item buying add ons.
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="ITEM_ID", insertable=false, updatable=false)
	public List<ItemBuyingAddOn> getBuyingAddOns() {
		return buyingAddOns;
	}

	public void setBuyingAddOns(List<ItemBuyingAddOn> buyingAddOns) {
		this.buyingAddOns = buyingAddOns;
	}

	/**
	 * Get the item buying discounts.
	 * @return The item buying discounts.
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="ITEM_ID", insertable=false, updatable=false)
	public List<ItemBuyingDiscount> getBuyingDiscounts() {
		return buyingDiscounts;
	}

	public void setBuyingDiscounts(List<ItemBuyingDiscount> buyingDiscounts) {
		this.buyingDiscounts = buyingDiscounts;
	}

	@Transient
	public List<RItemDetail> getrItemDetails() {
		return rItemDetails;
	}

	public void setrItemDetails(List<RItemDetail> rItemDetails) {
		this.rItemDetails = rItemDetails;
	}

	@Column(name = "MANUFACTURER_PART_NO")
	public String getManufacturerPartNo() {
		return manufacturerPartNo;
	}

	public void setManufacturerPartNo(String manufacturerPartNo) {
		this.manufacturerPartNo = manufacturerPartNo;
	}

	@Column(name = "PURCHASE_DESC")
	public String getPurchaseDesc() {
		return purchaseDesc;
	}

	public void setPurchaseDesc(String purchaseDesc) {
		this.purchaseDesc = purchaseDesc;
	}

	@Column(name = "SALE_DESC")
	public String getSalesDesc() {
		return salesDesc;
	}

	public void setSalesDesc(String salesDesc) {
		this.salesDesc = salesDesc;
	}

	@Column(name = "MAX_ORDERING_POINT")
	public Integer getMaxOrderingPoint() {
		return maxOrderingPoint;
	}

	public void setMaxOrderingPoint(Integer maxOrderingPoint) {
		this.maxOrderingPoint = maxOrderingPoint;
	}

	@Column(name="BARCODE", columnDefinition="varchar(12)")
	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	@Column(name="ITEM_VAT_TYPE_ID", columnDefinition="int(10)")
	public Integer getItemVatTypeId() {
		return itemVatTypeId;
	}

	public void setItemVatTypeId(Integer itemVatTypeId) {
		this.itemVatTypeId = itemVatTypeId;
	}

	@Override
	public String toString() {
		return "Item [itemId=" + getId() + ", itemCategoryId=" + itemCategoryId
				+ ", unitMeasurementId=" + unitMeasurementId
				+ ", stockCode=" + stockCode + ", description="
				+ description + ", initialUnitCost=" + initialUnitCost
				+ ", initialQuantity=" + initialQuantity + ", active=" + active + "]";
	}

	@OneToOne
	@JoinColumn(name="ITEM_VAT_TYPE_ID", insertable=false, updatable=false)
	public ItemVatType getItemVatType() {
		return itemVatType;
	}

	public void setItemVatType(ItemVatType itemVatType) {
		this.itemVatType = itemVatType;
	}
}
