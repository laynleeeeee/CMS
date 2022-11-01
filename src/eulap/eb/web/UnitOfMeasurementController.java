package eulap.eb.web;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.UnitMeasurementService;
import eulap.eb.validator.UnitMeasurementValidator;

/**
 * Controller for unit of measurement.

 */

@Controller
@RequestMapping("/admin/unitMeasurement")
public class UnitOfMeasurementController {

	@Autowired
	private UnitMeasurementService unitMeasurementService;
	@Autowired
	private UnitMeasurementValidator unitMeasurementValidator;

	private final Logger logger = Logger.getLogger(UnitOfMeasurementController.class);

	@RequestMapping (method = RequestMethod.GET)
	public String showMeasurementMainForm(Model model, HttpSession session) {
		model.addAttribute("unitMeasurements", unitMeasurementService.searchUnitMeasurements("", true, true, 1));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		return "MeasurementMain.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form")
	public String showMeasurementForm(Model model) {
		UnitMeasurement unitMeasurement = new UnitMeasurement();
		unitMeasurement.setActive(true);
		model.addAttribute("unitMeasurement", unitMeasurement);
		return "MeasurementForm.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form", params = {"unitMeasurementId"})
	public String editMeasurementForm(@RequestParam int unitMeasurementId, Model model) {
		UnitMeasurement unitMeasurement = unitMeasurementService.getUnitMeasurement(unitMeasurementId);
		model.addAttribute("unitMeasurement", unitMeasurement);
		return "MeasurementForm.jsp";
	}

	@RequestMapping (method = RequestMethod.POST, value = "/form")
	public String saveUnitMeasurement (@ModelAttribute ("unitMeasurement") UnitMeasurement unitMeasurement, 
			BindingResult result, Model model, HttpSession session){
			User user = CurrentSessionHandler.getLoggedInUser(session);
			unitMeasurementValidator.validate(unitMeasurement, result, unitMeasurement.getId());
			if (result.hasErrors())
				return "MeasurementForm.jsp";
			unitMeasurementService.saveUnitMeasurement(unitMeasurement, user);
			model.addAttribute("message", "Sucessfully saved.");
			return "successfullySaved";
	}

	@RequestMapping(method = RequestMethod.GET, params = {"name" , "isActive" , "isAllSelected", "pageNumber"})
	public String searchUnitMeasurements(@RequestParam String name, @RequestParam boolean isActive, @RequestParam boolean isAllSelected,
			@RequestParam int pageNumber, Model model, HttpSession session) {
		Page<UnitMeasurement> searchMeasurements = unitMeasurementService.searchUnitMeasurements(name.trim(), isActive, isAllSelected, pageNumber);
		model.addAttribute("unitMeasurements", searchMeasurements);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		logger.info("Search for measurements");
		return "MeasurementFormTable.jsp";
	}
}
