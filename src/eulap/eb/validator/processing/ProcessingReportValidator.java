package eulap.eb.validator.processing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.PrByProduct;
import eulap.eb.domain.hibernate.PrMainProduct;
import eulap.eb.domain.hibernate.PrOtherMaterialsItem;
import eulap.eb.domain.hibernate.PrRawMaterialsItem;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.service.ArLineService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemBagQuantityService;
import eulap.eb.service.PRItemUtil;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.processing.ProcessingReportService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validator class for {@link ProcessingReport}

 *
 */
@Service
public class ProcessingReportValidator implements Validator {
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ArLineService arLineService;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private ProcessingReportService processingReportService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ProcessingReport.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		validate(object, errors, "", true);
	}

	public void validate(Object object, Errors errors, String fieldPrepend, boolean isInvidualSelection) {
		ProcessingReport processingReport = (ProcessingReport) object;

		ValidatorUtil.validateCompany(processingReport.getCompanyId(), companyService,
				errors, fieldPrepend+"companyId");

		if (processingReport.getRefNumber() != null &&  
				processingReport.getRefNumber().trim().length() > ProcessingReport.MAX_REF_NUMBER)
			errors.rejectValue(fieldPrepend+"refNumber", null, null,
					ValidatorMessages.getString("ProcessingReportValidator.0") + ProcessingReport.MAX_REF_NUMBER + ValidatorMessages.getString("ProcessingReportValidator.1"));

		if (processingReport.getRemarks() != null && !processingReport.getRemarks().trim().isEmpty()) {
			if (processingReport.getRemarks().trim().length() > ProcessingReport.MAX_REMARKS) {
				errors.rejectValue(fieldPrepend+"remarks", null, null, ValidatorMessages.getString("ProcessingReportValidator.2") +
						ProcessingReport.MAX_REMARKS + ValidatorMessages.getString("ProcessingReportValidator.3"));
			}
		}

		if (processingReport.getDate() == null) {
			errors.rejectValue(fieldPrepend+"date", null, null, ValidatorMessages.getString("ProcessingReportValidator.4"));
		} else if (!timePeriodService.isOpenDate(processingReport.getDate())) {
				errors.rejectValue(fieldPrepend+"date", null, null, ValidatorMessages.getString("ProcessingReportValidator.5"));
		}

		if(isInvidualSelection) {
			// Raw material items validations.
			List<PrRawMaterialsItem> prRawMaterialsItems = processingReport.getPrRawMaterialsItems();
			if (prRawMaterialsItems != null && !prRawMaterialsItems.isEmpty()) {
				if (SaleItemUtil.hasInvalidItem(prRawMaterialsItems)) {
					errors.rejectValue(fieldPrepend+"errRMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.6"));
				}
				if (PRItemUtil.hasDuplicateItem(prRawMaterialsItems)) {
					errors.rejectValue(fieldPrepend+"errRMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.7"));
				}
				if (SaleItemUtil.hasNoOrZeroQty(prRawMaterialsItems)) {
					errors.rejectValue(fieldPrepend+"errRMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.9"));
				} else if (SaleItemUtil.hasZeroOrNegQty(prRawMaterialsItems)) {
					errors.rejectValue(fieldPrepend+"errRMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.10"));
				} else {
					String errorMessage = null;
					for (PrRawMaterialsItem rm : prRawMaterialsItems) {
						errorMessage = ValidationUtil.validateRefId(rm.getStockCode(), rm.getRefenceObjectId());
						if(errorMessage == null) {
							errorMessage = ValidationUtil.validateAvailableBagsAndStock(itemBagQuantityService, rm.getStockCode(), processingReport.getCompanyId(), 
									rm.getItemId(), rm.getWarehouseId(), rm.getRefenceObjectId(), rm.getItemBagQuantity(), rm.getQuantity(), rm.getEbObjectId());
						}

						if(errorMessage != null) {
							errors.rejectValue(fieldPrepend+"errRMItems", null, null, errorMessage);
							break;
						}
					}
				}
				if (PRItemUtil.hasNoWarehouse(prRawMaterialsItems)) {
					errors.rejectValue(fieldPrepend+"errRMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.11"));
				}
			} else {
				errors.rejectValue(fieldPrepend+"errRMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.12"));
			}

			// Other material items validations.
			List<PrOtherMaterialsItem> prOtherMaterialsItems = processingReport.getPrOtherMaterialsItems();
			if (prOtherMaterialsItems != null && !prOtherMaterialsItems.isEmpty()) {
				if (SaleItemUtil.hasInvalidItem(prOtherMaterialsItems)) {
					errors.rejectValue(fieldPrepend+"errOMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.13"));
				}
				if (PRItemUtil.hasDuplicateItem(prOtherMaterialsItems)) {
					errors.rejectValue(fieldPrepend+"errOMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.14"));
				}
				if (SaleItemUtil.hasNoOrZeroQty(prOtherMaterialsItems)) {
					errors.rejectValue(fieldPrepend+"errOMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.16"));
				} else if (SaleItemUtil.hasZeroOrNegQty(prOtherMaterialsItems)) {
					errors.rejectValue(fieldPrepend+"errOMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.17"));
				} else {
					String errorMessage = null;
					for (PrOtherMaterialsItem om : prOtherMaterialsItems) {
						errorMessage = ValidationUtil.validateRefId(om.getStockCode(), om.getRefenceObjectId());
						if(errorMessage == null) {
							errorMessage = ValidationUtil.validateAvailableBagsAndStock(itemBagQuantityService, om.getStockCode(), processingReport.getCompanyId(), 
									om.getItemId(), om.getWarehouseId(), om.getRefenceObjectId(), om.getItemBagQuantity(), om.getQuantity(), om.getEbObjectId());
						}

						if(errorMessage != null) {
							errors.rejectValue(fieldPrepend+"errOMItems", null, null, errorMessage);
							break;
						}
					}
				}
				if (PRItemUtil.hasNoWarehouse(prOtherMaterialsItems)) {
					errors.rejectValue(fieldPrepend+"errOMItems", null, null, ValidatorMessages.getString("ProcessingReportValidator.18"));
				}
			}

			// Main products validations.
			List<PrMainProduct> prMainProducts = processingReport.getPrMainProducts();
			if (prMainProducts != null && !prMainProducts.isEmpty()) {
				if (SaleItemUtil.hasInvalidItem(prMainProducts)) {
					errors.rejectValue(fieldPrepend+"errMainProduct", null, null, ValidatorMessages.getString("ProcessingReportValidator.19"));
				}
				if (SaleItemUtil.hasNoOrZeroQty(prMainProducts)) {
					errors.rejectValue(fieldPrepend+"errMainProduct", null, null, ValidatorMessages.getString("ProcessingReportValidator.20"));
				} else if (SaleItemUtil.hasZeroOrNegQty(prMainProducts)) {
					errors.rejectValue(fieldPrepend+"errMainProduct", null, null, ValidatorMessages.getString("ProcessingReportValidator.21"));
				}
				if (PRItemUtil.hasNoWarehouse(prMainProducts)) {
					errors.rejectValue(fieldPrepend+"errMainProduct", null, null, ValidatorMessages.getString("ProcessingReportValidator.22"));
				}
			} else {
				errors.rejectValue(fieldPrepend+"errMainProduct", null, null, ValidatorMessages.getString("ProcessingReportValidator.23"));
			}

			// By products validations.
			List<PrByProduct> prByProducts = processingReport.getPrByProducts();
			if (prByProducts != null && !prByProducts.isEmpty()) {
				if (SaleItemUtil.hasInvalidItem(prByProducts)) {
					errors.rejectValue(fieldPrepend+"errByProduct", null, null, ValidatorMessages.getString("ProcessingReportValidator.24"));
				}
				if (SaleItemUtil.hasNoOrZeroQty(prByProducts)) {
					errors.rejectValue(fieldPrepend+"errByProduct", null, null, ValidatorMessages.getString("ProcessingReportValidator.25"));
				} else if (SaleItemUtil.hasZeroOrNegQty(prByProducts)) {
					errors.rejectValue(fieldPrepend+"errByProduct", null, null, ValidatorMessages.getString("ProcessingReportValidator.26"));
				}
				if (PRItemUtil.hasNoWarehouse(prByProducts)) {
					errors.rejectValue(fieldPrepend+"errByProduct", null, null, ValidatorMessages.getString("ProcessingReportValidator.27"));
				}
			}

			//Validate AR Lines/Other Charges
			List<AROtherCharge> otherCharges = new ArrayList<AROtherCharge>();
			otherCharges.addAll(processingReport.getPrOtherCharges());
			if(!otherCharges.isEmpty()) {
				String errorMessage = arLineService.validateArLines(otherCharges, null, processingReport.getCompanyId());
				if(errorMessage != null) {
					errors.rejectValue(fieldPrepend+"errOtherCharges", null, null, errorMessage);
				}
			}
		}

		//Validate form status
		FormWorkflow workflow = processingReport.getId() != 0 ? processingReportService.getFormWorkflow(processingReport.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
		}
	}
}
