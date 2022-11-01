package eulap.eb.web.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eulap.eb.domain.hibernate.SaleItem;

/**
 * Stock IN or Out Report DTO. This will be used in generating form report 
 * that includes stock-in or out. 


 *
 */
public abstract class StockInOutReport {

	private List<WarehouseStockInOutDto> warehouseStockInOutDtos;
	private String slipTitle;

	public StockInOutReport (List<? extends SaleItem> saleItems, String slipTitle){
		convertToWhDto(saleItems);
		this.slipTitle = slipTitle;
	}

	/**
	 * Instantiate this class.
	 */
	public StockInOutReport (List<? extends SaleItem> saleItems){
		convertToWhDto(saleItems);
	}

	/**
	 * Convert the {@link List<SaleItem>} to List<WarehouseStockInOutDto>.
	 * @param saleItems
	 */
	private void convertToWhDto(List<? extends SaleItem> saleItems) {
		warehouseStockInOutDtos = new ArrayList<WarehouseStockInOutDto>();
		Map<Integer, WarehouseStockInOutDto> hmWarehouseDtos = new HashMap<Integer, WarehouseStockInOutDto>();

		WarehouseStockInOutDto warehouseStockInOutDto = null;
		for (SaleItem saleItem : saleItems) {
			ItemStockInOut itemStockInOut = new ItemStockInOut();
			itemStockInOut.setDescription(saleItem.getItem().getDescription());
			itemStockInOut.setQuantity(saleItem.getQuantity());
			itemStockInOut.setStockCode(saleItem.getItem().getStockCode());
			itemStockInOut.setUom(saleItem.getItem().getUnitMeasurement().getName());
			if (!hmWarehouseDtos.containsKey(saleItem.getWarehouseId())) {
				warehouseStockInOutDto = new WarehouseStockInOutDto();
				warehouseStockInOutDto.setWarehouseId(saleItem.getWarehouseId());
				warehouseStockInOutDto.setWarehouseName(saleItem.getWarehouse().getName());
				warehouseStockInOutDto.setItemStockInOuts(new ArrayList<ItemStockInOut>());
				hmWarehouseDtos.put(warehouseStockInOutDto.getWarehouseId(), warehouseStockInOutDto);
			} else {
				warehouseStockInOutDto = hmWarehouseDtos.get(saleItem.getWarehouseId());
			}
			warehouseStockInOutDto.getItemStockInOuts().add(itemStockInOut);
		}

		if (!hmWarehouseDtos.isEmpty()) {
			warehouseStockInOutDtos = new ArrayList<WarehouseStockInOutDto>(hmWarehouseDtos.values());
		}
	}

	public List<WarehouseStockInOutDto> getWarehouseStockInOuts() {
		return warehouseStockInOutDtos;
	}

	public String getSlipTitle() {
		return slipTitle;
	}

	public void setSlipTitle(String slipTitle) {
		this.slipTitle = slipTitle;
	}

	@Override
	public String toString() {
		return "StockInOutReport [warehouseStockInOuts=" + warehouseStockInOutDtos
				+ "]";
	}
}
