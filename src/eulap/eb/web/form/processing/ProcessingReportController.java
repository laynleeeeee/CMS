package eulap.eb.web.form.processing;

import java.io.InvalidClassException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.domain.hibernate.ProcessingReportType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.processing.ProcessingReportService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.processing.ProcessingReportValidator;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.processing.dto.ProcessingReportPrintout;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;

/**
 * Controller class for {@link ProcessingReport}

 */
@Controller
@RequestMapping("/processingReport")
public class ProcessingReportController {
	private final Logger logger = Logger.getLogger(ProcessingReportController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ProcessingReportService processingReportService;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private ProcessingReportValidator processingReportValidator;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.GET)
	public String showCashSalesForm (@PathVariable("typeId") int processingTypeId,
			@RequestParam (value="pId", required = false) Integer pId, 
			Model model, HttpSession session) throws ConfigurationException {
		ProcessingReport processingReport = new ProcessingReport();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		boolean isEdit = false;
		if (pId == null) {
			Date currentDate = new Date();
			processingReport.setDate(currentDate);
			processingReport.setProcessingReportTypeId(processingTypeId);
		} else {
			logger.debug("Retrieving processing report  form by id "+pId);
			processingReport = processingReportService.getProcessingReport(pId, false);
			isEdit = workflowServiceHandler.isAllowedToEdit(processingReport.getWorkflowName(),
					user, processingReport.getFormWorkflow());
		}
		model.addAttribute("isEdit", isEdit);
		return loadProcessReportForm(processingReport, model, user);
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String submit (@PathVariable("typeId") int processingTypeId,
			@ModelAttribute ("processingReport") ProcessingReport processingReport, BindingResult result, 
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		processingReport.deSerializeRMItems();
		processingReport.deSerializeOMItems();
		processingReport.deSerializePrMainProducts();
		processingReport.deSerializePrByProducts();
		processingReport.deSerializePrOtherCharges();
		processingReportService.processPRItems(processingReport, true);
		synchronized (this) {
			processingReportValidator.validate(processingReport, result);
			if (result.hasErrors()) {
				return loadProcessReportForm(processingReport, model, user);
			}
			ebFormServiceHandler.saveForm(processingReport, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", processingReport.getSequenceNo());
		model.addAttribute("formId", processingReport.getId());
		model.addAttribute("ebObjectId", processingReport.getEbObjectId());
		return "successfullySaved";
	}

	private String loadProcessReportForm(ProcessingReport processingReport, Model model, User user) {
		logger.info("Retrieving active companies.");
		model.addAttribute("companies", companyService.getActiveCompanies(user, null, null, null));
		logger.debug("Putting processReport object in a model.");
		processingReport.serializeRMItems();
		processingReport.serializeOMItems();
		processingReport.serializePrMainProducts();
		processingReport.serializePrByProducts();
		processingReport.serializePrOtherCharges();
		ProcessingReportType type = processingReportService
				.getProcessingType(processingReport.getProcessingReportTypeId());
		model.addAttribute("processingType", type.getName());
		model.addAttribute("processingReport", processingReport);
		logger.info("Showing the Processing Report form of type "+type.getName());
		return "ProcessingReportForm.jsp";
	}

	@RequestMapping(value="{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchProcessReport(@PathVariable("typeId") int processingTypeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = processingReportService.searchProcessingReports(processingTypeId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="/viewForm", method=RequestMethod.GET)
	public String viewProcessingReport(@RequestParam(value="pId", required=false) Integer pId, Model model) {
		logger.info("Loading the view form of Processing Report");
		ProcessingReport processingReport = processingReportService.getProcessingReport(pId, false);
		model.addAttribute("processingReport", processingReport);
		return "ProcessingReportView.jsp";
	}

	@RequestMapping(value="/print", method = RequestMethod.GET)
	public String printProcessingReport(
			@RequestParam(required=true, value="pId") Integer pId, 
			@RequestParam(value="fontSize", required=false) Integer fontSize, Model model) {
		if (pId != null) {
			logger.info("Loading the processing report printout in pdf format.");
			ProcessingReport processingReport = 
					processingReportService.getProcessingReport(pId, true);
			List<ProcessingReportPrintout> printout = 
					processingReportService.getPRPrintout(processingReport);
			JRDataSource dataSource = new JRBeanCollectionDataSource(printout);
			getReportParams(model, dataSource, processingReport);
			if(processingReport.getProcessingReportTypeId().equals(ProcessingReportType.MILLING_REPORT)) {
				SaleItemUtil.getFontSize(fontSize, model);
				Double recoveryPercentage = 
						processingReportService.getRecoveryPercentage(pId);
				model.addAttribute("recoveryPercentage", recoveryPercentage);
				logger.info("Sucessfully loaded the processing printout.");
				return "ProcessingReport.jasper";
			} else {
				return "ProcessingReportPDF.jasper";
			}
		}
		throw new RuntimeException("Processing report id : " + pId + " not found.");
	}

	private void getReportParams (Model model, JRDataSource dataSource, 
			ProcessingReport processingReport) {
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");

		model.addAttribute("companyLogo", processingReport.getCompany().getLogo());
		model.addAttribute("companyAddress", processingReport.getCompany().getAddress());
		model.addAttribute("companyName", processingReport.getCompany().getName());
		String processingType = processingReport.getProcessingType().getName();
		model.addAttribute("reportTitle" , "Processing - "+processingType);

		model.addAttribute("sequenceNo", processingReport.getSequenceNo());

		FormWorkflow formWorkflow = processingReport.getFormWorkflow();
		model.addAttribute("status", formWorkflow.getCurrentFormStatus().getDescription());
		model.addAttribute("date", DateUtil.formatDate(processingReport.getDate()));

		String refNumber = processingReport.getRefNumber();
		if (refNumber != null && !refNumber.isEmpty()) {
			model.addAttribute("refNo", refNumber.trim());
		}

		String remarks = processingReport.getRemarks();
		if (remarks != null && !remarks.trim().isEmpty()) {
			model.addAttribute("remarks", remarks.trim());
		}
		model.addAttribute("printoutName", processingType);
	}
}
