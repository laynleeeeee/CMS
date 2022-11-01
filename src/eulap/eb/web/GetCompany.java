package eulap.eb.web;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.SupplierAccountService;
import bp.web.ar.CurrentSessionHandler;

/**
 * Controller that will retrieve the company using the company number.

 *
 */
@Controller
@RequestMapping("getCompany")
public class GetCompany {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private SupplierAccountService supplierAcctService;
	@Autowired
	private ArCustomerAcctService custAcctService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getCompany (@RequestParam (value="companyNumber") String companyNumber,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Company company = companyService.getCompanyByNumber(companyNumber, user);
		JSONObject jsonObject = JSONObject.fromObject(company);
		return company == null ? "No company found" : jsonObject.toString();
	}

	/**
	 * Get the company by name and the logged user.
	 * @param companyName The name of the company.
	 * @param session Session that will determine the logged user.
	 * @return The company otherwise, the error message.
	 */
	@RequestMapping(value="/name", method = RequestMethod.GET)
	public @ResponseBody String getCompanyByName (@RequestParam (value="name") String companyName,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Company company = companyService.getCompanyByName(user, companyName);
		JSONObject jsonObject = JSONObject.fromObject(company);
		return company == null ? "No company found" : jsonObject.toString();
	}

	@RequestMapping(value = "/all",method = RequestMethod.GET)
	public @ResponseBody String getAllCompanies (
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Collection<Company> companys = companyService.getActiveCompanies(user, null, null, null);
		JSONArray jsonArray = JSONArray.fromObject(companys);
		return jsonArray.toString();
	}

	@RequestMapping(value="/filter", method = RequestMethod.GET)
	public @ResponseBody String getCompanies (
			@RequestParam (value="companyName", required=false) String companyName,
			@RequestParam (value="companyNames", required=false) String companyNames,
			@RequestParam (value="filterCNames", required=false) String filterCNames,
			HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Collection<Company> companys = companyService.getActiveCompanies(user, companyName, companyNames, filterCNames);
		JSONArray jsonArray = JSONArray.fromObject(companys);
		return jsonArray.toString();
	}

	@RequestMapping(value = "/byName",method = RequestMethod.GET)
	public @ResponseBody String getCompaniesByName (
			@RequestParam (value="companyName") String companyName,
			@RequestParam (value="isActiveOnly") boolean isActiveOnly,
			@RequestParam (value="limit", required=false) Integer limit,
			HttpSession session) {
		List<Company> companys = companyService.getActiveCompaniesByName(companyName, isActiveOnly, limit);
		JSONArray jsonArray = JSONArray.fromObject(companys);
		return jsonArray.toString();
	}

	@RequestMapping(value="/supplierAcct", method = RequestMethod.GET)
	public @ResponseBody String getCompanyByCupplierAcct (@RequestParam (value="supplierAccountId") Integer supplierAccountId,
			HttpSession session) {
		SupplierAccount supplierAccount = supplierAcctService.getSupplierAccount(supplierAccountId);
		Company company = companyService.getCompany(supplierAccount.getCompanyId());
		JSONObject jsonObject = JSONObject.fromObject(company);
		return company == null ? "No company found" : jsonObject.toString();
	}

	@RequestMapping(value="/arCustomerAcct", method = RequestMethod.GET)
	public @ResponseBody String getCompanyByCustomerAcct (@RequestParam (value="arCustomerAccountId") Integer arCustomerAccountId,
			HttpSession session) {
		ArCustomerAccount arCustomerAccount = custAcctService.getAccount(arCustomerAccountId);
		Company company = companyService.getCompany(arCustomerAccount.getCompanyId());
		JSONObject jsonObject = JSONObject.fromObject(company);
		return company == null ? "No company found" : jsonObject.toString();
	}
}
