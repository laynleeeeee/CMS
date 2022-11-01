package eulap.eb.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.common.util.Page;
import eulap.eb.service.ArTransactionService;
import eulap.eb.web.dto.ArTransactionRegisterDto;

/**
 * Controller that will retrieve the ar transactions.
 * based on the service lease key of the logged user.


 *
 */
@Controller
@RequestMapping(value="getArTransactions")
public class GetArTransactions {
	@Autowired
	private ArTransactionService arTransactionService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getArCustomers (
			@RequestParam(value="criteria", required=false) String criteria,
			@RequestParam(value="arCustAcctId") Integer arCustAcctId,
			@RequestParam(value="id", required=false) Integer id,
			@RequestParam(value="tNumbers", required=false) String tNumbers,
			@RequestParam(value="noLimit", required=false) Boolean noLimit,
			@RequestParam(value="isReference", required=false) Boolean isReference,
			@RequestParam(value="isShow", required=false) Boolean isShow,
			@RequestParam(value="isExact", required=false) Boolean isExact,
			HttpSession session) {
		List<ArTransactionRegisterDto> transactions = new ArrayList<ArTransactionRegisterDto>();
		Page<ArTransactionRegisterDto> arTransactionDtos = arTransactionService.getArTransactions(arCustAcctId, id, tNumbers, criteria, isShow, isExact);
		for (ArTransactionRegisterDto arTransactionRegisterDto : arTransactionDtos.getData()) {
			transactions.add(arTransactionRegisterDto);
		}
		JSONArray jsonArray = JSONArray.fromObject(transactions);
		return jsonArray.toString();
	}
}