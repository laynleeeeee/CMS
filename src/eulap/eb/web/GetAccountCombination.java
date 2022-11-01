package eulap.eb.web;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.service.AccountCombinationService;

/**
 * A controller class that retrieves the different account combinations

 *
 */
@Controller
@RequestMapping ("getAccountCombination")
public class GetAccountCombination {
	@Autowired
	private AccountCombinationService combinationService;
	private static Logger logger = Logger.getLogger(GetAccountCombination.class);
	
	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getDivisions (@RequestParam(value="aCId", required = false) Integer accountCombinationId,
			@RequestParam(value="companyNumber", required = false) String companyNumber,
			@RequestParam(value="divisionNumber", required = false) String divisionNumber,
			@RequestParam(value="accountNumber", required = false) String accountNumber) {
		AccountCombination ac = null;
		if (accountCombinationId != null) {
			ac = combinationService.getAccountCombination(accountCombinationId);
		} else if (companyNumber != null && divisionNumber != null && accountNumber != null) {
			ac = combinationService.getAccountCombination(companyNumber, divisionNumber, accountNumber);
		}
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(ac, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "/byCombination", method = RequestMethod.GET)
	public @ResponseBody String getAcctCombination(
			@RequestParam(value = "companyId", required = false) Integer companyId,
			@RequestParam(value = "divisionId", required = false) Integer divisionId,
			@RequestParam(value = "accountId", required = false) Integer accountId) {
		logger.info("Get Account Combination. Params: companyId=" + companyId + ",divisionId=" + divisionId
				+ ",accountId=" + accountId);
		AccountCombination ac = combinationService.getAccountCombination(companyId, divisionId, accountId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(ac, jConfig);
		logger.info(jsonArray.toString());
		return jsonArray.toString();
	}

	@RequestMapping(value = "/byId", method = RequestMethod.GET)
	public @ResponseBody String getAcctCombination(
			@RequestParam(value = "id", required = false) Integer accountCombinationId) {
		logger.info("Get Account Combination. Param: ID=" + accountCombinationId);
		AccountCombination ac = combinationService.getAccountCombination(accountCombinationId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(ac, jConfig);
		logger.info(jsonArray.toString());
		return jsonArray.toString();
	}
}
