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
import eulap.eb.domain.hibernate.CustomerType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CustomerTypeService;

/**
 * Controller class that will handle request for {@link CustomerType}

 */

@Controller
@RequestMapping("admin/customerType")
public class CustomerTypeController {
	private final Logger logger = Logger.getLogger(CustomerTypeController.class);
	@Autowired
	private CustomerTypeService customerTypeService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	/**
	 * Load the main page of the form
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(Model model) {
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		loadCustomerTypes("", "", "All", 1, model);
		return "CustomerType.jsp";
	}

	/**
	 * Load the customer type form
	 */
	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showForm(@RequestParam(value="pId", required=false) Integer pId,
			Model model) {
		CustomerType customerType = new CustomerType();
		if(pId != null) {
			logger.info("Loading customer type with id: "+pId);
			customerType = customerTypeService.getCustomerType(pId);
		} else {
			customerType.setActive(true);
		}
		model.addAttribute("customerType", customerType);
		return "CustomerTypeForm.jsp";
	}

	/**
	 * Validating and Saving the form.
	 */
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveCustomerType(@ModelAttribute("customerType") CustomerType customerType,
			BindingResult result, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		customerTypeService.validateForm(customerType, result);
		if(result.hasErrors()) {
			logger.debug("Reloading the Customer Type Form");
			return "CustomerTypeForm.jsp";
		}
		customerTypeService.saveCustomerType(user, customerType);
		logger.info("Successfully saved the Customer Type.");
		return "successfullySaved";
	}

	/**
	 * Load the customer types
	 * @param name The name of the customer type
	 * @param description The description of the customer type
	 * @param status The status of the customer type
	 * @param pageNumber The page number
	 */
	private void loadCustomerTypes(String name, String description, String status, Integer pageNumber, Model model) {
		Page<CustomerType> customerTypes = 
				customerTypeService.searchCustomerTypes(name, description, status, pageNumber);
		model.addAttribute("customerTypes", customerTypes);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	/**
	 * Search the customer type
	 * @param name The name of the customer type
	 * @param description The description of the customer type
	 * @param status The status of the customer type
	 * @param pageNumber The page number
	 * @return The customer type in a paged table format
	 */
	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchSuppliers(@RequestParam(value="name", required=false) String name,
			@RequestParam(value="description", required=false) String description,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber, Model model) {
		loadCustomerTypes(name, description, status, pageNumber, model);
		return "CustomerTypeTable.jsp";
	}
}
