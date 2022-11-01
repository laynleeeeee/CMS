package eulap.eb.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.ApPaymentInvoiceDao;
import eulap.eb.dao.ApPaymentLineDao;
import eulap.eb.dao.BankAccountDao;
import eulap.eb.dao.CheckbookDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.FormWorkflowDao;
import eulap.eb.dao.RReceivingReportDao;
import eulap.eb.dao.RReceivingReportItemDao;
import eulap.eb.dao.RReturnToSupplierItemDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.SupplierAdvPaymentAccountDao;
import eulap.eb.dao.SupplierAdvPaymentDao;
import eulap.eb.dao.SupplierDao;
import eulap.eb.dao.TimePeriodDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.ApPaymentInvoice;
import eulap.eb.domain.hibernate.ApPaymentLine;
import eulap.eb.domain.hibernate.ApPaymentLineType;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Checkbook;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.SupplierAdvPaymentAccount;
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ApInvoiceDto;
import eulap.eb.web.dto.ApPaymentLineDto;

/**
 * Business logic that will handle all the requests for AP payments.

 */
@Service
public class ApPaymentService extends BaseWorkflowService{
private static Logger logger = Logger.getLogger(ApPaymentService.class);
	@Autowired
	private ApPaymentDao paymentDao;
	@Autowired
	private APInvoiceDao invoiceDao;
	@Autowired
	private CheckbookDao checkbookDao;
	@Autowired
	private TimePeriodDao timePeriodDao;
	@Autowired
	private SupplierAccountDao supplierAccountDao;
	@Autowired
	private ApPaymentInvoiceDao apPaymentInvoiceDao;
	@Autowired
	private BankAccountDao bankAccountDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private AccountCombinationDao combinationDao;
	@Autowired
	private RReceivingReportDao receivingReportDao;
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private RReceivingReportItemDao rReceivingReportItemDao;
	@Autowired
	private RReturnToSupplierItemDao returnToSupplierItemDao;
	@Autowired
	private ApPaymentLineDao apPaymentLineDao;
	@Autowired
	private SupplierAdvPaymentDao sapDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private SupplierAdvPaymentAccountDao sapAccountDao;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private FormWorkflowDao formWorkflowDao;

	/**
	 * Get the AP payment object by its Id.
	 * @param paymentId The unique Id of the payment.
	 * @return The AP payment object.
	 */
	public ApPayment getApPayment (int paymentId) {
		ApPayment apPayment = paymentDao.get(paymentId);
		getApPayment(apPayment);
		return apPayment;
	}

	/**
	 * 
	 * @param apPayment
	 * @throws IOException
	 */
	public void processPaymentRefDoc(ApPayment apPayment) throws IOException {
		apPayment.setReferenceDocuments(refDocumentService.processReferenceDocs(apPayment.getEbObjectId()));
	}

	/**
	 * Get the AP payment object.
	 * @param payment The the payment.
	 * @return The AP payment object.
	 */
	public void getApPayment (ApPayment apPayment) {
		apPayment.setApPaymentInvoices(apPaymentInvoiceDao.getAllByRefId("apPaymentId", apPayment.getId()));
		apPayment.setSupplier(supplierDao.get(apPayment.getSupplierId()));
		apPayment.setSupplierAccount(supplierAccountDao.get(apPayment.getSupplierAccountId()));
		double rate = apPayment.getCurrencyRateValue() > 0.0 ? apPayment.getCurrencyRateValue() : 1.0;
		apPayment.setAmount(NumberFormatUtil.roundOffNumber(
				(CurrencyUtil.convertMonetaryValues(apPayment.getAmount(), rate)), 6));
		if (apPayment.getBankAccountId() != null) {
			apPayment.setBankAccount(bankAccountDao.get(apPayment.getBankAccountId()));
		}
		if (apPayment.getCheckbookId() != null) {
			apPayment.setCheckbook(checkbookDao.get(apPayment.getCheckbookId()));
		}
		getApPaymentLineDto(apPayment);
	}

	private void getApPaymentLineDto(ApPayment apPayment) {
		List<ApPaymentLine> paymentLines = apPaymentLineDao.getAllByRefId(ApPaymentLine.FIELD.apPaymentId.name(), apPayment.getId());
		List<ApPaymentLineDto> dtos = new ArrayList<ApPaymentLineDto>();
		ApPaymentLineDto dto = null;
		for(ApPaymentLine apl : paymentLines) {
			//TODO: Add reference object id.
			EBObject referenceObject = ooLinkHelper.getReferenceObject(apl.getEbObjectId(),
					ApPaymentLine.OBJECT_TYPE);
			double rate = apl.getCurrencyRateValue() > 0.0 ? apl.getCurrencyRateValue() : 1.0;
			dto = new ApPaymentLineDto();
			Double amount = CurrencyUtil.convertMonetaryValues(apl.getPaidAmount(), rate);
			dto.setRefenceObjectId(referenceObject.getId());
			dto.setAmount(NumberFormatUtil.roundOffNumber(amount, 6));
			dto.setReferenceNumber(getReferenceNumber(apl));
			dto.setApPaymentLineTypeId(apl.getApPaymentLineTypeId());
			dtos.add(dto);
		}
		apPayment.setApPaymentLineDtos(dtos);
	}

