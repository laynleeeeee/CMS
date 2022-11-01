package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.common.util.ListProcessorUtil;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.RepackingDao;
import eulap.eb.dao.RepackingFinishedGoodDao;
import eulap.eb.dao.RepackingRawMaterialDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.domain.hibernate.RepackingFinishedGood;
import eulap.eb.domain.hibernate.RepackingRawMaterial;
import eulap.eb.domain.hibernate.RepackingType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.inventory.AllocatorKey;
import eulap.eb.service.inventory.WeightedAveItemAllocator;
import eulap.eb.service.inventory.FormUnitCosthandler;
import eulap.eb.service.inventory.RItemCostUpdateService;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.inv.RepackingValidator;
import eulap.eb.web.dto.ItemTransaction;

/**
 * Service class that will handle business logic for repacking - item conversion

 */

@Service
public class RpItemConversionService extends BaseWorkflowService implements FormUnitCosthandler {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private RepackingDao repackingDao;
	@Autowired
	private RepackingRawMaterialDao rpRawMaterialDao;
	@Autowired
	private RepackingFinishedGoodDao rpFinishedGoodDao;
	@Autowired
	private RepackingService repackingService;
	@Autowired
	private RepackingValidator repackingValidator;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private FormStatusService formStatusService;

	/**
	 * Validate the repacking - item conversion
	 * @param repacking The repacking form object
	 * @param errors The validation errors
	 */
	public void validateForm(Repacking repacking, Errors errors) {
		boolean isRepacking = repacking.getRepackingTypeId() == RepackingType.TYPE_REPACKING;
		repackingValidator.validate(repacking, errors, isRepacking);
		if (!isRepacking) {
			boolean hasRawMaterials = false;
			String errorMessage = null; 
			List<RepackingRawMaterial> repackingRawMaterials = repacking.getRepackingRawMaterials();
			if (repackingRawMaterials != null && !repackingRawMaterials.isEmpty()) {
				hasRawMaterials = true;
				int rmRowCount = 0;
				for (RepackingRawMaterial rrm : repackingRawMaterials) {
					++rmRowCount;
					if (rrm.getItemId() == null) {
						errors.rejectValue("repackingRawMaterials", null, null,
								String.format(ValidatorMessages.getString("RpItemConversionService.1"), rmRowCount));
					}

					if (rrm.getQuantity() == null) {
						errors.rejectValue("repackingRawMaterials", null, null,
								String.format(ValidatorMessages.getString("RpItemConversionService.2"), rmRowCount));
					} else if (rrm.getQuantity() <= 0) {
						errors.rejectValue("repackingRawMaterials", null, null,
								String.format(ValidatorMessages.getString("RpItemConversionService.3"), rmRowCount));
					} else {
						errorMessage = ValidationUtil.validateQuantity(itemService, warehouseService, rrm.getItemId(),
								repacking.getrDate(), repacking.getWarehouseId(), rrm.getQuantity(), rmRowCount);
						if (errorMessage != null) {
							errors.rejectValue("repackingRawMaterials", null, null, errorMessage);
						}
					}
				}
			}

			boolean hasFinishedGoods = false;
			List<RepackingFinishedGood> repackingFinishedGoods = repacking.getRepackingFinishedGoods();
			if (repackingFinishedGoods != null && !repackingFinishedGoods.isEmpty()) {
				hasFinishedGoods = true;
				int rgRowCount = 0;
				for (RepackingFinishedGood rfg : repackingFinishedGoods) {
					++rgRowCount;
					if (rfg.getItemId() == null) {
						errors.rejectValue("repackingFinishedGoods", null, null,
								String.format(ValidatorMessages.getString("RpItemConversionService.1"), rgRowCount));
					}

					if (rfg.getQuantity() == null) {
						errors.rejectValue("repackingFinishedGoods", null, null,
								String.format(ValidatorMessages.getString("RpItemConversionService.2"), rgRowCount));
					} else if (rfg.getQuantity() <= 0) {
						errors.rejectValue("repackingFinishedGoods", null, null,
								String.format(ValidatorMessages.getString("RpItemConversionService.3"), rgRowCount));
					}
				}
			}

			if (!hasRawMaterials) {
				errors.rejectValue("repackingMessage", null, null,
						String.format(ValidatorMessages.getString("RpItemConversionService.0"), "raw material/s"));
			}

			if (!hasFinishedGoods) {
				errors.rejectValue("repackingMessage", null, null,
						String.format(ValidatorMessages.getString("RpItemConversionService.0"), "finished good/s"));
			}
		}
		//Validate form status
		FormWorkflow workflow = repacking.getId() != 0 ? repackingService.getFormWorkflow(repacking.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}
	}

