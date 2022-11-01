package eulap.eb.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.WtaxAcctSettingService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Common controller class that will handle requests for {@link WithholdingTaxAcctSetting}

 */

@Controller
@RequestMapping(value="/getWithholdingTaxAcct")
public class GetWithholdingTaxAcct {
	@Autowired
	private WtaxAcctSettingService wtaxAcctSettingService;

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String getWtAcctSettings(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="isCreditable", required=false) Boolean isCreditable) {
		List<WithholdingTaxAcctSetting> wtAcctSettings = wtaxAcctSettingService.getWtAccountSettings(companyId, divisionId, isCreditable);
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(wtAcctSettings, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(method=RequestMethod.GET, value="/getWtPercentage")
	public @ResponseBody String getWtAcctSettValue(@RequestParam(value="wtAccountSettingId") int wtAccountSettingId) {
		double wtPercentage = 0;
		WithholdingTaxAcctSetting wtAcctSetting = wtaxAcctSettingService.getWtAccountSetting(wtAccountSettingId);
		if (wtAcctSetting != null) {
			wtPercentage = wtAcctSetting.getValue();
			wtAcctSetting = null;
		}
		return NumberFormatUtil.format(wtPercentage);
	}
}
