package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.SupplierAccountService;

/**
 * A validator class that handles that data validation of Supplier's account

 *
 */
@Service
public class SupplierAccountValidator implements Validator{
	@Autowired
	private SupplierAccountService sAccountService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Supplier.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SupplierAccount supplierAccount = (SupplierAccount) target;
		if (supplierAccount.getName() == null || supplierAccount.getName().trim().isEmpty())
			errors.rejectValue("name", null, null, ValidatorMessages.getString("SupplierAccountValidator.0"));
		else if (!sAccountService.isUnique(supplierAccount))
			errors.rejectValue("name", null, null, ValidatorMessages.getString("SupplierAccountValidator.1"));

		if (supplierAccount.getCompanyId() == 0)
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("SupplierAccountValidator.2"));
		else
			ValidatorUtil.checkInactiveCompany(companyService, supplierAccount.getCompanyId(), "companyId", errors, null);

		if (supplierAccount.getCreditAccountId() == 0
				|| supplierAccount.getCreditDivisionId() == 0)
			errors.rejectValue("defaultCreditACId", null, null, ValidatorMessages.getString("SupplierAccountValidator.3"));

		boolean hasDebitDivision = supplierAccount.getDebitDivisionId() != 0;
		boolean hasDebitAccount = supplierAccount.getDebitAccountId() != 0;

		if ((hasDebitDivision & !hasDebitAccount) || (!hasDebitDivision & hasDebitAccount))
			errors.rejectValue("defaultDebitACId", null, null, ValidatorMessages.getString("SupplierAccountValidator.4"));

		if (hasDebitDivision) {
			ValidatorUtil.checkInactiveDivision(divisionService, supplierAccount.getDebitDivisionId(), "debitDivisionId", errors, null);
		}
		if (hasDebitAccount) {
			ValidatorUtil.checkInactiveAccount(accountService, supplierAccount.getDebitAccountId(), "debitAccountId", errors, null);
		}

		if (supplierAccount.getCreditDivisionId() != 0) {
			ValidatorUtil.checkInactiveDivision(divisionService, supplierAccount.getCreditDivisionId(), "creditDivisionId", errors, null);
		}
		if (supplierAccount.getCreditAccountId() != 0) {
			ValidatorUtil.checkInactiveAccount(accountService, supplierAccount.getCreditAccountId(), "creditAccountId", errors, null);
		}
	}
}
