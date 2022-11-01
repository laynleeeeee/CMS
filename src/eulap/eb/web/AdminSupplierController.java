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
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.SupplierService;
import eulap.eb.validator.SupplierValidator;

/**
 * YBL Supplier controller class.

 */
@Controller
@RequestMapping("/admin/supplier")
public class AdminSupplierController {
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private SupplierValidator supplierValidator;
	private final Logger logger = Logger.getLogger(AdminSupplierController.class);

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method=RequestMethod.GET)
	public String showSuppliers(HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Page<Supplier> suppliers = supplierService.loadSuppliers(user.getServiceLeaseKeyId(), 1);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		model.addAttribute("busRegType", supplierService.getAllBusRegType());
		model.addAttribute("classifications", supplierService.getBusinessClassifications(null));
		List<String> searchStatus = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatus);
		logger.info("Show suppliers.");
		return "supplier.jsp";
	}

	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String searchSuppliers(@RequestParam(value="bussinessClassificationId", required=false) Integer bussinessClassificationId,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="streetBrgy", required=false) String streetBrgy,
			@RequestParam(value="cityProvince", required=false) String cityProvince,
			@RequestParam(value="busRegType", required=false) int busRegType,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber") int pageNumber, HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Page<Supplier> suppliers = supplierService.searchSuppliers(bussinessClassificationId, name, streetBrgy, cityProvince, status, busRegType,
				user.getServiceLeaseKeyId(), pageNumber);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		logger.info("Search suppliers.");
		return "supplierTable.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form")
	public String addSupplierForm(Model model) {
		Supplier supplier = new Supplier();
		supplier.setActive(true);
		return showSupplierForm(supplier, model);
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form", params = {"supplierId"})
	public String editSupplierForm(@RequestParam int supplierId, Model model) {
		Supplier supplier = supplierService.getSupplier(supplierId);
		return showSupplierForm(supplier, model);
	}

	private String showSupplierForm(Supplier supplier, Model model){
		model.addAttribute("busRegType", supplierService.getAllBusRegType());
		model.addAttribute("classifications", supplierService.getBusinessClassifications(supplier.getBussinessClassificationId()));
		model.addAttribute(supplier);
		return "supplierForm.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveSupplier(@ModelAttribute("supplier") Supplier supplier, BindingResult result,
			HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		supplierValidator.validate(supplier, user, result);
		if(result.hasErrors()) {
			return showSupplierForm(supplier, model);
		}
		supplierService.saveSupplier(user, supplier);
		logger.info("Successfully saved the supplier with name: "+supplier.getName());
		return "successfullySaved";
	}
}
