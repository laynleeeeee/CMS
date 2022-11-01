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
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.RTransferReceiptDao;
import eulap.eb.dao.RTransferReceiptItemDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RReceivingReportRmItem;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.domain.hibernate.RTransferReceiptItem;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;

/**
 * Class that handles business logic of {@link RTransferReceipt} for Individual Selection

 *
 */
@Service
public class TransferReceiptISService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(TransferReceiptISService.class);
	@Autowired
	private RTransferReceiptDao trDao;
	@Autowired
	private RTransferReceiptItemDao trItemDao;
	@Autowired
	private RTransferReceiptService trService;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private EbObjectService ebObjectService;
	@Autowired
	private ItemBagQuantityDao itemBagQuantityDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private RTransferReceiptService transferReceiptService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;

	/**
	 * Get the transfer receipt object
	 * @param id The transfer receipt id
	 * @return The transfer receipt object
	 */
	public RTransferReceipt getTransferReceiptIs(int id) {
		return trDao.get(id);
	}

	/**
	 * Get and process the transfer receipt and items
	 * @param id The transfer receipt id
	 * @return The processed transfer receipt object
	 */
	public RTransferReceipt getTrAndItems(int id) {
		RTransferReceipt tr = getTransferReceiptIs(id);
		EBObject ebObject = null;
		for (RTransferReceiptItem tri : tr.getrTrItems()) {
			tri.setOrigQty(tri.getQuantity());
			tri.setStockCodeIs(tri.getItem().getStockCode());
			itemBagQuantityService.setItemBagQty(tri, tri.getEbObjectId(), ItemBagQuantity.TR_IS_BAG_QTY);
			ebObject = ooLinkHelper.getReferenceObject(tri.getEbObjectId(), 8);
			tri.setOrigRefObjectId(ebObject.getId());
			tri.setReferenceObjectId(ebObject.getId());
		}
		return tr;
	}

	/**
	 * Save the transfer receipt form and items
	 * @param form The transfer receipt object
	 * @param workflowName The workflow name
	 * @param user The current user logged
	 */
	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		logger.info("Saving the Transfer Receipt.");
		RTransferReceipt transferReceipt = (RTransferReceipt) form;
		Integer transferReceiptId = transferReceipt.getId();
		boolean isNew = transferReceiptId == 0 ? true : false;
		Integer trNumber = null;
		Integer userId = user.getId();
		Date currentDate = new Date();
		AuditUtil.addAudit(transferReceipt, new Audit(userId, isNew, currentDate));

		if(isNew) {
			trNumber = trDao.generateTRNumber(transferReceipt.getCompanyId());
			transferReceipt.setTrNumber(trNumber);
		} else {
			RTransferReceipt savedTR = getTransferReceiptIs(transferReceiptId);
			DateUtil.setCreatedDate(transferReceipt, savedTR.getCreatedDate());

			logger.debug("Deleting the saved TRIS Items of TR: "+transferReceiptId);
			List<RTransferReceiptItem> rTRItems = trService.getTrItems(transferReceiptId);
			for (RTransferReceiptItem receiptItem : rTRItems) {
				ItemBagQuantity ibq =  itemBagQuantityDao.getByRefId(receiptItem.getEbObjectId(), ItemBagQuantity.TR_IS_BAG_QTY);
				if (ibq != null) {
					ibq.setActive(false);
					itemBagQuantityDao.update(ibq);
				}
				trItemDao.delete(receiptItem);
			}
			logger.trace("Deleted "+rTRItems.size()+" TR Items");
			logger.debug("Successfully deleted TR Items of TRIS: "+transferReceiptId);
		}

		String drNumber = transferReceipt.getDrNumber();
		if(drNumber != null) {
			transferReceipt.setDrNumber(drNumber.trim());
		}
		trDao.saveOrUpdate(transferReceipt);
		logger.info("Successfully saved IS - Transfer Receipt.");
		logger.debug("Saved the TRIS Number: "+transferReceipt.getFormattedTRNumber());

		transferReceiptId = transferReceipt.getId();
		List<Domain> toBeSavedIbqs = new ArrayList<>();
		List<Domain> oos = new ArrayList<>();
		List<RTransferReceiptItem> rTRItems = transferReceipt.getrTrItems();
		logger.debug("Saving the TR Items.");
		for (RTransferReceiptItem receiptItem : rTRItems) {
			receiptItem.setrTransferReceiptId(transferReceiptId);
			if (receiptItem.getItemBagQuantity() != null) {
				int ibqObjectId = ebObjectService.saveAndGetEbObjectId(userId, ItemBagQuantity.OBJECT_TYPE_ID, currentDate);
				ItemBagQuantity ibq = ItemBagQuantity.getInstanceOf(receiptItem.getItemId(), ibqObjectId, receiptItem.getItemBagQuantity());
				ibq.setCreatedBy(userId);
				AuditUtil.addAudit(ibq, new Audit(userId, isNew, currentDate));
				toBeSavedIbqs.add(ibq);
				oos.add(ObjectToObject.getInstanceOf(receiptItem.getEbObjectId(),
						ibqObjectId, ItemBagQuantity.TR_IS_BAG_QTY, user, currentDate));
			}
			SaleItemUtil.setNullUnitCostToZero(receiptItem);
			trItemDao.save(receiptItem);
		}
		if (!toBeSavedIbqs.isEmpty()) {
			itemBagQuantityDao.batchSave(toBeSavedIbqs);
		}
		if (!oos.isEmpty()) {
			objectToObjectDao.batchSave(oos);
		}
		logger.debug("Successfully saved the TR Items.");
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return trDao.getByEbObjectId(ebObjectId);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return getTransferReceiptIs(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return trDao.getByWorkflowId(workflowId);
	}

	/**
	 * Get the Transfer Receipt - IS and process its items.
	 * @param transferReceiptId The unique id of the TR.
	 * @return The  Transfer Receipt - IS
	 */
	public RTransferReceipt getTrIsWithItems(int transferReceiptId) {
		RTransferReceipt tr = transferReceiptService.getRTransferReceipt(transferReceiptId);
		List<RTransferReceiptItem> trItems = processTrItems(tr.getrTrItems());
		for (RTransferReceiptItem tri : trItems) {
			tri.setStockCode(tri.getItem().getStockCode());
			double existingStocks = itemService.getTotalAvailStocks(tri.getStockCode(), tr.getWarehouseFromId());
			tri.setExistingStocks(existingStocks);
			tri.setOrigQty(tri.getQuantity());
		}
		tr.setrTrItems(trItems);
		return tr;
	}

	/**
	 * Process transfer receipt items
	 * @param trItems The transfer receipt items
	 * @return The processed list transfer receipt items
	 */
	public List<RTransferReceiptItem> processTrItems(List<RTransferReceiptItem> trItems) {
		for (RTransferReceiptItem tri : trItems) {
			itemBagQuantityService.setItemBagQty(tri, tri.getEbObjectId(), ItemBagQuantity.TR_IS_BAG_QTY);
		}
		return trItems;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		return transferReceiptService.createObjectInfo(ebObjectId, "Transfer Receipt - IS", user);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case RTransferReceipt.TR_IS_OBJECT_TYPE_ID:
			return trDao.getByEbObjectId(ebObjectId);
		case RTransferReceiptItem.OBJECT_TYPE_ID:
			return trItemDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		RTransferReceipt tr = trDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID
				&& tr.getFormWorkflow().isComplete()) {
			//Check if used as reference
			List<RTransferReceiptItem> trItems = trItemDao.getAllByRefId(RTransferReceiptItem.FIELD.rTransferReceiptId.name(), tr.getId());
			String errorMessage = ValidationUtil.validateToBeCancelledRefForm(itemService, trItems);
			if(errorMessage != null) {
				bindingResult.reject("workflowMessage", errorMessage);
				currentWorkflowLog.setWorkflowMessage(errorMessage);
			}
		}
	}
}