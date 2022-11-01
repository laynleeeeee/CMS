package eulap.eb.web;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.StockAdjustmentType;
import eulap.eb.service.StockAdjustmentTypeService;

/**
 * Controller class to retrieve the list of stock adjustment types.

 *
 */
@Controller
@RequestMapping("/getSAdjustmentTypes")
public class GetStockAdjustmentType {
	private static Logger logger = Logger.getLogger(GetStockAdjustmentType.class);
	@Autowired
	private StockAdjustmentTypeService sadjustmentTypeService;

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String getSAdjustmentTypes(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="activeOnly", required=false) Boolean activeOnly) {
		List<StockAdjustmentType> adjustmentTypes = sadjustmentTypeService.getStockAdjustmentTypes(companyId, divisionId, activeOnly);
		logger.debug("Retrieved "+adjustmentTypes.size()+" Adjustment Types.");
		String [] exclude = {"stockAdjustment"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(adjustmentTypes, jConfig);
		return jsonArray.toString();
	}
}
