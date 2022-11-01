package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.RepackingDao;
import eulap.eb.dao.RepackingFinishedGoodDao;
import eulap.eb.dao.RepackingItemDao;
import eulap.eb.dao.RepackingRawMaterialDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.domain.hibernate.RepackingFinishedGood;
import eulap.eb.domain.hibernate.RepackingItem;
import eulap.eb.domain.hibernate.RepackingRawMaterial;
import eulap.eb.domain.hibernate.RepackingType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.RItemCostUpdateService;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.RepackingItemDto;

/**
 * Service class for all transactions under Repacking.

 *
 */
@Service
public class RepackingService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(RepackingService.class);
	@Autowired
	private RepackingDao repackingDao;
	@Autowired
	private RepackingItemDao repackingItemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private RepackingRawMaterialDao rpRawMaterialDao;
	@Autowired
	private RepackingFinishedGoodDao rpFinishedGoodDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private RItemCostUpdateService itemCostUpdateService;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return getRepackingObj(id).getFormWorkflow();
	}

	/**
	 * Get the repacking object with existing stocks of the from and to items.
	 * @param repackingId The unique id of repacking.
	 * @param typeId The repacking type id
	 * @return The repacking object.
	 */
	public Repacking getRepacking(int repackingId, int typeId) {
		Repacking repacking = getRepackingObj(repackingId);
		int warehouseId = repacking.getWarehouseId();
		Item fromItem = null;
		Item toItem = null;
		if (typeId == RepackingType.TYPE_REPACKING) {
			for (RepackingItem ri : repacking.getrItems()) {
				logger.info("Getting the existing stocks of 'FROM ITEM'");
				double fromExistingStocks = getItemExistingStocks(ri.getFromItemId(), warehouseId);
				fromItem = itemService.getItem(ri.getFromItemId());
				fromItem.setExistingStocks(fromExistingStocks);
				ri.setFromItem(fromItem);
				ri.setFromStockCode(fromItem.getStockCode());
				ri.setFromExistingStock(fromExistingStocks);
				ri.setFromDescription(fromItem.getDescription());

				logger.info("Getting the existing stocks of 'TO ITEM'");
				double toExistingStocks = getItemExistingStocks(ri.getToItemId(), warehouseId);
				toItem = itemService.getItem(ri.getToItemId());
				toItem.setExistingStocks(toExistingStocks);
				ri.setToItem(toItem);
				ri.setToStockCode(toItem.getStockCode());
				ri.setToExistingStock(toExistingStocks);
				ri.setOrigQty(ri.getQuantity());
				ri.setToDescription(toItem.getDescription());

				ri.setOrigItemId(fromItem.getId());
				ri.setOrigUnitCost(ri.getUnitCost());
				ri.setToOrigQty(ri.getRepackedQuantity());
			}
		} else {
			List<RepackingRawMaterial> savedRawMaterials = rpRawMaterialDao.getAllByRefId("repackingId", repackingId);
			List<RepackingRawMaterial> summarizedRmItems = summarizeRawMaterials(savedRawMaterials);
			Item item = null;
			int itemId;
			for (RepackingRawMaterial rrm : summarizedRmItems) {
				itemId = rrm.getItemId();
				item = itemDao.get(itemId);
				rrm.setItem(item);
				rrm.setStockCode(item.getStockCode());
				rrm.setExistingStocks(getItemExistingStocks(itemId, warehouseId));
				rrm.setOrigQty(rrm.getQuantity());
				// free up object
				item = null;
			}
			repacking.setRepackingRawMaterials(summarizedRmItems);
			savedRawMaterials = null;

			List<RepackingFinishedGood> savedFinishedGoods = rpFinishedGoodDao.getAllByRefId("repackingId", repackingId);
			for (RepackingFinishedGood rfg : savedFinishedGoods) {
				itemId = rfg.getItemId();
				item = itemDao.get(itemId);
				rfg.setItem(item);
				rfg.setStockCode(item.getStockCode());
				rfg.setExistingStocks(getItemExistingStocks(itemId, warehouseId));
				rfg.setOrigQty(rfg.getQuantity());
				// free up object
				item = null;
			}
			repacking.setRepackingFinishedGoods(savedFinishedGoods);
		}
		return repacking;
	}

	/**
	 * Summarize the saved raw materials
	 * @param repackingRawMaterials The repacking raw materials
	 * @return The summarized list of the saved raw materials
	 */
	public List<RepackingRawMaterial> summarizeRawMaterials(List<RepackingRawMaterial> repackingRawMaterials) {
		List<RepackingRawMaterial> updatedItems = new ArrayList<RepackingRawMaterial>();
		Map<String, RepackingRawMaterial> rmHm = new HashMap<String, RepackingRawMaterial>();

		RepackingRawMaterial editedItem = null;
		String itemKey = null;
		for (RepackingRawMaterial rrm : repackingRawMaterials) {
			itemKey = "i" + rrm.getItemId();
			if(rmHm.containsKey(itemKey)) {
				editedItem = processEditedItem(rrm, rmHm.get(itemKey));
				rmHm.put(itemKey, editedItem);
			} else {
				rmHm.put(itemKey, rrm);
			}
		}

		for (Map.Entry<String, RepackingRawMaterial> iHM : rmHm.entrySet()) {
			updatedItems.add(iHM.getValue());
		}

		rmHm = null;
		editedItem = null;

		Collections.sort(updatedItems, new Comparator<RepackingRawMaterial>() {
			@Override
			public int compare(RepackingRawMaterial rrm1, RepackingRawMaterial rrm2) {
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

	private RepackingRawMaterial processEditedItem(RepackingRawMaterial rrm, RepackingRawMaterial editedItem) {
		editedItem.setQuantity(rrm.getQuantity() + editedItem.getQuantity());
		editedItem.setOrigQty((rrm.getOrigQty() != null ? rrm.getOrigQty() : 0)
				+ (editedItem.getOrigQty() != null ? editedItem.getOrigQty() : 0));
		return editedItem;
	}

	private double getItemExistingStocks(int itemId, int warehouseId) {
		return itemService.getItemExistingStocks(itemId, warehouseId, new Date());
	}

	/**
	 * Get the repacking object only.
	 * @param repackingId The unique id of repacking.
	 * @return The repacking object.
	 */
	public Repacking getRepackingObj(int repackingId) {
		return repackingDao.get(repackingId);
	}

	public List<RepackingItem> getRItems(int repackingId) {
		return repackingItemDao.getRepackingItems(repackingId, null);
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		Repacking repacking = (Repacking) form;
		List<Integer> toBeDeletedIds = new ArrayList<Integer>();
		Integer repackingId = repacking.getId();
		boolean isNew = repackingId == 0;
		if (!isNew) {
			logger.debug("Deleting the saved Repacking Items of Repacking id: "+repackingId);
			List<RepackingItem> savedItems = getRItems(repackingId);
			if (savedItems != null && !savedItems.isEmpty()) {
				for (RepackingItem ri : savedItems) {
					toBeDeletedIds.add(ri.getId());
					logger.debug("Deleting the Repacking Item: "+ri.getId());
				}
				repackingItemDao.delete(toBeDeletedIds);
			}
			logger.debug("Successfully deleted the Items of Repacking: "+repackingId);
			logger.trace("Deleted "+toBeDeletedIds.size()+" Repacking Items.");
			toBeDeletedIds = null;
			savedItems = null;
		}

		List<RepackingItem> rItems = repacking.getrItems();
		logger.debug("Saving the Items.");
		logger.trace("Saving "+rItems.size()+" Items.");
		List<RepackingItemDto> toBeSavedRpItems = new ArrayList<RepackingItemDto>();
		Map<AllocatorKey, WeightedAveItemAllocator<RepackingItemDto>> itemId2Allocator = new HashMap<AllocatorKey, WeightedAveItemAllocator<RepackingItemDto>>();
		List<RepackingItemDto> repackingDtos = convertRpItemsToDto(rItems, repacking.getId());
		WeightedAveItemAllocator<RepackingItemDto> allocator = null;
		AllocatorKey key = null;
		logger.debug("Performing the unit cost allocation of Repacking Items.");
		for (RepackingItemDto rpDto : repackingDtos) {
			allocator = itemId2Allocator.get(rpDto.getItemId());
			if (allocator == null) {
				allocator = new WeightedAveItemAllocator<RepackingItemDto>(itemDao, itemService,
						rpDto.getItemId(), repacking.getWarehouseId(), repacking.getrDate());
				key = AllocatorKey.getInstanceOf(rpDto.getItemId(), repacking.getWarehouseId());
				itemId2Allocator.put(key, allocator);
			}

			List<RepackingItemDto> allocatedRpItems = null;
			try {
				allocatedRpItems = allocator.allocateCost(rpDto);
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}

			RepackingItemDto allocatedRpItem = null;
			double totalAmount = 0;
			// If there are split price, total the unit cost and quantity and divide it by the repacked quantity
			for (RepackingItemDto allocItem : allocatedRpItems) {
				logger.debug("Successfully allocated the item id: "+allocItem.getItemId()
						+" with unit cost: "+allocItem.getUnitCost());
				//Set unit cost to 0.0 if null
				allocItem.setUnitCost(allocItem.getUnitCost() != null ? allocItem.getUnitCost() : 0.0);
				if (allocatedRpItem == null) {
					allocatedRpItem = RepackingItemDto.getInstanceOf(allocItem.getId(),
							allocItem.getItemId(), allocItem.getQuantity(), allocItem.getToItemId(),
							allocItem.getRepackedQuantity(), allocItem.getOrigQty(), allocItem.getToOrigQty());
				} else {
					allocatedRpItem.setQuantity(allocatedRpItem.getQuantity() + allocItem.getQuantity());
				}
				totalAmount += NumberFormatUtil.multiplyWFP(allocItem.getQuantity(), allocItem.getUnitCost());
			}

			//Set the average unit cost
			allocatedRpItem.setUnitCost(NumberFormatUtil.divideWFP(totalAmount, allocatedRpItem.getQuantity()));
			logger.debug("Average unit: "+allocatedRpItem.getUnitCost());
			//Set the computed unit cost for repacked item
			allocatedRpItem.setComputedUnitCost(NumberFormatUtil.divideWFP(totalAmount, allocatedRpItem.getRepackedQuantity()));
			logger.debug("Computed unit cost of item "+allocatedRpItem.getComputedUnitCost());
			toBeSavedRpItems.add(allocatedRpItem);
		}
		repacking.setrItems(convert(toBeSavedRpItems));
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		Repacking repacking = (Repacking) form;
		logger.info("Saving the Repacking form");
		Integer repackingId = repacking.getId();
		boolean isNew = repackingId == 0;
		Integer parentObjectId = repacking.getEbObjectId();
		AuditUtil.addAudit(repacking, new Audit(user.getId(), isNew, new Date()));
		if (isNew) {
			Integer rNumber = repackingDao.generateRNumberByTypeId(repacking.getCompanyId(),
					repacking.getRepackingTypeId(), repacking.getDivisionId());
			repacking.setrNumber(rNumber);
		} else {
			Repacking savedRepacking = getRepackingObj(repackingId);
			DateUtil.setCreatedDate(repacking, savedRepacking.getCreatedDate());
		}

		String remarks = repacking.getRemarks();
		if (remarks != null) {
			repacking.setRemarks(repacking.getRemarks().trim());
		}
		repackingDao.saveOrUpdate(repacking);
		logger.info("Successfully saved the Repacking form.");
		logger.debug("Saved the R Number: "+repacking.getFormattedRNumber());
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				repacking.getReferenceDocuments(), true);
		// get the saved repacking id
		repackingId = repacking.getId();
		// Set the Re-pack ID to re-pack line items
		List<Domain> toBeSavedItems = new ArrayList<Domain>();
		for (RepackingItem ri : repacking.getrItems()) {
			ri.setRepackingId(repackingId);
			toBeSavedItems.add(ri);
		}
		repackingItemDao.batchSaveOrUpdate(toBeSavedItems);

		logger.debug("Successfully saved "+repacking.getrItems().size()+" Items.");
		logger.info("Successfully saved Repacking and its Items.");
		// logger.info("======>>> Freeing up memory allocation");
		toBeSavedItems = null;
		repackingId = null;
		remarks = null;
	}

	private List<RepackingItemDto> convertRpItemsToDto(List<RepackingItem> repackingItems, int repackingId) {
		List<RepackingItemDto> repackingDtos = new ArrayList<RepackingItemDto>();
		for (RepackingItem rpi : repackingItems) {
			repackingDtos.add(RepackingItemDto.getInstanceOf(repackingId, rpi.getFromItemId(),
					rpi.getQuantity(), rpi.getToItemId(), rpi.getRepackedQuantity(),
					rpi.getOrigQty(), rpi.getToOrigQty()));
		}
		return repackingDtos;
	}

	private List<RepackingItem> convert(List<RepackingItemDto> rpDtos) {
		List<RepackingItem> rpItems = new ArrayList<>();
		RepackingItem rpItem = null;
		for (RepackingItemDto rpDto : rpDtos) {
			rpItem = new RepackingItem();
			rpItem.setFromItemId(rpDto.getItemId());
			rpItem.setToItemId(rpDto.getToItemId());
			rpItem.setQuantity(rpDto.getQuantity());
			rpItem.setUnitCost(rpDto.getUnitCost());
			rpItem.setFromTotal(NumberFormatUtil.roundOffTo2DecPlaces(rpDto.getQuantity() * rpDto.getUnitCost()));
			double repackedQty = NumberFormatUtil.roundOffTo2DecPlaces(rpDto.getRepackedQuantity());
			rpItem.setRepackedQuantity(repackedQty);
			rpItem.setRepackedUnitCost(rpDto.getComputedUnitCost());
			rpItem.setOrigQty(rpDto.getOrigQty());
			rpItem.setToOrigQty(rpDto.getToOrigQty());
			rpItem.setToTotal(NumberFormatUtil.roundOffTo2DecPlaces(repackedQty * rpDto.getComputedUnitCost()));
			rpItems.add(rpItem);
		}
		return rpItems;
	}

	/**
	 * Removes the duplicates from the list.
	 * @param repackingItems The list of items.
	 * @return List of items without duplicates.
	 */
	public List<RepackingItem> grouprItemsByItemId(List<RepackingItem> repackingItems) {
		if(repackingItems.isEmpty()) {
			return repackingItems;
		}

		Map<Integer, RepackingItem> rpItemHashMap = new HashMap<Integer, RepackingItem>();
		RepackingItem editedRpItem = null;
		for (RepackingItem rpi : repackingItems) {
			double origQty = rpi.getOrigQty() != null ? rpi.getOrigQty() : 0;
			if(rpItemHashMap.containsKey(rpi.getFromItemId())) {
				editedRpItem = rpItemHashMap.get(rpi.getFromItemId());
				editedRpItem.setTotalQty(NumberFormatUtil.roundOffTo2DecPlaces(editedRpItem.getTotalQty()+rpi.getQuantity()-origQty));
				rpItemHashMap.put(rpi.getFromItemId(), editedRpItem);
			} else {
				rpi.setTotalQty(rpi.getQuantity() - origQty);
				rpItemHashMap.put(rpi.getFromItemId(), rpi);
			}
		}

		List<RepackingItem> updatedList = new ArrayList<RepackingItem>();
		for (Map.Entry<Integer, RepackingItem> m : rpItemHashMap.entrySet()) {
			updatedList.add(m.getValue());
		}

		rpItemHashMap = null;
		editedRpItem = null;
		return updatedList;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		Repacking rp = repackingDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			for (RepackingItem rpi : rp.getrItems()) {
				if (rp.getFormWorkflow().isComplete()) {
					// No need to total the quantity since items with split price will have an average unit cost.
					String errorMessage = ValidationUtil.validateToBeCancelledItem(itemService, rpi.getToItemId(),
							rp.getWarehouseId(), rp.getrDate(), rpi.getRepackedQuantity());
					if (errorMessage != null) {
						bindingResult.reject("workflowMessage", errorMessage);
						currentWorkflowLog.setWorkflowMessage(errorMessage);
						break;
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
	 * Get re-packing transaction. 
	 * @param itemId The selected item id. 
	 * @param warehouseId The selected warehosue id. 
	 * @param date The transaction date. 
	 */
	public Page<Repacking> getRepackingTransactions (int itemId, int warehouseId, Date date){
		PageSetting ps = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		return repackingDao.getRepackingTransactions(itemId, warehouseId, date, ps);
	}

	/**
	 * Get all of the re-packed items. 
	 */
	public Page<RepackingItem> getAllRepackedItems () {
		PageSetting ps = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		return repackingItemDao.getAllRepackingItem(ps);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return repackingDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		
		Repacking repacking = repackingDao.getByEbObjectId(ebObjectId);
		int typeId = repacking.getRepackingTypeId();
		String formLabel = "";
		if(repacking.getRepackingTypeId() == RepackingType.TYPE_REPACKING && repacking.getDivisionId() != null) {
			formLabel = "Reclass";
		} else {
			formLabel = repacking.getRepackingTypeId() == RepackingType.TYPE_REPACKING
					? "Repacking" : "Item Conversion";
		}
		int pId = repacking.getId();
		FormProperty property = workflowHandler.getProperty(repacking.getWorkflowName(), user);
		String popupLink = "/repacking/"+typeId+"/"+repacking.getDivisionId()+"/form?pId="+pId;
		String printOutLink = "/"+property.getPrint()+"?pId="+pId;

		String latestStatus = repacking.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formLabel + " - " +repacking.getDivision().getName()+" - "+ repacking.getrNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + repacking.getCompany().getName())
				.append(" " + repacking.getDivision().getName())
				.append(" " + repacking.getWarehouse().getName())
				.append(" " + DateUtil.formatDate(repacking.getrDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case Repacking.REPACKING_OBJECT_TYPE_ID:
			return repackingDao.getByEbObjectId(ebObjectId);
		case RepackingItem.REPACKING_ITEM_OBJ_TYPE_ID:
			return repackingItemDao.getByEbObjectId(ebObjectId);
		case RepackingRawMaterial.RP_RAW_MATERIAL_OBJ_TYPE_ID:
			return rpRawMaterialDao.getByEbObjectId(ebObjectId);
		case RepackingFinishedGood.RP_FINISHED_GOOD_OBJ_TYPE_ID:
			return rpFinishedGoodDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/*
	 * Disable the computation of weighted average
	@Override
	public List<? extends Domain> getItems(BaseFormWorkflow form) {
		Integer formId = form.getId();
		List<? extends Domain> ret = getRItems(formId);
		return ret;
	}

	@Override
	public InventoryItem getItemTransaction(Integer objectId) {
		return repackingItemDao.getByEbObjectId(objectId);
	}

	@Override
	public List<Integer> getWarehouses(BaseFormWorkflow form, Domain itemLine) {
		Repacking rp = (Repacking) form;
		List<Integer> warehouses = new ArrayList<>();
		warehouses.add(rp.getWarehouseId());
		return warehouses;
	}

	@Override
	public List<Integer> getItems(BaseFormWorkflow form, Domain itemLine) {
		Repacking rp = (Repacking) form;
		for (RepackingItem tri : getRItems(rp.getId())) {
			if (tri.getId() == itemLine.getId()) {
				List<Integer> items = new ArrayList<>();
				items.add(tri.getFromItemId());
				items.add(tri.getToItemId());
				return items;
			}
		}
		throw new RuntimeException("Unable to find item.");
	}
	*/
}
