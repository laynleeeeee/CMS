package eulap.eb.service.processing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.common.util.ListProcessorUtil;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.PrMainProductDao;
import eulap.eb.dao.PrRawMaterialsItemDao;
import eulap.eb.dao.ProcessingReportDao;
import eulap.eb.dao.ProcessingReportTypeDao;
import eulap.eb.dao.ProductLineDao;
import eulap.eb.dao.ProductLineItemDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.PrMainProduct;
import eulap.eb.domain.hibernate.PrRawMaterialsItem;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.domain.hibernate.ProcessingReportType;
import eulap.eb.domain.hibernate.ProductLine;
import eulap.eb.domain.hibernate.ProductLineItem;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.ItemService;
import eulap.eb.service.PRItemUtil;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.inventory.FormUnitCosthandler;
import eulap.eb.service.inventory.RItemCostUpdateService;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ItemTransaction;
import eulap.eb.web.dto.PrItemDto;
import eulap.eb.web.dto.PrItemRawMatDto;
import eulap.eb.web.processing.dto.ProcessingReportPrintout;

/**
 * Class that handles business logic of {@link ProcessingReport}

 */

@Service
public class ProductionReportService extends BaseWorkflowService implements FormUnitCosthandler {
	private static Logger logger = Logger.getLogger(ProductionReportService.class);
	@Autowired
	private ProcessingReportDao processingReportDao;
	@Autowired
	private PrRawMaterialsItemDao prRawMaterialsItemDao;
	@Autowired
	private PrMainProductDao prMainProductDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private UnitMeasurementDao unitMeasurementDao;
	@Autowired
	private ProcessingReportTypeDao processingTypeDao;
	@Autowired
	private ProductLineDao productLineDao;
	@Autowired
	private ProductLineItemDao productLineItemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ProcessingReportService processingReportService;

	/**
	 * Get the processing report object.
	 * @param processingReportId The object id.
	 * @param isMainObjectOnly True to get the main/header object only while false
	 * to get all the related objects. 
	 * @return The processing report object.
	 */
	public ProcessingReport getProcessingReport(int processingReportId, boolean isMainObjectOnly) {
		return getProcessingReport(processingReportId, isMainObjectOnly, true);
	}

	public ProcessingReport getProcessingReport(int processingReportId, boolean isMainObjectOnly,
			boolean isSummarizeRawMaterials) {
		ProcessingReport processingReport = processingReportDao.get(processingReportId);
		if (isMainObjectOnly) {
			return processingReport;
		}
		processingReport.setPrRawMaterialsItems(getPrRawMaterials(processingReportId, isSummarizeRawMaterials));
		processingReport.setPrMainProducts(getPrMainProducts(processingReportId));
		return processingReport;
	}

	private List<PrRawMaterialsItem> getPrRawMaterials(int processingReportId, boolean isSummarized) {
		List<PrRawMaterialsItem> prRawMaterials = prRawMaterialsItemDao.getAllByRefId("processingReportId", processingReportId);
		Item item = null;
		for (PrRawMaterialsItem rawMaterial : prRawMaterials) {
			int itemId = rawMaterial.getItemId();
			item = itemDao.get(itemId);
			rawMaterial.setItem(item);
			rawMaterial.setStockCode(item.getStockCode());
			item = null;
			rawMaterial.setOrigQty(rawMaterial.getQuantity());
			rawMaterial.setAvailableStocks(itemService.getItemExistingStocks(itemId, rawMaterial.getWarehouseId()));
		}
		if (isSummarized) {
			PRItemUtil<PrRawMaterialsItem> prItemUtil = new PRItemUtil<PrRawMaterialsItem>();
			return prItemUtil.getSummarizedISItems(prRawMaterials);
		}
		return prRawMaterials;
	}

