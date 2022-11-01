package eulap.eb.web.dto;

import java.util.List;

/**
 * Class that handles the in and out transaction per warehouse.


 * 
 */
public class WarehouseStockInOutDto {
	private int warehouseId;
	private String warehouseName;
	private List<ItemStockInOut> itemStockInOuts;

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public List<ItemStockInOut> getItemStockInOuts() {
		return itemStockInOuts;
	}

	public void setItemStockInOuts(List<ItemStockInOut> itemStockInOuts) {
		this.itemStockInOuts = itemStockInOuts;
	}

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Override
	public String toString() {
		return "WarehouseStockInOutDto [warehouseId=" + warehouseId
				+ ", warehouseName=" + warehouseName + ", ItemStockInOuts="
				+ itemStockInOuts + "]";
	}
}
