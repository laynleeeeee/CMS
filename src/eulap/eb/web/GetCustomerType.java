package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.CustomerType;
import eulap.eb.service.CustomerTypeService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Controller class that will retrieve customer types

 */

@Controller
@RequestMapping(value="getCustomerType")
public class GetCustomerType {
	@Autowired
	private CustomerTypeService customerTypeService;

	@RequestMapping(value="/byCustomerId", method = RequestMethod.GET)
	public @ResponseBody String getCustomerTypes(@RequestParam(value="customerTypeId", required=false) Integer customerTypeId,
			@RequestParam(value="arCustomerId", required=false) Integer arCustomerId, 
			HttpSession session) {
		List<CustomerType> customerTypes = customerTypeService.getCustomerTypesWithInactive(customerTypeId, arCustomerId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(customerTypes, jConfig);
		return jsonArray.toString();
	}
}