	private List<PrMainProduct> getPrMainProducts(int processingReportId) {
		List<PrMainProduct> prMainProducts = prMainProductDao.getAllByRefId("processingReportId", processingReportId);
		Item item = null;
		for (PrMainProduct mp : prMainProducts) {
			int itemId = mp.getItemId();
			item = itemDao.get(itemId);
			mp.setItem(item);
			mp.setStockCode(item.getStockCode());
			item = null;
			mp.setAvailableStocks(itemService.getItemExistingStocks(itemId, mp.getWarehouseId()));
		}
		return prMainProducts;
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		ProcessingReport processingReport = (ProcessingReport) form;
		int typeId = processingReport.getProcessingReportTypeId();
		// Retrieve the saved raw materials and delete before allocating raw materials.
		List<PrRawMaterialsItem> savedRMItems =
				prRawMaterialsItemDao.getAllByRefId("processingReportId", processingReport.getId());
		if (savedRMItems != null && !savedRMItems.isEmpty()) {
			for (PrRawMaterialsItem rmi : savedRMItems) {
				prRawMaterialsItemDao.delete(rmi);
			}
		}
		savedRMItems = null;

		List<PrRawMaterialsItem> prRawMaterialsItems = getAndAllocateRMIUnitCost(typeId,
				processingReport.getDate(), processingReport.getPrRawMaterialsItems());
		processingReport.setPrRawMaterialsItems(prRawMaterialsItems);
		prRawMaterialsItems = null;

		// Compute unit cost of the Main Products.
		if (typeId == ProcessingReportType.PRODUCTION) {
			// Compute raw materials based from the Product Line configuration.
			computeMainProdUC(processingReport);
		} else {
			// Get the average unit cost for all raw materials used.
			double unitCost = computeMPUnitCost(processingReport);
			for (PrMainProduct mainProd : processingReport.getPrMainProducts()) {
				mainProd.setUnitCost(unitCost);
			}
		}
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		
	}

	/**
	 * Compute the unit cost of the main product by the amount of the raw materials.
	 * Computation is based from the product line configuration.
	 */
	private void computeMainProdUC(ProcessingReport processingReport) {
		List<PrMainProduct> mainProducts = processingReport.getPrMainProducts();
		List<PrRawMaterialsItem> rawMaterials = processingReport.getPrRawMaterialsItems();
		ProductLine productLine = null;
		List<ProductLineItem> plItems = null;
		for (PrMainProduct mainProd : mainProducts) {
			logger.info("Computing unit cost for main product -- "+mainProd.getStockCode()+" || Quantity -- "+mainProd.getQuantity());
			productLine = productLineDao.getByItem(mainProd.getItemId());
			plItems = productLineItemDao.getAllByRefId("productLineId", productLine.getId());
			double totalAmt = 0;
			for (ProductLineItem productLineItem : plItems) {
				double qtyPerItem = 0;
				double amtPerItem = 0;
				double rmCost = 0;
				for (PrRawMaterialsItem rawMaterial : rawMaterials) {
					// If the item id from Product Line Item and Raw Materials are the same,
					// Compute total quantity and amount to get the unit cost of the main product.
					if (productLineItem.getItemId().intValue() == rawMaterial.getItemId().intValue()) {
						rmCost = rawMaterial.getUnitCost() != null ? rawMaterial.getUnitCost() : 0.0;
						// Compute the total amount and the quantity of the current raw material to get the average cost.
						qtyPerItem += rawMaterial.getQuantity();
						amtPerItem += (rawMaterial.getQuantity() * rmCost);
					}
				} // End of Raw Materials loop

				// If split price, get the average unit cost of the current raw material.
				double aveUnitCost = 0;
				if (qtyPerItem != 0) {
					aveUnitCost = amtPerItem / qtyPerItem;
				}
				logger.info("Average unit cost : "+aveUnitCost);

				// Compute the cost of the raw material
				// Total of the raw materials to be used multiplied to the average unit cost computed of the raw material.
				double totalRmQty = productLineItem.getQuantity() * mainProd.getQuantity();
				double rmAmount = totalRmQty * aveUnitCost;
				logger.info("Raw Material consumption >>> Quantity : "+totalRmQty+" | Amount : "+rmAmount
						+" | Item : "+productLineItem.getItem().getStockCodeAndDesc());
				totalAmt += rmAmount;
			}

			// Compute the unit cost of the main product.
			// Divide the total cost of the raw materials to the quantity of the main product.
			double computedUC = totalAmt / mainProd.getQuantity();
			mainProd.setUnitCost(computedUC);
			logger.info("Computed unit cost for item "+productLine.getMainItem().getStockCodeAndDesc()+" is "+computedUC);
		}
	}

