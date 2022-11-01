package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

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
import eulap.eb.dao.ApInvoiceAccountDao;
import eulap.eb.dao.ApInvoiceGoodsDao;
import eulap.eb.dao.ApInvoiceLineDao;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.RPurchaseOrderDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.SerialItemDao;
import eulap.eb.dao.VatAcctSetupDao;
import eulap.eb.dao.WithholdingTaxAcctSettingDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApInvoiceAccount;
import eulap.eb.domain.hibernate.ApInvoiceGoods;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.VatAcctSetup;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.OOServiceHandler;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.ApInvoiceDto;
import eulap.eb.web.dto.ApInvoiceItemDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.InvoicePrintoutDto;
import eulap.eb.web.dto.ResultProperty;

/**
 * Service class that will handle business logic for AP invoice goods / services

 */

@Service
public class ApInvoiceGsService extends BaseWorkflowService {
	@Autowired
	private APInvoiceService apInvoiceService;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private ApInvoiceGoodsDao apInvoiceGoodsDao;
	@Autowired
	private ApInvoiceLineDao apInvoiceLineDao;
	@Autowired
	private ReferenceDocumentDao referenceDocDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private OOServiceHandler o2oServiceHandler;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private RReceivingReportService rrService;
	@Autowired
	private TermService termService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private SupplierAccountService supplierAcctService;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private ItemService itemService;
	@Autowired
	private RPurchaseOrderDao purchaseOrderDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private AccountCombinationService acctCombinationService;
	@Autowired
	private VatAcctSetupDao vatAcctSetupDao;
	@Autowired
	private ApInvoiceAccountDao apInvoiceAcctDao;
	@Autowired
	private WithholdingTaxAcctSettingDao wtAcctSettingDao;
	@Autowired
	private SerialItemDao serialItemDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ApPaymentDao apPaymentDao;

