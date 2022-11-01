package eulap.eb.web;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.service.TermService;

/**
 * Controllers class that retrieves the list of {@link Term}.

 *
 */
@Controller
@RequestMapping ("getTerm")
public class GetTerm {
	private static Logger logger = Logger.getLogger(GetTerm.class);
	@Autowired
	private TermService termService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getTerm (@RequestParam (value="supplierAccountId", required=false) Integer supplierAccountId,
			@RequestParam(value="termId", required=false) Integer termId,
			HttpSession session) {
		Term term = null;
		if(supplierAccountId != null) {
			logger.info("Get term by supplier account id: "+supplierAccountId);
			term = termService.getTermBySupplierAccount(supplierAccountId, CurrentSessionHandler.getLoggedInUser(session));
		} else if(termId != null) {
			logger.info("Get term by term id: "+termId);
			term = termService.getTerm(termId);
		}

		logger.trace("Retrieved the term: "+term);
		JSONObject jsonObject = JSONObject.fromObject(term);
		return jsonObject.toString();
	}
}
