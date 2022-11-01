package eulap.eb.web.report.jesper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleArLine;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.CashSaleItemService;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.web.dto.CashSaleDto;
/**
 * A class the generate PDF file format of retail Cash Sales.

 */
@Controller
@RequestMapping("cashSalePDF")
public class CashSaleJR {
	private final static int OR_LENGTH = 10;
	private static Logger logger =  Logger.getLogger(CashSaleJR.class);
	private final static String REPORT_TITLE = "ORDER SLIP";
	private final static String SLIP_TITLE = "CASH SALE - OUT SLIP";
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private CashSaleItemService cashSaleItemService;

	@RequestMapping(value="/{typeId}/pdf", method = RequestMethod.GET)
	private String showReport (@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize, Model model){
		if (pId != null) {
			logger.info("Loadingthe cash sales pdf format.");
			logger.debug("Loading cash sales with an id " + pId);
			JRDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(pId, typeId));
			getCommonParam(model, dataSource, pId, fontSize, "pdf");
		}
		logger.info("Sucessfully loaded the cash sales.");
		if (typeId == CashSaleType.PROCESSING) {
			return "CashSaleProcessing.jasper";
		} else if (typeId == CashSaleType.INDIV_SELECTION) {
			return "CashSaleISForm.jasper";
		}
		return "CashSaleForm.jasper";
	}

	private List<CashSaleDto> getDataSource(int pId, Integer typeId) {
		List<CashSaleDto> datasource = new ArrayList<CashSaleDto>();
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		List<CashSaleItem> csItems = null;
		if (!typeId.equals(CashSaleType.INDIV_SELECTION)) {
			csItems = saleItemUtil.generateSaleItemPrintout(cashSaleItemService.getAllCashSaleItems(pId));
		} else {
			csItems = saleItemUtil.generateSaleItemPrintout(cashSaleService.getCSItems(pId));
		}
		List<CashSaleArLine> arLines = cashSaleService.getCSArLines(pId);
		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		otherCharges.addAll(arLines);
		List<AROtherCharge> processedList = cashSaleService.getDetailOtherCharges(otherCharges);
		List<CashSaleArLine> processedArLines = new ArrayList<CashSaleArLine>();
		for (AROtherCharge oc : processedList) {
			processedArLines.add((CashSaleArLine) oc);
		}
		datasource.add(CashSaleDto.getInstanceOf(csItems, processedArLines));
		return datasource;
	}

	private void getCommonParam(Model model, JRDataSource dataSource, Integer cashSaleId, Integer fontSize, String format) {
		CashSale cashSale =   cashSaleService.getCashSalePrint(cashSaleId);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", format);

		model.addAttribute("companyLogo", cashSale.getCompany().getLogo());
		model.addAttribute("slipTitle", SLIP_TITLE);
		model.addAttribute("companyName", cashSale.getCompany().getName());
		model.addAttribute("companyAddress", cashSale.getCompany().getAddress());
		model.addAttribute("companyTin", cashSale.getCompany().getTin());
		model.addAttribute("reportTitle" , REPORT_TITLE);
		model.addAttribute("customerName", cashSale.getArCustomer().getName());
		model.addAttribute("customerAcct", cashSale.getArCustomerAccount().getName());
		model.addAttribute("address", cashSale.getArCustomer().getAddress());
		model.addAttribute("salesInvoiceNo", cashSale.getSalesInvoiceNo());
		model.addAttribute("csNumber", cashSale.getFormattedCSNumber());
		model.addAttribute("date", DateUtil.removeTimeFromDate(cashSale.getReceiptDate()));
		model.addAttribute("term", cashSale.getArCustomerAccount().getTerm().getName());
		model.addAttribute("currDate", new Date());

		model.addAttribute("cash", cashSale.getCash());
		model.addAttribute("checkNo", cashSale.getRefNumber());
		double subTotal = cashSale.getTotalAmount();
		double totalVat = cashSaleService.computeTotalVat(cashSaleId);
		model.addAttribute("total", subTotal-totalVat);
		model.addAttribute("totalVat", totalVat);
		double totalAmountDue = subTotal;
		WithholdingTaxAcctSetting wtax = cashSale.getWtAcctSetting();
		if (wtax != null) {
			double wtAmount = cashSale.getWtAmount() != null ? cashSale.getWtAmount() : 0;
			totalAmountDue -= wtAmount;
			model.addAttribute("wtAmount", wtAmount);
			model.addAttribute("wtAcctSetting", wtax.getName());
		}
		model.addAttribute("totalAmountDue", totalAmountDue);
		model.addAttribute("change", (cashSale.getCash() - totalAmountDue));
		String formLabel = cashSaleService.getCsLabel(cashSale.getCashSaleTypeId());
		model.addAttribute("formLabel", formLabel + " No.");
		SaleItemUtil.getFontSize(fontSize, model);

		FormWorkflow formWorkflow = cashSale.getFormWorkflow();
		List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
		for (FormWorkflowLog log : logs) {
			User user = log.getCreated();
			Position position = user.getPosition();
			if (log.getFormStatusId() == FormStatus.CREATED_ID) {
				model.addAttribute("preparedBy",
						user.getLastName() + " " + user.getFirstName());
				model.addAttribute("preparedPosition", position.getName());
			}
		}

		FormWorkflowLog log = cashSale.getFormWorkflow().getCurrentLogStatus();
		if(log != null) {
			String name = log.getCreated().getLastName() + ", " + log.getCreated().getFirstName();
			model.addAttribute("receivedBy", name);
			model.addAttribute("authorizedPerson", name);
		}
		String orNumber = cashSale.getCsNumber().toString();
		int orLength = orNumber.length();
		if(orLength != OR_LENGTH){
			for (; orLength < OR_LENGTH; orLength++) {
				orNumber = "0"+orNumber;
			}
		}
		model.addAttribute("orNumber", orNumber);
	}

	@RequestMapping(value="/{typeId}/html", method = RequestMethod.GET)
	private String showHTMLReport (@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize, Model model){
		if(pId != null) {
			logger.info("Loadingthe cash sales pdf format.");
			logger.debug("Loading cash sales with an id " + pId);
			JRDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(pId, typeId));
			getCommonParam(model, dataSource, pId, fontSize, "html");
		}
		logger.info("Sucessfully loaded the cash sales.");
		if (typeId == CashSaleType.PROCESSING) {
			return "CashSaleProcessing.jasper";
		}
		return "CashSaleFormHTML.jasper";
	}

	@RequestMapping(value="/receipt", method = RequestMethod.GET)
	private String showReceiptReport (@RequestParam(value="pId", required=true) Integer pId,
			@RequestParam(value="fontSize", required=false) Integer fontSize, Model model){
		if(pId != null) {
			logger.info("Loadingthe cash sales pdf format.");
			logger.debug("Loading cash sales with an id " + pId);
			JRDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(pId, CashSaleType.RETAIL));
			getCommonParam(model, dataSource, pId, fontSize, "html");
		}
		logger.info("Sucessfully loaded the cash sales.");
		return "CashSaleReceipt.jasper";
	}
}
