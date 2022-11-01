package eulap.eb.validator;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.LoanProceedsDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.APLine;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApPaymentInvoice;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Currency;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.InvoiceImportationDetails;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.LoanProceeds;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.APInvoiceService;
import eulap.eb.service.AccountCombinationService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CurrencyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.TermService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;

/**
 * Validation class for APInvoice.

 */
@Service
public class APInvoiceValidator implements Validator{
	@Autowired
	private APInvoiceService invoiceService;
	@Autowired
	private AccountCombinationService acctCombinationService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ReferenceDocumentService documentService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private TermService termService;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private LoanProceedsDao loanProceedsDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return APInvoice.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		//Do Nothing.
	}

	public void validate(Object target, Errors errors, User user) {
		validate(target, errors, user, "", true, false);
	}

	public void validate(Object target, Errors errors, User user, String fieldPrepend, boolean isValidateTotalAmount, boolean isValidateHeaderFieldsOnly) {
		validate(target, errors, user, "", true, false, false);
	}

	public void validate(Object target, Errors errors, User user, String fieldPrepend,
			boolean isValidateTotalAmount, boolean isValidateHeaderFieldsOnly, boolean requireInvoiceNumber) {
		APInvoice invoice = (APInvoice) target;
		Collection<ApPaymentInvoice> ap = invoiceService.getPaidInvoices(invoice.getId()); 
		Integer companyId = invoice.getCompanyId();
		ValidatorUtil.validateCompany(companyId, companyService, errors, "companyId");
		Currency currency = null;
		Division division = null;
		//Invoice Number
		boolean isImportation = false;
		int invoiceTypeId = invoice.getInvoiceTypeId();
		if(invoiceTypeId >= InvoiceType.API_IMPORT_CENTRAL && invoice.getInvoiceTypeId() <= InvoiceType.API_IMPORT_NSB8A) {
			isImportation = true;
		}

		if(!invoiceService.isLoanInvoiceType(invoiceTypeId)) {
			if(requireInvoiceNumber) {
				if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().trim().isEmpty()) {
					errors.rejectValue(fieldPrepend+"invoiceNumber", null, null, ValidatorMessages.getString("APInvoiceValidator.34"));
				} else if (invoice.getInvoiceNumber().trim().length() > 100) {
					errors.rejectValue(fieldPrepend+"invoiceNumber", null, null, ValidatorMessages.getString("APInvoiceValidator.35"));
				} else if (invoice.getSupplierId() != null &&
						!apInvoiceDao.isUniqueInvoiceNoBySupplier(invoice.getSupplierId(), invoice.getId(), invoice.getInvoiceNumber())) {
					errors.rejectValue(fieldPrepend+"invoiceNumber", null, null, ValidatorMessages.getString("APInvoiceValidator.36"));
				}
			}

			//Reference number
//			if (invoice.getReferenceNo() == null || invoice.getReferenceNo().trim().isEmpty())
//				errors.rejectValue(fieldPrepend+"referenceNo", null, null, ValidatorMessages.getString("APInvoiceValidator.34"));
//			else if (invoice.getReferenceNo().trim().length() > 50)
//				errors.rejectValue(fieldPrepend+"referenceNo", null, null, ValidatorMessages.getString("APInvoiceValidator.35"));
//			else if (!invoiceService.isUniqueReferenceNo(invoice))
//				errors.rejectValue(fieldPrepend+"referenceNo", null, null, ValidatorMessages.getString("APInvoiceValidator.36"));

			//Division
			if(invoice.getDivisionId() == null) {
				errors.rejectValue(fieldPrepend+"divisionId", null, null, ValidatorMessages.getString("ApLineSetupValidator.4"));
			} else {
				division = divisionService.getDivision(invoice.getDivisionId());
				if(!division.isActive()) {
					errors.rejectValue(fieldPrepend+"divisionId", null, null, ValidatorMessages.getString("ApLineSetupValidator.9"));
				}
			}

			//Currency
			if(invoice.getCurrencyId() == null) {
				errors.rejectValue(fieldPrepend+"currencyId", null, null, ValidatorMessages.getString("CurrencyRateService.3"));
			} else {
				currency = currencyService.getCurency(invoice.getCurrencyId());
				if(!currency.isActive()) {
					errors.rejectValue(fieldPrepend+"currencyId", null, null, ValidatorMessages.getString("APInvoiceValidator.38"));
				}
			}

			//Bms no
			if (invoice.getBmsNumber().trim().length() > 50)
				errors.rejectValue(fieldPrepend+"bmsNumber", null, null, ValidatorMessages.getString("APInvoiceValidator.37"));

			//Description
			if (invoice.getDescription() == null || invoice.getDescription().trim().isEmpty())
				errors.rejectValue(fieldPrepend+"description", null, null,  ValidatorMessages.getString("APInvoiceValidator.3"));

			//Amount
			double headerAmount = NumberFormatUtil.roundOffTo2DecPlaces(invoice.getAmount());
			if(isImportation) {
				if (headerAmount > 10000000000.00) {
					errors.rejectValue(fieldPrepend+"amount", null, null, ValidatorMessages.getString("APInvoiceValidator.4"));
				}
				else if (headerAmount < -10000000000.00) {
					errors.rejectValue(fieldPrepend+"amount", null, null, ValidatorMessages.getString("APInvoiceValidator.5"));
				}
				//Invoice Importation Details
				InvoiceImportationDetails iid = removeWhiteSpacesIID(invoice.getInvoiceImportationDetails());
				if(iid.getImportEntryNo().trim() != "") {
					if(iid.getImportEntryNo().trim().length() > InvoiceImportationDetails.MAX_FIELD_CHARACTERS) {
						errors.rejectValue(fieldPrepend+"invoiceImportationDetails.importEntryNo", null, null, ValidatorMessages.getString("APInvoiceValidator.46"));
					}
				}
				if(iid.getRegisteredName().trim() != null) {
					if(iid.getRegisteredName().trim().length() > InvoiceImportationDetails.MAX_FIELD_CHARACTERS) {
						errors.rejectValue(fieldPrepend+"invoiceImportationDetails.registeredName", null, null, ValidatorMessages.getString("APInvoiceValidator.47"));
					}
				}
				if(iid.getCountryOfOrigin().trim() != null) {
					if(iid.getCountryOfOrigin().trim().length() > InvoiceImportationDetails.MAX_FIELD_CHARACTERS) {
						errors.rejectValue(fieldPrepend+"invoiceImportationDetails.countryOfOrigin", null, null, ValidatorMessages.getString("APInvoiceValidator.48"));
					}
				}
				if(iid.getOrNumber().trim() != null) {
					if(iid.getOrNumber().trim().length() > InvoiceImportationDetails.MAX_FIELD_CHARACTERS) {
						errors.rejectValue(fieldPrepend+"invoiceImportationDetails.orNumber", null, null, ValidatorMessages.getString("APInvoiceValidator.49"));
					}
				}
			} else {
				if (headerAmount > 10000000000.00)
					errors.rejectValue(fieldPrepend+"amount", null, null, ValidatorMessages.getString("APInvoiceValidator.4"));
				else if (headerAmount < -10000000000.00)
					errors.rejectValue(fieldPrepend+"amount", null, null, ValidatorMessages.getString("APInvoiceValidator.5"));
				else if (invoice.getInvoiceClassificationId() == 1 || invoice.getInvoiceClassificationId() == 2 || invoice.getInvoiceClassificationId() == 4) {
					if (headerAmount < 0)
						errors.rejectValue(fieldPrepend+"amount", null, null,  ValidatorMessages.getString("APInvoiceValidator.6"));
				} else if (invoice.getInvoiceClassificationId() == 3) {
					if (headerAmount > 0)
						errors.rejectValue(fieldPrepend+"amount", null, null, ValidatorMessages.getString("APInvoiceValidator.7"));
				}
			}

			//Supplier Name
			if (invoice.getSupplierId() == null || invoice.getSupplierId() == 0) {
				errors.rejectValue(fieldPrepend+"supplierId", null, null, ValidatorMessages.getString("APInvoiceValidator.8"));	
			} else {
				//Inactive Supplier
				Supplier supplier = invoiceService.getSupplier(invoice.getSupplierId());
				if (!supplier.isActive()) {
					errors.rejectValue(fieldPrepend+"supplierId", null, null, ValidatorMessages.getString("APInvoiceValidator.9"));
				}
			}

			if(invoice.getTermId() == null) {
				errors.rejectValue(fieldPrepend+"termId", null, null, ValidatorMessages.getString("APInvoiceValidator.39"));
			} else if (!termService.getTerm(invoice.getTermId()).isActive()) {
				errors.rejectValue(fieldPrepend+"termId", null, null, ValidatorMessages.getString("APInvoiceValidator.40"));
			}

			//Supplier Account
			boolean hasSupplierAccount = true;
			if (invoice.getSupplierAccountId() == null || invoice.getSupplierAccountId() == 0) {
				errors.rejectValue(fieldPrepend+"supplierAccountId", null, null, ValidatorMessages.getString("APInvoiceValidator.43"));
				hasSupplierAccount = false;
			}

			//Inactive Supplier Account
			if (hasSupplierAccount) {
				SupplierAccount supplierAccount = invoiceService.getSupplierAccount(invoice.getSupplierAccountId());
				if (!supplierAccount.isActive()) {
					errors.rejectValue(fieldPrepend+"supplierAccountId", null, null, ValidatorMessages.getString("APInvoiceValidator.10"));
					hasSupplierAccount = false;
				}
				//Validate supplier in supplier account
				if (supplierAccount.getSupplierId() != invoice.getSupplierId()){
					errors.rejectValue(fieldPrepend+"supplierAccountId", null, null, ValidatorMessages.getString("APInvoiceValidator.11"));
				}
			}

			//Invoice Date
			if (invoice.getInvoiceDate() == null)
				errors.rejectValue(fieldPrepend+"invoiceDate", null, null, ValidatorMessages.getString("APInvoiceValidator.12"));

			//GL Date
			boolean validateDueDate = true;
			if (invoice.getGlDate() == null) {
				errors.rejectValue(fieldPrepend+"glDate", null, null, ValidatorMessages.getString("APInvoiceValidator.13"));
				validateDueDate = false;
			} else if (!timePeriodService.isOpenDate(invoice.getGlDate())) {
				errors.rejectValue(fieldPrepend+"glDate", null, null, ValidatorMessages.getString("APInvoiceValidator.14"));
			}

			//Due Date
			if (invoice.getDueDate() == null)
				errors.rejectValue(fieldPrepend+"dueDate", null, null, ValidatorMessages.getString("APInvoiceValidator.15"));
			else if (validateDueDate) {
				if (invoice.getDueDate().before(invoice.getGlDate()))
				errors.rejectValue(fieldPrepend+"dueDate", null, null, ValidatorMessages.getString("APInvoiceValidator.16"));
			}

			int rowNumber = 0;
			if (!isValidateHeaderFieldsOnly) {
				// AP Lines
				double totalAmount = 0;
				double totalVatAmount = 0;
				boolean validateTotalAmt = true;
				Collection<APLine> apLines = invoice.getaPlines();
				boolean isEmptyApline = true;
				Company company = companyService.getCompany(invoice.getCompanyId());
				for(APLine apLine : apLines) {
					rowNumber++;
					apLine.setCompanyNumber(company.getCompanyNumber());
					if ((apLine.getCompanyNumber() != null && !apLine.getCompanyNumber().trim().isEmpty()) && 
							(apLine.getDivisionNumber() != null && !apLine.getDivisionNumber().trim().isEmpty()) && 
							(apLine.getAccountNumber() != null && !apLine.getAccountNumber().trim().isEmpty())) {
						isEmptyApline = false;
						validateTotalAmt = false;
						AccountCombination ac = invoiceService.getAccountCombination(apLine);
						if (ac == null) {
							errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.17")+ " "+ rowNumber + ".");
							break;
						}
						//Amount
						if(isImportation) {
							if (apLine.getVatAmount() == 0.0 || apLine.getVatAmount() >= 10000000000.00) {
								errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.44")+ " "+rowNumber+".");
							}
							if(apLine.getTaxTypeId() == null) {
								errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.45")+ " "+rowNumber+".");
							}
						} else {
							if (apLine.getAmount() == 0.0 || apLine.getAmount() >= 10000000000.00) {
								errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.18")+ " "+rowNumber+".");
							}
						}

						totalAmount += apLine.getAmount();
						totalVatAmount += apLine.getVatAmount() != null ? apLine.getVatAmount() : 0.0;

						boolean inactiveDA = false;
						//Validate active , division, and account number
						if(!divisionService.getDivision(apLine.getDivisionId()).isActive()) {
							errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.41")+ " "+ rowNumber+".");
							inactiveDA = true;
						}
						if(!ac.isActive()) {
							errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.42")+ " "+ rowNumber+".");
							inactiveDA = true;
						}
						//Validate the Company, Division and Account Number if it is existing
						if (!inactiveDA) {
							if (!invoiceService.isExistingAcctCombi(apLine, user.getServiceLeaseKeyId(), 1)) {
								errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.19")+ " "+ rowNumber+".");
								break;
							} else if (!invoiceService.isExistingAcctCombi(apLine, user.getServiceLeaseKeyId(), 2)){
								errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.20")+rowNumber+".");
								break;
							} else if (!invoiceService.isExistingAcctCombi(apLine, user.getServiceLeaseKeyId(), 3)) {
								errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.21")+rowNumber+".");
								break;
							}
						}

						//Company Id
						if (hasSupplierAccount) {
							SupplierAccount sa = invoiceService.getSupplierAccount(invoice.getSupplierAccountId());
							ac = acctCombinationService.getAccountCombination(apLine.getCompanyNumber(), "", "");
							if (sa.getCompanyId() != ac.getCompanyId()) {
								errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.22"));
								break;
							}

							if (sa.getDefaultDebitACId() != null) { //Validate if there is a default debit account
								if (!invoiceService.isActiveAC(invoice.getSupplierAccountId())) { //Inactive Account Combination
									errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.23"));
									break;
								} else if (!invoiceService.isActiveAcctCombi(apLine, 1)) { //Company
									errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.24"));
									break;
								} else if (!invoiceService.isActiveAcctCombi(apLine, 2)) { //Division
									errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.25"));
									break;
								} else if (!invoiceService.isActiveAcctCombi(apLine, 3)) { //Account
									errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.26"));
									break;
								}
							}
						}
					} else if (((apLine.getCompanyNumber() == null || apLine.getCompanyNumber().trim().isEmpty())
							&& (apLine.getDivisionNumber() == null || apLine.getDivisionNumber().trim().isEmpty())
							&& (apLine.getAccountNumber() == null || apLine.getAccountNumber().trim().isEmpty()))
							&& (apLine.getAmount() != 0.0 || (apLine.getDescription() != null && !apLine.getDescription().trim().isEmpty()))) {
						errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.27")+ " "+ rowNumber + ".");
						isEmptyApline = false;
					}

					if (!hasAccCombination(apLine)) {
						errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.27")+ " "+ rowNumber + ".");
					}

					// Description
					if ((apLine.getDescription() == null || apLine.getDescription().trim().isEmpty())
							&& apLine.getAmount() != 0.0 && !isEmptyApline) {
						errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.28")+" " +rowNumber+".");
					}

					validateTotalAmt = true;
				}

				//Is empty AP Line
				if (isEmptyApline){
					errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.29"));
				}

				double totalApLineAmount = totalAmount + totalVatAmount;
				if (invoice.getWtAcctSettingId() != null) {
					totalApLineAmount = totalApLineAmount - NumberFormatUtil.roundOffTo2DecPlaces(invoice.getWtAmount());
				}

				// Total amount
				totalApLineAmount = NumberFormatUtil.roundOffRuleOfFive(totalApLineAmount);
				if ((totalApLineAmount != headerAmount) && validateTotalAmt && isValidateTotalAmount && !isImportation) {
					errors.rejectValue(fieldPrepend+"wtAmount", null, null, ValidatorMessages.getString("APInvoiceValidator.33"));
				} else if ((NumberFormatUtil.roundOffTo2DecPlaces(totalVatAmount) != headerAmount) && isImportation) {
					errors.rejectValue(fieldPrepend+"wtAmount", null, null, ValidatorMessages.getString("APInvoiceValidator.33"));
				}

				//Has paid invoices
				String checkNumbers = "";
				for (ApPaymentInvoice apPaymentInvoice : ap) {
					if (invoice.getId() == apPaymentInvoice.getApInvoice().getId()){
						FormStatus fs = apPaymentInvoice.getApPayment().getFormWorkflow().getCurrentFormStatus();
						if (fs.getId() != FormStatus.CANCELLED_ID)
							checkNumbers += invoiceService.getApPaymentCheckNumbers(apPaymentInvoice.getApPaymentId()) + " ";
					}
				}

				if (!checkNumbers.isEmpty()){
					errors.rejectValue(fieldPrepend+"aPlineMessage", null, null,ValidatorMessages.getString("APInvoiceValidator.31")+" "+checkNumbers+ ".");
				}

			}

			rowNumber = 0;

			//Validating reference document.
			documentService.validateReferences(invoice.getReferenceDocuments(), errors);

			//Validate form status
			FormWorkflow workflow = invoice.getId() != 0 ? invoiceService.getFormWorkflow(invoice.getId()) : null;
			String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
			if (workflowError != null ) {
				errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
			}
		} else {
			validateApLoan(invoice, errors, user, fieldPrepend);
		}
	}

	private boolean hasAccCombination(APLine apLine) {
		if ((apLine.getCompanyNumber() == null || apLine.getCompanyNumber().trim().isEmpty())
				|| (apLine.getDivisionNumber() == null || apLine.getDivisionNumber().trim().isEmpty())
				|| (apLine.getAccountNumber() == null || apLine.getAccountNumber().trim().isEmpty())) {
			return false;
		}
		return true;
	}

	private InvoiceImportationDetails removeWhiteSpacesIID(InvoiceImportationDetails iid) {
		if(iid.getImportEntryNo().trim() != null) {
			iid.setImportEntryNo(StringFormatUtil.removeExtraWhiteSpaces(iid.getImportEntryNo()).trim());
		}
		if(iid.getRegisteredName().trim() != null) {
			iid.setRegisteredName(StringFormatUtil.removeExtraWhiteSpaces(iid.getRegisteredName()).trim());
		}
		if(iid.getCountryOfOrigin().trim() != null) {
			iid.setCountryOfOrigin(StringFormatUtil.removeExtraWhiteSpaces(iid.getCountryOfOrigin()).trim());
		}
		if(iid.getCountryOfOrigin().trim() != null) {
			iid.setCountryOfOrigin(StringFormatUtil.removeExtraWhiteSpaces(iid.getCountryOfOrigin()).trim());
		}
		return iid;
	}

	private void validateApLoan(APInvoice invoice, Errors errors, User user, String fieldPrepend) {
		Currency currency = null;
		Division division = null;
		//Division
		if(invoice.getDivisionId() == null) {
			errors.rejectValue(fieldPrepend+"divisionId", null, null, ValidatorMessages.getString("ApLineSetupValidator.4"));
		} else {
			division = divisionService.getDivision(invoice.getDivisionId());
			if(!division.isActive()) {
				errors.rejectValue(fieldPrepend+"divisionId", null, null, ValidatorMessages.getString("ApLineSetupValidator.9"));
			}
		}

		//Currency
		if(invoice.getCurrencyId() == null) {
			errors.rejectValue(fieldPrepend+"currencyId", null, null, ValidatorMessages.getString("CurrencyRateService.3"));
		} else {
			currency = currencyService.getCurency(invoice.getCurrencyId());
			if(!currency.isActive()) {
				errors.rejectValue(fieldPrepend+"currencyId", null, null, ValidatorMessages.getString("APInvoiceValidator.38"));
			}
		}

		//Supplier Name
		if (invoice.getSupplierId() == null || invoice.getSupplierId() == 0) {
			errors.rejectValue(fieldPrepend+"supplierId", null, null, ValidatorMessages.getString("APInvoiceValidator.53"));	
		} else {
			//Inactive Supplier
			Supplier supplier = invoiceService.getSupplier(invoice.getSupplierId());
			if (!supplier.isActive()) {
				errors.rejectValue(fieldPrepend+"supplierId", null, null, ValidatorMessages.getString("APInvoiceValidator.54"));
			}
		}

		//Supplier Account
		boolean hasSupplierAccount = true;
		if (invoice.getSupplierAccountId() == null || invoice.getSupplierAccountId() == 0) {
			errors.rejectValue(fieldPrepend+"supplierAccountId", null, null, ValidatorMessages.getString("APInvoiceValidator.55"));
			hasSupplierAccount = false;
		}

		//Inactive Supplier Account
		if (hasSupplierAccount) {
			SupplierAccount supplierAccount = invoiceService.getSupplierAccount(invoice.getSupplierAccountId());
			if (!supplierAccount.isActive()) {
				errors.rejectValue(fieldPrepend+"supplierAccountId", null, null, ValidatorMessages.getString("APInvoiceValidator.56"));
				hasSupplierAccount = false;
			}
			//Validate supplier in supplier account
			if (supplierAccount.getSupplierId() != invoice.getSupplierId()){
				errors.rejectValue(fieldPrepend+"supplierAccountId", null, null, ValidatorMessages.getString("APInvoiceValidator.57"));
			}
		}

		//GL Date
		if (invoice.getGlDate() == null) {
			errors.rejectValue(fieldPrepend+"glDate", null, null, ValidatorMessages.getString("APInvoiceValidator.13"));
		} else if (!timePeriodService.isOpenDate(invoice.getGlDate())) {
			errors.rejectValue(fieldPrepend+"glDate", null, null, ValidatorMessages.getString("APInvoiceValidator.14"));
		}

		//Validating reference document.
		documentService.validateReferences(invoice.getReferenceDocuments(), errors);

		//Validate form status
		FormWorkflow workflow = invoice.getId() != 0 ? invoiceService.getFormWorkflow(invoice.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
		}

		Double principalPayment = invoice.getPrincipalPayment();
		if (principalPayment != null && invoice.getReferenceObjectId() != null) {
			LoanProceeds lp = loanProceedsDao.getByEbObjectId(invoice.getReferenceObjectId());
			double totalLoansPaid = invoiceService.getTotalLoanPaid(invoice, lp);
			double balance =  NumberFormatUtil.subtractWFP(lp.getAmount(), totalLoansPaid);
			if (NumberFormatUtil.roundOffRuleOfFive(balance) < NumberFormatUtil.roundOffRuleOfFive(principalPayment)) {
				//Principal payment should not exceed the remaining loan amount.
				errors.rejectValue(fieldPrepend+"principalPayment", null, null, ValidatorMessages.getString("APInvoiceValidator.51"));
			}
		}

		double totalAmount = principalPayment != null ? principalPayment : 0;
		if(invoice.getaPlines() != null) {
			int row = 0;
			for(APLine apl : invoice.getaPlines()) {
				row++;
				if(apl.getGrossAmount() == null || apl.getGrossAmount() == 0) {
					//Gross amount is required in row %d.
					errors.rejectValue(fieldPrepend+"aPlineMessage", null, null, String.format(
							ValidatorMessages.getString("APInvoiceValidator.52"), row));
				}
				totalAmount += apl.getAmount();
			}
		}

		if(NumberFormatUtil.roundOffTo2DecPlaces(totalAmount) == 0) {
			//Total transaction amount should be greater than zero.
			errors.rejectValue(fieldPrepend+"apInvoiceErrMessage", null, null, ValidatorMessages.getString("APInvoiceValidator.50"));
		}
	}
}