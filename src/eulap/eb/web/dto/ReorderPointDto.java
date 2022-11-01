package eulap.eb.web.dto;

/**
 * Container class for the attributes needed for the Reordering Point Report.


 * 
 */
public class ReorderPointDto implements Cloneable {
	private Integer itemId;
	private String stockCode;
	private String description;
	private String warehouseName;
	private Integer warehouseId;
	private Integer divisionId;
	private String divisionName;
	private Integer reorderingPoint;
	private Double onHand;
	private Double unservedPO;
	private String uom;

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
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

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Integer getReorderingPoint() {
		return reorderingPoint;
	}

	public void setReorderingPoint(Integer reorderingPoint) {
		this.reorderingPoint = reorderingPoint;
	}

	public Double getOnHand() {
		return onHand;
	}

	public void setOnHand(Double onHand) {
		this.onHand = onHand;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getUnservedPO() {
		return unservedPO;
	}

	public void setUnservedPO(Double unservedPO) {
		this.unservedPO = unservedPO;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	@Override
	public String toString() {
		return "ReorderPointDto [itemId=" + itemId + ", stockCode=" + stockCode + ", description=" + description
				+ ", warehouseName=" + warehouseName + ", warehouseId=" + warehouseId + ", divisionId=" + divisionId
				+ ", divisionName=" + divisionName + ", reorderingPoint=" + reorderingPoint + ", onHand=" + onHand
				+ ", unservedPO=" + unservedPO + ", uom=" + uom + "]";
	}
}
