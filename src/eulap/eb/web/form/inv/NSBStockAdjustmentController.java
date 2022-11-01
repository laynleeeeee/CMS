package eulap.eb.web.form.inv;

import java.io.IOException;
import java.io.InvalidClassException;
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
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.NSBStockAdjustmentService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.SerialItemService;
import eulap.eb.service.StockAdjustmentService;
import eulap.eb.validator.inv.StockAdjustmentValidator;
import eulap.eb.web.dto.NSBStockAdjustmentDto;

/**
 * Controller class for NSB Stock Adjustment.

 *
 */

@Controller
@RequestMapping("/nsbStockAdjustment")
public class NSBStockAdjustmentController {
	private static Logger logger = Logger.getLogger(NSBStockAdjustmentController.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private StockAdjustmentValidator adjustmentValidator;
	@Autowired
	private NSBStockAdjustmentService nsbStockAdjustmentService;
	@Autowired
	private StockAdjustmentService stockAdjustmentService;
	@Autowired
	private SerialItemService serializedItemService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private ReferenceDocumentService refDocumentService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/{divisionId}/form", method=RequestMethod.GET)
	public String showSAForm(@PathVariable("typeId") int typeId,
			@PathVariable("divisionId") int divisionId,
			@RequestParam(value="pId", required=false) Integer pId,
			HttpSession session, Model model) throws IOException {
		logger.info("Loading the Stock Adjustment form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		NSBStockAdjustmentDto stockAdjustmentDto = new NSBStockAdjustmentDto();
		StockAdjustment stockAdjustment = new StockAdjustment();
		stockAdjustment.setDivisionId(divisionId);
		stockAdjustment.setDivision(divisionService.getDivision(divisionId));
		if (pId != null) {
			logger.debug("Loading the stock adjustment form: "+pId);
			stockAdjustment = stockAdjustmentService.getProcessedStockAdjustment(pId, typeId);
			Integer ebObjectId = stockAdjustment.getEbObjectId();
			boolean isCancelled = false;
			if(stockAdjustment.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID) {
				isCancelled = true;
			}
			stockAdjustmentDto.setSerialItems(serializedItemService.getSerializeItemsByRefObjectIdAndObject(ebObjectId,
					stockAdjustment.getWarehouseId(), typeId == StockAdjustment.STOCK_ADJUSTMENT_IN ?
							NSBStockAdjustmentDto.SA_IN_TO_SI_OR_TYPE_ID : NSBStockAdjustmentDto.SA_OUT_TO_SI_OR_TYPE_ID,
							isCancelled, stockAdjustment));
			stockAdjustment.setReferenceDocuments(refDocumentService.processReferenceDocs(ebObjectId));
		} else {
			stockAdjustment.setSaDate(new Date());
		}
		stockAdjustment.serializeItems();
		stockAdjustment.serializeReferenceDocuments();
		stockAdjustmentDto.serializeSerialItems();
		stockAdjustmentDto.setStockAdjustment(stockAdjustment);
		model.addAttribute("stockAdjustmentDto", stockAdjustmentDto);
		model.addAttribute("isStockIn", typeId == 1);
		loadSelections(stockAdjustmentDto, model, user, typeId);
		return "NSBStockAdjustmentForm.jsp";
	}

	private void loadSelections(NSBStockAdjustmentDto stockAdjustmentDto, Model model, User user, int typeId) {
		model.addAttribute("typeId", typeId);
		model.addAttribute("companies", companyService.getCompanies(user));
		model.addAttribute("currencies", currencyService.getActiveCurrencies(stockAdjustmentDto.getStockAdjustment().getCurrencyId()));
	}

	@RequestMapping(value="{typeId}/{divisionId}/form", method=RequestMethod.POST)
	public String saveSAForm(@PathVariable("typeId") int typeId,
			@PathVariable("divisionId") int divisionId,
			@ModelAttribute("stockAdjustmentDto") NSBStockAdjustmentDto stockAdjustmentDto,
			BindingResult result, Model model, HttpSession session) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		StockAdjustment stockAdjustment = stockAdjustmentDto.getStockAdjustment();
		stockAdjustment.setTypeId(typeId);
		int classificationId = nsbStockAdjustmentService.setClassificationByDivision(divisionId, typeId);
		stockAdjustment.setStockAdjustmentClassificationId(classificationId);
		stockAdjustment.deSerializeItems();
		stockAdjustment.deserializeReferenceDocuments();
		stockAdjustmentDto.derializeSerialItems();
		synchronized (this) {
			adjustmentValidator.validateForm(stockAdjustmentDto, result);
			if (result.hasErrors()) {
				logger.debug("Form has error/s. Reloading the Stock Adjustment form.");
				if (stockAdjustment.getFormWorkflowId() != null) {
					stockAdjustmentDto.getStockAdjustment().setFormWorkflow(
							nsbStockAdjustmentService.getFormWorkflow(stockAdjustment.getId()));
				}
				loadSelections(stockAdjustmentDto, model, user, typeId);
				return "NSBStockAdjustmentForm.jsp";
			}
			nsbStockAdjustmentService.saveStockAdjustment(stockAdjustmentDto, user, typeId);
			logger.info("Successfully saved the Stock Adjustment Form.");
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", stockAdjustmentDto.getStockAdjustment().getSaNumber());
		model.addAttribute("formId", stockAdjustmentDto.getStockAdjustment().getId());
		model.addAttribute("ebObjectId", stockAdjustmentDto.getStockAdjustment().getEbObjectId());
		return "successfullySaved";
	}

	@RequestMapping(value="{typeId}/viewForm", method=RequestMethod.GET)
	public String viewStockAdjustmentForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model) throws IOException {
		logger.info("Showing the view form of Stock Adjustment.");
		NSBStockAdjustmentDto stockAdjustmentDto = new NSBStockAdjustmentDto();
		StockAdjustment stockAdjustment = stockAdjustmentService.getProcessedStockAdjustment(pId, typeId);
		Integer ebObjectId = stockAdjustment.getEbObjectId();
		List<SerialItem> serializedItems = null;
		if (stockAdjustment.getFormWorkflow().getCurrentStatusId() != FormStatus.CANCELLED_ID){
			serializedItems = serializedItemService.getSerializeItemsByRefObjectIdAndObject(ebObjectId,
					stockAdjustment.getWarehouseId(), typeId == StockAdjustment.STOCK_ADJUSTMENT_IN ?
							NSBStockAdjustmentDto.SA_IN_TO_SI_OR_TYPE_ID : NSBStockAdjustmentDto.SA_OUT_TO_SI_OR_TYPE_ID,
							false, stockAdjustment);
		} else {
			serializedItems = (List<SerialItem>) serializedItemService.getCancelledSerialItemByRef(ebObjectId,
						stockAdjustment.getWarehouseId()).getData();
		}
		stockAdjustmentDto.setSerialItems(serializedItems);
		stockAdjustment.setReferenceDocuments(refDocumentService.processReferenceDocs(ebObjectId));
		stockAdjustmentDto.setStockAdjustment(stockAdjustment);
		model.addAttribute("hasReference", !stockAdjustment.getReferenceDocuments().isEmpty());
		model.addAttribute("stockAdjustmentDto", stockAdjustmentDto);
		model.addAttribute("typeId", typeId);
		return "NSBStockAdjustmentView.jsp";
	}
}
