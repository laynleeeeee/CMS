package eulap.eb.web;

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
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.FleetInsurancePermitRenewalService;
import eulap.eb.service.FleetTypeService;
import eulap.eb.web.dto.FleetInsurancePermitRenewalDto;

/**
 * Controller for fleet insurance permit renewal service

 *
 */
@Controller
@RequestMapping ("/insurancePermitRenewal")
public class FleetInsurancePermitRenewalCtrlr {
	@Autowired
	private FleetInsurancePermitRenewalService fleetInsurancePermitRenewalService;
	@Autowired
	private FleetTypeService fleetTypeService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showNewForm (@RequestParam(value="refObjectId", required=false) Integer refObjectId,
			Model model, HttpSession session) {
		FleetInsurancePermitRenewalDto insurancePermitRenewalDto = new FleetInsurancePermitRenewalDto();
		if(refObjectId != null){
			insurancePermitRenewalDto.setInsurancePermitRenewals(fleetInsurancePermitRenewalService.getFleetIPRByRefObjectId(refObjectId));
			insurancePermitRenewalDto.setReferenceDocuments(fleetInsurancePermitRenewalService.getReferenceDocumentByRefObjectId(refObjectId));
			insurancePermitRenewalDto.setFleetTypeId(fleetTypeService.getFleetTypeByReferenceObjectId(refObjectId).getId());
		}
		insurancePermitRenewalDto.setFpEbObjectId(refObjectId);
		return loadIPR(insurancePermitRenewalDto, model);
	}

	@RequestMapping (method = RequestMethod.POST)
	public String saveInsurance (@ModelAttribute("insurancePermitRenewalDto") FleetInsurancePermitRenewalDto insurancePermitRenewalDto,
			BindingResult result, Model model,
			HttpSession session) {
		insurancePermitRenewalDto.deserializeInsurancePermitRenewals();
		insurancePermitRenewalDto.deserializeReferenceDocuments();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		fleetInsurancePermitRenewalService.validate(insurancePermitRenewalDto, result);
		if(result.hasErrors()){
			return loadIPR(insurancePermitRenewalDto, model);
		}
		fleetInsurancePermitRenewalService.saveIPR(insurancePermitRenewalDto, user);
		model.addAttribute("success", true);
		model.addAttribute("formNumber", insurancePermitRenewalDto.getFpEbObjectId());
		model.addAttribute("formId", insurancePermitRenewalDto.getFpEbObjectId());
		return "FormSuccess.jsp";
	}

	private String loadIPR(FleetInsurancePermitRenewalDto insurancePermitRenewalDto, Model model){
		insurancePermitRenewalDto.serializeInsurancePermitRenewals();
		insurancePermitRenewalDto.serializeReferenceDocuments();
		model.addAttribute("insurancePermitRenewalDto", insurancePermitRenewalDto);
		return "FleetIprForm.jsp";
	}
}