	private String getReferenceNumber(ApPaymentLine apl) {
		String referenceNumber = "";
		if (apl.getApPaymentLineTypeId() == ApPaymentLineType.INVOICE) {
			APInvoice invoice = invoiceDao.getByChildEbObject(apl.getEbObjectId());
			int invTypeId = invoice.getInvoiceTypeId();
			int sequenceNo = invoice.getSequenceNumber();
			if (invTypeId >= InvoiceType.API_NON_PO_CENTRAL && invTypeId <= InvoiceType.API_NON_PO_NSB8A) {
				referenceNumber = "API NPO - " + sequenceNo;
			} else if (invTypeId >= InvoiceType.API_GS_CENTRAL && invTypeId <= InvoiceType.API_GS_NSB8A) {
				referenceNumber = "API G/S - " + sequenceNo;
			} else if (invTypeId == InvoiceType.RTS_TYPE_ID || (invTypeId >= InvoiceType.RTS_CENTRAL_TYPE_ID
					&& invTypeId <= InvoiceType.RTS_NSB8A_TYPE_ID)) {
				referenceNumber = "RTS - " + sequenceNo;
			} else if (invTypeId >= InvoiceType.API_CONF_CENTRAL && invTypeId <= InvoiceType.API_CONF_NSB8A) {
				referenceNumber = "API C - " + sequenceNo;
			} else if (invTypeId >= InvoiceType.API_IMPORT_CENTRAL && invTypeId <= InvoiceType.API_IMPORT_NSB8A) {
				referenceNumber = "API I - " + sequenceNo;
			} else if (invTypeId >= InvoiceType.AP_LOAN_CENTRAL && invTypeId <= InvoiceType.AP_LOAN_NSB8A) {
				referenceNumber = "API L - " + sequenceNo;
			} else if (invTypeId >= InvoiceType.PCR_CENTRAL && invTypeId <= InvoiceType.PCR_NSB8A) {
				referenceNumber = "PCR - " + sequenceNo;
			} else {
				referenceNumber = "API - " + sequenceNo;
			}
		} else {
			referenceNumber = "SAP - " + sapDao.getByChildEbObject(apl.getEbObjectId()).getSequenceNumber();
		}
		return referenceNumber;
	}

	/**
	 * Get the AP payment object for printout generation
	 * @param paymentId The AP payment id
	 * @return THe AP payment object
	 */
	public ApPayment getApPaymentPrintoutData (int paymentId) {
		ApPayment payment = getApPayment(paymentId);
		if (payment != null) {
			List<ApPaymentLine> paymentLines = apPaymentLineDao.getAllByRefId(ApPaymentLine.FIELD.apPaymentId.name(), payment.getId());
			List<ApPaymentLineDto> dtos = new ArrayList<ApPaymentLineDto>();
			ApPaymentLineDto dto = null;
			Account defaultCreditAcct = payment.getSupplierAccount().getDefaultCreditAC().getAccount();
			SupplierAdvPaymentAccount sapAccount = sapAccountDao.getSapAcctByCompanyDiv(payment.getCompanyId(), payment.getDivisionId());
			AccountCombination ac = combinationDao.get(sapAccount.getAccountCombinationId());
			String sapAccName = ac.getAccount().getAccountName();
			String sapAccNo = ac.getAccount().getNumber();
			String particular = null;
			String accountName = null;
			String accountNo = null;
			Double debit = null;
			Double credit = null;
			for (ApPaymentLine apl : paymentLines) {
				dto = new ApPaymentLineDto();
				dto.setReferenceNumber(getReferenceNumber(apl));
				boolean isInvoice = apl.getApPaymentLineTypeId() == ApPaymentLineType.INVOICE;
				boolean isSap = apl.getApPaymentLineTypeId() == ApPaymentLineType.SUPLIER_ADVANCE_PAYMENT;
				double rate = apl.getCurrencyRateValue() > 0.0 ? apl.getCurrencyRateValue() : 1.0;
				double paidAmount = apl.getPaidAmount();
				if (isInvoice) {
					particular = invoiceDao.getByChildEbObject(apl.getEbObjectId()).getDescription();
					accountName = defaultCreditAcct.getAccountName();
					accountNo = defaultCreditAcct.getNumber();
					debit = paidAmount > 0 ? CurrencyUtil.convertMonetaryValues(paidAmount, rate) : null;
					credit = paidAmount < 0 ? CurrencyUtil.convertMonetaryValues(Math.abs(paidAmount), rate) : null;
				} else if(isSap) {
					particular = sapDao.getByChildEbObject(apl.getEbObjectId()).getRemarks();
					if(paidAmount > 0) {//For sap transactions with positive payments.
						accountName = defaultCreditAcct.getAccountName();
						accountNo = defaultCreditAcct.getNumber();
						debit = CurrencyUtil.convertMonetaryValues(Math.abs(paidAmount), rate);
					} else {//For sap transactions with negative payments.
						accountName = sapAccName;
						accountNo = sapAccNo;
						credit = CurrencyUtil.convertMonetaryValues(Math.abs(paidAmount), rate);
					}
				}
				dto.setAccountName(accountName);
				dto.setAccountNumber(accountNo);
				dto.setParticular(particular);
				dto.setDebit(debit);
				dto.setCredit(credit);
				dtos.add(dto);

				// Free up memory
				dto = null;
				particular = null;
				accountName = null;
				accountNo = null;
				debit = null;
				credit = null;
			}
			AccountCombination accountCombination = combinationDao.get(payment.getBankAccount().getCashInBankAcctId());
			Account account = accountCombination.getAccount();
			dto = new ApPaymentLineDto();
			dto.setReferenceNumber(null);
			dto.setCredit(payment.getAmount());
			dto.setAccountName(account.getAccountName());
			dto.setAccountNumber(account.getNumber());
			dtos.add(dto);
			payment.setApPaymentLineDtos(dtos);
		}
		return payment;
	}

