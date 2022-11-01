package eulap.eb.web;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
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
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormDeduction;
import eulap.eb.domain.hibernate.FormDeductionLine;
import eulap.eb.domain.hibernate.FormDeductionType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.FormDeductionService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * Controller class for Form Deduction.

 *
 */
@Controller
@RequestMapping("/formDeduction")
public class FormDeductionCtrlr {

	@Autowired
	private CompanyService companyService;
	@Autowired 
	private FormDeductionService formDeductionService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private WorkflowServiceHandler workflowService;

	private static final String FD_ATTRIB_NAME = "formDeduction";
	private static final String TITLE_ATD = "AUTHORITY TO DEDUCT";
	private static final String TITLE_CBC = "CASH BOND CONTRACT";

	@InitBinder
	public void init (WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{typeId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable("typeId") int typeId, @RequestParam(value="pId", required=false) Integer pId,
			Model model, HttpSession session) throws ConfigurationException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		FormDeduction formDeduction = new FormDeduction();
		if(pId == null){
			formDeduction.setFormDate(new Date());
			formDeduction.setStartDate(new Date());
			formDeduction.setFormDeductionTypeId(typeId);
		} else {
			formDeduction = formDeductionService.getFormDeduction(pId);
			model.addAttribute("pId", pId);
			model.addAttribute("SO Number", formDeduction.getSequenceNumber());
		}
		model.addAttribute("typeId", typeId);
		formDeduction.serializeReferenceDocuments();
		return loadForm(formDeduction, typeId, user, model);
	}
	
	private String loadForm(FormDeduction formDeduction, int typeId, User user, Model model){
		boolean isNew = formDeduction.getId() == 0;
		Integer deductionTypeId = null;
		if(isNew){
			model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		} else {
			deductionTypeId = formDeduction.getDeductionTypeId();
			model.addAttribute("companies", companyService.getCompaniesWithInactives(user, formDeduction.getCompanyId()));
			formDeduction.setFormWorkflow(formDeductionService.getFormWorkflow(formDeduction.getId()));
		}
		model.addAttribute("deductionTypes", formDeductionService.getDeductionTypes(deductionTypeId));
		model.addAttribute(FD_ATTRIB_NAME, formDeduction);
		return "FormDeductionForm.jsp";
	}

