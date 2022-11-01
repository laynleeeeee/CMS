package eulap.eb.web;


import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.service.SupplierService;

/**
 * Controller for getting Supplier object.

 *
 */
@Controller
@RequestMapping(value="/getSupplier")
public class GetSupplier {
	@Autowired
	private SupplierService supplierService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String loadSupplier(@RequestParam(value="name") String name,
			@RequestParam(value="busRegTypeId", required=false) Integer busRegTypeId) {
		Supplier supplier = null;
		if(name != null)
			supplier = supplierService.getSupplier(name.trim());
		else if(busRegTypeId != null)
			supplier = supplierService.getSupplierByBusRegType(name, busRegTypeId);
		JSONObject jsonObject = JSONObject.fromObject(supplier);
		return jsonObject.toString();
	}
}
