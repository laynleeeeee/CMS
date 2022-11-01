package eulap.eb.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.ArMiscellaneous;
import eulap.eb.domain.hibernate.ArMiscellaneousType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ServiceLine;
import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.ArMiscellaneousService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.ServiceSettingService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.UnitMeasurementService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;

/**
 * Validator class for AR Miscellaneous Form. 

 *
 */
@Service
public class ArMiscellaneousValidator implements Validator{
	@Autowired
	private ArMiscellaneousService arMiscellaneousService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired 
	private ServiceSettingService serviceSettingService;
	@Autowired
	private UnitMeasurementService uomService;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private ReferenceDocumentService refDocumentService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ArMiscellaneous.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		ArMiscellaneous arMiscellaneous = (ArMiscellaneous) object;
		
		if (arMiscellaneous.getArMiscellaneousTypeId() != null && 
				arMiscellaneous.getArMiscellaneousTypeId() == ArMiscellaneousType.TYPE_CHECK) {
			if (arMiscellaneous.getRefNumber() == null || arMiscellaneous.getRefNumber().trim().isEmpty())
				errors.rejectValue("refNumber", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.0"));
			else if (arMiscellaneous.getRefNumber().trim().length() > ArMiscellaneous.MAX_CHECK_NUMBER)
				errors.rejectValue("refNumber", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.1") + ArMiscellaneous.MAX_CHECK_NUMBER + ValidatorMessages.getString("ArMiscellaneousValidator.2")); //$NON-NLS-3$
		}
		
		if (arMiscellaneous.getReceiptNumber() == null || arMiscellaneous.getReceiptNumber().trim().isEmpty()) {
			errors.rejectValue("receiptNumber", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.3"));
		} else {
			if (arMiscellaneous.getReceiptNumber().trim().length() > ArMiscellaneous.MAX_RECEIPT_NUMBER)
				errors.rejectValue("receiptNumber", null, null, 
						ValidatorMessages.getString("ArMiscellaneousValidator.4") + ArMiscellaneous.MAX_RECEIPT_NUMBER + ValidatorMessages.getString("ArMiscellaneousValidator.5"));
			if (!arMiscellaneousService.isUniqueReceiptNo(arMiscellaneous))
				errors.rejectValue("receiptNumber", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.6"));
		}

		Integer arCustomerId = arMiscellaneous.getArCustomerId();
		if(!arMiscellaneous.getCustomerName().trim().isEmpty() && arCustomerId == null) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("SalesOrderService.35"));
		} else {
			if (arCustomerId == null) {
				errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("SalesOrderService.3"));
			} else if (!arCustomerService.getCustomer(arCustomerId).isActive()) {
				errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("SalesOrderService.4"));
			}
		}

		if(arMiscellaneous.getDivisionId() == null) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.18"));
		} else if (!divisionService.getDivision(arMiscellaneous.getDivisionId()).isActive()){
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.19"));
		}

		if (arMiscellaneous.getArCustomerAccountId() == null) {
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.8"));
		}

		if (arCustomerId != null && arMiscellaneous.getArCustomerAccountId() != null) {
			if(!arCustomerAcctService.getAccount(arMiscellaneous.getArCustomerAccountId()).isActive()) {
				errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("ArTransactionValidator.26"));
			} else if (!arCustomerAcctService.belongsToCustomer(arCustomerId, arMiscellaneous.getArCustomerAccountId())) {
				errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("ArTransactionValidator.6"));
			}
		}
		
		if (arMiscellaneous.getReceiptDate() == null)
			errors.rejectValue("receiptDate", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.9"));
		else if (!timePeriodService.isOpenDate(arMiscellaneous.getReceiptDate()))
				errors.rejectValue("receiptDate", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.10"));
		
		if (arMiscellaneous.getMaturityDate() == null)
			errors.rejectValue("maturityDate", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.11"));
		else if (!timePeriodService.isOpenDate(arMiscellaneous.getMaturityDate()))
				errors.rejectValue("maturityDate", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.12"));
		
		if (arMiscellaneous.getDescription() == null || arMiscellaneous.getDescription().trim().isEmpty()) {
			errors.rejectValue("description", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.13"));
		}
		
		if (arMiscellaneous.getAmount() == null || arMiscellaneous.getAmount() == 0.0)
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.14"));
		else {
			if (arMiscellaneous.getAmount() < 0.0)
				errors.rejectValue("amount", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.15"));
		}

		if (arMiscellaneous.getArMiscellaneousLines() == null || arMiscellaneous.getArMiscellaneousLines().isEmpty())
			errors.rejectValue("arMiscellaneousLines", null, null, ValidatorMessages.getString("ArMiscellaneousValidator.16"));
		else {
			List<ServiceLine> otherCharges = new ArrayList<ServiceLine>();
			otherCharges.addAll(arMiscellaneous.getArMiscellaneousLines());
			validateOtherCharges(otherCharges, errors, "arMiscellaneousLines");
			if (!arMiscellaneousService.isValidAmount(arMiscellaneous)) {
				errors.rejectValue("totalAmount", null, null,
						ValidatorMessages.getString("ArMiscellaneousValidator.17"));
			}
		}

		//Validate form status
		FormWorkflow workflow = arMiscellaneous.getId() != 0 ? arMiscellaneousService.getFormWorkflow(arMiscellaneous.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}
		refDocumentService.validateReferences(arMiscellaneous.getReferenceDocuments(), errors);
	}

	private <T extends ServiceLine> void validateOtherCharges(List<T> otherCharges, Errors errors, String field) {
		int row = 0;
		ServiceSetting service = null;
		for (ServiceLine arOtherCharge : otherCharges) {
			row++;
			UnitMeasurement uom = null;
			Integer serviceId = arOtherCharge.getServiceSettingId();
			if (serviceId == null) {
				if(!arOtherCharge.getServiceSettingName().isEmpty()) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesOrderService.22"), arOtherCharge.getServiceSettingName(), row));
				} else {
					errors.rejectValue(field, null, null, 
							String.format(ValidatorMessages.getString("SalesOrderService.18"), row));
				}
			} else {
				if(arOtherCharge.getServiceSettingName() != null) {
					service = serviceSettingService.getServiceSettingByName(arOtherCharge.getServiceSettingName());
					if (service == null) {
						errors.rejectValue(field, null, null,
								String.format(ValidatorMessages.getString("SalesOrderService.22"), arOtherCharge.getServiceSettingName(), row));
					}
					else if (!serviceSettingService.getServiceSetting(serviceId).isActive()) {
						errors.rejectValue(field, null, null,
								String.format(ValidatorMessages.getString("SalesOrderService.19"), arOtherCharge.getServiceSettingName(), row));
					}
				}
			}
			if(arOtherCharge.getUnitMeasurementName() != null) {
				uom = uomService.getUMByName(arOtherCharge.getUnitMeasurementName());
				if(uom == null) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesOrderService.34"), arOtherCharge.getUnitMeasurementName(), row));
				}
				else if (!uom.isActive()) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesOrderService.20"), row));
				}
			}
			if(arOtherCharge.getAmount() == null || arOtherCharge.getAmount() <= 0.0) {
				errors.rejectValue(field, null, null,
						String.format(ValidatorMessages.getString("SalesOrderService.36"), row));
			}
		}
	}
}
