package eulap.eb.service;

import java.io.IOException;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Calendar;
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
import eulap.eb.common.util.ListProcessorUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.APLineDao;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.ApInvoiceLineDao;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.ApPaymentInvoiceDao;
import eulap.eb.dao.ApPaymentLineDao;
import eulap.eb.dao.BirAtcDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.FormWorkflowDao;
import eulap.eb.dao.InvoiceImportationDetailsDao;
import eulap.eb.dao.InvoiceTypeDao;
import eulap.eb.dao.ItemSrpDao;
import eulap.eb.dao.LoanProceedsDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.RReceivingReportDao;
import eulap.eb.dao.RReceivingReportItemDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.SupplierDao;
import eulap.eb.dao.VatAcctSetupDao;
import eulap.eb.dao.WithholdingTaxAcctSettingDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.APLine;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApInvoiceGoods;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.ApPaymentInvoice;
import eulap.eb.domain.hibernate.ApPaymentLine;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.BirAtc;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceImportationDetails;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.LoanProceeds;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.VatAcctSetup;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.inventory.RItemCostUpdateService;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.OOServiceHandler;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ApLineDto;
import eulap.eb.web.dto.COCTaxDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.InvoicePrintoutDto;
import eulap.eb.web.dto.LoanProceedsDto;
import eulap.eb.web.dto.ResultProperty;

/**
 * A class that handles the business logic of accounts payable invoices. 

 *
 */
