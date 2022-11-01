package eulap.eb.web;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.service.AccountCombinationService;

/**
 * Get Account Combination Controller

 *
 */
@RequestMapping("getAccntCombination")
@Controller
public class GetAccntCombinations {
	@Autowired
	private AccountCombinationService combinationService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getAccntCombinations (@RequestParam(value="companyId", required = false) Integer companyId,
			@RequestParam(value="divisionId", required = false) Integer divisionId,
			@RequestParam(value="accountName", required = false) String accountName,
			@RequestParam(value="limit", required = false) Integer limit) {
		List<AccountCombination> ac = null;
		if (companyId != null && divisionId != null) {
			ac = combinationService.getAccountCombinations(companyId, divisionId, accountName, limit);
		}
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(ac, jConfig);
		return jsonArray.toString();
	}
}
