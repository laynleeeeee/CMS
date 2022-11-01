package eulap.eb.web;

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

import eulap.eb.domain.hibernate.CheckbookTemplate;
import eulap.eb.service.CheckbookTemplateService;

/**
 * A controller class that retrieves that checkbook template.

 *
 */
@Controller
@RequestMapping ("getCheckbookTemplate")
public class GetCheckBookTemplate {
	private static Logger logger = Logger.getLogger(GetCheckBookTemplate.class);
	@Autowired
	private CheckbookTemplateService templateService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getAccounts (@RequestParam (value="checkbookTemplateId", required=true)Integer checkbookTemplateId,
			HttpSession session) {
			logger.info("Retrieving checkbook template...");
			CheckbookTemplate template = templateService.getCheckTemplate(checkbookTemplateId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONArray jsonArray = JSONArray.fromObject(template, jsonConfig);
			return jsonArray.toString();
	}
}
