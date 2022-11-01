package eulap.eb.web.search;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fsp.StockAdjustmentSearcher;
import eulap.eb.web.dto.FormSearchResult;

/**
 * A controller class that handles the searching for Retail - TR forms.

 */
@Controller
@RequestMapping("/searchStockAdjustment/{typeId}")
public class StockAdjustmentSearchCtrlr {
	@Autowired
	private StockAdjustmentSearcher adjustmentSearcher;

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String searchSAForms(@PathVariable("typeId") int typeId,
			@RequestParam(value="criteria", defaultValue="", required=true)
			String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = adjustmentSearcher.search(user, criteria, typeId);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
