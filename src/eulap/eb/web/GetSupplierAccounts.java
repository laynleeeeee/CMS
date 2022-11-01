package eulap.eb.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.SupplierAccountService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * A controllers class that retrieves the suppliers accounts. 

 *
 */
@Controller
@RequestMapping ("getSupplierAccounts")
public class GetSupplierAccounts {
	@Autowired
	private SupplierAccountService accountService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getSuppliersAccount (@RequestParam (value="supplierId") Integer supplierId,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="supplierAccountId", required=false) Integer supplierAccountId, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<SupplierAccount> supplierAccunts = new ArrayList<SupplierAccount>();
		if(divisionId != null) {
			supplierAccunts = accountService.getSupplierAccnts(supplierId, user, divisionId, supplierAccountId);
		}
		else {
			supplierAccunts = accountService.getSupplierAccts(supplierId, companyId);
		}
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(supplierAccunts, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="/byId", method = RequestMethod.GET)
	public @ResponseBody String getSupplierAccountById (@RequestParam (value="supplierAccountId") Integer supplierAccountId,
			HttpSession session) {
		SupplierAccount supplierAccount = accountService.getSupplierAccount(supplierAccountId);
		JSONObject jsonObject = JSONObject.fromObject(supplierAccount);
		return supplierAccount == null ? "No supplier account found" : jsonObject.toString();
	}
}
