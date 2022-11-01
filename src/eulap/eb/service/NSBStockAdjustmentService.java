
package eulap.eb.service;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import eulap.common.domain.Domain;
import eulap.eb.dao.StockAdjustmentDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.StockAdjustmentClassification;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.web.dto.NSBStockAdjustmentDto;

/**
 * A class that handles the business logic of Stock Adjustment for NSB

 *
 */
@Service
public class NSBStockAdjustmentService extends BaseWorkflowService {
	private static final Logger logger = Logger.getLogger(NSBStockAdjustmentService.class);
	@Autowired
	private StockAdjustmentService adjustmentService;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private StockAdjustmentDao saDao;
	@Autowired
	private EBFormServiceHandler eBFormServiceHandler;
	@Autowired
	private ReferenceDocumentService refDocumentService;

	/**
	 * Saving NSB stock adjustment.
	 * @param stockAdjustmentDto The stock adjustment DTO for NSB.
	 * @param user The user current log.
	 * @param typeId The type id.
	 * @throws CloneNotSupportedException
	 * @throws ClassNotFoundException 
	 * @throws InvalidClassException 
	 */
	public void saveStockAdjustment(NSBStockAdjustmentDto stockAdjustmentDto, User user, int typeId) throws CloneNotSupportedException,
			InvalidClassException, ClassNotFoundException {
		StockAdjustment stockAdjustment = stockAdjustmentDto.getStockAdjustment();
		boolean isNew = stockAdjustment.getId() == 0;
		Integer parentObjectId = stockAdjustment.getEbObjectId();
		boolean isStockIn = typeId == StockAdjustment.STOCK_ADJUSTMENT_IN;
		int orTypeId = isStockIn ? NSBStockAdjustmentDto.SA_IN_TO_SI_OR_TYPE_ID : NSBStockAdjustmentDto.SA_OUT_TO_SI_OR_TYPE_ID;
		int warehouseId = stockAdjustment.getWarehouseId();
		boolean isValidRate = stockAdjustment.getCurrencyRateValue() != null && stockAdjustment.getCurrencyRateValue() != 0;
		double currencyRateValue = isValidRate ? stockAdjustment.getCurrencyRateValue() : Currency.DEFUALT_PHP_VALUE;
		if (!isNew) {
			List<SerialItem> savedSerialItems = serialItemService.getSerializeItemsByRefObjectIdAndObject(
					stockAdjustment.getEbObjectId(), warehouseId, orTypeId, false, stockAdjustment);
			serialItemService.setSerialNumberToInactive(savedSerialItems);
			savedSerialItems = null;
		}
		logger.info("Saving stock adjustment for NSB.");
		int currencyId = stockAdjustment.getCurrencyId() != null ? stockAdjustment.getCurrencyId() : Currency.DEFUALT_PHP_ID;
		stockAdjustment.setCurrencyId(currencyId);
		stockAdjustment.setCurrencyRateValue(currencyRateValue);
		eBFormServiceHandler.saveForm(stockAdjustment, user);

		// Save serialized items
		List<SerialItem> serialItems = stockAdjustmentDto.getSerialItems();
		if (serialItems != null && !serialItems.isEmpty()) {
			if (isStockIn) {
				CurrencyUtil.convertSerialItemsAmountToPhp(serialItems, currencyRateValue);
			}
			serialItemService.saveSerializedItems(serialItems, stockAdjustment.getEbObjectId(),
					warehouseId, user, orTypeId, isStockIn);
		}

		// Save reference document
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				stockAdjustment.getReferenceDocuments(), true);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		adjustmentService.saveForm(form, workflowName, user);
	}

	@Override
	public FormWorkflow getFormWorkflow(int stockAdjustmentId) {
		return saDao.get(stockAdjustmentId).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return saDao.getByWorkflowId(workflowId);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		adjustmentService.doBeforeSaving(currentWorkflowLog, bindingResult);
		StockAdjustment adjustment = saDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID && !currentWorkflowLog.getComment().isEmpty()) {
			List<SerialItem> saiSerialItems = serialItemService.getSerializeItemsByRefObjectIdAndObject(adjustment.getEbObjectId(),
					adjustment.getWarehouseId(), NSBStockAdjustmentDto.SA_IN_TO_SI_OR_TYPE_ID, false, adjustment);
			List<SerialItem> saoSerialItems = serialItemService.getSerializeItemsByRefObjectIdAndObject(adjustment.getEbObjectId(),
					adjustment.getWarehouseId(), NSBStockAdjustmentDto.SA_OUT_TO_SI_OR_TYPE_ID, false, adjustment);
			if (adjustment.getFormWorkflow().isComplete() && !saiSerialItems.isEmpty()) {
				String errorMessage = serialItemService.validateToBeCanceledRefForm(saiSerialItems);
				if(errorMessage != null) {
					bindingResult.reject("workflowMessage", errorMessage);
					currentWorkflowLog.setWorkflowMessage(errorMessage);
					return;
				}
			}
			serialItemService.setSerialNumberToInactive(saiSerialItems);
			serialItemService.setSerialNumberToInactive(saoSerialItems);
		}
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		adjustmentService.doAfterSaving(currentWorkflowLog);
	}

	/**
	 * Get the list of gvch stock adjustment data.
	 * @param sa The stock adjustment.
	 * @param typeId The stock adjustment classification type type.
	 * @return The list of gvch stock adjustment.
	 */
	public List<NSBStockAdjustmentDto> getStockAdjustmentDtos(StockAdjustment sa, int typeId) {
		NSBStockAdjustmentDto stockAdjustmentDto = new NSBStockAdjustmentDto();
		stockAdjustmentDto.setStockAdjustment(sa);
		boolean isCancelled = sa.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID;

		List<NSBStockAdjustmentDto> saDtos = new ArrayList<>();
		if(typeId == StockAdjustment.STOCK_ADJUSTMENT_IN){
			stockAdjustmentDto.setSerialItems(serialItemService.getSerializeItemsByRefObjectIdAndObject(sa.getEbObjectId(),
					sa.getWarehouseId(), NSBStockAdjustmentDto.SA_IN_TO_SI_OR_TYPE_ID, isCancelled, sa));
		} else {
			stockAdjustmentDto.setSerialItems(serialItemService.getSerializeItemsByRefObjectIdAndObject(sa.getEbObjectId(),
					sa.getWarehouseId(), NSBStockAdjustmentDto.SA_OUT_TO_SI_OR_TYPE_ID, isCancelled, sa));
		}
		saDtos.add(stockAdjustmentDto);
		return saDtos;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		return adjustmentService.getObjectInfo(ebObjectId, user);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		return adjustmentService.getDomain(ebObject);
	}

	/**
	 * Set stock adjustment classification by type and division
	 * @param divisionId The division id.
	 * @param typeId The type id.
	 * @return The classification.
	 */
	public Integer setClassificationByDivision(Integer divisionId, Integer typeId) {
		if(typeId == StockAdjustment.STOCK_ADJUSTMENT_IN) {
			switch (divisionId) {
			case 1:
				return StockAdjustmentClassification.STOCK_IN_CENTRAL;
			case 2:
				return StockAdjustmentClassification.STOCK_IN_NSB3;
			case 3:
				return StockAdjustmentClassification.STOCK_IN_NSB4;
			case 4:
				return StockAdjustmentClassification.STOCK_IN_NSB5;
			case 5:
				return StockAdjustmentClassification.STOCK_IN_NSB8;
			case 6:
				return StockAdjustmentClassification.STOCK_IN_NSB8A;
			default:
				return StockAdjustmentClassification.STOCK_IN;
			}
		}else {
			switch (divisionId) {
			case 1:
				return StockAdjustmentClassification.STOCK_OUT_CENTRAL;
			case 2:
				return StockAdjustmentClassification.STOCK_OUT_NSB3;
			case 3:
				return StockAdjustmentClassification.STOCK_OUT_NSB4;
			case 4:
				return StockAdjustmentClassification.STOCK_OUT_NSB5;
			case 5:
				return StockAdjustmentClassification.STOCK_OUT_NSB8;
			case 6:
				return StockAdjustmentClassification.STOCK_OUT_NSB8A;
			default:
				return StockAdjustmentClassification.STOCK_OUT;
			}
		}
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		StockAdjustment sa = (StockAdjustment) form;
		if(sa.getId() != 0) {
			List<SerialItem> savedSerialItems = serialItemService.getSerializedItemByRefObjId(sa.getEbObjectId(), NSBStockAdjustmentDto.SA_IN_TO_SI_OR_TYPE_ID);
			serialItemService.setSerialNumberToInactive(savedSerialItems);
			savedSerialItems = null;
		}
	}
}
