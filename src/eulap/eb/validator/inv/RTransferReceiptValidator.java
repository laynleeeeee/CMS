package eulap.eb.validator.inv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.domain.hibernate.RTransferReceiptItem;
import eulap.eb.domain.hibernate.TransferReceiptType;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemBagQuantityService;
import eulap.eb.service.ItemService;
import eulap.eb.service.RTransferReceiptService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validator class of Retail - Transfer Receipt

 *
 */
@Service
public class RTransferReceiptValidator implements Validator{
	private static Logger logger = Logger.getLogger(RTransferReceiptValidator.class);
	@Autowired
	private ItemService itemService;
	@Autowired
	private RTransferReceiptService trService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ItemBagQuantityService itemBagQtyService;
	@Autowired
	private FormStatusService formStatusService;

	@Override
	public boolean supports(Class<?> clazz) {
		return RTransferReceipt.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		validate(target, errors, "", true);
	}

	public void validate(Object obj, Errors errors, String fieldPrepend, boolean isValidateTrItemsOnly) {
		logger.info("Validating the Retail - Transfer Receipt.");
		RTransferReceipt tr = (RTransferReceipt) obj;
		boolean validateWarehouse = true;

		logger.debug("Validating TR Date: "+tr.getTrDate());
		if(tr.getTrDate() == null)
			errors.rejectValue(fieldPrepend+"trDate", null, null, ValidatorMessages.getString("RTransferReceiptValidator.0"));
		else if(!timePeriodService.isOpenDate(tr.getTrDate()))
			errors.rejectValue(fieldPrepend+"trDate", null, null, ValidatorMessages.getString("RTransferReceiptValidator.1"));

		logger.debug("Validating company id: "+tr.getCompanyId());
		ValidatorUtil.validateCompany(tr.getCompanyId(), companyService,
				errors, fieldPrepend+"companyId");

		logger.debug("Validating warehouse from id: "+tr.getWarehouseFromId());
		if(tr.getWarehouseFromId() == null) {
			validateWarehouse = false;
			errors.rejectValue(fieldPrepend+"warehouseFromId", null, null, ValidatorMessages.getString("RTransferReceiptValidator.2"));
		}

		logger.debug("Validating warehouse to id: "+tr.getWarehouseToId());
		if(tr.getWarehouseToId() == null) {
			validateWarehouse = false;
			errors.rejectValue(fieldPrepend+"warehouseToId", null, null, ValidatorMessages.getString("RTransferReceiptValidator.3"));
		}

		if(validateWarehouse) {
			logger.debug("Validate warehouse from and to.");
			if(tr.getWarehouseFromId().equals(tr.getWarehouseToId()))
				errors.rejectValue(fieldPrepend+"warehouseToId", null, null, ValidatorMessages.getString("RTransferReceiptValidator.4"));
		}

		logger.debug("Validating DR Number "+tr.getDrNumber());
		if(tr.getDrNumber() != null) {
			if(tr.getDrNumber().trim().length() > 20)
				errors.rejectValue(fieldPrepend+"drNumber", null, null, ValidatorMessages.getString("RTransferReceiptValidator.5"));
		}

		RTransferReceipt savedTr = null;
		boolean isWarehouseChanged = false;
		if(tr.getId() > 0) {
			savedTr = trService.getRTransferReceipt(tr.getId());
			isWarehouseChanged = !tr.getWarehouseFromId().equals(savedTr.getWarehouseFromId());
		}

		List<RTransferReceiptItem> trItems = tr.getrTrItems();

		if(tr.getTrDate() != null) {
			validateTrItems(errors, tr, isWarehouseChanged, trItems, fieldPrepend, isValidateTrItemsOnly);
		}

		//If there are no errors, set the summaries list of TR Items to be saved.
		if(!errors.hasErrors()) {
			tr.setrTrItems(trItems);
		}

		//Validate form status
		FormWorkflow workflow = tr.getId() != 0 ? trService.getFormWorkflow(tr.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
		}
	}

