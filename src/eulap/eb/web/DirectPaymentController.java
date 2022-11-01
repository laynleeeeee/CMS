package eulap.eb.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
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
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.DirectPayment;
import eulap.eb.domain.hibernate.DirectPaymentType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PaymentType;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DirectPaymentService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.SupplierService;
import eulap.eb.service.TermService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.DirectPaymentDto;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;

/**
 * Controller class that will handle Direct Payment.

 */

@Controller
@RequestMapping("/directPayment")
public class DirectPaymentController {
	@Autowired
	private DirectPaymentService directPaymentService;
	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TermService termService;
	@Autowired
	private WorkflowServiceHandler workflowService;
	@Autowired
	private ReferenceDocumentService refDocumentService;

	private static final String TITLE_CASH_VOUCHER = "CASH VOUCHER";
	private static final String TITLE_CHECK_VOUCHER = "CHECK VOUCHER";
	private static final Logger logger = Logger.getLogger(DirectPaymentController.class);

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String showPaymentForm(@RequestParam (value="pId", required = false)Integer pId,
			@RequestParam (value="isEdit", defaultValue="true", required=false) boolean isEdit,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		DirectPaymentDto directPaymentDto = new DirectPaymentDto();
		ApPayment apPayment = null;
		if(pId != null) {
			directPaymentDto = directPaymentService.getDirectPayment(pId);
			model.addAttribute("voucherNo", directPaymentDto.getPayment().getVoucherNumber());
			model.addAttribute("pId", pId);
		} else {
			Date currentDate = new Date();
			apPayment = new ApPayment();
			apPayment.setPaymentDate(currentDate);
			apPayment.setCheckDate(currentDate);
			directPaymentDto.setPayment(apPayment);
			directPaymentDto.setDirectPayment(new DirectPayment());
			directPaymentDto.setReferenceDocuments(new ArrayList<ReferenceDocument>());
		}
		return loadDirectPayment(directPaymentDto, user, model);
	}

	private String loadDirectPayment(DirectPaymentDto directPaymentDto, User user, Model model) {
		directPaymentDto.serializeReferenceDocuments();
		model.addAttribute("directPaymentDto", directPaymentDto);
		model.addAttribute("paymentTypes", directPaymentService.getPaymentTypes());
		List<Term> terms = termService.getTerms(user);
		model.addAttribute("terms", terms);
		return "DirectPaymentForm.jsp";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String submit (@ModelAttribute ("directPaymentDto") DirectPaymentDto paymentDto, BindingResult result,
			Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ApPayment apPayment = paymentDto.getPayment();
		DirectPayment directPayment = paymentDto.getDirectPayment();
		paymentDto.deserializeReferenceDocuments();
		apPayment.setPaymentTypeId(PaymentType.TYPE_DIRECT_PAYMENT);
		directPayment.setPaymentLines(directPaymentService.processInvoiceLines(directPayment.getPaymentLines()));
		directPaymentService.validate(paymentDto, result);
		if(result.hasErrors()) {
			return loadDirectPayment(paymentDto, user, model);
		}
		directPaymentService.saveDirectPayment(paymentDto, user);
		logger.info("Successfully saved the direct payment form with voucher number : "+apPayment.getVoucherNumber());
		model.addAttribute("success", true);
		model.addAttribute("formNumber", apPayment.getVoucherNumber());
		model.addAttribute("formId", apPayment.getId());
		model.addAttribute("ebObjectId", directPayment.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value = "/form/viewForm", method = RequestMethod.GET)
	public String showForm (@RequestParam (value="pId", required = false)Integer pId, Model model,
			HttpSession session) throws ConfigurationException {
		DirectPaymentDto directPayment = null;
		if (pId != null){
			directPayment = directPaymentService.getDirectPayment(pId);
			directPayment.setReferenceDocuments(refDocumentService.getReferenceDocuments(directPayment.getDirectPayment().getEbObjectId()));
		}else{
			throw new RuntimeException("Direct Payment form id is required");
		}
		User user = CurrentSessionHandler.getLoggedInUser(session);
		model.addAttribute("hasEditAccess", hasEditAccess(pId, "DirectPayment", user));
		model.addAttribute("directPayment", directPayment);
		return "DirectPaymentFormView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, directPaymentService.getFormWorkflow(pId));
	}

	@RequestMapping (value="pdf", method = RequestMethod.GET)
	private String showFormPdf (@RequestParam (value="pId", required = true)Integer pId,
			Model model, HttpSession session){
		DirectPaymentDto directPaymentDto = directPaymentService.getDirectPaymentPrintOut(pId);
		DirectPayment directPayment = directPaymentDto.getDirectPayment();
		ApPayment apPayment = directPaymentDto.getPayment();
		boolean isCash = directPayment.getDirectPaymentTypeId() == DirectPaymentType.TYPE_CASH;
		String title = isCash ? TITLE_CASH_VOUCHER : TITLE_CHECK_VOUCHER;

		JRDataSource dataSource = new JRBeanCollectionDataSource(directPaymentDto.getApInvoiceDtos());

		model.addAttribute("format", "pdf");
		model.addAttribute("datasource", dataSource);
		model.addAttribute("title", title);

		Supplier supplier = supplierService.getSupplier(apPayment.getSupplierId());
		model.addAttribute("payee", supplier.getName());
		model.addAttribute("address", supplier.getAddress());
		model.addAttribute("tin", supplier.getTin());
		model.addAttribute("voucherNo", apPayment.getVoucherNumber().toString());
		model.addAttribute("paymentDate", DateUtil.formatDate(apPayment.getPaymentDate()));
		String decStr = NumberFormatUtil.amountToWordsWithDecimals(apPayment.getAmount())
				+ " ("+ NumberFormatUtil.format(apPayment.getAmount()).toString()+")";
		model.addAttribute("amountWords", decStr);
		model.addAttribute("bankAccount", bankAccountService.getBankAccount(apPayment.getBankAccountId()).getName());
		if(!isCash) {
			model.addAttribute("checkNo", apPayment.getCheckNumber().toString());
			model.addAttribute("checkDate", DateUtil.formatDate(apPayment.getCheckDate()));
		}
		model.addAttribute("bankLable", isCash ? "Payment Account" : "Bank Account");
		//Company information
		Company company = companyService.getCompany(apPayment.getCompanyId());

		model.addAttribute("companyLogo", company.getLogo());

		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyAddress", company.getAddress());
		String companyTin = company.getTin();
		if(companyTin != null) {
			if(!companyTin.trim().isEmpty())
				companyTin = companyService.getTin(companyTin);
		}
		model.addAttribute("companyTin", companyTin);

		if (apPayment.getCreatedDate() != null)
			model.addAttribute("preparedDate", DateUtil.formatDate(apPayment.getCreatedDate()));
		FormWorkflow pWorkflow = apPayment.getFormWorkflow();
		List<FormWorkflowLog> pLogs = pWorkflow.getFormWorkflowLogs();
		if(pWorkflow.getCurrentStatusId() == 4 ){
			model.addAttribute("cancelled", "cancelled.png");
		}
		for (FormWorkflowLog log : pLogs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.PREPARED_ID) {
				model.addAttribute("preparedBy",  
						user.getLastName() + " " + user.getFirstName());
				model.addAttribute("preparedPosition", position.getName());
				break;
			}
		}
		return "DirectPaymentVoucher.jasper";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String searchDirectPayments(@RequestParam(required=true, value="criteria", defaultValue="") String criteria,
			HttpSession session,
			Model model){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = directPaymentService.searchDirectPayments(criteria, user);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
