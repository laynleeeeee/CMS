package eulap.eb.web;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.service.ArTransactionService;
import eulap.eb.web.dto.ArTransactionRegisterDto;

/**
 * Controller that will retrieve the Ar Transaction object.

 *
 */
@Controller
@RequestMapping(value="getArTransaction")
public class GetArTransaction {
	@Autowired
	private ArTransactionService arTransactionService;
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getCompany (
			@RequestParam (value="criteria", required=false) String criteria,
			@RequestParam (value="arCustomerAcctId", required=false) Integer arCustomerAcctId,
			HttpSession session) {
		ArTransaction arTransaction = null;
		if(!criteria.trim().isEmpty()){
			arTransaction = arTransactionService.getArTransactionByNumber(criteria, arCustomerAcctId);
		}
		ArTransactionRegisterDto transaction = new ArTransactionRegisterDto();
		if (arTransaction != null) {
			transaction = ArTransactionRegisterDto.getInstanceOf(arTransaction.getId(), arTransaction.getSequenceNumber(), 
					arTransaction.getTransactionNumber(), arTransaction.getAmount(), 0);
		}
		JSONObject jsonObject = JSONObject.fromObject(transaction);
		return arTransaction == null ? "No transaction found" : jsonObject.toString();
	}
}
