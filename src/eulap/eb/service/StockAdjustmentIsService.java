package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.StockAdjustmentDao;
import eulap.eb.dao.StockAdjustmentItemDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.StockAdjustmentItem;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;

/**
 * Business logic for Stock Adjustment - Individual Selection module.

 *
 */
@Service
public class StockAdjustmentIsService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(StockAdjustmentIsService.class);
	@Autowired
	private StockAdjustmentItemDao stockAdjustmentItemDao;
	@Autowired
	private StockAdjustmentDao stockAdjustmentDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private EbObjectService ebObjectService;
	@Autowired
	private ItemBagQuantityDao itemBagQuantityDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;

	/**
	 * Get the stock adjustment object using the unique id.
	 * @param id The id of the stock adjustment.
	 * @return The {@link StockAdjustment}
	 */
	public StockAdjustment getStockAdjustmentIs(int id) {
		return stockAdjustmentDao.get(id);
	}

	public void reloadForm(StockAdjustment stockAdjustment) {
		//Reload form
		if(stockAdjustment.getCompanyId() != null) {
			stockAdjustment.setCompany(companyService.getCompany(stockAdjustment.getCompanyId()));
		}
		if(stockAdjustment.getFormWorkflowId() != null) {
			stockAdjustment.setFormWorkflow(getFormWorkflow(stockAdjustment.getId()));
		}
	}

	/**
	 * Get the stock adjustment object and set the available stocks for each item.
	 * @param id The unique id of the stock adjustment.
	 * @param typeId The adjustment type id.
	 * @return The {@link StockAdjustment}
	 */
	public StockAdjustment getSAIsWithAvailStocks(int id, int typeId) {
		StockAdjustment sa = getStockAdjustmentIs(id);
		for (StockAdjustmentItem sai : sa.getSaItems()) {
			itemBagQuantityService.setItemBagQty(sai, sai.getEbObjectId(),
					typeId == StockAdjustment.STOCK_ADJUSTMENT_IS_IN ? ItemBagQuantity.SAI_BAG_QTY : ItemBagQuantity.SAO_BAG_QTY);
			double availStocks = itemService.getTotalAvailStocks(
					sai.getItem().getStockCode(), sa.getWarehouseId());
			sai.setExistingStocks(availStocks);
		}
		return sa;
	}

	/**
	 * Get and process the stock adjustment and its related tables for viewing.
	 * @param id The unique id of the stock adjustment.
	 * @param typeId The adjustment type id.
	 * @return The {@link StockAdjustment}
	 */
	public StockAdjustment getSAIsAndProcessedItems(int id, int typeId) {
		StockAdjustment stockAdjustment = getStockAdjustmentIs(id);
		logger.info("Retrieving the stock adjustment object for viewing.");
		EBObject ebObject = null;
		for (StockAdjustmentItem sai : stockAdjustment.getSaItems()) {
			sai.setStockCodeIs(sai.getItem().getStockCode());
			double quantity = sai.getQuantity();
			itemBagQuantityService.setItemBagQty(sai, sai.getEbObjectId(),
					typeId == StockAdjustment.STOCK_ADJUSTMENT_IS_IN ? ItemBagQuantity.SAI_BAG_QTY : ItemBagQuantity.SAO_BAG_QTY);
			if(typeId == StockAdjustment.STOCK_ADJUSTMENT_IS_OUT) {
				ebObject = ooLinkHelper.getReferenceObject(sai.getEbObjectId(), 3);
				sai.setReferenceObjectId(ebObject.getId());
				sai.setOrigRefObjectId(ebObject.getId());
			}
			sai.setOrigQty(quantity);
		}
		return stockAdjustment;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		StockAdjustment stockAdjustment = getStockAdjustmentIs(id);
		if(stockAdjustment != null) {
			logger.debug("Retrieved the workflow for Stock Adjustment - IS "
					+ "using the id: "+id);
			return stockAdjustment.getFormWorkflow();
		}
		logger.warn("Could not retrieved Stock Adjustment using the id: "+id);
		return null;
	}

	public void loadCompanies(Model model, User user) {
		model.addAttribute("companies", companyService.getCompanies(user));
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		logger.info("Saving the Stock Adjustment - IS form.");
		StockAdjustment stockAdjustment = (StockAdjustment) form;
		boolean isNew = stockAdjustment.getId() == 0;
		Date currentDate = new Date();
		int userId = user.getId();
		AuditUtil.addAudit(stockAdjustment, new Audit(userId, isNew, currentDate));
		int typeId = stockAdjustment.getStockAdjustmentClassificationId();
		if(isNew) {
			int saNumber = stockAdjustmentDao.generateSANoByCompAndTypeId(stockAdjustment.getCompanyId(), typeId);
			logger.debug("Generated the sequence number for Stock Adjustment - IS form: "+saNumber);
			stockAdjustment.setSaNumber(saNumber);
		} else {
			StockAdjustment savedSA = getStockAdjustmentIs(stockAdjustment.getId());
			DateUtil.setCreatedDate(stockAdjustment, savedSA.getCreatedDate());

			List<StockAdjustmentItem> savedItems = stockAdjustmentItemDao.getAllByRefId("stockAdjustmentId", stockAdjustment.getId());

			for (StockAdjustmentItem sai : savedItems) {
				ItemBagQuantity ibq =  itemBagQuantityDao.getByRefId(sai.getEbObjectId(),
						typeId == StockAdjustment.STOCK_ADJUSTMENT_IS_IN ? ItemBagQuantity.SAI_BAG_QTY : ItemBagQuantity.SAO_BAG_QTY);
				if (ibq != null) {
					ibq.setActive(false);
					itemBagQuantityDao.update(ibq);
				}
				stockAdjustmentItemDao.delete(sai);
			}
		}

		String remarks = stockAdjustment.getRemarks();
		if(remarks != null) {
			stockAdjustment.setRemarks(remarks.trim());
		}

		//Save the Stock Adjustment
		stockAdjustmentDao.saveOrUpdate(stockAdjustment);

		//Save Stock Adjustment Items
		List<Domain> toBeSavedIbqs = new ArrayList<>();
		List<Domain> oos = new ArrayList<>();
		for (StockAdjustmentItem sai : stockAdjustment.getSaItems()) {
			sai.setStockAdjustmentId(stockAdjustment.getId());
			if (sai.getItemBagQuantity() != null) {
				int ibqObjectId = ebObjectService.saveAndGetEbObjectId(userId, ItemBagQuantity.OBJECT_TYPE_ID, currentDate);
				ItemBagQuantity ibq = ItemBagQuantity.getInstanceOf(sai.getItemId(), ibqObjectId, sai.getItemBagQuantity());
				ibq.setCreatedBy(userId);
				AuditUtil.addAudit(ibq, new Audit(userId, isNew, currentDate));
				toBeSavedIbqs.add(ibq);
				oos.add(ObjectToObject.getInstanceOf(sai.getEbObjectId(), ibqObjectId,
						typeId == StockAdjustment.STOCK_ADJUSTMENT_IS_IN ? ItemBagQuantity.SAI_BAG_QTY : ItemBagQuantity.SAO_BAG_QTY,
						user, currentDate));
			}
			SaleItemUtil.setNullUnitCostToZero(sai);
			stockAdjustmentItemDao.save(sai);
		}
		if (!toBeSavedIbqs.isEmpty()) {
			itemBagQuantityDao.batchSave(toBeSavedIbqs);
		}
		if (!oos.isEmpty()) {
			objectToObjectDao.batchSave(oos);
		}
	}


	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return stockAdjustmentDao.getAllByRefId("ebObjectId", ebObjectId).get(0);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		StockAdjustment stockAdjustment = stockAdjustmentDao.getByWorkflowId(
				currentWorkflowLog.getFormWorkflowId());
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID
				&& stockAdjustment.getFormWorkflow().isComplete()) {
			String errorMessage = ValidationUtil.validateToBeCancelledRefForm(itemService,
					stockAdjustment.getSaItems());
			if(errorMessage != null) {
				bindingResult.reject("workflowMessage", errorMessage);
				currentWorkflowLog.setWorkflowMessage(errorMessage);
			}
		}
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return stockAdjustmentDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		return stockAdjustmentDao.getByEbObjectId(ebObject.getId());
	}
}