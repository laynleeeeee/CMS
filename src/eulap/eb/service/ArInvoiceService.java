package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.ArInvoiceDao;
import eulap.eb.dao.ArInvoiceItemDao;
import eulap.eb.dao.ArInvoiceLineDao;
import eulap.eb.dao.ArInvoiceTypeDao;
import eulap.eb.dao.ArReceiptDao;
import eulap.eb.dao.AuthorityToWithdrawDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.dao.DeliveryReceiptDao;
import eulap.eb.dao.ItemDiscountTypeDao;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.dao.SalesPersonnelDao;
import eulap.eb.dao.SerialItemDao;
import eulap.eb.dao.ServiceSettingDao;
import eulap.eb.dao.TermDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.ArInvoiceItem;
import eulap.eb.domain.hibernate.ArInvoiceLine;
import eulap.eb.domain.hibernate.ArInvoiceType;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.AuthorityToWithdraw;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.DeliveryReceiptItem;
import eulap.eb.domain.hibernate.DeliveryReceiptLine;
import eulap.eb.domain.hibernate.DeliveryReceiptType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.SOType;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.domain.hibernate.TaxType;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Service class for {@link ArInvoice}

 */

@Service
public class ArInvoiceService extends BaseWorkflowService {
	@Autowired
	private ArInvoiceDao arInvoiceDao;
	@Autowired
	private ArInvoiceItemDao ariItemDao;
	@Autowired
	private ArInvoiceLineDao ariLineDao;
	@Autowired
	private SerialItemDao serialItemDao;
	@Autowired
	private DeliveryReceiptDao deliveryReceiptDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private ArCustomerDao arCustomerDao;
	@Autowired
	private ArCustomerAcctDao arCustomerAcctDao;
	@Autowired
	private TermDao termDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private DeliveryReceiptService drService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private AuthorityToWithdrawDao atwDao;
	@Autowired
	private SalesOrderDao salesOrderDao;
	@Autowired
	private UnitMeasurementDao uomDao;
	@Autowired
	private ItemDiscountTypeDao itemDiscountTypeDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CustomerAdvancePaymentDao capDao;
	@Autowired
	private ArInvoiceTypeDao arInvoiceTypeDao;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private SalesPersonnelDao salesPersonnelDao;
	@Autowired
	private ServiceSettingDao serviceSettingDao;
	@Autowired
	private ArReceiptDao arReceiptDao;
	@Autowired
	private ArInvoiceItemDao arInvoiceItemDao;
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private OOLinkHelper ooLinkHelper;

	/**
	 * Get the {@link DeliveryReceipt} by form object id
	 * @param deliveryReceiptId The form object id
	 * @return The delivery receipt object.
	 */
	public ArInvoice getArInvoice(int arInvoiceId, boolean isPopulateHeader, boolean isIncludeItems) {
		ArInvoice arInvoice = arInvoiceDao.get(arInvoiceId);
		double rate = arInvoice.getCurrencyRateValue();
		int refObjectId = arInvoice.getEbObjectId();
		if (isPopulateHeader) {
			arInvoice.setCompany(companyDao.get(arInvoice.getCompanyId()));
			arInvoice.setArCustomer(arCustomerDao.get(arInvoice.getArCustomerId()));
			arInvoice.setArCustomerAccount(arCustomerAcctDao.get(arInvoice.getArCustomerAccountId()));
			arInvoice.setTerm(termDao.get(arInvoice.getTermId()));
			List<Integer> drRefIds = convertIdsToInt(arInvoice.getStrDrRefIds());
			StringBuilder drNumbers = new StringBuilder();
			DeliveryReceipt dr = null;
			int index = 0;
			for (Integer drId : drRefIds) {
				dr = drService.getDeliveryReceipt(drId, 0);
				drNumbers.append(dr.getSequenceNo().toString());
				dr = null;
				if (index < drRefIds.size() - 1) {
					drNumbers.append(", ");
				}
				index++;
			}
			arInvoice.setDrNumber(drNumbers.toString());
		}
		Date currentDate = new Date();
		EBObject ebObject = null;
		if (isIncludeItems) {
			boolean isCancelled = arInvoice.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID;
			List<SerialItem> serialArItems = serialItemService.getSerializeItemsByRefObjectId(refObjectId,
					null, ArInvoice.ARI_SERIAL_ITEM_OR_TYPE_ID, isCancelled);
			for (SerialItem si : serialArItems) {
				si.setUnitCost(CurrencyUtil.convertMonetaryValues(si.getUnitCost(), rate));
				si.setSrp(CurrencyUtil.convertMonetaryValues(si.getSrp(), rate));
				si.setAmount(CurrencyUtil.convertMonetaryValues(si.getAmount(), rate));
				double vatAmount = si.getVatAmount() != null ? si.getVatAmount() : 0;
				if (vatAmount > 0) {
					si.setVatAmount(CurrencyUtil.convertMonetaryValues(vatAmount, rate));
				}
				double discount = si.getDiscount() != null ? si.getDiscount() : 0;
				if (discount > 0) {
					si.setDiscount(CurrencyUtil.convertMonetaryValues(discount, rate));
				}
			}
			arInvoice.setSerialArItems(serialArItems);
			List<ArInvoiceItem> nonSerialArItems = ariItemDao.getAllByRefId(ArInvoiceItem.FIELD.arInvoiceId.name(), arInvoiceId);
			for (ArInvoiceItem ari : nonSerialArItems) {
				ari.setOrigWarehouseId(ari.getWarehouseId());
				ari.setStockCode(itemService.getItem(ari.getItemId()).getStockCode());
				ari.setExistingStocks(itemService.getItemExistingStocks(ari.getItemId(),
						ari.getWarehouseId(), currentDate));
				ebObject = ooLinkHelper.getReferenceObject(ari.getEbObjectId(), ArInvoice.DRI_ARI_ITEM_OR_TYPE_ID);
				if (ebObject != null) {
					ari.setRefenceObjectId(ebObject.getId());
					ebObject = null;
				}
				ari.setUnitCost(CurrencyUtil.convertMonetaryValues(ari.getUnitCost(), rate));
				ari.setSrp(CurrencyUtil.convertMonetaryValues(ari.getSrp(), rate));
				ari.setAmount(CurrencyUtil.convertMonetaryValues(ari.getAmount(), rate));
				double vatAmount = ari.getVatAmount() != null ? ari.getVatAmount() : 0;
				if (vatAmount > 0) {
					ari.setVatAmount(CurrencyUtil.convertMonetaryValues(vatAmount, rate));
				}
				double discount = ari.getDiscount() != null ? ari.getDiscount() : 0;
				if (discount > 0) {
					ari.setDiscount(CurrencyUtil.convertMonetaryValues(discount, rate));
				}
			}
			arInvoice.setNonSerialArItems(nonSerialArItems);
			List<ArInvoiceLine> ariLines = ariLineDao.getAllByRefId(ArInvoiceLine.FIELD.arInvoiceId.name(), arInvoiceId);
			processAriLines(ariLines, rate);
			arInvoice.setAriLines(ariLines);
		}
		arInvoice.setAmount(CurrencyUtil.convertMonetaryValues(arInvoice.getAmount(), rate));
		arInvoice.setWtAmount(CurrencyUtil.convertMonetaryValues(arInvoice.getWtAmount(), rate));
		arInvoice.setWtVatAmount(CurrencyUtil.convertMonetaryValues(arInvoice.getWtVatAmount(), rate));
		arInvoice.setRecoupment(CurrencyUtil.convertMonetaryValues(arInvoice.getRecoupment(), rate));
		arInvoice.setRetention(CurrencyUtil.convertMonetaryValues(arInvoice.getRetention(), rate));
		return arInvoice;
	}