	@RequestMapping (value="{typeId}/deductionLine",method = RequestMethod.GET)
	public @ResponseBody String loadPayrollDeductionline (@RequestParam(value="date", required=true) Date date,
			@RequestParam(value="amountToDeduct", required=true) Double amountToDeduct,
			@RequestParam(value="noPayrollDeduction", required=true) Integer noPayrollDeduction,
			@RequestParam(value="dates", required=false) String dates,
			HttpSession session) {
		List<FormDeductionLine> deductionLines = new ArrayList<FormDeductionLine>();
		if(date != null) {
			deductionLines = formDeductionService.processDeductionLine(date, amountToDeduct, noPayrollDeduction, dates);
		}
		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(deductionLines, jConfig);
		return jsonArray.toString();
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String saveForm(@PathVariable("typeId") int typeId, @ModelAttribute ("formDeduction") FormDeduction formDeduction,
			BindingResult result, Model model, HttpSession session)throws InvalidClassException, ClassNotFoundException{
		User user = CurrentSessionHandler.getLoggedInUser(session);
		formDeduction.setFormDeductionTypeId(typeId);
		formDeduction.deserializeReferenceDocuments();
		synchronized (this) {
			formDeductionService.validate(formDeduction, result);
			if(result.hasErrors()){
				return loadForm(formDeduction, typeId, user, model);
			}
			ebFormServiceHandler.saveForm(formDeduction, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", formDeduction.getSequenceNumber());
		model.addAttribute("formId", formDeduction.getId());
		model.addAttribute("ebObjectId", formDeduction.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="{typeId}/search", method=RequestMethod.GET)
	public @ResponseBody String search(@RequestParam(required = true, value = "criteria", defaultValue = "") String criteria,
			@PathVariable("typeId") int typeId, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> results = formDeductionService.search(criteria, typeId, user);
		JSONArray jsonArray = JSONArray.fromObject(results);
		return jsonArray.toString();
	}

	@RequestMapping(value="/form/view", method=RequestMethod.GET)
	public String viewForm(@RequestParam(value = "pId", required = false) Integer pId, Model model, HttpSession session) throws ConfigurationException{
		User user = CurrentSessionHandler.getLoggedInUser(session);
		FormDeduction formDeduction = formDeductionService.getFormDeduction(pId);
		Integer typeId = formDeduction.getFormDeductionTypeId();
		model.addAttribute(FD_ATTRIB_NAME, formDeduction);
		model.addAttribute("typeId", typeId);
		if(formDeduction.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID) {
			model.addAttribute("hasEditAccess", hasEditAccess(pId, "FormDeduction"+typeId, user));
		}
		return "FormDeductionView.jsp";
	}

	private boolean hasEditAccess(Integer pId, String workflowName, User user) throws ConfigurationException {
		return workflowService.hasEditAccess(workflowName, user, formDeductionService.getFormWorkflow(pId));
	}

	@RequestMapping (value="/pdf", method = RequestMethod.GET)
	public String generatePDF(@RequestParam(value="pId", required=true) Integer pId, Model model) {
		FormDeduction formDeduction = formDeductionService.getFormDeduction(pId);
		setCommonParam(formDeduction, model, "pdf");
		if(formDeduction.getFormDeductionTypeId().equals(FormDeductionType.TYPE_AUTHORITY_TO_DEDUCT)){
			return "AuthorityToDeduct.jasper";
		} else {
			getCashBondParam(formDeduction, model);
			return "CashBondContract.jasper";
		}
	}

	public void setCommonParam(FormDeduction formDeduction, Model model, String format) {
		JRDataSource dataSource = new JRBeanCollectionDataSource(formDeduction.getFormDeductionLines());
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", format );
		String rptTitle = formDeduction.getFormDeductionTypeId() ==
				FormDeductionType.TYPE_AUTHORITY_TO_DEDUCT ? TITLE_ATD : TITLE_CBC;
		model.addAttribute("reportTitle" , rptTitle);
		Company company  = companyService.getCompany(formDeduction.getCompanyId());
		FormWorkflow formWorkflow = formDeduction.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("approvedBy",
						user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("approvedPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("createdBy",
						user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("createdPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.NOTED_ID) {
				model.addAttribute("notedBy",
						user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("notedPosition", position.getName());
			} else if (log.getFormStatusId() == FormStatus.VERIFIED_ID) {
				model.addAttribute("verifiedBy",
						user.getLastName() + ", " + user.getFirstName());
				model.addAttribute("verifiedPosition", position.getName());
			}
		}
		model.addAttribute("parentCompany" , company.getName());
		model.addAttribute("companyCode", company.getCompanyCode());
		model.addAttribute("employeeName" , formDeduction.getEmployee().getFullName());
		model.addAttribute("deductionType" , formDeduction.getDeductionType().getName());
	}

	private void getCashBondParam(FormDeduction formDeduction, Model model) {
		String strDecutionLine = "";
		int count = 1;
		int size= formDeduction.getFormDeductionLines().size();
		String decduction = null;
		for (FormDeductionLine deductionLine : formDeduction.getFormDeductionLines()) {
			decduction = deductionLine.getDeductionDate()+" "+String.format("%,.2f", deductionLine.getAmount());
			if(count == 1){
				strDecutionLine +=decduction;
			} else if(size == count){
				strDecutionLine +=", and "+decduction;
			} else {
				strDecutionLine +=", " +decduction;
			}
			count++;
		}
		model.addAttribute("paydays", NumberFormatUtil.numbersToWords(Double.valueOf(size)).split("Peso")[0] + "("+size+") ");
		model.addAttribute("strDecutionLine", strDecutionLine);
		model.addAttribute("strTotalDeduction", NumberFormatUtil.numbersToWords(formDeduction.getTotalDeductionAmount()));
		model.addAttribute("totalDeduction", formDeduction.getTotalDeductionAmount());
	}

	@RequestMapping (value="/html", method = RequestMethod.GET)
	public String generateHTML(@RequestParam(value="pId", required=true) Integer pId, Model model) {
		FormDeduction formDeduction = formDeductionService.getFormDeduction(pId);
		setCommonParam(formDeduction, model, "html");
		if(formDeduction.getFormDeductionTypeId().equals(FormDeductionType.TYPE_AUTHORITY_TO_DEDUCT)){
			return "AuthorityToDeductHTML.jasper";
		} else {
			getCashBondParam(formDeduction, model);
			return "CashBondContractHTML.jasper";
		}
	}
}
