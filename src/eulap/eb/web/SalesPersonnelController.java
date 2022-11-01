package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.SalesPersonnel;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.SalesPersonnelService;

/**
 * Controller class that will handle request for {@link SalesPersonnel}

 */

@Controller
@RequestMapping("admin/salesPersonnel")
public class SalesPersonnelController {
	private final Logger logger = Logger.getLogger(SalesPersonnelController.class);
	@Autowired
	private SalesPersonnelService salesPersonnelService;
	@Autowired
	private CompanyService companyService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		loadSalesPersonnel(0, "", "All", 1, model, user);
		logger.info("Loading sales personnel form.");
		return "SalesPersonnel.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		SalesPersonnel salesPersonnel = new SalesPersonnel();
		Integer companyId = salesPersonnel.getCompanyId() == null ? 0 : salesPersonnel.getCompanyId();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if(pId != null) {
			salesPersonnel = salesPersonnelService.getSalesPersonnel(pId);
			logger.info("Loading sales personnel : "+salesPersonnel.getName());
		} else {
			salesPersonnel.setActive(true);
		}
		loadSalesPersonnel(companyId, "", "All", 1, model, user);
		model.addAttribute("salesPersonnel", salesPersonnel);
		return "SalesPersonnelForm.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String save(@ModelAttribute("salesPersonnel") SalesPersonnel salesPersonnel,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		salesPersonnelService.validateForm(salesPersonnel, result);
		if(result.hasErrors()) {
			logger.debug("Reloading sales personnel form.");
			loadSalesPersonnel(salesPersonnel.getCompanyId(), "", "All", 1, model, user);
			return "SalesPersonnelForm.jsp";
		}
		salesPersonnelService.saveSalesPersonnel(user, salesPersonnel);
		logger.info("Successfully saved the sales personnel.");
		return "successfullySaved";
	}

	private void loadSalesPersonnel(Integer companyId, String name,  String status,  
			Integer pageNumber, Model model, User user) {
		Page<SalesPersonnel> salesPersonnels = 
				salesPersonnelService.searchSalesPersonnel(companyId, name, status, pageNumber);
		model.addAttribute("salesPersonnels", salesPersonnels);
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String search(@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSalesPersonnel(companyId, name, status, pageNumber, model, user);
		return "SalesPersonnelTable.jsp";
	}
}
