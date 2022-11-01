package eulap.eb.web.dto;


import java.util.Date;

import eulap.eb.service.inventory.InventoryItem;

/**
 * DTO for Physical Inventory worksheet report.

 */
public class PhysicalInventory implements InventoryItem {
	private String divisionName;
	private Integer itemId;
	private Integer itemCategoryId;
	private Integer unitMeasurementId;
	private String stockCode;
	private String description;
	private String measurement;
	private Double quantity;
	private Double unitCost;
	private Double srp;
	private boolean ignore;
	private Integer receivedStockId;
	private Double amount;

	public static final int ALL_OPTION = -1;

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;

	}
	public String getDivisionName() {
		return divisionName;
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

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getUnitMeasurementId() {
		return unitMeasurementId;
	}
	
	public void setUnitMeasurementId(Integer unitMeasurementId) {
		this.unitMeasurementId = unitMeasurementId;
	}

	public Integer getItemCategoryId() {
		return itemCategoryId;
	}

	public void setItemCategoryId(Integer itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public Double getSrp() {
		return srp;
	}

	public void setSrp(Double srp) {
		this.srp = srp;
	}

	@Override
	public Double getInventoryCost() {
		return unitCost;
	}

	@Override
	public void setInventoryCost(Double inventoryCost) {
		this.unitCost = inventoryCost;
	}

	@Override
	public boolean isIgnore() {
		return ignore;
	}

	@Override
	public void setIgnore(boolean isIgnore) {
		this.ignore = isIgnore;
	}

	@Override
	public Integer getReceivedStockId() {
		return receivedStockId;
	}

	@Override
	public void setReceivedStockId(Integer receivedStockId) {
		this.receivedStockId = receivedStockId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return "PhysicalInventory [divisionName ="+divisionName+",itemId=" + itemId + ", itemCategoryId="
				+ itemCategoryId + ", stockCode=" + stockCode + ", quantity="
				+ quantity + ", unitCost=" + unitCost + ", srp=" + srp + "]";
	}
	
	@Override
	public int getId() {
		return 0;
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
