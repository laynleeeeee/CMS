package eulap.eb.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.LoanChargeType;
import eulap.eb.service.LoanChargeTypeService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * A controller class that will handle requests for {@link LoanChargeType}

 */

@Controller
@RequestMapping("/getLoanChargeType")
public class GetLoanChargeType {
	@Autowired
	private LoanChargeTypeService loanChargeTypeService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getLoanChargeTypes(@RequestParam(value="loanChargeTypeId", required=false) Integer loanChargeTypeId) {
		List<LoanChargeType> loanDetailTypes = loanChargeTypeService.getAllActiveLoanChargeTypes(loanChargeTypeId);
		JSONArray jsonArray = JSONArray.fromObject(loanDetailTypes, new JsonConfig());
		return jsonArray.toString();
	}
}
