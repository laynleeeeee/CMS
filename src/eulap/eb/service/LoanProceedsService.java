package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.LPLineDao;
import eulap.eb.dao.LoanProceedsDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.VatAcctSetupDao;
import eulap.eb.dao.WithholdingTaxAcctSettingDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.LPLine;
import eulap.eb.domain.hibernate.LoanProceeds;
import eulap.eb.domain.hibernate.LoanProceedsType;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.VatAcctSetup;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ApLineDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * A class that handles the business logic of Loan proceeds. 

 *
 */
@Service
public class LoanProceedsService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(LoanProceedsService.class);
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private LPLineDao lpLineDao;
	@Autowired
	private SupplierAccountDao supplierAcctDao;
	@Autowired
	private AccountCombinationDao combinationDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ReferenceDocumentDao referenceDocDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private WithholdingTaxAcctSettingDao wtAcctSettingDao;
	@Autowired
	private VatAcctSetupDao vatAcctSetupDao;
	@Autowired
	private LoanProceedsDao loanProceedsDao;
	@Autowired
	private ReceiptMethodService receiptMethodService;
	@Autowired
	private APInvoiceService apInvoiceService;

	//The default division ids.
	private static final int CENTRAL_DIVISION_ID = 1;
	private static final int NSB3_DIVISION_ID = 2;
	private static final int NSB4_DIVISION_ID = 3;
	private static final int NSB5_DIVISION_ID = 4;
	private static final int NSB8_DIVISION_ID = 5;
	private static final int NSB8A_DIVISION_ID = 6;

	private AccountCombination getAccountCombination(int acctCombinationId) {
		return combinationDao.get(acctCombinationId);
	}

	private AccountCombination getAccountCombination(int companyId, int divisionId, int accountId) {
		return combinationDao.getAccountCombination(companyId, divisionId, accountId);
	}

	private Account getAccount(int accountId) {
		return accountDao.get(accountId);
	}

	/**
	 * get the Loan Proceeds.
	 * @param laonProceedsId The unique id of loan proceeds. 
	 * @throws Exception 
	 */
	public LoanProceeds getLoanProceedsPdf (int laonProceedsId) throws Exception {
		LoanProceeds loanProceeds = getLoanProceeds(laonProceedsId);
		List<LPLine> lpLines = loanProceeds.getlPlines();
		List<ApLineDto> lines = new ArrayList<ApLineDto>();
		Integer taxTypeId = null;
		AccountCombination ac = null;
		Account acct = null;
		VatAcctSetup vatAcctSetup = null;
		int companyId = loanProceeds.getCompanyId();
		int divisionId = loanProceeds.getDivisionId();
		AccountCombination debitAc = null;
		if(loanProceeds.getReceiptMethod().getDebitAcctCombination() != null) {
			debitAc = loanProceeds.getReceiptMethod().getDebitAcctCombination();
		}

		if(loanProceeds.getReceiptMethod().getBankAccount() != null) {
			debitAc = loanProceeds.getReceiptMethod().getBankAccount().getCashInBank();
		}

		Double totalCharges = 0.00;
		if (lpLines != null && !lpLines.isEmpty()) {
			for (LPLine lpLine : lpLines) {
				Account account = lpLine.getAccountCombination().getAccount();
				lines.add(new ApLineDto(account.getNumber(), account.getAccountName(),
						(lpLine.getAmount() >= 0.0 ? lpLine.getAmount() : null),
						(lpLine.getAmount() < 0.0 ? -lpLine.getAmount() : null)));
				taxTypeId = lpLine.getTaxTypeId();
				double vatAmount = lpLine.getVatAmount() != null ? lpLine.getVatAmount() : 0.0;
				if (taxTypeId != null && vatAmount != 0.0) {
					vatAcctSetup = vatAcctSetupDao.getVatAccountSetup(companyId, divisionId, taxTypeId);
					ac = getAccountCombination(lpLine.getAccountCombinationId());
					if (vatAcctSetup != null) {
						ac = getAccountCombination(vatAcctSetup.getInputVatAcId());
						acct = getAccount(ac.getAccountId());
						lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(),
								(lpLine.getVatAmount() >= 0.0 ? lpLine.getVatAmount() : null),
								(lpLine.getVatAmount() < 0.0 ? -lpLine.getVatAmount() : null)));
						vatAcctSetup = null;
						acct = null;
					}
					ac = null;
				}
				taxTypeId = null;
				totalCharges += lpLine.getGrossAmount();
			}

		}
		// Withholding tax
		WithholdingTaxAcctSetting wtas = null;
		Integer wtaxAcctSettingId = loanProceeds.getWtAcctSettingId();
		double wtaxAmount = wtaxAcctSettingId != null ? loanProceeds.getWtAmount() : 0; 
		if (wtaxAcctSettingId != null) {
			wtas = wtAcctSettingDao.get(wtaxAcctSettingId);
			ac = getAccountCombination(wtas.getAcctCombinationId());
			acct = getAccount(ac.getAccountId());
			lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(), wtaxAmount, null));
			wtas = null;
			ac = null;
			acct = null;
		}

		//Cash
		Double lpAmount = loanProceeds.getAmount();
		if(debitAc != null) {
			acct = getAccount(debitAc.getAccountId());//The receipt method debit account
			Double totalCash = lpAmount - totalCharges - wtaxAmount;
			lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(), totalCash, null));
			//Clear memory.
			ac = null;
			acct = null;
		}

		// Accounts payable
		acct = getAccountCombination(companyId, divisionId, loanProceeds.getLoanAccountId()).getAccount();
		lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(), 
				lpAmount < 0.0 ? -lpAmount : null, 
						lpAmount >= 0.0 ? lpAmount : null));
		loanProceeds.setApLineDtos(lines);
		acct = null;
		return loanProceeds;
	}

	/**
	 * Save the Loan Proceeds and its Lines.
	 * @param user The logged user.
	 * @param loanProceeds The Loan Proceeds object.
	 * @throws CloneNotSupportedException 
	 */
	public void saveLoanProceeds(User user, LoanProceeds loanProceeds, String workflowName) throws CloneNotSupportedException {
		logger.info("Saving the AP Invoice.");
		boolean isNew = loanProceeds.getId() == 0;
		Date currDate = new Date();
		AuditUtil.addAudit(loanProceeds, new Audit (user.getId(), isNew, currDate));
		Integer parentObjectId = loanProceeds.getEbObjectId();
		// Saving Loan Proceeds and LP Lines
		double currencyRate = loanProceeds.getCurrencyRateValue() != null ? loanProceeds.getCurrencyRateValue() : 1;
		loanProceeds.setCurrencyRateValue(currencyRate);
		loanProceeds.setWtAmount(CurrencyUtil.convertAmountToPhpRate(loanProceeds.getWtAmount(), currencyRate, true));
		loanProceeds.setAmount(CurrencyUtil.convertAmountToPhpRate(loanProceeds.getAmount(), currencyRate, true));
		loanProceedsDao.saveOrUpdate(loanProceeds);
		logger.debug("Saved Loan Proceeds id: "+loanProceeds.getId());

		logger.debug("Saving LP Lines.");
		List<LPLine> apLines = loanProceeds.getlPlines();
		double totalLoanProceeds = loanProceeds.getAmount();
		if (apLines != null && !apLines.isEmpty()) {
			convertLpLineToPhp(loanProceeds.getlPlines(), currencyRate); //Lines
			List<Domain> toBeSavedLines = new ArrayList<Domain>();
			for (LPLine lpl : apLines) {
				lpl.setLoadnProceedsId(loanProceeds.getId());
				toBeSavedLines.add(lpl);
				totalLoanProceeds -= lpl.getVatAmount() != null ? lpl.getVatAmount() : 0;
				totalLoanProceeds -= lpl.getAmount();
			}
			lpLineDao.batchSave(toBeSavedLines);
		}
		totalLoanProceeds -= loanProceeds.getWtAmount();
		loanProceeds.setLoanProceedAmt(NumberFormatUtil.roundOffTo2DecPlaces(totalLoanProceeds));
		loanProceedsDao.update(loanProceeds);
		// Save attached document
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				loanProceeds.getReferenceDocuments(), true);
	}

	private void convertLpLineToPhp(List<LPLine> lpLines, double rate) {
		for (LPLine lpl : lpLines) {
			double vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(lpl.getVatAmount() != null ? lpl.getVatAmount() : 0);
			lpl.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, rate, true));
			double grossAmount = lpl.getGrossAmount();
			lpl.setGrossAmount(CurrencyUtil.convertAmountToPhpRate(grossAmount, rate));
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(grossAmount - vatAmount);
			lpl.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, rate, true));
		}
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		LoanProceeds loanProceeds = (LoanProceeds) form;
		boolean isNew = loanProceeds.getId() == 0;
		if (isNew) {
			loanProceeds.setSequenceNumber(loanProceedsDao.generateLpSequenceNo(loanProceeds.getCompanyId(),
					loanProceeds.getLoanProceedsTypeId()));
		} else {
			List<Integer> toBeDeleted = new ArrayList<Integer>();
			List<LPLine> dbLPLines = lpLineDao.getLPLines(loanProceeds.getId());
			if (dbLPLines != null && !dbLPLines.isEmpty()) {
				for (LPLine dBLPLine : dbLPLines) {
					toBeDeleted.add(dBLPLine.getId());
				}
				lpLineDao.delete(toBeDeleted);
			}
			logger.debug("Successfully deleted "+toBeDeleted.size()+" LP Lines.");
		}
	}


	/**
	 * Get the Supplier Account of the Invoice.
	 * @param supplierAccountId The Id of the supplier account.
	 */
	public SupplierAccount getSupplierAccount (int supplierAccountId) {
		return supplierAcctDao.get(supplierAccountId);
	}

	/**
	 * Get the account combination of the ap line.
	 * @param lpl The Loan proceeds line object.
	 * @return The account combination object.
	 */
	public AccountCombination getAccountCombination (LPLine lpl) {
		return combinationDao.getAccountCombination(lpl.getCompanyNumber(),
				lpl.getDivisionNumber(), lpl.getAccountNumber());
	}

	/**
	 * Get LP line combinations
	 * @param proceeds
	 */
	public void setLpLineCombination(LoanProceeds proceeds) {
		Collection<LPLine> lpLines = proceeds.getlPlines();
		for (LPLine lpl : lpLines) {
			if (lpl.getAccountNumber() != null || lpl.getCompanyNumber() != null || lpl.getDivisionNumber() != null) {
				AccountCombination ac = combinationDao.getAccountCombination(lpl.getCompanyNumber(),
						lpl.getDivisionNumber(), lpl.getAccountNumber());
				lpl.setAccountCombinationId(ac.getId());
			}
		}
	}

	/**
	 * Validate the account combination's Company, Division and Account if active.
	 * @param lpLine The LP Line object.
	 * @param lpLineField 1 = Company, 2 = Division, 3 = Account
	 * @return True if active, otherwise false.
	 */
	public boolean isActiveAcctCombi(LPLine lpLine, int lpLineField) {
		AccountCombination ac = getAccountCombination(lpLine);
		if(lpLineField == 1) {
			Company company = companyDao.get(ac.getCompanyId());
			if(company.isActive())
				return true;
		}
		if(lpLineField == 2) {
			Division division = divisionDao.get(ac.getDivisionId());
			if(division.isActive())
				return true;
		}
		if(lpLineField == 3) {
			Account account = accountDao.get(ac.getAccountId());
			if(account.isActive())
				return true;
		}
		return false;
	}

	/**
	 * Validate the company, division and account number if it is existing.
	 * @param lpLineField 1 = Company, 2 = Division, 3 = Account
	 * @return True if it is existing, otherwise false.
	 */
	public boolean isExistingAcctCombi(LPLine lpLine, int serviceLeaseKeyId, int lpLineField) {
		if(lpLineField == 1) {
			Company company = companyDao.getCompanyByNumber
					(lpLine.getCompanyNumber(), serviceLeaseKeyId);
			if(company != null)
				return true;
		}
		if(lpLineField == 2) {
			Division division = divisionDao.getDivisionByDivNumber
					(lpLine.getDivisionNumber(), serviceLeaseKeyId);
			if(division != null)
				return true;
		}
		if(lpLineField == 3) {
			Account account = accountDao.getAccountByNumber
					(lpLine.getAccountNumber(), serviceLeaseKeyId);
			if(account != null)
				return true;
		}
		return false;
	}

	/**
	 * Get the account combination using the supplier account.
	 * @param supplierAccountId The Id of the supplier account.
	 * @return The account combination.
	 */
	public AccountCombination getAccountCombinationId (int supplierAccountId) {
		return combinationDao.getAcctCombiBySupplierAcctId(supplierAccountId);
	}

	/**
	 * Validate the account combination if it is active or inactive.
	 * @param supplierAccountId The Id of the supplier account.
	 * @return True if active, otherwise false.
	 */
	public boolean isActiveAC(int supplierAccountId) {
		AccountCombination ac = getAccountCombinationId(supplierAccountId);
		if(ac.isActive())
			return true;
		return false;
	}

	/**
	 * Get the List of Line/s of the Loan Proceeds.
	 * @param loanProceeds The Loan proceeds.
	 * @return The List of LPLine object.
	 */
	public List<LPLine> getLPLine(LoanProceeds loanProceeds){
		List<LPLine> lp = loanProceeds.getlPlines();
		AccountCombination acctCombination = null;
		Company company = null;
		Division division = null;
		Account account = null;
		String companyName = "";
		String divisionName = "";
		String accountName = "";
		for (LPLine lpl : lp) {
			acctCombination = combinationDao.get(lpl.getAccountCombinationId());
			if (acctCombination != null) {
				company = companyDao.get(acctCombination.getCompanyId());
				if (company != null) {
					lpl.setCompanyId(company.getId());
					lpl.setCompanyNumber(company.getCompanyNumber());
					companyName = company.getName();
					lpl.setCompanyName(companyName);
				}
				division = divisionDao.get(acctCombination.getDivisionId());
				if (division != null) {
					lpl.setDivisionId(division.getId());
					lpl.setDivisionNumber(division.getNumber());
					divisionName = division.getName();
					lpl.setDivisionName(divisionName);
				}
				account = accountDao.get(acctCombination.getAccountId());
				if (account != null) {
					lpl.setAccountId(account.getId());
					lpl.setAccountNumber(account.getNumber());
					accountName = account.getAccountName();
					lpl.setAccountName(accountName);
				}
				lpl.setAcctCombinationName(companyName + " - " + divisionName + " - " + accountName);
			}
		}
		return lp;
	}

	/**
	 * Get the loan proceeds currency rate value.
	 * Set rate to 1 if currency rate is null.
	 * @param loanProceeds The {@link LoanProceeds}.
	 * @return The currency rate value.
	 */
	private double getRate(LoanProceeds loanProceeds) {
		return loanProceeds.getCurrencyRateValue() != null ? loanProceeds.getCurrencyRateValue() : 1;
	}

	private void convertLpLineMonetaryValues(List<LPLine> lpLines, double rate) {
		if(lpLines != null) {
			for(LPLine lpLine : lpLines) {
				lpLine.setGrossAmount(CurrencyUtil.convertMonetaryValues(lpLine.getGrossAmount(), rate));
				lpLine.setVatAmount(CurrencyUtil.convertMonetaryValues(lpLine.getVatAmount(), rate));
				lpLine.setAmount(CurrencyUtil.convertMonetaryValues(lpLine.getAmount(), rate));
			}
		}
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		LoanProceeds apInvoice = loanProceedsDao.get(id);
		if(apInvoice != null) {
			return apInvoice.getFormWorkflow();
		}
		logger.debug("No workflow for AP Invoice: "+id);
		return null;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID 
				&& currentWorkflowLog.getComment() != null && !currentWorkflowLog.getComment().isEmpty()) {
			LoanProceeds lp = loanProceedsDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
			if(lp != null) {
				StringBuffer errorMessage = null;
				List<APInvoice> apLoans = apInvoiceService.getApLoansByLoanProceed(lp);
				if(apLoans != null && !apLoans.isEmpty()) {
					errorMessage = new StringBuffer("Transaction cannot be cancelled because it has associated transaction/s: ");
					for(APInvoice apLoan : apLoans) {
						errorMessage.append("<br> APL No. : " + apLoan.getSequenceNumber());
					}
					if(errorMessage != null) {
						bindingResult.reject("workflowMessage", errorMessage.toString());
						currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
					}
				}
			}
		}
	}

	/**
	 * Set the workflow error message.
	 * @param bindingResult The {@link BindingResult} Object.
	 * @param currentWorkflowLog The Current {@link FormWorkflowLog}
	 * @param errorMessage The error message.
	 */
	protected void setErrorMsg(BindingResult bindingResult,
			FormWorkflowLog currentWorkflowLog, String errorMessage) {
		bindingResult.reject("workflowMessage", errorMessage);
		currentWorkflowLog.setWorkflowMessage(errorMessage);
	}

	/**
	 * Saving loan proceeds.
	 */
	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		LoanProceeds loanProceeds = (LoanProceeds) form;
		try {
			saveLoanProceeds(user, loanProceeds, workflowName);
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	

	/**
	 * Process the to be saved LP Lines
	 * @param toBeSavedLines The LP lines
	 * @return The processed list of to be saved LP lines
	 */
	public List<LPLine> processLPLines(List<LPLine> toBeSavedLines) {
		List<LPLine> processedLPLines = new ArrayList<LPLine>();
		boolean hasCompany = false;
		boolean hasDivision = false;
		boolean hasAccount = false;
		boolean hasAmount = false;
		boolean hasDescription = false;
		boolean hasTaxType = false;
		boolean hasGrossAmount = false;
		for (LPLine lpLine : toBeSavedLines) {
			hasCompany = lpLine.getCompanyNumber() != null && !lpLine.getCompanyNumber().trim().isEmpty();
			hasDivision = lpLine.getDivisionNumber() != null && !lpLine.getDivisionNumber().trim().isEmpty();
			hasAccount = lpLine.getAccountNumber() != null && !lpLine.getAccountNumber().trim().isEmpty();
			hasGrossAmount = lpLine.getGrossAmount() != null && lpLine.getGrossAmount() != 0.0;
			hasTaxType = lpLine.getTaxTypeId() != null;
			hasAmount = lpLine.getAmount() != 0.0;
			hasDescription = lpLine.getDescription() != null && !lpLine.getDescription().trim().isEmpty();
			if (hasCompany || hasDivision || hasAccount || hasAmount || hasDescription || hasTaxType || hasGrossAmount) {
				processedLPLines.add(lpLine);
			}
		}
		return processedLPLines;
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return loanProceedsDao.getByEbObjectId(ebObjectId);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return loanProceedsDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		String formName = LoanProceeds.class.getSimpleName();

		LoanProceeds apInvoice = loanProceedsDao.getByEbObjectId(ebObjectId);
		Integer pId = apInvoice.getId();
		FormProperty property = workflowHandler.getProperty(apInvoice.getWorkflowName(), user);

		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printoutLink = "/"+ property.getPrint() + "?pId=" + pId;

		String latestStatus = apInvoice.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName+" - " + apInvoice.getSequenceNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + apInvoice.getSupplier().getName())
				.append(" " + apInvoice.getSupplierAccount().getName())
				.append(" " + DateUtil.formatDate(apInvoice.getGlDate()));
		shortDescription.append(" " + apInvoice.getAmount());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printoutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case LPLine.LP_LINE_OBJECT_TYPE:
				return lpLineDao.getByEbObjectId(ebObjectId);
			case ReferenceDocument.OBJECT_TYPE_ID:
				return referenceDocDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get Loan proceeds by id.
	 * @param pId The loan proceeds id.
	 * @return {@link LoanProceeds}
	 * @throws IOException 
	 */
	public LoanProceeds getLoanProceeds(Integer pId) throws IOException {
		LoanProceeds loanProceeds = loanProceedsDao.get(pId);
		loanProceeds.setReceiptMethod(receiptMethodService.getReceiptMethod(loanProceeds.getReceiptMethodId()));
		Integer ebObjectId = loanProceeds.getEbObjectId();
		//Convert monetary values.
		double rate = getRate(loanProceeds);
		loanProceeds.setAmount(CurrencyUtil.convertMonetaryValues(loanProceeds.getAmount(), rate));
		loanProceeds.setWtAmount(CurrencyUtil.convertMonetaryValues(loanProceeds.getWtAmount(), rate));
		convertLpLineMonetaryValues(loanProceeds.getlPlines(), getRate(loanProceeds));
		if (ebObjectId != null) {
			logger.info("Retrieving the list of reference documents.");
			loanProceeds.setReferenceDocuments(refDocumentService.processReferenceDocs(ebObjectId));
		}
		return loanProceeds;
	}

	/**
	 * Get the division id based on the loan proceeds type id.
	 * The returned division id is based on the default divisions for NSB.
	 * @param typeId The loan proceeds type.
	 * @return The division id.
	 */
	public int getDivisionByLoanProceedsType(int typeId) {
		int divisionId;
		switch (typeId) {
		case LoanProceedsType.LPT_CENTRAL:
			divisionId = CENTRAL_DIVISION_ID;
			break;
		case LoanProceedsType.LPT_NSB3:
			divisionId = NSB3_DIVISION_ID;
			break;
		case LoanProceedsType.LPT_NSB4:
			divisionId = NSB4_DIVISION_ID;
			break;
		case LoanProceedsType.LPT_NSB5:
			divisionId = NSB5_DIVISION_ID;
			break;
		case LoanProceedsType.LPT_NSB8:
			divisionId = NSB8_DIVISION_ID;
			break;
		case LoanProceedsType.LPT_NSB8A:
			divisionId = NSB8A_DIVISION_ID;
			break;
		default:
			divisionId = 0;
			break;
		}
		return divisionId;
	}

	/**
	 * Search loan proceeds objects.
	 * @param typeId The loan proceed type id.
	 * @param criteria The loan proceeds sequence number search criteria.
	 * @return The list of loan proceeds.
	 */
	public List<FormSearchResult> searchLoanProceeds(Integer typeId, String searchCriteria) {
		Page<LoanProceeds> loanProceeds = loanProceedsDao.searchLoanProceeds(typeId, searchCriteria, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		Company company = null;
		Supplier supplier = null;
		SupplierAccount supplierAcct = null;
		Term term = null;
		for(LoanProceeds lp : loanProceeds.getData()) {
			company = lp.getCompany();
			supplier = lp.getSupplier();
			supplierAcct = lp.getSupplierAccount();
			term = lp.getTerm();
			title = String.valueOf(company.getCompanyCode() + " " + lp.getSequenceNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", company.getName()));
			properties.add(ResultProperty.getInstance("Supplier", supplier.getName()));
			properties.add(ResultProperty.getInstance("Supplier Account", supplierAcct.getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(lp.getDate())));
			properties.add(ResultProperty.getInstance("Term", term.getName()));
			status = lp.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(lp.getId(), title, properties));
		}
		title = null;
		status = null;
		company = null;
		supplier = null;
		supplierAcct = null;
		term = null;
		return result;
	}
}