	private List<Integer> convertIdsToInt(String strDrRefIds) {
		List<Integer> drRefIds = new ArrayList<>();
		if (strDrRefIds != null && !strDrRefIds.isEmpty()) {
			String[] objIds = strDrRefIds.split(", ");
			for (String s : objIds) {
				drRefIds.add(Integer.parseInt(s));
			}
		}
		return drRefIds;
	}

	/**
	 * Set ar invoice reference document.
	 * @param deliveryReceipt
	 * @throws IOException
	 */
	public void setArInvoiceRefDoc(ArInvoice arInvoice) throws IOException {
		arInvoice.setReferenceDocuments(refDocumentService.processReferenceDocs(arInvoice.getEbObjectId()));
	}

	private void processAriLines(List<ArInvoiceLine> ariLines, double currencyRateValue) {
		UnitMeasurement uom = null;
		EBObject ebObject = null;
		ServiceSetting serviceSetting = null;
		for (ArInvoiceLine aril : ariLines) {
			ebObject = ooLinkHelper.getReferenceObject(aril.getEbObjectId(), ArInvoice.DRI_ARI_ITEM_OR_TYPE_ID);
			if (ebObject != null) {
				aril.setRefenceObjectId(ebObject.getId());
				ebObject = null;
			}
			serviceSetting = serviceSettingDao.get(aril.getServiceSettingId());
			if (serviceSetting != null) {
				aril.setServiceSettingName(serviceSetting.getName());
				aril.setServiceSetting(serviceSetting);
			}
			if (aril.getUnitOfMeasurementId() != null) {
				uom = uomDao.get(aril.getUnitOfMeasurementId());
				aril.setUnitMeasurementName(uom.getName());
			}
			Integer itemDiscountTypeId = aril.getDiscountTypeId();
			if (itemDiscountTypeId != null) {
				aril.setItemDiscountType(itemDiscountTypeDao.get(itemDiscountTypeId));
				aril.setDiscount(CurrencyUtil.convertAmountToPhpRate(aril.getDiscount(), currencyRateValue));
				itemDiscountTypeId = null;
			}
			aril.setAmount(CurrencyUtil.convertMonetaryValues(aril.getAmount(), currencyRateValue));
			aril.setUpAmount(CurrencyUtil.convertMonetaryValues(aril.getUpAmount(), currencyRateValue));
			if (aril.getVatAmount() != null) {
				aril.setVatAmount(CurrencyUtil.convertMonetaryValues(aril.getVatAmount(), currencyRateValue));
			}
		}
	}

