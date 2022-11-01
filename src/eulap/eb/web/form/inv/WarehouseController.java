package eulap.eb.web.form.inv;

import java.io.InvalidClassException;
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
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.WarehouseService;
import eulap.eb.validator.inv.WarehouseValidator;
import eulap.eb.web.dto.WarehouseDto;

/**
 * Controller for admin warehouse.


 *
 */
@Controller
@RequestMapping("/admin/warehouses")
public class WarehouseController {
	private final Logger logger = Logger.getLogger(WarehouseController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private WarehouseValidator warehouseValidator;
	@Autowired
	private DivisionService divisionService;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showWarehouses(Model model, HttpSession session){
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		loadCompanies(session, model, 0);
		loadDivisions(session, model, 0);
		loadWarehouses(model, -1, -1, "", "", SearchStatus.All.name(), 1);
		List<String> searchStatuses = SearchStatus.getSearchStatus();
		model.addAttribute("status", searchStatuses);
		return "Warehouse.jsp";
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchWarehouses(
			@RequestParam(value="companyId", required=false) Integer companyId,
			@RequestParam(value="divisionId", required=false) Integer divisionId,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="address", required=false) String address,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="page") Integer pageNumber,
			HttpSession session, Model model) {
		logger.debug(status);
		loadWarehouses(model, companyId, divisionId, name, address, status, pageNumber);
		return "WarehouseTable.jsp";
	}

	private void loadWarehouses(Model model, Integer companyId, Integer divisionId, String name, String address, 
			String status, Integer pageNumber) {
		Page<WarehouseDto> warehouses = warehouseService.getWarehouseWithSubs(companyId, divisionId, 
				name, address, status, pageNumber);
		model.addAttribute("warehouses", warehouses);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showWarehouseForm(@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) {
		Warehouse warehouse = new Warehouse();
		warehouse.setActive(true);
		int companyId = 0;
		int divisionId = 0;
		if (pId != null) {
			logger.info("Editing the Warehouse: "+pId);
			warehouse = warehouseService.getWarehouse(pId);
			companyId = warehouse.getCompanyId();
			divisionId = warehouse.getDivisionId();
		}
		model.addAttribute("warehouse", warehouse);
		loadCompanies(session, model, companyId);
		loadDivisions(session, model, divisionId);
		logger.info("Showing the Warehouse form.");
		return "WarehouseForm.jsp";
	}

	private void loadDivisions(HttpSession session, Model model, Integer divisionId) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("divisions", divisionService.getActiveDivsions(user, divisionId != null ? divisionId : 0));
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveWarehouse(@ModelAttribute("warehouse") Warehouse warehouse,
			BindingResult result, HttpSession session, Model model) {
		logger.info("Saving the warehouse form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		warehouseValidator.validate(warehouse, result);
		if (result.hasErrors()) {
			loadCompanies(session, model, warehouse.getCompanyId());
			loadDivisions(session, model, warehouse.getDivisionId());
			model.addAttribute("warehouse", warehouse);
			return "WarehouseForm.jsp";
		}
		warehouseService.saveWarehouse(warehouse, user);
		return "successfullySaved";
	}

	private void loadCompanies(HttpSession session, Model model, int companyId) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
	}

	@RequestMapping(value="/showChildren", method=RequestMethod.GET)
	public String addChildWarehouses(@RequestParam(value="warehouseId") Integer warehouseId,
			@RequestParam(value="status", required=false) String status,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		model.addAttribute("warehouses", warehouseService.getAllChildren(warehouseId, status));
		return "AddChildWarehouse.jsp";
	}
}