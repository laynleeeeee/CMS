package eulap.eb.validator.processing;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.PrMainProduct;
import eulap.eb.domain.hibernate.PrRawMaterialsItem;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.domain.hibernate.ProcessingReportType;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemService;
import eulap.eb.service.PRItemUtil;
import eulap.eb.service.SaleItemUtil;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.processing.ProductionReportService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validator class for {@link ProcessingReport}

 *
 */
@Service
public class ProductionReportValidator implements Validator {
	private static Logger logger = Logger.getLogger(ProcessingReportValidator.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private ProductionReportService productionReportService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ProcessingReport.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		ProcessingReport processingReport = (ProcessingReport) object;

		ValidatorUtil.validateCompany(processingReport.getCompanyId(), companyService,
				errors, "companyId");

		if (processingReport.getRefNumber() != null &&  
				processingReport.getRefNumber().trim().length() > ProcessingReport.MAX_REF_NUMBER)
			errors.rejectValue("refNumber", null, null,
					ValidatorMessages.getString("ProductionReportValidator.0")
						+ ProcessingReport.MAX_REF_NUMBER
						+ ValidatorMessages.getString("ProductionReportValidator.1"));

		if (processingReport.getRemarks() != null && !processingReport.getRemarks().trim().isEmpty()) {
			processingReport.setRemarks(StringFormatUtil.removeExtraWhiteSpaces(processingReport.getRemarks(), null));
			if (processingReport.getRemarks().trim().length() > ProcessingReport.MAX_REMARKS) {
				errors.rejectValue("remarks", null, null, ValidatorMessages.getString("ProductionReportValidator.2") +
						ProcessingReport.MAX_REMARKS + ValidatorMessages.getString("ProductionReportValidator.3"));
			}
		}

		if (processingReport.getDate() == null) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("ProductionReportValidator.4"));
		} else if (!timePeriodService.isOpenDate(processingReport.getDate())) {
				errors.rejectValue("date", null, null, ValidatorMessages.getString("ProductionReportValidator.5"));
		}

		// Raw material items validations.
		List<PrRawMaterialsItem> prRawMaterialsItems = processingReport.getPrRawMaterialsItems();
		if (prRawMaterialsItems != null && !prRawMaterialsItems.isEmpty()) {
			boolean isMixingReport = processingReport.getProcessingReportTypeId() == ProcessingReportType.PRODUCTION;
			if (SaleItemUtil.hasInvalidItem(prRawMaterialsItems)) {
				errors.rejectValue("errRMItems", null, null, ValidatorMessages.getString("ProductionReportValidator.6"));
			}
			StringBuffer errMsg = null;
			if (SaleItemUtil.hasNoOrZeroQty(prRawMaterialsItems)) {
				errors.rejectValue("errRMItems", null, null, ValidatorMessages.getString("ProductionReportValidator.7"));
			} else if (SaleItemUtil.hasZeroOrNegQty(prRawMaterialsItems)) {
				errors.rejectValue("errRMItems", null, null, ValidatorMessages.getString("ProductionReportValidator.8"));
			} else {
				for (PrRawMaterialsItem rm : prRawMaterialsItems) {
					double existingStocks = NumberFormatUtil.roundOffTo2DecPlaces(itemService.getItemExistingStocks(
							rm.getItemId(), rm.getWarehouseId(), new Date()));
					logger.debug("Existing stocks for stock code "+rm.getStockCode()+" is "+existingStocks);
					double qtyToBeWithdrawn = rm.getQuantity() - (rm.getOrigQty() == null ? 0 : rm.getOrigQty());
					if (existingStocks < NumberFormatUtil.roundOffTo2DecPlaces(qtyToBeWithdrawn)) {
						if (isMixingReport) {
							if (errMsg == null) {
								errMsg = new StringBuffer();
							} else {
								errMsg.append(", ");
							}
							errMsg.append(rm.getItem().getStockCodeAndDesc());
						} else {
							errors.rejectValue("errRMItems", null, null,
									ValidatorMessages.getString("ProductionReportValidator.9"));
							break;
						}
					}
				}
				if (errMsg != null) {
					errors.rejectValue("errRMItems", null, null,
							ValidatorMessages.getString("ProductionReportValidator.10")+errMsg.toString());
				}
				errMsg = null;
			}
			if (PRItemUtil.hasDuplicateFifoItem(prRawMaterialsItems) &&
					processingReport.getProcessingReportTypeId().intValue() == ProcessingReportType.WIP_BAKING){
				errors.rejectValue("errRMItems", null, null, ValidatorMessages.getString("ProductionReportValidator.11"));
			}
			if (PRItemUtil.hasNoWarehouse(prRawMaterialsItems)) {
				errors.rejectValue("errRMItems", null, null, ValidatorMessages.getString("ProductionReportValidator.12"));
			}
		} else {
			if (processingReport.getProcessingReportTypeId().intValue() != ProcessingReportType.PRODUCTION) {
				errors.rejectValue("errRMItems", null, null, ValidatorMessages.getString("ProductionReportValidator.13"));
			}
		}

		// Main products validations.
		List<PrMainProduct> prMainProducts = processingReport.getPrMainProducts();
		if (prMainProducts != null && !prMainProducts.isEmpty()) {
			if (SaleItemUtil.hasInvalidItem(prMainProducts)) {
				errors.rejectValue("errMainProduct", null, null, ValidatorMessages.getString("ProductionReportValidator.14"));
			}
			if (SaleItemUtil.hasNoOrZeroQty(prMainProducts)) {
				errors.rejectValue("errMainProduct", null, null, ValidatorMessages.getString("ProductionReportValidator.15"));
			} else if (SaleItemUtil.hasZeroOrNegQty(prMainProducts)) {
				errors.rejectValue("errMainProduct", null, null, ValidatorMessages.getString("ProductionReportValidator.16"));
			}
			if (PRItemUtil.hasNoWarehouse(prMainProducts)) {
				errors.rejectValue("errMainProduct", null, null, ValidatorMessages.getString("ProductionReportValidator.17"));
			}
			if (PRItemUtil.hasDuplicateFifoItem(prMainProducts)){
				errors.rejectValue("errMainProduct", null, null, ValidatorMessages.getString("ProductionReportValidator.18"));
			}
		} else {
			errors.rejectValue("errMainProduct", null, null, ValidatorMessages.getString("ProductionReportValidator.19"));
		}

		//Validate form status
		FormWorkflow workflow = processingReport.getId() != 0 ? productionReportService.getFormWorkflow(processingReport.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}
	}
}
