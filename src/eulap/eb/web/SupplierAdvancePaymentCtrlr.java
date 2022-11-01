package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.SupplierAdvancePayment;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.SupplierAdvPaymentService;
import eulap.eb.service.SupplierService;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.PurchaseOrderDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class that will handle request for supplier advance payment

 */

@Controller
@RequestMapping(value="/supplierAdvPayment")
public class SupplierAdvancePaymentCtrlr {
	@Autowired
	private SupplierAdvPaymentService advPaymentService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private SupplierService supplierService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{divisionId}/form", method = RequestMethod.GET)
	public String showForm(@PathVariable(value="divisionId") int divisionId,
			@RequestParam (value="pId", required = false) Integer pId,
			Model model, HttpSession session) throws IOException {
		SupplierAdvancePayment supplierAdvPayment = new SupplierAdvancePayment();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		supplierAdvPayment.setDivisionId(divisionId);
		supplierAdvPayment.setDivision(advPaymentService.getDivision(divisionId));
		if (pId != null) {
			supplierAdvPayment = advPaymentService.getSupplrAdvPayment(pId);
		} else {
			Date currentDate = new Date();
			supplierAdvPayment.setDate(currentDate);
			supplierAdvPayment.setInvoiceDate(currentDate);
			supplierAdvPayment.setGlDate(currentDate);
		}
		return loadForm(supplierAdvPayment, user, model);
	}

	public String loadForm(SupplierAdvancePayment supplierAdvPayment, User user, Model model) {
		supplierAdvPayment.serializeAdvPaymentLines();
		supplierAdvPayment.serializeReferenceDocuments();
		int companyId = supplierAdvPayment.getCompanyId() != null ? supplierAdvPayment.getCompanyId() : 0;
		model.addAttribute("companies", advPaymentService.getCompanies(user, companyId));
		model.addAttribute("currencies", advPaymentService.getActiveCurrencies(supplierAdvPayment.getCurrencyId()));
		model.addAttribute("supplierAdvPayment", supplierAdvPayment);
		return "SupplierAdvancePaymentForm.jsp";
	}

