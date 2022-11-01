package bp.web.ar;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.LabelName2FieldNameCompany;
import eulap.eb.validator.CompanyValidator;

import java.util.List;

/**
 * Controller for company module. Handles the adding, updating and deleting of company. 

 *
 */
@Controller
@RequestMapping ("/company")
public class CompanyController {
	public static final String COMPANY_ATTRIBUTE_NAME = "companies";
	private final CompanyService companyService;
	private final CompanyValidator companyValidator;
	private final Logger logger = Logger.getLogger(Company.class);

	@Autowired
	public CompanyController (CompanyService companyService,
			CompanyValidator companyValidator){
		this.companyService = companyService;
		this.companyValidator = companyValidator;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
         DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showCompanyList (Model model, HttpSession currentSession) {
		List<String> searchCompanyOptionCategory = LabelName2FieldNameCompany.getLabels();
		List<String> searchCompanyOptionStatus = SearchStatus.getSearchStatus();

		logger.info("GET: Show companyList");
		Page<Company> page = companyService.getCompanies("*", "name", 1);

		model.addAttribute("searchCompanyOptionCategory", searchCompanyOptionCategory);
		model.addAttribute("searchCompanyOptionStatus", searchCompanyOptionStatus);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		model.addAttribute(COMPANY_ATTRIBUTE_NAME, page);
		return "showCompanyList";
	}

	@RequestMapping (method = RequestMethod.GET, params = {"searchText", "category", "status", "page"})
	public String searchCompany (@RequestParam ("searchText") String searchText, 
			@RequestParam ("category") String category, 
			@RequestParam ("status") String status,
			@RequestParam ("page") int page, 
			Model model, HttpSession currentSession) {
		logger.info("GET: Search company");
		PageSetting pageSetting = new PageSetting(page);
		Page<Company> pageCompany = companyService.searchCompanies(searchText, category,
				status, pageSetting);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, page);
		model.addAttribute(COMPANY_ATTRIBUTE_NAME, pageCompany);
		return "showCompanyTable";
	}

	@RequestMapping (value = "/add", method = RequestMethod.GET)
	public String addCompany (Model model, HttpSession session) {
		logger.info("Show company form");
		Company company = new Company ();
		model.addAttribute(company);
		return "addCompany";
	}

	@RequestMapping (value = "/add", method = RequestMethod.POST)
	public String saveCompany (@ModelAttribute ("company") Company company,
			BindingResult result, Model model, HttpSession session) {
		companyValidator.validate(company, result);
		if (result.hasErrors())
			return "addCompany";
		saveCompany(company, true, session);
		logger.info("Successfully saved the company: "+company);
		return "successfullySaved";
	}

	@RequestMapping (value = "/edit/{companyId}", method = RequestMethod.GET)
	public String editCompany (@PathVariable ("companyId") String companyId,
			Model model, HttpSession session) {
		logger.info("Editing company : " + companyId);
		Company company = companyService.getCompany(Integer.valueOf(companyId));
		model.addAttribute(company);
		return "editCompany";
	}

	@RequestMapping (value = "/edit/{companyId}", method = RequestMethod.POST)
	public String saveCompany (@PathVariable ("companyId") String companyId, 
			@ModelAttribute ("company") Company company , BindingResult result, 
			Model model, HttpSession session) {
		companyValidator.validate(company, result);
		if (result.hasErrors())
			return "editCompany";
		saveCompany(company, false, session) ;
		logger.info("Successfully edited the company"+company);
		return "successfullySaved";
	}

	@RequestMapping (value = "/{pageNumber}/print", method = RequestMethod.GET)
	public String printCompany (@PathVariable ("pageNumber") int pageNumber, 
			Model model, HttpSession session) {
		logger.info("Printing...");
		Page<Company> page = companyService.getCompanies("", "name", pageNumber);
		model.addAttribute(COMPANY_ATTRIBUTE_NAME, page);
		return "printCompany";
	}

	/**
	 * Factored save function fragment used both by add and edit functions.
	 * @param c The company.
	 */
	private void saveCompany (Company c, boolean isNewRecord, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		companyService.save(c, user, isNewRecord);
	}
}