package eulap.eb.web.report;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.report.SupplierBalancesParam;
import eulap.eb.service.report.SupplierBalancesReportServiceImpl;
import eulap.eb.web.dto.SupplierBalancesReportDto;
import bp.web.ar.CurrentSessionHandler;
/**
 * A class that handles the supplier balances report generation.

 */

@Controller
@RequestMapping("supplierBalancesReport")
public class SupplierBalancesReportController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private SupplierBalancesReportServiceImpl reportServiceImpl;
	@RequestMapping(method = RequestMethod.GET)
	public String showSupplierBalancesReport(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		//Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);

		return "SupplierBalancesReport.jsp";
	}

	@RequestMapping (value="/generate", method=RequestMethod.GET)
	public @ResponseBody String generateReport (@RequestParam (value="companyId", required=true) int companyId, 
			@RequestParam (value="supplierId", required=false, defaultValue="-1") int supplierId,
			@RequestParam (value="supplierAccountId", required=false, defaultValue="-1") int supplierAccountId,
			@RequestParam (value="asOfDate", required=false) String asOfDate,
			@RequestParam (value="sSearch", required=false) String sSearch,
			@RequestParam (value="sEcho", required=false) int sEcho,
			@RequestParam (value="iDisplayLength", required=false) int iDisplayLength,
			@RequestParam (value="iDisplayStart", required=false) int iDisplayStart,
			@RequestParam (value="iSortingCols", required=false) int iSortingCols,
			HttpSession session) {
		int pageNumber = (iDisplayStart/iDisplayLength) + 1;
		PageSetting pageSetting = new PageSetting(pageNumber, iDisplayLength);
		User user = CurrentSessionHandler.getLoggedInUser(session);

		SupplierBalancesParam param = new SupplierBalancesParam();
		param.setCompanyId(companyId);
		param.setSupplierId(supplierId);
		param.setSupplierAccountId(supplierAccountId);
		param.setAsOfDate(DateUtil.parseDate(asOfDate));
		Page<SupplierBalancesReportDto> supplierBalancesDto =
				reportServiceImpl.generateReport(user, pageSetting, param);
		supplierBalancesDto.setsEcho(sEcho);
		return supplierBalancesDto.toJSONObject();
	}
}
