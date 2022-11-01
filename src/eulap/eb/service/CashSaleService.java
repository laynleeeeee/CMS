package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import eulap.eb.dao.CashSaleArLineDao;
import eulap.eb.dao.CashSaleDao;
import eulap.eb.dao.CashSaleItemDao;
import eulap.eb.dao.CashSaleReturnDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleArLine;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ItemAddOn;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.ItemDiscountType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.DailyCashCollection;
import eulap.eb.web.dto.DailySaleDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Class that handles business logic of {@link CashSale}

 *
 */
@Service
public class CashSaleService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(CashSaleService.class);
	@Autowired
	private CashSaleDao cashSaleDao;
	@Autowired
	private CashSaleItemDao cashSaleItemDao;
	@Autowired
	private CashSaleArLineDao cashSaleArLineDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private CashSaleItemService csItemService;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private CashSaleReturnDao cashSaleReturnDao;
	@Autowired
	private CashSaleReturnService cashSaleReturnService;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormStatusDao formStatusDao;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return cashSaleDao.get(id).getFormWorkflow();
	}

	/**
	 * Get the cash sale object by id.
	 * @param cashSaleId The cash sale id.
	 * @return The cash sale object.
	 */
	public CashSale getCashSale(Integer cashSaleId) {
		return cashSaleDao.get(cashSaleId);
	}

	public CashSale getCashSaleWithItems(Integer cashSaleId) {
		CashSale cashSale = getCashSale(cashSaleId);
		List<CashSaleItem> cashSaleItems = csItemService.getAllCashSaleItems(cashSaleId, false);
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		List<CashSaleItem> processedList = saleItemUtil.processSaleItemsForViewing(cashSaleItems);
		Double existingStocks = null;
		for (CashSaleItem csi : processedList) {
			ItemAddOn addOn = csi.getItemAddOn();
			double origSrp = csi.getItemSrp().getSrp();
			if(addOn != null) {
				double computeAddOn = addOn.getItemAddOnTypeId().equals(ItemDiscountType.DISCOUNT_TYPE_AMOUNT)
						? addOn.getValue() : SaleItemUtil.computeAddOn(origSrp, csi.getQuantity(), csi.getSrp());
				csi.setAddOn(computeAddOn);
			}
			existingStocks = itemService.getItemExistingStocks(csi.getItemId(), csi.getWarehouseId(), new Date());
			csi.setStockCode(csi.getItem().getStockCode());
			csi.setExistingStocks(existingStocks);
			csi.setOrigWarehouseId(csi.getWarehouseId());
			csi.setOrigSrp(origSrp);
		}

		cashSale.setCashSaleItems(processedList);
		cashSale.setCashSaleArLines(getDetailedArLines(cashSaleId));
		return cashSale;
	}

	/**
	 * Get the cash sales items
	 * @param cashSaleId The cash sale id
	 * @return The cash sales items
	 */
	public List<CashSaleItem> getCSItems(Integer cashSaleId) {
		CashSale cashSale = cashSaleDao.get(cashSaleId);
		List<CashSaleItem> csItems = cashSaleItemDao.getCashSaleItems(cashSaleId, null, null);
		double discount = 0;
		for (CashSaleItem csi : csItems) {
			if (cashSale.getCashSaleTypeId().equals(CashSaleType.INDIV_SELECTION)) {
				itemBagQuantityService.setItemBagQty(csi, csi.getEbObjectId(), ItemBagQuantity.CS_IS_BAG_QTY);
			}
			discount = csi.getDiscount() != null ? csi.getDiscount() : 0;
			csi.setAmount((csi.getQuantity() * csi.getSrp()) - discount);
		}
		return  csItems;
	}

	/**
	 * Get the Cash Sales object with CS Items and Other Charges.
	 * @param cashSaleId The cash sale id.
	 * @return The {@link CashSale}
	 */
	public CashSale getCsWithItemsAndOC(Integer cashSaleId) {
		CashSale cashSale = getCashSale(cashSaleId);
		List<CashSaleItem> csItems = getCSItems(cashSaleId);
		EBObject ebObject = null;
		for (CashSaleItem csi : csItems) {
			csi.setOrigQty(csi.getQuantity());
			csi.setStockCode(csi.getItem().getStockCode());
			ebObject = ooLinkHelper.getReferenceObject(csi.getEbObjectId(), 4);
			csi.setOrigRefObjectId(ebObject.getId());
			csi.setReferenceObjectId(ebObject.getId());
			double origSrp = csi.getItemSrp().getSrp();
			csi.setOrigSrp(origSrp);
			itemBagQuantityService.setItemBagQty(csi, csi.getEbObjectId(), ItemBagQuantity.CS_IS_BAG_QTY);
		}
		cashSale.setCashSaleItems(csItems);
		cashSale.setCashSaleArLines(getDetailedArLines(cashSaleId));
		return cashSale;
	}
	/**
	 * Get the list of Cash Sales AR Lines.
	 * @param cashSaleId The id of the cash sale.
	 * @return List of Cash Sales AR Lines.
	 */
	public List<CashSaleArLine> getCSArLines(Integer cashSaleId) {
		return cashSaleArLineDao.getCsArLines(cashSaleId);
	}

	/**
	 * Get the detailed AR Lines of Cash Sales.
	 * @param cashSaleId The id of the cash sales.
	 * @return The list of AR Lines
	 */
	public List<CashSaleArLine> getDetailedArLines(Integer cashSaleId) {
		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		otherCharges.addAll(getCSArLines(cashSaleId));
		List<AROtherCharge> processedList = getDetailOtherCharges(otherCharges);
		List<CashSaleArLine> ret = new ArrayList<CashSaleArLine>();
		for (AROtherCharge oc : processedList) {
			ret.add((CashSaleArLine) oc);
		}
		return ret;
	}

	/**
	 * Get the detailed Other Charges and set the AR Line Setup name and UOM name.
	 * @return The list of Other Charges.
	 */
	public List<AROtherCharge> getDetailOtherCharges(List<AROtherCharge> otherCharges) {
		if(otherCharges.isEmpty()) {
			logger.debug("No AR Lines/Other Charges to be processed.");
			return otherCharges;
		}
		List<AROtherCharge> ret = new ArrayList<AROtherCharge>();
		for (AROtherCharge oc : otherCharges) {
			logger.debug("Setting the AR Line setup name and UOM name to AR Line: "+oc.getId());
			oc.setArLineSetupName(oc.getArLineSetup().getName());
			if(oc.getUnitOfMeasurementId() != null) {
				oc.setUnitMeasurementName(oc.getUnitMeasurement().getName());
			}
			ret.add(oc);
		}
		logger.debug("Successfully retrieved "+ret.size()+" AR Lines.");
		return ret;
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		CashSale cashSale = (CashSale) form;
		boolean isNew = cashSale.getId() == 0;
		Date currDate = new Date();
		AuditUtil.addAudit(cashSale, new Audit(user.getId(), isNew, currDate));
		if (isNew) {
			int csNumber = cashSaleDao.generateCsNumber(cashSale.getCompanyId(), cashSale.getCashSaleTypeId());
			logger.info("Generated CS Number: "+csNumber+" for type: "+cashSale.getCashSaleTypeId());
			cashSale.setCsNumber(csNumber);
		} else {
			CashSale savedCS = getCashSale(cashSale.getId());
			DateUtil.setCreatedDate(cashSale, savedCS.getCreatedDate());
			// Get the CS Items
			List<CashSaleItem> savedCSItems = getCSItems(cashSale.getId());
			if (!savedCSItems.isEmpty()) {
				List<Integer> toBeDeletedCSItems = new ArrayList<Integer>();
				for (CashSaleItem csItem : savedCSItems) 
					toBeDeletedCSItems.add(csItem.getId());
				cashSaleItemDao.delete(toBeDeletedCSItems);
				toBeDeletedCSItems = null;
			}
		}
		cashSaleDao.saveOrUpdate(cashSale);

		List<CashSaleItem> csItems = cashSale.getCashSaleItems();
		if (csItems != null && !csItems.isEmpty()) {
			try {
				allocateCsItems(cashSale);
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}

		saveOtherCharges(cashSale.getCashSaleArLines(), cashSale.getId(), isNew);
	}

	private void allocateCsItems (CashSale cashSale) throws CloneNotSupportedException {
		List<CashSaleItem> csItems = cashSale.getCashSaleItems();
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		// Disable the computation of weighted average and implement FIFO
		// Map<AllocatorKey, ItemAllocator<CashSaleItem>> itemId2CostAllocator =
		//		new HashMap<AllocatorKey, ItemAllocator<CashSaleItem>>();
		Map<AllocatorKey, WeightedAveItemAllocator<CashSaleItem>> itemId2CostAllocator =
				new HashMap<AllocatorKey, WeightedAveItemAllocator<CashSaleItem>>();
		AllocatorKey key = null;
		List<Domain> csToBeSaved = new ArrayList<>();
		for (CashSaleItem csItem : csItems) {
			if (csItem.getAmount() == null)
				csItem.setAmount(0.0);
			if (csItem.getItemId() != null) {
				logger.debug("Performing the unit cost allocation for the Cash Sale Items.");
				// ItemAllocator<CashSaleItem> itemAllocator = itemId2CostAllocator.get(csItem.getItemId());
				WeightedAveItemAllocator<CashSaleItem> itemAllocator = itemId2CostAllocator.get(csItem.getItemId());
				if (itemAllocator == null) {
					// itemAllocator = new WeightedAverageAllocator<CashSaleItem>(itemService, csItem.getItemId(),
					//		cashSale.getCompanyId(), csItem.getWarehouseId(), cashSale.getReceiptDate());
					itemAllocator = new WeightedAveItemAllocator<CashSaleItem>(itemDao, itemService,
							csItem.getItemId(), csItem.getWarehouseId(), cashSale.getReceiptDate());
					key = AllocatorKey.getInstanceOf(csItem.getItemId(), csItem.getWarehouseId());
					itemId2CostAllocator.put(key, itemAllocator);
				}

				List<CashSaleItem> allocatedCsItems = itemAllocator.allocateCost(csItem);
				allocatedCsItems = saleItemUtil.processDiscountAndAmount(allocatedCsItems, itemDiscountService);
				for (CashSaleItem csi : allocatedCsItems) {
					logger.debug("Successfully allocated the item id: "+csi.getItemId()
							+" with unit cost: "+csi.getUnitCost());
					csi.setCashSaleId(cashSale.getId());
					SaleItemUtil.setNullUnitCostToZero(csi);
					csToBeSaved.add(csi);
				}
			}
		}
		cashSaleItemDao.batchSave(csToBeSaved);
	}

	/**
	 * Save and set the other chages
	 * @param csArLines The to be save cash sales AR lines
	 * @param cashSaleId The cash sale object id
	 * @param isNew True if the form is new, otherwise edit
	 * @return The processed other charges list
	 */
	public List<Domain> processOtherCharges(List<CashSaleArLine> csArLines, int cashSaleId, boolean isNew) {
		saveOtherCharges(csArLines, cashSaleId, isNew);
		List<Domain> savedDomains = new ArrayList<>();
		for (CashSaleArLine cashSaleArLine : csArLines) {
			savedDomains.add(cashSaleArLine);
		}
		return savedDomains;
	}

	/**
	 * Save the other charges
	 * @param csArLines The to be save cash sales AR lines
	 * @param cashSaleId The cash sale object id
	 * @param isNew True if the form is new, otherwise edit
	 */
	public void saveOtherCharges(List<CashSaleArLine> csArLines, int cashSaleId, boolean isNew) {
		if (!isNew) {
			logger.debug("Deleting saved Other Charges.");
			List<Integer> toBeDeletedIds = new ArrayList<Integer>();
			List<CashSaleArLine> toBeDeleted = getCSArLines(cashSaleId);
			for (CashSaleArLine csa : toBeDeleted) {
				toBeDeletedIds.add(csa.getId());
			}
			cashSaleArLineDao.delete(toBeDeletedIds);
		}

		if (csArLines != null && !csArLines.isEmpty()) {
			for (CashSaleArLine csa : csArLines) {
				csa.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(csa.getAmount()));
				csa.setCashSaleId(cashSaleId);
				cashSaleArLineDao.save(csa);
			}
			logger.info("Successfully saved Other Charges of Cash Sale "+cashSaleId);
		} else {
			logger.info("No Other Charges of Cash Sale "+cashSaleId + " to be saved.");
		}
	}

	/**
	 * Get the cash sales.
	 * @param typeId The type of the cash sales {1 = Retail}
	 * @param param The search parameter.
	 * @return The page result
	 */
	public Page<CashSale> getCashSales(ApprovalSearchParam searchParam, List<Integer> formStatusIds, int typeId,
			PageSetting pageSetting) {
		return cashSaleDao.getCashSales(searchParam, formStatusIds, typeId, pageSetting);
	}

	/**
	 * Checks if the cash is less than the total amount.
	 * @param totalAmount The total amount of the cash sale items.
	 * @param cash The cash entered by the user.
	 * @return True if cash is less than amount, otherwise false.
	 */
	public boolean isCashLessTAmount (double totalAmount, double cash) {
		return NumberFormatUtil.roundOffTo2DecPlaces(cash) < 
				NumberFormatUtil.roundOffTo2DecPlaces(totalAmount);
	}

	/**
	 * Get the cash sale object by id.
	 * @param cashSaleId The cash sale id.
	 * @return The cash sale object.
	 */
	public CashSale getCashSalePrint(Integer cashSaleId) {
		CashSale cashSale = cashSaleDao.get(cashSaleId);
		if (cashSale != null) {
			double discount = 0;
			double totalAmount = 0;
			List<CashSaleItem> csItems = cashSaleItemDao.getCashSaleItems(cashSaleId, null, null);
			for (CashSaleItem csi : csItems) {
				discount = csi.getDiscount() != null ? csi.getDiscount() : 0;
				totalAmount += (csi.getQuantity() * csi.getSrp()) - discount;
			}
			totalAmount += getTotalArLinesAmt(cashSaleId);
			cashSale.setTotalAmount(totalAmount);
		}
		return cashSale;
	}

	/**
	 * Compute the total amount of other charges.
	 * @param cashSaleId The id of the cash sale.
	 * @return The total amount for other charges, otherwise zero.
	 */
	public double getTotalArLinesAmt(int cashSaleId) {
		logger.debug("Computing the total amount in other charges.");
		List<CashSaleArLine> arLines = getCSArLines(cashSaleId);
		double totalAmt = 0;
		if(arLines.isEmpty()) {
			logger.debug("No Other Charges for cash sale "+cashSaleId);
			return totalAmt;
		}

		for (CashSaleArLine csa : arLines) {
			totalAmt += csa.getAmount();
		}
		logger.debug("Total amount of other charges is: "+totalAmt);
		return NumberFormatUtil.roundOffTo2DecPlaces(totalAmt);
	}

	/**
	 * Compute the total VAT for item sold and other charges
	 * @param cashSaleId The cash sale id
	 * @return The computed total VAT
	 */
	public double computeTotalVat(Integer cashSaleId) {
		double totalVat = 0.0;
		List<CashSaleItem> cashSaleItems = getCSItems(cashSaleId);
		for (CashSaleItem csi : cashSaleItems) {
			totalVat += (csi.getVatAmount() != null ? csi.getVatAmount() : 0.0);
		}
		cashSaleItems = null;

		List<CashSaleArLine> cashSaleArLines = getCSArLines(cashSaleId);
		for (CashSaleArLine csa : cashSaleArLines) {
			totalVat += (csa.getVatAmount() != null ? csa.getVatAmount() : 0.0);
		}
		cashSaleArLines = null;
		return NumberFormatUtil.roundOffTo2DecPlaces(totalVat);
	}

	/**
	 * Get the daily sales report.
	 * @param companyId The company id.
	 * @param salesInvoiceNo The sales invoice number.
	 * @param customerName The name of the customer.
	 * @param date The date.
	 * @return The list of daily sales.
	 */
	public List<DailySaleDto> generateDailySales (Integer companyId, String salesInvoiceNo,
			String customerName, Date date) {
		List<DailySaleDto> dailySales = new ArrayList<DailySaleDto>();
		List<CashSale> cashSales = cashSaleDao.getCashSales(companyId, salesInvoiceNo, customerName, date);
		if (cashSales != null && !cashSales.isEmpty()) {
			for (CashSale cs : cashSales) {
				double totalAmount = cashSaleItemDao.getTotalCSIAmtWithTax(cs.getId());
				DailySaleDto dto = DailySaleDto.getInstanceOf(cs.getFormattedCSNumber(), cs.getSalesInvoiceNo(), 
						cs.getArCustomer().getName(), totalAmount);
				dailySales.add(dto);
			}
		}
		return dailySales;
	}

	/**
	 * Get the list of daily cash collections.
	 * @param companyId The company id.
	 * @param userId The user id.
	 * @param date The date.
	 * @param orderType REFERENCE_NO, INVOICE_NO... Default is REFERENCE_NO. 
	 * @return The list of daily cash collections.
	 */
	public List<DailyCashCollection> getDailyCashCollections (Integer companyId, Integer userId, Date date, 
			final String orderType, Integer status) {
		return cashSaleDao.getDailyCashCollections(companyId, userId, date, orderType, status);
	}

	/**
	 * Get the paged list of cash sales for CS Reference.
	 * @param companyId The company id.
	 * @param arCustomerId The customer id.
	 * @param arCustomerAccountId The customer account id.
	 * @param csNumber The cash sale number.
	 * @param dateFrom The start of date range.
	 * @param dateTo The end of date range.
	 * @param status The status of the cash sale.
	 * @param typeId The type of the cash sales, {1 = Retail}
	 * @param user The current user logged.
	 * @param pageSetting The page setting.
	 * @return The paged list of cash sales.
	 */
	public Page<CashSale> getCashSales (Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer csNumber, Date dateFrom, Date dateTo, Integer status, Integer typeId, int pageNumber, User user) {
		return cashSaleDao.getCashSales(companyId, arCustomerId, arCustomerAccountId,
				csNumber, dateFrom, dateTo, status, typeId, new PageSetting(pageNumber), user);
	}

	/**
	 * Remove duplicate cash sale items.
	 */
	public void removeDuplicateCSItems () {
		List<CashSale> cashSales = (List<CashSale>) cashSaleDao.getAll();
		for (CashSale cs : cashSales) {
			int csId = cs.getId();
			boolean hasDuplicate = true;
			while (hasDuplicate) {
				logger.info("Removing duplicate items for cash sale " + csId);
				int currentSize = 0;
				int size = cashSaleItemDao.getCSItemSize(csId);
				while (currentSize <= size) {
					List<CashSaleItem> cashSaleItems = cashSaleItemDao.getCSItemsWithLimit(csId, 200);
					csItemService.removeDuplicateCSItems(csId, cashSaleItems);
					currentSize += 200;
				}
				hasDuplicate = cashSaleItemDao.hasDuplicate(csId);
			}
		}	
		logger.info("Done removing duplicates for Cash Sales");
	}
	
	/**
	 * Get the oldest transaction date.
	 */
	public Date getOldestTrans (int itemId, int warehouseId) {
		return cashSaleDao.getOldestTransactionDate(itemId, warehouseId);
	}
	
	public Page<CashSale> getTransactionPerDate (int itemId, int warehouseId, Date date) {
		PageSetting pS = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		return cashSaleDao.getCashSalePerDate(itemId, warehouseId, date, pS);
	}
	
	/**
	 * Fix the computation of discount and amount of cash sale items.
	 */
	public void fixDiscAndAmount () {
		int maxPerPage = 100;
		int csSize = (cashSaleDao.getCSSize() / maxPerPage) + 1;
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		for (int i=1; i<=csSize; i++) {
			Page<CashSale> cashSales = cashSaleDao.getCashSales(new PageSetting(i, maxPerPage));
			if (cashSales != null && !cashSales.getData().isEmpty()) {
				for (CashSale cs : cashSales.getData()) {
					logger.info("Processing Cash Sale : " + cs.getFormattedCSNumber());
					logger.debug("Getting the cash sale items.");
					List<CashSaleItem> csItems = getCSItems(cs.getId());
					logger.debug("Processing the discount and amount of cash sale items.");
					csItems = saleItemUtil.processDiscountAndAmount(csItems, itemDiscountService);
					if (csItems != null && !csItems.isEmpty()) {
						for (CashSaleItem csItem : csItems) {
							logger.debug("Saving the cash sale item : " + csItem.getId());
							cashSaleItemDao.saveOrUpdate(csItem);
						}
					}
					logger.debug("Freeing the cash sale items.");
					csItems = null;
 				}
			}
			logger.debug("Freeing the cash sales.");
			cashSales = null;
		}
	}


	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		validateFormUsedAsReference(currentWorkflowLog, bindingResult);
	}

	/**
	 * Check if the cash sale form was used as reference form in cash sale return forms
	 * @param currentWorkflowLog The current workflow log
	 * @param bindingResult The binding result
	 */
	public void validateFormUsedAsReference(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		CashSale cashSale = cashSaleDao
				.getCashSaleByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (cashSale != null) {
			if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
				StringBuffer message = null;
				String prefix = cashSaleReturnService.getCsrLabel(cashSale.getCashSaleTypeId());
				Boolean cashSaleReturn = cashSaleDao.isExistingReturn(cashSale
						.getId());
				if(cashSaleReturn) {
					logger.info("Checking Cash sale order with the id: "
							+cashSale.getId()+" if used as reference form");
					String csTypeName = cashSale.getCashSaleTypeId() == CashSaleType.RETAIL
							? "Cash Sale Order" : "Cash Sale - IS";
					message = new StringBuffer(csTypeName+" was used as reference in: ");
					List<CashSaleReturn> cashSaleReturns =
							cashSaleReturnDao.getCSRByRefCSId(cashSale.getId());
					for (CashSaleReturn csr : cashSaleReturns) {
						message.append("<br> "+prefix+" No. "+ csr.getCsrNumber());
					}
					bindingResult.reject("workflowMessage", message.toString());
					currentWorkflowLog.setWorkflowMessage(message.toString());
				}
			}
		}
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		//Do nothing
	}

	/**
	 * Search Cash Sales forms.
	 * @param searchCriteria The search criteria.
	 * @param typeId The type of the cash sales.
	 */
	public List<FormSearchResult> searchCashSales(String searchCriteria, int typeId) {
		logger.info("Searching Cash Sale.");
		logger.debug("Searching for: "+searchCriteria.trim());
		Page<CashSale> cashSales = cashSaleDao.searchCashSales(searchCriteria.trim(), typeId, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		for (CashSale cs : cashSales.getData()) {
			logger.trace("Retrieved CS No. " + cs.getCsNumber());
			title = String.valueOf(cs.getFormattedCSNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", cs.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Customer", cs.getArCustomer().getNumberAndName()));
			properties.add(ResultProperty.getInstance("Customer Account", cs.getArCustomerAccount().getName()));
			properties.add(ResultProperty.getInstance("Receipt Date", DateUtil.formatDate(cs.getReceiptDate())));
			properties.add(ResultProperty.getInstance("Maturity Date", DateUtil.formatDate(cs.getMaturityDate())));
			status = formStatusDao.get(cs.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(cs.getId(), title, properties));
		}
		return result;
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return cashSaleDao.getAllByRefId("ebObjectId", ebObjectId).get(0);
	}

	/**
	 * Get the CS Label of the form based on the type of the CS.
	 * @param typeId The id of the Cash sale type.
	 * @return The label of the form.
	 */
	public String getCsLabel(int typeId) {
		switch (typeId) {
		case CashSaleType.INDIV_SELECTION:
			return "CS - IS ";

		case CashSaleType.PROCESSING:
			return "CS - P ";

		default:
			return "CS ";
		}
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return cashSaleDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		CashSale cashSale = cashSaleDao.getByEbObjectId(ebObjectId);
		Integer typeId = cashSale.getCashSaleTypeId();
		Integer pId = cashSale.getId();
		FormProperty property = workflowHandler.getProperty(cashSale.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String formName = "";
		
		switch (typeId) {
		case CashSaleType.RETAIL:
			formName = "Cash Sale";
			break;

		case CashSaleType.INDIV_SELECTION:
			formName = "Cash Sale - IS";
			break;
		
		case CashSaleType.PROCESSING:
			formName = "Cash Sale - Processing";
			break;
		}

		String latestStatus = cashSale.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName + " - " + cashSale.getCsNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + cashSale.getArCustomer().getName())
				.append(" " + cashSale.getArCustomerAccount().getName())
				.append("<b> RECEIPT DATE : </b>" + DateUtil.formatDate(cashSale.getReceiptDate()))
				.append("<b> MATURITY DATE : </b>" + DateUtil.formatDate(cashSale.getMaturityDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case 13:
			return cashSaleDao.getByEbObjectId(ebObjectId);
		case 12:
			return cashSaleItemDao.getByEbObjectId(ebObjectId);
		case 17:
			return cashSaleArLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/*
	 * Disable the computation of weighted average
	@Override
	public List<? extends Domain> getItems(BaseFormWorkflow form) {
		Integer formId = form.getId();
		List<? extends Domain> ret = getCSItems(formId);
		return ret;
	}

	@Override
	public InventoryItem getItemTransaction(Integer objectId) {
		return cashSaleItemDao.getByEbObjectId(objectId);
	}

	@Override
	public List<Integer> getWarehouses(BaseFormWorkflow form, Domain itemLine) {
		CashSale cs = (CashSale) form;
		for (CashSaleItem csi : getCSItems(cs.getId())) {
			if (csi.getId() == itemLine.getId()) {
				List<Integer> ret = new ArrayList<>();
				ret.add(csi.getWarehouseId());
				return ret;
			}
		}
		throw new RuntimeException("Unable to find warehouse.");
	}

	@Override
	public List<Integer> getItems(BaseFormWorkflow form, Domain itemLine) {
		CashSale cs = (CashSale) form;
		for (CashSaleItem csi : getCSItems(cs.getId())) {
			if (csi.getId() == itemLine.getId()) {
				List<Integer> ret = new ArrayList<>();
				ret.add(csi.getItemId());
				return ret;
			}
		}
		throw new RuntimeException("Unable to find item.");
	}
	*/
}
