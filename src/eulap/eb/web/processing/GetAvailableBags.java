package eulap.eb.web.processing;

import java.io.InvalidClassException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ItemBagQuantityService;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.web.dto.AvblStocksAndBagsDto;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Controller class to retrieve available bags.

 *
 */
@Controller
@RequestMapping("/getAvailableBags")
public class GetAvailableBags {
	@Autowired
	private ItemBagQuantityService itemBagQtyService;
	@Autowired
	private OOLinkHelper ooLinkHelper;

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String getAvailableItemBags(
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="itemId", required=false) Integer itemId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="ebObjectId", required=false) Integer ebObjectId) {
		List<AvblStocksAndBagsDto> availableBagsAndStocks = itemBagQtyService.getAvailableBags(companyId, itemId, warehouseId, null, ebObjectId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(availableBagsAndStocks, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/origRef", method=RequestMethod.GET)
	public @ResponseBody String getRefSourceForm(@RequestParam (value="orTypeId", required=false) Integer orTypeId,
			@RequestParam (value="sourceObjectId", required=false) Integer ebObjectId,
			HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		String shortDesc  = ooLinkHelper.getRefShortName(ebObjectId, orTypeId, user);
		return shortDesc == null ? "" : shortDesc;
	}

	@RequestMapping(value="/proportionQty", method=RequestMethod.GET)
	public @ResponseBody String getProportionQty(
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="itemId", required=false) Integer itemId,
			@RequestParam (value="warehouseId", required=false) Integer warehouseId,
			@RequestParam (value="refObjectId", required=false) Integer refObjectId,
			@RequestParam (value="bagsToWithdraw", required=false) Double bagsToWithdraw,
			@RequestParam (value="origRefObjectId", required=false) Integer origRefObjectId) {
		return itemBagQtyService.getProportionQty(companyId, refObjectId,
				itemId, warehouseId, bagsToWithdraw, origRefObjectId).toString();
	}

}
