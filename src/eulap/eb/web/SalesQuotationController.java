package eulap.eb.web;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SalesQuotation;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.SalesQuotationService;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;

/**
 * Controller class that will handle requests for {@link SalesQuotation}

 */

@Controller
@RequestMapping(value="/salesQuotation")
public class SalesQuotationController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private SalesQuotationService salesQuotationService;

	private static final String DEFAUL_GEN_CON = "The price/rate are quoted in Philippine Peso Currency.;Above given rates are exclusive of 12% VAT.;"
			+ "Duly approved Purchase Order (P.O) or Job Order (J.O) shall be required before we render our services or deliver our goods.;";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String showForm(@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) {
		SalesQuotation salesQuotation = new SalesQuotation();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if (pId != null) {
			salesQuotation = salesQuotationService.getSalesQuotation(pId);
		} else {
			salesQuotation.setDate(new Date());
			salesQuotation.setGeneralConditions(DEFAUL_GEN_CON);
		}
		return loadForm(salesQuotation, user, model);
	}

	public String loadForm(SalesQuotation salesQuotation, User user, Model model) {
		int companyId = salesQuotation.getCompanyId() != null ? salesQuotation.getCompanyId() : 0;
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		salesQuotation.serializeSQItems();
		salesQuotation.serializeSQLines();
		salesQuotation.serializeSQTLines();
		salesQuotation.serializeSQELines();
		model.addAttribute("salesQuotation", salesQuotation);
		return "SalesQuotationForm.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String saveForm(@ModelAttribute ("salesQuotation") SalesQuotation salesQuotation, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		salesQuotation.deserializeSQItems();
		salesQuotation.deserializeSQLines();
		salesQuotation.deserializeSQTLines();
		salesQuotation.deserializeSQELines();
		salesQuotationService.validateForm(salesQuotation, result);
		if (result.hasErrors()) {
			return loadForm(salesQuotation, user, model);
		}
		ebFormServiceHandler.saveForm(salesQuotation, user);
		model.addAttribute("success", true);
		model.addAttribute("formId", salesQuotation.getId());
		model.addAttribute("ebObjectId", salesQuotation.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value = "/viewForm", method = RequestMethod.GET)
	public String showViewForm (@RequestParam(value="pId", required = false) Integer pId, Model model) {
		SalesQuotation salesQuotation = salesQuotationService.getSalesQuotation(pId);
		model.addAttribute("salesQuotation", salesQuotation);
		return "SalesQuotationView.jsp";
	}

	@RequestMapping(value = "/getCustomerShipTo", method = RequestMethod.GET)
	public @ResponseBody String getCustomerShipTo(@RequestParam(value="arCustomerId", required=false) Integer arCustomerId,
			HttpSession session) {
		String shipTo = salesQuotationService.getCustomerShipTo(arCustomerId);
		return shipTo != null ? shipTo : "";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String searchForms(@RequestParam(required=true, value="criteria", defaultValue="") String searchCriteria,
			HttpSession session) {
		List<FormSearchResult> result = salesQuotationService.retrieveForms(searchCriteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/printout", method = RequestMethod.GET)
	public String generatePrintout(@RequestParam(value="pId", required=true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session) {
		SalesQuotation salesQuotation = salesQuotationService.getSalesQuotation(pId);
		List<SalesQuotation> salesQuotations = new ArrayList<SalesQuotation>();
		salesQuotations.add(salesQuotation);
		JRDataSource dataSource = new JRBeanCollectionDataSource(salesQuotations);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		model.addAttribute("reportTitle", "QUOTATION");
		model.addAttribute("companyAddress", salesQuotation.getCompany().getAddress());
		model.addAttribute("companyLogo", salesQuotation.getCompany().getLogo());
		model.addAttribute("date", salesQuotation.getDate());
		model.addAttribute("customerName", salesQuotation.getArCustomer().getName());
		model.addAttribute("customerAddress", salesQuotation.getArCustomer().getAddress());
		model.addAttribute("subject", salesQuotation.getSubject());
		model.addAttribute("fromName", "Nonito M. Cabais");
		model.addAttribute("fromPosition", "President / General Manager");
		model.addAttribute("sequenceNo", salesQuotation.getSequenceNumber());
		model.addAttribute("generalConditions", salesQuotationService.getGeneralConditions(
				salesQuotation.getGeneralConditions()));

		FormWorkflow workflowLog = salesQuotation.getFormWorkflow();
		for (FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			String name = log.getCreated().getFirstName() + " " + log.getCreated().getLastName();
			String position = log.getCreated().getPosition().getName();
			Date date = log.getCreatedDate();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", name);
				model.addAttribute("createdByPosition", position);
				model.addAttribute("createdDate", date);
			}
			if(log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy", name);
				model.addAttribute("approvedByPosition", position);
				model.addAttribute("approvedDate", date);
			}
		}
		return "SalesQuotationPdf.jasper";
	}

}