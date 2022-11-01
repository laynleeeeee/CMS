package eulap.eb.validator.inv;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.StockAdjustmentItem;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ItemService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.SerialItemService;
import eulap.eb.service.StockAdjustmentService;
import eulap.eb.service.StockAdjustmentTypeService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.NSBStockAdjustmentDto;

/**
 * Validation class of Stock Adjustment.

 *
 */
@Service
public class StockAdjustmentValidator implements Validator{
	private Logger logger = Logger.getLogger(StockAdjustmentValidator.class);
	@Autowired
	private ItemService itemService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private StockAdjustmentService stockAdjustmentService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private StockAdjustmentTypeService satService;
	@Autowired
	private SerialItemService serialItemService;

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public void validate(Object obj, Errors errors){
		validate(obj, errors, "", true);
	}

	public void validateForm(NSBStockAdjustmentDto stockAdjustmentDto, Errors errors) {
		StockAdjustment stockAdjustment = stockAdjustmentDto.getStockAdjustment();
		validate(stockAdjustment, errors, "stockAdjustment.", false);
		serialItemService.validateSerialItems("stockAdjustment.saMessage", "siMessage",
				stockAdjustmentDto.getStockAdjustment().getSaItems().isEmpty(),
				stockAdjustment.getTypeId() == 1, false, stockAdjustmentDto.getSerialItems(), errors);
	}

