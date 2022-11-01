package eulap.eb.service.inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.service.ItemService;


/**
 * Allocate the unit cost of the item using the method
 * of weighted average. 

 *
 */
public class WeightedAverageAllocator<T extends InventoryItem> implements ItemAllocator<T>{
	private ReceivedStock availableStock;
	
	public WeightedAverageAllocator(ItemService itemService, int itemId, int companyId, 
			int warehouseId, Date date) {
		availableStock = itemService.getItemAvailableStocksAsOfDate(itemId, companyId, warehouseId, date);
	}

	@Override
	public List<T> allocateCost(T item) throws CloneNotSupportedException {
		if (item.getQuantity() > availableStock.getQuantity()) {
			throw new RuntimeException ("Stocks to be allocated is greater than the available stocks");
		}
		item.setUnitCost(NumberFormatUtil.roundOffTo2DecPlaces(availableStock.getUnitCost()));
		List<T> ret = new ArrayList<>();
		ret.add(item);
		return ret;
	}
}
