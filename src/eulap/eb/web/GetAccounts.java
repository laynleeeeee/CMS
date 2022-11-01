package eulap.eb.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountService;
import eulap.eb.web.dto.AccountDto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * A controller class that retrieves that accounts based on
 * the account combination or its account number.

 *
 */
@Controller
@RequestMapping ("getAccounts")
public class GetAccounts {
	private static Logger logger = Logger.getLogger(GetAccounts.class);
	@Autowired
	private AccountService accountService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getAccounts (@RequestParam (value="companyId", required=false)Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="accountId", required=false) Integer accountId,
			@RequestParam (value="accountNumber", required=false) String accountNumber, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		String returnMessage = "";
		if(companyId != null && divisionId != null) {
			List<Account> accounts = accountId != null ? (List<Account>) accountService.getAccounts(companyId, divisionId, accountId) :
				accountService.getAccounts(companyId, divisionId);
			JsonConfig jConfig = new JsonConfig();
			JSONArray jsonArray = JSONArray.fromObject(accounts, jConfig);
			returnMessage = jsonArray.toString();
			logger.info("Retrieved "+accounts.size()+" under company id: "+companyId+" and division id: "+divisionId);
		} else if(accountNumber != null) {
			Account account = accountService.getAccountByNumber(accountNumber, user);
			JSONObject jsonObject = JSONObject.fromObject(account);
			returnMessage = account  == null ? "No account found" : jsonObject.toString();
		}
		return returnMessage;
	}

	@RequestMapping (value="/byName", method = RequestMethod.GET)
	public @ResponseBody String getAccountsByName (
			@RequestParam (value="accountName", required=false) String accountName,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="limit", required=false) Integer limit,
			HttpSession session) {
		List<Account> accounts = accountService.getAccountsByName(accountName, companyId, divisionId, limit);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(accounts, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="withdrawalCustAcct",method = RequestMethod.GET)
	public @ResponseBody String getwithdrawalCustAcct (@RequestParam (value="arCustomerAcctId", required=false) Integer arCustomerAcctId,
			HttpSession session) {
		String returnMessage = "";
		if(arCustomerAcctId != null) {
			List<Account> accounts =  new ArrayList<>();
			Account account = accountService.getwithdrawalCustAcct(arCustomerAcctId);
			accounts.add(account);
			JsonConfig jConfig = new JsonConfig();
			JSONArray jsonArray = JSONArray.fromObject(accounts, jConfig);
			returnMessage = accounts.isEmpty() ? "" : jsonArray.toString();
		}
		return returnMessage;
	}

	@RequestMapping (value="/getParentAccounts",method = RequestMethod.GET)
	public @ResponseBody String getParentAccounts (
			@RequestParam (value="numberOrName", required=false) String numberOrName,
			@RequestParam (value="accountId", required=false) Integer accountId,
			HttpSession session) {
		List<Account> accounts = accountService.getAccounts(numberOrName, accountId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(accounts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/byExactName", method = RequestMethod.GET)
	public @ResponseBody String getAccountByExactName (
			@RequestParam (value="accountName", required=false) String accountName,
			HttpSession session) {
		Account account = accountService.getAcctByName(accountName, true);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(account, jsonConfig);
		return jsonObj.toString();

	}

	@RequestMapping (value="/byNameAndType", method = RequestMethod.GET)
	public @ResponseBody String getAccountsByNameAndType (
			@RequestParam (value="accountName", required=false) String accountName,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="accountTypeId", required=false) Integer accountTypeId,
			@RequestParam (value="limit", required=false) Integer limit,
			HttpSession session) {
		List<Account> accounts = accountService.getAccountsByNameAndType(accountName, companyId, divisionId, accountTypeId, limit);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(accounts, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/getAcctbyNameAndType", method = RequestMethod.GET)
	public @ResponseBody String getAccountByNameAndType (
			@RequestParam (value="accountName", required=false) String accountName,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="accountTypeId", required=false) Integer accountTypeId,
			HttpSession session) {
		Account account = accountService.getAccountByNameAndType(accountName, companyId, divisionId, accountTypeId);
		JSONObject jsonObject = JSONObject.fromObject(account);
		return account  == null ? "No account found" : jsonObject.toString();
	}

	@RequestMapping (value="/byCompany", method = RequestMethod.GET)
	public @ResponseBody String getAccountsByCombination (
			@RequestParam (value="acctNameOrNumber", required=false) String acctNameOrNumber,
			@RequestParam (value="companyId", required=false) Integer companyId,
			HttpSession session) {
		List<AccountDto> accountDtos = accountService.getByCombinationAndType(companyId, null, null, acctNameOrNumber);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(accountDtos, jsonConfig);
		return jsonArray.toString();
	}

	@RequestMapping (value="/byNameAndExcludeAcctId", method = RequestMethod.GET)
	public @ResponseBody String getAccountAndExcludeAcctId (
			@RequestParam (value="accountName", required=false) String accountName,
			@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="isExact", required=false, defaultValue="false") boolean isExact,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="accountId", required=false) Integer accountId,
			@RequestParam (value="limit", required=false) Integer limit,
			HttpSession session) {
		List<Account> accounts = accountService.getAccountAndExcludeId(accountName, companyId, divisionId, accountId, limit, isExact);
		JsonConfig jsonConfig = new JsonConfig();
		if(isExact) {
			Account account = null;
			if(!accounts.isEmpty()) {
				account = accounts.iterator().next();
			}
			JSONObject jsonObject = JSONObject.fromObject(account);
			return account  == null ? "No account found" : jsonObject.toString();
		}
		JSONArray jsonArray = JSONArray.fromObject(accounts, jsonConfig);
		return jsonArray.toString();
	}
}