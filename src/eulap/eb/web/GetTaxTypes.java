package eulap.eb.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.TaxType;
import eulap.eb.service.TaxTypeService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * A controller class that will handle requests for {@link TaxType}

 */

@Controller
@RequestMapping("/getTaxTypes")
public class GetTaxTypes {
	@Autowired
	private TaxTypeService taxTypeService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getTaxTypes(@RequestParam(value="taxTypeId", required=false) Integer taxTypeId,
			HttpSession session) {
		List<TaxType> taxTypes = taxTypeService.getTaxTypes(taxTypeId);
		JSONArray jsonArray = JSONArray.fromObject(taxTypes, new JsonConfig());
		return jsonArray.toString();
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public @ResponseBody String getTaxTypes(@RequestParam(value="isReceivable") boolean isReceivable,
			@RequestParam(value="taxTypeId", required=false) Integer taxTypeId,
			@RequestParam(value="isImportation", required=false) boolean isImportation,
			HttpSession session) {
		List<TaxType> taxTypes = new ArrayList<TaxType>();
		if (isReceivable) {
			taxTypes = taxTypeService.getAcctReceivableTaxTypes(taxTypeId);
		} else {
			if(isImportation) {
				taxTypes = taxTypeService.getAcctPayableTaxTypesWithImportation(taxTypeId, isImportation);
			} else {
				taxTypes = taxTypeService.getAcctPayableTaxTypes(taxTypeId);
			}
		}
		JSONArray jsonArray = JSONArray.fromObject(taxTypes, new JsonConfig());
		return jsonArray.toString();
	}
}
