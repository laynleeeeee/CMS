package eulap.eb.validator.inv;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.domain.hibernate.RepackingItem;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ItemService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.RepackingService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.WarehouseService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validation class for Repacking.

 *
 */
@Service
public class RepackingValidator implements Validator{
	private static Logger logger = Logger.getLogger(RepackingValidator.class);
	@Autowired
	private ItemService itemService;
	@Autowired
	private RepackingService repackingService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private DivisionService divisionService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Repacking.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		validate(obj, errors, true);
	}

	public void validate(Object obj, Errors errors, boolean validateRepackingItems) {
		Repacking rp = (Repacking) obj;

		logger.debug("Validating company:"+rp.getCompanyId());
		ValidatorUtil.validateCompany(rp.getCompanyId(), companyService,
				errors, "companyId");

		logger.debug("Validating warehouse: "+rp.getWarehouseId());
		if (rp.getWarehouseId() == null) {
			errors.rejectValue("warehouseId", null, null, ValidatorMessages.getString("RepackingValidator.0"));
		} else if(!warehouseService.getWarehouse(rp.getWarehouseId()).isActive()) {
			errors.rejectValue("warehouseId", null, null, ValidatorMessages.getString("RepackingValidator.17"));
		}

		// Disable this for non-NSB projects
		Integer divisionId = rp.getDivisionId();
		if (divisionId == null) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("RepackingValidator.15"));
		} else if (!divisionService.getDivision(divisionId).isActive()) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("RepackingValidator.16"));
		}

		logger.debug("Validating date: "+rp.getrDate());
		if (rp.getrDate() == null) {
			errors.rejectValue("rDate", null, null, ValidatorMessages.getString("RepackingValidator.1"));
		} else if (DateUtil.formatDate(rp.getrDate()) == null) {
			errors.rejectValue(ValidatorMessages.getString("RepackingValidator.2"), null, null,
					ValidatorMessages.getString("RepackingValidator.3"));
		} else if (!timePeriodService.isOpenDate(rp.getrDate())) {
			errors.rejectValue("rDate", null, null, ValidatorMessages.getString("RepackingValidator.4"));
		}

		if (rp.getRemarks() != null) {
			logger.debug("Validating remarks: "+rp.getRemarks());
			if(rp.getRemarks().trim().length() > 100)
				errors.rejectValue("remarks", null, null, ValidatorMessages.getString("RepackingValidator.5"));
		}

		if (validateRepackingItems) {
			Integer row = 0;
			List<RepackingItem> rItems = rp.getrItems();
			Integer fromItemId = null;
			Integer toItemId = null;
			boolean isValidItemIdsAndQty = true;
			boolean isWarehouseChanged = false;
			if (rp.getId() > 0) {
				Repacking savedRp = repackingService.getRepacking(rp.getId(), rp.getRepackingTypeId());
				isWarehouseChanged = !rp.getWarehouseId().equals(savedRp.getWarehouseId());
			}
			logger.debug("Validating Repacking Items.");
			if(rItems != null) {
				for (RepackingItem ri : rItems) {
					fromItemId = ri.getFromItemId();
					toItemId = ri.getToItemId();
					if(fromItemId != null && toItemId != null) {
						row++;
						if(!itemService.getItem(fromItemId).isActive()) {
							errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.18") + row);
							break;
						}
						if(!itemService.getItem(toItemId).isActive()) {
							errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.19") + row);
							break;
						}

						logger.debug("Validating item id from: "+ fromItemId +" and item id to: "+ toItemId);
						if(fromItemId.equals(toItemId)) {
							errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.6") + row);
							break;
						}

						logger.debug("Validating the quantity: "+ri.getQuantity());
						if(ri.getQuantity() == null) {
							isValidItemIdsAndQty = false;
							errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.7"));
							break;
						} else if(ri.getQuantity() <= 0) {
							isValidItemIdsAndQty = false;
							errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.8"));
							break;
						}

						logger.debug("Validating the repacked quantity: "+ri.getRepackedQuantity());
						if(ri.getRepackedQuantity() == null) {
							errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.9"));
							break;
						} else if(ri.getRepackedQuantity() <= 0) {
							errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.10"));
							break;
						} else if(rp.getrDate() != null && rp.getWarehouseId() != null && isValidItemIdsAndQty) {
							//Validate the existing stocks as of the form date.
							double totalQty = getTotalQtyPerItem(isWarehouseChanged, ri.getFromItemId(), rItems);
							String qtyErrorMsg = ValidationUtil.validateQuantity(itemService, warehouseService,
									ri.getFromItemId(), rp.getrDate(), rp.getWarehouseId(), totalQty, row);
							if(qtyErrorMsg != null) {
								errors.rejectValue("repackingMessage", null, null, qtyErrorMsg);
								break;
							}
						}

					} else if(fromItemId == null && toItemId != null) {
						row++;
						isValidItemIdsAndQty = false;
						errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.11"));
						break;
					} else if (toItemId == null && fromItemId != null) {
						row++;
						isValidItemIdsAndQty = false;
						errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.12"));
						break;
					} else if((fromItemId == null && toItemId == null)
							&& (ri.getQuantity() != null || ri.getRepackedQuantity() != null)) {
						row++;
						isValidItemIdsAndQty = false;
						errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.13"));
						break;
					}
				}
			}
			if(row == 0) {
				errors.rejectValue("repackingMessage", null, null, ValidatorMessages.getString("RepackingValidator.14"));
			}
		}
		refDocumentService.validateReferences(rp.getReferenceDocuments(), errors);
	}

	private double getTotalQtyPerItem(boolean isWarehouseChanged, Integer itemId, List<RepackingItem> rpItems) {
		double totalQty = 0;
		for (RepackingItem rpi : rpItems) {
			if(itemId.equals(rpi.getFromItemId()) && rpi.getQuantity() != null) {
				if(isWarehouseChanged) {
					totalQty += rpi.getQuantity();
				} else {
					totalQty += Math.abs(rpi.getQuantity()) - (rpi.getOrigQty() != null ? rpi.getOrigQty() : 0);
				}
			}
		}
		return totalQty;
	}
}
