package eulap.eb.web.form.processing;

import java.io.InvalidClassException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.domain.hibernate.ProcessingReportType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.processing.ProductionReportService;
import eulap.eb.validator.processing.ProductionReportValidator;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.PrItemDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.json.JSONArray;

/**
 * Controller class for Production Report module.

 */

@Controller
@RequestMapping("/productionReport")
public class ProductionReportController {
	private final Logger logger = Logger.getLogger(ProductionReportController.class);
	@Autowired
	private ProductionReportService productionReportService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private ProductionReportValidator productionReportValidator;

	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/form", method = RequestMethod.GET)
	public String showForm(@PathVariable("typeId") int processingTypeId,
			@RequestParam (value="pId", required = false) Integer pId, 
			Model model, HttpSession session) throws ConfigurationException {
		ProcessingReport processingReport = new ProcessingReport();
		User user = CurrentSessionHandler.getLoggedInUser(session);
		if (pId == null) {
			processingReport.setDate(new Date());
		} else {
			logger.debug("Retrieving Production Report  form by id "+pId);
			processingReport = productionReportService.getProcessingReport(pId, false);
		}
		processingReport.setProcessingReportTypeId(processingTypeId);
		return loadProcessReportForm(processingReport, model, user);
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String submit (@PathVariable("typeId") int processingTypeId,
			@ModelAttribute ("processingReport") ProcessingReport processingReport, BindingResult result, 
			Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		processingReport.setProcessingReportTypeId(processingTypeId);
		processingReport.deSerializeRMItems();
		processingReport.deSerializePrMainProducts();
		productionReportService.processItems(processingReport);
		synchronized (this) {
			productionReportValidator.validate(processingReport, result);
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
		processingReport.serializeRMItems();
		processingReport.serializePrMainProducts();
		model.addAttribute("processingType", productionReportService.getProcessingType(
				processingReport.getProcessingReportTypeId()).getName());
		model.addAttribute("processingReport", processingReport);
		return "ProductionReportForm.jsp";
	}

	@RequestMapping(value="{typeId}/search", method = RequestMethod.GET)
	public @ResponseBody String searchProcessReport(@PathVariable("typeId") int processingTypeId,
			@RequestParam(required=true, value="criteria", defaultValue="") String criteria) {
		List<FormSearchResult> result = productionReportService.searchPrForms(processingTypeId, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}

	@RequestMapping(value="{typeId}/view", method=RequestMethod.GET)
	public String viewProcessingReport(@PathVariable("typeId") int processingTypeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model) {
		logger.info("Loading the view form of Processing Report");
		ProcessingReport processingReport = productionReportService.getProcessingReport(pId, false);
		model.addAttribute("processingReport", processingReport);
		model.addAttribute("processingType", productionReportService.getFormTypeName(processingTypeId));
		model.addAttribute("prItems", productionReportService.getPrItemDto(processingReport));
		return "ProductionReportView.jsp";
	}

	@RequestMapping(value="/print", method = RequestMethod.GET)
	public String printProcessingReport(@RequestParam(required=true, value="pId") Integer pId, Model model) {
		if (pId != null) {
			logger.info("Loading the processing report printout in pdf format.");
			JRDataSource dataSource = null;
			ProcessingReport processingReport = productionReportService.getProcessingReport(pId, false);
			boolean isProduction = processingReport.getProcessingReportTypeId().equals(ProcessingReportType.PRODUCTION);
			if (isProduction) {
				List<PrItemDto> printout = productionReportService.getPrItemDto(processingReport);
				dataSource = new JRBeanCollectionDataSource(printout);
			} else {
				List<ProcessingReport> processingReports = new ArrayList<ProcessingReport>();
				processingReports.add(processingReport);
				dataSource = new JRBeanCollectionDataSource(processingReports);
			}
			getReportParams(model, dataSource, processingReport);
			return isProduction ? "ProductionReportV2.jasper" : "ProductionReport.jasper";
		} else {
			throw new RuntimeException("Production report id : " + pId + " not found.");
		}
	}

	private void getReportParams (Model model, JRDataSource dataSource, 
			ProcessingReport processingReport) {
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");

		model.addAttribute("companyLogo", processingReport.getCompany().getLogo());
		model.addAttribute("companyAddress", processingReport.getCompany().getAddress());
		model.addAttribute("companyName", processingReport.getCompany().getName());
		String processingType = processingReport.getProcessingType().getName();
		model.addAttribute("reportTitle" , processingReport.getProcessingType().getName().toUpperCase());

		model.addAttribute("sequenceNo", processingReport.getFormattedPRNumber());

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

		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("preparedBy",
						user.getLastName() + " " + user.getFirstName());
				model.addAttribute("preparedPosition", position.getName());
			}
			if(log.getFormStatusId() == FormStatus.APPROVED_ID) {
				model.addAttribute("notedBy",
						user.getLastName() + " " + user.getFirstName());
				model.addAttribute("notedPosition", position.getName());
			}
		}
	}

	@RequestMapping(method = RequestMethod.GET, value="/recomputeCost")
	public @ResponseBody String recomputeFinishedProductCost() {
		logger.info("Recomputing the production report costs");
		productionReportService.recomputeCost();
		logger.info("Successfully recomputed the production report costs");
		return "Successfully recomputed the production report costs";
	}
}
