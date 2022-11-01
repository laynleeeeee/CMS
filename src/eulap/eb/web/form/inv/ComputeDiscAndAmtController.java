package eulap.eb.web.form.inv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.service.ArTransactionService;
import eulap.eb.service.CashSaleService;

/**
 * Controller that triggers the computation of discount and amount.

 *
 */
@Controller
@RequestMapping ("/computeDiscountAndAmount")
public class ComputeDiscAndAmtController {
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private ArTransactionService arTransactionService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String computeDiscountAndAmount () {
		cashSaleService.fixDiscAndAmount();
		arTransactionService.fixDiscAndAmount();
		return "Successfully re computed discount and amount.";
	}
}
