package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.Driver;
import eulap.eb.service.DriverService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * A controller class that retrieves the drivers.

 *
 */
@Controller
@RequestMapping ("getDrivers")
public class GetDrivers {
	@Autowired
	private DriverService driverService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getDrivers (@RequestParam(value="name", required=false) String name,
			@RequestParam(value="companyId", required=false) Integer companyId,
			HttpSession session) {
		List<Driver> drivers = driverService.getDriversByName(companyId, name);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(drivers, jConfig);
		return jsonArray.toString();
	}
}
