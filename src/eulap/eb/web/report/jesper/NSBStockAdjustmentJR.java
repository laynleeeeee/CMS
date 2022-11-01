package eulap.eb.web.report.jesper;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.service.NSBStockAdjustmentService;
import eulap.eb.service.StockAdjustmentService;
import eulap.eb.web.dto.NSBStockAdjustmentDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Controller class for generating the printout of NSB Stock Adjustment Form.

 *
 */
@Controller
@RequestMapping("/nsbStockAdjustmentPDF")
public class NSBStockAdjustmentJR {
	@Autowired
	private StockAdjustmentService saService;
	@Autowired
	private StockAdjustmentJR adjustmentJR;
	@Autowired
	private NSBStockAdjustmentService stockAdjustmentService;

	private static final String UNIT_COST_COL_LABEL = "Unit Cost";
	private static final String TOTAL_COL_LABEL = "Total";

	@RequestMapping(value="{typeId}", method=RequestMethod.GET)
	public String generatePrintout(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId") int stockAdjustmentId,
			Model model, HttpSession session) {
		StockAdjustment sa = saService.getProcessedStockAdjustment(stockAdjustmentId, typeId);

		List<NSBStockAdjustmentDto> saDtos = stockAdjustmentService.getStockAdjustmentDtos(sa, typeId);

		JRDataSource dataSource = new JRBeanCollectionDataSource(saDtos);
		adjustmentJR.getCommonParam(typeId, model, null, dataSource, sa, null, "pdf", session);

		model.addAttribute("formLabel", (typeId == StockAdjustment.STOCK_ADJUSTMENT_IN ? "SA I" : "SA O") + " No.");
		model.addAttribute("unitCostLabel", UNIT_COST_COL_LABEL);
		model.addAttribute("totalLabel", TOTAL_COL_LABEL);
		model.addAttribute("sequenceNo", sa.getSaNumber());
		model.addAttribute("divisionName", sa.getDivision().getName());
		model.addAttribute("currencyName", sa.getCurrency().getName());
		model.addAttribute("bmsNumber", sa.getBmsNumber());
		model.addAttribute("typeId", typeId);
		FormWorkflow workflow = sa.getFormWorkflow();
		String name = null;
		String position = null;
		for (FormWorkflowLog log : workflow.getFormWorkflowLogs()) {
			if(log.getFormStatusId() == FormStatus.APPROVED_ID) {
				name = log.getCreated().getUserFullName();
				position = log.getCreated().getPosition().getName();
				model.addAttribute("approvedBy", name);
				model.addAttribute("approvedByPosition", position);
			}
		}
		return "NSBStockAdjustmentPDF.jasper";
	}
}