	private void validateTrItems(Errors errors, RTransferReceipt tr, boolean isWarehouseChanged,
			List<RTransferReceiptItem> trItems, String fieldPrepend, boolean isValidateTrItemsOnly) {
		Integer row = 0;
		Integer typeId = tr.getTransferReceiptTypeId();
		Integer whFromId = tr.getWarehouseFromId();
		List<Integer> trItemIds = extractItemIds(trItems);
		logger.debug("Validating the TR Items.");
		for (RTransferReceiptItem trItem : trItems) {
			if(trItem.getItemId() != null) {
				row++;

				logger.info("Validating the item id: "+trItem.getItemId());
				if(typeId.equals(RTransferReceipt.TR_RETAIL)) {
					if(Collections.frequency(trItemIds, trItem.getItemId()) > 1) {
						errors.rejectValue(fieldPrepend+"trMessage", null, null, ValidatorMessages.getString("RTransferReceiptValidator.6"));
						break;
					}
				}

				logger.debug("Validating the quantity: "+trItem.getQuantity());
				if(trItem.getQuantity() == null) {
					errors.rejectValue(fieldPrepend+"trMessage", null, null, ValidatorMessages.getString("RTransferReceiptValidator.7")+row+ValidatorMessages.getString("RTransferReceiptValidator.8")); //$NON-NLS-3$
					break;
				} else if(trItem.getQuantity() <= 0) {
					errors.rejectValue(fieldPrepend+"trMessage", null, null, ValidatorMessages.getString("RTransferReceiptValidator.9")+row+ValidatorMessages.getString("RTransferReceiptValidator.10")); //$NON-NLS-3$
					break;
				} else {
					String quantityErrorMsg = null;

					switch (tr.getTransferReceiptTypeId()) {
					case TransferReceiptType.RETAIL:
						//Validate the existing stocks as of the form date.
						quantityErrorMsg = ValidationUtil.validateWithdrawnQty(itemService, warehouseService,
								trItem.getItemId(), isWarehouseChanged, trItems, tr.getTrDate(), tr.getWarehouseFromId(), row);
						if(quantityErrorMsg != null) {
							errors.rejectValue(fieldPrepend+"trMessage", null, null, quantityErrorMsg);
						}
						break;

					case TransferReceiptType.INDIVIDUAL_SELECTION:
						trItem.setStockCode(trItem.getStockCodeIs());
						String errorMessage = null;
						errorMessage = ValidationUtil.validateRefId(trItem.getStockCode(), trItem.getRefenceObjectId());
						if(errorMessage == null) {
							errorMessage = ValidationUtil.validateAvailableBagsAndStock(
									itemBagQtyService, trItem.getStockCode(), tr.getCompanyId(), trItem.getItemId(),
									whFromId, trItem.getRefenceObjectId(), trItem.getItemBagQuantity(),
									trItem.getQuantity(), trItem.getEbObjectId());
						}
						if(errorMessage != null) {
							errors.rejectValue("trMessage", null, null, errorMessage);
						}
					}
				}

			} else if(trItem.getQuantity() != null || trItem.getStockCode() != null) {
				errors.rejectValue(fieldPrepend+"trMessage", null, null, ValidatorMessages.getString("RTransferReceiptValidator.13"));
				break;
			}
		}

		if(row == 0 && isValidateTrItemsOnly) {
			logger.debug("User did not select stock code to transfer.");
			errors.rejectValue(fieldPrepend+"trMessage", null, null, ValidatorMessages.getString("RTransferReceiptValidator.14"));
		}
	}

	private List<Integer> extractItemIds(List<RTransferReceiptItem> receiptItems) {
		List<Integer> extractedIds = new ArrayList<Integer>();
		for (RTransferReceiptItem tri : receiptItems) {
			extractedIds.add(tri.getItemId());
			logger.trace("Added id: "+tri.getItemId());
		}
		logger.debug("Extracted "+extractedIds.size()+" ids.");
		return extractedIds;
	}
}