	/**
	 * Compute the total payment amount per supplier account.
	 * @param companyId The id of the company.
	 * @param supplierAcctId The id of the supplier account.
	 * @param asOfDate End date of the date range.
	 * @return The total payment amount.
	 */
	public double getTotalPayments(int companyId, int supplierAcctId, Date asOfDate) {
		return NumberFormatUtil.roundOffTo2DecPlaces(paymentDao.getTotalPaymentAmount(companyId, supplierAcctId, asOfDate));
	}

	/**
	 * Get the AP payment object by its Id.
	 * @param paymentId The unique Id of the payment.
	 * @return The AP payment object.
	 */
	public ApPayment getApPaymentPDF (int paymentId) {
		ApPayment payment = getApPayment(paymentId);
		if (payment != null) {
			List<ApPaymentInvoice> pInvoices = payment.getApPaymentInvoices();
			List<ApInvoiceDto> apInvoiceDtos = new ArrayList<ApInvoiceDto>();
			Account defaultCreditAcct = payment.getSupplierAccount().getDefaultCreditAC().getAccount();
			for (ApPaymentInvoice pInvoice : pInvoices) {
				APInvoice invoice = pInvoice.getApInvoice();
				invoice.setAmount(pInvoice.getPaidAmount());
				Double debit = invoice.getAmount() >= 0.0 ? invoice.getAmount() : null;
				Double credit = invoice.getAmount() < 0.0 ? -invoice.getAmount() : null;
				String invoiceNumber = invoice.getInvoiceNumber();
				String refNo = null;
				refNo = setReferenceNo(invoice, refNo);
				String description = invoice.getDescription() != null
						? invoice.getDescription() : refNo;
				String accountNumber = defaultCreditAcct.getNumber();
				String accountName = defaultCreditAcct.getAccountName();
				apInvoiceDtos.add(ApInvoiceDto.getInstanceOf(debit, credit, invoiceNumber, description,
						accountNumber, accountName));
			}
			AccountCombination accountCombination = combinationDao.get(payment.getBankAccount().getCashInBankAcctId());
			Account account = accountCombination.getAccount();
			apInvoiceDtos.add(ApInvoiceDto.getInstanceOf(payment.getAmount() < 0.0 ? -payment.getAmount() : null,
						payment.getAmount() >= 0.0 ? payment.getAmount() : null, null, null,
						account.getNumber(), account.getAccountName()));
			payment.setApInvoices(apInvoiceDtos);
		}
		return payment;
	}

	/**
	 * Generate the next available Check Number of the Checkbook.
	 * @param checkbook The checkbook.
	 * @return The next available check number.
	 */
	public BigDecimal generateCheckNumber (Checkbook checkbook) {
		BigDecimal checkNumber = paymentDao.getMaxCheckNumber(checkbook);
		//Generated check number is out of range
		if (checkNumber.compareTo(checkbook.getCheckbookNoTo()) > 0) {
			return null;
		}
		return checkNumber;
	}

	/**
	 * Get approved and unpaid invoices.
	 * @param supplierAccountId The Id of the supplier account.
	 * @return Collection of approved and unpaid invoices.
	 */
	public Collection<APInvoice> loadApprovedInvoices (int supplierAccountId, String invoiceNumber, String invoiceIds) {
		Page<APInvoice> apInvoices = invoiceDao.getUnpaidInvoices(supplierAccountId, invoiceNumber, invoiceIds, new PageSetting(1, 
				PageSetting.NO_PAGE_CONSTRAINT));
		for (APInvoice apInvoice : apInvoices.getData()) {
			if (apInvoice.getInvoiceTypeId().equals(InvoiceType.RTS_TYPE_ID)
					|| apInvoice.getInvoiceTypeId().equals(InvoiceType.RTS_EB_TYPE_ID)) {
				apInvoice.setAmount(-apInvoice.getAmount());
			}
		}
		return apInvoices.getData();
	}