	public static final int MAX_DECIMAL_PLACES = 6;

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice apInvoice = (APInvoice) form;
		if (apInvoice.getId() == 0) {
			apInvoice.setSequenceNumber(apInvoiceDao.generateSequenceNumber(apInvoice.getInvoiceTypeId(),
					apInvoice.getCompanyId()));
		} else {
			List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(apInvoice.getEbObjectId(), APInvoice.GS_SERIAL_ITEM_OR_TYPE_ID);
			serialItemService.setSerialNumberToInactive(savedSerialItems);
			savedSerialItems = null;
			//Delete saved invoice lines.
			List<ApInvoiceLine> savedOtherCharges = apInvoiceLineDao.getAllByRefId("apInvoiceId", apInvoice.getId());
			for (ApInvoiceLine oc : savedOtherCharges) {
				apInvoiceLineDao.delete(oc);
			}
			savedOtherCharges = null;
			List<ApInvoiceGoods> savedApIgoods = apInvoiceGoodsDao.getAllByRefId("apInvoiceId", apInvoice.getId());
			for(ApInvoiceGoods apig : savedApIgoods) {
				apInvoiceGoodsDao.delete(apig);
			}
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice apInvoice = (APInvoice) form;
		boolean isNew = apInvoice.getId() == 0;
		Integer parentObjectId = apInvoice.getEbObjectId();
		AuditUtil.addAudit(apInvoice, new Audit(user.getId(), isNew, new Date()));
		if (!isNew) {
			APInvoice savedApInvoice = apInvoiceDao.get(apInvoice.getId());
			DateUtil.setCreatedDate(savedApInvoice, savedApInvoice.getCreatedDate());
			savedApInvoice = null;
		} else {
			apInvoice.setCurrencyRateId((apInvoice.getCurrencyRateId() != null && apInvoice.getCurrencyRateId() != 0)
					? apInvoice.getCurrencyRateId() : null);
			// Save RR to AP invoice goods relationship
			o2oServiceHandler.saveObjectToObjectRelationship(apInvoice.getReferenceObjectId(), parentObjectId,
					APInvoice.RR_INVOICE_GS_OR_TYPE_ID, user);
		}
		String description = apInvoice.getDescription();
		if (description != null) {
			apInvoice.setDescription(StringFormatUtil.removeExtraWhiteSpaces(description));
		}
		apInvoice.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		boolean isValidRate = apInvoice.getCurrencyRateValue() != null && apInvoice.getCurrencyRateValue() != 0;
		double currencyRate = isValidRate ? apInvoice.getCurrencyRateValue() : 1.0;
		apInvoice.setCurrencyRateValue(currencyRate);
		if (apInvoice.getWtAcctSettingId() != null) {
			apInvoice.setWtAmount(CurrencyUtil.convertAmountToPhpRate(apInvoice.getWtAmount(), currencyRate, true));
		}
		apInvoiceDao.saveOrUpdate(apInvoice);
		saveSerializedItems(apInvoice, currencyRate, user);
		saveInvoiceGoods(apInvoice, currencyRate);
		saveInvoiceServices(apInvoice, currencyRate);
		recomputeAmount(apInvoice);
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				apInvoice.getReferenceDocuments(), true);
	}

	private void recomputeAmount(APInvoice apInvoice) {
		double totalAmount = 0;
		List<SerialItem> serialItems = apInvoice.getSerialItems();
		for (SerialItem si : serialItems) {
			totalAmount += si.getAmount();
			totalAmount += si.getVatAmount() != null ? si.getVatAmount() : 0;
		}
		List<ApInvoiceGoods> apInvoiceGoods = apInvoice.getApInvoiceGoods();
		for (ApInvoiceGoods apig : apInvoiceGoods) {
			totalAmount += apig.getAmount();
			totalAmount += apig.getVatAmount() != null ? apig.getVatAmount() : 0;
		}
		List<ApInvoiceLine> invoiceLines = apInvoice.getApInvoiceLines();
		for (ApInvoiceLine apil : invoiceLines) {
			totalAmount += apil.getAmount();
			totalAmount += apil.getVatAmount() != null ? apil.getVatAmount() : 0;
		}
		apInvoice.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalAmount - apInvoice.getWtAmount()));
		apInvoiceDao.update(apInvoice);
	}

	private void saveSerializedItems(APInvoice apInvoice, double currencyRate, User user) {
		List<SerialItem> serialItems = apInvoice.getSerialItems();
		// Convert serialized amount values to PHP before saving
		for (SerialItem si : serialItems) {
			double quantity = si.getQuantity() != null ? si.getQuantity() : 1;
			double vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(si.getVatAmount() != null ? si.getVatAmount() : 0);
			double unitCost = si.getUnitCost() != null ? si.getUnitCost() : 0;
			double vatQuantity = NumberFormatUtil.divideWFP(vatAmount, quantity, 12);
			si.setUnitCost(CurrencyUtil.convertAmountToPhpRate((unitCost - vatQuantity), currencyRate));
			si.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(si.getDiscount() != null ? si.getDiscount() : 0);
			si.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, unitCost) - vatAmount - discount);
			si.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		}
		serialItemService.saveSerializedItems(serialItems, apInvoice.getEbObjectId(),
				apInvoice.getWarehouseId(), user, APInvoice.GS_SERIAL_ITEM_OR_TYPE_ID, true);
	}


	private void saveInvoiceGoods(APInvoice apInvoice, double currencyRate) {
		List<ApInvoiceGoods> apInvoiceGoods = apInvoice.getApInvoiceGoods();
		for (ApInvoiceGoods apig : apInvoiceGoods) {
			apig.setApInvoiceId(apInvoice.getId());
			double quantity = apig.getQuantity();
			double vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(apig.getVatAmount() != null ? apig.getVatAmount() : 0);
			double unitCost = apig.getUnitCost() != null ? apig.getUnitCost() : 0;
			double vatQuantity = NumberFormatUtil.divideWFP(vatAmount, quantity, 12);
			apig.setUnitCost(CurrencyUtil.convertAmountToPhpRate((unitCost - vatQuantity), currencyRate));
			apig.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(apig.getDiscount() != null ? apig.getDiscount() : 0);
			apig.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, unitCost) - vatAmount - discount);
			apig.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
			apInvoiceGoodsDao.save(apig);
		}
	}

	private void saveInvoiceServices(APInvoice apInvoice, double currencyRate) {
		List<ApInvoiceLine> invoiceLines = apInvoice.getApInvoiceLines();
		for (ApInvoiceLine apl : invoiceLines) {
			apl.setApInvoiceId(apInvoice.getId());
			OtherChargeUtil.recomputeOCCosts(apl, apl.getDiscount(), currencyRate);
			apInvoiceLineDao.save(apl);
		}
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return apInvoiceService.getFormWorkflow(id);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return apInvoiceService.getFormByWorkflow(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		APInvoice apInvoice = apInvoiceDao.getByEbObjectId(ebObjectId);
		Integer pId = apInvoice.getId();
		FormProperty property = workflowHandler.getProperty(apInvoice.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit()  + "?pId=" + pId;
		String printoutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = apInvoice.getFormWorkflow().getCurrentFormStatus().getDescription();
		Division div = divisionService.getDivision(apInvoice.getDivisionId());
		String title = "AP Invoice Goods/Services - " + div.getName() + " " + apInvoice.getSequenceNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + apInvoice.getSupplier().getName())
				.append(" " + apInvoice.getSupplierAccount().getName())
				.append("<b> INVOICE DATE : </b>" + DateUtil.formatDate(apInvoice.getInvoiceDate()))
				.append("<b> GL DATE : </b>" + DateUtil.formatDate(apInvoice.getGlDate()))
				.append("<b> DUE DATE : </b>" + DateUtil.formatDate(apInvoice.getDueDate()))
				.append(NumberFormatUtil.format(apInvoice.getAmount()));
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), "", popupLink, printoutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case APInvoice.INVOICE_GS_OBJECT_TYPE_ID:
				return apInvoiceDao.getByEbObjectId(ebObjectId);
			case SerialItem.OBJECT_TYPE_ID:
				return serialItemDao.getByEbObjectId(ebObjectId);
			case ApInvoiceGoods.OBJECT_TYPE_ID:
				return apInvoiceGoodsDao.getByEbObjectId(ebObjectId);
			case ApInvoiceLine.OBJECT_TYPE_ID:
				return apInvoiceLineDao.getByEbObjectId(ebObjectId);
			case ReferenceDocument.OBJECT_TYPE_ID:
				return referenceDocDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the list of term objects
	 * @param termId The term id
	 * @return The list of term objects
	 */
	public List<Term> getTerms(Integer termId) {
		return termService.getTerms(termId);
	}

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
	 * Get the company object
	 * @param companyId The company id
	 * @return The company object
	 */
	public Company getCompany(int companyId) {
		return companyService.getCompany(companyId);
	}

	/**
	 * Get the list of active currencies. 
	 * Add the inactive currency based on the currencyId parameter.
	 * @param currencyId The currency id.
	 * @return The list of active {@link Currency}.
	 */
	public List<Currency> getActiveCurrencies(Integer currencyId) {
		return currencyService.getActiveCurrencies(currencyId);
	}

	/**
	 * Get the currency object
	 * @param currencyId The currency id
	 * @return The currency object
	 */
	public Currency getCurrency(Integer currencyId) {
		return currencyService.getCurency(currencyId);
	}

	/**
	 * Get the division object
	 * @param divisionId The division id
	 * @return The division object
	 */
	public Division getDivision(int divisionId) {
		return divisionService.getDivision(divisionId);
	}

	/**
	 * Get the list of AP invoice RR form references
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param supplierId The supplier id
	 * @param rrNumber The RR number
	 * @param bmsNumber The BMS number
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @param status The form status
	 * @param pageNumber The page number
	 * @return The list of AP invoice RR form references
	 */
	public Page<ApInvoiceDto> getRrReferences(Integer companyId, Integer divisionId, Integer supplierId, Integer rrNumber,
			String bmsNumber, Date dateFrom, Date dateTo, Integer status, Integer pageNumber) {
		return apInvoiceDao.getRrReferences(companyId, divisionId, supplierId, rrNumber, bmsNumber, dateFrom, dateTo,
				status, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Convert RR object to AP invoice object
	 * @param rrReferenceId The RR reference id
	 * @return The AP invoice object
	 */
	public APInvoice convertRrToInvoice(Integer rrReferenceId) throws IOException {
		APInvoice rrInvoice = rrService.getRrForEditing(rrReferenceId);
		RReceivingReport rr = rrInvoice.getReceivingReport();
		// Get PO reference to set default currency
		EBObject referenceObject = ooLinkHelper.getReferenceObject(rrInvoice.getEbObjectId(),
				RReceivingReport.RECEIVING_REPORT_TO_PURCHASE_ORDER);
		RPurchaseOrder po = purchaseOrderDao.getByEbObjectId(referenceObject.getId());
		APInvoice apInvoice = new APInvoice();
		apInvoice.setWarehouseId(rr.getWarehouseId());
		apInvoice.setCompany(companyService.getCompany(rr.getCompanyId()));
		apInvoice.setCompanyId(rr.getCompanyId());
		apInvoice.setDivision(divisionService.getDivision(rrInvoice.getDivisionId()));
		apInvoice.setDivisionId(rrInvoice.getDivisionId());
		apInvoice.setReferenceObjectId(rrInvoice.getEbObjectId());
		apInvoice.setBmsNumber(rr.getBmsNumber());
		apInvoice.setPoNumber(rr.getPoNumber());
		apInvoice.setRrNumber(rrInvoice.getSequenceNumber());
		apInvoice.setSupplier(supplierService.getSupplier(rrInvoice.getSupplierId()));
		apInvoice.setSupplierId(rrInvoice.getSupplierId());
		apInvoice.setSupplierAccount(supplierAcctService.getSupplierAccount(rrInvoice.getSupplierAccountId()));
		apInvoice.setSupplierAccountId(rrInvoice.getSupplierAccountId());
		apInvoice.setCurrencyId(po.getCurrencyId());
		apInvoice.setCurrencyRateId(po.getCurrencyRateId());
		apInvoice.setTerm(po.getTerm());
		apInvoice.setTermId(po.getTermId());
		double value = po.getCurrencyRateValue();
		apInvoice.setCurrencyRateValue(value);

		List<SerialItem> serilizedItems = new ArrayList<SerialItem>();
		List<SerialItem> rrSerialItems = serialItemService.getRrRemainingItems(rrInvoice.getEbObjectId());
		SerialItem serialItem = null;
		for (SerialItem rrsi : rrSerialItems) {
			serialItem = new SerialItem();
			Double unitCost = rrsi.getUnitCost();
			Double vatAmount = rrsi.getVatAmount();
			if(vatAmount != null) {
				unitCost += vatAmount;
			}
			serialItem.setItemId(rrsi.getItemId());
			serialItem.setStockCode(itemService.getItem(rrsi.getItemId()).getStockCode());
			serialItem.setQuantity(1.0);
			serialItem.setUnitCost(CurrencyUtil.convertMonetaryValues(unitCost, value));
			serialItem.setRefenceObjectId(rrsi.getEbObjectId());
			serialItem.setOrigRefObjectId(rrsi.getEbObjectId());
			serialItem.setVatAmount(CurrencyUtil.convertMonetaryValues(vatAmount, value));
			serialItem.setTaxTypeId(rrsi.getTaxTypeId());
			serialItem.setSerialNumber(rrsi.getSerialNumber());
			serilizedItems.add(serialItem);
			serialItem = null;
		}
		apInvoice.setSerialItems(serilizedItems);

		List<ApInvoiceGoods> apInvoiceGoods = new ArrayList<ApInvoiceGoods>();
		List<RReceivingReportItem> rrItems = rrInvoice.getRrItems();
		ApInvoiceGoods invGs = null;
		for (RReceivingReportItem rri : rrItems) {
			double remainingQty = apInvoiceGoodsDao.getRrRemainingQty(null,
					rrInvoice.getEbObjectId(), rri.getItemId());
			if(remainingQty > 0) {
				invGs = new ApInvoiceGoods();
				invGs.setItemId(rri.getItemId());
				invGs.setStockCode(rri.getItem().getStockCode());
				invGs.setQuantity(remainingQty);
				invGs.setUnitCost(rri.getUnitCost());
				invGs.setReferenceObjectId(rri.getEbObjectId());
				invGs.setOrigRefObjectId(rri.getEbObjectId());
				invGs.setVatAmount(rri.getVatAmount());
				invGs.setTaxTypeId(rri.getTaxTypeId());
				apInvoiceGoods.add(invGs);
				invGs = null;
			}
		}
		apInvoice.setApInvoiceGoods(apInvoiceGoods);

		List<ApInvoiceLine> apInvoiceLines = new ArrayList<ApInvoiceLine>();
		List<ApInvoiceLine> rrApLines = rrInvoice.getApInvoiceLines();
		ApInvoiceLine apLine = null;
		for (ApInvoiceLine apil : rrApLines) {
			double remainingQty = apInvoiceLineDao.getRemainingRrLineQty(null,
					apil.getEbObjectId(), apil.getApLineSetupId());
			if(remainingQty > 0) {
				apLine = new ApInvoiceLine();
				apLine.setApLineSetupId(apil.getApLineSetupId());
				apLine.setReferenceObjectId(apil.getEbObjectId());
				apLine.setApLineSetupName(apil.getApLineSetupName());
				apLine.setQuantity(remainingQty);
				if (apil.getQuantity() != null) {
					apLine.setPercentile(NumberFormatUtil.roundOffTo2DecPlaces(apil.getPercentile()));
				}
				apLine.setUnitOfMeasurementId(apil.getUnitOfMeasurementId());
				apLine.setUnitMeasurementName(apil.getUnitMeasurementName());
				apLine.setUpAmount(apil.getUpAmount());
				apLine.setVatAmount(apil.getVatAmount());
				apLine.setAmount(apil.getAmount());
				apLine.setTaxTypeId(apil.getTaxTypeId());
				apInvoiceLines.add(apLine);
				apLine = null;
			}
		}
		apInvoice.setApInvoiceLines(apInvoiceLines);

		// Free up memory
		rrInvoice = null;
		rr = null;
		rrSerialItems = null;
		rrItems = null;
		rrApLines = null;
		return apInvoice;
	}

	/**
	 * Get the AP invoice Goods/Services object for form view and edit
	 * @param invoiceId The AP invoice id
	 * @return The AP invoice Goods/Services object for form view and edit
	 */
	public APInvoice getApInvoiceGs(Integer invoiceId) throws IOException {
		APInvoice apInvoice = getInvoiceGoodsServices(invoiceId);
		apInvoice.setReferenceDocuments(refDocumentService.processReferenceDocs(apInvoice.getEbObjectId()));
		return apInvoice;
	}

	/**
	 * Get the AP invoice Goods/Services object
	 * @param invoiceId The AP invoice id
	 * @return The AP invoice Goods/Services object
	 */
	public APInvoice getInvoiceGoodsServices(Integer invoiceId) {
		APInvoice apInvoice = apInvoiceDao.get(invoiceId);
		EBObject referenceObject = ooLinkHelper.getReferenceObject(apInvoice.getEbObjectId(),
				APInvoice.RR_INVOICE_GS_OR_TYPE_ID);
		Integer referenceObjId = referenceObject.getId();
		apInvoice.setReferenceObjectId(referenceObjId);
		apInvoice.setCompany(companyService.getCompany(apInvoice.getCompanyId()));
		apInvoice.setSupplier(supplierService.getSupplier(apInvoice.getSupplierId()));
		apInvoice.setSupplierAccount(supplierAcctService.getSupplierAccount(apInvoice.getSupplierAccountId()));
		APInvoice rrInvoice = apInvoiceDao.getByEbObjectId(referenceObjId);
		RReceivingReport rr = rrService.getRrByInvoiceId(rrInvoice.getId());
		apInvoice.setPoNumber(rr.getPoNumber());
		apInvoice.setRrNumber(rrInvoice.getSequenceNumber());
		apInvoice.setWarehouseId(rr.getWarehouseId());
		double rate = apInvoice.getCurrencyRateValue();
		apInvoice.setWtAmount(CurrencyUtil.convertMonetaryValues(apInvoice.getWtAmount(), rate));
		apInvoice.setAmount(CurrencyUtil.convertMonetaryValues(apInvoice.getAmount(), rate, true));
		// Get serialized items
		boolean isCancelled = apInvoice.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID;
		List<SerialItem> serialItems = serialItemService.getSerializeItemsByRefObjectId(apInvoice.getCompanyId(),
				apInvoice.getDivisionId(), apInvoice.getEbObjectId(), rr.getWarehouseId(),
				APInvoice.GS_SERIAL_ITEM_OR_TYPE_ID, isCancelled);
		for (SerialItem si : serialItems) {
			double unitCost = si.getUnitCost();
			if (TaxUtil.isVatable(si.getTaxTypeId())) {
				double vatAmount = si.getVatAmount();
				unitCost += vatAmount;
				si.setVatAmount(CurrencyUtil.convertMonetaryValues(vatAmount, rate, true));
			}
			if (si.getDiscount() != null) {
				si.setDiscount(CurrencyUtil.convertMonetaryValues(si.getDiscount(), rate, true));
			}
			si.setUnitCost(CurrencyUtil.convertMonetaryValues(unitCost, rate));
			si.setAmount(CurrencyUtil.convertMonetaryValues(si.getAmount(), rate, true));
		}
		// Get non-serialized items
		List<ApInvoiceGoods> apInvoiceGoods = apInvoiceGoodsDao.getAllByRefId("apInvoiceId", invoiceId);
		for (ApInvoiceGoods apig : apInvoiceGoods) {
			referenceObject = ooLinkHelper.getReferenceObject(apig.getEbObjectId(),
					APInvoice.GS_INVOICE_CHILD_TO_CHILD_OR_TYPE_ID);
			if (referenceObject != null) {
				apig.setReferenceObjectId(referenceObject.getId());
			}
			apig.setStockCode(apig.getItem().getStockCode());
			apig.setExistingStocks(itemService.getItemExistingStocks(apig.getItemId(), rr.getWarehouseId()));
			double unitCost = apig.getUnitCost();
			if (TaxUtil.isVatable(apig.getTaxTypeId())) {
				double vatAmount = apig.getVatAmount();
				unitCost += (NumberFormatUtil.divideWFP(vatAmount, apig.getQuantity()));
				apig.setVatAmount(CurrencyUtil.convertMonetaryValues(vatAmount, rate, true));
			}
			if (apig.getDiscount() != null) {
				apig.setDiscount(CurrencyUtil.convertMonetaryValues(apig.getDiscount(), rate, true));
			}
			apig.setUnitCost(CurrencyUtil.convertMonetaryValues(unitCost, rate));
			apig.setAmount(CurrencyUtil.convertMonetaryValues(apig.getAmount(), rate, true));
		}

		// Get AP invoice lines
		List<ApInvoiceLine> apInvoiceLines = apInvoiceLineDao.getAllByRefId("apInvoiceId", invoiceId);
		for (ApInvoiceLine apl : apInvoiceLines) {
			referenceObject = ooLinkHelper.getReferenceObject(apl.getEbObjectId(),
					APInvoice.GS_INVOICE_CHILD_TO_CHILD_OR_TYPE_ID);
			if (referenceObject != null) {
				apl.setReferenceObjectId(referenceObject.getId());
			}
			apl.setApLineSetupName(apl.getApLineSetup().getName());
			if (apl.getUnitOfMeasurementId() != null) {
				apl.setUnitMeasurementName(apl.getUnitMeasurement().getName());
			}
			if (apl.getDiscount() != null) {
				apl.setDiscount(CurrencyUtil.convertMonetaryValues(apl.getDiscount(), rate, true));
			}
			apl.setUpAmount(CurrencyUtil.convertMonetaryValues(apl.getUpAmount(), rate));
			apl.setVatAmount(CurrencyUtil.convertMonetaryValues(apl.getVatAmount(), rate, true));
			apl.setAmount(CurrencyUtil.convertMonetaryValues(apl.getAmount(), rate, true));
		}

		apInvoice.setSerialItems(serialItems);
		apInvoice.setApInvoiceGoods(apInvoiceGoods);
		apInvoice.setApInvoiceLines(apInvoiceLines);
		return apInvoice;
	}

	/**
	 * Validate the AP invoice goods/services form
	 * @param apInvoice The AP invoice object
	 * @param errors The validation errors
	 */
	public void validateForm(APInvoice apInvoice, Errors errors) {
		if (apInvoice.getReferenceObjectId() == null) {
			errors.rejectValue("rrNumber", null, null, ValidatorMessages.getString("ApInvoiceGsService.0"));
		}

		ValidatorUtil.validateCompany(apInvoice.getCompanyId(), companyService, errors, "companyId");

		Integer divisionId = apInvoice.getDivisionId();
		if (divisionId == null) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ApInvoiceGsService.1"));
		} else if (!divisionService.getDivision(divisionId).isActive()) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ApInvoiceGsService.2"));
		}

		String invoiceNumber = apInvoice.getInvoiceNumber();
		if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
			errors.rejectValue("invoiceNumber", null, null, ValidatorMessages.getString("ApInvoiceGsService.3"));
		} else if (StringFormatUtil.removeExtraWhiteSpaces(invoiceNumber).length() > APInvoice.MAX_CHAR_INVOICE_NO) {
			errors.rejectValue("invoiceNumber", null, null, ValidatorMessages.getString("ApInvoiceGsService.4"));
		} else if(apInvoice.getSupplierId() != null &&
				!apInvoiceDao.isUniqueInvoiceNoBySupplier(apInvoice.getSupplierId(), apInvoice.getId(), invoiceNumber)) {
			errors.rejectValue("invoiceNumber", null, null, ValidatorMessages.getString("ApInvoiceGsService.33"));
		}

		Integer termId = apInvoice.getTermId();
		if (termId == null) {
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("ApInvoiceGsService.5"));
		} else if (!termService.getTerm(termId).isActive()) {
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("ApInvoiceGsService.6"));
		}

		Integer supplierId = apInvoice.getSupplierId();
		if (supplierId == null) {
			errors.rejectValue("supplierId", null, null, ValidatorMessages.getString("ApInvoiceGsService.7"));
		} else if (!supplierService.getSupplier(supplierId).isActive()) {
			errors.rejectValue("supplierId", null, null, ValidatorMessages.getString("ApInvoiceGsService.8"));
		}

		Integer supplierAccountId = apInvoice.getSupplierAccountId();
		if (supplierAccountId == null) {
			errors.rejectValue("supplierAccountId", null, null, ValidatorMessages.getString("ApInvoiceGsService.9"));
		} else if (!supplierAcctService.getSupplierAccount(supplierAccountId).isActive()) {
			errors.rejectValue("supplierAccountId", null, null, ValidatorMessages.getString("ApInvoiceGsService.10"));
		}

		Date invoiceDate = apInvoice.getInvoiceDate();
		if (invoiceDate == null) {
			errors.rejectValue("invoiceDate", null, null, ValidatorMessages.getString("ApInvoiceGsService.11"));
		} else if (!timePeriodService.isOpenDate(invoiceDate)) {
			errors.rejectValue("invoiceDate", null, null, ValidatorMessages.getString("ApInvoiceGsService.12"));
		}

		Date glDate = apInvoice.getGlDate();
		if (glDate == null) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("ApInvoiceGsService.13"));
		} else if (!timePeriodService.isOpenDate(glDate)) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("ApInvoiceGsService.14"));
		}

		Date dueDate = apInvoice.getDueDate();
		if (dueDate == null) {
			errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("ApInvoiceGsService.15"));
		} else if (!timePeriodService.isOpenDate(dueDate)) {
			errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("ApInvoiceGsService.16"));
		}

		if (apInvoice.getDescription() == null) {
			errors.rejectValue("description", null, null, ValidatorMessages.getString("ApInvoiceGsService.17"));
		}

		double amount = apInvoice.getAmount();
		if (amount == 0.0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ApInvoiceGsService.18"));
		} else if (amount < 0.0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ApInvoiceGsService.19"));
		}

		if(apInvoice.getCurrencyId() == null) {
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("ApInvoiceGsService.28"));
		} else if(!currencyService.getCurency(apInvoice.getCurrencyId()).isActive()) {
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("ApInvoiceGsService.29"));
		}

		if(apInvoice.getDescription() == null || apInvoice.getDescription().trim().isEmpty()) {
			errors.rejectValue("description", null, null, ValidatorMessages.getString("ApInvoiceGsService.31"));
		}

		List<SerialItem> serialItems = apInvoice.getSerialItems();
		List<ApInvoiceGoods> invoiceGoods = apInvoice.getApInvoiceGoods();
		List<ApInvoiceLine> serviceLines = apInvoice.getApInvoiceLines();
		boolean hasInvoiceGoods = invoiceGoods != null && !invoiceGoods.isEmpty();
		boolean hasSerialItems = serialItems != null && !serialItems.isEmpty();
		boolean hasLines = serviceLines != null && !serviceLines.isEmpty();
		double totalAmountDue = 0;
		if (!hasInvoiceGoods && !hasSerialItems && !hasLines) {
			errors.rejectValue("apInvoiceErrMessage", null, null, ValidatorMessages.getString("ApInvoiceGsService.32"));
		}

		if (hasInvoiceGoods || hasSerialItems) {
			serialItemService.validateSerialItems("apInvoiceGoods", "serialItems", !hasInvoiceGoods,
					false, false, serialItems, errors);
			for (SerialItem si : serialItems) {
				totalAmountDue += si.getAmount();
				totalAmountDue += si.getVatAmount() != null ? si.getVatAmount() : 0;
			}
			int row = 0;
			for (ApInvoiceGoods ig : invoiceGoods) {
				row++;
				double quantity = ig.getQuantity();
				double remainingQty = apInvoiceGoodsDao.getRrRemainingQty(apInvoice.getId(),
						apInvoice.getReferenceObjectId(), ig.getItemId());
				if (ig.getQuantity() > remainingQty) {
					errors.rejectValue("apInvoiceGoods", null, null, String.format(ValidatorMessages.getString("ApInvoiceGsService.20"),
							NumberFormatUtil.format(remainingQty), row));
				} else if (quantity < 0.0) {
					errors.rejectValue("apInvoiceGoods", null, null, String.format(ValidatorMessages.getString("ApInvoiceGsService.21"), row));
				}
				if(!itemDao.get(ig.getItemId()).isActive()) {
					errors.rejectValue("apInvoiceGoods", null, null, String.format(ValidatorMessages.getString("ApInvoiceGsService.30"), row));
				}
				totalAmountDue += ig.getAmount();
				totalAmountDue += ig.getVatAmount() != null ? ig.getVatAmount() : 0;
			}
		}

		if (hasLines) {
			int row = 0;
			for (ApInvoiceLine apl : serviceLines) {
				row++;
				Integer apLineSetupId = apl.getApLineSetupId();
				String apLineName = apl.getApLineSetupName();
				Double rowAmount = apl.getAmount();
				if (apLineSetupId != null || apLineName != null || rowAmount != null) {
					if (apLineSetupId == null) {
						if (apLineName != null && !apLineName.isEmpty()) {
							errors.rejectValue("apInvoiceLines", null, null,
									String.format(ValidatorMessages.getString("ApInvoiceGsService.22"), row));
						} else {
							errors.rejectValue("apInvoiceLines", null, null,
									String.format(ValidatorMessages.getString("ApInvoiceGsService.23"), row));
						}
					}
				}
				Double quantity = apl.getQuantity();
				if (quantity != null) {
					double remainingQty = apInvoiceLineDao.getRemainingRrLineQty(apInvoice.getId(),
							apl.getRefenceObjectId(), apl.getApLineSetupId());
					if (quantity > remainingQty) {
						errors.rejectValue("apInvoiceLines", null, null, 
								String.format(ValidatorMessages.getString("ApInvoiceGsService.26"),
										NumberFormatUtil.format(remainingQty), row));
					}
				}
				totalAmountDue += rowAmount != null ? rowAmount : 0;
				totalAmountDue += apl.getVatAmount() != null ? apl.getVatAmount() : 0;
			}
		}

		if (apInvoice.getWtAcctSettingId() != null) {
			totalAmountDue -= NumberFormatUtil.roundOffTo2DecPlaces(apInvoice.getWtAmount());
		}

		if (NumberFormatUtil.roundOffTo2DecPlaces(amount) != NumberFormatUtil.roundOffTo2DecPlaces(totalAmountDue)) {
			errors.rejectValue("wtAmount", null, null, ValidatorMessages.getString("ApInvoiceGsService.27"));
		}

		refDocumentService.validateReferences(apInvoice.getReferenceDocuments(), errors);
	}

	/**
	 * Get AP invoice withholding tax, BIR 2307 printout data
	 * @param pId The AP invoice id
	 * @return The BIR 2307 printout data
	 * @throws IOException 
	 */
	public List<InvoicePrintoutDto> getInvoicePrintoutDtos(Integer pId) throws IOException {
		List<InvoicePrintoutDto> invoicePrintoutDtos = new ArrayList<InvoicePrintoutDto>();
		InvoicePrintoutDto invoicePrintoutDto = new InvoicePrintoutDto();

		List<ApInvoiceItemDto> apInvoiceItemDtos = new ArrayList<ApInvoiceItemDto>();
		ApInvoiceItemDto apInvoiceItemDto = getInvoiceParticulars(pId);
		apInvoiceItemDto.setApInvoiceDtos(processInvoiceItemDto(apInvoiceItemDto));
		apInvoiceItemDtos.add(apInvoiceItemDto);
		invoicePrintoutDto.setApInvoiceItemDtos(apInvoiceItemDtos);
		invoicePrintoutDtos.add(invoicePrintoutDto);
		invoicePrintoutDto.setCocTaxDtos(apInvoiceService.getCocTaxDtos(apInvoiceItemDto.getApInvoice()));
		return invoicePrintoutDtos;
	}

	/**
	 * Combine and sum the amounts of {@link ApInvoiceDto} with the same account.
	 * @param apInvoiceItemDto The {@link ApInvoiceItemDto}.
	 * @return The processed list of {@link ApInvoiceDto}.
	 */
	private List<ApInvoiceDto> processInvoiceItemDto(ApInvoiceItemDto apInvoiceItemDto) {
		List<ApInvoiceDto> apInvoiceDtos = apInvoiceItemDto.getApInvoiceDtos();
		Map<String, ApInvoiceDto> discHM = new HashMap<String, ApInvoiceDto>();
		ApInvoiceDto apInvoiceDto = null;
		for(ApInvoiceDto dto : apInvoiceDtos) {
			if(discHM.containsKey(dto.getAccountNo())) {
				apInvoiceDto = discHM.get(dto.getAccountNo());
				if(apInvoiceDto.getCredit() != null) {
					apInvoiceDto.setCredit(apInvoiceDto.getCredit() + dto.getCredit());
				}
				if(apInvoiceDto.getDebit() != null) {
					apInvoiceDto.setDebit(apInvoiceDto.getDebit() + dto.getDebit());
				}
				discHM.put(dto.getAccountNo(), apInvoiceDto);
			} else {
				discHM.put(dto.getAccountNo(), dto);
			}
		}
		return new ArrayList<ApInvoiceDto>(discHM.values());
	}

	private ApInvoiceItemDto getInvoiceParticulars(Integer pId) throws IOException {
		ApInvoiceItemDto dto = new ApInvoiceItemDto();
		List<ApInvoiceDto> invDtos = new ArrayList<ApInvoiceDto>();
		ApInvoiceDto invDto = null;
		APInvoice apInvoice = getApInvoiceGs(pId);
		dto.setApInvoice(apInvoice);
		List<SerialItem> serialItems = apInvoice.getSerialItems();
		AccountCombination ac = null;
		Account acct = null;
		VatAcctSetup vatAcctSetup = null;
		int companyId = apInvoice.getCompanyId();
		int divisionId = apInvoice.getDivisionId();
		ApInvoiceAccount invAcct = apInvoiceAcctDao.getInvoiceAcctByCompanyDiv(companyId, divisionId);
		for (SerialItem si : serialItems) {
			double vat = si.getVatAmount() != null ? si.getVatAmount() : 0;
			double discount = si.getDiscount() != null ? si.getDiscount() : 0;
			if (invAcct != null) {
				ac = acctCombinationService.getAccountCombination(invAcct.getAccountCombinationId());
				acct = ac.getAccount();
				invDto = new ApInvoiceDto();
				invDto.setAccountId(acct.getId());
				invDto.setAccountNo(acct.getNumber());
				invDto.setAccountName(acct.getAccountName());
				invDto.setDebit(si.getAmount());
				invDtos.add(invDto);
				ac = null;
				acct = null;
			}
			if (si.getTaxTypeId() != null && vat > 0.0) {
				// Add tax type account particular
				vatAcctSetup = vatAcctSetupDao.getVatAccountSetup(companyId, divisionId, si.getTaxTypeId());
				if (vatAcctSetup != null) {
					ac = acctCombinationService.getAccountCombination(vatAcctSetup.getInputVatAcId());
					acct = ac.getAccount();
					invDto = new ApInvoiceDto();
					invDto.setAccountId(acct.getId());
					invDto.setAccountNo(acct.getNumber());
					invDto.setAccountName(acct.getAccountName());
					invDto.setDebit(vat);
					invDtos.add(invDto);
					ac = null;
					acct = null;
					vatAcctSetup = null;
				}
			}
			if (si.getItemDiscountTypeId() != null && discount > 0.0) {
				if (invAcct != null) {
					ac = acctCombinationService.getAccountCombination(invAcct.getDiscountAcId());
					acct = ac.getAccount();
					invDto = new ApInvoiceDto();
					invDto.setAccountId(acct.getId());
					invDto.setAccountNo(acct.getNumber());
					invDto.setAccountName(acct.getAccountName());
					invDto.setCredit(discount);
					invDtos.add(invDto);
					ac = null;
					acct = null;
				}
			}
		}
		List<ApInvoiceGoods> apInvoiceGoods = apInvoice.getApInvoiceGoods();
		for (ApInvoiceGoods apig : apInvoiceGoods) {
			double vat = apig.getVatAmount() != null ? apig.getVatAmount() : 0;
			double discount = apig.getDiscount() != null ? apig.getDiscount() : 0;
			if (invAcct != null) {
				ac = acctCombinationService.getAccountCombination(invAcct.getAccountCombinationId());
				acct = ac.getAccount();
				invDto = new ApInvoiceDto();
				invDto.setAccountId(acct.getId());
				invDto.setAccountNo(acct.getNumber());
				invDto.setAccountName(acct.getAccountName());
				invDto.setDebit(apig.getAmount());
				invDtos.add(invDto);
				ac = null;
				acct = null;
			}
			if (apig.getTaxTypeId() != null && vat > 0.0) {
				// Add tax type account particular
				vatAcctSetup = vatAcctSetupDao.getVatAccountSetup(companyId, divisionId, apig.getTaxTypeId());
				if (vatAcctSetup != null) {
					ac = acctCombinationService.getAccountCombination(vatAcctSetup.getInputVatAcId());
					acct = ac.getAccount();
					invDto = new ApInvoiceDto();
					invDto.setAccountId(acct.getId());
					invDto.setAccountNo(acct.getNumber());
					invDto.setAccountName(acct.getAccountName());
					invDto.setDebit(vat);
					invDtos.add(invDto);
					ac = null;
					acct = null;
					vatAcctSetup = null;
				}
			}
			if (apig.getItemDiscountTypeId() != null && discount > 0.0) {
				if (invAcct != null) {
					ac = acctCombinationService.getAccountCombination(invAcct.getDiscountAcId());
					acct = ac.getAccount();
					invDto = new ApInvoiceDto();
					invDto.setAccountId(acct.getId());
					invDto.setAccountNo(acct.getNumber());
					invDto.setAccountName(acct.getAccountName());
					invDto.setCredit(discount);
					invDtos.add(invDto);
					ac = null;
					acct = null;
				}
			}
		}
		List<ApInvoiceLine> apInvoiceLines = apInvoice.getApInvoiceLines();
		for (ApInvoiceLine apl : apInvoiceLines) {
			double vat = apl.getVatAmount() != null ? apl.getVatAmount() : 0;
			double discount = apl.getDiscount() != null ? apl.getDiscount() : 0;
			if (invAcct != null) {
				ac = acctCombinationService.getAccountCombination(invAcct.getAccountCombinationId());
				acct = ac.getAccount();
				invDto = new ApInvoiceDto();
				invDto.setAccountId(acct.getId());
				invDto.setAccountNo(acct.getNumber());
				invDto.setAccountName(acct.getAccountName());
				invDto.setDebit(apl.getAmount() + discount);
				invDtos.add(invDto);
				ac = null;
				acct = null;
			}
			if (apl.getTaxTypeId() != null && vat > 0.0) {
				// Add tax type account particular
				vatAcctSetup = vatAcctSetupDao.getVatAccountSetup(companyId, divisionId, apl.getTaxTypeId());
				if (vatAcctSetup != null) {
					ac = acctCombinationService.getAccountCombination(vatAcctSetup.getInputVatAcId());
					acct = ac.getAccount();
					invDto = new ApInvoiceDto();
					invDto.setAccountId(acct.getId());
					invDto.setAccountNo(acct.getNumber());
					invDto.setAccountName(acct.getAccountName());
					invDto.setDebit(vat);
					invDtos.add(invDto);
					ac = null;
					acct = null;
					vatAcctSetup = null;
				}
			}
			if (apl.getDiscountTypeId() != null && discount > 0.0) {
				if (invAcct != null) {
					ac = acctCombinationService.getAccountCombination(invAcct.getDiscountAcId());
					acct = ac.getAccount();
					invDto = new ApInvoiceDto();
					invDto.setAccountId(acct.getId());
					invDto.setAccountNo(acct.getNumber());
					invDto.setAccountName(acct.getAccountName());
					invDto.setCredit(discount);
					invDtos.add(invDto);
					ac = null;
					acct = null;
				}
			}
		}

		invDto = new ApInvoiceDto();
		ac = apInvoice.getSupplierAccount().getDefaultCreditAC();
		acct = ac.getAccount();
		invDto.setAccountName(acct.getAccountName());
		invDto.setAccountNo(acct.getNumber());
		invDto.setCredit(apInvoice.getAmount());
		invDtos.add(invDto);
		ac = null;
		acct = null;

		Integer wtaxAcctSettingId = apInvoice.getWtAcctSettingId();
		if (wtaxAcctSettingId != null) {
			WithholdingTaxAcctSetting wTaxAcctSetting = wtAcctSettingDao.get(wtaxAcctSettingId);
			ac = acctCombinationService.getAccountCombination(wTaxAcctSetting.getAcctCombinationId());
			acct = ac.getAccount();
			invDto = new ApInvoiceDto();
			invDto.setAccountName(acct.getAccountName());
			invDto.setAccountNo(acct.getNumber());
			invDto.setCredit(apInvoice.getWtAmount());
			invDtos.add(invDto);
			ac = null;
			acct = null;
			wTaxAcctSetting = null;
		}
		dto.setApInvoiceDtos(invDtos);
		return dto;
	}

	/**
	 * Get the list of AP invoices for general search
	 * @param typeId The invoice type id
	 * @param divisionId The division id
	 * @param searchCriteria The search criteria
	 * @return The list of AP invoices for general search
	 */
	public List<FormSearchResult> searchInvoices(int typeId, int divisionId, String searchCriteria) {
		Page<APInvoice> supplierAdvPayments = apInvoiceDao.searchInvoiceGoodsAndServices(typeId, divisionId,
				searchCriteria, new PageSetting(PageSetting.START_PAGE));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		for(APInvoice sap : supplierAdvPayments.getData()) {
			title = String.valueOf(sap.getCompany().getCompanyCode() + " " + sap.getSequenceNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Sequence No.", sap.getSequenceNumber().toString()));
			properties.add(ResultProperty.getInstance("Company", sap.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Division", sap.getDivision().getName()));
			properties.add(ResultProperty.getInstance("Supplier", sap.getSupplier().getName()));
			properties.add(ResultProperty.getInstance("Supplier Account", sap.getSupplierAccount().getName()));
			properties.add(ResultProperty.getInstance("SI/SOA No.", sap.getInvoiceNumber()));
			properties.add(ResultProperty.getInstance("SI/SOA Date", DateUtil.formatDate(sap.getInvoiceDate())));
			properties.add(ResultProperty.getInstance("GL Date", DateUtil.formatDate(sap.getGlDate())));
			properties.add(ResultProperty.getInstance("Due Date", DateUtil.formatDate(sap.getDueDate())));
			status = sap.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(sap.getId(), title, properties));
		}
		title = null;
		status = null;
		return result;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		APInvoice apInvoice = apInvoiceDao.getAPInvoiceByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (apInvoice != null) {
			String remarks = currentWorkflowLog.getComment();
			if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID &&
					remarks != null && !remarks.trim().isEmpty()) {
				List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(apInvoice.getEbObjectId(),
						APInvoice.GS_SERIAL_ITEM_OR_TYPE_ID);
				serialItemService.setSerialNumberToInactive(savedSerialItems);
				StringBuffer errorMessage = null;
				Integer objectId = apInvoice.getEbObjectId();
				//Check if used in RTS or AP Payment forms.
				List<APInvoice> rtsInvoices = apInvoiceDao.getChildInvoicesByParentInvoiceObjectId(objectId);
				List<ApPayment> apPayments = apPaymentDao.getApPaymentsByObjectId(objectId);
				if((rtsInvoices != null && !rtsInvoices.isEmpty()) ||
						(apPayments != null && !apPayments.isEmpty())) {
					errorMessage = new StringBuffer("Transaction cannot be cancelled because it has associated transaction/s: ");
					//RTS
					for (APInvoice rts : rtsInvoices) {
						errorMessage.append("<br> RTS No. : " + rts.getSequenceNumber());
					}
					//AP Payment
					for (ApPayment payment : apPayments) {
						errorMessage.append("<br> APP No. : " + payment.getVoucherNumber());
					}
					if(errorMessage != null) {
						bindingResult.reject("workflowMessage", errorMessage.toString());
						currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
					}
				}
			}
		}
	}
}
