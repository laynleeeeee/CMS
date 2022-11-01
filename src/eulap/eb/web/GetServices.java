package eulap.eb.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.service.ServiceSettingService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Controller that will retrieve the Service setting.

 *
 */
@Controller
@RequestMapping ("getServices")
public class GetServices {
	@Autowired
	private ServiceSettingService serviceSettingService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getServices(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="arCustAcctId", required=false) Integer arCustAcctId,
			@RequestParam(value="id", required=false) Integer id,
			@RequestParam(value="noLimit", required=false) Boolean noLimit,
			@RequestParam(value="isExact", required=false) Boolean isExact,
			@RequestParam(value="divisionId", required=false) Integer divisionId) {
		List<ServiceSetting> services = serviceSettingService.getServiceSettings(name, arCustAcctId, id,
				noLimit, isExact, divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(services, jConfig);
		return jsonArray.toString();
	}
	@RequestMapping (value="/byDivision",method = RequestMethod.GET)
	public @ResponseBody String getServicesByDivision(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="customerAcctId", required=false) Integer customerAcctId,
			@RequestParam(value="noLimit", required=false) Boolean noLimit,
			@RequestParam(value="isExact", required=false, defaultValue="false") Boolean isExact,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="companyId", required=false) Integer companyId){
		List<ServiceSetting> services = serviceSettingService.getServiceSettingByDivision(name, noLimit, isExact, divisionId,companyId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(services, jConfig);
		return jsonArray.toString();
	}
}
