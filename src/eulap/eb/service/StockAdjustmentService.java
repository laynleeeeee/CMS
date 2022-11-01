package eulap.eb.service;

import java.io.IOException;
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
import eulap.eb.common.util.ListProcessorUtil;
import eulap.eb.dao.FormWorkflowDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.SerialItemDao;
import eulap.eb.dao.StockAdjustmentDao;
import eulap.eb.dao.StockAdjustmentItemDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.StockAdjustmentClassification;
import eulap.eb.domain.hibernate.StockAdjustmentItem;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.RItemCostUpdateService;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.StockAdjustmentDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Business logic for Retail - Transfer Receipt.


 *
 */
@Service
public class StockAdjustmentService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(StockAdjustmentService.class);
	@Autowired
	private StockAdjustmentDao saDao;
	@Autowired
	private StockAdjustmentItemDao saItemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormWorkflowDao formWorkflowDao;
	@Autowired
	private SerialItemDao saiDao;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return getStockAdjustment(id).getFormWorkflow();
	}

	/**
	 * Get the Stock Adjustment object.
	 * @param stockAdjustmentId The unique id of stock adjustment.
	 * @return The stock adjustment object.
	 */
	public StockAdjustment getStockAdjustment(int stockAdjustmentId) {
		return saDao.get(stockAdjustmentId);
	}

	/**
	 * Get the stock adjustment object and process the stock adjustment items.
	 * @param stockAdjustmentId The unique id of the stock adjustment.
	 * @return The processed stock adjustment object.
	 */
	public StockAdjustment getProcessedStockAdjustment(int stockAdjustmentId, int typeId) {
		logger.info("Retrieving the stock adjustment id: "+stockAdjustmentId);
		StockAdjustment stockAdjustment = getStockAdjustment(stockAdjustmentId);
		stockAdjustment.setTypeId(typeId);
		List<StockAdjustmentItem> saItems = stockAdjustment.getSaItems();
		if (!saItems.isEmpty()) {
			if (typeId == StockAdjustment.STOCK_ADJUSTMENT_OUT) {
				logger.debug("Removing the duplicates from the stock adjustment list.");
				ListProcessorUtil<StockAdjustmentItem> remover = new ListProcessorUtil<StockAdjustmentItem>();
				saItems = remover.removeDuplicate(saItems);
			}
			double rate = stockAdjustment.getCurrencyRateValue();
			Item item = null;
			for (StockAdjustmentItem sai : saItems) {
				if (stockAdjustment.getTypeId() == StockAdjustment.STOCK_ADJUSTMENT_OUT) {
					logger.debug("Set the edited quantity for item id: "+sai.getItemId());
					sai.setQuantity(sai.getProcessedQty());
					sai.setUnitCost(getItemAverageCost(sai.getStockAdjustmentId(), sai.getItemId()));
				}
				item = sai.getItem();
				item.setExistingStocks(itemService.getItemExistingStocks(sai.getItemId(), stockAdjustment.getWarehouseId()));
				sai.setItem(item);
				sai.setOrigItemId(sai.getItemId());
				sai.setStockCode(item.getStockCode());
				double quantity = sai.getQuantity();
				double unitCost = sai.getUnitCost();
				sai.setUnitCost(CurrencyUtil.convertMonetaryValues(unitCost, rate));
				sai.setOrigUnitCost(unitCost);
				sai.setOrigQty(Math.abs(quantity));
				double amount = quantity * unitCost;
				sai.setAmount(CurrencyUtil.convertMonetaryValues(amount, rate, true));
				item = null;
			}
			stockAdjustment.setSaItems(saItems);
		}
		logger.debug("Freeing up memory allocation.");
		saItems = null;
		return stockAdjustment;
	}

	private double getItemAverageCost(int stockAdjustmentId, int itemId) {
		return saItemDao.getItemAverageCost(stockAdjustmentId, itemId);
	}

	public List<StockAdjustmentItem> getSaItems(int stockAdjustmentId) {
		return saItemDao.getSAItems(stockAdjustmentId, null);
	}

	/**
	 * Generate SA Number by company and stock adjustment type.
	 * @param companyId The company id.
	 * @param typeId The type id.
	 * @return The SA number.
	 */
	public int generateSANoByCompAndTypeId(Integer companyId, Integer typeId){
		return saDao.generateSANoByCompAndTypeId(companyId, typeId);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		StockAdjustment stockAdjustment = (StockAdjustment) form;
		logger.info("Saving the Stock Adjustment.");
		Integer stockAdjustmentId = stockAdjustment.getId();
		int typeId = stockAdjustment.getStockAdjustmentClassificationId();
		boolean isNew = stockAdjustmentId == 0;
		Integer saNumber = null;
		Date currentDate = new Date();
		AuditUtil.addAudit(stockAdjustment, new Audit(user.getId(), isNew, currentDate));
		stockAdjustment.setStockAdjustmentClassificationId(typeId);
		List<Integer> toBeDeletedIds = new ArrayList<Integer>();
		List<StockAdjustmentItem> saItems = null;
		if (isNew) {
			stockAdjustment.setSaNumber(generateSANoByCompAndTypeId(stockAdjustment.getCompanyId(), typeId));
			saNumber = saDao.generateSANoByCompAndTypeId(stockAdjustment.getCompanyId(), typeId);
			stockAdjustment.setSaNumber(saNumber);
		} else {
			StockAdjustment savedSA = getStockAdjustment(stockAdjustmentId);
			DateUtil.setCreatedDate(stockAdjustment, savedSA.getCreatedDate());
			logger.debug("Deleting the saved SA Items of SA: "+stockAdjustmentId);
			saItems = getSaItems(stockAdjustmentId);
			if (!saItems.isEmpty()) {
				for (StockAdjustmentItem saItem : saItems) {
					// Compute and save weighted average
					// Deduct to total quantity for stock in, otherwise add for stock out
					toBeDeletedIds.add(saItem.getId());
				}
				saItemDao.delete(toBeDeletedIds);
			}
			logger.trace("Deleted "+toBeDeletedIds.size()+" SA Items.");
			logger.debug("Successfully deleted SA Items of SA: "+stockAdjustmentId);
			savedSA = null;
			toBeDeletedIds = null;
			saItems = null;
		}

		String remarks = stockAdjustment.getRemarks();
		if (remarks != null)
			stockAdjustment.setRemarks(remarks.trim());
		boolean isValidRate = stockAdjustment.getCurrencyRateValue() != null && stockAdjustment.getCurrencyRateValue() != 0;
		double currencyRate = isValidRate ? stockAdjustment.getCurrencyRateValue() : 1.0;
		stockAdjustment.setCurrencyRateValue(currencyRate);
		saDao.saveOrUpdate(stockAdjustment);
		logger.debug("Saved the SA Number: "+stockAdjustment.getFormattedSANumber());

		stockAdjustmentId = stockAdjustment.getId();
		List<Domain> toBeSavedItems = new ArrayList<Domain>();
		saItems = stockAdjustment.getSaItems();
		logger.debug("Saving the SA Items.");
		logger.trace("Saving "+saItems.size()+" Items.");
		if (stockAdjustment.getTypeId() == StockAdjustment.STOCK_ADJUSTMENT_OUT) {
			logger.debug("Allocate the items for Stock Adjustment OUT using the FIFO Cost Allocator.");
			Map<AllocatorKey, WeightedAveItemAllocator<StockAdjustmentItem>> itemId2CostAllocator =
					new HashMap<AllocatorKey, WeightedAveItemAllocator<StockAdjustmentItem>>();
			WeightedAveItemAllocator<StockAdjustmentItem> itemAllocator = null;
			List<StockAdjustmentItem> allocatedSaItem = null;
			AllocatorKey key = null;
			for (StockAdjustmentItem sai : saItems) {
				itemAllocator = itemId2CostAllocator.get(sai.getItemId());
				if (itemAllocator == null) {
					itemAllocator = new WeightedAveItemAllocator<StockAdjustmentItem>(itemDao, itemService, sai.getItemId(),
							stockAdjustment.getWarehouseId(), stockAdjustment.getSaDate());
					key = AllocatorKey.getInstanceOf(sai.getItemId(), stockAdjustment.getWarehouseId());
					itemId2CostAllocator.put(key, itemAllocator);
				}
				sai.setQuantity(Math.abs(sai.getQuantity()));
				try {
					allocatedSaItem = itemAllocator.allocateCost(sai);
				} catch (CloneNotSupportedException e) {
					throw new RuntimeException(e);
				}
				for (StockAdjustmentItem allocItem : allocatedSaItem) {
					logger.debug("Successfully allocated the item id: "+sai.getItemId()
							+" with unit cost: "+sai.getUnitCost());
					double origQty = allocItem.getOrigQty() != null ? allocItem.getOrigQty() : 0;
					allocItem.setOrigQty(-origQty);
					allocItem.setQuantity(-allocItem.getQuantity());
					allocItem.setStockAdjustmentId(stockAdjustmentId);
					double amount = NumberFormatUtil.multiplyWFP(sai.getQuantity(), sai.getUnitCost());
					allocItem.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(amount));
					toBeSavedItems.add(allocItem);
				}
			}
		} else {
			for (StockAdjustmentItem sai : saItems) {
				sai.setStockAdjustmentId(stockAdjustmentId);
				toBeSavedItems.add(processStockAdjItem(sai, currencyRate));
			}
		}
		saItemDao.batchSave(toBeSavedItems);

		logger.debug("Successfully saved "+toBeSavedItems.size()+" SA Items.");
		logger.info("Sucessfully saved the Stock Adjustment.");

		logger.info("======>>> Freeing up memory allocation");
		toBeDeletedIds = null;
		toBeSavedItems = null;
		stockAdjustmentId = null;
		remarks = null;

		// Trigger form unit cost reallocation, this line of code is only possible if the form has a single workflow
		FormWorkflow formWorkflow = formWorkflowDao.get(stockAdjustment.getFormWorkflowId());
		if (formWorkflow.isComplete()) {
			List<FormWorkflowLog> logs = formWorkflow.getFormWorkflowLogs();
			if (logs != null && !logs.isEmpty()) {
				doAfterSaving(logs.iterator().next());
			}
		}
		formWorkflow = null;
	}

	/**
	 * Convert the other charge cost/amount to PHP value
	 * @param sai The other charges
	 * @param currencyRate The currency rate
	 * @return 
	 * @return The other charge cost/amount to PHP value
	 */
	public StockAdjustmentItem processStockAdjItem(StockAdjustmentItem sai, double currencyRate) {
		double unitCost = sai.getUnitCost();
		sai.setUnitCost(CurrencyUtil.convertAmountToPhpRate(unitCost, currencyRate));
		sai.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(sai.getQuantity(), sai.getUnitCost())));
		return sai;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		StockAdjustment stockAdjustment = saDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		Integer selectedStatusId = currentWorkflowLog.getFormStatusId();
		if (selectedStatusId.equals(FormStatus.CANCELLED_ID)) {
			for (StockAdjustmentItem sai : stockAdjustment.getSaItems()) {
				boolean isStockOut = sai.getQuantity() < 0;
				if (stockAdjustment.getFormWorkflow().isComplete()) {
					if (!isStockOut) {
						double totalQty = ValidationUtil.getTotalQtyPerItem(sai.getItemId(), stockAdjustment.getSaItems());
						String errorMessage = ValidationUtil.validateToBeCancelledItem(itemService, sai.getItemId(),
								stockAdjustment.getWarehouseId(), stockAdjustment.getSaDate(), totalQty);
						if (errorMessage != null) {
							bindingResult.reject("workflowMessage", errorMessage);
							currentWorkflowLog.setWorkflowMessage(errorMessage);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		// Do nothing
	}

	/**
	 * Get all the withdrawn stock adjustment.
	 * @param itemId The selected item.
	 * @param warehouseId the warehouse id. 
	 * @param date The current date.
	 * @return All of the withdrawn adjustment in the specified date. 
	 */
	public Page<StockAdjustment> getWithdrawnStockAdjustment (int itemId, int warehouseId, Date date) {
		PageSetting ps = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		return saDao.getWithdrawnStockAdjustments(itemId, warehouseId, date, ps);
	}

	/**
	 * Generate the datasource of the Stock Adjustment Register report.
	 * @param companyId The id of the company.
	 * @param warehouseId The id of the warehouse.
	 * @param adjustmentTypeId The id of the adjustment type.
	 * @param strDateFrom Start date of the date range.
	 * @param strDateTo End date of the date range.
	 * @return The generated datasource.
	 */
	public JRDataSource generateSAdjustmentRegisterDS(Integer companyId,
			Integer warehouseId, Integer adjustmentTypeId, String strDateFrom, String strDateTo) {
		EBJRServiceHandler<StockAdjustmentDto> saRegisterHandler = new StockAdjustmentJRHandler(saItemDao,
				companyId, warehouseId, adjustmentTypeId, strDateFrom, strDateTo, itemBagQuantityService);
		return new EBDataSource<StockAdjustmentDto>(saRegisterHandler);
	}

	private static class StockAdjustmentJRHandler implements EBJRServiceHandler<StockAdjustmentDto> {
		private static Logger logger = Logger.getLogger(StockAdjustmentJRHandler.class);
		private final StockAdjustmentItemDao saItemDao;
		private final Integer companyId;
		private final Integer warehouseId;
		private final Integer adjustmentTypeId;
		private final Date dateFrom;
		private final Date dateTo;
		private Page<StockAdjustmentDto> currentPage;
		private final ItemBagQuantityService itemBagQuantityService;

		private StockAdjustmentJRHandler(StockAdjustmentItemDao sAdjustmentItemDao, Integer companyId,
				Integer warehouseId, Integer adjustmentTypeId, String strDateFrom, String strDateTo,
				ItemBagQuantityService itemBagQuantityService) {
			this.saItemDao = sAdjustmentItemDao;
			this.companyId = companyId;
			this.warehouseId = warehouseId;
			this.adjustmentTypeId = adjustmentTypeId;
			this.dateFrom = DateUtil.parseDate(strDateFrom);
			this.dateTo = DateUtil.parseDate(strDateTo);
			this.itemBagQuantityService = itemBagQuantityService;
		}

		@Override
		public void close() throws IOException {
			currentPage = null;
		}

		@Override
		public Page<StockAdjustmentDto> nextPage(PageSetting pageSetting) {
			Page<StockAdjustmentItem> sARegisterData = saItemDao.getStockAdjustmentRegisterData(companyId,
					warehouseId, adjustmentTypeId, dateFrom, dateTo, pageSetting);
			List<StockAdjustmentDto> currentData = new ArrayList<StockAdjustmentDto>();

			if(sARegisterData.getData().isEmpty()) {
				logger.info("No transactions found for Stock Adjustment Register.");
				return new Page<StockAdjustmentDto>(pageSetting, currentData, sARegisterData.getTotalRecords());
			}
			logger.debug("Successfully retrieved "+sARegisterData.getData().size()+" Stock Adjustment Items.");
			logger.debug("Processing the retrieved Stock Adjustment Items.");
			StockAdjustment sa = null;
			Item item = null;
			Double amount = null;

			for (StockAdjustmentItem sai : sARegisterData.getData()) {
				sa = sai.getStockAdjustment();
				item = sai.getItem();
				if(sai.getUnitCost() == null) {
					logger.warn("Unit Cost of item: "+sai.getItemId()+" for Stock Adjustment: "
							+sai.getId()+" is null. Setting the unit cost to zero.");
					sai.setUnitCost(0.0);
				}
				amount = NumberFormatUtil.multiplyWFP(sai.getQuantity(), sai.getUnitCost());
				if (sai.getEbObjectId() != null) {
					itemBagQuantityService.setItemBagQty(sai, sai.getEbObjectId(), ItemBagQuantity.SAI_BAG_QTY);
				}

				currentData.add(StockAdjustmentDto.getInstanceOf(sa.getId(),sa.getDivision().getName(), sa.getSaDate(),
						sa.getFormattedSANumber(), item.getStockCode(), item.getDescription(), sa.getBmsNumber(),
						sa.getRemarks(), item.getUnitMeasurement().getName(),
						sai.getQuantity(), sai.getItemBagQuantity(), sai.getUnitCost(), amount));
				}

			currentPage = new Page<StockAdjustmentDto>(pageSetting, currentData, sARegisterData.getTotalRecords());
			return currentPage;
		}
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return saDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		String formName = "";

		StockAdjustment stockAdjustment = saDao.getByEbObjectId(ebObjectId);
		Integer pId = stockAdjustment.getId();
		int classificationTypeId = stockAdjustment.getStockAdjustmentClassificationId();

		switch (classificationTypeId) {
			case StockAdjustmentClassification.STOCK_IN:
				formName = "Stock Adjusment IN";
				break;
			case StockAdjustmentClassification.STOCK_OUT:
				formName = "Stock Adjusment OUT";
				break;
			case StockAdjustmentClassification.STOCK_IN_IS:
				formName = "Stock Adjusment IN - IS";
				break;
			case StockAdjustmentClassification.STOCK_IN_OUT:
				formName = "Stock Adjusment OUT - IS";
				break;
			case StockAdjustmentClassification.STOCK_IN_CENTRAL:
				formName = "Stock Adjustment - IN - Central";
				break;
			case StockAdjustmentClassification.STOCK_IN_NSB3:
				formName = "Stock Adjustment - IN - NSB 3";
				break;
			case StockAdjustmentClassification.STOCK_IN_NSB4:
				formName = "Stock Adjustment - IN - NSB 4";
				break;
			case StockAdjustmentClassification.STOCK_IN_NSB5:
				formName = "Stock Adjustment - IN - NSB 5";
				break;
			case StockAdjustmentClassification.STOCK_IN_NSB8:
				formName = "Stock Adjustment - IN - NSB 8";
				break;
			case StockAdjustmentClassification.STOCK_IN_NSB8A:
				formName = "Stock Adjustment - IN - NSB 8A";
				break;
			case StockAdjustmentClassification.STOCK_OUT_CENTRAL:
				formName = "Stock Adjustment - OUT - Central";
				break;
			case StockAdjustmentClassification.STOCK_OUT_NSB3:
				formName = "Stock Adjustment - OUT - NSB 3";
				break;
			case StockAdjustmentClassification.STOCK_OUT_NSB4:
				formName = "Stock Adjustment - OUT - NSB 4";
				break;
			case StockAdjustmentClassification.STOCK_OUT_NSB5:
				formName = "Stock Adjustment - OUT - NSB 5";
				break;
			case StockAdjustmentClassification.STOCK_OUT_NSB8:
				formName = "Stock Adjustment - OUT - NSB 8";
				break;
			case StockAdjustmentClassification.STOCK_OUT_NSB8A:
				formName = "Stock Adjustment - OUT - NSB 8A";
				break;
		}

		FormProperty property = workflowHandler.getProperty(stockAdjustment.getClass().getSimpleName() +
				stockAdjustment.getStockAdjustmentClassificationId(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = stockAdjustment.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName + " - " + stockAdjustment.getSaNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + stockAdjustment.getCompany().getName())
				.append(" " + stockAdjustment.getWarehouse().getName())
				.append("<b> DATE : </b>" + DateUtil.formatDate(stockAdjustment.getSaDate()))
				.append("<b> ADJUSTMENT TYPE : </b>" + stockAdjustment.getAdjustmentType().getName());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case 10:
			return saDao.getByEbObjectId(ebObjectId);
		case 11:
		case 16:
			return saItemDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the list of stock adjustment items for both serial and non serial.
	 * @param companyId The id of the Company.
	 * @param warehouseId the id of the warehouse.
	 * @param stockAdjustmentTypeId The id of the Stock Adjustment Type.
	 * @param strDateFrom The start date.
	 * @param strDateTo the end date.
	 * @return The the list of stock adjustment items both serial and non serial.
	 */
	public List<StockAdjustmentDto> getStockAdjustmentRegisterData(Integer companyId, Integer warehouseId, Integer divisionId,
			Integer stockAdjustmentTypeId, String strDateFrom, String strDateTo, Integer formStatusId) {
		return saiDao.getStockAdjustmentRegisterData(companyId, warehouseId, divisionId, stockAdjustmentTypeId,
				DateUtil.parseDate(strDateFrom), DateUtil.parseDate(strDateTo), formStatusId);
	}
}
