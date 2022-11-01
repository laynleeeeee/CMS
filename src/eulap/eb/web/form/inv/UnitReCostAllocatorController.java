package eulap.eb.web.form.inv;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.inventory.CostAllocatorHandler;
import eulap.eb.service.inventory.CostAllocatorHandler.AllocatorStatusHandler;

/**
 * A temporary controller class that will handle the allocation of inventory items.

 *
 */
@Controller
@RequestMapping ("/processAllocation")
public class UnitReCostAllocatorController {
	private static Logger logger = Logger.getLogger(UnitReCostAllocatorController.class);
	@Autowired
	private CostAllocatorHandler costHandler;
	@Autowired 
	private WarehouseService warehouseService;

	@RequestMapping(method = RequestMethod.GET)
	public String showInitPage () {
		return "reallocatedTemplate";
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody String doAllocateCost (@RequestParam(value="warehouseId",
					required=true) Integer warehouseId,
					@RequestParam (value="date", required=true) Date date,
					@RequestParam (value="pageNumber", required=true) int pageNumber,
					final HttpServletResponse response)
							throws CloneNotSupportedException, HttpMessageNotWritableException, IOException {

		final Set<Item> errors = new HashSet<Item>();
		final Set<Item> successfulItems = new HashSet<Item>();
		AllocatorStatusHandler statusHandler = new AllocatorStatusHandler() {
			@Override
			public void progressReport(Item item, String str) {
				logger.info(item.getDescription() + " : " + str);
			}

			@Override
			public void error(Item item,String message) {
				String msg = "ITEM ID : "+ item.getId()+ ", Item stock code :" + item.getStockCode();
				msg += " message :" + message;
				logger.info(msg);
				errors.add(item);
			}

			@Override
			public void completed(Item item, String message) {
				successfulItems.add(item);
			}
		};
		Warehouse warehouse = warehouseService.getWarehouse(warehouseId);
		boolean hasNext = costHandler.doAllocation(warehouse.getCompanyId(), warehouseId, date, statusHandler, pageNumber);
		String str = "Allocated successfully : " + successfulItems.size() + "</br>";

		str += "<b>With errors and needs manual intervention :"+errors.size()+"</b></br>";
		for (Item item :errors) {
			str += item.getStockCode() + "</br>";
		}
		if (pageNumber == 0){
			str = "=======================================</br>"
					+ "Repacked items </br>"
				+ "=======================================</br>"+ str +"</br>";
		} else if (hasNext) {
			str = "=======================================</br>"
					+ "Page " + pageNumber+"</br>"
				+ "=======================================</br>"+ str+"</br>";
		} else {
			str = "Last Page </br>"+ str;
		}
		return str;
	}
}
