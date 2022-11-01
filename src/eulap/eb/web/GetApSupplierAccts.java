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

import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.service.SupplierAccountService;

/**
 * Controller class for retrieving the AP Supplier Accounts.

 *
 */
@Controller
@RequestMapping(value="getApSupplierAccts")
public class GetApSupplierAccts {
	@Autowired
	private SupplierAccountService supplierAccountService;

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String getSupplierAccts(@RequestParam(value="supplierId") int supplierId,
			@RequestParam(value="supplierAccountId", required=false) Integer supplierAccountId,
			@RequestParam(value="companyId") int companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="activeOnly", defaultValue ="true", required=false) boolean activeOnly){
		List<SupplierAccount> supplierAccts = supplierAccountService.getSupplierAccts(supplierId, supplierAccountId, companyId, divisionId, activeOnly);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(supplierAccts, jConfig);
		return jsonArray.toString();
	}
}