@Service
public class APInvoiceService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(APInvoiceService.class);
	@Autowired
	private RReceivingReportService receivingReportService;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private APLineDao apLineDao;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private SupplierAccountDao supplierAcctDao;
	@Autowired
	private AccountCombinationDao combinationDao;
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private ApPaymentInvoiceDao aPPaymentInvoiceDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private RReceivingReportItemDao rrItemDao;
	@Autowired
	private RReceivingReportDao rrDao;
	@Autowired
	private ItemSrpDao itemSrpDao;
	@Autowired
	private RItemCostUpdateService ucUpdateService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ReferenceDocumentDao referenceDocDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private ApInvoiceLineDao apInvoiceLineDao;
	@Autowired
	private WithholdingTaxAcctSettingDao wtAcctSettingDao;
	@Autowired
	private SerialItemService serialItemService;
	@Autowired
	private ApPaymentInvoiceDao apPaymentInvoiceDao;
	@Autowired
	private VatAcctSetupDao vatAcctSetupDao;
	@Autowired
	private InvoiceTypeDao invoiceTypeDao;
	@Autowired
	private InvoiceImportationDetailsDao iidDao;
	@Autowired
	private LoanProceedsDao loanProceedsDao;
	@Autowired
	private OOServiceHandler o2oServiceHandler;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private ObjectToObjectDao otoDao;
	@Autowired
	private ApPaymentDao apPaymentDao;
	@Autowired
	private FormWorkflowDao fwDao;
	@Autowired
	private ApPaymentLineDao apPaymentLineDao;
	@Autowired
	private BirAtcDao birAtcDao;

	/**
	 * get the AP invoice.
	 * @param invoiceId The unique id of invoice. 
	 */
	public APInvoice getInvoice (int invoiceId) {
		APInvoice apInvoice = apInvoiceDao.get(invoiceId);
		apInvoice.setWtAmount(CurrencyUtil.convertMonetaryValues(apInvoice.getWtAmount(), apInvoice.getCurrencyRateValue()));
		apInvoice.setAmount(CurrencyUtil.convertMonetaryValues(apInvoice.getAmount(), apInvoice.getCurrencyRateValue()));
		ValueRange importationRange = ValueRange.of(InvoiceType.API_IMPORT_CENTRAL, InvoiceType.API_IMPORT_NSB8A);
		if(importationRange.isValidValue(apInvoice.getInvoiceTypeId())) {
			apInvoice.setInvoiceImportationDetails(iidDao.getIIDByInvoiceId(apInvoice.getId()));
		}
		if(isLoanInvoiceType(apInvoice.getInvoiceTypeId())) {
			processApLoanData(apInvoice);
		}
		return apInvoice;
	}

	private void processApLoanData(APInvoice apInvoice) {
		double rate = apInvoice.getCurrencyRateValue() != null ? apInvoice.getCurrencyRateValue() : 1;
		LoanProceeds lp = getLoanProceedsByLoanObjId(apInvoice);
		apInvoice.setReferenceObjectId(lp.getEbObjectId());
		apInvoice.setPrincipalLoan(CurrencyUtil.convertMonetaryValues(apInvoice.getPrincipalLoan(), rate));
		apInvoice.setPrincipalPayment(CurrencyUtil.convertMonetaryValues(apInvoice.getPrincipalPayment(), rate));
		apInvoice.setLpNumber(lp.getSequenceNumber());
	}

	//Will not delete this code yet, will remove this after the client's confirmation.
	//This implementation is for ideal situations, ap loans without charges and full payment.
	//Ex.
	//Loan proceeds (LP-1) total loan = 1,000,000.00
	//AP Loan Transaction:
	//Principal Payment = 100,000.00
	//Charges = 0.00
	//The first AP loan transaction has been fully paid in the AP Payment form.
	//Upon retrieving the LP-1 for second loan payment, the total principal loan should be 900,000.00
	//Note: Loan transactions must be paid in the AP Payment, for it to be recorded. 
	private double computeRemainingPrincipalLoan(APInvoice apInvoice, LoanProceeds lp) {
		double balance = lp.getAmount();
		List<APInvoice> apLoans = getApLoansByLoanProceed(lp);//The ap loans related to the parent loan proceed transactions.
		List<ApPaymentLine> paymentLines = getRefApPaymentLines(apLoans, apInvoice.getEbObjectId());//Retrieve all the payments made for all the ap loans transaction.
		for(ApPaymentLine paymentLine : paymentLines) {
			balance -= paymentLine.getPaidAmount();//Subtract all payments.
		}
		return balance;
	}

	/**
	 * Get all reference ap payment lines.
	 * Exclude payment line if it the child transaction of the exclude invoice object id parameter.
	 * @param apLoans The list of ap loans.
	 * @param excludeInvoiceObjectId The to be excluded invoice transaction.
	 * @return The list of not cancelled payment lines.
	 */
	private List<ApPaymentLine> getRefApPaymentLines(List<APInvoice> apLoans, Integer excludeInvoiceObjectId) {
		List<ApPaymentLine> paymentLines = new ArrayList<ApPaymentLine>();
		ApPaymentLine paymentLine = null;
		for(APInvoice apLoan : apLoans) {
			List<ObjectToObject> otos = otoDao.getReferenceObjects(apLoan.getEbObjectId(), null, ApPaymentLine.OBJECT_TYPE);
			for(ObjectToObject oto : otos) {
				paymentLine = apPaymentLineDao.getByEbObjectId(oto.getToObjectId());
				ApPayment payment = apPaymentDao.get(paymentLine.getApPaymentId());
				if(fwDao.get(payment.getFormWorkflowId()).getCurrentStatusId() != FormStatus.CANCELLED_ID) {
					if(excludeInvoiceObjectId != null && oto.getFromObjectId() == excludeInvoiceObjectId) {
						break;//Exclude the ap loan transaction that is being edited.
					}
					paymentLines.add(paymentLine);
				}
				payment = null;
			}
		}
		return paymentLines;
	}

	/**
	 * Compute the total invoice amount per supplier account for all AP Invoice status except cancelled.
	 * @param companyId The id of the company.
	 * @param supplierAcctId The id of the supplier account.
	 * @param asOfDate End date of the date range.
	 * @return The total invoice amount.
	 */
	public double getTotalInvoices(int companyId, int supplierAcctId, Date asOfDate) {
		return NumberFormatUtil.roundOffTo2DecPlaces(apInvoiceDao.getTotalInvoiceAmount(companyId, supplierAcctId, asOfDate, false));
	}

	/**
	 * Get the total amount of RTS.
	 * @param companyId The company id.
	 * @param supplierAcctId The supplier account id.
	 * @param asOfDate The gl date.
	 * @return Get the total amount of rts type of invoices
	 */
	public double getTotalOfInvoices(int companyId, int supplierAcctId, Date asOfDate) {
		return NumberFormatUtil.roundOffTo2DecPlaces(apInvoiceDao.getTotalAmountOfInvoice(companyId, supplierAcctId, asOfDate, true));
	}

	private AccountCombination getAccountCombination(int acctCombinationId) {
		return combinationDao.get(acctCombinationId);
	}

	private Account getAccount(int accountId) {
		return accountDao.get(accountId);
	}

	/**
	 * Get the AP invoice printout data
	 * @param pId The AP invoice object id
	 * @return The AP invoice printout data
	 * @throws Exception 
	 */
	public List<InvoicePrintoutDto> getInvoicePrintoutDtos(Integer pId, Integer invoiceTypeId) throws Exception {
		List<InvoicePrintoutDto> invoicePrintoutDtos = new ArrayList<InvoicePrintoutDto>();
		InvoicePrintoutDto invoicePrintoutDto = new InvoicePrintoutDto();
		APInvoice apInvoice = null;
		if(isLoanInvoiceType(invoiceTypeId)) {
			apInvoice = getLoanPdf(pId);
		} else {
			apInvoice = getInvoicePdf(pId);
		}
		ValueRange importationRange = ValueRange.of(InvoiceType.API_IMPORT_CENTRAL, InvoiceType.API_IMPORT_NSB8A);
		if(importationRange.isValidValue(apInvoice.getInvoiceTypeId())) {
			invoicePrintoutDto.setApLineDtos(removeNullAmountForImportation(apInvoice.getApLineDtos()));
		} else {
			invoicePrintoutDto.setApLineDtos(apInvoice.getApLineDtos());
		}
		invoicePrintoutDto.setCocTaxDtos(getCocTaxDtos(apInvoice));
		invoicePrintoutDtos.add(invoicePrintoutDto);
		return invoicePrintoutDtos;
	}

	private List<ApLineDto> removeNullAmountForImportation(List<ApLineDto> apLineDtos){
		List<ApLineDto> filteredLines = new ArrayList<ApLineDto>();
		for(ApLineDto apLineDto: apLineDtos) {
			if(!(apLineDto.getCredit() == null && apLineDto.getDebit() == 0.0)){
				filteredLines.add(apLineDto);
			}
		}
		return filteredLines;
	}

	/**
	 * Get the certificate of creditable tax data
	 * @param apInvoice The AP invoice object
	 * @return The certificate of creditable tax data
	 */
	public List<COCTaxDto> getCocTaxDtos(APInvoice apInvoice) {
		List<COCTaxDto> cocTaxDtos = new ArrayList<COCTaxDto>();
		Integer wtAcctSettingId = apInvoice.getWtAcctSettingId();
		Integer invoiceClassificationType = apInvoice.getInvoiceClassificationId();
		Integer invoiceTypeId = apInvoice.getInvoiceTypeId();
		ValueRange apiGsTypeRange = ValueRange.of(InvoiceType.API_GS_CENTRAL, InvoiceType.API_GS_NSB8A);
		if (wtAcctSettingId != null) {
			double totalAmount = 0;
			if (apiGsTypeRange.isValidIntValue(invoiceTypeId)) {//API-G/S
				List<SerialItem> serialItems = apInvoice.getSerialItems();
				List<ApInvoiceGoods> apigs = apInvoice.getApInvoiceGoods();
				List<ApInvoiceLine> apils = apInvoice.getApInvoiceLines();
				double vatAmt = 0;
				double discountAmt = 0;
				double unitCost = 0;
				if(serialItems != null) {
					for(SerialItem si : serialItems) {
						vatAmt = si.getVatAmount() != null ? si.getVatAmount() : 0.00;
						discountAmt = si.getDiscount() != null ? si.getDiscount() : 0.00;
						unitCost = si.getUnitCost() != null ? si.getUnitCost() : 0;
						totalAmount += NumberFormatUtil.multiplyWFP(si.getQuantity(), unitCost) - vatAmt - discountAmt;
					}
				}
				if(apigs != null) {
					for(ApInvoiceGoods item : apigs) {
						vatAmt = item.getVatAmount() != null ? item.getVatAmount() : 0.00;
						discountAmt = item.getDiscount() != null ? item.getDiscount() : 0.00;
						unitCost = item.getUnitCost() != null ? item.getUnitCost() : 0;
						totalAmount += NumberFormatUtil.multiplyWFP(item.getQuantity(), unitCost) - vatAmt - discountAmt;
					}
				}
				if(apils != null) {
					for(ApInvoiceLine apil : apils) {
						totalAmount += apil.getAmount();
					}
				}
			} else if(isLoanInvoiceType(invoiceTypeId) 
					|| invoiceClassificationType.equals(InvoiceType.REGULAR_TYPE_ID) 
					|| invoiceClassificationType.equals(InvoiceType.DEBIT_MEMO_TYPE_ID)
					|| invoiceClassificationType.equals(InvoiceType.CREDIT_MEMO_TYPE_ID)) {
				for (APLine apl : getAPLine(apInvoice)) {
					totalAmount += apl.getAmount(); // net of VAT
				}
			} else {
				return cocTaxDtos;
			}
			COCTaxDto cocTaxDto = new COCTaxDto();
			Date glDate = DateUtil.removeTimeFromDate(apInvoice.getGlDate());
			cocTaxDto.setStartPeriod(DateUtil.getFirstDayOfMonth(glDate));
			cocTaxDto.setEndPeriod(DateUtil.getEndDayOfMonth(glDate));
			WithholdingTaxAcctSetting wtas = wtAcctSettingDao.get(wtAcctSettingId);
			BirAtc birAtc = birAtcDao.get(wtas.getBirAtcId());
			cocTaxDto.setWtDesc(birAtc.getDescription());
			cocTaxDto.setAtc(wtas.getName());
			totalAmount = NumberFormatUtil.roundOffTo2DecPlaces(totalAmount);
			if (isBelongToMonth(glDate, 1) || isBelongToMonth(glDate, 4)
					|| isBelongToMonth(glDate, 7) || isBelongToMonth(glDate, 10)) {
				cocTaxDto.setFirstQuarter(totalAmount);
			} else if (isBelongToMonth(glDate, 2) || isBelongToMonth(glDate, 5)
					|| isBelongToMonth(glDate, 8) || isBelongToMonth(glDate, 11)) {
				cocTaxDto.setSecondQuarter(totalAmount);
			} else {
				cocTaxDto.setThirdQuarter(totalAmount);
			}
			cocTaxDto.setTaxWithheld(NumberFormatUtil.roundOffTo2DecPlaces(apInvoice.getWtAmount()));
			cocTaxDtos.add(cocTaxDto);
		}
		return cocTaxDtos;
	}

	private boolean isBelongToMonth(Date date, Integer month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Integer calMonth = DateUtil.getMonth(cal.getTime()) + 1;
		return calMonth.equals(month);
	}

	/**
	 * get the AP invoice.
	 * @param invoiceId The unique id of invoice. 
	 * @throws Exception 
	 */
	public APInvoice getInvoicePdf (int invoiceId) throws Exception {
		APInvoice apInvoice = getInvoice(invoiceId);
		List<APLine> apLines = getAPLine(apInvoice);
		List<ApLineDto> lines = new ArrayList<ApLineDto>();
		Integer taxTypeId = null;
		AccountCombination ac = null;
		Account acct = null;
		VatAcctSetup vatAcctSetup = null;
		int companyId = apInvoice.getCompanyId();
		int divisionId = apInvoice.getDivisionId();
		if (apLines != null && !apLines.isEmpty()) {
			for (APLine apLine : apLines) {
				Account account = apLine.getAccountCombination().getAccount();
				lines.add(new ApLineDto(account.getNumber(), account.getAccountName(),
						(apLine.getAmount() >= 0.0 ? apLine.getAmount() : null),
						(apLine.getAmount() < 0.0 ? -apLine.getAmount() : null)));
				taxTypeId = apLine.getTaxTypeId();
				double vatAmount = apLine.getVatAmount() != null ? apLine.getVatAmount() : 0.0;
				if (taxTypeId != null && vatAmount != 0.0) {
					vatAcctSetup = vatAcctSetupDao.getVatAccountSetup(companyId, divisionId, taxTypeId);
					ac = getAccountCombination(apLine.getAccountCombinationId());
					if (vatAcctSetup != null) {
						ac = getAccountCombination(vatAcctSetup.getInputVatAcId());
						acct = getAccount(ac.getAccountId());
						lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(),
								(apLine.getVatAmount() >= 0.0 ? apLine.getVatAmount() : null),
								(apLine.getVatAmount() < 0.0 ? -apLine.getVatAmount() : null)));
						vatAcctSetup = null;
						acct = null;
					}
					ac = null;
				}
				taxTypeId = null;
			}

			// Withholding tax
			WithholdingTaxAcctSetting wtas = null;
			double wtaxAmount = 0;
			Integer wtaxAcctSettingId = apInvoice.getWtAcctSettingId();
			if (wtaxAcctSettingId != null) {
				wtaxAmount = apInvoice.getWtAmount();
				wtas = wtAcctSettingDao.get(wtaxAcctSettingId);
				ac = getAccountCombination(wtas.getAcctCombinationId());
				acct = getAccount(ac.getAccountId());
				lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(),
						(wtaxAmount < 0.0 ? -wtaxAmount : null), (wtaxAmount >= 0.0 ? wtaxAmount : null)));
				wtas = null;
				ac = null;
				acct = null;
			}

			// Accounts payable
			acct = getAccountCombination(apInvoice.getSupplierAccount().getDefaultCreditACId()).getAccount();
			lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(), 
					apInvoice.getAmount() < 0.0 ? -apInvoice.getAmount() : null, 
					apInvoice.getAmount() >= 0.0 ? apInvoice.getAmount() : null));
			apInvoice.setApLineDtos(lines);
			acct = null;
		}
		return apInvoice;
	}

	public APInvoice getLoanPdf (int invoiceId) throws Exception {
		APInvoice apInvoice = getInvoice(invoiceId);
		List<APLine> apLines = getAPLine(apInvoice);
		List<ApLineDto> lines = new ArrayList<ApLineDto>();
		Integer taxTypeId = null;
		AccountCombination ac = null;
		Account acct = null;
		VatAcctSetup vatAcctSetup = null;
		int companyId = apInvoice.getCompanyId();
		int divisionId = apInvoice.getDivisionId();
		//Loans Payable
		double principalPayment = apInvoice.getPrincipalPayment();
		if(principalPayment > 0) {
			acct = apInvoice.getLoanAccount();
			lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(), principalPayment, null));
			apInvoice.setApLineDtos(lines);
			acct = null;
		}
		if (apLines != null && !apLines.isEmpty()) {
			//Loan Charges
			for (APLine apLine : apLines) {
				Account account = apLine.getAccountCombination().getAccount();
				lines.add(new ApLineDto(account.getNumber(), account.getAccountName(),
						(apLine.getAmount() >= 0.0 ? apLine.getAmount() : null),
						(apLine.getAmount() < 0.0 ? -apLine.getAmount() : null)));
				taxTypeId = apLine.getTaxTypeId();
				double vatAmount = apLine.getVatAmount() != null ? apLine.getVatAmount() : 0.0;
				if (taxTypeId != null && vatAmount != 0.0) {
					vatAcctSetup = vatAcctSetupDao.getVatAccountSetup(companyId, divisionId, taxTypeId);
					ac = getAccountCombination(apLine.getAccountCombinationId());
					if (vatAcctSetup != null) {
						ac = getAccountCombination(vatAcctSetup.getInputVatAcId());
						acct = getAccount(ac.getAccountId());
						lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(),
								(apLine.getVatAmount() >= 0.0 ? apLine.getVatAmount() : null),
								(apLine.getVatAmount() < 0.0 ? -apLine.getVatAmount() : null)));
						vatAcctSetup = null;
						acct = null;
					}
					ac = null;
				}
				taxTypeId = null;
			}
		}

		// Withholding tax
		WithholdingTaxAcctSetting wtas = null;
		double wtaxAmount = 0;
		Integer wtaxAcctSettingId = apInvoice.getWtAcctSettingId();
		if (wtaxAcctSettingId != null) {
			wtaxAmount = apInvoice.getWtAmount();
			wtas = wtAcctSettingDao.get(wtaxAcctSettingId);
			ac = getAccountCombination(wtas.getAcctCombinationId());
			acct = getAccount(ac.getAccountId());
			lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(),
					(wtaxAmount < 0.0 ? -wtaxAmount : null), (wtaxAmount >= 0.0 ? wtaxAmount : null)));
			wtas = null;
			ac = null;
			acct = null;
		}

		//Supplier default credit account.
		//Accounts payable
		acct = getAccountCombination(apInvoice.getSupplierAccount().getDefaultCreditACId()).getAccount();
		lines.add(new ApLineDto(acct.getNumber(), acct.getAccountName(), null, apInvoice.getAmount()));
		apInvoice.setApLineDtos(lines);
		return apInvoice;
	}

	/**
	 * Evaluate if the invoice number is unique per supplier.
	 * @param invoice The AP Invoice object.
	 * @return True if unique, otherwise false.
	 */
	public boolean isUniqueInvoiceNumber(APInvoice invoice) {
		if(invoice.getId() != 0) {
			APInvoice existingInvoice = apInvoiceDao.get(invoice.getId());
			if(invoice.getInvoiceNumber().trim().equalsIgnoreCase(existingInvoice.getInvoiceNumber().trim()))
				return true;
		}
		else if (invoice.getSupplierAccountId() == null || invoice.getSupplierAccountId() == 0){
			return true;
		}
		return apInvoiceDao.isUniqueInvoiceNo(invoice.getInvoiceNumber(), invoice.getSupplierAccountId());
	}

	/**
	 * Evaluate if the invoice number is unique per division.
	 * @param invoice The AP Invoice object.
	 * @return True if unique, otherwise false.
	 */
	public boolean isUniqueReferenceNo(APInvoice invoice) {
		if(invoice.getId() != 0) {
			APInvoice existingInvoice = apInvoiceDao.get(invoice.getId());
			if(invoice.getInvoiceNumber().trim().equalsIgnoreCase(existingInvoice.getInvoiceNumber().trim()))
				return true;
		}
		return apInvoiceDao.isUniqueReferenceNo(invoice.getInvoiceNumber(), invoice.getDivisionId(), invoice.getInvoiceTypeId());
	}

	/**
	 * Get AP Invoices by supplier account.
	 * @param supplierAccountId The Id of the supplier account.
	 * @return Collection of AP invoices.
	 */
	public Collection<APInvoice> getInvoices (int supplierAccountId) {
		return apInvoiceDao.getInvoicesBySupplierAcct(supplierAccountId);
	}

	/**
	 * Save the AP Invoice
	 */
	public void saveInvoice(User user, APInvoice apInvoice) throws CloneNotSupportedException {
		String workflowName = APInvoice.class.getSimpleName();
		saveInvoice(user, apInvoice, workflowName);
	}

	/**
	 * Save the AP Invoice and its AP Lines.
	 * @param user The logged user.
	 * @param invoice The AP invoice object.
	 * @throws CloneNotSupportedException 
	 */
	public void saveInvoice(User user, APInvoice invoice, String workflowName) throws CloneNotSupportedException {
		logger.info("Saving the AP Invoice.");
		int invoiceTypeId = invoice.getInvoiceTypeId();
		boolean isNew = invoice.getId() == 0;
		Date currDate = new Date();
		AuditUtil.addAudit(invoice, new Audit (user.getId(), isNew, currDate));
		invoice.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		Integer parentObjectId = invoice.getEbObjectId();
		boolean isValidRate = invoice.getCurrencyRateValue() != null && invoice.getCurrencyRateValue() != 0;
		double currencyRate = isValidRate ? invoice.getCurrencyRateValue() : 1.0;

		FormWorkflow workflow = getFormWorkflow(invoice.getId());
		boolean isComplete = false;
		if (workflow != null) {
			logger.debug("Workflow is completed.");
			isComplete = workflow.isComplete();
		}

		if (!isNew) {
			List<APLine> savedApLines = apLineDao.getAllByRefId(APLine.FIELD.aPInvoiceId.name(), invoice.getId());
			if (savedApLines != null && !savedApLines.isEmpty()) {
				for (APLine apLine : savedApLines) {
					apLineDao.delete(apLine);
				}
			}
		}

		if (!isComplete) {
			// Saving AP Invoice and AP Lines
			invoice.setCurrencyRateValue(currencyRate);
			invoice.setWtAmount(CurrencyUtil.convertAmountToPhpRate(invoice.getWtAmount(), currencyRate, true));
			boolean isLoanInvoice = isLoanInvoiceType(invoiceTypeId);
			double principalPayment = 0;
			if (isLoanInvoice) {
				invoice.setPrincipalPayment(CurrencyUtil.convertAmountToPhpRate(invoice.getPrincipalPayment(), currencyRate, true));
				principalPayment = CurrencyUtil.convertAmountToPhpRate(invoice.getPrincipalLoan(), currencyRate, true);
				invoice.setPrincipalLoan(principalPayment);
				invoice.setAmount(CurrencyUtil.convertAmountToPhpRate(invoice.getAmount(), currencyRate, true));
			}
			apInvoiceDao.saveOrUpdate(invoice);
			logger.debug("Saved AP Invoice id: "+invoice.getId());

			logger.debug("Saving AP Lines.");
			boolean isImportation = ValueRange.of(InvoiceType.API_IMPORT_CENTRAL, InvoiceType.API_IMPORT_NSB8A).isValidValue(invoiceTypeId);
			double totalAmount = 0;
			List<APLine> apLines = invoice.getaPlines();
			if (apLines != null && !apLines.isEmpty()) {
				List<Domain> toBeSavedLines = new ArrayList<Domain>();
				for (APLine apl : convertAmountToPHP(apLines, currencyRate, isImportation)) {
					apl.setaPInvoiceId(invoice.getId());
					toBeSavedLines.add(apl);
					if (!isImportation) {
						totalAmount += apl.getVatAmount() != null ? apl.getVatAmount() : 0;
					}
					totalAmount += apl.getAmount();
				}
				apLineDao.batchSave(toBeSavedLines);
			}

			invoice.setAmount(NumberFormatUtil.roundOffTo2DecPlaces((totalAmount + principalPayment) - invoice.getWtAmount()));
			apInvoiceDao.update(invoice);

			//Save object to object relationship for loan proceeds and ap loan.
			if (isNew && isLoanInvoiceType(invoiceTypeId)) {
				o2oServiceHandler.saveObjectToObjectRelationship(invoice.getReferenceObjectId(), 
						parentObjectId, APInvoice.LP_AP_LOAN_OR_TYPE_ID, user);
			}

			//Delete the saved referenced documents.
			List<ReferenceDocument> toBeDeleteRefDocs = refDocumentService.getReferenceDocuments(invoice.getEbObjectId());
			for (ReferenceDocument referenceDocument : toBeDeleteRefDocs) {
				referenceDocDao.delete(referenceDocument);
			}

			InvoiceImportationDetails invImportDetail = invoice.getInvoiceImportationDetails();
			ValueRange iidRange = ValueRange.of(InvoiceType.API_IMPORT_CENTRAL, InvoiceType.API_IMPORT_NSB8A);
			if (iidRange.isValidValue(invoiceTypeId)) {
				invImportDetail.setApInvoiceId(invoice.getId());
				iidDao.saveOrUpdate(invImportDetail);
			}

			refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
					invoice.getReferenceDocuments(), true);
		}
	}

	/**
	 * Currency conversion of ap line
	 * @param apLines The ap line
	 * @param currencyRate The currency rate
	 * @return Converted ap lines
	 */
	public List<APLine> convertAmountToPHP(List<APLine> apLines, double currencyRate, boolean isImportation) {
		for (APLine apl : apLines) {
			double grossAmount = apl.getGrossAmount() != null ? apl.getGrossAmount() : 0;
			double vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(apl.getVatAmount() != null ? apl.getVatAmount() : 0);
			apl.setGrossAmount(CurrencyUtil.convertAmountToPhpRate(grossAmount, currencyRate));
			apl.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
			double amount = isImportation ? NumberFormatUtil.roundOffTo2DecPlaces(vatAmount)
					: NumberFormatUtil.roundOffTo2DecPlaces(grossAmount - vatAmount);
			apl.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		}
		return apLines;
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice invoice = (APInvoice) form;
		// Set sequence number only when invoice form is new.
		if(invoice.getId() == 0) {
			Integer companyId = supplierAcctDao.get(invoice.getSupplierAccountId()).getCompanyId();
			Integer sequenceNo = apInvoiceDao.generateSequenceNumber(invoice.getInvoiceTypeId(), companyId);
			invoice.setSequenceNumber(sequenceNo);
		}
		//Compute invoice amount for ap loan transactions.
		//Set invoice date and due date with the save value as gl date.
		if(isLoanInvoiceType(invoice.getInvoiceTypeId())) {
			Date glDate = invoice.getGlDate();
			invoice.setAmount(computeLoanInvoiceAmount(invoice));
			invoice.setInvoiceDate(glDate);
			invoice.setDueDate(glDate);
		}

		List<Integer> toBeDeleted = new ArrayList<Integer>();
		// User data
		// Saved data from database.
		List<APLine> dbAPLines = apLineDao.getAPLines(invoice.getId());
		if (dbAPLines != null && !dbAPLines.isEmpty()) {
			for (APLine dBAPLine : dbAPLines) {
				toBeDeleted.add(dBAPLine.getId());
			}
			apLineDao.delete(toBeDeleted);
		}
		logger.debug("Successfully deleted "+toBeDeleted.size()+" AP Lines.");
	}

	private double computeLoanInvoiceAmount(APInvoice apInvoice) {
		double principalPayment = apInvoice.getPrincipalPayment();
		double wtAmount = apInvoice.getWtAmount();
		if(apInvoice.getaPlines() != null) {
			for(APLine apl : apInvoice.getaPlines()) {
				principalPayment += apl.getGrossAmount();//Add ap line amount with vat.
			}
		}
		return principalPayment - wtAmount;
	}

	/**
	 * Get and process the receiving report items for viewing
	 * @param invoiceId The RR AP invoice id
	 * @param isIncludeRRItems True if include item lines, otherwise false
	 * @return
	 * @throws IOException 
	 */
	public APInvoice processReceivingReportAndItems(Integer invoiceId, boolean isIncludeRRItems) throws IOException {
		APInvoice invoice = apInvoiceDao.get(invoiceId);
		RReceivingReport receivingReport = rrDao.getRrByInvoiceId(invoiceId);
		double currencyRateValue = invoice.getCurrencyRateValue() > 0 ? invoice.getCurrencyRateValue() : 1;
		// Set serialized item
		boolean isCancelled = invoice.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID;
		List<SerialItem> serialItems = serialItemService.getSerializeItemsByRefObjectId(invoice.getCompanyId(),
				invoice.getDivisionId(), invoice.getEbObjectId(), receivingReport.getWarehouseId(),
				RReceivingReport.RECEIVING_REPORT_TO_SERIAL_ITEM, isCancelled);
		for(SerialItem serialItem : serialItems) {
			double unitCost = serialItem.getUnitCost() != null ? CurrencyUtil.convertMonetaryValues(serialItem.getUnitCost(), currencyRateValue) : 0;
			double quantity = serialItem.getQuantity();
			serialItem.setUnitCost(unitCost);
			serialItem.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(unitCost, quantity)));
		}
		receivingReport.setSerialItems(serialItems);
		invoice.setReceivingReport(receivingReport);

		// Set non-serialized item
		Double srpAmount = null;
		Double existingStocks = null;
		double unitCost = 0;
		double quantity = 0;
		double rowAmount = 0;
		if (isIncludeRRItems) {
			List<RReceivingReportItem> rrItems = rrItemDao.getRrItems(invoiceId);
			for(RReceivingReportItem rrItem: rrItems) {
				srpAmount = getItemSrp(receivingReport.getCompanyId(), rrItem.getItemId(), receivingReport.getDivisionId());
				rrItem.setSrp(srpAmount);
				rrItem.setStockCode(rrItem.getItem().getStockCode());
				existingStocks = itemService.getItemExistingStocks(rrItem.getItemId(),
						receivingReport.getWarehouseId());
				rrItem.setExistingStocks(existingStocks);
				unitCost = rrItem.getUnitCost() != null ? CurrencyUtil.convertMonetaryValues(rrItem.getUnitCost(), currencyRateValue) : 0;
				quantity = rrItem.getQuantity();
				rrItem.setUnitCost(unitCost);
				rowAmount = NumberFormatUtil.multiplyWFP(unitCost, quantity);
				rrItem.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(rowAmount));
			}
			invoice.setRrItems(rrItems);

			List<ApInvoiceLine> apInvoiceLines = receivingReportService.getApInvoiceLines(invoiceId);
			if(apInvoiceLines != null) {
				for(ApInvoiceLine apl : apInvoiceLines) {
					String apLineName = apl.getApLineSetup().getName();
					if (apLineName != null) {
						apl.getApLineSetup().setName(StringFormatUtil.removeExtraWhiteSpaces(apLineName));
					}
					if(apl.getUnitMeasurement() != null) {
						apl.setUnitMeasurementName(apl.getUnitMeasurement().getName());
					}
					apl.setAmount(CurrencyUtil.convertMonetaryValues(apl.getAmount(), currencyRateValue));
				}
			}
			invoice.setApInvoiceLines(apInvoiceLines);
		}
		invoice.setReferenceDocuments(refDocumentService.processReferenceDocs(invoice.getEbObjectId()));
		return invoice;
	}

	/**
	 * Process the receiving report and rr items of ap invoice
	 * @param invoice The ap invoice object.
	 * @return The ap invoice object.
	 * @throws IOException 
	 */
	public APInvoice processReceivingReportAndItems(Integer invoiceId) throws IOException {
		return processReceivingReportAndItems(invoiceId, true);
	}

	/**
	 * Get the item srp.
	 * @param companyId The company id.
	 * @param itemId The item id.
	 * @return The item srp.
	 */
	public double getItemSrp(Integer companyId, Integer itemId, Integer divisionId) {
		ItemSrp itemSrp = itemSrpDao.getLatestItemSrp(companyId, itemId, divisionId);
		return NumberFormatUtil.roundOffTo2DecPlaces(itemSrp == null ? 0 : itemSrp.getSrp());
	}

	/**
	 * Get the Supplier Account of the Invoice.
	 * @param supplierAccountId The Id of the supplier account.
	 */
	public SupplierAccount getSupplierAccount (int supplierAccountId) {
		return supplierAcctDao.get(supplierAccountId);
	}

	/**
	 * Get the supplier of the invoice.
	 * @param supplierId The id of the supplier.
	 */
	public Supplier getSupplier (int supplierId) {
		return supplierDao.get(supplierId);
	}

	/**
	 * Get the account combination of the ap line.
	 * @param ap The AP line object.
	 * @return The account combination object.
	 */
	public AccountCombination getAccountCombination (APLine ap) {
		return combinationDao.getAccountCombination(ap.getCompanyNumber(),
				ap.getDivisionNumber(), ap.getAccountNumber());
	}

	/**
	 * Validate the account combination's Company, Division and Account if active.
	 * @param apLine The AP Line object.
	 * @param apLineField 1 = Company, 2 = Division, 3 = Account
	 * @return True if active, otherwise false.
	 */
	public boolean isActiveAcctCombi(APLine apLine, int apLineField) {
		AccountCombination ac = getAccountCombination(apLine);
		if(apLineField == 1) {
			Company company = companyDao.get(ac.getCompanyId());
			if(company.isActive())
				return true;
		}
		if(apLineField == 2) {
			Division division = divisionDao.get(ac.getDivisionId());
			if(division.isActive())
				return true;
		}
		if(apLineField == 3) {
			Account account = accountDao.get(ac.getAccountId());
			if(account.isActive())
				return true;
		}
		return false;
	}

	/**
	 * Validate the company, division and account number if it is existing.
	 * @param apLineField 1 = Company, 2 = Division, 3 = Account
	 * @return True if it is existing, otherwise false.
	 */
	public boolean isExistingAcctCombi(APLine apLine, int serviceLeaseKeyId, int apLineField) {
		if(apLineField == 1) {
			Company company = companyDao.getCompanyByNumber
					(apLine.getCompanyNumber(), serviceLeaseKeyId);
			if(company != null)
				return true;
		}
		if(apLineField == 2) {
			Division division = divisionDao.getDivisionByDivNumber
					(apLine.getDivisionNumber(), serviceLeaseKeyId);
			if(division != null)
				return true;
		}
		if(apLineField == 3) {
			Account account = accountDao.getAccountByNumber
					(apLine.getAccountNumber(), serviceLeaseKeyId);
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
	 * Get the List of AP Line/s of the AP Invoice.
	 * @param aPInvoice The AP Invoice/s.
	 * @return The List of APLine object.
	 */
	public List<APLine> getAPLine(APInvoice aPInvoice){
		List<APLine> ap = apLineDao.getAPLines(aPInvoice.getId());
		if (ap == null) {
			return null;
		}
		AccountCombination acctCombination = null;
		Company company = null;
		Division division = null;
		Account account = null;
		String companyName = "";
		String divisionName = "";
		String accountName = "";
		Double rate = aPInvoice.getCurrencyRateValue();
		for (APLine apl : ap) {
			acctCombination = combinationDao.get(apl.getAccountCombinationId());
			if (acctCombination != null) {
				company = companyDao.get(acctCombination.getCompanyId());
				if (company != null) {
					apl.setCompanyId(company.getId());
					apl.setCompanyNumber(company.getCompanyNumber());
					companyName = company.getName();
					apl.setCompanyName(companyName);
				}
				division = divisionDao.get(acctCombination.getDivisionId());
				if (division != null) {
					apl.setDivisionId(division.getId());
					apl.setDivisionNumber(division.getNumber());
					divisionName = division.getName();
					apl.setDivisionName(divisionName);
				}
				account = accountDao.get(acctCombination.getAccountId());
				if (account != null) {
					apl.setAccountId(account.getId());
					apl.setAccountNumber(account.getNumber());
					accountName = account.getAccountName();
					apl.setAccountName(accountName);
				}
				apl.setAcctCombinationName(companyName + " - " + divisionName + " - " + accountName);
			}
			apl.setGrossAmount(CurrencyUtil.convertMonetaryValues(apl.getGrossAmount(), rate));
			apl.setVatAmount(CurrencyUtil.convertMonetaryValues(apl.getVatAmount(), rate));
			apl.setAmount(CurrencyUtil.convertMonetaryValues(apl.getAmount(), rate));
		}
		return ap;
	}

	/**
	 * Get AP Payment Invoice of the selected AP Invoice
	 * @param invoiceId The id of AP Invoice
	 * @return the AP Payment Invoice object
	 */
	public ApPaymentInvoice getApPaymentInvoice (int invoiceId){
		return aPPaymentInvoiceDao.getApPaymentCheckBookNumber(invoiceId);
	}

	/**
	 * Get all AP Payment Invoice of the selected AP Invoice
	 * @param invoiceId The id of AP Invoice
	 * @return the AP Payment Invoice object
	 */
	public Collection<ApPaymentInvoice> getPaidInvoices (int invoiceId){
		return aPPaymentInvoiceDao.getPaidInvoices(invoiceId);
	}

	/**
	 * Get Check numbers that is associated with the selected AP Invoice
	 * @param invoiceId The id of AP Invoice object
	 * @return checkNumberList The String containing all associated check no. of the invoice 
	 */
	public String getApPaymentCheckNumbers (int invoiceId){
		String checkNumberList = ": ";
		ApPaymentInvoice apCollection = aPPaymentInvoiceDao.getApPaymentCheckBookNumber(invoiceId);
		checkNumberList += apCollection.getApPayment().getCheckNumber();

		return checkNumberList;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		APInvoice apInvoice = apInvoiceDao.get(id);
		if(apInvoice != null) {
			return apInvoice.getFormWorkflow();
		}
		logger.debug("No workflow for AP Invoice: "+id);
		return null;
	}

	/**
	 * Get the AP Invoice object.
	 * @param refId The reference id.
	 * @param isRrItem set to true if reference is from RR Item, otherwise RTS Item.
	 * @return The AP Invoice object, otherwise null.
	 */
	public APInvoice getInvoiceByRefId(Integer refId, boolean isRrItem) {
		return apInvoiceDao.getInvoiceByRefId(refId, isRrItem);
	}

	/**
	 * Validate cancellation of AP Invoice related form with AP Payment.
	 * @param apInvoice The {@link APInvoice} object.
	 * @param formName The name of the form.
	 * @param currentWorkflowLog The current {@link FormWorkflowLog} object.
	 * @param bindingResult The {@link BindingResult} object.
	 */
	protected void validatedInvoiceWithPayment(APInvoice apInvoice, String formName,
			FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		int apInvoiceId = apInvoice.getId();
		if (apInvoiceDao.hasAssociatedPayment(apInvoiceId)) {
			String message = formName;
			message += " with invoice number " + apInvoice.getInvoiceNumber() + " cannot " +
					"be cancelled because it has associated payment/s: ";
			List<ApPaymentInvoice> pInvoices =
					aPPaymentInvoiceDao.getPaidInvoices(apInvoiceId, null);
			if (pInvoices != null) {
				String strInvoices = "";
				for (ApPaymentInvoice pI : pInvoices)
					strInvoices += "<br> Check# " + pI.getApPayment().getCheckNumber();
				message += strInvoices;
			}
			setErrorMsg(bindingResult, currentWorkflowLog, message);
		}
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		APInvoice apInvoice = apInvoiceDao.getAPInvoiceByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (apInvoice != null) {
			validatedInvoiceWithPayment(apInvoice, "AP Invoice", currentWorkflowLog, bindingResult);

			// Checks if GL date is open time period.
			Date glDate = apInvoice.getGlDate();
			String errorMessage = "";
			if (!timePeriodService.isOpenDate(glDate)) {
				errorMessage = "GL date should be in an open time period.";
				setErrorMsg(bindingResult, currentWorkflowLog, errorMessage);
			}

			FormWorkflow fw = fwDao.get(currentWorkflowLog.getFormWorkflowId());
			if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID && fw.isComplete()) {
				List<ApPayment> payments = apPaymentDao.getApPaymentsByObjectId(apInvoice.getEbObjectId());
				if (payments != null && !payments.isEmpty()) {
					errorMessage = "Transaction cannot be cancelled because it has associated payment/s: ";
					for(ApPayment payment : payments) {
						errorMessage += "<br> Check# " + payment.getCheckNumber();
					}
					setErrorMsg(bindingResult, currentWorkflowLog, errorMessage);
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
	 * Update unit cost after RR.
	 * @param apInvoiceId The id of the {@link APInvoice}
	 * @param formDate The date of the form.
	 */
	protected void updateTransactionsAfterRr(int apInvoiceId, Date formDate) {
		RReceivingReport rr = rrDao.getRrByInvoiceId(apInvoiceId);
		List<RReceivingReportItem> rrItems = rrItemDao.getRrItems(apInvoiceId);
		ListProcessorUtil<RReceivingReportItem> processor = new ListProcessorUtil<RReceivingReportItem>();
		List<Integer> itemIds = processor.collectDisctinctItemIds(rrItems);
		for (Integer itemId : itemIds) {
			logger.debug("Updating the unit cost of item: "+itemId+" of warehouse: "+rr.getWarehouseId());
			ucUpdateService.updateItemUnitCost(itemId, rr.getWarehouseId(), formDate);
		}

		//Set the memory allocation to null
		processor = null;
		rrItems = null;
	}

	/**
	 * Saving AP invoice.
	 */
	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice invoice = (APInvoice) form;
		try {
			saveInvoice(user, invoice, workflowName);
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Process the to be saved AP Lines
	 * @param toBeSavedLines The AP lines
	 * @return The processed list of to be saved AP lines
	 */
	public List<APLine> processAPLines(List<APLine> toBeSavedLines) {
		List<APLine> processedAPLines = new ArrayList<APLine>();
		boolean hasCompany = false;
		boolean hasDivision = false;
		boolean hasAccount = false;
		boolean hasAmount = false;
		boolean hasDescription = false;
		boolean hasTaxType = false;
		boolean hasGrossAmount = false;
		boolean hasLoanChargeType = false;
		for (APLine apLine : toBeSavedLines) {
			hasCompany = apLine.getCompanyNumber() != null && !apLine.getCompanyNumber().trim().isEmpty();
			hasDivision = apLine.getDivisionNumber() != null && !apLine.getDivisionNumber().trim().isEmpty();
			hasAccount = apLine.getAccountNumber() != null && !apLine.getAccountNumber().trim().isEmpty();
			hasGrossAmount = apLine.getGrossAmount() != null && apLine.getGrossAmount() != 0.0;
			hasTaxType = apLine.getTaxTypeId() != null;
			hasLoanChargeType = apLine.getLoanChargeTypeId() != null;
			hasAmount = apLine.getAmount() != 0.0;
			hasDescription = apLine.getDescription() != null && !apLine.getDescription().trim().isEmpty();
			if (hasCompany || hasDivision || hasAccount || hasAmount || hasDescription
					|| hasTaxType || hasGrossAmount || hasLoanChargeType) {
				processedAPLines.add(apLine);
			}
		}
		return processedAPLines;
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return apInvoiceDao.getByEbObjectId(ebObjectId);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return apInvoiceDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		String formName = "";

		APInvoice apInvoice = apInvoiceDao.getByEbObjectId(ebObjectId);
		Integer pId = apInvoice.getId();
		FormProperty property = workflowHandler.getProperty(apInvoice.getWorkflowName(), user);
		Integer typeId = apInvoice.getInvoiceTypeId();
		if(typeId != null) {
		switch (typeId) {
			case InvoiceType.RR_TYPE_ID:
				formName = "Receiving Report";
				break;
			case InvoiceType.RR_RAW_MAT_TYPE_ID:
				formName = "Receiving Report - Raw Materials";
				break;
			case InvoiceType.RTS_TYPE_ID:
				formName = "Return to Supplier";
				break;
			case InvoiceType.API_NON_PO_CENTRAL:
				formName = "AP Invoice - Non Po - Central";
				break;
			case InvoiceType.API_NON_PO_NSB3:
				formName = "AP Invoice - Non Po - NSB 3";
				break;
			case InvoiceType.API_NON_PO_NSB4:
				formName = "AP Invoice - Non Po - NSB 4";
				break;
			case InvoiceType.API_NON_PO_NSB5:
				formName = "AP Invoice - Non Po - NSB 5";
				break;
			case InvoiceType.API_NON_PO_NSB8:
				formName = "AP Invoice - Non Po - NSB 8";
				break;
			case InvoiceType.API_NON_PO_NSB8A:
				formName = "AP Invoice - Non Po - NSB 8A";
				break;
			case InvoiceType.API_CONF_CENTRAL:
				formName = "AP Invoice - Confidential - Central";
				break;
			case InvoiceType.API_CONF_NSB3:
				formName = "AP Invoice - Confidential - NSB 3";
				break;
			case InvoiceType.API_CONF_NSB4:
				formName = "AP Invoice - Confidential - NSB 4";
				break;
			case InvoiceType.API_CONF_NSB5:
				formName = "AP Invoice - Confidential - NSB 5";
				break;
			case InvoiceType.API_CONF_NSB8:
				formName = "AP Invoice - Confidential - NSB 8";
				break;
			case InvoiceType.API_CONF_NSB8A:
				formName = "AP Invoice - Confidential - NSB 8A";
				break;
			case InvoiceType.API_IMPORT_CENTRAL:
				formName = "AP Invoice - Importation - Central";
				break;
			case InvoiceType.API_IMPORT_NSB3:
				formName = "AP Invoice - Importation - NSB 3";
				break;
			case InvoiceType.API_IMPORT_NSB4:
				formName = "AP Invoice - Importation - NSB 4";
				break;
			case InvoiceType.API_IMPORT_NSB5:
				formName = "AP Invoice - Importation - NSB 5";
				break;
			case InvoiceType.API_IMPORT_NSB8:
				formName = "AP Invoice - Importation - NSB 8";
				break;
			case InvoiceType.API_IMPORT_NSB8A:
				formName = "AP Invoice - Importation - NSB 8A";
				break;
			case InvoiceType.AP_LOAN_CENTRAL:
				formName = "AP Loan - Central";
				break;
			case InvoiceType.AP_LOAN_NSB3:
				formName = "AP Loan - NSB 3";
				break;
			case InvoiceType.AP_LOAN_NSB4:
				formName = "AP Loan - NSB 4";
				break;
			case InvoiceType.AP_LOAN_NSB5:
				formName = "AP Loan - NSB 5";
				break;
			case InvoiceType.AP_LOAN_NSB8:
				formName = "AP Loan - NSB 8";
				break;
			case InvoiceType.AP_LOAN_NSB8A:
				formName = "AP Loan - NSB 8A";
				break;
			default:
				formName = "AP Invoice";
				break;
			}
		}

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

	/**
	 * Get the Invoice Title Name
	 * @param invoiceTypeId the invoice type id
	 * @return The Invoice title name. 
	 */
	public String getInvoiceHeaderName (int invoiceTypeId) {
		InvoiceType it = invoiceTypeDao.get(invoiceTypeId);
		return it.getName(); 
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case APInvoice.AP_INVOICE_OBJECT_TYPE_ID:
				return apInvoiceDao.getByEbObjectId(ebObjectId);
			case APLine.AP_LINE_OBJECT_TYPE:
				return apLineDao.getByEbObjectId(ebObjectId);
			case ReferenceDocument.OBJECT_TYPE_ID:
				return referenceDocDao.getByEbObjectId(ebObjectId);
			case ApInvoiceLine.OBJECT_TYPE_ID:
				return apInvoiceLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the list of ap invoices for general search
	 * @param user The user currently logged in
	 * @param searchCriteria The search criteria
	 * @return The list of ap invoices
	 */
	public List<FormSearchResult> search(User user, String searchCriteria, int divisionId) {
		Page<APInvoice> invoices = 
				apInvoiceDao.searchAPInvoice(searchCriteria, new PageSetting(1), divisionId);
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (APInvoice inv : invoices.getData()) {
			String title = (inv.getInvoiceNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String status = inv.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Company", inv.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Supplier", inv.getSupplier().getName()));
			properties.add(ResultProperty.getInstance("Supplier Account", inv.getSupplierAccount().getName()));
			properties.add(ResultProperty.getInstance("Invoice Date", DateUtil.formatDate(inv.getInvoiceDate())));
			properties.add(ResultProperty.getInstance("GL Date", DateUtil.formatDate(inv.getGlDate())));
			properties.add(ResultProperty.getInstance("Due Date", DateUtil.formatDate(inv.getDueDate())));
			properties.add(ResultProperty.getInstance("Amount", NumberFormatUtil.format(inv.getAmount())));
			String strBalance = NumberFormatUtil.format(getBalance(inv));
			properties.add(ResultProperty.getInstance("Balance",strBalance));
			properties.add(ResultProperty.getInstance("Status", status));
			result.add(FormSearchResult.getInstanceOf(inv.getId(), title, properties));
		}
		return result;
	}

	private double getBalance(APInvoice ap){
		Collection<ApPaymentInvoice> paidInvoices = apPaymentInvoiceDao.getPaidInvoices(ap.getId());
		double balance = 0;
		if(!paidInvoices.isEmpty()) {
			double totalPaidAmount = 0;
			for(ApPaymentInvoice pi : paidInvoices)
				totalPaidAmount += pi.getPaidAmount();
				balance += ap.getAmount() - totalPaidAmount;
				return balance;
		}else{
			balance += ap.getAmount();
		}
		return balance;
	}

	/**
	 * Check if the {@code InvoiceImportationDetails} is empty
	 * @param iid The {@code InvoiceImportationDetails} object
	 * @return True if the object is empty, otherwise false
	 */
	public boolean isInvoiceImportationDetailsEmpty(InvoiceImportationDetails iid) {
		if (iid != null) {
			boolean hasEntryNo = iid.getImportEntryNo() != null && !iid.getImportEntryNo().trim().isEmpty();
			boolean hasReleaseDate = iid.getAssessmentReleaseDate() != null;
			boolean hasRegisteredName = iid.getRegisteredName() != null && !iid.getRegisteredName().trim().isEmpty();
			boolean hasImportationDate = iid.getImportationDate() != null;
			boolean hasCountryOfOrigin = iid.getCountryOfOrigin() != null && !iid.getCountryOfOrigin().trim().isEmpty();
			boolean hasLandedCost = iid.getTotalLandedCost() != 0;
			boolean hasDutiableValue = iid.getDutiableValue() != 0;
			boolean hasChargesFromCustom = iid.getChargesFromCustom() != 0;
			boolean hasTaxable = iid.getTaxableImport() != 0;
			boolean hasExempt = iid.getExemptImport() != 0;
			boolean hasOrNumber = iid.getOrNumber() != null && !iid.getOrNumber().trim().isEmpty();
			boolean hasPaymentDate = iid.getPaymentDate() != null;
			if (hasEntryNo || hasReleaseDate || hasRegisteredName || hasImportationDate
					|| hasCountryOfOrigin || hasLandedCost || hasDutiableValue || hasChargesFromCustom
					|| hasTaxable || hasExempt || hasOrNumber || hasPaymentDate) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the completed loan proceeds as reference transaction for AP Loan.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param supplierid The supplier id.
	 * @param supplierAcctId The supplier account id.
	 * @param dateFrom The start date filter.
	 * @param dateTo The end date filter.
	 * @param statusId The status id.
	 * @param pageNumber The page number.
	 * @return The list of completed {@link LoanProceeds} in page format.
	 */
	public Page<LoanProceedsDto> getReferenceLoanProceeds(Integer companyId, Integer divisionId, Integer supplierid,
			Integer supplierAcctId, Date dateFrom, Date dateTo, Integer statusId, Integer lpNumber, Integer pageNumber) {
		return loanProceedsDao.getReferenceLoanProceeds(companyId, divisionId, supplierid, supplierAcctId, dateFrom, dateTo,
				statusId, lpNumber, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Convert loan proceeds to ap loan.
	 * @param lpId The loan proceeds id.
	 * @return The converted ap loan.
	 */
	public APInvoice convertLpToApLoan(Integer lpId) {
		LoanProceeds lp = loanProceedsDao.get(lpId);
		APInvoice apLoan = new APInvoice();
		if(lp != null) {
			double rate = lp.getCurrencyRateValue() != null ? lp.getCurrencyRateValue() : 1;
			apLoan.setReferenceObjectId(lp.getEbObjectId());
			apLoan.setLpNumber(lp.getSequenceNumber());
			apLoan.setCompanyId(lp.getCompanyId());
			apLoan.setCompany(lp.getCompany());
			apLoan.setDivisionId(lp.getDivisionId());
			apLoan.setDivision(lp.getDivision());
			apLoan.setSupplierId(lp.getSupplierId());
			apLoan.setSupplier(lp.getSupplier());
			apLoan.setSupplierAccountId(lp.getSupplierAccountId());
			apLoan.setSupplierAccount(lp.getSupplierAccount());
			apLoan.setCurrencyId(lp.getCurrencyId());
			apLoan.setCurrency(lp.getCurrency());
			apLoan.setCurrencyRateValue(rate);
			apLoan.setLoanAccountId(lp.getLoanAccountId());
			apLoan.setLoanAccount(lp.getLoanAccount());
			double principalLoan = lp.getAmount() - getTotalLoanPaid(apLoan, lp);
			apLoan.setPrincipalLoan(CurrencyUtil.convertMonetaryValues(principalLoan, rate));
		}
		return apLoan;
	}

	/**
	 * Check if the invoice type id is a loan invoice type.
	 * @param typeId The invoice type id.
	 * @return True if it is a loan invoice type, otherwise false.
	 */
	public boolean isLoanInvoiceType(int typeId) {
		ValueRange apLoanRange = ValueRange.of(InvoiceType.AP_LOAN_CENTRAL, InvoiceType.AP_LOAN_NSB8A);
		if(apLoanRange.isValidValue(typeId)) {
			return true;
		}
		return false;
	}

	/**
	 * Get the {@link LoanProceeds} parent object of the {@link APInvoice}.
	 * @param apInvoice The child {@link APInvoice}.
	 * @return The parent {@link LoanProceeds}.
	 */
	public LoanProceeds getLoanProceedsByLoanObjId(APInvoice apInvoice) {
		EBObject refObj = ooLinkHelper.getReferenceObject(apInvoice.getEbObjectId(), APInvoice.LP_AP_LOAN_OR_TYPE_ID);
		return loanProceedsDao.getByEbObjectId(refObj.getId());
	}

	/**
	 * Get the list of child ap loans by the parent loan proceeds.
	 * @param lp The {@link Loan}
	 * @return
	 */
	public List<APInvoice> getApLoansByLoanProceed(LoanProceeds lp) {
		List<ObjectToObject> otos = otoDao.getReferenceObjects(lp.getEbObjectId(), null, APInvoice.LP_AP_LOAN_OR_TYPE_ID);
		List<APInvoice> apLoans = new ArrayList<APInvoice>();
		APInvoice apLoan = null;
		for(ObjectToObject oto : otos) {
			apLoan = apInvoiceDao.getByEbObjectId(oto.getToObjectId());
			if(fwDao.get(apLoan.getFormWorkflowId()).getCurrentStatusId() != FormStatus.CANCELLED_ID) {
				apLoans.add(apLoan);
			}
		}
		return apLoans;
	}

	/**
	 * Get the total paid principal loan.
	 * Note: This computation of total principal loan is only based on all created ap loan transactions.
	 * 		It does not have any checking if that transaction is paid or not in the ap payment form.
	 * @param apInvoice The ap loan transaction.
	 * @return The total principal loan based on the create ap loans.
	 */
	public double getTotalLoanPaid(APInvoice apInvoice, LoanProceeds lp) {
		double totalLoansPaid = 0;
		List<APInvoice> apLoans = getApLoansByLoanProceed(lp);
		for(APInvoice api : apLoans) {
			totalLoansPaid += api.getPrincipalPayment();
		}
		return totalLoansPaid;
	}
}