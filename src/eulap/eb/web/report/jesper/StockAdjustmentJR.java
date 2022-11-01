package eulap.eb.web.report.jesper;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.StockAdjustmentItem;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.StockAdjustmentIsService;
import eulap.eb.service.StockAdjustmentService;

/**
 * Controller class for generating the printout of Stock Adjustment Form.

 *
 */
@Controller
@RequestMapping("/stockAdjustmentPDF")
public class StockAdjustmentJR {
	private static Logger logger = Logger.getLogger(StockAdjustmentJR.class);
	@Autowired
	private StockAdjustmentService saService;
	@Autowired
	private StockAdjustmentIsService saIsService;

	@RequestMapping(value="{typeId}/pdf", method=RequestMethod.GET)
	public String generatePrintout(@PathVariable(value="typeId") int typeId,
			@RequestParam(value="pId") int stockAdjustmentId,
			Model model, HttpSession session) {
		getParams(typeId, stockAdjustmentId, model, "pdf", session);
		logger.info("Loading the Jasper report.");
		return typeId == StockAdjustment.STOCK_ADJUSTMENT_IN || typeId == StockAdjustment.STOCK_ADJUSTMENT_OUT ?  "StockAdjustmentPDF.jasper" 
				: "StockAdjustmentISPDF.jasper";
	}

	/**
	 * Get common parameters stock adjustment
	 * @param typeId The type id
	 * @param model The model
	 * @param slipTitle The slip title
	 * @param dataSource The datasource
	 * @param sa The {@link StockAdjustment}
	 * @param fontSize The font size
	 * @param format The format
	 * @param session The session
	 */ 
	public void getCommonParam(int typeId, Model model, String slipTitle, JRDataSource dataSource,
			StockAdjustment sa, Integer fontSize, String format, HttpSession session) {
		logger.debug("Generating the params.");
		Company company = sa.getCompany();
		model.addAttribute("datasource", dataSource);
		logger.debug("Successfully generated the datasource of the printout.");

		model.addAttribute("format", format);
		model.addAttribute("slipTitle", slipTitle);
		if(format == "pdf") {
			model.addAttribute("companyLogo", company.getLogo());
		} else {
			model.addAttribute("companyLogo", session.getServletContext().getContextPath() +"/images/logo/"+ company.getLogo());
		}
		model.addAttribute("companyName", company.getName());
		model.addAttribute("companyTin", company.getTin());
		model.addAttribute("companyAddress", company.getAddress());
		model.addAttribute("warehouse", sa.getWarehouse().getName());
		model.addAttribute("adjustmentType", sa.getAdjustmentType().getName());
		model.addAttribute("saNumber", sa.getFormattedSANumber());
		model.addAttribute("date", sa.getSaDate());
		model.addAttribute("supplier", "");
		model.addAttribute("remarks", sa.getRemarks());
		model.addAttribute("formLabel", sa.getShortLabel() + " No.");
		model.addAttribute("reportTitle", ((typeId == StockAdjustment.STOCK_ADJUSTMENT_IN) || (typeId == StockAdjustment.STOCK_ADJUSTMENT_IS_IN))
				? "Stock Adjustment - IN" : "Stock Adjustment - OUT");
		SaleItemUtil.getFontSize(fontSize, model);

		String name = null;
		String position = null;
		FormWorkflow workflow = sa.getFormWorkflow();
		for (FormWorkflowLog log : workflow.getFormWorkflowLogs()) {
			if(log.getFormStatusId() == FormStatus.CREATED_ID) {
				name = log.getCreated().getUserFullName();
				position = log.getCreated().getPosition().getName();
				model.addAttribute("createdBy", name);
				model.addAttribute("createdByPosition", position);
			}
		}

		company = null;
		name = null;
		position = null;
		workflow = null;
	}

	private void getParams(int typeId, int stockAdjustmentId, Model model, String format, HttpSession session) {
		logger.info("Generating the printout of Stock Adjustment: "+stockAdjustmentId);
		StockAdjustment sa = typeId ==  StockAdjustment.STOCK_ADJUSTMENT_IN || typeId == StockAdjustment.STOCK_ADJUSTMENT_OUT ? 
				saService.getProcessedStockAdjustment(stockAdjustmentId, typeId) : saIsService.getSAIsAndProcessedItems(stockAdjustmentId, typeId);
		List<StockAdjustmentItem> saItems = sa.getSaItems();
		JRDataSource dataSource = new JRBeanCollectionDataSource(saItems);
		getCommonParam(typeId, model, null, dataSource, sa, null, format, session);
		logger.info("Freeing up the memory allocation.");
		sa = null;
		saItems = null;
	}

	@RequestMapping(value="{typeId}/html", method=RequestMethod.GET)
	public String generateHTMLPrintout(
			@PathVariable("typeId") int typeId,
			@RequestParam(value="pId") int stockAdjustmentId,
			Model model, HttpSession session) {
		getParams(typeId, stockAdjustmentId, model, "html", session);
		logger.info("Loading the Jasper report.");
		return "StockAdjustmentPDFHTML.jasper";
	}
}
