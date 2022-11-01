package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.service.ReceiptMethodService;

/**
 * Controller that will retrieve the receipt methods.
 * based on the service lease key of the logged user.

 *
 */
@Controller
@RequestMapping(value="getArReceiptMethods")
public class GetArReceiptMethods {
	@Autowired
	private ReceiptMethodService receiptMethodService;
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getArCustomers (
			@RequestParam(value="companyId") Integer companyId,
			HttpSession session) {
		List<ReceiptMethod> receiptMethods = receiptMethodService.getReceiptMethods(companyId);
		String [] exclude = {"debitAcctCombination", "creditAcctCombination", "bankAccount", "company"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(receiptMethods, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value="byDivision", method = RequestMethod.GET)
	public @ResponseBody String getReceiptMethods (
			@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="divisionId") Integer divisionId,
			@RequestParam(value="receiptMethodId", required = false)Integer receiptMethodId,
			HttpSession session) {
		List<ReceiptMethod> receiptMethods = 
				receiptMethodService.getReceiptMethodByCompanyAndDivision(companyId, divisionId, receiptMethodId);
		String [] exclude = {"debitAcctCombination", "creditAcctCombination", "bankAccount", "company"};
		JsonConfig jConfig = new JsonConfig();
		jConfig.setExcludes(exclude);
		JSONArray jsonArray = JSONArray.fromObject(receiptMethods, jConfig);
		return jsonArray.toString();
	}

}