	/**
	 * Saved the processing report main object and the associated objects.
	 * @param form The object to be saved in the database.
	 * @param user The logged user.
	 */
	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		logger.info("Saving processing report.");
		ProcessingReport processingReport = (ProcessingReport) form;
		int processingReportId =  processingReport.getId();
		boolean isNew = processingReportId == 0;
		AuditUtil.addAudit(processingReport, new Audit(user.getId(), isNew, new Date ()));
		if (isNew) {
			int seqNo = processingReportDao.generateSeqNo(processingReport.getProcessingReportTypeId(), processingReport.getCompanyId());
			logger.info("Successufully generated sequence no: "+seqNo+" for processing type: "+processingReport.getProcessingReportTypeId());
			processingReport.setSequenceNo(seqNo);
		} else {
			ProcessingReport savedPR = processingReportDao.get(processingReportId);
			DateUtil.setCreatedDate(processingReport, savedPR.getCreatedDate());
		}
		logger.info("Saving the processing report header/main object.");
		processingReportDao.saveOrUpdate(processingReport);
		logger.info("Done saving the processing report header/main object.");

		logger.info("Saving the processing report associated objects.");
		saveRMItems(processingReport);
		saveMainProducts(processingReport);
		logger.info("Done saving the processing report associated objects.");
		logger.info("Done saving processing report.");
	}

	private void saveRMItems (ProcessingReport processingReport){
		logger.info("Saving save raw material items");
		int processingReportId = processingReport.getId();
		// Save the items.
		List<PrRawMaterialsItem> rawMaterialsItems = processingReport.getPrRawMaterialsItems();
		if (rawMaterialsItems != null && !rawMaterialsItems.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<Domain>();
			for (PrRawMaterialsItem rmi : rawMaterialsItems) {
				rmi.setId(0);
				rmi.setProcessingReportId(processingReportId);
				SaleItemUtil.setNullUnitCostToZero(rmi);
				toBeSaved.add(rmi);
			}
			prRawMaterialsItemDao.batchSave(toBeSaved);
			toBeSaved = null;
		}
		rawMaterialsItems = null;
		logger.info("Sucessfully saved raw material items.");
	}


	private void saveMainProducts (ProcessingReport processingReport) {
		logger.info("Saving main products.");
		int processingReportId = processingReport.getId();
		// Retrieve the saved items and delete. 
		List<PrMainProduct> savedMProducts = 
				prMainProductDao.getAllByRefId("processingReportId", processingReportId);
		if (savedMProducts != null && !savedMProducts.isEmpty()) {
			for (PrMainProduct mp : savedMProducts) {
				prMainProductDao.delete(mp);
			}
		}
		savedMProducts = null;

		// Save the items.
		List<PrMainProduct> mainProducts = processingReport.getPrMainProducts();
		if (mainProducts != null && !mainProducts.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<Domain>();
			for (PrMainProduct mp : mainProducts) {
				mp.setId(0);
				mp.setProcessingReportId(processingReportId);
				toBeSaved.add(mp);
			}
			prMainProductDao.batchSave(toBeSaved);
			toBeSaved = null;
		}
		mainProducts = null;
		logger.info("Sucessfully saved main products.");
	}

	private List<PrRawMaterialsItem> getAndAllocateRMIUnitCost(int typeId, Date prDate, List<PrRawMaterialsItem> prRawMaterialsItems) {
		Map<AllocatorKey, WeightedAveItemAllocator<PrRawMaterialsItem>> itemId2CostAllocator =
				new HashMap<AllocatorKey, WeightedAveItemAllocator<PrRawMaterialsItem>>();
		AllocatorKey key = null;
		List<PrRawMaterialsItem> processedItems = new ArrayList<PrRawMaterialsItem>();
		for (PrRawMaterialsItem rm : prRawMaterialsItems) {
			WeightedAveItemAllocator<PrRawMaterialsItem> itemAllocator = itemId2CostAllocator.get(rm.getItemId());
			if (itemAllocator == null) {
				itemAllocator = new WeightedAveItemAllocator<PrRawMaterialsItem>(itemDao, itemService,
						rm.getItemId(), rm.getWarehouseId(), prDate);
				key = AllocatorKey.getInstanceOf(rm.getItemId(), rm.getWarehouseId());
				itemId2CostAllocator.put(key, itemAllocator);
			}
			try {
				List<PrRawMaterialsItem> allocatedRMs = itemAllocator.allocateCost(rm);
				for (PrRawMaterialsItem rmi : allocatedRMs) {
					SaleItemUtil.setNullUnitCostToZero(rmi);
					processedItems.add(rmi);
				}
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		return processedItems;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return processingReportDao.get(id).getFormWorkflow();
	}

	/**
	 * Compute the total main product unit cost.
	 * @param processingReport The processing report object.
	 * @return The computed unit cost.
	 */
	public double computeMPUnitCost (ProcessingReport processingReport) {
		double totalAmount = 0;
		double totalMPQty = 0;
		List<PrRawMaterialsItem> prRawMaterialsItems = processingReport.getPrRawMaterialsItems();
		List<PrMainProduct> prMainProducts = processingReport.getPrMainProducts();
		// Add all PR raw material item unit cost to the total amount.
		if (prRawMaterialsItems != null && !prRawMaterialsItems.isEmpty()) {
			for (PrRawMaterialsItem rmi : prRawMaterialsItems) {
				totalAmount += ((rmi.getUnitCost() != null ? rmi.getUnitCost() : 0) * rmi.getQuantity());
			}
		}
		// Add all main product quantity.
		if (prMainProducts != null && !prMainProducts.isEmpty()) {
			for (PrMainProduct  mp : prMainProducts) {
				totalMPQty += mp.getQuantity();
			}
		}
		return totalAmount / totalMPQty;
	}

	public List<PrMainProduct> getMainProductsPrintout (int processingReportId) {
		List<PrMainProduct> prMainProducts = 
				prMainProductDao.getAllByRefId("processingReportId", processingReportId);
		if (prMainProducts != null && !prMainProducts.isEmpty()) {
			for (PrMainProduct prmp : prMainProducts) {
				Item item = itemDao.get(prmp.getItemId());
				UnitMeasurement um = unitMeasurementDao.get(item.getUnitMeasurementId());
				Warehouse wh = warehouseService.getWarehouse(prmp.getWarehouseId());
				item.setUnitMeasurement(um);
				prmp.setItem(item);
				prmp.setWarehouse(wh);
			}
		}
		return prMainProducts;
	}

	public List<ProcessingReportPrintout> getPRPrintout (ProcessingReport pr) {
		int processingReportId = pr.getId();
		List<ProcessingReportPrintout> ret = new ArrayList<ProcessingReportPrintout>();
		ProcessingReportPrintout printout = new ProcessingReportPrintout();
		printout.setPrRawMaterials(getPrRawMaterials(processingReportId, true));
		printout.setPrMainProducts(getMainProductsPrintout(processingReportId));
		ret.add(printout);
		return ret;
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return processingReportDao.getByEbObjectId(ebObjectId);
	}

	/**
	 * Get the Processing Type object using its unique id.
	 * @param processingTypeId The id of the processing report type.
	 * @return The {@link ProcessingReportType}.
	 */
	public ProcessingReportType getProcessingType(int processingTypeId) {
		return processingTypeDao.get(processingTypeId);
	}

	/**
	 * Get the form type name
	 * @param processingTypeId The processing report type id
	 * @return The form type name
	 */
	public String getFormTypeName(int processingTypeId) {
		return getProcessingType(processingTypeId).getName();
	}

	/**
	 * Initialize the raw materials given the main product and the 
	 * associating product lines for Mixing Report.
	 * @param mainProducts The list of main products.
	 * @return The list of raw material items for mixing report.
	 */
	public List<PrRawMaterialsItem> initMixingReportRawMaterials (ProcessingReport processingReport) {
		List<PrMainProduct> mainProducts = processingReport.getPrMainProducts();
		List<PrRawMaterialsItem> prRawMaterialsItems = new ArrayList<>();
		Map<Integer, ProductLineItem> hmPrRMaterialItems = new HashMap<Integer, ProductLineItem>();
		if (mainProducts != null && !mainProducts.isEmpty()) {
			List<ProductLineItem> productLineItems = null;
			for (PrMainProduct mp : mainProducts) {
				if (mp.getQuantity() == null || (mp.getQuantity().compareTo(0.0) <= 0)) {
					// No need to initialize raw materials if the quantity
					// of the main product is less than or equal to zero.
					continue;
				}
				Item mpItem = itemDao.getItemByStockCode(mp.getStockCode(), null);
				if (mpItem != null && mp.getWarehouseId() != null) {
					int warehouseId = mp.getWarehouseId();
					ProductLine pl = productLineDao.getByItem(mpItem.getId());
					productLineItems = productLineItemDao.getAllByRefId("productLineId", pl.getId());
					if (productLineItems != null && !productLineItems.isEmpty()) {
						for (ProductLineItem pli : productLineItems) {
							double quantity = (mp.getQuantity() != null ? mp.getQuantity() : 0) * pli.getQuantity();
							pli.setQuantity(quantity);
							pli.setWarehouseId(warehouseId);
							// Group product lines with same item id but sum up the quantity.
							if (hmPrRMaterialItems.containsKey(pli.getItemId())) {
								ProductLineItem hmPli = hmPrRMaterialItems.get(pli.getItemId());
								hmPli.setQuantity(hmPli.getQuantity() + quantity);
								hmPrRMaterialItems.put(hmPli.getItemId(), hmPli);
							} else {
								hmPrRMaterialItems.put(pli.getItemId(), pli);
							}
						}
					}
					// Free the used product lines.
					productLineItems = null;
				}
			}
			// Free the used main products.
			mainProducts = null;
		}

		List<ProductLineItem> productLineItems = new ArrayList<>(hmPrRMaterialItems.values());
		// Free the used hash map raw materials.
		hmPrRMaterialItems = null;
		List<PrRawMaterialsItem> prRawMaterials = prRawMaterialsItemDao.getAllByRefId("processingReportId", processingReport.getId());
		if (!productLineItems.isEmpty()) {
			for (ProductLineItem pli : productLineItems) {
				double originalQty = 0;
				for (PrRawMaterialsItem rm : prRawMaterials) {
					if (rm.getItemId().equals(pli.getItemId())) {
						// No break, for the split price.
						originalQty = rm.getQuantity();
					}
				}
				prRawMaterialsItems.add(PrRawMaterialsItem.getInstanceOf(pli, pli.getItem(), originalQty));
			}
		}
		return prRawMaterialsItems;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		ProcessingReport processingReport = processingReportDao.getByWorkflowId(
				currentWorkflowLog.getFormWorkflowId());
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID
				&& processingReport.getFormWorkflow().isComplete()) {
			String errorMessage = null;
			//PR Main Product
			List<PrMainProduct> mainProducts =
					prMainProductDao.getAllByRefId("processingReportId", processingReport.getId());

			if(!mainProducts.isEmpty()) {
				errorMessage = ValidationUtil.validateToBeCancelledRefForm(itemService, mainProducts);
			}

			if(errorMessage != null) {
				bindingResult.reject("workflowMessage", errorMessage);
				currentWorkflowLog.setWorkflowMessage(errorMessage);
			}
		}
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return processingReportDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		String formName = "";

		ProcessingReport processingReport = processingReportDao.getByEbObjectId(ebObjectId);
		Integer pId = processingReport.getId();
		int typeId = processingReport.getProcessingReportTypeId();
		formName = typeId == ProcessingReportType.PRODUCTION ? "Production Report" : "Processing Report";

		FormProperty property = workflowHandler.getProperty(processingReport.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = processingReport.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName + " - " + processingReport.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + processingReport.getCompany().getNumberAndName())
				.append(" " + processingReport.getRefNumber())
				.append("<b> DATE : </b>" + DateUtil.formatDate(processingReport.getDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case ProcessingReport.PR_WIP_BAKING_OBJECT_TYPE_ID:
		case ProcessingReport.PR_PRODUCTION_OBJECT_TYPE_ID:
			return processingReportDao.getByEbObjectId(ebObjectId);
		case PrRawMaterialsItem.OBJECT_TYPE_ID:
			return prRawMaterialsItemDao.getByEbObjectId(ebObjectId);
		case PrMainProduct.OBJECT_TYPE_ID:
			return prMainProductDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	@Override
	public void updateUnitCost(RItemCostUpdateService costUpdateService,
			WeightedAveItemAllocator<ItemTransaction> fifoAllocator, ItemTransaction it, int itemId, int warehouseId,
			Date formDate, boolean isAllocateRpTo) {
		// Do nothing
	}

	@Override
	public void processAllocatedItem(int itemId, int warehouseId, Queue<ItemTransaction> allocatedItems,
			ItemTransaction currentAllocItem) throws CloneNotSupportedException {
		int pId = currentAllocItem.getId();
		List<PrRawMaterialsItem> rawMaterialsItems = prRawMaterialsItemDao.getAllPrRawMaterialItems(pId, itemId);
		ListProcessorUtil<PrRawMaterialsItem> remover = new ListProcessorUtil<PrRawMaterialsItem>();
		List<Integer> formIds = remover.collectFormIds(rawMaterialsItems);
		List<PrRawMaterialsItem> processedItems = summarizeRawMaterials(rawMaterialsItems);
		Double allocQty = currentAllocItem.getQuantity();
		Double qtyToBeWithdrawn = null;
		PrRawMaterialsItem splitItem = null;
		List<Integer> savedFormIds = new ArrayList<Integer>();
		for (PrRawMaterialsItem rrm : processedItems) {
			while (currentAllocItem != null) {
				if (qtyToBeWithdrawn == null) {
					qtyToBeWithdrawn = rrm.getQuantity();
				}
				if (allocQty >= qtyToBeWithdrawn) {
					rrm.setUnitCost(currentAllocItem.getUnitCost());
					rrm.setQuantity(qtyToBeWithdrawn);
					// update raw material
					prRawMaterialsItemDao.saveOrUpdate(rrm);
					savedFormIds.add(rrm.getId());
					allocQty = NumberFormatUtil.roundOffNumber((allocQty - qtyToBeWithdrawn),
							NumberFormatUtil.SIX_DECIMAL_PLACES);
					if (allocQty == 0.0) {
						currentAllocItem = getNextAllocItem(allocatedItems);
						allocQty = getAllocatedQty(currentAllocItem);
					}
					qtyToBeWithdrawn = null;
					break;
				} else {
					if (allocQty > 0) {
						splitItem = (PrRawMaterialsItem) rrm.clone();
						splitItem.setId(0);
						splitItem.setQuantity(allocQty);
						splitItem.setUnitCost(currentAllocItem.getUnitCost());
						// save raw material
						prRawMaterialsItemDao.saveOrUpdate(splitItem);
						qtyToBeWithdrawn = qtyToBeWithdrawn - allocQty;
						currentAllocItem = getNextAllocItem(allocatedItems);
						allocQty = getAllocatedQty(currentAllocItem);
					}
				}
			}
		}

		List<Domain> toBeDeleted = new ArrayList<Domain>();
		int frequency = 0;
		for (Integer id : formIds) {
			//Delete the items that were not updated.
			frequency = Collections.frequency(savedFormIds, id);
			if (frequency == 0) {
				toBeDeleted.add(prRawMaterialsItemDao.get(id));
			}
		}

		if (!toBeDeleted.isEmpty()) {
			for (Domain tbd : toBeDeleted) {
				prRawMaterialsItemDao.delete(tbd);
			}
		}

		rawMaterialsItems = null;
		remover = null;
		processedItems = null;
		toBeDeleted = null;
		formIds = null;
		savedFormIds = null;

		// Recalculate main product unit cost
		ProcessingReport pr = getProcessingReport(pId, false, false);
		boolean isMenuProduct = pr.getProcessingReportTypeId().equals(ProcessingReportType.PRODUCTION);
		double unitCost = 0;
		if (isMenuProduct) {
			// Compute raw materials based from the Product Line configuration.
			computeMainProdUC(pr);
		} else {
			unitCost = computeMPUnitCost(pr);
		}
		for (PrMainProduct mp : pr.getPrMainProducts()) {
			if (!isMenuProduct) {
				mp.setUnitCost(unitCost);
			}
			prMainProductDao.update(mp);
		}
	}

	private ItemTransaction getNextAllocItem(Queue<ItemTransaction> allocatedItems) {
		return allocatedItems.poll();
	}

	private double getAllocatedQty(ItemTransaction currentAllocItem) {
		if (currentAllocItem != null) {
			return currentAllocItem.getQuantity();
		}
		return 0;
	}

	private List<PrRawMaterialsItem> summarizeRawMaterials(List<PrRawMaterialsItem> repackingRawMaterials) {
		List<PrRawMaterialsItem> updatedItems = new ArrayList<PrRawMaterialsItem>();
		Map<String, PrRawMaterialsItem> rmHm = new HashMap<String, PrRawMaterialsItem>();

		PrRawMaterialsItem editedItem = null;
		String itemKey = null;
		for (PrRawMaterialsItem rrm : repackingRawMaterials) {
			itemKey = "i" + rrm.getItemId();
			if(rmHm.containsKey(itemKey)) {
				editedItem = processEditedItem(rrm, rmHm.get(itemKey));
				rmHm.put(itemKey, editedItem);
			} else {
				rmHm.put(itemKey, rrm);
			}
		}

		for (Map.Entry<String, PrRawMaterialsItem> iHM : rmHm.entrySet()) {
			updatedItems.add(iHM.getValue());
		}

		rmHm = null;
		editedItem = null;

		Collections.sort(updatedItems, new Comparator<PrRawMaterialsItem>() {
			@Override
			public int compare(PrRawMaterialsItem rrm1, PrRawMaterialsItem rrm2) {
				if (rrm1.getId() < rrm2.getId()) {
					return -1;
				} else if (rrm1.getId() > rrm2.getId()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return updatedItems;
	}

	private PrRawMaterialsItem processEditedItem(PrRawMaterialsItem rrm, PrRawMaterialsItem editedItem) {
		editedItem.setQuantity(rrm.getQuantity() + editedItem.getQuantity());
		editedItem.setOrigQty((rrm.getOrigQty() != null ? rrm.getOrigQty() : 0)
				+ (editedItem.getOrigQty() != null ? editedItem.getOrigQty() : 0));
		return editedItem;
	}

	@Override
	public String getFormLabel() {
		return "PR";
	}

	/**
	 * Process processing report items
	 * @param processingReport The processing report object
	 */
	public void processItems(ProcessingReport processingReport) {
		if (processingReport.getProcessingReportTypeId() == ProcessingReportType.PRODUCTION) {
			processingReport.setPrRawMaterialsItems(initMixingReportRawMaterials(processingReport));
		}
		processingReportService.processPRItems(processingReport, true);
	}

	/**
	 * Get the list of processing report form based on the selected criteria
	 * @param typeId The processing report type id
	 * @param criteria The search criteria
	 * @return
	 */
	public List<FormSearchResult> searchPrForms(int typeId, String criteria) {
		return processingReportService.searchProcessingReports(typeId, criteria);
	}

	/**
	 * Process main product and raw materials data.
	 * Compute total raw materials used for each main product.
	 * @param prMainProducts The list of {@link PrMainProduct}.
	 * @return The {@link PrItemDto} object.
	 */
	public List<PrItemDto> getPrItemDto(ProcessingReport processingReport) {
		int typeId = processingReport.getProcessingReportTypeId();
		PrItemDto dto = null;
		List<ProductLineItem> plItems = null;
		PrItemRawMatDto rawMat = null;
		List<PrItemRawMatDto> rawMats = null;
		List<PrItemDto> dtos = new ArrayList<PrItemDto>();
		if (typeId == ProcessingReportType.PRODUCTION) {
			for (PrMainProduct prMainProduct : processingReport.getPrMainProducts()) {
				dto = new PrItemDto();
				plItems = productLineItemDao.getRawMaterials(prMainProduct.getItemId());
				Double qty = prMainProduct.getQuantity();

				//process data for main product.
				dto.setStockCode(prMainProduct.getItem().getStockCode());
				dto.setDescription(prMainProduct.getItem().getDescription());
				dto.setWarehouse(prMainProduct.getWarehouse().getName());
				dto.setQuantity(qty);
				dto.setUom(prMainProduct.getItem().getUnitMeasurement().getName());

				//process data for raw materials.
				rawMats = new ArrayList<PrItemRawMatDto>();
				for(ProductLineItem productLineItems : plItems) {
					rawMat = new PrItemRawMatDto();
					Item item = productLineItems.getItem();
					rawMat.setDescription(item.getDescription());
					rawMat.setStockCode(item.getStockCode());
					rawMat.setUom(item.getUnitMeasurement().getName());
					rawMat.setQuantity(productLineItems.getQuantity() * qty);
					rawMats.add(rawMat);
					item = null; //clear memory.
					rawMat = null;//clear memory.
				}
				dto.setPrRawMatDtos(rawMats);
				dtos.add(dto);
			}
		}
		return dtos;
	}

	/**
	 * Recompute total item costs
	 */
	public void recomputeCost() {
		// Set start date
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.DAY_OF_MONTH, 7);
		calendar.set(Calendar.MONTH, 2);
		calendar.set(Calendar.YEAR, 2021);
		Date startTime = calendar.getTime();
		// Get PR forms between the dates
		List<ProcessingReport> processingReports = processingReportDao.getProcessingReportByTypeId(
				ProcessingReportType.PRODUCTION, startTime, new Date());
		List<PrMainProduct> prMainProducts = null;
		List<ProductLineItem> productLineItems = null;
		int prId = 0;
		List<PrRawMaterialsItem> savedPrRawMaterials = null;
		for (ProcessingReport pr : processingReports) {
			prId = pr.getId();
			prMainProducts = getPrMainProducts(prId);
			// Re-process PR raw materials
			pr.setPrRawMaterialsItems(initPrRawMaterials(prMainProducts, prId));
			savedPrRawMaterials = prRawMaterialsItemDao.getAllByRefId("processingReportId", prId);
			if (savedPrRawMaterials != null && !savedPrRawMaterials.isEmpty()) {
				for (PrRawMaterialsItem rmi : savedPrRawMaterials) {
					prRawMaterialsItemDao.delete(rmi);
				}
				savedPrRawMaterials = null;
			}
			saveRMItems(pr);
			// Re compute main product costs
			for (PrMainProduct mp : prMainProducts) {
				productLineItems = productLineItemDao.getRawMaterials(mp.getItemId());
				mp.setUnitCost(computeCost(pr.getPrRawMaterialsItems(), productLineItems));
				prMainProductDao.update(mp);
				productLineItems = null;
			}
			prMainProducts = null;
		}
	}

	public List<PrRawMaterialsItem> initPrRawMaterials(List<PrMainProduct> mainProducts, int prId) {
		List<PrRawMaterialsItem> prRawMaterialsItems = new ArrayList<PrRawMaterialsItem>();
		Map<Integer, ProductLineItem> hmPrRMaterialItems = new HashMap<Integer, ProductLineItem>();
		if (mainProducts != null && !mainProducts.isEmpty()) {
			List<ProductLineItem> productLineItems = null;
			for (PrMainProduct mp : mainProducts) {
				if (mp.getQuantity() == null || mp.getQuantity() == 0.0) {
					// No need to initialize raw materials if the quantity of the main product is less than or equal to zero.
					continue;
				}
				ProductLine pl = productLineDao.getByItem(mp.getItemId());
				productLineItems = productLineItemDao.getAllByRefId("productLineId", pl.getId());
				if (productLineItems != null && !productLineItems.isEmpty()) {
					for (ProductLineItem pli : productLineItems) {
						double quantity = (mp.getQuantity() != null ? mp.getQuantity() : 0) * pli.getQuantity();
						pli.setQuantity(quantity);
						pli.setWarehouseId(mp.getWarehouseId());
						// Group product lines with same item id but sum up the quantity.
						if (hmPrRMaterialItems.containsKey(pli.getItemId())) {
							ProductLineItem hmPli = hmPrRMaterialItems.get(pli.getItemId());
							hmPli.setQuantity(hmPli.getQuantity() + quantity);
							hmPrRMaterialItems.put(hmPli.getItemId(), hmPli);
						} else {
							hmPrRMaterialItems.put(pli.getItemId(), pli);
						}
					}
				}
				// Free the used product lines.
				productLineItems = null;
			}
		}

		List<ProductLineItem> productLineItems = new ArrayList<>(hmPrRMaterialItems.values());
		if (!productLineItems.isEmpty()) {
			List<PrRawMaterialsItem> savedPrRawMaterial = getPrRawMaterials(prId, true);
			if (!productLineItems.isEmpty()) {
				for (ProductLineItem pli : productLineItems) {
					double unitCost = 0;
					for (PrRawMaterialsItem rm : savedPrRawMaterial) {
						if (rm.getItemId().equals(pli.getItemId())) {
							unitCost = rm.getUnitCost();
						}
					}
					prRawMaterialsItems.add(PrRawMaterialsItem.getInstanceOf(pli, unitCost));
				}
			}
		}
		// Free the used hash map raw materials.
		hmPrRMaterialItems = null;
		productLineItems = null;
		return prRawMaterialsItems;
	}

	private double computeCost(List<PrRawMaterialsItem> rawMaterials, List<ProductLineItem> productLineItems) {
		double computedCost = 0;
		int plItemId = 0;
		List<ProductLineItem> plItems = null;
		for (PrRawMaterialsItem rm : rawMaterials) {
			for (ProductLineItem pli : productLineItems) {
				plItemId = pli.getItemId();
				if (rm.getItemId().intValue() == plItemId) {
					if (productLineDao.getByItem(plItemId) != null) {
						plItems = productLineItemDao.getRawMaterials(plItemId);
						for (ProductLineItem premix : plItems) {
							if (rm.getItemId().equals(premix.getItemId())) {
								computedCost += premix.getQuantity() * rm.getUnitCost();
							}
						}
						plItems = null;
					} else {
						computedCost += pli.getQuantity() * rm.getUnitCost();
					}
				}
			}
		}
		return computedCost;
	}
}