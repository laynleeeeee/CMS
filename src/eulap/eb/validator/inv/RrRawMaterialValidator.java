package eulap.eb.validator.inv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.ApLineSetup;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RriBagDiscount;
import eulap.eb.domain.hibernate.RriBagQuantity;
import eulap.eb.service.ApLineSetupService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.RrRawMaterialService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validation class for RR - Raw Materials form.

 *
 */
@Service
public class RrRawMaterialValidator implements Validator {
	@Autowired
	private RrRawMaterialService rawMaterialService;
	@Autowired
	private ApLineSetupService apLineSetupService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private FormStatusService formStatusService;

	@Override
	public boolean supports(Class<?> clazz) {
		return APInvoice.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		APInvoice rawMaterial = (APInvoice) obj;
		boolean isExistingInvNo = false;
		Integer typeId = rawMaterial.getInvoiceTypeId();
		boolean isRrRmIs = typeId.equals(InvoiceType.RR_RAW_MAT_TYPE_ID);

		RReceivingReport rr = rawMaterial.getReceivingReport();
		ValidatorUtil.validateCompany(rr.getCompanyId(), companyService,
				errors, "receivingReport.companyId");

		if(rr.getWarehouseId() == null) {
			errors.rejectValue("receivingReport.warehouseId", null, null, ValidatorMessages.getString("RrRawMaterialValidator.0"));
		}

		if(rr.getPoNumber() != null) {
			if(rr.getPoNumber().trim().length() > 100) {
				errors.rejectValue("receivingReport.poNumber", null, null, ValidatorMessages.getString("RrRawMaterialValidator.1"));
			}
		}

		if(rawMaterial.getGlDate() == null) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("RrRawMaterialValidator.2"));
		} else if(!timePeriodService.isOpenDate(rawMaterial.getGlDate())) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("RrRawMaterialValidator.3"));
		}

		if(rawMaterial.getInvoiceDate() == null) {
			errors.rejectValue("invoiceDate", null, null, ValidatorMessages.getString("RrRawMaterialValidator.4"));
		}

		if(rawMaterial.getDueDate() == null) {
			errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("RrRawMaterialValidator.5"));
		}

		if(rawMaterial.getSupplierId() == null || rawMaterial.getSupplierId() == -1) {
			errors.rejectValue("supplierId", null, null, ValidatorMessages.getString("RrRawMaterialValidator.6"));
		}

		if(rawMaterial.getSupplierAccountId() == null || rawMaterial.getSupplierAccountId() == -1) {
			errors.rejectValue("supplierAccountId", null, null, ValidatorMessages.getString("RrRawMaterialValidator.7"));
		}

		if(rawMaterial.getTermId() == null || rawMaterial.getTermId() == 0) {
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("RrRawMaterialValidator.8"));
		}

		String supplierInvNo = rawMaterial.getInvoiceNumber();
		List<RReceivingReportItem> rrItems = rawMaterial.getRrItems();
		boolean isValidItems = true;
		if(isRrRmIs) {
			if(supplierInvNo != null && !supplierInvNo.trim().isEmpty()) {
				isExistingInvNo = true;
				if(supplierInvNo.trim().length() > APInvoice.MAX_CHAR_INVOICE_NO)
					errors.rejectValue("invoiceNumber", null, null,
							String.format(ValidatorMessages.getString("RrRawMaterialValidator.9"), APInvoice.MAX_CHAR_INVOICE_NO));
			}
	
			if ((rr.getDeliveryReceiptNo() == null || rr.getDeliveryReceiptNo().trim().isEmpty()) && !isExistingInvNo) {
				errors.rejectValue("receivingReport.deliveryReceiptNo", null, null,
						ValidatorMessages.getString("RrRawMaterialValidator.10"));
			} else if(rr.getDeliveryReceiptNo().length() > 20) {
				errors.rejectValue("receivingReport.deliveryReceiptNo", null, null,
						ValidatorMessages.getString("RrRawMaterialValidator.11"));
			}

			Integer row = 0;
			for (RReceivingReportItem rri : rrItems) {
				if(rri.getItemId() != null && rri.getItemId() != 0) {
					row++;
					if(rri.getQuantity() == null) {
						errors.rejectValue("rrItems", null, null, ValidatorMessages.getString("RrRawMaterialValidator.12"));
						isValidItems = false;
						break;
					} else if(rri.getQuantity() <= 0) {
						errors.rejectValue("rrItems", null, null, ValidatorMessages.getString("RrRawMaterialValidator.13"));
						isValidItems = false;
						break;
					}

					if(rri.getUnitCost() != null && rri.getUnitCost() < 0) {
						errors.rejectValue("rrItems", null, null, ValidatorMessages.getString("RrRawMaterialValidator.14"));
						isValidItems = false;
						break;
					}
				} else if(rri.getQuantity() != null && rri.getQuantity() > 0) {
					errors.rejectValue("rrItems", null, null, ValidatorMessages.getString("RrRawMaterialValidator.15")
							+ row + ValidatorMessages.getString("RrRawMaterialValidator.16"));
					break;
				} else if (rri.getItemId() == null && !rri.getStockCode().trim().isEmpty()) {
					errors.rejectValue("rrItems", null, null, ValidatorMessages.getString("RrRawMaterialValidator.17"));
					break;
				}
			}

			if(row == 0) {
				errors.rejectValue("rrItems", null, null, ValidatorMessages.getString("RrRawMaterialValidator.18"));
			}
		} else {
			if(supplierInvNo == null || supplierInvNo.trim().isEmpty()) {
				errors.rejectValue("invoiceNumber", null, null, ValidatorMessages.getString("RrRawMaterialValidator.28"));
			} else {
				if(supplierInvNo.trim().length() > APInvoice.MAX_CHAR_INVOICE_NO) {
					errors.rejectValue("invoiceNumber", null, null,
							String.format(ValidatorMessages.getString("RrRawMaterialValidator.34"), APInvoice.MAX_CHAR_INVOICE_NO));
				}
			}

			if(rrItems != null && !rrItems.isEmpty()) {
				for (RReceivingReportItem rri : rrItems) {
					 if (rri.getItemId() == null) {
							errors.rejectValue("errorMsgStockCode", null, null, ValidatorMessages.getString("RrRawMaterialValidator.17"));
							break;
					} else {
						if(rri.getUnitCost() != null && rri.getUnitCost() < 0) {
							errors.rejectValue("errorMsgBuyingPrice", null, null, ValidatorMessages.getString("RrRawMaterialValidator.35"));
							isValidItems = false;
							break;
						}
					}
				}
			} else {
				errors.rejectValue("errorMsgStockCode", null, null, ValidatorMessages.getString("RrRawMaterialValidator.36"));
			}
		}

		List<ApInvoiceLine> apInvoiceLines = rawMaterial.getApInvoiceLines();
		boolean isValidApLines = true;
		if(!apInvoiceLines.isEmpty()) {
			List<ApLineSetup> apLineSetups = null;
			ApLineSetup apLineSetup = null;
			for (ApInvoiceLine apl : apInvoiceLines) {
				if(apl.getApLineSetupId() == null && apl.getAmount() != null) {
					errors.rejectValue("apInvoiceLines", null, null, ValidatorMessages.getString("RrRawMaterialValidator.19"));
					break;
				} else if(apl.getApLineSetupName() != null && !apl.getApLineSetupName().trim().isEmpty()) {
					apLineSetups = apLineSetupService.getApLineSetups(rr.getCompanyId(), null, apl.getApLineSetupName());
					if(!apLineSetups.isEmpty()) {
						apLineSetup = apLineSetups.get(0);
						String errorMessage = validateApLineSetup(apLineSetup, rr.getCompanyId());
						if(errorMessage != null) {
							errors.rejectValue("apInvoiceLines", null, null, errorMessage);
							break;
						}
					} else {
						errors.rejectValue("apInvoiceLines", null, null, ValidatorMessages.getString("RrRawMaterialValidator.20"));
						break;
					}

					// With AP Line but without amount
					if(apl.getAmount() == null) {
						errors.rejectValue("apInvoiceLines", null, null, ValidatorMessages.getString("RrRawMaterialValidator.21"));
						isValidApLines = false;
						break;
					}
				}
			}
		}

		double grandTotal = 0;
		boolean isValidRRItemOCharges = isValidItems && isValidApLines;
		if(isRrRmIs) {
			if(isValidRRItemOCharges) {
				List<RReceivingReportItem> processedItems = rawMaterialService.processAddOnsAndDiscounts(rrItems);
				grandTotal = rawMaterialService.computeTotalAmt(processedItems, apInvoiceLines);
			}
		} else {
			if(isValidRRItemOCharges && !validateRriBagQty(rawMaterial.getRriBagQuantities(), errors)
					&& !validateRriBagDiscount(rawMaterial.getRriBagDiscounts(), errors)) {
				grandTotal = rawMaterialService.computeTotalNetAmount(rrItems, apInvoiceLines, rawMaterial.getRriBagQuantities(),
						rawMaterial.getRriBagDiscounts(), rawMaterial.getRrRawMatItemDto());
			}
		}

		if(grandTotal < 0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("RrRawMaterialValidator.22"));
		}

		//Validate form status
		FormWorkflow workflow = rawMaterial.getId() != 0 ? rawMaterialService.getFormWorkflow(rawMaterial.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}
	}

	private String validateApLineSetup(ApLineSetup apLineSetup, int companyId) {
		AccountCombination ac = apLineSetup.getAccountCombination();
		if(ac != null) {
			if(ac.getCompanyId() != companyId) {
				return ValidatorMessages.getString("RrRawMaterialValidator.23");
			}
		}
		return null;
	}

	private boolean validateRriBagQty(List<RriBagQuantity> rriBagQtys, Errors errors) {
		String attribName = "rriBagQtyErrMsg";
		boolean hasError = false;
		if(rriBagQtys != null && !rriBagQtys.isEmpty()) {
			int index = 0;
			for(RriBagQuantity rriBagQty : rriBagQtys) {
				++index;
				if(rriBagQty.getBagQuantity() == null) {
					errors.rejectValue(attribName, null, null, String.format(ValidatorMessages.getString("RrRawMaterialValidator.24"), index));
					hasError = true;
				} else if(rriBagQty.getBagQuantity() < 0){
					errors.rejectValue(attribName, null, null, String.format(ValidatorMessages.getString("RrRawMaterialValidator.30"), index));
					hasError = true;
				}

				if(rriBagQty.getQuantity() == null) {
					errors.rejectValue(attribName, null, null, String.format(ValidatorMessages.getString("RrRawMaterialValidator.25"), index));
					hasError = true;
				} else if(rriBagQty.getQuantity() < 0){
					errors.rejectValue(attribName, null, null, String.format(ValidatorMessages.getString("RrRawMaterialValidator.31"), index));
					hasError = true;
				}

				if(rriBagQty.getNetWeight() < 0) {
					errors.rejectValue(attribName, null, null, String.format(ValidatorMessages.getString("RrRawMaterialValidator.33"), index));
					hasError = true;
				}
			}
		} else {
			hasError = true;
			errors.rejectValue(attribName, null, null, ValidatorMessages.getString("RrRawMaterialValidator.29"));
		}
		return hasError;
	}

	private boolean validateRriBagDiscount(List<RriBagDiscount> rriBagDiscounts, Errors errors) {
		boolean hasError = false;
		if(rriBagDiscounts != null && !rriBagDiscounts.isEmpty()) {
			String attribName = "rriBagDiscountErrMsg";
			int index = 0;
			for(RriBagDiscount rriBagDiscount : rriBagDiscounts) {
				++index;
				if(rriBagDiscount.getBagQuantity() == null) {
					errors.rejectValue(attribName, null, null, String.format(ValidatorMessages.getString("RrRawMaterialValidator.24"), index));
					hasError = true;
				} else if(rriBagDiscount.getBagQuantity() < 0){
					errors.rejectValue(attribName, null, null, String.format(ValidatorMessages.getString("RrRawMaterialValidator.30"), index));
					hasError = true;
				}
				if(rriBagDiscount.getDiscountQuantity() == null) {
					errors.rejectValue(attribName, null, null, String.format(ValidatorMessages.getString("RrRawMaterialValidator.26"), index));
					hasError = true;
				} else if(rriBagDiscount.getDiscountQuantity() < 0){
					errors.rejectValue(attribName, null, null, String.format(ValidatorMessages.getString("RrRawMaterialValidator.32"), index));
					hasError = true;
				}
			}
		}
		return hasError;
	}
}
