package eulap.eb.web.form.inv;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.RrRawMaterialService;
import eulap.eb.validator.inv.RrRawMaterialValidator;
import eulap.eb.web.dto.RrRawMaterialsDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Controller class for RR - Raw Materials module.

 *
 */
@Controller
@RequestMapping(value="/rrRawMaterial")
public class RrRawMaterialsController {
	private Logger logger = Logger.getLogger(RrRawMaterialsController.class);
	@Autowired
	private RrRawMaterialService rawMaterialService;
	@Autowired
	private RrRawMaterialValidator rawMaterialValidator;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/form", method=RequestMethod.GET)
	public String showForm(@PathVariable(value="typeId") Integer typeId,
			@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		APInvoice rrRawMaterial = new APInvoice();
		if(pId != null) {
			logger.info("Editing the RR - Raw Materials form of id: "+pId);
			rrRawMaterial = rawMaterialService.getRawMaterialWithItems(pId, typeId);
		} else {
			logger.info("Loading the add form of RR - Raw Materials.");
			Date currentDate = new Date();
			rrRawMaterial.setGlDate(currentDate);
			rrRawMaterial.setInvoiceDate(currentDate);
			rrRawMaterial.setReceivingReport(new RReceivingReport());
		}
		rrRawMaterial.setInvoiceTypeId(typeId);
		rrRawMaterial.serializeRrItems();
		rrRawMaterial.serializeInvoiceLines();
		if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
			rrRawMaterial.serializeRriBagQuantities();
			rrRawMaterial.serializeRriBagDiscounts();
		}
		rawMaterialService.loadSelections(user, model);
		model.addAttribute("rrRawMaterial", rrRawMaterial);
		if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
			return "RRRawMaterialsNetWeightForm.jsp";
		} else {
			return "RrRawMaterialsForm.jsp";
		}
	}

	@RequestMapping(value = "{typeId}/form", method = RequestMethod.POST)
	public String saveForm(@PathVariable("typeId") Integer typeId,
		@ModelAttribute("rrRawMaterial") APInvoice rrRawMaterial,
		BindingResult result, Model model, HttpSession session) 
		throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		logger.info("Form was submitted. Validating the RR - Raw Material Form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		rrRawMaterial.deserializeInvoiceLines();
		rrRawMaterial.deserializeRrItems();
		if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
			rrRawMaterial.deserializeRriBagQuantities();
			rrRawMaterial.deserializeRriBagDiscounts();
			rrRawMaterial.setRrItems(rawMaterialService.converDtoToRrItem(
					rrRawMaterial.getRrRawMatItemDto()));
		}
		synchronized (this) {
			rawMaterialValidator.validate(rrRawMaterial, result);
			if(result.hasErrors()) {
				rawMaterialService.reloadForm(rrRawMaterial);
				rawMaterialService.loadSelections(user, model);
				model.addAttribute("rrRawMaterial", rrRawMaterial);
				if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
					return "RRRawMaterialsNetWeightForm.jsp";
				} else {
					return "RrRawMaterialsForm.jsp";
				}
			}
			logger.info("Saving the RR - Raw Material form.");
			ebFormServiceHandler.saveForm(rrRawMaterial, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", rrRawMaterial.getSequenceNumber());
		model.addAttribute("formId", rrRawMaterial.getId());
		model.addAttribute("ebObjectId", rrRawMaterial.getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="{typeId}/view", method=RequestMethod.GET)
	public String showViewForm(@PathVariable("typeId") Integer typeId,
			@RequestParam (value="pId", required=false) Integer pId, Model model) {
		logger.info("Loading the view form of RR - Raw Material with id: "+pId);
		model.addAttribute("rrRawMaterial", rawMaterialService.getRRawMaterialsForViewing(pId, typeId));
		if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
			return "RRRawMaterialsNetWeightView.jsp";
		} else {
			return "RrRawMaterialsFormView.jsp";
		}
	}

	@RequestMapping(value="{typeId}/pdf", method = RequestMethod.GET)
	public String showReport(@PathVariable("typeId") Integer typeId,
			@RequestParam(value="pId", required=true) Integer pId, Model model) {
		if(pId != null) {
			logger.debug("Generating RR - Raw materials printout of id: "+pId);
			APInvoice rrRawMaterials = rawMaterialService.getRawMaterialWithItems(pId, typeId);
			setDataSource(rrRawMaterials, model);
			String title = "Receiving Report - Raw Materials";
			getCommonParams(model, rrRawMaterials, "pdf", title, typeId);
		} else{
			logger.error("RR-Raw Materials id required.");
			throw new RuntimeException("RR-Raw Materials id is required.");
		}
		logger.info("Sucessfully loaded RR- Raw Materials printout.");
		return "RrRawMaterialsPDF.jasper";
	}

	private void getCommonParams(Model model, APInvoice rrRawMaterials,
			String format, String title, Integer typeId) {
		RReceivingReport receivingReport = rrRawMaterials.getReceivingReport();
		model.addAttribute("format", format);
		model.addAttribute("companyLogo", receivingReport.getCompany().getLogo());
		model.addAttribute("companyName", receivingReport.getCompany().getName());
		model.addAttribute("companyAddress", receivingReport.getCompany().getAddress());
		model.addAttribute("companyTin", receivingReport.getCompany().getTin());
		model.addAttribute("reportTitle", title);
		model.addAttribute("rrNumber", typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)
				? rrRawMaterials.getSequenceNumber() : receivingReport.getFormattedRRNumber());
		model.addAttribute("rrDate", rrRawMaterials.getGlDate());
		model.addAttribute("supplier", rrRawMaterials.getSupplier().getName());
		model.addAttribute("supplierInvoiceNo", rrRawMaterials.getInvoiceNumber());
		model.addAttribute("deliveryReceiptNo", receivingReport.getDeliveryReceiptNo());
		model.addAttribute("warehouse", receivingReport.getWarehouse().getName());
		model.addAttribute("total", rrRawMaterials.getAmount());
		model.addAttribute("invoiceDate", rrRawMaterials.getInvoiceDate());
		model.addAttribute("term", rrRawMaterials.getTerm().getName());
		model.addAttribute("dueDate", rrRawMaterials.getDueDate());

		FormWorkflow workflowLog = rrRawMaterials.getFormWorkflow();
		for (FormWorkflowLog log : workflowLog.getFormWorkflowLogs()) {
			String name = log.getCreated().getFullName();
			String position = log.getCreated().getPosition().getName();
			model.addAttribute("createdBy", name);
			model.addAttribute("creatorPosition", position);
			break;
		}
		logger.debug("Successfully generated the parameters for the report.");
	}

	private void setDataSource(APInvoice rrRawMaterials, Model model) {
		List<RrRawMaterialsDto> rmDataSource = new ArrayList<>();
		rmDataSource.add(RrRawMaterialsDto.getInstanceOf(rrRawMaterials));
		JRDataSource dataSource = new JRBeanCollectionDataSource(rmDataSource);
		model.addAttribute("datasource", dataSource);
	}

	@RequestMapping(value="{typeId}/scaleSheetRpt", method = RequestMethod.GET)
	public String showScaleSheetRpt(@PathVariable("typeId") Integer typeId,
			@RequestParam(value="pId", required=true) Integer pId, Model model) {
		if(pId != null) {
			logger.debug("Generating RR - Raw materials printout of id: " + pId);
			APInvoice rrRawMaterials = rawMaterialService.getRawMaterialWithItems(pId, typeId);
			setDataSource(rrRawMaterials, model);
			String title = "Receiving Report";
			getCommonParams(model, rrRawMaterials, "html", title, typeId);
			model.addAttribute("supplier", rrRawMaterials.getSupplier().getName());
			model.addAttribute("supplierAccount", rrRawMaterials.getSupplierAccount().getName());
			model.addAttribute("stockCode", rrRawMaterials.getRrRawMatItemDto().getStockCode());
			model.addAttribute("description", rrRawMaterials.getRrRawMatItemDto().getDescription());
			model.addAttribute("scaleSheetNo", rrRawMaterials.getInvoiceNumber());
			model.addAttribute("buyingPrice", rrRawMaterials.getRrRawMatItemDto().getBuyingPrice());
			model.addAttribute("weight", rrRawMaterials.getRrRawMatItemDto().getTotalWeight());
			model.addAttribute("discount", rrRawMaterials.getRrRawMatItemDto().getTotalDiscount());
			model.addAttribute("otherCharges", rrRawMaterials.getRrRawMatItemDto().getTotalOtherCharges());
		} else{
			logger.error("RR-Raw Materials id required.");
			throw new RuntimeException("RR-Raw Materials id is required.");
		}
		logger.info("Sucessfully loaded RR- Raw Materials printout.");
		return "RrScaleSheetRptHtml.jasper";
	}
}
