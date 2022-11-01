package eulap.eb.validator;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArServiceLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArTransactionService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.CurrencyUtil;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.ServiceSettingService;
import eulap.eb.service.TermService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.UnitMeasurementService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;

/**
 * AR Transaction validator.

 *
 */
@Service
public class ArTransactionValidator implements Validator {
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private TermService termService;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private ServiceSettingService serviceSettingService;
	@Autowired
	private UnitMeasurementService uomService;
	@Autowired
	private ArCustomerDao arCustomerDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return ArTransaction.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		ArTransaction arTransaction = (ArTransaction) object;
		Integer trTypeId = arTransaction.getTransactionClassificationId();
		Integer companyId = arTransaction.getCompanyId();
		boolean hasDivision = arTransaction.getDivisionId() != null ? true : false;
		ValidatorUtil.validateCompany(companyId, companyService, errors, "companyId"); 

		String trNumber = arTransaction.getTransactionNumber();
		if (trNumber == null || trNumber.trim().isEmpty()) {
			errors.rejectValue("transactionNumber", null, null, ValidatorMessages.getString("ArTransactionValidator.0"));
		} else {
			if (!arTransactionService.isUniqueTransactionNo(arTransaction)) {
				errors.rejectValue("transactionNumber", null, null, ValidatorMessages.getString("ArTransactionValidator.1"));
			}
			if (trNumber.trim().length() > ArTransaction.MAX_TRANSACTION_NUMBER) {
				errors.rejectValue("transactionNumber", null, null, ValidatorMessages.getString("ArTransactionValidator.2")
						+ ArTransaction.MAX_TRANSACTION_NUMBER + ValidatorMessages.getString("ArTransactionValidator.3"));
			}
		}

