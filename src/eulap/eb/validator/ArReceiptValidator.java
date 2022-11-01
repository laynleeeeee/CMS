package eulap.eb.validator;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.ArReceiptLineDao;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptLine;
import eulap.eb.domain.hibernate.ArReceiptType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.service.ArReceiptService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ReceiptMethodService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;

/**
 * Validator class for {@link ArReceipt}

 *
 */
@Service
public class ArReceiptValidator implements Validator {
	private final Logger logger = Logger.getLogger(ArReceiptValidator.class);
	@Autowired
	private ArReceiptService arReceiptService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private ArReceiptLineDao arReceiptLineDao;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private ReceiptMethodService receiptMethodService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ReferenceDocumentService documentService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ArReceipt.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		ArReceipt arReceipt = (ArReceipt) object;

		ValidatorUtil.validateCompany(arReceipt.getCompanyId(),
				companyService, errors, "companyId"); 

		if (arReceipt.getArReceiptTypeId() != null && arReceipt.getArReceiptTypeId() == ArReceiptType.TYPE_CHECK) {
			if (arReceipt.getRefNumber() == null || arReceipt.getRefNumber().trim().isEmpty()) {
				errors.rejectValue("refNumber", null, null, ValidatorMessages.getString("ArReceiptValidator.0")); 
			} else if (arReceipt.getRefNumber().trim().length() > ArReceipt.MAX_CHECK_NUMBER) {
				errors.rejectValue("refNumber", null, null, ValidatorMessages.getString("ArReceiptValidator.1")
						+ ArReceipt.MAX_CHECK_NUMBER + ValidatorMessages.getString("ArReceiptValidator.2"));
			}
		}

		if (arReceipt.getReceiptNumber() != null && !arReceipt.getReceiptNumber().trim().isEmpty()) {
			if (arReceipt.getReceiptNumber().trim().length() > ArReceipt.MAX_RECEIPT_NUMBER) {
				errors.rejectValue("receiptNumber", null, null, ValidatorMessages.getString("ArReceiptValidator.4")
						+ ArReceipt.MAX_RECEIPT_NUMBER + ValidatorMessages.getString("ArReceiptValidator.5")); 
			}

			if (!arReceiptService.isUniqueReceiptNo(arReceipt)) {
				errors.rejectValue("receiptNumber", null, null, ValidatorMessages.getString("ArReceiptValidator.6")); 
			}
		} else {
			errors.rejectValue("receiptNumber", null, null, ValidatorMessages.getString("ArReceiptValidator.26")); 
		}

		if (arReceipt.getArCustomerId() == null) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("ArReceiptValidator.7")); 
		}

		if (arReceipt.getArCustomerAccountId() == null) {
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("ArReceiptValidator.8")); 
		}

		if (arReceipt.getReceiptDate() == null) {
			errors.rejectValue("receiptDate", null, null, ValidatorMessages.getString("ArReceiptValidator.9")); 
		} else if (!timePeriodService.isOpenDate(arReceipt.getReceiptDate())) {
			errors.rejectValue("receiptDate", null, null, ValidatorMessages.getString("ArReceiptValidator.10")); 
		}

		if (arReceipt.getMaturityDate() == null) {
			errors.rejectValue("maturityDate", null, null, ValidatorMessages.getString("ArReceiptValidator.11")); 
		} else if (!timePeriodService.isOpenDate(arReceipt.getMaturityDate())) {
			errors.rejectValue("maturityDate", null, null, ValidatorMessages.getString("ArReceiptValidator.12")); 
		}

		if (arReceipt.getAmount() == null) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArReceiptValidator.13")); 
		} else if(!arReceiptService.isValidAmount(arReceipt)){
			errors.rejectValue("acArLineMesage", null, null, ValidatorMessages.getString("ArReceiptValidator.15")); 
		}

		if(arReceipt.getCurrencyId() == null) {
			//Currency is required.
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("ArReceiptValidator.27")); 
		} else if(!currencyService.getCurency(arReceipt.getCurrencyId()).isActive()) {
			//Currency is inactive.
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("ArReceiptValidator.28")); 
		}

		if(arReceipt.getReceiptMethodId() == null) {
			//Receipt method is required.
			errors.rejectValue("receiptMethodId", null, null, ValidatorMessages.getString("ArReceiptValidator.29")); 
		} else if(!receiptMethodService.getReceiptMethod(arReceipt.getReceiptMethodId()).isActive()) {
			//Receipt method is inactive.
			errors.rejectValue("receiptMethodId", null, null, ValidatorMessages.getString("ArReceiptValidator.30")); 
		}

		if(arReceipt.getDivisionId() == null) {
			//Division is required.
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ArReceiptValidator.31")); 
		} else if(!divisionService.getDivision(arReceipt.getDivisionId()).isActive()) {
			//Division is inactive.
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ArReceiptValidator.32")); 
		}


		List<ArReceiptLine> arReceiptLines = arReceipt.getArReceiptLines();
		if (arReceiptLines == null || arReceiptLines.isEmpty()) {
			errors.rejectValue("arReceiptLineJson", null, null, ValidatorMessages.getString("ArReceiptValidator.25")); 
		} else {
			int row = 0;
			for (ArReceiptLine arReceiptLine : arReceiptLines) {
				row++;
				double remainingBalance = NumberFormatUtil.roundOffRuleOfFive(arReceiptLineDao.getRemainingBalance(arReceiptLine.getRefenceObjectId(),
						arReceiptLine.getArReceiptLineTypeId(), arReceiptLine.getArReceiptId()));
				if (Math.abs(arReceiptLine.getAmount()) > Math.abs(remainingBalance)) {
					logger.info("Remaining balance: " + remainingBalance);
					//Amount at row %d should not exceed the reference transaction remaining balance.
					errors.rejectValue("arReceiptLineJson", null, null, String.format(
							ValidatorMessages.getString("ArReceiptValidator.22"), row));
				}
			}
			double recoupment = arReceipt.getRecoupment() != null ? arReceipt.getRecoupment() : 0;
			if (recoupment != 0 && arReceiptLines.size() == 1) {
				Double capBalance = NumberFormatUtil.roundOffRuleOfFive(arReceiptService.getCapRemainingBalance(
						arReceiptLines.get(0).getRefenceObjectId(), arReceipt.getId()));
				if (capBalance < arReceipt.getRecoupment()) {
					errors.rejectValue("recoupment", null, null, String.format(
							ValidatorMessages.getString("ArReceiptValidator.33"), row));
				}
			}
		}

		//Validating reference document.
		documentService.validateReferences(arReceipt.getReferenceDocuments(), errors);

		//Validate form status
		FormWorkflow workflow = arReceipt.getId() != 0 ? arReceiptService.getFormWorkflow(arReceipt.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatusCancelled(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}
	}
}
