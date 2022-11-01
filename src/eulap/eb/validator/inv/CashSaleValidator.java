package eulap.eb.validator.inv;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.ArReceiptType;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleArLine;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.service.ArLineService;
import eulap.eb.service.CashSaleItemService;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.CompanyService;
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
 * Validator class for {@link CashSale}

 *
 */
@Service
public class CashSaleValidator implements Validator {
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private CashSaleItemService cashSaleItemService;
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
		return CashSale.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		validate(object, errors, "", true);
	}

	public void validate(Object object, Errors errors, String fieldPrepend, Boolean isValidateItems) {
		CashSale cashSale = (CashSale) object;

		ValidatorUtil.validateCompany(cashSale.getCompanyId(), companyService,
				errors, fieldPrepend+"companyId");

		if (cashSale.getArReceiptTypeId() == null)
			errors.rejectValue(fieldPrepend+"arReceiptTypeId", null, null, ValidatorMessages.getString("CashSaleValidator.0"));
		else {
			if (cashSale.getArReceiptTypeId() == ArReceiptType.TYPE_CHECK) {
				if (cashSale.getRefNumber() == null || cashSale.getRefNumber().trim().isEmpty())
					errors.rejectValue(fieldPrepend+"refNumber", null, null, ValidatorMessages.getString("CashSaleValidator.1"));
				else {
					if (cashSale.getRefNumber().trim().length() > CashSale.MAX_REF_NUMBER)
						errors.rejectValue(fieldPrepend+"refNumber", null, null,
								ValidatorMessages.getString("CashSaleValidator.2") + CashSale.MAX_REF_NUMBER + ValidatorMessages.getString("CashSaleValidator.3"));
				}
			}
		}

		if (cashSale.getArCustomerId() == null || cashSale.getArCustomerId() == -1)
			errors.rejectValue(fieldPrepend+"arCustomerId", null, null, ValidatorMessages.getString("CashSaleValidator.4"));

		if (cashSale.getArCustomerAccountId() == null)
			errors.rejectValue(fieldPrepend+"arCustomerAccountId", null, null, ValidatorMessages.getString("CashSaleValidator.5"));

		if (cashSale.getSalesInvoiceNo() != null && !cashSale.getSalesInvoiceNo().trim().isEmpty()) {
			if (cashSale.getSalesInvoiceNo().trim().length() > CashSale.MAX_SALE_INVOICE_NO)
				errors.rejectValue(fieldPrepend+"salesInvoiceNo", null, null, ValidatorMessages.getString("CashSaleValidator.6") +
						CashSale.MAX_SALE_INVOICE_NO + ValidatorMessages.getString("CashSaleValidator.7"));
		}

		if (cashSale.getReceiptDate() == null)
			errors.rejectValue(fieldPrepend+"receiptDate", null, null, ValidatorMessages.getString("CashSaleValidator.8"));
		else if (!timePeriodService.isOpenDate(cashSale.getReceiptDate()))
				errors.rejectValue(fieldPrepend+"receiptDate", null, null, ValidatorMessages.getString("CashSaleValidator.9"));

		if (cashSale.getMaturityDate() == null)
			errors.rejectValue(fieldPrepend+"maturityDate", null, null, ValidatorMessages.getString("CashSaleValidator.10"));
		else if (!timePeriodService.isOpenDate(cashSale.getMaturityDate()))
				errors.rejectValue(fieldPrepend+"maturityDate", null, null, ValidatorMessages.getString("CashSaleValidator.11"));

		if (cashSale.getCash() == null) {
			errors.rejectValue(fieldPrepend+"cash", null, null, ValidatorMessages.getString("CashSaleValidator.12"));
		} else {
			double totalAmount = cashSale.getTotalAmount() != null ? cashSale.getTotalAmount() : 0.0;
			List<CashSaleArLine> arLines = cashSale.getCashSaleArLines();
			if (arLines != null && !arLines.isEmpty()) {
				for (CashSaleArLine cashSaleArLine : arLines) {
					double csLAmount = cashSaleArLine.getAmount() != null ? cashSaleArLine.getAmount() : 0;
					totalAmount += csLAmount;
				}
			}
			if(cashSale.getWtAmount() != null) {
				totalAmount -= cashSale.getWtAmount();
			}
			if (cashSaleService.isCashLessTAmount(totalAmount, cashSale.getCash()))
				errors.rejectValue(fieldPrepend+"cash", null, null, ValidatorMessages.getString("CashSaleValidator.13"));
		}

		List<CashSaleItem> cashSaleItems = new ArrayList<CashSaleItem>(cashSale.getCashSaleItems());
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		boolean isValidItemsAndQty = true;
		if (cashSaleItems != null && !cashSaleItems.isEmpty()) {
			if (cashSaleItemService.hasInvalidItem(cashSaleItems))
				errors.rejectValue(fieldPrepend+"errorCSItems", null, null, ValidatorMessages.getString("CashSaleValidator.14"));
			if (cashSaleItemService.hasNoWarehouse(cashSaleItems))
				errors.rejectValue(fieldPrepend+"errorCSItems", null, null, ValidatorMessages.getString("CashSaleValidator.15"));
			if (saleItemUtil.hasNoQty(cashSaleItems)) {
				isValidItemsAndQty = false;
				errors.rejectValue(fieldPrepend+"errorCSItems", null, null, ValidatorMessages.getString("CashSaleValidator.16"));
			} else if (cashSaleItemService.hasNegQty(cashSaleItems)) {
				isValidItemsAndQty = false;
				errors.rejectValue(fieldPrepend+"errorCSItems", null, null, ValidatorMessages.getString("CashSaleValidator.17"));
			}

			if(!cashSale.getCashSaleTypeId().equals(CashSaleType.INDIV_SELECTION)) {
				String duplicateDiscErrorMsg = saleItemUtil.validateDuplicateItemAndDisc(cashSaleItems);
				if(duplicateDiscErrorMsg != null) {
					errors.rejectValue(fieldPrepend+"errorCSItems", null, null, duplicateDiscErrorMsg);
				}
			}
		} else if(isValidateItems){
			isValidItemsAndQty = false;
			errors.rejectValue(fieldPrepend+"errorCSItems", null, null, ValidatorMessages.getString("CashSaleValidator.18"));
		}

		if(cashSale.getCashSaleTypeId().equals(CashSaleType.INDIV_SELECTION) && isValidItemsAndQty) {
			//Validate the quantity from individual selection
			for (CashSaleItem csi : cashSaleItems) {
				String errorMessage = null;
				errorMessage = ValidationUtil.validateRefId(csi.getStockCode(), csi.getRefenceObjectId());
				if(errorMessage == null) {
					errorMessage = ValidationUtil.validateAvailableBagsAndStock(itemBagQuantityService, csi.getStockCode(), cashSale.getCompanyId(), 
							csi.getItemId(), csi.getWarehouseId(), csi.getRefenceObjectId(), csi.getItemBagQuantity(), csi.getQuantity(), csi.getEbObjectId());
				}

				if(errorMessage != null) {
					errors.rejectValue(fieldPrepend+"errorCSItems", null, null, errorMessage);
					break;
				}
			}

		} else {
			//Validate the existing stocks as of the form date.
			if(cashSale.getReceiptDate() != null && isValidItemsAndQty) {
				int row = 0;
				for (CashSaleItem csi : cashSaleItems) {
					row++;
					if (csi.getWarehouseId() != null) {
						boolean isWarehouseChanged = !csi.getWarehouseId().equals(csi.getOrigWarehouseId());
						String quantityErrorMsg = ValidationUtil.validateSaleItems(itemService, warehouseService, csi.getItemId(),
								isWarehouseChanged, cashSaleItems, cashSale.getReceiptDate(), csi.getWarehouseId(), row);
						if(quantityErrorMsg != null) {
							errors.rejectValue(fieldPrepend+"errorCSItems", null, null, quantityErrorMsg);
							break;
						}
					}
				}
			}
		}

		//Validate AR Lines/Other Charges
		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		otherCharges.addAll(cashSale.getCashSaleArLines());
		if(!otherCharges.isEmpty()) {
			String errorMessage = arLineService.validateArLines(otherCharges, null, cashSale.getCompanyId());
			if(errorMessage != null) {
				errors.rejectValue(fieldPrepend+"csArLineMesage", null, null, errorMessage);
			}
		}

		double totalSalesAmt = SaleItemUtil.computeTotalAmt(cashSaleItems);
		double totalOtherCharges = SaleItemUtil.computeTotalOtherCharges(otherCharges);
		if((totalSalesAmt+totalOtherCharges) < 0) {
			errors.rejectValue(fieldPrepend+"csArLineMesage", null, null, ValidatorMessages.getString("CashSaleValidator.19"));
		}
		cashSaleItems = null;

		//Validate form status
		FormWorkflow workflow = cashSale.getId() != 0 ? cashSaleService.getFormWorkflow(cashSale.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
		}
	}
}
