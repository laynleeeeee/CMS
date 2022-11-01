package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.WarehouseService;
/**
 * A controller class that will get the list of warehouses.

 */
@Controller
@RequestMapping ("getWarehouse")
public class GetWarehouse {
	private static Logger logger = Logger.getLogger(GetWarehouse.class);
	@Autowired
	private WarehouseService warehouseService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getWarehouse(@RequestParam(value="companyId", required=false) Integer companyId){
		logger.info("Retrieving the list of warehouse");
		logger.debug("Retrieving list of warehouses by company id " + companyId);
		List<Warehouse> warehouseList = warehouseService.getWarehouseList(companyId);
		return getWarehouses(warehouseList);
	}

	@RequestMapping (method = RequestMethod.GET, value="/new")
	public @ResponseBody String getAllWarehouse(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId,
			@RequestParam(value="divisionId", required=false) Integer divisionId){
		logger.info("Retrieving the list of warehouse");
		List<Warehouse> warehouseList = warehouseService.getWarehouses(companyId, warehouseId, divisionId);
		return getWarehouses(warehouseList);
	}


	private String getWarehouses(List<Warehouse> warehouseList) {
		if(warehouseList.isEmpty()) {
			logger.warn("No warehouse found");
		} else {
			logger.debug("Successfully retrieved "+warehouseList.size()+" warehouses.");
		}
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(warehouseList, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (method = RequestMethod.GET, value="/withES")
	public @ResponseBody String getWarehousesWithES(@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="itemId") Integer itemId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId,
			@RequestParam(value="divisionId", required=false) Integer divisionId) {
		List<Warehouse> warehouses = warehouseService.getWarehousesWithES(companyId, itemId, warehouseId, divisionId);
		return getWarehouses(warehouses);
	}

	@RequestMapping (method = RequestMethod.GET, value="/byUserCompany")
	public @ResponseBody String getWHByUserCompany(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="isActiveOnly", required=false) boolean isActiveOnly,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId,
			HttpSession session){
		logger.info("Retrieving the list of warehouse");
		logger.debug("Retrieving list of warehouses by company id " + companyId);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Warehouse> warehouseList = warehouseService.getWHsByUserCompany(companyId, user, isActiveOnly, warehouseId);
		return getWarehouses(warehouseList);
	}

	@RequestMapping (method = RequestMethod.GET, value="/withInactive")
	public @ResponseBody String getWarehousesWithInactive(
			@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="itemId") Integer itemId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId) {
		List<Warehouse> warehouses = warehouseService.getWarehousesWithInactive(companyId, itemId, warehouseId);
		return getWarehouses(warehouses);
	}

	@RequestMapping (method = RequestMethod.GET, value="/getParentWarehouses")
	public @ResponseBody String getParentWarehouses(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="warehouseId", required=false) Integer warehouseId,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="isExact", required=false) Boolean isExact,
			@RequestParam(value="isActive", required=false) Boolean isActive) {
		return getWarehouses(warehouseService.getParentWarehouses(companyId, divisionId, warehouseId, name,
				isExact, (isActive != null ? isActive : false)));
	}
}
