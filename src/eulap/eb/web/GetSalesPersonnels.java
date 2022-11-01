package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.SalesPersonnel;
import eulap.eb.service.SalesPersonnelService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Controllers class that retrieves the list of {@link SalesPersonnel}.

 */
@Controller
@RequestMapping("/getSalesPersonnels")
public class GetSalesPersonnels {
	private static final Logger logger = Logger.getLogger(GetSalesPersonnels.class);
	@Autowired
	private SalesPersonnelService salesPersonnelService;
	
	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getSalesPersonnels (@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="isExact", required=false, defaultValue ="false") Boolean isExact,
			HttpSession session) {
		logger.info("Retrieving sales personnels.");
		List<SalesPersonnel> salesPersonnels = salesPersonnelService.getSalesPersonnelByName(companyId, name, isExact);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(salesPersonnels, jConfig);
		return jsonArray.toString();
	}
}