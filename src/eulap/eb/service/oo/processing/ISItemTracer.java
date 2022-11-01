package eulap.eb.service.oo.processing;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.domain.Domain;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.PrRawMaterialsItem;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.domain.hibernate.RTransferReceiptItem;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountSaleISService;
import eulap.eb.service.CashSaleISService;
import eulap.eb.service.CashSaleReturnIsService;
import eulap.eb.service.RrRawMaterialService;
import eulap.eb.service.TransferReceiptISService;
import eulap.eb.service.oo.OOServiceHandler;
import eulap.eb.service.oo.ObjectInfoProcessor;
import eulap.eb.service.processing.ProcessingReportService;

/**
 * Trace the history / source of the item. 



 *
 */
@Service
public class ISItemTracer {
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private OOServiceHandler ooServiceHandler;
	@Autowired
	private RrRawMaterialService rRRawMaterialService;
	@Autowired
	private ProcessingReportService prService;
	@Autowired
	private CashSaleISService cashSaleISService;

	/**
	 * Trace source of the child domain object given its eb object id.
	 * @param childObjectId The eb object id of the child domain.
	 * @param user The logged user.
	 * @return The ISItemTrail object.
	 * @throws InvalidClassException
	 * @throws ClassNotFoundException
	 */
	public ISItemTrail trace (Integer childObjectId, User user) throws InvalidClassException, ClassNotFoundException {
		EBObject parentObject = objectToObjectDao.getParent(childObjectId);
		ObjectInfoProcessor objecInfoProcessor = ooServiceHandler.getObjectInfoProcessor(parentObject.getId(), user);
		Domain parent = objecInfoProcessor.getDomain(parentObject);
		if (parent instanceof APInvoice) {
			return trace ((APInvoice) parent); // Receiving Report
		} else if (parent instanceof StockAdjustment) {
			return trace ((StockAdjustment) parent);
		} else if (parent instanceof ProcessingReport) {
			return trace ((ProcessingReport) parent, user);
		} else if (parent instanceof RTransferReceipt) {
			return trace ((TransferReceiptISService) objecInfoProcessor, (RTransferReceipt) parent, user);
		} else if (parent instanceof CashSaleReturn) {
			return trace ((CashSaleReturnIsService) objecInfoProcessor, (CashSaleReturn) parent, user);
		} else if (parent instanceof ArTransaction) {
			return trace ((AccountSaleISService) objecInfoProcessor, (ArTransaction) parent, user);
		}
		throw new RuntimeException("Unkown domain: " + parent);
	}

	private ISItemTrail trace (APInvoice ap) {
		ap = rRRawMaterialService.getRawMaterialWithItems(ap.getId(), ap.getInvoiceTypeId());
		List<RReceivingReportItem> rrItems = ap.getRrItems();
		List<Domain> children = new ArrayList<>();
		for (RReceivingReportItem rrItem : rrItems) {
			children.add(rrItem.getRmItem());
		}
		return ISItemTrail.getInstance(ap, null, children, true);
	}

	private ISItemTrail trace (StockAdjustment adjustment) {
		List<Domain> children = new ArrayList<>(adjustment.getSaItems());
		return ISItemTrail.getInstance(adjustment, null, children, true);
	}

	private ISItemTrail trace (TransferReceiptISService transferReceiptISService, RTransferReceipt transferReceipt, User user) 
			throws InvalidClassException, ClassNotFoundException {
		transferReceipt = transferReceiptISService.getTrAndItems(transferReceipt.getId()) ;
		List<RTransferReceiptItem> rTransferReceiptItems = transferReceipt.getrTrItems();
		List<Domain> children = new ArrayList<>(rTransferReceiptItems);
		List<ISItemTrail> sources = new ArrayList<>();
		for (RTransferReceiptItem tri : rTransferReceiptItems) {
			Integer refObjectId = tri.getRefenceObjectId();
			ISItemTrail source = trace(refObjectId, user);
			sources.add(source);
		}
		return ISItemTrail.getInstance(transferReceipt, sources, children, false);
	}

	private ISItemTrail trace (ProcessingReport processingReport, User user) throws InvalidClassException, ClassNotFoundException {
		// Re-initialize object.
		processingReport = prService.getProcessingReport(processingReport.getId(), false);
		List<Domain> children = new ArrayList<>(processingReport.getPrMainProducts());
		List<PrRawMaterialsItem> prRawMaterialsItems = processingReport.getPrRawMaterialsItems();
		List<ISItemTrail> sources = new ArrayList<>();
		for (PrRawMaterialsItem prRawMaterialsItem : prRawMaterialsItems) {
			Integer refObjectId = prRawMaterialsItem.getRefenceObjectId();
			ISItemTrail source = trace(refObjectId, user);
			sources.add(source);
		}
		return ISItemTrail.getInstance(processingReport, sources, children, false);
	}

	private ISItemTrail trace (CashSaleReturnIsService cashSaleReturnIsService, CashSaleReturn cashSaleReturn, User user) 
			throws InvalidClassException, ClassNotFoundException {
		List<CashSaleReturnItem> csrItems = cashSaleReturnIsService.getCsrItems(cashSaleReturn.getId());
		List<Domain> children = new ArrayList<>();
		List<ISItemTrail> sources = new ArrayList<>();
		CashSaleItem csi = null;
		for (CashSaleReturnItem csri : csrItems) {
			if (csri.getQuantity() < 0) { // Only the returns. Ignore the exchange.
				csi = cashSaleISService.getCSItemWithReference(csri.getCashSaleItemId());
				Integer refObjectId = csi.getRefenceObjectId();
				ISItemTrail source = trace(refObjectId, user);
				sources.add(source);
				children.add(csri);
			}
		}
		return ISItemTrail.getInstance(cashSaleReturn, sources, children, false);
	}

	private ISItemTrail trace (AccountSaleISService accountSaleISService, ArTransaction arTransaction, User user) 
			throws InvalidClassException, ClassNotFoundException {
		List<AccountSaleItem> asrItems = accountSaleISService.getASRItems(arTransaction.getId());
		List<Domain> children = new ArrayList<>();
		List<ISItemTrail> sources = new ArrayList<>();
		AccountSaleItem asi = null;
		for (AccountSaleItem asri : asrItems) {
			if (asri.getQuantity() < 0) { // Only the returns. Ignore the exchange.
				asi = accountSaleISService.getASItemWithReference(asri.getRefAccountSaleItemId());
				Integer refObjectId = asi.getRefenceObjectId();
				ISItemTrail source = trace(refObjectId, user);
				sources.add(source);
				children.add(asi);
			}
		}
		return ISItemTrail.getInstance(arTransaction, sources, children, false);
	}
}
