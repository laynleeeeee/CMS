package eulap.eb.validator.inv;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.ApInvoiceGoodsDao;
import eulap.eb.dao.ApInvoiceLineDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RReturnToSupplier;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ItemService;
import eulap.eb.service.RReceivingReportService;
import eulap.eb.service.RReturnToSupplierService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.SerialItemService;
import eulap.eb.service.SupplierAccountService;
import eulap.eb.service.SupplierService;
import eulap.eb.service.TermService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.WarehouseService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * A class that handles the validation of RReturnToSupplier.

 */
@Service
public class RReturnToSupplierValidator implements Validator{
	private static Logger logger = Logger.getLogger(RReturnToSupplierValidator.class);
	@Autowired
	private ItemService itemService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private RReceivingReportService rrService;
	@Autowired
	private RReturnToSupplierService rtsService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private SupplierAccountService supplierAcctService;
	@Autowired
	private TermService termService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private ApInvoiceGoodsDao apInvoiceGoodsDao;
	@Autowired
	private ApInvoiceLineDao apInvoiceLineDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;

	@Override
	public boolean supports(Class<?> clazz) {
		return APInvoice.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		validate(obj, errors, "");
	}

	public void validate(Object obj, Errors errors, String fieldPrepend) {
		APInvoice apInvoice = (APInvoice) obj;
		logger.info("Performing return to supplier validation process");

		RReturnToSupplier rts = apInvoice.getReturnToSupplier();
		logger.debug("Validating the company id: "+rts.getCompanyId());
		ValidatorUtil.validateCompany(rts.getCompanyId(), companyService,
				errors, fieldPrepend+"returnToSupplier.companyId");

		if(rts.getWarehouseId() == null || rts.getWarehouseId() == -1)
			errors.rejectValue(fieldPrepend+"returnToSupplier.warehouseId", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.0"));

		logger.debug("Validating the RTS Date: "+apInvoice.getGlDate());
		if(apInvoice.getGlDate() == null)
			errors.rejectValue(fieldPrepend+"glDate", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.1"));
		else if(!timePeriodService.isOpenDate(apInvoice.getGlDate()))
			errors.rejectValue(fieldPrepend+"glDate", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.2"));

		Integer row = 0;
		boolean isWarehouseChanged = false;
		if(apInvoice.getId() > 0) {
			RReturnToSupplier savedRts = rtsService.getRtsByInvoiceId(apInvoice.getId());
			isWarehouseChanged = !rts.getWarehouseId().equals(savedRts.getWarehouseId());
		}
		List<RReturnToSupplierItem> rtsItems = apInvoice.getRtsItems();
		logger.info("Validating return to supplier items");
		if (rtsItems != null && !rtsItems.isEmpty()) {
			for(RReturnToSupplierItem rtsI : rtsItems) {
				if(rtsI.getrReceivingReportItemId() == null) {
					errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.3"));
					break;
				}else{
					if(rtsI.getItemId() != null && rtsI.getItemId() != 0) {
						row++;

						if (apInvoice.getGlDate() != null && rtsI.getQuantity() != null) {
							//Validate the existing stocks as of the form date.
							String quantityErrorMsg = ValidationUtil.validateWithdrawnQty(itemService, warehouseService,
									rtsI.getItemId(), isWarehouseChanged, rtsItems, apInvoice.getGlDate(), rts.getWarehouseId(), row);
							if(quantityErrorMsg != null) {
								errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, quantityErrorMsg);
								break;
							}
						}

						if(rtsI.getQuantity() == null || rtsI.getQuantity() <= 0) {
							errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.4"));
							break;
						} else {
							RReceivingReportItem rrItem = rrService.getRrItem(rtsI.getrReceivingReportItemId());
							double unReturnedQty = rrItem.getQuantity() - rtsService.getReturnedQty(rrItem.getId(), rtsI.getId());
							logger.debug("Total unreturned quantity: "+unReturnedQty+" for item id: "+rtsI.getItemId());
							double qtyToBeValidated = rtsI.getQuantity();
							if(qtyToBeValidated > rrItem.getQuantity() || qtyToBeValidated > unReturnedQty) {
								errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.5"));
								break;
							}
						}

					}else if(rtsI.getQuantity() != null && rtsI.getQuantity() > 0) {
						errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.6")+row+ValidatorMessages.getString("RReturnToSupplierValidator.7"));
						break;
					}else if( rtsI.getItemId() == null && !rtsI.getStockCode().trim().isEmpty()){
						errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.8"));
						break;
					}
				}
			}
		}

		if(row == 0){
			logger.warn("Return to supplier item is empty");
			errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.11"));
		}
		//Validate form status
		FormWorkflow workflow = apInvoice.getId() != 0 ? rtsService.getFormWorkflow(apInvoice.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
		}
	}

	/**
	 * Validate the AR invoice - RTS form
	 * @param apInvoice The AP invoice form
	 * @param errors The validation errors
	 */
	public void validateForm(APInvoice apInvoice, Errors errors) {
		if (apInvoice.getReferenceObjectId() == null) {
			errors.rejectValue("rrNumber", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.10"));
		} else {
			RReturnToSupplier rts = apInvoice.getReturnToSupplier();
			ValidatorUtil.checkInactiveCompany(companyService, rts.getCompanyId(), "returnToSupplier.companyId", errors, null);

			Integer divisionId = apInvoice.getDivisionId();
			if (divisionId != null && !divisionService.getDivision(divisionId).isActive()) {
				errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ApInvoiceGsService.2"));
			}

			Integer supplierId = apInvoice.getSupplierId();
			if (supplierId != null && !supplierService.getSupplier(supplierId).isActive()) {
				errors.rejectValue("supplierId", null, null, ValidatorMessages.getString("ApInvoiceGsService.8"));
			}

			Integer supplierAccountId = apInvoice.getSupplierAccountId();
			if (supplierAccountId != null && !supplierAcctService.getSupplierAccount(supplierAccountId).isActive()) {
				errors.rejectValue("supplierAccountId", null, null, ValidatorMessages.getString("ApInvoiceGsService.10"));
			}

			Integer termId = apInvoice.getTermId();
			if (termId != null && !termService.getTerm(termId).isActive()) {
				errors.rejectValue("termId", null, null, ValidatorMessages.getString("ApInvoiceGsService.6"));
			}

			Integer currencyId = apInvoice.getCurrencyId();
			if (currencyId != null && !currencyService.getCurency(currencyId).isActive()) {
				errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.11"));
			}

			List<SerialItem> serialItems = apInvoice.getSerialItems();
			List<RReturnToSupplierItem> nonserialItems = apInvoice.getRtsItems();
			List<ApInvoiceLine> serviceLines = apInvoice.getApInvoiceLines();
			boolean hasInvoiceGoods = nonserialItems != null && !nonserialItems.isEmpty();
			boolean hasSerialItems = serialItems != null && !serialItems.isEmpty();
			boolean hasLines = serviceLines != null && !serviceLines.isEmpty();
			if(!hasInvoiceGoods && !hasSerialItems && !hasLines) {
				errors.rejectValue("apInvoiceLines", null, null, ValidatorMessages.getString("ApInvoiceGsService.32"));
			}

			boolean hasNonserializedItems = nonserialItems != null && !nonserialItems.isEmpty();

			serialItemService.validateSerialItems("rtsItems", "serialItems", false,
					false, false, serialItems, errors, true);
			if (hasNonserializedItems) {
				int row = 0;
				for (RReturnToSupplierItem rtsi : nonserialItems) {
					row++;
					double quantity = rtsi.getQuantity();
					double remainingQty = apInvoiceGoodsDao.getInvGsRemainingQty(apInvoice.getId(),
							apInvoice.getReferenceObjectId(), rtsi.getItemId());
					if (rtsi.getQuantity() > remainingQty) {
						errors.rejectValue("rtsItems", null, null, String.format(ValidatorMessages.getString("ApInvoiceGsService.20"),
								NumberFormatUtil.format(remainingQty), row));
					} else if (quantity < 0.0) {
						errors.rejectValue("rtsItems", null, null, String.format(ValidatorMessages.getString("ApInvoiceGsService.21"), row));
					}
				}
			}

			if (hasLines) {
				int row = 0;
				for (ApInvoiceLine apl : serviceLines) {
					row++;
					Integer apLineSetupId = apl.getApLineSetupId();
					String apLineName = apl.getApLineSetupName();
					Double rowAmount = apl.getAmount();
					if (apLineSetupId != null || apLineName != null || rowAmount != null) {
						if (apLineSetupId == null) {
							if (apLineName != null && !apLineName.isEmpty()) {
								errors.rejectValue("apInvoiceLines", null, null,
										String.format(ValidatorMessages.getString("ApInvoiceGsService.22"), row));
							} else {
								errors.rejectValue("apInvoiceLines", null, null,
										String.format(ValidatorMessages.getString("ApInvoiceGsService.23"), row));
							}
						}
						if (rowAmount == null) {
							errors.rejectValue("apInvoiceLines", null, null,
									String.format(ValidatorMessages.getString("ApInvoiceGsService.24"), row));
						} else if (rowAmount == 0) {
							errors.rejectValue("apInvoiceLines", null, null,
									String.format(ValidatorMessages.getString("ApInvoiceGsService.25"), row));
						}
					}
					Double quantity = apl.getQuantity();
					if (quantity != null) {
						double remainingQty = apInvoiceLineDao.getRemainingInvGsLineQty(apInvoice.getId(),
								apInvoice.getReferenceObjectId(), apl.getApLineSetupId());
						if (quantity > remainingQty) {
							errors.rejectValue("apInvoiceLines", null, null, 
									String.format(ValidatorMessages.getString("ApInvoiceGsService.26"), NumberFormatUtil.format(remainingQty), row));
						}
					}
				}
			}
		}

		if (apInvoice.getGlDate() == null) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.1"));
		} else if (!timePeriodService.isOpenDate(apInvoice.getGlDate())) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("RReturnToSupplierValidator.2"));
		}

		// Validate form status
		FormWorkflow workflow = apInvoice.getId() != 0 ? rtsService.getFormWorkflow(apInvoice.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}

		refDocumentService.validateReferences(apInvoice.getReferenceDocuments(), errors,"");
	}
}
