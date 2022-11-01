package eulap.eb.web.processing;

import java.util.List;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.service.ItemService;
import eulap.eb.web.processing.dto.AvailableStock;

/**
 * Controller class to retrieve available stocks

 *
 */
@Controller
@RequestMapping("/getAvailableStocks")
public class GetAvailableStocks {
	@Autowired
	private ItemService itemService;

	@RequestMapping (value="/filter", method = RequestMethod.GET)
	public @ResponseBody String getAvailableStocks(
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="stockCode", required=false) String stockCode,
			@RequestParam (value="referenceObjectId", required=false) Integer refObjectId) {
		List<AvailableStock> availableStocks = itemService.getAvailableStocks(stockCode, warehouseId, companyId);
		if(refObjectId != null) {
			availableStocks = itemService.addRefObjectToAvailStocks(availableStocks, refObjectId, stockCode, warehouseId);
		}
		JSONArray jsonArray = JSONArray.fromObject(availableStocks);
		return jsonArray.toString();
	}

	@RequestMapping(value="/total", method=RequestMethod.GET)
	public @ResponseBody String getTotalAS(@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="stockCode", required=false) String stockCode) {
		Double totalAvailStocks = itemService.getTotalAvailStocks(stockCode, warehouseId);
		return totalAvailStocks.toString();
	}

	@RequestMapping (value="/items", method = RequestMethod.GET)
	public @ResponseBody String getItemsWithStocks(
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="stockCode", required=false) String stockCode) {
		List<AvailableStock> availableStocks = itemService.getItemsWithAvailStocks(stockCode, warehouseId);
		JSONArray jsonArray = JSONArray.fromObject(availableStocks);
		return jsonArray.toString();
	}
}
