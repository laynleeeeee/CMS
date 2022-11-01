package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.dao.DeliveryReceiptDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.ItemDiscountTypeDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.dao.SalesOrderEquipmentLineDao;
import eulap.eb.dao.SalesOrderItemDao;
import eulap.eb.dao.SalesOrderLineDao;
import eulap.eb.dao.SalesOrderTruckingLineDao;
import eulap.eb.dao.SalesQuotationItemDao;
import eulap.eb.dao.ServiceSettingDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.dao.WorkOrderDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.DeliveryReceiptType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.SalesOrderEquipmentLine;
import eulap.eb.domain.hibernate.SalesOrderItem;
import eulap.eb.domain.hibernate.SalesOrderLine;
import eulap.eb.domain.hibernate.SalesOrderTruckingLine;
import eulap.eb.domain.hibernate.SalesQuotation;
import eulap.eb.domain.hibernate.SalesQuotationEquipmentLine;
import eulap.eb.domain.hibernate.SalesQuotationItem;
import eulap.eb.domain.hibernate.SalesQuotationLine;
import eulap.eb.domain.hibernate.SalesQuotationTruckingLine;
import eulap.eb.domain.hibernate.ServiceLine;
import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * A service class that will handle business logic for {@link SalesQuotation}

 */

@Service
public class SalesOrderService extends BaseWorkflowService {
	@Autowired
	private SalesOrderDao salesOrderDao;
	@Autowired
	private SalesOrderItemDao salesOrderItemDao;
	@Autowired
	private SalesOrderLineDao salesOrderLineDao;
	@Autowired
	private SalesOrderTruckingLineDao truckingLineDao;
	@Autowired
	private SalesOrderEquipmentLineDao equipmentLineDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private SalesQuotationService salesQuotationService;
	@Autowired
	private ItemDiscountTypeDao itemDiscountTypeDao;
	@Autowired
	private UnitMeasurementDao uomDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private UnitMeasurementService uomService;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private ObjectToObjectDao o2oDao;
	@Autowired
	private SalesQuotationItemDao salesQuotationItemDao;
	@Autowired
	private CustomerAdvancePaymentDao capDao;
	@Autowired
	private DeliveryReceiptDao drDao;
	@Autowired
	private WorkOrderDao workOrderDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private SOTypeService soTypeService;
	@Autowired
	private TermService termService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ServiceSettingService serviceSettingService;
	@Autowired
	private ServiceSettingDao serviceSettingDao;
	@Autowired
	private CurrencyService currencyService;

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		SalesOrder salesOrder = (SalesOrder) form;
		int salesOrderId = salesOrder.getId();
		boolean isNew = salesOrderId == 0;
		Integer parentObjectId = salesOrder.getEbObjectId();
		AuditUtil.addAudit(salesOrder, new Audit(user.getId(), isNew, new Date()));
		if (isNew) {
			salesOrder.setSequenceNumber(salesOrderDao.generateSequenceNo(salesOrder.getCompanyId(), salesOrder.getDivisionId()));
		} else {
			List<Integer> toBeDeleted = new ArrayList<Integer>();
			List<SalesOrderItem> savedSOItems = salesOrderItemDao.getAllByRefId(SalesOrderItem.FIELD.salesOrderId.name(), salesOrderId);
			for (SalesOrderItem soi : savedSOItems) {
				toBeDeleted.add(soi.getId());
			}
			salesOrderItemDao.delete(toBeDeleted);

			toBeDeleted = new ArrayList<Integer>();
			List<SalesOrderLine> savedSOLines = salesOrderLineDao.getAllByRefId(SalesOrderLine.FIELD.salesOrderId.name(), salesOrderId);
			for (SalesOrderLine sol : savedSOLines) {
				toBeDeleted.add(sol.getId());
			}
			salesOrderLineDao.delete(toBeDeleted);

			// Free up memory
			toBeDeleted = null;
			savedSOItems = null;
			savedSOLines = null;
		}
		salesOrder.setShipTo(StringFormatUtil.removeExtraWhiteSpaces(salesOrder.getShipTo()));
		boolean isValidRate = salesOrder.getCurrencyRateValue() != null && salesOrder.getCurrencyRateValue() != 0;
		double currencyRate = isValidRate ? salesOrder.getCurrencyRateValue() : 1.0;
		salesOrder.setCurrencyRateValue(currencyRate);
		salesOrder.setAdvancePayment(CurrencyUtil.convertAmountToPhpRate(salesOrder.getAdvancePayment(), currencyRate, true));
		salesOrder.setWtAmount(CurrencyUtil.convertAmountToPhpRate(salesOrder.getWtAmount(), currencyRate, true));
		salesOrder.setWtVatAmount(CurrencyUtil.convertAmountToPhpRate(salesOrder.getWtVatAmount(), currencyRate, true));
		salesOrderDao.saveOrUpdate(salesOrder);