	@RequestMapping(value = "{divisionId}/form", method = RequestMethod.POST)
	public String saveForm(@PathVariable(value="divisionId") int divisionId,
			@ModelAttribute ("supplierAdvPayment") SupplierAdvancePayment supplierAdvPayment,
			BindingResult result, Model model, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		supplierAdvPayment.deserializeAdvPaymentLines();
		supplierAdvPayment.deserializeReferenceDocuments();
		synchronized (this) {
			advPaymentService.validateForm(supplierAdvPayment, result);
			if (result.hasErrors()) {
				return loadForm(supplierAdvPayment, user, model);
			}
			ebFormServiceHandler.saveForm(supplierAdvPayment, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formId", supplierAdvPayment.getId());
		model.addAttribute("ebObjectId", supplierAdvPayment.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="{divisionId}/showPoReferences", method=RequestMethod.GET)
	public String showPoReferenceForm(@PathVariable(value="divisionId") int divisionId,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		Collection<Company> companies = advPaymentService.getCompanies(user, 0);
		model.addAttribute("companies", companies);
		model.addAttribute("divisionId", divisionId);
		Integer companyId = companies != null ? companies.iterator().next().getId() : null;
		loadPoReferenceForms(companyId, divisionId, null, null, null, null, null, null,
				PageSetting.START_PAGE, model);
		return "PoAdvPaymentReference.jsp";
	}

	private void loadPoReferenceForms(Integer companyId, Integer divisionId, Integer supplierId,
			Integer supplierAcctId, Integer poNumber, String bmsNumber, Date dateFrom, Date dateTo,
			Integer pageNumber, Model model) {
		Page<PurchaseOrderDto> rpurchaseOrders = advPaymentService.getPurchaseOrders(companyId,
				divisionId, supplierId, supplierAcctId, poNumber, bmsNumber, dateFrom, dateTo,
				pageNumber);
		model.addAttribute("rpurchaseOrders", rpurchaseOrders);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/getPurchaseOrdeRefs", method=RequestMethod.GET)
	public String getPurchaseOrdeRefs(@RequestParam (value="companyId", required=false) Integer companyId,
			@RequestParam (value="divisionId", required=false) Integer divisionId,
			@RequestParam (value="supplierId", required=false) Integer supplierId,
			@RequestParam (value="supplierAcctId", required=false) Integer supplierAcctId,
			@RequestParam (value="poNumber", required=false) Integer poNumber,
			@RequestParam (value="bmsNumber", required=false) String bmsNumber,
			@RequestParam (value="dateFrom", required=false) Date dateFrom,
			@RequestParam (value="dateTo", required=false) Date dateTo,
			@RequestParam (value="pageNumber") Integer pageNumber,
			Model model, HttpSession session) {
		loadPoReferenceForms(companyId, divisionId, supplierId, supplierAcctId, poNumber,
				bmsNumber, dateFrom, dateTo, pageNumber, model);
		return "PoAdvPaymentReferenceTbl.jsp";
	}

	@RequestMapping(value="/loadPoReference",method=RequestMethod.GET)
	public @ResponseBody String loadRefWorkOrder(@RequestParam (value="purchaseOrderRefId", required=true) Integer purchaseOrderRefId,
			@RequestParam (value="advPaymentId", required=false) Integer advPaymentId,
			Model model, HttpSession session) {
		SupplierAdvancePayment advPayment = advPaymentService.convertPoToAdvPayment(advPaymentId, purchaseOrderRefId);
		JSONObject jsonObject = JSONObject.fromObject(advPayment, new JsonConfig());
		return jsonObject.toString();
	}

	@RequestMapping(value="/viewForm", method=RequestMethod.GET)
	public String viewPurchaseOrder(@RequestParam(value="pId", required=false) Integer pId, Model model) throws IOException {
		SupplierAdvancePayment supplierAdvPayment = advPaymentService.getSupplrAdvPayment(pId);
		model.addAttribute("supplierAdvPayment", supplierAdvPayment);
		return "SupplierAdvancePaymentView.jsp";
	}

	@RequestMapping(value="/pdf", method=RequestMethod.GET)
	public String generatePrintout(@RequestParam(value="pId", required=false) Integer pId, Model model) throws IOException {
		List<SupplierAdvancePayment> supplierAdvancePayments = new ArrayList<SupplierAdvancePayment>();
		SupplierAdvancePayment supplierAdvPayment = advPaymentService.getSupplrAdvPayment(pId);
		supplierAdvancePayments.add(supplierAdvPayment);
		JRDataSource dataSource = new JRBeanCollectionDataSource(supplierAdvancePayments);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");

		Company company = supplierAdvPayment.getCompany();
		model.addAttribute("companyLogo", company.getLogo());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("reportTitle" , "SUPPLIER ADVANCE PAYMENT");

		model.addAttribute("divisionName", supplierAdvPayment.getDivision().getName());
		model.addAttribute("supplierName", supplierAdvPayment.getSupplier().getName());
		model.addAttribute("supplierAcct", supplierAdvPayment.getSupplierAccount().getName());
		model.addAttribute("supplierAddress", supplierService.processSupplierAddress(supplierAdvPayment.getSupplier()));
		model.addAttribute("supplierTin", StringFormatUtil.processBirTinTo13Digits(supplierAdvPayment.getSupplier().getTin()));
		model.addAttribute("currency", supplierAdvPayment.getCurrency().getName());
		model.addAttribute("sequenceNo", supplierAdvPayment.getSequenceNumber());
		model.addAttribute("formDate", supplierAdvPayment.getDate());

		Double amount = supplierAdvPayment.getAmount();
		String amountStr = amount.toString();
		String decStr = "";
		String sumAmountStr = "";
		if (amountStr.contains(".0")){
			decStr = " (" + NumberFormatUtil.format(amount).toString() + ")";
			sumAmountStr = NumberFormatUtil.numbersToWords(amount) + " Only."
					+ " (" + NumberFormatUtil.format(amount).toString() + ")";
		} else {
			decStr = " and " + amountStr.substring((amountStr.length())-2, (amountStr.length())) + "/100";
			sumAmountStr = NumberFormatUtil.numbersToWords(amount) + decStr + " Only."
					+ " (" + NumberFormatUtil.format(amount).toString() + ")";
		}
		model.addAttribute("amountInWords", sumAmountStr);
		model.addAttribute("totalAmount", supplierAdvPayment.getTotalLineAmount() - supplierAdvPayment.getAmount());
		model.addAttribute("advancePayment", -supplierAdvPayment.getAmount());

		FormWorkflow formWorkflow = supplierAdvPayment.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy", user.getUserFullName());
				model.addAttribute("creatorPosition", position.getName());
			}
			if (log.getFormStatusId() == FormStatus.REVIEWED_ID) {
				model.addAttribute("reviewedBy", user.getUserFullName());
				model.addAttribute("reviewerPosition", position.getName());
			}
		}
		return "SupplierAdvancePayment.jasper";
	}

	@RequestMapping(value="/{divisionId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchOrderSlips(@PathVariable(value="divisionId") int divisionId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = advPaymentService.searchAdvancePayments(divisionId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
