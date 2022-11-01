package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.SupplierAccountService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller that will get the bank accounts.

 *
 */
@Controller
@RequestMapping ("getBankAccounts")
public class GetBankAccounts {
	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private SupplierAccountService supplierAccountService;
	
	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getArLines(
			@RequestParam(value="companyId") Integer companyId,
			HttpSession session){
		List<BankAccount> bankAccounts = bankAccountService.getBankAccounts(companyId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(bankAccounts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/all", method = RequestMethod.GET)
	public @ResponseBody String getAllBankAccounts(
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="limit", required=false) Integer limit,
			@RequestParam(value="companyId", required=false) Integer companyId,
			HttpSession httpSession) {
		return getAllBankAcct(name, limit, companyId, null, httpSession);
	}

	private String getAllBankAcct(String name, Integer limit, Integer companyId, Integer divisionId, HttpSession httpSession) {
		User user = CurrentSessionHandler.getLoggedInUser(httpSession);
		List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByName(name, limit, user, companyId, divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(bankAccounts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/bySupplierAcc", method = RequestMethod.GET)
	public @ResponseBody String getAllBankAccountsBySupplierAcc(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="limit", required=false) Integer limit,
			@RequestParam(value="supplierAccountId", required=false) Integer supplierAccountId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			HttpSession httpSession) {
		if (supplierAccountId != null) {
			SupplierAccount supAccount = supplierAccountService.getSupplierAccount(supplierAccountId);
			companyId = supAccount != null ? supAccount.getCompanyId() : null;
		}
		return getAllBankAcct(name, limit, companyId, divisionId, httpSession);
	}

	@RequestMapping(value="/byName", method = RequestMethod.GET)
	public @ResponseBody String getBankAccount(@RequestParam(value="name") String name,
			@RequestParam(value="divisionId", required=false) Integer divisionId, HttpSession httpSession) {
		BankAccount bankAccounts = bankAccountService.getBankAccoutByName(name, null, divisionId);
		JSONObject jsonObject = JSONObject.fromObject(bankAccounts);
		return bankAccounts == null ? "No bank account found." : jsonObject.toString();
	}
}
