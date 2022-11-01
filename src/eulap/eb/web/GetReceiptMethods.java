package eulap.eb.web;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.service.ReceiptMethodService;

/**
 * Get the list of Receipt Methods per company.

 *
 */
@Controller
@RequestMapping ("getReceiptMethods")
public class GetReceiptMethods {
	private Logger logger = Logger.getLogger(GetReceiptMethods.class);
	@Autowired
	private ReceiptMethodService receiptMethodService;

	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getAccounts (@RequestParam (value="companyId", required=false) Integer companyId) {
		logger.info("Retrieving receipt methods for company: "+companyId);
		List<ReceiptMethod> receiptMethods = receiptMethodService.getReceiptMethods(companyId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(receiptMethods, jConfig);
		logger.info("Retrieved "+receiptMethods.size()+" Receipt Methods.");
		return jsonArray.toString();
	}

	@RequestMapping (value = "/byDivision", method = RequestMethod.GET)
	public @ResponseBody String getReceiptMethods (@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId) {
		List<ReceiptMethod> receiptMethods = receiptMethodService.getReceiptMethodByCompanyAndDivision(companyId, divisionId);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(receiptMethods, jConfig);
		logger.info("Retrieved "+receiptMethods.size()+" Receipt Methods.");
		return jsonArray.toString();
	}
}
