package eulap.eb.validator.inv;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.StockAdjustmentItem;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemBagQuantityService;
import eulap.eb.service.StockAdjustmentIsService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validation class of Stock Adjustment - Individual Selection.

 *
 */
@Service
public class StockAdjustmentIsValidator implements Validator{
	private Logger logger = Logger.getLogger(StockAdjustmentIsValidator.class);
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ItemBagQuantityService itemBagQtyService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private StockAdjustmentIsService stockAdjustmentService;

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public void validate(Object obj, Errors errors) {
		logger.info("Validating the Stock Adjustment form.");
		StockAdjustment sa = (StockAdjustment) obj;

		logger.debug("Validating company: "+sa.getCompanyId());
		ValidatorUtil.validateCompany(sa.getCompanyId(),
				companyService, errors, "companyId");

		logger.debug("Validating warehouse: "+sa.getWarehouseId());
		if(sa.getWarehouseId() == null)
			errors.rejectValue("warehouseId", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.0"));

		logger.debug("Validating adjustment type: "+sa.getStockAdjustmentTypeId());
		if(sa.getStockAdjustmentTypeId() == null)
			errors.rejectValue("stockAdjustmentTypeId", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.1"));

		logger.debug("Validating date: "+sa.getSaDate());
		if(sa.getSaDate() == null)
			errors.rejectValue("saDate", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.2"));
		else if(DateUtil.formatDate(sa.getSaDate()) == null)
			errors.rejectValue("saDate", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.3"));
		else if(!timePeriodService.isOpenDate(sa.getSaDate()))
			errors.rejectValue("saDate", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.4"));

		if(sa.getRemarks() != null) {
			logger.debug("Validating remarks: "+sa.getRemarks());
			if(sa.getRemarks().trim().length() > 100)
				errors.rejectValue("remarks", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.5"));
		}

		Integer row = 0;
		List<StockAdjustmentItem> saItems = sa.getSaItems();
		logger.debug("Validating SA Items.");
		for (StockAdjustmentItem adjustmentItem : saItems) {
			logger.debug("Validating item: "+adjustmentItem);
			if(adjustmentItem.getItemId() != null) {
				row++;

				logger.debug("Validating the quantity: "+adjustmentItem.getQuantity());
				if(adjustmentItem.getQuantity() == null) {
					errors.rejectValue("saMessage", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.6"));
					break;
				} else if(adjustmentItem.getQuantity() == 0) {
					errors.rejectValue("saMessage", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.7"));
					break;
				} else {
					if(sa.getTypeId().equals(StockAdjustment.STOCK_ADJUSTMENT_IS_IN)) {
						logger.debug("Validating the unit cost: "+adjustmentItem.getUnitCost());
						if(adjustmentItem.getUnitCost() != null) {
							if(adjustmentItem.getUnitCost() < 0) {
								errors.rejectValue("saMessage", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.8"));
								break;
							}
						}
						if (adjustmentItem.getQuantity() < 0) {
							errors.rejectValue("saMessage", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.9"));
							break;
						}
						if(adjustmentItem.getItemBagQuantity() != null) {
							if (adjustmentItem.getItemBagQuantity() < 0) {
								errors.rejectValue("saMessage", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.10"));
								break;
							}
						}
					} else if (sa.getTypeId().equals(StockAdjustment.STOCK_ADJUSTMENT_IS_OUT)) {
						String errorMessage = null;
						errorMessage = ValidationUtil.validateRefId(adjustmentItem.getStockCodeIs(), adjustmentItem.getRefenceObjectId());
						if(errorMessage != null) {
							errors.rejectValue("saMessage", null, null, errorMessage);
							break;
						}
						if(adjustmentItem.getQuantity() > 0) {
							errors.rejectValue("saMessage", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.11"));
							break;
						}
						if(adjustmentItem.getItemBagQuantity() != null && adjustmentItem.getItemBagQuantity() > 0) {
							errors.rejectValue("saMessage", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.12"));
							break;
						} else {
							Double bagQty = null;
							if(adjustmentItem.getItemBagQuantity() != null) {
								bagQty = Math.abs(adjustmentItem.getItemBagQuantity());
							}
							errorMessage = ValidationUtil.validateAvailableBagsAndStock(
									itemBagQtyService, adjustmentItem.getStockCodeIs(), sa.getCompanyId(),
									adjustmentItem.getItemId(), sa.getWarehouseId(), adjustmentItem.getRefenceObjectId(),
									bagQty, Math.abs(adjustmentItem.getQuantity()), adjustmentItem.getEbObjectId());
							if(errorMessage != null) {
								errors.rejectValue("saMessage", null, null, errorMessage);
							}
						}
					}
				}

			} else if(adjustmentItem.getQuantity() != null || adjustmentItem.getStockCode() != null) {
				errors.rejectValue("saMessage", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.13"));
				break;
			}
		}

		if(row == 0) {
			errors.rejectValue("saMessage", null, null, ValidatorMessages.getString("StockAdjustmentIsValidator.14"));
		}

		//Validate form status
		FormWorkflow workflow = sa.getId() != 0 ? stockAdjustmentService.getFormWorkflow(sa.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}
	}
}