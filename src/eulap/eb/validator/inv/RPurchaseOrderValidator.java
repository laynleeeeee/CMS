package eulap.eb.validator.inv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.PurchaseOrderLine;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.RPurchaseOrderItem;
import eulap.eb.service.CompanyService;
import eulap.eb.service.RPurchaseOrderService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.RequisitionFormService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validation class of Retail - Purchase Order

 *
 */
@Service
public class RPurchaseOrderValidator implements Validator{
	private static Logger logger = Logger.getLogger(RPurchaseOrderValidator.class);
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private RequisitionFormService requisitionFormService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private RPurchaseOrderService poService;

	@Override
	public boolean supports(Class<?> clazz) {
		return RPurchaseOrder.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		validate(object, errors, false);
	}

	public void validate(Object object, Errors errors, boolean isRequestByUserInput) {
		RPurchaseOrder po = (RPurchaseOrder) object;

		logger.debug("Validating company id: "+po.getCompanyId());
		ValidatorUtil.validateCompany(po.getCompanyId(), companyService, errors, "companyId");

		logger.debug("Validating supplier id: "+po.getSupplierId());
		if (po.getSupplierId() == null)
			errors.rejectValue("supplierId", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.0"));

		logger.debug("Validating supplier account id: "+po.getSupplierAccountId());
		if (po.getSupplierAccountId() == null)
			errors.rejectValue("supplierAccountId", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.1"));

		logger.debug("Validating term id: "+po.getTermId());
		if (po.getTermId() == null)
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.2"));

		logger.debug("Validating date: "+po.getPoDate());
		if (po.getPoDate() == null)
			errors.rejectValue("poDate", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.3"));
		else if (!timePeriodService.isOpenDate(po.getPoDate()))
			errors.rejectValue("poDate", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.4"));

		if (isRequestByUserInput) {
			String requestorName = po.getRequesterName();
			if (requestorName != null && !StringFormatUtil.removeExtraWhiteSpaces(requestorName).isEmpty()) {
				if (StringFormatUtil.removeExtraWhiteSpaces(requestorName).length() > RPurchaseOrder.REQUESTER_NAME_MAX_CHAR) {
					errors.rejectValue("requesterName", null, null,
						String.format(ValidatorMessages.getString("RPurchaseOrderValidator.16"),
							RPurchaseOrder.REQUESTER_NAME_MAX_CHAR));
				}
			}
		}

		Integer row = 0;
		List<RPurchaseOrderItem> poItems = po.getrPoItems();
		List<PurchaseOrderLine> otherCharges = po.getPoLines();
		List<Integer> poItemIds = extractItemIds(poItems);
		Integer frequencyOfItemId = null;
		Integer itemId = null;
		boolean hasItems = poItems != null && !poItems.isEmpty();
		boolean hasLines = otherCharges != null && !otherCharges.isEmpty();
		double totalPoAmount = 0;
		if(!hasItems && !hasLines) {
			errors.rejectValue("poMessage", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.27"));
		} else {
			//Items
			for (RPurchaseOrderItem poItem : poItems) {
				if (poItem.getItemId() != null) {
					row++;
					//Validate stock code
					itemId = poItem.getItemId();
					logger.debug("Validating item id: "+itemId);
					if (itemId == -1) {
						errors.rejectValue("rPoItems", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.6")+row);
						break;
					}
					//Duplicate stock codes
					logger.debug("Checking for duplicates");
					frequencyOfItemId = Collections.frequency(poItemIds, poItem.getItemId());
					logger.debug("Frequency for current item is: "+frequencyOfItemId);
					if (frequencyOfItemId > 1) {
						errors.rejectValue("rPoItems", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.7"));
						break;
					}
					//Quantity
					logger.debug("Validating quantity: "+poItem.getQuantity());
					if(poItem.getQuantity() != null && 
							poService.hasExceedMaxOrderingPoint(itemId, poItem.getQuantity(), poItem.getId())) {
						errors.rejectValue("rPoItems", null, null,
								String.format(ValidatorMessages.getString("RPurchaseOrderValidator.26"), row));
					}
					if (poItem.getQuantity() == null) {
						errors.rejectValue("rPoItems", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.8")
								+ row + ValidatorMessages.getString("RPurchaseOrderValidator.9"));
						break;
					} else if (poItem.getQuantity() <= 0) {
						errors.rejectValue("rPoItems", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.10")
								+ row + ValidatorMessages.getString("RPurchaseOrderValidator.11"));
						break;
					} else if (po.getPrReference() != null && !po.getPrReference().isEmpty()) {
						double remQty = NumberFormatUtil.roundOffTo2DecPlaces(
								requisitionFormService.getRemainingRFQty(poItem.getRefenceObjectId(), po.getId()));
						if (remQty < poItem.getQuantity()) {
							errors.rejectValue("rPoItems", null, null, String.format(ValidatorMessages.getString("RPurchaseOrderValidator.17"),
									poItem.getStockCode(), row));
						}
						break;
					}
					//Unit Cost
					logger.debug("Validating quantity: " +poItem.getUnitCost());
					if (poItem.getUnitCost() != null) {
						if (poItem.getUnitCost() < 0) {
							errors.rejectValue("rPoItems", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.12")
									+ row + ValidatorMessages.getString("RPurchaseOrderValidator.13"));
							break;
						}
						totalPoAmount += poItem.getUnitCost();
					}
				} else if (poItem.getQuantity() != null || poItem.getStockCode() != null) {
					errors.rejectValue("rPoItems", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.14"));
					break;
				}
			}
			//Lines
			if (otherCharges != null && !otherCharges.isEmpty()) {
				int rowCount = 0;
				for (PurchaseOrderLine oc : otherCharges) {
					rowCount++;
					Integer apLineSetupId = oc.getApLineSetupId();
					String apLineName = oc.getApLineSetupName();
					Double rowAmount = oc.getAmount();
					if (apLineSetupId != null || apLineName != null || rowAmount != null) {
						if (apLineSetupId == null) {
							if (apLineName != null && !apLineName.isEmpty()) {
								errors.rejectValue("poLines", null, null,
										String.format(ValidatorMessages.getString("RReceivingReportValidator.23"), rowCount));
							} else {
								errors.rejectValue("poLines", null, null,
										String.format(ValidatorMessages.getString("RReceivingReportValidator.22"), rowCount));
							}
						}
						if(oc.getQuantity() == null || oc.getQuantity() == 0) {
							//Quantity is required in row %d.
							errors.rejectValue("poLines", null, null,
									String.format(ValidatorMessages.getString("RPurchaseOrderValidator.24"), rowCount));
						}
						if(oc.getUnitOfMeasurementId() == null) {
							//UOM is required in row %d.
							errors.rejectValue("poLines", null, null,
									String.format(ValidatorMessages.getString("RPurchaseOrderValidator.25"), rowCount));
						}
						totalPoAmount += oc.getUpAmount() != null ? oc.getUpAmount() : 0;
					}
				}
			}
		}
		if(totalPoAmount == 0) {
			errors.rejectValue("poMessage", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.28"));
		}

		Date estimatedDeliveryDate = po.getEstDeliveryDate();
		if (estimatedDeliveryDate == null) {
			errors.rejectValue("estDeliveryDate", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.18"));
		} else if (!timePeriodService.isOpenDate(estimatedDeliveryDate)) {
			errors.rejectValue("estDeliveryDate", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.19"));
		}

		String bmsNumber = po.getBmsNumber();
		if (bmsNumber != null && bmsNumber.trim().length() > RPurchaseOrder.BMS_NUMBER_MAX_CHAR) {
			errors.rejectValue("bmsNumber", null, null,
				String.format(ValidatorMessages.getString("RPurchaseOrderValidator.20"),
					RPurchaseOrder.BMS_NUMBER_MAX_CHAR));
		}

		if (po.getCurrencyId() == null) {
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("RPurchaseOrderValidator.21"));
		}

		refDocumentService.validateReferences(po.getReferenceDocuments(), errors);
	}

	/**
	 * Extract the item ids from the list of PO Items
	 * @return List of item ids.
	 */
	public List<Integer> extractItemIds(List<RPurchaseOrderItem> poItems) {
		logger.debug("Extracting item ids from list.");
		List<Integer> itemIds = new ArrayList<Integer>();
		if (poItems != null) {
			for (RPurchaseOrderItem item : poItems) {
				if (item.getItemId() != null) {
					if (item.getItemId() != -1) {
						itemIds.add(item.getItemId());
						logger.trace("Added id: "+item.getItemId());
					}
				}
			}
		}
		if (itemIds.isEmpty()) {
			logger.debug("No item selected for PO.");
		} else {
			logger.debug("Successfully extracted "+itemIds.size()+" item ids.");
		}
		return itemIds;
	}
}
