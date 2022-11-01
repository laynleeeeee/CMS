package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.TaxUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.ApInvoiceLineDao;
import eulap.eb.dao.ApLineSetupDao;
import eulap.eb.dao.ItemSrpDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.PurchaseOrderLineDao;
import eulap.eb.dao.RPurchaseOrderDao;
import eulap.eb.dao.RPurchaseOrderItemDao;
import eulap.eb.dao.RReceivingReportDao;
import eulap.eb.dao.RReceivingReportItemDao;
import eulap.eb.dao.RReturnToSupplierDao;
import eulap.eb.dao.RReturnToSupplierItemDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PurchaseOrderLine;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.RPurchaseOrderItem;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RReturnToSupplier;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.SerialItemSetup;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.web.dto.InvoicePrintoutDto;
import eulap.eb.web.dto.ReceivingReportDto;

/**
 * A class that handles the business logic of {@link RReceivingReport}


 */

@Service
public class RReceivingReportService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(RReceivingReportService.class);
	@Autowired
	private APInvoiceService apInvoiceService;
	@Autowired
	private ApInvoiceLineDao apInvoiceLineDao;
	@Autowired
	private RReceivingReportDao receivingReportDao;
	@Autowired
	private RReceivingReportItemDao receivingReportItemDao;
	@Autowired
	private ItemSrpDao itemSrpDao;
	@Autowired
	private RPurchaseOrderService purchaseOrderService;
	@Autowired
	private RPurchaseOrderItemDao rPoItemDao;
	@Autowired
	private SupplierAccountDao supplierAcctDao;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private ObjectToObjectDao o2oDao;
	@Autowired
	private RPurchaseOrderDao rPurchaseOrderDao;
	@Autowired
	private RrRawMaterialService rawMaterialService;
	@Autowired
	private RReturnToSupplierItemDao rtsItemDao;
	@Autowired
	private RReturnToSupplierDao rtsDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private SerialItemSetupService sisService;
	@Autowired
	private SerialItemService siService;
	@Autowired
	private ApLineSetupDao apLineSetupDao;
	@Autowired
	private UnitMeasurementDao unitMeasurementDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private PurchaseOrderLineDao polLineDao;

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice apInvoice = (APInvoice) form;
		SupplierAccount supplierAct = supplierAcctDao.get(apInvoice.getSupplierAccountId());
		if (apInvoice.getId() == 0) {
			apInvoice.setSequenceNumber(apInvoiceDao.generateSequenceNumber(apInvoice.getInvoiceTypeId(),
					supplierAct.getCompanyId()));
			supplierAct = null;
		}
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		APInvoice apInvoice = apInvoiceDao.getAPInvoiceByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (apInvoice != null) {
			// Validate RR if it has payment.
			apInvoiceService.validatedInvoiceWithPayment(apInvoice, "RR", currentWorkflowLog, bindingResult);
			if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID 
					&& currentWorkflowLog.getComment() != null && !currentWorkflowLog.getComment().isEmpty()) {
				logger.info("Cancelling RR ");
				String errorMessage = "RR is used as reference in RTS";
				boolean hasError = false;
				int apInvoiceId = 0;
				List<RReceivingReportItem> rrItems = receivingReportItemDao.getRrItems(apInvoice.getId());
				List<RReturnToSupplierItem> rtsItems = null;
				for (RReceivingReportItem rri : rrItems) {
					rtsItems = rtsItemDao.getRtsItemsByRrItem(false, rri.getId());
					if (!rtsItems.isEmpty()) {
						apInvoiceId = rtsItems.get(0).getApInvoiceId();
						RReturnToSupplier rts = rtsDao.getRtsByInvoiceId(apInvoiceId);
						errorMessage += " "+rts.getFormattedRTSNumber();
						apInvoiceService.setErrorMsg(bindingResult, currentWorkflowLog, errorMessage);
						hasError = true;
						break;
					}

					//Only RR with completed status will be validated.
					if (apInvoice.getFormWorkflow().isComplete()) {
						RReceivingReport rr = getRrByInvoiceId(apInvoice.getId());
						double totalQty = ValidationUtil.getTotalQtyPerItem(rri.getItemId(), rrItems);
						errorMessage = ValidationUtil.validateToBeCancelledItem(itemService, rri.getItemId(),
								rr.getWarehouseId(), apInvoice.getGlDate(), totalQty);
						if (errorMessage != null) {
							apInvoiceService.setErrorMsg(bindingResult, currentWorkflowLog, errorMessage);
							break;
						} 
					}
				}

				RReceivingReport rr = getRrByInvoiceId(apInvoice.getId());
				APInvoice apiGs = apInvoiceDao.getApInvoiceGsByRR(rr.getId());
				if(apiGs != null) {
					errorMessage = "RR has been used in AP Invoice Goods/Service " + apiGs.getSequenceNumber();
					apInvoiceService.setErrorMsg(bindingResult, currentWorkflowLog, errorMessage);
					hasError = true;
				}

				if(rtsItems == null || rtsItems.isEmpty()) {
					//Validate to be cancelled Serial Items.
					List<SerialItem> rrSerialItems = siService.getSerializeItemsByRefObjectId(
							apInvoice.getEbObjectId(), rr.getWarehouseId(), RReceivingReport.RECEIVING_REPORT_TO_SERIAL_ITEM, false);
					if(!rrSerialItems.isEmpty()) {
						errorMessage = siService.validateToBeCanceledRefForm(rrSerialItems);
						if(errorMessage != null) {
							hasError = true;
							bindingResult.reject("workflowMessage", errorMessage);
							currentWorkflowLog.setWorkflowMessage(errorMessage);
							return;
						}
					}

					if(!hasError) {
						// Set cancelled serialized items to inactive
						siService.setSerialNumberToInactive(rrSerialItems);
					}
				}
			}
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice apInvoice = (APInvoice) form;
		double currencyRate = apInvoice.getCurrencyRateValue() > 0 ? apInvoice.getCurrencyRateValue() : 1;
		RReceivingReport rr = apInvoice.getReceivingReport();
		apInvoice.setCompanyId(rr.getCompanyId());
		apInvoice.setDivisionId(rr.getDivisionId());
		apInvoice.setBmsNumber(rr.getBmsNumber());
		List<SerialItem> toBeInactivedSerialItems = null;
		List<RReceivingReportItem> toBeDeleteNonSerialItems = null;
		boolean isNew = rr.getId() == 0;
		AuditUtil.addAudit(rr, new Audit(user.getId(), isNew, new Date()));
		if (!isNew) {
			toBeInactivedSerialItems = siService.getSerializedItemByRefObjId(apInvoice.getEbObjectId(), RReceivingReport.RECEIVING_REPORT_TO_SERIAL_ITEM);
			toBeDeleteNonSerialItems = receivingReportItemDao.getAllByRefId(RReceivingReportItem.FIELD.apInvoiceId.name(), apInvoice.getId());

			RReceivingReport savedRr = receivingReportDao.getRrByInvoiceId(apInvoice.getId());
			DateUtil.setCreatedDate(rr, savedRr.getCreatedDate());
		}
		apInvoiceService.saveForm(apInvoice, workflowName, user);
		Integer purchaseOrderId = apInvoice.getPurchaseOderId();
		if (purchaseOrderId != null && isNew) {
			RPurchaseOrder purchaseOrder = rPurchaseOrderDao.get(purchaseOrderId);
			if (purchaseOrder != null) {
				ObjectToObject objectToObject = new ObjectToObject();
				objectToObject.setFromObjectId(purchaseOrder.getEbObjectId());
				objectToObject.setToObjectId(apInvoice.getEbObjectId());
				objectToObject.setOrTypeId(RReceivingReport.RECEIVING_REPORT_TO_PURCHASE_ORDER);
				objectToObject.setCreatedBy(user.getId());
				objectToObject.setCreatedDate(new Date());
				o2oDao.save(objectToObject);

				// Free up memory
				purchaseOrder = null;
				objectToObject = null;
			}
		}
		rr.setApInvoiceId(apInvoice.getId());
		receivingReportDao.saveOrUpdate(rr);

		// Save RR serialized items
		saveRrSerialItems(apInvoice, rr, currencyRate, user);

		// Save RR non-serialized items
		saveRrItems(apInvoice, currencyRate);

		// Save RR other charges
		apInvoice.setApInvoiceLines(processApLines(apInvoice.getApInvoiceLines(), currencyRate));
		rawMaterialService.saveApInvoiceLines(apInvoice);

		// Re-compute total RR amount
		recomputeAmount(apInvoice);

		// Removed previous items/lines
		if (toBeInactivedSerialItems != null) {
			siService.setSerialNumberToInactive(toBeInactivedSerialItems);
			toBeInactivedSerialItems = null;
		}

		if (toBeDeleteNonSerialItems != null) {
			for (RReceivingReportItem rri : toBeDeleteNonSerialItems) {
				receivingReportItemDao.delete(rri);
			}
			toBeDeleteNonSerialItems = null;
		}
	}

	private void saveRrSerialItems(APInvoice apInvoice, RReceivingReport rr, double currencyRate, User user) {
		List<SerialItem> serialItems = rr.getSerialItems();
		for (SerialItem si : serialItems) {
			double quantity = si.getQuantity() != null ? si.getQuantity() : 1;
			double unitCost = si.getUnitCost() != null ? si.getUnitCost() : 0;
			double amount = NumberFormatUtil.multiplyWFP(quantity, unitCost);
			double vatAmount = 0;
			if (TaxUtil.isVatable(si.getTaxTypeId())) {
				vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(TaxUtil.computeVat(amount));
			}
			si.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			double vatQuantity = NumberFormatUtil.divideWFP(vatAmount, quantity, 12);
			si.setUnitCost(CurrencyUtil.convertAmountToPhpRate((unitCost - vatQuantity), currencyRate));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(si.getDiscount() != null ? si.getDiscount() : 0);
			si.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double totalAmount = NumberFormatUtil.roundOffTo2DecPlaces(amount - vatAmount - discount);
			si.setAmount(CurrencyUtil.convertAmountToPhpRate(totalAmount, currencyRate, true));
		}
		siService.saveSerializedItems(serialItems, apInvoice.getEbObjectId(), rr.getWarehouseId(),
				user, RReceivingReport.RECEIVING_REPORT_TO_SERIAL_ITEM, true);
	}

	private List<ApInvoiceLine> processApLines(List<ApInvoiceLine> invoiceLines, double currencyRate) {
		for (ApInvoiceLine apl : invoiceLines) {
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(apl.getDiscount() != null ? apl.getDiscount() : 0);
			apl.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			OtherChargeUtil.recomputeOCCosts(apl, discount, currencyRate);
		}
		return invoiceLines;
	}

	private void saveRrItems(APInvoice apInvoice, double currencyRate) {
		List<RReceivingReportItem> rrItems = apInvoice.getRrItems();
		for (RReceivingReportItem rri : rrItems) {
			rri.setApInvoiceId(apInvoice.getId());
			double quantity = rri.getQuantity();
			double unitCost = rri.getUnitCost() != null ? rri.getUnitCost() : 0;
			double amount = NumberFormatUtil.multiplyWFP(quantity, unitCost);
			double vatAmount = 0;
			if (TaxUtil.isVatable(rri.getTaxTypeId())) {
				vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(TaxUtil.computeVat(amount));
			}
			rri.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			double vatQuantity = NumberFormatUtil.divideWFP(vatAmount, quantity, 12);
			rri.setUnitCost(CurrencyUtil.convertAmountToPhpRate((unitCost - vatQuantity), currencyRate));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(rri.getDiscount() != null ? rri.getDiscount() : 0);
			rri.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double totalAmount = NumberFormatUtil.roundOffTo2DecPlaces(amount - vatAmount - discount);
			rri.setAmount(CurrencyUtil.convertAmountToPhpRate(totalAmount, currencyRate, true));
			receivingReportItemDao.save(rri);
		}
		logger.debug("Successfully saved "+rrItems.size()+" RR Items.");
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		// Do nothing
	}

	private void recomputeAmount(APInvoice apInvoice) {
		double totalAmount = 0;
		List<SerialItem> serialItems = apInvoice.getSerialItems();
		if (serialItems != null && !serialItems.isEmpty()) {
			for (SerialItem si : serialItems) {
				totalAmount += si.getAmount();
				totalAmount += si.getVatAmount() != null ? si.getVatAmount() : 0;
				totalAmount -= si.getDiscount() != null ? si.getDiscount() : 0;
			}
		}
		List<RReceivingReportItem> rrItems = apInvoice.getRrItems();
		if (rrItems != null && !rrItems.isEmpty()) {
			for (RReceivingReportItem rri : rrItems) {
				totalAmount += rri.getAmount();
				totalAmount += rri.getVatAmount() != null ? rri.getVatAmount() : 0;
				totalAmount -= rri.getDiscount() != null ? rri.getDiscount() : 0;
			}
		}
		List<ApInvoiceLine> invoiceLines = apInvoice.getApInvoiceLines();
		if (invoiceLines != null && !invoiceLines.isEmpty()) {
			for (ApInvoiceLine apil : invoiceLines) {
				totalAmount += apil.getAmount();
				totalAmount += apil.getVatAmount() != null ? apil.getVatAmount() : 0;
				totalAmount -= apil.getDiscount() != null ? apil.getDiscount() : 0;
			}
		}
		apInvoice.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalAmount - apInvoice.getWtAmount()));
		apInvoiceDao.update(apInvoice);
	}

	/**
	 * Get the receiving report object.
	 * @param receivingReportId The unique id of receiving report.
	 * @return The receiving report object.
	 */
	public RReceivingReport getReceivingReport (Integer receivingReportId) {
		logger.debug("Retreiving receiving report by id " + receivingReportId);
		RReceivingReport receivingreport = receivingReportDao.get(receivingReportId);
		logger.debug("Retreiving list receiving report items");
		List<RReceivingReportItem> rrItems = receivingreport.getApInvoice().getRrItems();
		Integer companyId = receivingreport.getCompanyId();
		Integer divisionId = receivingreport.getDivisionId();
		Integer itemId = null;
		Double srpAmount = null;
		for(RReceivingReportItem rrItem : rrItems) {
			itemId = rrItem.getItemId();
			srpAmount = itemSrpDao.getLatestItemSrp(companyId, itemId, divisionId).getSrp();
			logger.trace(srpAmount + " is the srp of item id " + itemId+" of company id " + companyId);
			rrItem.getItem().setItemSrp(srpAmount);
			rrItem.setStockCode(rrItem.getItem().getStockCode());
		}
		rrItems = null; // Freeing up memory
		logger.debug("Done retreiving receiving report");
		return receivingreport;
	}

	public List<RReceivingReportItem> getRrItems(int apInvoiceId) {
		return receivingReportItemDao.getRrItems(apInvoiceId);
	}

	public RReceivingReportItem getRrItem(int rrItemId) {
		return receivingReportItemDao.get(rrItemId);
	}

	/**
	 * Get the Receiving Report object by AP Invoice id.
	 * @param apInvoiceId The id of the ap invocie.
	 * @return The Receiving Report object.
	 */
	public RReceivingReport getRrByInvoiceId(int apInvoiceId) {
		return receivingReportDao.getRrByInvoiceId(apInvoiceId);
	}

	/**
	 * Get the list of receiving reports.
	 * @param companyId The company id
	 * @param user The current logged user.
	 * @param warehouseId The ware house id
	 * @param supplierId The supplier id
	 * @param rrNumber The receiving report number
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param status Used, UnUsed and All
	 * @param pageNumber The paged number
	 * @return The page result
	 */
	public Page<RReceivingReport> getReceivingReports(User user, Integer companyId, Integer warehouseId, Integer supplierId,
			String rrNumber, Date dateFrom, Date dateTo, Integer status, Integer pageNumber) {
		Page<RReceivingReport> receivingReports = receivingReportDao.getReceivingReports(user, companyId, warehouseId, 
				supplierId, rrNumber, dateFrom, dateTo, status, new PageSetting(pageNumber));
		return receivingReports;
	}

	/**
	 * Get the item latest unit cost per supplier account. 
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param supplierAcctId The id of the account of the supplier.
	 * @return  The latest unit cost per supplier account.
	 */
	public double getLatestUcPerSupplierAcct(int itemId, int warehouseId, int supplierAcctId, boolean isSerial) {
		return receivingReportItemDao.getLatestUcPerSupplierAcct(itemId, warehouseId, supplierAcctId, isSerial);
	}

	/**
	 * Get the latest unit cost of the item by warehouse.
	 * @param itemId The unique id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @return The latest unit cost of the item.
	 */
	public double getLatestUcPerWarehouse(int itemId, int warehouseId, boolean isSerial) {
		return receivingReportItemDao.getLatestUcPerWarehouse(itemId, warehouseId, isSerial);
	}

	/**
	 * Get the objects needed to edit the Receiving Report form.
	 * @param apInvoiceId The unique id of the ap invoice.
	 * @return The {@link APInvoice}
	 * @throws IOException 
	 */
	public APInvoice getRrForEditing(Integer apInvoiceId) throws IOException {
		APInvoice apInvoice = apInvoiceService.getInvoice(apInvoiceId);
		RReceivingReport rr = getRrByInvoiceId(apInvoiceId);
		apInvoice.setReceivingReport(rr);
		Integer companyId = rr.getCompanyId();
		Integer divisionId = rr.getDivisionId();
		Integer warehouseId = rr.getWarehouseId();
		boolean isCancelled = apInvoice.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID;
		double currencyRateValue = apInvoice.getCurrencyRateValue() > 0 ? apInvoice.getCurrencyRateValue() : 1;
		List<SerialItem> serialItems = siService.getSerializeItemsByRefObjectId(companyId,
				divisionId, apInvoice.getEbObjectId(), apInvoice.getReceivingReport().getWarehouseId(),
				RReceivingReport.RECEIVING_REPORT_TO_SERIAL_ITEM, isCancelled);
		List<RReceivingReportItem> rrItems = getRrItems(apInvoiceId);
		for(SerialItem si : serialItems) {
			Double unitCost = si.getUnitCost();
			Double vatAmount =  null;
			if(si.getVatAmount() != null) {
				vatAmount = si.getVatAmount();
				unitCost += vatAmount;//Add vat amount.
			}
			si.setUnitCost(CurrencyUtil.convertMonetaryValues(unitCost, currencyRateValue));
			si.setVatAmount(CurrencyUtil.convertMonetaryValues(vatAmount, currencyRateValue));
		}
		RPurchaseOrderItem orderItem = null;
		for (RReceivingReportItem rri : rrItems) {
			int itemId = rri.getItemId();
			rri.setSrp(apInvoiceService.getItemSrp(companyId, itemId, divisionId));
			rri.setStockCode(rri.getItem().getStockCode());
			rri.setExistingStocks(itemService.getItemExistingStocks(itemId, warehouseId));
			if (rri.getEbObjectId() != null) {
				orderItem = purchaseOrderService.getPOByInvoiceEBObject(rri.getEbObjectId());
				rri.setReferenceObjectId(orderItem.getEbObjectId());
				rri.setOrigRefObjectId(orderItem.getEbObjectId());
				apInvoice.setPurchaseOderId(orderItem.getrPurchaseOrderId());
				orderItem = null;
			}
			Double unitCost =rri.getUnitCost() != null ? rri.getUnitCost() : 0.0;
			Double vatAmount =  null;
			Double qty = rri.getQuantity();
			if (rri.getVatAmount() != null) {
				vatAmount = rri.getVatAmount();
				unitCost += NumberFormatUtil.divideWFP(vatAmount, qty);
			}
			rri.setOrigQty(qty);
			rri.setUnitCost(CurrencyUtil.convertMonetaryValues(unitCost, currencyRateValue));
			rri.setVatAmount(CurrencyUtil.convertMonetaryValues(vatAmount, currencyRateValue));
		}
		apInvoice.setRrItems(rrItems);

		List<ApInvoiceLine> apInvoiceLines = getApInvoiceLines(apInvoiceId);
		PurchaseOrderLine pol = null;
		for (ApInvoiceLine apl : apInvoiceLines) {
			// Set the name for editing.
			pol = polLineDao.getByApInvoiceLineObjectId(apl.getEbObjectId());
			if(pol != null) {
				apl.setReferenceObjectId(pol.getEbObjectId());
			}
			apl.setApLineSetupName(apl.getApLineSetup().getName());
			if (apl.getUnitOfMeasurementId() != null) {
				apl.setUnitMeasurementName(apl.getUnitMeasurement().getName());
			}
			apl.setAmount(CurrencyUtil.convertMonetaryValues(apl.getAmount(), currencyRateValue));
			apl.setUpAmount(CurrencyUtil.convertMonetaryValues(apl.getUpAmount(), currencyRateValue));
			Double vatAmount =  null;
			if(apl.getVatAmount() != null) {
				vatAmount = CurrencyUtil.convertMonetaryValues(apl.getVatAmount(), currencyRateValue);
			}
			apl.setVatAmount(vatAmount);
		}
		apInvoice.setApInvoiceLines(apInvoiceLines);
		apInvoice.getReceivingReport().setSerialItems(serialItems);
		apInvoice.setReferenceDocuments(refDocumentService.processReferenceDocs(apInvoice.getEbObjectId()));
		return apInvoice;
	}

	/**
	 * Get the list of AP Invoice lines by AP Invoice id.
	 * @param apInvoiceId The unique id of the ap invoice.
	 * @return The list of {@link ApInvoiceLine}
	 */
	public List<ApInvoiceLine> getApInvoiceLines(int apInvoiceId) {
		return apInvoiceLineDao.getAllByRefId("apInvoiceId", apInvoiceId);
	}

	/**
	 * Convert PO to Receiving report.
	 * @param poId The PO ID.
	 * @param user The current user logged.
	 * @return The converted receiving report.
	 */
	public RReceivingReport getAndConvertPO(Integer poId, User user) throws IOException {
		logger.debug("Get and convert PO to RR.");
		RPurchaseOrder purchaseOrder = purchaseOrderService.getRpurchaseOrder(poId);
		APInvoice invoiceRr = new APInvoice();
		RReceivingReport rr = new RReceivingReport();

		//Set the values for RR
		rr.setCompanyId(purchaseOrder.getCompanyId());
		rr.setCompany(purchaseOrder.getCompany());
		rr.setPoNumber(purchaseOrder.getPoNumber().toString());

		//Set the values for AP Invoice
		invoiceRr.setCompanyId(purchaseOrder.getCompanyId());
		invoiceRr.setDivisionId(purchaseOrder.getDivisionId());
		invoiceRr.setCurrencyId(purchaseOrder.getCurrencyId());
		invoiceRr.setCurrencyRateId(purchaseOrder.getCurrencyRateId());
		invoiceRr.setCurrencyRateValue(purchaseOrder.getCurrencyRateValue());
		invoiceRr.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		invoiceRr.setSupplierId(purchaseOrder.getSupplierId());
		invoiceRr.setSupplier(purchaseOrder.getSupplier());
		invoiceRr.setSupplierAccountId(purchaseOrder.getSupplierAccountId());
		invoiceRr.setSupplierAccount(purchaseOrder.getSupplierAccount());
		invoiceRr.setInvoiceDate(purchaseOrder.getPoDate());
		invoiceRr.setPurchaseOderId(poId);
		invoiceRr.setTermId(purchaseOrder.getTermId());
		invoiceRr.setInvoiceTypeId(getRrApInvoiceType(purchaseOrder.getDivisionId()));
		invoiceRr.setBmsNumber(purchaseOrder.getBmsNumber());

		// Set the values for RR Items
		Item item = null;
		RReceivingReportItem rrItem = null;
		SerialItem serialItem = null;
		List<RReceivingReportItem> rrItems = new ArrayList<RReceivingReportItem>();
		List<SerialItem> rrSerialItems = new ArrayList<>();
		List<RPurchaseOrderItem> items = purchaseOrder.getrPoItems();
		for (RPurchaseOrderItem poItem : items) {
			item = poItem.getItem();
			SerialItemSetup si = sisService.getSerialItemSetup(item.getId());
			if (si != null && si.isSerializedItem() == true) {
				double vatAmount = NumberFormatUtil.divideWFP(poItem.getVatAmount(), poItem.getQuantity());
				double remainingQty = serialItemService.getPoRemainingQuantity(purchaseOrder.getEbObjectId(), poItem.getItemId());
				for (int i=0; i < remainingQty; i++) {
					serialItem = new SerialItem();
					serialItem.setItem(item);
					serialItem.setItemId(item.getId());
					serialItem.setQuantity(1.0);
					serialItem.setUnitCost(poItem.getUnitCost());
					serialItem.setStockCode(item.getStockCode());
					serialItem.setRefenceObjectId(poItem.getEbObjectId());
					serialItem.setOrigRefObjectId(poItem.getEbObjectId());
					serialItem.setVatAmount(vatAmount);
					serialItem.setTaxTypeId(poItem.getTaxTypeId());
					rrSerialItems.add(serialItem);
				}
			} else {
				rrItem = new RReceivingReportItem();
				double remQty = getRemainingQty(poItem.getEbObjectId(), poItem.getItemId());
				if(remQty > 0) {
					rrItem.setItem(item);
					rrItem.setItemId(item.getId());
					rrItem.setQuantity(remQty);
					rrItem.setUnitCost(poItem.getUnitCost());
					rrItem.setStockCode(item.getStockCode());
					rrItem.setReferenceObjectId(poItem.getEbObjectId());
					rrItem.setOrigRefObjectId(poItem.getEbObjectId());
					rrItem.setVatAmount(poItem.getVatAmount());
					rrItem.setTaxTypeId(poItem.getTaxTypeId());
					rrItems.add(rrItem);
				}
			}
		}
		rr.setSerialItems(rrSerialItems);
		invoiceRr.setRrItems(rrItems);

		List<ApInvoiceLine> apInvoiceLines = new ArrayList<ApInvoiceLine>();
		ApInvoiceLine apLine = null;
		List<PurchaseOrderLine> poLines = purchaseOrder.getPoLines();
		for (PurchaseOrderLine pol : poLines) {
			double remainingQty = apInvoiceLineDao.getRemainingQty(pol.getEbObjectId(), pol.getApLineSetupId(), null);
			if(remainingQty > 0) {
				apLine = new ApInvoiceLine();
				apLine.setApLineSetupId(pol.getApLineSetupId());
				apLine.setReferenceObjectId(pol.getEbObjectId());
				apLine.setApLineSetupName(apLineSetupDao.get(pol.getApLineSetupId()).getName());
				apLine.setQuantity(remainingQty);
				if(pol.getQuantity() != null) {
					apLine.setPercentile(NumberFormatUtil.multiplyWFP(NumberFormatUtil.divideWFP(remainingQty, pol.getQuantity()), 100.00));
				}
				Integer uomId = pol.getUnitOfMeasurementId();
				if (uomId != null) {
					apLine.setUnitOfMeasurementId(uomId);
					apLine.setUnitMeasurementName(unitMeasurementDao.get(uomId).getName());
				}
				apLine.setUpAmount(pol.getUpAmount());
				apLine.setAmount(pol.getAmount());
				apLine.setVatAmount(pol.getVatAmount());
				apLine.setTaxTypeId(pol.getTaxTypeId());
				apInvoiceLines.add(apLine);
			}
		}
		invoiceRr.setApInvoiceLines(apInvoiceLines);
		rr.setApInvoice(invoiceRr);
		logger.debug("Successfully converted the PO to RR object.");
		return rr;
	}

	/**
	 * Get the total quantity of the item per purchase order number.
	 * @param itemId The unique id of the item.
	 * @param poNumber The purchase order number of the receiving report.
	 * @return The total quantity of the item per purchase order number.
	 */
	public double getTotalItemQtyByPO(int itemId, String poNumber) {
		return receivingReportItemDao.getTotalItemQtyByPO(itemId, poNumber);
	}

	/**
	 * Get the PO Remaining QTY by reference object id.
	 * @param refenceObjectId The reference object id.
	 * @return The remaining quantity.
	 */
	public double getRemainingQty(Integer refenceObjectId, Integer itemId) {
		return rPoItemDao.getRemainingQty(refenceObjectId, itemId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		ObjectInfo apInvoiceObjectInfo = apInvoiceService.getObjectInfo(ebObjectId, user);
		APInvoice apInvoice = apInvoiceDao.getByEbObjectId(ebObjectId);
		String title = "Receiving Report - " + apInvoice.getSequenceNumber();
		return ObjectInfo.getInstance(ebObjectId, title,
				apInvoiceObjectInfo.getLatestStatus(),
				apInvoiceObjectInfo.getShortDescription(),
				apInvoiceObjectInfo.getFullDescription(),
				apInvoiceObjectInfo.getPopupLink(),
				apInvoiceObjectInfo.getPrintOutLink());
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case APInvoice.RECEIVING_REPORT_OBJECT_TYPE_ID:
				return apInvoiceDao.getByEbObjectId(ebObjectId);
			case RReceivingReportItem.RR_ITEM_OBJECT_TYPE_ID:
				return receivingReportItemDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return apInvoiceService.getFormWorkflow(id);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return apInvoiceService.getFormByWorkflow(workflowId);
	}

	/**
	 * Compute the sub total of items and other charges
	 * @param receivingReportItems The list of RR items
	 * @param apInvoiceLines The list of other charges
	 * @return The computed subtotal amount
	 */
	public double getSubTotal(List<RReceivingReportItem> receivingReportItems,
			List<ApInvoiceLine> apInvoiceLines, List<SerialItem> serialItems) {
		double subTotal = 0;
		for (RReceivingReportItem rri : receivingReportItems) {
			double unitCost = rri.getUnitCost() != null ? rri.getUnitCost() : 0;
			double vat = rri.getVatAmount() != null ? rri.getVatAmount() : 0;
			double discount = rri.getDiscount() != null ? rri.getDiscount() : 0;
			subTotal += NumberFormatUtil.roundOffTo2DecPlaces((NumberFormatUtil.multiplyWFP(unitCost, rri.getQuantity()) + vat) - discount);
		}
		for (SerialItem si : serialItems) {
			double unitCost = si.getUnitCost() != null ? si.getUnitCost() : 0;
			double vat = si.getVatAmount() != null ? si.getVatAmount() : 0;
			double discount = si.getDiscount() != null ? si.getDiscount() : 0;
			subTotal += NumberFormatUtil.roundOffTo2DecPlaces((NumberFormatUtil.multiplyWFP(unitCost, si.getQuantity()) + vat) - discount);
		}
		for (ApInvoiceLine apl : apInvoiceLines) {
			subTotal += apl.getAmount();
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(subTotal);
	}

	/**
	 * Compute the sub total of items and other charges
	 * @param receivingReportItems The list of RR items
	 * @param apInvoiceLines The list of other charges
	 * @return The computed total VAT amount
	 */
	public double getTotalVat(List<RReceivingReportItem> receivingReportItems,
			List<ApInvoiceLine> apInvoiceLines, List<SerialItem> serialItems) {
		double totalVat = 0;
		for (RReceivingReportItem rri : receivingReportItems) {
			totalVat += (rri.getVatAmount() != null ? rri.getVatAmount() : 0);
		}
		for (ApInvoiceLine apl : apInvoiceLines) {
			totalVat += (apl.getVatAmount() != null ? apl.getVatAmount() : 0);
		}
		for (SerialItem si : serialItems) {
			totalVat += (si.getVatAmount() != null ? si.getVatAmount() : 0);
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalVat);
	}

	/**
	 * Get the AP invoice - RR printout data
	 * @param pId The AP invoice RR object
	 * @return The AP invoice - RR printout data
	 */
	public List<InvoicePrintoutDto> getInvoicePrintoutDtos(APInvoice apInvoice) {
		List<InvoicePrintoutDto> invoicePrintoutDtos = new ArrayList<InvoicePrintoutDto>();
		InvoicePrintoutDto invoicePrintoutDto = new InvoicePrintoutDto();
		List<ReceivingReportDto> receivingReportDtos = new ArrayList<ReceivingReportDto>();
		ReceivingReportDto receivingReportDto = new ReceivingReportDto();
		receivingReportDto.setApInvoiceLines(apInvoice.getApInvoiceLines());
		receivingReportDto.setReceivingReportItems(apInvoice.getRrItems());
		receivingReportDto.setSerialItems(apInvoice.getReceivingReport().getSerialItems());
		receivingReportDtos.add(receivingReportDto);
		invoicePrintoutDto.setReceivingReportDtos(receivingReportDtos);
		invoicePrintoutDto.setCocTaxDtos(apInvoiceService.getCocTaxDtos(apInvoice));
		invoicePrintoutDtos.add(invoicePrintoutDto);
		return invoicePrintoutDtos;
	}

	/**
	 * Get the RR Invoice type by division id.
	 * @param divisionId The division id.
	 * @return The RR AP Invoice type.
	 */
	public int getRrApInvoiceType(Integer divisionId) {
		switch(divisionId) {
		case 1:
			return InvoiceType.RR_CENTRAL_TYPE_ID;
		case 2:
			return InvoiceType.RR_NSB3_TYPE_ID;
		case 3:
			return InvoiceType.RR_NSB4_TYPE_ID;
		case 4:
			return InvoiceType.RR_NSB5_TYPE_ID;
		case 5:
			return InvoiceType.RR_NSB8_TYPE_ID;
		case 6:
			return InvoiceType.RR_NSB8A_TYPE_ID;
		default:
			return 0;
		}
	}
}
