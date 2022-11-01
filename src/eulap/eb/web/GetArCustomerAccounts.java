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

import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.service.ArCustomerAcctService;

/**
 * Controller that will retrieve the ar customer accounts
 * based on the ar customer.

 *
 */
@Controller
@RequestMapping(value="getArCustomerAccounts")
public class GetArCustomerAccounts {
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getArCustomerAccounts(@RequestParam (value="arCustomerId") Integer arCustomerId,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="activeOnly", required=false) boolean activeOnly){
		List<ArCustomerAccount> arCustomerAccounts = arCustomerAcctService.getArCustomerAccounts(arCustomerId,
				((companyId != null && companyId > 0) ? companyId : null), divisionId, activeOnly);
		return getCustomerAccounts(arCustomerAccounts);
	}

	@RequestMapping (method = RequestMethod.GET, value="/includeSavedInactive")
	public @ResponseBody String getArCustomerAccounts (@RequestParam (value="arCustomerId") Integer arCustomerId, 
			@RequestParam (value="arCustomerAccountId", required=false) Integer arCustomerAccountId,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId) {
		List<ArCustomerAccount> arCustomerAccounts = arCustomerAcctService.getArCustomerAccounts(arCustomerId,
				((companyId != null && companyId > 0) ? companyId : null), divisionId, arCustomerAccountId);
		return getCustomerAccounts(arCustomerAccounts);
	}

	private String getCustomerAccounts(List<ArCustomerAccount> arCustomerAccounts) {
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arCustomerAccounts, jConfig);
		return jsonArray.toString();
	}
}
