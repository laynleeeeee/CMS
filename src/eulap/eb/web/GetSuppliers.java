package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.SupplierService;
import bp.web.ar.CurrentSessionHandler;

/**
 * Get the different suppliers.

 */
@Controller
@RequestMapping("/getSuppliers")
public class GetSuppliers {
	private static final Logger logger = Logger.getLogger(GetSuppliers.class);
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private BankAccountService bankAccountService;
	
	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getSuppliers (@RequestParam(value="term", required=false) String name,
			@RequestParam(value="busRegTypeId", required=false) Integer busRegTypeId,
			HttpSession session) {
		List<Supplier> suppliers = null;
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(busRegTypeId != null)
			suppliers = supplierService.getSuppliersByBusRegType(user, name, busRegTypeId);
		else
			suppliers = supplierService.getSuppliers(user, name);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(suppliers, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public @ResponseBody String getSupplierList(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="isExact", required=false) Boolean isExact,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Retreiving the list of suppliers");
		return getSuppliers(companyId, divisionId, name, isExact, user);
	}

	@RequestMapping(value = "/byBank", method = RequestMethod.GET)
	public @ResponseBody String getSuppliersByBank(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="bankAccountId", required=false) Integer bankAccountId,
			HttpSession session) {
		logger.info("Retreiving the list of suppliers");
		BankAccount bankAccount = bankAccountService.getBankAcct(bankAccountId);
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if (bankAccount == null) {
			throw new RuntimeException("Bank Account " + bankAccountId + " is not found.");
		}
		return getSuppliers(bankAccount.getCompanyId(), bankAccount.getCashInBank().getDivisionId(), name, null, user);
	}

	private String getSuppliers (Integer companyId, Integer divisionId, String name, Boolean isExact, User user) {
		List<Supplier> suppliers = supplierService.getSuppliers(companyId, divisionId, name, isExact, user);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(suppliers, jConfig);
		logger.trace("Successfully done retreiving " + suppliers.size() + " suppliers");
		suppliers = null; //Freeing up memory
		return jsonArray.toString();
	}

	@RequestMapping(value="/byName", method=RequestMethod.GET)
	public @ResponseBody String getSupplier(@RequestParam(value="supplierName", required=false) String supplierName) {
		Supplier supplier = supplierService.getSupplier(supplierName);
		if(supplier != null && !supplierName.trim().isEmpty()) {
			JSONObject jsonObject = JSONObject.fromObject(supplier);
			return supplier == null ? "No supplier found" : jsonObject.toString();
		}
		String errorMessage = "No supplier found";
		return errorMessage;
	}

	@RequestMapping(value="/getCompanies")
	public @ResponseBody String getCompaniesBySupplier(@RequestParam (value="supplierId", required=false) Integer supplierId,
			@RequestParam (value="companyName", required=false) String companyName,
			HttpSession httpSession) {
		User user = CurrentSessionHandler.getLoggedInUser(httpSession);
		List<Company> companies = supplierService.getCompaniesBySupplier(supplierId, companyName, user);
		JSONArray jsonArray = JSONArray.fromObject(companies);
		return jsonArray.toString();
	}
}