	public void validate(Object obj, Errors errors, String fieldPrepend, boolean isValidatedSAItems) {
		logger.info("Validating the Stock Adjustment form.");
		StockAdjustment sa = (StockAdjustment) obj;

		logger.debug("Validating company: "+sa.getCompanyId());
		ValidatorUtil.validateCompany(sa.getCompanyId(),
				companyService, errors, fieldPrepend+"companyId");

		logger.debug("Validating warehouse: "+sa.getWarehouseId());
		if(sa.getWarehouseId() == null) {
			errors.rejectValue(fieldPrepend+"warehouseId", null, null, ValidatorMessages.getString("StockAdjustmentValidator.0"));
		} else if (!warehouseService.getWarehouse(sa.getWarehouseId()).isActive()){
			errors.rejectValue(fieldPrepend+"warehouseId", null, null, ValidatorMessages.getString("StockAdjustmentValidator.20"));
		}

		logger.debug("Validating adjustment type: "+sa.getStockAdjustmentTypeId());
		if(sa.getStockAdjustmentTypeId() == null) {
			errors.rejectValue(fieldPrepend+"stockAdjustmentTypeId", null, null, ValidatorMessages.getString("StockAdjustmentValidator.1"));
		} else if (!satService.getSAdjustmentType(sa.getStockAdjustmentTypeId()).isActive()) {
			errors.rejectValue(fieldPrepend+"stockAdjustmentTypeId", null, null, ValidatorMessages.getString("StockAdjustmentValidator.19"));
		}

		logger.debug("Validating date: "+sa.getSaDate());
		if(sa.getSaDate() == null)
			errors.rejectValue(fieldPrepend+"saDate", null, null, ValidatorMessages.getString("StockAdjustmentValidator.2"));
		else if(DateUtil.formatDate(sa.getSaDate()) == null)
			errors.rejectValue(fieldPrepend+"saDate", null, null, ValidatorMessages.getString("StockAdjustmentValidator.3"));
		else if(!timePeriodService.isOpenDate(sa.getSaDate()))
			errors.rejectValue(fieldPrepend+"saDate", null, null, ValidatorMessages.getString("StockAdjustmentValidator.4"));

		if(sa.getDivisionId() == null) {
			errors.rejectValue(fieldPrepend+"divisionId", null, null, ValidatorMessages.getString("StockAdjustmentValidator.15"));
		} else if (!divisionService.getDivision(sa.getDivisionId()).isActive()) {
			errors.rejectValue(fieldPrepend+"divisionId", null, null, ValidatorMessages.getString("StockAdjustmentValidator.16"));
		}

		if (sa.getTypeId() == 1) {
			if (sa.getCurrencyId() == null) {
				errors.rejectValue(fieldPrepend+"currencyId", null, null, ValidatorMessages.getString("StockAdjustmentValidator.17"));
			} else if (!currencyService.getCurency(sa.getCurrencyId()).isActive()) {
				errors.rejectValue(fieldPrepend+"currencyId", null, null, ValidatorMessages.getString("StockAdjustmentValidator.18"));
			}
		}

		if(sa.getBmsNumber().length() > 50) {
			errors.rejectValue(fieldPrepend+"bmsNumber", null, null, ValidatorMessages.getString("StockAdjustmentValidator.14"));
		}

		Integer row = 0;
		List<StockAdjustmentItem> saItems = sa.getSaItems();
		logger.debug("Validating SA Items.");

		StockAdjustment savedAdjustment = null;
		boolean isWarehouseChanged = false;
		if(sa.getId() > 0) {
			savedAdjustment = stockAdjustmentService.getStockAdjustment(sa.getId());
			isWarehouseChanged = !sa.getWarehouseId().equals(savedAdjustment.getWarehouseId());
		}
		for (StockAdjustmentItem adjustmentItem : saItems) {
			logger.debug("Validating item: "+adjustmentItem);
			if(adjustmentItem.getItemId() != null) {
				row++;
				Item item = itemService.getItem(adjustmentItem.getItemId());
				if(!item.isActive()) {
					errors.rejectValue(fieldPrepend+"saMessage", null, null, String.format(ValidatorMessages.getString("StockAdjustmentValidator.21"), row));
				}
				if((itemService.ifFinishedGoods(item))&& sa.getTypeId() == 1) {
					if(adjustmentItem.getPoNumber() == null) {
						errors.rejectValue(fieldPrepend+"saMessage", null, null, String.format(ValidatorMessages.getString("StockAdjustmentValidator.22"), row));
					} else if (adjustmentItem.getPoNumber().trim().length() > 20) {
						errors.rejectValue(fieldPrepend+"saMessage", null, null, String.format(ValidatorMessages.getString("StockAdjustmentValidator.23"), row));
					}
				}
//				if (!itemService.ifFinishedGoods(item) && adjustmentItem.getPoNumber() != null) {
//					errors.rejectValue(fieldPrepend+"saMessage", null, null, String.format(ValidatorMessages.getString("StockAdjustmentValidator.24"), row));
//				}
				logger.debug("Validating the quantity: "+adjustmentItem.getQuantity());
				if(adjustmentItem.getQuantity() == null) {
					errors.rejectValue(fieldPrepend+"saMessage", null, null, ValidatorMessages.getString("StockAdjustmentValidator.6"));
					break;
				} else if(adjustmentItem.getQuantity() == 0) {
					errors.rejectValue(fieldPrepend+"saMessage", null, null, ValidatorMessages.getString("StockAdjustmentValidator.7"));
					break;
				} else {
					if(sa.getTypeId() == 1) {
						logger.debug("Validating the unit cost: "+adjustmentItem.getUnitCost());
						if(adjustmentItem.getUnitCost() == null) {
							adjustmentItem.setUnitCost(0.0);
						}
						if(adjustmentItem.getUnitCost() < 0 ) {
							errors.rejectValue(fieldPrepend+"saMessage", null, null, ValidatorMessages.getString("StockAdjustmentValidator.9"));
							break;
						} else if(adjustmentItem.getQuantity() <= 0) {
							errors.rejectValue(fieldPrepend+"saMessage", null, null, ValidatorMessages.getString("StockAdjustmentValidator.10"));
							break;
						}
					} else {
						if(adjustmentItem.getQuantity() > 0) {
							errors.rejectValue(fieldPrepend+"saMessage", null, null, ValidatorMessages.getString("StockAdjustmentValidator.11"));
							break;
						} else if (sa.getTypeId() == 2) {
							//Validate only the stock adjustment out.
							if(sa.getSaDate() != null) {
								String quantityErrorMsg = ValidationUtil.validateWithdrawnQty(itemService, warehouseService, 
										adjustmentItem.getItemId(), isWarehouseChanged, saItems, sa.getSaDate(), sa.getWarehouseId(), row);
								if(quantityErrorMsg != null) {
									errors.rejectValue(fieldPrepend+"saMessage", null, null, quantityErrorMsg);
									break;
								}
							}
						}
					}
				}

			} else if(adjustmentItem.getQuantity() != null || adjustmentItem.getStockCode() != null){
				errors.rejectValue(fieldPrepend+"saMessage", null, null, ValidatorMessages.getString("StockAdjustmentValidator.12"));
				break;
			}
		}

		if(row == 0 && isValidatedSAItems) {
			errors.rejectValue(fieldPrepend+"saMessage", null, null, ValidatorMessages.getString("StockAdjustmentValidator.13"));
		}

		//Validate form status
		FormWorkflow workflow = sa.getId() != 0 ? stockAdjustmentService.getFormWorkflow(sa.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
		}

		refDocumentService.validateReferences(sa.getReferenceDocuments(), errors,fieldPrepend);
	}
}
