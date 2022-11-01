package eulap.eb.web.dto;

/**
 * Cash Sale item dto.

 *
 */
public class CSItemDto {
	private Integer itemId;
	private Integer warehouseId;
	private Double quantity;
	private Double unitCost;
	
	public static CSItemDto getInstanceOf (Integer itemId, Integer warehouseId, Double quantity, Double unitCost) {
		CSItemDto csItemDto = new CSItemDto();
		csItemDto.itemId = itemId;
		csItemDto.warehouseId = warehouseId;
		csItemDto.quantity = quantity;
		csItemDto.unitCost = unitCost;
		return csItemDto;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
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
}