	/**
	 * Save the item conversion form
	 */
	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		Repacking repacking = (Repacking) form;
		int repackingId = repacking.getId();
		boolean isNew = repackingId == 0;
		Date currDate = new Date();
		AuditUtil.addAudit(repacking, new Audit(user.getId(), isNew, currDate));
		if (isNew) {
			repacking.setrNumber(repackingDao.generateRNumber(repacking.getCompanyId()));
		} else {
			Repacking savedRepacking = repackingDao.get(repackingId);
			DateUtil.setCreatedDate(repacking, savedRepacking.getCreatedDate());
			savedRepacking = null;
		}
		String remarks = repacking.getRemarks();
		if (remarks != null) {
			repacking.setRemarks(StringFormatUtil.removeExtraWhiteSpaces(remarks));
		}
		repackingDao.saveOrUpdate(repacking);

		if (!isNew) {
			List<RepackingRawMaterial> savedRawMaterials = rpRawMaterialDao.getAllByRefId("repackingId", repackingId);
			if (savedRawMaterials != null && !savedRawMaterials.isEmpty()) {
				for (RepackingRawMaterial rrm : savedRawMaterials) {
					rpRawMaterialDao.delete(rrm);
				}
				savedRawMaterials = null;
			}

			List<RepackingFinishedGood> savedFinishedGoods = rpFinishedGoodDao.getAllByRefId("repackingId", repackingId);
			if (savedFinishedGoods != null && !savedFinishedGoods.isEmpty()) {
				for (RepackingFinishedGood rfg : savedFinishedGoods) {
					rpFinishedGoodDao.delete(rfg);
				}
				savedFinishedGoods = null;
			}
		}
		allocateRepackingItems(repacking);
	}

	private void allocateRepackingItems(Repacking repacking) {
		int repackingId = repacking.getId();
		Map<AllocatorKey, WeightedAveItemAllocator<RepackingRawMaterial>> itemId2CostAllocator =
				new HashMap<AllocatorKey, WeightedAveItemAllocator<RepackingRawMaterial>>();
		List<RepackingRawMaterial> repackingRawMaterials = repacking.getRepackingRawMaterials();
		AllocatorKey key = null;
		double totalMaterialCost = 0;
		List<Domain> toBeSaveRmItems = new ArrayList<Domain>();
		for (RepackingRawMaterial rm : repackingRawMaterials) {
			WeightedAveItemAllocator<RepackingRawMaterial> itemAllocator = itemId2CostAllocator.get(rm.getItemId());
			if (itemAllocator == null) {
				itemAllocator = new WeightedAveItemAllocator<RepackingRawMaterial>(itemDao, itemService,
						rm.getItemId(), repacking.getWarehouseId(), repacking.getrDate());
				key = AllocatorKey.getInstanceOf(rm.getItemId(), repacking.getWarehouseId());
				itemId2CostAllocator.put(key, itemAllocator);
			}

			try {
				List<RepackingRawMaterial> allocatedRMs = itemAllocator.allocateCost(rm);
				for (RepackingRawMaterial arm : allocatedRMs) {
					double qty = arm.getQuantity();
					double uc = arm.getUnitCost() != null ? arm.getUnitCost() : 0.0;
					totalMaterialCost += (NumberFormatUtil.multiplyWFP(qty, uc));
					// Set to be save raw material items
					arm.setRepackingId(repackingId);
					toBeSaveRmItems.add(arm);
				}
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		rpRawMaterialDao.batchSave(toBeSaveRmItems);

		List<Domain> toBeSavedFgItems = new ArrayList<Domain>();
		List<RepackingFinishedGood> repackingFinishedGoods = repacking.getRepackingFinishedGoods();
		double computedUnitCost = NumberFormatUtil.divideWFP(totalMaterialCost, getTotalQuantity(repackingFinishedGoods));
		for (RepackingFinishedGood rfg : repackingFinishedGoods) {
			rfg.setRepackingId(repackingId);
			rfg.setUnitCost(computedUnitCost);
			toBeSavedFgItems.add(rfg);
		}
		rpFinishedGoodDao.batchSave(toBeSavedFgItems);
	}

	private double getTotalQuantity(List<RepackingFinishedGood> repackingFinishedGoods) {
		double total = 0;
		for (RepackingFinishedGood rfg : repackingFinishedGoods) {
			total += rfg.getQuantity();
		}
		return total;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		Repacking repacking = repackingDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID
				&& repacking.getFormWorkflow().isComplete()) {
			int warehouseId = repacking.getWarehouseId();
			Date rDate = repacking.getrDate();
			List<RepackingFinishedGood> savedFinishedGoods = rpFinishedGoodDao.getAllByRefId("repackingId", repacking.getId());
			if (savedFinishedGoods != null && !savedFinishedGoods.isEmpty()) {
				double totalQty = 0;
				int itemId;
				String errorMessage = null;
				for (RepackingFinishedGood rfg : savedFinishedGoods) {
					itemId = rfg.getItemId();
					totalQty = ValidationUtil.getTotalQtyPerItem(itemId, savedFinishedGoods);
					errorMessage = ValidationUtil.validateToBeCancelledItem(itemService, itemId,
							warehouseId, rDate, totalQty);
					if (errorMessage != null) {
						bindingResult.reject("workflowMessage", errorMessage);
						currentWorkflowLog.setWorkflowMessage(errorMessage);
						break;
					}
				}
				savedFinishedGoods = null;
			}
		}
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		//Do nothing
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return repackingService.getFormWorkflow(id);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return repackingService.getFormByWorkflow(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		return repackingService.getObjectInfo(ebObjectId, user);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		return repackingService.getDomain(ebObject);
	}

	@Override
	public void updateUnitCost(RItemCostUpdateService costUpdateService, WeightedAveItemAllocator<ItemTransaction> fifoAllocator,
			ItemTransaction it, int itemId, int warehouseId, Date formDate, boolean isAllocateRpTo) {
		// do nothing
	}

	@Override
	public void processAllocatedItem(int itemId, int warehouseId, Queue<ItemTransaction> allocatedItems,
			ItemTransaction currentAllocItem) throws CloneNotSupportedException {
		int repackingId = currentAllocItem.getId();
		List<RepackingRawMaterial> repackingRawMaterials = rpRawMaterialDao.getRawMaterialItems(repackingId, itemId);
		ListProcessorUtil<RepackingRawMaterial> remover = new ListProcessorUtil<RepackingRawMaterial>();
		List<Integer> formIds = remover.collectFormIds(repackingRawMaterials);
		List<RepackingRawMaterial> processedItems = repackingService.summarizeRawMaterials(repackingRawMaterials);
		Double allocQty = currentAllocItem.getQuantity();
		Double qtyToBeWithdrawn = null;
		RepackingRawMaterial splitItem = null;
		List<Integer> savedFormIds = new ArrayList<Integer>();
		for (RepackingRawMaterial rrm : processedItems) {
			while (currentAllocItem != null) {
				if (qtyToBeWithdrawn == null) {
					qtyToBeWithdrawn = rrm.getQuantity();
				}
				if (allocQty >= qtyToBeWithdrawn) {
					rrm.setUnitCost(currentAllocItem.getUnitCost());
					rrm.setQuantity(qtyToBeWithdrawn);
					// update raw material
					rpRawMaterialDao.saveOrUpdate(rrm);
					savedFormIds.add(rrm.getId());
					allocQty = NumberFormatUtil.roundOffNumber((allocQty - qtyToBeWithdrawn), NumberFormatUtil.SIX_DECIMAL_PLACES);
					if (allocQty == 0.0) {
						currentAllocItem = getNextAllocItem(allocatedItems);
						allocQty = getAllocatedQty(currentAllocItem);
					}
					qtyToBeWithdrawn = null;
					break;
				} else {
					if (allocQty > 0) {
						splitItem = (RepackingRawMaterial) rrm.clone();
						splitItem.setId(0);
						splitItem.setQuantity(allocQty);
						splitItem.setUnitCost(currentAllocItem.getUnitCost());
						// save raw material
						rpRawMaterialDao.saveOrUpdate(splitItem);
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
				toBeDeleted.add(rpRawMaterialDao.get(id));
			}
		}

		if (!toBeDeleted.isEmpty()) {
			for (Domain tbd : toBeDeleted) {
				rpRawMaterialDao.delete(tbd);
			}
		}

		repackingRawMaterials = null;
		remover = null;
		processedItems = null;
		toBeDeleted = null;
		formIds = null;
		savedFormIds = null;

		recomputeFinishedGoodCost(repackingId);
	}

	private void recomputeFinishedGoodCost(int repackingId) {
		List<RepackingRawMaterial> repackingRawMaterials = rpRawMaterialDao.getAllByRefId("repackingId", repackingId);
		double totalMaterialCost = 0;
		for (RepackingRawMaterial rrm : repackingRawMaterials) {
			double qty = rrm.getQuantity();
			double uc = rrm.getUnitCost() != null ? rrm.getUnitCost() : 0.0;
			totalMaterialCost += (NumberFormatUtil.multiplyWFP(qty, uc));
		}

		List<RepackingFinishedGood> repackingFinishedGoods = rpFinishedGoodDao.getAllByRefId("repackingId", repackingId);
		double computedUnitCost = NumberFormatUtil.divideWFP(totalMaterialCost,  getTotalQuantity(repackingFinishedGoods));
		for (RepackingFinishedGood rfg : repackingFinishedGoods) {
			rfg.setUnitCost(computedUnitCost);
			rpFinishedGoodDao.update(rfg);
		}

		repackingRawMaterials = null;
		repackingFinishedGoods = null;
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

	@Override
	public String getFormLabel() {
		return "IC";
	}
}
