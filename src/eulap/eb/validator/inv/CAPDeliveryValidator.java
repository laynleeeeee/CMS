package eulap.eb.validator.inv;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.CAPDelivery;
import eulap.eb.domain.hibernate.CAPDeliveryArLine;
import eulap.eb.domain.hibernate.CAPDeliveryItem;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.service.ArLineService;
import eulap.eb.service.CAPDeliveryService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CustomerAdvancePaymentService;
import eulap.eb.service.ItemBagQuantityService;
import eulap.eb.service.ItemService;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validation class for {@link CAPDelivery}

 *
 */
@Service
public class CAPDeliveryValidator implements Validator {
	@Autowired
	private CustomerAdvancePaymentService caPaymentService;
	@Autowired
	private CAPDeliveryService capDeliveryService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ArLineService arLineService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private FormStatusService formStatusService;


	@Override
	public boolean supports(Class<?> clazz) {
		return CAPDelivery.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		CAPDelivery delivery = (CAPDelivery) object;
		boolean isValidReference = true;

		ValidatorUtil.validateCompany(delivery.getCompanyId(), companyService,
				errors, "customerAdvancePaymentId");

		Integer capId = delivery.getCustomerAdvancePaymentId();
		if(delivery.getWipsoId() == null && delivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.WIP_SPECIAL_ORDER)){
			isValidReference = false;
			errors.rejectValue("wipsoId", null, null, ValidatorMessages.getString("CAPDeliveryValidator.0"));
		} else if(capId == null && !delivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.WIP_SPECIAL_ORDER)) {
			isValidReference = false;
			errors.rejectValue("customerAdvancePaymentId", null, null, ValidatorMessages.getString("CAPDeliveryValidator.1"));
		}

		if(delivery.getSalesInvoiceNo().trim().isEmpty()) {
			errors.rejectValue("salesInvoiceNo", null, null, ValidatorMessages.getString("CAPDeliveryValidator.2"));
		} else if(delivery.getSalesInvoiceNo().trim().length() > 100) {
			errors.rejectValue("salesInvoiceNo", null, null, ValidatorMessages.getString("CAPDeliveryValidator.3"));
		}

		Date deliveryDate = delivery.getDeliveryDate();
		if(deliveryDate == null) {
			errors.rejectValue("deliveryDate", null, null, ValidatorMessages.getString("CAPDeliveryValidator.4"));
		} else if(!timePeriodService.isOpenDate(deliveryDate)) {
			errors.rejectValue("deliveryDate", null, null, ValidatorMessages.getString("CAPDeliveryValidator.5"));
		}

		List<CAPDeliveryItem> deliveryItems = delivery.getDeliveryItems();
		if(deliveryItems == null || deliveryItems.isEmpty()) {
			errors.rejectValue("deliveryItems", null, null, ValidatorMessages.getString("CAPDeliveryValidator.6"));
		} else if(isValidReference){
			int row = 0;
			for (CAPDeliveryItem di : deliveryItems) {
				row++;
				if(di.getItemId() == null) {
					errors.rejectValue("deliveryItems", null, null, ValidatorMessages.getString("CAPDeliveryValidator.7"));
					break;
				}

				if(di.getQuantity() == null || di.getQuantity() <= 0) {
					errors.rejectValue("deliveryItems", null, null, ValidatorMessages.getString("CAPDeliveryValidator.8"));
					break;
				}

				if(di.getWarehouseId() == null) {
					errors.rejectValue("deliveryItems", null, null, ValidatorMessages.getString("CAPDeliveryValidator.9"));
					break;
				}

				if(delivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.INDIV_SELECTION)) {
					String errorMessage = ValidationUtil.validateRefId(di.getStockCode(), di.getRefenceObjectId());
					if(errorMessage != null) {
						errors.rejectValue("deliveryItems", null, null, errorMessage);
						break;
					}
				}

				if(!delivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.INDIV_SELECTION) &&
						!delivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.WIP_SPECIAL_ORDER)) {
					if(deliveryDate != null) {
						boolean isWarehouseChanged = !di.getWarehouseId().equals(di.getOrigWarehouseId());
						String errorMsg = ValidationUtil.validateWithdrawnQty(itemService, warehouseService,
								di.getItemId(), isWarehouseChanged, deliveryItems, deliveryDate, di.getWarehouseId(), row);
						if(errorMsg != null) {
							errors.rejectValue("deliveryItems", null, null, errorMsg);
							break;
						}
					}
				}
			}

			if(delivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.RETAIL)) {
				SaleItemUtil<CAPDeliveryItem> saleUtil = new SaleItemUtil<CAPDeliveryItem>();
				String duplicateItemAndDisc = saleUtil.validateDuplicateItemAndDisc(deliveryItems);
				if (duplicateItemAndDisc != null) {
					errors.rejectValue("deliveryItems", null, null, ValidatorMessages.getString("CAPDeliveryValidator.10"));
				}
			} else {
				// Validate the quantity from individual selection
				boolean isCapWIPSO = delivery.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.WIP_SPECIAL_ORDER);
				for (CAPDeliveryItem capdItem : deliveryItems) {
					double qtyToBeWithdrawn = (capdItem.getQuantity() == null ? 0 : capdItem.getQuantity()) - (capdItem.getOrigQty() == null ? 0 : capdItem.getOrigQty());
					if(isCapWIPSO){
						if (capdItem.getReferenceId() == null) {
							errors.rejectValue("deliveryItems", null, null, ValidatorMessages.getString("CAPDeliveryValidator.11"));
						} else {
							double availableStock = itemService.getTotalAvailStocks(capdItem.getStockCode(), capdItem.getWarehouseId());
							if(availableStock < qtyToBeWithdrawn){
								errors.rejectValue("deliveryItems", null, null, capdItem.getStockCode() + ValidatorMessages.getString("CAPDeliveryValidator.12"));
							}
						}
					}
					Double remainingBagQty = itemBagQuantityService.getCAPISRemainingItmBagQty(capId, capdItem.getItemId(), delivery.getId());
					if((remainingBagQty != null && capdItem.getItemBagQuantity() != null)
							&& (capdItem.getItemBagQuantity() > remainingBagQty)) {
						errors.rejectValue("deliveryItems", null, null, capdItem.getStockCode() + ValidatorMessages.getString("CAPDeliveryValidator.15"));
					}
				}
			}

			//Validate Other Charges
			List<CAPDeliveryArLine> arLines = delivery.getDeliveryArLines();
			List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
			if(arLines != null && !arLines.isEmpty()) {
				otherCharges.addAll(arLines);
				String arLinesValMessage = arLineService.validateArLines(otherCharges, delivery.getArCustomerAcctId());
				if(arLinesValMessage != null) {
					errors.rejectValue("deliveryArLines", null, null, arLinesValMessage);
				}
			}

			//Validate total amount. Total sales + total other charges.
			double totalDeliverySales = SaleItemUtil.computeTotalAmt(deliveryItems);
			totalDeliverySales += SaleItemUtil.computeTotalOtherCharges(otherCharges);
			if(totalDeliverySales < 0) {
				errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("CAPDeliveryValidator.13"));
			} else {
				double remainingAmount = 0;
				//Check the remaining CAP amount
				remainingAmount = capDeliveryService.computeRemainingAmt(caPaymentService.computeTotalCapSales(capId),
						delivery.getId(), capId);
				if(totalDeliverySales > remainingAmount) {
					errors.rejectValue("errorMessage", null, null, String.format(ValidatorMessages.getString("CAPDeliveryValidator.14"),
							NumberFormatUtil.format(remainingAmount)));
				}
			}

			//Freeing memory allocation
			arLines = null;
			otherCharges = null;
			deliveryItems = null;
		}
		//Validate form status
		FormWorkflow workflow = delivery.getId() != 0 ? capDeliveryService.getFormWorkflow(delivery.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}
	}

}
