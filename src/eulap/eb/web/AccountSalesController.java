package eulap.eb.web;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;

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
import eulap.eb.domain.hibernate.AccountSales;
import eulap.eb.domain.hibernate.AccountSalesPoItem;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountSalePoService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.validator.inv.AccountSalesPOValidator;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Controller class for Account Sales

 *
 */
@Controller
@RequestMapping("/accountSales")
public class AccountSalesController {
	private static Logger logger = Logger.getLogger(AccountSalesController.class);
	@Autowired
	private AccountSalePoService accountSalePoService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private AccountSalesPOValidator accountSalesPOValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String loadASForm(@RequestParam(value="pId", required=false) Integer pId, HttpSession session, Model model) {
		logger.info("Showing the Account Sales form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		AccountSales accountSales = new AccountSales();
		if(pId != null) {
			logger.debug("Loading the AS PO form: "+pId);
			accountSales = accountSalePoService.getAccountSales(pId);
		} else {
			Date currentDate = new Date();
			accountSales.setPoDate(currentDate);
			accountSales.setAsPoItems(new ArrayList<AccountSalesPoItem>());
		}
		accountSales.serializeItems();
		model.addAttribute("accountSales", accountSales);
		loadSelections(model, user);
		return "AccountSalesForm.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveAccountSales(@ModelAttribute("accountSales") AccountSales accountSales,
			BindingResult result, Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		logger.info("Saving the Account Sales.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		accountSales.deSerializeItems();
		accountSalesPOValidator.validate(accountSales, result);
		if(result.hasErrors()) {
			logger.debug("Form has error/s. Reloading the Account Sales form.");
			loadSelections(model, user);
			return "AccountSalesForm.jsp";
		}
		ebFormServiceHandler.saveForm(accountSales, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", accountSales.getPoNumber());
		model.addAttribute("formId", accountSales.getId());
		model.addAttribute("ebObjectId", accountSales.getEbObjectId());
		return "successfullySaved";
	}

	private void loadSelections(Model model, User user) {
		model.addAttribute("companies", companyService.getCompanies(user));
	}

	@RequestMapping(value="/viewForm", method=RequestMethod.GET)
	public String viewAccountSales(@RequestParam(value="pId", required=false) Integer pId, Model model) {
		logger.info("Loading the view form of Account Sales");
		model.addAttribute("accountSales", accountSalePoService.getAccountSales(pId));
		return "AccountSalesView.jsp";
	}

	@RequestMapping(value="/pdf", method=RequestMethod.GET)
	public String generatePrintout(@RequestParam(value="pId") int pId, Model model, HttpSession session) {
		AccountSales accountSales = accountSalePoService.getAccountSales(pId);
		logger.info("Generating Account sales printout.");
		getParams(accountSales, model, "pdf", session);
		return "AccountSalesPDF.jasper";
	}

	private void getParams(AccountSales accountSales, Model model, String format, HttpSession session) {
		JRDataSource dataSource = new JRBeanCollectionDataSource(accountSales.getAsPoItems());
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", format);
		logger.debug("Successfully generated the datasource of the printout.");

		Company company = accountSales.getCompany();
		if(format == "pdf") {
			model.addAttribute("companyLogo", company.getLogo());
		} else {
			model.addAttribute("companyLogo", session.getServletContext().getContextPath() +"/images/logo/"+ company.getLogo());
		}
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("customer", accountSales.getArCustomer().getName());
		model.addAttribute("customerAccount", accountSales.getArCustomerAccount().getName());
		model.addAttribute("poNumber", accountSales.getFormattedPONumber());
		model.addAttribute("poDate", accountSales.getPoDate());
		model.addAttribute("remarks", accountSales.getRemarks());

		FormWorkflow workflowLog = accountSales.getFormWorkflow();
		for (FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				String name = log.getCreated().getFirstName() + " " + log.getCreated().getLastName();
				model.addAttribute("createdBy", name);
				model.addAttribute("createdByPosition", log.getCreated().getPosition().getName());
			}
		}
		logger.debug("Successfully generated the parameters for the printout.");
		logger.info("Successfully generated the printout.");
	}
}
