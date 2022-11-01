package eulap.eb.validator.inv;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.StockAdjustmentType;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.AccountService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.StockAdjustmentTypeService;
import eulap.eb.validator.ValidatorMessages;

/**
 * Validation class for Stock Adjustment Type.

 *
 */
@Service
public class StockAdjustmentTypeValidator implements Validator{
	private static Logger logger = Logger.getLogger(StockAdjustmentTypeValidator.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountCombinationService acService;
	@Autowired
	private StockAdjustmentTypeService satService;

	@Override
	public boolean supports(Class<?> clazz) {
		return StockAdjustmentType.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		StockAdjustmentType sat = (StockAdjustmentType) obj;
		boolean isValidCombination = true;

		Integer companyId = sat.getCompanyId();
		Integer divisionId = sat.getDivisionId();
		logger.debug("Validating the company id:"+companyId);
		if(companyId == null) {
			isValidCombination = false;
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.0"));
		} else if(!companyService.getCompany(companyId).isActive()) {
			isValidCombination = false;
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.1"));
		}

		logger.debug("Validating the name: "+sat.getName());
		if(sat.getName() == null)
			errors.rejectValue("name", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.2"));
		else if(sat.getName().trim().isEmpty())
			errors.rejectValue("name", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.3"));
		else if(sat.getName().length() > 100)
			errors.rejectValue("name", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.4"));
		else if (companyId != null && divisionId != null) {
			if (!satService.isUniqueSATName(sat.getName(), companyId, sat.getId(), divisionId)) {
				errors.rejectValue("name", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.5"));
			}
		}

		logger.debug("Validating the division id: "+divisionId);
		if(divisionId == 0) {
			isValidCombination = false;
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.6"));
		} else if(!divisionService.getDivision(divisionId).isActive()) {
			isValidCombination = false;
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.7"));
		}

		Integer accountId = sat.getAccountId();
		logger.debug("Validating the charged account id: "+accountId);
		if(accountId == 0) {
			isValidCombination = false;
			errors.rejectValue("accountId", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.8"));
		} else if(!accountService.getAccount(accountId).isActive()) {
			isValidCombination = false;
			errors.rejectValue("accountId", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.9"));
		}

		AccountCombination acctCombi = null;
		if(isValidCombination) {
			acctCombi = acService.getAccountCombination(companyId, divisionId, accountId);
			if(acctCombi == null)
				errors.rejectValue("accountId", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.10"));
			else if(!acctCombi.isActive())
				errors.rejectValue("accountId", null, null, ValidatorMessages.getString("StockAdjustmentTypeValidator.11"));
		}

		logger.debug("============>> Freeing up memory allocation.");
		companyId = null;
		divisionId = null;
		accountId = null;
		acctCombi = null;
	}
}
