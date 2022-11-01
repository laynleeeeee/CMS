package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

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
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.CustomerTypeService;
import eulap.eb.validator.ArCustomerValidator;

/**
 * Controller class that will handle the requests for AR Customer.

 *
 */
@Controller
@RequestMapping("/admin/arCustomer")
public class ArCustomerController {
	@Autowired
	private ArCustomerService customerService;
	@Autowired
	private ArCustomerValidator customerValidator;
	@Autowired
	private CustomerTypeService customerTypeService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	/**
	 * Load the AR Customer main page
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm (Model model) {
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		model.addAttribute("classifications", customerService.getBusinessClassifications(null));
		return "ArCustomer.jsp";
	}

	/**
	 * Load the AR Customer form
	 */
	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) {
		ArCustomer arCustomer = new ArCustomer();
		arCustomer.setActive(true);
		if (pId != null) {
			arCustomer = customerService.getCustomer(pId);
		}
		return loadForm(arCustomer, model);
	}

	private String loadForm(ArCustomer arCustomer, Model model) {
		model.addAttribute("classifications", customerService.getBusinessClassifications(arCustomer.getBussinessClassificationId()));
		model.addAttribute("customerTypes", customerTypeService.getCustomerTypesWithInactive(arCustomer.getCustomerTypeId()));
		model.addAttribute("arCustomer", arCustomer);
		return "ArCustomerForm.jsp";
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchSuppliers(@RequestParam(value="bussClassId", required=false) Integer bussClassId,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="address", required=false) String address,
			@RequestParam(value="streetBrgy", required=false) String streetBrgy,
			@RequestParam(value="cityProvince", required=false) String cityProvince,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber") int pageNumber, Model model) {
		Page<ArCustomer> arCustomers = 
				customerService.searchCustomers(bussClassId, name, streetBrgy, cityProvince, status, pageNumber);
		model.addAttribute("arCustomers", arCustomers);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "ArCustomerTable.jsp";
	}

	/**
	 * Validating and Saving the form
	 */
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveCustomer(@ModelAttribute("arCustomer") ArCustomer customer, BindingResult result,
			Model model, HttpSession session) {
		customerValidator.validate(customer, result);
		if (result.hasErrors()) {
			return loadForm(customer, model);
		}
		User user = CurrentSessionHandler.getLoggedInUser(session);
		customerService.saveArCustomer(user, customer);
		return "successfullySaved";
	}
}
