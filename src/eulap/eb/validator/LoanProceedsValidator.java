package eulap.eb.validator;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.ReceiptMethodDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.LPLine;
import eulap.eb.domain.hibernate.LoanProceeds;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.LoanProceedsService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.SupplierService;
import eulap.eb.service.TermService;
import eulap.eb.service.TimePeriodService;

/**
 * Validation class for LoanProceeds.

 */
@Service
public class LoanProceedsValidator implements Validator{
	@Autowired
	private LoanProceedsService invoiceService;
	@Autowired
	private AccountCombinationService acctCombinationService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ReferenceDocumentService documentService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private TermService termService;
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private ReceiptMethodDao receiptMethodDao;
	@Autowired
	private AccountCombinationDao combinationDao;
	@Autowired
	private AccountDao accountDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return LoanProceeds.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		LoanProceeds loanProceeds = (LoanProceeds) target;
		Integer companyId = loanProceeds.getCompanyId();
		ValidatorUtil.validateCompany(companyId, companyService, errors, "companyId");
		Currency currency = null;
		Division division = null;

		//Division
		if(loanProceeds.getDivisionId() == null) {
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("LoanProceedsValidator.0"));
		} else {
			division = divisionService.getDivision(loanProceeds.getDivisionId());
			if(!division.isActive()) {
				errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("LoanProceedsValidator.1"));
			}
		}

		//Currency
		if(loanProceeds.getCurrencyId() == null) {
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("CurrencyRateService.3"));
		} else {
			currency = currencyService.getCurency(loanProceeds.getCurrencyId());
			if(!currency.isActive()) {
				errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("LoanProceedsValidator.2"));
			}
		}

		//Description
		if (loanProceeds.getDescription() == null || loanProceeds.getDescription().trim().isEmpty()) {
			errors.rejectValue("description", null, null,  ValidatorMessages.getString("LoanProceedsValidator.3"));
		}

		//Amount
		double headerAmount = NumberFormatUtil.roundOffTo2DecPlaces(loanProceeds.getAmount());
		if (headerAmount > 10000000000.00) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("LoanProceedsValidator.4"));
		} else if (headerAmount < -10000000000.00) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("LoanProceedsValidator.5"));
		} else if (headerAmount <= 0) {
			errors.rejectValue("amount", null, null,  ValidatorMessages.getString("LoanProceedsValidator.6"));
		}

		//Supplier Name
		if (loanProceeds.getSupplierId() == null || loanProceeds.getSupplierId() == 0) {
			errors.rejectValue("supplierId", null, null, ValidatorMessages.getString("LoanProceedsValidator.7"));	
		} else {
			//Inactive Supplier
			Supplier supplier = supplierService.getSupplier(loanProceeds.getSupplierId());
			if (!supplier.isActive()) {
				errors.rejectValue("supplierId", null, null, ValidatorMessages.getString("LoanProceedsValidator.8"));
			}
		}

		if(loanProceeds.getTermId() == null) {
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("LoanProceedsValidator.9"));
		} else if (!termService.getTerm(loanProceeds.getTermId()).isActive()) {
			errors.rejectValue("termId", null, null, ValidatorMessages.getString("LoanProceedsValidator.10"));
		}

		//Supplier Account
		boolean hasSupplierAccount = true;
		if (loanProceeds.getSupplierAccountId() == null || loanProceeds.getSupplierAccountId() == 0) {
			errors.rejectValue("supplierAccountId", null, null, ValidatorMessages.getString("LoanProceedsValidator.11"));
			hasSupplierAccount = false;
		}

		//Inactive Supplier Account
		if (hasSupplierAccount) {
			SupplierAccount supplierAccount = invoiceService.getSupplierAccount(loanProceeds.getSupplierAccountId());
			if (!supplierAccount.isActive()) {
				errors.rejectValue("supplierAccountId", null, null, ValidatorMessages.getString("APInvoiceValidator.10"));
				hasSupplierAccount = false;
			}
			//Validate supplier in supplier account
			if (supplierAccount.getSupplierId() != loanProceeds.getSupplierId()){
				errors.rejectValue("supplierAccountId", null, null, ValidatorMessages.getString("APInvoiceValidator.11"));
			}
		}

		//Invoice Date
		if (loanProceeds.getDate() == null) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("LoanProceedsValidator.12"));
		}

		//GL Date
		if (loanProceeds.getGlDate() == null) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("LoanProceedsValidator.13"));
		} else if (!timePeriodService.isOpenDate(loanProceeds.getGlDate())) {
			errors.rejectValue("glDate", null, null, ValidatorMessages.getString("LoanProceedsValidator.14"));
		}

		if(loanProceeds.getReceiptMethodId() == null) {
			//Receipt method is required.
			errors.rejectValue("receiptMethodId", null, null, ValidatorMessages.getString("LoanProceedsValidator.31"));
		} else if(!receiptMethodDao.get(loanProceeds.getReceiptMethodId()).isActive()) {
			//Inactive reciept method.
			errors.rejectValue("receiptMethodId", null, null, ValidatorMessages.getString("LoanProceedsValidator.32"));
		}

		Integer loanAcctId = loanProceeds.getLoanAccountId();
		if(loanProceeds.getLoanAccountId() == null) {
			//Loan account is required.
			errors.rejectValue("loanAccountId", null, null, ValidatorMessages.getString("LoanProceedsValidator.33"));
		} else if(!combinationDao.getAccountCombination(companyId, loanProceeds.getDivisionId(), loanAcctId).isActive()) {
			//Account combination is inactive.
			errors.rejectValue("loanAccountId", null, null, ValidatorMessages.getString("LoanProceedsValidator.34"));
		} else if(!accountDao.get(loanAcctId).isActive()) {
			//Account is inactive.
			errors.rejectValue("loanAccountId", null, null, ValidatorMessages.getString("LoanProceedsValidator.35"));
		}

		int rowNumber = 0;
		// AP Lines
		Collection<LPLine> lpLines = loanProceeds.getlPlines();
		boolean isEmptyApline = true;
		Company company = companyService.getCompany(loanProceeds.getCompanyId());
		for(LPLine line : lpLines) {
			rowNumber++;
			line.setCompanyNumber(company.getCompanyNumber());
			if ((line.getCompanyNumber() != null && !line.getCompanyNumber().trim().isEmpty()) && 
					(line.getDivisionNumber() != null && !line.getDivisionNumber().trim().isEmpty()) && 
					(line.getAccountNumber() != null && !line.getAccountNumber().trim().isEmpty())) {
				isEmptyApline = false;
				Integer serviceLeaseKeyId = company.getServiceLeaseKeyId();
				AccountCombination ac = invoiceService.getAccountCombination(line);
				if (ac == null) {
					errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.15")+ " "+ rowNumber + ".");
					break;
				}

				//Amount
				if (line.getAmount() == 0.0 || line.getAmount() >= 10000000000.00) {
					errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.16")+ " "+rowNumber+".");
				}

				boolean inactiveDA = false;
				//Validate active , division, and account number
				if(!divisionService.getDivision(line.getDivisionId()).isActive()) {
					errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.17")+ " "+ rowNumber+".");
					inactiveDA = true;
				}
				if(!ac.isActive()) {
					errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.18")+ " "+ rowNumber+".");
					inactiveDA = true;
				}
				//Validate the Company, Division and Account Number if it is existing
				if (!inactiveDA) {
					if (!invoiceService.isExistingAcctCombi(line, serviceLeaseKeyId, 1)) {
						errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.19")+ " "+ rowNumber+".");
						break;
					} else if (!invoiceService.isExistingAcctCombi(line, serviceLeaseKeyId, 2)){
						errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.20")+rowNumber+".");
						break;
					} else if (!invoiceService.isExistingAcctCombi(line, serviceLeaseKeyId, 3)) {
						errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.21")+rowNumber+".");
						break;
					}
				}

				//Company Id
				if (hasSupplierAccount) {
					SupplierAccount sa = invoiceService.getSupplierAccount(loanProceeds.getSupplierAccountId());
					ac = acctCombinationService.getAccountCombination(line.getCompanyNumber(), "", "");
					if (sa.getCompanyId() != ac.getCompanyId()) {
						errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.22"));
						break;
					}

					if (sa.getDefaultDebitACId() != null) { //Validate if there is a default debit account
						if (!invoiceService.isActiveAC(loanProceeds.getSupplierAccountId())) { //Inactive Account Combination
							errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.23"));
							break;
						} else if (!invoiceService.isActiveAcctCombi(line, 1)) { //Company
							errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.24"));
							break;
						} else if (!invoiceService.isActiveAcctCombi(line, 2)) { //Division
							errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.25"));
							break;
						} else if (!invoiceService.isActiveAcctCombi(line, 3)) { //Account
							errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.26"));
							break;
						}
					}
				}
			} else if (((line.getCompanyNumber() == null || line.getCompanyNumber().trim().isEmpty())
					&& (line.getDivisionNumber() == null || line.getDivisionNumber().trim().isEmpty())
					&& (line.getAccountNumber() == null || line.getAccountNumber().trim().isEmpty()))
					&& (line.getAmount() != 0.0 || (line.getDescription() != null && !line.getDescription().trim().isEmpty()))) {
				errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.27")+ " "+ rowNumber + ".");
				isEmptyApline = false;
			}

			if (!hasAccCombination(line)) {
				errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.27")+ " "+ rowNumber + ".");
			}

			// Description
			if ((line.getDescription() == null || line.getDescription().trim().isEmpty())
					&& line.getAmount() != 0.0 && !isEmptyApline) {
				errors.rejectValue("lPlineMessage", null, null, ValidatorMessages.getString("LoanProceedsValidator.28")+" " +rowNumber+".");
			}
		}

		rowNumber = 0;

		//Validating reference document.
		documentService.validateReferences(loanProceeds.getReferenceDocuments(), errors);

	}

	private boolean hasAccCombination(LPLine lpLine) {
		if ((lpLine.getCompanyNumber() == null || lpLine.getCompanyNumber().trim().isEmpty())
				|| (lpLine.getDivisionNumber() == null || lpLine.getDivisionNumber().trim().isEmpty())
				|| (lpLine.getAccountNumber() == null || lpLine.getAccountNumber().trim().isEmpty())) {
			return false;
		}
		return true;
	}
}