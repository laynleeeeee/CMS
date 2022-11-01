package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.CustodianAccount;
import eulap.eb.service.CustodianAccountService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Controller that will retrieve the custodian accounts.

 *
 */
@Controller
@RequestMapping(value="getCustodianAccounts")
public class GetCustodianAccounts {
	@Autowired
	private CustodianAccountService custodianAccountService;

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public @ResponseBody String showCustodianAccounts (@RequestParam(value="name", required=false) String name,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="isExact", required=false) Boolean isExact,
			HttpSession session) {
		List<CustodianAccount> custodianAccounts = custodianAccountService.getCustodianAccounts(companyId, divisionId, name, isExact);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(custodianAccounts, jConfig);
		custodianAccounts = null; //Freeing up memory
		return jsonArray.toString();
	}
}
