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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.AuthorityToWithdraw;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AuthorityToWithdrawService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.SoToAtwDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * A controller class that will handle request for {@link AuthorityToWithdraw}

 */

@Controller
@RequestMapping(value="/authorityToWithdaw")
public class AuthorityToWithdrawController {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private AuthorityToWithdrawService atwService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String showForm(@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) {
		AuthorityToWithdraw authorityToWithdraw = new AuthorityToWithdraw();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if (pId != null) {
			authorityToWithdraw = atwService.getAuthorityToWithdraw(pId);
		} else {
			authorityToWithdraw.setDate(new Date());
		}
		return loadForm(authorityToWithdraw, user, model);
	}

	public String loadForm(AuthorityToWithdraw authorityToWithdraw, User user, Model model) {
		int companyId = authorityToWithdraw.getCompanyId() != null ? authorityToWithdraw.getCompanyId() : 0;
		model.addAttribute("companies", companyService.getCompaniesWithInactives(user, companyId));
		authorityToWithdraw.serializeSerialItems();
		authorityToWithdraw.serializeATWItems();
		authorityToWithdraw.serializeReferenceDocuments();
		authorityToWithdraw.serializeAtwLines();
		model.addAttribute("isStockIn", false);
		model.addAttribute("authorityToWithdraw", authorityToWithdraw);
		return "AuthorityToWithdrawForm.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String saveForm(@ModelAttribute ("authorityToWithdraw") AuthorityToWithdraw authorityToWithdraw, BindingResult result,
			Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		authorityToWithdraw.deserializeSerialItems();
		authorityToWithdraw.deserializeATWItems();
		authorityToWithdraw.deserializeReferenceDocuments();
		authorityToWithdraw.deserializeAtwLines();
		atwService.validateForm(authorityToWithdraw, result);
		if (result.hasErrors()) {
			return loadForm(authorityToWithdraw, user, model);
		}
		ebFormServiceHandler.saveForm(authorityToWithdraw, user);
		model.addAttribute("success", true);
		model.addAttribute("formId", authorityToWithdraw.getId());
		model.addAttribute("ebObjectId", authorityToWithdraw.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="/showReferenceForm", method=RequestMethod.GET)
	public String showReferenceForm(@RequestParam (value="companyId", required=false) Integer companyId,
			Model model, HttpSession session) {
		loadReferenceForms(companyId, null, null, null, SalesOrder.STATUS_UNUSED, PageSetting.START_PAGE, model);
		return "SOReference.jsp";
	}

	private void loadReferenceForms(Integer companyId, Integer soNumber, Integer arCustomerId,
			Integer arCustomerAcctId, Integer statusId, Integer pageNumber, Model model) {
		Page<SalesOrder> salesOrders = atwService.getSaleOrderForms(companyId, soNumber, arCustomerId,
				arCustomerAcctId, statusId, pageNumber);
		model.addAttribute("salesOrders", salesOrders);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/getSaleOrderForms", method=RequestMethod.GET)
	public String getSaleOrderForms(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="soNumber", required=false) Integer soNumber,
			@RequestParam (value="arCustomerId", required=false) Integer arCustomerId,
			@RequestParam (value="arCustomerAcctId", required=false) Integer arCustomerAcctId,
			@RequestParam (value="status", required=false) Integer statusId,
			@RequestParam (value="pageNumber") Integer pageNumber, 
			Model model, HttpSession session) {
		loadReferenceForms(companyId, soNumber, arCustomerId, arCustomerAcctId, statusId, pageNumber, model);
		return "SOReferenceTable.jsp";
	}

	@RequestMapping(value="/loadSOReferenceForm",method=RequestMethod.GET)
	public @ResponseBody String loadSOReferenceForm(@RequestParam (value="soRefId", required=true) Integer salesOrderId,
			Model model, HttpSession session) {
		SoToAtwDto soToAtwDto = atwService.convertSOtoATW(salesOrderId);
		JsonConfig jsonConfig = new JsonConfig();
		String [] excludes = {"glDate", "itemCategory", "itemSrps", "itemDiscounts", "itemAddOns", "buyingPrices",
				"buyingAddOns", "buyingDiscounts", "rItemDetails", "soItems", "formWorkflow"};
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(excludes);
		JSONObject jsonObject = JSONObject.fromObject(soToAtwDto, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value = "/viewForm", method = RequestMethod.GET)
	public String showViewForm (@RequestParam(value="pId", required = false) Integer pId, Model model) {
		AuthorityToWithdraw authorityToWithdraw = atwService.getAuthorityToWithdraw(pId);
		model.addAttribute("authorityToWithdraw", authorityToWithdraw);
		return "AuthorityToWithdrawView.jsp";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String searchForms(@RequestParam(required=true, value="criteria", defaultValue="") String searchCriteria,
			HttpSession session) {
		List<FormSearchResult> result = atwService.retrieveForms(searchCriteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/printout", method = RequestMethod.GET)
	public String generatePrintout(@RequestParam(value="pId", required=true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize,
			Model model, HttpSession session) {
		AuthorityToWithdraw authorityToWithdraw = atwService.getAuthorityToWithdraw(pId);
		List<AuthorityToWithdraw> atws = new ArrayList<AuthorityToWithdraw>();
		atws.add(authorityToWithdraw);
		JRDataSource dataSource = new JRBeanCollectionDataSource(atws);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		model.addAttribute("reportTitle", "AUTHORITY TO WITHDRAW");
		model.addAttribute("companyAddress", authorityToWithdraw.getCompany().getAddress());
		model.addAttribute("companyLogo", authorityToWithdraw.getCompany().getLogo());
		model.addAttribute("date", authorityToWithdraw.getDate());
		model.addAttribute("customerName", authorityToWithdraw.getArCustomer().getName());
		model.addAttribute("customerAddress", authorityToWithdraw.getArCustomer().getAddress());
		model.addAttribute("remarks", authorityToWithdraw.getRemarks());
		model.addAttribute("sequenceNo", authorityToWithdraw.getSequenceNumber());
		model.addAttribute("shipTo", authorityToWithdraw.getShipTo());
		model.addAttribute("driverName", authorityToWithdraw.getDriverName());
		if(authorityToWithdraw.getFleetProfile() != null) {
			model.addAttribute("plateNo", authorityToWithdraw.getFleetProfile().getPlateNo());
		}
		FormWorkflow workflowLog = authorityToWithdraw.getFormWorkflow();
		for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			String name = log.getCreated().getLastName() + ", " + log.getCreated().getFirstName();
			String position = log.getCreated().getPosition().getName();
			Date createdDate = log.getCreatedDate();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", name);
				model.addAttribute("creatorPosition", position);
				model.addAttribute("createdDate", createdDate);
			} else if (log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy", name);
				model.addAttribute("approverPosition", position);
				model.addAttribute("approvedDate", createdDate);
			}
		}
		return "AuthorityToWithdrawPdf.jasper";
	}
}
