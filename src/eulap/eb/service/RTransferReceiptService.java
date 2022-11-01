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
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.RTransferReceiptDao;
import eulap.eb.dao.RTransferReceiptItemDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.domain.hibernate.RTransferReceiptItem;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;

/**
 * Business logic for Retail - Transfer Receipt.

 *
 */
@Service
public class RTransferReceiptService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(RTransferReceiptService.class);
	@Autowired
	private RTransferReceiptDao trDao;
	@Autowired
	private RTransferReceiptItemDao trItemDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;

	/**
	 * Get the Retail - Transfer Receipt object.
	 * @param transferReceiptId The unique id of the TR.
	 * @return The TR object.
	 */
	public RTransferReceipt getRTransferReceipt(int transferReceiptId) {
		return trDao.get(transferReceiptId);
	}

	/**
	 * Get the Retail - Transfer Receipt and process its items.
	 * @param transferReceiptId The unique id of the TR.
	 * @return The {@link RTransferReceipt}
	 */
	public RTransferReceipt getTrWithItems(int transferReceiptId) {
		RTransferReceipt tr = getRTransferReceipt(transferReceiptId);
		List<RTransferReceiptItem> trItems = tr.getrTrItems();
		List<RTransferReceiptItem> summarisedTrItems = summariseTrItems(trItems);
		for (RTransferReceiptItem tri : summarisedTrItems) {
			tri.setStockCode(tri.getItem().getStockCode());
			//Set the existing stocks for current date.
			tri.setExistingStocks(itemService.getItemExistingStocks(tri.getItemId(), tr.getWarehouseFromId()));
			tri.setOrigQty(tri.getQuantity());
		}
		tr.setrTrItems(summarisedTrItems);
		return tr;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		logger.info("Retrieving the form workflow of purchase order: "+id);
		return getRTransferReceipt(id).getFormWorkflow();
	}

	/**
	 * Get the list of Transfer Receipt Items.
	 * @param transferReceiptId The id of the TR.
	 * @return The list of TR Items.
	 */
	public List<RTransferReceiptItem> getTrItems (int transferReceiptId) {
		return trItemDao.getRTrItems(transferReceiptId, null);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		logger.info("Saving the Transfer Receipt.");
		RTransferReceipt transferReceipt = (RTransferReceipt) form;
		boolean isNew = transferReceipt.getId() == 0 ? true : false;
		Integer trNumber = null;
		Date currDate = new Date();
		AuditUtil.addAudit(transferReceipt, new Audit(user.getId(), isNew, currDate));
		if(isNew) {
			trNumber = trDao.generateTRNumber(transferReceipt.getCompanyId());
			transferReceipt.setTrNumber(trNumber);
		} else {
			RTransferReceipt savedTR = getRTransferReceipt(transferReceipt.getId());
			DateUtil.setCreatedDate(transferReceipt, savedTR.getCreatedDate());
		}

		String drNumber = transferReceipt.getDrNumber();
		if(drNumber != null)
			transferReceipt.setDrNumber(drNumber.trim());
		trDao.saveOrUpdate(transferReceipt);
		logger.info("Successfully saved Retail - Transfer Receipt.");
		logger.debug("Saved the TR Number: "+transferReceipt.getFormattedTRNumber());

		List<Integer> toBeDeletedTrItemIds = new ArrayList<Integer>();
		List<RTransferReceiptItem> rTRItems = null;
		Integer transferReceiptId = transferReceipt.getId();
		if(!isNew) {
			logger.debug("Deleting the saved TR Items of TR: "+transferReceiptId);
			rTRItems = getTrItems(transferReceiptId);
			if(!rTRItems.isEmpty()) {
				for (RTransferReceiptItem receiptItem : rTRItems)
					toBeDeletedTrItemIds.add(receiptItem.getId());
				trItemDao.delete(toBeDeletedTrItemIds);
				logger.trace("Deleted "+toBeDeletedTrItemIds.size()+" TR Items");
				logger.debug("Successfully deleted TR Items of TR: "+transferReceiptId);
			}
		}

		rTRItems = transferReceipt.getrTrItems();
		logger.debug("Saving the TR Items.");
		logger.trace("Saving "+rTRItems.size()+" TR Items.");
		// Disable the computation of weighted average
		// Map<AllocatorKey, ItemAllocator<RTransferReceiptItem>> itemId2CostAllocator =
		//		new HashMap<AllocatorKey, ItemAllocator<RTransferReceiptItem>>();
		Map<AllocatorKey, WeightedAveItemAllocator<RTransferReceiptItem>> itemId2CostAllocator =
				new HashMap<AllocatorKey, WeightedAveItemAllocator<RTransferReceiptItem>>();
		for (RTransferReceiptItem receiptItem : rTRItems) {
			logger.debug("Allocating the unit cost of the TR Item using the FIFO Item Allocator.");
			// ItemAllocator<RTransferReceiptItem> itemAllocator = itemId2CostAllocator.get(receiptItem.getItemId());
			WeightedAveItemAllocator<RTransferReceiptItem> itemAllocator = itemId2CostAllocator.get(receiptItem.getItemId());
			AllocatorKey key = null;
			if (itemAllocator == null) {
				// itemAllocator = new WeightedAverageAllocator<RTransferReceiptItem>(itemService, receiptItem.getItemId(),
				//		transferReceipt.getCompanyId(), transferReceipt.getWarehouseFromId(), transferReceipt.getTrDate());
				itemAllocator = new WeightedAveItemAllocator<RTransferReceiptItem>(itemDao, itemService,
						receiptItem.getItemId(), transferReceipt.getWarehouseFromId(), transferReceipt.getTrDate());
				key = AllocatorKey.getInstanceOf(receiptItem.getItemId(), transferReceipt.getWarehouseFromId());
				itemId2CostAllocator.put(key, itemAllocator);
			}
			try {
				List<RTransferReceiptItem> allocatedReceiptItems = itemAllocator.allocateCost(receiptItem);
				for (RTransferReceiptItem allocatedRItem : allocatedReceiptItems) {
					logger.debug("Successfully allocated the item id: "+allocatedRItem.getItemId()
							+" with unit cost: "+allocatedRItem.getUnitCost());
					allocatedRItem.setrTransferReceiptId(transferReceiptId);
					SaleItemUtil.setNullUnitCostToZero(allocatedRItem);
					trItemDao.save(allocatedRItem);
				}
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		logger.debug("Successfully saved the TR Items.");

		logger.warn("=====>>> Freeing up the memory allocation");
		toBeDeletedTrItemIds = null;
		transferReceiptId = null;
		trNumber = null;
		drNumber = null;
	}

	/**
	 * Groups the TR Items by the item id.
	 * @param trItems The list of TR Items to be grouped.
	 * @return The list of TR Items without duplicate item ids.
	 */
	public List<RTransferReceiptItem> summariseTrItems(List<RTransferReceiptItem> trItems) {
		logger.debug("Group the TR Items by item id.");
		List<RTransferReceiptItem> updatedTrItems = new ArrayList<RTransferReceiptItem>();
		Map<Integer, RTransferReceiptItem> trItemHashMap = new HashMap<Integer, RTransferReceiptItem>();
		RTransferReceiptItem editedItem = null;
		double editedOrigQty = 0;
		double origQty = 0;
		for (RTransferReceiptItem tri : trItems) {
			if(trItemHashMap.containsKey(tri.getItemId())) {
				editedItem = trItemHashMap.get(tri.getItemId());
				editedOrigQty = editedItem.getOrigQty() != null ? editedItem.getOrigQty() : 0;
				origQty = tri.getOrigQty() != null ? tri.getOrigQty() : 0;
				editedItem.setOrigQty(origQty + editedOrigQty);
				editedItem.setQuantity(tri.getQuantity()+editedItem.getQuantity());
				trItemHashMap.put(tri.getItemId(), editedItem);
			} else {
				trItemHashMap.put(tri.getItemId(), tri);
			}
		}

		for (Map.Entry<Integer, RTransferReceiptItem> tri : trItemHashMap.entrySet()) {
			updatedTrItems.add(tri.getValue());
		}

		trItemHashMap = null;
		editedItem = null;
		return updatedTrItems;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			RTransferReceipt tr = trDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
			if(tr.getFormWorkflow().isComplete()) {
				for (RTransferReceiptItem tri : tr.getrTrItems()) {
					double totalQty = ValidationUtil.getTotalQtyPerItem(tri.getItemId(), tr.getrTrItems());
					String errorMessage = ValidationUtil.validateToBeCancelledItem(itemService, tri.getItemId(),
							tr.getWarehouseToId(), tr.getTrDate(), totalQty);
					if(errorMessage != null) {
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

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return trDao.getByWorkflowId(workflowId);
	}

	public ObjectInfo createObjectInfo(int ebObjectId, String formName, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
	
		RTransferReceipt rTransferReceipt = trDao.getByEbObjectId(ebObjectId);
		int typeId = rTransferReceipt.getTransferReceiptTypeId();
		Integer pId = rTransferReceipt.getId();
		FormProperty property = workflowHandler.getProperty(rTransferReceipt.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
	
		String latestStatus = rTransferReceipt.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName + " - " + rTransferReceipt.getTrNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + rTransferReceipt.getCompany().getName())
				.append("<b> WAREHOUSE FROM </b>" + rTransferReceipt.getWarehouseFrom().getName())
				.append("<b> WAREHOUSE TO </b> " + rTransferReceipt.getWarehouseTo().getName())
				.append("<b> DATE : </b>" + DateUtil.formatDate(rTransferReceipt.getTrDate()));
	
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		return createObjectInfo(ebObjectId, "Transfer Receipt", user);
		
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case RTransferReceipt.TR_OBJECT_TYPE_ID:
			return trDao.getByEbObjectId(ebObjectId);
		case RTransferReceiptItem.OBJECT_TYPE_ID:
			return trItemDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/*
	 * Disable the computation of weighted average
	@Override
	public List<? extends Domain> getItems(BaseFormWorkflow form) {
		Integer formId = form.getId();
		List<? extends Domain> ret = getTrItems(formId);
		return ret;
	}

	@Override
	public InventoryItem getItemTransaction(Integer objectId) {
		return trItemDao.getByEbObjectId(objectId);
	}

	@Override
	public List<Integer> getItems(BaseFormWorkflow form, Domain itemLine) {
		RTransferReceipt transferReceipt = (RTransferReceipt) form;
		for (RTransferReceiptItem tri : getTrItems(transferReceipt.getId())) {
			if (tri.getId() == itemLine.getId()) {
				List<Integer> ret = new ArrayList<>();
				ret.add(tri.getItemId());
				return ret;
			}
		}
		throw new RuntimeException("Unable to find item.");
	}

	@Override
	public List<Integer> getWarehouses(BaseFormWorkflow form, Domain itemLine) {
		RTransferReceipt tr = (RTransferReceipt) form;
		List<Integer> warehouses = new ArrayList<>();
		warehouses.add(tr.getWarehouseFromId());
		warehouses.add(tr.getWarehouseToId());
		return warehouses;
	}
	*/
}