	/**
	 * Get the paged list of delivery receipt form references
	 * @param companyId The company id
	 * @param arCustomerId The customer id
	 * @param arCustomerAccountId The customer account id
	 * @param drNumber The DR number
	 * @param statusId The form status id
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param pageNumber The page number
	 * @return The paged list of delivery receipt form references
	 */
	public Page<DeliveryReceipt> getDeliveryReceipts(Integer companyId, Integer divisionId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer drNumber, Integer statusId, Date dateFrom, Date dateTo, String drRefNumber,
			 Integer typeId, Integer pageNumber) {
		Page<DeliveryReceipt> deliveryReceipts = deliveryReceiptDao.getDRReferences(companyId, divisionId, arCustomerId,
				arCustomerAccountId, drNumber, statusId, dateFrom, dateTo, drRefNumber, typeId, new PageSetting(pageNumber));
		for (DeliveryReceipt dr : deliveryReceipts.getData()) {
			dr.setCurrency(currencyService.getCurency(dr.getCurrencyId()));
		}
		return deliveryReceipts;
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		ArInvoice arInvoice = (ArInvoice) form;
		boolean isNew = arInvoice.getId() == 0;
		int typeId = arInvoice.getArInvoiceTypeId();
		double currencyRate = arInvoice.getCurrencyRateValue();
		if (isNew) {
			arInvoice.setSequenceNo(arInvoiceDao.generateSeqNo(arInvoice.getCompanyId(), typeId));
		} else {
			List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(
					arInvoice.getEbObjectId(), ArInvoice.ARI_SERIAL_ITEM_OR_TYPE_ID);
			serialItemService.setSerialNumberToInactive(savedSerialItems);

			//Delete ar invoice items
			List<ArInvoiceItem> savedAriItems = arInvoiceItemDao.getAllByRefId(
					ArInvoiceItem.FIELD.arInvoiceId.name(), arInvoice.getId());
			for (ArInvoiceItem ari : savedAriItems) {
				arInvoiceItemDao.delete(ari);
			}

			//Delete ar invoice lines.
			List<ArInvoiceLine> savedAriLines = ariLineDao.getAllByRefId(
					ArInvoiceLine.FIELD.arInvoiceId.name(), arInvoice.getId());
			for (ArInvoiceLine aril : savedAriLines) {
				ariLineDao.delete(aril);
			}

			// Free up memory
			savedSerialItems = null;
			savedAriItems = null;
			savedAriLines = null;
		}
		arInvoice.setWtAmount(CurrencyUtil.convertAmountToPhpRate(arInvoice.getWtAmount(), currencyRate, true));
		arInvoice.setRecoupment(CurrencyUtil.convertAmountToPhpRate(arInvoice.getRecoupment(), currencyRate, true));
		arInvoice.setRetention(CurrencyUtil.convertAmountToPhpRate(arInvoice.getRetention(), currencyRate, true));
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		ArInvoice arInvoice = (ArInvoice) form;
		boolean isNew = arInvoice.getId() == 0;
		AuditUtil.addAudit(arInvoice, new Audit(user.getId(), isNew, new Date()));
		double currencyRate = arInvoice.getCurrencyRateValue();
		Integer parentObjectId = arInvoice.getEbObjectId();
		arInvoiceDao.saveOrUpdate(arInvoice);

		double totalAmount = 0;
		double totalWtTaxAmt = 0;
		List<ArInvoiceItem> arInvoiceItems = processArInvoiceItems(arInvoice.getNonSerialArItems(), currencyRate);
		for (ArInvoiceItem ari : arInvoiceItems) {
			ari.setArInvoiceId(arInvoice.getId());
			ariItemDao.save(ari);
			totalAmount += ari.getAmount();
			totalAmount += ari.getVatAmount() != null ? ari.getVatAmount() : 0;
			Integer ariTaxTypeId = ari.getTaxTypeId();
			if (ariTaxTypeId != null && ariTaxTypeId.equals(TaxType.GOVERNMENT)) {
				totalWtTaxAmt += NumberFormatUtil.roundOffTo2DecPlaces(ari.getAmount() * TaxUtil.GOVT_TAX_PERCENTAGE);
			}
		}

		List<SerialItem> serialItems = processSerializedItems(arInvoice.getSerialArItems(), currencyRate);
		for (SerialItem si : serialItems) {
			totalAmount += si.getAmount();
			totalAmount += si.getVatAmount() != null ? si.getVatAmount() : 0;
			Integer siTaxTypeId = si.getTaxTypeId();
			if (siTaxTypeId != null && si.getTaxTypeId().equals(TaxType.GOVERNMENT)) {
				totalWtTaxAmt += NumberFormatUtil.roundOffTo2DecPlaces(si.getAmount() * TaxUtil.GOVT_TAX_PERCENTAGE);
			}
		}
		serialItemService.saveSerializedItems(serialItems, arInvoice.getEbObjectId(),
				null, user, ArInvoice.ARI_SERIAL_ITEM_OR_TYPE_ID, false);

		List<ArInvoiceLine> arInvoiceLines = processArLines(arInvoice.getAriLines(), currencyRate);
		for (ArInvoiceLine aril : arInvoiceLines) {
			aril.setArInvoiceId(arInvoice.getId());
			String description = aril.getDescription();
			if (description != null && !description.isEmpty()) {
				aril.setDescription(StringFormatUtil.removeExtraWhiteSpaces(description));
			}
			ariLineDao.save(aril);
			totalAmount += aril.getAmount();
			totalAmount += aril.getVatAmount() != null ? aril.getVatAmount() : 0;
			Integer arilTaxTypeId = aril.getTaxTypeId();
			if (arilTaxTypeId != null && arilTaxTypeId.equals(TaxType.GOVERNMENT)) {
				totalWtTaxAmt += NumberFormatUtil.roundOffTo2DecPlaces(aril.getAmount() * TaxUtil.GOVT_TAX_PERCENTAGE);
			}
		}

		arInvoice.setWtVatAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalWtTaxAmt));
		double amount = totalAmount - arInvoice.getWtAmount() - arInvoice.getWtVatAmount() - arInvoice.getRecoupment() - arInvoice.getRetention();
		arInvoice.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(amount));
		arInvoiceDao.update(arInvoice);

		// Save reference documents
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				arInvoice.getReferenceDocuments(), true);
	}

	private List<SerialItem> processSerializedItems(List<SerialItem> serializedItems, double currencyRate) {
		for (SerialItem ari : serializedItems) {
			double quantity = ari.getQuantity();
			ari.setUnitCost(CurrencyUtil.convertAmountToPhpRate(ari.getUnitCost(), currencyRate));
			double srp = ari.getSrp();
			ari.setSrp(CurrencyUtil.convertAmountToPhpRate(srp, currencyRate));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(ari.getDiscount() != null ? ari.getDiscount() : 0);
			ari.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double vatAmount = 0;
			if (TaxUtil.isVatable(ari.getTaxTypeId())) {
				vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(ari.getVatAmount() != null ? ari.getVatAmount() : 0);
				ari.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			}
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, srp) - vatAmount - discount);
			ari.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		}
		return serializedItems;
	}

	private List<ArInvoiceItem> processArInvoiceItems(List<ArInvoiceItem> arInvoiceItems, double currencyRate) {
		for (ArInvoiceItem ari : arInvoiceItems) {
			double quantity = ari.getQuantity();
			ari.setUnitCost(CurrencyUtil.convertAmountToPhpRate(ari.getUnitCost(), currencyRate));
			double srp = ari.getSrp();
			ari.setSrp(CurrencyUtil.convertAmountToPhpRate(srp, currencyRate));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(ari.getDiscount() != null ? ari.getDiscount() : 0);
			ari.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double vatAmount = 0;
			if (TaxUtil.isVatable(ari.getTaxTypeId())) {
				vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(ari.getVatAmount() != null ? ari.getVatAmount() : 0);
				ari.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			}
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, srp) - vatAmount - discount);
			ari.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		}
		return arInvoiceItems;
	}

	private List<ArInvoiceLine> processArLines(List<ArInvoiceLine> arInvoiceLines, double currencyRate) {
		for (ArInvoiceLine aril : arInvoiceLines) {
			double quantity = aril.getQuantity() != null ? aril.getQuantity() : 1.0;
			double srp = aril.getUpAmount() != null ? aril.getUpAmount() : 0;
			aril.setUpAmount(CurrencyUtil.convertAmountToPhpRate(srp, currencyRate));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(aril.getDiscount() != null ? aril.getDiscount() : 0);
			aril.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double vatAmount = 0;
			if (TaxUtil.isVatable(aril.getTaxTypeId())) {
				vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(aril.getVatAmount() != null ? aril.getVatAmount() : 0);
				aril.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			}
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, srp) - vatAmount - discount);
			aril.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		}
		return arInvoiceLines;
	}

	/**
	 * Validate the delivery receipt object before saving.
	 * @param dr The delivery receipt object to be validated.
	 * @param errors The binding result.
	 */
	public void validate(ArInvoice arInvoice, Errors errors) {
		if (arInvoice.getCompanyId() == null) {
			//Company is Required
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("ArInvoiceService.1"));
		} else if (!companyDao.get(arInvoice.getCompanyId()).isActive()) {
			//Company is inactive.
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("ArInvoiceService.2"));
		}
		if (arInvoice.getArCustomerId() == null) {
			//Customer is required.
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("ArInvoiceService.3"));
		} else if (!arCustomerDao.get(arInvoice.getArCustomerId()).isActive()) {
			//Customer is inactive.
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("ArInvoiceService.4"));
		}
		if (arInvoice.getArCustomerAccountId() == null) {
			//Customer account is required.
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("ArInvoiceService.5"));
		} else if (!arCustomerAcctDao.get(arInvoice.getArCustomerAccountId()).isActive()) {
			//Customer account is inactive.
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("ArInvoiceService.6"));
		}
		if (arInvoice.getTermId() == null) {
			//Term is requried.
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("ArInvoiceService.7"));
		} else if (!termDao.get(arInvoice.getTermId()).isActive()) {
			//Term is inactive.
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("ArInvoiceService.8"));
		}

		if (arInvoice.getDate() == null) {
			//Date is required.
			errors.rejectValue("date", null, null, ValidatorMessages.getString("ArInvoiceService.9"));
		} else if (!timePeriodService.isOpenDate(arInvoice.getDate())) {
			//Date should be in an open time period.
			errors.rejectValue("date", null, null, ValidatorMessages.getString("ArInvoiceService.10"));
		}

		if (arInvoice.getDueDate() == null) {
			//Due date is required.
			errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("ArInvoiceService.11"));
		}

		Double recoupment = arInvoice.getRecoupment();
		if (recoupment != null && recoupment > getCapRemainingBalance(arInvoice.getStrDrRefIds(), arInvoice.getId())) {
			errors.rejectValue("recoupment", null, null, ValidatorMessages.getString("ArInvoiceService.18"));
		}

		// Amount
		Integer customerId = arInvoice.getArCustomerId();
		Double ariAmount = arInvoice.getAmount();
		Double availableBalance = arTransactionService.computeAvailableBalance(customerId);
		Double rate = arInvoice.getCurrencyRateValue();
		ArCustomer arCustomer = arCustomerDao.get(customerId);
		Double creditLimit = arCustomer.getMaxAmount() != null ? arCustomer.getMaxAmount() : 0;
		if (CurrencyUtil.convertAmountToPhpRate(ariAmount, rate) > availableBalance && creditLimit != 0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArInvoiceService.20") + NumberFormatUtil.format(availableBalance));
		}

		boolean isEmptySerialItem = arInvoice.getSerialArItems() == null || arInvoice.getSerialArItems().isEmpty();
		boolean isEmptyNonSerialItem = arInvoice.getNonSerialArItems() == null || arInvoice.getNonSerialArItems().isEmpty();
		boolean isEmptyInvoiceLine = arInvoice.getAriLines() == null || arInvoice.getAriLines().isEmpty();
		boolean isEmptyTruckingLine = arInvoice.getArInvoiceTruckingLines() == null || arInvoice.getArInvoiceTruckingLines().isEmpty();
		boolean isEmptyEquipmentLine = arInvoice.getArInvoiceEquipmentLines() == null || arInvoice.getArInvoiceEquipmentLines().isEmpty();
		if (ArInvoiceType.ARI_SERVICE_TYPE_ID == arInvoice.getArInvoiceTypeId()
				&& isEmptyInvoiceLine && isEmptyTruckingLine && isEmptyEquipmentLine) {
			errors.rejectValue("commonErrorMsg", null, null, ValidatorMessages.getString("ArInvoiceService.12"));
		} else if (ArInvoiceType.ARI_ITEM_TYPE_ID == arInvoice.getArInvoiceTypeId() 
				&& isEmptySerialItem && isEmptyNonSerialItem) {
			errors.rejectValue("commonErrorMsg", null, null, ValidatorMessages.getString("ArInvoiceService.13"));
		} else if ((ArInvoiceType.ARI_CENTRAL_TYPE_ID >=  arInvoice.getArInvoiceTypeId() && ArInvoiceType.ARI_NSB8A_TYPE_ID <= arInvoice.getArInvoiceTypeId())
				&& isEmptySerialItem && isEmptyNonSerialItem && isEmptyInvoiceLine) {
			errors.rejectValue("commonErrorMsg", null, null, ValidatorMessages.getString("ArInvoiceService.14"));
		}
		refDocumentService.validateReferences(arInvoice.getReferenceDocuments(), errors);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return arInvoiceDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return arInvoiceDao.getByWorkflowId(workflowId);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		ArInvoice arInvoice = arInvoiceDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		if (arInvoice != null) {
			Integer selectedStatusId = currentWorkflowLog.getFormStatusId();
			if (selectedStatusId == FormStatus.CANCELLED_ID
					&& currentWorkflowLog.getComment() != null && !currentWorkflowLog.getComment().isEmpty()) {
				StringBuffer errorMessage = null;
				List<ArReceipt> arReceipts = arReceiptDao.geArReceipsByArInvoiceId(arInvoice.getId());
				if (arReceipts != null && !arReceipts.isEmpty()) {
					errorMessage = new StringBuffer("Transaction cannot be cancelled because it has associated payment/s: ");
					for (ArReceipt arr : arReceipts) {
						errorMessage.append("<br> AC No. : " + arr.getSequenceNo()); 
					}
					bindingResult.reject("workflowMessage", errorMessage.toString());
					currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
				}
				if (errorMessage == null) {
					List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(
							arInvoice.getEbObjectId(), ArInvoice.ARI_SERIAL_ITEM_OR_TYPE_ID);
					serialItemService.setSerialNumberToInactive(savedSerialItems);
				}
			}
		}
	}

	/**
	 * Search the {@link ArInvoice}
	 * @param searchCriteria The search Criteria
	 * @return The List of searched AR Invoices.
	 */
	public List<FormSearchResult> searchARInvoice(String criteria, int typeId) {
		Page<ArInvoice> arInvoices = arInvoiceDao.searchARInvoice(criteria, typeId, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		Company company = null;
		ArCustomer customer = null;
		ArCustomerAccount custAcct = null;
		Term term = null;
		for (ArInvoice arInvoice : arInvoices.getData()) {
			company = companyDao.get(arInvoice.getCompanyId());
			customer = arCustomerDao.get(arInvoice.getArCustomerId());
			custAcct = arCustomerAcctDao.get(arInvoice.getArCustomerAccountId());
			term = termDao.get(arInvoice.getTermId());
			title = String.valueOf(company.getCompanyCode() + " " + arInvoice.getSequenceNo());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", company.getName()));
			properties.add(ResultProperty.getInstance("Customer", customer.getName()));
			properties.add(ResultProperty.getInstance("Customer Account", custAcct.getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(arInvoice.getDate())));
			properties.add(ResultProperty.getInstance("Term", term.getName()));
			status = arInvoice.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(arInvoice.getId(), title, properties));
		}
		title = null;
		status = null;
		company = null;
		customer = null;
		custAcct = null;
		term = null;
		return result;
	}

	/**
	 * Get delivery receipt by EB object id
	 * @param ebObjectId The EB object id
	 * @return The delivery receipt object
	 */
	public ArInvoice getByDeliveryReceipt(Integer ebObjectId) {
		return arInvoiceDao.getByDeliveryReceipt(ebObjectId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		ArInvoice arInvoice = arInvoiceDao.getByEbObjectId(ebObjectId);
		Integer pId = arInvoice.getId();
		FormProperty property = workflowHandler.getProperty(arInvoice.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = arInvoice.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = getFormName(arInvoice.getArInvoiceTypeId()) + " - " + arInvoice.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + companyDao.get(arInvoice.getCompanyId()).getName())
				.append(" " + arCustomerDao.get(arInvoice.getArCustomerId()).getName())
				.append("<b> DATE : </b>" + DateUtil.formatDate(arInvoice.getDate()));
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	private String getFormName(Integer drTypeId) {
		return arInvoiceTypeDao.get(drTypeId).getName();
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case ArInvoice.OBJECT_TYPE_ID:
				return arInvoiceDao.getByEbObjectId(ebObjectId);
			case ArInvoiceItem.OBJECT_TYPE_ID:
				return ariItemDao.getByEbObjectId(ebObjectId);
			case ArInvoiceLine.OBJECT_TYPE_ID:
				return ariLineDao.getByEbObjectId(ebObjectId);
			case SerialItem.OBJECT_TYPE_ID:
				return serialItemDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Convert multiple DR reference to AR invoice
	 * @param drRefId The DR reference id
	 * @return The AR invoice object
	 */
	public ArInvoice convertDRtoARI(String referenceDrIds) {
		String strDrReferenceIds[] = referenceDrIds.split(", ");
		DeliveryReceipt dr = null;
		ArInvoice arInvoice = new ArInvoice();
		int count = 0;
		double currencyRate = 0;
		double totalWtax = 0;
		double totalWtaxVAT = 0;
		String concatenatedDrNumbers = "";
		String concatenatedRemarks = "";
		List<SerialItem> serializedItems = new ArrayList<SerialItem>();
		List<ArInvoiceItem> arInvoiceItems = new ArrayList<ArInvoiceItem>();
		List<ArInvoiceLine> arInvoiceLines = new ArrayList<ArInvoiceLine>();
		for (String strDrId : strDrReferenceIds) {
			int drId = Integer.parseInt(strDrId);
			dr = drService.getDeliveryReceipt(drId, true, true, null);
			currencyRate = dr.getCurrencyRateValue();
			String remarks = dr.getRemarks();
			String drNumber = dr.getSequenceNo().toString();
			if (count == 0) {
				arInvoice.setCompanyId(dr.getCompanyId());
				arInvoice.setCompany(dr.getCompany());
				arInvoice.setArCustomerId(dr.getArCustomerId());
				arInvoice.setArCustomer(dr.getArCustomer());
				arInvoice.setArCustomerAccountId(dr.getArCustomerAccountId());
				arInvoice.setArCustomerAccount(dr.getArCustomerAccount());
				arInvoice.setTermId(dr.getArCustomerAccount().getTermId());
				arInvoice.setTerm(dr.getArCustomerAccount().getTerm());
				arInvoice.setDivisionId(dr.getDivisionId());
				arInvoice.setDivision(dr.getDivision());
				arInvoice.setCurrencyId(dr.getCurrencyId());
				arInvoice.setCurrency(currencyService.getCurency(dr.getCurrencyId()));
				arInvoice.setCurrencyRateId(dr.getCurrencyRateId());
				arInvoice.setCurrencyRateValue(currencyRate);
				arInvoice.setWtAcctSettingId(dr.getWtAcctSettingId());
				concatenatedDrNumbers += drNumber;
				if (remarks != null && !remarks.isEmpty()) {
					concatenatedRemarks += remarks;
				}
			} else {
				concatenatedDrNumbers += ", " + drNumber;
				if (remarks != null && !remarks.isEmpty()) {
					concatenatedRemarks += "; " + remarks;
				}
			}
			count++;
			totalWtax += dr.getWtAmount() != null ? dr.getWtAmount() : 0;
			totalWtaxVAT += dr.getWtVatAmount() != null ? dr.getWtVatAmount() : 0;

			// Add all serialized items
			if (dr.getSerialDrItems() != null && !dr.getSerialDrItems().isEmpty()) {
				serializedItems.addAll(processDrSerialItems(dr.getSerialDrItems(), currencyRate));
			}

			// Add all non serialized items
			if (dr.getNonSerialDrItems() != null && !dr.getNonSerialDrItems().isEmpty()) {
				arInvoiceItems.addAll(processARInvoiceItems(dr.getNonSerialDrItems(), currencyRate));
			}

			// Add all services
			if (dr.getDrLines() != null && !dr.getDrLines().isEmpty()) {
				arInvoiceLines.addAll(processArInvoiceLines(dr.getDrLines(), currencyRate));
			}
		}
		arInvoice.setDrNumber(concatenatedDrNumbers);
		arInvoice.setRemarks(concatenatedRemarks);
		arInvoice.setWtAmount(totalWtax);
		arInvoice.setWtVatAmount(totalWtaxVAT);
		arInvoice.setSerialArItems(serializedItems);
		arInvoice.setNonSerialArItems(arInvoiceItems);
		arInvoice.setAriLines(arInvoiceLines);
		return arInvoice;
	}

	private List<SerialItem> processDrSerialItems(List<SerialItem> drSerialItems, double currencyRate) {
		Item item = null;
		SerialItem serialItem = null;
		List<SerialItem> serialItems = new ArrayList<SerialItem>();
		for (SerialItem si : drSerialItems) {
			serialItem = new SerialItem();
			item = itemService.getItem(si.getItemId());
			serialItem.setRefenceObjectId(si.getEbObjectId());
			serialItem.setItem(item);
			serialItem.setItemId(item.getId());
			serialItem.setStockCode(item.getStockCode());
			serialItem.setWarehouseId(si.getWarehouseId());
			serialItem.setSerialNumber(si.getSerialNumber());
			serialItem.setQuantity(si.getQuantity());
			serialItem.setItemSrpId(si.getItemSrpId());
			double srp = CurrencyUtil.convertMonetaryValues(si.getSrp(), currencyRate);
			serialItem.setOrigSrp(srp);
			serialItem.setSrp(srp);
			serialItem.setDiscount(CurrencyUtil.convertMonetaryValues(si.getDiscount(), currencyRate));
			serialItem.setTaxTypeId(si.getTaxTypeId());
			serialItem.setVatAmount(CurrencyUtil.convertMonetaryValues(si.getVatAmount(), currencyRate));
			serialItem.setItemDiscountTypeId(si.getItemDiscountTypeId());
			serialItem.setDiscountValue(si.getDiscountValue());
			serialItem.setActive(si.isActive());
			serialItem.setUnitCost(CurrencyUtil.convertMonetaryValues(si.getUnitCost(), currencyRate));
			serialItems.add(serialItem);
			item = null;
			serialItem = null;
		}
		return serialItems;
	}

	private List<ArInvoiceItem> processARInvoiceItems(List<DeliveryReceiptItem> drItems, double currencyRate) {
		ArInvoiceItem arInvoiceItem = null;
		Item item = null;
		List<ArInvoiceItem> arInvoiceItems = new ArrayList<ArInvoiceItem>();
		for (DeliveryReceiptItem dri : drItems) {
			arInvoiceItem = new ArInvoiceItem();
			item = itemService.getItem(dri.getItemId());
			arInvoiceItem.setRefenceObjectId(dri.getEbObjectId());
			arInvoiceItem.setItem(item);
			arInvoiceItem.setItemId(item.getId());
			arInvoiceItem.setStockCode(item.getStockCode());
			arInvoiceItem.setWarehouseId(dri.getWarehouseId());
			arInvoiceItem.setItemSrpId(dri.getItemSrpId());
			arInvoiceItem.setQuantity(dri.getQuantity());
			double srp = CurrencyUtil.convertMonetaryValues(dri.getSrp(), currencyRate);
			arInvoiceItem.setOrigSrp(srp);
			arInvoiceItem.setSrp(srp);
			arInvoiceItem.setDiscount(CurrencyUtil.convertMonetaryValues(dri.getDiscount(), currencyRate));
			arInvoiceItem.setTaxTypeId(dri.getTaxTypeId());
			arInvoiceItem.setVatAmount(CurrencyUtil.convertMonetaryValues(dri.getVatAmount(), currencyRate));
			arInvoiceItem.setItemDiscountTypeId(dri.getItemDiscountTypeId());
			arInvoiceItem.setDiscountValue(dri.getDiscountValue());
			arInvoiceItem.setUnitCost(CurrencyUtil.convertMonetaryValues(dri.getUnitCost(), currencyRate));
			arInvoiceItems.add(arInvoiceItem);
			item = null;
			arInvoiceItem = null;
		}
		return arInvoiceItems;
	}

	private List<ArInvoiceLine> processArInvoiceLines(List<DeliveryReceiptLine> drLines, double currencyRate) {
		List<ArInvoiceLine> arInvoiceLines = new ArrayList<ArInvoiceLine>();
		ArInvoiceLine ariLine = null;
		for (DeliveryReceiptLine drl : drLines) {
			double upAmount = CurrencyUtil.convertMonetaryValues(drl.getUpAmount(), currencyRate);
			ariLine = new ArInvoiceLine();
			ariLine.setRefenceObjectId(drl.getEbObjectId());
			ariLine.setServiceSettingId(drl.getServiceSettingId());
			ariLine.setServiceSettingName(drl.getServiceSettingName());
			ariLine.setDescription(drl.getDescription());
			ariLine.setQuantity(drl.getQuantity());
			ariLine.setPercentile(drl.getPercentile());
			ariLine.setUpAmount(upAmount);
			ariLine.setDiscountTypeId(drl.getDiscountTypeId());
			ariLine.setDiscountValue(drl.getDiscountValue());
			ariLine.setDiscount(CurrencyUtil.convertMonetaryValues(drl.getDiscount(), currencyRate));
			ariLine.setTaxTypeId(drl.getTaxTypeId());
			ariLine.setVatAmount(CurrencyUtil.convertMonetaryValues(drl.getVatAmount(), currencyRate));
			Integer uomId = drl.getUnitOfMeasurementId();
			if (uomId != null) {
				ariLine.setUnitOfMeasurementId(uomId);
				ariLine.setUnitMeasurementName(drl.getUnitMeasurement().getName());
			}
			ariLine.setAmount(NumberFormatUtil.multiplyWFP(upAmount, drl.getQuantity()));
			arInvoiceLines.add(ariLine);
			ariLine = null;
		}
		return arInvoiceLines;
	}

	/**
	 * Get the total customer advances
	 * @param salesOrderId The sales order id
	 * @return The total customer advances
	 */
	public Double getTotalCustomerAdvances(int salesOrderId) {
		return capDao.getTotalAdvPaymentsBySO(salesOrderId, 0);
	}

	/**
	 * Get the total customer advances paid for the selected DR
	 * @param deliveryReceiptId The delivery receipt id
	 * @return The total customer advances paid for the selected DR
	 */
	public double getTotalAdvancePayments(int deliveryReceiptId) {
		DeliveryReceipt dr = drService.getDeliveryReceipt(deliveryReceiptId, false, false, 0);
		Integer salesOrderId = null;
		if (dr.getDeliveryReceiptTypeId() == DeliveryReceiptType.DR_TYPE_ID) {
			AuthorityToWithdraw atw = atwDao.get(dr.getAuthorityToWithdrawId());
			salesOrderId = atw.getSalesOrderId();
		} else {
			salesOrderId = dr.getSalesOrderId();
		}
		return getTotalCustomerAdvances(salesOrderId);
	}

	/**
	 * Compute the total VAT.
	 * @param arInvoice The {@link ArInvoice}
	 * @return The total VAT.
	 */
	public Double computeTotalVat(ArInvoice arInvoice) {
		Double total = 0.0;
		if (arInvoice != null) {
			//Serial Item
			if (arInvoice.getSerialArItems() != null) {
				for (SerialItem si : arInvoice.getSerialArItems()) {
					if (si.getVatAmount() != null) {
						total += si.getVatAmount();
					}
				}
			}
			//Non-serial Item
			if (arInvoice.getNonSerialArItems() != null) {
				for (ArInvoiceItem item : arInvoice.getNonSerialArItems()) {
					if (item.getVatAmount() != null) {
						total += item.getVatAmount();
					}
				}
			}
			//Lines
			if (arInvoice.getAriLines() != null) {
				for (ArInvoiceLine ariLine : arInvoice.getAriLines()) {
					if (ariLine.getVatAmount() != null) {
						total += ariLine.getVatAmount();
					}
				}
			}
		}
		return total;
	}

	/**
	 * Get division by ar invoice type id.
	 * @param typeId The ar invoice type id.
	 * @return The division id.
	 */
	public int getDivisionIdByAriType(int typeId) {
		int divisionId = 0;
		switch (typeId) {
		case ArInvoiceType.ARI_CENTRAL_TYPE_ID:
			divisionId = 1;
			break;
		case ArInvoiceType.ARI_NSB3_TYPE_ID:
			divisionId = 2;
			break;
		case ArInvoiceType.ARI_NSB4_TYPE_ID:
			divisionId = 3;
			break;
		case ArInvoiceType.ARI_NSB5_TYPE_ID:
			divisionId = 4;
			break;
		case ArInvoiceType.ARI_NSB8_TYPE_ID:
			divisionId = 5;
			break;
		case ArInvoiceType.ARI_NSB8A_TYPE_ID:
			divisionId = 6;
			break;
		}
		return divisionId;
	}

	/**
	 * Get PO number from {@link SalesOrder} by {@link ArInvoice} deliveryReceipt reference id.
	 * @param arInvoice The {@link ArInvoice}.
	 * @return The {@link SalesOrder} PO number.
	 */
	public String getPoNumber(ArInvoice arInvoice) {
		String referenceDrIds = arInvoice.getStrDrRefIds();
		String strDrReferenceIds[] = referenceDrIds.split(", ");
		SalesOrder so = null;
		String poNumbers = "";
		int index = 0;
		HashMap<String, String> hmPoNumbers = new HashMap<String, String>();
		for (String strDrId : strDrReferenceIds) {
			int drId = Integer.parseInt(strDrId);
			so = salesOrderDao.getSoByDr(drId);
			if (so.getSoTypeId() == SOType.PO_SO_TYPE_ID) {
				String poNumber = so.getPoNumber();
				String key = poNumber;
				if (!hmPoNumbers.containsKey(key)) {
					if (index == 0) {
						poNumbers += so.getPoNumber();
					} else {
						poNumbers += ", " + so.getPoNumber();
					}
					hmPoNumbers.put(key, poNumber);
					index++;
				}
			}
			so = null;
		}
		hmPoNumbers = null;
		return poNumbers;
	}

	/**
	 * Get the {@link DeliveryReceipt} by {@link ArInvoice} deliveryReceipt reference id.
	 * @param arInvoice The {@link ArInvoice}.
	 * @return The {@link DeliveryReceipt).
	 */
	public DeliveryReceipt getDrByAri(ArInvoice arInvoice) {
		DeliveryReceipt dr = null;
		String referenceDrIds = arInvoice.getStrDrRefIds();
		String strDrReferenceIds[] = referenceDrIds.split(", ");
		int salesPersonnelCounter = 0;
		int drNumberCounter = 0;
		String salesPersonnels = "";
		String drNumbers = "";
		HashMap<String, String> hmSalesPersonnel = new HashMap<String, String>();
		for (String strDrId : strDrReferenceIds) {
			int drId = Integer.parseInt(strDrId);
			dr = deliveryReceiptDao.get(drId);
			String personelName = "";
			if (dr.getSalesPersonnelId() != null) {
				personelName = salesPersonnelDao.get(dr.getSalesPersonnelId()).getName();
				String key = personelName;
				if (!hmSalesPersonnel.containsKey(key)) {
					if (salesPersonnelCounter == 0) {
						salesPersonnels += personelName;
					} else {
						salesPersonnels += ", " + personelName;
					}
					salesPersonnelCounter++;
					hmSalesPersonnel.put(key, personelName);
				}
			}
			// set DR reference sequence numbers
			String drRefNumber = dr.getDrRefNumber();
			if (drRefNumber != null && !drRefNumber.isEmpty()) {
				if (drNumberCounter == 0) {
					drNumbers += drRefNumber;
				} else {
					drNumbers += ", " + drRefNumber;
				}
				drNumberCounter++;
			}
			// free up memory
			dr = null;
		}
		dr = new DeliveryReceipt();
		dr.setSalesPersonnelName(salesPersonnels);
		dr.setDrRefNumber(drNumbers);
		return dr;
	}

	/**
	 * Validate ar invoice receiving details.
	 * @param arInvoice The {@link ArInvoice}.
	 * @param errors The {@link Error}
	 */
	public void validateStatusLogs(ArInvoice arInvoice, Errors errors) {
		if (arInvoice.getDateReceived() == null) {
			//Received date is required.
			errors.rejectValue("dateReceived", null, null, ValidatorMessages.getString("ArInvoiceService.15"));
		}else if (!timePeriodService.isOpenDate(arInvoice.getDateReceived())) {
				errors.rejectValue("dateReceived", null, null, ValidatorMessages.getString("ArInvoiceService.19"));
		}
		if (arInvoice.getReceiver() == null || arInvoice.getReceiver().trim().isEmpty()) {
			//Received by is required.
			errors.rejectValue("receiver", null, null, ValidatorMessages.getString("ArInvoiceService.16"));
		} else if (arInvoice.getReceiver().length() > ArInvoice.MAX_RECEIVER) {
			//Received by should not exceed %d characters.
			errors.rejectValue("receiver", null, null,
					String.format(ValidatorMessages.getString("ArInvoiceService.17"), ArInvoice.MAX_RECEIVER));
		}
	}

	/**
	 * Save ar invoice with receiving details.
	 * @param arInvoice The {@link ArInvoice}.
	 * @param user The {@link User}.
	 */
	public void saveAriReceivingDetails(ArInvoice arInvoice, User user) {
		ArInvoice savedAri = arInvoiceDao.get(arInvoice.getId());
		//Set receiving details
		savedAri.setDateReceived(arInvoice.getDateReceived());
		savedAri.setReceiver(StringFormatUtil.removeExtraWhiteSpaces(arInvoice.getReceiver()));
		AuditUtil.addAudit(savedAri, new Audit(user.getId(), true, new Date()));
		//Save
		deliveryReceiptDao.saveOrUpdate(savedAri);
	}

	/**
	 * Get the remaining unused advance payment balance.
	 * @param deliveryReceiptId The delivery receipt id.
	 * @param arInvoiceId The ar invoice id.
	 * @return The remaining balance.
	 */
	public double getCapRemainingBalance(String deliveryReceiptIds, Integer arInvoiceId) {
		String drIds[] = deliveryReceiptIds.split(", ");
		DeliveryReceipt dr = null;
		double totalRemainingBalance = 0;
		for (String drId : drIds) {
			dr = deliveryReceiptDao.get(Integer.parseInt(drId));
			if (dr != null) {
				totalRemainingBalance += capDao.getRemainingCapBalance(dr.getSalesOrderId(), arInvoiceId, null);
			}
		}
		return totalRemainingBalance;
	}

	/**
	 * Get the invoice gross amount.
	 * @param arInvoice The {@link ArInvoice}.
	 * @return The invoice gross amount.
	 */
	public double getInvoiceGrossAmount(ArInvoice arInvoice) {
		return arInvoice.getAmount() + arInvoice.getWtAmount() + arInvoice.getWtVatAmount();
	}

	/**
	 * Get the invoice sub total
	 * @param arInvoice The {@link ArInvoice}.
	 * @return The invoice sub total.
	 */
	public double getSubtotal(ArInvoice arInvoice) {
		return arInvoice.getAmount() + arInvoice.getWtAmount() + arInvoice.getWtVatAmount() + arInvoice.getRecoupment() + arInvoice.getRetention() - computeTotalVat(arInvoice);
	}

	/**
	 * Get the invoice tax type
	 * @param arInvoice The {@link ArInvoice}.
	 * @return The tax type of the invoice.
	 */
	public Integer getTaxType(ArInvoice arInvoice) {
		Integer taxTypeId = -1;
		if (arInvoice != null) {
			//Serial Item
			if (arInvoice.getSerialArItems() != null) {
				for (SerialItem si : arInvoice.getSerialArItems()) {
					if (si.getTaxTypeId() != null) {
						taxTypeId = si.getTaxTypeId();
					}
				}
			}
			//Non-serial Item
			if (arInvoice.getNonSerialArItems() != null) {
				for (ArInvoiceItem item : arInvoice.getNonSerialArItems()) {
					if (item.getTaxTypeId() != null) {
						taxTypeId = item.getTaxTypeId();
					}
				}
			}
			//Lines
			if (arInvoice.getAriLines() != null) {
				for (ArInvoiceLine ariLine : arInvoice.getAriLines()) {
					if (ariLine.getTaxTypeId() != null) {
						taxTypeId = ariLine.getTaxTypeId();
					}
				}
			}
		}
		return taxTypeId;
	}
}
