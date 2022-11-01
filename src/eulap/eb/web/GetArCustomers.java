package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.service.ArCustomerService;

/**
 * Controller that will retrieve the ar customers
 * based on the service lease key of the logged user.

 *
 */
@Controller
@RequestMapping(value="getArCustomers")
public class GetArCustomers {
	Logger logger = Logger.getLogger(GetArCustomers.class);
	@Autowired
	private ArCustomerService arCustomerService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getArCustomers (@RequestParam(value="name", required=false) String name, 
			@RequestParam(value="isExact", required=false) Boolean isExact,
			HttpSession session) {
		List<ArCustomer> arCustomers = arCustomerService.getArCustomers(name, isExact, CurrentSessionHandler.getLoggedInUser(session));
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arCustomers, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(method = RequestMethod.GET, value="includeAll")
	public @ResponseBody String getArCustomers (@RequestParam(value="name", required=false) String name, 
			@RequestParam(value="limit", required=false) Integer limit,
			HttpSession session) {
		List<ArCustomer> arCustomers = arCustomerService.getArCustomers(name, false, limit);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arCustomers, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public @ResponseBody String getArCustomerList (@RequestParam(value="name", required=false) String name,
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="isExact", required=false) Boolean isExact,
			HttpSession session) {
		logger.info("Retrieving the list of AR Customers for company: "+companyId);
		List<ArCustomer> arCustomers = arCustomerService.getArCustomers(companyId, name, isExact, divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arCustomers, jConfig);
		logger.info("Successfully retrieved" + arCustomers.size() + " ar customers");
		arCustomers = null; //Freeing up memory
		return jsonArray.toString();
	}

	@RequestMapping(value = "/byReceiptMethod", method = RequestMethod.GET)
	public @ResponseBody String getArCustomersByRM (@RequestParam(value="name", required=false) String name,
			@RequestParam(value="receiptMethodId", required=false) Integer receiptMethodId,
			@RequestParam(value="isExact", required=false) Boolean isExact,
			HttpSession session) {
		logger.info("Retrieving the list of AR Customers for receipt method: "+receiptMethodId);
		List<ArCustomer> arCustomers = arCustomerService.getArCustomersByRM(receiptMethodId, name, isExact);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(arCustomers, jConfig);
		logger.info("Successfully retrieved" + arCustomers.size() + " ar customers");
		arCustomers = null; //Freeing up memory
		return jsonArray.toString();
	}
}