		if(hasDivision) {
			if(arTransaction.getCurrencyId() == null) {
				errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("ArTransactionValidator.22"));
			} else if (!currencyService.getCurency(arTransaction.getCurrencyId()).isActive()) {
				errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("ArTransactionValidator.23"));
			}
			if(!divisionService.getDivision(arTransaction.getDivisionId()).isActive()) {
				errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ArTransactionValidator.24"));
			}
		}

		Integer customerId = arTransaction.getCustomerId();
		Integer customerAcctId = arTransaction.getCustomerAcctId();
		if (customerId == null) {
			if(arTransaction.getCustomerName() != null) {
				errors.rejectValue("customerId", null, null, ValidatorMessages.getString("ArTransactionValidator.28"));
			} else {
				errors.rejectValue("customerId", null, null, ValidatorMessages.getString("ArTransactionValidator.4"));
			}
		} else if(!arCustomerService.getCustomer(customerId).isActive()) {
			errors.rejectValue("customerId", null, null, ValidatorMessages.getString("ArTransactionValidator.27"));
		}

		if (customerAcctId == null) {
			errors.rejectValue("customerAcctId", null, null, ValidatorMessages.getString("ArTransactionValidator.5"));
		}

		if (customerId != null && customerAcctId != null) {
			if(!arCustomerAcctService.getAccount(customerAcctId).isActive()) {
				errors.rejectValue("customerAcctId", null, null, ValidatorMessages.getString("ArTransactionValidator.26"));
			} else if (!arCustomerAcctService.belongsToCustomer(customerId, customerAcctId)) {
				errors.rejectValue("customerAcctId", null, null, ValidatorMessages.getString("ArTransactionValidator.6"));
			}
		}

		if (arTransaction.getTermId() == null) {
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("ArTransactionValidator.7"));
		} else if(!termService.getTerm(arTransaction.getTermId()).isActive()){
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("ArTransactionValidator.25"));
		}

		String trDescription = arTransaction.getDescription();
		if (trDescription == null || trDescription.trim().isEmpty()) {
			errors.rejectValue("description", null, null, ValidatorMessages.getString("ArTransactionValidator.8"));
		}

		// Amount
		Double trAmount = arTransaction.getAmount();
		Double availableBalance = arTransactionService.computeAvailableBalance(customerId);
		Double rate = arTransaction.getCurrencyRateValue() != null ? arTransaction.getCurrencyRateValue() : 1;
		ArCustomer arCustomer = arCustomerDao.get(customerId);
		Double creditLimit = arCustomer.getMaxAmount() != null ? arCustomer.getMaxAmount() : 0;
		if (trAmount == null || trAmount == 0.0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArTransactionValidator.9"));
		} else if (CurrencyUtil.convertAmountToPhpRate(trAmount, rate) > availableBalance && creditLimit != 0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArTransactionValidator.29") + NumberFormatUtil.format(availableBalance));
		} else {
			if (trAmount >= 10000000000.00) {
				errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArTransactionValidator.10"));
			} else if (trAmount <= -10000000000.00) {
				errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArTransactionValidator.11"));
			} else if (trTypeId.equals(ArTransactionType.TYPE_CREDIT_MEMO) && trAmount > 0.0)  {
				errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArTransactionValidator.12"));
			} else if (trTypeId.equals(ArTransactionType.TYPE_DEBIT_MEMO) && trAmount < 0.0) {
				errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArTransactionValidator.13"));
			}
		}

		if (arTransaction.getTransactionDate() == null) {
			errors.rejectValue("transactionDate", null, null, ValidatorMessages.getString("ArTransactionValidator.14"));
		}

		// GL Date
		Date glDate = arTransaction.getGlDate();
		boolean validDueDate = true;
		if (glDate == null) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("ArTransactionValidator.15"));
			validDueDate = false;
		} else if (!timePeriodService.isOpenDate(glDate)) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("ArTransactionValidator.16"));
		}

		// Due Date
		Date dueDate = arTransaction.getDueDate();
		if (dueDate == null) {
			errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("ArTransactionValidator.17"));
		} else if (validDueDate) {
			if (dueDate.before(glDate)) {
				errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("ArTransactionValidator.18"));
			}
		}

		List<ArServiceLine> arServiceLines = arTransaction.getArServiceLines();
		if (arServiceLines == null || arServiceLines.isEmpty()) {
			errors.rejectValue("arLineError", null, null, ValidatorMessages.getString("ArTransactionValidator.19"));
		} else {
			boolean hasArLineSetup = true;
			double totalLineAmount = 0;
			if (arServiceLines != null && !arServiceLines.isEmpty()) {
				for (ArServiceLine oc : arServiceLines) {
					String serviceSetting = oc.getServiceSettingName();
					if (oc.getServiceSettingId() == null && (serviceSetting == null || serviceSetting.trim().isEmpty())) {
						hasArLineSetup = false;
					}
					totalLineAmount += (oc.getAmount() != null ? oc.getAmount() : 0.0);
					totalLineAmount += (oc.getVatAmount() != null ? oc.getVatAmount() : 0.0);
				}
			}

			if (hasArLineSetup) {
				validateOtherCharges(arServiceLines, errors, "arLineError");
			} else {
				errors.rejectValue("arLineError", null, null, ValidatorMessages.getString("ArTransactionValidator.20"));
			}

			Double wtAmount = arTransaction.getWtAmount() != null ? arTransaction.getWtAmount() : 0.0;
			Double wtVat = arTransaction.getWtVatAmount() != null ? arTransaction.getWtVatAmount() : 0.0;
			totalLineAmount -= (wtAmount + wtVat);
			trAmount = NumberFormatUtil.roundOffTo2DecPlaces(trAmount);
			totalLineAmount = NumberFormatUtil.roundOffTo2DecPlaces(totalLineAmount);
			if (hasArLineSetup && (trAmount != totalLineAmount)) {
				errors.rejectValue("totalAmount", null, null, ValidatorMessages.getString("ArTransactionValidator.21"));
			}
		}
		//Validate form status
		FormWorkflow workflow = arTransaction.getId() != 0 ? arTransactionService.getFormWorkflow(arTransaction.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}

		//Validating reference document.
		refDocumentService.validateReferences(arTransaction.getReferenceDocuments(), errors);
	}

	/**
	 * Validate other charges
	 * @param arServiceLines The AR Other Charges
	 * @param errors Error
	 * @param field Form field
	 */
	public void validateOtherCharges(List<ArServiceLine> arServiceLines, Errors errors, String field) {
		int row = 0;
		ServiceSetting service = null;
		UnitMeasurement uom = null;
		for (ArServiceLine arServiceLine : arServiceLines) {
			row++;
			Integer serviceId = arServiceLine.getServiceSettingId();
			if (serviceId == null) {
				if(!arServiceLine.getServiceSettingName().isEmpty()) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesOrderService.22"), arServiceLine.getServiceSettingName(), row));
				} else {
					errors.rejectValue(field, null, null, 
							String.format(ValidatorMessages.getString("SalesOrderService.18"), row));
				}
			} else {
				if(arServiceLine.getServiceSettingName() != null) {
					service = serviceSettingService.getServiceSettingByName(arServiceLine.getServiceSettingName());
					if (service == null) {
						errors.rejectValue(field, null, null,
								String.format(ValidatorMessages.getString("SalesOrderService.22"), arServiceLine.getServiceSettingName(), row));
					}
					else if (!serviceSettingService.getServiceSetting(serviceId).isActive()) {
						errors.rejectValue(field, null, null,
								String.format(ValidatorMessages.getString("SalesOrderService.19"), arServiceLine.getServiceSettingName(), row));
					}
				}
			}
			if(arServiceLine.getUnitMeasurementName() != null) {
				uom = uomService.getUMByName(arServiceLine.getUnitMeasurementName());
				if(uom == null) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesOrderService.34"), arServiceLine.getUnitMeasurementName(), row));
				}
				else if (!uom.isActive()) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesOrderService.20"), row));
				}
			}
			if (arServiceLine.getAmount() == null || arServiceLine.getAmount() == 0.0) {
				errors.rejectValue(field, null, null,
						String.format(ValidatorMessages.getString("SalesOrderService.36"), row));
			}
		}
	}
}
