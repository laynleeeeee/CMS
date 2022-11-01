package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.payroll.PayrollService;

/**
 * Validator for HTML version of {@link Payroll}

 *
 */
@Service
public class PayrollValidator implements Validator{
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PayrollService payrollService;
	@Autowired
	private FormStatusService formStatusService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Payroll.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		validate(object, errors, true);
	}

	/**
	 * Validate payroll form
	 * @param object The payroll form object
	 * @param errors The validation errors
	 * @param isRequiredField True if validate the required fields, otherwise false
	 */
	public void validate(Object object, Errors errors, boolean isRequiredField) {
		Payroll payroll = (Payroll) object;

		Integer companyId = payroll.getCompanyId();
		if(companyId == null) {
			errors.rejectValue("payroll.companyId", null, null, ValidatorMessages.getString("PayrollValidator.0"));
		} else if(!companyService.getCompany(companyId).isActive()) {
			errors.rejectValue("payroll.companyId", null, null, ValidatorMessages.getString("PayrollValidator.1"));
		}

		if (payroll.getDate() == null) {
			errors.rejectValue("payroll.date", null, null, ValidatorMessages.getString("PayrollValidator.2"));
		}

		if (payroll.getPayrollTimePeriodId() == null && payroll.getPayrollTimePeriodScheduleId() == null) {
			errors.rejectValue("payroll.payrollTimePeriodScheduleId", null, null, ValidatorMessages.getString("PayrollValidator.3"));
		}

		if (payroll.getPayrollTimePeriodId() != null && payroll.getPayrollTimePeriodScheduleId() != null
				&& payrollService.hasExistingPayroll(payroll.getId(), payroll.getPayrollTimePeriodId(), 
						payroll.getPayrollTimePeriodScheduleId(), payroll.getDivisionId(), payroll.getCompanyId())) {
			errors.rejectValue("payroll.payrollTimePeriodScheduleId", null, null,
					ValidatorMessages.getString("PayrollValidator.4"));
		}

		if (isRequiredField) {
			if (payroll.getEmployeeTypeId() == null) {
				errors.rejectValue("payroll.employeeTypeId", null, null, ValidatorMessages.getString("PayrollValidator.5"));
			}
		}

		if(payroll.getEmployeeSalaryDTOs() == null || payroll.getEmployeeSalaryDTOs().isEmpty()){
			errors.rejectValue("payroll.payrollEmployeeSalaries", null, null, ValidatorMessages.getString("PayrollValidator.6"));
		}
		//Validate form status
		FormWorkflow workflow = payroll.getId() != 0 ? payrollService.getFormWorkflow(payroll.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("payroll.formWorkflowId", null, null, workflowError);
		}
	}
}
