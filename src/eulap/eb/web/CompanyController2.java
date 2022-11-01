package eulap.eb.web;

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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.validator.CompanyValidator;

/**
 * Controller for company module.

 */
@Controller
@RequestMapping("/admin/company")
public class CompanyController2 {
	private final Logger logger = Logger.getLogger(CompanyController2.class);
	public static final String COMPANY_ATTRIBUTE_NAME = "companies";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CompanyValidator companyValidator;

	@RequestMapping (method = RequestMethod.GET)
	public String showCompany(Model model, HttpSession session){
		logger.info("Loading company profiles.");
		loadCompany("","","", "", 1, model, CurrentSessionHandler.getLoggedInUser(session));
		return "showCompany";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method  = RequestMethod.GET, params = {"companyNumber", "name", "tin", "pageNumber", "code"})
	public String searchCompany (@RequestParam String companyNumber,@RequestParam String name, @RequestParam String tin,
			@RequestParam String code,@RequestParam int pageNumber, Model model, HttpSession session){
		loadCompany(companyNumber,name,tin, code, pageNumber, model, CurrentSessionHandler.getLoggedInUser(session));
		return "showCompanyTable";
	}

	@RequestMapping(value = "/form", method=RequestMethod.GET)
	public String addCompany(Model model){
		logger.info("Adding company");
		Company comp = new Company();
		comp.setActive(true);
		return showCompanyForm(comp, model);
	}

	private void loadCompany(String companyNumber, String name, String tin, String code , int pageNumber, Model model, User user){
		Page<Company> companyList = companyService.getCompanyList(companyNumber, name, tin, code, user, pageNumber);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute(COMPANY_ATTRIBUTE_NAME, companyList);
	}

	@RequestMapping  (value = "/form", method = RequestMethod.GET, params = {"companyId"})
	public String editCompany(@RequestParam int companyId,	Model model){
		logger.info("Edit company..........:" + companyId);
		return showCompanyForm(companyService.getCompany(Integer.valueOf(companyId)), model);
	}

	@RequestMapping (value = "/form", method = RequestMethod.POST)
	public String saveCompany(@ModelAttribute("company") Company company, BindingResult result , HttpSession session, Model model){
		logger.info("Saving company");
		companyValidator.validate(company, CurrentSessionHandler.getLoggedInUser(session), result);
		if(result.hasErrors())
			return showCompanyForm(company, model);
		saveCompany(company, session) ;
		model.addAttribute("message", "Sucessfully saved.");
		return "successfullySaved";
	}

	private String showCompanyForm(Company comp, Model model){
		model.addAttribute(comp);
		return "showCompanyForm";
	}

	private void saveCompany (Company c, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isNewRecord = c.getId() == 0 ? true : false;
		companyService.save(c, user, isNewRecord);
	}
}