package eulap.eb.web;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.Checkbook;
import eulap.eb.service.CheckbookService;

/**
 * A controller class that retrieves from checkbook.

 */
@Controller
@RequestMapping ("getCheckbook")
public class GetCheckbook {
	private static Logger logger = Logger.getLogger(GetCheckbook.class);
	@Autowired
	private CheckbookService checkbookService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getCheckbook (@RequestParam(value="bankAccountId") Integer bankAccountId,
			@RequestParam(value="checkBookName", required=false) String checkBookName,
			HttpSession session) {
		logger.info("Retrieving checkkbook : " + checkBookName);
		Checkbook checkbook = checkbookService.getCheckBook(CurrentSessionHandler.getLoggedInUser(session),
				bankAccountId, checkBookName);
		JsonConfig jConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(checkbook, jConfig);
		return jsonObject.toString();
	}
}
