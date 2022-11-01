package eulap.eb.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.CurrencyRate;
import eulap.eb.service.CurrencyRateService;
import net.sf.json.JSONObject;

/**
 * Controller that will retrieve the currency

 */

@Controller
@RequestMapping("getCurrency")
public class GetCurrency {
	@Autowired
	private CurrencyRateService currencyRateService;

	@RequestMapping(value="/getLatestRate", method = RequestMethod.GET)
	public @ResponseBody String getCompanyByName (@RequestParam (value="currencyId") Integer currencyId,
			HttpSession session) {
		CurrencyRate rate = currencyRateService.getLatestRate(currencyId);
		JSONObject jsonObject = JSONObject.fromObject(rate);
		return rate == null ? "No rate found" : jsonObject.toString();
	}
}
