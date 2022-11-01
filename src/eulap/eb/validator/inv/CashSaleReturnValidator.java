package eulap.eb.validator.inv;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnArLine;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.service.ArLineService;
import eulap.eb.service.CashSaleItemService;
import eulap.eb.service.CashSaleReturnIsService;
import eulap.eb.service.CashSaleReturnItemService;
import eulap.eb.service.CashSaleReturnService;
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
 * Validator class for {@link CashSaleReturn}

 *
 */
@Service
public class CashSaleReturnValidator implements Validator{
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CashSaleReturnItemService cashSaleReturnItemService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private CashSaleItemService cashSaleItemService;
	@Autowired
	private CashSaleReturnIsService cashSaleReturnIsService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ArLineService arLineService;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private CashSaleReturnService cashSaleReturnService;
	@Autowired
	private FormStatusService formStatusService;

	private final Logger logger = Logger.getLogger(CashSaleReturnValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return CashSaleReturn.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors){
		validate(object, errors, "", true);
	}

	public void validate(Object object, Errors errors, String fieldPrepend, boolean isValidateCSRItems){
		logger.info("Validating Cash Sales Return form.");
		CashSaleReturn cashSaleReturn = (CashSaleReturn) object;

		ValidatorUtil.validateCompany(cashSaleReturn.getCompanyId(),
				companyService, errors, fieldPrepend+"companyId");

		Boolean isValidQty = true;
		if (cashSaleReturn.getCashSaleId() == null && cashSaleReturn.getRefCashSaleReturnId() == null) {
			errors.rejectValue(fieldPrepend+"cashSaleId", null, null, ValidatorMessages.getString("CashSaleReturnValidator.0"));
		}

		if (cashSaleReturn.getArCustomerId() == null) {
			errors.rejectValue(fieldPrepend+"arCustomerId", null, null, ValidatorMessages.getString("CashSaleReturnValidator.1"));
		}

		if (cashSaleReturn.getArCustomerAccountId() == null) {
			errors.rejectValue(fieldPrepend+"arCustomerAccountId", null, null, ValidatorMessages.getString("CashSaleReturnValidator.2"));
		}

		if (cashSaleReturn.getSalesInvoiceNo() != null && !cashSaleReturn.getSalesInvoiceNo().trim().isEmpty()) {
			if (cashSaleReturn.getSalesInvoiceNo().trim().length() > CashSale.MAX_SALE_INVOICE_NO)
				errors.rejectValue(fieldPrepend+"salesInvoiceNo", null, null, ValidatorMessages.getString("CashSaleReturnValidator.3") +
						CashSale.MAX_SALE_INVOICE_NO + ValidatorMessages.getString("CashSaleReturnValidator.4"));
		}

		if (cashSaleReturn.getDate() == null) {
			errors.rejectValue(fieldPrepend+"date", null, null, ValidatorMessages.getString("CashSaleReturnValidator.5"));
		} else if (!timePeriodService.isOpenDate(cashSaleReturn.getDate())) {
				errors.rejectValue(fieldPrepend+"date", null, null, ValidatorMessages.getString("CashSaleReturnValidator.6"));
		}
		List<CashSaleReturnItem> cashSaleReturnItems = null;
		if(cashSaleReturn.getCashSaleReturnItems() != null){
			cashSaleReturnItems = new ArrayList<CashSaleReturnItem>(cashSaleReturn.getCashSaleReturnItems());
		}
		if (cashSaleReturnItems != null && !cashSaleReturnItems.isEmpty()) {
			if (cashSaleReturnItemService.hasInvalidItem(cashSaleReturnItems)) {
				errors.rejectValue(fieldPrepend+"errorCSRItems", null, null, ValidatorMessages.getString("CashSaleReturnValidator.7"));
			}

			if (cashSaleReturnItemService.hasNoWarehouse(cashSaleReturnItems)) {
				errors.rejectValue(fieldPrepend+"errorCSRItems", null, null, ValidatorMessages.getString("CashSaleReturnValidator.8"));
			}

			if (cashSaleReturnItemService.hasNoQty(cashSaleReturnItems)) {
				errors.rejectValue(fieldPrepend+"errorCSRItems", null, null, ValidatorMessages.getString("CashSaleReturnValidator.9"));
				isValidQty = false;
			} else if (cashSaleReturnItemService.hasNoAmount(cashSaleReturnItems)) {
					errors.rejectValue(fieldPrepend+"errorCSRItems", null, null, ValidatorMessages.getString("CashSaleReturnValidator.10"));
			}

			if (cashSaleReturnItemService.hasNoReturnItems(cashSaleReturnItems)) {
				errors.rejectValue(fieldPrepend+"errorCSRItems", null, null, ValidatorMessages.getString("CashSaleReturnValidator.11"));
			}

			SaleItemUtil<CashSaleReturnItem> saleUtil = new SaleItemUtil<CashSaleReturnItem>();
			List<CashSaleReturnItem> exchangedItems = SaleItemUtil.filterSaleReturnItems(cashSaleReturnItems, false);
			List<CashSaleReturnItem> returnedItems = SaleItemUtil.filterSaleReturnItems(cashSaleReturnItems, true);
			List<CashSaleReturnItem> csrItems = new ArrayList<>(cashSaleReturnItems);
			SaleItemUtil<CashSaleItem> csSaleUtil = new SaleItemUtil<CashSaleItem>();

			if(isValidQty){
				//Validate exchanged items
				if (saleUtil.isExchangingNeg(exchangedItems)) {
					errors.rejectValue(fieldPrepend+"errorCSRItems", null, null, ValidatorMessages.getString("CashSaleReturnValidator.12"));
				}

				//Validate returned items
				if (cashSaleReturnItemService.hasInvalidReturnItem(returnedItems)) {
					errors.rejectValue(fieldPrepend+"errorCSRItems", null, null, ValidatorMessages.getString("CashSaleReturnValidator.13"));
				}
			}
			boolean isSplit = false;
			if (cashSaleReturn.getCashSaleId() != null && cashSaleReturn.getCashSaleId() != 0) {
				isSplit = csSaleUtil.hasSameItemAndDiscount(cashSaleItemService.getCSItems(cashSaleReturn.getCashSaleId()));
				List<CashSaleItem> cashSaleItems = cashSaleItemService.getAllCashSaleItems(cashSaleReturn.getCashSaleId());
				List<CashSaleReturnItem> returnedStocks =
						saleUtil.getSummarisedSaleItems(cashSaleReturnItemService.getCSRItemsByReference(cashSaleReturn.getCashSaleId()));
				SaleItemUtil.updateRefQuantity(csrItems, cashSaleItems, returnedStocks);
			} else if (cashSaleReturn.getRefCashSaleReturnId() != null && cashSaleReturn.getRefCashSaleReturnId() != 0) {
				isSplit = saleUtil.hasSameItemAndDiscount(cashSaleReturnItemService.getCSRItems(cashSaleReturn.getRefCashSaleReturnId()));
				List<CashSaleReturnItem> returnItems = cashSaleReturnItemService.getAllCashSaleReturnItems(cashSaleReturn.getRefCashSaleReturnId(), true);
				List<CashSaleReturnItem> returnedStocks =
						saleUtil.getSummarisedSaleItems(cashSaleReturnItemService.getCSRItemsByReferenceCSR(cashSaleReturn.getRefCashSaleReturnId(), cashSaleReturn.getId()));
				SaleItemUtil.updateRefQuantity(csrItems, returnItems, returnedStocks);
			}

			//Validate returned items
			for (CashSaleReturnItem csri : returnedItems) {
				boolean isCSAsReference = csri.getCashSaleItemId() != null ? true : false;
				int refCSItemId = isCSAsReference ? csri.getCashSaleItemId() : csri.getRefCashSaleReturnItemId();
				int refCashSaleId = isCSAsReference ? cashSaleReturn.getCashSaleId() : cashSaleReturn.getRefCashSaleReturnId();
				double remainingQty = isSplit ? cashSaleReturnItemService.getRemainingQty(refCashSaleId, csri.getItemId(),
							csri.getWarehouseId(), isCSAsReference) : cashSaleReturnIsService.getRemainingQty(refCSItemId, isCSAsReference);
				Integer refObjectId = cashSaleReturn.getCashSaleTypeId() == CashSaleType.INDIV_SELECTION
						? csri.getRefenceObjectId() : null;
				String errorMessage = ValidationUtil.validateReturnItems(csri, refCSItemId,
						refObjectId, remainingQty, csri.getStockCode());
				if(errorMessage != null) {
					errors.rejectValue(fieldPrepend+"errorCSRItems", null, null, errorMessage);
					break;
				}
				if(csri.getItemBagQuantity() != null) {
					if(csri.getOrigBagQty() == null || csri.getOrigBagQty() == 0.0) {
						errors.rejectValue(fieldPrepend+"errorCSRItems", null, null,
								ValidatorMessages.getString("CashSaleReturnValidator.17"));
						break;
					}
					if(csri.getItemBagQuantity() > 0) {
						errors.rejectValue(fieldPrepend+"errorCSRItems", null, null,
								ValidatorMessages.getString("CashSaleReturnValidator.15"));
						break;
					} else if (Math.abs(csri.getItemBagQuantity()) > csri.getOrigBagQty()) {
						errors.rejectValue(fieldPrepend+"errorCSRItems", null, null,
								ValidatorMessages.getString("CashSaleReturnValidator.16"));
						break;
					}
				}
			}

			//Validate exchanged items from individual selection type only
			if(cashSaleReturn.getCashSaleTypeId().equals(CashSaleType.INDIV_SELECTION)) {
				for (CashSaleReturnItem csri : csrItems) {
					String errorMessage = null;
					if(csri.getCashSaleItemId() == null) {
						if(errorMessage == null) {
							errorMessage = ValidationUtil.validateAvailableBagsAndStock(itemBagQuantityService, csri.getStockCode(), cashSaleReturn.getCompanyId(), 
									csri.getItemId(), csri.getWarehouseId(), csri.getRefenceObjectId(), csri.getItemBagQuantity(), csri.getQuantity(), csri.getEbObjectId());
						}
						if(errorMessage != null) {
							errors.rejectValue(fieldPrepend+"errorCSRItems", null, null, errorMessage);
							break;
						}
					}
				}
			} else {
				//Validate exchanged retail items.
				if (cashSaleReturn.getDate() != null && returnedItems != null) {
					int row = returnedItems.size();
					for (CashSaleReturnItem csri : exchangedItems) {
						row++;
						boolean isWarehouseChanged = !csri.getWarehouseId().equals(csri.getOrigWarehouseId());
						String quantityErrorMsg = ValidationUtil.validateWithdrawnQty(itemService, warehouseService,
								csri.getItemId(), isWarehouseChanged, exchangedItems, cashSaleReturn.getDate(), csri.getWarehouseId(), row);
						if(quantityErrorMsg != null) {
							errors.rejectValue(ValidatorMessages.getString("CashSaleReturnValidator.31"), null, null, quantityErrorMsg);
							break;
						}
					}
				}
			}
		} else if(isValidateCSRItems){
			errors.rejectValue(fieldPrepend+"errorCSRItems", null, null, ValidatorMessages.getString("CashSaleReturnValidator.14"));
		}

		cashSaleReturnItems = null;

		List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
		List<CashSaleReturnArLine> csrArLines = cashSaleReturn.getCashSaleReturnArLines();
		if(csrArLines != null) {
			otherCharges.addAll(csrArLines);
			if(!otherCharges.isEmpty()) {
				String errorMessage = arLineService.validateArLines(otherCharges, null, cashSaleReturn.getCompanyId());
				if(errorMessage != null) {
					errors.rejectValue(fieldPrepend+"csrArLineMesage", null, null, errorMessage);
				}
			}
		}

		//Validate form status
		FormWorkflow workflow = cashSaleReturn.getId() != 0 ? cashSaleReturnService.getFormWorkflow(cashSaleReturn.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
		}
	}
}