package eulap.eb.web;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.PosMiddlewareSetting;
import eulap.eb.service.PosMiddlewareSettingService;

/**
 * Controller class to POS Middleware Settings.

 *
 */
@Controller
@RequestMapping("/getPosSetting")
public class GetPosSetting {
	private static final Logger logger = Logger.getLogger(GetPosSetting.class);
	@Autowired
	private PosMiddlewareSettingService middlewareSettingService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getItems(@RequestParam (value="companyId") int companyId) {
		PosMiddlewareSetting setting = middlewareSettingService.getByCompany(companyId);
		if(setting == null) {
			logger.info("NO POS Middleware Configuration for company ID "+companyId);
			return "No POS Middleware Configuration";
		}
		logger.info("Retrieved  "+setting.getArCustomer().getName()+" AR Customer "
				+ "and "+setting.getWarehouse().getName()+"warehouse configurations "
				+ "for "+setting.getCompany().getName());
		JSONObject jsonObject = JSONObject.fromObject(setting);
		return jsonObject.toString();
	}
}
