package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.StringFormatUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeStatus;
import eulap.eb.domain.hibernate.EmployeeType;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EmployeeService;
import eulap.eb.service.PositionService;

/**
 * Validator class for {@link Employee}

 *
 */
@Service
public class EmployeeValidator implements Validator {
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PositionService postionService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Employee.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		validate(obj, errors, true);
	}

	public void validate(Object obj, Errors errors, boolean isAddressRequired) {
		Employee employee = (Employee) obj;

		//Company
		if(employee.getCompanyId() == null){
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("EmployeeValidator.0"));
		} else {
			Company company = companyService.getCompany(employee.getCompanyId());
			if (!company.isActive()){
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("EmployeeValidator.1"));
			}
		}

		// Biometric
		if (employee.getBiometricId() != null && !employeeService.isUniqueBiometricId(employee)) {
			errors.rejectValue("biometricId", null, null, ValidatorMessages.getString("EmployeeValidator.3"));
		}

		// Employee No
		if (employee.getEmployeeNo() != null && !employee.getEmployeeNo().trim().isEmpty()) {
			if (employee.getEmployeeNo().trim().length() > Employee.EMPLOYEE_NO_MAX_CHAR) {
				errors.rejectValue("employeeNo", null, null, ValidatorMessages.getString("EmployeeValidator.5")
						+ Employee.EMPLOYEE_NO_MAX_CHAR + ValidatorMessages.getString("EmployeeValidator.6"));
			} else if (!employeeService.isUniqueEmployeeNo(employee)) {
				errors.rejectValue("employeeNo", null, null, ValidatorMessages.getString("EmployeeValidator.7"));
			}
		}

		//First name
		if(employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
			errors.rejectValue("firstName", null, null, ValidatorMessages.getString("EmployeeValidator.8"));
		} else if(employee.getFirstName().trim().length() > Employee.NAME_MAX_CHAR) {
			errors.rejectValue("firstName", null, null,
					ValidatorMessages.getString("EmployeeValidator.9")+Employee.NAME_MAX_CHAR+ValidatorMessages.getString("EmployeeValidator.10"));
		}

		//Middle name
		if(employee.getFirstName() != null &&
				employee.getMiddleName().trim().length() > Employee.NAME_MAX_CHAR) {
			errors.rejectValue("middleName", null, null,
					ValidatorMessages.getString("EmployeeValidator.11")+Employee.NAME_MAX_CHAR+ValidatorMessages.getString("EmployeeValidator.12"));
		}

		//Last name
		if(employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
			errors.rejectValue("lastName", null, null, ValidatorMessages.getString("EmployeeValidator.13"));
		} else if(employee.getLastName().trim().length() > Employee.NAME_MAX_CHAR) {
			errors.rejectValue("lastName", null, null,
					ValidatorMessages.getString("EmployeeValidator.14")+Employee.NAME_MAX_CHAR+ValidatorMessages.getString("EmployeeValidator.15"));
		}

		// Complete Name
		if(employee.getId() == 0 && employee.getFirstName() != null && employee.getLastName() != null) {
			String middleName = employee.getMiddleName() != null ? employee.getMiddleName() : "";
			String completeName = StringFormatUtil.removeExtraWhiteSpaces(employee.getFirstName() + " " + middleName + " " + employee.getLastName());
			if(!employeeService.isUniqueEmployeeName(employee.getCompanyId(), completeName)) {
				errors.rejectValue("lastName", null, null, completeName + ValidatorMessages.getString("EmployeeValidator.41"));
			}
		}

		//Position
		if(employee.getPositionId() == null) {
			errors.rejectValue("positionId", null, null, ValidatorMessages.getString("EmployeeValidator.16"));
		} else {
			Position position = postionService.getPosition(employee.getPositionId());
			if (!position.isActive()){
				errors.rejectValue("positionId", null, null, ValidatorMessages.getString("EmployeeValidator.17"));
			}
		}

		//Birthday
		if(employee.getBirthDate() == null) {
			errors.rejectValue("birthDate", null, null, ValidatorMessages.getString("EmployeeValidator.18"));
		}

		//Contact number
		if(employee.getContactNo() != null &&
				employee.getContactNo().trim().length() > Employee.CONTACT_NO_MAX_CHAR) {
			errors.rejectValue("contactNo", null, null,
					ValidatorMessages.getString("EmployeeValidator.19")+Employee.CONTACT_NO_MAX_CHAR+ValidatorMessages.getString("EmployeeValidator.20"));
		}

		//Address
		if((employee.getAddress() == null || employee.getAddress().trim().isEmpty()) && isAddressRequired) {
			errors.rejectValue("address", null, null, ValidatorMessages.getString("EmployeeValidator.21"));
		} else if(employee.getAddress().trim().length() > Employee.ADDRESS_MAX_CHAR) {
			errors.rejectValue("address", null, null,
					ValidatorMessages.getString("EmployeeValidator.22")+Employee.ADDRESS_MAX_CHAR+ValidatorMessages.getString("EmployeeValidator.23"));
		}

		//Email address
		if(employee.getEmailAddress() != null &&
				employee.getEmailAddress().trim().length() > Employee.NAME_MAX_CHAR) {
			errors.rejectValue("emailAddress", null, null,
					ValidatorMessages.getString("EmployeeValidator.24")+Employee.NAME_MAX_CHAR+ValidatorMessages.getString("EmployeeValidator.25"));
		}

		//Employee type
		if(employee.getEmployeeTypeId() == null) {
			errors.rejectValue("employeeTypeId", null, null, ValidatorMessages.getString("EmployeeValidator.26"));
		} else {
			EmployeeType employeeType = employeeService.getEmployeeType(employee.getEmployeeTypeId());
			if (!employeeType.isActive()) {
				errors.rejectValue("employeeTypeId", null, null, ValidatorMessages.getString("EmployeeValidator.27"));
			}
		}

		//Employee status
		if(employee.getEmployeeStatusId() == null) {
			errors.rejectValue("employeeStatusId", null, null, ValidatorMessages.getString("EmployeeValidator.28"));
		} else {
			EmployeeStatus employeeStatus = employeeService.getEmployeeStatus(employee.getEmployeeStatusId());
			if (!employeeStatus.isActive()) {
				errors.rejectValue("employeeStatusId", null, null, ValidatorMessages.getString("EmployeeValidator.29"));
			}
		}

		//Basic salary
		if(employee.getSalaryDetail().getEcola() < 0) {
			errors.rejectValue("salaryDetail.ecola", null, null,
					ValidatorMessages.getString("EmployeeValidator.30"));
		}

		//Basic salary
		if(employee.getSalaryDetail().getBasicSalary() <= 0) {
			errors.rejectValue("salaryDetail.basicSalary", null, null,
					ValidatorMessages.getString("EmployeeValidator.31"));
		}

		//Daily salary
		if(employee.getSalaryDetail().getDailySalary() <= 0) {
			errors.rejectValue("salaryDetail.dailySalary", null, null,
					ValidatorMessages.getString("EmployeeValidator.32"));
		}

		//De minimis
		if(employee.getSalaryDetail().getDeMinimis() < 0) {
			errors.rejectValue("salaryDetail.deMinimis", null, null,
					ValidatorMessages.getString("EmployeeValidator.33"));
		}

		//SSS contribution
		if(employee.getSalaryDetail().getSssContribution() < 0) {
			errors.rejectValue("salaryDetail.sssContribution", null, null,
					ValidatorMessages.getString("EmployeeValidator.34"));
		}

		//Additional SSS contribution
		if(employee.getSalaryDetail().getAddSssContribution() < 0) {
			errors.rejectValue("salaryDetail.addSssContribution", null, null,
					ValidatorMessages.getString("EmployeeValidator.35"));
		}

		//PhilHealth contribution
		if(employee.getSalaryDetail().getPhilHealthContribution() < 0) {
			errors.rejectValue("salaryDetail.philHealthContribution", null, null,
					ValidatorMessages.getString("EmployeeValidator.36"));
		}

		//Pag-ibig contribution
		if(employee.getSalaryDetail().getPagIbigContribution() < 0) {
			errors.rejectValue("salaryDetail.pagIbigContribution", null, null,
					ValidatorMessages.getString("EmployeeValidator.37"));
		}

		//Additional Pag-ibig contribution
		if(employee.getSalaryDetail().getAddPagIbigContribution() < 0) {
			errors.rejectValue("salaryDetail.addPagIbigContribution", null, null,
					ValidatorMessages.getString("EmployeeValidator.38"));
		}

		//Other Deductions
		if(employee.getSalaryDetail().getOtherDeduction() < 0) {
			errors.rejectValue("salaryDetail.otherDeduction", null, null,
					ValidatorMessages.getString("EmployeeValidator.39"));
		}

		//Bonus
		if(employee.getSalaryDetail().getBonus() < 0) {
			errors.rejectValue("salaryDetail.otherDeduction", null, null,
					ValidatorMessages.getString("EmployeeValidator.40"));
		}
	}

}