		double totalAmount = 0;
		List<SalesOrderItem> salesOrderItems = processSalesOrderItems(salesOrder.getSoItems(), currencyRate);
		for (SalesOrderItem soi : salesOrderItems) {
			soi.setSalesOrderId(salesOrder.getId());
			totalAmount += soi.getAmount();
			totalAmount += soi.getVatAmount() != null ? soi.getVatAmount() : 0;
			salesOrderItemDao.save(soi);
		}

		List<SalesOrderLine> salesOrderLines = processSalesOrderLines(salesOrder.getSoLines(), currencyRate);
		for (SalesOrderLine sol : salesOrderLines) {
			sol.setSalesOrderId(salesOrder.getId());
			String description = sol.getDescription();
			if (description != null && !description.isEmpty()) {
				sol.setDescription(StringFormatUtil.removeExtraWhiteSpaces(description));
			}
			totalAmount += sol.getAmount();
			totalAmount += sol.getVatAmount() != null ? sol.getVatAmount() : 0;
			salesOrderLineDao.save(sol);
		}

		salesOrder.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalAmount - salesOrder.getWtVatAmount() - salesOrder.getWtAmount()));
		salesOrderDao.update(salesOrder);

		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				salesOrder.getReferenceDocuments(), true);
	}

	private List<SalesOrderItem> processSalesOrderItems(List<SalesOrderItem> salesOrderItems, double currencyRate) {
		for (SalesOrderItem soi : salesOrderItems) {
			double quantity = soi.getQuantity();
			double grossAmount = soi.getGrossAmount() != null ? soi.getGrossAmount() : 0.0;
			soi.setGrossAmount(CurrencyUtil.convertAmountToPhpRate(grossAmount, currencyRate));
			double discount = NumberFormatUtil.roundOffTo2DecPlaces(soi.getDiscount() != null ? soi.getDiscount() : 0);
			soi.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			double vatAmount = 0;
			if (TaxUtil.isVatable(soi.getTaxTypeId())) {
				vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(soi.getVatAmount() != null ? soi.getVatAmount() : 0);
				soi.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			}
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, grossAmount) - vatAmount - discount);
			soi.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		}
		return salesOrderItems;
	}

	public List<SalesOrderLine> processSalesOrderLines(List<SalesOrderLine> salesOrderLines, double currencyRate) {
		for (SalesOrderLine sol : salesOrderLines) {
			double quantity = sol.getQuantity() != null ? sol.getQuantity() : 1.0;
			double upAmount = sol.getUpAmount() != null ? sol.getUpAmount() : 0.0;
			sol.setUpAmount(CurrencyUtil.convertAmountToPhpRate(upAmount, currencyRate));
			double vatAmount = 0;
			if (TaxUtil.isVatable(sol.getTaxTypeId())) {
				vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(sol.getVatAmount() != null ? sol.getVatAmount() : 0);
				sol.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			}
			double discount = 0;
			if (sol.getDiscountTypeId() != null) {
				discount = NumberFormatUtil.roundOffTo2DecPlaces(sol.getDiscount() != null ? sol.getDiscount() : 0.0);
				sol.setDiscount(CurrencyUtil.convertAmountToPhpRate(discount, currencyRate, true));
			}
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, upAmount) - vatAmount - discount);
			sol.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		}
		return salesOrderLines;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return salesOrderDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return salesOrderDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		SalesOrder salesOrder = salesOrderDao.getByEbObjectId(ebObjectId);
		int pId = salesOrder.getId();
		FormProperty property = workflowHandler.getProperty(salesOrder.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = salesOrder.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Sales Order - "+ salesOrder.getDivision().getName()+" - " + salesOrder.getSequenceNumber();
		shortDescription = new StringBuffer(title).append(" " + salesOrder.getArCustomer().getName())
				.append(" " + DateUtil.formatDate(salesOrder.getDate()));
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus, shortDescription.toString(),
				fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case SalesOrder.OBJECT_TYPE:
				return salesOrderDao.getByEbObjectId(ebObjectId);
			case SalesOrderItem.OBJECT_TYPE:
				return salesOrderItemDao.getByEbObjectId(ebObjectId);
			case SalesOrderLine.OBJECT_TYPE:
				return salesOrderLineDao.getByEbObjectId(ebObjectId);
			case SalesOrderTruckingLine.OBJECT_TYPE:
				return truckingLineDao.getByEbObjectId(ebObjectId);
			case SalesOrderEquipmentLine.OBJECT_TYPE:
				return equipmentLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Converts the account sale ar transaction to account sale return.
	 * @param savedTransaction The account sale transaction.
	 * @return The account sale return object.
	 */
	public SalesOrder conv2SalesOrder (Integer salesQuotationId) {
		SalesQuotation sq = salesQuotationService.getSalesQuotation(salesQuotationId);
		SalesOrder so = new SalesOrder();
		if (sq != null) {
			so.setCompanyId(sq.getCompanyId());
			so.setCompany(sq.getCompany());
			so.setDate(sq.getDate());
			so.setSalesQuotationId(sq.getId());
			so.setArCustomerId(sq.getArCustomerId());
			so.setArCustomer(sq.getArCustomer());
			so.setArCustomerAcctId(sq.getArCustomerAcctId());
			so.setArCustomerAccount(sq.getArCustomerAccount());
			so.setCustomerTypeId(sq.getCustomerTypeId());
			so.setCustomerType(sq.getCustomerType());
			so.setShipTo(sq.getShipTo());
			so.setWtAcctSettingId(sq.getWtAcctSettingId());
			so.setRefSQNumber(sq.getCompany().getCompanyCode() + "-" + sq.getSequenceNumber());

			List<SalesOrderItem> soItems = new ArrayList<>();
			List<SalesQuotationItem> sqItems = sq.getSqItems();
			if (sqItems != null && !sqItems.isEmpty()) {
				for(SalesQuotationItem sqItem : sqItems) {
					Item item = sqItem.getItem();
					SalesOrderItem soItem = new SalesOrderItem();
					soItem.setStockCode(item.getStockCode());
					soItem.setItemId(sqItem.getItemId());
					soItem.setItem(sqItem.getItem());
					soItem.setQuantity(sqItem.getQuantity());
					soItem.setGrossAmount(sqItem.getGrossAmount());
					soItem.setDiscount(sqItem.getDiscount());
					soItem.setDiscountValue(sqItem.getDiscountValue());
					soItem.setItemDiscountTypeId(sqItem.getItemDiscountTypeId());
					soItem.setVatAmount(sqItem.getVatAmount());
					soItem.setTaxTypeId(sqItem.getTaxTypeId());
					soItem.setAmount(sqItem.getAmount());
					soItem.setMemo(sqItem.getMemo());
					soItem.setReferenceObjectId(sqItem.getEbObjectId());
					soItems.add(soItem);
				}
				so.setSoItems(soItems);
			}

			List<SalesOrderLine> soLines = new ArrayList<>();
			List<SalesQuotationLine> sqLines = sq.getSqLines();
			if (sqLines != null && !sqLines.isEmpty()) {
				for(SalesQuotationLine sqLine : sqLines) {
					SalesOrderLine soLine = new SalesOrderLine();
//					soLine.setArLineSetupId(sqLine.getArLineSetupId());
//					soLine.setArLineSetupName(sqLine.getArLineSetup().getName());
					soLine.setQuantity(sqLine.getQuantity());
					soLine.setUpAmount(sqLine.getUpAmount());
					soLine.setDiscountTypeId(sqLine.getDiscountTypeId());
					soLine.setDiscountValue(sqLine.getDiscountValue());
					soLine.setDiscount(sqLine.getDiscount());
					soLine.setTaxTypeId(sqLine.getTaxTypeId());
					soLine.setVatAmount(sqLine.getVatAmount());
					soLine.setUnitOfMeasurementId(sqLine.getUnitOfMeasurementId());
					if(sqLine.getUnitOfMeasurementId() != null) {
						soLine.setUnitMeasurementName(sqLine.getUnitMeasurement().getName());
					}
					soLine.setAmount(sqLine.getAmount());
					soLines.add(soLine);
				}
				so.setSoLines(soLines);
			}

			List<SalesOrderTruckingLine> sotLines = new ArrayList<>();
			List<SalesQuotationTruckingLine> sqtLines = sq.getSqtLines();
			if (sqtLines != null && !sqtLines.isEmpty()) {
				for(SalesQuotationTruckingLine sqLine : sqtLines) {
					SalesOrderTruckingLine soLine = new SalesOrderTruckingLine();
					soLine.setArLineSetupId(sqLine.getArLineSetupId());
					soLine.setArLineSetupName(sqLine.getArLineSetup().getName());
					soLine.setQuantity(sqLine.getQuantity());
					soLine.setUpAmount(sqLine.getUpAmount());
					soLine.setDiscountTypeId(sqLine.getDiscountTypeId());
					soLine.setDiscountValue(sqLine.getDiscountValue());
					soLine.setDiscount(sqLine.getDiscount());
					soLine.setTaxTypeId(sqLine.getTaxTypeId());
					soLine.setVatAmount(sqLine.getVatAmount());
					soLine.setUnitOfMeasurementId(sqLine.getUnitOfMeasurementId());
					if(sqLine.getUnitOfMeasurementId() != null) {
						soLine.setUnitMeasurementName(sqLine.getUnitMeasurement().getName());
					}
					soLine.setAmount(sqLine.getAmount());
					sotLines.add(soLine);
				}
				so.setSotLines(sotLines);
			}

			List<SalesOrderEquipmentLine> soeLines = new ArrayList<>();
			List<SalesQuotationEquipmentLine> sqeLines = sq.getSqeLines();
			if (sqeLines != null && !sqeLines.isEmpty()) {
				for(SalesQuotationEquipmentLine sqLine : sqeLines) {
					SalesOrderEquipmentLine soLine = new SalesOrderEquipmentLine();
					soLine.setArLineSetupId(sqLine.getArLineSetupId());
					soLine.setArLineSetupName(sqLine.getArLineSetup().getName());
					soLine.setQuantity(sqLine.getQuantity());
					soLine.setUpAmount(sqLine.getUpAmount());
					soLine.setDiscountTypeId(sqLine.getDiscountTypeId());
					soLine.setDiscountValue(sqLine.getDiscountValue());
					soLine.setDiscount(sqLine.getDiscount());
					soLine.setTaxTypeId(sqLine.getTaxTypeId());
					soLine.setVatAmount(sqLine.getVatAmount());
					soLine.setUnitOfMeasurementId(sqLine.getUnitOfMeasurementId());
					if(sqLine.getUnitOfMeasurementId() != null) {
						soLine.setUnitMeasurementName(sqLine.getUnitMeasurement().getName());
					}
					soLine.setAmount(sqLine.getAmount());
					soeLines.add(soLine);
				}
				so.setSoeLines(soeLines);
			}
		}
		return so;
	}

	/**
	 * Get sales quotation object
	 * @param pId The sales quotation object id
	 * @return The sales quotation object id
	 */
	public SalesOrder getSalesOrder(int pId) {
		return salesOrderDao.get(pId);
	}

	/**
	 * Get sales quotation object
	 * @param pId The sales quotation object id
	 * @return The sales quotation object
	 */
	public SalesOrder getSalesOrder(int pId, boolean isIncludeItemLines) {
		SalesOrder salesOrder = getSalesOrder(pId);
		boolean hasSQReference = salesOrder.getSalesQuotationId() != null;
		if(hasSQReference) {
			SalesQuotation sq = salesQuotationService.getSalesQuotation(salesOrder.getSalesQuotationId());
			salesOrder.setRefSQNumber(sq.getCompany().getCompanyCode() + "-" + sq.getSequenceNumber());
		}
		double rate = salesOrder.getCurrencyRateValue();
		double grandTotal = 0;
		if (isIncludeItemLines) {
			salesOrder.setSoItems(getSOItems(pId, hasSQReference));
			salesOrder.setSoLines(getSOLines(pId));
		}
		for(SalesOrderItem soi: salesOrder.getSoItems()) {
			soi.setDiscount(CurrencyUtil.convertMonetaryValues(soi.getDiscount(), rate));
			soi.setGrossAmount(CurrencyUtil.convertMonetaryValues(soi.getGrossAmount(), rate));
			soi.setVatAmount(CurrencyUtil.convertMonetaryValues(soi.getVatAmount(), rate));
			soi.setAmount(CurrencyUtil.convertMonetaryValues(soi.getAmount(), rate));
			grandTotal += ((soi.getAmount()+soi.getVatAmount()));
		}
		for(SalesOrderLine sol: salesOrder.getSoLines()) {
			sol.setDiscount(CurrencyUtil.convertMonetaryValues(sol.getDiscount(), rate));
			sol.setUpAmount(CurrencyUtil.convertMonetaryValues(sol.getUpAmount(),rate));
			sol.setVatAmount(CurrencyUtil.convertMonetaryValues(sol.getVatAmount(), rate));
			sol.setAmount(CurrencyUtil.convertMonetaryValues(sol.getAmount(), rate));
			grandTotal += ((sol.getAmount()+sol.getVatAmount()));
		}
		salesOrder.setWtAmount(salesOrder.getWtAmount() != null ? CurrencyUtil.convertMonetaryValues(salesOrder.getWtAmount(), rate) : 0.0);
		salesOrder.setWtVatAmount(salesOrder.getWtVatAmount() != null ? CurrencyUtil.convertMonetaryValues(salesOrder.getWtVatAmount(), rate) : 0.0);
		double wtAmount = salesOrder.getWtAmount();
		double wtVatAmount = salesOrder.getWtVatAmount();
		salesOrder.setAmount(grandTotal - wtAmount - wtVatAmount);
		salesOrder.setAdvancePayment(salesOrder.getAdvancePayment() != null
				? CurrencyUtil.convertMonetaryValues(salesOrder.getAdvancePayment(), rate) : null);
		return salesOrder;
	}

	/**
	 * Get the list of sales order items by sales order id.
	 * @param soId The sales order id.
	 * @param hasSQReference True if sales order has sales quotation reference, otherwise false.
	 * @return the list of sales order items by sales order id.
	 */
	public List<SalesOrderItem> getSOItems(int soId, boolean hasSQReference) {
		List<SalesOrderItem> salesOrderItems =
				salesOrderItemDao.getAllByRefId(SalesOrderItem.FIELD.salesOrderId.name(), soId);
		for (SalesOrderItem soi : salesOrderItems) {
			soi.setStockCode(soi.getItem().getStockCode());
			Integer itemDiscountTypeId = soi.getItemDiscountTypeId();
			if (itemDiscountTypeId != null) {
				soi.setItemDiscountType(itemDiscountTypeDao.get(itemDiscountTypeId));
				itemDiscountTypeId = null;
			}
			if(hasSQReference) {
				EBObject refSQIObjId = o2oDao.getOtherReference(soi.getEbObjectId(), SalesOrderItem.SOI_SQI_RELATIONSHIP);
				if(refSQIObjId != null) {
					SalesQuotationItem sqi = salesQuotationItemDao.getByEbObjectId(refSQIObjId.getId());
					soi.setReferenceObjectId(sqi.getEbObjectId());
				}
			}
		}
		return salesOrderItems;
	}

	/**
	 * Get the list of sales order lines for the sales order id
	 * @param soId The sales order id
	 * @return The list of sales order lines for the sales order id
	 */
	public List<SalesOrderLine> getSOLines(int soId) {
		List<SalesOrderLine> salesOrderLines = salesOrderLineDao.getAllByRefId(
				SalesOrderLine.FIELD.salesOrderId.name(), soId);
		setOtherCharges(salesOrderLines);
		return salesOrderLines;
	}

	private <T extends ServiceLine> void setOtherCharges(List<T> otherCharges) {
		UnitMeasurement uom = null;
		ServiceSetting service = null;
		for (ServiceLine sql : otherCharges) {
			service = serviceSettingDao.get(sql.getServiceSettingId());
			if (service != null) {
				sql.setServiceSettingName(service.getName());
			}
			if (sql.getUnitOfMeasurementId() != null) {
				uom = uomDao.get(sql.getUnitOfMeasurementId());
				sql.setUnitMeasurementName(uom.getName());
			}
			Integer itemDiscountTypeId = sql.getDiscountTypeId();
			if (itemDiscountTypeId != null) {
				sql.setItemDiscountType(itemDiscountTypeDao.get(itemDiscountTypeId));
				itemDiscountTypeId = null;
			}
		}
	}

	/**
	 * Get the total vat amount.
	 * @param pId The form id
	 * @return The total vat amount.
	 */
	public double getTotalVatAmount(int pId) {
		double totalVAT = 0;
		List<SalesOrderItem> soItems = getSOItems(pId, false);
		for (SalesOrderItem soItem : soItems) {
			totalVAT += soItem.getVatAmount() != null ? soItem.getVatAmount() : 0.0;
		}
		soItems = null;

		List<SalesOrderLine> soLines = getSOLines(pId);
		for (SalesOrderLine soLine : soLines) {
			totalVAT += soLine.getVatAmount() != null ? soLine.getVatAmount() : 0.0;
		}
		soLines = null;

		return totalVAT;
	}

	/**
	 * Get the subtotal
	 * @param pId The form id
	 * @return The subtotal
	 */
	public double getSubtotal(int pId) {
		double subtotal = 0;
		List<SalesOrderItem> soItems = getSOItems(pId, false);
		double grossAmount = 0.0;
		double quantity = 0.0;
		double discount = 0.0;
		double taxes = 0.0;
		for (SalesOrderItem soItem : soItems) {
			taxes += soItem.getVatAmount() != null ? soItem.getVatAmount() : 0.0;
			discount += soItem.getDiscount() != null ? soItem.getDiscount() : 0.0;
			grossAmount += soItem.getGrossAmount() != null ? soItem.getGrossAmount() : 0.0;
			quantity += soItem.getQuantity() != null ? soItem.getQuantity() : 0.0;
			subtotal += NumberFormatUtil.multiplyWFP(grossAmount, quantity);
		}
		soItems = null;
		List<SalesOrderLine> soLines = getSOLines(pId);
		for (SalesOrderLine soLine : soLines) {
			taxes += soLine.getVatAmount() != null ? soLine.getVatAmount() : 0.0;
			discount += soLine.getDiscount() != null ? soLine.getDiscount() : 0.0;
			subtotal += soLine.getAmount() != null ? soLine.getAmount() : 0.0;
		}
		soLines = null;

		return subtotal - discount - taxes;
	}

	/**
	 * Get the sub total
	 * @param pId The sales order id
	 * @return The sub total
	 */
	public Double getConvertedSubtotal(int pId) {
		SalesOrder salesOrder = getSalesOrder(pId, true);
		Double subTotal = 0.0;
		if(salesOrder.getSoItems() != null || !salesOrder.getSoItems().isEmpty()) {
			for(SalesOrderItem soItem: salesOrder.getSoItems()) {
				subTotal += soItem.getAmount();
			}
		}
		if(salesOrder.getSoLines() != null || !salesOrder.getSoLines().isEmpty()) {
			for(SalesOrderLine soLines: salesOrder.getSoLines()) {
				subTotal += soLines.getAmount();
			}
		}
		return subTotal;
	}

	public Double getConvertedTotalVat(int pId) {
		SalesOrder so = getSalesOrder(pId, true);
		Double totalVat = 0.0;
		if(so.getSoItems() != null || so.getSoItems().isEmpty()) {
			for(SalesOrderItem soItem: so.getSoItems()) {
				totalVat += soItem.getVatAmount();
			}
		}
		if(so.getSoLines() != null || so.getSoLines().isEmpty()) {
			for(SalesOrderLine soLines: so.getSoLines()) {
				totalVat += soLines.getVatAmount();
			}
		}
		return totalVat;
	}
	/**
	 * Validate the form
	 * @param salesOrder The form object for validation
	 * @param errors The validation errors
	 */
	public void validateForm(SalesOrder salesOrder, Errors errors) {
		ValidatorUtil.validateCompany(salesOrder.getCompanyId(), companyService, errors, "companyId");

		if(salesOrder.getDivisionId() == null) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("SalesOrderService.25"));
		} else if (!divisionService.getDivision(salesOrder.getDivisionId()).isActive()) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("SalesOrderService.26"));
		}

		if(salesOrder.getSoTypeId() == null) {
			errors.rejectValue("soTypeId", null, null, ValidatorMessages.getString("SalesOrderService.27"));
		} else if (!soTypeService.getSOType(salesOrder.getSoTypeId()).isActive()) {
			errors.rejectValue("soTypeId", null, null, ValidatorMessages.getString("SalesOrderService.28"));
		}
		Date date = salesOrder.getDate();
		if (date == null) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("SalesOrderService.1"));
		} else if (!timePeriodService.isOpenDate(date)) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("SalesOrderService.2"));
		}

		if(salesOrder.getPoNumber() != null) {
			String poNumber = StringFormatUtil.removeExtraWhiteSpaces(salesOrder.getPoNumber()).trim();
			salesOrder.setPoNumber(poNumber);
			if(poNumber.length() > 50) {
				errors.rejectValue("poNumber", null, null, ValidatorMessages.getString("SalesOrderService.31"));
			}
		}

		Date deliveryDate = salesOrder.getDeliveryDate();
		if (deliveryDate == null) {
			errors.rejectValue("deliveryDate", null, null, ValidatorMessages.getString("SalesOrderService.23"));
		} else if (!timePeriodService.isOpenDate(deliveryDate)) {
			errors.rejectValue("deliveryDate", null, null, ValidatorMessages.getString("SalesOrderService.24"));
		}

		Integer arCustomerId = salesOrder.getArCustomerId();
		if(!salesOrder.getCustomerName().trim().isEmpty() && arCustomerId == null) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("SalesOrderService.35"));
		} else {
			if (arCustomerId == null) {
				errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("SalesOrderService.3"));
			} else if (!arCustomerService.getCustomer(arCustomerId).isActive()) {
				errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("SalesOrderService.4"));
			}
		}

		Integer arCustomerAcctId = salesOrder.getArCustomerAcctId();
		if (arCustomerAcctId == null) {
			errors.rejectValue("arCustomerAcctId", null, null, ValidatorMessages.getString("SalesOrderService.5"));
		} else if (!arCustomerAcctService.getAccount(arCustomerAcctId).isActive()) {
			errors.rejectValue("arCustomerAcctId", null, null, ValidatorMessages.getString("SalesOrderService.6"));
		}

