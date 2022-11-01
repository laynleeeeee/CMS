package eulap.eb.validator;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.GeneralLedgerService;
import eulap.eb.service.GlEntryService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.web.dto.GeneralLedgerDto;

/**
 * General ledger entry validator.
 * 

 *
 */
@Service
public class GlEntryValidator implements Validator {
	@Autowired
	private GeneralLedgerService generalLedgerService;
	@Autowired
	private GlEntryService glEntryService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private CurrencyService currencyService;

	@Override
	public boolean supports(Class<?> clazz) {
		return GlEntry.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors error) {
		GeneralLedgerDto generalLedgerDto = (GeneralLedgerDto) object;
		GeneralLedger generalLedger = generalLedgerDto.getGeneralLedger();
		Collection<GlEntry> glEntries = generalLedgerDto.getGlEntries();
		Collection<TimePeriod> timePeriods = generalLedgerService.getOpenTimePeriods();

		double debit = glEntryService.getTotalAmt(glEntries, Boolean.TRUE);
		double credit = glEntryService.getTotalAmt(glEntries, Boolean.FALSE);
		if (debit != credit) {
			error.rejectValue("gLlineMessage", null, null, ValidatorMessages.getString("GlEntryValidator.0"));
		}

		if (generalLedger.getGlDate() == null) {
			error.rejectValue("generalLedger.glDate", null, null, ValidatorMessages.getString("GlEntryValidator.1"));
		} else {
			boolean isWithin = false;
			for (TimePeriod tp : timePeriods) {
				Date glDate = generalLedger.getGlDate();
				Date dateFrom = tp.getDateFrom();
				Date dateTo = tp.getDateTo();

				if (glDate.compareTo(dateFrom) >= 0 && glDate.compareTo(dateTo) <= 0) {
					isWithin = true;
					break;
				}
			}
			if (!isWithin) {
				error.rejectValue("generalLedger.glDate", null, null,
						ValidatorMessages.getString("GlEntryValidator.2"));
			}
		}

		if (generalLedger.getComment().trim().isEmpty()) {
			error.rejectValue("generalLedger.comment", null, null, ValidatorMessages.getString("GlEntryValidator.3"));
		}

		if (glEntries.size() == 0) {
			error.rejectValue("glEntrySize", null, null, ValidatorMessages.getString("GlEntryValidator.4"));
		}

		boolean hasZeroOrNegativeAmount = false;
		boolean isAllEqual = true;
		boolean isValidAmount = true;
		boolean isValidAcctCombination = true;
		boolean isValidDescription = true;
		int companyId = glEntries.size() > 0 ? glEntries.iterator().next().getCompanyId() : 0;
		int ctr = 1;
		for (GlEntry gl : glEntries) {
			// Validate the amount
			boolean isZeroOrLess = gl.getDebitAmount() < 0.0 || gl.getCreditAmount() < 0.0;
			boolean isBothDebitOrCreditZero = gl.getDebitAmount() == 0.0 && gl.getCreditAmount() == 0.0;
			if ((gl.getAmount() <= 0.0) || isZeroOrLess || isBothDebitOrCreditZero) {
				hasZeroOrNegativeAmount = true;
			} else if (gl.getDebitAmount() != 0.0 && gl.getCreditAmount() != 0.0) {
				isValidAmount = false;
			}
			if (companyId != 0 && gl.getCompanyId() != 0 && gl.getCompanyId() != companyId) {
				isAllEqual = false;
			}
			if (gl.getAccountCombinationId() == 0) {
				isValidAcctCombination = false;
			}
			// Validate the description per entry
			if (gl.getDescription() == null || gl.getDescription().isEmpty()) {
				isValidDescription = false;
			}
			// Validate account combination
			if (gl != null) {
				if (!generalLedgerService.isActiveGlLineCombination(gl.getAccountCombinationId())) {
					error.rejectValue("gLlineMessage", null, null, ValidatorMessages.getString("GlEntryValidator.5"));
					break;
				} else if (!generalLedgerService.isActiveAcctCombination(gl.getAccountCombinationId(), 1)) {
					error.rejectValue("gLlineMessage", null, null, ValidatorMessages.getString("GlEntryValidator.6"));
					break;
				} else if (!generalLedgerService.isActiveAcctCombination(gl.getAccountCombinationId(), 2)) {
					error.rejectValue("gLlineMessage", null, null, ValidatorMessages.getString("GlEntryValidator.7"));
					break;
				} else if (!generalLedgerService.isActiveAcctCombination(gl.getAccountCombinationId(), 3)) {
					error.rejectValue("gLlineMessage", null, null, ValidatorMessages.getString("GlEntryValidator.8"));
					break;
				} else if (gl.getAccountCombinationId() == 0 && gl.getAmount() != 0)
					error.rejectValue("gLlineMessage", null, null, ValidatorMessages.getString("GlEntryValidator.9")
							+ ctr + ValidatorMessages.getString("GlEntryValidator.10"));
			}
			ctr++;
		}

		if (hasZeroOrNegativeAmount)
			error.rejectValue("glEntry.amount", null, null, ValidatorMessages.getString("GlEntryValidator.11"));
		else if (!isValidAmount)
			error.rejectValue("glEntry.amount", null, null, ValidatorMessages.getString("GlEntryValidator.13"));
		if (!isAllEqual)
			error.rejectValue("glEntry.companyId", null, null, ValidatorMessages.getString("GlEntryValidator.12"));
		if (!isValidAcctCombination)
			error.rejectValue("glEntry.accountCombinationId", null, null,
					ValidatorMessages.getString("GlEntryValidator.14"));
		if (!isValidDescription)
			error.rejectValue("glEntry.description", null, null, ValidatorMessages.getString("GlEntryValidator.15"));

		// Validate form status
		FormWorkflow workflow = generalLedger.getId() != 0 ? generalLedgerService.getFormWorkflow(generalLedger.getId())
				: null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null) {
			error.rejectValue("generalLedger.formWorkflowId", null, null, workflowError);
		}

		boolean hasDivision = generalLedger.getDivisionId() != null;
		if (hasDivision) {
			if (generalLedger.getCurrencyId() == null) {
				error.rejectValue("generalLedger.currencyId", null, null, ValidatorMessages.getString("ArTransactionValidator.22"));
			} else if (!currencyService.getCurency(generalLedger.getCurrencyId()).isActive()) {
				error.rejectValue("generalLedger.currencyId", null, null, ValidatorMessages.getString("ArTransactionValidator.23"));
			}

			if (!divisionService.getDivision(generalLedger.getDivisionId()).isActive()) {
				error.rejectValue("generalLedger.divisionId", null, null, ValidatorMessages.getString("ArTransactionValidator.24"));
			}
		}
		refDocumentService.validateReferences(generalLedgerDto.getReferenceDocuments(), error);
	}
}
