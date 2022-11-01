package eulap.eb.web;

import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.service.ApPaymentService;

/**
 * Controller class for retrieving the AP Invoices

 */
@Controller
@RequestMapping(value="/getApInvoices")
public class GetApInvoices {
	@Autowired
	private ApPaymentService apPaymentService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getInvoicesBySupplierAccount(@RequestParam(value="supplierAccountId") int supplierAcctId,
			@RequestParam(value="invoiceNumber", required=false) String invoiceNumber,
			@RequestParam(value="invoiceIds", required=false) String invoiceIds) {
		Collection<APInvoice> invoices = apPaymentService.loadApprovedInvoices(supplierAcctId, invoiceNumber, invoiceIds);
		String [] excludes = {"aPlines", "supplier", "supplierAccount", "term", "apLineDtos", "rrItems", "rtsItems",
				"receivingReport", "returnToSupplier", "rrItemsJson", "rtsItemsJson", "serviceLeaseKeyId", "companyId", "objectTypeId"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONArray jsonArray = JSONArray.fromObject(invoices, jsonConfig);
		return jsonArray.toString();
	}
}