//		Disabling this since this not required on this project
//		Integer customerTypeId = salesOrder.getCustomerTypeId();
//		if (customerTypeId == null) {
//			errors.rejectValue("customerTypeId", null, null, ValidatorMessages.getString("SalesOrderService.7"));
//		} else if (customerTypeService.isActiveCustomerType(customerTypeId)) {
//			errors.rejectValue("customerTypeId", null, null, ValidatorMessages.getString("SalesOrderService.8"));
//		}

		if(salesOrder.getTermId() == null) {
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("SalesOrderService.29"));
		} else if (!termService.getTerm(salesOrder.getTermId()).isActive()) {
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("SalesOrderService.30"));
		}

		String shipTo = salesOrder.getShipTo();
		if (shipTo == null || shipTo.isEmpty()) {
			errors.rejectValue("shipTo", null, null, ValidatorMessages.getString("SalesOrderService.9"));
		}

		if(salesOrder.isDeposit()) {
			if(salesOrder.getAdvancePayment() <= 0) {
				errors.rejectValue("advancePayment", null, null, ValidatorMessages.getString("SalesOrderService.37"));
			}
		}

		Double amount = salesOrder.getAmount();
		if (amount == null) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("SalesOrderService.10"));
		} else if (amount <= 0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("SalesOrderService.11"));
		}

		if(salesOrder.getCurrencyId() == null) {
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("SalesOrderService.32"));
		} else if (!currencyService.getCurency(salesOrder.getCurrencyId()).isActive()) {
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("SalesOrderService.33"));
		}

		boolean hasItems = false;
		List<SalesOrderItem> salesOrderItems = salesOrder.getSoItems();
		if (salesOrderItems != null && !salesOrderItems.isEmpty()) {
			int row = 0;
			for (SalesOrderItem soi : salesOrderItems) {
				row++;
				Integer itemId = soi.getItemId();
				if (itemId == null) {
					errors.rejectValue("soItems", null, null, 
							String.format(ValidatorMessages.getString("SalesOrderService.13"), row));
				} else if (!itemService.getItem(itemId).isActive()) {
					errors.rejectValue("soItems", null, null,
							String.format(ValidatorMessages.getString("SalesOrderService.14"), soi.getStockCode(), row));
				}
				double quantity = soi.getQuantity() != null ? soi.getQuantity() : 0;
				if (quantity <= 0) {
					errors.rejectValue("soItems", null, null,
							String.format(ValidatorMessages.getString("SalesOrderService.15"), row));
				} else if(soi.getRefenceObjectId() != null) {
					if(soi.getRefenceObjectId() != null) {
						SalesQuotationItem sqi = salesQuotationItemDao.getByEbObjectId(soi.getRefenceObjectId());
						if(quantity > sqi.getQuantity()) {
							errors.rejectValue("soItems", null, null,
									String.format(ValidatorMessages.getString("SalesOrderService.16"), row));
						}
					}
				}
				if (soi.getItemDiscountTypeId() != null) {
					double discount = soi.getDiscountValue() != null ? soi.getDiscountValue() : 0;
					if (discount <= 0) {
						errors.rejectValue("soItems", null, null,
								String.format(ValidatorMessages.getString("SalesOrderService.17"), row));
					}
				}
				if(soi.getGrossAmount() != null) {
					if (soi.getGrossAmount() < 0.0) {
						errors.rejectValue("soItems", null, null,
								String.format(ValidatorMessages.getString("SalesOrderService.36"), row));
					}
				}
				if(itemId != null) {
					Item i = itemService.getItem(itemId);
					if (!i.getUnitMeasurement().isActive()) {
						errors.rejectValue("soItems", null, null,
								String.format(ValidatorMessages.getString("SalesOrderService.20"), row));
					}
				}
			}
		} else {
			hasItems = true;
		}

		boolean hasOtherCharges = false;
		List<SalesOrderLine> salesOrderLines = salesOrder.getSoLines();
		if (salesOrderLines != null && !salesOrderLines.isEmpty()) {
			validateOtherCharges(salesOrderLines, errors, "soLines");
		} else {
			hasOtherCharges = true;
		}

		if (hasItems && hasOtherCharges) {
			errors.rejectValue("soLines", null, null, ValidatorMessages.getString("SalesOrderService.12"));
		}

		//Validating reference document.
		refDocumentService.validateReferences(salesOrder.getReferenceDocuments(), errors);
	}

	private <T extends ServiceLine> void validateOtherCharges(List<T> otherCharges, Errors errors, String field) {
		int row = 0;
		ServiceSetting service = null;
		UnitMeasurement uom = null;
		if(!otherCharges.isEmpty() || otherCharges == null) {
			for (ServiceLine arOtherCharge : otherCharges) {
				row++;
				Integer serviceId = arOtherCharge.getServiceSettingId();
				if (serviceId == null) {
					if(!(arOtherCharge.getServiceSettingName() == null)) {
						errors.rejectValue(field, null, null,
								String.format(ValidatorMessages.getString("SalesOrderService.22"), arOtherCharge.getServiceSettingName(), row));
					} else {
						errors.rejectValue("soLines", null, null, 
								String.format(ValidatorMessages.getString("SalesOrderService.18"), row));
					}
				} else {
					if(arOtherCharge.getServiceSettingName() != null) {
						service = serviceSettingService.getServiceSettingByName(arOtherCharge.getServiceSettingName());
						if (service == null) {
							errors.rejectValue(field, null, null,
									String.format(ValidatorMessages.getString("SalesOrderService.22"), arOtherCharge.getServiceSettingName(), row));
						}
						else if (!serviceSettingService.getServiceSetting(serviceId).isActive()) {
							errors.rejectValue(field, null, null,
									String.format(ValidatorMessages.getString("SalesOrderService.19"), arOtherCharge.getServiceSettingName(), row));
						}
					}
				}
				if(arOtherCharge.getQuantity() == null) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesOrderService.38"), row));
				} else if (arOtherCharge.getQuantity() <= 0) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesOrderService.39"), row));
				}
				if(arOtherCharge.getUnitMeasurementName() != null) {
					uom = uomService.getUMByName(arOtherCharge.getUnitMeasurementName());
					if(uom == null) {
						errors.rejectValue(field, null, null,
								String.format(ValidatorMessages.getString("SalesOrderService.34"), arOtherCharge.getUnitMeasurementName(), row));
					}
					else if (!uom.isActive()) {
						errors.rejectValue(field, null, null,
								String.format(ValidatorMessages.getString("SalesOrderService.20"), row));
					}
				}
				//Uncomment this code snippet to reject null/0 amounts.
				//if (arOtherCharge.getAmount() == null || arOtherCharge.getAmount() <= 0.0) {
				//	errors.rejectValue(field, null, null,
				//	String.format(ValidatorMessages.getString("SalesOrderService.36"), row));
				//}
			}
		}
	}

	/**
	 * Retrieve the page list of {@code SalesQuotation} for general search
	 * @param searchCriteria The search criteria
	 * @return The page list of {@code SalesQuotation} for general search
	 */
	public List<FormSearchResult> getSOForms(int typeId, String searchCriteria) {
		Page<SalesOrder> salesOrders = salesOrderDao.getSOForms(typeId, searchCriteria, new PageSetting(PageSetting.START_PAGE));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (SalesOrder so : salesOrders.getData()) {
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String title = so.getCompany().getCompanyCode() + " " + so.getSequenceNumber();
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(so.getDate())));
			properties.add(ResultProperty.getInstance("Customer", so.getArCustomer().getName()));
			String status = formStatusDao.get(so.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", status));
			result.add(FormSearchResult.getInstanceOf(so.getId(), title, properties));
		}
		return result;
	}

	/**
	 * Get the list of deposit sales order forms by sequence number and company id search filters.
	 * @param seqNo The sequence number.
	 * @param companyId The company id. 
	 * @return The list of sales orders.
	 */
	public Collection<SalesOrder> showDepositSOForms(String seqNo, Integer companyId) {
		return salesOrderDao.showDepositSOForms(seqNo, companyId, new PageSetting(1, 
				PageSetting.NO_PAGE_CONSTRAINT)).getData();
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			SalesOrder salesOrder = salesOrderDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
			StringBuffer errorMessage = null;
			if(salesOrder != null) {
				Integer soId = salesOrder.getId();
				if(salesOrderDao.isUsedByCAP(soId)) {
					List<CustomerAdvancePayment> caps = capDao.getCAPsBySalesOrderId(soId);
					if(!caps.isEmpty()) {
						errorMessage = new StringBuffer("Sales Order form has been used by the ff forms:");
						for(CustomerAdvancePayment cap : caps) {
							errorMessage.append("<br>CAP-"+cap.getCapNumber());
						}
					}
				}

				if(salesOrderDao.isUsedByWo(soId)) {
					List<WorkOrder> wos = workOrderDao.getWoBySalesOrderId(soId);
					if(!wos.isEmpty()) {
						if(errorMessage == null) {
							errorMessage = new StringBuffer("Sales Order form has been used by the ff forms:");
						}
						for(WorkOrder wo : wos) {
							errorMessage.append("<br>WO-"+wo.getSequenceNumber());
						}
					}
				}

				// Check SO form if used as reference in DR form
				List<DeliveryReceipt> deliveryReceipts = drDao.getUsedDrsBySoId(soId, null);
				if(!deliveryReceipts.isEmpty()) {
					if(errorMessage == null) {
						errorMessage = new StringBuffer("Sales Order form has been used by the following DR:");
					}
					for(DeliveryReceipt dr : deliveryReceipts) {
						errorMessage.append("<br>"+appendDrNoPrefix(dr.getDeliveryReceiptTypeId(), dr.getSequenceNo()));
					}
				}

				if (errorMessage != null) {
					bindingResult.reject("workflowMessage", errorMessage.toString());
					currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
				}
			}
		}
	}

	private String appendDrNoPrefix(Integer typeId, Integer sequenceNo) {
		String prefix = "";
		if (typeId == DeliveryReceiptType.WAYBILL_DR_TYPE_ID) {
			prefix = "WB";
		} else if (typeId == DeliveryReceiptType.DR_TYPE_ID) {
			prefix = "DR-G";
		} else if (typeId == DeliveryReceiptType.EQ_UTIL_DR_TYPE_ID) {
			prefix = "EU";
		} else if (typeId == DeliveryReceiptType.DR_SERVICE_TYPE_ID) {
			prefix = "DR-S";
		}
		return prefix + " " + sequenceNo;
	}

	/**
	 * Get the latest encoded ship to for the selected customer
	 * @param arCustomerId The customer id
	 * @return The latest encoded ship to field
	 */
	public String getCustomerShipTo(Integer arCustomerId) {
		return salesOrderDao.getCustomerShipTo(arCustomerId);
	}
}