	/**
	 * Get the list of unpaid payment lines.
	 * @param supplierAcctId The supplier account id.
	 * @param divisionId The division id.
	 * @param invoiceNumber The invoice number.
	 * @param ebObjectIds The eb object ids in string format.
	 * @return The list of unpaid payment lines.
	 */
	public Collection<ApPaymentLineDto> loadApPaymentLines(Integer supplierAccountId, Integer divisionId,
			String invoiceNumber, String ebObjectIds, Integer currencyId) {
		Page<ApPaymentLineDto> dtos = apPaymentLineDao.getUnpaidPaymentLines(supplierAccountId, divisionId,
				invoiceNumber, ebObjectIds, currencyId, new PageSetting(PageSetting.START_PAGE, PageSetting.NO_PAGE_CONSTRAINT));
		convertPaymentLineMonetaryVal(dtos.getData());
		return dtos.getData();
	}

	private void convertPaymentLineMonetaryVal(Collection<ApPaymentLineDto> collection) {
		for(ApPaymentLineDto dto : collection) {
			double currencyRateValue = dto.getCurrencyRateValue() != null ? dto.getCurrencyRateValue() : 1;
			dto.setAmount(CurrencyUtil.convertMonetaryValues(dto.getAmount(), currencyRateValue));
			dto.setBalance(CurrencyUtil.convertMonetaryValues(dto.getBalance(), currencyRateValue));
			dto.setCurrencyRateValue(currencyRateValue);
		}
	}

	/**
	 * Get the paid invoices.
	 * @param payment The payment object.
	 * @return The list of paid invoices.
	 */
	public List<ApInvoiceDto> getAPInvoices(ApPayment payment){
		List<ApInvoiceDto> invoiceDto = new ArrayList<ApInvoiceDto>();
		Collection<ApPaymentInvoice> pc = apPaymentInvoiceDao.getPaidInvoicesByPaymentId(payment.getId());
		if(pc == null)
			return null;
		APInvoice inv = null;
		ApInvoiceDto dto = null;
		String refNo = null;
		int invoiceId = 0;
		for(ApPaymentInvoice ap : pc){
			invoiceId = ap.getInvoiceId();
			inv = invoiceDao.get(invoiceId);
			dto = new ApInvoiceDto();
			dto.setInvoiceId(invoiceId);
			refNo = setReferenceNo(inv, refNo);
			dto.setInvoiceNumber(refNo);
			dto.setAmount(ap.getPaidAmount());
			dto.setInvoiceTypeId(inv.getInvoiceTypeId());
			invoiceDto.add(dto);
		}
		return invoiceDto;
	}

