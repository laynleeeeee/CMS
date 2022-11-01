package eulap.eb.service;

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
import eulap.common.util.StringFormatUtil;
import eulap.common.util.TaxUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.ApInvoiceGoodsDao;
import eulap.eb.dao.ApInvoiceLineDao;
import eulap.eb.dao.RReceivingReportDao;
import eulap.eb.dao.RReceivingReportItemDao;
import eulap.eb.dao.RReturnToSupplierDao;
import eulap.eb.dao.RReturnToSupplierItemDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.SerialItemDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceGoods;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RReturnToSupplier;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.OOServiceHandler;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.web.dto.ApInvoiceDto;

/**
 * A class that handles the business logic of {@link RReturnToSupplier}

 */

@Service
public class RReturnToSupplierService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(RReturnToSupplierService.class);
	@Autowired
	private RReturnToSupplierDao rReturnToSupplierDao;
	@Autowired
	private RReturnToSupplierItemDao rReturnToSupplierItemDao;
	@Autowired
	private RReceivingReportItemDao receivingReportItemDao;
	@Autowired
	private RReceivingReportDao receivingReportDao;
	@Autowired
	private APInvoiceService apInvoiceService;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ApInvoiceGsService apInvoiceGsService;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private ApInvoiceLineDao apInvoiceLineDao;
	@Autowired
	private ApInvoiceGoodsDao apInvoiceGoodsDao;
	@Autowired
	private TermService termService;
	@Autowired
	private OOServiceHandler o2oServiceHandler;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private SerialItemDao serialItemDao;
	@Autowired
	private ReferenceDocumentDao referenceDocDao;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private SupplierAccountService supplierAcctService;
	@Autowired
	private RReceivingReportService rrService;
	@Autowired
	private RrRawMaterialService rawMaterialService;

	public static final int MAX_DECIMAL_PLACES = 6;

	/**
	 * Get the list of companies
	 * @param user The current logged user object
	 * @param companyId The company id
	 * @return The list of companies
	 */
	public List<Company> getCompanies(User user, int companyId) {
		return (List<Company>) companyService.getCompaniesWithInactives(user, companyId);
	}

	/**
	 * Get the return to supplier.
	 * @param returnToSupplierId The unique id.
	 * @return The return to supplier object.
	 */
	public RReturnToSupplier getReturnToSupplier(Integer returnToSupplierId) {
		return rReturnToSupplierDao.get(returnToSupplierId);
	}

	/**
	 * Get Retail - RTS by invoice id.
	 * @param apInvoiceId The id of the ap invoice.
	 * @return The Retail - RTS object.
	 */
	public RReturnToSupplier getRtsByInvoiceId(int apInvoiceId) {
		return rReturnToSupplierDao.getRtsByInvoiceId(apInvoiceId);
	}

	/**
	 * Get the list of RTS Items using the invoice id.
	 * @param apInvoiceId The invoice id.
	 * @return The list of RTS Items.
	 */
	public List<RReturnToSupplierItem> getRtsItems(int apInvoiceId) {
		List<RReturnToSupplierItem> returnToSupplierItems = rReturnToSupplierItemDao.getRtsItems(apInvoiceId);
		for (RReturnToSupplierItem rtsItem : returnToSupplierItems) {
			rtsItem.setStockCode(rtsItem.getItem().getStockCode());
			rtsItem.setOrigQty(rtsItem.getQuantity());
		}
		return returnToSupplierItems;
	}
	
	/**
	 * Get the receiving report object and convert it to return to supplier. 
	 * @param receivingReportId The receiving report object.
	 */
	public RReturnToSupplier getAndConvertRR (int receivingReportId) {
		logger.debug("Get and convert RR to RTS.");
		RReceivingReport rr = receivingReportDao.get(receivingReportId);
		logger.trace("The RR to be converted: "+receivingReportId);
		APInvoice invoiceRr = apInvoiceService.getInvoice(rr.getApInvoiceId());
		APInvoice invoiceRts = new APInvoice();
		RReturnToSupplier rts = new RReturnToSupplier();
		if (invoiceRr.getInvoiceDate() != null) {
			rts.setInvoiceDate(DateUtil.formatDate(invoiceRr.getInvoiceDate()));
		}

		//Set the values for RTS
		rts.setRrNumber(rr.getFormattedRRNumber());
		rts.setCompanyId(rr.getCompanyId());
		rts.setWarehouseId(rr.getWarehouseId());
		rts.setCompany(rr.getCompany());
		rts.setWarehouse(rr.getWarehouse());
		rts.setApInvoiceRefId(invoiceRr.getId());

		//Set the values for AP Invoice
		invoiceRts.setServiceLeaseKeyId(invoiceRr.getServiceLeaseKeyId());
		invoiceRts.setSupplierId(invoiceRr.getSupplierId());
		invoiceRts.setSupplier(invoiceRr.getSupplier());
		invoiceRts.setSupplierAccountId(invoiceRr.getSupplierAccountId());
		invoiceRts.setSupplierAccount(invoiceRr.getSupplierAccount());
		invoiceRts.setInvoiceDate(invoiceRr.getInvoiceDate());
		invoiceRts.setInvoiceNumber(invoiceRr.getInvoiceNumber());
		Integer wtAcctSettingId = invoiceRr.getWtAcctSettingId();
		if (wtAcctSettingId != null) {
			invoiceRts.setWtAcctSettingId(wtAcctSettingId);
			invoiceRts.setWtAmount(invoiceRr.getWtAmount());
		}

		//Set the values for RTS Items
		Item item = null;
		RReturnToSupplierItem rtsItem = null;
		List<RReturnToSupplierItem> rrItems = new ArrayList<RReturnToSupplierItem>();
		List<RReceivingReportItem> rReceivingReportItems = receivingReportItemDao.getRrItems(invoiceRr.getId());
		for (RReceivingReportItem rrItem : rReceivingReportItems) {
			item = rrItem.getItem();
			rtsItem = new RReturnToSupplierItem();
			rtsItem.setrReceivingReportItemId(rrItem.getId());
			rtsItem.setItem(item);
			rtsItem.setItemId(item.getId());
			rtsItem.setQuantity(rrItem.getQuantity());
			double unitCost = rrItem.getUnitCost();
			if (TaxUtil.isVatable(rrItem.getTaxTypeId())) {
				double vatQuantity = NumberFormatUtil.divideWFP(rrItem.getVatAmount(), rrItem.getQuantity());
				unitCost = unitCost + vatQuantity;
			}
			rtsItem.setUnitCost(unitCost);
			rtsItem.setStockCode(item.getStockCode());
			rtsItem.setTaxTypeId(rrItem.getTaxTypeId());
			rtsItem.setVatAmount(rrItem.getVatAmount() != null ? rrItem.getVatAmount() : 0.0);
			rrItems.add(rtsItem);
		}
		invoiceRts.setRtsItems(rrItems);
		invoiceRts.setInvoiceTypeId(InvoiceType.RTS_TYPE_ID);
		rts.setApInvoice(invoiceRts);

		logger.debug("Successfully converted the RR to RTS object.");
		return rts;
	}

	/**
	 * Get the total returned quantity of the RR Item.
	 * @param rrItemId
	 * @param rtsItemId
	 * @return
	 */
	public double getReturnedQty(int rrItemId, Integer rtsItemId) {
		List<RReturnToSupplierItem> rtsItems = rReturnToSupplierItemDao.getRtsItemsByRrItem(true, rrItemId);
		double totalReturnedQty = 0;
		if (rtsItems.isEmpty()) {
			logger.debug("No RTS Items retrieved. Total returned quantity is zero.");
			return totalReturnedQty;
		}

		for (RReturnToSupplierItem rtsi : rtsItems) {
			if (rtsItemId != null) {
				//Do not add its own quantity.
				if (rtsItemId.equals(rtsi.getId()))
					continue;
			}
			totalReturnedQty += rtsi.getQuantity();
			logger.trace("Running total returned quantity: "+totalReturnedQty);
		}
		logger.debug("Total quantity returned: "+totalReturnedQty);
		return NumberFormatUtil.roundOffTo2DecPlaces(totalReturnedQty);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return apInvoiceDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return apInvoiceDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		ObjectInfo apInvObjectInfo = apInvoiceService.getObjectInfo(ebObjectId, user);
		StringBuilder shortDesc = new StringBuilder(apInvObjectInfo.getShortDescription());
		String title = "";
		APInvoice apInvoice = apInvoiceDao.getByEbObjectId(ebObjectId);
		int sequenceNo = apInvoice.getSequenceNumber();
		title = "Return to Supplier - " + sequenceNo;
		return ObjectInfo.getInstance(ebObjectId, title,
				apInvObjectInfo.getLatestStatus(),
				shortDesc.toString(),
				apInvObjectInfo.getFullDescription(),
				apInvObjectInfo.getPopupLink(),
				apInvObjectInfo.getPrintOutLink());
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case APInvoice.RTS_OBJECT_TYPE_ID:
				return apInvoiceDao.getByEbObjectId(ebObjectId);
			case SerialItem.OBJECT_TYPE_ID:
				return serialItemDao.getByEbObjectId(ebObjectId);
			case RReturnToSupplierItem.OBJECT_TYPE_ID:
				return rReturnToSupplierItemDao.getByEbObjectId(ebObjectId);
			case ApInvoiceLine.OBJECT_TYPE_ID:
				return apInvoiceLineDao.getByEbObjectId(ebObjectId);
			case ReferenceDocument.OBJECT_TYPE_ID:
				return referenceDocDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice apInvoice = (APInvoice) form;
		RReturnToSupplier rts = apInvoice.getReturnToSupplier();
		boolean isNew = apInvoice.getId() == 0;
		if (isNew) {
			apInvoice.setSequenceNumber(apInvoiceDao.generateSequenceNumber(apInvoice.getInvoiceTypeId(), rts.getCompanyId()));
		} else {
			// Reset created date from the already saved object
			APInvoice savedApInvoice = apInvoiceDao.get(apInvoice.getId());
			DateUtil.setCreatedDate(savedApInvoice, savedApInvoice.getCreatedDate());
			savedApInvoice = null;

			// Inactivate saved serialized items
			List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(apInvoice.getEbObjectId(),
					RReturnToSupplier.RTS_SERIAL_ITEM_OR_TYPE_ID);
			serialItemService.setSerialNumberToInactive(savedSerialItems);
			savedSerialItems = null;

			// Delete saved RTS items
			List<Integer> toBeDeleted = new ArrayList<Integer>();
			List<RReturnToSupplierItem> rtsiItems = rReturnToSupplierItemDao.getRtsItems(apInvoice.getId());
			for (RReturnToSupplierItem rtsi : rtsiItems) {
				toBeDeleted.add(rtsi.getId());
			}
			rReturnToSupplierItemDao.delete(toBeDeleted);
			toBeDeleted = null;
			rtsiItems = null;
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice apInvoice = (APInvoice) form;
		RReturnToSupplier rts = apInvoice.getReturnToSupplier();
		boolean isNew = apInvoice.getId() == 0;
		Integer parentObjectId = apInvoice.getEbObjectId();

		if (!isNew) {
			APInvoice savedApInvoice = apInvoiceDao.get(apInvoice.getId());
			DateUtil.setCreatedDate(savedApInvoice, savedApInvoice.getCreatedDate());
			savedApInvoice = null;
		} else {
			apInvoice.setCurrencyRateId((apInvoice.getCurrencyRateId() != null && apInvoice.getCurrencyRateId() != 0)
					? apInvoice.getCurrencyRateId() : null);
			// Save RR to AP invoice goods relationship
			o2oServiceHandler.saveObjectToObjectRelationship(apInvoice.getReferenceObjectId(), parentObjectId,
					RReturnToSupplier.RTS_API_GS_OR_TYPE_ID, user);
		}

		boolean isValidRate = apInvoice.getCurrencyRateValue() != null && apInvoice.getCurrencyRateValue() != 0;
		double currencyRate = isValidRate ? apInvoice.getCurrencyRateValue() : 1;
		apInvoice.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		apInvoice.setCurrencyRateValue(currencyRate);
		if (apInvoice.getWtAcctSettingId() != null) {
			apInvoice.setWtAmount(CurrencyUtil.convertAmountToPhpRate(apInvoice.getWtAmount(), currencyRate, true));
		}
		apInvoice.setCompanyId(rts.getCompanyId());
		AuditUtil.addAudit(apInvoice, new Audit(user.getId(), isNew, new Date()));
		apInvoiceDao.saveOrUpdate(apInvoice);

		AuditUtil.addAudit(rts, new Audit(user.getId(), isNew, new Date()));
		rts.setApInvoiceId(apInvoice.getId());
		rReturnToSupplierDao.saveOrUpdate(rts);

		// Save serialized items
		saveRtsSerialItems(apInvoice, rts.getWarehouseId(), currencyRate, user);

		// Save RTS items
		saveRtsItems(apInvoice, currencyRate);

		// Save AP lines (services)
		apInvoice.setApInvoiceLines(processApLines(apInvoice.getApInvoiceLines(), currencyRate));
		rawMaterialService.saveApInvoiceLines(apInvoice);

		recomputeRtsTotal(apInvoice);

		// Saved reference documents
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				apInvoice.getReferenceDocuments(), true);
	}

	private void saveRtsSerialItems(APInvoice apInvoice, int warehouseId, double currencyRate, User user) {
		List<SerialItem> serialItems = apInvoice.getSerialItems();
		for (SerialItem si : serialItems) {
			double quantity = si.getQuantity() != null ? si.getQuantity() : 1;
			double vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(si.getVatAmount() != null ? si.getVatAmount() : 0);
			double unitCost = si.getUnitCost() != null ? si.getUnitCost() : 0;
			double vatQuantity = NumberFormatUtil.divideWFP(vatAmount, quantity, 12);
			si.setUnitCost(CurrencyUtil.convertAmountToPhpRate((unitCost-vatQuantity), currencyRate));
			si.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(si.getDiscount() != null ? si.getDiscount() : 0);
			si.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, unitCost) - vatAmount - discount);
			si.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		}
		serialItemService.saveSerializedItems(serialItems, apInvoice.getEbObjectId(), warehouseId,
				user, RReturnToSupplier.RTS_SERIAL_ITEM_OR_TYPE_ID, true);
	}

	private void saveRtsItems(APInvoice apInvoice, double currencyRate) {
		List<RReturnToSupplierItem> rtsItems = apInvoice.getRtsItems();
		for (RReturnToSupplierItem rtsi : rtsItems) {
			rtsi.setApInvoiceId(apInvoice.getId());
			double quantity = rtsi.getQuantity();
			double vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(rtsi.getVatAmount() != null ? rtsi.getVatAmount() : 0);
			double unitCost = rtsi.getUnitCost() != null ? rtsi.getUnitCost() : 0;
			double vatQuantity = NumberFormatUtil.divideWFP(vatAmount, quantity, 12);
			rtsi.setUnitCost(CurrencyUtil.convertAmountToPhpRate((unitCost-vatQuantity), currencyRate));
			rtsi.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(rtsi.getDiscount() != null ? rtsi.getDiscount() : 0);
			rtsi.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, unitCost) - vatAmount - discount);
			rtsi.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
			rReturnToSupplierItemDao.save(rtsi);
		}
		logger.debug("Successfully saved "+rtsItems.size()+" RTS Items.");
	}

	private List<ApInvoiceLine> processApLines(List<ApInvoiceLine> invoiceLines, double currencyRate) {
		for (ApInvoiceLine apl : invoiceLines) {
			double quantity = apl.getQuantity() != null ? apl.getQuantity() : 1;
			double vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(apl.getVatAmount() != null ? apl.getVatAmount() : 0);
			double upAmount = apl.getUpAmount() != null ? apl.getUpAmount() : 0;
			apl.setUpAmount(CurrencyUtil.convertAmountToPhpRate(upAmount, currencyRate));
			apl.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(apl.getDiscount() != null ? apl.getDiscount() : 0);
			apl.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, upAmount) - vatAmount - discount);
			apl.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		}
		return invoiceLines;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		APInvoice apInvoice = apInvoiceDao.getAPInvoiceByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (apInvoice != null) {
			// Validate RTS if it has payment.
			apInvoiceService.validatedInvoiceWithPayment(apInvoice, "RTS ", currentWorkflowLog, bindingResult);
		}
		if (!bindingResult.hasErrors() && currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			// Set cancelled serialized items to inactive
			int apInvoiceId = apInvoice.getId();
			RReturnToSupplier rts = getRtsByInvoiceId(apInvoiceId);
			List<SerialItem> serialItems = serialItemService.getSerializeItemsByRefObjectId(apInvoice.getCompanyId(),
					apInvoice.getDivisionId(), apInvoice.getEbObjectId(), rts.getWarehouseId(),
					RReturnToSupplier.RTS_SERIAL_ITEM_OR_TYPE_ID, false);
			serialItemService.setSerialNumberToInactive(serialItems);
		}
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		// Do nothing
	}

	/**
	 * Compute the total amount of RTS Items.
	 */
	private void recomputeRtsTotal(APInvoice apInvoice) {
		double totalAmount = 0;
		List<SerialItem> serialItems = apInvoice.getSerialItems();
		if (serialItems != null && !serialItems.isEmpty()) {
			for (SerialItem si : serialItems) {
				totalAmount += si.getAmount();
				totalAmount += si.getVatAmount() != null ? si.getVatAmount() : 0;
				totalAmount -= si.getDiscount() != null ? si.getDiscount() : 0;
			}
		}
		List<RReturnToSupplierItem> rtsItems = apInvoice.getRtsItems();
		if (rtsItems != null && !rtsItems.isEmpty()) {
			for (RReturnToSupplierItem rtsi : rtsItems) {
				totalAmount += rtsi.getAmount();
				totalAmount += rtsi.getVatAmount() != null ? rtsi.getVatAmount() : 0;
				totalAmount -= rtsi.getDiscount() != null ? rtsi.getDiscount() : 0;
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
	 * Get the RTS - AP invoice
	 * @param pId The AP invoice form id
	 * @return The RTS - AP invoice object
	 */
	public APInvoice getRtsApInvoice(int pId) {
		logger.debug("Retrieve return to supplier of form id " + pId);
		APInvoice apInvoice = apInvoiceService.getInvoice(pId);
		RReturnToSupplier rts = getRtsByInvoiceId(pId);
		rts.setCompany(companyService.getCompany(apInvoice.getCompanyId()));
		rts.setCompanyId(apInvoice.getCompanyId());
		// Get the AP invoice reference object id
		EBObject referenceObject = ooLinkHelper.getReferenceObject(apInvoice.getEbObjectId(),
				RReturnToSupplier.RTS_API_GS_OR_TYPE_ID);
		Integer referenceObjId = referenceObject.getId();
		apInvoice.setReferenceObjectId(referenceObjId);
		apInvoice.setSupplier(supplierService.getSupplier(apInvoice.getSupplierId()));
		apInvoice.setSupplierAccount(supplierAcctService.getSupplierAccount(apInvoice.getSupplierAccountId()));
		// Get the invoice g/s object
		APInvoice invoiceGs = apInvoiceDao.getByEbObjectId(referenceObjId);
		EBObject rrRefObject = ooLinkHelper.getReferenceObject(invoiceGs.getEbObjectId(),
				APInvoice.RR_INVOICE_GS_OR_TYPE_ID);
		APInvoice rrInvoice = apInvoiceDao.getByEbObjectId(rrRefObject.getId());
		RReceivingReport rr = rrService.getRrByInvoiceId(rrInvoice.getId());
		apInvoice.setPoNumber(rr.getPoNumber());
		apInvoice.setRrNumber(invoiceGs.getSequenceNumber());
		apInvoice.setStrInvoiceDate(DateUtil.formatDate(invoiceGs.getInvoiceDate()));
		apInvoice.setStrGlDate(DateUtil.formatDate(invoiceGs.getGlDate()));
		// Get serialized items
		double rate = apInvoice.getCurrencyRateValue();
		boolean isCancelled = apInvoice.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID;
		List<SerialItem> serialItems = serialItemService.getSerializeItemsByRefObjectId(apInvoice.getCompanyId(),
				apInvoice.getDivisionId(), apInvoice.getEbObjectId(), rts.getWarehouseId(),
				RReturnToSupplier.RTS_SERIAL_ITEM_OR_TYPE_ID, isCancelled);
		EBObject refObject = null;
		for (SerialItem serialItem : serialItems) {
			refObject = ooLinkHelper.getReferenceObject(serialItem.getEbObjectId(), RReturnToSupplier.RTS_CHILD_TO_CHILD_OR_TYP_ID);
			if (refObject != null) {
				serialItem.setOrigRefObjectId(refObject.getId());
				serialItem.setRefenceObjectId(refObject.getId());
			}
			double unitCost = serialItem.getUnitCost() != null ? serialItem.getUnitCost() : 0.0;
			double vatAmount = 0;
			double qty = serialItem.getQuantity();
			if (serialItem.getVatAmount() != null) {
				vatAmount = serialItem.getVatAmount();
				unitCost += NumberFormatUtil.divideWFP(vatAmount, qty);
			}
			serialItem.setUnitCost(CurrencyUtil.convertMonetaryValues(unitCost, rate));
			serialItem.setVatAmount(CurrencyUtil.convertMonetaryValues(vatAmount, rate));
			serialItem.setDiscount(CurrencyUtil.convertMonetaryValues(serialItem.getDiscount(), rate));
		}
		apInvoice.setSerialItems(serialItems);
		// Get non-serialized items
		List<RReturnToSupplierItem> rtsItems = getRtsItems(pId);
		for (RReturnToSupplierItem rtsi : rtsItems) {
			refObject = ooLinkHelper.getReferenceObject(rtsi.getEbObjectId(), RReturnToSupplier.RTS_CHILD_TO_CHILD_OR_TYP_ID);
			if (refObject != null) {
				rtsi.setOrigRefObjectId(refObject.getId());
				rtsi.setRefenceObjectId(refObject.getId());
			}
			double unitCost = rtsi.getUnitCost() != null ? rtsi.getUnitCost() : 0.0;
			double vatAmount = 0;
			double qty = rtsi.getQuantity();
			if (rtsi.getVatAmount() != null) {
				vatAmount = rtsi.getVatAmount();
				unitCost += NumberFormatUtil.divideWFP(vatAmount, qty);
			}
			rtsi.setExistingStocks(itemService.getItemExistingStocks(rtsi.getItemId(), rts.getWarehouseId()));
			rtsi.setUnitCost(CurrencyUtil.convertMonetaryValues(unitCost, rate));
			rtsi.setVatAmount(CurrencyUtil.convertMonetaryValues(vatAmount, rate));
			rtsi.setDiscount(CurrencyUtil.convertMonetaryValues(rtsi.getDiscount(), rate));
		}
		apInvoice.setRtsItems(rtsItems);
		// Get AP invoice lines
		List<ApInvoiceLine> apInvoiceLines = apInvoiceLineDao.getAllByRefId("apInvoiceId", pId);
		for (ApInvoiceLine apl : apInvoiceLines) {
			String apLineName = apl.getApLineSetup().getName();
			if (apLineName != null) {
				apl.getApLineSetup().setName(StringFormatUtil.removeExtraWhiteSpaces(apLineName));
			}
			refObject = ooLinkHelper.getReferenceObject(apl.getEbObjectId(), RReturnToSupplier.RTS_CHILD_TO_CHILD_OR_TYP_ID);
			if (refObject != null) {
				apl.setReferenceObjectId(refObject.getId());
			}
			if (apl.getUnitOfMeasurementId() != null) {
				apl.setUnitMeasurementName(apl.getUnitMeasurement().getName());
			}
			apl.setUpAmount(CurrencyUtil.convertMonetaryValues(apl.getUpAmount(), rate));
			apl.setVatAmount(CurrencyUtil.convertMonetaryValues(apl.getVatAmount(), rate));
			apl.setAmount(CurrencyUtil.convertMonetaryValues(apl.getAmount(), rate));
			apl.setDiscount(CurrencyUtil.convertMonetaryValues(apl.getDiscount(), rate));
		}
		apInvoice.setApInvoiceLines(apInvoiceLines);
		apInvoice.setReturnToSupplier(rts);
		return apInvoice;
	}

	/**
	 * Get the list of Goods and Services by filter/s.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param supplierId The supplier id.
	 * @param supplierAcctId The supplier account id.
	 * @param invGsNumber The Good Service References.
	 * @param bmsNumber The BMS number.
	 * @param dateFrom The date filtering will start.
	 * @param dateTo The date filtering will end.
	 * @param status The status (1 = all, 2 = unused, 3 = used.)
	 * @param pageNumber The page number
	 * @return The list of Goods and Services.
	 */
	public Page<ApInvoiceDto> getInvGsReferences(Integer companyId, Integer divisionId, Integer supplierId,
			Integer supplierAcctId, Integer invGsNumber, String bmsNumber, Date dateFrom, Date dateTo,
			Integer status, Integer pageNumber) {
		return apInvoiceDao.getInvGsReferences(companyId, divisionId, supplierId, supplierAcctId, invGsNumber,
				bmsNumber, dateFrom, dateTo, status, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Convert Invoice GS to RTS
	 * @param invoiceId The invoiced id
	 * @return The converted RTS
	 */
	public APInvoice convertInvGsToRts(Integer apInvoiceId) {
		APInvoice rtsInvoice = new APInvoice();
		APInvoice invoiceGs = apInvoiceGsService.getInvoiceGoodsServices(apInvoiceId);
		if (invoiceGs != null) {
			rtsInvoice.setDivision(invoiceGs.getDivision());
			rtsInvoice.setDivisionId(invoiceGs.getDivisionId());
			rtsInvoice.setRrNumber(invoiceGs.getSequenceNumber());
			rtsInvoice.setInvoiceNumber(invoiceGs.getInvoiceNumber());
			rtsInvoice.setBmsNumber(invoiceGs.getBmsNumber());
			rtsInvoice.setSupplier(invoiceGs.getSupplier());
			rtsInvoice.setSupplierId(invoiceGs.getSupplierId());
			rtsInvoice.setSupplierAccount(invoiceGs.getSupplierAccount());
			rtsInvoice.setSupplierAccountId(invoiceGs.getSupplierAccountId());
			rtsInvoice.setTerm(termService.getTerm(invoiceGs.getTermId()));
			rtsInvoice.setTermId(invoiceGs.getTermId());
			rtsInvoice.setStrInvoiceDate(DateUtil.formatDate(invoiceGs.getInvoiceDate()));
			rtsInvoice.setStrGlDate(DateUtil.formatDate(invoiceGs.getGlDate()));
			rtsInvoice.setCurrency(invoiceGs.getCurrency());
			rtsInvoice.setCurrencyId(invoiceGs.getCurrencyId());
			rtsInvoice.setCurrencyRateId(invoiceGs.getCurrencyRateId());
			rtsInvoice.setCurrencyRateValue(invoiceGs.getCurrencyRateValue());
			rtsInvoice.setWtAcctSettingId(invoiceGs.getWtAcctSettingId());
			rtsInvoice.setReferenceObjectId(invoiceGs.getEbObjectId());

			RReturnToSupplier rts = new RReturnToSupplier();
			rts.setCompanyId(invoiceGs.getCompanyId());
			rts.setCompany(invoiceGs.getCompany());
			rts.setWarehouseId(invoiceGs.getWarehouseId());
			rtsInvoice.setReturnToSupplier(rts);

			List<SerialItem> serilizedItems = new ArrayList<SerialItem>();
			SerialItem serialItem = null;
			for (SerialItem rrsi : invoiceGs.getSerialItems()) {
				serialItem = new SerialItem();
				if(serialItemDao.isAlreadyUsedSerialNumber(rrsi.getSerialNumber())) {
					continue;
				}
				serialItem.setItemId(rrsi.getItemId());
				serialItem.setStockCode(itemService.getItem(rrsi.getItemId()).getStockCode());
				serialItem.setQuantity(1.0);
				serialItem.setUnitCost(rrsi.getUnitCost());
				serialItem.setRefenceObjectId(rrsi.getEbObjectId());
				serialItem.setOrigRefObjectId(rrsi.getEbObjectId());
				serialItem.setDiscount(rrsi.getDiscount());
				serialItem.setItemDiscount(rrsi.getItemDiscount());
				serialItem.setItemDiscountId(rrsi.getItemDiscountId());
				serialItem.setItemDiscountTypeId(rrsi.getItemDiscountTypeId());
				serialItem.setItemDiscountType(rrsi.getItemDiscountType());
				serialItem.setDiscountValue(rrsi.getDiscountValue());
				serialItem.setVatAmount(rrsi.getVatAmount());
				serialItem.setTaxTypeId(rrsi.getTaxTypeId());
				serialItem.setSerialNumber(rrsi.getSerialNumber());
				serilizedItems.add(serialItem);
				serialItem = null;
			}
			rtsInvoice.setSerialItems(serilizedItems);

			List<ApInvoiceGoods> apInvoiceGoods = invoiceGs.getApInvoiceGoods();
			List<RReturnToSupplierItem> rtsItems = new ArrayList<RReturnToSupplierItem>();
			RReturnToSupplierItem rtsItem = null;
			for (ApInvoiceGoods goods : apInvoiceGoods) {
				double remainingQty = apInvoiceGoodsDao.getInvGsRemainingQty(null,
						invoiceGs.getEbObjectId(), goods.getItemId());
				if (remainingQty > 0) {
					rtsItem = new RReturnToSupplierItem();
					rtsItem.setRefenceObjectId(goods.getEbObjectId());
					rtsItem.setOrigRefObjectId(goods.getEbObjectId());
					rtsItem.setQuantity(remainingQty);
					rtsItem.setItemId(goods.getItemId());
					rtsItem.setStockCode(goods.getItem().getStockCode());
					rtsItem.setUnitCost(goods.getUnitCost());
					rtsItem.setVatAmount(goods.getVatAmount());
					rtsItem.setTaxTypeId(goods.getTaxTypeId());
					rtsItem.setDiscountValue(goods.getDiscountValue());
					rtsItem.setDiscount(goods.getDiscount());
					rtsItem.setItemDiscountTypeId(goods.getItemDiscountTypeId());
					rtsItem.setItemDiscountType(goods.getItemDiscountType());
					rtsItems.add(rtsItem);
				}
			}
			rtsInvoice.setRtsItems(rtsItems);

			List<ApInvoiceLine> apInvoiceLines = new ArrayList<ApInvoiceLine>();
			List<ApInvoiceLine> rrApLines = invoiceGs.getApInvoiceLines();
			ApInvoiceLine apLine = null;
			for (ApInvoiceLine apil : rrApLines) {
				double remainingQty = apInvoiceLineDao.getRemainingInvGsLineQty(null,
						invoiceGs.getEbObjectId(), apil.getApLineSetupId());
				if (remainingQty > 0) {
					apLine = new ApInvoiceLine();
					apLine.setReferenceObjectId(apil.getEbObjectId());
					apLine.setQuantity(remainingQty);
					apLine.setApLineSetupId(apil.getApLineSetupId());
					apLine.setApLineSetupName(apil.getApLineSetupName());
					apLine.setPercentile(NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(NumberFormatUtil.divideWFP(remainingQty, apil.getQuantity()), 100)));
					apLine.setUnitOfMeasurementId(apil.getUnitOfMeasurementId());
					apLine.setUnitMeasurementName(apil.getUnitMeasurementName());
					apLine.setUpAmount(apil.getUpAmount());
					apLine.setVatAmount(apil.getVatAmount());
					apLine.setAmount(apil.getAmount());
					apLine.setTaxTypeId(apil.getTaxTypeId());

					apLine.setDiscount(apil.getDiscount());
					apLine.setItemDiscountType(apil.getItemDiscountType());
					apLine.setDiscountTypeId(apil.getDiscountTypeId());
					apLine.setDiscountValue(apil.getDiscountValue());
					apInvoiceLines.add(apLine);
					apLine = null;
				}
			}
			rtsInvoice.setApInvoiceLines(apInvoiceLines);
		}
		return rtsInvoice;
	}
}
