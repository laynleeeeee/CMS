package eulap.eb.web.form.inv;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

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
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.CAPDelivery;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.CAPDeliveryService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CustomerAdvancePaymentService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.fsp.CAPDeliverySearcher;
import eulap.eb.validator.inv.CAPDeliveryValidator;
import eulap.eb.web.dto.CapDeliveryDto;
import eulap.eb.web.dto.FormSearchResult;

/**
 * Class that will be the entry point for CAP Delivery related transactions.

 *
 */
@Controller
@RequestMapping("/capDelivery")
public class CAPDeliveryController {
	private final Logger logger = Logger.getLogger(CAPDeliveryController.class);
	private final String CAP_DELIVERY_ATTRIB_NAME = "capDelivery";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CAPDeliveryService deliveryService;
	@Autowired
	private CAPDeliverySearcher deliverySearcher;
	@Autowired
	private CAPDeliveryValidator deliveryValidator;
	@Autowired
	private CustomerAdvancePaymentService capService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.GET)
	public String showDeliveryForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model) {
		CAPDelivery capDelivery = new CAPDelivery();
		if (pId != null) {
			logger.info("Editing the Paid in Advance Delivery form with id: "+pId);
			capDelivery = deliveryService.getDeliveryWithItems(pId);
		} else {
			logger.info("Showing the Paid in Advance Delivery form.");
			capDelivery.setDeliveryDate(new Date());
		}
		capDelivery.serializeItems();
		capDelivery.serializeArLines();
		capDelivery.setCustomerAdvancePaymentTypeId(typeId);
		return loadCapDeliveryForm(capDelivery, model);
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String saveCAPDelivery(@ModelAttribute("capDelivery") CAPDelivery capDelivery,
			BindingResult result, Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		logger.info("Form was submitted. Validating the Delivery Form.");
		capDelivery.deserializeArLines();
		capDelivery.deserializeItems();
		synchronized (this) {
			deliveryValidator.validate(capDelivery, result);
			if (result.hasErrors()) {
				if (capDelivery.getArCustomerAcctId() != null) {
					ArCustomerAccount account = arCustomerAcctService.getAccount(capDelivery.getArCustomerAcctId());
					capDelivery.setArCustomerAccount(account);
					capDelivery.setArCustomer(account.getArCustomer());
					capDelivery.setCompany(account.getCompany());
				}
				return loadCapDeliveryForm(capDelivery, model);
			}
			User user = CurrentSessionHandler.getLoggedInUser(session);
			ebFormServiceHandler.saveForm(capDelivery, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", capDelivery.getCapdNumber());
		model.addAttribute("ebObjectId", capDelivery.getEbObjectId());
		return "successfullySaved";
	}

	private String loadCapDeliveryForm(CAPDelivery capDelivery, Model model) {
		model.addAttribute(CAP_DELIVERY_ATTRIB_NAME, capDelivery);
		if (capDelivery.getCustomerAdvancePaymentTypeId() == CustomerAdvancePaymentType.INDIV_SELECTION) {
			return "CAPDeliveryISForm.jsp";
		}
		return "CAPDeliveryForm.jsp";
	}

	@RequestMapping(value = "{typeId}/loadReferences", method = RequestMethod.GET)
	public String loadReferences(@PathVariable("typeId") int typeId, Model model, HttpSession session) {
		logger.info("Loading the list of CAP to be used as references.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(model, user, PageSetting.START_PAGE);
		model.addAttribute("advancePayments", deliveryService.getUnusedReferences(user, typeId));
		model.addAttribute("typeId", typeId);
		return "CAPDeliveryReference.jsp";
	}

	private void loadSelections(Model model, User user, int pageNumber) {
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	
	@RequestMapping(value = "{typeId}/capReference", method = RequestMethod.GET)
	public String loadRefTable(@PathVariable("typeId") int typeId,
			@RequestParam(value="companyId") Integer companyId,
			@RequestParam(value="customerId", required=false) Integer customerId,
			@RequestParam(value="capNo", required=false) int capNo,
			@RequestParam(value="dateFrom", required=false) String dateFrom,
			@RequestParam(value="dateTo", required=false) String dateTo,
			@RequestParam(value="statusId", required=false) Integer statusId,
			@RequestParam(value="pageNumber", required=false) Integer pageNumber,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("advancePayments", deliveryService.getCAPReferences(companyId, typeId, customerId, -1, capNo, 
				DateUtil.parseDate(dateFrom), DateUtil.parseDate(dateTo), statusId, pageNumber , user));
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		return "CAPDeliveryRefTable.jsp";
	}

	@RequestMapping(value="/viewForm", method=RequestMethod.GET)
	public String viewCapDelivery(@RequestParam(value="pId", required=false) Integer pId, Model model) {
		logger.info("Loading the view form of Paid in Advance Delivery");
		CAPDelivery delivery = deliveryService.getDeliveryWithItems(pId);
		model.addAttribute(CAP_DELIVERY_ATTRIB_NAME, delivery);
		return "CAPDeliveryView.jsp";
	}

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String getReference (@RequestParam (value="capId", required=true) Integer capId,
			Model model, HttpSession session) {
		// Setting Delivery.
		logger.info("setting CAP Delivery form.");
		CustomerAdvancePayment cap = capService.getCustomerAdvancePayment(capId);
		CAPDelivery capDelivery = null;
		if (cap != null) {
			capDelivery = deliveryService.convertCAPtoDelivery(cap);
		}
		String [] exclude = {"item"};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(capDelivery, jsonConfig);
		return capDelivery == null ? "No customer advance payment found" : jsonObject.toString();
	}

	@RequestMapping(value="{typeId}/search", method=RequestMethod.GET)
	public @ResponseBody String searchDeliveries(@PathVariable("typeId") int typeId,
			@RequestParam(required=true, value="criteria",
			defaultValue="") String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = deliverySearcher.search(user, typeId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/printOut", method = RequestMethod.GET)
	private String showReport (@RequestParam(value="pId", required=true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize, Model model){
		boolean isIndivSelection = false;
		if (pId != null) {
			CAPDelivery delivery = deliveryService.getCAPDeliveryPrintOut(pId);
			JRDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(delivery));
			model.addAttribute("datasource", dataSource);
			model.addAttribute("format", "pdf");

			model.addAttribute("companyLogo", delivery.getCompany().getLogo());
			model.addAttribute("companyName", delivery.getCompany().getName());
			model.addAttribute("companyAddress", delivery.getCompany().getAddress());
			String reportTitle = "PAID IN ADVANCE DELIVERY";
			String snLabel = "PIAD No.";
			if (delivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.INDIV_SELECTION)) {
				reportTitle += " - IS";
				snLabel = "PIAD - IS No.";
				isIndivSelection = true;
			}
			model.addAttribute("reportTitle" , reportTitle);
			model.addAttribute("snLabel", snLabel);

			model.addAttribute("customerName", delivery.getArCustomer().getName());
			model.addAttribute("customerAddress", delivery.getArCustomer().getAddress());
			model.addAttribute("salesInvoiceNo", delivery.getSalesInvoiceNo());
			model.addAttribute("piadNumber", delivery.getFormattedCAPDNumber());
			model.addAttribute("date", DateUtil.removeTimeFromDate(delivery.getDeliveryDate()));
			model.addAttribute("typeId", delivery.getCustomerAdvancePaymentTypeId());
			SaleItemUtil.getFontSize(fontSize, model);

			double subTotal = delivery.getTotalAmount();
			model.addAttribute("subTotal", subTotal);
			double totalVAT = deliveryService.getTotalVAT(pId);
			model.addAttribute("totalVat", totalVAT);
			model.addAttribute("wtAcctSetting", (delivery.getWtAcctSetting() != null ? delivery.getWtAcctSetting().getName() : ""));
			double wtAmount = delivery.getWtAmount();
			model.addAttribute("wtAmount", wtAmount);
			double totalAmountDue = (subTotal + totalVAT) - wtAmount;
			model.addAttribute("total", totalAmountDue);

			FormWorkflow workflowLog = delivery.getFormWorkflow();
			for(FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
				if (log.getFormStatusId() == FormStatus.CREATED_ID) {
					String name = log.getCreated().getFirstName() + " " + log.getCreated().getLastName();
					model.addAttribute("createdBy", name);
					model.addAttribute("creatorPosition", log.getCreated().getPosition().getName());
				} else if (log.getFormStatusId() == FormStatus.VALIDATED_ID) {
					String name = log.getCreated().getFirstName() + " " + log.getCreated().getLastName();
					model.addAttribute("validatedBy", name);
					model.addAttribute("validatorPosition", log.getCreated().getPosition().getName());
				}
			}
		}else{
			logger.error("PIAD id is required.");
			throw new RuntimeException("PIAD id is required.");
		}
		logger.info("Sucessfully generated the printout for Paid in Advance Delivery.");
		if (isIndivSelection) {
			return "CAPDeliveryFormIS.jasper";
		}
		return "CAPDeliveryForm.jasper";
	}

	private List<CapDeliveryDto> getDataSource(CAPDelivery delivery) {
		List<CapDeliveryDto> datasource = new ArrayList<CapDeliveryDto>();
		datasource.add(CapDeliveryDto.getInstanceOf(delivery.getDeliveryItems(), delivery.getDeliveryArLines()));
		return datasource;
	}
}
