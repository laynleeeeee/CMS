package eulap.eb.web.dto;

import java.util.Date;

import eulap.eb.service.inventory.InventoryItem;

/**
 * Holds the the information of item transaction.
 * 

 * 
 */
public class ItemTransaction implements InventoryItem{
	private Integer id;
	private Date date;
	private String location;
	private Integer number;
	private String form;
	private String supplier;
	private String stockCode;
	private String description;
	private Integer itemId;
	private Double quantity;
	private Double unitCost;
	private Double inventoryCost;
	private String formType;
	private String measurement;
	private Integer itemCategoryId;
	private Integer costCenterId;
	private Integer costCenterTypeId;
	private Integer receivedStockId;
	private String ccRefId;
	private String ccDescription;
	private boolean ignore;
	private Integer warehouseId;
	private Integer itemDiscountId;
	private Integer itemSrpId;

	public static String TR_FORM_NAME = "TR";
	public static String FW_FORM_NAME = "FW";
	public static String RSS_FORM_NAME = "RSS";
	public static String RTS_FORM_NAME = "RTS";
	public static String MRIS_FORM_NAME = "MRIS";
	public static String RTS_OUTSOURE_FORM_NAME = "RTSO";

	//Inventory Retail
	public static String ACCT_SALE_FORM_NAME = "AS";
	public static String ACCT_SALE_RETURN_FORM_NAME = "ASR";
	public static String REPACKING_FORM_NAME = "RP";
	public static String STOCK_ADJUSTMENT_FORM_NAME = "SA";
	public static String CASH_SALE_FORM_NAME = "CS";
	public static String CASH_SALE_RETURN_FORM_NAME = "CSR";
	public static String CAP_DELIVERY_FORM_NAME = "CD";
	public static String RTS_EB_FORM_NAME = "RTS-EB";
	public static String ACCT_SALE_WS_FORM_NAME = "ASW";
	public static String ACCT_SALE_RET_WS_FORM_NAME = "ASRW";

	public ItemTransaction() {
		//Do nothing.
	}

	private ItemTransaction (Integer id, Integer itemId, Integer warehouseId, Integer itemDiscountId,
			Integer itemSrpId, Double quantity, Double unitCost, String form, Integer number) {
		this.id = id;
		this.itemId = itemId;
		this.warehouseId = warehouseId;
		this.itemDiscountId = itemDiscountId;
		this.itemSrpId = itemSrpId;
		this.quantity = quantity;
		this.unitCost = unitCost;
		this.form = form;
		this.number = number;
	}

	public static ItemTransaction getRItemTransaction(Integer id, Integer itemId, Integer warehouseId,
			Integer itemDiscountId, Integer itemSrpId, Double quantity, Double unitCost, String form, Integer number) {
		return new ItemTransaction(id, itemId, warehouseId, itemDiscountId, itemSrpId, quantity, unitCost, form, number);
	}

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public boolean isIgnore() {
		return ignore;
	}

	@Override
	public void setIgnore(boolean isIgnore) {
		this.ignore = isIgnore;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public Double getInventoryCost() {
		return inventoryCost;
	}

	public void setInventoryCost(Double inventoryCost) {
		this.inventoryCost = inventoryCost;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	public Integer getItemCategoryId() {
		return itemCategoryId;
	}

	public void setItemCategoryId(Integer itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	public Integer getCostCenterId() {
		return costCenterId;
	}

	public void setCostCenterId(Integer costCenterId) {
		this.costCenterId = costCenterId;
	}

	public Integer getCostCenterTypeId() {
		return costCenterTypeId;
	}

	public void setCostCenterTypeId(Integer costCenterTypeId) {
		this.costCenterTypeId = costCenterTypeId;
	}

	@Override
	public Integer getReceivedStockId() {
		return receivedStockId;
	}

	public void setReceivedStockId(Integer receivedStockId) {
		this.receivedStockId = receivedStockId;
	}

	public String getCcRefId() {
		return ccRefId;
	}

	public void setCcRefId(String ccRefId) {
		this.ccRefId = ccRefId;
	}

	public String getCcDescription() {
		return ccDescription;
	}

	public void setCcDescription(String ccDescription) {
		this.ccDescription = ccDescription;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Integer getItemDiscountId() {
		return itemDiscountId;
	}

	public void setItemDiscountId(Integer itemDiscountId) {
		this.itemDiscountId = itemDiscountId;
	}

	public Integer getItemSrpId() {
		return itemSrpId;
	}

	public void setItemSrpId(Integer itemSrpId) {
		this.itemSrpId = itemSrpId;
	}
	
	@Override
	public String toString() {
		return "ItemTransaction [Id=" + id + ", date=" + date + ", location="
				+ location + ", number=" + number + ", form=" + form
				+ ", supplier=" + supplier + ", stockCode=" + stockCode
				+ ", description=" + description + ", itemId=" + itemId
				+ ", quantity=" + quantity + ", unitCost=" + unitCost
				+ ", inventoryCost=" + inventoryCost + ", formType=" + formType
				+ ", measurement=" + measurement + ", itemCategoryId="
				+ itemCategoryId + ", costCenterId=" + costCenterId
				+ ", costCenterTypeId=" + costCenterTypeId + ", ccRefId="
				+ ccRefId + ", ccDescription=" + ccDescription + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void setId(int id) {
		
	}

	@Override
	public void setCreatedBy(int id) {
		
	}

	@Override
	public int getCreatedBy() {
		return 0;
	}

	@Override
	public void setCreatedDate(Date date) {
	}

	@Override
	public Date getCreatedDate() {
		return null;
	}

	@Override
	public void setUpdatedBy(int id) {
	}

	@Override
	public int getUpdatedBy() {
		return 0;
	}

	@Override
	public void setUpdatedDate(Date date) {
	}

	@Override
	public Date getUpdatedDate() {
		return null;
	}
}