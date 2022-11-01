package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.CAPDeliveryArLineDao;
import eulap.eb.dao.CAPDeliveryDao;
import eulap.eb.dao.CAPDeliveryItemDao;
import eulap.eb.dao.CapArLineDao;
import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.dao.CustomerAdvancePaymentItemDao;
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.CAPDelivery;
import eulap.eb.domain.hibernate.CAPDeliveryArLine;
import eulap.eb.domain.hibernate.CAPDeliveryItem;
import eulap.eb.domain.hibernate.CapArLine;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentItem;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;

/**
 * Business logic for CAP Delivery.

 *
 */
@Service
public class CAPDeliveryService extends BaseWorkflowService{
	private Logger logger = Logger.getLogger(CAPDeliveryService.class);
	@Autowired
	private CAPDeliveryDao deliveryDao;
	@Autowired
	private CAPDeliveryItemDao deliveryItemDao;
	@Autowired
	private CAPDeliveryArLineDao deliveryArLineDao;
	@Autowired
	private CustomerAdvancePaymentDao caPaymentDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private CustomerAdvancePaymentItemDao capItemDao;
	@Autowired
	private CapArLineDao capArLineDao;
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private EbObjectService ebObjectService;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private ItemBagQuantityDao itemBagQuantityDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;

	/**
	 * Get the CAP delivery object by id
	 * @param id The CAP delivery id
	 * @return The CAP delivery object
	 */
	public CAPDelivery getCAPDelivery(int id) {
		return deliveryDao.get(id);
	}

	/**
	 * Get the {@link CAPDelivery} and process the list of {@link CAPDeliveryItem}
	 * @param id The unique id of the CAP Delivery.
	 * @return The CAP Delivery object.
	 */
	public CAPDelivery getDeliveryWithItems(int id) {
		CAPDelivery delivery = getCAPDelivery(id);
		delivery.setDeliveryItems(getDeliveryItems(id));
		List<CAPDeliveryItem> deliveryItems = delivery.getDeliveryItems();
		SaleItemUtil<CAPDeliveryItem> saleItemUtil = 
				new SaleItemUtil<CAPDeliveryItem>();
		if (delivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.RETAIL)) {
			deliveryItems = saleItemUtil.processSaleItemsForViewing(deliveryItems);
		}

