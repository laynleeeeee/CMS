package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.service.AccountService;
import eulap.eb.service.BankAccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ReceiptMethodService;

/**
 * A class that handles data validation for {@link ReceiptMethod}

 */
@Service
public class ReceiptMethodValidator implements Validator{
	@Autowired
	private ReceiptMethodService receiptMethodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private BankAccountService bankAcctService;

	@Override
	public boolean supports(Class<?> arg0) {
		return ReceiptMethod.class.equals(arg0);
	}

	public void validate(Object target, Errors error, int receiptId) {
		ReceiptMethod receiptMethod = (ReceiptMethod) target;

		ValidatorUtil.validateCompany(receiptMethod.getCompanyId(), companyService,
				error, "companyId");

		if (receiptMethod.getName() == null || receiptMethod.getName().trim().isEmpty()) {
			error.rejectValue("name", null, null, ValidatorMessages.getString("ReceiptMethodValidator.0"));
		} else if (!receiptMethodService.isUnique(receiptMethod)) {
			error.rejectValue("name", null, null, ValidatorMessages.getString("ReceiptMethodValidator.1"));
		}

		if (receiptMethodService.noBAIdAndDDAC(receiptMethod.getBankAccountId(), receiptMethod.getDbACDivisionId(),
				receiptMethod.getDbACAccountId()) || receiptMethodService.hasBAIdAndDDAC(receiptMethod.getBankAccountId(),
						receiptMethod.getDbACDivisionId(), receiptMethod.getDbACAccountId())) {
			error.rejectValue("bankAccountId", null, null,
					ValidatorMessages.getString("ReceiptMethodValidator.2"));
			error.rejectValue("debitAcctCombinationId", null, null,
					ValidatorMessages.getString("ReceiptMethodValidator.3"));
		} else {
			BankAccount ba = bankAcctService.getBankAccount(receiptMethod.getBankAccountId());
			if (ba != null) {
				if (ba.getCashInBank().getDivisionId() != receiptMethod.getCrACDivisionId()) {
					error.rejectValue("creditAcctCombinationId", null, null,
							ValidatorMessages.getString("ReceiptMethodValidator.6"));
				}
			} else {
				if (receiptMethod.getDbACDivisionId() != receiptMethod.getCrACDivisionId()) {
					error.rejectValue("creditAcctCombinationId", null, null,
							ValidatorMessages.getString("ReceiptMethodValidator.7"));
				}
			}
		}

		if (receiptMethod.getCrACDivisionId() == 0 || receiptMethod.getCrACAccountId() == 0) {
			error.rejectValue("creditAcctCombinationId", null, null, ValidatorMessages.getString("ReceiptMethodValidator.4"));
		}

		if (receiptMethod.getDbACDivisionId() != 0) {
			ValidatorUtil.checkInactiveDivision(divisionService, receiptMethod.getDbACDivisionId(), "dbACDivisionId", error, null);
		}

		if (receiptMethod.getDbACAccountId() != 0) {
			ValidatorUtil.checkInactiveAccount(accountService, receiptMethod.getDbACAccountId(), "dbACAccountId", error, null);
		}

		if (receiptMethod.getCrACDivisionId() != 0) {
			ValidatorUtil.checkInactiveDivision(divisionService, receiptMethod.getCrACDivisionId(), "crACDivisionId", error, null);
		}

		if (receiptMethod.getCrACAccountId() != 0) {
			ValidatorUtil.checkInactiveAccount(accountService, receiptMethod.getCrACAccountId(), "crACAccountId", error, null);
		}
	}

	@Override
	public void validate(Object obj, Errors error) {
		throw new RuntimeException(ValidatorMessages.getString("ReceiptMethodValidator.5"));
	}
}
