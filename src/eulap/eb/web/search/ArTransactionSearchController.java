package eulap.eb.web.search;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fsp.ArTransactionSearcher;
import eulap.eb.web.dto.FormSearchResult;

/**
 * A controller class that handles the searching of AR Transactions.

 */
@Controller
@RequestMapping("/searchArTransactions")
public class ArTransactionSearchController {
	@Autowired
	private ArTransactionSearcher arTransactionSearcher;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String searchAPInvoices(@RequestParam(required=true, value="criteria", defaultValue="")
	String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = arTransactionSearcher.search(user, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