		EBObject ebObject = null;
		for (CAPDeliveryItem capdItem : deliveryItems) {
			if (delivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.RETAIL)) {
				double existingStocks = itemService.getItemExistingStocks(capdItem.getItemId(), capdItem.getWarehouseId());
				capdItem.setExistingStocks(existingStocks);
			} else {
				ebObject = ooLinkHelper.getReferenceObject(capdItem.getEbObjectId(), 12);
				capdItem.setReferenceId(ebObject.getId());
				capdItem.setReferenceObjectId(ebObject.getId());
				capdItem.setOrigRefObjectId(ebObject.getId());
				itemBagQuantityService.setItemBagQty(capdItem, capdItem.getEbObjectId(), ItemBagQuantity.PIAD_IS_BAG_QTY);
			}
			capdItem.setStockCode(capdItem.getItem().getStockCode());
			// Set the quantity to the the original quantity
			double origSrp = capdItem.getItemSrp().getSrp();
			capdItem.setOrigQty(capdItem.getQuantity());
			capdItem.setOrigWarehouseId(capdItem.getWarehouseId());
			capdItem.setOrigSrp(origSrp);
			double computeAddOn = SaleItemUtil.computeAddOn(origSrp, capdItem.getQuantity(), capdItem.getSrp());
			capdItem.setAddOn(computeAddOn);
		}

		delivery.setDeliveryItems(deliveryItems);
		delivery.setDeliveryArLines(getDetailedDeliveryArLines(id));
		return delivery;
	}

	/**
	 * Get the list of {@link CAPDeliveryArLine}.
	 * @param capDeliveryId The id of the CAP Delivery.
	 * @return The list of Delivery AR Lines/Other Charges.
	 */
	public List<CAPDeliveryArLine> getDeliveryArLines(int capDeliveryId) {
		return deliveryArLineDao.getAllByRefId("capDeliveryId", capDeliveryId);
	}

	/**
	 * Get the detailed list of {@link CAPDeliveryArLine}.
	 * @param capDeliveryId The id of the CAP Delivery.
	 * @return The list of Delivery AR Lines/Other Charges.
	 */
	public List<CAPDeliveryArLine> getDetailedDeliveryArLines(int capDeliveryId) {
		List<CAPDeliveryArLine> arLines = getDeliveryArLines(capDeliveryId);
		if (arLines.isEmpty()) {
			return Collections.emptyList();
		}

		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>(arLines);
		otherCharges = cashSaleService.getDetailOtherCharges(otherCharges);
		List<CAPDeliveryArLine> processedList = new ArrayList<CAPDeliveryArLine>();
		for (AROtherCharge oc : otherCharges) {
			processedList.add((CAPDeliveryArLine) oc);
		}
		return processedList;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return getCAPDelivery(id).getFormWorkflow();
	}

	/**
	 * Allocate and save the CAP Delivery items.
	 */
	private void allocateAndSaveItems(CAPDelivery delivery, boolean isNew) throws CloneNotSupportedException {
		logger.info("Allocating and Saving CAP Delivery Items.");
		int deliveryId = delivery.getId();
		SaleItemUtil<CAPDeliveryItem> saleItemUtil = new SaleItemUtil<CAPDeliveryItem>();
		List<Domain> toBeSavedItems = new ArrayList<Domain>();
		List<CAPDeliveryItem> deliveryItems = delivery.getDeliveryItems();
		// Disable the computation of weighted average and implement FIFO
		// Map<AllocatorKey, ItemAllocator<CAPDeliveryItem>> itemId2CostAllocator =
		//		new HashMap<AllocatorKey, ItemAllocator<CAPDeliveryItem>>();
		// ItemAllocator<CAPDeliveryItem> itemAllocator = null;
		Map<AllocatorKey, WeightedAveItemAllocator<CAPDeliveryItem>> itemId2CostAllocator =
				new HashMap<AllocatorKey, WeightedAveItemAllocator<CAPDeliveryItem>>();
		WeightedAveItemAllocator<CAPDeliveryItem> itemAllocator = null;
		AllocatorKey key = null;
		for (CAPDeliveryItem di : deliveryItems) {
			itemAllocator = itemId2CostAllocator.get(di.getItemId());
			if (itemAllocator == null) {
				// itemAllocator = new WeightedAverageAllocator<CAPDeliveryItem>(itemService, di.getItemId(), delivery.getCompanyId(),
				//		di.getWarehouseId(), delivery.getDeliveryDate());
				itemAllocator = new WeightedAveItemAllocator<CAPDeliveryItem>(itemDao, itemService,
						di.getItemId(), di.getWarehouseId(), delivery.getDeliveryDate());
				key = AllocatorKey.getInstanceOf(di.getItemId(), di.getWarehouseId());
				itemId2CostAllocator.put(key, itemAllocator);
			}

			List<CAPDeliveryItem> allocatedDeliveryItems = itemAllocator.allocateCost(di);
			allocatedDeliveryItems = saleItemUtil.processDiscountAndAmount(allocatedDeliveryItems, itemDiscountService);
			for (CAPDeliveryItem allocItem : allocatedDeliveryItems) {
				allocItem.setCapDeliveryId(deliveryId);
				SaleItemUtil.setNullUnitCostToZero(allocItem);
				toBeSavedItems.add(allocItem);
			}
		}

		deliveryItemDao.batchSave(toBeSavedItems); //Batch save the delivery items.
		logger.info("Successfully saved the delivery items.");
	}

	/**
	 * Delete the Delivery Items after the form is edited.
	 * @param deliveryId The id of the Paid in Advance Delivery object.
	 */
	private void deleteDeliveryItems(int deliveryId) {
		List<CAPDeliveryItem> deliveryItems = null;
		if (deliveryId > 0) {
			deliveryItems = getDeliveryItems(deliveryId);
			for (CAPDeliveryItem di : deliveryItems) {
				deliveryItemDao.delete(di);
			}
		}
	}

	/**
	 * Save Other Charges of the CAP Delivery.
	 */
	private void saveOtherCharges(CAPDelivery delivery, boolean isNew) {
		List<CAPDeliveryArLine> arlines = delivery.getDeliveryArLines();
		if (arlines.isEmpty()) {
			logger.info("No other charges to be saved.");
		} else {
			int deliveryId = delivery.getId();
			logger.debug("Saving other charges.");
			if (!isNew) {
				List<CAPDeliveryArLine> savedArLines = getDeliveryArLines(deliveryId);
				if (!savedArLines.isEmpty()) {
					logger.debug("Deleting saved Other Charges.");
					for (CAPDeliveryArLine arl : savedArLines) {
						deliveryArLineDao.delete(arl);
					}
					logger.debug("Deleted "+savedArLines.size()+" Other Charges.");
				}
			}

			for (CAPDeliveryArLine arl : arlines) {
				arl.setCapDeliveryId(deliveryId);
				deliveryArLineDao.save(arl);
			}
			logger.info("Successfully saved Other Charges.");
		}
	}

	/**
	 * Get the list of {@link CAPDeliveryItem} by CAP Delivery id.
	 * @param deliveryId The unique id of the {@link CAPDelivery}.
	 * @return The list of Delivery Items.
	 */
	public List<CAPDeliveryItem> getDeliveryItems(int deliveryId) {
		List<CAPDeliveryItem> capDeliveryItems = deliveryItemDao.getAllByRefId("capDeliveryId", deliveryId);
		for(CAPDeliveryItem capDeliveryItem : capDeliveryItems) {
			if (capDeliveryItem.getEbObjectId() != null) {
				itemBagQuantityService.setItemBagQty(capDeliveryItem, capDeliveryItem.getEbObjectId(), ItemBagQuantity.PIAD_IS_BAG_QTY);
			}
		}
		return capDeliveryItems;
	}

	/**
	 * Get the list of Customer Advance Payments to be used as reference for {@link CAPDelivery}.
	 * @param companyId The id of the company
	 * @param typeId The inventory type of the CAP {1=Retail, 3=Inventory Listing}.
	 * @param arCustomerId The id of the customer.
	 * @param arCustomerAccountId The id of the customer account.
	 * @param dateFrom Start date of the date range.
	 * @param dateTo End date of the date range.
	 * @param statusId The status id.
	 * @param pageNumber The page number.
	 * @param user The current user logged.
	 * @return The list of {@link CustomerAdvancePayment}.
	 */
	public Page<CustomerAdvancePayment> getCAPReferences(int companyId, int typeId, int arCustomerId,
			int arCustomerAccountId, int capNo, Date dateFrom, Date dateTo, int statusId, int pageNumber, User user) {
		return caPaymentDao.getCAPReferences(companyId, typeId, arCustomerId, arCustomerAccountId,
				capNo, dateFrom, dateTo, statusId, new PageSetting(pageNumber), user);
	}

	/**
	 * Get the list of unused {@link CustomerAdvancePayment}.
	 * @param user The current user logged.
	 * @param typeId The inventory type of the CAP {1=Retail, 3=Inventory Listing}.
	 * @return The list of unused {@link CustomerAdvancePayment}.
	 */
	public Page<CustomerAdvancePayment> getUnusedReferences(User user, int typeId) {
		return getCAPReferences(-1, typeId, -1, -1, -1,  null, null, SaleItemUtil.STATUS_UNUSED, PageSetting.START_PAGE, user);
	}

	/**
	 * Convert the customer advance payment to delivery customer advance payment.
	 * @param cap Customer advance payment.
	 * @return The converted cap object.
	 */
	public CAPDelivery convertCAPtoDelivery (CustomerAdvancePayment cap) {
		if (cap != null) {
			CAPDelivery capDelivery = new CAPDelivery();
			capDelivery.setCustomerAdvancePaymentId(cap.getId());
			capDelivery.setCompanyId(cap.getCompanyId());
			capDelivery.setArCustomerId(cap.getArCustomerId());
			capDelivery.setArCustomerAcctId(cap.getArCustomerAccountId());
			capDelivery.setSalesInvoiceNo(cap.getSalesInvoiceNo());
			capDelivery.setCompany(cap.getCompany());
			capDelivery.setArCustomer(cap.getArCustomer());
			capDelivery.setArCustomerAccount(cap.getArCustomerAccount());
			capDelivery.setCustomerAdvancePaymentTypeId(cap.getCustomerAdvancePaymentTypeId());
			List<CustomerAdvancePaymentItem> capItems = capItemDao.getCAPItems(cap.getId(), null, null);
			SaleItemUtil<CustomerAdvancePaymentItem> saleItemUtil = null;
			int typeId = cap.getCustomerAdvancePaymentTypeId();
			if (capItems != null && !capItems.isEmpty()) {
				if (typeId == CustomerAdvancePaymentType.RETAIL) {
					saleItemUtil = new SaleItemUtil<CustomerAdvancePaymentItem>();
					capItems = saleItemUtil.processSaleItemsForViewing(capItems);
				}
				List<CAPDeliveryItem> capDItems = new ArrayList<CAPDeliveryItem>();
				for (CustomerAdvancePaymentItem capi : capItems) {
					Item item = capi.getItem();
					CAPDeliveryItem capDItem = new CAPDeliveryItem();
					capDItem.setCapItemId(capi.getId());
					capDItem.setStockCode(item.getStockCode());
					capDItem.setItem(item);
					capDItem.setItemId(capi.getItemId());
					capDItem.setWarehouseId(capi.getWarehouseId());
					capDItem.setQuantity(capi.getQuantity());
					capDItem.setRefQuantity(capi.getQuantity());
					capDItem.setItemDiscountId(capi.getItemDiscountId());
					capDItem.setItemAddOnId(capi.getItemAddOnId());
					capDItem.setDiscount(capi.getDiscount());
					capDItem.setAmount(capi.getAmount());
					capDItem.setUnitCost(capi.getUnitCost());
					capDItem.setSrp(capi.getSrp());
					capDItem.setOrigSrp(capi.getItemSrp().getSrp());
					capDItem.setItemSrpId(capi.getItemSrpId());
					if (typeId == CustomerAdvancePaymentType.INDIV_SELECTION) {
						EBObject ebObject = ooLinkHelper.getReferenceObject(capi.getEbObjectId(), 11);
						logger.warn("Reference EB Object ID from CAP Item: "+ebObject.getId());
						capDItem.setOrigRefObjectId(ebObject.getId());
						capDItem.setReferenceId(ebObject.getId());
						capDItem.setEbObjectId(capi.getEbObjectId());
						itemBagQuantityService.setItemBagQty(capDItem, capDItem.getEbObjectId(), ItemBagQuantity.CAP_IS_BAG_QTY);
					} else {
						
					}
					capDItems.add(capDItem);
				}
				capDelivery.setDeliveryItems(capDItems);
			}

			capDelivery.setDeliveryArLines(convertOtherCharges(cap.getId()));
			logger.debug("Successfully converted the CAP to CAP Delivery object.");
			return capDelivery;
		}
		return null;
	}

	/**
	 * Convert the list of {@link CapArLine} to list of {@link CAPDeliveryArLine}
	 * @param customerAdvPaymentId The id of the customer advance payment.
	 * @return The list of {@link CAPDeliveryArLine}
	 */
	public List<CAPDeliveryArLine> convertOtherCharges(int customerAdvPaymentId) {
		List<CapArLine> arLines = capArLineDao.getAllByRefId("customerAdvancePaymentId", customerAdvPaymentId);
		if (!arLines.isEmpty()) {
			List<CAPDeliveryArLine> deliveryArLines = new ArrayList<CAPDeliveryArLine>();
			CAPDeliveryArLine delArLine = null;
			for (CapArLine arl : arLines) {
				delArLine = CAPDeliveryArLine.getInstanceOf(arl.getArLineSetupId(), arl.getUnitOfMeasurementId(),
						arl.getQuantity(), arl.getUpAmount(), arl.getAmount(),
						arl.getArLineSetup(), arl.getUnitMeasurement());
				deliveryArLines.add(delArLine);
			}
			return deliveryArLines;
		}
		return Collections.emptyList();
	}

	/** 
	 * Get the CAP Delivery object by id.
	 * @param capDelId The CAP Delivery id.
	 * @return The CAP Delivery object.
	 */
	public CAPDelivery getCAPDeliveryPrintOut(Integer capDelId) {
		CAPDelivery capDelivery = deliveryDao.get(capDelId);
		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		List<CAPDeliveryArLine> deliveryArLines = getDetailedDeliveryArLines(capDelId);
		otherCharges.addAll(deliveryArLines);
		capDelivery.setDeliveryArLines(deliveryArLines);
		SaleItemUtil<CAPDeliveryItem> saleItemUtil = new SaleItemUtil<CAPDeliveryItem>();
		capDelivery.setDeliveryItems(saleItemUtil.getSummarisedSaleItems(getDeliveryItems(capDelId)));
		saleItemUtil.generateSaleItemPrintout(capDelivery.getDeliveryItems());
		if (capDelivery != null) {
			double totalAmount = SaleItemUtil.computeTotalAmt(capDelivery.getDeliveryItems());
			getDetailedDeliveryArLines(capDelId);
			if (otherCharges != null) {
				totalAmount += SaleItemUtil.computeTotalOtherCharges(otherCharges);
			}
			capDelivery.setTotalAmount(totalAmount);
		}
		return capDelivery;
	}

	/**
	 * Get the total VAT
	 * @param capDeliveryId The {@link CAPDelivery} object id
	 * @return The total VAT
	 */
	public double getTotalVAT(int capDeliveryId) {
		double totalVAT = 0;
			List<CAPDeliveryItem> capDeliveryItems = getDeliveryItems(capDeliveryId);
		for (CAPDeliveryItem capdi : capDeliveryItems) {
			totalVAT += (capdi.getVatAmount() != null ? capdi.getVatAmount() : 0.0);
		}
		List<CAPDeliveryArLine> capDeliveryArLines = getDeliveryArLines(capDeliveryId);
		for (CAPDeliveryArLine capdal : capDeliveryArLines) {
			totalVAT += (capdal.getVatAmount() != null ? capdal.getVatAmount() : 0.0);
		}
		return totalVAT;
	}

	/**
	 * Compute the remaining amount for CAP.
	 * @param totalCapSales The total amount of CAP.
	 * @param customerAdvPaymentId The id of the CAP.
	 * @return The remaining amount.
	 */
	public double computeRemainingAmt(double totalCapSales, int capDeliveryId, int customerAdvPaymentId) {
		logger.info("Checking the remaining amount of CAP id: "+customerAdvPaymentId);
		double remainingAmt = totalCapSales;
		double totalDeliveredAmt = deliveryItemDao.getTotalDeliveredAmt(capDeliveryId, customerAdvPaymentId);
		remainingAmt -= totalDeliveredAmt;
		logger.info("Remaining amount for CAP id: " + customerAdvPaymentId
				+ " is " + NumberFormatUtil.format(remainingAmt));
		return remainingAmt;
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		CAPDelivery capDelivery = (CAPDelivery) form;
		logger.info("Saving the CAP Delivery.");
		boolean isNew = capDelivery.getId() == 0;
		Date currentDate = new Date();
		AuditUtil.addAudit(capDelivery, new Audit(user.getId(), isNew, currentDate));
		if (isNew) {
			int sequenceNo = deliveryDao.generateSequenceNo(capDelivery.getCustomerAdvancePaymentTypeId(),
					capDelivery.getCompanyId());
			capDelivery.setCapdNumber(sequenceNo);
		} else {
			CAPDelivery savedDelivery = getCAPDelivery(capDelivery.getId());
			DateUtil.setCreatedDate(capDelivery, savedDelivery.getCreatedDate());
			deleteDeliveryItems(capDelivery.getId());
		}

		deliveryDao.saveOrUpdate(capDelivery);

		List<Domain> toBeSavedIbqs = new ArrayList<>();
		List<Domain> o2os = new ArrayList<>();
		if (!capDelivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.INDIV_SELECTION)
				&& !capDelivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.WIP_SPECIAL_ORDER)) {
			try {
				allocateAndSaveItems(capDelivery, isNew);
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		} else {
			for (CAPDeliveryItem capi : capDelivery.getDeliveryItems()) {
				capi.setCapDeliveryId(capDelivery.getId());
				SaleItemUtil.setNullUnitCostToZero(capi);
				deliveryDao.save(capi);
				if (capi.getItemBagQuantity() != null) {
					int ibqObjectId = ebObjectService.saveAndGetEbObjectId(user.getId(), ItemBagQuantity.OBJECT_TYPE_ID, currentDate);
					ItemBagQuantity ibq = ItemBagQuantity.getInstanceOf(capi.getItemId(), ibqObjectId, capi.getItemBagQuantity());
					ibq.setCreatedBy(user.getId());
					AuditUtil.addAudit(ibq, new Audit(user.getId(), isNew, currentDate));
					toBeSavedIbqs.add(ibq);
					o2os.add(ObjectToObject.getInstanceOf(capi.getEbObjectId(),
							ibqObjectId, ItemBagQuantity.PIAD_IS_BAG_QTY, user, currentDate));
				}
			}
			if (!toBeSavedIbqs.isEmpty()) {
				itemBagQuantityDao.batchSave(toBeSavedIbqs);
			}
			if (!o2os.isEmpty()) {
				objectToObjectDao.batchSave(o2os);
			}
		}
		saveOtherCharges(capDelivery, isNew);
		logger.info("Successfully saved Paid in Advance Delivery "
				+ "with sequence number: "+capDelivery.getCapdNumber());
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return deliveryDao.getByEbObjectId(ebObjectId);
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return deliveryDao.getByWorkflowId(workflowId);
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		// Do nothing
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		CAPDelivery capDelivery = deliveryDao.getByEbObjectId(ebObjectId);
		Integer typeId = capDelivery.getCustomerAdvancePaymentTypeId();
		Integer pId = capDelivery.getId();
		FormProperty property = workflowHandler.getProperty(capDelivery.getWorkflowName(), user);
		String popupLink = "/capDelivery/"+typeId+"/form?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String formName = typeId == CustomerAdvancePaymentType.INDIV_SELECTION ? "Paid in Advance Delivery - IS" : "Paid in Advance Delivery";
		String latestStatus = capDelivery.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName + " - " + capDelivery.getCapdNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + capDelivery.getArCustomer().getName())
				.append(" " + capDelivery.getArCustomerAccount().getName())
				.append("<b> DELIVERY DATE : </b>" + DateUtil.formatDate(capDelivery.getDeliveryDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);

	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case CAPDelivery.PIAD_RETAIL_OBJECT_TYPE_ID:
		case CAPDelivery.PIAD_IS_OBJECT_TYPE_ID:
		case CAPDelivery.PIAD_WIP_SO_OBJECT_TYPE_ID:
			return deliveryDao.getByEbObjectId(ebObjectId);
		case CAPDeliveryItem.OBJECT_TYPE_ID:
			return deliveryItemDao.getByEbObjectId(ebObjectId);
		case CAPDeliveryArLine.OBJECT_TYPE_ID:
			return deliveryArLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/*
	 * Disable the computation of weighted average
	@Override
	public List<? extends Domain> getItems(BaseFormWorkflow form) {
		Integer formId = form.getId();
		List<? extends Domain> ret = getDeliveryItems(formId);
		return ret;
	}

	@Override
	public InventoryItem getItemTransaction(Integer objectId) {
		return deliveryItemDao.getByEbObjectId(objectId);
	}

	@Override
	public List<Integer> getWarehouses(BaseFormWorkflow form, Domain itemLine) {
		CAPDelivery capDelivery = (CAPDelivery) form;
		for (CAPDeliveryItem capdi : getDeliveryItems(capDelivery.getId())) {
			if (capdi.getId() == itemLine.getId()) {
				List<Integer> ret = new ArrayList<>();
				ret.add(capdi.getWarehouseId());
				return ret;
			}
		}
		throw new RuntimeException("Unable to find warehouse.");
	}

	@Override
	public List<Integer> getItems(BaseFormWorkflow form, Domain itemLine) {
		CAPDelivery capDelivery = (CAPDelivery) form;
		for (CAPDeliveryItem capdi : getDeliveryItems(capDelivery.getId())) {
			if (capdi.getId() == itemLine.getId()) {
				List<Integer> ret = new ArrayList<>();
				ret.add(capdi.getItemId());
				return ret;
			}
		}
		throw new RuntimeException("Unable to find item.");
	}
	*/
}