	private String setReferenceNo(APInvoice ap, String refNo) {
		RReceivingReport rr = new RReceivingReport();
		Integer invoiceTypeId = ap.getInvoiceTypeId();
		List<Integer> rrItemTypeIds = new ArrayList<>(Arrays.asList(InvoiceType.RR_TYPE_ID, InvoiceType.RR_RAW_MAT_TYPE_ID,
				InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID, InvoiceType.RTS_TYPE_ID, InvoiceType.RTS_EB_TYPE_ID));
		if(rrItemTypeIds.contains(invoiceTypeId)) {
			rr = receivingReportDao.getRrByInvoiceId(ap.getId());
			String formLabel = "";
			switch (invoiceTypeId) {
			case InvoiceType.RR_RAW_MAT_TYPE_ID:
				formLabel += "RM IS-";
				break;
			case InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID:
				formLabel += "RM P-";
				break;
			case InvoiceType.RTS_TYPE_ID:
				formLabel += "RTS-";
				break;
			case InvoiceType.RTS_EB_TYPE_ID:
				formLabel += "RTS-EB-";
				break;
			default:
				formLabel += "RR-";
				break;
			}
			refNo = formLabel + ap.getSequenceNumber();
			refNo += ap.getInvoiceNumber() != null && !ap.getInvoiceNumber().trim().isEmpty()
					? (invoiceTypeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID) ? " SS#: " : " Inv#: ") + ap.getInvoiceNumber() : "";
			if (invoiceTypeId.equals(InvoiceType.RTS_TYPE_ID)) {
				RReturnToSupplierItem rtsi = returnToSupplierItemDao.getRtsItems(ap.getId()).get(0);
				Integer rrItemId = rtsi.getrReceivingReportItemId();
				if (rrItemId != null) {
					rr = receivingReportDao.getRrByInvoiceId(rReceivingReportItemDao.get(rrItemId).getApInvoiceId());
				}
			}
			if(!invoiceTypeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
				refNo += rr.getDeliveryReceiptNo() != null && !rr.getDeliveryReceiptNo().trim().isEmpty()? " DR#: "+rr.getDeliveryReceiptNo() : "";
			}
		} else {
			if (invoiceTypeId <= InvoiceType.CREDIT_MEMO_TYPE_ID) {
				refNo = ap.getInvoiceNumber();
			} else if (invoiceTypeId.equals(InvoiceType.INVOICE_ITEM_TYPE_ID)) {
				refNo = "AII-" + ap.getSequenceNumber() + " Inv#:"  + ap.getInvoiceNumber();
			} else if (invoiceTypeId.equals(InvoiceType.INVOICE_SERVICE_TYPE_ID)) {
				refNo = "AIS-" + ap.getSequenceNumber() + " Inv#:"  + ap.getInvoiceNumber();
			} else {
				refNo = "AP INVOICE-" + ap.getSequenceNumber();
			}
		}
		return refNo;
	}

	/**
	 * Compute the total paid amount of the invoice.
	 * @param apInvoice The invoice object.
	 * @param paymentId The ap payment id.
	 * @return the total paid amount
	 */
	public double totalPaidAmount(APInvoice apInvoice, Integer paymentId) {
		Collection<ApPaymentInvoice> payments = apPaymentInvoiceDao.getPaidInvoicesExcludeInPaymentId(apInvoice.getId(), paymentId);
		double totalPaidAmount = 0;
		for (ApPaymentInvoice apPaymentInvoice : payments) {
			totalPaidAmount += apPaymentInvoice.getPaidAmount();
		}
		return NumberFormatUtil.roundOffNumber(totalPaidAmount, 6);
	}

	/**
	 * Compute the total paid amount of the invoice.
	 * @param apInvoice The invoice object.
	 * @param paymentId The ap payment id.
	 * @return the total paid amount
	 */
	public double totalPaidAmount(APInvoice apInvoice) {
		return totalPaidAmount(apInvoice, null);
	}

	/**
	 * Get the list of supplier accounts filtered by the company
	 * of the bank account and the supplier.
	 * @return The list of supplier accounts.
	 */
	public List<SupplierAccount> getSupplierAccounts(int supplierId, int companyId) {
		return supplierAccountDao.getSupplierAccounts(supplierId, companyId, null, true);
	}

	/**
	 * Get the list of active and the selected in active bank account.
	 * @return The list of bank accounts.
	 */
	public List<BankAccount> getBankAccounts(User user, ApPayment apPayment) {
		List<BankAccount> bankAccts = bankAccountDao.getBankAccounts(user.getServiceLeaseKeyId());
		if(apPayment.getBankAccountId() != null) {
			Collection<Integer> bankAcctIds = new ArrayList<Integer>();
			for (BankAccount ba : bankAccts) {
				bankAcctIds.add(ba.getId());
			}
			if(!bankAcctIds.contains(apPayment.getBankAccountId()))
				bankAccts.add(bankAccountDao.get(apPayment.getBankAccountId()));
		}
		return bankAccts;
	}

	/**
	 * Get the list of active and the selected in active check book.
	 * @param user The user current logged.
	 * @param bankAccountId The unique id of the bank account.
	 * @param checkBookId The unique id of the check book.
	 * @param checkBookName The checkbook name.
	 * @param limit the limit of items to be showed
	 * @return The list of check books.
	 */
	public List<Checkbook> getCheckBooks(User user, int bankAccountId, Integer checkBookId, String checkBookName,
			Integer limit){
		List<Checkbook> checkBooks = checkbookDao.getCheckbooks(user, bankAccountId, checkBookName, limit);
		return checkBooks;
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		ApPayment apPayment = (ApPayment) form;
		boolean isNew = apPayment.getId() == 0;
		Date currentDate = new Date();
		if(isNew) {
			int companyId = getCompanyId(apPayment.getSupplierAccountId());
			apPayment.setCompanyId(companyId);
			apPayment.setVoucherNumber(paymentDao.generateVoucherNumber(companyId, apPayment.getPaymentTypeId()));
			logger.debug("Generated voucher number "+apPayment.getVoucherNumber()+" for AP Payment form.");
		} else {
			ApPayment savedPayment = getApPayment(apPayment.getId());
			DateUtil.setCreatedDate(apPayment, savedPayment.getCreatedDate());
			if(apPayment.getFormWorkflow() == null && apPayment.getFormWorkflowId() != null && apPayment.getFormWorkflowId() != 0) {
				apPayment.setFormWorkflow(formWorkflowDao.get(apPayment.getFormWorkflowId()));
			}
			if (apPayment.getFormWorkflow().getCurrentFormStatus().getId() == FormStatus.NEGOTIABLE_ID) {
				int paymentId = apPayment.getId();
				List<ApInvoiceDto> toBePaidInvoices = apPayment.getApInvoices();
				apPayment = paymentDao.get(paymentId);
				apPayment.setApInvoices(toBePaidInvoices);
				apPayment.setAmount(0);
				Collection<ApPaymentInvoice> apPInvoices = apPaymentInvoiceDao.getPaidInvoicesByPaymentId(paymentId);
				for (ApPaymentInvoice api : apPInvoices) {
					api.setPaidAmount(0.0);
					apPaymentInvoiceDao.saveOrUpdate(api);
				}
			}
		}
		double currencyRateValue = apPayment.getCurrencyRateValue() > 0 ? apPayment.getCurrencyRateValue() : 1;
		AuditUtil.addAudit(apPayment, user, currentDate);
		Integer parentObjectId = apPayment.getEbObjectId();
		apPayment.setAmount(CurrencyUtil.convertAmountToPhpRate(apPayment.getAmount(), currencyRateValue));
		paymentDao.saveOrUpdate(apPayment);
		//Save payment lines
		savePaymentLines(apPayment, currencyRateValue);
		refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
				apPayment.getReferenceDocuments(), true);
		logger.info("Successfully saved AP Payment form with voucher no. "+apPayment.getVoucherNumber()
				+", check no. "+apPayment.getCheckNumber());
	}

	private void savePaymentLines(ApPayment apPayment, double currencyRateValue) {
		for(ApPaymentLine apPaymentLine : apPayment.getApPaymentLines()) {
			apPaymentLine.setApPaymentId(apPayment.getId());
			apPaymentLineDao.saveOrUpdate(apPaymentLine);
		}
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		ApPayment apPayment = (ApPayment) form;
		apPayment.setApPaymentLines(convertDtoToPaymentLine(apPayment, user));
	}

	private List<ApPaymentLine> convertDtoToPaymentLine(ApPayment apPayment, User user) {
		double currencyRateValue = apPayment.getCurrencyRateValue() > 0 ? apPayment.getCurrencyRateValue() : 1;
		apPayment.setCurrencyRateValue(currencyRateValue);
		List<ApPaymentLine> paymentLines = apPaymentLineDao.getAllByRefId(ApPaymentInvoice.FIELD.apPaymentId.name(), apPayment.getId());
		for(ApPaymentLine apl : paymentLines) {
			apPaymentLineDao.delete(apl);
		}
		List<ApPaymentLine> toBeSaved = new ArrayList<>();
		List<ApPaymentLineDto> dtos = apPayment.getApPaymentLineDtos();
		for(ApPaymentLineDto dto : dtos) {
			currencyRateValue = dto.getCurrencyRateValue() != null && dto.getCurrencyRateValue() > 0 ? dto.getCurrencyRateValue() : 1;
			ApPaymentLine apl = new ApPaymentLine();
			apl.setEbObjectId(dto.getEbObjectId());
			apl.setPaidAmount(CurrencyUtil.convertAmountToPhpRate(dto.getAmount(), currencyRateValue));
			apl.setApPaymentLineTypeId(dto.getApPaymentLineTypeId());
			apl.setReferenceObjectId(dto.getRefenceObjectId());
			apl.setCurrencyRateValue(currencyRateValue);
			AuditUtil.addAudit(apl, new Audit(user.getId(), true, new Date ()));
			toBeSaved.add(apl);
		}
		return toBeSaved;
	}

	/**
	 * Get the Id of the company of the supplier account.
	 */
	private int getCompanyId(int supplierAcctId) {
		SupplierAccount sa = supplierAccountDao.get(supplierAcctId);
		return sa.getCompanyId();
	}

	/**
	 * Get the AP Invoice using its invoice number and supplier account.
	 * @return The AP Invoice object.
	 */
	public APInvoice getInvoiceByInvNumber(int supplierAccountId,
			String invoiceNumber, int serviceLeaseKeyId) {
		APInvoice apInvoice = invoiceDao.getInvoice(supplierAccountId, invoiceNumber, serviceLeaseKeyId);
		if (apInvoice == null) {
			return null;
		} else {
			apInvoice.setRrItems(new ArrayList<RReceivingReportItem>());
			apInvoice.setRtsItems(new ArrayList<RReturnToSupplierItem>());
			apInvoice.setApInvoiceLines(new ArrayList<ApInvoiceLine>());
			double totalPaidAmt = totalPaidAmount(apInvoice);
			double balance = 0;
			if (apInvoice.getInvoiceTypeId().equals(InvoiceType.RTS_TYPE_ID)
					|| apInvoice.getInvoiceTypeId().equals(InvoiceType.RTS_EB_TYPE_ID)) {
				apInvoice.setAmount(-apInvoice.getAmount());
			}
			if (totalPaidAmt != apInvoice.getAmount()) {
				balance = apInvoice.getAmount() - totalPaidAmt;
				apInvoice.setBalance(NumberFormatUtil.roundOffNumber(balance, 6));
				return apInvoice;
			}
		}
		return null;
	}

	/**
	 * Validate the check number if it is unique.
	 * @param bankAccountId The Id of the bank account.
	 * @param apPayment The AP Payment object.
	 * @return True if unique, otherwise false.
	 */
	public boolean isUniqueCheckNumber(int bankAccountId, ApPayment apPayment) {
		if(apPayment.getId() == 0)
			return paymentDao.isUniqueCheckNumber(apPayment, bankAccountId);
		ApPayment oldPayment = paymentDao.get(apPayment.getId());
		if(oldPayment.getCheckNumber().equals(apPayment.getCheckNumber()))
			return true;
		return paymentDao.isUniqueCheckNumber(apPayment, bankAccountId);
	}

	/**
	 * Validate the check number if it is in range.
	 * @return True if in range, otherwise false.
	 */
	public boolean isCheckNumberInRange(int checkbookId, BigDecimal checkNumber) {
		Checkbook cb = checkbookDao.get(checkbookId);
		BigDecimal checkNoFrom = cb.getCheckbookNoFrom();
		BigDecimal checkNoTo = cb.getCheckbookNoTo();
		if((checkNoFrom.compareTo(checkNumber) < 0 && checkNoTo.compareTo(checkNumber) > 0)
				|| checkNoFrom.equals(checkNumber) || checkNoTo.equals(checkNumber)) {
			return true;
		}
		return false;
	}

	/**
	 * Validate the invoice number if it is existing and unpaid.
	 * @param supplierAccountId The supplier account Id.
	 * @param apInvoiceId The id of the selected invoice.
	 * @param invoiceNumber The invoice number to be evaluated.
	 * @param paymentId The payment id.
	 * @return True if the invoice number is existing, otherwise false.
	 */
	public boolean isValidInvoiceNumber(int supplierAccountId, int apInvoiceId, String invoiceNumber, User user, int paymentId) {
		APInvoice invoice = invoiceDao.getInvoice(supplierAccountId,
				apInvoiceId, invoiceNumber, user.getServiceLeaseKeyId());
		if(invoice != null) {
			Double balance = invoice.getAmount() - totalPaidAmount(invoice, paymentId);
			if(balance != 0)
				return true;
		}
		return false;
	}

	/**
	 * Validate the Payment Date if it is in open time period.
	 * @param paymentDate The date to be validated.
	 * @return True if it is in an open time period, otherwise false.
	 */
	public boolean isValidPaymentDate (Date paymentDate) {
		Collection<TimePeriod> openTimePeriods = timePeriodDao.getOpenTimePeriods();
		for(TimePeriod tp : openTimePeriods) {
			Date dateBefore = DateUtil.sqlDateToJavaDate(tp.getDateFrom());
			Date dateAfter = DateUtil.sqlDateToJavaDate(tp.getDateTo());
			if((dateBefore.before(paymentDate) && dateAfter.after(paymentDate))
					|| dateBefore.equals(paymentDate) || dateAfter.equals(paymentDate))
				return true;
		}
		return false;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		ApPayment apPayment = paymentDao.get(id);
		FormWorkflow workflow = apPayment.getFormWorkflow();
		List<FormWorkflowLog> orderedLogs = new ArrayList<>();
		// Get the list of all status configured for AP Payment.
		List<FormStatus> formStatuses = workflowHandler.getAllWorkflow(apPayment.getWorkflowName(), null, workflow, true);
		for (FormStatus fs : formStatuses) {
			for (FormWorkflowLog formWorkflowLog : workflow.getFormWorkflowLogs()) {
				// Order the list of Form Workflow Logs based on the form status retrieved.
				if(formWorkflowLog.getFormStatusId() == fs.getId()) {
					orderedLogs.add(formWorkflowLog);
					break;
				}
			}
		}
		workflow.setFormWorkflowLogs(orderedLogs);
		return workflow;
	}
	
	/**
	 * Get the status for payment.
	 * @return Payment statuses.
	 */
	public List<FormStatus> getPaymentStatuses () {
		return formStatusDao.getPaymentStatuses();
	}
	
	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		ApPayment apPayment = paymentDao.getApPaymentByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (apPayment != null) {
			// Checks if check date and payment date is open time period.
			Date checkDate = apPayment.getCheckDate();
			Date paymentDate = apPayment.getPaymentDate();
			boolean invalidCDate = false;
			if(checkDate != null) {
				invalidCDate = !timePeriodService.isOpenDate(checkDate);
			}
			boolean invalidPDate = !timePeriodService.isOpenDate(paymentDate);
			String message = "";
			if (invalidCDate || invalidPDate) {
				if (invalidCDate) {
					message = "Check date should be in an open time period.";
				}
				if (invalidPDate) {
					message += (invalidCDate ? "<br>" : "") + "Payment date should be in an open time period.";
				}
				bindingResult.reject("workflowMessage", message);
				currentWorkflowLog.setWorkflowMessage(message);
			}
			//execute when canceling transaction
			if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID 
					&& currentWorkflowLog.getComment() != null && !currentWorkflowLog.getComment().isEmpty()) {
				List<ApPaymentLine> paymentLines = apPaymentLineDao.getAllByRefId(ApPaymentLine.FIELD.apPaymentId.name(), apPayment.getId());
				String paymentNos = "";
				for(ApPaymentLine paymentLine : paymentLines) {
					if(paymentLine.getApPaymentLineTypeId() == ApPaymentLineType.SUPLIER_ADVANCE_PAYMENT) {
						EBObject referenceObject = ooLinkHelper.getReferenceObject(paymentLine.getEbObjectId(),
								ApPaymentLine.OBJECT_TYPE);
						List<ApPayment> usedPayments = paymentDao.getApPaymentsWithNegativeSap(referenceObject.getId(), apPayment.getId());
						if(usedPayments != null && !usedPayments.isEmpty() && usedPayments.get(0) != null) {
							int index = 0;
							for(ApPayment payment : usedPayments) {
								paymentNos += index == 0 ? payment.getVoucherNumber() : ", " + payment.getVoucherNumber();
								index++;
							}
						}
					}
					if(!paymentNos.trim().isEmpty()) {
						message = "The SAP Invoice line has been used in the following payment transaction/s: " + paymentNos;
						bindingResult.reject("workflowMessage", message);
						currentWorkflowLog.setWorkflowMessage(message);
					}
				}
			}
		}
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return paymentDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		ApPayment apPayment = getApPayment(paymentDao.getByEbObjectId(ebObjectId).getId());
		Integer pId = apPayment.getId();
		FormProperty property = workflowHandler.getProperty(apPayment.getWorkflowName(), user);
		Integer divisionId = apPayment.getDivisionId();
		String popupLink = "/apPayment/"+divisionId+"/form?pId=" + pId;
		String printOutLink = "/" +property.getPrint() + "?pId=" + pId;
		String latestStatus = apPayment.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "AP Payment - " + apPayment.getVoucherNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + apPayment.getSupplier().getName())
				.append(" " + apPayment.getSupplierAccount().getName())
				.append(" " + DateUtil.formatDate(apPayment.getCheckDate()))
				.append("<b> PAYMENT DATE : </b>" + DateUtil.formatDate(apPayment.getPaymentDate()))
				.append(" " + apPayment.getAmount());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case ApPayment.AP_PAYMENT_OBJECT_TYPE_ID:
			return paymentDao.getByEbObjectId(ebObjectId);
		case ApPaymentInvoice.OBJECT_TYPE:
			return apPaymentInvoiceDao.getByEbObjectId(ebObjectId);
		case ApPaymentLine.OBJECT_TYPE:
			return apPaymentLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Get the list of {@link ApPaymentLine} by parent eb object and ap payment id.
	 * @param parentObjectId The parent eb object id.
	 * @param apPaymentId The ap payment id.
	 * @return
	 */
	public Double getTotalApLineAmt(Integer parentObjectId, Integer apPaymentId) {
		List<ApPaymentLine> apPaymentLines = apPaymentLineDao.getApPaymentLines(parentObjectId, apPaymentId);
		Double total = 0.00;
		for (ApPaymentLine apl : apPaymentLines) {
			if (apl.getApPaymentLineTypeId() != ApPaymentLineType.SUPLIER_ADVANCE_PAYMENT) {
				total += apl.getPaidAmount();
			}
		}
		return total;
	}

	/**
	 * Get related Supplier advance payment transaction.
	 * @param invoiceEbObject The invoice eb object id.
	 * @return The list of ApPaymentLineDto.
	 */
	public List<ApPaymentLineDto> getSapRefTrans(Integer invoiceEbObject, String ebObjectIds, Integer currencyId) {
		List<ApPaymentLineDto> dtos = apPaymentLineDao.getSapRefTrans(invoiceEbObject, ebObjectIds, currencyId);
		convertPaymentLineMonetaryVal(dtos);
		return dtos;
	}

	/**
	 * Remove "Pesos" from the string parameter.
	 * @param str The string.
	 * @return The processed string.
	 */
	public String removePesos(String str) {
		return str.replace("Pesos", "");
	}

	/**
	 * Save ap payment with clearing details.
	 * @param apPayment The {@link ApPayment}.
	 * @param user The {@link User}.
	 */
	public void savePaymentReceivingDetails(ApPayment apPayment, User user) {
		ApPayment savedPayment = paymentDao.get(apPayment.getId());
		savedPayment.setDateCleared(apPayment.getDateCleared());
		AuditUtil.addAudit(savedPayment, new Audit(user.getId(), true, new Date()));
		//Save
		paymentDao.saveOrUpdate(savedPayment);
	}
}