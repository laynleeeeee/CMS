package eulap.eb.validator.inv;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CsiRawMaterial;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.service.CashSaleItemService;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemService;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.TimePeriodService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validator class for Cash Sales - Processing module

 *
 */
@Service
public class CashSaleProcessingValidator implements Validator {
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private CashSaleItemService cashSaleItemService;
	@Autowired
	private ItemService itemService;

	@Override
	public boolean supports(Class<?> clazz) {
		return CashSale.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		CashSale cashSale = (CashSale) object;

		ValidatorUtil.validateCompany(cashSale.getCompanyId(), companyService,
				errors, "companyId");

		if (cashSale.getSalesInvoiceNo() != null && !cashSale.getSalesInvoiceNo().trim().isEmpty()) {
			if (cashSale.getSalesInvoiceNo().trim().length() > CashSale.MAX_SALE_INVOICE_NO)
				errors.rejectValue("salesInvoiceNo", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.1")
						+ CashSale.MAX_SALE_INVOICE_NO + " characters.");
		}

		Date receiptDate = cashSale.getReceiptDate();
		if (receiptDate == null) {
			errors.rejectValue("receiptDate", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.2"));
		} else if (!timePeriodService.isOpenDate(receiptDate)) {
			errors.rejectValue("receiptDate", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.3"));
		}

		boolean isValidItemsAndQty = true;
		List<CashSaleItem> cashSaleItems = cashSale.getCashSaleItems();
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		if (cashSaleItems != null && !cashSaleItems.isEmpty()) {
			if (cashSaleItemService.hasInvalidItem(cashSaleItems)) {
				isValidItemsAndQty = false;
				errors.rejectValue("errorCSItems", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.4"));
			} if (saleItemUtil.hasNoQty(cashSaleItems)) {
				isValidItemsAndQty = false;
				errors.rejectValue("errorCSItems", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.5"));
			} else if (cashSaleItemService.hasNegQty(cashSaleItems)) {
				isValidItemsAndQty = false;
				errors.rejectValue("errorCSItems", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.6"));
			}
		} else {
			isValidItemsAndQty = false;
			errors.rejectValue("errorCSItems", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.7"));
		}

		// Check only Raw Materials if the prerequisites are valid.
		if (isValidItemsAndQty) {
			// Check raw materials of the main products.
			StringBuilder errorMessage = null;
			if (receiptDate != null) {
				Item item = null;
				for (CsiRawMaterial csiRm : cashSale.getRawMaterials()) {
					double existingStocks = NumberFormatUtil.roundOffTo2DecPlaces(itemService.getItemExistingStocks(
							csiRm.getItemId(), csiRm.getWarehouseId(), receiptDate));
					double qtyToBeWithdrawn = csiRm.getQuantity() - (csiRm.getOrigQty() == null ? 0 : csiRm.getOrigQty());
					if (existingStocks < NumberFormatUtil.roundOffTo2DecPlaces(qtyToBeWithdrawn)) {
						// Build the error message for the insufficient raw materials
						item = itemService.getItem(csiRm.getItemId());
						String stockCodeAndDesc = item.getStockCodeAndDesc();
						if (errorMessage == null) {
							errorMessage = new StringBuilder(ValidatorMessages.getString("CashSaleProcessingValidator.8")+stockCodeAndDesc);
						} else {
							errorMessage.append(", "+stockCodeAndDesc);
						}
						item = null;
					}
				}
			}
			if (errorMessage != null) {
				errors.rejectValue("errorCSItems", null, null,
						errorMessage.toString());
			}
		}

		// Check grand total
		double totalSalesAmt = SaleItemUtil.computeTotalAmt(cashSaleItems);
		if (totalSalesAmt < 0) {
			errors.rejectValue("cash", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.9"));
		} else {
			if (cashSaleService.isCashLessTAmount(totalSalesAmt, cashSale.getCash()))
				errors.rejectValue("cash", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.10"));
		}

		if (cashSale.getArCustomerId() == null || cashSale.getArCustomerId() == -1) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.11"));
		}

		if (cashSale.getArCustomerAccountId() == null) {
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("CashSaleProcessingValidator.12"));
		}
	}
}
